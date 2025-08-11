package br.jus.tre_mt.caixa1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dao.Estado;
import dao.Municipio;

public class EstadoAdapter extends ArrayAdapter<Estado>  {

    private Context ctx;
    private int resource;
    private List<Estado> estados;

    public EstadoAdapter(Context ctx, int resource, List<Estado> estados) {

        super(ctx, resource, estados);

        this.ctx = ctx;
        this.resource = resource;
        this.estados = estados;

    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }



    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);

        }

        Estado estado = estados.get(position);

        if (estado != null) {

            TextView lblName = view.findViewById(R.id.lbl_name);
            TextView lblUf = view.findViewById(R.id.lbl_uf);

            if (lblName != null)
                lblName.setText(estado.getDescricao());

            if (lblUf != null)
                lblUf.setText(estado.getUf());

        }

        return view;
    }


}
