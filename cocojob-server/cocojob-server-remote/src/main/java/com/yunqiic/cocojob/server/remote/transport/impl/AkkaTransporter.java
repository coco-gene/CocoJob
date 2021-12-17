package com.yunqiic.cocojob.server.remote.transport.impl;

import akka.actor.ActorSelection;
import akka.pattern.Patterns;
import com.yunqiic.cocojob.common.PowerSerializable;
import com.yunqiic.cocojob.common.enums.Protocol;
import com.yunqiic.cocojob.common.RemoteConstant;
import com.yunqiic.cocojob.common.response.AskResponse;
import com.yunqiic.cocojob.server.remote.transport.Transporter;
import com.yunqiic.cocojob.server.remote.transport.starter.AkkaStarter;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * akka transporter
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Service
public class AkkaTransporter implements Transporter {

    @Override
    public Protocol getProtocol() {
        return Protocol.AKKA;
    }

    @Override
    public String getAddress() {
        return AkkaStarter.getActorSystemAddress();
    }

    @Override
    public void tell(String address, PowerSerializable object) {
        ActorSelection taskTrackerActor = AkkaStarter.getWorkerActor(address);
        taskTrackerActor.tell(object, null);
    }

    @Override
    public AskResponse ask(String address, PowerSerializable object) throws Exception {
        ActorSelection taskTrackerActor = AkkaStarter.getWorkerActor(address);
        CompletionStage<Object> askCS = Patterns.ask(taskTrackerActor, object, Duration.ofMillis(RemoteConstant.DEFAULT_TIMEOUT_MS));
        return  (AskResponse) askCS.toCompletableFuture().get(RemoteConstant.DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }
}
