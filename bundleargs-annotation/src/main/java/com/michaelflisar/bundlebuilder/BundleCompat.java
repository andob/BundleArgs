package com.michaelflisar.bundlebuilder;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings({"unchecked", "RedundantCast"})
public class BundleCompat
{
    @Nullable
    public static <T extends Serializable> T getSerializable(Bundle bundle, String key, Class<? extends T> type)
    {
        try
        {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
                return bundle.getSerializable(key, type);
            return (T)bundle.getSerializable(key);
        }
        catch (Throwable ex)
        {
            return null;
        }
    }

    @Nullable
    public static <T extends Parcelable> T getParcelable(Bundle bundle, String key, Class<? extends T> type)
    {
        try
        {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
                return bundle.getParcelable(key, type);
            return (T)bundle.getParcelable(key);
        }
        catch (Throwable ex)
        {
            return null;
        }
    }

    @NotNull
    public static <T extends Parcelable> T[] getParcelableArray(Bundle bundle, String key, Class<? extends T> type)
    {
        try
        {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
                return Objects.requireNonNull(bundle.getParcelableArray(key, type));
            return (T[])Objects.requireNonNull(bundle.getParcelableArray(key));
        }
        catch (Throwable ex)
        {
            return (T[])Array.newInstance(type, 0);
        }
    }

    public static <T extends Parcelable> ArrayList<T> getParcelableArrayList(Bundle bundle, String key, Class<? extends T> type)
    {
        try
        {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
                return Objects.requireNonNull(bundle.getParcelableArrayList(key, type));
            return (ArrayList<T>)Objects.requireNonNull(bundle.getParcelableArrayList(key));
        }
        catch (Throwable ex)
        {
            return new ArrayList<>();
        }
    }

    public static <T extends Parcelable> SparseArray<T> getSparseParcelableArray(Bundle bundle, String key, Class<? extends T> type)
    {
        try
        {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
                return Objects.requireNonNull(bundle.getSparseParcelableArray(key, type));
            return (SparseArray<T>)Objects.requireNonNull(bundle.getSparseParcelableArray(key));
        }
        catch (Throwable ex)
        {
            return new SparseArray<>();
        }
    }
}
