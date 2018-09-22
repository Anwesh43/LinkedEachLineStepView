package com.anwesh.uiprojects.linkedeachlinestepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.eachlinestepview.EachLineStepView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EachLineStepView.create(this)
    }
}
