package dk.itu.projectdee.audiomap;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Dee on 9/29/2015.
 */

public	class ServerConnect extends Thread {

    String date;
    String time;
    String mac;
    String soundLevel;
    public static final String TAG = "TAG";


    public ServerConnect(String date, String time, String mac, String soundLevel) {
        this.date = date;
        this.time = time;
        this.mac = mac;
        this.soundLevel = soundLevel;
    }


    protected void sendData()   {
        thread.start();

    }

    Thread thread = new Thread()    {

        public void run()   {
        try {
            //      String sound = Double.toString(soundLevel);
            Log.d(TAG, "from ServerConnection");
            Log.d(TAG, "date:  " + date);
            Log.d(TAG, "time:  " + time);
            Log.d(TAG, "mac" + mac);
            Log.d(TAG, "soundlevel:  " + soundLevel);

            URL url = new URL("http://martinutzon.dk/cloud/index.php");
            URLConnection connection = url.openConnection();
            JSONObject json = new JSONObject();
            json.put("date", date);
            json.put("Time", time);
            json.put("mac", mac);
            json.put("Soundlevel:  ", soundLevel);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(json.toString());
            out.close();





        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        }



//        };thread.start();
//
//
//        String sound = Double.toString(soundLevel);
//        Log.d(TAG, "from ServerConnection");
//        Log.d(TAG, "date:  " + date);
//        Log.d(TAG, "time:  " + time);
//        Log.d(TAG, "mac" + mac);
//        Log.d(TAG, "soundlevel" + sound);

//        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-mm-dd");
//        SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm:ss");
//        String date = dateformat.format(new Date());
//        String time = timeformat.format(new Date());
//

    };


}

