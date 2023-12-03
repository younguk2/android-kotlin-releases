package com.example.myproject

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReviseItemActivity: AppCompatActivity() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("item")
    private var reviseItem: Item? =null;

    private val editItemName by lazy { findViewById<EditText>(R.id.editItemName)}
    private val editPrice by lazy {findViewById<EditText>(R.id.editPrice)}
    private val editTitle by lazy {findViewById<EditText>(R.id.editTitle)}
    private val editDetail by lazy {findViewById<EditText>(R.id.editDetail)}
    private val switchSoldout by lazy {findViewById<Switch>(R.id.soldout)}
    var soldout="false"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revise)

        val receivedItemID=intent.getStringExtra("id")
        findViewById<Button>(R.id.button_goMain)?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        findViewById<Button>(R.id.buttonAddUpdate)?.setOnClickListener {
            addItem(receivedItemID)
            startActivity(Intent(this, MainActivity::class.java))
        }
        findViewById<Button>(R.id.delete)?.setOnClickListener {
            deleteItem(receivedItemID)
            startActivity(Intent(this, MainActivity::class.java))
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

    private fun addItem(receivedItemID:String?) {

        val name = editItemName.text.toString()
        if (name.isEmpty()) {
            Snackbar.make(editItemName, "Input name!", Snackbar.LENGTH_SHORT).show()
            return
        }
        val price = editPrice.text.toString().toInt()
        val title=editTitle.text.toString()
        val detail=editDetail.text.toString()
        val seller=Firebase.auth.currentUser?.email
        soldout="false"

        if (switchSoldout.isChecked) {
            soldout="true"
        } else {
            soldout="false"
        }

        val itemMap = hashMapOf(
            "name" to name,
            "price" to price,
            "detail" to detail,
            "title" to title,
            "seller" to seller,
            "soldout" to soldout
        )
        if (receivedItemID != null) {
            db.collection("item")
                .document(receivedItemID)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firestore", "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error deleting document", e)
                }
        }

        itemsCollectionRef.add(itemMap)
            .addOnSuccessListener {  }.addOnFailureListener {  }
    }

    private fun deleteItem(receivedItemID:String?) {
        if (receivedItemID != null) {
            db.collection("item")
                .document(receivedItemID)
                .delete()
                .addOnSuccessListener {
                    // 삭제 성공 시 동작할 코드
                    Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                }
        }
    }

}