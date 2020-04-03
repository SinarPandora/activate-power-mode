package com.jiyuanime;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.util.messages.MessageBusConnection;
import com.jiyuanime.config.Config;
import com.jiyuanime.listener.ActivatePowerDocumentListener;
import com.jiyuanime.particle.ParticlePanel;
import com.jiyuanime.shake.ShakeManager;
import com.jiyuanime.utils.IntegerUtil;

import org.jetbrains.annotations.NotNull;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Timer;

import javax.swing.*;

/**
 * 效果管理器
 * Created by KADO on 2017/1/11.
 */
public class ActivatePowerModeManage {

    private static ActivatePowerModeManage sActivatePowerModeManage;

    public static ActivatePowerModeManage getInstance() {
        if (sActivatePowerModeManage == null) {
            sActivatePowerModeManage = new ActivatePowerModeManage();
        }
        return sActivatePowerModeManage;
    }

    private Config.State state = Config.getInstance().state;

    private HashMap<Project, ActivatePowerDocumentListener> mDocListenerMap = new HashMap<>();
    private Map<String, MessageBusConnection> activeConnections = new HashMap<>();
    private Editor mCurrentEditor;

    private long mClickTimeStamp;
    private int mClickCombo;

    private JLabel mComboLabel, mMaxComboLabel;
    private JPanel mComboPanel;
    private JProgressBar mClickTimeStampProgressBar;

    public void init(Project project) {

        if (project != null) {
            // 监听FileEditor的状态
            MessageBusConnection connection = project.getMessageBus().connect();
            this.activeConnections.put(project.getBasePath(), connection);
            connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
                @Override
                public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                    destroyShake();
                    destroyParticle();
                    mCurrentEditor = null;

                    initDocument(source.getProject(), FileDocumentManager.getInstance().getDocument(file));
                }

                @Override
                public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                    ActivatePowerDocumentListener activatePowerDocumentListener = mDocListenerMap.get(source.getProject());
                    if (activatePowerDocumentListener != null)
                        activatePowerDocumentListener.clean(FileDocumentManager.getInstance().getDocument(file), true);
                }

