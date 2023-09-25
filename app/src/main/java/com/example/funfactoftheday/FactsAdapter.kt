package com.example.funfactoftheday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.FactBinding

//TODO: Create empty constructor
class FactsAdapter : ListAdapter<FactModel, FactsAdapter.FactsViewHolder>(FactsComparator()) {

    //inflates layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactsViewHolder {
        val itemBinding = FactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FactsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: FactsViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
//        holder.itemView.apply {
//            tvFactSentence.text = factModels[position].sentence
//            cbFavorite.isChecked = factModels[position].isFavorite
//        }

    }

    class FactsViewHolder(private val itemBinding: FactBinding):RecyclerView.ViewHolder(itemBinding.root){
        fun bind(fact:FactModel){
            itemBinding.tvFactName.text = fact.factName
            itemBinding.cbFavorite.isChecked = fact.isFavorite
        }
    }

    class FactsComparator : DiffUtil.ItemCallback<FactModel>(){
        override fun areItemsTheSame(oldItem: FactModel, newItem: FactModel): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: FactModel, newItem: FactModel): Boolean {
            //TODO: check for isfavorite is true also
            return oldItem.factName == newItem.factName
        }
    }

}