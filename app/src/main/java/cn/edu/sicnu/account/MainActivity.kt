package cn.edu.sicnu.account

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import cn.edu.sicnu.account.arouter.Path.Companion.main
import cn.edu.sicnu.account.fragment.AccountDetailFragment
import cn.edu.sicnu.account.fragment.ChartFragment
import cn.edu.sicnu.account.fragment.DiscoverFragment
import cn.edu.sicnu.account.fragment.MineFragment
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.activity_main.*
import cn.edu.sicnu.account.arouter.Path
import com.alibaba.android.arouter.launcher.ARouter
import java.nio.channels.Channel

@Route(path = Path.main)
class MainActivity : AppCompatActivity() {

    val Channel_ID = "my channal"
    val Notification_ID = 1

    var detailFragment : AccountDetailFragment? = null
    var chartFragment : ChartFragment? = null
    var discoverFragment : DiscoverFragment? = null
    var mineFragment : MineFragment? = null

    var fm : FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initNotification()
        initView()
        bindListener()
    }

    private fun initNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder: Notification.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(Channel_ID, "test", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
            builder = Notification.Builder(this, Channel_ID)
        } else {
            builder = Notification.Builder(this)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = builder.setSmallIcon(R.drawable.app_icon)
            .setContentTitle("BB 账本")
            .setContentText("需要记账，马上点我~")
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .build()

        notificationManager.notify(Notification_ID, notification)
    }

    // 初始化视图
    private fun initView() {
        detailFragment = AccountDetailFragment.newInstance()
        chartFragment = ChartFragment.newInstance()
        discoverFragment = DiscoverFragment.newInstance()
        mineFragment = MineFragment.newInstance()
        val fragments = listOf(detailFragment, chartFragment,discoverFragment,mineFragment)
        fm  = supportFragmentManager
        val ft = fm!!.beginTransaction()
        for (f in fragments){
            ft.add(R.id.container,f)
        }
        ft.hide(detailFragment)
        ft.hide(chartFragment)
        ft.hide(discoverFragment)
        ft.hide(mineFragment)
        ft.show(detailFragment)
        ft.commit()

    }

    // 绑定事件监听
    private fun bindListener() {
        // add 添加按钮
        iv_add.setOnClickListener { ARouter.getInstance().build(Path.edit).navigation() }
        //底部导航栏的点击事件
        rg_bottom.setOnCheckedChangeListener { _, id ->
            when(id){
                R.id.rb_detail -> showFragment(detailFragment!!)
                R.id.rb_chart -> showFragment(chartFragment!!)
                R.id.rb_discover -> showFragment(discoverFragment!!)
                R.id.rb_mine -> showFragment(mineFragment!!)
            }
        }
    }

    private fun showFragment(f : Fragment){
        val ft = fm!!.beginTransaction()
        ft.hide(detailFragment)
        ft.hide(chartFragment)
        ft.hide(discoverFragment)
        ft.hide(mineFragment)
        ft.show(f)
        ft.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

