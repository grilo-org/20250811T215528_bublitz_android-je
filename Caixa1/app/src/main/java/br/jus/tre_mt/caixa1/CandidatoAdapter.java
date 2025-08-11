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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dao.CandPart;
import dao.Candidato;


public class CandidatoAdapter extends ArrayAdapter<Candidato> {

    private Context ctx;
    private List<Candidato> candidatosListFull;

    public CandidatoAdapter(@NonNull Context context, @NonNull List<Candidato> candidatoList) {
        super(context, 0, candidatoList);

        candidatosListFull = new ArrayList<>(candidatoList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return candidatosFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.row_candidato, parent, false
            );
        }

        TextView tvNomeUrna = convertView.findViewById(R.id.lbl_urna);
        TextView tvNome = convertView.findViewById(R.id.lbl_name);
        TextView tvIdcandidato = convertView.findViewById(R.id.tvIdCandidato);

        Candidato candidatoItem = getItem(position);

        if (candidatoItem != null) {
            tvNomeUrna.setText("("+candidatoItem.getNumero()+") " + candidatoItem.getNomeUrna());
            tvNome.setText(candidatoItem.getNomeCompleto());
            tvIdcandidato.setText(candidatoItem.getId().toString());
        }


        return convertView;
    }



    private Filter candidatosFilter = new Filter() {

        @Override
        //Invoked in a worker thread to filter the data according to the constraint.
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            List<Candidato> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(candidatosListFull);

            } else {

                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Candidato item : candidatosListFull) {

                    if (item.getNomeCompleto().trim().toLowerCase().contains(filterPattern) ||
                            item.getNomeUrna().trim().toLowerCase().contains(filterPattern) ||
                            String.valueOf(item.getNumero()).trim().contains(filterPattern)) {

                        suggestions.add(item);

                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        //Invoked in the UI thread to publish the filtering results in the user interface.
        protected void publishResults(CharSequence constraint, FilterResults results) {

            clear();
            addAll((List) results.values);
            notifyDataSetChanged();

        }

        @Override
        //Converts a value from the filtered set into a CharSequence.
        public CharSequence convertResultToString(Object resultValue) {
            return ((Candidato)resultValue).getNomeUrna();
        }
    };


}
