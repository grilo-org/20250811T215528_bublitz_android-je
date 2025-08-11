package br.jus.tre_mt.caixa1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import dao.Estado;
import dao.TipoDeGasto;

public class TipoDeGastoAdapter extends ArrayAdapter<TipoDeGasto>  {

    private Context ctx;
    private int resource;
    private List<TipoDeGasto> listTipoDeGasto;

    public TipoDeGastoAdapter(Context ctx, int resource, List<TipoDeGasto> listTipoDeGasto) {

        super(ctx, resource, listTipoDeGasto);

        this.ctx = ctx;
        this.resource = resource;
        this.listTipoDeGasto = listTipoDeGasto;

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

        TipoDeGasto tipoDeGasto = listTipoDeGasto.get(position);

        if (tipoDeGasto != null) {

            TextView lblName = view.findViewById(R.id.lbl_name);
            TextView lblId = view.findViewById(R.id.lbl_id);


            if (lblName != null)
                lblName.setText(tipoDeGasto.getDescricao());

            if (lblId != null)
                lblId.setText(String.valueOf(tipoDeGasto.getId()));
        }

        return view;
    }


}
