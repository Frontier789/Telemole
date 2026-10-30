package org.telegram.messenger;

/**
 * Switches for the features this fork removes (see REMOVALS.md).
 * Upstream code asks here, so turning a feature back on is a one-line change.
 */
public class ForkFeatures {

    public static boolean aiEnabled() {
        return false;
    }

    public static boolean premiumPromosEnabled() {
        return false;
    }

    public static boolean storiesEnabled() {
        return false;
    }

    public static boolean adsEnabled() {
        return false;
    }

    public static boolean nagsEnabled() {
        return false;
    }

    public static boolean recommendationsEnabled() {
        return false;
    }

    public static boolean giftsEnabled() {
        return false;
    }

    public static boolean starsEnabled() {
        return false;
    }

    public static boolean voiceToTextEnabled() {
        return false;
    }

    public static boolean chatAutomationEnabled() {
        return false;
    }

    public static boolean boostBadgesEnabled() {
        return false;
    }

    public static boolean callIconInChatHeaderEnabled() {
        return false;
    }

    public static boolean attachBadgeOnMenuIconEnabled() {
        return false;
    }

    public static boolean archiveRowEnabled() {
        return false;
    }

    public static boolean composeButtonEnabled() {
        return false;
    }

    public static boolean telemetryEnabled() {
        return false;
    }

    public static boolean trendingStickersEnabled() {
        return false;
    }

    public static boolean swipeBackFromChatEnabled() {
        return false;
    }

    public static boolean restoreChatAfterKillEnabled() {
        return false;
    }
}
