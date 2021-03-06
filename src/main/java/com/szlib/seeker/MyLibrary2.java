package com.szlib.seeker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyLibrary2 {
	private static final Log log = LogFactory.get();
	private static Setting setting = new Setting("project2.setting", true);
	
	private static final OkHttpClient client = new OkHttpClient();

	private static String run(String url,String ua) throws IOException {
		Request request = new Request.Builder()
				.addHeader("User-Agent",ua)
				.url(url).build();

		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}
	
	public static void main(String[] args) throws IOException {
		Objects.requireNonNull(setting, "配置不能为空");
		Setting header = setting.getSetting("header");
		List<String> uas = new ArrayList<>();
		uas.add("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36 Edg/88.0.705.56");
		uas.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Gecko/20100101 Firefox/86.0");
		String userAgent = uas.get(RandomUtil.randomInt(0, 1));
//		String baseUrl = setting.getStr("baseUrl");
		String url = setting.getStr("initUrl");
		String host = header.getStr("host");
		
		String[] referers = header.getStrings("referer");
		String referer = referers[0];
		String connection = header.getStr("connection");
		String accept = header.getStr("accept");

		Setting proxySetting = setting.getSetting("proxy");
		Proxy proxy= null;
		boolean enable = proxySetting.getBool("enable");
		if(enable) {
			String proxyHost = proxySetting.getStr("host");
			int proxyPort = proxySetting.getInt("port");
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
		}

//		String result = buildRequest(url,proxy).header(Header.USER_AGENT, userAgent).header(Header.HOST, host)
//				.header(Header.REFERER, referer).header(Header.ACCEPT, accept).header(Header.CONNECTION, connection)
//				.execute().body();
//		System.out.println(result);
		String result = run(url,userAgent);
		String ajaxUrl = setting.getStr("ajaxUrl");
		referer = referers[1];
		long current = DateUtil.current();
//		long current = DateUtil.currentSeconds();
		String ajaxUrl_format = StrUtil.format(ajaxUrl, 1, current);
		
		accept = header.getStr("accept-json");
		String x_requested_with = header.getStr("x_requested_with");
		
//		result = buildRequest(ajaxUrl_format,proxy)
//				.header(Header.USER_AGENT, userAgent)
//				.header(Header.HOST, host)
//				.header(Header.REFERER, referer)
//				.header(Header.ACCEPT, accept)
//				.header(Header.CONNECTION, connection)
//				.header("Cache-Control","max-age=0")
////				.header("X-Requested-With", x_requested_with)
//				.header("Accept-Encoding","gzip, deflate, br")
//				.header("Sec-Fetch-Dest","document")
//				.header("Sec-Fetch-Mode","navigate")
//				.header("Sec-Fetch-Site","none")
//				.execute().body();
		result = run(ajaxUrl_format,userAgent);
		 System.out.println(result);

		JSONObject resultJson = JSONUtil.parseObj(result);
		int totalno = resultJson.getInt("totalno");
		int totalPage = PageUtil.totalPage(totalno, 10);
		
		JSONArray resultArray = resultJson.getJSONArray("result");
		List<NewBook> newBooks = json2NewBook(resultArray);

		int currentYear = DateUtil.thisYear();
		int randomStart = RandomUtil.randomInt(currentYear, RandomUtil.randomInt(currentYear+2,currentYear+38));
		if(currentYear - randomStart > -1) {
			System.out.println("currentYear:"+currentYear+",randomStart:"+randomStart);
			randomStart = randomStart + RandomUtil.randomInt(1, currentYear);
		}
		try {
			for (int i = 2; i < totalPage; i++) {
				System.out.println(i);
//				current = DateUtil.current(false);
				current = DateUtil.current();
				ajaxUrl_format = StrUtil.format(ajaxUrl, i, current);
//				result = buildRequest(ajaxUrl_format,proxy)
//						.header(Header.USER_AGENT, userAgent)
//						.header(Header.HOST, host)
//						.header(Header.REFERER, referer)
//						.header(Header.ACCEPT, accept)
//						.header(Header.CONNECTION, connection)
//						.header("Cache-Control","max-age=0")
////						.header("X-Requested-With", x_requested_with)
//						.header("Accept-Encoding","gzip, deflate, br")
//						.header("Sec-Fetch-Dest","document")
//						.header("Sec-Fetch-Mode","navigate")
//						.header("Sec-Fetch-Site","none")
//						.execute().body();
				result = run(ajaxUrl_format,userAgent);
				resultJson = JSONUtil.parseObj(result);
				resultArray = resultJson.getJSONArray("result");
				newBooks.addAll(json2NewBook(resultArray));
				ThreadUtil.safeSleep(RandomUtil.randomInt(currentYear, randomStart));
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		Comparator<NewBook> comparator = (b1, b2) -> b1.getPublisher_time().compareTo(b2.getPublisher_time());
		newBooks.sort(comparator.reversed());
		String today = DateUtil.today();
		int caredYear = DateUtil.year(new Date());//重点关注的年份
		String fileName = today + "深图新书选购目录.txt";
		if(caredYear > 0) {
			fileName = today + "深图"+caredYear+"年后出版新书选购目录.txt";
		}
		FileWriter writer = new FileWriter(fileName);

		newBooks.stream().filter(o->DateUtil.year(o.getPublisher_time()) >=caredYear).forEach(o->{
			List<String> lines = new ArrayList<>();
			lines.add("标题:" + o.getTitle());
			lines.add("作者:" + o.getAuthor());
			lines.add("价格:" + o.getPrice());
			lines.add("出版者:" + o.getPublisher_name());
			lines.add("出版年:" + o.getPublisher_date());
			lines.add("简介:" + o.getAbstract_self());
//			lines.add("能否可借:" + o.getCheckUrl());
//			lines.add("读者自取:" + o.getReaderAccessUrl());
			lines.add("\n");
			writer.appendLines(lines);
		});
//		newBooks.forEach(o -> {
//			List<String> lines = new ArrayList<>();
//			lines.add("标题:" + o.getTitle());
//			lines.add("作者:" + o.getAuthor());
//			lines.add("价格:" + o.getPrice());
//			lines.add("出版者:" + o.getPublisher_name());
//			lines.add("出版年:" + o.getPublisher_date());
//			lines.add("简介:" + o.getAbstract_self());
//			lines.add("能否可借:" + o.getCheckUrl());
//			lines.add("读者自取:" + o.getReaderAccessUrl());
//			lines.add("\n");
//			writer.appendLines(lines);
//		});
	}

	private static HttpRequest buildRequest(String url, Proxy proxy) {
		HttpRequest httpRequest = HttpRequest.get(url);
		if(proxy!=null) {
			httpRequest = httpRequest.setProxy(proxy);
		}
		System.out.println(url);
		return httpRequest;
	}
	
	private static List<NewBook> json2NewBook(JSONArray resultArray){
		List<NewBook> newBooks = new ArrayList<NewBook>();
		String format = "yyyy.MM";
//		String checkUrl = setting.getStr("checkUrl");
//		String readerAccessUrl = setting.getStr("readerAccessUrl");
//		String addExpressUrl = setting.getStr("addExpressUrl");
		
		char[] slash = new char[] { '/','-' };
		char[]  brackets = new char[] { '[', ']' };
		for (int i = 0, size = resultArray.size(); i < size; i++) {
			JSONObject book = resultArray.getJSONObject(i);
			NewBook newBook = new NewBook();
			String title = book.getStr("title");
			String author = book.getStr("author");
			String publisher = book.getStr("publisher");
			String[] publisher_spilt = StrUtil.split(publisher, ",");
			String abstract_ = book.getStr("abstract");
			String isbn = book.getStr("isbn");
			String price = book.getStr("price");

			String biblisomtableid = book.getStr("biblisomtableid");
			String biblisommetaid = book.getStr("biblisommetaid");
			String ordcatamid = book.getStr("ordcatamid");

			newBook.setTitle(title);
			newBook.setAuthor(author);
			newBook.setAbstract_self(abstract_);
			newBook.setIsbn(isbn);
			newBook.setPublisher_name(publisher_spilt[0]);
			newBook.setPublisher_date(publisher_spilt[1]);
			DateTime pdate = null;
			String publisher_spilt_1 = null;
			try {
				System.out.println(publisher_spilt[1]);
				if(StrUtil.isBlankOrUndefined(publisher_spilt[1])) {
					pdate = DateUtil.parse("1989.09", format);
				}else {
					publisher_spilt_1 = StrUtil.replaceChars(publisher_spilt[1], slash, ".");
//					System.out.println(publisher_spilt_1);
					pdate = DateUtil.parse(publisher_spilt_1, format);
				}
			} catch (Exception e) {
//				 log.error(publisher_spilt[1],e);
//				System.err.println(publisher_spilt[1]);
				e.printStackTrace();
				publisher_spilt_1 = StrUtil.replaceChars(publisher_spilt[1], brackets, "");
				pdate = DateUtil.parse(publisher_spilt_1, "yyyy");
			}

			newBook.setPublisher_time(pdate);
			newBook.setPrice(price);

			newBook.setBiblisomtableid(biblisomtableid);
			newBook.setBiblisommetaid(biblisommetaid);
			newBook.setOrdcatamid(ordcatamid);

			// 我优先关注出版年份晚于2017的
//			if (DateUtil.year(pdate) >= 2018 && DateUtil.month(pdate) > 5) {
//				String checkUrl_format = StrUtil.format(checkUrl, biblisomtableid, biblisommetaid, ordcatamid);
//				String readerAccessUrl_format = StrUtil.format(readerAccessUrl, biblisomtableid, biblisommetaid,
//						ordcatamid);
//				newBook.setCheckUrl(checkUrl_format);
//				newBook.setReaderAccessUrl(readerAccessUrl_format);
//				newBook.setAddExpressUrl(addExpressUrl);
//			}

			newBooks.add(newBook);
		}
		return newBooks;
	}

//	private static List<NewBook> json2NewBook(JSONArray resultArray) {
//		List<NewBook> newBooks = new ArrayList<NewBook>();
//		String format = "yyyy.MM";
//		String checkUrl = setting.getStr("checkUrl");
//		String readerAccessUrl = setting.getStr("readerAccessUrl");
//		String addExpressUrl = setting.getStr("addExpressUrl");
//		
//		char[] slash = new char[] { '/' };
//		char[]  brackets = new char[] { '[', ']' };
//		for (int i = 0, size = resultArray.size(); i < size; i++) {
//			JSONObject book = resultArray.getJSONObject(i);
//			NewBook newBook = new NewBook();
//			String title = book.getStr("title");
//			String author = book.getStr("author");
//			String publisher = book.getStr("publisher");
//			String[] publisher_spilt = StrUtil.split(publisher, ",");
//			String abstract_ = book.getStr("abstract");
//			String isbn = book.getStr("isbn");
//			String price = book.getStr("price");
//
//			String biblisomtableid = book.getStr("biblisomtableid");
//			String biblisommetaid = book.getStr("biblisommetaid");
//			String ordcatamid = book.getStr("ordcatamid");
//
//			newBook.setTitle(title);
//			newBook.setAuthor(author);
//			newBook.setAbstract_self(abstract_);
//			newBook.setIsbn(isbn);
//			newBook.setPublisher_name(publisher_spilt[0]);
//			newBook.setPublisher_date(publisher_spilt[1]);
//			DateTime pdate = null;
//			String publisher_spilt_1 = null;
//			try {
//				publisher_spilt_1 = StrUtil.replaceChars(publisher_spilt[1], slash, ".");
//				pdate = DateUtil.parse(publisher_spilt[1], format);
//			} catch (Exception e) {
////				 log.error(publisher_spilt[1],e);
////				System.err.println(publisher_spilt[1]);
//				e.printStackTrace();
//				publisher_spilt_1 = StrUtil.replaceChars(publisher_spilt[1], brackets, "");
//				pdate = DateUtil.parse(publisher_spilt_1, "yyyy");
//			}
//
//			newBook.setPublisher_time(pdate);
//			newBook.setPrice(price);
//
//			newBook.setBiblisomtableid(biblisomtableid);
//			newBook.setBiblisommetaid(biblisommetaid);
//			newBook.setOrdcatamid(ordcatamid);
//
//			// 我优先关注出版年份晚于2017的
//			if (DateUtil.year(pdate) >= 2018 && DateUtil.month(pdate) > 5) {
//				String checkUrl_format = StrUtil.format(checkUrl, biblisomtableid, biblisommetaid, ordcatamid);
//				String readerAccessUrl_format = StrUtil.format(readerAccessUrl, biblisomtableid, biblisommetaid,
//						ordcatamid);
//				newBook.setCheckUrl(checkUrl_format);
//				newBook.setReaderAccessUrl(readerAccessUrl_format);
//				newBook.setAddExpressUrl(addExpressUrl);
//			}
//
//			newBooks.add(newBook);
//		}
//		return newBooks;
//	}
}
