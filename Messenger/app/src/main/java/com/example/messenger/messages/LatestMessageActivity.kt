package com.example.messenger.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.registerlogin.InLoggedUser
import com.example.messenger.R
import com.example.messenger.models.Chatmessage
import com.example.messenger.models.User
import com.example.messenger.registerlogin.RegisterActivity
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_message.*
import kotlinx.android.synthetic.main.cow_recyclerview_layout.view.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

class LatestMessageActivity : AppCompatActivity() {
    var username=""
    var userid=""
    var picurl=""
    lateinit var useritem:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)
//        setupDummyRows()
        recyclerview_latest_messages.layoutManager = LinearLayoutManager(this)

        recyclerview_latest_messages.adapter = adapter
        setupDummyRows()
        verifyUserLoggedIn()
        Log.d("id", InLoggedUser.uid)

        adapter.setOnItemClickListener { item, view ->

            val userItem = item as LatestMessageRow

            val intent = Intent(view.context, ChatLogActivity::class.java)
//          intent.putExtra(USER_KEY,  userItem.user.username)
            intent.putExtra(NewMessageActivity.USER_KEY, userItem.user)
            startActivity(intent)
        }

    }
    class LatestMessageRow(var user:User,var testupdate:String,var usernameshow:String,var url:String):Item<ViewHolder>(){
        override fun  bind(viewHolder: ViewHolder,position:Int){
            viewHolder.itemView.message_textview_latest_message.text=testupdate
            viewHolder.itemView.username_textview_latest_message.text=usernameshow
            Picasso.get().load(url).into(viewHolder.itemView.imageview_latest_message)
        }

        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }
    }

    val adapter=GroupAdapter<ViewHolder>()
    private fun setupDummyRows(){
        val updatechat = object : Thread() {
            override fun run() {
                val client = KMongo.createClient("54.164.138.27:27017")
                val database = client.getDatabase("CowChat")
                val col = database.getCollection<Chatmessage>("latest-messages")

                val message_list_update: List<Chatmessage> = col.find().toList()

                val col2 = database.getCollection<User>("Users")

                val user: List<User> = col2.find().toList()

                message_list_update.forEach {
//                    if (it.fromId == InLoggedUser.uid){
//                        this@LatestMessageActivity.runOnUiThread {
//                            adapter.add(ChatToItem(it.text))
//                            adapter.add((LatestMessageRow()))
//                        }
//                    }else{
                    if (it._id.startsWith(InLoggedUser.uid,0) == true){
                        this@LatestMessageActivity.runOnUiThread {
                            userid=it.fromId
                            user.forEach {
                                if (userid==it._id){
                                    username=it.userName
                                    picurl=it.profileImageUrl
                                    useritem=it
                                }
                            }
                            adapter.add((LatestMessageRow(useritem,it.text,username,picurl)))
                        }
                    }
                }



            }
        }
        updatechat.start()
    }


    private fun verifyUserLoggedIn(){
        if(InLoggedUser.usersEmail == ""){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_new_message ->{
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out ->{
                InLoggedUser.usersEmail = ""
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}
