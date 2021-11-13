package com.example.task.ui.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.task.R
import com.example.task.data.model.ListData
import com.example.task.data.repository.MainRepository
import com.example.task.data.repository.RetrofitService
import com.example.task.ui.OnRvListItemClickListener
import com.example.task.ui.TaskViewModelFactory
import com.example.task.ui.adapters.HorizontalListItemAdapter
import com.example.task.ui.adapters.ListItemAdapter
import com.example.task.ui.viewmodel.ListDataViewModel
import com.example.task.utils.Status
import kotlinx.android.synthetic.main.fragment_list_view.*

class ListViewFragment : Fragment(), OnRvListItemClickListener {
    private val TAG = "ListViewFragment"
    private lateinit var mViewModel: ListDataViewModel
    private val mRetrofitService = RetrofitService.getInstance()
    private lateinit var mListAdapter: ListItemAdapter
    private lateinit var mHorizontalListItemAdapter: HorizontalListItemAdapter
    private lateinit var mRvFlvSelectedTitleList: RecyclerView
    private lateinit var mRvFlvTitleList: RecyclerView
    private lateinit var mLlFlvLoaderContainer: LinearLayout
    private lateinit var mTvFlvSelectedTitle: TextView
    private lateinit var mTvFlvListTitle: TextView
    private var mListItem : ArrayList<ListData> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_list_view, container, false)
        setupUI(view)
        setupViewModel()
        setupObserver()
        return view
    }

    private fun setupObserver() {
        mViewModel.getAllListDatas().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    mLlFlvLoaderContainer.visibility = View.GONE
                    it.data?.let { listData -> renderList(listData) }
                    rvFlvTitleList.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    mLlFlvLoaderContainer.visibility = View.VISIBLE
                    rvFlvTitleList.visibility = View.GONE
                }
                Status.ERROR -> {
                    //Handle Error
                    mLlFlvLoaderContainer.visibility = View.GONE
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProvider(this, TaskViewModelFactory(MainRepository(mRetrofitService))).get(
                ListDataViewModel::class.java
            )
    }

    private fun setupUI(view: View) {
        mTvFlvListTitle = view.findViewById(R.id.tvFlvListTitle)
        mTvFlvSelectedTitle = view.findViewById(R.id.tvFlvSelectedTitle)
        mLlFlvLoaderContainer = view.findViewById(R.id.llFlvLoaderContainer)
        mRvFlvTitleList = view.findViewById(R.id.rvFlvTitleList)
        mListAdapter = ListItemAdapter(arrayListOf())
        mListAdapter.setOnItemClickListener(this)
        mRvFlvTitleList.adapter = mListAdapter
        mRvFlvSelectedTitleList = view.findViewById(R.id.rvFlvSelectedTitleList)
        mHorizontalListItemAdapter = HorizontalListItemAdapter(arrayListOf())
        mHorizontalListItemAdapter.setOnItemClickListener(this)
        mRvFlvSelectedTitleList.adapter = mHorizontalListItemAdapter
        mTvFlvSelectedTitle.setText(activity?.resources?.getString(R.string.no_select_data_found))
        mRvFlvSelectedTitleList.visibility = View.GONE

    }

    private fun renderList(listData: List<ListData>) {
        mListItem.addAll(listData)
        mListAdapter.addAllData(listData)
        mListAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(
        view: View?,
        itemSelectedPosition: Int,
        itemSelected: ListData,
        showNoDataText: Boolean
    ) {
        when (view?.id) {
            R.id.cbAliTitle -> {
                if (showNoDataText) {
                    mTvFlvListTitle.setText(activity?.resources?.getString(R.string.no_data_found))
                    mTvFlvSelectedTitle.visibility = View.GONE
                    mRvFlvSelectedTitleList.visibility = View.GONE
                    mRvFlvTitleList.visibility = View.GONE
                } else {
                    mTvFlvListTitle.setText(activity?.resources?.getString(R.string.data_list))
                    mTvFlvSelectedTitle.visibility = View.VISIBLE
                    mRvFlvSelectedTitleList.visibility = View.VISIBLE
                    mRvFlvTitleList.visibility = View.VISIBLE
                    mRvFlvTitleList.post(Runnable {
                        mHorizontalListItemAdapter.addItem(itemSelected)
                    })
                }
            }
            R.id.tvAhliSelectedTitle -> {
                if (showNoDataText) {
                    mTvFlvSelectedTitle.setText(activity?.resources?.getString(R.string.no_select_data_found))
                    mRvFlvSelectedTitleList.visibility = View.GONE
                } else {
                    mTvFlvSelectedTitle.setText(activity?.resources?.getString(R.string.selected_data))
                    mRvFlvSelectedTitleList.visibility = View.VISIBLE
                    mRvFlvTitleList.post(Runnable {
                        mListAdapter.addItem(itemSelected)
                    })
                }
            }
        }
    }


}