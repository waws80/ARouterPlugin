package pw.androidthanatos.arouterplugin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppRouter.init(this.application, true)

        findViewById<View>(R.id.tv).setOnClickListener {
            AppRouter.getInstance().navigation("/test/test", this, 100){
                code, data->
                println(data.getStringExtra("a"))
            }
        }
    }
}
