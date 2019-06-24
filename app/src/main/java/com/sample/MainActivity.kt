package com.sample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun makeSampleMap(ind: Int): Map<String, String> {
        return mapOf(
            "keyA" to "Lorem ipsum dolor sit amet, consectetur adipiscing elit $ind 1",
            "keyB" to "Lorem ipsum dolor sit amet, consectetur adipiscing elit $ind 2",
            "keyC" to "Lorem ipsum dolor sit amet, consectetur adipiscing elit $ind 3"
        )
    }

    fun addData() {
        val db = FirebaseFirestore.getInstance()

        val r = Random()

        // Too heavy for the main thread, but that's ok for a sample app
        val batch = db.batch()
        for (i in 1..500) {

            val doc = mutableMapOf<String, Any>(
                "f1" to "String1",
                "f2" to "String2",
                "f3" to r.nextDouble(),
                "f4" to r.nextInt(10)
            )
            for (k in 1..5) {
                doc["ff$k"] = mapOf(
                    "kA" to makeSampleMap(k),
                    "kB" to makeSampleMap(k),
                    "kC" to makeSampleMap(k)
                )
            }
            for (k in 1..5) {
                doc["aa$k"] = listOf(
                    makeSampleMap(k),
                    makeSampleMap(k),
                    makeSampleMap(k)
                )
            }


            val docRef = db.collection("sample").document(UUID.randomUUID().toString())
            batch.set(docRef, doc)
        }
        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(this, "Data uploaded", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Data upload failed: $it", Toast.LENGTH_SHORT).show()
            }

    }

    fun queryData(limit: Long) {
        val db = FirebaseFirestore.getInstance()

//        val settings = FirebaseFirestoreSettings.Builder()
//            .setPersistenceEnabled(false)
//            .build()
//        db.firestoreSettings = settings

        val startTime = System.currentTimeMillis()
        db.collection("sample")
            .whereEqualTo("f4", 2)
            .limit(limit)
            .get()
            .addOnSuccessListener {
                val n = it.documents.size
                val dt = System.currentTimeMillis() - startTime
                Toast.makeText(this, "Data downloaded, $n items, time: $dt ms", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Data download failed", Toast.LENGTH_SHORT).show()
            }
    }

    fun onPopulateClick(view: View) {
        addData()
    }

    fun onQueryClick(view: View) {
        queryData(10000)
    }

    fun onQueryClick1(view: View) {
        queryData(1)
    }

}
