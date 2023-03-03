package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acpagro.tareopalta.PantallaDetalleBinLectura;
import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.BinCabecera;
import com.acpagro.tareopalta.modelo.BinDetalle;


public class AdapterListaBinDetalle extends ArrayAdapter<BinDetalle> {
    private Activity contexto;
    public AdapterListaBinDetalle(Activity context){
        super(context, R.layout.item_lista_detalle_bin, BinDetalle.listaDetalle);
        this.contexto = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_lista_detalle_bin, null);

        final BinDetalle objA = BinDetalle.listaDetalle.get(position);

        TextView item_consumidor = item.findViewById(R.id.item_consumidor);
        TextView item_bines = item.findViewById(R.id.item_bines);
        LinearLayout item_fila = item.findViewById(R.id.item_fila);

        item_consumidor.setText(objA.getIDCONSUMIDOR());
        item_bines.setText(objA.getBINES());

        return item;
    }

}
