/*
 * CLASSE USADA PARA MANTER AS VARIAVEIS GLOBAIS DO SISTEMA
 * 
 */

package br.com.inventario.utils;

import android.app.Application;
import android.net.Uri;

import java.util.Date;

public class VariaveisGlobais extends Application {

    /* INICIO Variáveis RASTREADOR */
    public static String LAT_MOMENTO = "11.1111111"; // quando chegar no WS 11.1111111 é porque o localizador desligado
    public static String LNG_MOMENTO = "11.1111111"; //  quando chegar no WS 11.1111111 é porque o localizador desligado
    public static String STATUS_LOCALIZADOR = ""; //  GRAVA SITUACAO DO LOCALIZADOR
    public static Uri outputUri  ;


    /* INICIO Variáveis para o GPS */
    public static String LAT = "00.0000000"; //  Latitude
    public static String LNG = "00.0000000"; //  Longitude

    public static String LATO = "00.0000000"; //  Latitude
    public static String LNGO = "00.0000000"; //  Longitude

    public static String motivotentativa = "000000000"; //
    public static String obs_ocorrencia = "000000000"; //
    public static String recebedor = "000000000"; //

    public static String idnota = "000000000"; //
    public static String nrnff = "000000000"; //
    public static String IMEI_GLOBAL = "000000000"; //

    public static Date Data_Atual; //  Para guardar data atual
    public static Date Data_Coordenada; //  Para guardar data da coordenada
    public static long longlocation;
    private static String usuariologado; // USADO PARA SABER SE O USUARIO LOGOU OU NAO
    private static String chave; //  CHAVE ENVIADA PELO WEBSERVICE
 /* FIM Variáveis para o GPS */
    private static String nome_usuario;
    private static String msg_erro_login;
    private static String login;

    public Uri getOutputUri() {
        return outputUri;
    }

    public Uri setOutputUri() {
        return outputUri;
    }

    // INICIO Para tratar Latitude e Longitude
     long getoutputUri() {
        return longlocation;
    }

    public static long getlonglocation() {
        return longlocation;
    }

    public static Date getData_Atual() {
        return Data_Atual;
    }

    public void setData_Atual(Date Data_Atual) {
    }

    public static Date getData_Coordenada() {
        return Data_Coordenada;
    }

    public void setData_Coordenada(Date Data_Coordenada) {
    }

    public static String getlat() {
        return LAT;
    }

    public static String getMotivotentativa() {
        return motivotentativa;
    }
    public static String getobs_ocorrencia() {
        return obs_ocorrencia;
    }

    public static String getrecebedor() {
        return recebedor;
    }

    public static String getIdnota() {
        return idnota;
    }

    public static String getNrnff() {
        return nrnff;
    }
    public static String getImeiGlobal() {
        return IMEI_GLOBAL;
    }


    public static String getlng() {
        return LNG;
    }

    public static void setlng(String LNG) {
        VariaveisGlobais.LNG = "00.0000000";
    }

    public static void setMotivotentativa(String motivotentativa) {
        VariaveisGlobais.motivotentativa = "000000000";
    }
    public static void setobs_ocorrencia(String obs_ocorrencia) {
        VariaveisGlobais.obs_ocorrencia = "000000000";
    }

    public static void setrecebedor(String recebedor) {
        VariaveisGlobais.recebedor = "000000000";
    }

    public static void setIdnota(String idnota) {
        VariaveisGlobais.idnota = "000000000";
    }
    public static void setNrnff(String nrnff) {
        VariaveisGlobais.nrnff = "000000000";
    }

    public static void setImeiGlobal(String IMEI_GLOBAL) {
        VariaveisGlobais.IMEI_GLOBAL = "000000000";
    }

    public static String getLogin() {
        return login;
    }

    public static void setLogin(String login) {
        VariaveisGlobais.login = login;
    }

// FIM Para tratar Latitude e Longitude

    public static String getMsg_erro_login() {
        return msg_erro_login;
    }

    public static void setMsg_erro_login(String msg_erro_login) {
        VariaveisGlobais.msg_erro_login = msg_erro_login;
    }

    public static String getNome_usuario() {
        return nome_usuario;
    }

    public void setNome_usuario(String nome_usuario) {
        VariaveisGlobais.nome_usuario = nome_usuario;
    }

    public static String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        VariaveisGlobais.chave = chave;
    }

    public void setLonglocation(long longlocation) {
    }

    public void setlat(String LAT) {
        VariaveisGlobais.LAT = "00.0000000";
    }

    public String getUsuariologado() {
        return usuariologado;
    }

    public void setUsuariologado(String usuariologado) {
        VariaveisGlobais.usuariologado = usuariologado;
    }


}
