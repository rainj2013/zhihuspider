package top.rainj2013.spider;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;
import org.nutz.dao.impl.NutDao;
import org.nutz.http.Http;

import top.rainj2013.AppContext;
import top.rainj2013.bean.IP;
import top.rainj2013.util.HttpUtil;

public class IPSpider {
	static final String chartset = "utf-8";
	static ScriptEngine jse = new ScriptEngineManager()
			.getEngineByName("JavaScript");
	Calendar calendar = Calendar.getInstance();
	private static NutDao dao = AppContext.getDao();

	@Test
	public static void CaptureIP() throws ScriptException {
		/*String url = "pachong.org";
		String html = HttpUtil.get(url, "utf-8");
		Document doc = Jsoup.parse(html);

		String vars = doc.select("script").get(2).html();

		Elements trs = doc.select("tbody").get(0).select("tr");
		int id = 0;
		String host;
		String portString;
		int port;
		for (Element tr : trs) {
			portString = tr.select("td").get(2).select("script").html();
			portString = portString.substring(portString.indexOf("(") + 1);
			portString = portString.substring(0, portString.lastIndexOf(")"));

			port = (int) ((Double) jse.eval(vars + portString))
					.doubleValue();
			host = tr.select("td").get(1).text();

			IP _ip = new IP(host, port,false);
			_ip.setId(id);
			try {
				dao.insert(_ip);
			} catch (DaoException e) {
				System.out.println("IP已存在与数据库中，跳过");
			}
			id++;
			System.out.println(_ip);
		}*/
		String url = "www.66ip.cn/mo.php?sxb=&tqsl=1000&port=&export=&ktip=&sxa=&submit=%CC%E1++%C8%A1&textarea=";
		String html = HttpUtil.get(url, "utf-8");
		int id = 0;
		String host;
		int port;
		System.out.println(html);
		String[] ips = html.split("></script>")[1].trim().split(
				"<br />");

		for (String ip : ips) {
			try {
				host = ip.trim().split(":")[0];
				port = Integer.parseInt(ip.trim().split(":")[1]);
				IP _ip = new IP(host, port, false);
				_ip.setId(id);
				dao.insert(_ip);
			} catch (Exception e) {
				System.out.println("IP已存在与数据库中，跳过");
				continue;
			}
			id++;
		}
	}

	@Test
	public static void cleanIP() {
		List<IP> ips = dao.query(IP.class,null);
		for (IP ip : ips) {
			Http.setHttpProxy(ip.getAddress(), ip.getPort());

			ExecutorService exec = Executors.newFixedThreadPool(1);
			Callable<String> call = new Callable<String>() {
				public String call() throws Exception {
					return HttpUtil.get("weibo.cn", "utf-8");
				}
			};
			try {
				Future<String> future = exec.submit(call);
				String html = future.get(1, TimeUnit.SECONDS);
				if(html.length()>0){
					ip.setVerify(true);
					dao.update(ip);
					System.out.println("验证通过，保存" + ip);
				}else{
					dao.delete(ip);
					System.out.println("删除" + ip);
				}
				
			} catch (TimeoutException ex) {
				dao.delete(ip);
				System.out.println("删除" + ip);
			} catch (Exception e) {
				dao.delete(ip);
				System.out.println("出错了/r/n删除" + ip);
			}
			exec.shutdown();
		}
		resetId();
	}

	public static void resetId() {
		List<IP> ips = dao.query(IP.class,null);
		int id = 0;
		for (IP ip : ips) {
			ip.setId(Integer.valueOf(id));
			dao.update(ip);
			id++;
		}
	}

	public static void exec() {
		try {
			CaptureIP();
		} catch (ScriptException e) {
			System.out.println("抓取IP出错");
		}
		cleanIP();
	}

	public static void main(String[] args) {
		try {
			CaptureIP();
		} catch (ScriptException e) {
			System.out.println("抓取IP出错");
		}
		cleanIP();
		//resetId();
	}
}
