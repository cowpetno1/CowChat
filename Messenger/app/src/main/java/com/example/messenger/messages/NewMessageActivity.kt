package com.example.messenger.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.messenger.R
import com.example.messenger.models.User
import com.example.messenger.registerlogin.InLoggedUser
import com.squareup.picasso.Picasso
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection


data class FindUser(val userName : String, val userEmail : String, val userPassword : String)

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        recyclerview_newmessage.adapter =adapter


/*        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())

        recyclerview_newmessage.adapter = adapter*/

        fetchUsers()

    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(){
        GetUserThread.start()

        adapter.setOnItemClickListener { item, view ->

            val userItem = item as UserItem

            val intent = Intent(view.context, ChatLogActivity::class.java)
//          intent.putExtra(USER_KEY,  userItem.user.username)
            intent.putExtra(USER_KEY, userItem.user)
            startActivity(intent)

            finish()

        }
    }

    val adapter = GroupAdapter<ViewHolder>()


    val GetUserThread = object : Thread(){
        override fun run(){
            val client = KMongo.createClient("54.164.138.27:27017")
            val database = client.getDatabase("CowChat")
            val col = database.getCollection<User>("Users")



            val list : List<User> = col.find().toList()

            list.forEach {
                val user = it.userName
                if(it._id !=InLoggedUser.uid ){
                    this@NewMessageActivity.runOnUiThread(java.lang.Runnable{
                        adapter.add(UserItem(it))
                    })

                }
            }
            Log.d("TEsting",list.toString())
        }
    }

}

class UserItem(val user : User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.Username.text =user.userName
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}
