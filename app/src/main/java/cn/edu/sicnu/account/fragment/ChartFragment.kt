package cn.edu.sicnu.account.fragment

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import cn.edu.sicnu.account.R
import cn.edu.sicnu.account.data.Time
import cn.edu.sicnu.account.sqlite.SqliteOpenHelper
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.fragment_chart.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast

class ass : OnChartValueSelectedListener {
    override fun onNothingSelected() {
        TODO("Not yet implemented")
    }

    override fun onValueSelected(e: Entry?, dataSetIndex: Int, h: Highlight?) {
        TODO("Not yet implemented")
    }

}

class ChartFragment : Fragment(), OnChartValueSelectedListener {

    companion object {

        fun newInstance():ChartFragment{
            val args = Bundle()
            val fragment = ChartFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chart,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindlistener()
    }

    override fun onNothingSelected() {
        TODO("Not yet implemented")
    }

    override fun onValueSelected(e: Entry?, dataSetIndex: Int, h: Highlight?) {
        TODO("Not yet implemented")
    }


    @SuppressLint("SetTextI18n")
    private fun initView() {
        tv_time.text = Time.year.toString() + "." + Time.month.toString()
        initPieChart(tv_time.text.toString())
        initBarChart(tv_time.text.toString())
    }


    @SuppressLint("SetTextI18n", "Recycle")
    private fun initPieChart (time: String) {
        tv_pc.text = "$time  ·  收/支  ·  饼状图"
        //1、基本设置
        pieChart.setDrawCenterText(true)  //饼状图中间文字不显示
        pieChart.setDescription("")
        pieChart.setCenterTextSize(14f)
        pieChart.isDrawHoleEnabled = true    //设置实心
        pieChart.rotationAngle = 90f // 初始旋转角度
        pieChart.setUsePercentValues(false)
        pieChart.isHighlightPerTapEnabled = true    // 点击Item高亮是否可用
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad) // 设置pieChart图表展示动画效果
        //2、添加数据
        val xValues = ArrayList<String>()  //xVals用来表示每个饼块上的内容
        xValues.add("当月总支出")
        xValues.add("当月总收入")

        val yValues = ArrayList<Entry>()

        val openHelper = SqliteOpenHelper(activity)
        val db = openHelper.readableDatabase
        val cursor: Cursor = db.query("bills", null, "time" + " like '%$time%'", null, null, null, null)
        var incomes = 0.0
        var outcomes = 0.0
        while (cursor.moveToNext()) {
            if (cursor.getInt(1) == 1)
                incomes += cursor.getDouble(2)
            else if (cursor.getInt(1) == 0)
                outcomes += cursor.getDouble(2)
        }
        cursor.close()

        val v = incomes - outcomes
        if (v >= 0)
            pieChart.setCenterText("总盈利：+$v 元")
        else
            pieChart.setCenterText("总亏损：$v 元")

        yValues.add(Entry(outcomes.toFloat(), 0))
        yValues.add(Entry(incomes.toFloat(), 1))
        //3、y轴数据
        val pieDataSet = PieDataSet(yValues, "1"/*显示在比例图上*/)
        pieDataSet.sliceSpace = 0.2f //设置个饼状图之间的距离
        //4、设置颜色
        val colors = ArrayList<Int>()
        colors.add(Color.argb(220,255,87,34))
        colors.add(Color.argb(220,159,221,86))
        pieDataSet.colors = colors
        pieDataSet.valueTextSize = 11f
        //5、 设置数据
        val pieData = PieData(xValues, pieDataSet)
        val metrics = resources.displayMetrics
        val px = 5 * (metrics.densityDpi / 160f)
        pieDataSet.selectionShift = px  // 选中态多出的长度
        pieData.setDrawValues(true) //设置是否显示数据实体
//        pieData.setValueFormatter(PercentFormatter()) //显示百分比
        //6、去掉比例尺和说明
        val legend = pieChart.legend  //下标说明，false
        legend.textSize = 14f   // 标签文本大小
        legend.isEnabled = false
        pieChart.setDescription("")
        //7、显示
        pieChart.data = pieData
    }

    @SuppressLint("SetTextI18n")
    private fun initBarChart (time: String) {
        tv_bc.text = "$time · 账单详情 · 柱状图"
        //1、基本设置
        val xAxis = barChart.xAxis
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(false)
        barChart.setDrawGridBackground(false) // 是否显示表格颜色
        barChart.axisLeft.setDrawAxisLine(false)
        barChart.setTouchEnabled(true) // 设置是否可以触摸
        barChart.isDragEnabled = true // 是否可以拖拽
        barChart.setScaleEnabled(true) // 是否可以缩放
        //2、y轴和比例尺

        barChart.setDescription("") // 数据描述
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false
        val legend = barChart.legend //隐藏比例尺
        legend.isEnabled = false


        val openHelper = SqliteOpenHelper(activity)
        val db = openHelper.readableDatabase
        val cursor: Cursor = db.query("bills", null, "time" + " like '%$time%'", null, null, null, "time")
        var index = 0

        //3、x轴数据,和显示位置
        val xValues = ArrayList<String>()
        //4、y轴数据
        val yValues = ArrayList<BarEntry>()
        //5、设置颜色
        val colors = ArrayList<Int>()

        while (cursor.moveToNext()) {
            xValues.add(cursor.getString(5))
            //new BarEntry(20, 0)前面代表数据，后面代码柱状图的位置
            if (cursor.getInt(1) == 1) {
                colors.add(Color.argb(220,159,221,86))
            } else {
                colors.add(Color.argb(220,255,87,34))
            }

            yValues.add(BarEntry(cursor.getDouble(2).toFloat(), index++))
        }
        cursor.close()


        xAxis.position = XAxis.XAxisPosition.BOTTOM //数据位于底部

        //6、设置显示的数字为整形
        val barDataSet = BarDataSet(yValues,"")
        barDataSet.colors = colors

        //7、显示，柱状图的宽度和动画效果
        val barData = BarData(xValues, barDataSet)
        barDataSet.barSpacePercent = 40f //值越大，柱状图就越宽度越小；
        barChart.animateY(1000)
        barChart.data = barData
    }

    private fun bindlistener() {
        tv_time.setOnClickListener {
            initPieChart(Time.year.toString() + "." + Time.month.toString())
            initBarChart(Time.year.toString() + "." + Time.month.toString())
        }

        tv_choose.setOnClickListener {
            alert {

                var et_year: EditText?= null
                var et_month: EditText?= null

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

                    initPieChart("$year.$month")
                    initBarChart("$year.$month")

                    toast("选择日期：${year}年${month}月")
                }
            }.show()
        }
    }
}