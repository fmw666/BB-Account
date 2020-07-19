package cn.edu.sicnu.account.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import cn.edu.sicnu.account.MainActivity
import cn.edu.sicnu.account.R
import cn.edu.sicnu.account.adapter.ItemAdapter
import cn.edu.sicnu.account.data.Items
import cn.edu.sicnu.account.data.Time
import cn.edu.sicnu.account.sqlite.SqliteOpenHelper
import kotlinx.android.synthetic.main.fragment_account_detail.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import java.util.*
import kotlin.collections.ArrayList
import android.icu.util.Calendar as Calendar1


class AccountDetailFragment : Fragment() {

    companion object {

        fun newInstance():AccountDetailFragment{
            val args = Bundle()
            val fragment = AccountDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    var incomes : Double = 0.0
    var outcomes: Double = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account_detail,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(2,0,0)
        initListener()
    }

    @SuppressLint("Recycle")
    private fun initView(model: Int, y: Int=0, m: Int=0) {
        val year:Int = if (y == 0)
            Time.year
        else
            y
        val month:Int = if (m == 0)
            Time.month
        else
            m
        var sb = StringBuffer("$year")
        sb.append(getString(R.string.year)).append("\n").append("$month").append(getString(R.string.month)).append(" 🔽")//🔻
        var ss = SpannableString(sb.toString())
        ss.setSpan(ForegroundColorSpan(Color.parseColor("#000000")),"$year".length+1,sb.toString().length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_data.text = ss;

        val openHelper = SqliteOpenHelper(activity)
        val db = openHelper.readableDatabase
        val viewDate = getViewDate("year")+"."+getViewDate("month")
        val cursor: Cursor
        cursor = if (model == 0 || model == 1) {
            db.query("bills", null,"time" + " like '%$viewDate%' and in_or_out = $model", null, null, null, "time")
        } else {
            db.query("bills", null, "time" + " like '%$viewDate%'", null, null, null, "time")
        }
        val itemList = ArrayList<Items>()
        var items : Items
        incomes = 0.0
        outcomes = 0.0
        while (cursor.moveToNext()) {
            items = Items(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getDouble(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5))
            if (cursor.getInt(1) == 1)
                incomes += cursor.getDouble(2)
            else if (cursor.getInt(1) == 0)
                outcomes += cursor.getDouble(2)
            itemList.add(items)
        }

        lv_data.adapter = ItemAdapter(activity, itemList)

        sb = StringBuffer(getString(R.string.income))
        sb.append("\n").append(incomes)
        ss = SpannableString(sb.toString())
        ss.setSpan(ForegroundColorSpan(Color.parseColor("#000000")), 3, sb.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_income.text = ss

        sb = StringBuffer(getString(R.string.outcome))
        sb.append("\n").append(outcomes)
        ss = SpannableString(sb.toString())
        ss.setSpan(ForegroundColorSpan(Color.parseColor("#000000")), 3, sb.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_outcome.text = ss
    }

    @SuppressLint("Recycle")
    private fun initListener() {
        tv_income.setOnClickListener {
            initView(1)
            toast("${getViewDate("year")}年${getViewDate("month")}月 总收入：$incomes")
        }

        tv_outcome.setOnClickListener {
            initView(0)
            toast("${getViewDate("year")}年${getViewDate("month")}月 总支出：$outcomes")
        }

        lv_data.setOnItemClickListener { adapterView, _, i, _ ->
            val id = adapterView.getItemIdAtPosition(i)

            val openHelper = SqliteOpenHelper(context)
            val db = openHelper.readableDatabase
            val cursor = db.query("bills", null, "_id=$id", null, null, null, null)

            var in_or_out:Int ?= null
            var money_list:List<String> ?= null
            var type:String ?= null
            var note:String ?= null

            while (cursor.moveToNext()) {
                in_or_out =  cursor.getInt(1)
                money_list = cursor.getDouble(2).toString().split('.')
                type = cursor.getString(3)
                note = cursor.getString(4)
            }

            alert {

                var btn_income: RadioButton ?= null
                var btn_outcome: RadioButton ?= null
                var et_money: EditText ?= null
                var et_money_d: EditText ?= null

                var et_type: EditText ?= null
                var et_note: EditText ?= null

                customView {
                    verticalLayout {
                        textView {
                            text = "修改记录"
                            textSize = 18f
                            textColor = R.color.black
                        }.lparams {
                            topMargin = dip(17)
                            horizontalMargin = dip(17)
                            bottomMargin = dip(10)
                        }

                        relativeLayout {
                            radioGroup {
                                orientation = LinearLayout.HORIZONTAL
                                padding = dip(10)

                                btn_income = radioButton {
                                    text = getText(R.string.income)
                                }

                                btn_outcome = radioButton {
                                    text = getText(R.string.outcome)
                                }

                                if (in_or_out == 1)
                                    check(btn_income!!.id)
                                else if (in_or_out == 0)
                                    check(btn_outcome!!.id)
                            }

                            textView {
                                text = "支出/收入"
                                textSize = 16f
                                textColor = R.color.black
                            }.lparams {
                                alignParentRight()
                                centerVertically()
                                setMargins(0, 0, dip(16), 0)
                            }

                        }

                        relativeLayout {
                            et_money = editText {
                                inputType = InputType.TYPE_CLASS_NUMBER
                                isFocusable = true
                            }.lparams(width = 250, height = wrapContent) {
                                centerVertically()
                                alignParentLeft()
                                setMargins(dip(16), 0, 0, 0)
                            }

                            et_money!!.setText(money_list!![0])

                            textView {
                                text = "."
                                textSize = 16f
                                textColor = R.color.black
                            }.lparams {
                                alignParentRight()
                                centerVertically()
                                setMargins(0, 0, dip(200), 0)
                            }

                            et_money_d = editText {
                                inputType = InputType.TYPE_CLASS_NUMBER
                                isFocusable = true
                                filters = Array<InputFilter>(1) {
                                    InputFilter.LengthFilter(2)
                                }
                            }.lparams(width = 100, height = wrapContent) {
                                centerVertically()
                                alignParentLeft()
                                setMargins(dip(120), 0, 0, 0)
                            }

                            et_money_d!!.setText(money_list[1])

                            textView {
                                text = "金额"
                                textSize = 16f
                                textColor = R.color.black
                            }.lparams {
                                alignParentRight()
                                centerVertically()
                                setMargins(0, 0, dip(16), 0)
                            }
                        }

                        relativeLayout {
                            et_type = editText {
                                isFocusable = true
                            }.lparams(width = 250, height = wrapContent) {
                                centerVertically()
                                alignParentLeft()
                                setMargins(dip(16), 0, 0, 0)
                            }

                            et_type!!.setText(type)

                            textView {
                                text = "分类"
                                textSize = 16f
                                textColor = R.color.black
                            }.lparams {
                                alignParentRight()
                                centerVertically()
                                setMargins(0, 0, dip(16), 0)
                            }
                        }

                        relativeLayout {
                            et_note = editText {
                                isFocusable = true
                            }.lparams(width = 500, height = wrapContent) {
                                centerVertically()
                                alignParentLeft()
                                setMargins(dip(16), 0, 0, 0)
                            }

                            et_note!!.setText(note)

                            textView {
                                text = "备注"
                                textSize = 16f
                                textColor = R.color.black
                            }.lparams {
                                alignParentRight()
                                centerVertically()
                                setMargins(0, 0, dip(16), 0)
                            }
                        }

                    }
                }
                positiveButton("确认修改") {
                    if (btn_income!!.isChecked) {
                        in_or_out = 1
                    } else if (btn_outcome!!.isChecked) {
                        in_or_out = 0
                    }
                    val money_str = et_money!!.text.toString() + "." + et_money_d!!.text.toString()
                    val money = money_str.toDouble()
                    type = et_type!!.text.toString()
                    note = et_note!!.text.toString()

                    alert("确认修改？", "") {
                        yesButton {
                            db.execSQL("update bills set type='$type', money=$money, in_or_out=$in_or_out, note='$note' where _id=$id;")
                            initView(3)
                            toast("修改成功！")
                        }
                        cancelButton {  }
                    }.show()
                }
                negativeButton("删除此条记录") {
                    alert("确认删除？", "") {
                        yesButton {
                            db.execSQL("delete from bills where _id=$id;")
                            initView(3)
                            toast("删除成功！")
                        }
                        cancelButton {  }
                    }.show()
                }
                cursor.close()
            }.show()
        }

        tv_data.setOnClickListener {
            alert {

                var et_year: EditText ?= null
                var et_month: EditText ?= null

                customView {
                    verticalLayout {
                        textView {
                            text = "选择日期"
                            textSize = 18f
                            textColor = R.color.black
                        }.lparams {
                            topMargin = dip(17)
                            horizontalMargin = dip(17)
                            bottomMargin = dip(10)
                        }

                        relativeLayout {
                            et_year = editText {
                                hint = Time.year.toString()
                                inputType = InputType.TYPE_CLASS_NUMBER
                                isFocusable = true
                                filters = Array<InputFilter>(1) {
                                    InputFilter.LengthFilter(4)
                                }
                            }.lparams(width = wrapContent, height = wrapContent) {
                                centerVertically()
                                alignParentLeft()
                                setMargins(dip(16), 0, 0, 0)
                            }

                            textView {
                                text = "年"
                                textSize = 16f
                                textColor = R.color.black
                            }.lparams {
                                alignParentRight()
                                centerVertically()
                                setMargins(0, 0, dip(16), 0)
                            }
                        }

                        relativeLayout {
                            et_month = editText {
                                hint = Time.month.toString()
                                inputType = InputType.TYPE_CLASS_NUMBER
                                isFocusable = true
                                filters = Array<InputFilter>(1) {
                                    InputFilter.LengthFilter(2)
                                }
                            }.lparams(width = wrapContent, height = wrapContent) {
                                centerVertically()
                                alignParentLeft()
                                setMargins(dip(16), 0, 0, 0)
                            }

                            textView {
                                text = "月"
                                textSize = 16f
                                textColor = R.color.black
                            }.lparams {
                                alignParentRight()
                                centerVertically()
                                setMargins(0, 0, dip(16), 0)
                            }
                        }

                    }
                }
                okButton {
                    val year: Int = if (et_year?.text.toString() == "")
                        Time.year
                    else
                        et_year?.text.toString().toInt()
                    val month: Int = if (et_month?.text.toString() == "")
                        Time.month
                    else
                        et_month?.text.toString().toInt()

                    initView(3, year, month)

                    val sb = StringBuffer("$year")
                    sb.append(getString(R.string.year)).append("\n").append("$month")
                        .append(getString(R.string.month)).append(" 🔽")//🔻
                    val ss = SpannableString(sb.toString())
                    ss.setSpan(
                        ForegroundColorSpan(Color.parseColor("#000000")),
                        "$year".length + 1,
                        sb.toString().length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    tv_data.text = ss;

                    toast("选择日期：${year}年${month}月")
                }
            }.show()
//            val dialog = DatePickerDialog(activity, android.R.style.Theme_Holo_Dialog, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                toast("选择日期：${year}年${monthOfYear+1}月")
//            }, Time.year, Time.month, Time.day)
//            dialog.show()
        }

    }

    private fun getViewDate(ch: String): String {
        val timeStr = tv_data.text.toString()
        var ret = ""
        if (ch == "year") {
            ret = timeStr.split("年\n")[0]
        } else if (ch == "month") {
            ret = timeStr.split("年\n")[1].split("月")[0]
        }
        return ret
    }

}