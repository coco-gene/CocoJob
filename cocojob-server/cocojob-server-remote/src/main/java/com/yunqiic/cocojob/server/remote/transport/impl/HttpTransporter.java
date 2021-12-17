package com.yunqiic.cocojob.server.remote.transport.impl;

import com.yunqiic.cocojob.common.PowerSerializable;
import com.yunqiic.cocojob.common.enums.Protocol;
import com.yunqiic.cocojob.common.RemoteConstant;
import com.yunqiic.cocojob.common.response.AskResponse;
import com.yunqiic.cocojob.common.utils.NetUtils;
import com.yunqiic.cocojob.server.remote.transport.Transporter;
import com.yunqiic.cocojob.server.remote.transport.starter.VertXStarter;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * http transporter powered by vert.x
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@Service
public class HttpTransporter implements Transporter {

    private final WebClient webClient;

    public HttpTransporter() {
        WebClientOptions options = new WebClientOptions()
                .setKeepAlive(false)
                .setConnectTimeout((int) RemoteConstant.DEFAULT_TIMEOUT_MS);
        webClient = WebClient.create(Vertx.vertx(), options);
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @Override
    public String getAddress() {
        return VertXStarter.getAddress();
    }

    @Override
    public void tell(String address, PowerSerializable object) {
        postRequest(address, object);
    }

    @Override
    public AskResponse ask(String address, PowerSerializable object) throws Exception {
        CompletableFuture<HttpResponse<Buffer>> future = postRequest(address, object).toCompletionStage().toCompletableFuture();
        HttpResponse<Buffer> httpResponse = future.get(RemoteConstant.DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        return httpResponse.bodyAsJson(AskResponse.class);
    }

    private Future<HttpResponse<Buffer>> postRequest(String address, PowerSerializable object) {
        Pair<String, Integer> ipAndPort = NetUtils.splitAddress2IpAndPort(address);
        String ip = ipAndPort.getLeft();
        int port = ipAndPort.getRight();
        return webClient.post(port, ip, object.path())
                .sendJson(object)
                .onSuccess(res -> log.info("[HttpTransporter] send request to {}{} successfully: {}, response: {}", address, object.path(), object, res))
                .onFailure(t -> log.warn("[HttpTransporter] send request to {}{} failed: {}", address, object.path(), object, t));
    }
}
