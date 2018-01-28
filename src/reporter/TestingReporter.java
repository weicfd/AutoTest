package reporter;

import config.SysConfig;

import java.io.File;

/**
 * 测试报告生成器
 * 输入: operation to test, testing script, testing oracle, testing result
 * 输出形式: xml | excel | json
 * Created by tangmh on 17/12/22.
 */
public class TestingReporter {
    String path = SysConfig.REPORT_PATH;
    File report;

    public TestingReporter() {
        report = new File(path);
        if (!report.exists()) System.err.println("未找到测试报告生成路径");
    }

    public void report() {
        // 生成测试报告

    }
}
