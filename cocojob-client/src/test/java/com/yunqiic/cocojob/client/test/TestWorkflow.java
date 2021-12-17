package com.yunqiic.cocojob.client.test;

import com.alibaba.fastjson.JSONObject;
import com.yunqiic.cocojob.client.CocoJobClient;
import com.yunqiic.cocojob.common.enums.ExecuteType;
import com.yunqiic.cocojob.common.enums.ProcessorType;
import com.yunqiic.cocojob.common.enums.TimeExpressionType;
import com.yunqiic.cocojob.common.enums.WorkflowNodeType;
import com.yunqiic.cocojob.common.model.PEWorkflowDAG;
import com.yunqiic.cocojob.common.request.http.SaveJobInfoRequest;
import com.yunqiic.cocojob.common.request.http.SaveWorkflowNodeRequest;
import com.yunqiic.cocojob.common.request.http.SaveWorkflowRequest;
import com.yunqiic.cocojob.common.response.ResultDTO;
import com.yunqiic.cocojob.common.response.WorkflowInfoDTO;
import com.yunqiic.cocojob.common.response.WorkflowInstanceInfoDTO;
import com.yunqiic.cocojob.common.response.WorkflowNodeInfoDTO;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Test cases for {@link CocoJobClient} workflow.
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
class TestWorkflow extends ClientInitializer {

    private static final long WF_ID = 1;

    @Test
    void initTestData() {
        SaveJobInfoRequest base = new SaveJobInfoRequest();
        base.setJobName("DAG-Node-");
        base.setTimeExpressionType(TimeExpressionType.WORKFLOW);
        base.setExecuteType(ExecuteType.STANDALONE);
        base.setProcessorType(ProcessorType.BUILT_IN);
        base.setProcessorInfo("com.yunqiic.cocojob.samples.workflow.WorkflowStandaloneProcessor");

        for (int i = 0; i < 5; i++) {
            SaveJobInfoRequest request = JSONObject.parseObject(JSONObject.toJSONBytes(base), SaveJobInfoRequest.class);
            request.setJobName(request.getJobName() + i);
            ResultDTO<Long> res = cocoJobClient.saveJob(request);
            System.out.println(res);
            Assertions.assertNotNull(res);

        }
    }

    @Test
    void testSaveWorkflow() {

        SaveWorkflowRequest req = new SaveWorkflowRequest();

        req.setWfName("workflow-by-client");
        req.setWfDescription("created by client");
        req.setEnable(true);
        req.setTimeExpressionType(TimeExpressionType.API);

        System.out.println("req ->" + JSONObject.toJSON(req));
        ResultDTO<Long> res = cocoJobClient.saveWorkflow(req);
        System.out.println(res);
        Assertions.assertNotNull(res);

        req.setId(res.getData());

        // 创建节点
        SaveWorkflowNodeRequest saveWorkflowNodeRequest1 = new SaveWorkflowNodeRequest();
        saveWorkflowNodeRequest1.setJobId(1L);
        saveWorkflowNodeRequest1.setNodeName("DAG-Node-1");
        saveWorkflowNodeRequest1.setType(WorkflowNodeType.JOB);

        SaveWorkflowNodeRequest saveWorkflowNodeRequest2 = new SaveWorkflowNodeRequest();
        saveWorkflowNodeRequest2.setJobId(1L);
        saveWorkflowNodeRequest2.setNodeName("DAG-Node-2");
        saveWorkflowNodeRequest2.setType(WorkflowNodeType.JOB);


        SaveWorkflowNodeRequest saveWorkflowNodeRequest3 = new SaveWorkflowNodeRequest();
        saveWorkflowNodeRequest3.setJobId(1L);
        saveWorkflowNodeRequest3.setNodeName("DAG-Node-3");
        saveWorkflowNodeRequest3.setType(WorkflowNodeType.JOB);


        List<WorkflowNodeInfoDTO> nodeList = cocoJobClient.saveWorkflowNode(Lists.newArrayList(saveWorkflowNodeRequest1,saveWorkflowNodeRequest2,saveWorkflowNodeRequest3)).getData();
        System.out.println(nodeList);
        Assertions.assertNotNull(nodeList);


        // DAG 图
        List<PEWorkflowDAG.Node> nodes = Lists.newLinkedList();
        List<PEWorkflowDAG.Edge> edges = Lists.newLinkedList();

        nodes.add(new PEWorkflowDAG.Node(nodeList.get(0).getId()));
        nodes.add(new PEWorkflowDAG.Node(nodeList.get(1).getId()));
        nodes.add(new PEWorkflowDAG.Node(nodeList.get(2).getId()));

        edges.add(new PEWorkflowDAG.Edge(nodeList.get(0).getId(), nodeList.get(1).getId()));
        edges.add(new PEWorkflowDAG.Edge(nodeList.get(1).getId(), nodeList.get(2).getId()));
        PEWorkflowDAG peWorkflowDAG = new PEWorkflowDAG(nodes, edges);

        // 保存完整信息
        req.setDag(peWorkflowDAG);
        res = cocoJobClient.saveWorkflow(req);

        System.out.println(res);
        Assertions.assertNotNull(res);

    }

