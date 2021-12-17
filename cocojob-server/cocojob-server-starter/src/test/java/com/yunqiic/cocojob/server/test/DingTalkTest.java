package com.yunqiic.cocojob.server.test;

import com.yunqiic.cocojob.server.extension.defaultimpl.alram.impl.DingTalkUtils;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 测试钉钉消息工具
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class DingTalkTest {

    private static final Long AGENT_ID = 847044348L;
    private static final DingTalkUtils dingTalkUtils = new DingTalkUtils("dingauqwkvxxnqskknfv", "XWrEPdAZMPgJeFtHuL0LH73LRj-74umF2_0BFcoXMfvnX0pCQvt0rpb1JOJU_HLl");

    @Test
    public void testFetchUserId() throws Exception {
        System.out.println(dingTalkUtils.fetchUserIdByMobile("38353"));
    }

    @Test
    public void testSendMarkdown() throws Exception {
        String userId = "2159453017839770,1234";

        List<DingTalkUtils.MarkdownEntity> mds = Lists.newLinkedList();
        mds.add(new DingTalkUtils.MarkdownEntity("t111","hahahahahahahha1"));
        mds.add(new DingTalkUtils.MarkdownEntity("t2222","hahahahahahahha2"));
        mds.add(new DingTalkUtils.MarkdownEntity("t3333","hahahahahahahha3"));

        dingTalkUtils.sendMarkdownAsync("CocoJob AlarmService", mds, userId, AGENT_ID);
    }

}
