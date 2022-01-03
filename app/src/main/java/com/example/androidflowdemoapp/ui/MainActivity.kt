package com.example.androidflowdemoapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.androidflowdemoapp.databinding.ActivityMainBinding
import com.example.androidflowdemoapp.model.CommentModel
import com.example.androidflowdemoapp.network.Status
import com.example.androidflowdemoapp.viewmodel.CommentsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), View.OnClickListener {
    // create a CommentsViewModel
    // variable to initialize it later
    private lateinit var viewModel: CommentsViewModel

    // create a view binding variable
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewInflation()
        setUpViewModel()
    }

    /**
     * Setup view model
     */
    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this)[CommentsViewModel::class.java]

        // Since flow run asynchronously, start listening on background thread
        lifecycleScope.launch {
            viewModel.commentState.collect {
                // When state to check the
                // state of received data
                when (it.status) {
                    // If its loading state then show the progress bar
                    Status.LOADING -> {
                        binding.progressBar.isVisible = true
                    }
                    // If api call was a success , Update the UI with data and make progress bar invisible
                    Status.SUCCESS -> {
                        binding.progressBar.isVisible = false
                        it.data?.let { comment ->
                            loadCommentData(comment)
                        }
                    }
                    // In case of error, show some data to user
                    else -> {
                        binding.progressBar.isVisible = false
                        Toast.makeText(this@MainActivity, "${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }


    /**
     * Inflate View
     */
    private fun viewInflation() {
        binding.btnSearch.setOnClickListener(this)
    }

    /**
     * Bind Data to View
     */
    private fun loadCommentData(comment: CommentModel){
        binding.tvCommentIdValue.text = comment.id.toString()
        binding.tvNameValue.text = comment.name
        binding.tvEmailValue.text = comment.email
        binding.tvCommentValue.text = comment.comment
    }

    override fun onClick(v: View?) {
        if (v?.id == binding.btnSearch.id) {
            if (binding.etSearch.text.isNullOrEmpty()) {
                Toast.makeText(this, "Query Can't be empty", Toast.LENGTH_SHORT).show()
            } else {
                // if Query isn't empty, make the api call
                viewModel.getNewComment(binding.etSearch.text.toString().toInt())
            }
        }
    }
}