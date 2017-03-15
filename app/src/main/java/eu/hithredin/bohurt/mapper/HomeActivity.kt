package eu.hithredin.bohurt.mapper

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        button_test.setOnClickListener { Toast.makeText(this, "Hello", Toast.LENGTH_SHORT) }
    }
}
