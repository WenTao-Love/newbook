package com.szlib.seeker.interview;

/**
 *指定加锁对象:对给定对象加锁，进入同步代码前需要活的给定对象的锁。
 *直接作用于实例方法:相当于对当前实例加锁，进入同步代码前要获得当前实例的锁。
 *直接作用于静态方法:相当于对当前类加锁，进入同步代码前要获得当前类的锁。 
 *
 *线程也很珍贵，所以建议使用线程池，线程池用的很多，后续准备分享下，特别重要，需要做到心中有数。
 *给线程起名字，当线上cpu高的时候，需要用到高级jstack，如果有名称就方便很多。
 *多线程特别需要注意线程安全问题，也需要了解jdk那些是线程安全不安全，那样使用的时候不会出现莫名其妙问题。
 */
public class TestSync2 implements Runnable {

	int b = 100;

	synchronized void m1() throws InterruptedException {
		b = 1000;
		Thread.sleep(500);
		// 6
		System.out.println("b=" + b);
	}

	synchronized void m2() throws InterruptedException

	{
		Thread.sleep(250);
		// 5
		b = 2000;
	}

	public static void main(String[] args) throws InterruptedException {
		TestSync2 tt = new TestSync2();
		Thread t = new Thread(tt);
		// 1
		t.start();
		// 2
		tt.m2();
		// 3
		System.out.println("main thread b=" + tt.b);
		// 4
	}

	@Override
	public void run() {
		try {
			m1();
		} catch (InterruptedException e) {
			e.printStackTrace();

		}
	}
}