package com.azatberdimyradov.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.azatberdimyradov.newsapp.R
import com.azatberdimyradov.newsapp.adapters.NewsAdapter
import com.azatberdimyradov.newsapp.adapters.NewsLoadStateAdapter
import com.azatberdimyradov.newsapp.databinding.FragmentBreakingNewsBinding
import com.azatberdimyradov.newsapp.ui.MainActivity
import com.azatberdimyradov.newsapp.ui.NewsViewModel
import com.azatberdimyradov.newsapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    private val viewModel: NewsViewModel by viewModels()
    private lateinit var binding: FragmentBreakingNewsBinding
    lateinit var newsAdapter: NewsAdapter

    val TAG = "BreakingNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBreakingNewsBinding.bind(view)
        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }
        observe()
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.breakingNewsEvent.collect { event ->
                when(event){
                    is NewsViewModel.NewsState.BreakingNewsResponse -> {
                        newsAdapter.submitData(event.resource)
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter.withLoadStateFooter(
                footer = NewsLoadStateAdapter{ newsAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
        newsAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading){
                showProgressBar()
            }else {
                hideProgressBar()

                //getting the error
                val error = when {
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                error?.let {
                    Toast.makeText(requireContext(),it.error.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }
}