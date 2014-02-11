package org.thecongers.sdrweather;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.ScrollingMovementMethod;

import com.stericson.RootTools.*;

public class MainActivity extends Activity {
    RtlTask mTask;
    private static final String TAG = "SDRWeather";
    private Cursor events;
    private Cursor fips;
    private Cursor clc;
    private EventDatabase eventdb;
    private FipsDatabase fipsdb;
    private ClcDatabase clcdb;
    private Spinner spinner1;
    private SharedPreferences sharedPrefs;
    private static final int SETTINGS_RESULT = 1;
    private String dataRoot;
    boolean m_stop = false;
    AudioTrack m_audioTrack;
    Thread m_audioThread;
    Button startButten;
    ImageButton playButten;
    ImageButton stopButten;
    TextView mText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eventdb = new EventDatabase(this);
        fipsdb = new FipsDatabase(this);
        clcdb = new ClcDatabase(this);

        // Setup Buttons
        startButten=(Button)findViewById(R.id.button1);
        playButten=(ImageButton)findViewById(R.id.imageButton1);
        playButten.setEnabled(false);
        stopButten=(ImageButton)findViewById(R.id.imageButton2);
        stopButten.setEnabled(false);
        
        mText = (TextView) findViewById(R.id.TextView02);
        mText.setMovementMethod(new ScrollingMovementMethod());
        
        //Set Initial Frequency From Preferences
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int freq = Integer.parseInt(sharedPrefs.getString("prefDefaultFreq", "6"));
        Log.d(TAG, "Freq set to: " + freq );
        spinner1.setSelection(freq);
        
