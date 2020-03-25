package com.jiyuanime.action;

/**
 * 粒子开关
 * <p>
 * Created by suika on 15-12-22.
 */
public class ParticleSwitchAction extends BaseSwitchAction {
    @Override
    boolean getSwitchFieldValue() {
        return state.IS_SPARK;
    }

    @Override
    void setSwitchFieldValue(boolean isEnable) {
        state.IS_SPARK = isEnable;
    }
}
