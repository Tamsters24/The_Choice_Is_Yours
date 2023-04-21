package com.example.thechoiceisyours

import android.content.Context

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.BufferedReader
import java.io.InputStreamReader

class ResetChaptersVisited {
    companion object {
        @JvmStatic
        fun resetNodesVisited(context: Context, vol: String) {
            val firebaseAuth = FirebaseAuth.getInstance()
            val database = FirebaseDatabase.getInstance()

            // Determine the user's UID
            val user = firebaseAuth.currentUser
            val userId = user?.uid.toString()

            // Get a reference to the "users" node
            val usersRef = database.getReference("users")
            val userRef = usersRef.child(userId)


            /** Update current chapter bookmark to for chapter 1 **
             *********  (designated 1a in content files) *********/
            val volBookmarkName = vol + "Bookmark"
            val volCurrentChapter = userRef.child(volBookmarkName)
            val volBookMarkValue = "1a"

            // Write the current chapter bookmark values to the database
            volCurrentChapter.setValue(volBookMarkValue).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Database write successful
                    println("Vol 1 Bookmark write successful")
                } else {
                    // Handle database write error
                    println("Vol 1 Bookmark write failed")
                }
            }

            /** Populate all nodes from the book's NodeNames.txt *****
             ** file as "false". This is used for the progress graph**/
            val volNodesVisitedName = vol + "NodesVisited"
            val volNodesVisited = userRef.child(volNodesVisitedName)

            // Create a map of the boolean values for vol1
            val assetManager = context.assets
            val assetsName = vol + "_files/" + vol + "NodeNames.txt"
            val volAssetNodes = assetManager.open(assetsName)
            val volNodesReader = BufferedReader(InputStreamReader(volAssetNodes))
            var volNode = volNodesReader.readLine()
            val volBooleanValues = mutableMapOf(volNode to true)  // Chapter 1
            volNode = volNodesReader.readLine()
            while (volNode != null) {  // All other chapters
                volBooleanValues[volNode] = false
                volNode = volNodesReader.readLine()
            }
            volNodesReader.close()

            // Write the volume boolean values to the database
            volNodesVisited.setValue(volBooleanValues).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Database write successful
                    println("Vol 1 Node Database write successful")
                } else {
                    // Handle database write error
                    println("Vol 1 Node Database write failed")
                }
            }
        }
    }
}