    @Test
    void testCopyWorkflow() {
        ResultDTO<Long> res = cocoJobClient.copyWorkflow(WF_ID);
        System.out.println(res);
        Assertions.assertNotNull(res);
    }


    @Test
    void testDisableWorkflow() {
        ResultDTO<Void> res = cocoJobClient.disableWorkflow(WF_ID);
        System.out.println(res);
        Assertions.assertNotNull(res);
    }

    @Test
    void testDeleteWorkflow() {
        ResultDTO<Void> res = cocoJobClient.deleteWorkflow(WF_ID);
        System.out.println(res);
        Assertions.assertNotNull(res);
    }

    @Test
    void testEnableWorkflow() {
        ResultDTO<Void> res = cocoJobClient.enableWorkflow(WF_ID);
        System.out.println(res);
        Assertions.assertNotNull(res);
    }

    @Test
    void testFetchWorkflowInfo() {
        ResultDTO<WorkflowInfoDTO> res = cocoJobClient.fetchWorkflow(WF_ID);
        System.out.println(res);
        Assertions.assertNotNull(res);
    }

    @Test
    void testRunWorkflow() {
        ResultDTO<Long> res = cocoJobClient.runWorkflow(WF_ID);
        System.out.println(res);
        Assertions.assertNotNull(res);
    }

    @Test
    void testStopWorkflowInstance() {
        ResultDTO<Void> res = cocoJobClient.stopWorkflowInstance(149962433421639744L);
        System.out.println(res);
        Assertions.assertNotNull(res);
    }

    @Test
    void testRetryWorkflowInstance() {
        ResultDTO<Void> res = cocoJobClient.retryWorkflowInstance(149962433421639744L);
        System.out.println(res);
        Assertions.assertNotNull(res);
    }

    @Test
    void testMarkWorkflowNodeAsSuccess() {
        ResultDTO<Void> res = cocoJobClient.markWorkflowNodeAsSuccess(149962433421639744L, 1L);
        System.out.println(res);
        Assertions.assertNotNull(res);
    }

    @Test
    void testFetchWfInstanceInfo() {
        ResultDTO<WorkflowInstanceInfoDTO> res = cocoJobClient.fetchWorkflowInstanceInfo(149962433421639744L);
        System.out.println(res);
        Assertions.assertNotNull(res);
    }

    @Test
    void testRunWorkflowPlus() {
        ResultDTO<Long> res = cocoJobClient.runWorkflow(WF_ID, "this is init Params 2", 90000);
        System.out.println(res);
        Assertions.assertNotNull(res);
    }
}
