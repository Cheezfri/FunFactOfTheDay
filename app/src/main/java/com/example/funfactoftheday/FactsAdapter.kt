package com.example.funfactoftheday

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.FactBinding
import timber.log.Timber

class FactsAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<FactModel, FactsAdapter.FactsViewHolder>(FactsComparator())
{

    fun getItemPosition(factName: String?): Int {
        for (i in 0 until this.currentList.size) {
            if (this.currentList[i].factName.contentEquals(factName)) {
                return i
            }
        }
        return -1
    }

    //inflates layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactsViewHolder {
        val itemBinding = FactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FactsViewHolder(itemBinding, listener)
    }

    //called everytime new row is made
    override fun onBindViewHolder(holder: FactsViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class FactsViewHolder(private val itemBinding: FactBinding, private val listener: OnItemClickListener):RecyclerView.ViewHolder(itemBinding.root){
        fun bind(fact:FactModel){
            itemBinding.tvFactName.text = fact.factName
            itemBinding.cbFavorite.isChecked = fact.isFavorite
            itemBinding.cbFavorite.setOnClickListener{
                listener.onItemClick(itemBinding)
            }
        }
    }

    class FactsComparator : DiffUtil.ItemCallback<FactModel>(){
        override fun areItemsTheSame(oldItem: FactModel, newItem: FactModel): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: FactModel, newItem: FactModel): Boolean {
            return (oldItem.factName == newItem.factName && oldItem.isFavorite == newItem.isFavorite)
        }
    }

    interface OnItemClickListener{
        fun onItemClick(itemBinding:FactBinding)
    }

}