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


public class Uploadtentativa extends Activity {
    // LogCat tag
   // private static final String TAG = Main4Activity.class.getSimpleName();
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
    private String motivotentativa = "0000000";
    private String chave = "0000000";
    private String nrnff = "0000000";

    int SDK_INT = android.os.Build.VERSION.SDK_INT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_tentativas);
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
            idnota = extras.getString("idnota");
            lat = extras.getString("lat");
            log = extras.getString("log");
            nrnff = extras.getString("nrnff");
            motivotentativa = extras.getString("motivotentativa");
            chave = extras.getString("chave");

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
                Toast.makeText(getApplicationContext(),
                        "AGUARDE TRANSMISSAO DO ARQUIVO!", Toast.LENGTH_LONG).show();
                enableViews(false);
                new UploadFileToServer().execute();
            }
        });

    }

    /**
     * Displaying captured image/video on the screen
     * <p>
     * int inSampleSize
     * Se for definido para um valor> 1, solicita o descodificador à subamostra a imagem original,
     * retornando uma imagem menor para economizar memória. O tamanho da amostra é o número de pixels
     * em qualquer dimensão que correspondem a um único pixel no mapa de bits descodificados.
     * Por exemplo, inSampleSize == 4 retorna uma imagem que é um quarto da largura / altura do original, e 1/16 o número de pixels.
     * Qualquer valor <= 1 é tratado da mesma forma 1. Nota:
     * o descodificador utiliza a um valor final com base em potências de 2, qualquer outro valor
     * será arredondado para baixo para o mais próximo de energia 2.
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
            if (SDK_INT > 23) {
                options.inSampleSize = 2;
            } else {
                options.inSampleSize = 6;
            }
            //options.inSampleSize = 6;
            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            /*INICIO: REDIMENSIONANDO IMAGEM em 15/12/2015*/
            File file;
            file = new File(filePath);
            if (SDK_INT < 24) {
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

            if (SDK_INT > 23) {
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
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_TENTATIVA);
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
                entity.addPart("lat", new StringBody("" + lat));
                entity.addPart("log", new StringBody("" + log));
                entity.addPart("nrnff", new StringBody("" + nrnff));
                entity.addPart("motivotentativa", new StringBody("" + motivotentativa));
                entity.addPart("chave", new StringBody("" + chave));
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
                    responseString = "Erro na conexão com o Servidor codigo de erro: "
                            + statusCode;
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
            //showAlert(result);
            //super.onPostExecute(result);
            /* IMPLEMENTAR RETORNO PARA TELA INICIAL*/

            AlertDialog.Builder dialog3 = new
                    AlertDialog.Builder(Uploadtentativa.this);
            dialog3.setMessage(" " + result);
            dialog3.setIcon(R.drawable.success);
            dialog3.setPositiveButton("OK", new
                    DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface di, int arg) {
                            /* zera as variaveis globais das coordenadas*/

                            VariaveisGlobais.LAT = "00.000000";
                            VariaveisGlobais.LNG = "00.000000";

                            /* fecha a pagina atual e envia para a tela inicial*/

                            Intent myIntent = new Intent
                                    (Uploadtentativa.this, MainActivity.class);
                            Uploadtentativa.this.startActivity(myIntent);
                            Uploadtentativa.this.finish();


                        }
                    });
            dialog3.setTitle("Resultado:");
            dialog3.show();

            /*ATENÇÃO NÃO ESTÁ RETORNANDO CORRETO*/


        }
    }
}

