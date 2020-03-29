package me.ibrahimsn.lib

import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Created by BrookMG on 3/29/2020 in me.ibrahimsn.lib
inside the project SmoothBottomBar .
 */
class Util {

    companion object {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun roundedRect(
            rect: RectF,
            rx: Float, ry: Float,
            tl: Boolean, tr: Boolean, br: Boolean, bl: Boolean
        ): Path {
            val path = Path();
            val corners: FloatArray = floatArrayOf(
                (if (tl) rx else 0f), (if (tl) ry else 0f),
                (if (tr) rx else 0f), (if (tr) ry else 0f),
                (if (br) rx else 0f), (if (br) ry else 0f),
                (if (bl) rx else 0f), (if (bl) ry else 0f)
            );

            path.addRoundRect(rect, corners, Path.Direction.CW)
            return path
        }

    }

}