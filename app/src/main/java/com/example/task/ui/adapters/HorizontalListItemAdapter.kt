package com.example.task.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.task.R
import com.example.task.data.model.ListData
import com.example.task.ui.OnRvListItemClickListener
import kotlinx.android.synthetic.main.adapter_horizontal_list_item.view.*


class HorizontalListItemAdapter
    (
    private val mSelectedListData: ArrayList<ListData>
) : RecyclerView.Adapter<HorizontalListItemAdapter.DataViewHolder>(), View.OnClickListener {
    lateinit var mRvListItemClickListener: OnRvListItemClickListener
    lateinit var viewHolder: DataViewHolder

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAHLISelectedTitle: TextView = itemView.tvAhliSelectedTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_horizontal_list_item, parent,
                false
            )
        )

    override fun getItemCount(): Int = mSelectedListData.size

    override fun onBindViewHolder(itemView: DataViewHolder, position: Int) {
        val listData = mSelectedListData[position]
        viewHolder = itemView
        viewHolder.tvAHLISelectedTitle.text = listData.title
        viewHolder.tvAHLISelectedTitle.setOnClickListener(this)
        viewHolder.tvAHLISelectedTitle.setTag(R.id.selected_list_pos, position)
    }

    fun addAllData(list: List<ListData>) {
        mSelectedListData.addAll(list)
    }

    fun removeAt(position: Int) {
        mSelectedListData.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, mSelectedListData.size)
    }

    fun addItem(listData: ListData) {
        mSelectedListData.add(listData)
        var index = mSelectedListData.indexOf(listData)
        notifyItemInserted(index)
        notifyItemChanged(index, mSelectedListData.size)
    }

    fun setOnItemClickListener(rvListItemClickListener: OnRvListItemClickListener) {
        mRvListItemClickListener = rvListItemClickListener
    }

    override fun onClick(view: View?) {
        var position: Int = view?.getTag(R.id.selected_list_pos) as Int
        val data: ListData = this.mSelectedListData[position]
        removeAt(position)
        var showNoData : Boolean = false
        if (mSelectedListData.size == 0){
            showNoData = true
        }
        mRvListItemClickListener.onItemClick(view, position, data, showNoData)
    }

}
