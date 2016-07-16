package com.dmsg;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest
{
   @Test
   public void redis() {

      Jedis jedis = new Jedis("localhost");
      jedis.del("hashTest");

      System.out.println("读取数据：" + jedis.get("hello"));

   }

   @Test
   public void hash(){
      Jedis jedis = new Jedis("localhost");
      Map<String, String> data = new HashMap<String, String>();

      for (int i = 1; i < 1000000; i++) {

         data.put("key" + i, "val" + i);
         //jedis.hset("hashTest", "key" + i, "val" + i);
      }

      long begin = System.currentTimeMillis();
      System.out.println(data.get("key10002"));
      System.out.println("hashMap 扫描耗时:" + (System.currentTimeMillis() - begin));

      begin = System.currentTimeMillis();
      System.out.println(jedis.hget("hashTest","key10002"));
      System.out.println("redis 扫描耗时:" + (System.currentTimeMillis() - begin));
   }







}
