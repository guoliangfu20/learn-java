import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class ExecuteLinuxShell {

	public static void main(String[] args) throws Exception {

		String cmd = "sh /root/test/test.sh " + args[0] + " " + args[1];

		System.out.println(cmd);

		Process process = Runtime.getRuntime().exec(cmd);


		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String s = bufferedReader.readLine();
		while (s != null) {
			System.out.println("result--- " + s);
			s = bufferedReader.readLine();
		}

		bufferedReader.close();


		// 等待脚本执行完成
		process.waitFor(10, TimeUnit.SECONDS);

	}
}
