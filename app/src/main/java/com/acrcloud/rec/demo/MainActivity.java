package com.acrcloud.rec.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.acrcloud.rec.sdk.ACRCloudClient;
import com.acrcloud.rec.sdk.ACRCloudConfig;
import com.acrcloud.rec.sdk.IACRCloudListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;

public class MainActivity extends Activity implements IACRCloudListener{

	private ACRCloudClient mClient;
	private ACRCloudConfig mConfig;

	private TextView mVolume, mResult, tv_time;

	private boolean mProcessing = false;
	private boolean initState = false;

	private String path = "";

	private long startTime = 0;
	private long stopTime = 0;
	String AdName = "\n";

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DownloadFilesTask asyn = new DownloadFilesTask();
		//Threadpool
		asyn.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		path = Environment.getExternalStorageDirectory().toString()
				+ "/acrcloud/model";

		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}

		mVolume = (TextView) findViewById(R.id.volume);
		mResult = (TextView) findViewById(R.id.result);
		tv_time = (TextView) findViewById(R.id.time);

		Button startBtn = (Button) findViewById(R.id.start);
		startBtn.setText(getResources().getString(R.string.start));

		Button stopBtn = (Button) findViewById(R.id.stop);
		stopBtn.setText(getResources().getString(R.string.stop));

		findViewById(R.id.stop).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						stop();
					}
				});

		Button cancelBtn = (Button) findViewById(R.id.cancel);
		cancelBtn.setText(getResources().getString(R.string.cancel));

		findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				start();
			}
		});

		findViewById(R.id.cancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						cancel();
					}
				});


        this.mConfig = new ACRCloudConfig();
        this.mConfig.acrcloudListener = this;
        this.mConfig.context = this;
        this.mConfig.host = "eu-west-1.api.acrcloud.com";
        this.mConfig.dbPath = path; // offline db path, you can change it with other path which this app can access.
        this.mConfig.accessKey = "7127d856d06ee29fa4d43ca7f0995b60";
        this.mConfig.accessSecret = "txhXStHgobti6dMfjAFGxWV6qb8o2ocrGgllEPeS";
        this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;
        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_LOCAL;
        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_BOTH;

        this.mClient = new ACRCloudClient();
        // If reqMode is REC_MODE_LOCAL or REC_MODE_BOTH,
        // the function initWithConfig is used to load offline db, and it may cost long time.
        this.initState = this.mClient.initWithConfig(this.mConfig);
        if (this.initState) {
            this.mClient.startPreRecord(3000); //start prerecord, you can call "this.mClient.stopPreRecord()" to stop prerecord.
        }
	}


	public void start() {

		if (!this.initState) {
            Toast.makeText(this, "init error", Toast.LENGTH_SHORT).show();
            return;
        }

		if (!mProcessing) {
			mProcessing = true;
			mVolume.setText("");
			mResult.setText("");
			if (this.mClient == null || !this.mClient.startRecognize()) {
				mProcessing = false;
				mResult.setText("start error!");
			}
            startTime = System.currentTimeMillis();
		}
	}



	protected void stop() {
		if (mProcessing && this.mClient != null) {
			this.mClient.stopRecordToRecognize();
		}
		mProcessing = false;

		stopTime = System.currentTimeMillis();
	}

	protected void cancel() {
		if (mProcessing && this.mClient != null) {
			mProcessing = false;
			this.mClient.cancel();
			tv_time.setText("");
			mResult.setText("");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onResult(String result) {
		//if (this.mClient != null) {
		//	this.mClient.cancel();
		//	mProcessing = false;
		//}

		String tres = "\n";


		try {
		    JSONObject j = new JSONObject(result);
		    JSONObject j1 = j.getJSONObject("status");
		    int j2 = j1.getInt("code");
		    if(j2 == 0){
		    	JSONObject metadata = j.getJSONObject("metadata");
		    	//
		    	if (metadata.has("humming")) {
		    		JSONArray hummings = metadata.getJSONArray("humming");
		    		for(int i=0; i<hummings.length(); i++) {
		    			JSONObject tt = (JSONObject) hummings.get(i);
		    			String title = tt.getString("title");
		    			JSONArray artistt = tt.getJSONArray("artists");
		    			JSONObject art = (JSONObject) artistt.get(0);
		    			String artist = art.getString("name");
		    			tres = tres + (i+1) + ".  " + title + "\n";
		    		}
		    	}
		    	if (metadata.has("music")) {
		    		JSONArray musics = metadata.getJSONArray("music");
		    		for(int i=0; i<musics.length(); i++) {
		    			JSONObject tt = (JSONObject) musics.get(i);
		    			String title = tt.getString("title");
		    			JSONArray artistt = tt.getJSONArray("artists");
		    			JSONObject art = (JSONObject) artistt.get(0);
		    			String artist = art.getString("name");
		    			tres = tres + (i+1) + ".  Title: " + title + "    Artist: " + artist + "\n";
		    		}
		    	}
		    	if (metadata.has("streams")) {
		    		JSONArray musics = metadata.getJSONArray("streams");
		    		for(int i=0; i<musics.length(); i++) {
		    			JSONObject tt = (JSONObject) musics.get(i);
		    			String title = tt.getString("title");
		    			String channelId = tt.getString("channel_id");
		    			tres = tres + (i+1) + ".  Title: " + title + "    Channel Id: " + channelId + "\n";
		    		}
		    	}
		    	if (metadata.has("custom_files")) {
		    		JSONArray musics = metadata.getJSONArray("custom_files");
		    		for(int i=0; i<musics.length(); i++) {
		    			JSONObject tt = (JSONObject) musics.get(i);
		    			String title = tt.getString("title");
		    			tres = tres + (i+1) + ".  Title: " + title + "\n";
						if (AdName.compareTo(title) != 0) {
							AdName = title;
							System.out.println("Sobrescribiendo la variable ADname!!!!!!!" + AdName);
						}else{
							System.out.println("AdName still valid");
						}
		    		}
		    	}
		    	//tres = tres + "\n\n" + result;
		    }else{
		    	tres = result;
		    }
		} catch (JSONException e) {
			tres = result;
		    e.printStackTrace();
		}
		//system.out.println("hola :!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		mResult.setText(tres);

			//serverResponse();
		//try {
		//	Thread.sleep(3000);
		//} catch(InterruptedException ex) {
		//	Thread.currentThread().interrupt();
		//}
		//start();
	}

	@Override
	public void onVolumeChanged(double volume) {
		long time = (System.currentTimeMillis() - startTime) / 1000;
		mVolume.setText("Tiempo capturado: " + volume + "\n\n Tiempo transcurrido： " + time + " s");
	}

	@Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MainActivity", "release");
        if (this.mClient != null) {
        	this.mClient.release();
        	this.initState = false;
        	this.mClient = null;
        }
    }

	private class DownloadFilesTask extends AsyncTask<URL, Integer, Integer> {
		protected Integer doInBackground(URL... urls) {
			try {
				System.out.println("Before Server Response *******************************");
				serverResponse();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return 0;
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(Long result) {

		}


		@TargetApi(Build.VERSION_CODES.KITKAT)
		public void serverResponse() throws IOException {

			final ServerSocket server = new ServerSocket(10000);
			System.out.println("Listening for connection on port 10000 ....");
			while(true) {
				Socket clientSocket = server.accept();
				InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
				BufferedReader reader = new BufferedReader(isr);
				try {
					String line = reader.readLine();
					while (line!=null && !line.isEmpty()) {
						System.out.println(line);
						line = reader.readLine();
					}
				}catch (SocketException e){

					e.printStackTrace();
				}

				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
				out.write("HTTP/1.0 200 OK\r\n");
				out.write("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n");
				out.write("Server: Apache/0.8.4\r\n");
				out.write("Content-Type: text/html\r\n");
				out.write("Content-Length: 59\r\n");
				out.write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n");
				out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
				out.write("\r\n");
				out.write(AdName);

				// on ferme les flux.
				System.out.println("Connexion client termino");
				System.out.println("------------------------------- " + AdName);
				out.close();
				reader.close();
				clientSocket.close();
			}
		}
	}

}
