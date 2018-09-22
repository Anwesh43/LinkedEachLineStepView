package com.anwesh.uiprojects.linkedeachlinestepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.anwesh.uiprojects.eachlinestepview.EachLineStepView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : EachLineStepView = EachLineStepView.create(this)
        fullScreen()
        view.addEachLineAnimationListener({i -> createToast("animation number ${i + 1} is complete")}, {i -> createToast("animation number ${i + 1} is reset")})
    }

    private fun createToast(text : String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}