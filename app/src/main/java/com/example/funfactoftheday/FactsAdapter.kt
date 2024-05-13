package com.example.funfactoftheday

import android.graphics.ColorSpace.Model
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        if(current.isDeletable){
            holder.bindDeletable(current)
        } else {
            holder.bindFavorite(current)
        }
    }

    class FactsViewHolder(private val itemBinding: FactBinding, private val listener: OnItemClickListener):RecyclerView.ViewHolder(itemBinding.root){
        fun bindFavorite(fact:FactModel){
            itemBinding.tvFactName.text = fact.factName
            itemBinding.cbFavorite.isChecked = fact.isFavorite
            itemBinding.cbFavorite.visibility = View.VISIBLE
            itemBinding.cbDelete.visibility = View.INVISIBLE
            itemBinding.cbFavorite.setOnClickListener{
                listener.onFavoriteClick(itemBinding)
            }
            itemBinding.tvFactName.setOnLongClickListener{
                listener.onTextHold(itemBinding)
                return@setOnLongClickListener true
            }
            itemBinding.cbDelete.setOnClickListener{
                listener.onDeleteClick(itemBinding)
            }
        }
        fun bindDeletable(fact:FactModel){
            itemBinding.tvFactName.text = fact.factName
            itemBinding.cbFavorite.visibility = View.INVISIBLE
            itemBinding.cbDelete.visibility = View.VISIBLE
            itemBinding.cbFavorite.setOnClickListener{
                listener.onFavoriteClick(itemBinding)
            }
            itemBinding.tvFactName.setOnLongClickListener{
                listener.onTextHold(itemBinding)
                return@setOnLongClickListener true
            }
            itemBinding.cbDelete.setOnClickListener{
                listener.onDeleteClick(itemBinding)
            }
        }
    }

    class FactsComparator : DiffUtil.ItemCallback<FactModel>(){
        override fun areItemsTheSame(oldItem: FactModel, newItem: FactModel): Boolean {
            return (oldItem.factName == newItem.factName &&
                    oldItem.isFavorite == newItem.isFavorite &&
                    oldItem.isDeletable == newItem.isDeletable)
        }

        override fun areContentsTheSame(oldItem: FactModel, newItem: FactModel): Boolean {
            return (oldItem.factName == newItem.factName &&
                    oldItem.isFavorite == newItem.isFavorite &&
                    oldItem.isDeletable == newItem.isDeletable)
        }
    }

    interface OnItemClickListener{
        fun onFavoriteClick(itemBinding:FactBinding)
        fun onDeleteClick(itemBinding: FactBinding)
        fun onTextHold(itemBinding: FactBinding)
    }

}