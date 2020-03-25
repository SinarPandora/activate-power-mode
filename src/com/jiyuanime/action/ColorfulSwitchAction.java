package com.jiyuanime.action;

/**
 * Created by hentai_mew on 15-12-24.
 * 彩虹色块开关
 */
public class ColorfulSwitchAction extends BaseSwitchAction {
    @Override
    boolean getSwitchFieldValue() {
        return state.IS_COLORFUL;
    }

    @Override
    void setSwitchFieldValue(boolean isEnable) {
        state.IS_COLORFUL = isEnable;
    }
}
