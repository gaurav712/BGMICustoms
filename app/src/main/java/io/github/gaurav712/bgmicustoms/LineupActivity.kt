package io.github.gaurav712.bgmicustoms

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

val lineup: Array<JSONObject?> = arrayOfNulls(24)
lateinit var jsonObject: JSONObject
lateinit var jsonArray: JSONArray

class LineupActivity : AppCompatActivity() {

    private lateinit var recyclerViewAdapter: LineupRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lineup)

        recyclerView = findViewById(R.id.lineupRecyclerView)
        recyclerViewAdapter = LineupRecyclerViewAdapter(this)

        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseReference.child("lineup").get().addOnSuccessListener {

            if (it.value == null) {
                Toast.makeText(this, "No lineup available!", Toast.LENGTH_LONG).show()
                finish()
            } else {

                /* Parse the data */
                jsonObject = JSONObject(it.value.toString())

                jsonArray = jsonObject.names()!!

                for (num in 0 until jsonObject.length()) {

                    /* Get slot number to calculate index */
                    val currentObject = jsonObject.getJSONObject(jsonArray[num].toString())
                    val index = currentObject.getString("slot").toInt() - 2

                    lineup[index] = currentObject
                    Log.d("name", "$currentObject.toString()$index")
                }

                /* Now load it all up in the RecyclerView */
                recyclerView.adapter = recyclerViewAdapter

                Log.d("lineup", it.value.toString())
            }
        }
    }
}