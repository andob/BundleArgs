package com.michaelflisar.bundlebuilder;

import android.os.Bundle;
import java.lang.reflect.Method;
import java.util.Objects;

public class BundleArgs
{
    public static void bind(Object target, Bundle bundle)
    {
        try
        {
            Class<?> targetClass = target.getClass();
            Class<?> builderClass = Objects.requireNonNull(targetClass.getClassLoader()).loadClass(targetClass.getCanonicalName() + "BundleBuilder");
            Method injectMethod = builderClass.getDeclaredMethod("inject", Bundle.class, targetClass);
            injectMethod.invoke(null, bundle, target);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
