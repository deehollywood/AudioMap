package dk.itu.projectdee.audiomap;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by Dee on 9/27/2015.
 */
//public class dBMeter implements  Average{
public class dBMeter{

    private static final int SAMPLING_RATE = 44100;  // The sampling rate for the audio recorder.
    private RecordingThread mRecordingThread;
    private int mBufferSize;
    private short[] mAudioBuffer;
    private String mDecibelFormat;
    private String mDecibelMaxFormat;
    private long time;
    private long timeDetected;
    private long timeElapsed;
    private static double avgTenReadings;
    public static final String LOGTAG = "MyLog";

    //start method that starts to record on the microphone
    public void startrecording()    {
        // Compute the minimum required audio buffer size and allocate the buffer.
        mBufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudioBuffer = new short[mBufferSize / 2];
        mDecibelFormat = mDecibelMaxFormat = "%1$.1f dB";
        mRecordingThread = new RecordingThread();
        mRecordingThread.start();
    }

    /*@Override
    public double returnSoundLevel() {
        return avgTenReadings;
    }*/

    private class RecordingThread extends Thread {

        private boolean mShouldContinue = true;

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLING_RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
            record.startRecording();
            time = System.currentTimeMillis();
            while (shouldContinue()) {
                record.read(mAudioBuffer, 0, mBufferSize / 2);
                updateDecibelLevel();
            }

            record.stop();
            record.release();
            TrackingService.returnDbResult(avgTenReadings);
        }

        /**
         * Gets a value indicating whether the thread should continue running.
         *
         * @return true if the thread should continue running or false if it should stop
         */
        private synchronized boolean shouldContinue() {
            return mShouldContinue;
        }

        /** Notifies the thread that it should stop running at the next opportunity. */
        public synchronized void stopRunning() {
            mShouldContinue = false;
        }

        /**
         * Computes the decibel level of the current sound buffer and updates the appropriate text
         * view.
         */
        int readingCounter = 0;
        double db = 0;
        double dbMax = 0;
        double readingsAccumulated = 0;
        private void updateDecibelLevel() {
            // Compute the root-mean-squared of the sound buffer and then apply the formula for
            // computing the decibel level, 20 * log_10(rms). This is an uncalibrated calculation
            // that assumes no noise in the samples; with 16-bit recording, it can range from
            // -90 dB to 0 dB.
            double sum = 0;

            for (short rawSample : mAudioBuffer) {
                double sample = rawSample / 32768.0;
                sum += sample * sample;
            }

            double rms = Math.sqrt(sum / mAudioBuffer.length);

            db = (20 * Math.log10(rms) + 90) * 1.1;

            if(db > dbMax){
                dbMax = db;
            }

            timeDetected = System.currentTimeMillis();
            Log.d(LOGTAG, time +"   "+ timeDetected+"    "+String.format(mDecibelFormat, db) + " -- DBMAX: " + String.format(mDecibelMaxFormat, dbMax));
            timeElapsed = timeDetected - time;
            if(timeElapsed > 1500){
                Log.d(LOGTAG, "Counter: " + readingCounter);
                readingsAccumulated += dbMax;
                dbMax = 0;
                readingCounter++;
                if(readingCounter == 10){
                    stopRunning();
                    avgTenReadings = readingsAccumulated/10;
                    Log.d(LOGTAG, "AVG of ten readdings: " + readingsAccumulated / 10);
                }
                time = System.currentTimeMillis();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //was used in the activity
    protected void onPause() {


        if (mRecordingThread != null) {
            mRecordingThread.stopRunning();
            mRecordingThread = null;
        }
    }

/////////////////////////////////////////////////////////////////////////////

    /*public static final String LOGTAG = "MyLog";
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
*/
    /*
    public void onResume() {
        super.onResume();
        startRecorder();
    }

    public void onPause() {
        super.onPause();
        stopRecorder();
    }*/

 /*   public void startRecorder() {
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
*/
}
