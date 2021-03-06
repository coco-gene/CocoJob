package com.yunqiic.cocojob.worker.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.yunqiic.cocojob.worker.container.OmsContainerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.yunqiic.cocojob.common.request.*;

/**
 * Worker节点Actor，接受服务器请求
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@AllArgsConstructor
public class WorkerActor extends AbstractActor {

    private final ActorRef taskTrackerActorRef;

    public static Props props(ActorRef taskTrackerActorRef) {
        return Props.create(WorkerActor.class, () -> new WorkerActor(taskTrackerActorRef));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ServerDeployContainerRequest.class, this::onReceiveServerDeployContainerRequest)
                .match(ServerDestroyContainerRequest.class, this::onReceiveServerDestroyContainerRequest)
                .match(ServerScheduleJobReq.class, this::forward2TaskTracker)
                .match(ServerStopInstanceReq.class, this::forward2TaskTracker)
                .match(ServerQueryInstanceStatusReq.class, this::forward2TaskTracker)
                .matchAny(obj -> log.warn("[WorkerActor] receive unknown request: {}.", obj))
                .build();
    }

    private void onReceiveServerDeployContainerRequest(ServerDeployContainerRequest request) {
        OmsContainerFactory.deployContainer(request);
    }

    private void onReceiveServerDestroyContainerRequest(ServerDestroyContainerRequest request) {
        OmsContainerFactory.destroyContainer(request.getContainerId());
    }

    private void forward2TaskTracker(Object obj) {
        taskTrackerActorRef.forward(obj, getContext());
    }
}
