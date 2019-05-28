package br.com.inventario.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import br.com.inventario.domain.WrapData;

public class HttpConnection {
    public static String getSetDataWeb(WrapData wd) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(wd.getUrl());
        String answer = "";

        try {
            ArrayList<NameValuePair> valores = new ArrayList<NameValuePair>();
            valores.add(new BasicNameValuePair("method", wd.getMethod()));
            valores.add(new BasicNameValuePair("name", wd.getName()));
            valores.add(new BasicNameValuePair("email", wd.getEmail()));
            valores.add(new BasicNameValuePair("age", wd.getAge() + ""));
            valores.add(new BasicNameValuePair("nrnff", wd.getNrnff()));
            valores.add(new BasicNameValuePair("img-mime", wd.getImage().getMime()));
            valores.add(new BasicNameValuePair("img-image", wd.getImage().getBitmapBase64()));

            httpPost.setEntity(new UrlEncodedFormEntity(valores));
            HttpResponse resposta = httpClient.execute(httpPost);
            answer = EntityUtils.toString(resposta.getEntity());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (answer);
    }
}
