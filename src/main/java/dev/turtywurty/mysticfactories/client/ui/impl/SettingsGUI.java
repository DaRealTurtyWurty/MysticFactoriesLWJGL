package dev.turtywurty.mysticfactories.client.ui;

import dev.turtywurty.mysticfactories.client.settings.Settings;
import dev.turtywurty.mysticfactories.client.ui.widget.*;

import java.util.List;
import java.util.function.Function;

/**
 * Settings screen showing multiple fake categories so the TabPane can be previewed.
 */
public class SettingsGUI extends GUI {
    private final Runnable onClose;

    public SettingsGUI(Runnable onClose) {
        this.onClose = onClose;
    }

    @Override
    protected void buildWidgets(int screenWidth, int screenHeight) {
        float panelWidth = Math.min(screenWidth - 140f, 760f);
        float panelHeight = Math.min(screenHeight - 160f, 520f);
        float x = (screenWidth - panelWidth) * 0.5f;
        float y = (screenHeight - panelHeight) * 0.5f;

        TabPane.Builder paneBuilder = TabPane.builder()
                .position(x, y)
                .size(panelWidth, panelHeight);

        paneBuilder.addTab(buildDisplayTab(panelWidth, panelHeight));
        paneBuilder.addTab(buildAudioTab(panelWidth, panelHeight));
        paneBuilder.addTab(buildGameplayTab(panelWidth, panelHeight));
        paneBuilder.addTab(buildControlsTab(panelWidth, panelHeight));
        paneBuilder.addTab(buildAccessibilityTab(panelWidth, panelHeight));

        TabPane pane = paneBuilder.build();
        addWidget(pane);

        addWidget(Button.builder()
                .position(x, y + panelHeight + 16f)
                .size(128f, 32f)
                .text("Apply")
                .onClick(btn -> {
                })
                .build());

        addWidget(Button.builder()
                .position(x + panelWidth - 128f, y + panelHeight + 16f)
                .size(128f, 32f)
                .text("Back")
                .onClick(btn -> this.onClose.run())
                .build());
    }

    private Tab buildDisplayTab(float width, float height) {
        ScrollList list = createCategoryList(width, height);
        list.addChild(sectionLabel("Display Modes"));
        list.addChild(row("Window Mode", dropdown(List.of("Windowed", "Borderless", "Fullscreen"), "Borderless", 220f)));
        list.addChild(row("Resolution", dropdown(List.of("1920x1080", "2560x1440", "3840x2160"), "2560x1440", 220f)));
        list.addChild(row("Brightness", slider(0f, 100f, 65f, 5f, "%")));
        list.addChild(row("UI Scale", dropdown(List.of("75%", "100%", "125%", "150%"), "100%", 180f)));
        list.addChild(row("Framerate Cap", slider(30f, 240f, 144f, 6f, value -> String.format("%.0f FPS", value))));
        list.addChild(row("VSync", toggle(true)));
        list.addChild(row("Show FPS Counter", toggle(false)));
        return new Tab("Display", list);
    }

    private Tab buildAudioTab(float width, float height) {
        ScrollList list = createCategoryList(width, height);
        list.addChild(sectionLabel("Mixing"));
        list.addChild(row("Master Volume", slider(0f, 100f, 80f, 5f, "%")));
        list.addChild(row("Music Volume", slider(0f, 100f, 55f, 5f, "%")));
        list.addChild(row("Effects Volume", slider(0f, 100f, 72f, 5f, "%")));
        list.addChild(row("Dialogue Volume", slider(0f, 100f, 65f, 5f, "%")));
        list.addChild(row("Audio Output", dropdown(List.of("Speakers", "Headphones", "Surround"), "Headphones", 220f)));
        list.addChild(row("Mute When Unfocused", toggle(false)));
        list.addChild(row("Subtitles", toggle(true)));
        return new Tab("Audio", list);
    }

    private Tab buildGameplayTab(float width, float height) {
        ScrollList list = createCategoryList(width, height);
        list.addChild(sectionLabel("Experience"));
        list.addChild(row("Difficulty", dropdown(List.of("Relaxed", "Standard", "Challenging", "Nightmare"), "Standard", 220f)));
        list.addChild(row("Tutorial Hints", toggle(true)));
        list.addChild(row("Auto-Save Interval", dropdown(List.of("5 Minutes", "10 Minutes", "30 Minutes"), "10 Minutes", 200f)));
        list.addChild(row("Camera Shake", toggle(true)));
        list.addChild(row("Field of View", slider(70f, 110f, 90f, 2f, value -> String.format("%.0f°", value))));
        list.addChild(row("Show Damage Numbers", toggle(true)));
        var clanTag = TextInput.builder()
                .size(220f, 32f)
                .placeholder("Squad Tag")
                .build();
        clanTag.setText("MF");
        list.addChild(row("Profile Tag", clanTag));
        return new Tab("Gameplay", list);
    }

