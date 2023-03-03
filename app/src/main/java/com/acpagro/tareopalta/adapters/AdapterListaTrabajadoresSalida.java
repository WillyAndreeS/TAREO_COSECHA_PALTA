package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.SalidaPersonal;

public class AdapterListaTrabajadoresSalida extends ArrayAdapter<SalidaPersonal> {
    private Activity contexto;
    String codigo_asistencia_sgte = "null";

    public AdapterListaTrabajadoresSalida(Activity context){
        super(context, R.layout.item_lista_trabajadores_salida, SalidaPersonal.listaPersonalConSalidaHoy);
        this.contexto = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_lista_trabajadores_salida, null);

        final SalidaPersonal objR = SalidaPersonal.listaPersonalConSalidaHoy.get(position);

        TextView item_dni = (TextView)item.findViewById(R.id.item_dni);
        TextView item_nombres = (TextView)item.findViewById(R.id.item_nombre);
        TextView item_hora = (TextView)item.findViewById(R.id.item_hora);


        String nombres = "";
        if(objR.getNombre()== null || objR.getNombre().equalsIgnoreCase("null")){
            nombres = "TRABAJADOR NUEVO";
        }else{
            nombres = objR.getNombre();
        }
        item_dni.setText(objR.getDni());
        item_nombres.setText(nombres);
        item_hora.setText(objR.getHora());

        return item;
    }

}
