package com.yunqiic.cocojob.server.remote.server;

import akka.actor.AbstractActor;
import com.yunqiic.cocojob.common.response.AskResponse;
import com.yunqiic.cocojob.common.serialize.JsonUtils;
import com.yunqiic.cocojob.server.remote.server.election.Ping;
import com.yunqiic.cocojob.server.remote.server.redirector.RemoteProcessReq;
import com.yunqiic.cocojob.server.remote.server.redirector.RemoteRequestProcessor;
import com.yunqiic.cocojob.server.remote.transport.TransportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * 处理朋友们的信息（处理服务器与服务器之间的通讯）
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
public class FriendRequestHandler extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Ping.class, this::onReceivePing)
                .match(RemoteProcessReq.class, this::onReceiveRemoteProcessReq)
                .matchAny(obj -> log.warn("[FriendActor] receive unknown request: {}.", obj))
                .build();
    }

    /**
     * 处理存活检测的请求
     */
    private void onReceivePing(Ping ping) {
        getSender().tell(AskResponse.succeed(TransportService.getAllAddress()), getSelf());
    }

    private void onReceiveRemoteProcessReq(RemoteProcessReq req) {

        AskResponse response = new AskResponse();
        response.setSuccess(true);
        try {
            response.setData(JsonUtils.toBytes(RemoteRequestProcessor.processRemoteRequest(req)));
        } catch (Throwable t) {
            log.error("[FriendActor] process remote request[{}] failed!", req, t);
            response.setSuccess(false);
            response.setMessage(ExceptionUtils.getMessage(t));
        }
        getSender().tell(response, getSelf());
    }
}
