package com.example.funfactoftheday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.FactBinding

class FactsAdapter (
    private val factModels: MutableList<FactModel>
): RecyclerView.Adapter<FactsAdapter.FactsViewHolder>() {

    //inflates layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactsViewHolder {
        val itemBinding = FactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FactsViewHolder(itemBinding)
    }

    class FactsViewHolder(private val itemBinding: FactBinding):RecyclerView.ViewHolder(itemBinding.root){
        fun bind(fact:FactModel){
            itemBinding.tvFactSentence.text = fact.factName
            itemBinding.cbFavorite.isChecked = fact.isFavorite
        }
    }

    override fun onBindViewHolder(holder: FactsViewHolder, position: Int) {
        val factModel: FactModel = factModels[position]
        holder.bind(factModel)
//        holder.itemView.apply {
//            tvFactSentence.text = factModels[position].sentence
//            cbFavorite.isChecked = factModels[position].isFavorite
//        }

    }

    override fun getItemCount(): Int {
        return factModels.size
    }

}