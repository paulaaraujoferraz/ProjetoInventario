package br.com.inventario.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//import br.com.inventario.Main2Activity;
//import br.com.inventario.Main4Activity;
import br.com.inventario.R;
import br.com.inventario.adapters.RegistraNotaEntregueAdapter;
import br.com.inventario.model.NotaFiscal;
import br.com.inventario.utils.DetectorInternet;
import br.com.inventario.utils.VariaveisGlobais;
import br.com.inventario.web.ConectaFacade;


public class RegitrarNotaEntregueActivity extends Activity implements
        OnItemClickListener {
    public static String NUM_CODIGOBARRAS = "codigobarras";
    public static String KEY_ERROR = "error";
    ArrayList<NotaFiscal> codigoslist = new ArrayList<NotaFiscal>();
    Context context;
    VariaveisGlobais var_global;
    String chave = null;
    private RegistraNotaEntregueAdapter rowAdapter;
    private DetectorInternet detectainternet;
    private ProgressDialog pDialog;
    private TextView totalreg;
    private ListView lstView;

    private EditText edtnrpesquisa;


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

        setContentView(R.layout.registrarentrega);
        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
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
          //  Toast.makeText(RegitrarNotaEntregueActivity.this, "Localizador do celular desligado ",Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //startActivity(intent);
            /*Se chegou aqui é porque o localizador está desligado, então colocar 11.111111 para sinalizar ao site que não pegou localizacao*/
            VariaveisGlobais.LAT = "11.1111111";
            VariaveisGlobais.LNG = "11.1111111";
        }
        // location updates: at least 1 meter and 200millsecs change
        locationManager.requestLocationUpdates(provider, 0, 0, mylistener);
/* INICIO: INSERIR CHAMADA PARA LISTAR NOTAS APÓS CAPTURAR COORDENADAS */
       // setContentView(R.layout.registrarentrega);
        chave = VariaveisGlobais.getChave();
        detectainternet = new DetectorInternet(getApplicationContext());
        new ListaNotasWeb().execute();
        lstView = (ListView) findViewById(R.id.listView1);
        lstView.setOnItemClickListener(this);
