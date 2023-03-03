package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.acpagro.tareopalta.PantallaAsistenciaPersonal;
import com.acpagro.tareopalta.PantallaTareoAsistencia;
import com.acpagro.tareopalta.PantallaTareosActualizar;
import com.acpagro.tareopalta.PretareoPersonal;
import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.Asistencia;
import com.acpagro.tareopalta.modelo.DetalleTareo;
import com.acpagro.tareopalta.modelo.Tareo;

public class AdapterPretareo extends ArrayAdapter<Tareo> {

    private Activity contexto;
    String codigo_tareo_sgte = "null";


    public AdapterPretareo(Activity context){
        super(context, R.layout.item_pretareo, Tareo.listaTareoPantallaSQLiteCompleta);
        this.contexto = context;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_pretareo, null);

        final Tareo objA = Tareo.listaTareoPantallaSQLiteCompleta.get(position);

        TextView nombre_consumidor = (TextView)item.findViewById(R.id.nombre_consumidor);
        TextView nombre_actividad = (TextView)item.findViewById(R.id.nombre_actividad);
        TextView nombre_labor = (TextView)item.findViewById(R.id.nombre_labor);
        TextView nombre_sublabor = (TextView)item.findViewById(R.id.nombre_sublabor);
        final TextView item_fecha = (TextView)item.findViewById(R.id.item_fecha);
        TextView txt_total_trabajadores = (TextView)item.findViewById(R.id.txt_total_trabajadores);
        final LinearLayout ly_tareo = (LinearLayout)item.findViewById(R.id.ly_tareo);
        final LinearLayout ly_cabecera = (LinearLayout)item.findViewById(R.id.ly_cabecera);
        RelativeLayout rl_editar = (RelativeLayout)item.findViewById(R.id.rl_editar);
        ImageButton btn_eliminar_tareo = (ImageButton) item.findViewById(R.id.btn_eliminar_tareo);
        ImageButton btn_editar_tareo = (ImageButton) item.findViewById(R.id.btn_editar_tareo);


        /*if(objA.getSINCRONIZADO().equalsIgnoreCase("0")){
            rl_editar.setVisibility(View.VISIBLE);
        }else{
            rl_editar.setVisibility(View.GONE);
        }*/

        String grupo = "";

        if (objA.getGRUPO().equals("SI")){
            grupo = "GRUPAL";
        }else {
            grupo = "INDIVIDUAL";
        }


       /* if(objA.getSINCRONIZADO().equalsIgnoreCase("0")){
            btn_editar_tareo.setVisibility(View.VISIBLE);
        }else{
            btn_editar_tareo.setVisibility(View.GONE);
        }*/

        ly_cabecera.setBackgroundColor(Color.parseColor("#6C4820"));
        nombre_consumidor.setText(objA.getNOMBRECONSUMIDOR() + " - " + grupo.toString() );
        nombre_actividad.setText(objA.getNOMBREACTIVIDAD());
        nombre_labor.setText(objA.getNOMBRELABOR() + " | " + objA.getOBSERVACION());


        nombre_sublabor.setText(objA.getNOMBRESUBLABOR() + " - " + grupo.toString());
        txt_total_trabajadores.setText("Personas : "+ objA.getTOTALTRABAJADORES());
        item_fecha.setText(objA.getFECHA().substring(8, 10) + "/" + objA.getFECHA().substring(5, 7) + "/" + objA.getFECHA().substring(0, 4) + " - " + objA.getFECHA().substring(11, 19));

        ly_tareo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Bundle parametros = new Bundle();
                Intent pAP = new Intent(getContext(), PretareoPersonal.class);
                parametros.putString("p_id_tareo", objA.getIDTAREO());
                parametros.putString("p_consumidor", objA.getNOMBRECONSUMIDOR());
                parametros.putString("p_actividad", objA.getNOMBREACTIVIDAD());
                parametros.putString("p_idactividad", objA.getIDACTIVIDAD());
                parametros.putString("p_idconsumidor", objA.getIDCONSUMIDOR());
                parametros.putString("p_idlabor", objA.getIDLABOR());
                parametros.putString("p_labor", objA.getNOMBRELABOR());
                parametros.putString("p_fechaYhora", item_fecha.getText().toString());
                pAP.putExtras(parametros);
                getContext().startActivity(pAP);

            }
        });

//        btn_eliminar_tareo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view){
//                AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
//                builder.setTitle("¿Eliminar?");
//                builder.setMessage("Se eliminará el tareo con | "  + objA.getTOTALTRABAJADORES() + " personas registradas.");
//                builder.setIcon(R.drawable.ic_eliminar_negro);
//                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        eliminarTareo(objA.getIDTAREO());
//                    }
//                });
//                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();
//
//            }
//        });
//
//        btn_editar_tareo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
//                builder.setTitle("¿EDITAR?");
//                builder.setIcon(R.drawable.ic_consumidor_negro);
//                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Bundle parametros = new Bundle();
//                        Intent pAP = new Intent(getContext(), PantallaTareosActualizar.class);
//                        parametros.putString("p_id_tareo", objA.getIDTAREO());
//                        parametros.putString("p_idconsumidor", objA.getIDCONSUMIDOR());
//                        parametros.putString("p_consumidor", objA.getNOMBRECONSUMIDOR());
//                        parametros.putString("p_actividad", objA.getNOMBREACTIVIDAD());
//                        parametros.putString("p_idactividad", objA.getIDACTIVIDAD());
//                        parametros.putString("p_idlabor", objA.getIDLABOR());
//                        parametros.putString("p_labor", objA.getNOMBRELABOR());
//                        parametros.putString("p_idsublabor", objA.getIDSUBLABOR());
//                        parametros.putString("p_sublabor", objA.getNOMBRESUBLABOR());
//                        parametros.putString("p_tipolabor", objA.getTIPOLABOR());
//                        parametros.putString("p_horas", objA.getHORAS());
//                        parametros.putString("p_idturno", objA.getIDTURNO());
//                        pAP.putExtras(parametros);
//                        getContext().startActivity(pAP);
//                    }
//                });
//                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });

        return item;
    }

    public void eliminarTareo(String idTareo){
        Tareo tareo = new Tareo();
        Long respuesta_tareo = tareo.eliminarTareo(idTareo);

        Asistencia asistencia = new Asistencia();
        Long respuesta_asistencia = asistencia.eliminarTareoAsistencia(idTareo);

        DetalleTareo detalleTareo = new DetalleTareo();
        Long respuesta_detalletareo = detalleTareo.eliminarTareoDetalle(idTareo);

        cargarLista();
    }

    public void cargarLista(){
        Tareo objA = new Tareo();
        objA.cargarListaTareoPantallaListaSQLite();
        notifyDataSetChanged();
    }

}

