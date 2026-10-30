package org.telegram.messenger;

import org.telegram.ui.ChatActivity;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * A chat that is shown again after the app was away does not mark its messages read until the user types or
 * scrolls, so a quick look followed by Back leaves them unread (see REMOVALS.md).
 */
public class ForkChatReadHold {

    private static final Set<ChatActivity> heldChats = Collections.newSetFromMap(new WeakHashMap<>());

    public static void holdUntilInteraction(ChatActivity chat) {
        heldChats.add(chat);
    }

    public static void holdIfAppIsGoingAway(ChatActivity chat) {
        if (ApplicationLoader.mainInterfacePaused) {
            holdUntilInteraction(chat);
        }
    }

    public static boolean isHolding(ChatActivity chat) {
        return heldChats.contains(chat);
    }

    public static void releaseOnInteraction(ChatActivity chat) {
        if (heldChats.remove(chat)) {
            AndroidUtilities.runOnUIThread(() -> chat.updateMessagesVisiblePart(false));
        }
    }
}
