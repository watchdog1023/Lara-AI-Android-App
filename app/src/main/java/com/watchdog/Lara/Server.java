package com.watchdog.Lara;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class Server {
	MainActivity activity;
	ServerSocket serverSocket;
	String message = "";
	static final int socketServerPORT = 8080;
	MediaPlayer mp;
	private static final boolean debug = true;

	public Server(MainActivity activity)
	{
		if (debug) {
			mp = MediaPlayer.create(activity, R.raw.a2);
		}
		else {
			mp = MediaPlayer.create(activity, R.raw.alarm_sound);
		}
		this.activity = activity;
		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();
	}


	public void stop()
	{
		mp.stop();
		mp.prepareAsync();
	}

	public void locstart()
	{
		mp.seekTo(0);
		mp.start();
		mp.setLooping(true);
	}


	public int getPort()
	{
		return socketServerPORT;
	}

	public void onDestroy() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class SocketServerThread extends Thread {
		@Override
		public void run() {
			try {
				serverSocket = new ServerSocket(socketServerPORT);

				while(true) {
					Intent contentIntent = new Intent(activity, MainActivity.class);
					PendingIntent pendingContentIntent = PendingIntent.getActivity(activity, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					Socket socket = serverSocket.accept();
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity);
					mBuilder.setSmallIcon(R.drawable.notification_bell);
					mBuilder.setContentTitle("Notification Alert");
					mBuilder.setContentText("This Is your Alarm!");
					mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("This Is your Alarm!"));
					mBuilder.setContentIntent(pendingContentIntent);
					mBuilder.setAutoCancel(true);
					NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
					mNotificationManager.notify(0, mBuilder.build());
					locstart();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class SocketServerReplyThread extends Thread {

		private Socket hostThreadSocket;
		int cnt;

		@Override
		public void run() {
			OutputStream outputStream;
			String msgReply = "Hello from Server, you are #" + cnt;

			try {
				outputStream = hostThreadSocket.getOutputStream();
				PrintStream printStream = new PrintStream(outputStream);
				printStream.print(msgReply);
				printStream.close();

				message += "replayed: " + msgReply + "\n";

				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						activity.msg.setText(message);
					}
				});

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message += "Something wrong! " + e.toString() + "\n";
			}

			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					activity.msg.setText(message);
				}
			});
		}

	}

	public String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
						.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface
						.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress
							.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += "Server running at : "
								+ inetAddress.getHostAddress();
					}
				}
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Something Wrong! " + e.toString() + "\n";
		}
		return ip;
	}
}
