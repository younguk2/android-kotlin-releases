package com.example.myproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


import com.google.firebase.firestore.*

class MainActivity : AppCompatActivity() , ItemAdapter.OnItemClickListener {
    private val db: FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("items")
    private var snapshotListener: ListenerRegistration? = null
    private var adapter: ItemAdapter? = null
    val itemList = ArrayList<Item>()

    //private val recyclerViewItems by lazy { findViewById<RecyclerView>(R.id.recyclerViewItems) }
    //private val textSnapshotListener by lazy { findViewById<TextView>(R.id.textSnapshotListener) }
    private val messageButton by lazy{findViewById<TextView>(R.id.button_message)}
    private val recyclerView by lazy{findViewById<RecyclerView>(R.id.recyclerViewItems)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Firebase.auth.currentUser == null) {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
        }
        val email=Firebase.auth.currentUser?.email?: "No User"
        findViewById<TextView>(R.id.textUID)?.text = email+"님 환영합니다"
        findViewById<Button>(R.id.button_signout)?.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<Button>(R.id.button_upload)?.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
        messageButton.setOnClickListener{
            startActivity(Intent(this,MessageActivity::class.java))
        }
        findViewById<Button>(R.id.buttonQuery)?.setOnClickListener {
            queryWhere()
        }
        db.collection("item")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val item = Item(document as QueryDocumentSnapshot)
                    itemList.add(item)
                }

                // ItemAdapter 생성 및 RecyclerView에 설정
                adapter = ItemAdapter(itemList,this)
                recyclerView.adapter = adapter

                // 어댑터 갱신
                adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // 오류 처리
                Log.e("Firestore", "Error getting documents: ", exception)
            }
        adapter=ItemAdapter(itemList,this)
        recyclerView.setAdapter(adapter)
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)

        updateList()

    }
    //여기서부터 아이템 수정하는 거 하자
    override fun onItemClick(item: Item) {
        if(item.seller==Firebase.auth.currentUser?.email){

            Toast.makeText(this, "아이템 클릭됨: ${item.title}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ReviseItemActivity::class.java)
            intent.putExtra("id",item.id)
            startActivity(intent)

        }else{
            Toast.makeText(this, "아이템 클릭됨: ${item.title}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DetailItemActivity::class.java)
            intent.putExtra("id",item.id)
            intent.putExtra("seller",item.seller)
            startActivity(intent)
        }
    }

    private fun updateList() {
        itemsCollectionRef.get().addOnSuccessListener {
            val items = mutableListOf<Item>()
            for (doc in it) {
                items.add(Item(doc))
            }
        }
    }
    private fun queryWhere() {
        db.collection("item")
            .whereEqualTo("soldout", "false")
            .get()
            .addOnSuccessListener { result ->
                itemList.clear() // 기존 데이터를 지우고 새로운 데이터로 갱신
                for (document in result) {
                    val item = document.toObject(Item::class.java)
                    itemList.add(item)
                }
                adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리
            }
    }
    companion object {
        const val TAG = "FirestoreActivity"
    }
}
