package com.michaelflisar.bundlebuilder;

import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings({"unchecked", "RedundantCast"})
public class IntentCompat
{
    @Nullable
    public static <T extends Serializable> T getSerializable(Intent intent, String key, Class<? extends T> type)
    {
        try
        {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
                return intent.getSerializableExtra(key, type);
            return (T)intent.getSerializableExtra(key);
        }
        catch (Throwable ex)
        {
            return null;
        }
    }

    @Nullable
    public static <T extends Parcelable> T getParcelableExtra(Intent intent, String key, Class<? extends T> type)
    {
        try
        {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
                return intent.getParcelableExtra(key, type);
            return (T)intent.getParcelableExtra(key);
        }
        catch (Throwable ex)
        {
            return null;
        }
    }

    @NotNull
    public static <T extends Parcelable> T[] getParcelableArrayExtra(Intent intent, String key, Class<? extends T> type)
    {
        try
        {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
                return Objects.requireNonNull(intent.getParcelableArrayExtra(key, type));
            return (T[])Objects.requireNonNull(intent.getParcelableArrayExtra(key));
        }
        catch (Throwable ex)
        {
            return (T[])Array.newInstance(type, 0);
        }
    }

    @NotNull
    public static <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(Intent intent, String key, Class<? extends T> type)
    {
        try
        {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
                return Objects.requireNonNull(intent.getParcelableArrayListExtra(key, type));
            return (ArrayList<T>)Objects.requireNonNull(intent.getParcelableArrayListExtra(key));
        }
        catch (Throwable ex)
        {
            return new ArrayList<>();
        }
    }
}
