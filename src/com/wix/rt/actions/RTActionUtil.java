package com.wix.rt.actions;

import com.intellij.openapi.project.Project;
import com.wix.rt.RTProjectComponent;

/**
 * RTActionUtil
 * Created by idok on 11/20/14.
 */
public final class RTActionUtil {

    private RTActionUtil() {
    }

    public static boolean isRTEnabled(Project project) {
        if (project == null) {
            return false;
        }
        RTProjectComponent conf = project.getComponent(RTProjectComponent.class);
        return conf != null && conf.isEnabled();
    }
}
