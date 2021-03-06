package com.yunqiic.cocojob.server.core.container;

import com.yunqiic.cocojob.common.OmsConstant;
import com.yunqiic.cocojob.common.enums.Protocol;
import com.yunqiic.cocojob.common.model.DeployedContainerInfo;
import com.yunqiic.cocojob.common.model.GitRepoInfo;
import com.yunqiic.cocojob.common.request.ServerDeployContainerRequest;
import com.yunqiic.cocojob.common.request.ServerDestroyContainerRequest;
import com.yunqiic.cocojob.common.utils.CommonUtils;
import com.yunqiic.cocojob.common.serialize.JsonUtils;
import com.yunqiic.cocojob.common.utils.NetUtils;
import com.yunqiic.cocojob.common.utils.SegmentLock;
import com.yunqiic.cocojob.server.common.constants.ContainerSourceType;
import com.yunqiic.cocojob.server.common.constants.SwitchableStatus;
import com.yunqiic.cocojob.server.common.utils.OmsFileUtils;
import com.yunqiic.cocojob.server.extension.LockService;
import com.yunqiic.cocojob.server.persistence.remote.model.ContainerInfoDO;
import com.yunqiic.cocojob.server.persistence.remote.repository.ContainerInfoRepository;
import com.yunqiic.cocojob.server.persistence.mongodb.GridFsManager;
import com.yunqiic.cocojob.server.remote.transport.TransportService;
import com.yunqiic.cocojob.server.remote.worker.WorkerClusterQueryService;
import com.yunqiic.cocojob.server.common.module.WorkerInfo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * ????????????
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@Service
public class ContainerService {

    @Resource
    private Environment environment;
    @Resource
    private LockService lockService;
    @Resource
    private ContainerInfoRepository containerInfoRepository;
    @Resource
    private GridFsManager gridFsManager;
    @Resource
    private TransportService transportService;

    @Resource
    private WorkerClusterQueryService workerClusterQueryService;

    // ?????????????????????
    private final SegmentLock segmentLock = new SegmentLock(4);
    // ???????????????????????????
    private static final int DEPLOY_BATCH_NUM = 50;
    // ????????????
    private static final long DEPLOY_MIN_INTERVAL = 10 * 60 * 1000;

    /**
     * ????????????
     * @param container ??????????????????
     */
    public void save(ContainerInfoDO container) {


        Long originId = container.getId();
        if (originId != null) {
            // just validate
            containerInfoRepository.findById(originId).orElseThrow(() -> new IllegalArgumentException("can't find container by id: " + originId));
        } else {
            container.setGmtCreate(new Date());
        }
        container.setGmtModified(new Date());

        // ????????????????????? sourceInfo ??????????????? md5 ??????Git????????? md5 ?????????????????????
        if (container.getSourceType() == ContainerSourceType.FatJar.getV()) {
            container.setVersion(container.getSourceInfo());
        }else {
            container.setVersion("init");
        }
        containerInfoRepository.saveAndFlush(container);
    }

    /**
     * ????????????????????? Worker ???????????? & ??????????????????
     * @param appId ??????ID????????????????????????
     * @param containerId ??????ID
     */
    public void delete(Long appId, Long containerId) {

        ContainerInfoDO container = containerInfoRepository.findById(containerId).orElseThrow(() -> new IllegalArgumentException("can't find container by id: " + containerId));

        if (!Objects.equals(appId, container.getAppId())) {
            throw new RuntimeException("Permission Denied!");
        }

        ServerDestroyContainerRequest destroyRequest = new ServerDestroyContainerRequest(container.getId());
        workerClusterQueryService.getAllAliveWorkers(container.getAppId()).forEach(workerInfo -> {
            transportService.tell(Protocol.AKKA, workerInfo.getAddress(), destroyRequest);
        });

        log.info("[ContainerService] delete container: {}.", container);
        // ?????????
        container.setStatus(SwitchableStatus.DELETED.getV());
        container.setGmtModified(new Date());
        containerInfoRepository.saveAndFlush(container);
    }

