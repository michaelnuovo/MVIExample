package com.codingwithmitch.mviexample.ui.main.state

sealed class DataRequestEvent {
    class GetBlogPostsEvent : DataRequestEvent()
    class GetUserEvent(val userId: String) : DataRequestEvent()
    class None : DataRequestEvent()
}