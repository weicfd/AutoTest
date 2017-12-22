package generator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;

/**
 * 输入脚本数据的完整生成及预期的生成
 * Created by tangmh on 17/12/22.
 */
public class InputGenerator {
    private DocumentBuilderFactory factory;

    public InputGenerator() {
        factory  = DocumentBuilderFactory.newInstance();
    }

    public void generateInput(File script) {
        if (script.exists()) {
            // 使用DOM树操作
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(script);

                Element root = document.getDocumentElement();
                // 需要设置一个param标识,标明这个File是新生成的还是已经优化过的
                if (root.getAttribute("completed").equals("true")) return;


            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
