package com.anwesh.uiprojects.eachlinestepview

/**
 * Created by anweshmishra on 22/09/18.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.content.Context
import android.app.Activity

val nodes : Int = 5

fun Canvas.drawELSNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val xGap : Float = w / ((nodes + 1) * 2)
    val yGap : Float = h / (nodes + 1)
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = Color.BLUE
    save()
    translate(w/2, yGap * i + yGap)
    for (j in 0..1) {
        val sc : Float = Math.min(0.5f, Math.max(0f, scale - j * 0.5f)) * 2
        val sf : Float = 1f - 2 * j
        save()
        translate((xGap * i) * sf, 0f)
        drawLine(0f, 0f, xGap * sc, 0f, paint)
        restore()
    }
    restore()
}

class EachLineStepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class ELSNode(var i : Int, val state : State = State()) {

        private var prev : ELSNode? = null
        private var next : ELSNode? = null

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = ELSNode(i + 1)
                next?.prev = this
            }
        }

        fun getNext(dir : Int, cb : () -> Unit) : ELSNode {
            var curr : ELSNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawELSNode(i, state.scale, paint)
            prev?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

    }

    data class EachLineStep(var i : Int) {
        private var curr : ELSNode = ELSNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : EachLineStepView) {

        private val animator : Animator = Animator(view)
        private val eachLineStep : EachLineStep = EachLineStep(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            eachLineStep.draw(canvas, paint)
            animator.animate {
                eachLineStep.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            eachLineStep.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : EachLineStepView {
            val view : EachLineStepView = EachLineStepView(activity)
            activity.setContentView(view)
            return view
        }
    }
}