package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * help.getPromoData carries the sponsored proxy/PSA chat and the banner suggestions.
 * This fork keeps only the account-security suggestions.
 */
public class ForkPromoData {

    private static final List<String> ACCOUNT_SECURITY_SUGGESTIONS = Arrays.asList(
        "VALIDATE_PHONE_NUMBER",
        "VALIDATE_PASSWORD",
        "SETUP_LOGIN_EMAIL",
        "SETUP_LOGIN_EMAIL_NOSKIP"
    );

    public static void keepOnlyAccountSecurity(TLRPC.TL_help_promoData promoData) {
        promoData.peer = null;
        promoData.proxy = false;
        promoData.psa_type = null;
        promoData.psa_message = null;
        promoData.custom_pending_suggestion = null;
        final ArrayList<String> kept = new ArrayList<>();
        for (String suggestion : promoData.pending_suggestions) {
            if (ACCOUNT_SECURITY_SUGGESTIONS.contains(suggestion)) {
                kept.add(suggestion);
            }
        }
        promoData.pending_suggestions = kept;
    }
}
