package cn.edu.sicnu.account.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import cn.edu.sicnu.account.R
import cn.edu.sicnu.account.arouter.Path
import cn.edu.sicnu.account.auth.LoginActivity
import cn.edu.sicnu.account.auth.User
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.android.synthetic.main.fragment_mine_login.*

class MineFragment : Fragment() {

    companion object {

        fun newInstance():MineFragment{
            val args = Bundle()
            val fragment = MineFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutID(),container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!User.isLogin) {
            tv_mine_login.setOnClickListener {
                ARouter.getInstance().build(Path.login).navigation()
            }

            tv_mine_register.setOnClickListener {
                ARouter.getInstance().build(Path.register).navigation()
            }
        } else {
            btn_logout.setOnClickListener {
                ARouter.getInstance().build(Path.login).navigation()
            }
        }
    }

    private fun getLayoutID(): Int {
        return if (User.isLogin)
            R.layout.fragment_mine_login
        else
            R.layout.fragment_mine
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}