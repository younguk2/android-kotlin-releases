package com.example.myproject

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MessageActivity: AppCompatActivity() {
    private var adapter: MessageAdapter? = null
    private val db: FirebaseFirestore = Firebase.firestore
    val messageList = ArrayList<Message>()

    private val mainButton by lazy { findViewById<Button>(R.id.goMain_button) }
    private val editSender by lazy { findViewById<EditText>(R.id.editSender) }
    private val editContent by lazy { findViewById<EditText>(R.id.editContent) }
    private val sendButton by lazy { findViewById<Button>(R.id.send_button) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerViewMessage) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        updateList()

        val receiver=intent.getStringExtra("receiver")
        if(receiver!=null){
            editSender.text= Editable.Factory.getInstance().newEditable(receiver)
        }
        mainButton.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
        sendButton.setOnClickListener{
            addItem()
            updateList()
        }

        db.collection("message")
            .whereEqualTo("receiver", Firebase.auth.currentUser?.email)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val message = Message(document as QueryDocumentSnapshot)
                    messageList.add(message)
                }

                // ItemAdapter 생성 및 RecyclerView에 설정
                adapter = MessageAdapter(messageList)
                recyclerView.adapter = adapter

                // 어댑터 갱신
                adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // 오류 처리
                Log.e("Firestore", "Error getting documents: ", exception)
            }
        adapter=MessageAdapter(messageList)
        recyclerView.setAdapter(adapter)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }


    //if(doc.data.contains(Firebase.auth.currentUser?.email)){
    private fun updateList() {
        db.collection("message")
            .whereEqualTo("receiver", Firebase.auth.currentUser?.email)
            .get()
            .addOnSuccessListener {
                val message = ArrayList<Message>()
                for (doc in it) {
                    message.add(Message(doc))
                }
                adapter?.updateList(message)
            }
    }

    private fun addItem() {
        val autoID=true
        val receiver=editSender.text.toString()
        val content=editContent.text.toString()
        val sender=Firebase.auth.currentUser?.email
        val itemMap = hashMapOf(
            "sender" to sender,
            "content" to content,
            "receiver" to receiver
        )
        if (autoID) {
            db.collection("message")
                .add(itemMap)
                .addOnSuccessListener { updateList() }.addOnFailureListener {  }
        }
        updateList()
    }

}