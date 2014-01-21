package org.thecongers.sdrweather;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.stericson.RootTools.*;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	TextView view;
	Process nativeApp;
	
	// File Copy Function
	private static void copyFile(String assetPath, String localPath, Context context) {
	    try {
	        InputStream in = context.getAssets().open(assetPath);
	        FileOutputStream out = new FileOutputStream(localPath);
	        int read;
	        byte[] buffer = new byte[4096];
	        while ((read = in.read(buffer)) > 0) {
	            out.write(buffer, 0, read);
	        }
	        out.close();
	        in.close();
	        Log.d(TAG, "File " + assetPath + " copied successfully.");

	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	
    	// Create a File object for the parent directory
    	File nativeDirectory = new File("/data/data/org.thecongers.sdrweather/nativeFolder/");
    	// have the object build the directory structure, if needed.
    	nativeDirectory.mkdirs();
    	// Copy binaries
    	copyFile("nativeFolder/multimon","/data/data/org.thecongers.sdrweather/nativeFolder/multimon",getBaseContext());
    	copyFile("nativeFolder/rtl_fm","/data/data/org.thecongers.sdrweather/nativeFolder/rtl_fm",getBaseContext());
    	// Set execute permissions
        StringBuilder command = new StringBuilder("chmod 700 ");
        command.append("/data/data/org.thecongers.sdrweather/nativeFolder/multimon");
        try {
			Runtime.getRuntime().exec(command.toString());
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
        // Set execute permissions
        StringBuilder command2 = new StringBuilder("chmod 700 ");
        command2.append("/data/data/org.thecongers.sdrweather/nativeFolder/rtl_fm");
        try {
			Runtime.getRuntime().exec(command2.toString());
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
    }
    
    // When Start button is touched
    public void onClickStart(View view) {
        // Check for root
        if (RootTools.isRootAvailable()) {
        	Process nativeApp = null;
        	//Start
        	try {
        		String[] cmd = { "/system/xbin/su", "-c", "/data/data/org.thecongers.sdrweather/nativeFolder/rtl_fm -N -f 162.546M -s 22.5k -g 50 | /data/data/org.thecongers.sdrweather/nativeFolder/multimon -a EAS -q -t raw -" };
        		//String[] cmd = { "/system/xbin/su", "-c", "/data/data/org.thecongers.sdrweather/nativeFolder/multimon" };
        		nativeApp = Runtime.getRuntime().exec(cmd);
        	} catch (IOException e3) {
        		// TODO Auto-generated catch block
        		e3.printStackTrace();
        	}
        	//End
        	view =  (TextView) findViewById(R.id.TextView02);
        	BufferedReader reader = new BufferedReader(new InputStreamReader(nativeApp.getInputStream()));
        	String line;
        	int read;
        	char[] buffer = new char[4096];
        	StringBuffer output = new StringBuffer();
        	
        	try {
        		//while ((line = reader.readLine()) != null) {
        	    //    System.out.println(line); 
        	    //    view.append(line);
        	    //}
        		while ((read = reader.read(buffer)) > 0) {
        			output.append(buffer, 0, read);
        			((TextView) view).append(output.toString());
        		}
        		reader.close();
        	} catch (IOException e1) {
        		// TODO Auto-generated catch block
        		e1.printStackTrace();
        	}

        	// Waits for the command to finish.
        	//try {
        	//	nativeApp.waitFor();
        	//} catch (InterruptedException e) {
        		// TODO Auto-generated catch block
        	//	e.printStackTrace();
        	//}

        	//String nativeOutput =  output.toString();
        	//TextView view =  (TextView) findViewById(R.id.TextView02);
        	//view.setText(nativeOutput);
        } else {
        	String error = "No SU!";
        	view =  (TextView) findViewById(R.id.TextView02);
        	((TextView) view).setText(error);
        	RootTools.offerSuperUser(MainActivity.this);
        }
        
    }
    // When Stop button is pressed
    public void onClickStop(View view) {
    	nativeApp.destroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
