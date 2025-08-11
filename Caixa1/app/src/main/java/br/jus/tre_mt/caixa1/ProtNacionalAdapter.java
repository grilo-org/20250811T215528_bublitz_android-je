package br.jus.tre_mt.caixa1;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import dao.DBAdapter;
import dao.GastoComp;
import dao.UnidadeEleitoral;

/**
 * Created by jorgebublitz on 28/08/16.
 */
public class ProtNacionalAdapter extends BaseAdapter {

    private Context ctx;
    private List<GastoComp> protocolos;
    static DBAdapter db;
    UnidadeEleitoral ue;

    public ProtNacionalAdapter(Context ctx, List<GastoComp> protocolos) {
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

        View v = LayoutInflater.from(ctx).inflate(R.layout.row_protocolo_nacional, null);
        TextView txtProtocolo = (TextView) v.findViewById(R.id.txtProtocolo);
        txtProtocolo.setText(protocolos.get(position).getProtocolo());
        TextView txtDH = (TextView) v.findViewById(R.id.txtDH);
        txtDH.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(protocolos.get(position).getDh_envio()));
        TextView txtCand = (TextView) v.findViewById(R.id.txtCand);


        //Seleciona a unidade eleitoral (municipío)
        db = new DBAdapter(ctx);
        db.open();
        ue = db.getUnidadeEleitoral(protocolos.get(position).getIdUe());
        db.close();

        txtCand.setText(ue.getNome() + "\n" +
                protocolos.get(position).getCandidato());
        TextView txtStatus = (TextView) v.findViewById(R.id.txtStatus);
        txtStatus.setText("Situação: Em análise");

        LinearLayout linearLayout = v.findViewById(R.id.llLinkProcesso);

        if (!protocolos.get(position).getProcesso().equals("null")) {

            TextView linkProcesso = v.findViewById(R.id.tvLinkProcesso);
            linkProcesso.setClickable(true);
            linkProcesso.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "<a href='"+protocolos.get(position).getProcesso()+"'>Registro de candidatura e prestação de contas</a>";
            linkProcesso.setText(Html.fromHtml(text));

            linearLayout.setVisibility(View.VISIBLE);

        }else{
            linearLayout.setVisibility(View.GONE);
        }




        return v;

    }
}
