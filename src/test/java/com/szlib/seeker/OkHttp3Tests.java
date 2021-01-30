package com.szlib.seeker;

import java.io.IOException;

import cn.hutool.core.util.StrUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttp3Tests {

	OkHttpClient client = new OkHttpClient();

	String run(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();

		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}

	public static void main(String[] args) throws IOException {
//		OkHttp3Tests example = new OkHttp3Tests();
//		String response = example.run("https://www.szlib.org.cn/MyLibrary/proxyBasic.jsp?requestmanage/getNBbyIndex?pageIndex=1&pageSize=10&v_index=&v_value=&flag=true&library=044005&local=2Z&catname=深图新书选购目录&_=1611980140338");
//		System.out.println(response);
		char[] slash = new char[] { '/','-' };
		String s = StrUtil.replaceChars("2018-05", slash, ".");
		System.out.println(s);
	}

}
