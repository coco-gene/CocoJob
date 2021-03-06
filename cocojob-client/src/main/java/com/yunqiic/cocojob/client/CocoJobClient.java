package com.yunqiic.cocojob.client;

import com.alibaba.fastjson.JSON;
import com.yunqiic.cocojob.common.enums.InstanceStatus;
import com.yunqiic.cocojob.common.OmsConstant;
import com.yunqiic.cocojob.common.OpenAPIConstant;
import com.yunqiic.cocojob.common.exception.CocoJobException;
import com.yunqiic.cocojob.common.request.http.SaveJobInfoRequest;
import com.yunqiic.cocojob.common.request.http.SaveWorkflowNodeRequest;
import com.yunqiic.cocojob.common.request.http.SaveWorkflowRequest;
import com.yunqiic.cocojob.common.request.query.JobInfoQuery;
import com.yunqiic.cocojob.common.response.*;
import com.yunqiic.cocojob.common.utils.CommonUtils;
import com.yunqiic.cocojob.common.utils.HttpUtils;
import com.yunqiic.cocojob.common.serialize.JsonUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.yunqiic.cocojob.client.TypeStore.*;

/**
 * CocoJobClient, the client for OpenAPI.
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
public class CocoJobClient {

    private Long appId;
    private String currentAddress;
    private final List<String> allAddress;

    private static final String URL_PATTERN = "http://%s%s%s";

    /**
     * Init CocoJobClient with domain, appName and password.
     *
     * @param domain   like cocojob-server.apple-inc.com (Intranet Domain)
     * @param appName  name of the application
     * @param password password of the application
     */
    public CocoJobClient(String domain, String appName, String password) {
        this(Lists.newArrayList(domain), appName, password);
    }


    /**
     * Init CocoJobClient with server address, appName and password.
     *
     * @param addressList IP:Port address list, like 192.168.1.1:7700
     * @param appName     name of the application
     * @param password    password of the application
     */
    public CocoJobClient(List<String> addressList, String appName, String password) {

        CommonUtils.requireNonNull(addressList, "addressList can't be null!");
        CommonUtils.requireNonNull(appName, "appName can't be null");

        allAddress = addressList;
        for (String addr : addressList) {
            String url = getUrl(OpenAPIConstant.ASSERT, addr);
            try {
                String result = assertApp(appName, password, url);
                if (StringUtils.isNotEmpty(result)) {
                    ResultDTO<Long> resultDTO = JSON.parseObject(result, LONG_RESULT_TYPE);
                    if (resultDTO.isSuccess()) {
                        appId = resultDTO.getData();
                        currentAddress = addr;
                        break;
                    } else {
                        throw new CocoJobException(resultDTO.getMessage());
                    }
                }
            } catch (IOException ignore) {
                //
            }
        }

        if (StringUtils.isEmpty(currentAddress)) {
            throw new CocoJobException("no server available for CocoJobClient");
        }
        log.info("[CocoJobClient] {}'s CocoJobClient bootstrap successfully, using server: {}", appName, currentAddress);
    }

    private static String assertApp(String appName, String password, String url) throws IOException {
        FormBody.Builder builder = new FormBody.Builder()
                .add("appName", appName);
        if (password != null) {
            builder.add("password", password);
        }
        return HttpUtils.post(url, builder.build());
    }


    private static String getUrl(String path, String address) {
        return String.format(URL_PATTERN, address, OpenAPIConstant.WEB_PATH, path);
    }

    /* ************* Job ??? ************* */

    /**
     * Save one Job
     * When an ID exists in SaveJobInfoRequest, it is an update operation. Otherwise, it is a crate operation.
     *
     * @param request Job meta info
     * @return jobId
     */
    public ResultDTO<Long> saveJob(SaveJobInfoRequest request) {

        request.setAppId(appId);
        MediaType jsonType = MediaType.parse(OmsConstant.JSON_MEDIA_TYPE);
        String json = JSON.toJSONString(request);
        String post = postHA(OpenAPIConstant.SAVE_JOB, RequestBody.create(jsonType, json));
        return JSON.parseObject(post, LONG_RESULT_TYPE);
    }


    /**
     * Copy one Job
     *
     * @param jobId Job id
     * @return Id of job copy
     */
    public ResultDTO<Long> copyJob(Long jobId) {
        RequestBody body = new FormBody.Builder()
                .add("jobId", jobId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.COPY_JOB, body);
        return JSON.parseObject(post, LONG_RESULT_TYPE);
    }

    /**
     * Query JobInfo by jobId
     *
     * @param jobId jobId
     * @return Job meta info
     */
    public ResultDTO<JobInfoDTO> fetchJob(Long jobId) {
        RequestBody body = new FormBody.Builder()
                .add("jobId", jobId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.FETCH_JOB, body);
        return JSON.parseObject(post, JOB_RESULT_TYPE);
    }

    /**
     * Query all JobInfo
     *
     * @return All JobInfo
     */
    public ResultDTO<List<JobInfoDTO>> fetchAllJob() {
        RequestBody body = new FormBody.Builder()
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.FETCH_ALL_JOB, body);
        return JSON.parseObject(post, LIST_JOB_RESULT_TYPE);
    }

    /**
     * Query JobInfo by PowerQuery
     *
     * @param powerQuery JobQuery
     * @return JobInfo
     */
    public ResultDTO<List<JobInfoDTO>> queryJob(JobInfoQuery powerQuery) {
        powerQuery.setAppIdEq(appId);
        MediaType jsonType = MediaType.parse(OmsConstant.JSON_MEDIA_TYPE);
        String json = JsonUtils.toJSONStringUnsafe(powerQuery);
        String post = postHA(OpenAPIConstant.QUERY_JOB, RequestBody.create(jsonType, json));
        return JSON.parseObject(post, LIST_JOB_RESULT_TYPE);
    }

    /**
     * Disable one Job by jobId
     *
     * @param jobId jobId
     * @return Standard return object
     */
    public ResultDTO<Void> disableJob(Long jobId) {
        RequestBody body = new FormBody.Builder()
                .add("jobId", jobId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.DISABLE_JOB, body);
        return JSON.parseObject(post, VOID_RESULT_TYPE);
    }

    /**
     * Enable one job by jobId
     *
     * @param jobId jobId
     * @return Standard return object
     */
    public ResultDTO<Void> enableJob(Long jobId) {
        RequestBody body = new FormBody.Builder()
                .add("jobId", jobId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.ENABLE_JOB, body);
        return JSON.parseObject(post, VOID_RESULT_TYPE);
    }

    /**
     * Delete one job by jobId
     *
     * @param jobId jobId
     * @return Standard return object
     */
    public ResultDTO<Void> deleteJob(Long jobId) {
        RequestBody body = new FormBody.Builder()
                .add("jobId", jobId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.DELETE_JOB, body);
        return JSON.parseObject(post, VOID_RESULT_TYPE);
    }

    /**
     * Run a job once
     *
     * @param jobId          ID of the job to be run
     * @param instanceParams Runtime parameters of the job (TaskContext#instanceParams)
     * @param delayMS        Delay time???Milliseconds???
     * @return instanceId
     */
    public ResultDTO<Long> runJob(Long jobId, String instanceParams, long delayMS) {
        FormBody.Builder builder = new FormBody.Builder()
                .add("jobId", jobId.toString())
                .add("appId", appId.toString())
                .add("delay", String.valueOf(delayMS));

        if (StringUtils.isNotEmpty(instanceParams)) {
            builder.add("instanceParams", instanceParams);
        }
        String post = postHA(OpenAPIConstant.RUN_JOB, builder.build());
        return JSON.parseObject(post, LONG_RESULT_TYPE);
    }

    public ResultDTO<Long> runJob(Long jobId) {
        return runJob(jobId, null, 0);
    }

    /* ************* Instance API list ************* */

    /**
     * Stop one job instance
     *
     * @param instanceId instanceId
     * @return Standard return object
     */
    public ResultDTO<Void> stopInstance(Long instanceId) {
        RequestBody body = new FormBody.Builder()
                .add("instanceId", instanceId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.STOP_INSTANCE, body);
        return JSON.parseObject(post, VOID_RESULT_TYPE);
    }

    /**
     * Cancel a job instance that is not yet running
     * Notice???There is a time interval between the call interface time and the expected execution time of the job instance to be cancelled, otherwise reliability is not guaranteed
     *
     * @param instanceId instanceId
     * @return Standard return object
     */
    public ResultDTO<Void> cancelInstance(Long instanceId) {
        RequestBody body = new FormBody.Builder()
                .add("instanceId", instanceId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.CANCEL_INSTANCE, body);
        return JSON.parseObject(post, VOID_RESULT_TYPE);
    }

    /**
     * Retry failed job instance
     * Notice: Only job instance with completion status (success, failure, manually stopped, cancelled) can be retried, and retries of job instances within workflows are not supported yet.
     *
     * @param instanceId instanceId
     * @return Standard return object
     */
    public ResultDTO<Void> retryInstance(Long instanceId) {
        RequestBody body = new FormBody.Builder()
                .add("instanceId", instanceId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.RETRY_INSTANCE, body);
        return JSON.parseObject(post, VOID_RESULT_TYPE);
    }

    /**
     * Query status about a job instance
     *
     * @param instanceId instanceId
     * @return {@link InstanceStatus}
     */
    public ResultDTO<Integer> fetchInstanceStatus(Long instanceId) {
        RequestBody body = new FormBody.Builder()
                .add("instanceId", instanceId.toString())
                .build();
        String post = postHA(OpenAPIConstant.FETCH_INSTANCE_STATUS, body);
        return JSON.parseObject(post, INTEGER_RESULT_TYPE);
    }

    /**
     * Query detail about a job instance
     *
     * @param instanceId instanceId
     * @return instance detail
     */
    public ResultDTO<InstanceInfoDTO> fetchInstanceInfo(Long instanceId) {
        RequestBody body = new FormBody.Builder()
                .add("instanceId", instanceId.toString())
                .build();
        String post = postHA(OpenAPIConstant.FETCH_INSTANCE_INFO, body);
        return JSON.parseObject(post, INSTANCE_RESULT_TYPE);
    }

    /* ************* Workflow API list ************* */

    /**
     * Save one workflow
     * When an ID exists in SaveWorkflowRequest, it is an update operation. Otherwise, it is a crate operation.
     *
     * @param request Workflow meta info
     * @return workflowId
     */
    public ResultDTO<Long> saveWorkflow(SaveWorkflowRequest request) {
        request.setAppId(appId);
        MediaType jsonType = MediaType.parse(OmsConstant.JSON_MEDIA_TYPE);
        // ?????????????????? FastJSON ?????????????????? Server ????????? pEWorkflowDAG ??? null?????????.jpg
        String json = JsonUtils.toJSONStringUnsafe(request);
        String post = postHA(OpenAPIConstant.SAVE_WORKFLOW, RequestBody.create(jsonType, json));
        return JSON.parseObject(post, LONG_RESULT_TYPE);
    }

    /**
     * Copy one workflow
     *
     * @param workflowId Workflow id
     * @return Id of workflow copy
     */
    public ResultDTO<Long> copyWorkflow(Long workflowId) {
        RequestBody body = new FormBody.Builder()
                .add("workflowId", workflowId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.COPY_WORKFLOW, body);
        return JSON.parseObject(post, LONG_RESULT_TYPE);
    }


    /**
     * ?????????????????????
     *
     * @param requestList Node info list of Workflow
     * @return Standard return object
     */
    public ResultDTO<List<WorkflowNodeInfoDTO>> saveWorkflowNode(List<SaveWorkflowNodeRequest> requestList) {
        for (SaveWorkflowNodeRequest saveWorkflowNodeRequest : requestList) {
            saveWorkflowNodeRequest.setAppId(appId);
        }
        MediaType jsonType = MediaType.parse(OmsConstant.JSON_MEDIA_TYPE);
        String json = JsonUtils.toJSONStringUnsafe(requestList);
        String post = postHA(OpenAPIConstant.SAVE_WORKFLOW_NODE, RequestBody.create(jsonType, json));
        return JSON.parseObject(post, WF_NODE_LIST_RESULT_TYPE);
    }



    /**
     * Query Workflow by workflowId
     *
     * @param workflowId workflowId
     * @return Workflow meta info
     */
    public ResultDTO<WorkflowInfoDTO> fetchWorkflow(Long workflowId) {
        RequestBody body = new FormBody.Builder()
                .add("workflowId", workflowId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.FETCH_WORKFLOW, body);
        return JSON.parseObject(post, WF_RESULT_TYPE);
    }

    /**
     * Disable Workflow by workflowId
     *
     * @param workflowId workflowId
     * @return Standard return object
     */
    public ResultDTO<Void> disableWorkflow(Long workflowId) {
        RequestBody body = new FormBody.Builder()
                .add("workflowId", workflowId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.DISABLE_WORKFLOW, body);
        return JSON.parseObject(post, VOID_RESULT_TYPE);
    }

    /**
     * Enable Workflow by workflowId
     *
     * @param workflowId workflowId
     * @return Standard return object
     */
    public ResultDTO<Void> enableWorkflow(Long workflowId) {
        RequestBody body = new FormBody.Builder()
                .add("workflowId", workflowId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.ENABLE_WORKFLOW, body);
        return JSON.parseObject(post, VOID_RESULT_TYPE);
    }

    /**
     * Delete Workflow by workflowId
     *
     * @param workflowId workflowId
     * @return Standard return object
     */
    public ResultDTO<Void> deleteWorkflow(Long workflowId) {
        RequestBody body = new FormBody.Builder()
                .add("workflowId", workflowId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.DELETE_WORKFLOW, body);
        return JSON.parseObject(post, VOID_RESULT_TYPE);
    }

    /**
     * Run a workflow once
     *
     * @param workflowId workflowId
     * @param initParams workflow startup parameters
     * @param delayMS    Delay time???Milliseconds???
     * @return workflow instanceId
     */
    public ResultDTO<Long> runWorkflow(Long workflowId, String initParams, long delayMS) {
        FormBody.Builder builder = new FormBody.Builder()
                .add("workflowId", workflowId.toString())
                .add("appId", appId.toString())
                .add("delay", String.valueOf(delayMS));
        if (StringUtils.isNotEmpty(initParams)) {
            builder.add("initParams", initParams);
        }
        String post = postHA(OpenAPIConstant.RUN_WORKFLOW, builder.build());
        return JSON.parseObject(post, LONG_RESULT_TYPE);
    }

    public ResultDTO<Long> runWorkflow(Long workflowId) {
        return runWorkflow(workflowId, null, 0);
    }

    /* ************* Workflow Instance API list ************* */

    /**
     * Stop one workflow instance
     *
     * @param wfInstanceId workflow instanceId
     * @return Standard return object
     */
    public ResultDTO<Void> stopWorkflowInstance(Long wfInstanceId) {
        RequestBody body = new FormBody.Builder()
                .add("wfInstanceId", wfInstanceId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.STOP_WORKFLOW_INSTANCE, body);
        return JSON.parseObject(post, VOID_RESULT_TYPE);
    }

    /**
     * Retry one workflow instance
     *
     * @param wfInstanceId workflow instanceId
     * @return Standard return object
     */
    public ResultDTO<Void> retryWorkflowInstance(Long wfInstanceId) {
        RequestBody body = new FormBody.Builder()
                .add("wfInstanceId", wfInstanceId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.RETRY_WORKFLOW_INSTANCE, body);
        return JSON.parseObject(post, VOID_RESULT_TYPE);
    }

    /**
     * mark the workflow node as success
     *
     * @param wfInstanceId workflow instanceId
     * @param nodeId       node id
     * @return Standard return object
     */
    public ResultDTO<Void> markWorkflowNodeAsSuccess(Long wfInstanceId, Long nodeId) {
        RequestBody body = new FormBody.Builder()
                .add("wfInstanceId", wfInstanceId.toString())
                .add("nodeId", nodeId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.MARK_WORKFLOW_NODE_AS_SUCCESS, body);
        return JSON.parseObject(post, VOID_RESULT_TYPE);
    }

    /**
     * Query detail about a workflow instance
     *
     * @param wfInstanceId workflow instanceId
     * @return detail about a workflow
     */
    public ResultDTO<WorkflowInstanceInfoDTO> fetchWorkflowInstanceInfo(Long wfInstanceId) {
        RequestBody body = new FormBody.Builder()
                .add("wfInstanceId", wfInstanceId.toString())
                .add("appId", appId.toString())
                .build();
        String post = postHA(OpenAPIConstant.FETCH_WORKFLOW_INSTANCE_INFO, body);
        return JSON.parseObject(post, WF_INSTANCE_RESULT_TYPE);
    }


    private String postHA(String path, RequestBody requestBody) {

        // ?????????????????????
        String url = getUrl(path, currentAddress);
        try {
            String res = HttpUtils.post(url, requestBody);
            if (StringUtils.isNotEmpty(res)) {
                return res;
            }
        } catch (IOException e) {
            log.warn("[CocoJobClient] request url:{} failed, reason is {}.", url, e.toString());
        }

        // ?????????????????????
        for (String addr : allAddress) {
            if (Objects.equals(addr, currentAddress)) {
                continue;
            }
            url = getUrl(path, addr);
            try {
                String res = HttpUtils.post(url, requestBody);
                if (StringUtils.isNotEmpty(res)) {
                    log.warn("[CocoJobClient] server change: from({}) -> to({}).", currentAddress, addr);
                    currentAddress = addr;
                    return res;
                }
            } catch (IOException e) {
                log.warn("[CocoJobClient] request url:{} failed, reason is {}.", url, e.toString());
            }
        }

        log.error("[CocoJobClient] do post for path: {} failed because of no server available in {}.", path, allAddress);
        throw new CocoJobException("no server available when send post request");
    }
}
