package cn.edu.sicnu.account.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color.red
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import cn.edu.sicnu.account.R
import cn.edu.sicnu.account.data.Items

class ItemAdapter//构造
    (var context: Context, var arr: ArrayList<Items>) : BaseAdapter() {
    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return arr[position].id!!.toLong()
    }

    override fun getCount(): Int {
        return arr.size
    }

    @SuppressLint("ViewHolder", "SetTextI18n", "ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.data_items,null)
        val tv_in_or_out: TextView =view.findViewById(R.id.tv_in_or_out)
        val tv_money: TextView =view.findViewById(R.id.tv_money)
        val tv_type: TextView =view.findViewById(R.id.tv_type)
        val tv_note: TextView =view.findViewById(R.id.tv_note)
        val tv_time: TextView =view.findViewById(R.id.tv_time)

        var symbol = ""
        //通过text设置值
        if (arr[position].in_or_out == 1) {
            tv_in_or_out.setTextColor(context.resources.getColor(R.color.green))

            tv_in_or_out.text = "收入"
            symbol = "+"
        } else if (arr[position].in_or_out == 0) {
            tv_in_or_out.setTextColor(context.resources.getColor(R.color.red))
            tv_in_or_out.text = "支出"
            symbol = "-"
        }
        tv_money.text = symbol + arr[position].money.toString()
        tv_type.text = arr[position].type
        tv_note.text = arr[position].note
        tv_time.text = arr[position].time

        return  view
    }
}