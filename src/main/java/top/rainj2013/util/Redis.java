package top.rainj2013.util;

import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Redis {
	private JedisPool pool;

	public Redis(JedisPool pool) {
		this.pool = pool;
	}

	public void set(String key, String val) {
		Jedis jedis = pool.getResource();
		jedis.set(key, val);
		pool.returnResourceObject(jedis);
	}

	public String get(String key) {
		Jedis jedis = pool.getResource();
		String value = jedis.get(key);
		pool.returnResourceObject(jedis);
		return value;

	}

	public Map<String, String> hgetAll(String key) {
		Jedis jedis = pool.getResource();
		Map<String, String> map = jedis.hgetAll(key);
		pool.returnResourceObject(jedis);
		return map;
	}

	public String hget(String key, String field) {
		Jedis jedis = pool.getResource();
		String value = jedis.hget(key, field);
		pool.returnResourceObject(jedis);
		return value;
	}

	public void hset(String key, String field, String value) {
		Jedis jedis = pool.getResource();
		jedis.hset(key, field, value);
		pool.returnResourceObject(jedis);
	}
}