    /**
     * ?????????????????????????????? Jar ??????
     * @param file ???????????????
     * @return ???????????? md5 ???
     * @throws IOException ??????
     */
    public String uploadContainerJarFile(MultipartFile file) throws IOException {

        String workerDirStr = OmsFileUtils.genTemporaryWorkPath();
        String tmpFileStr = workerDirStr + "tmp.jar";

        File workerDir = new File(workerDirStr);
        File tmpFile = new File(tmpFileStr);

        try {
            // ???????????????
            FileUtils.forceMkdirParent(tmpFile);
            file.transferTo(tmpFile);

            // ??????MD5?????????????????????????????????
            String md5 = OmsFileUtils.md5(tmpFile);
            String fileName = genContainerJarName(md5);

            // ????????? mongoDB????????????????????????????????????????????????????????????????????????...????????????????????????????????????
            gridFsManager.store(tmpFile, GridFsManager.CONTAINER_BUCKET, fileName);

            // ?????????????????????????????????
            String finalFileStr = OmsFileUtils.genContainerJarPath() + fileName;
            File finalFile = new File(finalFileStr);
            if (finalFile.exists()) {
                FileUtils.forceDelete(finalFile);
            }
            FileUtils.moveFile(tmpFile, finalFile);

            return md5;

        }finally {
            CommonUtils.executeIgnoreException(() -> FileUtils.forceDelete(workerDir));
        }
    }

    /**
     * ?????????????????????????????? Jar ??????
     * @param version ??????
     * @return ??????Jar??????
     */
    public File fetchContainerJarFile(String version) {

        String fileName = genContainerJarName(version);
        String filePath = OmsFileUtils.genContainerJarPath() + fileName;
        File localFile = new File(filePath);

        if (localFile.exists()) {
            return localFile;
        }
        if (gridFsManager.available()) {
            downloadJarFromGridFS(fileName, localFile);
        }
        return localFile;
    }

    /**
     * ????????????
     * @param containerId ??????ID
     * @param session WebSocket Session
     * @throws Exception ??????
     */
    public void deploy(Long containerId, Session session) throws Exception {

        String deployLock = "containerDeployLock-" + containerId;
        RemoteEndpoint.Async remote = session.getAsyncRemote();
        // ?????????????????????10??????
        boolean lock = lockService.tryLock(deployLock, 10 * 60 * 1000);
        if (!lock) {
            remote.sendText("SYSTEM: acquire deploy lock failed, maybe other user is deploying, please wait until the running deploy task finished.");
            return;
        }

        try {

            Optional<ContainerInfoDO> containerInfoOpt = containerInfoRepository.findById(containerId);
            if (!containerInfoOpt.isPresent()) {
                remote.sendText("SYSTEM: can't find container by id: " + containerId);
                return;
            }
            ContainerInfoDO container = containerInfoOpt.get();

            Date lastDeployTime = container.getLastDeployTime();
            if (lastDeployTime != null) {
                if ((System.currentTimeMillis() - lastDeployTime.getTime()) < DEPLOY_MIN_INTERVAL) {
                    remote.sendText("SYSTEM: [warn] deploy too frequent, last deploy time is: " + DateFormatUtils.format(lastDeployTime, OmsConstant.TIME_PATTERN));
                }
            }

            // ????????????
            File jarFile = prepareJarFile(container, session);
            if (jarFile == null) {
                return;
            }

            double sizeMB = 1.0 * jarFile.length() / FileUtils.ONE_MB;
            remote.sendText(String.format("SYSTEM: the jarFile(size=%fMB) is prepared and ready to be deployed to the worker.", sizeMB));

            // ???????????????????????? MD5?????????????????????
            Date now = new Date();
            container.setGmtModified(now);
            container.setLastDeployTime(now);
            containerInfoRepository.saveAndFlush(container);

            // ????????????????????????????????????
            Set<String> workerAddressList = workerClusterQueryService.getAllAliveWorkers(container.getAppId())
                    .stream()
                    .map(WorkerInfo::getAddress)
                    .collect(Collectors.toSet());
            if (workerAddressList.isEmpty()) {
                remote.sendText("SYSTEM: there is no worker available now, deploy failed!");
                return;
            }

            String port = environment.getProperty("local.server.port");
            String downloadURL = String.format("http://%s:%s/container/downloadJar?version=%s", NetUtils.getLocalHost(), port, container.getVersion());
            ServerDeployContainerRequest req = new ServerDeployContainerRequest(containerId, container.getContainerName(), container.getVersion(), downloadURL);
            long sleepTime = calculateSleepTime(jarFile.length());

            AtomicInteger count = new AtomicInteger();
            workerAddressList.forEach(akkaAddress -> {
                transportService.tell(Protocol.AKKA, akkaAddress, req);

                remote.sendText("SYSTEM: send deploy request to " + akkaAddress);

                if (count.incrementAndGet() % DEPLOY_BATCH_NUM == 0) {
                    CommonUtils.executeIgnoreException(() -> Thread.sleep(sleepTime));
                }
            });

            remote.sendText("SYSTEM: deploy finished, congratulations!");

        }finally {
            lockService.unlock(deployLock);
        }
    }

