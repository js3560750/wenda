package com.nowcoder.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * 关注服务用到了userId和entityType和entityId userId是当前的操作者
 * 我们可以根据entityType和entityId来扩展该服务，根据不同的Type和Id可以关注很多不同类型的事物
 * 
 * @author 18894
 *
 */
@Service
public class FollowService {

	@Autowired
	JedisAdapter jedisAdapter;

	// 关注功能，可以关注一个用户，也可以关注一个人
	public boolean follow(int userId, int entityType, int entityId) {
		// 粉丝列表的key
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		// 我的关注列表的key
		String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
		// 关注的时间
		Date date = new Date();
		// 利用Redis事物保证下面两个zadd操作同时发生或者同时不发生
		Jedis jedis = jedisAdapter.getJedis();
		// Transaction是一个事物类
		// multi标记一个事务块的开始。 随后的指令将在执行EXEC时作为一个原子执行
		Transaction tx = jedisAdapter.multi(jedis);
		// 定义事物里具体的操作
		tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
		tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
		// EXEC是执行所有multi之后发的命令，EXEC的返回值是一个集合，其中每个元素与事物中的指令一一对应
		// 因此返回的List集合的Size应该等于2，因为事物中有两项操作
		List<Object> ret = jedisAdapter.exec(tx, jedis);

		// 如果上面事物操作成功，则下面的判断应该都为真
		return ret.size() == 2 && (long) ret.get(0) > 0 && (long) ret.get(1) > 0;
	}

	// 取消关注功能，可以关注一个用户，也可以关注一个人
	public boolean unfollow(int userId, int entityType, int entityId) {
		// 粉丝的key
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		// 我的关注列表的key
		String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
		// 关注的时间
		Date date = new Date();
		// 利用Redis事物保证下面两个zadd操作同时发生或者同时不发生
		Jedis jedis = jedisAdapter.getJedis();
		// Transaction是一个事物类
		// multi标记一个事务块的开始。 随后的指令将在执行EXEC时作为一个原子执行
		Transaction tx = jedisAdapter.multi(jedis);
		// 定义事物里具体的操作
		tx.zrem(followerKey, String.valueOf(userId));
		tx.zrem(followeeKey, String.valueOf(entityId));
		// EXEC是执行所有multi之后发的命令，EXEC的返回值是一个集合，其中每个元素与事物中的指令一一对应
		// 因此返回的List集合的Size应该等于2，因为事物中有两项操作
		List<Object> ret = jedisAdapter.exec(tx, jedis);

		// 如果上面事物操作成功，则下面的判断应该都为真
		return ret.size() == 2 && (long) ret.get(0) > 0 && (long) ret.get(1) > 0;
	}

	// 把Set类型转化为List<Integer>类型
	private List<Integer> getIdsFromSet(Set<String> idset) {
		List<Integer> ids = new ArrayList<>();
		for (String str : idset) {
			ids.add(Integer.parseInt(str));
		}
		return ids;
	}

	// 获取所有的粉丝
	public List<Integer> getFollowers(int entityType, int entityId, int count) {
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		// zrange返回的是Set类型，所以要用自定义的getIdsFromSet转换成List类型
		// zrevrange才能取最新的在前面，zrange是最旧的在前面
		return getIdsFromSet(jedisAdapter.zrevrange(followerKey, 0, count));

	}

	// 获取所有的关注者，添加了offset参数用来分页
	public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		// zrange返回的是Set类型，所以要用自定义的getIdsFromSet转换成List类型
		return getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, count));

	}

	// 获取我的关注列表
	public List<Integer> getFollowees(int userId, int entityType, int count) {
		String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
		// zrange返回的是Set类型，所以要用自定义的getIdsFromSet转换成List类型
		return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, 0, count));

	}

	// 获取我的关注列表，添加了offset参数用来分页
	public List<Integer> getFollowees(int userId, int entityType, int offset, int count) {
		String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
		// zrange返回的是Set类型，所以要用自定义的getIdsFromSet转换成List类型
		return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, count));

	}

	// 获取粉丝数目
	public long getFollowerCount(int entityType, int entityId) {
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		return jedisAdapter.zcard(followerKey);
	}

	// 获取我的关注列表里的数目
	public long getFolloweeCount(int userId, int entityType) {
		String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
		return jedisAdapter.zcard(followeeKey);
	}

	/**
	 * 判断用户是否关注了某个实体
	 * 
	 * @param userId
	 * @param entityType
	 * @param entityId
	 * @return
	 */
	public boolean isFollower(int userId, int entityType, int entityId) {
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		// 如果分数不等于空，则说明该用户关注了该实体
		return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
	}
}
