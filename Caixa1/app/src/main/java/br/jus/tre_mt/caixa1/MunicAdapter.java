package br.jus.tre_mt.caixa1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dao.Municipio;

/**
 * Created by jorgebublitz on 16/08/16.
 */
public class MunicAdapter extends ArrayAdapter<Municipio> implements Filterable {

    private Context ctx;
    int resource, textViewResourceId;
    private List<Municipio> municipios, tempItems, suggestions;

    public MunicAdapter(Context ctx, int resource, int textViewResourceId, List<Municipio> municipios) {
        super(ctx, resource, municipios);
        this.ctx = ctx;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
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
            view = inflater.inflate(R.layout.row_munic, parent, false);
        }
        Municipio people = municipios.get(position);
        if (people != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
            if (lblName != null)
                lblName.setText(people.getNome());
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
