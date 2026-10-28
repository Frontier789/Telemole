# Fork removals — the user's decisions

What the user wants gone from the app (CLAUDE.md rule 3), and what they want kept. Agents: read this before
removing anything, follow CLAUDE.md for *how*, and do not re-ask what is decided here.
One commit per row. Mark a row `done` (with the commit) when it has shipped.

The user is fed up with **dark patterns**: nags, upsells, promos, and prompts that push toward a purchase
or more engagement. Real error messages that explain why something failed are not dark patterns and stay.

Gates below are where an earlier survey found the narrowest choke point (symbol names first; line numbers are
for 13.1.1 and drift). Paths are under `TMessagesProj/src/main/java/org/telegram/`. Re-verify before editing.

## Remove

| ID | Feature | Gate | Status |
|----|---------|------|--------|
| 1 | **Stories**, all of it: viewer, chat-list row, avatar rings, posting/camera, profile tab, story links, story push, story messages in chats, story fetching and polling | `StoryViewer.open` early return (all overloads); `DialogsActivity.updateStoriesVisibility` forced hidden (not just the view: ~40 sites read `hasStories`); `StoriesUtilities.drawAvatarWithStory` and `getPredictiveUnreadState`; startup `loadAllStories`/`loadStories`/`loadHiddenStories` in `DialogsActivity`; `UserListPoller`; camera button; `StoryRecorder`; profile Stories tab; story links in `LaunchActivity`; story push in `PushListenerController`; ~8 story-message sites in chats. Traps: reply loading (`fillMessagesWithStories`) must still call its callback; `getFullUser`/`getFullChat` embed stories, ignore rather than skip; skipping hidden-stories fetch also kills the empty Archive folder | todo |
| 2 | **Premium reminders/upsells** | Force `MessagesController.premiumLocked` true after `applyAppConfig` (covers `premiumFeaturesBlocked()` ~90 callers, `premiumPurchaseBlocked()` incl. every gift entry, `PremiumPreviewFragment.onFragmentCreate` refusing to open). Plus the ungated ones: `PremiumFeatureBottomSheet` (~60 lock-tap sites, override `show()`), `tg://premium_offer`, `UnlockPremiumView` in `ContentPreviewViewer`. Plus pure-promo messages that need their own gate: birthday-gift hint (`ChatActivityEnterView` `birthdayHint`), `SavedTagReactionsPremiumHint`, `showMultipleReactionsPromo`, speed-up promos (`PremiumSpeedPromo` menu item, DownloadSpeedLimited bulletin). Skip `help.getPremiumPromo`, the gift-sticker load and the Premium billing query at startup. Side effects: Premium sticker sets, Premium reactions and Premium app icons disappear | todo |
| 3 | **AI composer button + "Rewrite, translate, or correct your text using AI" bubble**, and the tones fetch | Early return in `ChatActivityEnterView.showAiButton`; same in `CaptionPhotoViewer` and `ChatAttachAlert` (two buttons) | todo |
| 3a | AI Summary button on long messages | `TranslateController.isSummarizable` | todo (assumed from CLAUDE.md rule 3; not confirmed one by one) |
| 3b | AI Style links (`t.me/addstyle`) and the "OPEN AI STYLE" button | | todo (same) |
| 3c | AI buttons in the rich/article editor (`RichEditor`, `RichEditorToolbar`), `AIEditorAlert` | | todo (same) |
| A1 | Sponsored messages in channels and the bot ad bar | `MessagesController.getSponsoredMessages` returns null | todo |
| A2 | Sponsored peers in global search | `DialogsSearchAdapter` | todo |
| A3 | Video ads in the photo viewer | `VideoAds.load` | todo |
| A4 | Server promos and suggestion banners (proxy-sponsor chat, passkey, birthday, userpic, `PREMIUM_*`, SMS-jobs, ticks explainer) and the `help.getPromoData` request | `MessagesController.checkPromoInfoInternal`. **Open question below on the security prompts** | blocked on question |
| A5 | Leftover chat-list banners: clear-storage, gift auctions, Stars low balance | gate inside `DialogsActivity.updateDialogsHint`; `isStarsSubscriptionHintVisible`; `checkActiveGiftAuctionsHintCellVisibility` (also `GiftAuctionController.hasActiveAuctions` fires a request) | todo |
| G1 | Gift buttons: input bar, channel bottom bar, profile menus | Covered by forcing `premiumLocked` (input bar, profile menus, Settings "Send a gift", birthday hint). Own gates: channel bottom bar `ChatActivity` `showGiftButton`; profile `giftsView` around the avatar; `onGiftPermiumClicked` early return as fallback; optional message-menu "Send gift" | todo |
| G2 | Stars and TON | `MessagesController.starsPurchaseAvailable()` returns false (hides Settings and Profile Stars rows, top-up shows "not available"; paying with an existing balance, paid reactions, paid messages and earnings are NOT affected). TON row: `SettingsActivity` and `ProfileActivity` `tonRow` (also stops the TON balance prefetch). "Telegram Features" row in `SettingsActivity`. Gifts profile tab: `SharedMediaLayout` `hasGifts`. Optional: `walletAvailable` (server-set, probably already off) | todo |
| D2 | "Similar channels/bots" recommendations | `MessagesController.getChannelRecommendations` returns null; check its two other callers | todo |
| D4 | "Invite Friends" and the phonebook list in Contacts | `ContactsAdapter`, `ContactsActivity` | todo |
| D5 | "Add to contacts" and "Share my number" in the chat top panel (Report spam and Block stay) | `ChatActivity` top panel | todo |
| N1 | **Contacts** and **storage** permission prompts | `DialogsActivity.onResume`: force `hasNotContactsPermission` and `hasNotStoragePermission` false; in `askForPermissons` remove the contacts block and the storage/photos block (the fallback path still reaches them otherwise, which is why photos are asked at startup on Android 13+). `ContactsActivity` force `checkPermission = false`. `FilesMigrationService.checkBottomSheet` early return (likely dead). Keep the notification prompt and the MIUI/full-screen-intent dialogs | todo |
| N2 | "!" badge on the Contacts tab | `MainTabsActivity.checkContactsTabBadge` | todo |
| N3 | Update popup, update bar and forced-update screen | `BuildVars.CHECK_UPDATES` or early return in `LaunchActivity.checkAppUpdate` | todo |
| N4 | SMS-jobs program (menu item, banner, sticky notification) | Appears only if the server sends `PREMIUM_SMSJOBS` or you joined; `SMSJobController.init()` runs only from `SMSStatsActivity`, so nothing is fetched at startup. A4 already covers it: verify only | todo |
| H1 | One-time tooltips through `HintsController` | `HintsController.Hint.show()` returns false. It does NOT silence the "Hold to record..." bubble (its method has ungated callers), nor anything not routed through the 8 enum values | todo |
| V | **Voice-to-text** | (a) `ChatMessageCell` `useTranscribeButton = (` prefixed with a false gate: removes button, touch, layout, upsell. (b) `TranscribeButton.transcribePressed` early return: no `messages.transcribeAudio` can be built. Plus `ChatActivity` `showRateTranscription` false. Optional: `MessageObject.isVoiceTranscriptionOpen` false (cached transcripts keep rendering otherwise), `TranscribeButton.showOffTranscribe` perf gate. Leave the Premium page row and config parsing | todo |
| C | **Chat Automation** — the row on the *Edit Profile* screen, not in Settings | `UserInfoActivity` skip the `BUTTON_AI` if/else and change the following `UItem.asShadow(-3, ...)` to a plain shadow; optionally skip `BusinessChatbotController.load` on open (`account.getConnectedBots`). Keep the bot session management in `SessionsActivity` (the way to disconnect a bot). The Business page's "Chatbots" row is already hidden by `premiumLocked` | todo |
| B | **Boost badges**: the lightning counter next to a member's name in group messages | `ChatMessageCell` `from_boosts_applied` read, force 0 (layout-neutral, no network) | todo |

