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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.stericson.RootTools.*;

public class MainActivity extends Activity {
    RtlTask mTask;
    private static final String TAG = "SDRWeather";
    private Cursor events;
    private Cursor fips;
    private EventDatabase eventdb;
    private FipsDatabase fipsdb;
    private Spinner spinner1;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eventdb = new EventDatabase(this);
        fipsdb = new FipsDatabase(this);
        
        // Get data root
        String dataRoot = getApplicationContext().getFilesDir().getParentFile().getPath();
        String binDir = dataRoot + "/nativeFolder/";
    	// Create directory for binaries
    	File nativeDirectory = new File(binDir);
    	nativeDirectory.mkdirs();
    	// Copy binaries
    	copyFile("nativeFolder/test",dataRoot + "/nativeFolder/multimon",getBaseContext());
    	copyFile("nativeFolder/rtl_fm",dataRoot + "/nativeFolder/rtl_fm",getBaseContext());
    	// Set execute permissions
        StringBuilder command = new StringBuilder("chmod 700 ");
        command.append(dataRoot + "/nativeFolder/multimon");
        StringBuilder command2 = new StringBuilder("chmod 700 ");
        command2.append(dataRoot + "/nativeFolder/rtl_fm");
        try {
			Runtime.getRuntime().exec(command.toString());
			Runtime.getRuntime().exec(command2.toString());
		} catch (IOException e) {
		}
    }

    public void onClickStart(View view) {
    	if (RootTools.isRootAvailable()) {
    		// Get Frequency
    		spinner1 = (Spinner) findViewById(R.id.spinner1);
    		String freq = String.valueOf(spinner1.getSelectedItem());
    		// Call for process to start
    		mTask = new RtlTask();
    		mTask.execute(freq);
    	} else {
    		Toast.makeText(MainActivity.this,
    				"Root Not Available!",
    					Toast.LENGTH_SHORT).show();
    	}
    }
    public void onClickStop(View view) {
    	mTask.stop();
    }
    /*
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
	*/
    class RtlTask extends AsyncTask<String, Void, Void> {
        PipedOutputStream mPOut;
        PipedInputStream mPIn;
        LineNumberReader mReader;
        Process mProcess;

        TextView evLvlText = (TextView) findViewById(R.id.textView7);
        TextView evDescText = (TextView) findViewById(R.id.textView8);
        TextView countiesText = (TextView) findViewById(R.id.textView9);
        TextView orgText = (TextView) findViewById(R.id.textView1);
        TextView purgeTimeText = (TextView) findViewById(R.id.textView4);
        TextView issueTimeText = (TextView) findViewById(R.id.textView5);
        TextView callsignText = (TextView) findViewById(R.id.textView6);
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
            	Log.d(TAG, "Excuting command");
            	String dataRoot = getApplicationContext().getFilesDir().getParentFile().getPath();
            	Log.d(TAG, "Got data root: "+ dataRoot);
            	Log.d(TAG, "Frequency Selected: "+ params[0]);
            	//String[] cmd = { "/system/xbin/su", "-c", dataRoot + "/nativeFolder/rtl_fm -N -f "+ params[0] + "M -s 22.5k -g 50 | " + dataRoot + "/nativeFolder/multimon -a EAS -q -t raw -" };
            	//String[] cmd = { "/system/xbin/su", "-c", dataRoot + "/nativeFolder/rtl_fm -N -f 162.546M -s 22.5k -g 50 | " + dataRoot + "/nativeFolder/multimon -a EAS -q -t raw -" };
            	String[] cmd = { "/system/xbin/su", "-c", dataRoot + "/nativeFolder/multimon" };
                mProcess = new ProcessBuilder()
                    .command(cmd)
                    .redirectErrorStream(true)
                    .start();

                try {
                    InputStream in = mProcess.getInputStream();
                    OutputStream out = mProcess.getOutputStream();
                    byte[] buffer = new byte[1024];
                    int count;

                    // in -> buffer -> mPOut -> mReader -> 1 line of information to parse
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
        @SuppressLint("SimpleDateFormat")
		@Override
        protected void onProgressUpdate(Void... values) {
            try {
                // Is a line ready to read from the command?
                while (mReader.ready()) {
                	String currentLine = mReader.readLine();
                	// Display command output
                    mText.append(currentLine + "\n");
                    Log.d(TAG, "Output: " + currentLine);
                    // Check for alert
                    if (currentLine.contains("EAS:")) {
        				Log.d(TAG, "Found EAS Alert, parsing.....");
        				// Start parsing message
        				String [] rawEASMsg = currentLine.split(":");
        				rawEASMsg[1] = rawEASMsg[1].trim();
        				String [] easMsg = rawEASMsg[1].split("-");
        				int size = easMsg.length;
        				Log.d(TAG, "# of fields: " + size);
        				/*
        				 * Information from: http://en.wikipedia.org/wiki/Specific_Area_Message_Encoding
        				 * 
        				 * ORG Ñ Originator code; programmed per unit when put into operation:
        				 * * PEP Ð Primary Entry Point Station; President or other authorized national officials
        				 * * CIV Ð Civil authorities; i.e. Governor, state/local emergency management, local police/fire officials
        				 * * WXR Ð National Weather Service (or Environment Canada.); Any weather-related alert
        				 * * EAS Ð EAS Participant; Broadcasters. Generally only used with test messages.
        				 */
        				String org = easMsg[1];
        				Log.d(TAG, "Originator Code: " + org);
        				orgText.setText("Originator Code: " + org);
        				/*
        				 * EEE Ñ Event code; programmed at time of event
        				 */
        				String eee = easMsg[2];
        				Log.d(TAG, "Event Code: " + eee);
        				//Look up event code in database, return level and description
        				Log.d(TAG, "Looking up event code information for: " + eee);
        				events = eventdb.getEventInfo(eee);
        				if( events != null && events.moveToFirst() ){
        					String eventlevel = events.getString(events.getColumnIndex("eventlevel"));
        					evLvlText.setText(eventlevel);
        					if("Test".equals(eventlevel)){
        						evLvlText.setBackgroundResource(R.color.white);
        					}else if("Warning".equals(eventlevel)){
        						evLvlText.setBackgroundResource(R.color.red);
        					}else if("Watch".equals(eventlevel)){
        						evLvlText.setBackgroundResource(R.color.yellow);
        					}else if("Advisory".equals(eventlevel)){
        						evLvlText.setBackgroundResource(R.color.green);
        					}
        					evDescText.setText("Event: " + events.getString(events.getColumnIndex("eventdesc")));
        				}
        				
        				/*
        				 * PSSCCC Ñ Location codes (up to 31 location codes per message), each beginning with a dash character; 
        				 * programmed at time of event In the United States, the first digit (P) is zero if the entire county or area 
        				 * is included in the warning, otherwise, it is a non-zero number depending on the location of the emergency. 
        				 * In the United States, the remaining five digits are the FIPS state code (SS) and FIPS county code (CCC). 
        				 * The entire state may be specified by using county number 000 (three zeros). In Canada, all six digits specify 
        				 * the Canadian Location Code, which corresponds to a specific forecast region as used by the Meteorological 
        				 * Service of Canada. All forecast region numbers are six digits with the first digit always zero.
        				 */
        				String [] temp = easMsg[size - 3].split("\\+");
        				easMsg[size - 3] = temp[0];
        				int j=0;
        				String [] locationCodes = new String[size - 5];
        				countiesText.setText("Counties Affected: ");
        				for (int i=3; i < size - 2; i++) {
        					locationCodes[j] = easMsg[i];
        					Log.d(TAG, "Location Code: " + locationCodes[j]);

            				//Look up fips code in database, return county and state
        					String fipscode = locationCodes[j].substring(1, 6);
            				Log.d(TAG, "Looking up county/state for fips code: " + fipscode);
            				fips = fipsdb.getCountyState(fipscode);
            				if( fips != null && fips.moveToFirst() ){
            					Log.d(TAG, "Location: " + fips.getString(fips.getColumnIndex("county")) + ", " + fips.getString(fips.getColumnIndex("state")));
            					countiesText.append(fips.getString(fips.getColumnIndex("county")) + ", " + fips.getString(fips.getColumnIndex("state")) + "\n");
            				}
            				//fips.close();
        					j++;
        				}
        				/*
        				 * TTTT Ñ In the format hhmm, using 15 minute increments up to one hour, using 30 minute increments up to six hours,
        				 * and using hourly increments beyond six hours. Weekly and monthly tests sometimes have a 12 hour or greater
        				 * purge time to assure users have an ample opportunity to verify reception of the test event messages; 
        				 * however; 15 minutes is more common, especially on NOAA Weather Radio's tests.
        				 */
        				String purgeTime = temp[1];
        				Log.d(TAG, "Purge time: " + purgeTime);
        				String purgeTimeHour = purgeTime.substring(0,2);
        				String purgeTimeMin = purgeTime.substring(2,4);
        				purgeTimeText.setText("Expires in:" + purgeTimeHour + "h" + purgeTimeMin + "m");
        				
        				/*
        				 * JJJHHMM Ñ Exact time of issue, in UTC, (without time zone adjustments).
        				 * JJJ is the Ordinal date (day) of the year, with leading zeros
        				 * HHMM is the hours and minutes (24-hour format), in UTC, with leading zeros
        				 */
        				String timeOfIssue = easMsg[size - 2];
        				Log.d(TAG, "Time of issue (Ordinal Date): " + timeOfIssue);
        				
        				// Parse and convert date and time
        				String jjj = timeOfIssue.substring(0, 3);
        				String hh = timeOfIssue.substring(3, 5);
        				String mm = timeOfIssue.substring(5, 7);
        				int day = Integer.parseInt(jjj);
        				int year = Calendar.getInstance().get(Calendar.YEAR);
        				String convDate = formatOrdinal(year, day);
        				Log.d(TAG, "Time of issue (UTC): " + convDate + " " + hh + ":" + mm + " UTC");
        				
        				// Convert UTC to local time 
        				SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        				utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        				Date date = utcFormat.parse(convDate + "T" + hh + ":" + mm + ":00.000Z");
        				SimpleDateFormat pstFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        				pstFormat.setTimeZone(TimeZone.getDefault());

        				Log.d(TAG, "Time of issue (Local): " + pstFormat.format(date));
        				issueTimeText.setText("Time of issue: " + pstFormat.format(date));
        				
        				/*
        				 * LLLLLLLL Ñ Eight-character station callsign identification, with "/" used instead of "Ð" (such as the first eight
        				 * letters of a cable headend's location, WABC/FM for WABC-FM, or KLOX/NWS for a weather radio station
        				 * programmed from Los Angeles).
        				 */
        				String callSign = easMsg[size - 1];
        				Log.d(TAG, "Call Sign: " + callSign);
        				callsignText.setText("Call Sign: " + callSign);

        		    }
                    
                }
            } catch (IOException t) {
            } catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
 	// Convert Ordinal date format to simple
	@SuppressLint("SimpleDateFormat")
	static String formatOrdinal(int year, int day) {
		  Calendar cal = Calendar.getInstance();
		  cal.clear();
		  cal.set(Calendar.YEAR, year);
		  cal.set(Calendar.DAY_OF_YEAR, day);
		  Date date = cal.getTime();
		  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		  return formatter.format(date);
	}
}