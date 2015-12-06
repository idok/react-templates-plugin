package com.wix.rt.build;

import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import icons.RTIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class RTFileType extends LanguageFileType {
    public static final RTFileType INSTANCE = new RTFileType();
    public static final String RT_EXT = "rt";

    private RTFileType() {
        super(HTMLLanguage.INSTANCE);
    }

    @NotNull
    public String getName() {
        return "React Templates";
    }

    @NotNull
    public String getDescription() {
        return "React Templates file";
    }

    @NotNull
    public String getDefaultExtension() {
        return RT_EXT;
    }

    @NotNull
    public Icon getIcon() {
        return RTIcons.RT;
    }
}