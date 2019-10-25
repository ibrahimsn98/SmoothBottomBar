package me.ibrahimsn.lib

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.view.MotionEvent
import android.annotation.SuppressLint
import android.animation.ArgbEvaluator
import android.graphics.*
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.math.abs

class SmoothBottomBar : View {

    // Default attribute values
    private var barBackgroundColor = Color.parseColor("#FFFFFF")
    private var barIndicatorColor = Color.parseColor("#2DFFFFFF")
    private var barSideMargins = d2p(10f)

    private var itemPadding = d2p(10f)

    private var itemIconSize = d2p(18f)
    private var itemIconMargin = d2p(4f)
    private var itemIconTint = Color.parseColor("#C8FFFFFF")
    private var itemIconTintActive = Color.parseColor("#FFFFFF")

    private var itemTextColor = Color.parseColor("#FFFFFF")
    private var itemTextSize = d2p(11.0f)
    private var itemFontFamily = 0

    // Dynamic variables
    private var itemWidth = 0f
    private var activeItem = 0
    private var lastActiveItem = 0
    private var currentIconTint = itemIconTintActive
    private var indicatorLocation = barSideMargins
    private var iconMover = 0f
    private var textAlpha = 255

    private var items = listOf<BottomBarItem>()
    private var callback: BottomBarCallback? = null

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
            while (paintText.measureText(item.title) > itemWidth - itemIconSize - itemIconMargin - (itemPadding*2))
                item.title = item.title.dropLast(1)

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

            when (i) {
                activeItem -> {
                    item.icon.setBounds(item.rect.centerX().toInt() - itemIconSize.toInt() / 2 - ((textLength/2) * (1-iconMover)).toInt(),
                        height / 2 - itemIconSize.toInt() / 2,
                        item.rect.centerX().toInt() + itemIconSize.toInt() / 2 - ((textLength/2) * (1-iconMover)).toInt(),
                        height / 2 + itemIconSize.toInt() / 2)

                    this.paintText.alpha = textAlpha
                }
                lastActiveItem -> {
                    item.icon.setBounds(item.rect.centerX().toInt() - itemIconSize.toInt() / 2 - ((textLength/2) * iconMover).toInt(),
                        height / 2 - itemIconSize.toInt() / 2,
                        item.rect.centerX().toInt() + itemIconSize.toInt() / 2 - ((textLength/2) * iconMover).toInt(),
                        height / 2 + itemIconSize.toInt() / 2)

                    this.paintText.alpha = 255 - textAlpha
                }
                else -> {
                    item.icon.setBounds(item.rect.centerX().toInt() - itemIconSize.toInt() / 2,
                        height / 2 - itemIconSize.toInt() / 2,
                        item.rect.centerX().toInt() + itemIconSize.toInt() / 2,
                        height / 2 + itemIconSize.toInt() / 2)

                    this.paintText.alpha = 0
                }
            }

            DrawableCompat.setTint(item.icon , if (i == activeItem) currentIconTint else itemIconTint)
            item.icon.draw(canvas)

            canvas.drawText(item.title, item.rect.centerX() + itemIconSize/2 + itemIconMargin,
                item.rect.centerY() - textHeight, paintText)
        }

        // Draw indicator
        canvas.drawRoundRect(indicatorLocation,
            items[activeItem].rect.centerY() - itemIconSize/2 - itemPadding,
            indicatorLocation + itemWidth,
            items[activeItem].rect.centerY() + itemIconSize/2 + itemPadding,
            20f, 20f, paintIndicator)
    }

    // Handle item clicks
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP && abs(event.downTime - event.eventTime) < 500)
            for ((i, item) in items.withIndex())
                if (item.rect.contains(event.x, event.y))
                    if (i != this.activeItem) {
                        setActiveItem(i)
                        callback?.onItemSelect(i)
                    } else
                        callback?.onItemReselect(i)

        return true
    }

    fun setActiveItem(pos: Int) {
        activeItem = pos

        animateIndicator(pos)
        animateAlpha(pos)
        animateIconTint()
    }

    private fun animateAlpha(pos: Int) {
        val animator = ValueAnimator.ofInt(0, 255)
        animator.duration = 300

        animator.addUpdateListener {
            val value = it.animatedValue as Int
            textAlpha = value
            iconMover = (255 - value) / 255f
            invalidate()
        }

        animator.doOnEnd {
            lastActiveItem = pos
        }

        animator.start()
    }

    private fun animateIndicator(pos: Int) {
        val animator = ValueAnimator.ofFloat(indicatorLocation, items[pos].rect.left)
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()

        animator.addUpdateListener { animation ->
            indicatorLocation = animation.animatedValue as Float
        }

        animator.start()
    }

    private fun animateIconTint() {
        val animator = ValueAnimator.ofObject(ArgbEvaluator(), itemIconTint, itemIconTintActive)
        animator.duration = 300
        animator.addUpdateListener {
            currentIconTint = it.animatedValue as Int
        }

        animator.start()
    }

    private fun d2p(dp: Float): Float {
        return resources.displayMetrics.densityDpi.toFloat() / 160.toFloat() * dp
    }

    fun setBottomBarCallback(callback: BottomBarCallback) {
        this.callback = callback
    }

    interface BottomBarCallback  {
        fun onItemSelect(pos: Int)
        fun onItemReselect(pos: Int)
    }
}