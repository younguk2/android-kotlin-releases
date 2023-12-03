package com.example.myproject
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QueryDocumentSnapshot

data class Message(
    val id: String,
    val content:String,
    val sender:String,
    val receiver:String
) {
    constructor(doc: QueryDocumentSnapshot) :
            this(doc.id, doc["content"].toString(),doc["sender"].toString(),doc["receiver"].toString())
    constructor(key: String, map: Map<*, *>) :
            this(key, map["content"].toString(),map["sender"].toString(),map["receiver"].toString())
}
class MessageAdapter(private var message: ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    fun updateList(newList: ArrayList<Message>) {
        message = newList
        notifyDataSetChanged()
    }
    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderTextView: TextView = itemView.findViewById(R.id.sender)
        val contentTextView: TextView = itemView.findViewById(R.id.content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.message, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentItem = message[position]
        holder.senderTextView.text = currentItem.sender
        holder.contentTextView.text = currentItem.content

    }

    override fun getItemCount(): Int {
        Log.d("size", message.size.toString())
        return message.size
    }


}