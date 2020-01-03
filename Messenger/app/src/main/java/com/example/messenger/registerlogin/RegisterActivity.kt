package com.example.messenger.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import androidx.appcompat.app.AppCompatActivity
import com.example.messenger.R
import com.example.messenger.messages.LatestMessageActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.litote.kmongo.*
import java.util.*

data class Jedi(val userName : String, val userEmail : String, val userPassword : String, val userImageUrl : String)//insert Email , Passwd and ImageUrl

class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_text_view.setOnClickListener {
            Log.d(TAG,"Try to show login activity")
            //launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        selectphoto_button_register.setOnClickListener {
            Log.d(TAG,"Try to show photo selector ")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    var selectedPhotoUri: Uri? = null
    var name : String = ""
    var email: String = ""
    var password : String = ""
    var profileImageUrl : String = ""


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            // proceed  and check what the selected image was...
            Log.d(TAG,"Photo was  selected")

            selectedPhotoUri = data.data


            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f
        }
    }

    private fun performRegister(){
        name = username_edittext_register.text.toString()
        email = email_edittext_register.text.toString()
        password = password_edittext_register.text.toString()
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter text in email/pwd.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG,"Attempting to create user with email : $email")

        uploadImageToFirebaseStorage()

    }

    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")
                    profileImageUrl = it.toString()
                    saveUserToMongoDB()
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveUserToMongoDB(){

        RegisterThread.start()

        /*var background = object : Thread(){
            override fun run() {
                val client = KMongo.createClient("54.164.138.27:27017")
                val database = client.getDatabase("CowChat")
                val col = database.getCollection<Jedi>("Users")

                col.insertOne(Jedi(email, password, profileImageUrl))



                val userUri: Jedi? = col.findOne(Jedi::UserEmail eq email)
                if(userUri != null){
                    Log.d(TAG, "Finally we saved the user to Firebase Database")
                }else{
                    Log.d(TAG, "Failed to set value to database")
                    return
                }
            }
        }.start()*/

        val intent = Intent(this, LatestMessageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }

    var RegisterThread = object : Thread(){
        override fun run() {
            val client = KMongo.createClient("54.164.138.27:27017")
            val database = client.getDatabase("CowChat")
            val col = database.getCollection<Jedi>("Users")

            col.insertOne(
                Jedi(
                    name,
                    email,
                    password,
                    profileImageUrl
                )
            )}
    }





}



