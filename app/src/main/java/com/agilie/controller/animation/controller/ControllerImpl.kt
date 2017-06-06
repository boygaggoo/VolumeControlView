package com.agilie.controller.animation.controller

import android.graphics.Canvas
import android.graphics.PointF
import android.view.MotionEvent
import com.agilie.controller.animation.painter.*
import com.agilie.controller.calculateAngleWithTwoVectors
import com.agilie.controller.closestValue
import com.agilie.controller.getPointOnBorderLineOfCircle
import com.agilie.controller.view.ControllerView
import com.agilie.controller.view.ControllerView.Companion.INNER_CIRCLE_STROKE_WIDTH
import java.util.*

class ControllerImpl(val innerCircleImpl: InnerCircleImpl,
                     val movableCircleImpl: MovableCircleImpl,
                     val splinelPath: SplinelPath,
                     val mainCircleImpl: MainCircleImpl) : Controller {

    private var width = 0
    private var height = 0
    private var eventRadius: Float = 0f
    private var distance: Float = 0f
    private var mainCenter: PointF = PointF()
    private var mainRadius = 0f
    private var linesList = ArrayList<SimpleLineImpl>()


    override fun onDraw(canvas: Canvas) {
        mainCircleImpl.onDraw(canvas)
        linesList.forEach { it.onDraw(canvas) }
        splinelPath.onDraw(canvas)
        innerCircleImpl.onDraw(canvas)
        movableCircleImpl.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int) {
        this.width = w
        this.height = h
        setCircleRadius(w, h)
        setCenterCoordinates(w, h)
        createSpiralPath()
        initLines()
    }

    fun onTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onActionDown(PointF(event.x, event.y))
            }
            MotionEvent.ACTION_MOVE -> {
                onActionMove(PointF(event.x, event.y))
            }
            MotionEvent.ACTION_UP -> {
                onActionUp(event)
            }
        }
    }

    private fun onActionDown(touchPointF: PointF) {
        actionDownAngle = getClosestAngle(touchPointF)
        val startAngle = getStartAngle(touchPointF)
        val point = getPointOnBorderLineOfCircle(mainCenter, eventRadius, startAngle)

        previousAngle = actionDownAngle
        direction = Direction.UNDEFINED
        angleDelta = 0

        movableCircleImpl.onActionMove(point)
        splinelPath.onReset()
        splinelPath.onDrawBigSpline(actionDownAngle, startAngle)
    }

    private var actionDownAngle: Int = 0
    private var angleDelta = 0

    private var previousAngle = 0
    private var direction: Direction = Direction.UNDEFINED

    enum class Direction {
        UNDEFINED, CLOCKWISE, CCLOCKWISE
    }

    private fun onActionMove(touchPointF: PointF) {
        var currentAngle = getClosestAngle(touchPointF)
        val startAngle = getStartAngle(touchPointF)
        val moveToPoint = getPointOnBorderLineOfCircle(mainCenter, eventRadius, startAngle)
        val startPoint = getPointOnBorderLineOfCircle(mainCenter, eventRadius, getStartAngle(mainCenter))

        // angleDelta %= 360

        if (previousAngle != currentAngle) {
            if (overlappedClockwise(direction, previousAngle, currentAngle)) {
                angleDelta += (360 - previousAngle + currentAngle)
            } else if (overlappedCclockwise(direction, previousAngle, currentAngle)) {
                angleDelta -= (360 - currentAngle + previousAngle)
            } else if (previousAngle < currentAngle) {
                direction = Direction.CLOCKWISE
                angleDelta += (currentAngle - previousAngle)
            } else {
                direction = Direction.CCLOCKWISE
                angleDelta -= (previousAngle - currentAngle)
            }
        }

        val angle = Math.max(Math.min(actionDownAngle + angleDelta, 360), 0)

        if (moveMovableCircle(angle)) {
            movableCircleImpl.onActionMove(moveToPoint)
        } else {
            movableCircleImpl.onActionMove(startPoint)
        }

        splinelPath.onReset()
        splinelPath.onDrawBigSpline(angle, startAngle)

        previousAngle = currentAngle
    }


    private fun onActionUp(event: MotionEvent) {

    }

    private fun moveMovableCircle(angle: Int): Boolean {
        if (angle == 360 || angle == 0) {
            return false
        }
        return true
    }

    private fun createSpiralPath() {
        val startAngle = getStartAngle(mainCenter)
        splinelPath.onCreateSpiralPath(0, startAngle)
    }

    private fun setCenterCoordinates(w: Int, h: Int) {
        mainCenter.apply {
            x = w / 2f
            y = h / 2f
        }

        innerCircleImpl.center = mainCenter
        movableCircleImpl.center.apply {
            x = mainCenter.x
            y = mainCenter.y - eventRadius
        }

        mainCircleImpl.center = mainCenter

        splinelPath.spiralStartPoint = getPointOnBorderLineOfCircle(mainCenter,
                innerCircleImpl.radius + INNER_CIRCLE_STROKE_WIDTH, 0)

        splinelPath.innerCircleCenter = innerCircleImpl.center
        splinelPath.center = mainCenter
    }

    private fun setCircleRadius(w: Int, h: Int) {
        mainRadius = if (w > h) h / 3f else w / 3f
        mainCircleImpl.radius = mainRadius

        innerCircleImpl.radius = mainRadius / 2
        movableCircleImpl.radius = ControllerView.MOVABLE_CIRCLE_RADIUS

        splinelPath.innerCircleRadius = innerCircleImpl.radius
        splinelPath.radius = mainRadius

        eventRadius = innerCircleImpl.radius - movableCircleImpl.radius * 2
        distance = (mainRadius - innerCircleImpl.radius) / 360

        splinelPath.distance = distance
    }

    private fun initLines() {
        for (i in 0..360 step ControllerView.SECTOR_STEP) {
            val line = SimpleLineImpl(splinelPath.pathPaint)
            line.startPoint = mainCenter
            val endPoint = getPointOnBorderLineOfCircle(mainCenter.x,
                    mainCenter.y, mainRadius, i.toDouble())
            line.endPoint = endPoint
            linesList.add(line)
        }
    }

    private fun overlappedCclockwise(direction: Direction, previousAngle: Int, currentAngle: Int) = direction == Direction.CCLOCKWISE && (currentAngle - previousAngle) > 45

    private fun overlappedClockwise(direction: Direction, previousAngle: Int, currentAngle: Int) = direction == Direction.CLOCKWISE && (previousAngle - currentAngle) > 45

    private fun getClosestAngle(touchPointF: PointF) =
            closestValue(calculateAngleWithTwoVectors(touchPointF, mainCenter), ControllerView.SECTOR_STEP)

    private fun getStartAngle(touchPointF: PointF) =
            (Math.round(calculateAngleWithTwoVectors(touchPointF, mainCenter))).toInt()
}