package com.example.funfactoftheday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.CategoryBinding
import com.example.funfactoftheday.databinding.FactBinding

class CategoryAdapter (
    private val listener: OnItemClickListener
    ): ListAdapter<CategoryModel, CategoryAdapter.CategoriesViewHolder>(CategoriesComparator()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
            val itemBinding = CategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CategoriesViewHolder(itemBinding, listener)
        }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

        class CategoriesViewHolder(private val itemBinding: CategoryBinding, private val listener: OnItemClickListener):RecyclerView.ViewHolder(itemBinding.root){
            fun bind(category: CategoryModel){
                itemBinding.tvCategoryName.text = category.categoryName
                itemBinding.cbFavorite.isChecked = category.isFavorite
                itemBinding.cbFavorite.setOnClickListener{
                    listener.onItemClick(itemBinding)
                }
            }
        }

    class CategoriesComparator : DiffUtil.ItemCallback<CategoryModel>(){
        override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            //TODO: check for isfavorite is true also
            return oldItem.categoryName == newItem.categoryName
        }
    }

    interface OnItemClickListener{
        fun onItemClick(itemBinding: CategoryBinding)
    }

    }