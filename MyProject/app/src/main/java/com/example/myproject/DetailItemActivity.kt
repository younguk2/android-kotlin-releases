package com.example.myproject

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DetailItemActivity: AppCompatActivity() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("item")
    private var reviseItem: Item? =null;

    private val editItemName by lazy { findViewById<TextView>(R.id.editItemName)}
    private val editPrice by lazy {findViewById<TextView>(R.id.editPrice)}
    private val editTitle by lazy {findViewById<TextView>(R.id.editTitle)}
    private val editDetail by lazy {findViewById<TextView>(R.id.editDetail)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_item)

        val receivedItemID=intent.getStringExtra("id")
        val receivedSeller=intent.getStringExtra("seller")

        findViewById<Button>(R.id.button_goMain)?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<Button>(R.id.message)?.setOnClickListener {
            val intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("receiver",receivedSeller)
            startActivity(intent)
        }


        if(receivedItemID!=null){
            db.collection("item")
                .document(receivedItemID)
                .get()
                .addOnSuccessListener { result ->
                    val item=result.toObject(Item::class.java)
                    reviseItem = item
                    editItemName.text= Editable.Factory.getInstance().newEditable(reviseItem!!.name)
                    editPrice.text= Editable.Factory.getInstance().newEditable(reviseItem!!.price.toString())
                    editTitle.text= Editable.Factory.getInstance().newEditable(reviseItem!!.title)
                    editDetail.text= Editable.Factory.getInstance().newEditable(reviseItem!!.detail)
                }
                .addOnFailureListener { exception ->
                    // 오류 처리
                    Log.e("Firestore", "Error getting documents: ", exception)
                }
        }

    }
}