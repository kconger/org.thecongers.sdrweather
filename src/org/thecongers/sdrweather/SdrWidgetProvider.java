package org.thecongers.sdrweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

public class SdrWidgetProvider extends AppWidgetProvider {
	public static final String TAG = "SDRWeatherWidget";
	 
	   @Override
	   public void onUpdate(Context context, AppWidgetManager appWidgetManager,
	      int[] appWidgetIds) {
	 
	      try {
	         updateWidgetContent(context, appWidgetManager);
	      } catch (Exception e) {
	         Log.d(TAG, "Failed", e);
	      }
	   }
	 
	   public static void updateWidgetContent(Context context,
	      AppWidgetManager appWidgetManager) {
	       
		  EasDatabase easdb = new EasDatabase(context);
		  
	      RemoteViews remoteView = new RemoteViews(context.getPackageName(),      
	            R.layout.sdrweather_appwidget_layout);
	      
	      // Lookup and display latest active event
	      Cursor easmsg = easdb.getActiveEvent();
	      if( easmsg != null && easmsg.moveToFirst() ){
	    	  String eventlevel = easmsg.getString(easmsg.getColumnIndex("level"));
	    	  String eventdesc = easmsg.getString(easmsg.getColumnIndex("desc"));
	    	  remoteView.setTextViewText(R.id.alert, eventlevel + ": " + eventdesc);
	      } else {
	    	  remoteView.setTextViewText(R.id.alert, "No Active Events");
	      }
	 
	      // Launch app when widget is pressed
	      Intent launchAppIntent = new Intent(context, MainActivity.class);
	      PendingIntent launchAppPendingIntent = PendingIntent.getActivity(context, 
	            0, launchAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);     
	      remoteView.setOnClickPendingIntent(R.id.full_widget, launchAppPendingIntent);
	 
	      ComponentName sdrWidget = new ComponentName(context, 
	            SdrWidgetProvider.class);
	      appWidgetManager.updateAppWidget(sdrWidget, remoteView);
	   }

}