## Keep

| ID | Feature | Note |
|----|---------|------|
| D1 | Search tabs: Channels, Apps, Posts, Public posts | "for now" |
| D3 | Translate bar in chats | "for now" |
| H2 | "NEW" badges (`applyNewSpan`) | |
| H3 | Ad-hoc chat hints that show once or a bounded number of times | see the open question for the recurring ones |
| V1 | The four bottom navigation tabs | "as is for now" |
| N1 | The **notification** permission prompt (and the MIUI lockscreen / full-screen-intent dialogs, which stop after "Not now") | |
| — | `LimitReachedBottomSheet` and the `PREMIUM_ACCOUNT_REQUIRED` error handlers | they explain real errors |
| — | Boost restriction flow: "Boost this group to send messages" bar, `checkCanRemoveRestrictionsByBoosts`, boost `LimitReachedBottomSheet` types | explain why you cannot write or send media. Also the `premium.getBoostsStatus` request that feeds them |
| — | Frozen-account banner, new-device login prompt, terms-of-service prompt, `PREMIUM_GRACE` notice | borderline, kept by default |
| — | Quick Replies and To-Do badges in the attach menu | kept by default, not discussed |

## Open questions

- **A4 scope.** Returning early from `checkPromoInfoInternal` also removes three account-security prompts: "Is this still your phone number?" and "Do you remember your 2-step password?" at the top of Settings (`VALIDATE_PHONE_NUMBER`, `VALIDATE_PASSWORD`), and the server-requested "set up login email" screen (`SETUP_LOGIN_EMAIL`, `SETUP_LOGIN_EMAIL_NOSKIP`). Alternative: keep the request but only accept those four suggestions and ignore the promo peer.
- **Messages shown when a tap is refused for lack of Premium** (caption over the free limit, locked Premium emoji, tick a checklist item, hide sender name when forwarding). They explain a refused action but end in a Premium pitch.
- **Recurring hints not behind `HintsController`**: record-button "Hold to record" (every too-short tap), "Tap here for reply/forward options" (until tapped once), "Swipe to reply" bulletin, "Processing video may take a few minutes", poll quiz tip, view-once voice note text, full-screen player speed tip.
- **Photos permission:** request on opening the attach menu, or keep today's one-tap "Allow access" inside it (Android 13+).
