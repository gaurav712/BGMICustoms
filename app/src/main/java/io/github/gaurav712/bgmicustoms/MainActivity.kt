package io.github.gaurav712.bgmicustoms

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Secure
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest
import kotlin.experimental.and

const val EXTRA_MESSAGE = "io.github.gaurav712.bgmicustoms.MESSAGE"

class MainActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var nextEventNotifierTextView: TextView
    private lateinit var newsTextView: TextView
    private lateinit var previousWinnerTextView: TextView
    private lateinit var registrationLayout: LinearLayoutCompat
    private lateinit var registrationInfoTextView: TextView
    private lateinit var credentialsLayout: LinearLayoutCompat
    private lateinit var roomIdTextView: TextView
    private lateinit var roomPasswordTextView: TextView
    private lateinit var registerButton: Button

    /* To get team data */
    private lateinit var playerOneEditText: EditText
    private lateinit var playerTwoEditText: EditText
    private lateinit var playerThreeEditText: EditText
    private lateinit var playerFourEditText: EditText

    /* To store slot info */
    private lateinit var deviceId: String
    private lateinit var currentSlot: String

    /* Event info */
    private var eventAvailable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseReference = Firebase.database.reference
        nextEventNotifierTextView = findViewById(R.id.nextEventNotifierTextView)
        newsTextView = findViewById(R.id.newsTextView)
        previousWinnerTextView = findViewById(R.id.previousWinnerTextView)
        registrationLayout = findViewById(R.id.registrationLayout)
        registrationInfoTextView = findViewById(R.id.registrationInfoTextView)
        credentialsLayout = findViewById(R.id.credentialsLayout)
        roomIdTextView = findViewById(R.id.roomIdTextView)
        roomPasswordTextView = findViewById(R.id.roomPasswordTextView)
        registerButton = findViewById(R.id.registerButton)

        playerOneEditText = findViewById(R.id.playerOneEditText)
        playerTwoEditText = findViewById(R.id.playerTwoEditText)
        playerThreeEditText = findViewById(R.id.playerThreeEditText)
        playerFourEditText = findViewById(R.id.playerFourEditText)

        databaseReference.child("next_event").get().addOnSuccessListener {
            if (it.value.toString() == "none") {
                nextEventNotifierTextView.text = getString(R.string.no_events_message)
                eventAvailable = false
            } else {
                nextEventNotifierTextView.text = it.value.toString()
            }
        }.addOnFailureListener {
            nextEventNotifierTextView.text = it.message
        }

        databaseReference.child("latest_news").get().addOnSuccessListener {
            val text =  it.value.toString().replace("_n", "\n\n")
            newsTextView.text = text
        }.addOnFailureListener {
            newsTextView.text = it.message
        }

        databaseReference.child("previous_winners").get().addOnSuccessListener {
            val text =  it.value.toString().replace("_n", "\n\n")
            previousWinnerTextView.text = text
        }.addOnFailureListener {
            previousWinnerTextView.text = it.message
        }

        /* Get current slot */
        databaseReference.child("current_slot").get().addOnSuccessListener {
            currentSlot = it.value.toString()
        }

        /* Check if user is already registered */
        deviceId = sha512(Secure.getString(contentResolver, Secure.ANDROID_ID))

        databaseReference.child("lineup").child(deviceId).get().addOnSuccessListener { dataSnapshot ->

            if (dataSnapshot.value != null) {
                Log.d("registrationStatus", "Slot already allotted")

                /* Get the allotted slot number */
                databaseReference.child("lineup").child(deviceId).child("slot").get()
                    .addOnSuccessListener {
                        updateCurrentSlotMessage(it.value.toString())
                    }

                databaseReference.child("credentials").get().addOnSuccessListener {

                    if (it.value.toString() != "none") {
                        
                        val credentials = it.value.toString()

                        Log.d("roomCredentials", credentials)

                        /* Fetch the credentials and show up in the credentialsLayout */
                        credentialsLayout.visibility = View.VISIBLE
                        roomIdTextView.text = credentials.split('|')[0]
                        roomPasswordTextView.text = credentials.split('|')[1]
                    }
                }
            } else {

                Log.d("registrationStatus", "Team is not registered")

                /* Get the next slot */
                if (currentSlot == "25") {
                    Log.d("registrationStatus", "Slots are full")
                    registrationInfoTextView.text = getString(R.string.slot_full_text)
                } else if(eventAvailable) {
                    registerButton.visibility = View.VISIBLE
                }
            }
        }
    }

    fun registerForEvent(view: View) {
        view.visibility = View.GONE
        registrationLayout.visibility = View.VISIBLE
    }

    fun addTeamToLineup(view: View) {

        /* Upload the team data to cloud */
        val team = Team(
            playerOneEditText.text.toString(),
            playerTwoEditText.text.toString(),
            playerThreeEditText.text.toString(),
            playerFourEditText.text.toString())

        Log.d("dataToWrite", deviceId + team.playerOne + team.playerTwo + team.playerThree + team.playerFour + currentSlot)

        databaseReference.child("lineup").child(deviceId).setValue(team)
        currentSlot = (currentSlot.toInt() + 1).toString()
        databaseReference.child("lineup").child(deviceId).child("slot").setValue(currentSlot)
        updateCurrentSlotMessage()

        /* Now update the currentSlot value */
        databaseReference.child("current_slot").setValue(currentSlot)

        /* Now make the layout disappear */
        registrationLayout.visibility = View.GONE
    }

    private fun updateCurrentSlotMessage(allottedSlot: String = currentSlot) {
        registrationInfoTextView.text = String.format(
            getString(R.string.allotted_slot_text),
            allottedSlot
        )
    }

    /* To get SHA512 Digest */
    @Throws(Exception::class)
    private fun sha512(s: String): String {
        val md: MessageDigest = MessageDigest.getInstance("SHA-512")
        md.update(s.toByteArray())
        val bytes: ByteArray = md.digest()
        val buffer = StringBuffer()
        for (i in bytes.indices) {
            val tmp: String = ((bytes[i] and 0xff.toByte()) + 0x100).toString(16).substring(1)
            buffer.append(tmp)
        }
        return buffer.toString()
    }

    private class Team(val playerOne: String, val playerTwo: String, val playerThree: String, val playerFour: String)

    fun showCurrentLineup(view: View) {

        Log.d("lineupActivity", "launching lineup activity")

        /* Get the lineup and launch the LineupActivity */
        databaseReference.child("lineup").get().addOnSuccessListener {
            val intent = Intent(this, LineupActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, it.value.toString())
            }

            startActivity(intent)
        }
    }
}