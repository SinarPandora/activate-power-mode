package com.jiyuanime.listener;


import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.jiyuanime.ActivatePowerModeManage;
import com.jiyuanime.colorful.ColorFactory;
import com.jiyuanime.config.Config;
import com.jiyuanime.particle.ParticlePanel;
import com.jiyuanime.shake.ShakeManager;
import com.jiyuanime.utils.IntegerUtil;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

/**
 * 震动文本监听接口
 * Created by suika on 15-12-13.
 */
public class ActivatePowerDocumentListener implements DocumentListener {

    private final Config.State state = Config.getInstance().state;

    private Project mProject;
    private final List<Document> mDocumentList = new ArrayList<>();

    private Editor mEditor;

    private JLabel mComboLabel;

    public ActivatePowerDocumentListener(Project project) {
        mProject = project;
    }

    @Override
    public void beforeDocumentChange(DocumentEvent documentEvent) {

        ActivatePowerModeManage manage = ActivatePowerModeManage.getInstance();

        if (state.IS_COMBO) {
            // 文本变化在 CLICK_TIME_INTERVAL 时间内
            if (manage.getClickCombo() == 0 || System.currentTimeMillis() - manage.getClickTimeStamp() <= state.CLICK_TIME_INTERVAL) {
                manage.setClickCombo(manage.getClickCombo() + 1);
                state.MAX_CLICK_COMBO = Math.max(manage.getClickCombo(), state.MAX_CLICK_COMBO);
            } else
                manage.setClickCombo(0);

            manage.setClickTimeStamp(System.currentTimeMillis());
        }

        if ((state.IS_COMBO && manage.getClickCombo() > state.OPEN_FUNCTION_BORDER && mProject != null) || (!state.IS_COMBO && mProject != null))
            handleActivatePower(manage);

        if (mComboLabel != null) {
            mComboLabel.setText(String.valueOf(manage.getClickCombo()));
            if (IntegerUtil.isSizeTable(manage.getClickCombo())) {
                manage.updateComboLabelPosition(mProject);
            }
        }
    }

    @Override
    public void documentChanged(DocumentEvent documentEvent) {

        Editor selectedTextEditor = FileEditorManager.getInstance(mProject).getSelectedTextEditor();
        if (mEditor == null || mEditor != selectedTextEditor)
            mEditor = selectedTextEditor;

        if (mEditor != null) {
            try {
                Point point = mEditor.visualPositionToXY(mEditor.getCaretModel().getCurrentCaret().getSelectionEndPosition());
                ParticlePanel.getInstance().setCurrentCaretPosition(point);
            } catch (Exception ignored) {

            }
        }
    }

    public boolean addDocument(Document document) {
        if (!mDocumentList.contains(document)) {
            mDocumentList.add(document);
            return true;
        } else {
            return false;
        }
    }

    private void unbindDocument(Document document, boolean isRemoveInList) {
        if (document != null && mDocumentList.contains(document)) {
            document.removeDocumentListener(this);
            if (isRemoveInList)
                mDocumentList.remove(document);
        }
    }

    public void clean(Document document, boolean isRemoveInList) {
        cleanEditorCaret();
        if (document != null)
            unbindDocument(document, isRemoveInList);
    }

    private void cleanEditorCaret() {
        mEditor = null;
    }

    public void destroy() {
        if (mDocumentList != null) {
            for (Document document : mDocumentList)
                clean(document, false);
            mDocumentList.clear();
        }
        mProject = null;
    }

    /**
     * 处理ActivatePower效果
     */
    private void handleActivatePower(ActivatePowerModeManage manage) {
        Editor selectedTextEditor = FileEditorManager.getInstance(mProject).getSelectedTextEditor();
        if (mEditor == null || mEditor != selectedTextEditor)
            mEditor = selectedTextEditor;
        if (mEditor != null) {
            manage.resetEditor(mEditor);

            if (state.IS_SHAKE && ShakeManager.getInstance().isEnable() && !ShakeManager.getInstance().isShaking()) {
                ShakeManager.getInstance().shake();
            }

            if (state.IS_SPARK) {
                Color color;
                if (state.PARTICLE_COLOR != null) {
                    color = state.PARTICLE_COLOR;
                } else if (state.IS_COLORFUL) {
                    color = ColorFactory.gen(); // 生成一个随机颜色
                } else {
                    EditorEx editorEx = (EditorEx) mEditor;
                    EditorHighlighter editorHighlighter = editorEx.getHighlighter();
                    HighlighterIterator highlighterIterator = editorHighlighter.createIterator(mEditor.getCaretModel().getCurrentCaret().getOffset());
                    try {
                        Color fontColor = highlighterIterator.getTextAttributes().getForegroundColor();
                        color = fontColor != null ? fontColor : mEditor.getColorsScheme().getDefaultForeground();
                    } catch (IndexOutOfBoundsException ignored) {
                        color = mEditor.getColorsScheme().getDefaultForeground();
                    }
                }

                int fontSize = mEditor.getColorsScheme().getEditorFontSize();

                ParticlePanel particlePanel = ParticlePanel.getInstance();
                if (particlePanel.isEnable()) {
                    particlePanel.sparkAtPositionAction(color, fontSize);
                }
            }
        }
    }

    public void setComboLabel(JLabel comboLabel) {
        mComboLabel = comboLabel;
    }
}
