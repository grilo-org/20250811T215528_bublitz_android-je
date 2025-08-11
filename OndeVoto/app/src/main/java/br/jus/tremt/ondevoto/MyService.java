package br.jus.tremt.ondevoto;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

public class MyService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent2, int startId) {
		super.onStart(intent2, startId);

		// define sound URI, the sound to be played when there's a notification
		Uri soundUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		// intent triggered, you can add other intent for other actions
		Intent intent = new Intent(this.getApplicationContext(),
				VemPraUrna.class);
		// Intent intent = new Intent(TelaOpcao.this, TelaOpcao.class);
		PendingIntent pIntent = PendingIntent.getActivity(
				this.getApplicationContext(), 0, intent, 0);

		String longText = "Vote Consciente!!!!!";

		// this is it, we'll build the notification!
		// in the addAction method, if you don't want any icon, just set the
		// first param to 0
		Notification mNotification = new Notification.Builder(
				this.getApplicationContext())

		.setContentTitle("#VemPraUrna")
				.setStyle(new Notification.BigTextStyle().bigText(longText))
				.setContentText("Justiça Eleitoral")
				.setFullScreenIntent(pIntent, true).setSmallIcon(R.drawable.ic)
				.setContentIntent(pIntent).setSound(soundUri)
				// .addAction(R.drawable.ic, "Vote Consciente!", pIntent)
				.build();

		NotificationManager notificationManager = (NotificationManager) this
				.getApplicationContext().getSystemService(
						Context.NOTIFICATION_SERVICE);

		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(0, mNotification);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
