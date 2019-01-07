package pw.androidthanatos.arouterplugin

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = "/test/test")
class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("a","b")
            })
        }
        return super.onKeyDown(keyCode, event)
    }
}
