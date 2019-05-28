/*
 *
 * ADAPTER USADO NO REGISTRA MOTIVO DA TENTATIVA DE ENTREGA
 */

package br.com.inventario.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.inventario.R;
import br.com.inventario.model.NotaFiscal;


public class RegistraMotivoAdapter extends BaseAdapter {
    LayoutInflater inflater;
    String chave = null;
    ArrayList<NotaFiscal> codigoslist;
    private Context context;

    public RegistraMotivoAdapter(Context context, ArrayList<NotaFiscal> codigoslist) {
        inflater = LayoutInflater.from(context);
        this.codigoslist = codigoslist;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ListContent3 holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.itensmotivos, null);

            holder = new ListContent3();
            holder.txtMotivo = (TextView) convertView.findViewById(R.id.txtmotivo);



            convertView.setTag(holder);
        } else {
            holder = (ListContent3) convertView.getTag();
        }

        //  holder.txtnumero_codigobarras.setText(searchArrayList.get(position).getNumero_codigobarras());
        //   holder.txtresposta_servidor.setText(searchArrayList.get(position).getStatus());

        holder.txtMotivo.setText(codigoslist.get(position).getEmpresa());



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

class ListContent3 {
    TextView txtMotivo;



}
