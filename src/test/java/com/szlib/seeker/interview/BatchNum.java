package com.szlib.seeker.interview;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import cn.hutool.core.date.DateUtil;


public class BatchNum {
	private BatchNum() {}
	private static AtomicLong atomicNum = new AtomicLong();
	
	public static String transNo(Date date) {
		StringBuffer sb = new StringBuffer();
		sb.append(DateUtil.formatDateTime(date));
		long v = atomicNum.incrementAndGet();
		String str = String.format("%08d", v);
		sb.append(str);
		return sb.toString();
	}
	
	public static void reset() {
		atomicNum.set(0);
	}
}
