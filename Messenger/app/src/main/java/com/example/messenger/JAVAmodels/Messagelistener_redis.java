package com.example.messenger.JAVAmodels;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.messages.ChatFromItem;
import com.example.messenger.messages.ChatLogActivity;
import com.example.messenger.messages.ChatToItem;
import com.example.messenger.models.Chatmessage;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.ViewHolder;

import java.security.InvalidParameterException;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import redis.clients.jedis.Jedis;

public class Messagelistener_redis {


    public void listenformessage(){

         class redis_subscriber_runnable implements Runnable{

            @Override
            public void run() {
                Jedis jedis = new Jedis("3.231.90.126",6379);
                jedis.connect();
                jedis.auth("admin");

                Log.d("messagelistener_redis","this is subscriber");

                while (true){
                    List<String> brpop = jedis.brpop(0,"message_queue");
                    for (String string:brpop){
                        Log.d("messagelistener_redis",string);
                    }
                    brpop.clear();
                }

            }
        }

        Thread redis_subscriber_thread = new Thread(new redis_subscriber_runnable());
        redis_subscriber_thread.start();





    }

    public void pushmessage(final String text){
         class redis_publisher_runnable implements Runnable{

            @Override
            public void run() {
                Jedis jedis = new Jedis("3.231.90.126",6379);
                jedis.connect();
                jedis.auth("admin");


                jedis.lpush("message_queue",text);

                Log.d("messagelistener_redis","this is publisher");
            }
        }

        Thread redis_publisher_thread = new Thread(new redis_publisher_runnable());
        redis_publisher_thread.start();
    }









}
