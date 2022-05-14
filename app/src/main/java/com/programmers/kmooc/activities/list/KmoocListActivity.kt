package com.programmers.kmooc.activities.list

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.programmers.kmooc.KmoocApplication
import com.programmers.kmooc.activities.detail.KmoocDetailActivity
import com.programmers.kmooc.databinding.ActivityKmookListBinding
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.utils.toVisibility
import com.programmers.kmooc.viewmodels.KmoocListViewModel
import com.programmers.kmooc.viewmodels.KmoocListViewModelFactory

class KmoocListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKmookListBinding
    private lateinit var viewModel: KmoocListViewModel

    private val adapter by lazy {
        LecturesAdapter()
            .apply { onClick = this@KmoocListActivity::startDetailActivity }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kmoocRepository = (application as KmoocApplication).kmoocRepository
        viewModel = ViewModelProvider(this, KmoocListViewModelFactory(kmoocRepository)).get(
            KmoocListViewModel::class.java
        )

        binding = ActivityKmookListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        subscribeObserver()
        infinityRecyclerView()
    }

    private fun initViews() {
        binding.pullToRefresh.setOnRefreshListener {
            viewModel.list()
        }

        viewModel.list()

        binding.lectureList.layoutManager = LinearLayoutManager(this@KmoocListActivity)
        binding.lectureList.adapter = adapter
    }

    private fun subscribeObserver() {
        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = it.toVisibility()
        }

        viewModel.lectures.observe(this) {
            adapter.updateLectures(it.lectures)
            binding.pullToRefresh.isRefreshing = false
        }
    }

    private fun infinityRecyclerView() {
        binding.lectureList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Todo 중복 요청 해결 필요

                if (viewModel.isLoading.value != true) {
                    val layoutManager =
                        binding.lectureList.layoutManager ?: error("Not Initialized")
                    val lastItemManager =
                        (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

                    if (layoutManager.itemCount <= lastItemManager + 5) {
                        viewModel.next()
                    }
                }
            }
        })
    }

    private fun startDetailActivity(lecture: Lecture) {
        startActivity(
            Intent(this, KmoocDetailActivity::class.java)
                .apply { putExtra(KmoocDetailActivity.INTENT_PARAM_COURSE_ID, lecture.id) }
        )
    }
}
