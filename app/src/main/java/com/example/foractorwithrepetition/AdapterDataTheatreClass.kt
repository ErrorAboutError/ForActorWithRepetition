package com.example.foractorwithrepetition

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foractorwithrepetition.ui.detail.FragmentDetail
import com.example.foractorwithrepetition.ui.slideshow.SlideshowFragment

class AdapterDataTheatreClass(
    private var context: Context,
    private var dataList: List<DataThearteClass> = emptyList(), // Initialize with an empty list
    private val listener: OnItemClickListener // SlideshowFragment
) : RecyclerView.Adapter<AdapterDataTheatreClass.MyViewHolder>() {

    fun setSearchList(dataSearchList: List<DataThearteClass>?) {
        dataList = dataSearchList ?: emptyList() // Handle null case
        notifyDataSetChanged()
    }
    interface OnItemClickListener {
        fun onItemClick(item: DataThearteClass)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.recImage?.setImageResource(currentItem.getDataImage())
        holder.recTitle?.text = currentItem.getDataTitle()
        holder.recDesc?.text = currentItem.getDataDesc().toString()
        holder.recLang?.text = currentItem.getDataLang()
//        // Загрузка изображения
//        currentItem.imageUrl?.let { url ->
//            Glide.with(holder.recImage?.context)
//                .load(url)
//                .placeholder(R.drawable.theareslider)  // Замените на свой ресурс-заполнитель
//                .into(holder.recImage)
//        } ?: holder.recImage?.setImageResource(R.drawable.theareslider)  // Пустое изображение, если URL-адреса нет

//        holder.recCard?.setOnClickListener {
//            //Переход к фрагменту о мероприятии
//
//            val context = holder.itemView.context
//            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
//            val transaction = fragmentManager.beginTransaction()
//
//            val detailFragment = FragmentDetail().apply {
//                arguments = Bundle().apply {
//                    putString("Image", currentItem.getDataImage().toString())
//                    putString("Title", currentItem.getDataTitle())
//                    putString("Desc", currentItem.getDataDesc().toString())
//                }
//            }
//
//            // Подставьте корректный ID контейнера, в который должен быть заменён фрагмент
//            transaction.replace(
//                R.id.nav_host_fragment_content_activity_with_drawer_navigation,
//                detailFragment
//            )
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }
    }

            //Другой вариант
//            // Получаем NavController из корректного контекста
//            val navController = findNavController(holder.itemView)
//
//            // Создаем аргумент для перехода
//            val action = SlideshowFragment.action_yourCurrentFragment_to_fragmentDetail(
//                currentItem.getDataImage().toString(),
//                currentItem.getDataTitle(),
//                currentItem.getDataDesc().toString()
//            )
//
//            // Переход к FragmentDetail
//            navController.navigate(action)

  //      }



    override fun getItemCount(): Int {
        return dataList.size
    }

//    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var recImage: ImageView? = itemView.findViewById(R.id.recImage)
//        var recCard: CardView? = itemView.findViewById(R.id.recCard)
//        var recTitle: TextView? = itemView.findViewById(R.id.recTitle)
//        var recDesc: TextView? = itemView.findViewById(R.id.recDesc)
//        var recLang: TextView? = itemView.findViewById(R.id.recLang)
//    }
     inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recImage: ImageView? = itemView.findViewById(R.id.recImage)
        val recTitle: TextView? = itemView.findViewById(R.id.recTitle)
        val recDesc: TextView? = itemView.findViewById(R.id.recDesc)
        val recLang: TextView? = itemView.findViewById(R.id.recLang)
        private val recCard: CardView? = itemView.findViewById(R.id.recCard)

        init {
            recCard?.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(dataList[position])
                }
            }
        }

    }
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val itemView = LayoutInflater.from(context).inflate(R.layout.fragment_slideshow, parent, false)
//        return MyViewHolder(itemView)
//    }

//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val currentItem = dataList[position]
//        holder.recImage?.setImageResource(currentItem.getDataImage())
//        holder.recTitle?.text = currentItem.getDataTitle()
//        holder.recDesc?.text = context.getString(currentItem.getDataDesc())
//        holder.recLang?.text = currentItem.getDataLang()
//    }

}

