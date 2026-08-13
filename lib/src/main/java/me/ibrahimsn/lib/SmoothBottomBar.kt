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
import android.util.Log
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.animation.DecelerateInterpolator
import android.widget.PopupMenu
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import me.ibrahimsn.lib.ext.d2p
import kotlin.math.roundToInt

class SmoothBottomBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.SmoothBottomBarStyle
) : View(context, attrs, defStyleAttr) {
    // Dynamic Variables
    private var itemWidth: Float = 0F

    private var currentIconTint = itemIconTintActive

    private var indicatorLocation = barSideMargins
    private var _iconBackgroundColor: Int = Color.TRANSPARENT
    private val iconBackgroundPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = _iconBackgroundColor
    }
    // New padding backing (in PX)
    @Dimension
    private var _iconBackgroundPadding: Float = context.d2p(DEFAULT_ICON_BG_PADDING)

    // Public accessors
    var iconBackgroundColor: Int
        @ColorInt get() = _iconBackgroundColor
        set(@ColorInt value) {
            _iconBackgroundColor = value
            iconBackgroundPaint.color = value
            invalidate()
        }

    var iconBackgroundPadding: Float
        @Dimension get() = _iconBackgroundPadding
        set(@Dimension value) {
            _iconBackgroundPadding = value
            invalidate()
        }

    private val rect = RectF()

    // Cache for performance optimization
    private val iconBackgroundRect = RectF()
    private var cachedTextHeight: Float = 0f

    private var items = listOf<BottomBarItem>()

    // Attribute Defaults
    @ColorInt
    private var _barBackgroundColor = Color.WHITE

    @ColorInt
    private var _barIndicatorColor = Color.parseColor(DEFAULT_INDICATOR_COLOR)

    @Dimension
    private var _barIndicatorRadius = context.d2p(DEFAULT_CORNER_RADIUS)

    @Dimension
    private var _barSideMargins = context.d2p(DEFAULT_SIDE_MARGIN)

    @Dimension
    private var _barCornerRadius = context.d2p(DEFAULT_BAR_CORNER_RADIUS)

    private var _barCorners = DEFAULT_BAR_CORNERS

    @Dimension
    private var _itemPadding = context.d2p(DEFAULT_ITEM_PADDING)

    @Dimension
    private var _itemSpacing = context.d2p(DEFAULT_ITEM_SPACING)

    private var _itemAnimDuration = DEFAULT_ANIM_DURATION

    @Dimension
    private var _itemIconSize = context.d2p(DEFAULT_ICON_SIZE)

    @Dimension
    private var _itemIconMargin = context.d2p(DEFAULT_ICON_MARGIN)

    @ColorInt
    private var _itemIconTint = Color.parseColor(DEFAULT_TINT)

    @ColorInt
    private var _itemIconTintActive = Color.WHITE

    @ColorInt
    private var _itemTextColor = Color.WHITE

    @ColorInt
    private var _itemBadgeColor = Color.RED

    @Dimension
    private var _itemTextSize = context.d2p(DEFAULT_TEXT_SIZE)

    @FontRes
    private var _itemFontFamily: Int = INVALID_RES

    @XmlRes
    private var _itemMenuRes: Int = INVALID_RES

    private var _itemActiveIndex: Int = 0

    lateinit var menu: Menu


    private val badgeIndices = HashSet<Int>()

    // Core Attributes
    var barBackgroundColor: Int
        @ColorInt get() = _barBackgroundColor
        set(@ColorInt value) {
            _barBackgroundColor = value
            paintBackground.color = value
            invalidate()
        }

    var barIndicatorColor: Int
        @ColorInt get() = _barIndicatorColor
        set(@ColorInt value) {
            _barIndicatorColor = value
            paintIndicator.color = value
            invalidate()
        }

    var barIndicatorRadius: Float
        @Dimension get() = _barIndicatorRadius
        set(@Dimension value) {
            _barIndicatorRadius = value
            invalidate()
        }

    var barSideMargins: Float
        @Dimension get() = _barSideMargins
        set(@Dimension value) {
            _barSideMargins = value
            invalidate()
        }

    var barCornerRadius: Float
        @Dimension get() = _barCornerRadius
        set(@Dimension value) {
            _barCornerRadius = value
            invalidate()
        }

    var barCorners: Int
        get() = _barCorners
        set(value) {
            _barCorners = value
            invalidate()
        }

    var itemTextSize: Float
        @Dimension get() = _itemTextSize
        set(@Dimension value) {
            _itemTextSize = value
            paintText.textSize = value
            invalidate()
        }

    var itemTextColor: Int
        @ColorInt get() = _itemTextColor
        set(@ColorInt value) {
            _itemTextColor = value
            paintText.color = value
            invalidate()
        }
    var itemBadgeColor: Int
        @ColorInt get() = _itemBadgeColor
        set(@ColorInt value) {
            _itemBadgeColor = value
            badgePaint.color = value
            invalidate()
        }

    var itemPadding: Float
        @Dimension get() = _itemPadding
        set(@Dimension value) {
            _itemPadding = value
            invalidate()
        }

    var itemSpacing: Float
        @Dimension get() = _itemSpacing
        set(@Dimension value) {
            _itemSpacing = value
            invalidate()
        }

    var itemAnimDuration: Long
        get() = _itemAnimDuration
        set(value) {
            _itemAnimDuration = value
        }

    var itemIconSize: Float
        @Dimension get() = _itemIconSize
        set(@Dimension value) {
            _itemIconSize = value
            invalidate()
        }

    var itemIconMargin: Float
        @Dimension get() = _itemIconMargin
        set(@Dimension value) {
            _itemIconMargin = value
            invalidate()
        }

    var itemIconTint: Int
        @ColorInt get() = _itemIconTint
        set(@ColorInt value) {
            _itemIconTint = value
            invalidate()
        }

    var itemIconTintActive: Int
        @ColorInt get() = _itemIconTintActive
        set(@ColorInt value) {
            _itemIconTintActive = value
            invalidate()
        }

    var itemFontFamily: Int
        @FontRes get() = _itemFontFamily
        set(@FontRes value) {
            _itemFontFamily = value
            if (value != INVALID_RES) {
                paintText.typeface = ResourcesCompat.getFont(context, value)
                invalidate()
            }
        }

    var itemMenuRes: Int
        @XmlRes get() = _itemMenuRes
        set(value) {
            _itemMenuRes = value
            val popupMenu = PopupMenu(context, null)
            popupMenu.inflate(value)
            this.menu = popupMenu.menu
            if (value != INVALID_RES) {
                items = BottomBarParser(context, value).parse()
                invalidate()
            }
        }

    var itemActiveIndex: Int
        get() = _itemActiveIndex
        set(value) {
            _itemActiveIndex = value
            applyItemActiveIndex()
        }


    // Listeners
    var onItemSelectedListener: OnItemSelectedListener? = null

    var onItemReselectedListener: OnItemReselectedListener? = null

    var onItemSelected: ((Int) -> Unit)? = null

    var onItemReselected: ((Int) -> Unit)? = null

    // Paints
    private val paintBackground = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = barIndicatorColor
    }

    private val paintIndicator = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = barIndicatorColor
    }

    private val badgePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = itemBadgeColor
    }

    private val paintText = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = itemTextColor
        textSize = itemTextSize
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private var exploreByTouchHelper: AccessibleExploreByTouchHelper

    init {
        obtainStyledAttributes(attrs, defStyleAttr)
        exploreByTouchHelper = AccessibleExploreByTouchHelper(this, items, ::onClickAction)

        ViewCompat.setAccessibilityDelegate(this, exploreByTouchHelper)

        // Enable hardware acceleration for better performance
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    private fun obtainStyledAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SmoothBottomBar,
            defStyleAttr,
            0
        )

        try {
            iconBackgroundColor = typedArray.getColor(
                R.styleable.SmoothBottomBar_iconBackgroundColor,
                iconBackgroundColor
            )
            iconBackgroundPadding = typedArray.getDimension(
                R.styleable.SmoothBottomBar_iconBackgroundPadding,
                iconBackgroundPadding
            )
            barBackgroundColor = typedArray.getColor(
                R.styleable.SmoothBottomBar_backgroundColor,
                barBackgroundColor
            )
            barIndicatorColor = typedArray.getColor(
                R.styleable.SmoothBottomBar_indicatorColor,
                barIndicatorColor
            )
            barIndicatorRadius = typedArray.getDimension(
                R.styleable.SmoothBottomBar_indicatorRadius,
                barIndicatorRadius
            )
            barSideMargins = typedArray.getDimension(
                R.styleable.SmoothBottomBar_sideMargins,
                barSideMargins
            )
            barCornerRadius = typedArray.getDimension(
                R.styleable.SmoothBottomBar_cornerRadius,
                barCornerRadius
            )
            barCorners = typedArray.getInteger(
                R.styleable.SmoothBottomBar_corners,
                barCorners
            )
            itemPadding = typedArray.getDimension(
                R.styleable.SmoothBottomBar_itemPadding,
                itemPadding
            )
            itemSpacing = typedArray.getDimension(
                R.styleable.SmoothBottomBar_itemSpacing,
                itemSpacing
            )
            itemTextColor = typedArray.getColor(
                R.styleable.SmoothBottomBar_textColor,
                itemTextColor
            )
            itemTextSize = typedArray.getDimension(
                R.styleable.SmoothBottomBar_textSize,
                itemTextSize
            )
            itemIconSize = typedArray.getDimension(
                R.styleable.SmoothBottomBar_iconSize,
                itemIconSize
            )
            itemIconMargin = typedArray.getDimension(
                R.styleable.SmoothBottomBar_iconMargin,
                itemIconMargin
            )
            itemIconTint = typedArray.getColor(
                R.styleable.SmoothBottomBar_iconTint,
                itemIconTint
            )
            itemBadgeColor = typedArray.getColor(
                R.styleable.SmoothBottomBar_badgeColor,
                itemBadgeColor
            )
            itemIconTintActive = typedArray.getColor(
                R.styleable.SmoothBottomBar_iconTintActive,
                itemIconTintActive
            )
            itemActiveIndex = typedArray.getInt(
                R.styleable.SmoothBottomBar_activeItem,
                itemActiveIndex
            )
            itemFontFamily = typedArray.getResourceId(
                R.styleable.SmoothBottomBar_itemFontFamily,
                itemFontFamily
            )
            itemAnimDuration = typedArray.getInt(
                R.styleable.SmoothBottomBar_duration,
                itemAnimDuration.toInt()
            ).toLong()
            itemMenuRes = typedArray.getResourceId(
                R.styleable.SmoothBottomBar_menu,
                itemMenuRes
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse SmoothBottomBar attributes", e)
        } finally {
            typedArray.recycle()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateItemBounds()

        // Set initial active item state without animation
        if (items.isNotEmpty()) {
            for ((index, item) in items.withIndex()) {
                item.alpha = if (index == itemActiveIndex) OPAQUE else TRANSPARENT
            }
            indicatorLocation = items[itemActiveIndex].rect.centerX() - itemWidth / 2
            currentIconTint = itemIconTintActive
            invalidate()
        }
    }

    // Width the indicator should hug: icon + icon/text margin + text, plus
    // itemPadding on each side - not the item's full (leftover-space) rect width.
    private fun indicatorWidthFor(item: BottomBarItem): Float {
        return itemIconSize + itemIconMargin + paintText.measureText(item.title) + itemPadding * 2
    }

    private fun calculateItemBounds() {
        if (items.isEmpty() || width == 0 || height == 0) return

        // Calculate total spacing between items
        val totalSpacing = if (items.size > 1) itemSpacing * (items.size - 1) else 0f

        // Calculate available width after margins and spacing
        val totalAvailableWidth = width - (barSideMargins * 2) - totalSpacing

        // Calculate minimum width for inactive items (icon + padding only)
        val inactiveItemWidth = itemIconSize + (itemPadding * 2)

        // Calculate total width needed for inactive items
        val totalInactiveWidth = inactiveItemWidth * (items.size - 1)

        // Active item gets remaining space (ensures text fits and spreads across bar)
        val activeItemWidth = totalAvailableWidth - totalInactiveWidth

        var lastX = barSideMargins

        // reverse items layout order if layout direction is RTL
        val isRTL = layoutDirection == LAYOUT_DIRECTION_RTL
        val itemsToLayout = if (isRTL) items.reversed() else items

        for ((index, item) in itemsToLayout.withIndex()) {
            val actualIndex = if (isRTL) items.size - 1 - index else index

            // Assign width based on active state
            val currentItemWidth = if (actualIndex == itemActiveIndex) {
                activeItemWidth
            } else {
                inactiveItemWidth
            }

            item.rect.set(lastX, 0f, currentItemWidth + lastX, height.toFloat())
            lastX += currentItemWidth + itemSpacing
        }

        // Update itemWidth to match the active item's content width for the indicator
        if (_itemActiveIndex in items.indices) {
            itemWidth = indicatorWidthFor(items[_itemActiveIndex])
        }

        // Cache text height calculation
        cachedTextHeight = (paintText.descent() + paintText.ascent()) / 2
    }

    @JvmName("setBadge")
    fun setBadge(pos: Int) {
        badgeIndices.add(pos)
        invalidate()
    }

    @JvmName("removeBadge")
    fun removeBadge(pos: Int) {
        badgeIndices.remove(pos)
        invalidate()
    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw background
        if (barCornerRadius > 0) {
            canvas.drawRoundRect(
                0f, 0f,
                width.toFloat(),
                height.toFloat(),
                minOf(barCornerRadius.toFloat(), height.toFloat() / 2),
                minOf(barCornerRadius.toFloat(), height.toFloat() / 2),
                paintBackground
            )

            if (barCorners != ALL_CORNERS) {

                if ((barCorners and TOP_LEFT_CORNER) != TOP_LEFT_CORNER) {
                    // Draw a box to cover the curve on the top left
                    canvas.drawRect(
                        0f, 0f, width.toFloat() / 2,
                        height.toFloat() / 2, paintBackground
                    )
                }

                if ((barCorners and TOP_RIGHT_CORNER) != TOP_RIGHT_CORNER) {
                    // Draw a box to cover the curve on the top right
                    canvas.drawRect(
                        width.toFloat() / 2, 0f, width.toFloat(),
                        height.toFloat() / 2, paintBackground
                    )
                }

                if ((barCorners and BOTTOM_LEFT_CORNER) != BOTTOM_LEFT_CORNER) {
                    // Draw a box to cover the curve on the bottom left
                    canvas.drawRect(
                        0f, height.toFloat() / 2, width.toFloat() / 2,
                        height.toFloat(), paintBackground
                    )
                }

                if ((barCorners and BOTTOM_RIGHT_CORNER) != BOTTOM_RIGHT_CORNER) {
                    // Draw a box to cover the curve on the bottom right
                    canvas.drawRect(
                        width.toFloat() / 2, height.toFloat() / 2, width.toFloat(),
                        height.toFloat(), paintBackground
                    )
                }

            }

        } else {
            canvas.drawRect(
                0f, 0f,
                width.toFloat(),
                height.toFloat(),
                paintBackground
            )
        }

        // Draw indicator
        rect.left = indicatorLocation
        rect.top = items[itemActiveIndex].rect.centerY() - itemIconSize / 2 - itemPadding
        rect.right = indicatorLocation + itemWidth
        rect.bottom = items[itemActiveIndex].rect.centerY() + itemIconSize / 2 + itemPadding

        canvas.drawRoundRect(
            rect,
            barIndicatorRadius,
            barIndicatorRadius,
            paintIndicator
        )

        // Use cached text height
        val textHeight = cachedTextHeight

        // Pre-calculate common values
        val halfHeight = height / 2
        val halfIconSize = itemIconSize.toInt() / 2
        val opaqueFloat = OPAQUE.toFloat()

        if (layoutDirection == LAYOUT_DIRECTION_RTL) {
            for ((index, item) in items.withIndex()) {
                val textLength = paintText.measureText(item.title)
                val alphaFactor = (1 - (OPAQUE - item.alpha) / opaqueFloat)
                val textOffset = ((textLength / 2) * alphaFactor).toInt()
                val centerX = item.rect.centerX().toInt()

                item.icon.mutate()
                item.icon.setBounds(
                    centerX - halfIconSize + textOffset,
                    halfHeight - halfIconSize,
                    centerX + halfIconSize + textOffset,
                    halfHeight + halfIconSize
                )
                if (index != itemActiveIndex) {
                    drawIconBackground(item, index, canvas)
                }
                tintAndDrawIcon(item, index, canvas)
                if (badgeIndices.contains(index)) {
                    canvas.drawCircle(
                        centerX + halfIconSize.toFloat() + ((textLength / 2) * alphaFactor),
                        halfHeight.toFloat() - halfIconSize.toFloat(),
                        10f,
                        badgePaint
                    )
                }
                paintText.alpha = item.alpha
                canvas.drawText(
                    item.title,
                    item.rect.centerX() - (itemIconSize / 2 + itemIconMargin),
                    item.rect.centerY() - textHeight, paintText
                )
            }

        } else {
            for ((index, item) in items.withIndex()) {
                val textLength = paintText.measureText(item.title)
                val alphaFactor = (1 - (OPAQUE - item.alpha) / opaqueFloat)
                val textOffset = ((textLength / 2) * alphaFactor).toInt()
                val centerX = item.rect.centerX().toInt()

                item.icon.mutate()
                item.icon.setBounds(
                    centerX - halfIconSize - textOffset,
                    halfHeight - halfIconSize,
                    centerX + halfIconSize - textOffset,
                    halfHeight + halfIconSize
                )
                //set badge indicator

                if (index != itemActiveIndex) {
                    drawIconBackground(item, index, canvas)
                }
                tintAndDrawIcon(item, index, canvas)
                if (badgeIndices.contains(index)) {
                    canvas.drawCircle(
                        centerX - halfIconSize.toFloat() - ((textLength / 2) * alphaFactor),
                        halfHeight.toFloat() - halfIconSize.toFloat(),
                        10f,
                        badgePaint
                    )
                }
                paintText.alpha = item.alpha
                canvas.drawText(
                    item.title,
                    item.rect.centerX() + itemIconSize / 2 + itemIconMargin,
                    item.rect.centerY() - textHeight, paintText
                )
            }
        }
    }
    // New: draw icon background behind non-active icons

    private fun drawIconBackground(
        item: BottomBarItem,
        index: Int,
        canvas: Canvas
    ) {
        // Skip if not requested
        if (iconBackgroundColor == Color.TRANSPARENT) return

        val cx = item.rect.centerX()
        val cy = height / 2f
        val half = itemIconSize / 2f

        val left = cx - half - _iconBackgroundPadding
        val top = cy - half - _iconBackgroundPadding
        val right = cx + half + _iconBackgroundPadding
        val bottom = cy + half + _iconBackgroundPadding
        val radius = half + _iconBackgroundPadding

        // Reuse cached RectF object to avoid allocation
        iconBackgroundRect.set(left, top, right, bottom)
        canvas.drawRoundRect(iconBackgroundRect, radius, radius, iconBackgroundPaint)
    }
    private fun tintAndDrawIcon(
        item: BottomBarItem,
        index: Int,
        canvas: Canvas
    ) {
        DrawableCompat.setTint(
            item.icon,
            if (index == itemActiveIndex) currentIconTint else itemIconTint
        )

        item.icon.draw(canvas)
    }

    /**
     * Handle item clicks
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_UP -> {
                for ((i, item) in items.withIndex()) {
                    if (item.rect.contains(event.x, event.y)) {
                        onClickAction(i)
                        break
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun dispatchHoverEvent(event: MotionEvent): Boolean {
        return exploreByTouchHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event)
    }

    private fun onClickAction(viewId: Int) {
        exploreByTouchHelper.invalidateVirtualView(viewId)
        if (viewId != itemActiveIndex) {
            itemActiveIndex = viewId
            onItemSelected?.invoke(viewId)
            onItemSelectedListener?.onItemSelect(viewId)
        } else {
            onItemReselected?.invoke(viewId)
            onItemReselectedListener?.onItemReselect(viewId)
        }
        exploreByTouchHelper.sendEventForVirtualView(
            viewId,
            AccessibilityEvent.TYPE_VIEW_CLICKED
        )
    }

    private fun applyItemActiveIndex() {
        if (items.isNotEmpty()) {
            // Snapshot current bounds before recalculating, so item rects can
            // animate from where they are now instead of jumping straight to
            // the new layout (calculateItemBounds() below mutates them in place).
            val oldRects = items.map { RectF(it.rect) }
            val oldIndicatorLocation = indicatorLocation
            val oldItemWidth = itemWidth

            // This can fire before the view has ever been measured (e.g. a
            // NavController destination-changed listener invokes immediately
            // on registration, during onCreate()). calculateItemBounds() is a
            // no-op with no size yet, so there's nothing meaningful to animate
            // toward - onSizeChanged() will set the correct bounds directly
            // once real layout happens.
            val hadValidLayout = width > 0 && height > 0

            // Recalculate item bounds with new active index
            calculateItemBounds()

            for ((index, item) in items.withIndex()) {
                if (index == itemActiveIndex) {
                    // Set initial state immediately if not animated
                    if (item.alpha == TRANSPARENT) {
                        item.alpha = OPAQUE
                    }
                    animateAlpha(item, OPAQUE)
                } else {
                    animateAlpha(item, TRANSPARENT)
                }
            }

            if (hadValidLayout) {
                // Snapshot the newly calculated bounds as the animation target,
                // then hold items at their old bounds until the animator below runs.
                val targetRects = items.map { RectF(it.rect) }
                val targetItemWidth = itemWidth
                val targetIndicatorLocation = items[itemActiveIndex].rect.centerX() - targetItemWidth / 2
                items.forEachIndexed { index, item -> item.rect.set(oldRects[index]) }
                itemWidth = oldItemWidth

                // Animate item bounds, indicator position and indicator width smoothly
                ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = itemAnimDuration
                    interpolator = DecelerateInterpolator()
                    addUpdateListener { animation ->
                        val progress = animation.animatedValue as Float
                        for ((index, item) in items.withIndex()) {
                            val old = oldRects[index]
                            val target = targetRects[index]
                            item.rect.set(
                                old.left + (target.left - old.left) * progress,
                                old.top + (target.top - old.top) * progress,
                                old.right + (target.right - old.right) * progress,
                                old.bottom + (target.bottom - old.bottom) * progress
                            )
                        }
                        itemWidth = oldItemWidth + (targetItemWidth - oldItemWidth) * progress
                        indicatorLocation = oldIndicatorLocation +
                                (targetIndicatorLocation - oldIndicatorLocation) * progress
                        invalidate()
                    }
                    start()
                }
            }

            ValueAnimator.ofObject(ArgbEvaluator(), itemIconTint, itemIconTintActive).apply {
                duration = itemAnimDuration
                addUpdateListener {
                    currentIconTint = it.animatedValue as Int
                }
                start()
            }
        }
    }

    private fun animateAlpha(item: BottomBarItem, to: Int) {
        ValueAnimator.ofInt(item.alpha, to).apply {
            duration = itemAnimDuration
            addUpdateListener {
                val value = it.animatedValue as Int
                item.alpha = value
                invalidate()
            }
            start()
        }
    }

    fun setupWithNavController(menu: Menu, navController: NavController) {
        NavigationComponentHelper.setupWithNavController(menu, this, navController)
    }

    fun setupWithNavController(navController: NavController) {
        NavigationComponentHelper.setupWithNavController(this.menu, this, navController)
        Navigation.setViewNavController(this, navController)
    }

    fun setSelectedItem(pos: Int) {
        try {
            this.itemActiveIndex = pos
            NavigationUI.onNavDestinationSelected(this.menu.getItem(pos), this.findNavController())
            invalidate()
        } catch (e: Exception) {
            throw IllegalStateException("Failed to set selected item at position $pos", e)
        }
    }

    /**
     * Created by Vladislav Perevedentsev on 29.07.2020.
     *
     * Just call [SmoothBottomBar.setOnItemSelectedListener] to override [onItemSelectedListener]
     *
     * @sample
     * setOnItemSelectedListener { position ->
     *     //TODO: Something
     * }
     */
    fun setOnItemSelectedListener(listener: (position: Int) -> Unit) {
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelect(pos: Int): Boolean {
                listener.invoke(pos)
                return true
            }
        }
    }

    /**
     * Created by Vladislav Perevedentsev on 29.07.2020.
     *
     * Just call [SmoothBottomBar.setOnItemReselectedListener] to override [onItemReselectedListener]
     *
     * @sample
     * setOnItemReselectedListener { position ->
     *     //TODO: Something
     * }
     */
    fun setOnItemReselectedListener(listener: (position: Int) -> Unit) {
        onItemReselectedListener = object : OnItemReselectedListener {
            override fun onItemReselect(pos: Int) {
                listener.invoke(pos)
            }
        }
    }

    companion object {
        private const val TAG = "SmoothBottomBar"
        private const val INVALID_RES = -1
        private const val DEFAULT_INDICATOR_COLOR = "#2DFFFFFF"
        private const val DEFAULT_TINT = "#C8FFFFFF"
        private const val DEFAULT_ICON_BG_PADDING = 6f

        // corner flags
        private const val NO_CORNERS = 0;
        private const val TOP_LEFT_CORNER = 1;
        private const val TOP_RIGHT_CORNER = 2;
        private const val BOTTOM_RIGHT_CORNER = 4;
        private const val BOTTOM_LEFT_CORNER = 8;
        private const val ALL_CORNERS = 15;

        private const val DEFAULT_SIDE_MARGIN = 10f
        private const val DEFAULT_ITEM_PADDING = 10f
        private const val DEFAULT_ITEM_SPACING = 8f
        private const val DEFAULT_ANIM_DURATION = 200L
        private const val DEFAULT_ICON_SIZE = 18F
        private const val DEFAULT_ICON_MARGIN = 4F
        private const val DEFAULT_TEXT_SIZE = 11F
        private const val DEFAULT_CORNER_RADIUS = 20F
        private const val DEFAULT_BAR_CORNER_RADIUS = 0F
        private const val DEFAULT_BAR_CORNERS = TOP_LEFT_CORNER or TOP_RIGHT_CORNER

        private const val OPAQUE = 255
        private const val TRANSPARENT = 0
    }
}
