package com.example.funfactoftheday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.databinding.CategoryBinding

class CategoryAdapter (
    private val categoryModels: MutableList<CategoryModel>
    ): RecyclerView.Adapter<CategoryAdapter.CategoriesViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
                val itemBinding = CategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return CategoriesViewHolder(itemBinding)
        }

        class CategoriesViewHolder(private val itemBinding: CategoryBinding):RecyclerView.ViewHolder(itemBinding.root){
                fun bind(category: CategoryModel){
                        itemBinding.tvCategoryName.text = category.categoryName
                        itemBinding.cbFavorite.isChecked = category.isFavorite
                }
        }

        override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
                val categoryModel:CategoryModel = categoryModels[position]
                holder.bind(categoryModel)
        }

        override fun getItemCount(): Int {
                return categoryModels.size
        }

    }