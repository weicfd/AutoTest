package utils;

import java.io.File;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 * Created by tangmh on 18/1/17.
 */
public class Dom4jHelper {
    SAXReader reader;
    Document document;

    public Dom4jHelper(File file) {
        try {
            reader = new SAXReader();
            document = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


}