/* FIM: INSERIR CHAMADA PARA LISTAR NOTAS APÓS CAPTURAR COORDENADAS*/
/* FIM TRATAMENTO GPS*/

        //Toast.makeText(RegitrarNotaEntregueActivity.this, "TESTE NO ONCREATE NO FIM ",Toast.LENGTH_SHORT).show();

    }// onCreate

    /*INICIO: pesquisa por nr entrega*/

    public void pesquisar_entrega(View v) {
        edtnrpesquisa = (EditText) findViewById(R.id.edtnrentrega);
         if (edtnrpesquisa.length() < 2 ) {
             AlertDialog alertDialog = new AlertDialog.Builder(this).create();
             alertDialog.setTitle("Nao informou código para pesquisa");
             alertDialog.setMessage("Favor Repetir ");
             alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int which) {
// here you can add functions
                 }
             });
             alertDialog.setIcon(R.drawable.fail);
             alertDialog.show();

         } else {

             /*INICIO: chama tela para apresentar resultado da pesquisa*/
        //     Intent trocatela = new Intent(RegitrarNotaEntregueActivity.this, br.com.inventario.view.RegNotaEntreguePesquisaActivity.class);
             String nrdigitado = edtnrpesquisa.getText().toString();
        //     trocatela.putExtra("nrpesquisa", nrdigitado);
        //     RegitrarNotaEntregueActivity.this.startActivity(trocatela);
             RegitrarNotaEntregueActivity.this.finish();
             /*FIM: chama tela para apresentar resultado da pesquisa*/




             //Toast.makeText(this, "PESQUISA POR NR DA ENTREGA no JSON",
              //       Toast.LENGTH_SHORT).show();






         }




    }

    /*FIM: pesquisa por nr entrega*/



    /* INICIO: CLASSE PARA  O  NOVO GPS */
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            // Initialize the location fields
            //latitude.setText("Latitude: "+String.valueOf(location.getLatitude()));
            //longitude.setText("Longitude: "+String.valueOf(location.getLongitude()));
            //Valores que serao passados ao Webservices por meio do ConectaFacade
            double dlat = location.getLatitude();
            VariaveisGlobais.LAT = "" + dlat;
            double dlng = location.getLongitude();
            VariaveisGlobais.LNG = "" + dlng;
            //VariaveisGlobais.Data_Coordenada = new Date();
            //Toast.makeText(RegitrarNotaEntregueActivity.this, "Localizacao OK ",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Toast.makeText(RegitrarNotaEntregueActivity.this, provider + " status alterado para "+status +"!",
            //        Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            //Toast.makeText(RegitrarNotaEntregueActivity.this, "Provedor " + provider + " habilitado!",
            //        Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
            //Toast.makeText(RegitrarNotaEntregueActivity.this, "Provedor " + provider + " desabilitado!",
            //        Toast.LENGTH_SHORT).show();
        }
    }
 /* FIM: CLASSE PARA  O  NOVO GPS */



    public void updateList() {

        rowAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
                            long arg3
    ) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                RegitrarNotaEntregueActivity.this);
        alertDialogBuilder.setTitle ("Registrar OBS e Foto do Produto? " );
        alertDialogBuilder
                .setMessage("" + codigoslist.get(arg2).getNumero_nota())
                .setCancelable(false)
                .setIcon(R.drawable.question)
                .setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                    final String lat = VariaveisGlobais.LAT;
                                    final String log = VariaveisGlobais.LNG;
                                   // if (lat == "11.1111111"){ Toast.makeText(RegitrarNotaEntregueActivity.this, "Localizador Desligado ",Toast.LENGTH_SHORT).show(); }
                                    /* QUANDO VariaveisGlobais.LAT = 11.1111111 eh porque veio da pagina anterior ou entrou na condição de localizador desligado*/
                                     /*INICIO: chama tela que tira foto e passa as variáveis*/
                                    Intent trocatela = new
                                            Intent(RegitrarNotaEntregueActivity.this, br.com.inventario.view.BaixacomrecebedorActivity.class);
                                    String idnota = codigoslist.get(arg2).getId_nota();
                                    String nrnff = codigoslist.get(arg2).getNumero_nota();
                                    trocatela.putExtra("idnota", idnota);
                                    trocatela.putExtra("lat", lat);
                                    trocatela.putExtra("log", log);
                                    trocatela.putExtra("nrnff", nrnff);
                                    RegitrarNotaEntregueActivity.this.startActivity(trocatela);
                                    RegitrarNotaEntregueActivity.this.finish();
                                   /*FIM: chama tela que tira foto e passa as variáveis*/
                            }
                        })
                .setNegativeButton("Nao",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    public void startSecondActivity(View view) {
      //  Intent secondActivity = new Intent(this, Main2Activity.class);
     //   startActivity(secondActivity);
    }

    //AQUI � USADO ASYNCTASK PARA ACESSAR LISTA DE NOTAS NO WEBSERVICE. SEGUE O PADR�O ASYNC DO ANDRIOD
    class ListaNotasWeb extends AsyncTask<String, Void, ArrayList<NotaFiscal>> {
        String mensagemErro = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegitrarNotaEntregueActivity.this);
            pDialog.setMessage("Conectando. Aguarde ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected ArrayList<NotaFiscal> doInBackground(String... list) {
            JSONArray jsonarray;
            try {
                //
                ConectaFacade enviar = new ConectaFacade();
                JSONObject json = new JSONObject();
                json = enviar.listarNotas(chave);
                if (json.getString(KEY_ERROR).equals("false")) {
                    jsonarray = json.getJSONArray("notas");
                    for (int i = 0; i < jsonarray.length(); i++) {
                        NotaFiscal notafiscal = new NotaFiscal();
                        json = jsonarray.getJSONObject(i);
                        notafiscal.setId_nota(json.getString("id"));
                        notafiscal.setDatahoraleitura(json.getString("datahoraleitura"));
                        notafiscal.setEmpresa(json.getString("empresa"));
                        notafiscal.setNumero_nota(json.getString("numnota"));
                        notafiscal.setNumero_endereco(json.getString("endereco"));
                        notafiscal.setnome(json.getString("nome"));
                        notafiscal.setprioridade(json.getString("prioridade"));
                        notafiscal.setvolumes(json.getString("volumes"));


                        codigoslist.add(notafiscal);
                    }
                }
            } catch (JSONException e) {
                Log.e("json error", e.getMessage());
                e.printStackTrace();
                mensagemErro = ("erro." + e.toString());

            } catch (Exception e) {
                mensagemErro = ("Nao foi possivel conectar no servidor." + e
                        .toString());
            }
            return codigoslist;
        }

        protected void onPostExecute(ArrayList<NotaFiscal> list) {
            pDialog.dismiss();
            if (mensagemErro == null) {
                totalreg = (TextView) findViewById(R.id.txttotal);
                int total = codigoslist.size();
                totalreg.setText("Produtos Conferidos: " + total);
                rowAdapter = new RegistraNotaEntregueAdapter(getApplicationContext(), codigoslist);
                lstView = (ListView) findViewById(R.id.listView1);
                lstView.setAdapter(rowAdapter);
            } else {
                Toast toast = Toast.makeText(RegitrarNotaEntregueActivity.this,
                        mensagemErro, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Log.e("mensagemErro", mensagemErro);
            }
        }
    }
}
