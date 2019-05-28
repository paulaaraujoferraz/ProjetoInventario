package br.com.inventario;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.inventario.AndroidMultiPartEntity.ProgressListener;
import br.com.inventario.utils.VariaveisGlobais;
import br.com.inventario.view.MainActivity;
//import br.com.inventario.view.OcorrenciaretornoActivity;


public class UploadOcorrenciaActivity extends Activity {
    // LogCat tag
  //  private static final String TAG = Main4Activity.class.getSimpleName();
    long totalSize = 0;
    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private VideoView vidPreview;
    private Button btnUpload;
    private String idnota = "11111";
    private String lat = "00.0000000";
    private String log = "00.0000000";
    private String nrnff = "0000000";
    private String chave = "0000000";
    private String obs_ocorrencia = "0000000";
    private String controle = "0000000";;



    int SDK_INT = android.os.Build.VERSION.SDK_INT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_ocorrencia);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        vidPreview = (VideoView) findViewById(R.id.videoPreview);
        // Receiving the data from previous activity
        Intent i = getIntent();
        // image or video path that is captured in previous activity
        filePath = i.getStringExtra("filePath");
        // boolean flag to identify the media type, image or video
        boolean isImage = i.getBooleanExtra("isImage", true);

        /* Pegando dados da tela anterior*/
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idnota = VariaveisGlobais.idnota;
            nrnff = VariaveisGlobais.nrnff;
            obs_ocorrencia = VariaveisGlobais.obs_ocorrencia;
            chave = VariaveisGlobais.getChave();

            lat = extras.getString("lat");
            log = extras.getString("log");
            controle = extras.getString("controle");

            /* se a coordenada vier null colocar zero*/
            if (lat == null) {
                lat = "11.1111111";
                log = "11.1111111";
            }

        }


        if (filePath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);

        } else {
            Toast.makeText(getApplicationContext(),
                    "Atenção o arquivo com imagem está vazio!", Toast.LENGTH_LONG).show();
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
                // Toast.makeText(getApplicationContext(), "AGUARDE TRANSMISSAO DO ARQUIVO!", Toast.LENGTH_LONG).show();
                enableViews(false);
                new UploadFileToServer().execute();
            }
        });

    }

    /**
     * Displaying captured image/video on the screen
     *
     * int inSampleSize
     Se for definido para um valor> 1, solicita o descodificador à subamostra a imagem original,
     retornando uma imagem menor para economizar memória. O tamanho da amostra é o número de pixels
     em qualquer dimensão que correspondem a um único pixel no mapa de bits descodificados.
     Por exemplo, inSampleSize == 4 retorna uma imagem que é um quarto da largura / altura do original, e 1/16 o número de pixels.
     Qualquer valor <= 1 é tratado da mesma forma 1. Nota:
     o descodificador utiliza a um valor final com base em potências de 2, qualquer outro valor
     será arredondado para baixo para o mais próximo de energia 2.
     *
     */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreview.setVisibility(View.VISIBLE);
            vidPreview.setVisibility(View.GONE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();
            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            /* alterado em 06/09/2017*/
            //Toast.makeText(getApplicationContext(),"VERSAO DO ANDROID: "+SDK_INT, Toast.LENGTH_LONG).show();
            /* Se for Android 7.0 o SDK_INT vale 24
             * Para versões igual ou após Android 7
             * */
            if(SDK_INT > 23) {options.inSampleSize = 2;}else { options.inSampleSize = 6; }
            //options.inSampleSize = 6;
            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            /*INICIO: REDIMENSIONANDO IMAGEM em 15/12/2015*/
            File file;
            file = new File(filePath);
            if(SDK_INT < 24) {
                /*para versoes antes do Android 7*/
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    /* Se for Android 7.0 o SDK_INT vale 24*/
                    // versao 7 do android
                    //if(SDK_INT > 23) {bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);} else { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out); }
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    //Toast.makeText(getApplicationContext(),"COMPRESS 90 ANTES DO ANDROID7: "+SDK_INT, Toast.LENGTH_LONG).show();
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(SDK_INT > 23) {
                /*para versoes Igual ou Apos  do Android 7*/
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    /* Se for Android 7.0 o SDK_INT vale 24*/
                    // versao 7 do android
                    //if(SDK_INT > 23) {bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);} else { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out); }
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                    //Toast.makeText(getApplicationContext(),"COMPRESS 80 ANDROID7 EM DIANTE: "+SDK_INT, Toast.LENGTH_LONG).show();
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //fileUri = file;
            /*FIM: REDIMENSIONANDO IMAGEM em 15/12/2015*/


            imgPreview.setImageBitmap(bitmap);
        } else {
            imgPreview.setVisibility(View.GONE);
            vidPreview.setVisibility(View.VISIBLE);
            vidPreview.setVideoPath(filePath);
            // start playing
            vidPreview.start();
        }
    }


    public void enableViews(boolean status) {
        btnUpload.setEnabled(status);
        btnUpload.setTextColor(R.color.Vermelho);
        btnUpload.setTextSize(20);
        btnUpload.setText(status ? "Enviar" : "Enviando...AGUARDE !!!");
    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_OCORRENCIA);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                File sourceFile = new File(filePath);
                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));
                // Extra parameters if you want to pass to server
                entity.addPart("idnota", new StringBody("" + idnota));
                entity.addPart("nrnff", new StringBody("" + nrnff));
                entity.addPart("obs_ocorrencia", new StringBody("" + obs_ocorrencia));
                entity.addPart("chave", new StringBody("" + chave));

                entity.addPart("lat", new StringBody("" + lat));
                entity.addPart("log", new StringBody("" + log));
                entity.addPart("controle", new StringBody("" + controle));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    //responseString = "Erro na conexão com o Servidor codigo de erro: "
                    //       + statusCode;
                    responseString = ""+statusCode;
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            //Log.e(TAG, "Response from server: " + result);
            // showing the server response in an alert dialog
            //showAlert(result.replace(" ",""));

            super.onPostExecute(result);
            /* IMPLEMENTAR RETORNO PARA TELA INICIAL*/




            if( ( result.compareTo("0") != 0 ) && (result.compareTo("10") != 0 ) && (result.compareTo("500") != 0 )) {

                //int in = Integer.valueOf(result.toString());

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UploadOcorrenciaActivity.this);
                alertDialogBuilder.setTitle("OK! tirar mais fotos ? ");
                alertDialogBuilder
                        .setMessage(result + " Foto(s) gravada(s) para esta Ocorrência. Máximo de 10 fotos. ")
                        //.trim().replaceAll("\\s+", " ")
                        //.setMessage("result replacelen: "+result.trim().replaceAll("\\s+", " ").length()+" result: "+result+" length: "+result.length())
                        .setCancelable(false)
                        .setIcon(R.drawable.question)
                        .setPositiveButton("Sim",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        final String lat = VariaveisGlobais.LAT;
                                        final String log = VariaveisGlobais.LNG;

                                        /* AQUI temos o lat, long, motivo_id e idnota prontos para fazer o insert da tentativa */
                           //             Intent myIntent = new Intent
                          //                      (UploadOcorrenciaActivity.this, OcorrenciaretornoActivity.class);
                          //              UploadOcorrenciaActivity.this.startActivity(myIntent);
                                        UploadOcorrenciaActivity.this.finish();

                                    }
                                })
                        .setNegativeButton("Nao",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        Intent myIntent = new Intent
                                                (UploadOcorrenciaActivity.this, MainActivity.class);
                                        UploadOcorrenciaActivity.this.startActivity(myIntent);
                                        UploadOcorrenciaActivity.this.finish();

                                        //dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }else if (result.equals("10") ){

                AlertDialog alertDialog = new AlertDialog.Builder(UploadOcorrenciaActivity.this).create();
                alertDialog.setTitle("Ocorrência alcançou o número máximo de fotos ");
                alertDialog.setMessage(" 10 Fotos registradas. ");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // here you can add functions
                        Intent myIntent = new Intent
                                (UploadOcorrenciaActivity.this, MainActivity.class);
                        UploadOcorrenciaActivity.this.startActivity(myIntent);
                        UploadOcorrenciaActivity.this.finish();
                    }
                });
                alertDialog.setIcon(R.drawable.fail);
                alertDialog.show();


            }else if (result.equals("0") ){

                //Toast.makeText(getApplicationContext(),"valor retorno zero: "+result, Toast.LENGTH_LONG).show();
                AlertDialog alertDialog = new AlertDialog.Builder(UploadOcorrenciaActivity.this).create();
                alertDialog.setTitle("Erro "+result+result.length());
                alertDialog.setMessage(" Repetir ");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
// here you can add functions
                        // here you can add functions
                        Intent myIntent = new Intent
                                (UploadOcorrenciaActivity.this, MainActivity.class);
                        UploadOcorrenciaActivity.this.startActivity(myIntent);
                        UploadOcorrenciaActivity.this.finish();
                    }
                });
                alertDialog.setIcon(R.drawable.fail);
                alertDialog.show();



            }else if (result.equals("500") ){

                //Toast.makeText(getApplicationContext(),"Erro : "+result, Toast.LENGTH_LONG).show();
                AlertDialog alertDialog = new AlertDialog.Builder(UploadOcorrenciaActivity.this).create();
                alertDialog.setTitle("Erro "+result+result.length());
                alertDialog.setMessage(" Repetir ");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
// here you can add functions
                        // here you can add functions
                        Intent myIntent = new Intent
                                (UploadOcorrenciaActivity.this, MainActivity.class);
                        UploadOcorrenciaActivity.this.startActivity(myIntent);
                        UploadOcorrenciaActivity.this.finish();
                    }
                });
                alertDialog.setIcon(R.drawable.fail);
                alertDialog.show();



            }


        }
    }

}