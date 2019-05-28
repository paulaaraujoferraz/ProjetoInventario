package br.com.inventario.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.inventario.BuildConfig;
//import br.com.inventario.Main4Activity;
import br.com.inventario.R;
import br.com.inventario.adapters.RegistraMotivoAdapter;
import br.com.inventario.model.NotaFiscal;
import br.com.inventario.utils.DetectorInternet;
import br.com.inventario.utils.VariaveisGlobais;

import static android.content.ContentValues.TAG;


public class BaixacomrecebedorActivity extends Activity  {
    public static String KEY_ERROR = "error";
    ArrayList<NotaFiscal> codigoslist = new ArrayList<NotaFiscal>();
    String chave = null;
    private RegistraMotivoAdapter rowAdapter;
    private DetectorInternet detectainternet;
    private ProgressDialog pDialog;
    private TextView totalreg;
    private ListView lstView;
    /*VARIAVEIS PARA O NOVO GPS*/
    private TextView latitude;
    private TextView longitude;
    private TextView nrentrega2;
    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;
    /* VARIAVEIS PARA O  NOVO GPS*/
    private String idnota = "00000";
    private String lat = "11.1111111";
    private String log = "11.1111111";
    private String nrnff = "0000000";
    private String nova = "nova";

    private TextView nrentrega;

    //private Button button2;
    private ImageButton button2;
    private EditText edtobs; // EDITTEXT PARA ENTRADA OBS QUANDO DIGITADO

    /* variaveis para as fotos*/
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
  //  private static final String TAG = Main4Activity.class.getSimpleName();
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private String answer;
    private String controle = "0";
    private Uri fileUri; // file url to store image/video
    static String imageFilePath;

