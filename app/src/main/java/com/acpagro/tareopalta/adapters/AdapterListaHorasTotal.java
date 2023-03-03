package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acpagro.tareopalta.PantallaListaExcesoHoras;
import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.HoraPersonal;

public class AdapterListaHorasTotal extends ArrayAdapter<HoraPersonal> {
    private Activity contexto;
    String codigo_tareo_sgte = "null";

    public AdapterListaHorasTotal(Activity context){
        super(context, R.layout.item_lista_trabajadores_rendimiento, HoraPersonal.listaPersonalHorasPorFecha);
        this.contexto = context;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_lista_trabajadores_rendimiento, null);
        final HoraPersonal objHP = HoraPersonal.listaPersonalHorasPorFecha.get(position);

        TextView item_cab_dni = (TextView)item.findViewById(R.id.item_cab_dni);
        TextView item_cab_nombres = (TextView)item.findViewById(R.id.item_cab_nombres);
        TextView item_cab_rend = (TextView)item.findViewById(R.id.item_cab_rend);
        TextView item_dni = (TextView)item.findViewById(R.id.item_dni);
        TextView item_nombres = (TextView)item.findViewById(R.id.item_nombres);
        TextView item_rendimiento = (TextView)item.findViewById(R.id.item_rendimiento);
        LinearLayout item_fila_persona = (LinearLayout)item.findViewById(R.id.item_fila_persona);

        if(position == 0){
            item_cab_dni.setVisibility(View.VISIBLE);
            item_cab_nombres.setVisibility(View.VISIBLE);
            item_cab_rend.setText("HRS.");
            item_cab_rend.setVisibility(View.VISIBLE);
        }

        item_dni.setText(objHP.getDNI());
        item_nombres.setText(objHP.getNOMBRE());
        item_rendimiento.setText(objHP.getHORAS());

        if(Double.parseDouble(objHP.getHORAS()) <= PantallaListaExcesoHoras.horaLimite){
            item_fila_persona.setBackgroundColor(Color.parseColor("#ffffff"));
        }else{
            item_fila_persona.setBackgroundColor(Color.parseColor("#99cf161e"));
        }

        return item;
    }

}
