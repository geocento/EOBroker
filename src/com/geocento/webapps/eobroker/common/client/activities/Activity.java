package com.geocento.webapps.eobroker.common.client.activities;

import java.util.ArrayList;
import java.util.List;

public class Activity {

	private static List<ActivityCloseHandler> activityCloseHandlers = new ArrayList<ActivityCloseHandler>();

	/*
	 * add a close handler to take an action when the Activity.triggerActivityClose is called
	 * Activity.triggerActivityClose is normally called by the current activity when closing or sometimes by tabs in a view
	 */
	static public void addCloseHandler(ActivityCloseHandler closeHandler) {
		activityCloseHandlers.add(closeHandler);
	}
	
	/*
	 * call this method to trigger all widgets having registered for the activity close handler
	 * Activity.triggerActivityClose is normally called by the current activity when closing or sometimes by tabs in a view
	 */
	static public void triggerActivityClose() {
		for(ActivityCloseHandler handler : activityCloseHandlers) {
			try {
				handler.onActivityClose();
			} catch(RuntimeException e) {
				System.out.print(e.getMessage());
			}
		}
	}
}
