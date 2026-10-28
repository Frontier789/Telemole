package org.telegram.messenger;

/**
 * Switches for the features this fork removes (see REMOVALS.md).
 * Upstream code asks here, so turning a feature back on is a one-line change.
 */
public class ForkFeatures {

    public static boolean aiEnabled() {
        return false;
    }
}
