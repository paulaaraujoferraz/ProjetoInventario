/*
 * 
 * ADAPTER USADO NO REGISTRANOTA
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


public class RegistraNotaEntregueAdapter extends BaseAdapter {
    LayoutInflater inflater;
    String chave = null;
    ArrayList<NotaFiscal> codigoslist;
    private Context context;

    public RegistraNotaEntregueAdapter(Context context, ArrayList<NotaFiscal> codigoslist) {
        inflater = LayoutInflater.from(context);
        this.codigoslist = codigoslist;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ListContent2 holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.itensregistrarentrega, null);

            holder = new ListContent2();
            holder.txtEmpresa = (TextView) convertView.findViewById(R.id.txtempresa);
            holder.txtNumNota = (TextView) convertView.findViewById(R.id.txtnumnota);
             holder.txtendereco = (TextView) convertView.findViewById(R.id.txtendereco);
             holder.txtnome = (TextView) convertView.findViewById(R.id.txtnome);
            holder.txtprioridade = (TextView) convertView.findViewById(R.id.txtprioridade);
            holder.txtvolumes = (TextView) convertView.findViewById(R.id.txtvolumes);
            holder.Datahoraleitura = (TextView) convertView.findViewById(R.id.txtDatahoraleitura);



            convertView.setTag(holder);
        } else {
            holder = (ListContent2) convertView.getTag();
        }

        //  holder.txtnumero_codigobarras.setText(searchArrayList.get(position).getNumero_codigobarras());
        //   holder.txtresposta_servidor.setText(searchArrayList.get(position).getStatus());

        holder.txtEmpresa.setText(codigoslist.get(position).getEmpresa());
        holder.txtNumNota.setText(codigoslist.get(position).getNumero_nota());
        holder.txtendereco.setText(codigoslist.get(position).getNumero_endereco());
        holder.txtnome.setText(codigoslist.get(position).getnome());
        holder.txtprioridade.setText(codigoslist.get(position).getprioridade());
        holder.txtvolumes.setText(codigoslist.get(position).getvolumes());
        holder.Datahoraleitura.setText(codigoslist.get(position).getDatahoraleitura());


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

class ListContent2 {
    TextView txtEmpresa;
    TextView txtendereco;
    TextView txtNumNota;
    TextView txtnome;
    TextView txtprioridade;
    TextView txtvolumes;
    TextView Datahoraleitura;


}
