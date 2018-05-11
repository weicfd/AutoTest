package testng_version.generator;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import testng_version.Config;
import testng_version.entity.Method;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateWriter {
    VelocityEngine ve;
    String root_dir = Config.generate_test_dir;
    HashMap<Integer, String> data;


    public TemplateWriter(int fileID, List<Method> methods, String serviceName, boolean sucOrFail, String pat, HashMap<Integer, String> dataMap) {
        data = dataMap;
        ve = new VelocityEngine();
        ve.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, Config.template_file_path);
        //处理中文问题
        //        ve.setProperty(Velocity.INPUT_ENCODING,"GBK");
        //
        //        ve.setProperty(Velocity.OUTPUT_ENCODING,"GBK");
        try
        {
            //初始化模板
            ve.init();
            //获取模板(hello.html)
            Template template = ve.getTemplate("biscTest.vm");
            //获取上下文
            VelocityContext root = new VelocityContext();
            //把数据填入上下文
//            root.put("name","world");
            root.put("package_dir", Config.project_package_dir);
            root.put("test_naming", serviceName + fileID);
            root.put("group_naming", sucOrFail ? "Suc" : "Fail");
            root.put("root_url", Config.project_root_url);
            root.put("pattern", pat);

            // method list, param list, target list
            int len = methods.size();
            String[] methodList = new String[len];
            String[] paramList = new String[len];
            String[] targetList = new String[len];

            for (int i = 0; i < len; i++) {
                Method m = methods.get(i);
                methodList[i] = m.getMethodName();
                paramList[i] = convertToUrl(m.getDataCodeMap());
                targetList[i] = merge(m.getTargets());
            }

            // oracle compute

            //输出路径
            String outpath = root_dir + serviceName + '/' + serviceName + fileID + ".java";
            //输出
            Writer mywriter = new PrintWriter(new FileOutputStream(
                    new File(outpath)));
            template.merge(root, mywriter);
            mywriter.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String merge(List<String> targets) {
        StringBuilder builder = new StringBuilder();
        String con = "";
        for (String t :
                targets) {
            builder.append(con);
            con = "_"; // @note the connection is underline
            builder.append(t);
        }
        return builder.toString();
    }

    private String convertToUrl(Map<String, String> dataCodeSet) {
        StringBuilder urlBuilder = new StringBuilder();
        String con = "";
        if (!dataCodeSet.isEmpty()) {
            for (String code :
                    dataCodeSet.keySet()) {
                urlBuilder.append(con);
                con = "&";
                urlBuilder.append(dataCodeSet.get(code));
                urlBuilder.append('=');
                // TODO: 18/5/11 data generate fsm (design a new module)
                urlBuilder.append(data.get(Integer.valueOf(code)));
            }
        }
        return urlBuilder.toString();
    }
}
