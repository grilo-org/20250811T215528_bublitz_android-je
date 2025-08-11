package br.jus.tre_mt.caixa1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import dao.GastoComp;

/**
 * Created by jorgebublitz on 28/08/16.
 */
public class ProtAdapter extends BaseAdapter {

    private Context ctx;
    private List<GastoComp> protocolos;

    public ProtAdapter(Context ctx, List<GastoComp> protocolos) {
        this.ctx = ctx;
        this.protocolos = protocolos;
    }

    @Override
    public int getCount() {
        return protocolos.size();
    }

    @Override
    public Object getItem(int position) {
       return protocolos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return protocolos.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = LayoutInflater.from(ctx).inflate(R.layout.row_protocolo, null);
        TextView txtProtocolo = (TextView) v.findViewById(R.id.txtProtocolo);
        txtProtocolo.setText(protocolos.get(position).getProtocolo());
        TextView txtDH = (TextView) v.findViewById(R.id.txtDH);
        txtDH.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(protocolos.get(position).getDh_envio()));
        TextView txtCand = (TextView) v.findViewById(R.id.txtCand);
        txtCand.setText(protocolos.get(position).getMunicipio() + "\n" +
                protocolos.get(position).getCandidato());
        TextView txtStatus = (TextView) v.findViewById(R.id.txtStatus);
        txtStatus.setText("Situação: Em análise");
        return v;

    }
}
