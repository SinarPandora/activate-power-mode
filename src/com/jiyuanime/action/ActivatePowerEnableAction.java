package com.jiyuanime.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.jiyuanime.ActivatePowerModeManage;


/**
 * ActivatePower 开启 Action
 * Created by KADO on 2016/12/1.
 */
public class ActivatePowerEnableAction extends BaseSwitchAction {

    @Override
    protected void disable(AnActionEvent event) {
        super.disable(event);
        ActivatePowerModeManage.getInstance().destroyAll();
    }

    @Override
    protected void enable(AnActionEvent event) {
        super.enable(event);
        ActivatePowerModeManage.getInstance().init(event.getProject());
    }

    @Override
    protected void showEnable(Presentation presentation) {
        presentation.setIcon(AllIcons.General.InspectionsOK);
        presentation.setDescription("Enable");
        presentation.setText("Enable");
    }

    @Override
    protected void showDisable(Presentation presentation) {
        presentation.setIcon(AllIcons.Actions.Cancel);
        presentation.setDescription("Disable");
        presentation.setText("Disable");
    }

    @Override
    boolean getSwitchFieldValue() {
        return state.IS_ENABLE;
    }

    @Override
    void setSwitchFieldValue(boolean isEnable) {
        state.IS_ENABLE = isEnable;
    }
}
