package com.imin.communications.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.imin.user.User;

public class SyncUtils {
	private static final long SYNC_FREQUENCY = 60 * 60; // 1 hour (in seconds)
	private static final String PREF_SETUP_COMPLETE = "setup_complete";

	public static final String CONTENT_AUTHORITY = "com.imin.communications.sync";

	public static void createSyncAccount(Context context, User user) {
		boolean newAccount = false;
		boolean setupComplete = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE,
				false);

		// Create account if it's missing
		Account account = AccountService.getAccount(user.getPublicUserId());
		AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

		if (accountManager.addAccountExplicitly(account, null, null)) {
			// Inform the system that this account supports sync
			ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);

			// Inform the system that this account is eligible for auto sync when the network is up
			ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);

			// Recommend a schedule for automatic synchronization
			ContentResolver.addPeriodicSync(account, CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY);

			newAccount = true;
		}

		// Schedule an initial sync if we detect problems with either our account or our local
		// data has been deleted, note that it's possible to clear app data WITHOUT affecting
		// the account list, so wee need to check both
		if (newAccount || !setupComplete) {
			triggerRefresh(user);
			PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_SETUP_COMPLETE, true)
					.commit();
		}
	}

	public static void triggerRefresh(User user) {
		Bundle b = new Bundle();
		// Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
		b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		ContentResolver.requestSync(AccountService.getAccount(user.getPublicUserId()), CONTENT_AUTHORITY, new Bundle()); // Extras
	}
}
