package dk.itu.projectdee.audiomap;

import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Dee on 9/27/2015.
 */
public class dBMeter {

    public static final String LOGTAG = "MyLog";
    MediaRecorder mRecorder;
    Thread runner;
    static int counter = 0;
    static double tenReadings = 0;

    final Runnable updater = new Runnable() {

        public void run() {
            dbView();
        }
    };
    final Handler mHandler = new Handler();

    public void startTread() {

        startRecorder();
        Log.i(LOGTAG, "dBMeter");

        //Log.i("Build", "------------ "+ Build.VERSION.SDK_INT);

        if (runner == null) {
            runner = new Thread() {
                public void run() {
                    while (runner != null) {
                        try {
                            //counter++;
                            if (counter > 10) {
                                // runner.interrupt();
                                runner = null;

                            }
                            Thread.sleep(1000);
                            Log.i("Noise", "Tock" + counter);

                        } catch (InterruptedException e) {
                        }
                        ;
                        mHandler.post(updater);

                    }
                    counter = 0;
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }
    }

    /*
    public void onResume() {
        super.onResume();
        startRecorder();
    }

    public void onPause() {
        super.onPause();
        stopRecorder();
    }*/

    public void startRecorder() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " +
                        android.util.Log.getStackTraceString(ioe));

            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
            try {
                mRecorder.start();
            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
        }
    }

    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void dbView() {
        double amp2 = soundDb(getAmplitude());
        if (amp2 > 0) {
            tenReadings += amp2;
        }
        //mDbView.setText(Double.toString(amp2) + " dB");
        Log.i(LOGTAG,"Reading: "+ Double.toString(amp2) + " dB");
        //Log.i(LOGTAG,"AVG "+ tenReadings/ counter);
        if (counter == 0) {
            double avgReading = tenReadings / 10;

            Log.i(LOGTAG, "AVG " + tenReadings / 10);
        }
    }

    public double soundDb(double ampl) {
        return 20 * Math.log10(ampl / 1);
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude());
        else
            return 0;
    }
}
