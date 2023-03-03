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
import com.acpagro.tareopalta.modelo.Tareo;

public class AdapterListaTareos extends ArrayAdapter<Tareo> {

    private Activity contexto;
    String codigo_tareo_sgte = "null";

    public AdapterListaTareos(Activity context){
        super(context, R.layout.item_tareo, Tareo.listaTareoPantallaSQLite);
        this.contexto = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_tareo, null);

        final Tareo objA = Tareo.listaTareoPantallaSQLite.get(position);

        TextView nombre_consumidor = (TextView)item.findViewById(R.id.nombre_consumidor);
        TextView nombre_actividad = (TextView)item.findViewById(R.id.nombre_actividad);
        TextView nombre_labor = (TextView)item.findViewById(R.id.nombre_labor);
        TextView nombre_sublabor = (TextView)item.findViewById(R.id.nombre_sublabor);
        final TextView item_fecha = (TextView)item.findViewById(R.id.item_fecha);
        TextView txt_total_trabajadores = (TextView)item.findViewById(R.id.txt_total_trabajadores);
        LinearLayout ly_tareo = (LinearLayout)item.findViewById(R.id.ly_tareo);
        ly_tareo.setSelected(true);


        String grupo = "";

        if (objA.getGRUPO().equals("SI")){
            grupo = "GRUPAL";
        }else {
            grupo = "INDIVIDUAL";
        }

        nombre_consumidor.setText(objA.getNOMBRECONSUMIDOR() + " - " + grupo.toString() );
        nombre_actividad.setText(objA.getNOMBREACTIVIDAD());
        txt_total_trabajadores.setText("Personas : "+ objA.getTOTALTRABAJADORES());
        item_fecha.setText(objA.getFECHA().substring(8, 10) + "/" + objA.getFECHA().substring(5, 7) + "/" + objA.getFECHA().substring(0, 4) + " - " + objA.getFECHA().substring(11, 19));

        nombre_labor.setText(objA.getNOMBRELABOR()  + " | " + objA.getOBSERVACION());
        nombre_sublabor.setText(objA.getNOMBRESUBLABOR() + " - " + grupo.toString());

        ly_tareo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle parametros = new Bundle();

//                Intent pantallaLista = new Intent(getContext(), PantallaSeleccionSubLabor.class);

               /* parametros.putString("p_id_tareo", PantallaSeleccionSubLabor.idtareo);
                parametros.putString("p_consumidor", PantallaSeleccionSubLabor.consumidor);
                parametros.putString("p_id_actividad", PantallaSeleccionSubLabor.idactividad);
                parametros.putString("p_actividad", PantallaSeleccionSubLabor.actividad);
                parametros.putString("p_labor", PantallaSeleccionSubLabor.labor);
                parametros.putString("p_fechaYhora", PantallaSeleccionSubLabor.fechahora);
                parametros.putString("p_id_sublabor", objC.getIDSUBLABOR());
                parametros.putString("p_sublabor", objC.getSUBLABOR());
                parametros.putString("p_grupo", objC.getGRUPO());*/
                Intent pantallaLista = new Intent(getContext(), PantallaRendimientos.class);
                parametros.putString("p_id_tareo", objA.getIDTAREO());
                parametros.putString("p_consumidor", objA.getNOMBRECONSUMIDOR());
                parametros.putString("p_idconsumidor", objA.getIDCONSUMIDOR());
                parametros.putString("p_id_actividad", objA.getIDACTIVIDAD());
                parametros.putString("p_actividad", objA.getNOMBREACTIVIDAD());
                parametros.putString("p_labor", objA.getNOMBRELABOR());
                parametros.putString("p_fechaYhora", item_fecha.getText().toString());
                parametros.putString("p_id_sublabor", objA.getIDSUBLABOR());
                parametros.putString("p_sublabor", objA.getNOMBRESUBLABOR());
                parametros.putString("p_grupo", objA.getGRUPO());

                pantallaLista.putExtras(parametros);
                getContext().startActivity(pantallaLista);

            }
        });
        return item;
    }

}
