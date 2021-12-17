package com.yunqiic.cocojob.worker.container;

import com.yunqiic.cocojob.worker.core.processor.sdk.BasicProcessor;

/**
 * CocoJob 容器规范
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public interface OmsContainer extends LifeCycle {

    /**
     * 获取处理器
     * @param className 全限定类名
     * @return 处理器（可以是 MR、BD等处理器）
     */
    BasicProcessor getProcessor(String className);

    /**
     * 获取容器的类加载器
     * @return 类加载器
     */
    OhMyClassLoader getContainerClassLoader();

    Long getContainerId();
    Long getDeployedTime();
    String getName();
    String getVersion();

    /**
     * 尝试释放容器资源
     */
    void tryRelease();
}
