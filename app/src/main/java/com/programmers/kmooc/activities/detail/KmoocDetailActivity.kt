package com.programmers.kmooc.activities.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.KmoocApplication
import com.programmers.kmooc.databinding.ActivityKmookDetailBinding
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.network.ImageLoader
import com.programmers.kmooc.utils.DateUtil
import com.programmers.kmooc.utils.toVisibility
import com.programmers.kmooc.viewmodels.KmoocDetailViewModel
import com.programmers.kmooc.viewmodels.KmoocDetailViewModelFactory

class KmoocDetailActivity : AppCompatActivity() {

    companion object {
        const val INTENT_PARAM_COURSE_ID = "param_course_id"
    }

    private lateinit var binding: ActivityKmookDetailBinding
    private lateinit var viewModel: KmoocDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kmoocRepository = (application as KmoocApplication).kmoocRepository
        viewModel = ViewModelProvider(this, KmoocDetailViewModelFactory(kmoocRepository)).get(
            KmoocDetailViewModel::class.java
        )

        binding = ActivityKmookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        subscribeObserver()
    }

    private fun initViews() {
        val courseId = intent.getStringExtra(INTENT_PARAM_COURSE_ID)

        if (courseId.isNullOrEmpty()) {
            finish()
            return
        } else {
            viewModel.detail(courseId)
        }
    }

    private fun bindViews(lecture: Lecture) = with(binding) {
        toolbar.title = lecture.name
        ImageLoader.loadImage(lecture.courseImage) {
            lectureImage.setImageBitmap(it)
        }
        lectureNumber.setDescription("강좌번호 : ", lecture.number)
        lectureType.setDescription(
            "강좌분류 : ",
            "${lecture.classfyName} (${lecture.middleClassfyName})"
        )
        lectureOrg.setDescription("운영기관 : ", lecture.orgName)
        lectureTeachers.setDescription("교수정보 : ", lecture.teachers ?: "교수 정보가 없습니다.")
        lectureDue.setDescription(
            "운영기간 : ",
            DateUtil.formatDate(lecture.start) + " ~ " + DateUtil.formatDate(lecture.end)
        )
    }

    private fun subscribeObserver() {
        viewModel.lecture.observe(this) { bindViews(it) }
        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = it.toVisibility()
        }
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}