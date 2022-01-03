package com.example.androidflowdemoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidflowdemoapp.model.CommentModel
import com.example.androidflowdemoapp.network.CommentApiState
import com.example.androidflowdemoapp.network.Status
import com.example.androidflowdemoapp.repository.CommentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(val repository: CommentsRepository): ViewModel() {

    val commentState = MutableStateFlow(
        CommentApiState(
            Status.LOADING,
            CommentModel(), ""
        )
    )

    init {
        // Initiate a starting
        getNewComment(1)
    }

    /**
     * Function to get new Comments
     */
     fun getNewComment(commentId: Int) {
        // Since Network Calls takes time,Set the initial value to loading state
        commentState.value = CommentApiState.loading()

        viewModelScope.launch {

            // Collecting the data emitted by the function in repository
            repository.getComment(commentId)
                // If any errors occurs like 404 not found or invalid query, set the state to error State to show some info on screen
                .catch {
                    commentState.value =
                        CommentApiState.error(it.message.toString())
                }
                // If Api call is succeeded, set the State to Success and set the response data to data received from api
                .collect {
                    commentState.value = CommentApiState.success(it.data)
                }
        }
    }
}