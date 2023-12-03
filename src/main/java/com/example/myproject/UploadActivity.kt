package com.example.myproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UploadActivity : AppCompatActivity() {
    private var adapter: ItemAdapter? = null
    private val db: FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("item")
    private var snapshotListener: ListenerRegistration? = null

    private val editItemName by lazy { findViewById<EditText>(R.id.editItemName)}
    private val editPrice by lazy {findViewById<EditText>(R.id.editPrice)}
    private val editTitle by lazy {findViewById<EditText>(R.id.editTitle)}
    private val editDetail by lazy {findViewById<EditText>(R.id.editDetail)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        if (Firebase.auth.currentUser == null) {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
        }
        findViewById<Button>(R.id.button_goMain)?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<Button>(R.id.buttonAddUpdate)?.setOnClickListener {
            addItem()
        }
    }
    override fun onStart() {
        super.onStart()
        // snapshot listener for all items

        snapshotListener = itemsCollectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // 처리할 오류가 있는 경우 이곳에서 오류 처리를 수행할 수 있습니다.
                Log.e(TAG, "Error getting snapshot: $error")
                return@addSnapshotListener
            }
        }
    }

    override fun onStop() {
        super.onStop()
        snapshotListener?.remove()
    }

    private fun updateList() {
        itemsCollectionRef.get().addOnSuccessListener {
            val items = ArrayList<Item>()
            for (doc in it) {
                items.add(Item(doc))
            }
            adapter?.updateList(items)
        }
    }

    private fun addItem() {
        val name = editItemName.text.toString()
        if (name.isEmpty()) {
            Snackbar.make(editItemName, "Input name!", Snackbar.LENGTH_SHORT).show()
            return
        }
        val price = editPrice.text.toString().toInt()
        val title=editTitle.text.toString()
        val detail=editDetail.text.toString()
        val seller=Firebase.auth.currentUser?.email

        val itemMap = hashMapOf(
            "name" to name,
            "price" to price,
            "detail" to detail,
            "title" to title,
            "seller" to seller,
            "soldout" to "false"
        )

        itemsCollectionRef.add(itemMap)
            .addOnSuccessListener {
                updateList()
                startActivity(Intent(this, MainActivity::class.java))
            }.addOnFailureListener {

            }

    }

    companion object {
        const val TAG = "FirestoreActivity"
    }

}