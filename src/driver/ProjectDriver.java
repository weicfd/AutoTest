package driver;

import analyzer.BugAnalyzer;
import generator.InputGenerator;
import parser.InputParser;
import reporter.TestingReporter;
import utils.HttpRequsetUtil;

import java.io.File;
import java.io.IOException;

/**
 * 项目驱动器
 * 驱动项目要求环境:已正在运行的server
 * 项目可调用URL实现接口调用
 * Created by tangmh on 17/12/22.
 */
public class ProjectDriver {

    // 单例模式
    private static ProjectDriver instance;
    private ProjectDriver(){};

    public static ProjectDriver getInstance() {
        if (instance == null) {
            instance = new ProjectDriver();
        }
        return instance;
    }

    /**
     * 启动项目的驱动
     * @param args
     */
    public static void main(String[] args) {
        // search for the running server

//        System.out.println("输入当前运行系统的服务器根路径");
//        Scanner in = new Scanner(System.in);
//        String server = in.nextLine();
//        in.close();
        String server = "http://localhost:8080/"; // 或许可以从已载入的配置文件中获取?

        // generator input script and data
        InputGenerator generator = new InputGenerator();
        InputParser parser = new InputParser();

        try {
            File scripts = new File(config.SysConfig.SCRIPT_PATH);
            for (File script :
                    scripts.listFiles()) {
                generator.generateInput(script); // 生成器将原脚本加工成完整的脚本文件
                parser.parseInput(script); // 在script解析时再载入测试数据
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 是否需要做一个UI:提醒用户测试完成/询问用户是否需要查看测试报告/BUG分析报告?

        // use parser to send data
        TestingReporter reporter = new TestingReporter();
        BugAnalyzer analyzer = new BugAnalyzer();

        // 假设自动打开所有报告
        reporter.report();
        analyzer.report();
        return;
    }

    /**
     * 发送接口调用请求
     * @param path
     * @param params
     * @return
     */
    public String sendAPIRequest(String path, String params) {
        try {
            String sr = HttpRequsetUtil.sendPost(path, params, true);
            return sr;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
