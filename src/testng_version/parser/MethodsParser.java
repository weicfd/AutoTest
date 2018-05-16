package testng_version.parser;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import testng_version.Config;
import testng_version.entity.Method;
import testng_version.entity.Service;
import testng_version.fsm.DataCodeConverter;

import java.io.File;
import java.util.*;

public class MethodsParser {
    final int mADD = 1, mUPDATE = 2, mDELETE = 3, mFIND = 4, mUNKNOWN = 0;
    Service service;
    String methodName;
    String entityName; // used to find the related methods, 暂不考虑一个method对应多个entity的情况
    Map<Integer, String> dataCodeMap; // parameter
    List<String> targetSet;

    public MethodsParser(String resourcePath) {
        // use dom4j to parse it
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(new File(Config.service_xml_dir + resourcePath)); // 读取XML文件,获得document对象
            Element script = document.getRootElement();
            String serviceName = script.attributeValue("resourcesID");
            service = new Service(serviceName);
            int locatorCode = 0;

            // read the info of method
            // methodID entityCode and dataCodeList
            for (Iterator<Element> iterator = script.elements().iterator(); iterator.hasNext();) {
                Element method = iterator.next();
                String mName = method.attributeValue("path");
                // todo: codeSet need the parameter name?
                dataCodeMap = new HashMap<>();
                // 采用xpath查找需要引入jaxen-xx-xx.jar，否则会报java.lang.NoClassDefFoundError: org/jaxen/JaxenException异常?
                List<Node> params = method.selectNodes("param//element1");
                for (Node param :
                        params) {
                    // TODO: 18/5/11 deal with the problem of level
                    Element element = (Element)param;
                    int code = DataCodeConverter.castAttrToCode(mName, element.attributeValue("attribute"));
                    dataCodeMap.put(code,
                            element.attributeValue("name"));
                    if (element.attributeValue("location").equals("true")) locatorCode = code;
                }

                targetSet = new ArrayList<>();
//                List<Node> targets = method.selectNodes("resource/request/param//element");
//                for (Node target :
//                        targets) {
//                    Element e = (Element) target;
//                    targetSet.add(e.attributeValue("attribute"));
//                }


                service.addMethod(new Method(mName, convertAction(method.attributeValue("operation")), dataCodeMap, targetSet, locatorCode));

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private int convertAction(String name) {
        switch (name) {
            case "add":
                return mADD;
            case "update":
                return mUPDATE;
            case "delete":
                return mDELETE;
            case "find":
                return mFIND;
        }
        return mUNKNOWN;
    }

    public Service getService() {
        return service;
    }
}
