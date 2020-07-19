package cn.edu.sicnu.account.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import cn.edu.sicnu.account.R
import com.alibaba.android.arouter.launcher.ARouter
import cn.edu.sicnu.account.arouter.Path
import cn.edu.sicnu.account.sqlite.SqliteOpenHelper
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.alert
import java.net.PasswordAuthentication

@Route(path = Path.login)
class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tv_register.setOnClickListener {
            ARouter.getInstance().build(Path.register).navigation()
        }

        tv_login.setOnClickListener {
            val openHelper = SqliteOpenHelper(this)
            val db = openHelper.readableDatabase
            val phone:String = et_phone.text.toString()
            val password:String = et_password.text.toString()

            val columns = arrayOf<String>("phone", "password")
            val values = arrayOf<String>(phone, password)
            val cursor = db.query("user", columns, "phone=? and password=?", values, null, null, null)

            // 登陆成功，跳转页面
            if (cursor.count != 0) {
                cursor.close()
                User.isLogin = true
                ARouter.getInstance().build(Path.main).navigation()
            } else {
                alert("手机号或密码错误，请重新输入！", "登录失败") {
                    positiveButton("确定") { }
                    negativeButton("忘记密码？") { }
                }.show()
            }
            cursor.close()
        }

        tv_qq_login.setOnClickListener {
            alert ("功能未开放！", "敬请期待" ) {
                positiveButton("确定") {}
            }.show()
        }

        tv_wx_login.setOnClickListener {
            alert ("功能未开放！", "敬请期待" ) {
                positiveButton("确定") {}
            }.show()
        }

        tv_host.setOnClickListener {
            User.isLogin = false
            ARouter.getInstance().build(Path.main).navigation()
        }

        iv_isLook.setOnClickListener {
            // 如果密码可见
            if (et_password.inputType == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                iv_isLook.setImageResource(R.drawable.ic_password)
                et_password.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
                et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                iv_isLook.setImageResource(R.drawable.ic_password_visiable)
                et_password.inputType = EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            et_password.setSelection(et_password.text.toString().length)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}