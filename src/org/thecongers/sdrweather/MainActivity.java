package org.thecongers.sdrweather;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.stericson.RootTools.*;

public class MainActivity extends Activity {
    RtlTask mTask;
    private static final String TAG = "SDRWeather";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // String dataRoot = getAppContext().getFilesDir().getParentFile().getPath();
        String dataRoot = "/data/data/org.thecongers.sdrweather";
        String binDir = dataRoot + "/nativeFolder/";
    	// Create directory for binaries
    	File nativeDirectory = new File(binDir);
    	nativeDirectory.mkdirs();
    	// Copy binaries
    	copyFile("nativeFolder/multimon",dataRoot + "/nativeFolder/multimon",getBaseContext());
    	copyFile("nativeFolder/rtl_fm",dataRoot + "/nativeFolder/rtl_fm",getBaseContext());
    	// Set execute permissions
        StringBuilder command = new StringBuilder("chmod 700 ");
        command.append(dataRoot + "/nativeFolder/multimon");
        try {
			Runtime.getRuntime().exec(command.toString());
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
        // Set execute permissions
        StringBuilder command2 = new StringBuilder("chmod 700 ");
        command2.append(dataRoot + "/nativeFolder/rtl_fm");
        try {
			Runtime.getRuntime().exec(command2.toString());
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTask = new RtlTask();
        mTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTask.stop();
    }

    class RtlTask extends AsyncTask<String, Void, Void> {
        PipedOutputStream mPOut;
        PipedInputStream mPIn;
        LineNumberReader mReader;
        Process mProcess;
        TextView mText = (TextView) findViewById(R.id.TextView02);
        @Override
        protected void onPreExecute() {
            mPOut = new PipedOutputStream();
            try {
                mPIn = new PipedInputStream(mPOut);
                mReader = new LineNumberReader(new InputStreamReader(mPIn));
            } catch (IOException e) {
                cancel(true);
            }

        }

        public void stop() {
            Process p = mProcess;
            if (p != null) {
                p.destroy();
            }
            cancel(true);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
		Log.d(TAG, "Excuting rtl_fm/multimon");
		String[] cmd = { "/system/xbin/su", "-c", "/data/data/org.thecongers.sdrweather/nativeFolder/rtl_fm -N -f 162.546M -s 22.5k -g 50 | /data/data/org.thecongers.sdrweather/nativeFolder/multimon -a EAS -q -t raw -" };
		//String[] cmd = { "/system/xbin/su", "-c", "/data/data/org.thecongers.sdrweather/nativeFolder/multimon" };
                mProcess = new ProcessBuilder()
                    .command(cmd)
                    .redirectErrorStream(true)
                    .start();

                try {
                    InputStream in = mProcess.getInputStream();
                    OutputStream out = mProcess.getOutputStream();
                    byte[] buffer = new byte[1024];
                    int count;

                    // in -> buffer -> mPOut -> mReader -> 1 line of ping information to parse
                    while ((count = in.read(buffer)) != -1) {
                        mPOut.write(buffer, 0, count);
                        publishProgress();
                    }
                    out.close();
                    in.close();
                    mPOut.close();
                    mPIn.close();
                } finally {
                    mProcess.destroy();
                    mProcess = null;
                }
            } catch (IOException e) {
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            try {
                // Is a line ready to read from the command?
                while (mReader.ready()) {
                    // This just displays the output, you should typically parse it I guess.
                    mText.setText(mReader.readLine());
                    Log.d(TAG, "Output: " + mReader.readLine());
                    if (mReader.readLine().contains("EAS")) {
        				Log.d(TAG, "Found EAS Alert");
        		    }
                    
                }
            } catch (IOException t) {
            }
        }
    }
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
}