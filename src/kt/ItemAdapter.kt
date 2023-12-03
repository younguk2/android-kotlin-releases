package com.example.myproject

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QueryDocumentSnapshot
import org.w3c.dom.Text

data class Item(
    val id: String="",
    val title:String="",
    val seller:String="",
    val name: String="",
    val price: Long=0,
    val detail:String="",
    val soldout:String=""
) {
    constructor(doc: QueryDocumentSnapshot) :
            this(doc.id, doc["title"].toString(),doc["seller"].toString(),doc["name"].toString(),(doc["price"] as? Long) ?: 0,doc["detail"].toString(),doc["soldout"].toString())
    constructor(key: String, map: Map<*, *>) :
            this(key, map["title"].toString(),map["seller"].toString(),map["name"].toString(),(map["price"] as? Long) ?: 0,map["detail"].toString(),map["soldout"].toString())
}



class ItemAdapter(var items: ArrayList<Item>,private val listener: OnItemClickListener) : RecyclerView.Adapter<ItemAdapter.ItemsViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(item: Item)
    }
    fun updateList(newList: ArrayList<Item>) {
        items = newList
        notifyDataSetChanged()
    }
    inner class ItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        val sellerTextView: TextView = itemView.findViewById(R.id.seller)
        val priceTextView: TextView = itemView.findViewById(R.id.price)
        val detailTextView: TextView = itemView.findViewById(R.id.detail)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val clickedItem = items[position]
                listener.onItemClick(clickedItem)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ItemsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        val currentItem = items[position]

        holder.titleTextView.text = currentItem.title
        holder.sellerTextView.text = currentItem.seller
        holder.priceTextView.text = currentItem.price.toString()
        holder.detailTextView.text = currentItem.detail

    }

    override fun getItemCount(): Int {
        Log.d("size", items.size.toString())
        return items.size
    }
}