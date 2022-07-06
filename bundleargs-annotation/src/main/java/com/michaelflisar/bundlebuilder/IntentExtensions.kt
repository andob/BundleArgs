package com.michaelflisar.bundlebuilder

import android.content.Intent
import android.os.Parcelable
import java.io.Serializable

inline fun <reified T : Serializable> Intent.getSerializableExtraOfType(key : String) : T? =
    IntentCompat.getSerializable(this, key, T::class.java)

inline fun <reified T : Parcelable> Intent.getParcelableExtraOfType(key : String) : T? =
    IntentCompat.getParcelableExtra(this, key, T::class.java)

inline fun <reified T : Parcelable> Intent.getParcelableArrayExtraOfType(key : String) : Array<T> =
    IntentCompat.getParcelableArrayExtra(this, key, T::class.java)

inline fun <reified T : Parcelable> Intent.getParcelableArrayListExtraOfType(key : String) : ArrayList<T> =
    IntentCompat.getParcelableArrayListExtra(this, key, T::class.java)
