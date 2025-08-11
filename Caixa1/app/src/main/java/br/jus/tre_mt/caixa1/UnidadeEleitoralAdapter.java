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
import dao.UnidadeEleitoral;

/**
 * Created by jorgebublitz on 16/08/16.
 */
public class UnidadeEleitoralAdapter extends ArrayAdapter<UnidadeEleitoral> implements Filterable {

    private Context ctx;
    int resource;
    private List<UnidadeEleitoral> unidadeEleitoral, tempItems, suggestions;

    public UnidadeEleitoralAdapter(Context ctx, int resource, List<UnidadeEleitoral> unidadeEleitoral) {

        super(ctx, resource, unidadeEleitoral);
        this.ctx = ctx;
        this.resource = resource;
        this.unidadeEleitoral = unidadeEleitoral;
        tempItems = new ArrayList<>(unidadeEleitoral); // this makes the difference.
        suggestions = new ArrayList<>();

    }

    @Override
    public int getCount() {
        return unidadeEleitoral.size();
    }

    @Override
    public UnidadeEleitoral getItem(int i) {
        return unidadeEleitoral.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(unidadeEleitoral.get(i).getId());
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
        }

        UnidadeEleitoral ue = unidadeEleitoral.get(position);

        if (ue != null) {

            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);

            if (lblName != null)
                lblName.setText(ue.getNome());
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
            String str = ((UnidadeEleitoral) resultValue).getNome();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();

                for (UnidadeEleitoral ue : tempItems) {
                    if (ue.getNome().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(ue);
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

            List<UnidadeEleitoral> filterList = (ArrayList<UnidadeEleitoral>) results.values;

            if (results != null && results.count > 0) {

                clear();

                for (UnidadeEleitoral ue : filterList) {

                    add(ue);
                    notifyDataSetChanged();
                }
            }
        }
    };

}
