package com.michaelflisar.bundlebuilder;

import android.os.Bundle;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static com.michaelflisar.bundlebuilder.Util.containsSupertype;

/**
 * Created by Michael on 14.02.2017.
 */

public class ArgElement {
    private final Element mElement;
    private final String mParamIsSetPostFix;
    private final String mParamName;
    private final TypeMirror mType;
    private final boolean mOptional;
    private final boolean mNullable;

    public ArgElement(Element e) {
        mElement = e;
        String extraValue = e.getAnnotation(Arg.class).name();
        mParamIsSetPostFix = "IsSet";
        mParamName = extraValue != null && extraValue.trim().length() > 0 ? extraValue : e.getSimpleName().toString();
        mType = e.asType();
        mOptional = e.getAnnotation(Arg.class).optional();
        mNullable = Util.hasAnnotation(e, "Nullable");
    }

    public boolean isOptional() {
        return mOptional;
    }

    public String getParamName() {
        return mParamName;
    }

    public void addFieldsToClass(TypeSpec.Builder builder, boolean useConstructorForMandatoryArgs) {
        builder.addField(TypeName.BOOLEAN, mParamName + mParamIsSetPostFix, Modifier.PRIVATE);
        if (!mOptional && useConstructorForMandatoryArgs) {
            builder.addField(TypeName.get(mType), mParamName, Modifier.PRIVATE, Modifier.FINAL);
        } else {
            builder.addField(TypeName.get(mType), mParamName, Modifier.PRIVATE);
        }
    }

    public void addFieldToIntent(MethodSpec.Builder buildMethod) {
        if (!isOptional()) {
            if (!mNullable && !Util.isPrimitiveType(mType)) {
                Util.addNullCheckWithException(buildMethod, this, true);
            }
        }
        buildMethod
                .beginControlFlow("if ($N.get($S) != null)", Util.FIELD_HASH_MAP_NAME, mParamName)
                .addStatement("intent.putExtra($S, ($T)$N.get($S).second)", mParamName, mType, Util.FIELD_HASH_MAP_NAME, mParamName);
        if (mNullable) {
            buildMethod
                    .nextControlFlow("else")
                    .addStatement("intent.putExtra($S, ($T)null)", mParamName, mType);
        }
        buildMethod.endControlFlow();

        //buildMethod.addStatement("intent.putExtra($S, $N)", mParamName, mParamName);
    }

