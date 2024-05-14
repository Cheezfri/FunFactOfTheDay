package com.example.funfactoftheday.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.funfactoftheday.R
import com.example.funfactoftheday.databinding.ViewpagerItemBinding

class ViewPagerAdapter(private val viewPagerItemArrayList: ArrayList<ViewPagerItem>) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    lateinit var binding: ViewpagerItemBinding
    class ViewHolder(var view: ViewpagerItemBinding):RecyclerView.ViewHolder(view.root){
        fun bind(viewPagerItem: ViewPagerItem){
            view.ivImage.setImageResource(viewPagerItem.imageID)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        binding = ViewpagerItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val viewPagerItem = viewPagerItemArrayList[position]
        viewHolder.bind(viewPagerItem)
//        viewHolder.imageView.setImageResource(viewPagerItemArrayList[position].imageID)
    }

    override fun getItemCount(): Int {
        return viewPagerItemArrayList.size
    }

}