        // Get data root
        dataRoot = getApplicationContext().getFilesDir().getParentFile().getPath();
        String binDir = dataRoot + "/nativeFolder/";
    	// Create directory for binaries
    	File nativeDirectory = new File(binDir);
    	nativeDirectory.mkdirs();
    	// Copy binaries
    	//copyFile("nativeFolder/test",dataRoot + "/nativeFolder/multimon-ng",getBaseContext());
    	copyFile("nativeFolder/multimon-ng",dataRoot + "/nativeFolder/multimon-ng",getBaseContext());
    	copyFile("nativeFolder/rtl_fm",dataRoot + "/nativeFolder/rtl_fm",getBaseContext());
    	// Set execute permissions
        StringBuilder command = new StringBuilder("chmod 700 ");
        command.append(dataRoot + "/nativeFolder/multimon-ng");
        StringBuilder command2 = new StringBuilder("chmod 700 ");
        command2.append(dataRoot + "/nativeFolder/rtl_fm");
        // Create named pipe for audio
        StringBuilder command3 = new StringBuilder("mkfifo ");
        command3.append(dataRoot + "/pipe");
        try {
			Runtime.getRuntime().exec(command.toString());
			Runtime.getRuntime().exec(command2.toString());
			Runtime.getRuntime().exec(command3.toString());
		} catch (IOException e) {
		}
    }

    public void onClickStart(View view)
    {
    	if (RootTools.isRootAvailable() && RootTools.isBusyboxAvailable()) {
    		// Get Frequency and gain
    		String freq = String.valueOf(spinner1.getSelectedItem());
    		String gain = sharedPrefs.getString("prefGain", "42");
    		// Call for process to start
    		mTask = new RtlTask();
    		mTask.execute(freq,gain);
    		
    		startButten.setEnabled(false);
    		playButten.setEnabled(true);
    		if (sharedPrefs.getBoolean("prefStartAudio", false)) {
            	playButten.performClick();
            }
    	} else {
    		// Display message about lack of root
    		if (!RootTools.isRootAvailable()) {
    			Toast.makeText(MainActivity.this,
    					"Root Access Not Available!",
    					Toast.LENGTH_SHORT).show();
    		} else if (!RootTools.isBusyboxAvailable()) {
    			RootTools.offerBusyBox(MainActivity.this);   			
    		}
    	}
    }
    
    public void onClickStop(View view)
    {
    	stopButten.performClick();
    	
    	mTask.stop();
    	startButten.setEnabled(true);
    	playButten.setEnabled(false);
    }
    
    public void onClickAudioStart(View view)
    {
    	// Start playback
    	Log.d(TAG, "Start audio pressed" );
    	audioStart();
    	
    	playButten.setEnabled(false);
    	stopButten.setEnabled(true);	
    }
    
    public void onClickAudioStop(View view)
    {
    	// Stop playback
    	Log.d(TAG, "Stop audio pressed" );
    	audioStop();
    	
    	playButten.setEnabled(true);
    	stopButten.setEnabled(false);	
    }
    
    Runnable m_audioGenerator = new Runnable()
    {       
        public void run()
        {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            
            FileInputStream audioStream = null;
            try {
            	Log.d(TAG, "Setting audio stream to: " + dataRoot + "/pipe" );
                audioStream = new FileInputStream(dataRoot + "/pipe");
            } catch (FileNotFoundException e) {
            	e.printStackTrace();
            	Log.d(TAG, "Named Pipe Not Found" );
            }
            int bytesRead = 0;
            byte [] audioData = new byte[1024];
            Log.d(TAG, "Write Audio Out" );
            while(!m_stop) {
            	try {
					bytesRead = audioStream.read(audioData, 0, 1024);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	m_audioTrack.write(audioData, 0, bytesRead);   
            	//m_audioTrack.write(audioData, 0, audioData.length); 
            }
        }
    };

    void audioStart()
    {
        m_stop = false;
        m_audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 22050, AudioFormat.CHANNEL_OUT_MONO,
                                        AudioFormat.ENCODING_PCM_16BIT, 22050 /* 1 second buffer */,
                                        AudioTrack.MODE_STREAM);
        m_audioTrack.play();
        m_audioThread = new Thread(m_audioGenerator);
        m_audioThread.start();
    }

    void audioStop()
    {
        m_stop = true;          
        m_audioTrack.stop();
    }   

    // Draw Options Menu
    @Override
	public boolean onCreateOptionsMenu(Menu menu)
    {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    
    // When Settings Menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch (item.getItemId()) {
            case R.id.action_settings:
                // Settings Menu was selected
            	Log.d(TAG, "Settings Menu was selected");
            	Intent i = new Intent(getApplicationContext(), UserSettingActivity.class);
                startActivityForResult(i, SETTINGS_RESULT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SETTINGS_RESULT)
        {
        	updateUserSettings();
        }
    }
    
    private void updateUserSettings() 
    {
    	 int freq = Integer.parseInt(sharedPrefs.getString("prefDefaultFreq", "6"));
         Log.d(TAG, "Updating freqency spinner to index: " + freq );
         spinner1.setSelection(freq);
     }
    
    private void Notify(String notificationTitle, String notificationMessage, int notificationID) 
    {
    	  NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	  Intent notificationIntent = new Intent(this, MainActivity.class);
    	  PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
    	    	    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	  // Build notification
    	  Notification.Builder builder = new Notification.Builder(this);
    	  builder.setContentTitle(notificationTitle)
          	.setContentText(notificationMessage)
          	.setSmallIcon(R.drawable.app_icon)
          	.setContentIntent(pendingIntent);
    	  Notification notification = builder.build();  
    	  // Hide notification after its been selected
    	  notification.flags |= Notification.FLAG_AUTO_CANCEL;
    	  // Send notification
    	  notificationManager.notify(notificationID, notification);
    	  
    }
 
    class RtlTask extends AsyncTask<String, Void, Void> {
        PipedOutputStream mPOut;
        PipedInputStream mPIn;
        LineNumberReader mReader;
        Process mProcess;

        TextView evLvlText = (TextView) findViewById(R.id.textView7);
        TextView evDescText = (TextView) findViewById(R.id.textView8);
        TextView regionsText = (TextView) findViewById(R.id.textView9);
        TextView orgText = (TextView) findViewById(R.id.textView1);
        TextView purgeTimeText = (TextView) findViewById(R.id.textView4);
        TextView issueTimeText = (TextView) findViewById(R.id.textView5);
        TextView callsignText = (TextView) findViewById(R.id.textView6);

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
            // Kill Processes Fail Safe
            StringBuilder command1 = new StringBuilder("/system/xbin/su -c killall -9 ");
            command1.append("rtl_fm");
            StringBuilder command2 = new StringBuilder("/system/xbin/su -c killall -9 ");
            command2.append("multimon-ng");
            try {
    			Runtime.getRuntime().exec(command1.toString());
    			Runtime.getRuntime().exec(command2.toString());
    		} catch (IOException e) {
    		}
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
            	Log.d(TAG, "Excuting command");
            	String dataRoot = getApplicationContext().getFilesDir().getParentFile().getPath();
            	Log.d(TAG, "Got data root: "+ dataRoot);
            	Log.d(TAG, "Frequency Selected: "+ params[0]);
            	Log.d(TAG, "Gain: "+ params[1]);
            	String[] cmd = { "/system/xbin/su", "-c", dataRoot + "/nativeFolder/rtl_fm -f " + params[0] + "M -s 22050 -g " + params[1] + " | tee " + dataRoot + "/pipe | " + dataRoot + "/nativeFolder/multimon-ng -a EAS -q -t raw -" };
            	//String[] cmd = { "/system/xbin/su", "-c", dataRoot + "/nativeFolder/multimon-ng" };
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
        				String org = null;
        				String eventlevel = null;
        				String eventdesc = null;
        				String localTimeOfIssue = null;
        				String callSign = null;
        				int notificationID = 0;
        				
        				// Start parsing message
        				String [] rawEASMsg = currentLine.split(":");
        				rawEASMsg[1] = rawEASMsg[1].trim();
        				String [] easMsg = rawEASMsg[1].split("-");
        				int size = easMsg.length;
        				Log.d(TAG, "# of fields: " + size);
        				
        				//Check to see if its a real message
        				if (size > 5 ) {
        					/*
        					 * Information from: http://en.wikipedia.org/wiki/Specific_Area_Message_Encoding
        					 * 
        					 * ORG Ñ Originator code; programmed per unit when put into operation:
        					 * * PEP Ð Primary Entry Point Station; President or other authorized national officials
        					 * * CIV Ð Civil authorities; i.e. Governor, state/local emergency management, local police/fire officials
        					 * * WXR Ð National Weather Service (or Environment Canada.); Any weather-related alert
        					 * * EAS Ð EAS Participant; Broadcasters. Generally only used with test messages.
        					 */
        					org = easMsg[1];
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
        						eventlevel = events.getString(events.getColumnIndex("eventlevel"));
        						evLvlText.setText(eventlevel);
        						if("Test".equals(eventlevel)){
        							evLvlText.setBackgroundResource(R.color.white);
        							notificationID = 1;
        						}else if("Warning".equals(eventlevel)){
        							evLvlText.setBackgroundResource(R.color.red);
        							notificationID = 2;
        						}else if("Watch".equals(eventlevel)){
        							evLvlText.setBackgroundResource(R.color.yellow);
        							notificationID = 3;
        						}else if("Advisory".equals(eventlevel)){
        							evLvlText.setBackgroundResource(R.color.green);
        							notificationID = 4;
        						}
        						eventdesc = events.getString(events.getColumnIndex("eventdesc"));
        						evDescText.setText("Event: " + eventdesc);
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
        					
        					regionsText.setText("Regions Affected: ");
        					//Get Country From Preferences
        					int country = Integer.parseInt(sharedPrefs.getString("prefDefaultCountry", "0"));
        					Log.d(TAG, "Country Code: " + country);
        					if( country == 0 ){
        						for (int i=3; i < size - 2; i++) {
        							locationCodes[j] = easMsg[i];
        							Log.d(TAG, "Location Code: " + locationCodes[j]);
        							
        							//Look up fips code in database, return county and state
        							String fipscode = locationCodes[j].substring(1, 6);
        							Log.d(TAG, "Looking up county/state for fips code: " + fipscode);
        							fips = fipsdb.getCountyState(fipscode);
        							if( fips != null && fips.moveToFirst() ){
        								Log.d(TAG, "Location: " + fips.getString(fips.getColumnIndex("county")) + ", " + fips.getString(fips.getColumnIndex("state")));
        								regionsText.append(fips.getString(fips.getColumnIndex("county")) + ", " + fips.getString(fips.getColumnIndex("state")) + "; ");
        							}
        							//fips.close();
        							j++;
        						}
        					}else if( country == 1 ){
        						for (int i=3; i < size - 2; i++) {
        							locationCodes[j] = easMsg[i];
        							Log.d(TAG, "Location Code: " + locationCodes[j]);

        							//Look up clc code in database, return region and province/territory
        							String clccode = locationCodes[j].substring(1, 6);
        							Log.d(TAG, "Looking up region and province/territory information for clc code: " + clccode);
        							clc = clcdb.getCountyState(clccode);
        							if( clc != null && clc.moveToFirst() ){
        								Log.d(TAG, "Location: " + clc.getString(clc.getColumnIndex("region")) + ", " + clc.getString(clc.getColumnIndex("provinceterritory")));
        								regionsText.append(clc.getString(clc.getColumnIndex("region")) + ", " + clc.getString(clc.getColumnIndex("provinceterritory")) + "; ");
        							}
        							//clc.close();
        							j++;
        						}
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
        					SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        					defaultFormat.setTimeZone(TimeZone.getDefault());
        					
        					localTimeOfIssue = defaultFormat.format(date);
        					Log.d(TAG, "Time of issue (Local): " + localTimeOfIssue);
        					issueTimeText.setText("Time of issue: " + localTimeOfIssue);
        				
        					/*
        					 * LLLLLLLL Ñ Eight-character station callsign identification, with "/" used instead of "Ð" (such as the first eight
        					 * letters of a cable headend's location, WABC/FM for WABC-FM, or KLOX/NWS for a weather radio station
        					 * programmed from Los Angeles).
        					 */
        					callSign = easMsg[size - 1];
        					Log.d(TAG, "Call Sign: " + callSign);
        					callsignText.setText("Call Sign: " + callSign);
        					
        					// Send a notification
        					Notify("EAS " + eventlevel + " from " + callSign,
        							eventdesc + " was issued", notificationID);
        				
        					}

        		    } else if (currentLine.contains("No supported devices found")) {
        		    	Toast.makeText(MainActivity.this,
            					"No supported device found!",
            					Toast.LENGTH_SHORT).show();
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