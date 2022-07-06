package com.michaelflisar.bundlebuilder

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import java.io.Serializable

inline fun <reified T : Serializable> Bundle.getSerializableOfType(key : String) : T? =
    BundleCompat.getSerializable(this, key, T::class.java)

inline fun <reified T : Parcelable> Bundle.getParcelableOfType(key : String) : T? =
    BundleCompat.getParcelable(this, key, T::class.java)

inline fun <reified T : Parcelable> Bundle.getParcelableArrayOfType(key : String) : Array<T> =
    BundleCompat.getParcelableArray(this, key, T::class.java)

inline fun <reified T : Parcelable> Bundle.getParcelableArrayListOfType(key : String) : ArrayList<T> =
    BundleCompat.getParcelableArrayList(this, key, T::class.java)

inline fun <reified T : Parcelable> Bundle.getSparseParcelableArrayOfType(key : String) : SparseArray<T> =
    BundleCompat.getSparseParcelableArray(this, key, T::class.java)
