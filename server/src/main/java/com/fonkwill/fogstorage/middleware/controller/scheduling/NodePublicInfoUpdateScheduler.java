package com.fonkwill.fogstorage.middleware.controller.scheduling;

import com.fonkwill.fogstorage.middleware.controller.scheduling.task.NodePublicInfoUpdateTask;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NodePublicInfoUpdateScheduler {

    private TaskExecutor taskExecutor;

    private NodePublicInfoUpdateTask nodePublicInfoUpdateTask;

    public NodePublicInfoUpdateScheduler(TaskExecutor taskExecutor, NodePublicInfoUpdateTask nodePublicInfoUpdateTask) {
        this.taskExecutor = taskExecutor;
        this.nodePublicInfoUpdateTask = nodePublicInfoUpdateTask;
    }

    /**
     * Executes the Updater for the public information of a node every 5 minutes
     */
    @Scheduled(fixedRate = 300000)
    public void updateNodePublicInfoInDatabase() {
        taskExecutor.execute(nodePublicInfoUpdateTask);
    }




}
