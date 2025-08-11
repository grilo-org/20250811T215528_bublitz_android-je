package br.jus.tre_mt.caixa1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import utils.Utilitario;

import java.text.NumberFormat;
import java.util.List;

import dao.Limite;

/**
 * Created by jorgebublitz on 26/08/16.
 */
public class LimiteAdapterNacional extends ArrayAdapter<Limite> implements Filterable {

    private Context ctx;
    int resource;
    private List<Limite> limite;

    public LimiteAdapterNacional(Context ctx, int resource, List<Limite> limite) {

        super(ctx, resource, limite);
        this.ctx = ctx;
        this.resource = resource;
        this.limite = limite;

    }

    @Override
    public int getCount() {
        return limite.size();
    }

    @Override
    public Limite getItem(int i) {
        return limite.get(i);
    }

    /*@Override
    public long getItemId(int i) {
        return nacional.get(i).getId();
    }*/


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
        }


        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        TextView tvCargo = view.findViewById(R.id.idLabelCargo);

        String valorLimiteGasto = formatter.format(limite.get(position).getValorMaximo());
        TextView tvLimiteGasto = view.findViewById(R.id.idLimiteGasto);

        LinearLayout llLimiteGastoSegTurno = view.findViewById(R.id.llLimiteGastoSegTurno);
        String valorLimiteGastoSegTurno;
        TextView tvLimiteGastoSegTurno = view.findViewById(R.id.idLimiteGastoSegTurno);

        TextView tvQtdCabo = view.findViewById(R.id.idQtdCaboEleitorais);


        switch (limite.get(position).getIdCargo().toString()) {

            case "1":
                tvCargo.setText("Presidente");
                break;
            case "3":
                tvCargo.setText("Governador");
                break;
            case "5":
                tvCargo.setText("Senador");
                break;
            case "6":
                tvCargo.setText("Deputado Federal");
                break;
            case "7":
                tvCargo.setText("Deputado Estadual");
                break;
            default:
                tvCargo.setText("");
        }

        //Limite primeiro turno
        if (!Utilitario.isNullOrEmpty(valorLimiteGasto.trim())) {
            tvLimiteGasto.setText(valorLimiteGasto);
        }

        //Limite segundo turno caso tenha
        if (limite.get(position).getValorMaximoSegTurno() != null) {

            valorLimiteGastoSegTurno = formatter.format(limite.get(position).getValorMaximoSegTurno());
            tvLimiteGastoSegTurno.setText(valorLimiteGastoSegTurno);
            llLimiteGastoSegTurno.setVisibility(View.VISIBLE);

        } else {
            llLimiteGastoSegTurno.setVisibility(View.GONE);
        }

        if (!Utilitario.isNullOrEmpty(limite.get(position).getCaboMaximo().toString())) {

            tvQtdCabo.setText(limite.get(position).getCaboMaximo().toString());
        }


        return view;
    }
}
