package com.kitty.blog.infrastructure.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProcessDeploymentService {

    @Autowired
    private RepositoryService repositoryService;

    @PostConstruct
    public void init() {
        deployProcessIfNotExists();
    }

    public void deployProcessIfNotExists() {
        String processKey = "reportReviewProcess";

        // 查询现有的流程定义
        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .orderByProcessDefinitionVersion()
                .desc()
                .list();

        if (definitions.isEmpty()) {
            try {
                // 部署新的流程定义
                repositoryService.createDeployment()
                        .name("举报审核流程")
                        .addClasspathResource("config/workflow/reportReviewProcess.bpmn20.xml")
                        .deploy();
                log.info("流程定义部署成功: {}", processKey);
            } catch (Exception e) {
                log.error("流程定义部署失败: {}", processKey, e);
                throw new RuntimeException("流程定义部署失败", e);
            }
        } else {
            log.info("流程定义已存在: {}, 版本: {}", processKey, definitions.get(0).getVersion());
        }
    }
}