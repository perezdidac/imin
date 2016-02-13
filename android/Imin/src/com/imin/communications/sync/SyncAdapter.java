package com.imin.communications.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.imin.api.Events;

class SyncAdapter extends AbstractThreadedSyncAdapter {

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
			SyncResult syncResult) {

		// Synchronize events
		Events.syncEvents(getContext(), true);

		// Synchronize events
		Events.syncEventPictures(getContext());

		// syncResult.stats.numParseExceptions++;
	}

}