    /**
     * ??????????????????
     * @param appId ??????????????????ID
     * @param containerId ??????ID
     * @return ??????????????????????????????
     */
    public String fetchDeployedInfo(Long appId, Long containerId) {
        List<DeployedContainerInfo> infoList = workerClusterQueryService.getDeployedContainerInfos(appId, containerId);

        Set<String> aliveWorkers = workerClusterQueryService.getAllAliveWorkers(appId)
                .stream()
                .map(WorkerInfo::getAddress)
                .collect(Collectors.toSet());

        Set<String> deployedList = Sets.newLinkedHashSet();
        List<String> unDeployedList = Lists.newLinkedList();
        Multimap<String, String> version2Address = ArrayListMultimap.create();
        infoList.forEach(info -> {
            String targetWorkerAddress = info.getWorkerAddress();
            if (aliveWorkers.contains(targetWorkerAddress)) {
                deployedList.add(targetWorkerAddress);
                version2Address.put(info.getVersion(), targetWorkerAddress);
            }else {
                unDeployedList.add(targetWorkerAddress);
            }
        });

        StringBuilder sb = new StringBuilder("========== DeployedInfo ==========").append(System.lineSeparator());
        // ??????????????????worker??????????????????????????????
        if (version2Address.keySet().size() > 1) {
            sb.append("WARN: there exists multi version container now, please redeploy to fix this problem").append(System.lineSeparator());
            sb.append("divisive version ==> ").append(System.lineSeparator());
            version2Address.forEach((v, addressList) -> {
                sb.append("version: ").append(v).append(System.lineSeparator());
                sb.append(addressList);
            });
            sb.append(System.lineSeparator());
        }
        // ???????????????????????????
        if (!CollectionUtils.isEmpty(unDeployedList)) {
            sb.append("WARN: there exists unDeployed worker(CocoJob will auto fix when some job need to process)").append(System.lineSeparator());
            sb.append("unDeployed worker list ==> ").append(System.lineSeparator());
        }
        // ??????????????????
        sb.append("deployed worker list ==> ").append(System.lineSeparator());
        if (CollectionUtils.isEmpty(deployedList)) {
            sb.append("no worker deployed now~");
        }else {
            sb.append(deployedList);
        }

        return sb.toString();
    }

