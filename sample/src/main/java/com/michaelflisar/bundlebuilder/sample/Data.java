package com.michaelflisar.bundlebuilder.sample;

import java.io.Serializable;
import java.util.ArrayList;
import com.michaelflisar.bundlebuilder.Arg;

public class Data implements Serializable
{
    @Arg public final boolean someBoolean;
    @Arg public final int someInt;
    @Arg public final long someLong;
    @Arg public final float someFloat;
    @Arg public final double someDouble;
    @Arg public final byte someByte;
    @Arg public final char someChar;
    @Arg public final short someShort;
    @Arg public final String someString;
    @Arg public final ArrayList<String> someStrings;

    public Data()
    {
        this.someBoolean = true;
        this.someInt = 42;
        this.someLong = 42L;
        this.someFloat = 42.0f;
        this.someDouble = 42.0;
        this.someByte = 0x42;
        this.someChar = (char)0x42;
        this.someShort = 42;
        this.someString = "Meaning of life";
        this.someStrings = new ArrayList<>();
        this.someStrings.add("Meaning of life");
        this.someStrings.add("42");
    }

    @Override
    public String toString()
    {
        return "Data{"+
               "\nsomeBoolean="+someBoolean+
               ",\nsomeInt="+someInt+
               ",\nsomeLong="+someLong+
               ",\nsomeFloat="+someFloat+
               ",\nsomeDouble="+someDouble+
               ",\nsomeByte="+someByte+
               ",\nsomeChar="+someChar+
               ",\nsomeShort="+someShort+
               ",\nsomeString='"+someString+'\''+
               ",\nsomeStrings="+someStrings+
               "\n}";
    }
}
