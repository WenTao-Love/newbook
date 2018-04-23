package com.szlib.seeker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Ciep100 {
	public static void main(String[] args) throws IOException {
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3393.4 Safari/537.36";
		String firstUrl = "http://ciep.zhaopin.com/job.html";
//		String firstHtml = HttpRequest.get(firstUrl).header(Header.USER_AGENT, userAgent).execute().body();
//		//JobSearchShow
//		List<String> tables = ReUtil.findAll("<table id=\"JobSearchShow\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" spmodule=\"JobsFilter\" class=\"ajaxloading\" modperpage=\"10\" modturnpage=\"JobSearchMenu\" moddivi=\"dDataStr\" modpageclass=\"1\">(.*?)</table>", firstHtml, 1);
	
		Connection conn = Jsoup.connect(firstUrl);
		conn.header("User-Agent", userAgent);
		Document doc = conn.get();
		Elements  table = doc.getElementById("JobSearchShow").getElementsByAttribute("tbody");
		Elements trs = table.get(0).getElementsByAttribute("tr");
		List<ZLJob> jobs = new ArrayList<>();
		trs.forEach(o->{
			StringBuilder sb = new StringBuilder();
			Elements tds = o.getElementsByAttribute("td");
			
			sb.append(tds.get(0).text());
			sb.append(tds.get(1).text());
			sb.append(tds.get(2).text());
			sb.append(tds.get(4).getElementsByAttribute("href").text());
			System.out.println(sb.toString());
		});
	}
}