    public void addFieldToBundle(Elements elementUtils, Types typeUtils, Messager messager, MethodSpec.Builder buildMethod) {
        String bundleFunctionName = Util.getBundleFunctionName(elementUtils, typeUtils, messager, mType);
        if (bundleFunctionName != null) {
            if (!isOptional()) {
                buildMethod
                        .beginControlFlow("if (!$N.containsKey($S) || !$N.get($S).first)", Util.FIELD_HASH_MAP_NAME, mParamName, Util.FIELD_HASH_MAP_NAME, mParamName)
                        //.beginControlFlow("if (!$N)", mParamName + mParamIsSetPostFix)
                        .addStatement("throw new RuntimeException($S)", String.format("Mandatory field '%s' missing!", mParamName))
                        .endControlFlow();
            }

            buildMethod
                    .addCode("\ntry" +
                             "\n{" +
                             "\n\tif ("+Util.FIELD_HASH_MAP_NAME+".get(\""+mParamName+"\").first)"+
                             "\n\t\tbundle.put"+bundleFunctionName+"(\""+mParamName+"\", ("+mType+")"+Util.FIELD_HASH_MAP_NAME+".get(\""+mParamName+"\").second);"+
                             "\n}" +
                             "\ncatch (Exception ex) {}" +
                             "\n\n");
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("Field type \"%s\" not supported!", mType.toString()));
        }
    }

    @SuppressWarnings("DuplicateExpressions")
    public void addFieldToInjection(MethodSpec.Builder injectMethod, Types typeUtils) {
        if (!mOptional) {
            Util.addContainsCheckWithException(injectMethod, this, "args");
        }

        Map<String, String> primitiveMethodsFromBundleClass = new HashMap<>();
        primitiveMethodsFromBundleClass.put("boolean", "getBoolean");
        primitiveMethodsFromBundleClass.put("int", "getInt");
        primitiveMethodsFromBundleClass.put("long", "getLong");
        primitiveMethodsFromBundleClass.put("double", "getDouble");
        primitiveMethodsFromBundleClass.put("byte", "getByte");
        primitiveMethodsFromBundleClass.put("char", "getChar");
        primitiveMethodsFromBundleClass.put("short", "getShort");
        primitiveMethodsFromBundleClass.put("float", "getFloat");
        primitiveMethodsFromBundleClass.put("java.lang.Boolean", "getBoolean");
        primitiveMethodsFromBundleClass.put("java.lang.Integer", "getInt");
        primitiveMethodsFromBundleClass.put("java.lang.Long", "getLong");
        primitiveMethodsFromBundleClass.put("java.lang.Double", "getDouble");
        primitiveMethodsFromBundleClass.put("java.lang.Byte", "getByte");
        primitiveMethodsFromBundleClass.put("java.lang.Character", "getChar");
        primitiveMethodsFromBundleClass.put("java.lang.Short", "getShort");
        primitiveMethodsFromBundleClass.put("java.lang.Float", "getFloat");
        primitiveMethodsFromBundleClass.put("java.lang.String", "getString");
        primitiveMethodsFromBundleClass.put("java.lang.CharSequence", "getCharSequence");
        primitiveMethodsFromBundleClass.put("boolean[]", "getBooleanArray");
        primitiveMethodsFromBundleClass.put("int[]", "getIntArray");
        primitiveMethodsFromBundleClass.put("long[]", "getLongArray");
        primitiveMethodsFromBundleClass.put("double[]", "getDoubleArray");
        primitiveMethodsFromBundleClass.put("byte[]", "getByteArray");
        primitiveMethodsFromBundleClass.put("char[]", "getCharArray");
        primitiveMethodsFromBundleClass.put("short[]", "getShortArray");
        primitiveMethodsFromBundleClass.put("float[]", "getFloatArray");
        primitiveMethodsFromBundleClass.put("java.lang.Boolean[]", "getBooleanArray");
        primitiveMethodsFromBundleClass.put("java.lang.Integer[]", "getIntArray");
        primitiveMethodsFromBundleClass.put("java.lang.Long[]", "getLongArray");
        primitiveMethodsFromBundleClass.put("java.lang.Double[]", "getDoubleArray");
        primitiveMethodsFromBundleClass.put("java.lang.Byte[]", "getByteArray");
        primitiveMethodsFromBundleClass.put("java.lang.Character[]", "getCharArray");
        primitiveMethodsFromBundleClass.put("java.lang.Short[]", "getShortArray");
        primitiveMethodsFromBundleClass.put("java.lang.Float[]", "getFloatArray");
        primitiveMethodsFromBundleClass.put("java.lang.String[]", "getStringArray");
        primitiveMethodsFromBundleClass.put("java.lang.CharSequence[]", "getCharSequenceArray");

        injectMethod.beginControlFlow("if (args != null && args.containsKey($S))", mParamName);

        if (primitiveMethodsFromBundleClass.containsKey(mType.toString()))
        {
            injectMethod.addStatement("annotatedClass.$N = args.$N($S)",
                mElement.getSimpleName().toString(),
                primitiveMethodsFromBundleClass.get(mType.toString()),
                mParamName);
        }
        else if (containsSupertype(typeUtils, mType, "java.util.List<java.lang.Integer>"))
        {
            injectMethod.addStatement("annotatedClass.$N = args.getIntegerArrayList($S)",
                mElement.getSimpleName().toString(),
                mParamName);
        }
        else if (containsSupertype(typeUtils, mType, "java.util.List<java.lang.String>"))
        {
            injectMethod.addStatement("annotatedClass.$N = args.getStringArrayList($S)",
                mElement.getSimpleName().toString(),
                mParamName);
        }
        else if (containsSupertype(typeUtils, mType, "java.util.List<java.lang.CharSequence>"))
        {
            injectMethod.addStatement("annotatedClass.$N = args.getCharSequenceArrayList($S)",
                    mElement.getSimpleName().toString(),
                    mParamName);
        }
        else if (containsSupertype(typeUtils, mType, "java.io.Serializable"))
        {
            injectMethod.addStatement("annotatedClass.$N = com.michaelflisar.bundlebuilder.BundleCompat.getSerializable(args, $S, $N.class)",
                mElement.getSimpleName().toString(),
                mParamName,
                mType.toString().indexOf('<')!=-1
                    ?mType.toString().substring(0, mType.toString().indexOf('<'))
                    :mType.toString());
        }
        else if (containsSupertype(typeUtils, mType, "android.os.Parcelable"))
        {
            injectMethod.addStatement("annotatedClass.$N = com.michaelflisar.bundlebuilder.BundleCompat.getParcelable(args, $S, $N.class)",
                    mElement.getSimpleName().toString(),
                    mParamName,
                    mType.toString().indexOf('<')!=-1
                        ?mType.toString().substring(0, mType.toString().indexOf('<'))
                        :mType.toString());
        }
        else if (mType.getKind()==TypeKind.ARRAY && 
                containsSupertype(typeUtils, ((ArrayType)mType).getComponentType(), "java.io.Serializable"))
        {
            throw new RuntimeException("@Arg Serializable[] arguments are disallowed! Please Use ArrayList<Serializable>!");
        }
        else if (mType.getKind()==TypeKind.ARRAY && 
                containsSupertype(typeUtils, ((ArrayType)mType).getComponentType(), "android.os.Parcelable"))
        {
            injectMethod.addStatement("annotatedClass.$N = com.michaelflisar.bundlebuilder.BundleCompat.getParcelableArray(args, $S, $N.class)",
                    mElement.getSimpleName().toString(),
                    mParamName,
                    ((ArrayType)mType).getComponentType().toString().indexOf('<')!=-1
                        ?((ArrayType)mType).getComponentType().toString().substring(0, ((ArrayType)mType).getComponentType().toString().indexOf('<'))
                        :((ArrayType)mType).getComponentType().toString());
        }
        else
        {
            injectMethod.addStatement("annotatedClass.$N = ($T) args.get($S)",
                mElement.getSimpleName().toString(),
                mType,
                mParamName);
        }

        injectMethod.endControlFlow();
    }

    public void addSetter(TypeSpec.Builder builder, ClassName className) {
        builder.addMethod(MethodSpec.methodBuilder(mParamName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(mType), mParamName)
                .addStatement("this.$N.put($S, new Pair(true, $N))", Util.FIELD_HASH_MAP_NAME, mParamName, mParamName)
                //.addStatement("this.$N = $N", mParamName, mParamName)
                //.addStatement("this.$N = true", mParamName + mParamIsSetPostFix)
                .addStatement("return this")
                .returns(className)
                .build());
    }

    public void addFieldGetter(Element annotatedElement, TypeSpec.Builder builder) {
        MethodSpec.Builder getterMethod = MethodSpec
                .methodBuilder(getFieldGetterName())
                .returns(ClassName.get(mType))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(TypeName.get(annotatedElement.asType()), "annotatedClass")
                .addParameter(Bundle.class, "bundle");

        getterMethod
                .beginControlFlow("if (bundle != null && bundle.containsKey($S))", mParamName)
                .addStatement("return ($T) bundle.get($S)", mType, mParamName)
                .nextControlFlow("else")
                .addStatement("return annotatedClass.$L",  mParamName)
                .endControlFlow();

        builder.addMethod(getterMethod.build());
    }

    public String getFieldGetterName() {
        return "get" + mParamName.substring(0, 1).toUpperCase() + mParamName.substring(1);
    }
}
