package com.example.messenger.registerlogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.R
import com.example.messenger.messages.ChatLogActivity
import com.example.messenger.messages.NewMessageActivity
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.cow_recyclerview_layout.view.*
import kotlinx.android.synthetic.main.cowpicturetry.*

var cowpicture=""
class picturetry : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cowpicturetry)
        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(picture("韓國牛","https://firebasestorage.googleapis.com/v0/b/cowpet-6187a.appspot.com/o/koreafishead.png?alt=media&token=ad375ac9-122e-41aa-b805-6f02582192b8"))
        adapter.add(picture("英蚊牛","https://firebasestorage.googleapis.com/v0/b/cowpet-6187a.appspot.com/o/englishead.png?alt=media&token=33b96add-3c6d-40e8-b186-cf7b31c1f3ae"))
        adapter.add(picture("肥宅牛","https://firebasestorage.googleapis.com/v0/b/cowpet-6187a.appspot.com/o/fathead.png?alt=media&token=162529b9-94eb-41c6-903d-e85fb704f079"))
        adapter.add(picture("聞折牛","https://firebasestorage.googleapis.com/v0/b/cowpet-6187a.appspot.com/o/wenjehead.png?alt=media&token=b8578d7d-2f65-4673-9d08-ef75328e480a"))
        adapter.add(picture("維尼牛","https://firebasestorage.googleapis.com/v0/b/cowpet-6187a.appspot.com/o/waynihead.png?alt=media&token=31e18d38-f2b3-441b-8c84-0643333dd925"))
        adapter.add(picture("空白牛","https://firebasestorage.googleapis.com/v0/b/cowpet-6187a.appspot.com/o/whitehead.png?alt=media&token=dff4f6a2-bd84-4404-be5b-a3ce668f2c78"))
        adapter.add(picture("氚浦牛","https://firebasestorage.googleapis.com/v0/b/cowpet-6187a.appspot.com/o/trumphead.png?alt=media&token=18482164-8dbb-43dc-924a-a46267260b1d"))
        adapter.add(picture("慣老闆牛","https://firebasestorage.googleapis.com/v0/b/cowpet-6187a.appspot.com/o/bosshead.png?alt=media&token=14892f64-d4ee-49ed-b5ba-adef67f1631e"))
        adapter.add(picture("小三牛","https://firebasestorage.googleapis.com/v0/b/cowpet-6187a.appspot.com/o/bitchead.png?alt=media&token=4f5cd987-f741-4df2-a2f6-fae5463220ff"))
        adapter.add(picture("台閩牛","https://firebasestorage.googleapis.com/v0/b/cowpet-6187a.appspot.com/o/taiminghead.png?alt=media&token=1815a098-c445-4502-bb10-8e08ffaa473e"))
        recyclerView2.layoutManager = LinearLayoutManager(this)
        recyclerView2.adapter = adapter


        adapter.setOnItemClickListener { item, view ->
            val pic = item as picture
//            val intent = Intent(view.context, RegisterActivity::class.java)
////          intent.putExtra(USER_KEY,  userItem.user.username)
            var bundle=Bundle()
            cowpicture = pic.cowurl
            Log.d("123", cowpicture)
            bundle.putString("url", cowpicture)
            val newintent=Intent()
            newintent.putExtra("key", bundle)
            setResult(10, newintent)
//            startActivity(intent)
            finish()
        }
    }
}
class picture(var cowname:String,var cowurl:String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.cowtextview.text = cowname
        Picasso.get().load(cowurl).into(viewHolder.itemView.cowimageview)
//        viewHolder.itemView.cowimageview = cowname
//        viewHolder.itemView.setOnClickListener {
//            cowpicture=cowurl
//            Log.d("url",cowurl)
//            Picasso.get().load(cowpicture).into()
////            val intent = Intent(context, RegisterActivity::class.java)
////            it.context?.startActivity(intent)
//        }

    }

    override fun getLayout(): Int {
        return R.layout.cow_recyclerview_layout
    }
}
