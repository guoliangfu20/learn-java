import org.junit.Test;

/**
 *
 */
public class ThreadTest {
	public static void main(String[] args) throws InterruptedException {

		// Thread thread = new Thread();
		// thread.start();
		// thread.run();


		// 线程最大优先级
		// thread.setPriority(Thread.MAX_PRIORITY);

		// thread.join(2);

	}

	@Test
	public void NotJoinTest() {

		Thread thread = new Thread(() -> {
			for (int i = 1; i < 6; i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("儿子线程睡眠：" + i + " 秒");
			}
		});

		thread.start();

		for (int i = 1; i < 4; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("主线程睡眠：" + i + " 秒");
		}

	}

	@Test
	public void JoinTest() throws InterruptedException {

		Thread thread = new Thread(() -> {
			for (int i = 1; i < 6; i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("儿子线程睡眠：" + i + " 秒");
			}
		});

		thread.start();

		thread.join(2000);

		for (int i = 1; i < 4; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("主线程睡眠：" + i + " 秒");
		}
	}

	@Test
	public void YeildTest() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 10; i++) {
					System.out.println("线程：" + Thread.currentThread().getName() + "  " + i);
					if (i == 5) {
						Thread.yield();
					}
				}
			}
		};

		Thread thread1 = new Thread(runnable, "t1");
		Thread thread2 = new Thread(runnable, "t2");
		thread1.start();
		thread2.start();
	}
}
