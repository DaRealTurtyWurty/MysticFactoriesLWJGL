package dev.turtywurty.mysticfactories.client.ui.widget;

import lombok.Getter;

import java.util.Objects;

/**
 * Represents a single tab inside a {@link TabPane}. Holds the tab title and the content widget that
 * is rendered when the tab is selected.
 */
@Getter
public class Tab {
    private String title;
    private Widget content;

    public Tab(String title, Widget content) {
        this.title = Objects.requireNonNullElse(title, "");
        this.content = Objects.requireNonNull(content, "content");
    }

    /**
     * Sets the label for this tab.
     */
    public Tab setTitle(String title) {
        this.title = Objects.requireNonNullElse(title, "");
        return this;
    }

    /**
     * Replaces the content widget.
     */
    public Tab setContent(Widget content) {
        this.content = Objects.requireNonNull(content, "content");
        return this;
    }

    /**
     * Builder for convenience when constructing tab panes.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String title = "";
        private Widget content;

        private Builder() {}

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(Widget content) {
            this.content = content;
            return this;
        }

        public Tab build() {
            return new Tab(this.title, Objects.requireNonNull(this.content, "content"));
        }
    }
}
