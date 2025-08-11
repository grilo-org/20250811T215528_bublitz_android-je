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

import dao.CandPart;

/**
 * Created by jorgebublitz on 16/08/16.
 */
public class CandAdapter extends ArrayAdapter<CandPart> implements Filterable {
    private Context ctx;
    int resource, textViewResourceId;
    private List<CandPart> cands, tempItems, suggestions;

    public CandAdapter(Context ctx, int resource, int textViewResourceId, List<CandPart> cands) {
        super(ctx, resource, cands);
        this.ctx = ctx;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.cands = cands;
        tempItems = new ArrayList<CandPart>(cands); // this makes the difference.
        suggestions = new ArrayList<CandPart>();
    }

    @Override
    public int getCount() {
        return cands.size();
    }

    @Override
    public CandPart getItem(int i) {
        return cands.get(i);
    }

    @Override
    public long getItemId(int i) {
        return cands.get(i).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_munic, parent, false);
        }
        CandPart people = cands.get(position);
        if (people != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
            if (lblName != null)
                lblName.setText("(" + people.getNumero() + ") " + people.getNomeUrna());
            TextView lblUrna = (TextView) view.findViewById(R.id.lbl_urna);
            if (lblUrna != null) {
                lblUrna.setVisibility(View.VISIBLE);
                lblUrna.setText(people.getNome());
            }
        }
        return view;
    }


    @Override
    public Filter getFilter() {
        return nameFilter;
    }


    Filter nameFilter = new Filter() {


        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((CandPart) resultValue).getNomeUrna();
            return str;
        }


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (CandPart people : tempItems) {
                    if (Integer.toString(people.getNumero()).contains(constraint.toString())) {
                        suggestions.add(people);
                    }
                    if (people.getNome().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                    if (people.getNomeUrna().toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            List<CandPart> filterList = (ArrayList<CandPart>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (CandPart people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };


}
