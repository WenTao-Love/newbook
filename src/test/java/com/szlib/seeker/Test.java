package com.szlib.seeker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;

public class Test {
	
	public static void main(String[] args) throws IOException {
//		long current = DateUtil.current(false);
//		System.out.println(current);
//		DateTime pdate = DateUtil.parse("2017.12.01", "yyyy.MM");
//		System.out.println(pdate);
//		FileWriter writer = new FileWriter("szlib_test.properties");
//		writer.write("test");
//		System.out.println(StrUtil.replaceChars("2015", new char[] {'[',']'}, ""));
//		String path = "config.properties";
//		InputStream in = Test.class.getResource(path).openStream();
//		URL url = Test.class.getResource(path);
//		File file = FileUtil.file(url);
//		FileWriter writer = new FileWriter(file);
//		writer.write("test");
		
//		DateTime dt = DateUtil.parseDate("2018-03-05");
//		DateTime at = DateUtil.offsetDay(dt, 90);
//		System.out.println(at);
		
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<10;i++) {
			sb.append("第").append(i).append("次");
		}
	}
}
