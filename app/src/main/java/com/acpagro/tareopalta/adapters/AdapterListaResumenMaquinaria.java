package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.BinDetalle;
import com.acpagro.tareopalta.modelo.LecturaMaquinaria;


public class AdapterListaResumenMaquinaria extends ArrayAdapter<LecturaMaquinaria> {
    private Activity contexto;
    public AdapterListaResumenMaquinaria(Activity context){
        super(context, R.layout.item_lista_detalle_bin, LecturaMaquinaria.listaResumen);
        this.contexto = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_lista_detalle_bin, null);

        final LecturaMaquinaria objA = LecturaMaquinaria.listaResumen.get(position);

        TextView item_consumidor = item.findViewById(R.id.item_consumidor);
        TextView item_bines = item.findViewById(R.id.item_bines);
        LinearLayout item_fila = item.findViewById(R.id.item_fila);

        item_consumidor.setText(objA.getIDPORTABIN());
        item_bines.setText(objA.getCANTIDAD());

        return item;
    }

}