    /**
     * returning image / video
     *
     *
     */
    /** Add support for inflating the <fragment> tag. **/
    @NonNull
    private static File getOutputMediaFile(int type) {

        /* TESTE INICIO*/
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Camera");
        /* testes para Androi 8.0 e 7*/
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.d(TAG, "Falha na criação do diretório para gravar imagem  "
                        + br.com.inventario.Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        /* testes para Androi 8.0 e 7*/
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (image == null) {
            try {
                image = File.createTempFile(imageFileName,".jpg",storageDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (image.getAbsolutePath().length() > 0) {
            /* tem valor*/
            imageFilePath = image.getAbsolutePath();

        }else{
            //DB_PATH=getFilesDir().getAbsolutePath();

        }


        return image;
        /*TESTE FIM*/

    }




    // -------ONCREATE---------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baixacomrecebedor);
        // nrentrega.setText(VariaveisGlobais.nrnff);
        nrentrega = (TextView) findViewById(R.id.nrentrega);
        nrentrega.setText(VariaveisGlobais.nrnff);
        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        //nrentrega2 = (TextView) findViewById(R.id.nrentrega2);
        //button2 = (Button) findViewById(R.id.button2);
        button2 = (ImageButton) findViewById(R.id.button2);
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
           /*Se chegou aqui é porque o localizador está desligado,
           então colocar 11.111111 para sinalizar ao site que não pegou localizacao*/
            VariaveisGlobais.LAT = "11.1111111";
            VariaveisGlobais.LNG = "11.1111111";
        }
        // location updates: at least 1 meter and 200millsecs change
        locationManager.requestLocationUpdates(provider, 0, 0, mylistener);
        /* INICIO: INSERIR CHAMADA PARA LISTAR MOTIVOS APÓS CAPTURAR COORDENADAS */
        // setContentView(R.layout.registrarentrega);
        chave = VariaveisGlobais.getChave();
        detectainternet = new DetectorInternet(getApplicationContext());

// Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),"Atenção! Device Não Suporta Câmera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }

        /* Pegando dados da tela anterior */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idnota = extras.getString("idnota");
            lat = extras.getString("lat");
            log = extras.getString("log");
            nrnff = extras.getString("nrnff");
            nrentrega.setText(nrnff);

            VariaveisGlobais.nrnff = nrnff;
            VariaveisGlobais.idnota = idnota;

            VariaveisGlobais.LAT = lat;
            VariaveisGlobais.LNG = log;


            /* se a coordenada vier null colocar zero*/
            if (lat == null) {
                lat = "11.1111111";
                log = "11.1111111";
            }
        }
        /* Pegando dados da tela anterior
         *          *
         * */
        /* Bundle extras = getIntent().getExtras();*/
       /* if (extras != null) {
            idnota = extras.getString("idnota");
            nrnff = extras.getString("nrnff");*/
        /* Ocorrencias precisam de variaveis globais para tirar varias fotos
         *
         * Zera e atribui novamente*/
        // trocatela.putExtra("lat", lat);
        // trocatela.putExtra("log", log);


        // nrentrega2.setText(nrnff);
        // }

        edtobs = (EditText) findViewById(R.id.edtobs);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (VariaveisGlobais.idnota  != "00000") {

                    String obs_digitado = edtobs.getText().toString();

                    if (obs_digitado.length() < 2) {
                        obs_digitado = "NaoInformado";
                    }
                    //VariaveisGlobais.obs_ocorrencia = obs_digitado;
                    VariaveisGlobais.recebedor = obs_digitado;

                    captureImage();


                  /*  Intent it = new Intent(OcorrenciaActivity.this, InserirNolocalAguardandoActivity.class);
                    startActivity(it);
                              Intent trocatela = new Intent(OcorrenciaActivity.this, InserirNolocalAguardandoActivity.class);
                    trocatela.putExtra("idnota", idnota);
                    trocatela.putExtra("lat", VariaveisGlobais.LAT);
                    trocatela.putExtra("log", VariaveisGlobais.LNG);
                    trocatela.putExtra("nrnff", nrnff);
                    String obs_digitado = edtobs.getText().toString();
                    if (obs_digitado.length() < 2) {
                        obs_digitado = " Sem Observacoes ";
                    }
                    trocatela.putExtra("obs", obs_digitado);
                    OcorrenciaActivity.this.startActivity(trocatela);
                    OcorrenciaActivity.this.finish();*/
                    /*FIM: chama tela que escolhe motivos*/
                    /*FIM: chama tela que escolhe motivos*/

                } else {
                    /* se o idnota estiver zerado retorna para a tela inicial*/
                    Intent myIntent = new Intent
                            (BaixacomrecebedorActivity.this, br.com.inventario.view.MainActivity.class);
                    BaixacomrecebedorActivity.this.startActivity(myIntent);
                    BaixacomrecebedorActivity.this.finish();
                }



            }
        });




    }// onCreate

    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Launching camera app to capture image
     *
     *
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        /* teste*/
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clip=
                    ClipData.newUri(getContentResolver(), "A photo", fileUri);

            intent.setClipData(clip);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        else {
            List<ResolveInfo> resInfoList=
                    getPackageManager()
                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, fileUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }
        /*teste*/


        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }


    /**
     * Launching camera app to record video
     */
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file




        // name
        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity


                launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "Captura da imagem cancelada pelo usuário", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Falha na captura da imagem", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // video successfully recorded
                // launching upload activity
                launchUploadActivity(false);

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    private void launchUploadActivity(final boolean isImage) {
        Intent i = new Intent(BaixacomrecebedorActivity.this, br.com.inventario.UploadActivity.class);
        i.setDataAndType(fileUri, "image/jpeg");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        //i.putExtra("filePath", fileUri.getPath());
        if (imageFilePath.length()>0) {
            i.putExtra("filePath", imageFilePath);
        }else{
            i.putExtra("filePath", fileUri.getPath());
        }
        //Glide.with(this).load(imageFilePath).into(mImageView);


//Toast.makeText(getApplicationContext(),"pathPHP "+fileUri.getPath(), Toast.LENGTH_SHORT)
        // Toast.makeText(getApplicationContext(), "Foto Capturada com Sucesso ", Toast.LENGTH_SHORT).show();
        i.putExtra("isImage", isImage);
        i.putExtra("idnota", VariaveisGlobais.idnota);
        i.putExtra("lat", VariaveisGlobais.LAT);
        i.putExtra("log", VariaveisGlobais.LNG);


        i.putExtra("nrnff", VariaveisGlobais.nrnff);
        i.putExtra("recebedor",  VariaveisGlobais.recebedor);
        i.putExtra("chave", VariaveisGlobais.getChave());
        i.putExtra("controle", nova);

        // Toast.makeText(getApplicationContext(), "Primeira ou Foto?  "+nova, Toast.LENGTH_SHORT).show();


        startActivity(i);
        BaixacomrecebedorActivity.this.finish();
    }

    /**
     * Creating file uri to store image/video
     Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
     BuildConfig.APPLICATION_ID + ".provider",
     createImageFile());
     */
    public Uri getOutputMediaFileUri(int type) {
        //return Uri.fromFile(getOutputMediaFile(type));
        /*Uri photoURI = br.com.inventario.Fileprovider.getUriForFile(BaixacomrecebedorActivity.this,
                br.com.inventario.BuildConfig.APPLICATION_ID + ".provider",
                getOutputMediaFile(type));*/
        //Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "br.com.inventario", getOutputMediaFile(type));
        // return FileProvider.getUriForFile(getApplicationContext(), "br.com.inventario", getOutputMediaFile(type));

        //return photoURI;
        return FileProvider.getUriForFile(BaixacomrecebedorActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                getOutputMediaFile(type));
    }


    /* INICIO: CLASSE PARA  O  NOVO GPS */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // Initialize the location fields
            //latitude.setText("Lat: "+String.valueOf(location.getLatitude()));
            //longitude.setText("Long: "+String.valueOf(location.getLongitude()));
            //Valores que serao passados ao Webservices por meio do ConectaFacade
            double dlat = location.getLatitude();
            VariaveisGlobais.LAT = "" + dlat;
            double dlng = location.getLongitude();
            VariaveisGlobais.LNG = "" + dlng;
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

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        //finish();
        Intent myIntent = new Intent
                (BaixacomrecebedorActivity.this, br.com.inventario.view.MainActivity.class);
        BaixacomrecebedorActivity.this.startActivity(myIntent);
        BaixacomrecebedorActivity.this.finish();

    }

}


