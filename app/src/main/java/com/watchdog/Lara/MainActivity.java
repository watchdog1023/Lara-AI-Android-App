package com.watchdog.Lara;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//For Speech Recognition
import android.speech.RecognizerIntent;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.io.*;
import java.util.*;

//For WEBSOCKET Comm
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

//For hostname to IP
import java.net.InetAddress;
import java.net.UnknownHostException;



public class MainActivity extends AppCompatActivity
{
	Server server;
	TextView infoip, msg;
	private TextView txvResult;
	public InetAddress hostname;

	@Override
	protected void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			txvResult = (TextView) findViewById(R.id.txvResult);
			infoip = (TextView) findViewById(R.id.infoip);
			server = new Server(MainActivity.this);
			infoip.setText(server.getIpAddress()+":"+server.getPort());
			infoip.setTextColor(Color.WHITE);
			final Button button = (Button) findViewById(R.id.button);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					server.stop();
					Toast.makeText(MainActivity.this, "Alarm Stopped", Toast.LENGTH_LONG).show();
				}
			});
		}

	public void getSpeechInput(View view)
		{
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

			if (intent.resolveActivity(getPackageManager()) != null)
				{
					startActivityForResult(intent, 10);
				}
			else
				{
					Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
				}
		}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
		{
			super.onActivityResult(requestCode, resultCode, data);
			switch (requestCode)
				{
					case 10:
					if (resultCode == RESULT_OK && data != null)
						{
							ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
							txvResult.setText(result.get(0));
							String command = TextUtils.join(", ", result);
							sendMessage(command);
						}
					break;
				}
		}

	public void sendMessage(String message)
		{

	}


	public static void Save(File file, String[] data)
		{
			FileOutputStream fos = null;
			try
				{
					fos = new FileOutputStream(file);
				}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			try
				{
					try
						{
							for (int i = 0; i<data.length; i++)
								{
									fos.write(data[i].getBytes());
									if (i < data.length-1)
										{
											fos.write("\n".getBytes());
										}
								}
						}
					catch (IOException e)
						{
							e.printStackTrace();
						}
				}
			finally
				{
					try
						{
							fos.close();
						}
					catch (IOException e)
						{
							e.printStackTrace();
						}
				}
		}


	public static String[] Load(File file)
		{
			FileInputStream fis = null;
			try
				{
					fis = new FileInputStream(file);
				}
		catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);

		String test;
		int anzahl=0;
		try
			{
				while ((test=br.readLine()) != null)
					{
						anzahl++;
					}
			}
		catch (IOException e)
			{
				e.printStackTrace();
			}
		try
			{
				fis.getChannel().position(0);
			}
		catch (IOException e)
			{
				e.printStackTrace();
			}

		String[] array = new String[anzahl];
		String line;
		int i = 0;
		try
			{
				while((line=br.readLine())!=null)
					{
						array[i] = line;
						i++;
					}
			}
		catch (IOException e)
			{
				e.printStackTrace();
			}
		return array;
	}

	public void host2ip(String host)
		{
			InetAddress address = null;
			try
				{
					address = InetAddress.getByName(host);
					hostname = address;
				}
			catch (UnknownHostException e)
				{
					System.exit(2);
				}
		}

	public void video_display(String holo)
		{
			int[] vid = {R.raw.track1,R.raw.track2};
			int array_num = 0;
			if(holo == "greetings")
				{
					array_num = 1;
				}
			VideoView display = (VideoView)findViewById(R.id.videoView);
			String path = "android.resource://com.watchdog.Lara/" + vid[array_num];
			Uri uri = Uri.parse(path);
			display.setVideoURI(uri);
			display.requestFocus();
			display.start();
		}
}
