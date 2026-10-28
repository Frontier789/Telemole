package org.telegram.messenger;

import android.content.SharedPreferences;

public class ForkPermissions {

    private static final String GALLERY_ASKED_KEY = "attach_gallery_permission_asked";

    /** True only the first time, so the attach menu never nags; its own "Allow access" button covers later. */
    public static boolean shouldAskGalleryNow() {
        final SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (preferences.getBoolean(GALLERY_ASKED_KEY, false)) {
            return false;
        }
        preferences.edit().putBoolean(GALLERY_ASKED_KEY, true).apply();
        return true;
    }
}
