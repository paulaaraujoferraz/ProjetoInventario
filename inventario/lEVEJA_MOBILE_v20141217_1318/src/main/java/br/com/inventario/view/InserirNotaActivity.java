/*
 * classe utilizada para enviar a nota para insercao
 */

package br.com.inventario.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import br.com.inventario.R;
import br.com.inventario.adapters.CodigoBarrasAdapter;
import br.com.inventario.model.NotaFiscal;
import br.com.inventario.utils.DetectorInternet;
import br.com.inventario.utils.VariaveisGlobais;
import br.com.inventario.web.ConectaFacade;

public class InserirNotaActivity extends Activity {

    private static final int ZBAR_SCANNER_REQUEST = 0;  // USADO NA FUNCAO DO CODIGO DE BARRAS CODIGO BARRAS 2D
    private static final int ZBAR_QR_SCANNER_REQUEST = 1; // USADO NA FUNACO DO CODIGO DE BARRAS QR CODE
    public static String NUM_CODIGOBARRAS = "codigobarras";
    public static String KEY_ERROR = "error";
    private static String KEY_MESSAGE = "message";
    ArrayList<NotaFiscal> codigoslist = new ArrayList<NotaFiscal>(); // ARRAYLIST PARA NOTA FISCAL
    Context context;
    VariaveisGlobais var_global; // PARA ACESSAR A CLASSE DE VARIAVEIS GLOBAIS
    String chave = null;
    private CodigoBarrasAdapter rowAdapter;
    private DetectorInternet detectainternet;
    private ProgressDialog pDialog;
    private ListView lstView;
    private EditText edtcodigobarras; // EDITTEXT PARA ENTRADA DO CODIGO DE BARRAS QUANDO DIGITADO


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


    // -------ONCREATE---------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inserirnota2);

        /* INICIO: GPS NOVO*/
        latitudeo1 = (TextView) findViewById(R.id.latitudeo);
        longitudeo1 = (TextView) findViewById(R.id.longitudeo);
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
            Toast.makeText(InserirNotaActivity.this, "Localizacao Desligado ",Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //startActivity(intent);
            /* quando o localizador está desabilitado as coordenadas recebem 00.00000 e segue em frente*/

        }
        // location updates: at least 1 meter and 200millsecs change
        locationManager.requestLocationUpdates(provider, 200, 1, mylistener);

        /* FIM: GPS NOVO*/

        codigoslist.clear();

        // VERIFICA SE O ARRAYLIST EST� VAZIO OU J� EXISTE
        if (rowAdapter == null) {
            rowAdapter = new CodigoBarrasAdapter(getApplicationContext(),codigoslist);
            lstView = (ListView) findViewById(R.id.listView1);
            lstView.setAdapter(rowAdapter);
        } else {
            rowAdapter.notifyDataSetChanged();
        }

