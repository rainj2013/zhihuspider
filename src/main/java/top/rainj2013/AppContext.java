package top.rainj2013;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.lang.Lang;

import redis.clients.jedis.JedisPool;
import top.rainj2013.util.Redis;


public class AppContext {
	
	private static NutDao dao;
	private static Ioc ioc;
	private static Redis redis;
	
	public static Redis getRedis() {
		if (redis == null) {
			redis = new Redis(getIoc().get(JedisPool.class));
		}
		return redis;
	}
	
	public static NutDao getDao() {
		if (dao == null) {
			dao = getIoc().get(NutDao.class, "dao");
		}
		return dao;
	}
	
	public static Ioc getIoc() {
		if (ioc == null)
			try {
				ioc = new NutIoc(new ComboIocLoader(
						"*org.nutz.ioc.loader.json.JsonLoader", "config/dao.js","config/redis.js",
						"*org.nutz.ioc.loader.annotation.AnnotationIocLoader"));
			} catch (ClassNotFoundException e) {
				throw Lang.wrapThrow(e);
			}
		return ioc;
	}
}
