package com.example.task.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class Util {
    companion object{
        public fun bitmapDescriptorFromVector(context: Context?, vectorResId: Int): BitmapDescriptor? {
            return ContextCompat.getDrawable(context!!, vectorResId)?.run {
                val width = 100
                val height = 100
                setBounds(0, 0, width, height)
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                draw(Canvas(bitmap))
                BitmapDescriptorFactory.fromBitmap(bitmap)
            }
        }
    }
}