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
import androidx.core.net.toUri
import com.example.messenger.R
import com.example.messenger.messages.LatestMessageActivity
import com.example.messenger.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.ObjectIdGenerator
import java.util.*

var urlchoose=""
class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var selectedPhotoUri: Uri? = null

//        if(cowpicture != ""){
//            selectedPhotoUri= cowpicture.toUri()
//            Log.d("ad",selectedPhotoUri.toString())
////            selectphoto_imageview_register.setImageURI(selectedPhotoUri)
//            Picasso.get().load(cowpicture).into(selectphoto_imageview_register)
//        }


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
            val intent = Intent(this, picturetry::class.java)
//            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    var selectedPhotoUri: Uri? = null
    var uname : String = ""
    var email: String = ""
    var password : String = ""
    var profileImageUrl : String = ""


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        selectedPhotoUri = cowpicture.toUri()
        if (requestCode == 0 && resultCode == 10 && data != null){
            // proceed  and check what the selected image was...
            Log.d(TAG,"Photo was  selected")

            urlchoose= data?.getBundleExtra("key")?.getString("url").toString()

            Log.d("result",urlchoose)
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, url?.toUri())
//
//            selectphoto_imageview_register.setImageBitmap(bitmap)
//
//            selectphoto_button_register.alpha = 0f
            Picasso.get().load(urlchoose).into(selectphoto_imageview_register)
        }
    }

    private fun performRegister(){
        uname = username_edittext_register.text.toString()
        email = email_edittext_register.text.toString()
        password = password_edittext_register.text.toString()
        Log.d("name",uname)
        if(email.isEmpty() || password.isEmpty() || urlchoose==""){
            Toast.makeText(this, "Please enter text in email/pwd.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG,"Attempting to create user with email : $email")
        saveUserToMongoDB()
//        uploadImageToFirebaseStorage()

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
            val col = database.getCollection<User>("Users")

            val user_id = ObjectIdGenerator.newObjectId<ObjectId>().toString()
            Log.d("realname",uname)
            col.insertOne(
                User(
                    user_id,
                    uname,
                    email,
                    password,
                    555,
                    666,
                    urlchoose)
            )}
    }





}