                @Override
                public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                    if (state.IS_ENABLE) {
                        destroyShake();
                        destroyParticle();
                        mCurrentEditor = null;

                        FileEditorManager fileEditorManager = event.getManager();
                        VirtualFile virtualFile = event.getNewFile();
                        if (virtualFile != null)
                            initDocument(fileEditorManager.getProject(), FileDocumentManager.getInstance().getDocument(virtualFile));
                    }
                }
            });

            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

            if (fileEditorManager != null) {
                Editor editor = fileEditorManager.getSelectedTextEditor();
                if (editor != null) {
                    destroyShake();
                    destroyParticle();
                    mCurrentEditor = null;

                    initDocument(project, editor.getDocument());
                }
            }

        } else
            System.out.println("ActivatePowerEnableAction " + "初始化数据失败");

    }

    private void initEditor(Editor editor) {
        if (editor != null) {
            initShake(editor.getComponent());
            initParticle(editor.getContentComponent());
        }
    }

    private void initDocument(Project project, Document document) {
        if (project != null && document != null) {
            ActivatePowerDocumentListener activatePowerDocumentListener = mDocListenerMap.get(project);
            if (activatePowerDocumentListener == null) {
                activatePowerDocumentListener = new ActivatePowerDocumentListener(project);
                mDocListenerMap.put(project, activatePowerDocumentListener);
            }
            if (activatePowerDocumentListener.addDocument(document))
                document.addDocumentListener(activatePowerDocumentListener);

            initComboView(project);
        }
    }

    public void initComboView(Project project) {
        if (project != null && state.IS_COMBO) {
            if (mMaxComboLabel == null)
                mMaxComboLabel = initMaxComboLabel();
            if (mComboLabel == null)
                mComboLabel = initComboLabel();
            if (mComboPanel == null)
                mComboPanel = initComboPanel();
            if (mClickTimeStampProgressBar == null)
                mClickTimeStampProgressBar = initClickTimeStampProgressBar();

            Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (selectedTextEditor != null) {
                JComponent contentJComponent = selectedTextEditor.getContentComponent();

                JViewport jvp = (JViewport) contentJComponent.getParent();
                jvp.addChangeListener(e -> addComboLabel(contentJComponent, -contentJComponent.getX(), -contentJComponent.getY()));

                addComboLabel(contentJComponent, -contentJComponent.getX(), -contentJComponent.getY());
            }

            ActivatePowerDocumentListener activatePowerDocumentListener = mDocListenerMap.get(project);
            if (activatePowerDocumentListener != null) {
                activatePowerDocumentListener.setComboLabel(mComboLabel);
            }
        }
    }

    public void destroy(Project project, boolean isRemoveProject) {
        destroyShake();
        destroyParticle();
        destroyDocumentListener(project, isRemoveProject);
        mCurrentEditor = null;
        destroyProjectMessageBus(project, isRemoveProject);
    }

    public void destroyAll() {
        for (Project project : mDocListenerMap.keySet()) {
            destroy(project, false);
        }
        mDocListenerMap.clear();
    }

    private void initShake(JComponent jComponent) {
        // Config.State state = Config.getInstance().state;
        if (state.IS_SHAKE && ShakeManager.getInstance().getNowEditorJComponent() != jComponent) {
            ShakeManager.getInstance().reset(jComponent);
        }
    }

    private void initParticle(JComponent jContentComponent) {
        // Config.State state = Config.getInstance().state;
        if (state.IS_SPARK && ParticlePanel.getInstance().getNowEditorJComponent() != jContentComponent) {
            ParticlePanel.getInstance().reset(jContentComponent);
            jContentComponent.setBorder(ParticlePanel.getInstance());
        }
    }

    private JPanel initComboPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(null);
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        return panel;
    }

    private JLabel initComboLabel() {
        JLabel comboLabel = new JLabel("0");
        comboLabel.setHorizontalAlignment(SwingConstants.CENTER);
        comboLabel.setBackground(new JBColor(new Color(0x00FFFFFF, true), new Color(0x00FFFFFF, true)));
        comboLabel.setForeground(new JBColor(new Color(0xDD00E600, true), new Color(0xDD00E600, true)));

        try {
            Font font = initFile().deriveFont(Font.BOLD, 64f);
            comboLabel.setFont(font);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            comboLabel.setFont(new Font("Default", Font.BOLD, 64));
        }

        return comboLabel;
    }

    private Font initFile() throws IOException, FontFormatException {
        InputStream fontInputStream;

        if (Objects.isNull(state.FONT_FILE_LOCATION)) {
            fontInputStream = getClass().getResourceAsStream("/font/PressStart2P-Regular.ttf");
        } else {
            File fontFile = new File(state.FONT_FILE_LOCATION);
            if (fontFile.exists()) {
                fontInputStream = new FileInputStream(fontFile);
            } else {
                throw new IOException("Cannot load font file: " + state.FONT_FILE_LOCATION);
            }
        }

        return Font.createFont(Font.TRUETYPE_FONT, fontInputStream);
    }

    private JLabel initMaxComboLabel() {
        JLabel comboLabel = new JLabel("Max " + Config.getInstance().state.MAX_CLICK_COMBO);
        comboLabel.setHorizontalAlignment(SwingConstants.CENTER);
        comboLabel.setBackground(new JBColor(new Color(0x00FFFFFF, true), new Color(0x00FFFFFF, true)));
        comboLabel.setForeground(new JBColor(new Color(0xDD00E600, true), new Color(0xDD00E600, true)));

        try {
            Font font = initFile();
            font = font.deriveFont(Font.BOLD, 24f);
            comboLabel.setFont(font);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            comboLabel.setFont(new Font("Default", Font.BOLD, 24));
        }

        return comboLabel;
    }

    private JProgressBar initClickTimeStampProgressBar() {
        JProgressBar clickTimeStampProgressBar = new JProgressBar();
        clickTimeStampProgressBar.setForeground(JBColor.GREEN);
        clickTimeStampProgressBar.setVisible(false);

        return clickTimeStampProgressBar;
    }

    private void addComboLabel(JComponent contentComponent, int x, int y) {
        if (contentComponent != null && contentComponent.getParent() != null && mComboPanel != null && mMaxComboLabel != null && mComboLabel != null && mClickTimeStampProgressBar != null) {

            mMaxComboLabel.setText("Max " + Config.getInstance().state.MAX_CLICK_COMBO);

            mComboPanel.remove(mMaxComboLabel);
            mComboPanel.remove(mComboLabel);
            mComboPanel.remove(mClickTimeStampProgressBar);
            mComboPanel.add(mMaxComboLabel, BorderLayout.NORTH);
            mComboPanel.add(mComboLabel, BorderLayout.CENTER);
            mComboPanel.add(mClickTimeStampProgressBar, BorderLayout.SOUTH);

            contentComponent.setLayout(new FlowLayout(FlowLayout.LEFT, (int) (x + contentComponent.getParent().getWidth() - mComboPanel.getPreferredSize().getWidth() - 32), y + 32));

            contentComponent.remove(mComboPanel);
            contentComponent.add(mComboPanel);
        }
    }

    public void updateComboLabelPosition(Project project) {
        Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (selectedTextEditor != null) {
            JComponent contentJComponent = selectedTextEditor.getContentComponent();
            addComboLabel(contentJComponent, -contentJComponent.getX(), -contentJComponent.getY());
        }
    }

    public void clearComboView(Project project) {

        Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (selectedTextEditor != null) {
            JComponent contentJComponent = selectedTextEditor.getContentComponent();
            contentJComponent.remove(mComboPanel);
        }

        mComboLabel = null;
        mMaxComboLabel = null;
        mComboPanel = null;
        mClickTimeStampProgressBar = null;

        for (Project p : mDocListenerMap.keySet()) {
            ActivatePowerDocumentListener activatePowerDocumentListener = mDocListenerMap.get(p);
            if (activatePowerDocumentListener != null) {
                activatePowerDocumentListener.setComboLabel(null);
            }
        }
    }

    private Timer mClickTimeStampTimer;
    private ClickTimeStampTimerTask mClickTimeStampTimerTask;

    private void updateClickTimeStamp() {
        if (mClickTimeStampTimer == null)
            mClickTimeStampTimer = new Timer();
        if (mClickTimeStampTimerTask == null)
            mClickTimeStampTimerTask = new ClickTimeStampTimerTask();
        mClickTimeStampTimer.schedule(mClickTimeStampTimerTask, 0, ClickTimeStampTimerTask.CLICK_TIME_INTERVAL);
    }

    private void cancelClickTimeStamp() {
        if (mClickTimeStampTimer != null) {
            mClickTimeStampTimer.cancel();
            mClickTimeStampTimer.purge();
        }
        mClickTimeStampTimerTask = null;
        mClickTimeStampTimer = null;
    }

    public void resetEditor(Editor editor) {
        if (mCurrentEditor != editor) {
            mCurrentEditor = editor;
            initEditor(mCurrentEditor);
        }
    }

    private void destroyDocumentListener(Project project, boolean isRemoveProject) {
        ActivatePowerDocumentListener activatePowerDocumentListener = mDocListenerMap.get(project);
        if (activatePowerDocumentListener != null) {
            activatePowerDocumentListener.destroy();
            if (isRemoveProject)
                mDocListenerMap.remove(project);
        }
    }

    private void destroyShake() {
        ShakeManager.getInstance().destroy();
    }

    private void destroyParticle() {
        ParticlePanel.getInstance().destroy();
    }

    private void destroyProjectMessageBus(Project project, boolean isRemoveProject) {
        if (project != null && !project.isDisposed()) {
            Optional.ofNullable(this.activeConnections.remove(project.getBasePath()))
                    .ifPresent(MessageBusConnection::disconnect);
        }
        if (isRemoveProject)
            mDocListenerMap.remove(project);
    }

    public long getClickTimeStamp() {
        return mClickTimeStamp;
    }

    public void setClickTimeStamp(long clickTimeStamp) {
        mClickTimeStamp = clickTimeStamp;
        cancelClickTimeStamp();
        updateClickTimeStamp();
    }

    public int getClickCombo() {
        return mClickCombo;
    }

    public void setClickCombo(int clickCombo) {
        mClickCombo = clickCombo;
    }

    class ClickTimeStampTimerTask extends TimerTask {

        private static final int CLICK_TIME_INTERVAL = 100;

        private long mClickTimeStampInterval;

        ClickTimeStampTimerTask() {
            mClickTimeStampInterval = Config.getInstance().state.CLICK_TIME_INTERVAL;
        }

        @Override
        public void run() {
            if (mClickTimeStampInterval > 0) {
                mClickTimeStampInterval -= CLICK_TIME_INTERVAL;

                int value = (int) ((float) mClickTimeStampInterval / (float) Config.getInstance().state.CLICK_TIME_INTERVAL * 100);
                if (mClickTimeStampProgressBar != null) {
                    mClickTimeStampProgressBar.setValue(value);
                    mClickTimeStampProgressBar.setVisible(true);
                }
            } else {
                cancelClickTimeStamp();
                setClickCombo(0);

                if (mClickTimeStampProgressBar != null) {
                    mClickTimeStampProgressBar.setValue(0);
                    mClickTimeStampProgressBar.setVisible(false);
                }

                if (mComboLabel != null) {
                    mComboLabel.setText(String.valueOf(getClickCombo()));
                    if (IntegerUtil.isSizeTable(getClickCombo())) {
                        if (mCurrentEditor != null) {
                            JComponent contentJComponent = mCurrentEditor.getContentComponent();
                            addComboLabel(contentJComponent, -contentJComponent.getX(), -contentJComponent.getY());
                        }
                    }
                }
            }
        }
    }
}
