package com.jiyuanime.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.Comparing;
import com.intellij.ui.ColorPanel;
import com.jiyuanime.config.Config;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Setting implements Configurable {

    private ColorPanel colorChooser;
    private JPanel rootPanel;
    private JCheckBox colorAutoCheckBox;
    private JSlider particleSizeSlider;
    private JSlider particleMaxCountSlider;
    private JLabel maxCountValue;
    private JLabel sizeValue;

    private Config.State state = Config.getInstance().state;

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
    }
}
