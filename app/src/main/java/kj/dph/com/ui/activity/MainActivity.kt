package kj.dph.com.ui.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.HandlerThread
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import kj.dph.com.R
import kj.dph.com.base.BaseActivity2016
import kj.dph.com.ui.adapter.DemoAdapter
import kj.dph.com.ui.service.LocalIntentService
import kj.dph.com.util.logUtil.LogUtilYxy
import kj.dph.com.widget.refreshlayout.RefreshLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity2016(), RefreshLayout.OnRefreshListener {


    var list: ArrayList<String> = ArrayList()
    var adapter: DemoAdapter? = null
    override fun setCustomLayout(savedInstanceState: Bundle?) {
        super.setCustomLayout(savedInstanceState)
        setLayoutView(R.layout.activity_main, "哈哈哈");
    }

    override fun initView() {
        super.initView()
        adapter = DemoAdapter(this, R.layout.item_demo, list)
        refreshlayout.setOnRefreshListener(this)
        refreshlayout.setHeaderColorSchemeColors(blue, red, green, yellow)
        refreshlayout.setFooterColorSchemeColors(blue, red, green, yellow)
        rv_home_yygh.layoutManager = LinearLayoutManager(this)
        rv_home_yygh.itemAnimator = DefaultItemAnimator()
        rv_home_yygh.adapter = adapter
        /*adapter?.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
            override fun onItemClick(view: View, viewHolder: RecyclerView.ViewHolder, i: Int) {
                HomeHospitalHomepageActivity.actionStart(context, list[i].id)
            }

            override fun onItemLongClick(view: View, viewHolder: RecyclerView.ViewHolder, i: Int): Boolean {
                return false
            }
        })*/
        adapter?.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {

            override fun onItemClick(view: View?, holder: RecyclerView.ViewHolder?, position: Int) {

                when (position) {
                    0 -> {
                        MyAsyncTask("1").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "")
                        MyAsyncTask("2").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "")
                        MyAsyncTask("3").execute("")
                        MyAsyncTask("4").execute("")
                        MyAsyncTask("5").execute("")
                    }
                    1 -> {
                        var service = Intent(this@MainActivity, LocalIntentService::class.java)
                        service.putExtra("task_action", "com.yxy.action.task1")
                        startService(service)
                        service.putExtra("task_action","com.yxy.action.task2")
                        startService(service)
                        service.putExtra("task_action","com.yxy.action.task3")
                        startService(service)
                    }
                }


            }

            override fun onItemLongClick(view: View?, holder: RecyclerView.ViewHolder?, position: Int): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    private class MyAsyncTask : AsyncTask<String, Int, String> {
        var mname: String = "mAsyncTask"

        constructor(name: String) : super() {
            mname = "mAsyncTask" + name
        }


        override fun doInBackground(vararg p0: String?): String {
            Thread.sleep(3000)
            return mname
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            var df = SimpleDateFormat("HH:mm:ss")
            LogUtilYxy.e(result + "=====----" + df.format(Date()))
        }

    }


    override fun initData() {
        super.initData()
        for (item in 0..10) {
            list.add("张三" + item)
        }
        adapter?.notifyDataSetChanged()
    }

    override fun onHeaderRefresh() {
        refreshlayout.postDelayed(Runnable {
            kotlin.run {
                list.clear()
                for (item in 0..10) {
                    list.add("张三" + item)
                }
                adapter?.notifyDataSetChanged()
                refreshlayout.isHeaderRefreshing = false
            }
        }, 1000)
    }

    override fun onFooterRefresh() {
        refreshlayout.postDelayed(Runnable {
            kotlin.run {
                for (item in 10..20) {
                    list.add("张三" + item)
                }
                adapter?.notifyDataSetChanged()
                refreshlayout.isFooterRefreshing = false
            }
        }, 1000)

    }
}
