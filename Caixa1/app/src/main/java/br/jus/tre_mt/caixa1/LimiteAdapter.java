package br.jus.tre_mt.caixa1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import dao.Municipio;

/**
 * Created by jorgebublitz on 26/08/16.
 */
public class LimiteAdapter extends ArrayAdapter<Municipio> implements Filterable {
    private Context ctx;
    int resource, textViewResourceId;
    private List<Municipio> municipios, tempItems, suggestions;

    public LimiteAdapter(Context ctx, int resource, List<Municipio> municipios) {
        super(ctx, resource, municipios);
        this.ctx = ctx;
        this.resource = resource;
        this.municipios = municipios;
        tempItems = new ArrayList<Municipio>(municipios); // this makes the difference.
        suggestions = new ArrayList<Municipio>();
    }

    @Override
    public int getCount() {
        return municipios.size();
    }

    @Override
    public Municipio getItem(int i) {
        return municipios.get(i);
    }

    @Override
    public long getItemId(int i) {
        return municipios.get(i).getId();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
        }
        final Municipio people = municipios.get(position);
        if (people != null) {
            LinearLayout llMain = (LinearLayout) view.findViewById(R.id.llMain);
            //if (position % 2 == 0) {
            //    llMain.setBackgroundResource(R.drawable.green_button);
            //} else {
            //    llMain.setBackgroundResource(R.drawable.btn_blue);
            //}
            ImageButton btnCand = (ImageButton) view.findViewById(R.id.btnCand);
            btnCand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ctx, ActConsulta.class);
                    Bundle b = new Bundle();
                    b.putLong("id", people.getId());
                    i.putExtras(b);
                    ctx.startActivity(i);
                }
            });
            TextView txtMunic = (TextView) view.findViewById(R.id.txtMunic);
            if (txtMunic != null)
                txtMunic.setText(people.getNome());
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            String moneyString = formatter.format(people.getLim1Prefeito());
            TextView txtPre1 = (TextView) view.findViewById(R.id.txtPre1);
            if (txtPre1 != null)
                txtPre1.setText(moneyString);
            LinearLayout linear = (LinearLayout) view.findViewById(R.id.ll2Turno);
            if (people.getLim2Prefeito() > 0) {
                linear.setVisibility(View.VISIBLE);
                moneyString = formatter.format(people.getLim2Prefeito());
                TextView txtPre2 = (TextView) view.findViewById(R.id.txtPre2);
                if (txtPre2 != null)
                    txtPre2.setText(moneyString);
            } else {
                linear.setVisibility(View.GONE);
            }
            TextView txtPre3 = (TextView) view.findViewById(R.id.txtPre3);
            if (txtPre3 != null)
                txtPre3.setText(people.getQtCaboPrefeito()+"");
            moneyString = formatter.format(people.getLim1Vereador());
            TextView txtVer1 = (TextView) view.findViewById(R.id.txtVer1);
            if (txtVer1 != null)
                txtVer1.setText(moneyString);
            TextView txtVer2 = (TextView) view.findViewById(R.id.txtVer2);
            if (txtVer2 != null)
                txtVer2.setText(people.getQtCaboVereador()+"");
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Municipio) resultValue).getNome();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Municipio people : tempItems) {
                    if (people.getNome().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Municipio> filterList = (ArrayList<Municipio>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Municipio people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
