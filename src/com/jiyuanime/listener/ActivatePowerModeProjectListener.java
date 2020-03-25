package com.jiyuanime.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.jiyuanime.ActivatePowerModeManage;
import com.jiyuanime.config.Config;
import org.jetbrains.annotations.NotNull;

/**
 * Author: sinar
 * 2020/3/23 20:57
 */
public class ActivatePowerModeProjectListener implements ProjectManagerListener {

    private Config.State state = Config.getInstance().state;

    @Override
    public void projectOpened(@NotNull Project project) {
        if (state.IS_ENABLE) {
            ActivatePowerModeManage.getInstance().init(project);
        }
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        ActivatePowerModeManage.getInstance().destroy(project, true);
    }
}
