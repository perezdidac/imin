package com.imin.events;

public interface EventListListener {
	public void onEventPeople(int position);

	public void onEventJoin(int position);

	public void onEventEdit(int position);

	public void onEventReopen(int position);

	public void onEventPoll(int position);

	public void onEventRemove(int position);

	public void onEventClose(int position);

	public void onEventShare(int position);
}
