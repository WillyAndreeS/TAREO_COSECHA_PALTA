package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acpagro.tareopalta.PantallaListaResumenRendimiento;
import com.acpagro.tareopalta.PantallaRendimientos;
import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.DetalleTareo;

public class AdapterListaRendimientoGrupo extends ArrayAdapter<DetalleTareo> {
    private Activity contexto;
    String codigo_tareo_sgte = "null";

    public AdapterListaRendimientoGrupo(Activity context){
        super(context, R.layout.item_lista_rendimiento_grupo, DetalleTareo.listaDetalleTareoResumenSQLite);
        this.contexto = context;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_lista_rendimiento_grupo, null);

        final DetalleTareo objDA = DetalleTareo.listaDetalleTareoResumenSQLite.get(position);

        TextView item_cab_dni = (TextView)item.findViewById(R.id.item_cab_dni);
        TextView item_cab_nombres = (TextView)item.findViewById(R.id.item_cab_nombres);
        TextView item_cab_rend = (TextView)item.findViewById(R.id.item_cab_rend);
        TextView item_dni = (TextView)item.findViewById(R.id.item_dni);
        TextView item_nombres = (TextView)item.findViewById(R.id.item_nombres);
        TextView item_rendimiento = (TextView)item.findViewById(R.id.item_rendimiento);
        LinearLayout item_fila_persona = (LinearLayout)item.findViewById(R.id.item_fila_persona);
        ImageButton ibtnVerDetalle = (ImageButton)item.findViewById(R.id.ibtnVerDetalle);

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

        item_fila_persona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!PantallaRendimientos.touch_boton){
                    PantallaRendimientos.alertDialog = PantallaRendimientos.dialogAgregarRendimiento(contexto, objDA.getIDPERSONALGENERAL());
                    PantallaRendimientos.alertDialog.show();
                }

            }
        });

        ibtnVerDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle parametros = new Bundle();
                parametros.putString("p_dni", objDA.getIDPERSONALGENERAL());
                Intent pantallaRes = new Intent(contexto , PantallaListaResumenRendimiento.class);
                pantallaRes.putExtras(parametros);
                //contexto.startActivity(pantallaRes);
                contexto.startActivityForResult(pantallaRes, PantallaRendimientos.ACTUALIZAR_RENDIMIENTOS);

            }
        });

        return item;
    }

}
