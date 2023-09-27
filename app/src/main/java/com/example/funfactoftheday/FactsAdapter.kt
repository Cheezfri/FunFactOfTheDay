package com.example.funfactoftheday

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.FactBinding

//TODO: Turn Favorite button to an OnClickListener to toggle favorites
class FactsAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<FactModel, FactsAdapter.FactsViewHolder>(FactsComparator()) {

    //inflates layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactsViewHolder {
        val itemBinding = FactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FactsViewHolder(itemBinding, listener)
    }

    //called everytime new row is made
    override fun onBindViewHolder(holder: FactsViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
//        holder.itemView.apply {
//            tvFactSentence.text = factModels[position].sentence
//            cbFavorite.isChecked = factModels[position].isFavorite
//        }
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
            //TODO: check for isfavorite is true also
            return oldItem.factName == newItem.factName
        }
    }

    interface OnItemClickListener{
        fun onItemClick(itemBinding:FactBinding)
    }

}