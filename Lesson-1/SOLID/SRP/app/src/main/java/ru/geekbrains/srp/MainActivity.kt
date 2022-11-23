package ru.geekbrains.srp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private val formatString = "#0.00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_main_ring)
    }

    override fun onResume() {
        super.onResume()

    //    val r =findViewById<CircleView>(R.id.circle).getRad()
   //     val circleCalc = Circle(r)
    //    textArea.text = DecimalFormat(formatString).format(circleCalc.area);

// add color
        val colorList: MutableList<Int> = ArrayList()
        colorList.add(R.color.main_color)
        colorList.add(R.color.main_accent)
        colorList.add(R.color.main_red)

        // added percentage
        val rateList: MutableList<Float> = ArrayList()
        rateList.add(33.3f)
        rateList.add(33.3f)
        rateList.add(33.3f)
        val r =findViewById<RingView>(R.id.ringView)
        r.setShow(colorList, rateList, true, true)

    }
}