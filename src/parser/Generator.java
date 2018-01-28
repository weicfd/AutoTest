package parser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import utils.entity.Operation;
import utils.entity.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static config.SysConfig.DATA_PATH;

/**
 * Created by tangmh on 18/1/18.
 */
public class Generator {
    public void parseScript(File scriptFile) throws DocumentException {
        // init
        DataPool dataPool = DataPool.getInstance();
        SAXReader reader = new SAXReader();
        Document document = reader.read(scriptFile);

        Element service = document.getRootElement();
        if (dataPool.getService(service.valueOf("@id")) == null) {
            dataPool.addService(new Service(service.valueOf("@id"), service.valueOf("@name"), service.valueOf("@base")));
        }

        for (Iterator<Element> it = service.elementIterator(); it.hasNext();) {
            Element opElement = it.next();
            String opType = opElement.getName();
            // 获取操作的基本信息,构造
            Element resElement = opElement.element("resource");
            Operation op = new Operation(opType, resElement.valueOf("@name"), String.format("%s/%s", service.valueOf("@base"), resElement.valueOf("@path")));

            List<Element> params = resElement.element("request").element("param").elements();
            String locator;
            for (Element param:
                    params) {
//                List<Element> subParam = param.elements("element");
                // TODO: 18/1/19 多层次的参数
                op.addAttr(param.valueOf("@attribute"));
                if (param.valueOf("@location").equals("true")) op.setLocator(param.valueOf("@attribute"));
            }
            dataPool.add(op);
        }

    }

    public void parseData() throws DocumentException {
        // init
        DataPool dataPool = DataPool.getInstance();
        SAXReader reader = new SAXReader();

        File dataDir = new File(DATA_PATH);
        if (!dataDir.exists()) {
            System.err.println("找不到数据文件夹");
        }
        for (File serviceDir:
             dataDir.listFiles()) {
            String serviceName = serviceDir.getName();
            for (File actDir:
                 serviceDir.listFiles()) {
                String opType = actDir.getName();
                for (File opDir:
                     actDir.listFiles()) {
                    String opName = opDir.getName();
                    for (File data :
                            opDir.listFiles()) {
                        String attr = data.getName();
                        Document document = reader.read(data);
                        Element root = document.getRootElement();
                        List<String> values = new ArrayList<>();
                        for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
                            Element val = it.next();
                            values.add(val.getText());
                        }
//                        dataPool.addData(serviceName, opType, opName, attr, values);
                        dataPool.addData(root.valueOf("@attribute"), values);
                    }
                }
            }
        }

    }

//    private String findData(String serviceId, String type, String name, String paramName) throws DocumentException {
//        String path = String.format("%s/Data/%s/%s/%s/%s.xml", DATA_PATH, serviceId, type, name, paramName);
//        File dataFile = new File(path);
//        String value = "";
//        if (dataFile.exists()) {
//            SAXReader reader = new SAXReader();
//            Document document = reader.read(dataFile);
//            Element data = document.getRootElement();
//
//        } else {
//            System.err.println(String.format("%s路径错误", path));
//        }
//    }
}
