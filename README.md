# English | [简体中文](./README_zhCN.md)

<p align="center">
<img src="https://raw.githubusercontent.com/coco-gene/CocoJob/master/others/images/logo.png" alt="CocoJob" title="CocoJob" width="462"/>
</p>

<p align="center">
<a href="https://github.com/coco-gene/CocoJob/actions"><img src="https://github.com/coco-gene/CocoJob/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master" alt="actions"></a>
<a href="https://search.maven.org/search?q=com.yunqiic.cocojob"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/com.yunqiic.cocojob/cocojob-worker"></a>
<a href="https://github.com/coco-gene/CocoJob/releases"><img alt="GitHub release (latest SemVer)" src="https://img.shields.io/github/v/release/kfcfans/cocojob?color=%23E59866"></a>
<a href="https://github.com/coco-gene/CocoJob/blob/master/LICENSE"><img src="https://img.shields.io/github/license/coco-gene/CocoJob" alt="LICENSE"></a>
</p>

[CocoJob](https://github.com/coco-gene/CocoJob) is an open-source distributed computing and job scheduling framework which allows developers to easily schedule tasks in their own application.

Refer to [CocoJob Introduction](https://blog.yunqiic.com/cocojob/en/introduce) for detailed information.

# Introduction

### Features
- **Friendly UI:** [Front-end](https://cocojob.yunqiic.com/#/welcome?appName=cocojob-agent-test&password=123) page is provided and developers can manage their task, monitor the status, check the logs online, etc.

- **Abundant Timing Strategies:** Four timing strategies are supported, including CRON expression, fixed rate, fixed delay and OpenAPI which allows you to define your own scheduling policies, such as delaying execution.

- **Multiple Execution Mode:** Four execution modes are supported, including stand-alone, broadcast, Map and MapReduce. Distributed computing resource could be utilized in MapReduce mode, try the magic out [here](https://blog.yunqiic.com/cocojob/en/za1d96#9YOnV)!

- **Workflow(DAG) Support:** Both job dependency management and data communications between jobs are supported.

- **Extensive Processor Support:** Developers can write their processors in Java, Shell, Python, and will subsequently support multilingual scheduling via HTTP.

- **Powerful Disaster Tolerance:** As long as there are enough computing nodes, configurable retry policies make it possible for your task to be executed and finished successfully.

- **High Availability & High Performance:**  CocoJob supports unlimited horizontal expansion. It's easy to achieve high availability and performance by deploying as many CocoJob server and worker nodes.

### Applicable scenes

- Timed tasks, for example, allocating e-coupons on 9 AM every morning.
- Broadcast tasks, for example, broadcasting to the cluster to clear logs.
- MapReduce tasks, for example, speeding up certain job like updating large amounts of data.
- Delayed tasks, for example, processing overdue orders.
- Customized tasks, triggered with [OpenAPI](https://blog.yunqiic.com/cocojob/en/openapi).

### Online trial
- Address: [cocojob.yunqiic.com](https://cocojob.yunqiic.com/#/welcome?appName=cocojob-agent-test&password=123)
- Recommend reading the documentation first: [here](https://blog.yunqiic.com/cocojob/en/trial)

# Documents
**[Docs](https://blog.yunqiic.com/cocojob/en/introduce)**

**[中文文档](https://blog.yunqiic.com/cocojob/guidence/intro)**

# Known Users
[Click to register as CocoJob user!](https://github.com/coco-gene/CocoJob/issues/6)

# Stargazers over time

[![Stargazers over time](https://starchart.cc/coco-gene/CocoJob.svg)](https://starchart.cc/coco-gene/CocoJob)

# License

CocoJob is released under Apache License 2.0. Please refer to [License](./LICENSE) for details.

# Others

- Any developer interested in getting more involved in CocoJob may join our [Reddit](https://www.reddit.com/r/CocoJob) or [Gitter](https://gitter.im/CocoJob/community) community and make [contributions](https://github.com/coco-gene/CocoJob/pulls)!

- Reach out to me through email **zhangchunsheng423@gmail.com**. Any issues or questions are welcomed on [Issues](https://github.com/coco-gene/CocoJob/issues).

- Look forward to your opinions. Response may be late but not denied.
