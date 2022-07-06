package com.example.test;

import android.os.Parcel;
import android.os.Parcelable;

public class SomeParcelable implements Parcelable
{
    protected SomeParcelable(Parcel in)
    {
    }

    public static final Creator<SomeParcelable> CREATOR = new Creator<SomeParcelable>()
    {
        @Override
        public SomeParcelable createFromParcel(Parcel in)
        {
            return new SomeParcelable(in);
        }

        @Override
        public SomeParcelable[] newArray(int size)
        {
            return new SomeParcelable[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
    }
}
