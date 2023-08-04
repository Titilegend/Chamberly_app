package com.example.chamberlyapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.example.chamberlyapp.databinding.ActivityStartChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FieldValue

import com.google.firebase.firestore.FirebaseFirestore

class StartChatActivity : AppCompatActivity() {
    lateinit var startChatBinding: ActivityStartChatBinding

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    //private var chambersReference: DatabaseReference = database.child("ChatInfo")
   private val firestore = FirebaseFirestore.getInstance()
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startChatBinding = ActivityStartChatBinding.inflate(layoutInflater)
        val view = startChatBinding.root
        setContentView(view)
        context = this
        UserCacheManager.initialize(applicationContext)

        startChatBinding.createChamberButton1.setOnClickListener {

            if (validateEditText(startChatBinding.editChamberTitle)) {
                val chamberTitle = startChatBinding.editChamberTitle.text.toString().trim()

                createGroupChatIdsDocument(chamberTitle)

                 val intent = Intent(this@StartChatActivity, ChatActivity::class.java)
                startActivity(intent)






            }
        }


    }
    private fun validateEditText(editText: EditText): Boolean {
        val chamberTitle = editText.text.toString().trim()
        if (chamberTitle.isEmpty()) {
            editText.error = "Please enter a value"
            return false
        } else if (chamberTitle.length > 50) {
            editText.error = "Maximum character limit exceeded"
            editText.setText(chamberTitle.substring(0, 50))
            editText.setSelection(50) // Move cursor to the end of the text
            return false
        }
        return true
    }

    private fun createGroupChatIdsDocument(chamberTitle: String) {

        val currentUser = auth.currentUser
        val displayName = UserCacheManager.getInstance().getDisplayName()
        val uid = currentUser?.uid
        //val timestamp = FieldValue.serverTimestamp()


        if (displayName != null && uid != null) {
            val groupChatIdsCollection = firestore.collection("GroupChatIds")
            val groupChatId = groupChatIdsCollection.document()

            val chamberData = hashMapOf(
                "DocumentID" to groupChatId.id,
                "AuthorName" to displayName,
                "AuthorUID" to uid,
                "blockedUsers" to arrayListOf<String>(),
                "forPool" to false,
                "groupChatId" to groupChatId.id,
                "groupTitle" to chamberTitle, // Replace with the actual title entered by the user
                "isLocked" to false,
                "membersLimit" to 2,
                "publishedPool" to true,
                "timestamp" to FieldValue.serverTimestamp()
            )


            groupChatId
                .set(chamberData)
                .addOnSuccessListener {
                    val documentId = groupChatId.id

                    Log.d("Firestore", "GroupChatIds document created successfully")
                    Toast.makeText(this, "Chamber created successfully", Toast.LENGTH_SHORT).show()
                    createChamberDataInRealtimeDatabase(documentId,chamberTitle)


                }
                        .addOnFailureListener { e->
                            Toast.makeText(this,"Failed to create chamber", Toast.LENGTH_SHORT).show()
                            Log.e("Firestore","Error Updating GroupChatIds document", e)

                        }


                }

                }
    private fun createChamberDataInRealtimeDatabase(documentId: String,chamberTitle:String){
        val currentUser = FirebaseAuth.getInstance().currentUser



        currentUser?.let{user ->
            val hostUid = user.uid
            val hostDisplayName = UserCacheManager.getInstance().getDisplayName()
            val timestamp = ServerValue.TIMESTAMP
            val initialMessages = hashMapOf(hostUid to "")

            val chatInfo = mapOf(hostUid to hostDisplayName).let { UsersData(members = it as Map<String, String>) }
                .let {
                    ChatInfo(
                        host = hostUid,
                        messages = initialMessages,
                        timestamp = timestamp,
                        title = chamberTitle,
                        users = it

                    )
                }
            val newChambersRef = database.child(documentId)
            //val chamberKey = newChambersRef.key

                newChambersRef.setValue(chatInfo)
                    .addOnSuccessListener {
                        //val info = ChatInfo(hostUid, messages, timestamp, title, users )
                        Log.d("realtime", "database created successfully")
                        Toast.makeText(context," chamber created in  database", Toast.LENGTH_SHORT).show()


                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context," error in  database", Toast.LENGTH_SHORT).show()
                        Log.e("realtime","Error Updating GroupChatIds document", e)
                    }


        }
    }

        }


