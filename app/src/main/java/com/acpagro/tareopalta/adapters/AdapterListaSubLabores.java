package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acpagro.tareopalta.PantallaRendimientos;
import com.acpagro.tareopalta.PantallaSeleccionSubLabor;
import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.SubLabor;

public class AdapterListaSubLabores extends ArrayAdapter<SubLabor> {
    private Activity contexto;

    public AdapterListaSubLabores(Activity context){
        super(context, R.layout.item_dialog_row, SubLabor.listaSubLaborSQLite);
        this.contexto = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_dialog_row, null);

        final SubLabor objC = SubLabor.listaSubLaborSQLite.get(position);

        String grupal_individual = "";
        if(objC.getGRUPO().equals("SI")){
            grupal_individual=" [GRUPAL]";
        }else{
            grupal_individual=" [INDIVIDUAL]";
        }
        TextView txt_consumidor = (TextView)item.findViewById(R.id.txt_consumidor);
        TextView txt_grupal_individual = (TextView)item.findViewById(R.id.txt_grupal_individual);
        TextView txt_unidad = (TextView)item.findViewById(R.id.txt_unidad);
        LinearLayout item_consumidor = (LinearLayout)item.findViewById(R.id.item_consumidor);
        LinearLayout item_fondo = (LinearLayout)item.findViewById(R.id.item_fondo);
        //txt_consumidor.setText(objC.getSUBLABOR()+" - "+objC.getUNIDAD() + grupal_individual);
        txt_consumidor.setText(objC.getSUBLABOR());
        txt_grupal_individual.setText(grupal_individual);
        txt_unidad.setText(objC.getUNIDAD());

        item_fondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle parametros = new Bundle();

                Intent pantallaLista = new Intent(getContext(), PantallaRendimientos.class);

                parametros.putString("p_id_tareo", PantallaSeleccionSubLabor.idtareo);
                parametros.putString("p_consumidor", PantallaSeleccionSubLabor.consumidor);
                parametros.putString("p_id_actividad", PantallaSeleccionSubLabor.idactividad);
                parametros.putString("p_actividad", PantallaSeleccionSubLabor.actividad);
                parametros.putString("p_labor", PantallaSeleccionSubLabor.labor);
                parametros.putString("p_fechaYhora", PantallaSeleccionSubLabor.fechahora);
                parametros.putString("p_id_sublabor", objC.getIDSUBLABOR());
                parametros.putString("p_sublabor", objC.getSUBLABOR());
                parametros.putString("p_grupo", objC.getGRUPO());
                pantallaLista.putExtras(parametros);
                getContext().startActivity(pantallaLista);
            }
        });

        return item;
    }

}