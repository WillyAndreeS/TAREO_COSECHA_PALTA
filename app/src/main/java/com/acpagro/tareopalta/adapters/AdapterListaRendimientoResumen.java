package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.DetalleTareo;

public class AdapterListaRendimientoResumen extends ArrayAdapter<DetalleTareo> {
    private Activity contexto;
    String codigo_tareo_sgte = "null";

    public AdapterListaRendimientoResumen(Activity context){
        super(context, R.layout.item_lista_trabajadores_rendimiento, DetalleTareo.listaDetalleTareoResumenSQLite);
        this.contexto = context;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_lista_trabajadores_rendimiento, null);

        final DetalleTareo objDA = DetalleTareo.listaDetalleTareoResumenSQLite.get(position);

        TextView item_cab_dni = (TextView)item.findViewById(R.id.item_cab_dni);
        TextView item_cab_nombres = (TextView)item.findViewById(R.id.item_cab_nombres);
        TextView item_cab_rend = (TextView)item.findViewById(R.id.item_cab_rend);
        TextView item_dni = (TextView)item.findViewById(R.id.item_dni);
        TextView item_nombres = (TextView)item.findViewById(R.id.item_nombres);
        TextView item_rendimiento = (TextView)item.findViewById(R.id.item_rendimiento);

        if(position == 0){
            item_cab_dni.setVisibility(View.VISIBLE);
            item_cab_nombres.setVisibility(View.VISIBLE);
            item_cab_rend.setVisibility(View.VISIBLE);
        }

        String nombres = "";
        if(objDA.getNOMBRES()== null || objDA.getNOMBRES().equalsIgnoreCase("null")){
            nombres = "TRABAJADOR NUEVO";
        }else{
            nombres = objDA.getNOMBRES();
        }
        item_dni.setText(objDA.getIDPERSONALGENERAL());
        item_nombres.setText(nombres);
        item_rendimiento.setText(""+objDA.getRENDIMIENTO());

        return item;
    }

}
