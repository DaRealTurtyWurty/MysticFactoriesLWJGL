package dev.turtywurty.mysticfactories.client.ui;

import dev.turtywurty.mysticfactories.client.settings.FullscreenMode;
import dev.turtywurty.mysticfactories.client.settings.ResolutionPreset;
import dev.turtywurty.mysticfactories.client.settings.Settings;
import dev.turtywurty.mysticfactories.client.ui.widget.*;

import java.util.List;

/**
 * Settings screen for client options.
 */
public class SettingsGUI extends GUI {
    private final Runnable onClose;

    public SettingsGUI(Runnable onClose) {
        this.onClose = onClose;
    }

    @Override
    protected void buildWidgets(int screenWidth, int screenHeight) {
        float panelWidth = 320f;
        float panelHeight = 360f;
        float x = (screenWidth - panelWidth) * 0.5f;
        float y = (screenHeight - panelHeight) * 0.5f;

        ScrollList list = ScrollList.builder()
                .position(x, y)
                .size(panelWidth, panelHeight)
                .spacing(8f)
                .barWidth(10f)
                .build();

        list.addChild(label("Display Settings", panelWidth));
        list.addChild(fullscreenRow(panelWidth));
        //list.addChild(resolutionRow(panelWidth));
        list.addChild(vsyncRow(panelWidth));
        list.addChild(fpsRow(panelWidth));
        list.addChild(TextInput.builder()
                .size(200f, 28f)
                .build());

        list.addChild(Button.builder()
                .size(120f, 32f)
                .text("Back")
                .onClick(btn -> this.onClose.run())
                .build());

        addWidget(list);
    }

    private WidgetList row(String label, Widget control, float totalWidth) {
        var labelWidget = TextLabel.builder().textSupplier(() -> label).build();
        WidgetList row = WidgetList.horizontal()
                .spacing(8f)
                .addChild(labelWidget)
                .addChild(control)
                .build();
        float rowHeight = Math.max(labelWidget.getHeight(), control.getHeight());
        float rowWidth = totalWidth > 0 ? totalWidth : (labelWidget.getWidth() + 8f + control.getWidth());
        row.setSize(rowWidth, rowHeight);
        return row;
    }

    private WidgetList label(String text, float totalWidth) {
        var lbl = TextLabel.builder().textSupplier(() -> text).build();
        WidgetList row = WidgetList.horizontal()
                .addChild(lbl)
                .build();
        row.setSize(totalWidth > 0 ? totalWidth : lbl.getWidth(), lbl.getHeight());
        return row;
    }

    private WidgetList fullscreenRow(float totalWidth) {
        var dropdown = DropdownWidget.<FullscreenMode>builder()
                .size(180f, 28f)
                .options(() -> List.of(FullscreenMode.values()))
                .optionFactory(mode -> TextLabel.builder().textSupplier(mode::name).build())
                .selectedFactory(mode -> TextLabel.builder().textSupplier(mode::name).build())
                .onSelect(Settings.getInstance()::setFullscreenMode)
                .initialSelected(Settings.getInstance().getFullscreenMode())
                .build();
        return row("Fullscreen", dropdown, totalWidth);
    }

    private WidgetList resolutionRow(float totalWidth) {
        var dropdown = DropdownWidget.<ResolutionPreset>builder()
                .size(180f, 28f)
                .options(() -> List.of(ResolutionPreset.values()))
                .optionFactory(preset -> TextLabel.builder().textSupplier(() -> preset.getWidth() + "x" + preset.getHeight()).build())
                .selectedFactory(preset -> TextLabel.builder().textSupplier(() -> preset.getWidth() + "x" + preset.getHeight()).build())
                .onSelect(Settings.getInstance()::setResolutionPreset)
                .initialSelected(Settings.getInstance().getResolutionPreset())
                .build();
        return row("Resolution", dropdown, totalWidth);
    }

    private WidgetList vsyncRow(float totalWidth) {
        var toggle = ToggleButton.builder()
                .size(80f, 28f)
                .text("On", "Off")
                .initialState(Settings.getInstance().isVsync())
                .onToggle(tb -> Settings.getInstance().setVsync(tb.isOn()))
                .build();
        return row("VSync", toggle, totalWidth);
    }

    private WidgetList fpsRow(float totalWidth) {
        var dropdown = DropdownWidget.<Integer>builder()
                .size(100f, 28f)
                .options(() -> List.of(30, 60, 90, 120, 144, 240))
                .optionFactory(val -> TextLabel.builder().textSupplier(() -> val + "").build())
                .selectedFactory(val -> TextLabel.builder().textSupplier(() -> val + "").build())
                .onSelect(Settings.getInstance()::setFpsCap)
                .initialSelected(Settings.getInstance().getFpsCap())
                .build();
        return row("FPS Cap", dropdown, totalWidth);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        Settings.getInstance().save();
    }
}
