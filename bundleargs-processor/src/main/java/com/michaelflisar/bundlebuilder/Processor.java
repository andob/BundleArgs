package com.michaelflisar.bundlebuilder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class Processor extends AbstractProcessor {

    // --------------------
    // General functions
    // --------------------

    public static Processor instance;

    public Types typeUtils;
    public Elements elementUtils;
    public Filer filer;
    public Messager messager;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>() {{
            add(BundleBuilder.class.getCanonicalName());
        }};
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        instance = this;
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    // --------------------
    // Process class
    // --------------------

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element activityClass : roundEnv.getElementsAnnotatedWith(BundleBuilder.class)) {
            // Make sure element is a field or a method declaration
            if (!activityClass.getKind().isClass()) {
                logError(activityClass, "Only classes can be annotated with @%s", BundleBuilder.class.getSimpleName());
                return true;
            }

            try {
                TypeSpec builderSpec = getBuilderSpec(activityClass);
                JavaFile builderFile = JavaFile.builder(Util.getPackageName(activityClass), builderSpec).build();
                builderFile.writeTo(filer);
            } catch (Exception e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.toString());
                logError(activityClass, "Could not create BundleArgs builder class for %s: (Exception: %s)", activityClass.getSimpleName(), e.getMessage());
            }
        }
        return true;
    }

    // --------------------
    // Processor implementation
    // --------------------

    private TypeSpec getBuilderSpec(Element annotatedElement) {
        List<ArgElement> required = new ArrayList<>();
        List<ArgElement> optional = new ArrayList<>();
        List<ArgElement> all = new ArrayList<>();

        getAnnotatedFields(annotatedElement, required, optional);
        all.addAll(required);
        all.addAll(optional);

        // 1) create class
        final String name = Util.getBundleBuilderName(annotatedElement);
        TypeSpec.Builder builder = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        // 2) create a hashmap for all fields to keep field count low
        createHashMapField(annotatedElement, builder);

        // 3) create constructor with all necessary fields
        createConstructor(builder);

        // 4) add methods for all optional fields
        addSetters(name, annotatedElement, builder, required, optional);

        // 5) add buildIntent method to create an intent
        addBuildIntentFunction(annotatedElement, builder, all);
        addStartActivity(annotatedElement, builder);
        addCreateFragment(annotatedElement, builder);
        addCreate(annotatedElement, builder);

        // 6) add build method to create a bundle
        addBuildBundleFunction(builder, all);

        // 7) add inject method to read all fields into an annotated class
        addInjectFunction(annotatedElement, builder, all);

        return builder.build();
    }

    // --------------------
    // Main functions
    // --------------------

    private void createConstructor(TypeSpec.Builder builder) {
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        builder.addMethod(constructor.build());
    }

    private void createHashMapField(Element annotatedElement, TypeSpec.Builder builder) {
        ParameterizedTypeName subType = ParameterizedTypeName.get(Pair.class, Boolean.class, Object.class);
        //ParameterizedTypeName mainType = ParameterizedTypeName.get(HashMap.class, String.class, Pair.class);

        ParameterizedTypeName mainType = ParameterizedTypeName.get(
                ClassName.get(HashMap.class),
                ClassName.get(String.class),
                subType);

        builder.addField(
                FieldSpec.builder(mainType, Util.FIELD_HASH_MAP_NAME, Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("new HashMap<>()")
                        .build()
        );
    }

    private void addSetters(String name, Element annotatedElement, TypeSpec.Builder builder, List<ArgElement> required, List<ArgElement> optional) {
        List<ArgElement> elementsToPrecess = new ArrayList<>();

        // add setters for all required fields
        elementsToPrecess.addAll(required);

        // we always add setters for all optional fields
        elementsToPrecess.addAll(optional);

        ClassName className = ClassName.get(Util.getPackageName(annotatedElement), name);
        for (ArgElement e : elementsToPrecess) {
            e.addSetter(builder, className);
        }
    }

    private void addBuildIntentFunction(Element annotatedElement, TypeSpec.Builder builder, List<ArgElement> all) {
        if (!Util.checkIsOrExtendsActivity(elementUtils, typeUtils, annotatedElement)) {
            return;
        }

        MethodSpec.Builder buildIntentMethod = MethodSpec.methodBuilder("buildIntent")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Context.class, "context")
                .addStatement("$T intent = new Intent(context, $T.class)", Intent.class, TypeName.get(annotatedElement.asType()));

        for (ArgElement e : all) {
            e.addFieldToIntent(buildIntentMethod);
        }

        buildIntentMethod.returns(Intent.class)
                .addStatement("return intent");
        builder.addMethod(buildIntentMethod.build());
    }

    private void addStartActivity(Element annotatedElement, TypeSpec.Builder builder) {
        if (Util.checkIsOrExtendsActivity(elementUtils, typeUtils, annotatedElement)) {
            MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("startActivity")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(Context.class, "context")
                    .addStatement("$T intent = $L", Intent.class, "buildIntent(context)")
                    .addStatement("context.startActivity(intent)");
            builder.addMethod(buildMethod.build());
        }
    }

    private void addCreateFragment(Element annotatedElement, TypeSpec.Builder builder) {
        if (Util.checkIsOrExtendsFragment(elementUtils, typeUtils, annotatedElement)) {
            ClassName className = ClassName.get(Util.getPackageName(annotatedElement), annotatedElement.getSimpleName().toString());
            MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("createFragment")
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("$L fragment = new $L()", annotatedElement.getSimpleName(), annotatedElement.getSimpleName())
                    .addStatement("$T args = $L", Bundle.class, "build()")
                    .addStatement("fragment.setArguments(args)")
                    .returns(className)
                    .addStatement("return fragment");

            builder.addMethod(buildMethod.build());
        }
    }

    private void addCreate(Element annotatedElement, TypeSpec.Builder builder) {
        if (Util.checkForConstructorWithBundle(annotatedElement)) {
            ClassName className = ClassName.get(Util.getPackageName(annotatedElement), annotatedElement.getSimpleName().toString());
            MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("create")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(className)
                    .addStatement("return new $L(build())", annotatedElement.getSimpleName());

            builder.addMethod(buildMethod.build());
        }
    }

    private void addBuildBundleFunction(TypeSpec.Builder builder, List<ArgElement> all) {
        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$T bundle = new $T()", Bundle.class, Bundle.class);

        for (ArgElement e : all) {
            e.addFieldToBundle(elementUtils, typeUtils, messager, buildMethod);
        }

        buildMethod.returns(Bundle.class)
                .addStatement("return bundle");
        builder.addMethod(buildMethod.build());
    }

    private void addInjectFunction(Element annotatedElement, TypeSpec.Builder builder, List<ArgElement> all) {
        MethodSpec.Builder injectMethod = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Bundle.class, "args")
                .addParameter(TypeName.get(annotatedElement.asType()), "annotatedClass");

        for (ArgElement e : all) {
            e.addFieldToInjection(injectMethod, typeUtils);
        }

        builder.addMethod(injectMethod.build());
    }

    // --------------------
    // Helper functions
    // --------------------

    private void logError(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void getAnnotatedFields(Element annotatedElement, List<ArgElement> required, List<ArgElement> optional)
    {
        for (Element e : annotatedElement.getEnclosedElements()) {
            if (e.getAnnotation(Arg.class) != null) {
                ArgElement ae = new ArgElement(e);
                if (ae.isOptional()) {
                    optional.add(ae);
                } else {
                    required.add(ae);
                }
            }
        }

        Element superClass = getSuperClassElement(annotatedElement);
        if (superClass != null)
            getAnnotatedFields(superClass, required, optional);
    }

    private Element getSuperClassElement(Element annotatedElement)
    {
        List<? extends TypeMirror> superTypes = Processor.instance.typeUtils.directSupertypes(annotatedElement.asType());
        TypeMirror superClassType = superTypes.size() > 0 ? superTypes.get(0) : null;
        Element superClass = superClassType == null ? null : Processor.instance.typeUtils.asElement(superClassType);
        if (superClass != null && superClass.getKind() == ElementKind.CLASS)
        {
            String superClassNameWithPackage = superClass.asType().toString();
            if (superClassNameWithPackage.startsWith("java.") || 
                superClassNameWithPackage.startsWith("android.") || 
                superClassNameWithPackage.startsWith("androidx."))
                return null;
            return superClass;
        }

        return null;
    }
}