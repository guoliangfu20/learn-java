import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.*;

/**
 *
 */
public class ThreadPoolExecutorTest {
	public static void main(String[] args) throws ExecutionException, InterruptedException {

		// ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor();

		ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10, 100, 10L, TimeUnit.SECONDS,
				new LinkedBlockingDeque<>(20));

		poolExecutor.execute(new Runnable() {
			@Override
			public void run() {
				System.out.println("hello execute...");
			}
		});

		System.out.println("-------------------------------");

		Future<String> future = poolExecutor.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				System.out.println("hello submiy...");
				return " success!!";
			}
		});

		System.out.println(future.get());

	}


	@Test
	public void AbortPolicyTest() {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 3,
				10, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(2),
				new ThreadPoolExecutor.AbortPolicy());

		for (int i = 0; i < 7; i++) {
			threadPoolExecutor.execute(() -> {
				System.out.println(Thread.currentThread().getName());
			});
		}
	}


	@Test
	public void RejectExecuteTest() {
		// 拒绝策略
		ThreadPoolExecutor poolExecutor1 = new ThreadPoolExecutor(1, 3,
				10, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(2),
				new RejectedExecutionHandler() {
					@Override
					public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
						System.out.println("执行自定义拒绝策略!");
					}
				});

		for (int i = 0; i < 7; i++) {
			poolExecutor1.execute(() -> {
				System.out.println(Thread.currentThread().getName());
			});
		}
	}
}
