package cn.edu.sicnu.account

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter

class AccountApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG){
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
    }
}