package com.szlib.seeker.interview;

import java.util.Date;

import org.junit.Test;

import cn.hutool.core.date.DateUtil;

public class DidiTest {
	
	public static void main(String[] args) {
		new DidiTest().test();
	}
	
	@Test
	public void test() {
//		int x = yuesefu(13, 13);
//		System.out.println(x);
		Date now = new Date();
		Date d1 = DateUtil.offsetDay(now, -1);
		Date d2 = DateUtil.offsetDay(now, 1);
		for(int i=0;i<15;i++) {
			System.out.println(BatchNum.transNo(now));
		}
		for(int i=0;i<15;i++) {
			System.out.println(BatchNum.transNo(d1));
		}
		BatchNum.reset();
		for(int i=0;i<15;i++) {
			System.out.println(BatchNum.transNo(d2));
		}
		for(int i=0;i<15;i++) {
			System.out.println(BatchNum.transNo(now));
		}
		for(int i=0;i<15;i++) {
			System.out.println(BatchNum.transNo(d1));
		}
	}
	
	/**
	 * 约瑟夫环递推公式：f(n, m) = (f(n - 1, m) + m) % n;
	 */
	private static int yuesefu(int n, int m) {
		if (n == 1) {
			return 0; // 这里返回下标,从0开始，只有一个元素就是剩余的元素0
		} else {
			return (yuesefu(n - 1, m) + m) % n; // 我们传入的n是总共多少个数
		}
	}
}
