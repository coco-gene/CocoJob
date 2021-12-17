package com.yunqiic.cocojob.official.processors.impl;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import com.yunqiic.cocojob.official.processors.TestUtils;

/**
 * HttpProcessorTest
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
class HttpProcessorTest {
    
    @Test
    void testDefaultMethod() throws Exception {
        String url = "https://www.baidu.com";
        JSONObject params = new JSONObject();
        params.put("url", url);
        System.out.println(new HttpProcessor().process(TestUtils.genTaskContext(params.toJSONString())));
    }

    @Test
    void testGet() throws Exception {
        String url = "https://www.baidu.com";
        JSONObject params = new JSONObject();
        params.put("url", url);
        params.put("method", "GET");

        System.out.println(new HttpProcessor().process(TestUtils.genTaskContext(params.toJSONString())));
    }

    @Test
    void testPost() throws Exception {
        String url = "https://mock.uutool.cn/4f5qfgcdahj0?test=true";
        JSONObject params = new JSONObject();
        params.put("url", url);
        params.put("method", "POST");
        params.put("mediaType", "application/json");
        params.put("body", params.toJSONString());

        System.out.println(new HttpProcessor().process(TestUtils.genTaskContext(params.toJSONString())));
    }
    
    @Test
    void testPostDefaultJson() throws Exception {
        String url = "https://mock.uutool.cn/4f5qfgcdahj0?test=true";
        JSONObject params = new JSONObject();
        params.put("url", url);
        params.put("method", "POST");
        System.out.println(new HttpProcessor().process(TestUtils.genTaskContext(params.toJSONString())));
    }
    
    @Test
    void testPostDefaultWithMediaType() throws Exception {
        String url = "https://mock.uutool.cn/4f5qfgcdahj0?test=true";
        JSONObject params = new JSONObject();
        params.put("url", url);
        params.put("method", "POST");
        params.put("mediaType", "application/json");
        System.out.println(new HttpProcessor().process(TestUtils.genTaskContext(params.toJSONString())));
    }

    @Test
    void testTimeout() throws Exception {
        String url = "http://localhost:7700/tmp/sleep";
        JSONObject params = new JSONObject();
        params.put("url", url);
        params.put("method", "GET");
        params.put("timeout", 20);
        System.out.println(new HttpProcessor().process(TestUtils.genTaskContext(params.toJSONString())));
    }
}