package parser;

import utils.HttpRequsetUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by tangmh on 18/1/18.
 */
public class Sender {
    public String sendURLs(List<String> requestURLs, List<String> requestParams) throws IOException {
        String sr = null;
        int n = requestURLs.size();
        if (n != requestParams.size()) System.err.println("sender的参数没有对齐");
        for (int i = 0; i < n; i++) {
            sr = HttpRequsetUtil.sendPost(requestURLs.get(i), requestParams.get(i), true);
        }
        return sr;
    }

    public void parseResponse(String response) {
        // TODO: 18/1/23 处理响应
    }

    public void clear() {
        // TODO: 18/1/27 清空数据库
    }
}
