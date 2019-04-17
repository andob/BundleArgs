package com.michaelflisar.bundlebuilder;

import android.os.Bundle;

import java.lang.reflect.Method;

public class BundleArgs
{
    public static void bind(Object target, Bundle bundle) throws Exception
    {
        Class<?> targetClass = target.getClass();
        Class<?> builderClass = targetClass.getClassLoader().loadClass(targetClass.getCanonicalName() + "BundleBuilder");
        Method injectMethod=builderClass.getDeclaredMethod("inject", Bundle.class, targetClass);
        injectMethod.invoke(null, bundle, target);
    }
}
