package com.szlib.seeker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

public class MyLibrary {
//	private static final Log log = LogFactory.get();
	public static void main(String[] args) {
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.19 Safari/537.36";
		String baseUrl = "https://www.szlib.org.cn/MyLibrary/";
		String url = "https://www.szlib.org.cn/MyLibrary/newbook.jsp?catname=%E6%B7%B1%E5%9B%BE%E6%96%B0%E4%B9%A6%E9%80%89%E8%B4%AD%E7%9B%AE%E5%BD%95&library=044005&local=2Z#";
		String host = "www.szlib.org.cn";
		String referer = "https://www.szlib.org.cn/page/newbook.html";
		String connection = "keep-alive";
		String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8";
		String result = HttpRequest.get(url).header(Header.USER_AGENT, userAgent).header(Header.HOST, host)
				.header(Header.REFERER, referer).header(Header.ACCEPT, accept).header(Header.CONNECTION, connection)
				.execute().body();

		String ajaxUrl = "proxyBasic.jsp?requestmanage/getNBbyIndex?pageIndex={}&pageSize=10&library=044005&local=2Z&catname=%E6%B7%B1%E5%9B%BE%E6%96%B0%E4%B9%A6%E9%80%89%E8%B4%AD%E7%9B%AE%E5%BD%95&_={}";
		referer = "https://www.szlib.org.cn/MyLibrary/newbook.jsp?catname=%E6%B7%B1%E5%9B%BE%E6%96%B0%E4%B9%A6%E9%80%89%E8%B4%AD%E7%9B%AE%E5%BD%95&library=044005&local=2Z";
		long current = DateUtil.current(false);
		String ajaxUrl_format = StrUtil.format(ajaxUrl, 1, current);
		url = baseUrl + ajaxUrl_format;
		accept = "application/json, text/javascript, */*";
		String x_requested_with = "XMLHttpRequest";
		result = HttpRequest.get(url).header(Header.USER_AGENT, userAgent).header(Header.HOST, host)
				.header(Header.REFERER, referer).header(Header.ACCEPT, accept).header(Header.CONNECTION, connection)
				.header("X-Requested-With", x_requested_with).execute().body();
//		System.out.println(result);

		JSONObject resultJson = JSONUtil.parseObj(result);
		int totalno = resultJson.getInt("totalno");
		int totalPage = PageUtil.totalPage(totalno, 10);
		JSONArray resultArray = resultJson.getJSONArray("result");
		List<NewBook> newBooks = json2NewBook(resultArray);
		
		for(int i=2;i<totalPage;i++) {
			current = DateUtil.current(false);
			ajaxUrl_format = StrUtil.format(ajaxUrl, i, current);
			url = baseUrl + ajaxUrl_format;
			result = HttpRequest.get(url).header(Header.USER_AGENT, userAgent).header(Header.HOST, host)
					.header(Header.REFERER, referer).header(Header.ACCEPT, accept).header(Header.CONNECTION, connection)
					.header("X-Requested-With", x_requested_with).execute().body();
			resultJson = JSONUtil.parseObj(result);
			resultArray = resultJson.getJSONArray("result");
			newBooks.addAll(json2NewBook(resultArray));
			ThreadUtil.safeSleep(RandomUtil.randomInt(1210,2018));
		}
		Comparator<NewBook> comparator = (b1, b2) -> b1.getPublisher_time().compareTo(b2.getPublisher_time());
		newBooks.sort(comparator.reversed());
		String today= DateUtil.today();
		FileWriter writer = new FileWriter(today+"深图新书选购目录.txt");
		
		newBooks.forEach(o->{
			List<String> lines = new ArrayList<>();
			lines.add("标题:"+o.getTitle());
			lines.add("作者:"+o.getAuthor());
			lines.add("价格:"+o.getPrice());
			lines.add("出版者:"+o.getPublisher_name());
			lines.add("出版年:"+o.getPublisher_date());
			lines.add("简介:"+o.getAbstract_self());	
			lines.add("能否可借:"+o.getCheckUrl());
			lines.add("读者自取:"+o.getReaderAccessUrl());
			writer.appendLines(lines);
		});
	}
	
	private static List<NewBook> json2NewBook(JSONArray resultArray){
		List<NewBook> newBooks = new ArrayList<NewBook>();
		String format = "yyyy.MM";
		String baseUrl = "https://www.szlib.org.cn/MyLibrary/";
		String checkUrl = baseUrl + "proxyBasic.jsp?requestmanage/recommendCheck?linkmetatable={}&linkmetaid={}&library=044005&local=2Z&bookrecordno={}";
		String readerAccessUrl = baseUrl + "Reader-Access.jsp?destPage=ReserveSubmit.jsp?v_Tableid={}&v_recno={}&doclibrary=044005&local=2Z&bookrecordno={}";
		String addExpressUrl = baseUrl + "Reader-Access.jsp?destPage=newbook.jsp?catname=%E6%B7%B1%E5%9B%BE%E6%96%B0%E4%B9%A6%E9%80%89%E8%B4%AD%E7%9B%AE%E5%BD%95&local=2Z&library=044005";
		
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
				 publisher_spilt_1 = StrUtil.replaceChars(publisher_spilt[1], new char[] {'/'}, ".");
				 pdate = DateUtil.parse(publisher_spilt[1], format);				
			}catch (Exception e) {
//				log.error(publisher_spilt[1],e);
				System.err.println(publisher_spilt[1]);
				e.printStackTrace();
				publisher_spilt_1 = StrUtil.replaceChars(publisher_spilt[1], new char[] {'[',']'}, "");
				pdate = DateUtil.parse(publisher_spilt_1, "yyyy");
			}
			
			newBook.setPublisher_time(pdate);
			newBook.setPrice(price);
			
			newBook.setBiblisomtableid(biblisomtableid);
			newBook.setBiblisommetaid(biblisommetaid);
			newBook.setOrdcatamid(ordcatamid);
			
			//我优先关注出版年份晚于2017的
			if(DateUtil.year(pdate)>2017) {
				String checkUrl_format = StrUtil.format(checkUrl, biblisomtableid,biblisommetaid,ordcatamid);
				String readerAccessUrl_format = StrUtil.format(readerAccessUrl, biblisomtableid,biblisommetaid,ordcatamid);
				newBook.setCheckUrl(checkUrl_format);
				newBook.setReaderAccessUrl(readerAccessUrl_format);
				newBook.setAddExpressUrl(addExpressUrl);
			}
			
			
			newBooks.add(newBook);
		}
		return newBooks;
	}
}
