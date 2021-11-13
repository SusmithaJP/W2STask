package com.example.task.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.task.R
import com.example.task.data.model.ListData
import com.example.task.ui.OnRvListItemClickListener
import kotlinx.android.synthetic.main.adapter_list_item.view.*


class ListItemAdapter
    (
    private val mListData: ArrayList<ListData>
) : RecyclerView.Adapter<ListItemAdapter.DataViewHolder>(),View.OnClickListener {
    lateinit var mRvListItemClickListener: OnRvListItemClickListener
    lateinit var viewHolder: DataViewHolder

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cbALITitle : CheckBox = itemView.cbAliTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_list_item, parent,
                false
            )
        )

    override fun getItemCount(): Int = mListData.size

    override fun onBindViewHolder(itemView: DataViewHolder, position: Int) {
        val listData = mListData[position]
        viewHolder = itemView
        viewHolder.cbALITitle.text = listData.title
        viewHolder.cbALITitle.setOnCheckedChangeListener(null)
        viewHolder.cbALITitle.setSelected(listData.isSelected)
        viewHolder.cbALITitle.setOnClickListener(this)
        viewHolder.cbALITitle.setTag(R.id.selected_list_pos,position)
    }

    fun addAllData(list: List<ListData>) {
        mListData.addAll(list)
    }

    fun removeAt(position: Int) {
        mListData.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, mListData.size)
    }

    fun addItem(listData: ListData){
        mListData.add(listData)
        var index = mListData.indexOf(listData)
        notifyItemInserted(index)
        notifyItemChanged(index, mListData.size)
    }

    fun setOnItemClickListener(rvListItemClickListener: OnRvListItemClickListener) {
        mRvListItemClickListener = rvListItemClickListener
    }

    override fun onClick(view: View?) {
        var position : Int = view?.getTag(R.id.selected_list_pos) as Int
        val data : ListData = this.mListData[position]
        var checkBox : CheckBox = view as CheckBox
        checkBox?.setChecked(data.isSelected)
        removeAt(position)
        var showNoData : Boolean = false
        if (mListData.size == 0){
            showNoData = true
        }
        mRvListItemClickListener.onItemClick(view, position, data, showNoData)
    }

}



