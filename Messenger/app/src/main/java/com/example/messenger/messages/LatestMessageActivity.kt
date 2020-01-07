package com.example.messenger.messages

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.registerlogin.InLoggedUser
import com.example.messenger.R
import com.example.messenger.registerlogin.RegisterActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_message.*

class LatestMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)
        setupDummyRows()

        verifyUserLoggedIn()
    }

    class LatestMessageRow:Item<ViewHolder>(){
        override fun  bind(viewHolder: ViewHolder,position:Int){

        }

        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }
    }
    private fun setupDummyRows(){

        val adapter=GroupAdapter<ViewHolder>()

        adapter.add((LatestMessageRow()))
        adapter.add((LatestMessageRow()))
        adapter.add((LatestMessageRow()))

        recyclerview_latest_messages.layoutManager = LinearLayoutManager(this)
        recyclerview_latest_messages.adapter=adapter
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
