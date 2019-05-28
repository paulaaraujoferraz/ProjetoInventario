/*
 * 
 * ADAPTER USADO NO INSERIRNOTA
 * 
 *  Modelagem da listview ? realizada nessa classe
 */

package br.com.inventario.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.inventario.R;
import br.com.inventario.model.NotaFiscal;


public class CodigoBarrasAdapter extends BaseAdapter {
    LayoutInflater inflater;
    ArrayList<NotaFiscal> codigoslist;
    private Context context;

    public CodigoBarrasAdapter(Context context, ArrayList<NotaFiscal> codigoslist) {
        inflater = LayoutInflater.from(context);
        this.codigoslist = codigoslist;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ListContent holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_column, null);

            holder = new ListContent();
            holder.txtCodigoBarras = (TextView) convertView.findViewById(R.id.txtcodigobarras);
            holder.txtstatus = (TextView) convertView.findViewById(R.id.ColStatus);





            convertView.setTag(holder);
        } else {
            holder = (ListContent) convertView.getTag();
        }

        //  holder.txtnumero_codigobarras.setText(searchArrayList.get(position).getNumero_codigobarras());
        //   holder.txtresposta_servidor.setText(searchArrayList.get(position).getStatus());

        holder.txtCodigoBarras.setText(codigoslist.get(position).getNumero_codigobarras());
        holder.txtstatus.setText(codigoslist.get(position).getStatus());


        if (codigoslist.get(position).getCor().equals("VERDE")) {
            holder.txtstatus.setTextColor(Color.GREEN);

        } else if ((codigoslist.get(position).getCor().equals("RED"))) {
            holder.txtstatus.setTextColor(Color.RED);

        }


        //	Log.d("numbarras1",codigoslist.get(position).getNumero_codigobarras());


        return convertView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub

        return codigoslist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return codigoslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;

    }


}

class ListContent {
    TextView txtCodigoBarras;
    TextView txtstatus;

}
