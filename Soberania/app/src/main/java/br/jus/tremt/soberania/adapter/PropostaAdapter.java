package br.jus.tremt.soberania.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.jus.tremt.soberania.R;
import br.jus.tremt.soberania.modelo.Proposta;
import br.jus.tremt.soberania.utils.Validacao;

/**
 * Created by jorgebublitz on 27/12/2017.
 */

public class PropostaAdapter extends BaseAdapter {

    private Context ctx;
    private List<Proposta> lista = null;
    private ArrayList<Proposta> arraylist;

    public PropostaAdapter(Context ctx, List<Proposta> lista) {
        this.ctx = ctx;
        this.lista = lista;
        this.arraylist = new ArrayList<Proposta>();
        this.arraylist.addAll(lista);
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int i) {
        return lista.get(i);
    }

    @Override
    public long getItemId(int i) {
        return lista.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Proposta p = lista.get(i);

        String nomeClasse = ctx.getClass().getSimpleName();

        View v;

        ImageView imgIco;
        TextView txtStatus;

        String txt = "";

        if (p.getAbrangencia() == 1) {
            txt = "N";
        } else if (p.getAbrangencia() == 2) {
            txt = "E";
        } else {
            txt = "M";
        }


        if (!nomeClasse.equals("actAcompanharProjetos")) {

            v = LayoutInflater.from(ctx).inflate(R.layout.lst_proposta, null);

            imgIco = v.findViewById(R.id.imgIco);

            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(txt, Color.GRAY);

            imgIco.setImageDrawable(drawable);

        } else {

            v = LayoutInflater.from(ctx).inflate(R.layout.lst_acompanhar_projetos, null);

            txtStatus = v.findViewById(R.id.tvIdStatus);

            Long totalAprovado = p.getTotalAprovado();
            Long totalRejeitado = p.getTotalRejeitado();

            String status;
            String corFonte;

            if (totalAprovado > totalRejeitado) {

                status = "Aprovado";
                corFonte = "#53933f";

            } else if (totalAprovado < totalRejeitado) {

                status = "Rejeitado";
                corFonte = "#ef4444";

            } else {

                status = "Empate";
                corFonte = "#FFFFFF";
            }

            txtStatus.setText(status);
            txtStatus.setTextColor(Color.parseColor(corFonte));
            txtStatus.setTypeface(Typeface.DEFAULT, 1);
        }


        TextView txtNome = v.findViewById(R.id.txtNome);
        TextView txtData = v.findViewById(R.id.txtData);
        TextView txtDescricao = v.findViewById(R.id.txtDescricao);

        txtNome.setText(p.getNome());
        txtData.setText(Validacao.formatarData(p.getDataFim()));
        txtDescricao.setText(p.getDescricao());

        if (p.getLido().equals("N")) {

            txtData.setTypeface(Typeface.DEFAULT, 1);
            txtNome.setTypeface(Typeface.DEFAULT, 1);

        } else {
            txtData.setTypeface(Typeface.DEFAULT, 2);
            txtNome.setTypeface(Typeface.DEFAULT, 2);
        }

        return v;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        lista.clear();
        if (charText.length() == 0) {
            lista.addAll(arraylist);
        } else {
            for (Proposta wp : arraylist) {
                if (wp.getDescricao().toLowerCase(Locale.getDefault()).contains(charText)) {
                    lista.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
