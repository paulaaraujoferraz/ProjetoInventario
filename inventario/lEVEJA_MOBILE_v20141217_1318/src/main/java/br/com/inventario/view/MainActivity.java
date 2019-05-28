/*
 * classe usada para fazer o login do usuario no webservice
 * 
 */

package br.com.inventario.view;


import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.inventario.R;
import br.com.inventario.utils.DetectorInternet;
import br.com.inventario.utils.VariaveisGlobais;
import br.com.inventario.web.ConectaFacade;


/*public class MainActivity extends Activity implements
        OnCreateContextMenuListener {*/
public class MainActivity extends AppCompatActivity implements OnCreateContextMenuListener {
    /*Permissoes 2 inicio*/
    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_READ_PHONE_STATE = 0;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final int MY_PERMISSIONS_REQUESTS = 0;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 0;

    private static final int FLAG_GRANT_READ_URI_PERMISSION = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUESTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // FIXME: Handle this case the user denied to grant the permissions
                }
                break;
            }
            default:
                // TODO: Take care of this case later
                break;
        }
    }

    private void requestPermissions()
    {
        List<String> requiredPermissions = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.CAMERA);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.RECORD_AUDIO);
        }



        if (!requiredPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    requiredPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUESTS);
        }
    }


    /* Permissoes 2 fim*/

    public static final int CONTEXTMENUITEM_NEW = 100;
    public static final int CONTEXTMENUITEM_LIST = 101;
    private static String KEY_ERROR = "error";
    private static String KEY_APIKEY = "apiKey";
    private static String KEY_MESSAGE = "message";
    protected String IDAparelho; // USADO PARA GUARDAR O IMEI
    ImageButton[] menu = new ImageButton[6];
    Context context;
    VariaveisGlobais var = new VariaveisGlobais();
    private TextView entregador, msg1, msg2;
    private ProgressDialog pDialog;
    private String autorizacao_server = null;
    private String nome_user_logado;
    private String mensagemErro = null;
    private DetectorInternet detectainternet;



    private TextView latitudeo1;
    private TextView longitudeo1;


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
    /* VARIAVEIS PARA O  NOVO GPS*/

    /* pegar versao do android
    * Se for Android 7.0 o SDK_INT vale 24
     * Para versões igual ou após Android 7
    * */
    int SDK_INT = android.os.Build.VERSION.SDK_INT;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        menu[0] = (ImageButton) findViewById(R.id.btnregistrar);
        menu[1] = (ImageButton) findViewById(R.id.btnconfirmar);
        entregador = (TextView) findViewById(R.id.txtnomeentregador);
        msg1 = (TextView) findViewById(R.id.txtmensagem1);
        msg2 = (TextView) findViewById(R.id.txtmensagem2);


        /* chama para dar permissoes somente sdk 23 >*/
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            requestPermissions();
        } else {
            // Pre-Marshmallow
        }

        detectainternet = new DetectorInternet(getApplicationContext());
        if (Build.VERSION.SDK_INT >= 23) {
            /* INICIO TESTANDO SE FOI DADA PEMISSAO PHONE*/
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            } else {
                //TODO
                detectainternet = new DetectorInternet(getApplicationContext());
                TelephonyManager aparelho = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                IDAparelho = aparelho.getDeviceId();
                VariaveisGlobais.IMEI_GLOBAL = IDAparelho;
            }
            /* FIM TESTANDO SE FOI DADA PEMISSAO PHONE*/

        } else {
            // Pre-Marshmallow
            detectainternet = new DetectorInternet(getApplicationContext());
            TelephonyManager aparelho = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            IDAparelho = aparelho.getDeviceId();
        }

        if (Build.VERSION.SDK_INT >= 23) {
            /* INICIO TESTANDO SE FOI DADA PEMISSAO ACCESS_FINE_LOCATION*/
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            } else {
                //TODO

// Get the location manager
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
                } else {
                    // leads to the settings because there is no last known location
                    Toast.makeText(MainActivity.this, provider + " localizador desabilitado ",Toast.LENGTH_SHORT).show();
                    /* quando o localizador está desabilitado envia a mensagem e segue com o aplicativo*/
                    //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    //startActivity(intent);

                    /* zerando variaveis globais de lat e lng de origem porque o localizador está desligado*/
                    VariaveisGlobais.LATO = "11.1111111";
                    VariaveisGlobais.LNGO = "11.1111111";
                    VariaveisGlobais.LAT = "11.1111111";
                    VariaveisGlobais.LNG = "11.1111111";


                }
                // location updates: at least 1 meter and 200millsecs change
                locationManager.requestLocationUpdates(provider, 200, 1, mylistener);
            }
            /* FIM TESTANDO SE FOI DADA PEMISSAO PHONE
             *
             * ATENÇÃO TESTAR PARA TODOS
             *
             * */

            // Marshmallow+
        } else {
            // Pre-Marshmallow

// Get the location manager
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
            } else {
                // leads to the settings because there is no last known location
                Toast.makeText(MainActivity.this, provider + " localizador desabilitado ",Toast.LENGTH_SHORT).show();
                /* quando o localizador está desabilitado envia a mensagem e segue com o aplicativo*/
                //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //startActivity(intent);

                /* zerando variaveis globais de lat e lng de origem porque o localizador está desligado*/
                VariaveisGlobais.LATO = "11.1111111";
                VariaveisGlobais.LNGO = "11.1111111";
                VariaveisGlobais.LAT = "11.1111111";
                VariaveisGlobais.LNG = "11.1111111";


            }
            // location updates: at least 1 meter and 200millsecs change
            locationManager.requestLocationUpdates(provider, 200, 1, mylistener);
        }

        /****** INICIO Reiniciar aplicativo para aparece o Nome do entregador *****/
        if (Build.VERSION.SDK_INT >= 23) {
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showMessageOKCancel(" O Aplicativo será fechado. Favor reabrí-lo para conexão ",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            REQUEST_CODE_ASK_PERMISSIONS);

                                    /*REINICIAR O APP*/
                                    restartActivity();
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        /****** INICIO Reiniciar aplicativo para aparece o Nome do entregador *****/

        final Boolean internet = detectainternet.TemConexao();
        latitudeo1 = (TextView) findViewById(R.id.latitudeo1);
        longitudeo1 = (TextView) findViewById(R.id.longitudeo1);

        if (!internet) {

            Toast toast = Toast.makeText(MainActivity.this,
                    "Sem INTERNET.Ative sua conexao", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();

        } else {

            String[] list = new String[]{IDAparelho};

            new ConectaLogin().execute(list);

        }

        /* INICIO: TRATAMENTO PARA RASTREAMENTO
         *
         * 900000 eh 15 minutos
         * 600000 eh 10 minutos
         * 1000000 eh 17 minutos
         * */
        if(SDK_INT > 23) {
            /*para versoes Igual ou Apos  do Android 7*/
            /* em 20/10/2017: quando eh android  7.0 em diante 600000 = 6 minutos, para 10 minutos deve ser 1000000*/
            //Toast.makeText(MainActivity.this, "Android 7 ou superior!"+SDK_INT, Toast.LENGTH_SHORT).show();
            boolean alarmeAtivo = (PendingIntent.getBroadcast(this, 0,
                    new Intent("ALARME_DISPARADO"), PendingIntent.FLAG_NO_CREATE) == null);

           /* boolean alarmeAtivo = (PendingIntent.getBroadcast(this, 0,
                    new Intent(this, BroadcastReceiverAux.class), PendingIntent.FLAG_NO_CREATE) == null);*/

            if(alarmeAtivo){
                //Toast.makeText(MainActivity.this, "Novo Alarme sdk > 23: !"+SDK_INT, Toast.LENGTH_SHORT).show();
                Log.i("Script", "Novo alarme sdk > 23");
                //Intent intent = new Intent("ALARME_DISPARADO");

                Intent intent = new Intent(this, br.com.inventario.view.BroadcastReceiverAux.class); intent.setAction("ALARME_DISPARADO");

                PendingIntent p = PendingIntent.getBroadcast(this, 0, intent, 0);
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                c.add(Calendar.SECOND, 3);
                AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);

                alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 600000, p);


            }
            else{
                Log.i("Script", "Alarme ja ativo sdk > 23");
            }
        }

        if(SDK_INT < 24) {
            /*para versoes antes do Android 7*/
            boolean alarmeAtivo = (PendingIntent.getBroadcast(this, 0, new Intent("ALARME_DISPARADO"), PendingIntent.FLAG_NO_CREATE) == null);
            if(alarmeAtivo){
                Log.i("Script", "Novo alarme sdk < 24");
                Intent intent = new Intent("ALARME_DISPARADO");
                PendingIntent p = PendingIntent.getBroadcast(this, 0, intent, 0);
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                c.add(Calendar.SECOND, 3);
                AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);

                alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 600000, p);
            }
            else{
                Log.i("Script", "Alarme ja ativo sdk < 24");
            }


        }



        /* FIM: TRATAMENTO PARA RASTREAMENTO */

    }// ON CREATOR

    /* INICIO: CLASSE PARA  O  NOVO GPS */
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {


            // Initialize the location fields


           // latitudeo1.setText("Lat: "+String.valueOf(location.getLatitude()));
           // longitudeo1.setText("Long: "+String.valueOf(location.getLongitude()));
            //Valores que serao passados ao Webservices por meio do ConectaFacade
            /* zerando variaveis globais de lat e lng de origem e destino na primeira tela*/
            //VariaveisGlobais.LATO = "11.1111111";
            //VariaveisGlobais.LNGO = "11.1111111";
            //VariaveisGlobais.LAT = "11.1111111";
            //VariaveisGlobais.LNG = "11.1111111";
           // Toast.makeText(MainActivity.this, "Localizacao OK ",Toast.LENGTH_SHORT).show();
            //Toast.makeText(MainActivity.this,  "Location changed!",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MainActivity.this, "Provedor " + provider + " desabilitado!",
                    Toast.LENGTH_SHORT).show();
        }
    }
 /* FIM: CLASSE PARA  O  NOVO GPS */



    // AQUI � VERIFICADO SE O USUARIO ESTA LOGADO OU NAO, SE ESTIVER PODE ACESSAR OS BOTOES SE NA� ESTIVER NAO ACESSA


    public void executaOpcao(View v) {
        Intent i = new Intent(this, MainActivity.class);

        switch (v.getId()) {
            case R.id.btnregistrar:
                if (var.getLogin().equals("true")) {
                    i = new Intent(this, br.com.inventario.view.InserirNotaActivity.class);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Voce nao esta autorizado", Toast.LENGTH_LONG).show();

                }
                break;
            case R.id.btnconfirmar:
                if (var.getLogin().equals("true")) {
                    //i = new Intent(this, br.com.inventario.view.RegitrarNotaEntregueActivity.class);
                    i = new Intent(this, RegitrarNotaEntregueActivity.class);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Voce nao esta autorizado", Toast.LENGTH_LONG).show();

                }
                break;

        }
        startActivity(i);
        // testar chamada para GPS


    }


    // AQUI A � USADO O ASYNCTASK PARA FAZER O LOGIN NO WEBSERVICE
    class ConectaLogin extends AsyncTask<String, Void, String> {

        ConectaFacade userFunction = new ConectaFacade();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Conectando. Aguarde ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... list) {

            String imei = list[0];

            Log.d("imei", imei);

            try {

                JSONObject json = userFunction.loginUser(imei);

                if (json.getString(KEY_ERROR).equals("false")) {

                    mensagemErro = "false";
                    autorizacao_server = json.getString(KEY_APIKEY);
                    nome_user_logado = json.getString("name");

                } else {
                    mensagemErro = json.getString(KEY_MESSAGE);

                }

            } catch (Exception e) {
                mensagemErro = ("Refazer Conexao de Internet.");

            }

            return mensagemErro;

        }

        protected void onPostExecute(String result) {

            pDialog.dismiss();

            if (mensagemErro.equals("false")) {

                var.setChave(autorizacao_server);// AQUI A CHAVE ENVIADO PELO WEBSERVICE � GUARDADA COMO VARIAVEL GLOBAL(APIKEY)

                var.setNome_usuario(nome_user_logado);
                var.setLogin("true");
                entregador.setText(var.getNome_usuario());
                /*teste para colocar o nome no login*/
                entregador.setText(nome_user_logado);

            } else {

                var.setLogin("false");
                entregador.setText(mensagemErro);
                msg1.setText("IMEI do Entregador:");
                msg2.setText(IDAparelho);

            }

        } // onpost

    } // async

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    Log.d("permissoes1", "REQUEST_GALLERY");
                    Toast.makeText(MainActivity.this, "Permissoes OK " ,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    Log.d("permissoes1", "REQUEST_CAMERA");
               /* if (imageUri != null) {
                    inspect(imageUri);
                }*/
                    Toast.makeText(MainActivity.this, "Permissoes OK2 " ,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void restartActivity(){
        Intent mIntent = getIntent();
        finish();
        startActivity(mIntent);
    }
}// CLASS
