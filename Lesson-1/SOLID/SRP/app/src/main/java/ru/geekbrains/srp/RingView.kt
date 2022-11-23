package ru.geekbrains.srp

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.ArrayList

class RingView : View {
    private var windowsWith //Screen width
            = 0
    private var hght = 200 //Default high
    private var wdth = 200 //Default width
    private var mContext: Context? = null
    private var mPaint: Paint? = null
    private val mPaintWidth = 0f // The width of the brush
    private var mRes: Resources? = null
    private var showRateSize = 14f // display the size of the text
    private var circleCenterX =
        0f // The center point X should be equal to the radius of the outer circle
    private var circleCenterY =
        0f // The center point Y should be equal to the radius of the outer circle
    private var paddingSize =
        90f //The inner margin of the circle and the View, not proportional data
    private var whitePointRadius = 2f //White dot radius
    private var ringOuterRadius = 100f // radius of outer circle
    private var ringInnerRadius = 64f // The radius of the inner circle
    private var ringPointRadius = 80f // The radius of the circle where the point is
    private val extendLineWidth = 20f //The length of the folded horizontal line after the extension
    private val pointList: MutableList<Point> = ArrayList() //A collection of points
    private val outPointList: MutableList<Point> =
        ArrayList() // Collection of outer polyline points
    private var brokenRadius =
        0f //The radius of the circle where the outer polyline point is located, generally larger than the outer circle radius, and smaller than the view with/2
    private var brokenMargin = 20f //Maximum circle distance from turning point
    private var rectF // the rectangle where the outer circle is located
            : RectF? = null
    private var rectFPoint // the rectangle where the point is
            : RectF? = null
    private var rectFBrokenPoint //Rectangle where the outer turning point is
            : RectF? = null
    private var colorList: List<Int?>? = null
    private var rateList: List<Float>? = null
    private var isRing = false
    private var isShowRate = false
    private var preAngle =
        -135f //The initial drawing position is 0 degrees, and the horizontal starts clockwise
    private var endAngle = 0f
    private var dm: DisplayMetrics? = null

