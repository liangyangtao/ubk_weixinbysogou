package com.unbank.redis;
import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;



public class RedisClient {

    private Jedis jedis;//非切片额客户端连接
    private JedisPool jedisPool;//非切片连接池
    private ShardedJedis shardedJedis;//切片额客户端连接
    private ShardedJedisPool shardedJedisPool;//切片连接池
    
    public RedisClient() 
    { 
        initialPool(); 
        initialShardedPool(); 
        shardedJedis = shardedJedisPool.getResource(); 
        jedis = jedisPool.getResource(); 
        
        
    } 
 
    /**
     * 初始化非切片池
     */
    private void initialPool() 
    { 
        // 池基本配置 
        JedisPoolConfig config = new JedisPoolConfig(); 
//        config.setMaxActive(20); 
        config.setMaxTotal(20);
        config.setMaxIdle(5); 
//        config.setMaxIdle(maxIdle);
//        config.setMaxWait(1000l); 
        config.setMaxWaitMillis(5000l);
        config.setTestOnBorrow(false); 
        jedisPool = new JedisPool(config,"127.0.0.1",6379);
    }
    
    /** 
     * 初始化切片池 
     */ 
    private void initialShardedPool() 
    { 
        // 池基本配置 
        JedisPoolConfig config = new JedisPoolConfig(); 
//        config.setMaxActive(20); 
        config.setMaxTotal(20);
        config.setMaxIdle(5); 
//        config.setMaxWait(1000l); 
        config.setMaxWaitMillis(1000l);
        config.setTestOnBorrow(false); 
        // slave链接 
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(); 
        shards.add(new JedisShardInfo("127.0.0.1", 6379, "master")); 

        // 构造池 
        shardedJedisPool = new ShardedJedisPool(config, shards); 
    } 

    public void show() {     
//        KeyOperate(); 
//        StringOperate(); 
//        ListOperate(); 
//        SetOperate();
//        SortedSetOperate();
//        HashOperate(); 
//    	jedisPool.returnBrokenResource(jedis);
    	jedisPool.destroy();
//        jedisPool.returnResource(jedis);
//        shardedJedisPool.returnResource(shardedJedis);
//    	shardedJedisPool.
    } 

     
}