    private File prepareJarFile(ContainerInfoDO container, Session session) throws Exception {

        RemoteEndpoint.Async remote = session.getAsyncRemote();
        // ??????Jar???Git????????? clone???Jar??????MD5???JarFile???????????????
        ContainerSourceType sourceType = ContainerSourceType.of(container.getSourceType());
        if (sourceType == ContainerSourceType.Git) {

            String workerDirStr = OmsFileUtils.genTemporaryWorkPath();
            File workerDir = new File(workerDirStr);
            FileUtils.forceMkdir(workerDir);

            try {
                // git clone
                remote.sendText("SYSTEM: start to git clone the code repo, using config: " + container.getSourceInfo());
                GitRepoInfo gitRepoInfo = JsonUtils.parseObject(container.getSourceInfo(), GitRepoInfo.class);

                CloneCommand cloneCommand = Git.cloneRepository()
                        .setDirectory(workerDir)
                        .setURI(gitRepoInfo.getRepo())
                        .setBranch(gitRepoInfo.getBranch());
                if (!StringUtils.isEmpty(gitRepoInfo.getUsername())) {
                    CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(gitRepoInfo.getUsername(), gitRepoInfo.getPassword());
                    cloneCommand.setCredentialsProvider(credentialsProvider);
                }
                cloneCommand.call();

                // ??????????????? commitId ????????????
                String oldVersion = container.getVersion();
                try (Repository repository = Git.open(workerDir).getRepository()) {
                    Ref head = repository.getRefDatabase().findRef("HEAD");
                    container.setVersion(head.getObjectId().getName());
                }

                if (container.getVersion().equals(oldVersion)) {
                    remote.sendText(String.format("SYSTEM: this commitId(%s) is the same as the last.", oldVersion));
                }else {
                    remote.sendText(String.format("SYSTEM: new version detected, from %s to %s.", oldVersion, container.getVersion()));
                }
                remote.sendText("SYSTEM: git clone successfully, star to compile the project.");

                // mvn clean package -DskipTests -U
                Invoker mvnInvoker = new DefaultInvoker();
                InvocationRequest ivkReq = new DefaultInvocationRequest();
                // -U????????????Maven????????????SNAPSHOT????????????????????????????????????????????????
                // -e?????????????????????????????????????????????Maven???????????????stack trace
                // -B??????Maven??????????????????????????????????????????????????????????????????????????????????????????????????????
                ivkReq.setGoals(Lists.newArrayList("clean", "package", "-DskipTests", "-U", "-e", "-B"));
                ivkReq.setBaseDirectory(workerDir);
                ivkReq.setOutputHandler(remote::sendText);
                ivkReq.setBatchMode(true);

                mvnInvoker.execute(ivkReq);

                String targetDirStr = workerDirStr + "/target";
                File targetDir = new File(targetDirStr);
                IOFileFilter fileFilter = FileFilterUtils.asFileFilter((dir, name) -> name.endsWith("jar-with-dependencies.jar"));
                Collection<File> jarFile = FileUtils.listFiles(targetDir, fileFilter, null);

                if (CollectionUtils.isEmpty(jarFile)) {
                    remote.sendText("SYSTEM: can't find packaged jar(maybe maven build failed), so deploy failed.");
                    return null;
                }

                File jarWithDependency = jarFile.iterator().next();

                String jarFileName = genContainerJarName(container.getVersion());

                if (!gridFsManager.exists(GridFsManager.CONTAINER_BUCKET, jarFileName)) {
                    remote.sendText("SYSTEM: can't find the jar resource in remote, maybe this is a new version, start to upload new version.");
                    gridFsManager.store(jarWithDependency, GridFsManager.CONTAINER_BUCKET, jarFileName);
                    remote.sendText("SYSTEM: upload to GridFS successfully~");
                }else {
                    remote.sendText("SYSTEM: find the jar resource in remote successfully, so it's no need to upload anymore.");
                }

                // ???????????????????????????????????????????????????
                String localFileStr = OmsFileUtils.genContainerJarPath() + jarFileName;
                File localFile = new File(localFileStr);
                if (localFile.exists()) {
                    FileUtils.forceDelete(localFile);
                }
                FileUtils.copyFile(jarWithDependency, localFile);

                return localFile;
            }finally {
                // ?????????????????????
                FileUtils.forceDelete(workerDir);
            }
        }

        // ????????????????????????????????? Jar ??????
        String jarFileName = genContainerJarName(container.getVersion());
        String localFileStr = OmsFileUtils.genContainerJarPath() + jarFileName;
        File localFile = new File(localFileStr);
        if (localFile.exists()) {
            remote.sendText("SYSTEM: find the jar file in local disk.");
            return localFile;
        }

        // ??? MongoDB ??????
        remote.sendText(String.format("SYSTEM: try to find the jarFile(%s) in GridFS", jarFileName));
        downloadJarFromGridFS(jarFileName, localFile);
        remote.sendText("SYSTEM: download jar file from GridFS successfully~");
        return localFile;
    }

    private void downloadJarFromGridFS(String mongoFileName, File targetFile) {

        int lockId = mongoFileName.hashCode();
        try {
            segmentLock.lockInterruptibleSafe(lockId);

            if (targetFile.exists()) {
                return;
            }
            if (!gridFsManager.exists(GridFsManager.CONTAINER_BUCKET, mongoFileName)) {
                log.warn("[ContainerService] can't find container's jar file({}) in gridFS.", mongoFileName);
                return;
            }
            try {
                FileUtils.forceMkdirParent(targetFile);
                gridFsManager.download(targetFile, GridFsManager.CONTAINER_BUCKET, mongoFileName);
            }catch (Exception e) {
                CommonUtils.executeIgnoreException(() -> FileUtils.forceDelete(targetFile));
                ExceptionUtils.rethrow(e);
            }

        }finally {
            segmentLock.unlock(lockId);
        }

    }

    private static String genContainerJarName(String version) {
        return String.format("oms-container-%s.jar", version);
    }

    /**
     * ?????? sleep ????????????10M??????1S + 1???
     * @param fileLength ??????????????????
     * @return sleep ??????
     */
    private long calculateSleepTime(long fileLength) {
        return (fileLength / FileUtils.ONE_MB / 10 + 1) * 1000;
    }

}
