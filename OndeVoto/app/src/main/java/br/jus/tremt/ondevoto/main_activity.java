package br.jus.tremt.ondevoto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public final class main_activity extends Activity {

	final Context context = this;
	static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private static final String URL = "http://apps2.tre-mt.jus.br/app/ondevoto/pesqTituloLocal.asmx";
	private static final String NAMESPACE = "http://tre-mt.jus.br/";
	private static final String METHOD_NAME = "retornaLocal";
	private static final String SOAP_ACTION = "http://tre-mt.jus.br/retornaLocal";

	//private static final String URL = "http://ondevoto.tse.jus.br/ejbOndeVoto/wsOndeVoto/wsOndeVoto?wsdl";
	//private static final String NAMESPACE = "http://ondevoto.tse.jus.br/";
	//private static final String METHOD_NAME = "pesqLocal";
	//private static final String SOAP_ACTION = "";

	private LatLng coordTRE_MT = new LatLng(-15.563429, -56.064148);

	private GoogleMap map;
	private Double latPoint;
	private Double lngPoint;
	private LatLng latLng;
	private EditText txtTitulo;
	private TextView txtInfo;
	private String retornoWS;
	private String[] retorno;
	private boolean fechaRing;
	private String titulo;
	private ImageButton btnInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

//		Calendar de = Calendar.getInstance();
//		de.set(2014, Calendar.OCTOBER, 26);
//		de.set(Calendar.MILLISECOND, 0);
//		de.set(Calendar.SECOND, 0);
//		de.set(Calendar.MINUTE, 0);
//		de.set(Calendar.HOUR, 8);
//
//		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//		int id = (int) System.currentTimeMillis();
//		Intent intent = new Intent(this, TimeAlarm.class);
//		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id,
//				intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		alarmManager.set(AlarmManager.RTC_WAKEUP, de.getTimeInMillis(),
//				pendingIntent);

		txtInfo = (TextView) findViewById(R.id.textView2);
		txtTitulo = (EditText) findViewById(R.id.txtTitulo);
		btnInfo = (ImageButton) findViewById(R.id.btnInfo);
		btnInfo.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				final Dialog dialog = new Dialog(context);

				dialog.setContentView(R.layout.info);
				dialog.setTitle(getResources().getString(R.string.app_name));
				dialog.setCanceledOnTouchOutside(true);
				String formattedText1 = null;
				String formattedText2 = null;

				formattedText1 = getString(R.string.txt_info3_mt);
				formattedText2 = getString(R.string.txt_info2_mt);

				TextView txtInfo1 = (TextView) dialog
						.findViewById(R.id.txtInfo1);
				Spanned result = Html.fromHtml(formattedText1);
				txtInfo1.setText(result);

				TextView txtInfo2 = (TextView) dialog
						.findViewById(R.id.txtInfo2);
				result = Html.fromHtml(formattedText2);
				txtInfo2.setText(result);

				ImageButton btnOK = (ImageButton) dialog
						.findViewById(R.id.btnOK);
				btnOK.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
				return false;
			}
		});

		if (checkPlayServices()) {
			IniciarServicoGPS();
			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(getResources().getString(R.string.txt_error_title))
					.setMessage(
							getResources().getString(R.string.txt_error_maps))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setNegativeButton(
							getResources().getString(R.string.btn_exit), null)
					.show();
		}

		marcaTRE();

		txtTitulo
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							pesquisaTitulo(v);
							return true;
						}
						return false;
					}
				});

	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// }

	private void marcaTRE() {
		map.clear();
		map.setMyLocationEnabled(true);
		Marker mark = null;
		mark = map.addMarker(new MarkerOptions().position(coordTRE_MT));
		mark.setTitle(getResources().getString(R.string.txt_sede_tre_mt));
		mark.showInfoWindow();
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordTRE_MT, 5));
		map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
		txtInfo.setText("");
	}

	private boolean checkPlayServices() {
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);
		return status == ConnectionResult.SUCCESS;
	}

	void showErrorDialog(int code) {
		GooglePlayServicesUtil.getErrorDialog(code, this,
				REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_RECOVER_PLAY_SERVICES:
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(context,
						getResources().getString(R.string.txt_error_maps),
						Toast.LENGTH_SHORT).show();
				finish();
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void mostraInfo(View v) {
		final Dialog dialog = new Dialog(context);

		dialog.setContentView(R.layout.info);
		dialog.setTitle(getResources().getString(R.string.app_name));
		dialog.setCanceledOnTouchOutside(true);
		String formattedText1 = null;
		String formattedText2 = null;

		formattedText1 = getString(R.string.txt_info1_mt);
		formattedText2 = getString(R.string.txt_info2_mt);

		TextView txtInfo1 = (TextView) dialog.findViewById(R.id.txtInfo1);
		Spanned result = Html.fromHtml(formattedText1);
		txtInfo1.setText(result);

		TextView txtInfo2 = (TextView) dialog.findViewById(R.id.txtInfo2);
		result = Html.fromHtml(formattedText2);
		txtInfo2.setText(result);

		ImageButton btnOK = (ImageButton) dialog.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void pesquisaTitulo(View view) {
		txtInfo.setText("");
		InputMethodManager inputManager = (InputMethodManager) 
				getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(
				getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);

		titulo = txtTitulo.getText().toString();
		titulo = "000000000000" + titulo;
		titulo = titulo.substring(titulo.length() - 12);

		String ok = ValidarTitulo(titulo);
		if (ok.equals("ok")) {
			fechaRing = false;
			final ProgressDialog ringProgressDialog = ProgressDialog.show(
					context, getResources().getString(R.string.txt_wait),
					getResources().getString(R.string.txt_search), true);
			ringProgressDialog.setCancelable(false);
			try {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
							request.addProperty("numTitulo", titulo);
							SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
							envelope.dotNet = true; 
							envelope.setOutputSoapObject(request);
							try {
								HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
								androidHttpTransport.call(SOAP_ACTION, envelope);
								SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
								retornoWS = result.toString();
							} catch (Exception e) {
								retornoWS = "0|0|0";//e.toString();
							}

							// retornoWS = usado para testes
							// "234|E.E.P.S.G. PRESIDENTE MÉDICI|AV MATO GROSSO, 
							//  500|ARAES|CUIABÁ, MT";

							retorno = retornoWS.split("\\|");
							// 0 = número seção
							// 1 = nome local
							// 2 = endereco
							// 3 = bairro
							// 4 = cidade/uf
							if (!retorno[0].equals("0")) {
								new GeocoderTask().execute(retornoWS);
								while (fechaRing == false)
									Thread.sleep(40);
							} else {
								fechaRing = true;
								Message msg = new Message();
								msg.obj = retornoWS;
								handler.sendMessage(msg);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
						ringProgressDialog.dismiss();
					}
				}).start();
			} catch (Exception e) {
				ringProgressDialog.dismiss();
			}
		} else {
			txtTitulo.setError(ok);
			marcaTRE();
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(getResources().getString(R.string.txt_error_title))
					.setMessage(ok)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setNegativeButton(
							getResources().getString(R.string.btn_exit), null)
					.show();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			marcaTRE();
			String formattedText = null;
			formattedText = getString(R.string.txt_info0_mt);
			Spanned result = Html.fromHtml(formattedText);
			txtInfo.setText(result);
		}
	};

	public void IniciarServicoGPS() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				AtualizarGPS(location);
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);
	}

	public void AtualizarGPS(Location location) {
		latPoint = location.getLatitude();
		lngPoint = location.getLongitude();
	}

	private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {
		@Override
		protected List<Address> doInBackground(String... locationName) {
			Geocoder geocoder = new Geocoder(context);
			List<Address> addresses = null;
			String[] ender = locationName[0].split("\\|");
			// 0 = número seção
			// 1 = nome local
			// 2 = endereco
			// 3 = bairro
			// 4 = cidade/uf

			// Tenta Endereço+Bairro+Cidade/UF
			try {
				addresses = geocoder.getFromLocationName(ender[2] + " - "
						+ ender[3] + ", " + ender[4], 1);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			if ((addresses == null) || (addresses.size() == 0))
				// Tenta Endereço+Cidade/UF
				try {
					addresses = geocoder.getFromLocationName(ender[2] + ','
							+ ender[4], 1);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			if ((addresses == null) || (addresses.size() == 0))
				// Tenta Nome Local+Cidade/UF
				try {
					addresses = geocoder.getFromLocationName(ender[1] + ','
							+ ender[4], 1);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			return addresses;
		}

		@Override
		protected void onPostExecute(List<Address> addresses) {
			fechaRing = true;
			if (addresses == null || addresses.size() == 0) {
				marcaTRE();
				Toast.makeText(
						context,
						getResources()
								.getString(R.string.txt_error_google_maps),
						Toast.LENGTH_LONG).show();
			} else {
				map.clear();
				Address ender = (Address) addresses.get(0);
				latLng = new LatLng(ender.getLatitude(), ender.getLongitude());
				MarkerOptions options = new MarkerOptions();
				options.position(latLng);
				if (latPoint != null) {
					options.position(new LatLng(latPoint, lngPoint));
					String url = getMapsApiDirectionsUrl();
					ReadTask downloadTask = new ReadTask();
					downloadTask.execute(url);
				}
				map.addMarker(options);
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
				addMarkers();
			}
			try {
				txtInfo.setText(Html.fromHtml("<em>Local: </em><strong>"
						+ retorno[1] + "</strong><br>Seção: <strong>"
						+ retorno[0] + "<br><small>Endereço: </strong>"
						+ retorno[2] + ", " + retorno[3] + ", " + retorno[4]
						+ "</small>"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String getMapsApiDirectionsUrl() {
		String waypoints = "waypoints=optimize:true|" + latPoint + ","
				+ lngPoint + "|" + latLng.latitude + "," + latLng.longitude;
		String sensor = "sensor=false";
		String params = waypoints + "&" + sensor;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + params;
		return url;
	}

	private void addMarkers() {
		if (map != null) {
			try {
				map.addMarker(
						new MarkerOptions().position(
								new LatLng(latPoint, lngPoint)).title(
								"Estou aqui!")).showInfoWindow();
			} catch (Exception e) {
			}
			Marker mark = map.addMarker(new MarkerOptions().position(latLng));
			mark.setTitle(retorno[1]);
			// mark.setSnippet(retorno[2]);
			mark.showInfoWindow();
			map.setMyLocationEnabled(true);
		}
	}

	private class ReadTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... url) {
			String data = "";
			try {
				HttpConnection http = new HttpConnection();
				data = http.readUrl(url[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			new ParserTask().execute(result);
		}
	}

	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				PathJSONParser parser = new PathJSONParser();
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
			ArrayList<LatLng> points = null;
			PolylineOptions polyLineOptions = null;

			// pega os pontos para tra�ar a rota
			for (int i = 0; i < routes.size(); i++) {
				points = new ArrayList<LatLng>();
				polyLineOptions = new PolylineOptions();
				List<HashMap<String, String>> path = routes.get(i);

				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				polyLineOptions.addAll(points);
				polyLineOptions.width(4);
				polyLineOptions.color(Color.RED);
			}

			// Tra�a a rota entre os pontos
			try {
				map.addPolyline(polyLineOptions);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Validar Título de Eleitor
	public String ValidarTitulo(String strTitulo) {
		int dig1;
		int dig2;
		int dig3;
		int dig4;
		int dig5;
		int dig6;
		int dig7;
		int dig8;
		int dig9;
		int dig10;
		int dig11;
		int dig12;
		int dv1;
		int dv2;
		int qDig;

		if ((strTitulo.length() == 0) || (strTitulo.equals("000000000000"))) // Validação
																				// do
																				// preenchimento
		{
			return getResources().getString(R.string.txt_no_titulo); // Caso não
																		// seja
																		// informado
																		// o
																		// Título
		} else {
			if (strTitulo.length() < 12) { // Completar 12 dígitos
				strTitulo = "000000000000" + strTitulo;
				strTitulo = strTitulo.substring(strTitulo.length() - 12);
			} else if (strTitulo.length() > 12) {
				return getResources().getString(R.string.txt_no_titulo);
			}
		}

		qDig = strTitulo.length(); // Total de caracteres

		dig1 = Integer.parseInt(Mid(strTitulo, qDig - 11, 1));
		dig2 = Integer.parseInt(Mid(strTitulo, qDig - 10, 1));
		dig3 = Integer.parseInt(Mid(strTitulo, qDig - 9, 1));
		dig4 = Integer.parseInt(Mid(strTitulo, qDig - 8, 1));
		dig5 = Integer.parseInt(Mid(strTitulo, qDig - 7, 1));
		dig6 = Integer.parseInt(Mid(strTitulo, qDig - 6, 1));
		dig7 = Integer.parseInt(Mid(strTitulo, qDig - 5, 1));
		dig8 = Integer.parseInt(Mid(strTitulo, qDig - 4, 1));
		dig9 = Integer.parseInt(Mid(strTitulo, qDig - 3, 1));
		dig10 = Integer.parseInt(Mid(strTitulo, qDig - 2, 1));
		dig11 = Integer.parseInt(Mid(strTitulo, qDig - 1, 1));
		dig12 = Integer.parseInt(Mid(strTitulo, qDig, 1));

		// Cálculo para o primeiro dígito verificador
		dv1 = (dig1 * 2) + (dig2 * 3) + (dig3 * 4) + (dig4 * 5) + (dig5 * 6)
				+ (dig6 * 7) + (dig7 * 8) + (dig8 * 9);
		dv1 = dv1 % 11;

		if ((dig9 == 0) && ((dig10 == 1) || (dig11 == 2))) {// Se for SP ou MG
			if (dv1 == 0)
				dv1 = 1;
		}
		if (dv1 == 10) {
			dv1 = 0; // Se o resto for igual a 10, dv1 igual a zero
		}
		// Cálculo para o segundo dígito verificador
		dv2 = (dig9 * 7) + (dig10 * 8) + (dv1 * 9);
		dv2 = dv2 % 11;

		if ((dig9 == 0) && ((dig10 == 1) || (dig11 == 2))) {// Se for SP ou MG
			if (dv2 == 0)
				dv2 = 1;
		}
		if (dv2 == 10) {
			dv2 = 0; // Se o resto for igual a 10, dv1 igual a zero
		}

		// Validação dos dígitos verificadores
		if (dig11 == dv1 && dig12 == dv2) {
			return "ok";
		} else {
			return getResources().getString(R.string.txt_no_titulo);
		}
	}

	public static String Mid(String texto, int inicio, int tamanho) {
		String strMid = texto.substring(inicio - 1, inicio + (tamanho - 1));
		return strMid;
	}

}
