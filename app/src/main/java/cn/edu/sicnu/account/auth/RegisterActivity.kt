package cn.edu.sicnu.account.auth

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.text.InputType
import cn.edu.sicnu.account.R
import cn.edu.sicnu.account.arouter.Path
import cn.edu.sicnu.account.sqlite.SqliteOpenHelper
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.et_phone
import kotlinx.android.synthetic.main.activity_register.tv_login
import kotlinx.android.synthetic.main.activity_register.tv_register
import org.jetbrains.anko.*


@Route(path = Path.register)
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tv_code.setOnClickListener {
            alert {
                customView {
                    verticalLayout {
                        textView {
                            text = getString(R.string.input_phone_code)
                            textSize = 18f
                            textColor = R.color.black
                        }.lparams {
                            topMargin = dip(17)
                            horizontalMargin = dip(17)
                            bottomMargin = dip(10)
                        }

                        val quantity = editText {
                            hint = ctx.resources.getText(R.string.six_code)
                            inputType = InputType.TYPE_CLASS_NUMBER
                            isFocusable = true
                            filters = Array<InputFilter>(1) {
                                InputFilter.LengthFilter(6)
                            }
                        }.lparams(width = matchParent, height = wrapContent) {
                            bottomMargin = dip(8)
                            horizontalMargin = dip(14)
                        }

                        button(getString(R.string.confirm)) {
                            textColor = R.color.colorBtnWhite
                        }.lparams(width = matchParent, height = matchParent) {

                        }.setOnClickListener {
                            if (quantity.text.length == 6) {
                                if (quantity.text.toString() == "123456") {
                                    toast("验证成功")
                                    quantity.isFocusable = false
                                    tv_register.isEnabled = true
                                } else {
                                    toast("验证失败")
                                }
                            }
                            else {
                                alert("请输入正确的验证码！", "") {
                                    yesButton { toast("默认验证码为：123456") }
                                }.show()
                            }
                        }
                    }
                }
            }.show()
        }

        tv_register.setOnClickListener {
            val openHelper = SqliteOpenHelper(this)
            val db = openHelper.writableDatabase
            val phone:String = et_phone.text.toString()
            val password1:String = et_password1.text.toString()
            val password2:String = et_password2.text.toString()

            if (phone.length != 11) {
                alert("手机号格式错误，请重新输入.") {
                    title = "注册失败"
                    isCancelable = true
                    positiveButton("OK" ){
                        et_phone.setText("")
                    }
                }.show()
                return@setOnClickListener
            }

            if (password1 == "" || password2 == "") {
                alert("密码为空，请重新输入.") {
                    title = "注册失败"
                    isCancelable = true
                    positiveButton("OK" ){ }
                }.show()
                return@setOnClickListener
            }

            if (password1 != password2 && password1 != "") {
                alert("两次密码不一致，请重新输入.") {
                    title = "注册失败"
                    isCancelable = true
                    positiveButton("OK" ){
                        et_password1.setText("")
                        et_password2.setText("")
                    }
                }.show()
                return@setOnClickListener
            }
            db.execSQL("insert into user('phone', 'password') values('$phone', '$password1')")

            alert("注册成功") {
                yesButton {
                    db.close()
                    ARouter.getInstance().build(Path.login).withString("register_phone", phone).navigation()
                }
            }.show()
            db.close()
        }

        tv_login.setOnClickListener {
            ARouter.getInstance().build(Path.login).navigation()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}