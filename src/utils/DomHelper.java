//package utils;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.xml.sax.SAXException;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.File;
//import java.io.IOException;
//
///**
// * Created by tangmh on 17/12/28.
// */
//public class DomHelper {
//    private DocumentBuilderFactory factory;
//    private DocumentBuilder builder;
//    private Document document;
//    private Element root;
//
//    public DomHelper(File script) {
//        try {
//            factory  = DocumentBuilderFactory.newInstance();
//            builder = factory.newDocumentBuilder();
//            document = builder.parse(script);
//            root = document.getDocumentElement();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 获取XML文件中的某一节点的某一属性
//     * @param tags 从根节点到目标节点的所有节点名字符串序列,包含根节点
//     * @param indexs 与tags相对应,表明是当前节点的第几个index
//     * @param attr 属性名
//     * @return
//     */
//    public String getAttr(String[] tags, Integer[] indexs, String attr) {
//        // TODO: 17/12/28
//        return "";
//    }
//
//    /**
//     * 若tags属性缺省,获取根节点的某一属性
//     * @param attr 属性名
//     * @return
//     */
//    public String getAttr(String attr) {
//        return root.getAttribute(attr);
//    }
//
//    public String randomPick() {
//        return null;
//    }
//}
