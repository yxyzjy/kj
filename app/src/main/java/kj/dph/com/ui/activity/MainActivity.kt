package kj.dph.com.ui.activity

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import kj.dph.com.R
import kj.dph.com.base.BaseActivity2016
import kj.dph.com.ui.adapter.DemoAdapter
import kj.dph.com.widget.refreshlayout.RefreshLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity2016(), RefreshLayout.OnRefreshListener {


    var list: ArrayList<String> = ArrayList()
    var adapter: DemoAdapter ?= null
    override fun setCustomLayout(savedInstanceState: Bundle?) {
        super.setCustomLayout(savedInstanceState)
        setLayoutView(R.layout.activity_main, "哈哈哈");
    }

    override fun initView() {
        super.initView()
        adapter= DemoAdapter(this, R.layout.item_demo, list)
        refreshlayout.setOnRefreshListener(this)
        refreshlayout.setHeaderColorSchemeColors(blue, red, green, yellow)
        refreshlayout.setFooterColorSchemeColors(blue, red, green, yellow)
        rv_home_yygh.layoutManager = LinearLayoutManager(this)
        rv_home_yygh.itemAnimator = DefaultItemAnimator()
        rv_home_yygh.adapter = adapter
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
