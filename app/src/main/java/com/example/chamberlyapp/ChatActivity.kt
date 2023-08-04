package com.example.chamberlyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamberlyapp.databinding.ActivityChatactivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import org.jivesoftware.smack.chat2.ChatManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity: AppCompatActivity() {
    lateinit var chatActivityBinding: ActivityChatactivityBinding
    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var database: FirebaseDatabase
    private lateinit var chatAdapter: ChatAdapter
    private val auth = FirebaseAuth.getInstance()
    private var messageReceiverHandler: Handler? = null
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var messageEventListener: ValueEventListener
    private lateinit var currentUserUid: String
    private lateinit var currentUser: FirebaseUser
    private lateinit var senderName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatActivityBinding = ActivityChatactivityBinding.inflate(layoutInflater)
        val view = chatActivityBinding.root
        setContentView(view)
        currentUser = FirebaseAuth.getInstance().currentUser!!

        if (currentUser == null) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
            return
        }
        else {
            senderName = currentUser.displayName ?: ""

            // Set currentUserUid to the UID of the current user
            currentUserUid = currentUser.uid
            // Initialize Firebase
            database = FirebaseDatabase.getInstance()
            databaseReference =
                database.reference.child("messages") // "messages" is the chat node in the database

            // Initialize RecyclerView and Adapter
            chatAdapter = ChatAdapter(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            chatActivityBinding.recyclerGchat.layoutManager = LinearLayoutManager(this)
            chatActivityBinding.recyclerGchat.adapter = chatAdapter

            // Load chat messages from Firebase
            loadMessages()


            chatActivityBinding.buttonGchatSend.setOnClickListener {
                val messageText = chatActivityBinding.editGchatMessage.text.toString().trim()
                if (messageText.isNotEmpty()) {
                    val newMessage = ChatMessage(
                        content = messageText,
                        messageType = "text",
                        senderName = senderName,
                        senderUid = currentUserUid

                    )
                    sendMessage(newMessage)
                    chatActivityBinding.editGchatMessage.text.clear()
                }
            }
        }
    }

        private fun loadMessages() {
            // Listen for changes in the messages node
            databaseReference.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(ChatMessage::class.java)
                    if (message != null) {
                        chatAdapter.addMessage(message)
                        // Scroll to the bottom of the RecyclerView to show the latest message
                        chatActivityBinding.recyclerGchat.scrollToPosition(chatAdapter.itemCount - 1)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // Handle changes in a message if needed
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    // Handle message removal if needed
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    // Handle message movement if needed
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database errors
                    Log.e("Firebase", "Error loading messages: ${error.message}")
                }
            })
        }


    private fun sendMessage(message: ChatMessage) {
        message.senderName = senderName
        databaseReference.push().setValue(message)
    }

    }




