package com.codingwithmitch.mviexample.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codingwithmitch.mviexample.model.BlogPost
import com.codingwithmitch.mviexample.model.User
import com.codingwithmitch.mviexample.repository.Repository
import com.codingwithmitch.mviexample.ui.main.state.DataRequestEvent
import com.codingwithmitch.mviexample.ui.main.state.DataRequestEvent.*
import com.codingwithmitch.mviexample.ui.main.state.MainViewState
import com.codingwithmitch.mviexample.util.AbsentLiveData
import com.codingwithmitch.mviexample.util.DataState

class MainDataViewModel : ViewModel() {

    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData()
    val viewState: LiveData<MainViewState>
        get() = _viewState

    private val _dataStateEvent: MutableLiveData<DataRequestEvent> = MutableLiveData()
    val dataStateEvent: LiveData<DataState<MainViewState>>
        get() = Transformations.switchMap(_dataStateEvent) { stateEvent ->
            stateEvent?.let { handleStateEvent(stateEvent) }
        }

    fun emitDataEvent(event: DataRequestEvent) {
        _dataStateEvent.value = event
    }

    /**
     * Update blog posts in view state
     */
    fun emitBlogListDataToUi(blogPosts: List<BlogPost>) {
        _viewState.value = getCurrentViewStateOrNew().run {
            this.blogPosts = blogPosts
            this
        }
    }

    /**
     * Update user field in view state
     */
    fun emitUserDataToUi(user: User) {
        _viewState.value = getCurrentViewStateOrNew().run {
            this.user = user
            this
        }
    }

    // private

    /**
     * Return existing view state
     */
    private fun getCurrentViewStateOrNew(): MainViewState {
        return viewState.value?.let { it } ?: MainViewState()
    }

    /**
     *
     */
    private fun handleStateEvent(stateEvent: DataRequestEvent): LiveData<DataState<MainViewState>> {
        return when (stateEvent) {
            is GetBlogPostsEvent -> { Repository.getBlogPosts() }
            is GetUserEvent -> { Repository.getUser(stateEvent.userId) }
            is None -> { AbsentLiveData.create() }
        }
    }
}













