package com.nowcoder.util;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.controller.LoginController;
import com.nowcoder.model.User;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.*;

/**
 * 通过Jedis接口使用Redis数据库
 * 
 * 因为要在项目中用到，所以要添加@Service，把这个做成一个服务
 * 
 * 因为要初始化连接池JedisPool，所以要implements InitializingBean并调用默认的 afterPropertiesSet()方法
 * @author 18894
 *
 */
@Service
public class JedisAdapter implements InitializingBean{	
	
	//日志
	private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
	
	
	//连接池
	private JedisPool pool;
	

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		//初始化连接池，连接Redis中第10个数据库，Redis端口是6379，默认配置Redis最多有16个数据库
		pool = new JedisPool("redis://localhost:6379/10");
	}
	
	//包装一些对Redis数据库操作的方法！！！！！！！！！！！！！！！！！！！！
	//注意各个方法的返回值与Jedis对应方法的返回值相同
	public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

	//删除
    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    //获取数量
    //注意scard的返回类型是long
    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    //是否存在
    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    //brpop是一个阻塞的列表弹出原语
    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    //从队列的左边入队一个元素
    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }
    
    //从队列中返回指定Index范围内的元素
    public List<String> lrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
    
    //将所有指定成员添加到键为key有序集合（sorted set）里面
    public long zadd(String key, double score, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key, score, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    //从排序的集合（Sorted set)里面删除一个或多个成员
    public long zrem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }
    
    //根据指定的index返回有序集合的成员列表
    public Set<String> zrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    //返回有序集key中，指定区间内的成员
    public Set<String> zrevrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    //获取有序集合中的成员数量
    public long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    //返回有序集key中，成员member的score值
    public Double zscore(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    //获取一个Jedis
    public Jedis getJedis(){
    	return pool.getResource();
    }
    
    //下面几个方法是Redis的事物功能，multi事物开启，exec事物结束
    //比如A关注了B，则A的关注列表里一定要有B，B的粉丝里一定要有A。
    //这两件事必须同时发生，或者同时不发生，Redis事物就是确保了这两件事的一致性。
    public Transaction multi(Jedis jedis){
    	try {
			return jedis.multi();
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("jedis.multi发生异常"+e.getMessage());
		}
    	return null;
    }
    
    public List<Object> exec(Transaction tx,Jedis jedis){
    	try {
			return tx.exec();
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("exec发生异常"+e.getMessage());
		}finally{
			if(tx!=null){
				try {
					tx.close();
				} catch (Exception e2) {
					// TODO: handle exception
					logger.error("close发生异常"+e2.getMessage());
				}
			}
			if(jedis!=null){
				jedis.close();
			}
		}
    	return null;
    }
	
	
	//打印函数
	public static void print (int index,Object obj){
		System.out.println(String.format("%d, %s", index,obj.toString()));
	}
	
	
	
	//测试代码
	public static void main(String[] args) {
		
		//初始化Jedis,连接Redis中第9个数据库，Redis端口是6379
		//注意引入import redis.clients.jedis.*;
		Jedis jedis = new Jedis("redis://localhost:6379/9");
		
		//删除当前数据库里面的所有数据。
		jedis.flushDB();
		
		// get set
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newhello");
        print(1, jedis.get("newhello"));
        
        //setex函数可以设置过期时间，单位秒，过了这个时间，这个键值对就会自动删掉
        //这是一个非常好用的功能，比如用户登录填验证码的时候，用户填的验证码或者短信验证码就能用这个做
        //凡是有有效时间的都能用这个来做。
        jedis.setex("hello2", 1800, "world");

        //
        jedis.set("pv", "100");
        jedis.incr("pv");//+1
        jedis.incrBy("pv", 5);//+5
        print(2, jedis.get("pv"));
        jedis.decrBy("pv", 2);//-2
        print(2, jedis.get("pv"));

        print(3, jedis.keys("*"));	//获得所有键名

        String listName = "list";
        jedis.del(listName);	//删除指定的key
        for (int i = 0; i < 10; ++i) {
            jedis.lpush(listName, "a" + String.valueOf(i));	//lpush从队列左边入队一个或多个元素
        }
        print(4, jedis.lrange(listName, 0, 12));	//列出listName队列中第0到第12个键值
        print(4, jedis.lrange(listName, 0, 3));
        print(5, jedis.llen(listName));	//长度
        print(6, jedis.lpop(listName));	//弹出最左边的，这里是第0位
        print(7, jedis.llen(listName));
        print(8, jedis.lrange(listName, 2, 6));
        print(9, jedis.lindex(listName, 3));	//根据脚标列出键值
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));	//在键值a4之后插入xx键值
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "bb"));	//在键值a4之前插入bb键值
        print(11, jedis.lrange(listName, 0 ,12));

        // hash
        /**
         * Hash结构，h开头的操作，本来redis就是一个Key，
         * 这个userKey所对应的value又可以是一个HashMap一样的结构，
         * 这里就相当于
         * Key=userKey，value={name=jin,age=12,phone=18618181818}
         */
        String userKey = "userxx";
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "18618181818");
        print(12, jedis.hget(userKey, "name"));
        print(13, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "phone");
        print(14, jedis.hgetAll(userKey));
        print(15, jedis.hexists(userKey, "email"));
        print(16, jedis.hexists(userKey, "age"));
        print(17, jedis.hkeys(userKey));	//获得所有的键名
        print(18, jedis.hvals(userKey));	//获得所有的键值
        jedis.hsetnx(userKey, "school", "zju");	//如果不存在school键名，则增加school=zju,若school这个键名已经有了，则什么都不做
        jedis.hsetnx(userKey, "name", "yxy");
        print(19, jedis.hgetAll(userKey));

        // set
        /**
         * 这是一个集合
         * 集合的概念就是集合内没有重复元素，比如往集合内插入3个a，那么只会有1个a存在
         */
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for (int i = 0; i < 10; ++i) {
            jedis.sadd(likeKey1, String.valueOf(i));
            jedis.sadd(likeKey2, String.valueOf(i*i));
        }
        print(20, jedis.smembers(likeKey1));	//获得集合的所有成员
        print(21, jedis.smembers(likeKey2));
        print(22, jedis.sunion(likeKey1, likeKey2));	//获得两个集合的并集
        print(23, jedis.sdiff(likeKey1, likeKey2));		//第一个集合减去第二个集合存在的元素之后所剩下的元素
        print(24, jedis.sinter(likeKey1, likeKey2));	//获得两个集合的交集
        print(25, jedis.sismember(likeKey1, "12"));
        print(26, jedis.sismember(likeKey2, "16"));
        jedis.srem(likeKey1, "5");	//删除
        print(27, jedis.smembers(likeKey1));
        jedis.smove(likeKey2, likeKey1, "25");	//把likeKey2集合的25元素给likeKey1
        print(28, jedis.smembers(likeKey1));
        print(29, jedis.scard(likeKey1));	//获得元素个数

        
        //Sorted Sets带排序的集合
        /**
         * Sorted Sets，带排序的集合。z开头的操作，
         * 这里的rankKey是一个排序集合，人的名字不能重复，
         * 人名对应的数值（假设为考试分数）可以重复。
         * 后一次插入相同人名的数值会覆盖掉之前的数值
         */
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "jim");
        jedis.zadd(rankKey, 60, "Ben");
        jedis.zadd(rankKey, 90, "Lee");
        jedis.zadd(rankKey, 75, "Lucy");
        jedis.zadd(rankKey, 80, "Mei");
        print(30, jedis.zcard(rankKey));
        print(31, jedis.zcount(rankKey, 61, 100));	//61分到100分的个数
        print(32, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Lucy");
        print(33, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Luc");
        print(34, jedis.zscore(rankKey, "Luc"));
        print(35, jedis.zrange(rankKey, 0, 100));	//这个zrange列出0位到100位的人名
        print(36, jedis.zrange(rankKey, 0, 10));	//zrange默认是从低到高的排序
        print(36, jedis.zrange(rankKey, 1, 3));
        print(36, jedis.zrevrange(rankKey, 1, 3));	//默认从高到低
        
        //Tuple定义了一个数组
        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, "60", "100")) {
            print(37, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }

        print(38, jedis.zrank(rankKey, "Ben"));
        print(39, jedis.zrevrank(rankKey, "Ben"));

        String setKey = "zset";
        jedis.zadd(setKey, 1, "a");
        jedis.zadd(setKey, 1, "b");
        jedis.zadd(setKey, 1, "c");
        jedis.zadd(setKey, 1, "d");
        jedis.zadd(setKey, 1, "e");

        //zlexcount返回成员之间的成员数量
        print(40, jedis.zlexcount(setKey, "-", "+"));	//负无穷到正无穷
        print(41, jedis.zlexcount(setKey, "(b", "[d"));	//不包含b，但包含d，这里是c、d两个
        print(42, jedis.zlexcount(setKey, "[b", "[d"));	//包含b，包含d，这里是b、c、d三个
        jedis.zrem(setKey, "b");
        print(43, jedis.zrange(setKey, 0, 10));
        jedis.zremrangeByLex(setKey, "(c", "+"); //不包含c，比c大的都remove删除
        print(44, jedis.zrange(setKey, 0 ,2));
		
		//连接池
        /**
         * 如果网站访问量大，我们对Redis操作肯定是通过连接池JedisPool，
         * 连接池相当于开了8条线程。但是每次操作完，一定要close，
         * 即把从连接池里拿出来的资源还回池子，因为池子里只有8个资源
         */
		JedisPool pool2 = new JedisPool();
		for(int i=0;i<20;i++){
			//获得一个连接资源
			Jedis j = pool2.getResource();
			//使用这个连接资源
			print(45, jedis.get("newhello"));
			//把这个连接资源还回池子里！！！
			/**
			 * ！！！！！！！！！重要：如果没有j.close()
			 * 则每次拿出来的连接资源都没有还到池子里，池子里最多有8条资源
			 * 所以如果没j.close()，则这里只能打印出8条world，而不是20条
			 */
			j.close();	
		}
		
		/**
		 * 利用Redis做缓存
		 * 把User对象序列化后（属性值都转换为JSON格式的字符串）存入Redis
		 * 然后再取出来反序列化后又变成一个新的User对象但属性值一致
		 */
        User user = new User();
        user.setName("xx");
        user.setPassword("ppp");
        user.setHeadUrl("a.png");
        user.setSalt("salt");
        user.setId(1);
        print(46, JSONObject.toJSONString(user));
        //序列化存入Redis
        jedis.set("user1", JSONObject.toJSONString(user));

        //从Redis取出
        String value = jedis.get("user1");
        //反序列化
        User user2 = JSON.parseObject(value, User.class);	//注意第二个参数
        print(47, user2);
        
        
        jedis.lpush("EVENT_TEST", JSONObject.toJSONString(user));
        
        
	}



}
