package top.rainj2013.spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.nutz.lang.Strings;

import top.rainj2013.AppContext;
import top.rainj2013.bean.User;
import top.rainj2013.util.HttpTookit;
import top.rainj2013.util.Redis;

public class ZhihuSpider {
	static final String PAGEURL = "http://www.zhihu.com/#signin";
	static final String LOGINURL = "http://www.zhihu.com/login/email";
	static final String CAPTCHAURL = "http://www.zhihu.com/captcha.gif?r=";
	static final String CHARTSET = "utf-8";
	static NutDao dao = AppContext.getDao();
	static Redis redis = AppContext.getRedis();

	public void login(String email, String password) throws IOException {
		String _xsrf = "";
		String captcha = null;
		String rememberme = "true";

		String html = HttpTookit.doGet(PAGEURL, null);
		Document doc = Jsoup.parse(html);
		_xsrf = doc.select("[name=_xsrf]").attr("value");
		System.out.println("正在载入..");
		if (html.contains("换一张")) {
			// 下载验证码
			long time = System.currentTimeMillis();
			CloseableHttpResponse resp = HttpTookit.doGet(CAPTCHAURL + time);
			InputStream in = resp.getEntity().getContent();
			File file = new File("captcha");
			if (!file.exists()) {
				file.mkdir();
			}
			FileOutputStream fos = new FileOutputStream("captcha/captcha.gif");
			byte[] buffer = new byte[8];
			int byteread;
			while ((byteread = in.read(buffer)) != -1) {
				fos.write(buffer, 0, byteread);
			}
			resp.close();
			in.close();
			fos.flush();
			fos.close();

			// 输入验证码
			Scanner scn = new Scanner(System.in);
			System.out.println("请输入验证码：");
			captcha = scn.next();
			scn.close();
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("email", email);
		params.put("password", password);
		params.put("remember_me", rememberme);
		params.put("_xsrf", _xsrf);
		if (!Strings.isEmpty(captcha)) {
			params.put("captcha", captcha);
		}
		System.out.println(HttpTookit.doPost(LOGINURL, params));
	}

	public void getMessage(String uid) throws Exception {
		String url = "https://www.zhihu.com/people/" + uid + "/about";
		String html = HttpTookit.doGet(url, null);
		Document doc = Jsoup.parse(html);
		String username = doc.select("a[class=name]").html();
		String bio = doc.select("span[class=bio]").html();
		String employment = doc.select("span[class=employment item]").attr("title");
		String position = doc.select("span[class=position item]").attr("title");
		String education = doc.select("span[class=education item]").attr("title");
		String edu_extra = doc.select("span[class=education-extra item]").attr("title");
		String followees = doc.select("a[class=item]").get(5).select("strong").html();
		String followers = doc.select("a[class=item]").get(6).select("strong").html();
		String asks = doc.select("a[class=item]").get(0).select("span").html();
		String answers = doc.select("a[class=item]").get(1).select("span").html();
		String posts = doc.select("a[class=item]").get(2).select("span").html();
		String collections = doc.select("a[class=item]").get(3).select("span").html();
		String logs = doc.select("a[class=item]").get(4).select("span").html();
		User user = new User(uid, username, bio, employment, position, education, edu_extra, followees, followers, asks,
				answers, posts, collections, logs);
		dao.insert(user);
	}

	public Set<String> getFollowList(String uid) throws Exception {
		Set<String> follows = new HashSet<String>();
		String url = "https://www.zhihu.com/people/" + uid + "/followees";
		String html = HttpTookit.doGet(url, null);
		Document doc = Jsoup.parse(html);
		Elements eles = doc.select("h2[class=zm-list-content-title]");
		for (Element ele : eles) {
			String href = ele.select("a").attr("href");
			follows.add(href.substring(href.lastIndexOf("/") + 1));
		}
		return follows;
	}

	public void CaptureFollows(String uid) {
		Set<String> set = null;
		try {
			set = getFollowList(uid);
		} catch (Exception e1) {
			System.out.println("请求失败");
		}
		if (set == null) {
			return;
		}
		ExecutorService executorService = Executors.newCachedThreadPool();
		int size = set.size();
		int index = 0;
		Iterator<String> iterator = set.iterator();

		while (iterator.hasNext()) {
			final String follow = iterator.next();
			System.out.println(index++ + "/" + size);
			if (!Strings.isEmpty(redis.get(follow))) {
				System.out.println("用户：" + follow + "已存在");
				iterator.remove();
				continue;
			}
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					System.out.println(Thread.currentThread() + "正在抓取" + follow);
					try {
						getMessage(follow);
					} catch (Exception e) {
						System.out.println("用户已存在");
					}
					redis.set(follow, follow);
				}
			});
		}
		try {
			executorService.awaitTermination(2, TimeUnit.SECONDS);
		} catch (Exception e) {
			System.out.println("请求超时");
		}
		executorService.shutdown();
		System.out.println("end!");
	}

	@Test
	public void init() {
		try {
			login("email", "password");
			
			while (true) {
				List<User> users = dao.query(User.class,
						Cnd.where(null).limit(1, 10).orderBy("capturetime", "desc"));
				for (User user : users)
					CaptureFollows(user.getUid());
				System.out.println("完成一轮抓取..");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
