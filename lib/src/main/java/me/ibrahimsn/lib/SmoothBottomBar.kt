package me.ibrahimsn.lib

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

    private var activeAnimator: ValueAnimator? = null

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
    private var _itemBadgeRadius = context.d2p(DEFAULT_BADGE_RADIUS)

    @Dimension
    private var _itemTextSize = context.d2p(DEFAULT_TEXT_SIZE)

    @FontRes
    private var _itemFontFamily: Int = INVALID_RES

    @XmlRes
    private var _itemMenuRes: Int = INVALID_RES

    private var _itemActiveIndex: Int = 0

    var menu: Menu = PopupMenu(context, null).menu


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
            calculateItemBounds()
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

    var itemBadgeRadius: Float
        @Dimension get() = _itemBadgeRadius
        set(@Dimension value) {
            _itemBadgeRadius = value
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
                calculateItemBounds()
                invalidate()
            }
        }

    var itemMenuRes: Int
        @XmlRes get() = _itemMenuRes
        set(value) {
            _itemMenuRes = value
            if (value != INVALID_RES) {
                val popupMenu = PopupMenu(context, null)
                popupMenu.inflate(value)
                this.menu = popupMenu.menu
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
        color = barBackgroundColor
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

    // ponytail: border color/width are fixed rather than new attributes - this is
    // a punch-hole ring in the bar's own background, add a dedicated badgeBorderColor
    // attribute only if someone actually needs a ring that doesn't match the bar.
    private val badgeBorderWidth = context.d2p(DEFAULT_BADGE_BORDER_WIDTH)

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
        exploreByTouchHelper = AccessibleExploreByTouchHelper(this, items, ::onClickAction) { index ->
            badgeIndices.contains(index)
        }

        ViewCompat.setAccessibilityDelegate(this, exploreByTouchHelper)
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
            itemBadgeRadius = typedArray.getDimension(
                R.styleable.SmoothBottomBar_badgeRadius,
                itemBadgeRadius
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
            if (itemActiveIndex in items.indices) {
                indicatorLocation = indicatorCenterX(items[itemActiveIndex]) - itemWidth / 2
                currentIconTint = itemIconTintActive
            }
            invalidate()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        activeAnimator?.cancel()
        activeAnimator = null
    }

    // The icon+text content isn't actually centered on rect.centerX(): the icon
    // shifts one way and the text label extends the other way by itemIconMargin,
    // so the content's true center is offset from rect.centerX() by half that
    // margin (mirrored in RTL). Centering the indicator here instead of on
    // rect.centerX() keeps its left/right padding around the content equal.
    private fun indicatorCenterX(item: BottomBarItem): Float {
        val margin = itemIconMargin / 2
        return item.rect.centerX() + if (layoutDirection == LAYOUT_DIRECTION_RTL) -margin else margin
    }

    // Width the indicator should hug: icon + icon/text margin + text, plus
    // itemPadding on each side - not the item's full (leftover-space) rect width.
    private fun indicatorWidthFor(item: BottomBarItem): Float {
        return itemIconSize + itemIconMargin + item.titleWidth + itemPadding * 2
    }

    private fun calculateItemBounds() {
        if (items.isEmpty() || width == 0 || height == 0) return

        // Cache each item's measured text width once per layout pass instead
        // of remeasuring in onDraw() every frame - measureText() isn't free.
        for (item in items) {
            item.titleWidth = paintText.measureText(item.title)
        }

        // Calculate minimum width for inactive items (icon + padding only)
        val inactiveItemWidth = itemIconSize + (itemPadding * 2)

        // Active item only needs enough room for its icon + text content,
        // not all the space left over after the inactive items.
        val activeItemWidth = if (itemActiveIndex in items.indices) {
            indicatorWidthFor(items[itemActiveIndex])
        } else {
            inactiveItemWidth
        }

        // Total width the items' own content takes up, excluding spacing/margins
        val totalContentWidth = inactiveItemWidth * (items.size - 1) + activeItemWidth

        // Distribute whatever space is left over as spacing between items, so
        // the bar still spans edge-to-edge instead of the active item hogging
        // space or the row clustering to one side. itemSpacing acts as a floor
        // for tight layouts where there's no leftover space to distribute.
        val effectiveItemSpacing = if (items.size > 1) {
            ((width - (barSideMargins * 2) - totalContentWidth) / (items.size - 1))
                .coerceAtLeast(itemSpacing)
        } else {
            0f
        }

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
            lastX += currentItemWidth + effectiveItemSpacing
        }

        // Update itemWidth to match the active item's content width for the indicator
        if (_itemActiveIndex in items.indices) {
            itemWidth = activeItemWidth
        }

        // Cache text height calculation
        cachedTextHeight = (paintText.descent() + paintText.ascent()) / 2
    }

    @JvmName("setBadge")
    fun setBadge(pos: Int) {
        badgeIndices.add(pos)
        invalidate()
        exploreByTouchHelper.invalidateVirtualView(pos)
    }

    @JvmName("removeBadge")
    fun removeBadge(pos: Int) {
        badgeIndices.remove(pos)
        invalidate()
        exploreByTouchHelper.invalidateVirtualView(pos)
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
        if (itemActiveIndex in items.indices) {
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
        }

        // Use cached text height
        val textHeight = cachedTextHeight

        // Pre-calculate common values
        val halfHeight = height / 2
        val halfIconSize = itemIconSize.toInt() / 2
        val opaqueFloat = OPAQUE.toFloat()

        if (layoutDirection == LAYOUT_DIRECTION_RTL) {
            for (index in items.indices) {
                val item = items[index]
                val textLength = item.titleWidth
                val alphaFactor = (1 - (OPAQUE - item.alpha) / opaqueFloat)
                val textOffset = ((textLength / 2) * alphaFactor).toInt()
                val centerX = item.rect.centerX().toInt()

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
                    val badgeX = centerX + halfIconSize.toFloat() +
                            ((textLength / 2) * alphaFactor) - itemBadgeRadius
                    val badgeY = halfHeight.toFloat() - halfIconSize.toFloat() + itemBadgeRadius
                    canvas.drawCircle(badgeX, badgeY, itemBadgeRadius + badgeBorderWidth, paintBackground)
                    canvas.drawCircle(badgeX, badgeY, itemBadgeRadius, badgePaint)
                }
                paintText.alpha = item.alpha
                canvas.drawText(
                    item.title,
                    item.rect.centerX() - (itemIconSize / 2 + itemIconMargin),
                    item.rect.centerY() - textHeight, paintText
                )
            }

        } else {
            for (index in items.indices) {
                val item = items[index]
                val textLength = item.titleWidth
                val alphaFactor = (1 - (OPAQUE - item.alpha) / opaqueFloat)
                val textOffset = ((textLength / 2) * alphaFactor).toInt()
                val centerX = item.rect.centerX().toInt()

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
                    val badgeX = centerX - halfIconSize.toFloat() -
                            ((textLength / 2) * alphaFactor) + itemBadgeRadius
                    val badgeY = halfHeight.toFloat() - halfIconSize.toFloat() + itemBadgeRadius
                    canvas.drawCircle(badgeX, badgeY, itemBadgeRadius + badgeBorderWidth, paintBackground)
                    canvas.drawCircle(badgeX, badgeY, itemBadgeRadius, badgePaint)
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
        val tint = if (index == itemActiveIndex) currentIconTint else itemIconTint
        if (item.lastAppliedTint != tint) {
            DrawableCompat.setTint(item.icon, tint)
            item.lastAppliedTint = tint
        }
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
            // Cancel any in-flight transition before snapshotting "current"
            // state below, so a second tap mid-animation starts its snapshot
            // from wherever the interrupted animation left things, instead of
            // two animators racing and clobbering the same rect/tint fields.
            activeAnimator?.cancel()

            // Snapshot current bounds/alpha before recalculating, so everything
            // animates from where it is now instead of jumping straight to the
            // new state (calculateItemBounds() below mutates rects in place).
            val oldRects = items.map { RectF(it.rect) }
            val oldIndicatorLocation = indicatorLocation
            val oldItemWidth = itemWidth
            val oldAlphas = items.map { it.alpha }

            // This can fire before the view has ever been measured (e.g. a
            // NavController destination-changed listener invokes immediately
            // on registration, during onCreate()). calculateItemBounds() is a
            // no-op with no size yet, so there's nothing meaningful to animate
            // toward - onSizeChanged() will set the correct bounds directly
            // once real layout happens.
            val hadValidLayout = width > 0 && height > 0

            // Recalculate item bounds with new active index
            calculateItemBounds()

            val targetAlphas = items.indices.map { if (it == itemActiveIndex) OPAQUE else TRANSPARENT }

            if (hadValidLayout) {
                // Snapshot the newly calculated bounds as the animation target,
                // then hold items at their old bounds until the animator below runs.
                val targetRects = items.map { RectF(it.rect) }
                val targetItemWidth = itemWidth
                val targetIndicatorLocation = if (itemActiveIndex in items.indices) {
                    indicatorCenterX(items[itemActiveIndex]) - targetItemWidth / 2
                } else {
                    oldIndicatorLocation
                }
                items.forEachIndexed { index, item -> item.rect.set(oldRects[index]) }
                itemWidth = oldItemWidth

                // Single animator drives bounds, indicator position/width, item
                // alpha and icon tint together off the same progress value, so
                // onDraw()'s icon-shift (derived from item.alpha) and badge
                // position move on the exact same curve as the pill instead of
                // drifting apart on separate, differently-interpolated animators.
                activeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = itemAnimDuration
                    interpolator = DecelerateInterpolator()
                    addUpdateListener { animation ->
                        val progress = animation.animatedValue as Float
                        for (index in items.indices) {
                            val item = items[index]
                            val old = oldRects[index]
                            val target = targetRects[index]
                            item.rect.set(
                                old.left + (target.left - old.left) * progress,
                                old.top + (target.top - old.top) * progress,
                                old.right + (target.right - old.right) * progress,
                                old.bottom + (target.bottom - old.bottom) * progress
                            )
                            item.alpha = (oldAlphas[index] +
                                    (targetAlphas[index] - oldAlphas[index]) * progress).roundToInt()
                        }
                        itemWidth = oldItemWidth + (targetItemWidth - oldItemWidth) * progress
                        indicatorLocation = oldIndicatorLocation +
                                (targetIndicatorLocation - oldIndicatorLocation) * progress
                        currentIconTint = lerpColor(itemIconTint, itemIconTintActive, progress)
                        invalidate()
                    }
                    start()
                }
            } else {
                // No layout yet - nothing to animate toward; jump straight to
                // final state, matching what onSizeChanged() does for the true
                // initial-layout case.
                items.forEachIndexed { index, item -> item.alpha = targetAlphas[index] }
                currentIconTint = itemIconTintActive
            }
        }
    }

    // Hand-rolled instead of ArgbEvaluator to avoid boxing an Int -> Integer
    // on every animation frame.
    private fun lerpColor(from: Int, to: Int, progress: Float): Int {
        val a = (Color.alpha(from) + (Color.alpha(to) - Color.alpha(from)) * progress).roundToInt()
        val r = (Color.red(from) + (Color.red(to) - Color.red(from)) * progress).roundToInt()
        val g = (Color.green(from) + (Color.green(to) - Color.green(from)) * progress).roundToInt()
        val b = (Color.blue(from) + (Color.blue(to) - Color.blue(from)) * progress).roundToInt()
        return Color.argb(a, r, g, b)
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
        private const val DEFAULT_BADGE_RADIUS = 4F
        private const val DEFAULT_BADGE_BORDER_WIDTH = 2F
        private const val DEFAULT_TEXT_SIZE = 11F
        private const val DEFAULT_CORNER_RADIUS = 20F
        private const val DEFAULT_BAR_CORNER_RADIUS = 0F
        private const val DEFAULT_BAR_CORNERS = TOP_LEFT_CORNER or TOP_RIGHT_CORNER

        private const val OPAQUE = 255
        private const val TRANSPARENT = 0
    }
}
