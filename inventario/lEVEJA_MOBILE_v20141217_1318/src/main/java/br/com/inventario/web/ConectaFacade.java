package br.com.inventario.web;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ConectaFacade {


    /* novo WS a partir de 18/03/2018
     *
      * Ocorrencia com fotos e fotos na tentativa de entrega
      * */
    private static String loginURL = "http://inventario.jelastic.saveincloud.net/wsinventario/v1/index.php/login";
    private static String InserirNovaNotaURL = "http://inventario.jelastic.saveincloud.net/wsinventario/v1/index.php/inserirnota";
    private static String listaNotaURL = "http://inventario.jelastic.saveincloud.net/wsinventario/v1/index.php/listarnotas";
    private static String registraEntregaURL = "http://inventario.jelastic.saveincloud.net/wsinventario/v1/index.php/confirmarentrega";


    private static String listaMotivosURL = "http://inventario.jelastic.saveincloud.net/wsinventario/v1/index.php/listarmotivos";
    private static String inserirTentativaURL = "http://inventario.jelastic.saveincloud.net/wsinventario/v1/index.php/inserirtentativa";
    private static String inserirNolocalURL = "http://inventario.jelastic.saveincloud.net/wsinventario/v1/index.php/inserirnolocal";
    private static String baixaSemReciboURL = "http://inventario.jelastic.saveincloud.net/wsinventario/v1/index.php/baixasemrecibo";
    private static String listaNotaPesquisaURL = "http://inventario.jelastic.saveincloud.net/wsinventario/v1/index.php/listarnotaspesquisa";



/* PARA INSERIR LOCALIZACAO*/
/* EM 22/05/2017
O Controlog novo apntarará para o WStracking */
//private static String InserirlocalizacaoURL = "http://contrologtracking.jelastic.saveincloud.net/wstracking/v1/index.php/inserirlocalizacao";


    private br.com.inventario.web.Chamada_HTTP conecta;


    public ConectaFacade() {

        conecta = new br.com.inventario.web.Chamada_HTTP();


    }

    /**
     * funcao de login do entregador
     *
     * @param imei
     */
    public JSONObject loginUser(String imei) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("imei", imei));


        JSONObject json = conecta.getJSONFromUrl(loginURL, params);

        return json;
    }

    public JSONObject novaNotaWeb(String num_codigobarras, String chave, String lato, String lngo) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("codigobarras", num_codigobarras));
        params.add(new BasicNameValuePair("apikey", chave));
        params.add(new BasicNameValuePair("latorigem", lato));
        params.add(new BasicNameValuePair("longorigem", lngo));
        //Log.d("chave",chave);
        //	Log.d("codigobarras",num_codigobarras);
        JSONObject json = conecta.getJSONFromUrl(InserirNovaNotaURL, params);
        return json;
    }

    public JSONObject inserirtentativa(String motivoid, String idnota, String chave, String lato, String lngo) {
             // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("motivoid", motivoid));
        params.add(new BasicNameValuePair("idnota", idnota));
        params.add(new BasicNameValuePair("apikey", chave));
        params.add(new BasicNameValuePair("latorigem", lato));
        params.add(new BasicNameValuePair("longorigem", lngo));
        //Log.d("chave",chave);
        //	Log.d("codigobarras",num_codigobarras);
        JSONObject json = conecta.getJSONFromUrl(inserirTentativaURL, params);
        return json;
    }


    public JSONObject inserirnolocalaguardando(String obs, String idnota, String chave, String lat, String log) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("obs", obs));
        params.add(new BasicNameValuePair("idnota", idnota));
        params.add(new BasicNameValuePair("apikey", chave));
        params.add(new BasicNameValuePair("latorigem", lat));
        params.add(new BasicNameValuePair("longorigem", log));
        //Log.d("chave",chave);
        //	Log.d("codigobarras",num_codigobarras);
        JSONObject json = conecta.getJSONFromUrl(inserirNolocalURL, params);
        return json;
    }
    /* Baixasemreceibo com nome do recebedor, a partir de 11/05*/
    public JSONObject Baixasemrecibo(String idnota, String chave, String lato, String lngo, String recebedor) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id_nota", idnota));
        params.add(new BasicNameValuePair("apikey", chave));
        params.add(new BasicNameValuePair("lat", lato));
        params.add(new BasicNameValuePair("lng", lngo));
        params.add(new BasicNameValuePair("recebedor", recebedor));


        //Log.d("chave",chave);
        //	Log.d("codigobarras",num_codigobarras);


        JSONObject json = conecta.getJSONFromUrl(baixaSemReciboURL, params);
        return json;
    }


        /* FUNÇÃO PARA ENVIAR LOCALIZAÇÃO AO WS */

    /*public JSONObject novalocalizacao(String num_codigobarras, String chave, String lato, String lngo) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("codigobarras", num_codigobarras));
        params.add(new BasicNameValuePair("apikey", chave));
        params.add(new BasicNameValuePair("latorigem", lato));
        params.add(new BasicNameValuePair("longorigem", lngo));
        //Log.d("chave",chave);
        //	Log.d("codigobarras",num_codigobarras);
        JSONObject json = conecta.getJSONFromUrl(InserirlocalizacaoURL, params);
        return json;
    }*/


    public JSONObject listarNotas(String chave) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("apikey", chave));
        JSONObject json = conecta.getJSONFromUrl(listaNotaURL, params);
        return json;
    }

    public JSONObject listarNotasPesquisa(String chave,String nrentrega) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("apikey", chave));
        params.add(new BasicNameValuePair("nrentrega", nrentrega));

        JSONObject json = conecta.getJSONFromUrl(listaNotaPesquisaURL, params);
        return json;
    }



    public JSONObject listarmotivos(String chave) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("apikey", chave));
        JSONObject json = conecta.getJSONFromUrl(listaMotivosURL, params);
        return json;

    }


    public JSONObject registraEntrega(String id_nota, String chave, String lat, String lng) {
/* Rotina do GPS NÃO DÁ CERTO aqui*/
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("id_nota", id_nota));
        params.add(new BasicNameValuePair("apikey", chave));

// INICIO: Tratamento latitude e longitude
        params.add(new BasicNameValuePair("lat", lat));
        params.add(new BasicNameValuePair("lng", lng));


// FIM: Tratamento latitude e longitude


        JSONObject json = conecta.getJSONFromUrl(registraEntregaURL, params);

        return json;

    }


}
