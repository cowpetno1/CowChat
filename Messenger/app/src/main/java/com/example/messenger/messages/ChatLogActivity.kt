package com.example.messenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.messenger.JAVAmodels.Messagelistener_redis
import com.example.messenger.R
import com.example.messenger.models.Chatmessage
import com.example.messenger.models.User
import com.example.messenger.registerlogin.InLoggedUser
import com.example.messenger.registerlogin.LoggedinUser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mongodb.client.MongoCursor
import com.mongodb.client.model.changestream.FullDocument
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_from_row.view.textView
import kotlinx.android.synthetic.main.chat_to_row.view.*
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.ObjectIdGenerator
import org.litote.kmongo.id.WrappedObjectId
import redis.clients.jedis.Jedis
import java.sql.Timestamp
import java.util.*
import kotlin.collections.toList
import kotlin.properties.Delegates

var urlto=""
var urlfrom=""
class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }


    lateinit var Touser: User
    val FromUser = InLoggedUser.uid

    val adapter = GroupAdapter<ViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chatlog.adapter = adapter

        Touser = intent.getParcelableExtra<User>("USER_KEY")
        supportActionBar?.title = Touser.userName

        Log.d("urlto", InLoggedUser.uid)
        Log.d("urlfrom", Touser._id)
        Log.d("user", "")


        setupDummyData()

        //listen message
        listenMessage()

        //send message
        send_button_chat_log.setOnClickListener {

            Log.d(TAG, "Attempt to send message")

            performSendMessage()
        }
    }

    private fun listenMessage() {
        //message change listener

        class redis_subscriber_runnable : Runnable {

            override fun run() {
                val jedis = Jedis("3.231.90.126", 6379)
                jedis.connect()
                jedis.auth("admin")

                Log.d("messagelistener_redis", "this is subscriber")

                while (true) {
                    val brpop = jedis.brpop(0, InLoggedUser.subscriberkey.toString())

                    if (brpop != null) {
                        for (text in brpop) {
                            if (text != "message_queue") {
                                this@ChatLogActivity.runOnUiThread {
                                    adapter.add(ChatFromItem(text))
                                }
                                Log.d("messagelistener_redis", "im subscriber $text")
                            }
                        }
                        brpop.clear()
                    }
                }

            }
        }

        val redis_subscriber_thread = Thread(redis_subscriber_runnable())
        redis_subscriber_thread.start()

    }


    private fun performSendMessage() {
        //how do we actually send a message to mongodb ...
        //and use redis to notify data change

        val text = editText.text.toString()

        val message_id = ObjectIdGenerator.newObjectId<ObjectId>().toString()

        val fromId = InLoggedUser.uid
        val Toid = Touser._id

        val chatmessage =
            Chatmessage(message_id, text, fromId, Toid, System.currentTimeMillis() / 1000)
        val updatemessage =
            Chatmessage(Toid + fromId, text, fromId, Toid, System.currentTimeMillis() / 1000)

        var messageThread = object : Thread() {
            override fun run() {
                val client = KMongo.createClient("54.164.138.27:27017")
                val database = client.getDatabase("CowChat")
                val col = database.getCollection<Chatmessage>("Messages")

                col.insertOne(chatmessage)
//billkuo1234@gmail.com
                val col2 = database.getCollection<Chatmessage>("latest-messages")

                val yoda: Chatmessage? = col2.findOne(Chatmessage::_id eq Toid + fromId)


                if (yoda != null) {
                    col2.updateOneById(Toid + fromId, updatemessage)
                } else {
                    col2.insertOne(updatemessage)
                }


            }
        }

        //record message in mongo db
        messageThread.start()

        //notify message change
        class redis_publisher_runnable : Runnable {

            override fun run() {
                val jedis = Jedis("3.231.90.126", 6379)
                jedis.connect()
                jedis.auth("admin")


                jedis.lpush(InLoggedUser.publisherkey.toString(), text)

                this@ChatLogActivity.runOnUiThread {
                    adapter.add(ChatToItem(text))
                    editText.text.clear()
                    Log.d("textcheck", editText.text.toString())
                }

                Log.d("messagelistener_redis", "this is publisher")
            }
        }

        val redis_publisher_thread = Thread(redis_publisher_runnable())
        redis_publisher_thread.start()


    }

    private fun setupDummyData() {
        //set up history text

        val setupmessage = object : Thread() {
            override fun run() {
                val client = KMongo.createClient("54.164.138.27:27017")
                val database = client.getDatabase("CowChat")
                val col = database.getCollection<Chatmessage>("Messages")

                val message_list: List<Chatmessage> = col.find().toList()
                val col3 = database.getCollection<User>("Users")
                val user: List<User> = col3.find().toList()

//                user.forEach {
//                    if (it._id==InLoggedUser.uid){
//                        urlto=it.profileImageUrl
//                    }
//                    else if(it._id==Touser._id){
//                        urlfrom=it.profileImageUrl
//                    }
//                }

                message_list.forEach {
                    if (it.fromId == InLoggedUser.uid) {
                        user.forEach {
                            if (it._id == InLoggedUser.uid)
                                urlto = it.profileImageUrl
                        }
                        this@ChatLogActivity.runOnUiThread {
                            adapter.add(ChatToItem(it.text))
                        }
                    } else {
                        user.forEach {
                            if (it._id == Touser._id)
                                urlfrom = it.profileImageUrl
                        }
                        this@ChatLogActivity.runOnUiThread {
                            adapter.add(ChatFromItem(it.text))
                        }
                    }
                    Log.d(TAG, "this is user message: $it")
                }
            }
        }


        setupmessage.start()
    }
}


class ChatFromItem(val text:String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView.text = text
        Log.d("InLoggedUserurl",InLoggedUser.profileImageUrl)
        Picasso.get()?.load(urlfrom)?.into(viewHolder.itemView.imageViewFrom)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text:String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView.text = text
//        Log.d("InLoggedUserurl",InLoggedUser.profileImageUrl)
        Picasso.get().load(urlto).into(viewHolder.itemView.imageViewto)
    }


    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
