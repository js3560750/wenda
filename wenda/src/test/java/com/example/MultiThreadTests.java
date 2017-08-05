package com.example;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class Consumer implements Runnable{

	private BlockingQueue<String> q;
	
	public Consumer(BlockingQueue<String> q){
		this.q=q;
	}
	
	@Override
	public void run() {
		try {
			while(true){
				System.out.println(Thread.currentThread().getName()+":"+q.take());
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
		
	}
	
}

class Producer implements Runnable{
	private BlockingQueue<String> q;
	
	public Producer(BlockingQueue<String> q){
		this.q=q;
	}
	
	@Override
	public void run() {
		try {
			for (int i = 0; i < 100; i++) {
				Thread.sleep(1000);
				q.put(String.valueOf(i));
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
		
	}
}

public class MultiThreadTests {
	
	


	public static void main(String[] args) {
		
		//有一个生产者，两个消费者。生产者生成了之后停1秒再继续生成，生产的东西会立即被两个消费者轮流消费。
		BlockingQueue<String> q=new ArrayBlockingQueue<>(10);
		new Thread(new Producer(q)).start();
		Thread t1 = new Thread(new Consumer(q),"Consumer1");
		Thread t2 = new Thread(new Consumer(q),"Consumer2");
		
		//注意，可能是因为blockingqueue，即使设置两个消费者线程优先度差距很大，也是轮流着交替消费
		t1.setPriority(10);
		t2.setPriority(5);
		t1.start();
		t2.start();
		
		

	}

}
