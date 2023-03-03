package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.acpagro.tareopalta.PantallaAsistenciaPersonal;
import com.acpagro.tareopalta.PantallaGruposLista;
import com.acpagro.tareopalta.PantallaListaTareos;
import com.acpagro.tareopalta.PantallaSeleccionTareoHoras;
import com.acpagro.tareopalta.PantallaTareoAsistencia;
import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.Asistencia;
import com.acpagro.tareopalta.modelo.DetalleTareo;
import com.acpagro.tareopalta.modelo.Grupo;
import com.acpagro.tareopalta.modelo.Tareo;

public class AdapterGrupo extends ArrayAdapter<Grupo> {

    private Activity contexto;


    public AdapterGrupo(Activity context){
        super(context, R.layout.item_grupo, Grupo.listaGrupos);
        this.contexto = context;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_grupo, null);

        final Grupo objA = Grupo.listaGrupos.get(position);

        TextView tv_id_grupo = (TextView)item.findViewById(R.id.tv_id_grupo);
        TextView tv_nombre_grupo = (TextView)item.findViewById(R.id.tv_nombre_grupo);
        TextView tv_fecha = (TextView)item.findViewById(R.id.tv_fecha);
        ImageButton btn_copiar_grupo = (ImageButton) item.findViewById(R.id.btn_copiar_grupo);
        ImageButton btn_eliminar_grupo = (ImageButton) item.findViewById(R.id.btn_eliminar_grupo);
        LinearLayout ly_grupo = (LinearLayout) item.findViewById(R.id.ly_grupo);
        LinearLayout ly_cabecera = (LinearLayout) item.findViewById(R.id.ly_cabecera);


        tv_id_grupo.setText(objA.getIDGRUPO());
        tv_fecha.setText(objA.getFECHAREGISTRO());

        if(objA.getNUM_GRUPO_DESC().length()==3){
            btn_copiar_grupo.setVisibility(View.VISIBLE);
            tv_nombre_grupo.setText("GRUPO "+objA.getNUM_GRUPO_DESC());
        }else{
            btn_copiar_grupo.setVisibility(View.GONE);
            tv_nombre_grupo.setText("GRUPO "+objA.getNUM_GRUPO_DESC()+" Copia");
        }

        if(PantallaGruposLista.MENU.equalsIgnoreCase("2")){//TAREO Y ASISTENCIA
            ly_cabecera.setBackgroundColor(Color.parseColor("#6C4820"));
        }else if(PantallaGruposLista.MENU.equalsIgnoreCase("3")){//RENDIMIENTOS
            btn_copiar_grupo.setVisibility(View.GONE);
            ly_cabecera.setBackgroundColor(Color.parseColor("#00AB74"));
        }else if(PantallaGruposLista.MENU.equalsIgnoreCase("4")){//ASIG. HORAS
            btn_copiar_grupo.setVisibility(View.GONE);
            ly_cabecera.setBackgroundColor(Color.parseColor("#2d2d2d"));
        }

        ly_grupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pAP = null;
                if(PantallaGruposLista.MENU.equalsIgnoreCase("2")){//TAREO Y ASISTENCIA
                    pAP = new Intent(getContext(), PantallaTareoAsistencia.class);
                }else if(PantallaGruposLista.MENU.equalsIgnoreCase("3")){//RENDIMIENTOS
                    pAP = new Intent(getContext(), PantallaListaTareos.class);
                }else if(PantallaGruposLista.MENU.equalsIgnoreCase("4")){//ASIG. HORAS
                    pAP = new Intent(getContext(), PantallaSeleccionTareoHoras.class);
                }

                Bundle parametros = new Bundle();

                parametros.putString("p_idgrupo", objA.getIDGRUPO());
                parametros.putString("p_desc", objA.getNUM_GRUPO_DESC());
                pAP.putExtras(parametros);
                getContext().startActivity(pAP);
            }
        });

        btn_copiar_grupo.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                builder.setTitle("¿COPIAR?");
                builder.setMessage("Se copiará el grupo seleccionado, sus tareos y asistencias.");
                builder.setIcon(R.drawable.ic_copiar_negro);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new TareaCopiarGrupoCompleto().execute(objA.getIDGRUPO());
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        btn_eliminar_grupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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

    private class TareaCopiarGrupoCompleto extends AsyncTask<String, Void, Long> {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Copiando...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
        }

        @Override
        protected Long doInBackground(String... params) {
            String IDGRUPO = params[0];
            Grupo objA = new Grupo();
            return objA.copiarGrupoSQLite(IDGRUPO);
        }

        protected void onPostExecute(Long result) {
            pDialog.dismiss();
            Grupo obj = new Grupo();
            obj = new Grupo();
            obj.cargarListaGrupoSQLite();
            notifyDataSetChanged();
        }
    }

}

