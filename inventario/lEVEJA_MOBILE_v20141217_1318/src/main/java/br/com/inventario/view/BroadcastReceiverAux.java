package br.com.inventario.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import br.com.inventario.R;
import br.com.inventario.model.NotaFiscal;
import br.com.inventario.utils.VariaveisGlobais;
import br.com.inventario.web.ConectaFacade;


public class BroadcastReceiverAux extends BroadcastReceiver {

	/*VARIAVEIS PARA O NOVO GPS*/
	private TextView latitude;
	private TextView longitude;
	private TextView choice;
	private CheckBox fineAcc;
	private Button choose;
	private TextView provText;
	private LocationManager locationManager;
	private String provider;
	private MyLocationListener mylistener;
	private Criteria criteria;

	String chave = null;
	String lato = null;
	String logo = null;
	String numcodigo = "X"; /* VALOR DO CODIGO DE BARRAS QUE VAI COM X DEVIDO APROVEITAMENTO DO CODIGO */

	/* VARIAVEIS PARA O  NOVO GPS*/
	ArrayList<NotaFiscal> codigoslist = new ArrayList<NotaFiscal>(); // ARRAYLIST PARA NOTA FISCAL
	public static String KEY_ERROR = "error";
	private static String KEY_MESSAGE = "message";
	private static String CONTROLE = "Atencao";
	private static String RETORNOWS = "Atencao";
	protected String IDAparelho; // USADO PARA GUARDAR O IMEI





	@Override
	public void onReceive(Context context, Intent intent) {
		/* INICIO: PEGANDO IMEI PARA ENVIAR AO WS COM A LOCALIZACAO*/
		TelephonyManager aparelho = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		IDAparelho = aparelho.getDeviceId();
		String[] list = new String[]{IDAparelho};
		String imei = list[0];
		/* FIM: PEGANDO IMEI PARA ENVIAR AO WS COM A LOCALIZACAO*/

		// Get the location manager
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the location provider
		criteria = new Criteria();
		//criteria.setAccuracy(Criteria.ACCURACY_COARSE);	//default
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(false);
		// get the best provider depending on the criteria
		provider = locationManager.getBestProvider(criteria, false);
		// the last known location of this provider
		Location location = locationManager.getLastKnownLocation(provider);
		mylistener = new MyLocationListener();

		if (location != null) {
			mylistener.onLocationChanged(location);
			VariaveisGlobais.STATUS_LOCALIZADOR = "Ligado";

		} else {
			// leads to the settings because there is no last known location
			/*Toast.makeText(MainActivity.this, provider + " localizador desabilitado ",Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);*/
			/* ENVIAR MENSAGEM PARA O CELULAR E PARA O WS SE O LOCALIZADOR ESTIVER DESLIGADO*/
			VariaveisGlobais.STATUS_LOCALIZADOR = "Desligado";
		}
		// location updates: at least 0 meter and 0 millsecs change
		locationManager.requestLocationUpdates(provider, 0, 0, mylistener);


		if (VariaveisGlobais.STATUS_LOCALIZADOR == "Ligado") {
			/* CHAMAR WS E ENVIAR COORDENADAS
			 * CHAMAR ALERTA E ENVIAR MENSAGEM PARA CELULAR
			 *
			 * */
			//chave = VariaveisGlobais.getChave();
			chave = imei;
			//CLASSE ASYNCTASK PARA ACESSAR O WEBSERVICE USANDO THREADS. SEGUE O PADR�O ANDROID DA CLASSE
			class UploadFileAsync extends
					AsyncTask<String, Void, ArrayList<NotaFiscal>> {
				String mensagemErro = null;
				@Override
				protected ArrayList<NotaFiscal> doInBackground(String... list) {
					/* valores da localização de origem*/
					lato = VariaveisGlobais.LAT_MOMENTO;
					logo = VariaveisGlobais.LNG_MOMENTO;
					numcodigo = "X"; /* VALOR DO CODIGO DE BARRAS QUE VAI COM X DEVIDO APROVEITAMENTO DO CODIGO */


					NotaFiscal notafiscal = new NotaFiscal();
					try {
						//
						ConectaFacade enviar = new ConectaFacade();
						JSONObject json = new JSONObject();
						//json = enviar.novalocalizacao(numcodigo, chave, lato, logo);

						if (json.getString(KEY_ERROR).equals("false")) {

							/* sucesso no WS */
							CONTROLE = "OK";
							RETORNOWS = json.getString(KEY_MESSAGE);

						} else {

							/*erro no WS*/
							CONTROLE = "NOK";
							RETORNOWS = json.getString(KEY_MESSAGE);




						}


					} catch (Exception e) {
						mensagemErro = ("Nao foi possivel conectar no servidor." + e.toString());
						CONTROLE = "NOK";
					}
					return codigoslist;
				}

			} /* FIM CLASSE CHAMA WS*/


			String codigo_lido = "X";
			String[] list_itens = new String[]{codigo_lido};
			new UploadFileAsync().execute(list_itens);

			if (CONTROLE == "OK") {
				Log.i("Script", "-> Alarme");
				//gerarNotificacao(context, new Intent(context, MainActivity.class), "ATENCAO", "Localizacao Enviada. ", "Sucesso: "+RETORNOWS);

				//gerarNotificacao(context, new Intent(context, MainActivity.class), "ATENCAO", "Chamou e enviou. ", "Sucesso: "+RETORNOWS);

			}
			if (CONTROLE == "NOK") {
				Log.i("Script", "-> Alarme");
				//gerarNotificacao(context, new Intent(context, MainActivity.class), "ATENCAO", "Localizacao NAO Enviada. ", "");
				//gerarNotificacao(context, new Intent(context, MainActivity.class), "ATENCAO", "Chamou e NAO  enviou. ", "ERRO: "+RETORNOWS);

			}
		}
		if (VariaveisGlobais.STATUS_LOCALIZADOR == "Desligado") {
			/* CHAMAR WS E ENVIAR INFORMACAO QUE LOCALIZADOR ESTAVA DESLIGADO
			 * CHAMAR ALERTA E ENVIAR MENSAGEM PARA CELULAR
			 *
			 * */
			/*chave = VariaveisGlobais.getChave();*/
			chave = imei;
			/*Classe UploadFileAsync2 eh para quando o localizador estiver desligado*/
			//CLASSE ASYNCTASK PARA ACESSAR O WEBSERVICE USANDO THREADS. SEGUE O PADR�O ANDROID DA CLASSE
			class UploadFileAsync2 extends	AsyncTask<String, Void, ArrayList<NotaFiscal>> {
				String mensagemErro = null;
				@Override
				protected ArrayList<NotaFiscal> doInBackground(String... list) {
					/* valores da localização de origem*/
					lato = "11.1111111";
					logo = "11.1111111";
					numcodigo = "X"; /* VALOR DO CODIGO DE BARRAS QUE VAI COM X DEVIDO APROVEITAMENTO DO CODIGO */
					NotaFiscal notafiscal = new NotaFiscal();
					try {
						//
						ConectaFacade enviar = new ConectaFacade();
						JSONObject json = new JSONObject();
						//json = enviar.novalocalizacao(numcodigo, chave, lato, logo);

						if (json.getString(KEY_ERROR).equals("false")) {

							/* sucesso no WS */
							CONTROLE = "OK";


						} else {

							/*erro no WS*/
							CONTROLE = "NOK";
						}


					} catch (Exception e) {
						mensagemErro = ("Nao foi possivel conectar no servidor." + e.toString());
						CONTROLE = "NOK";
					}
					return codigoslist;
				}

			} /* FIM CLASSE CHAMA WS*/


			String codigo_lido = "X";
			String[] list_itens = new String[]{codigo_lido};
			new UploadFileAsync2().execute(list_itens);

			if (CONTROLE == "OK") {
				Log.i("Script", "-> Alarme");
				//gerarNotificacao(context, new Intent(context, MainActivity.class),
				//		"ATENCAO", "Localizador Desligado ", "Enviado ao Site - Sem Localizacao Definida");
			}
			if (CONTROLE == "NOK") {
				Log.i("Script", "-> Alarme");
				//gerarNotificacao(context, new Intent(context, MainActivity.class),
				//		"ATENCAO", "Localizacao NAO Enviada. ", "Erro");

			}
		}



	}


