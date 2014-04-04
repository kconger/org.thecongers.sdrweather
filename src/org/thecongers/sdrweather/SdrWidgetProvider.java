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
	    	  StringBuilder message = new StringBuilder(eventdesc);
	    	  
	    	  // Set TextView background color to represent event level
	    	  if("Test".equals(eventlevel)){
	    		  remoteView.setInt(R.id.alert, "setBackgroundColor", 
	    				  android.graphics.Color.WHITE);
	    	  }else if("Warning".equals(eventlevel)){
	    		  remoteView.setInt(R.id.alert, "setBackgroundColor", 
	    				  android.graphics.Color.RED);
	    	  }else if("Watch".equals(eventlevel)){
	    		  remoteView.setInt(R.id.alert, "setBackgroundColor", 
	    				  android.graphics.Color.YELLOW);
	    	  }else if("Advisory".equals(eventlevel)){
	    		  remoteView.setInt(R.id.alert, "setBackgroundColor", 
	    				  android.graphics.Color.GREEN);
	    	  }
	    	  
	    	  int numEvents = easmsg.getCount();
	    	  //Log.d(TAG, "Number of active events: " + String.valueOf(numEvents));
	    	  if (numEvents > 1){
	    		  if (numEvents == 2){
	    			  message.append("\nand " + String.valueOf(numEvents - 1 ) + " other active event..." );
	    		  } else {
	    			  message.append("\nand " + String.valueOf(numEvents - 1 ) + " other active events..." );
	    		  }
	    	  }
	    	  remoteView.setTextViewText(R.id.alert, message);
	      } else {
	    	  remoteView.setTextViewText(R.id.alert, "No Active Events Recieved");
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
