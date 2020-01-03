package com.example.messenger.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.messenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection


data class FindUser(val userName : String, val userEmail : String, val userPassword : String)

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

/*        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())

        recyclerview_newmessage.adapter = adapter*/

        fetchUsers()

    }

    private fun fetchUsers(){
        GetUserThread.start()



    }




    val GetUserThread = object : Thread(){
        override fun run(){
            val client = KMongo.createClient("54.164.138.27:27017")
            val database = client.getDatabase("CowChat")
            val col = database.getCollection<FindUser>("Users")

            val adapter = GroupAdapter<ViewHolder>()
            val list : List<FindUser> = col.find().toList()

            list.forEach {
                val user = it.userName
                if(user != null){
                    adapter.add(UserItem(user))
                    this@NewMessageActivity.runOnUiThread(java.lang.Runnable{
                        recyclerview_newmessage.adapter =adapter
                        adapter.setOnItemClickListener{item, view ->

                            val intent : Intent(this, )
                            startActivity()

                        }
                    })
                }
            }
            Log.d("TEsting",list.toString())
        }
    }

}



class UserItem(val nameofUser : String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.Username.text =nameofUser

    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}