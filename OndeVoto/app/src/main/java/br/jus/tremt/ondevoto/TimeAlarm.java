package br.jus.tremt.ondevoto;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

// The class has to extend the BroadcastReceiver to get the notification from the system
public class TimeAlarm extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service1 = new Intent(context, MyService.class);
		context.startService(service1);

	}

	public void on2Receive(Context context, Intent paramIntent) {

		// define sound URI, the sound to be played when there's a notification
		Uri soundUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		// intent triggered, you can add other intent for other actions
		Intent intent = new Intent(context, VemPraUrna.class);
		// Intent intent = new Intent(TelaOpcao.this, TelaOpcao.class);
		PendingIntent pIntent = PendingIntent
				.getActivity(context, 0, intent, 0);

		String longText = "Vote Consciente!!!!!";

		// this is it, we'll build the notification!
		// in the addAction method, if you don't want any icon, just set the
		// first param to 0
		Notification mNotification = new Notification.Builder(context)

		.setContentTitle("#VemPraUurna")
				.setStyle(new Notification.BigTextStyle().bigText(longText))
				.setContentText("Justiça Eleitoral")
				.setFullScreenIntent(pIntent, true).setSmallIcon(R.drawable.ic)
				.setContentIntent(pIntent).setSound(soundUri)
				.addAction(R.drawable.ic, "Veja", pIntent).build();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(0, mNotification);
	}

}
