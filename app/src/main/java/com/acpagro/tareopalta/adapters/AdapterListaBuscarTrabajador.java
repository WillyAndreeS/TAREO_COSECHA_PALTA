package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.Asistencia;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class AdapterListaBuscarTrabajador extends ArrayAdapter<Asistencia> implements Filterable {
    private Activity contexto;
    TextView item_dni, item_nombres;
    ImageButton ibtnSelect;
    LinearLayout ly_color;

    public ArrayList<Asistencia> orig;
    public ArrayList<Asistencia> listaAsistencia;

    public AdapterListaBuscarTrabajador(Activity context){
        super(context, R.layout.item_lista_trabajadores_asistencia_seleccionar, Asistencia.listaAsistenciaSQLite);
        this.contexto = context;
        listaAsistencia = Asistencia.listaAsistenciaSQLite;
    }

    //para buscar implementación==========================

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Asistencia> results = new ArrayList<Asistencia>();
                if (orig == null)
                    orig = listaAsistencia;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final Asistencia g : orig) {
                            if(g.getIDPERSONALGENERAL().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                    g.getNOMBRES().toLowerCase().contains(constraint.toString().toLowerCase())){
                                results.add(g);
                            }
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                listaAsistencia = (ArrayList<Asistencia>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return listaAsistencia.size();
    }

    @Override
    public Asistencia getItem(int position) {
        return listaAsistencia.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //====================================================

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_lista_trabajadores_asistencia_seleccionar, null);

        final Asistencia objA = listaAsistencia.get(position);

        item_dni = (TextView)item.findViewById(R.id.item_dni);
        ly_color = (LinearLayout)item.findViewById(R.id.ly_color);
        item_nombres = (TextView)item.findViewById(R.id.item_nombres);

        if(position % 2 == 0){
            ly_color.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        if(objA.getESTADOSALIDA() != null && objA.getESTADOSALIDA().length()!= 0){
            item_dni.setTextColor(Color.parseColor("#E50000"));
            item_nombres.setTextColor(Color.parseColor("#E50000"));
        }

        item_dni.setText(objA.getIDPERSONALGENERAL());
        item_nombres.setText(objA.getNOMBRES());

        ly_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = contexto.getIntent();
                intent.putExtra("dni", objA.getIDPERSONALGENERAL());
                contexto.setResult(RESULT_OK, intent);
                contexto.finish();
            }
        });

        return item;
    }
}