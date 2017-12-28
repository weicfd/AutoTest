package generator;

import org.xml.sax.SAXException;
import utils.DomHelper;
import utils.entity.Operation;
import utils.entity.ParamPair;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 输入脚本数据的完整生成及预期的生成
 * Created by tangmh on 17/12/22.
 */
public class InputGenerator {

    public InputGenerator() {
    }

    public void generateInput(File script) {
        if (script.exists()) {
            // 使用DOM树操作
            // script file格式: /outputxml/materialinformation_Service/0/1.xml
            DomHelper scriptFile = new DomHelper(script);
            // 需要设置一个param标识,标明这个File是新生成的还是已经优化过的
            if (scriptFile.getAttr("completed").equals("true")) return;
            else {
                DataPool pool = new DataPool();
                int objNo = 0;
                Operation op = getNextOperation(scriptFile);
                // data file格式: /Data/materialinformation_Service/add/Material_New/CommonsMultipartFile.xml
                if (op.getType().equals("ADD")) {
                    objNo++;
                    List<ParamPair> params = getData(op, objNo);
                    pool.add(op, params);
                } else if (op.getType().equals("UPDATE")) {
                    ParamPair locator = op.getLocator();
                    if (locator.getValue() == null) {
                        op.generateLocator(locator); // 这步好迷啊
                    }
                    pool.modify(op, locator);
                } else if (op.getType().equals("DELETE")) {
                    ParamPair locator = op.getLocator();
                    pool.delete(op, locator);
                } else if (op.getType().equals("FIND")) {
                    ParamPair locator = op.getLocator();
                    pool.find(op, locator);
                }
            }
        }
    }

    private List<ParamPair> getData(Operation op, int objNo) {
        // TODO: 17/12/29
        return null;
    }

    private Operation getNextOperation(DomHelper scriptFile) {
        // TODO: 17/12/29
        return null;
    }
}