	public void gerarNotificacao(Context context, Intent intent, CharSequence ticker, CharSequence titulo, CharSequence descricao){
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent p = PendingIntent.getActivity(context, 0, intent, 0);



		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setTicker(ticker);
		builder.setContentTitle(titulo);
		builder.setContentText(descricao);
		builder.setSmallIcon(R.drawable.logo);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo));
		builder.setContentIntent(p);


		Notification n = builder.build();
		//n.vibrate = new long[]{150, 300, 150, 600};
		n.flags = Notification.FLAG_AUTO_CANCEL;
		nm.notify(R.drawable.logo, n);

		try {
			//Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			//Ringtone toque = RingtoneManager.getRingtone(context, som);
			//toque.play();
		} catch (Exception e) {
		}







	}

	/* INICIO: CLASSE PARA  O  NOVO GPS */
	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			VariaveisGlobais.LAT_MOMENTO = "11.1111111";
			VariaveisGlobais.LNG_MOMENTO = "11.1111111";
			// PEGA LOCALIZAÇÃO DO MOMENTO E ALIMENTA AS VARIAVEIS GLOBAIS
			VariaveisGlobais.LAT_MOMENTO = String.valueOf(location.getLatitude());
			VariaveisGlobais.LNG_MOMENTO = String.valueOf(location.getLongitude());

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// Toast.makeText(MainActivity.this, provider + " status alterado "+status +"!",
			//       Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			//Toast.makeText(MainActivity.this, "Provedor " + provider + " habilitado!",
			//        Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onProviderDisabled(String provider) {
			//Toast.makeText(MainActivity.this, "Provedor " + provider + " desabilitado!",
			//		Toast.LENGTH_SHORT).show();
		}
	}
	/* FIM: CLASSE PARA  O  NOVO GPS */



}