        chave = VariaveisGlobais.getChave();
        detectainternet = new DetectorInternet(getApplicationContext());
        edtcodigobarras = (EditText) findViewById(R.id.edtnumcodigobarras);
        // AQUI ESTA MONITORANDO O EDITTEXT SE EXISTIR UM ENTER NO TECLADO , ELE IR� EXECUTAR ALGO
        edtcodigobarras.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            String codigo_lido = edtcodigobarras.getText()
                                    .toString();
                            String[] list_itens = new String[]{codigo_lido};
                            new UploadFileAsync().execute(list_itens);
                            edtcodigobarras.setText("");
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

    }// onCreate

    /* INICIO: CLASSE PARA  O  NOVO GPS */
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

           /* se o localizador estiver desligado as variaveis vão permanecer com o valor 11.1111111 */
            VariaveisGlobais.LATO = "11.1111111";
            VariaveisGlobais.LNGO = "11.1111111";
            VariaveisGlobais.LAT = "11.1111111";
            VariaveisGlobais.LNG = "11.1111111";

            // Initialize the location fields
            //latitudeo1.setText("Latitude: "+String.valueOf(location.getLatitude()));
           // longitudeo1.setText("Longitude: "+String.valueOf(location.getLongitude()));
            //Valores que serao passados ao Webservices por meio do ConectaFacade
            double dlat = location.getLatitude();
            VariaveisGlobais.LATO = "" + dlat;
            double dlng = location.getLongitude();
            VariaveisGlobais.LNGO = "" + dlng;
          //  VariaveisGlobais.Data_Coordenada = new Date();
           // Toast.makeText(InserirNotaActivity.this, "Localizacao OK ",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
           // Toast.makeText(InserirNotaActivity.this, provider + "'status provedor alterado "+status +"!",
           //         Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            //Toast.makeText(InserirNotaActivity.this, "Provedor " + provider + " habilitado!",
                    //Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
           // Toast.makeText(InserirNotaActivity.this, "Provedor " + provider + " desabilitado!",
           //         Toast.LENGTH_SHORT).show();
        }
    }
 /* FIM: CLASSE PARA  O  NOVO GPS */

    // AQUI � FEITO A ATUALIZA��O DA LISTA QUANDO ELA J� EXISTE
    public void updateList() {

        rowAdapter.notifyDataSetChanged();
        edtcodigobarras.requestFocus();
    }

    public void launchScanner(View v) {
        if (isCameraAvailable()) {
            if (Build.VERSION.SDK_INT < 24) {
                Log.i("leitor de barcode ", " versao do SDK "+Build.VERSION.SDK_INT);

                Intent intent = new Intent(this, ZBarScannerActivity.class);
                startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
            }else{
                Log.i("leitor de barcode ", " versao do SDK "+Build.VERSION.SDK_INT);
                Intent intent = new Intent(this, ZBarScannerActivity.class);
                startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
            }
        } else {
            Toast.makeText(this, "Rear Facing Camera Unavailable",
                    Toast.LENGTH_SHORT).show();
        }
    }

	/*
     * LEITOR DE BARRAS VIA CAMERA. NO PROJETO � NECESSARIO ADICIONAR A LIB ZBAR_LIB
	 */

    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ZBAR_SCANNER_REQUEST:
            case ZBAR_QR_SCANNER_REQUEST:
                if (resultCode == RESULT_OK) {

                    String codigo_lido_camera = data
                            .getStringExtra(ZBarConstants.SCAN_RESULT);
                    String[] list_itens = new String[]{codigo_lido_camera};

                    new UploadFileAsync().execute(list_itens);

                } else if (resultCode == RESULT_CANCELED && data != null) {
                    String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
                    if (!TextUtils.isEmpty(error)) {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //CLASSE ASYNCTASK PARA ACESSAR O WEBSERVICE USANDO THREADS. SEGUE O PADR�O ANDROID DA CLASSE
    class UploadFileAsync extends
            AsyncTask<String, Void, ArrayList<NotaFiscal>> {

        String mensagemErro = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(InserirNotaActivity.this);
            pDialog.setMessage("Conectando. Aguarde ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected ArrayList<NotaFiscal> doInBackground(String... list) {

            final Boolean internet = detectainternet.TemConexao();

            if (!internet) {

                mensagemErro = ("Sem INTERNET. Verifique sua conex�o");

            } else {


                /* valores da localização de origem*/
                String lato = VariaveisGlobais.LATO;
                String logo = VariaveisGlobais.LNGO;

                String numcodigo = list[0];

                NotaFiscal notafiscal = new NotaFiscal();

                try {
                    //

                        ConectaFacade enviar = new ConectaFacade();
                        JSONObject json = new JSONObject();
                        json = enviar.novaNotaWeb(numcodigo, chave, lato, logo);

                    if (json.getString(KEY_ERROR).equals("false")) {

                        notafiscal.setNumero_codigobarras(numcodigo);
                        notafiscal.setStatus(json.getString(KEY_MESSAGE));
                        notafiscal.setCor("VERDE");
                        codigoslist.add(notafiscal);

                    } else {

                        notafiscal.setNumero_codigobarras(numcodigo);
                        notafiscal.setStatus(json.getString(KEY_MESSAGE));
                        notafiscal.setCor("RED");
                        codigoslist.add(notafiscal);
                    }

                } catch (Exception e) {
                    mensagemErro = ("Erro:" + e.toString()+" NUNCOD: "+numcodigo+" API: "+chave+" LAT: "+lato+" lng: "+logo);

                }

            }

            return codigoslist;

        }

        protected void onPostExecute(ArrayList<NotaFiscal> list) {

            pDialog.dismiss();

            if (mensagemErro == null) {
                updateList();
            } else {

                Toast toast = Toast.makeText(InserirNotaActivity.this,
                        mensagemErro, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Log.e("mensagemErro", mensagemErro);

            }

        }

    }

    // classe
}
