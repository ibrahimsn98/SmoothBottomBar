package me.ibrahimsn.lib

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import me.ibrahimsn.lib.Constants.WHITE_COLOR_HEX
import kotlin.math.abs

class SmoothBottomBar : View {

    /**
     * Default attribute values
     */
    private var barBackgroundColor = Color.parseColor(WHITE_COLOR_HEX)
    private var barIndicatorColor = Color.parseColor("#2DFFFFFF")
    private var barSideMargins = d2p(10f)

    private var itemPadding = d2p(10f)
    private var itemAnimDuration = 300L

    private var itemIconSize = d2p(18f)
    private var itemIconMargin = d2p(4f)
    private var itemIconTint = Color.parseColor("#C8FFFFFF")
    private var itemIconTintActive = Color.parseColor(WHITE_COLOR_HEX)

    private var itemTextColor = Color.parseColor(WHITE_COLOR_HEX)
    private var itemTextSize = d2p(11.0f)
    private var itemFontFamily = 0

    /**
     * Dynamic variables
     */
    private var itemWidth = 0f
    private var activeItem = 0
    private var currentIconTint = itemIconTintActive
    private var indicatorLocation = barSideMargins

    private var items = listOf<BottomBarItem>()

    var onItemSelected: (Int) -> Unit = {}
    var onItemReselected: (Int) -> Unit = {}

    private val rect = RectF()

    private val paintIndicator = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = barIndicatorColor
    }

    private val paintText= Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = itemTextColor
        textSize = itemTextSize
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.SmoothBottomBar, 0, 0)
        barBackgroundColor = typedArray.getColor(R.styleable.SmoothBottomBar_backgroundColor, this.barBackgroundColor)
        barIndicatorColor = typedArray.getColor(R.styleable.SmoothBottomBar_indicatorColor, this.barIndicatorColor)
        barSideMargins = typedArray.getDimension(R.styleable.SmoothBottomBar_sideMargins, this.barSideMargins)
        itemPadding = typedArray.getDimension(R.styleable.SmoothBottomBar_itemPadding, this.itemPadding)
        itemTextColor = typedArray.getColor(R.styleable.SmoothBottomBar_textColor, this.itemTextColor)
        itemTextSize = typedArray.getDimension(R.styleable.SmoothBottomBar_textSize, this.itemTextSize)
        itemIconSize = typedArray.getDimension(R.styleable.SmoothBottomBar_iconSize, this.itemIconSize)
        itemIconTint = typedArray.getColor(R.styleable.SmoothBottomBar_iconTint, this.itemIconTint)
        itemIconTintActive = typedArray.getColor(R.styleable.SmoothBottomBar_iconTintActive, this.itemIconTintActive)
        activeItem = typedArray.getInt(R.styleable.SmoothBottomBar_activeItem, this.activeItem)
        itemFontFamily = typedArray.getResourceId(R.styleable.SmoothBottomBar_itemFontFamily, this.itemFontFamily)
        itemAnimDuration = typedArray.getInt(R.styleable.SmoothBottomBar_duration, this.itemAnimDuration.toInt()).toLong()
        items = BottomBarParser(context, typedArray.getResourceId(R.styleable.SmoothBottomBar_menu, 0)).parse()
        typedArray.recycle()

        setBackgroundColor(barBackgroundColor)

        // Update default attribute values
        paintIndicator.color = barIndicatorColor
        paintText.color = itemTextColor
        paintText.textSize = itemTextSize

        if (itemFontFamily != 0)
            paintText.typeface = ResourcesCompat.getFont(context, itemFontFamily)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        var lastX = barSideMargins
        itemWidth = (width - barSideMargins * 2) / items.size

        for (item in items) {
            // Prevent text overflow by shortening the item title
            var shorted = false
            while (paintText.measureText(item.title) > itemWidth - itemIconSize - itemIconMargin - (itemPadding*2)) {
                item.title = item.title.dropLast(1)
                shorted = true
            }

            // Add ellipsis character to item text if it is shorted
            if (shorted) {
                item.title = item.title.dropLast(1)
                item.title += context.getString(R.string.ellipsis)
            }

            item.rect = RectF(lastX, 0f, itemWidth + lastX, height.toFloat())
            lastX += itemWidth
        }

        // Set initial active item
        setActiveItem(activeItem)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val textHeight = (paintText.descent() + paintText.ascent()) / 2

        for ((i, item) in items.withIndex()) {
            val textLength = paintText.measureText(item.title)

            item.icon.mutate()
            item.icon.setBounds(item.rect.centerX().toInt() - itemIconSize.toInt() / 2 - ((textLength/2) * (1-(255 - item.alpha) / 255f)).toInt(),
                height / 2 - itemIconSize.toInt() / 2,
                item.rect.centerX().toInt() + itemIconSize.toInt() / 2 - ((textLength/2) * (1-(255 - item.alpha) / 255f)).toInt(),
                height / 2 + itemIconSize.toInt() / 2)

            this.paintText.alpha = item.alpha

            DrawableCompat.setTint(item.icon , if (i == activeItem) currentIconTint else itemIconTint)
            item.icon.draw(canvas)

            canvas.drawText(item.title, item.rect.centerX() + itemIconSize/2 + itemIconMargin,
                item.rect.centerY() - textHeight, paintText)
        }

        // Draw indicator
        rect.left = indicatorLocation
        rect.top = items[activeItem].rect.centerY() - itemIconSize/2 - itemPadding
        rect.right = indicatorLocation + itemWidth
        rect.bottom = items[activeItem].rect.centerY() + itemIconSize/2 + itemPadding
        canvas.drawRoundRect(rect, 20f, 20f, paintIndicator)
    }

    /**
     * Handle item clicks
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP && abs(event.downTime - event.eventTime) < 500)
            for ((itemId, item) in items.withIndex())
                if (item.rect.contains(event.x, event.y))
                    if (itemId != this.activeItem) {
                        setActiveItem(itemId)
                        onItemSelected(itemId)
                    } else
                        onItemReselected(itemId)

        return true
    }

    fun setActiveItem(pos: Int) {
        activeItem = pos

        animateIndicator(pos)

        for ((i, item) in items.withIndex())
            animateAlpha(item, if (i == pos) 255 else 0)

        animateIconTint()
    }

    private fun animateAlpha(item: BottomBarItem, to: Int) {
        val animator = ValueAnimator.ofInt(item.alpha, to)
        animator.duration = itemAnimDuration

        animator.addUpdateListener {
            val value = it.animatedValue as Int
            item.alpha = value
            invalidate()
        }

        animator.start()
    }

    private fun animateIndicator(pos: Int) {
        val animator = ValueAnimator.ofFloat(indicatorLocation, items[pos].rect.left)
        animator.duration = itemAnimDuration
        animator.interpolator = DecelerateInterpolator()

        animator.addUpdateListener { animation ->
            indicatorLocation = animation.animatedValue as Float
        }

        animator.start()
    }

    private fun animateIconTint() {
        val animator = ValueAnimator.ofObject(ArgbEvaluator(), itemIconTint, itemIconTintActive)
        animator.duration = itemAnimDuration
        animator.addUpdateListener {
            currentIconTint = it.animatedValue as Int
        }

        animator.start()
    }

    private fun d2p(dp: Float): Float {
        return resources.displayMetrics.densityDpi.toFloat() / 160.toFloat() * dp
    }
}