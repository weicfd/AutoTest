package parser;

import reporter.TestingReporter;
import utils.entity.Obj;
import utils.entity.Operation;
import utils.entity.ParamPair;

import java.io.IOException;
import java.util.*;

import static config.SysConfig.ADD_ID_VISIBLE;

/**
 * 解析器
 * Created by tangmh on 18/1/18.
 */
public class Parser {
    private DataPool dataPool;
    private List<Operation> operations;
    private List<Obj> objList;
    private Stack<String> requestURLs;
    private Stack<String> requestParams;
    private Sender sender;
    private TestingReporter reporter;

    public Parser() {
        dataPool = DataPool.getInstance();
        operations = dataPool.getOperations();
        objList = new ArrayList<>();
        requestURLs = new Stack<>();
        requestParams = new Stack<>();
        sender = new Sender();
        reporter = new TestingReporter();
    }

    // 这个解析真的是...男默女泪

    /**
     * parse operation sequence
     * input: operation sequence from datapool
     * output: a list of URL requests for each case generated
     */
    public void parseOperations() throws IOException {
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
                        String params = pickAttrData(attrName, attrs);
                        Obj nObj = new Obj(i, getTheObjName(op), attrs);
                        objList.add(nObj);
                        requestURLs.push(op.getPath());
                        requestParams.push(params);
                    }
                    // 加入stack
                    break;
                case "update":
                    String locator = op.getLocator();
                    if (locator == null || locator.equals("")) System.err.println("操作没有定位符");
                    // stack中找到
                    Obj objToUpdate = null;
                    for (int j = objList.size() - 1; j > 0; j--) {
                        if (objList.get(j).getAttrs().containsKey(locator)) {
                            objToUpdate = objList.get(j);
                            break;
                        }
                    }
                    if (objToUpdate == null) {
                        // 证明这是一个无效的更新操作, 采取策略为不作为
                        continue;
                    }
                    // 修改即可
                    // 修改策略:从相应的属性值中随机选择一个
                    else {
                        requestURLs.push(op.getPath());
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String attr :
                                op.getAttrs()) {
                            stringBuilder.append(attr);
                            stringBuilder.append('=');
                            if (attr.equals(locator)) {
                                // locator的值即update对象的值
                                stringBuilder.append(objToUpdate.getAttr(locator));
                            } else {
                                String nVal = "";
                                if (dataPool.countDataNo(attr) == 1) {
                                    // 若生成数据只有一发,采取策略为不作为,即修改为相同的数据
                                    nVal = dataPool.getAttrVal(attr);
                                } else {
                                    // 更新为新的数据
                                    String temp = dataPool.getAttrVal(attr);
                                    while (nVal.equals(temp)) {
                                        temp = dataPool.getAttrVal(attr);
                                    }
                                    nVal = temp;
                                }
                                objToUpdate.putAttr(attr, nVal);
                                stringBuilder.append(nVal);
                            }
                            stringBuilder.append('&');
                        }
                        stringBuilder.deleteCharAt(stringBuilder.length()-1); // remove last '&'
                        requestParams.push(stringBuilder.toString());
                    }
                    break;
                case "delete":
                    String locator2 = op.getLocator();
                    if (locator2 == null || locator2.equals("")) System.err.println("操作没有定位符");
                    // stack中找到
                    Obj objToDelete = null;
                    for (int j = objList.size() - 1; j > 0; j--) {
                        if (objList.get(j).getAttrs().containsKey(locator2)) {
                            objToDelete = objList.get(j);
                            break;
                        }
                    }
                    if (objToDelete == null) {
                        // 证明这是一个无效的删除操作, 采取策略为不作为
                        continue;
                    }
                    requestURLs.push(op.getPath());
                    requestParams.push(copyLocatorData(objToDelete, op));
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

        // 开始测试最后一个operation的正确性
        Operation operationToTest = operations.get(n-2);
        Operation operationFind = operations.get(n-1);
        if(!operationFind.getType().equals("find")) {
            System.err.println("操作序列最后一个操作不是find");
            System.exit(0);
        }
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

        String type = operationToTest.getType();
        switch (type) {
            case "add":
                Set<String> attrName = operationToTest.getAttrs();
                Map<String, ParamPair> attrs = new HashMap<>();
                String param = pickAttrData(attrName, attrs);
                Obj nObj = new Obj(n - 1, getTheObjName(operationToTest), attrs);

                // case 1: 正确  随机取的键值对
                objList.add(nObj);
                testCase(operationToTest, param);
                objList.remove(objList.size()-1);
                if (!find(operationFind)) {
                    System.err.println(operationToTest.getName() + " case 1 failed");
                }
                // case2: 不与前ID重复 取前一个同类型的ID，顺便直接取其数据测即可， 应该无法添加，find的时候
                String id = nObj.getAttr("id");
                nObj = null;
                if (id != null) {
                    // ID可见的情况下才测试
                    for (int i = 0; i < objList.size(); i++) {
                        if (objList.get(i).getId() == nObj.getId() || objList.get(i).getAttr("id").equals(id)) nObj = objList.get(i);
                    }
                    if (nObj != null) {
                        objList.add(nObj);
                        param = copyAttrData(nObj);
                        testCase(operationToTest, param);
                        if (!find(operationFind)) System.err.println(operationToTest.getName() + " case 2 failed");
                        objList.remove(objList.size()-1);
                        break;
                    }
                }
                break;
            case "update":
                String typeToUpdate = getTheObjName(operationToTest);
                if (!getTheObjName(operationFind).equals(typeToUpdate)) {
                    System.err.println("update的操作和find的操作对象不是同一个");
                    break;
                }
                // case 3: 正确 前一共类型ID的相应值
                // 找前一个同类的obj, 获取locator,pick余下的attribute, 即oracle
                for (int i = objList.size() - 1; i >= 0; i--) {
                    if (objList.get(i).getType().equals(typeToUpdate)) {
                        Obj toUpdate = objList.get(i);
                        Obj temp = new Obj(toUpdate.getId(), toUpdate.getType(), toUpdate.getAttrs());
                        String locator = operationToTest.getLocator();
                        String locatorValue = toUpdate.getAttr(locator);
                        temp.putAttr(locator, locatorValue);
                        for (String s :
                                operationToTest.getAttrs()) {
                            if (s.equals(locator)) {
                                operationToTest.getAttrs().remove(s);
                                break;
                            }
                        }
                        param = pickAttrData(operationToTest.getAttrs(), temp.getAttrs());
                        testCase(operationToTest, param);
                        // case 5
                        if (find(operationFind, toUpdate)) System.err.println(operationToTest.getName() + " case 5 未修改原对象");
                        objList.remove(i);
                        objList.add(i, temp);
                        if (!find(operationFind)) System.err.println(operationToTest.getName() + " case 3 failed");
                        // 复原术
                        objList.remove(i);
                        objList.add(i, toUpdate);
                        break;
                    }
                }
                // case 4: 没有对应locator的修改
                attrName = operationToTest.getAttrs();
                attrs = new HashMap<>();
                param = pickAttrData(attrName, attrs);
                nObj = new Obj(n, getTheObjName(operationToTest), attrs);
                testCase(operationToTest, param);
                if (find(operationFind, nObj)) System.err.println("case 4 no way to get this error!");
                break;
            case "delete":
                String typeToDelete = getTheObjName(operationToTest);
                if (!getTheObjName(operationFind).equals(typeToDelete)) {
                    System.err.println("delete的操作和find的操作对象不是同一个");
                    break;
                }// case 6: 正确 前一共类型ID的相应值
                for (int i = objList.size() - 1; i >= 0; i--) {
                    if (objList.get(i).getType().equals(typeToDelete)) {
                        Obj toDelete = objList.get(i);
                        String locator = operationToTest.getLocator();
                        String locatorValue = toDelete.getAttr(locator);
                        param = locator + "=" + locatorValue;
                        testCase(operationToTest, param);
                        // case 8
                        if (find(operationFind, toDelete)) System.err.println(operationToTest.getName() + " case 8 未修改原对象");
                        objList.remove(i);
                        if (!find(operationFind)) System.err.println(operationToTest.getName() + " case 6 failed");
                        // 复原术
                        objList.add(i, toDelete);
                        break;
                    }
                }
                // case 7: 没有对应locator的修改
                attrName = operationToTest.getAttrs();
                attrs = new HashMap<>();
                param = pickAttrData(attrName, attrs);
                nObj = new Obj(n, getTheObjName(operationToTest), attrs);
                testCase(operationToTest, param);
                if (find(operationFind, nObj)) System.err.println("case 7 no way to get this error!");
                break;
            case "find":
                // case 9: oracle一次
                if (!find(operationToTest)) System.err.println(operationToTest.getName() + " case 9 failed");
                // case 10: 非oracle一次
                attrName = operationToTest.getAttrs();
                attrs = new HashMap<>();
                param = pickAttrData(attrName, attrs);
                nObj = new Obj(n, getTheObjName(operationToTest), attrs);
                testCase(operationToTest, param);
                if (find(operationFind, nObj)) System.err.println("case 10 no way to get this error!");
                break;
            default:
                System.err.println("无法识别的脚本operation类型");
                break;
        }
    }

    private boolean find(Operation operationFind) throws IOException {
        return find(operationFind, objList.size(), null);
    }

    private boolean find(Operation operationFind, Obj obj) throws IOException {
        return find(operationFind, 1, obj);
    }

    private boolean find(Operation operationFind, int count, Obj obj) throws IOException {
        String objName = getTheObjName(operationFind);
        for (int i = objList.size() - 1; count > 0 && i >= 0; i--) {
            if (objList.get(i).getType().equals(objName)) {
                if (obj != null && objList.get(i) != obj) continue; // 查找指定ID的对象
                count--; // count可用于指定find的次数
                // oracle
                for (String attr:
                operationFind.getAttrs()) {
                    if (operationFind.getLocator().equals(attr)) continue;
                    String oracle = objList.get(i).getAttrs().get(attr).getValue();
                    if (!test(operationFind, copyLocatorData(objList.get(i), operationFind)).equals(oracle)) {
                        System.err.println(operationFind.getName() + " testCase " + attr);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private String copyLocatorData(Obj obj, Operation op) {
        StringBuilder stringBuilder = new StringBuilder();
        String locator = op.getLocator();
        for (String attr :
                op.getAttrs()) {
            if (attr.equals(locator)) {
                // locator的值即delete对象的值
                stringBuilder.append(attr);
                stringBuilder.append('=');
                stringBuilder.append(obj.getAttr(locator));
            }
            // 只操作一个locator值,其余值忽略
        }
        return stringBuilder.toString();
    }

    private String testCase(Operation operationToTest, String param) {
        // TODO: 18/1/27 需要先清空数据库之前的数据
        sender.clear();
        requestURLs.push(operationToTest.getPath());
        requestParams.push(param);
        String response = null;
        try {
            response = sender.sendURLs(requestURLs, requestParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestURLs.pop();
        requestParams.pop();
        sender.parseResponse(response);
        return response;
    }

    private String test(Operation operationToFind, String param) throws IOException {
        // 不清除之前的数据,只查询
        List<String> urls = new ArrayList<>();
        urls.add(operationToFind.getPath());
        List<String> params = new ArrayList<>();
        params.add(param);

        String response = sender.sendURLs(urls, params);
        sender.parseResponse(response);
        return response;
    }

    private String pickAttrData(Set<String> attrName, Map<String, ParamPair> attrs) {
        StringBuilder params = new StringBuilder();
        for (Iterator<String> it = attrName.iterator(); it.hasNext();) {
            String attr = it.next();
            String data = dataPool.getAttrVal(attr);
            attrs.put(attr, new ParamPair(attr, data));
            params.append(getLastAttr(attr)); // TODO: 18/1/23  check是不是只有末位
            params.append("=");
            params.append(data);
            if (it.hasNext()) params.append("&");
        }
        return params.toString();
    }

    private String copyAttrData(Obj sampleObj) {
        StringBuilder stringbuilder = new StringBuilder();
        Map<String, ParamPair> attrs = sampleObj.getAttrs();
        for (String attr :
                attrs.keySet()) {
            stringbuilder.append(getLastAttr(attr));
            stringbuilder.append("=");
            stringbuilder.append(attrs.get(attr).getValue());
            stringbuilder.append("&");
        }
        if (stringbuilder.length() > 0) stringbuilder.deleteCharAt(stringbuilder.length() - 1);
        return stringbuilder.toString();
    }

    private String getTheObjName(Operation op) {
        // TODO: 18/1/19 暂时用一下,希望能把脚本的格式改下
        for (String attr :
                op.getAttrs()) {
            return getLastAttr(attr);
        }
        return "";
    }

    private String getLastAttr(String attrName) {
        String[] split = attrName.split("\\.");
        if (split.length > 1) return split[split.length - 2];
        else return attrName;
    }

}
