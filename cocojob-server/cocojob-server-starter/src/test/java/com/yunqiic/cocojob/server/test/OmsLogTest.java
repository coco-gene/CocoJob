package com.yunqiic.cocojob.server.test;

import com.yunqiic.cocojob.server.common.utils.OmsFileUtils;
import com.yunqiic.cocojob.server.persistence.mongodb.GridFsManager;
import com.yunqiic.cocojob.server.core.scheduler.CleanService;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.function.Consumer;

/**
 * 在线日志测试
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */

@ActiveProfiles("daily")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OmsLogTest {

    @Resource
    private CleanService cleanService;
    @Resource
    private GridFsTemplate gridFsTemplate;

    @Test
    public void testLocalLogCleaner() {
        cleanService.cleanLocal(OmsFileUtils.genLogDirPath(), 0);
    }

    @Test
    public void testRemoteLogCleaner() {
        cleanService.cleanRemote(GridFsManager.LOG_BUCKET, 0);
    }

    @Test
    public void testGridFsQuery() {
        Query mongoQuery = Query.query(Criteria.where("uploadDate").gt(new Date()));
        gridFsTemplate.find(mongoQuery).forEach(new Consumer<GridFSFile>() {
            @Override
            public void accept(GridFSFile gridFSFile) {
                System.out.println(gridFSFile.getFilename());
            }
        });
    }
}