    constructor(context: Context?) : super(context, null) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        initView(attrs)
    }

    fun setShow(colorList: List<Int?>?, rateList: List<Float>?) {
        setShow(colorList, rateList, false)
    }

    fun setShow(colorList: List<Int?>?, rateList: List<Float>?, isRing: Boolean) {
        setShow(colorList, rateList, isRing, false)
    }

    fun setShow(
        colorList: List<Int?>?,
        rateList: List<Float>?,
        isRing: Boolean,
        isShowRate: Boolean
    ) {
        this.colorList = colorList
        this.rateList = rateList
        this.isRing = isRing
        this.isShowRate = isShowRate
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getMode(heightMeasureSpec)
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(wdth, hght)
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(wdth, heightSpecSize)
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, hght)
        }
        //Re-measure to obtain width and height
        wdth = measuredWidth
        hght = measuredHeight
        dm = DisplayMetrics()
        val wm = mContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(dm)

        //Determine the center of the circle
        circleCenterX = (wdth / 2).toFloat()
        circleCenterY = (wdth / 2).toFloat()
        if (wdth > windowsWith) {
            wdth = windowsWith
        }
        hght = wdth
        ringOuterRadius = wdth / 2 - paddingSize
        ringPointRadius = ringOuterRadius * 0.8f // 0.8 of the largest circle
        ringInnerRadius = (wdth / 2 - paddingSize) * 0.5f // 0.5 of the largest circle

        //The radius of the outer circle polyline point
        brokenRadius = wdth / 2 - paddingSize + brokenMargin

        // the rectangle where the outer circle is located
        rectF = RectF(
            paddingSize,
            paddingSize,
            wdth - paddingSize,
            wdth - paddingSize
        )
        // the rectangle where the point is
        rectFPoint = RectF(
            paddingSize + (ringOuterRadius - ringPointRadius),
            paddingSize + (ringOuterRadius - ringPointRadius),
            wdth - paddingSize - (ringOuterRadius - ringPointRadius),
            wdth - paddingSize - (ringOuterRadius - ringPointRadius)
        )

        //Rectangle where the outer turning point is
        rectFBrokenPoint = RectF(
            wdth / 2 - brokenRadius,
            wdth / 2 - brokenRadius,
            wdth / 2 + brokenRadius,
            wdth / 2 + brokenRadius
        )
    }

    private fun initView(attrs: AttributeSet?) {
        val a = mContext!!.obtainStyledAttributes(attrs, R.styleable.RingView)
        showRateSize = a.getDimension(R.styleable.RingView_proTextSize, showRateSize)
        paddingSize = a.getDimension(R.styleable.RingView_paddingSize, paddingSize)
        brokenMargin = a.getDimension(R.styleable.RingView_brokenMargin, brokenMargin)
        whitePointRadius = a.getDimension(R.styleable.RingView_whitePointRadius, whitePointRadius)
        a.recycle()
        mRes = mContext!!.resources
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        var dm = mContext!!.resources.displayMetrics
        windowsWith = dm.widthPixels
        mPaint!!.strokeWidth = mPaintWidth
        mPaint!!.style = Paint.Style.FILL
        mPaint!!.isAntiAlias = true //Anti-aliasing
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (colorList != null) {
            for (i in colorList!!.indices) {
                mPaint!!.color = mRes!!.getColor(colorList!![i]!!)
                mPaint!!.style = Paint.Style.FILL
                if (rateList != null) {
                    endAngle = getAngle(rateList!![i])
                }
                //Draw a sector
                canvas.drawArc(rectF!!, preAngle, endAngle, true, mPaint!!)
                if (isShowRate) {
                    // draw percentage, polyline
                    drawArcCenterPoint(canvas, i)
                }
                //After drawing one, update the starting angle to the ending angle of the previous one
                preAngle = preAngle + endAngle
            }
        }
        mPaint!!.style = Paint.Style.FILL
        if (isRing) {
            //Draw the inner circle
            drawInner(canvas)
        }
    }

    private fun drawInner(canvas: Canvas) {
        mPaint!!.color = mRes!!.getColor(R.color.white)
        canvas.drawCircle(circleCenterX, circleCenterY, ringInnerRadius, mPaint!!)

        //The circle where the outer circle point is located
        mPaint!!.color = mRes!!.getColor(R.color.main_red)
        mPaint!!.style = Paint.Style.STROKE
        canvas.drawCircle(circleCenterX, circleCenterY, brokenRadius, mPaint!!)

        //The circle where the white dot is
        mPaint!!.color = mRes!!.getColor(R.color.main_black)
        mPaint!!.style = Paint.Style.STROKE
        canvas.drawCircle(circleCenterX, circleCenterY, ringPointRadius, mPaint!!)
    }

    /**
     * // Draw percentage, polyline
     * @param canvas
     * @param position
     */
    private fun drawArcCenterPoint(canvas: Canvas, position: Int) {
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeWidth = dip2px(1f).toFloat()
        //White spot collection
        dealPoint(rectFPoint, preAngle, endAngle / 2, pointList)
        //Polyline point collection
        dealPoint(rectFBrokenPoint, preAngle, endAngle / 2, outPointList)
        val point = pointList[position]
        val brokenPoint = outPointList[position]
        mPaint!!.color = mRes!!.getColor(R.color.white)
        //Draw white dots
        canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), whitePointRadius, mPaint!!)
        val floats = FloatArray(8)
        floats[0] = point.x.toFloat()
        floats[1] = point.y.toFloat()
        floats[2] = brokenPoint.x.toFloat()
        floats[3] = brokenPoint.y.toFloat()
        floats[4] = brokenPoint.x.toFloat()
        floats[5] = brokenPoint.y.toFloat()
        if (point.x >= wdth / 2) {
            mPaint!!.textAlign = Paint.Align.LEFT //Text on the right
            floats[6] = brokenPoint.x + extendLineWidth
        } else {
            mPaint!!.textAlign = Paint.Align.RIGHT //Text on the left
            floats[6] = brokenPoint.x - extendLineWidth
        }
        floats[7] = brokenPoint.y.toFloat()
        mPaint!!.color = mRes!!.getColor(colorList!![position]!!)
        //Draw a polyline
        canvas.drawLines(
            floats,
            mPaint!!
        ) //{X1, y1, x2, y2, x3, y3,...} two by two form a straight line
        mPaint!!.textSize = showRateSize
        mPaint!!.style = Paint.Style.FILL
        //Draw text
        canvas.drawText(rateList!![position].toString() + "%", floats[6], floats[7], mPaint!!)
    }

    private fun dealPoint(
        rectF: RectF?,
        startAngle: Float,
        endAngle: Float,
        pointList: MutableList<Point>
    ) {
        val path = Path()
        //Create an arc with a specified angle through path
        path.addArc(rectF!!, startAngle, endAngle)
        //Measure the length of the path
        val measure = PathMeasure(path, false)
        val pos = floatArrayOf(0f, 0f)
        //Use PathMeasure to measure the coordinates of each point coords
        //The first parameter represents an interval between 0 and measure.getLength(), so here we take the end point coordinates of the arc and save the pos array
        measure.getPosTan(measure.length / 1, pos, null)
        Log.e("coords:", "x axis:" + pos[0] + "- y axis:" + pos[1])
        val x = pos[0]
        val y = pos[1]
        val point = Point(Math.round(x), Math.round(y))
        pointList.add(point)
    }

    /**
     * @param percent percentage
     * @return
     */
    private fun getAngle(percent: Float): Float {
        //In actual use, the accuracy will be lost according to the percentage of int. Add one more here to solve the problem of gaps in drawing
        return 361f / 100f * percent
    }

    /**
     * Converted from dp to px (pixel) according to the resolution of the phone
     */
    fun dip2px(dpValue: Float): Int {
        return (dpValue * dm!!.density + 0.5f).toInt()
    }

    /**
     * According to the resolution of the phone, the unit is converted from dp to px (pixel)
     */
    fun px2dip(pxValue: Float): Int {
        return (pxValue / dm!!.density + 0.5f).toInt()
    }
}