    private Tab buildControlsTab(float width, float height) {
        ScrollList list = createCategoryList(width, height);
        list.addChild(sectionLabel("Movement"));
        list.addChild(keybindRow("Move Forward", "W"));
        list.addChild(keybindRow("Move Backward", "S"));
        list.addChild(keybindRow("Strafe Left", "A"));
        list.addChild(keybindRow("Strafe Right", "D"));
        list.addChild(sectionLabel("Mouse"));
        list.addChild(row("Sensitivity", slider(0.1f, 10f, 3.5f, 0.1f, value -> String.format("%.1f", value))));
        list.addChild(row("Invert Y-Axis", toggle(false)));
        list.addChild(sectionLabel("Shortcuts"));
        list.addChild(keybindRow("Inventory", "Tab"));
        list.addChild(keybindRow("Quick Menu", "Q"));
        list.addChild(row("Hold To Sprint", toggle(true)));
        return new Tab("Controls", list);
    }

    private Tab buildAccessibilityTab(float width, float height) {
        ScrollList list = createCategoryList(width, height);
        list.addChild(sectionLabel("Visual Aids"));
        list.addChild(row("Color Blind Mode", dropdown(List.of("Off", "Protanopia", "Deuteranopia", "Tritanopia"), "Off", 220f)));
        list.addChild(row("High Contrast UI", toggle(false)));
        list.addChild(row("Text Size", dropdown(List.of("Small", "Normal", "Large", "Extra Large"), "Normal", 200f)));
        list.addChild(row("Cursor Size", slider(0.5f, 2.0f, 1.0f, 0.1f, value -> String.format("%.1fx", value))));
        list.addChild(sectionLabel("Assistance"));
        list.addChild(row("Screen Reader", toggle(false)));
        list.addChild(row("Chat Text-To-Speech", toggle(true)));
        list.addChild(row("Subtitle Background", toggle(true)));
        return new Tab("Accessibility", list);
    }

    private ScrollList createCategoryList(float width, float height) {
        return ScrollList.builder()
                .position(0f, 0f)
                .size(width, height)
                .spacing(14f)
                .scrollSpeed(20f)
                .barWidth(8f)
                .build();
    }

    private Widget sectionLabel(String text) {
        var label = TextLabel.builder()
                .text(text)
                .color(0xFF88D2FF)
                .build();
        WidgetList wrapper = WidgetList.horizontal()
                .addChild(label)
                .build();
        wrapper.setSize(label.getWidth(), label.getHeight());
        return wrapper;
    }

    private Widget row(String labelText, Widget control) {
        var label = TextLabel.builder()
                .text(labelText)
                .color(0xFFE0E0E0)
                .build();
        label.setSize(180f, label.getHeight());
        WidgetList row = WidgetList.horizontal()
                .spacing(16f)
                .addChild(label)
                .addChild(control)
                .build();
        float height = Math.max(label.getHeight(), control.getHeight());
        float width = label.getWidth() + 16f + control.getWidth();
        row.setSize(width, height);
        return row;
    }

    private DropdownWidget<String> dropdown(List<String> options, String selected, float width) {
        return DropdownWidget.<String>builder()
                .size(width, 32f)
                .optionHeight(28f)
                .options(() -> options)
                .optionFactory(value -> TextLabel.builder().text(value).build())
                .selectedFactory(value -> TextLabel.builder().text(value).build())
                .initialSelected(selected)
                .onSelect(value -> {
                })
                .build();
    }

    private ToggleButton toggle(boolean initialState) {
        return ToggleButton.builder()
                .size(120f, 32f)
                .text("On", "Off")
                .initialState(initialState)
                .onToggle(tb -> {
                })
                .build();
    }

    private SliderWidget slider(float min, float max, float value, float step, String suffix) {
        return slider(min, max, value, step, val -> formatSliderValue(val, step) + suffix);
    }

    private SliderWidget slider(float min, float max, float value, float step, Function<Float, String> formatter) {
        return SliderWidget.builder()
                .size(260f, 48f)
                .range(min, max)
                .initialValue(value)
                .step(step)
                .valueFormatter(formatter)
                .onValueChanged(v -> {
                })
                .build();
    }

    private String formatSliderValue(float value, float step) {
        if (step >= 1f) {
            return String.format("%.0f", value);
        }
        return String.format("%.1f", value);
    }

    private Widget keybindRow(String action, String binding) {
        var button = Button.builder()
                .size(140f, 30f)
                .text(binding)
                .onClick(btn -> {
                })
                .build();
        return row(action, button);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        Settings.getInstance().save();
    }
}
