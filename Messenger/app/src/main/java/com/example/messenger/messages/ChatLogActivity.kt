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
import com.example.messenger.registerlogin.Jedi
import com.example.messenger.registerlogin.LoggedinUser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mongodb.client.MongoCursor
import com.mongodb.client.model.changestream.FullDocument
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


class ChatLogActivity : AppCompatActivity() {

    companion object{
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

        setupDummyData()

        //listen message
        listenMessage()

        //send message
        send_button_chat_log.setOnClickListener {

            Log.d(TAG,"Attempt to send message")

            performSendMessage()
        }
    }

    private fun listenMessage(){
        //message change listener
//        val messagelistener = Messagelistener_redis()

//        val messagelistener = Messagelistener()
//        messagelistener.listenformessage()

        class redis_subscriber_runnable : Runnable {

            override fun run() {
                val jedis = Jedis("3.231.90.126", 6379)
                jedis.connect()
                jedis.auth("admin")

                Log.d("messagelistener_redis", "this is subscriber")

                while (true) {
                    val brpop = jedis.brpop(0, "message_queue")

                    if (brpop != null){
                        for (text in brpop){
                            if (text!="message_queue"){
                                this@ChatLogActivity.runOnUiThread {
                                    adapter.add(ChatToItem(text))
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


    private fun performSendMessage(){
        //how do we actually send a message to mongodb ...
        //and use redis to notify data change

        val text = editText.text.toString()

        val message_id = ObjectIdGenerator.newObjectId<ObjectId>().toString()

        val fromId = InLoggedUser.uid
        val Toid = Touser._id

        val chatmessage = Chatmessage(message_id,text,fromId,Toid,System.currentTimeMillis()/1000)


        var messageThread = object : Thread(){
            override fun run() {
                val client = KMongo.createClient("54.164.138.27:27017")
                val database = client.getDatabase("CowChat")
                val col = database.getCollection<Chatmessage>("Messages")

                col.insertOne(chatmessage)
            }
        }

        //record message in mongo db
        messageThread.start()

        //notify message change
//        val messagelistener = Messagelistener_redis()

//        val messagelistener = Messagelistener()
//        messagelistener.pushmessage(chatmessage.text)

        class redis_publisher_runnable : Runnable {

            override fun run() {
                val jedis = Jedis("3.231.90.126", 6379)
                jedis.connect()
                jedis.auth("admin")


                jedis.lpush("message_queue", text)

                this@ChatLogActivity.runOnUiThread {
                    adapter.add(ChatFromItem(text))
                }

                Log.d("messagelistener_redis", "this is publisher")
            }
        }

        val redis_publisher_thread = Thread(redis_publisher_runnable())
        redis_publisher_thread.start()


    }

    private fun setupDummyData(){
        val setupmessage = object : Thread() {
            override fun run() {
                val client = KMongo.createClient("54.164.138.27:27017")
                val database = client.getDatabase("CowChat")
                val col = database.getCollection<Chatmessage>("Messages")

                val message_list : List<Chatmessage> = col.find().toList()


                message_list.forEach {
                    if (it.fromId == InLoggedUser.uid){
                        this@ChatLogActivity.runOnUiThread {
                            adapter.add(ChatFromItem(it.text))
                        }
                    }else{
                        this@ChatLogActivity.runOnUiThread {
                            adapter.add(ChatToItem(it.text))
                        }
                    }

                    Log.d(TAG,"this is user message: $it")
                }
            }
        }

        setupmessage.start()

    }
}

class ChatFromItem(val text:String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text:String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}

class Messagelistener{


    var currentMessage : String by Delegates.observable(""){
            property, oldValue, newValue ->
            Log.d("messagelistener_redis","$oldValue -> $newValue")
    }

    fun listenformessage() {

        class redis_subscriber_runnable : Runnable {

            override fun run() {
                val jedis = Jedis("3.231.90.126", 6379)
                jedis.connect()
                jedis.auth("admin")

                Log.d("messagelistener_redis", "this is subscriber")

                while (true) {
                    val brpop = jedis.brpop(0, "message_queue")
//                    for (string in brpop) {
//                        Log.d("messagelistener_redis", "im subscriber $string")
//                    }
                    if (brpop != null){
                        for (text in brpop){
                            if (text!="message_queue"){
                                this@Messagelistener.run {
                                    currentMessage = text
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

    fun pushmessage(text: String) {
        class redis_publisher_runnable : Runnable {

            override fun run() {
                val jedis = Jedis("3.231.90.126", 6379)
                jedis.connect()
                jedis.auth("admin")


                jedis.lpush("message_queue", text)

                Log.d("messagelistener_redis", "this is publisher")
            }
        }

        val redis_publisher_thread = Thread(redis_publisher_runnable())
        redis_publisher_thread.start()
    }




}
