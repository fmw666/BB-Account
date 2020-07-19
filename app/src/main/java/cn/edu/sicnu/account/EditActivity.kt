package cn.edu.sicnu.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import cn.edu.sicnu.account.arouter.Path
import cn.edu.sicnu.account.data.Time
import cn.edu.sicnu.account.sqlite.SqliteOpenHelper
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.android.synthetic.main.activity_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

@Route(path = Path.edit)
class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        rg.check(rb_income.id)
        bindListener()
    }

    // 绑定事件监听
    @SuppressLint("Recycle")
    private fun bindListener() {
        tv_back.setOnClickListener { ARouter.getInstance().build(Path.main).navigation() }

        tv_confirm.setOnClickListener {
            var in_or_out:Int ?= null
            var msg = ""
            if (rb_income.isChecked) {
                in_or_out = 1
                msg = "收入"
            }
            if (rb_outcome.isChecked) {
                in_or_out = 0
                msg = "支出"
            }
            val moneyStr = et_money.text.toString()
            val type = sp_type.selectedItem.toString()
            val note = et_note.text.toString()
            val time = Time.year.toString()+'.'+Time.month.toString()+'.'+Time.day.toString()

            if (moneyStr == "")
                toast("请输入金额")
            else {
                val money = moneyStr.toDouble()
                alert("是否添加：$msg $money 元.\n选择分类：$type.\n添加备注：$note.\n当前日期：$time", "请求确认") {
                    positiveButton("确定") {
                        val openHelper = SqliteOpenHelper(applicationContext)
                        val db = openHelper.writableDatabase
                        db.execSQL("insert into bills(in_or_out, money, type, note, time) values($in_or_out, $money, '$type', '$note', '$time');")

                        ARouter.getInstance().build(Path.main).navigation()
                        toast("添加成功")
                    }
                    negativeButton("取消") { }
                }.show()
            }
        }
    }

}

