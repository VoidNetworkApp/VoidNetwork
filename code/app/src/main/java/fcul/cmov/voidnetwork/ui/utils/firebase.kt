package fcul.cmov.voidnetwork.ui.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

fun getCurrentUser(): FirebaseUser? = FirebaseAuth.getInstance().currentUser

fun FirebaseDatabase.getMessages() = reference.child("messages")
fun FirebaseDatabase.getLanguages() = reference.child("languages")
fun FirebaseDatabase.getPortals() = reference.child("portals")