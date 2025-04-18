import java.io.File;
import java.io.IOException;

/**
 * @author 13225
 * @date 2025/4/9 18:22
 */
public class StartServices {
    public static void main(String[] args) {
        /**
         * TODO 1.命令行启动SpringBoot项目
         *      2.Java启动命令行
         *      3.无关联服务异步启动
         *      4.相关联服务同步启动
         */
    }

    private static void startServices(){
        // 1. 设置相对路径（从当前目录的Spring子目录开始）
        String servicePath = "business/auth-service/src/main/java/com/czy/auth/AuthServiceApplication.java";
        File serviceDir = new File(servicePath);
        // 2. 验证路径是否存在
        if (!serviceDir.exists()) {
            System.err.println("Error: Service directory not found at " + serviceDir.getAbsolutePath());
            System.exit(1);
        }
        else {
            System.out.println("Service directory found at " + serviceDir.getAbsolutePath());
        }

        // 3. 启动服务
        startService(serviceDir);
    }

    private static void startService(File serviceDir) {
        try {
            // 创建用于启动的 ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "mvn spring-boot:run");
            processBuilder.directory(serviceDir); // 设置工作目录

            // 启动进程
            Process process = processBuilder.start();

            System.out.println("Started service: " + serviceDir.getName());
            // 可选: 输出进程的结果，可以使用 BufferedReader 读取输出流

            // 等待服务启动....这里可以添加代码来检查服务的健康状态，等待其启动成功
            // 暂时等待 10 秒（这个时间可以根据需要调整）
            Thread.sleep(10000);

            // 如果您要在这里检查服务是否启动成功，可通过 HTTP 请求等方式确认

        } catch (IOException e) {
            System.err.println("Error starting service: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Process was interrupted.");
        }
    }
}
