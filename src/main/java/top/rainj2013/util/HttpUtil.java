package top.rainj2013.util;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.nutz.http.Cookie;
import org.nutz.http.Header;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;

public class HttpUtil {
	
	private static Header header = Header.create();
	static {
		reloadConfig();
	}
	public static void reloadConfig(){
		header.set("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
		header.set("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
		header.set("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		header.set("Cache-Control","max-age=0");
		header.set("Accept-Encoding", "gzip, deflate");
		header.set("Host", "login.weibo.cn");
		header.set("Referer", "http://login.weibo.cn/login/?ns=1&revalid=2&backURL=http%3A%2F%2Fweibo.cn%2F%3Ffrom%3Dhome&backTitle=%CE%A2%B2%A9&vt=4");
		header.set("Origin", "http://login.weibo.cn");
		
		//Http.setHttpProxy("218.244.149.79", 3128);
	}
	
	public static String get(final String url,final String chartset){
		
		ExecutorService exec = Executors.newFixedThreadPool(1);
		Callable<String> call = new Callable<String>() {
			public String call() throws Exception {
				Request request = Request.get(url);
				request.setHeader(header);
				String html = Sender.create(request).send().getContent(chartset);
				return html;
			}
		};
		String html = "";
		try {
			Future<String> future = exec.submit(call);
			html = future.get(5, TimeUnit.SECONDS);
		} catch (TimeoutException ex) {
			System.out.println("抓取超时");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("处理失败.");
		}
		exec.shutdown();
		return html;
	}
	
	public static String get(String url,String chartset,Cookie cookie){
		Request request = Request.get(url);
		request.setHeader(header);
		request.setCookie(cookie);
		String html = Sender.create(request).send().getContent(chartset);
		return html;
	}
	
	public static Response get(String url){
		Request request = Request.create(url, Request.METHOD.GET);
		return Sender.create(request).send();
	}
	
	public static Cookie post_cookie(String url,Map<String,Object> params,String chartset){
		Request request = Request.create(url, Request.METHOD.POST, params);
		request.setHeader(header);
		Cookie cookie =  Sender.create(request).send().getCookie();
		return cookie;
	}
	
	public static String post_html(String url,Map<String,Object> params,String chartset){
		Request request = Request.create(url, Request.METHOD.POST, params);
		request.setHeader(header);
		String html = Sender.create(request).send().getContent();
		return html;
	}
	
	public static Response post(String url,Map<String,Object> params,String chartset){
		Request request = Request.create(url, Request.METHOD.POST, params);
		request.setHeader(header);
		return Sender.create(request).send();
	}
	
	public static Header getHeader(){
		return header;
	}
}
