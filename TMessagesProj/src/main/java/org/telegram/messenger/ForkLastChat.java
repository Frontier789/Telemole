package org.telegram.messenger;

import org.telegram.ui.ActionBar.BaseFragment;

import java.util.ArrayList;

/**
 * Closes the open chat when a message arrives while the app is away, so that reopening the app lands on the chat
 * list and the message stays unread instead of being marked read by merely showing the chat (see REMOVALS.md).
 */
public class ForkLastChat {

    private static boolean waitingForActivityResult;

    public static void activityLaunchedForResult() {
        waitingForActivityResult = true;
    }

    public static void activityResultReturned() {
        waitingForActivityResult = false;
    }

    public static boolean closeIfIncomingWhileAway(BaseFragment chat, ArrayList<MessageObject> newMessages) {
        if (!ApplicationLoader.mainInterfacePaused || waitingForActivityResult || !isLastInItsLayout(chat) || !hasIncoming(newMessages)) {
            return false;
        }
        chat.finishFragment(false);
        return true;
    }

    private static boolean isLastInItsLayout(BaseFragment fragment) {
        return fragment.getParentLayout() != null && fragment.getParentLayout().getLastFragment() == fragment;
    }

    private static boolean hasIncoming(ArrayList<MessageObject> messages) {
        for (int i = 0; i < messages.size(); i++) {
            if (!messages.get(i).isOut()) {
                return true;
            }
        }
        return false;
    }
}
