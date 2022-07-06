package com.example.test;

import android.os.Bundle;
import com.michaelflisar.bundlebuilder.Arg;
import com.michaelflisar.bundlebuilder.BundleBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@BundleBuilder
public class Test
{
    @Arg public boolean someBoolean;
    @Arg public int someInt;
    @Arg public long someLong;
    @Arg public double someDouble;
    @Arg public String someString;
    @Arg public byte someByte;
    @Arg public char someChar;
    @Arg public short someShort;
    @Arg public float someFloat;
    @Arg public CharSequence someCharSequence;
    @Arg public SomeParcelable someParcelable;
    @Arg public SomeParcelable[] someParcelableArray;
    @Arg public ArrayList<Integer> someIntegerArrayList;
    @Arg public ArrayList<String> someStringArrayList;
    @Arg public ArrayList<CharSequence> someCharSequenceArrayList;
    @Arg public ArrayList<SomeSerializable> someSerializableArrayList;
    @Arg public SomeSerializable someSerializable;
    @Arg public ArrayList<SomeSerializable> someSerializableList;
    @Arg public HashSet<SomeSerializable> someSerializableSet;
    @Arg public HashMap<SomeSerializable, SomeSerializable> someSerializableMap;
    @Arg public byte[] someByteArray;
    @Arg public short[] someShortArray;
    @Arg public char[] someCharArray;
    @Arg public float[] someFloatArray;
    @Arg public CharSequence[] someCharSequenceArray;
    @Arg public boolean[] someBooleanArray;
    @Arg public int[] someIntArray;
    @Arg public long[] someLongArray;
    @Arg public double[] someDoubleArray;
    @Arg public String[] someStringArray;

    public Test(Bundle args)
    {
        TestBundleBuilder.inject(args, this);
    }
}
