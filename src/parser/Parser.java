package parser;

import utils.entity.Obj;
import utils.entity.Operation;
import utils.entity.ParamPair;

import java.util.*;

import static config.SysConfig.ADD_ID_VISIBLE;

/**
 * Created by tangmh on 18/1/18.
 */
public class Parser {
    // 这个解析真的是...男默女泪

    /**
     * parse operation sequence
     * input: operation sequence from datapool
     * output: a list of URL requests for each case generated
     */
    public void parseOperation() {
        DataPool dataPool = DataPool.getInstance();
        List<Operation> operations = dataPool.getOperations();
        Stack<Obj> stack = new Stack<>();
        Stack<String> requestURLs = new Stack<>();

        int n = operations.size();

        for (int i = 0; i < n - 2; i++) {
            // 对于前n-2个,由于序列的递增性,已经测试确认其正确性
            Operation op = operations.get(i);
            String type = op.getType();
            switch (type) {
                case "add":
                    // 创建obj
                    if (ADD_ID_VISIBLE) {
                        // TODO: 18/1/19 假定add从response中返回ID
                    } else {
                        // 假定ID是内部属性,不会直接通过ID进行改删查
                        // 直接获取Param数据
                        Set<String> attrName = op.getAttrs();
                        Map<String, ParamPair> attrs = new HashMap<>();
                        for (String attr :
                                attrName) {
                            attrs.put(attr, new ParamPair(attr, dataPool.getAttrVal(attr)));
                        }
                        // TODO: 18/1/19  这个name还没想好怎么获取
                        Obj nObj = new Obj(i, "TODO", attrs);
                        stack.add(nObj);
                    }
                    // 加入stack
                    break;
                case "update":
                    // TODO: 18/1/19 update有问题的 再次获取原来的代码?
                    // stack中找到
                    // 修改即可
                    break;
                case "delete":
                    // TODO: 18/1/19 所以要是有tmd两个同id的,你知道要删那个嘛(╯‵□′)╯︵┻━┻
                    // 找到,删除
                    break;
                case "find":
                    // 保证正确性的情况下,可跳过
                    break;
                default:
                    break;
            }
        }

        if (n < 2) {
            System.err.println("脚本操作数小于2");
            System.exit(0);
        }

        // 开始测试这个正宫operation
        Operation operationToTest = operations.get(n-2);
        // 然而问题是最后一个FIND似乎是和这步相关联的,那么最后一个FIND的类型就很关键了
        /**
         * 要测的所有pattern整理：
         ADD
         -正确  随机取的键值对
         -不与前ID重复 取前一个同类型的ID，顺便直接取其数据测即可， 应该无法添加，find的时候

         UPDATE
         -正确 前一共类型ID的相应值
         -没有对应locator的修改
         -若未找到前一类型 oracle就是预定必失败

         DELETE
         同UPDATE

         FIND 与oracle相关
         oracle一次
         非oracle一次
         */
    }

}
