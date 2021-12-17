package com.yunqiic.cocojob.client.test;

import com.yunqiic.cocojob.client.CocoJobClient;
import org.junit.jupiter.api.BeforeAll;

/**
 * Initialize OhMyClient
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class ClientInitializer {

    protected static CocoJobClient cocoJobClient;

    @BeforeAll
    public static void initClient() throws Exception {
        cocoJobClient = new CocoJobClient("127.0.0.1:7700", "cocojob-agent-test", "123");
    }
}
