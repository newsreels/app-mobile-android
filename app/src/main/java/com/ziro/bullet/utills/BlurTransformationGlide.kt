package com.ziro.bullet.utills
import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Element
import android.renderscript.Allocation
import android.renderscript.ScriptIntrinsicBlur
import android.renderscript.RenderScript
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class BlurTransformationGlide(private val context: Context, private val radius: Int = 25, private val sampling: Int = 1) : BitmapTransformation() {

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val width = toTransform.width
        val height = toTransform.height
        val inputBitmap = pool.get(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(inputBitmap)
        canvas.drawBitmap(toTransform, 0f, 0f, null)

        val outputBitmap = pool.get(width / sampling, height / sampling, Bitmap.Config.ARGB_8888)

        val renderScript = RenderScript.create(context)
        val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

        val allocationIn = Allocation.createFromBitmap(renderScript, inputBitmap)
        val allocationOut = Allocation.createFromBitmap(renderScript, outputBitmap)

        scriptIntrinsicBlur.apply {
            setRadius(if (radius in 1..25) radius.toFloat() else 25f)
            setInput(allocationIn)
            forEach(allocationOut)
        }
        allocationOut.copyTo(outputBitmap)
        renderScript.destroy()
        inputBitmap.recycle()

        return outputBitmap
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("blurTransformation $radius $sampling".toByteArray(Key.CHARSET))
    }
}
