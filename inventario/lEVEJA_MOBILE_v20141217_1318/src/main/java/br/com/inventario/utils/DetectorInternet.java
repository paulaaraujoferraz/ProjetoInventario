/*
 * Classe utilizada para detectar se existe internet disponivel no celular
 * 
 */
package br.com.inventario.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DetectorInternet {

    private Context _context;

    public DetectorInternet(Context context) {
        this._context = context;
    }

    public final boolean TemConexao() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }
}
    
    
    
    
    
    
    
    
