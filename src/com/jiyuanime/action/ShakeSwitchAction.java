package com.jiyuanime.action;

/**
 * 震动开关
 * <p>
 * Created by suika on 15-12-22.
 */
public class ShakeSwitchAction extends BaseSwitchAction {

    @Override
    boolean getSwitchFieldValue() {
        return state.IS_SHAKE;
    }

    @Override
    void setSwitchFieldValue(boolean isEnable) {
        state.IS_SHAKE = isEnable;
    }
}
