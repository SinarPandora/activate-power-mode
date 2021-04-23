package com.jiyuanime.ui;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColorPanel;
import com.jiyuanime.ActivatePowerModeManage;
import com.jiyuanime.config.Config;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.Objects;

public class Setting implements Configurable {

    private ColorPanel colorChooser;
    private JPanel rootPanel;
    private JCheckBox colorAutoCheckBox;
    private JSlider particleSizeSlider;
    private JSlider particleMaxCountSlider;
    private JLabel maxCountValue;
    private JLabel sizeValue;
    private JTextField comboFont;
    private JButton setDefaultButton;
    private JButton chooseFontButton;

    private Config.State state = Config.getInstance().state;

    ActivatePowerModeManage manager = ActivatePowerModeManage.getInstance();

    @Nls
    @Override
    public String getDisplayName() {
        return "Activate Power Mode";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        initListener();
        initSetting();
        return this.rootPanel;
    }

    @Override
    public boolean isModified() {
        try {
            return !Comparing.equal(state.PARTICLE_MAX_COUNT, particleMaxCountSlider.getValue()) ||
                    !Comparing.equal(state.PARTICLE_SIZE, particleSizeSlider.getValue()) ||
                    !Comparing.strEqual(state.FONT_FILE_LOCATION == null? Config.DEFAULT: state.FONT_FILE_LOCATION, comboFont.getText()) ||
                    !Comparing.equal(state.PARTICLE_COLOR, colorAutoCheckBox.isSelected() ? null : colorChooser.getSelectedColor());
        } catch (NumberFormatException $ex) {
            return true;
        }
    }

    @Override
    public void apply() throws ConfigurationException {
        state.PARTICLE_MAX_COUNT = particleMaxCountSlider.getValue();
        state.PARTICLE_SIZE = particleSizeSlider.getValue();
        if(!colorAutoCheckBox.isSelected() && colorChooser.getSelectedColor() == null) {
            throw new ConfigurationException("'particle color' is not choose.'");
        }
        state.PARTICLE_COLOR = colorAutoCheckBox.isSelected() ? null : colorChooser.getSelectedColor();

        if ("".equals(comboFont.getText())) {
            throw new ConfigurationException("'combo label font must be set'");
        } else if (!Comparing.strEqual(Config.DEFAULT, comboFont.getText()) && !new File(comboFont.getText()).exists()) {
            throw new ConfigurationException("'font file must exist!'");
        }

        String selectFonts = comboFont.getText().equals(Config.DEFAULT) ? null : comboFont.getText();
        if (!Comparing.strEqual(selectFonts, state.FONT_FILE_LOCATION)) {
            // 检测到变化，重启 power mode 组件
            state.FONT_FILE_LOCATION = selectFonts;
            for (Project project : ProjectManager.getInstance().getOpenProjects()) {
                manager.clearComboView(project);
                manager.initComboView(project);
            }
        }
    }

    @Override
    public void reset() {
        this.initSetting();
    }

    private void initListener() {
        colorAutoCheckBox.addItemListener(event -> {
            JCheckBox item = (JCheckBox) event.getItem();
            colorChooser.setSelectedColor(null);
            colorChooser.setEditable(!item.isSelected());
        });
        particleSizeSlider.addChangeListener(event -> {
            JSlider source = (JSlider) event.getSource();
            sizeValue.setText(String.valueOf(source.getValue()));
        });
        particleMaxCountSlider.addChangeListener(event -> {
            JSlider source = (JSlider) event.getSource();
            maxCountValue.setText(String.valueOf(source.getValue()));
        });
        setDefaultButton.addActionListener(event -> this.comboFont.setText(Config.DEFAULT));
        FileChooserDescriptor fontFileConfig = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor();
        chooseFontButton.addActionListener(event -> {
            VirtualFile currentDir = LocalFileSystem.getInstance().findFileByIoFile(new File("./"));
            Project project = ProjectUtil.guessProjectForFile(currentDir);
            FileChooser.chooseFile(fontFileConfig, project, currentDir, file ->
                    this.comboFont.setText(file.getPath()));
        });
    }

    private void initSetting() {
        particleMaxCountSlider.setValue(state.PARTICLE_MAX_COUNT);
        maxCountValue.setText(String.valueOf(state.PARTICLE_MAX_COUNT));
        particleSizeSlider.setValue(state.PARTICLE_SIZE);
        sizeValue.setText(String.valueOf(state.PARTICLE_SIZE));

        if(state.PARTICLE_COLOR == null) {
            colorAutoCheckBox.setSelected(true);
        } else {
            colorChooser.setSelectedColor(state.PARTICLE_COLOR);
        }

        if (Objects.isNull(state.FONT_FILE_LOCATION)) {
            this.comboFont.setText(Config.DEFAULT);
        } else {
            this.comboFont.setText(state.FONT_FILE_LOCATION);
        }
    }
}
