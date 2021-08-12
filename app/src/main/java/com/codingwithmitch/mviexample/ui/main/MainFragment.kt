package com.codingwithmitch.mviexample.ui.main

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codingwithmitch.mviexample.R
import com.codingwithmitch.mviexample.model.BlogPost
import com.codingwithmitch.mviexample.model.User
import com.codingwithmitch.mviexample.ui.DataStateListener
import com.codingwithmitch.mviexample.ui.main.state.MainStateEvent.*
import com.codingwithmitch.mviexample.ui.main.state.MainViewState
import com.codingwithmitch.mviexample.util.DataState
import com.codingwithmitch.mviexample.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(),
    MainRecyclerAdapter.Interaction {
    lateinit var viewModel: MainViewModel
    lateinit var dataStateHandler: DataStateListener
    lateinit var mainRecyclerAdapter: MainRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = activity?.run {
            ViewModelProvider(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        subscribeObservers()
        initRecyclerView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataStateHandler = context as DataStateListener
        } catch (e: ClassCastException) {
            println("$context must implement DataStateListener")
        }
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        println("DEBUG: CLICKED ${position}")
        println("DEBUG: CLICKED ${item}")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_get_blogs -> triggerGetBlogsEvent()

            R.id.action_get_user -> triggerGetUserEvent()
        }

        return super.onOptionsItemSelected(item)
    }

    // private

    private fun subscribeObservers() {
        subscribeDataStateObserver()
        subscribeViewStateObserver()
    }

    private fun initRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecorator)
            mainRecyclerAdapter = MainRecyclerAdapter(this@MainFragment)
            adapter = mainRecyclerAdapter
        }
    }

    /**
     * Receive indicated data provided by the repository.
     */
    private fun subscribeDataStateObserver() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            handleLoadingAndMessage(dataState)
            handleData(dataState)
        })
    }

    /**
     *
     */
    private fun handleData(dataState: DataState<MainViewState>){
        dataState.data?.let { event ->
            event.getContentIfNotHandled()?.let { mainViewState ->
                mainViewState.blogPosts?.let { viewModel.setBlogListData(it) }
                mainViewState.user?.let { viewModel.setUser(it) }
            }
        }
    }

    private fun handleLoadingAndMessage(dataState: DataState<MainViewState>) {
        dataStateHandler.onDataStateChange(dataState)
    }

    private fun subscribeViewStateObserver() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.blogPosts?.let { blogPosts ->
                // set BlogPosts to RecyclerView
                println("DEBUG: Setting blog posts to RecyclerView: ${blogPosts}")
                mainRecyclerAdapter.submitList(blogPosts)
            }

            viewState.user?.let { user ->
                // set User data to widgets
                println("DEBUG: Setting User data: ${user}")
                setUserProperties(user)

            }
        })
    }

    private fun setUserProperties(user: User) {
        email.setText(user.email)
        username.setText(user.username)

        view?.let {
            Glide.with(it.context)
                .load(user.image)
                .into(image)
        }

    }

    private fun triggerGetUserEvent() {
        viewModel.setStateEvent(GetUserEvent("1"))
    }

    private fun triggerGetBlogsEvent() {
        viewModel.setStateEvent(GetBlogPostsEvent())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }
}














