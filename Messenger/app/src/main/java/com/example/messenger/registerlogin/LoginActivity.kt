package com.example.messenger.registerlogin

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messenger.R
import com.example.messenger.messages.FindUser
import com.example.messenger.messages.LatestMessageActivity
import com.example.messenger.models.User
import kotlinx.android.synthetic.main.activity_login.*
import org.bson.types.ObjectId
import org.litote.kmongo.*

data class LoggedinUser(val _id : String?, val userEmail : String?, val userPassword : String?)

object InLoggedUser{
    @JvmStatic var usersEmail : String = ""
    @JvmStatic lateinit var uid : String
    @JvmStatic  var subscriberkey = 0
    @JvmStatic  var publisherkey = 0

}

class LoginActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            performLogin()
        }

        back_to_register_textview.setOnClickListener {
            finish()
        }


    }

    var email : String = ""
    var password : String = ""

    private fun performLogin() {
        email = email_edittext_login.text.toString()
        password = password_edittext_login.text.toString()


        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill out email/pwd.", Toast.LENGTH_SHORT).show()
            return
        }

        LogInThread.start()
    }

    var LogInThread = object : Thread(){
        override fun run(){
            val client = KMongo.createClient("54.164.138.27:27017")
            val database = client.getDatabase("CowChat")
            val col = database.getCollection<User>("Users")

            var yoda : User? = col.findOne("{userEmail:'$email'}")

            Log.d("view",yoda.toString())


            if(yoda?.userPassword == password){//Account Exists
                Log.d("Login", "Successfully logged in")
                InLoggedUser.usersEmail = email
                InLoggedUser.uid = yoda._id as String
                InLoggedUser.subscriberkey = yoda.subscriberkey
                InLoggedUser.publisherkey = yoda.publisherkey

                afterLoggedinSuccess()

            }else{
                makeToasts()
            }
        }
    }

    private fun afterLoggedinSuccess(){
        val intent = Intent(this, LatestMessageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        return
    }

    private fun makeToasts(){
        Looper.prepare();
        Toast.makeText(this, "Failed to log in:", Toast.LENGTH_SHORT).show()
        Looper.loop()
        return
    }

}