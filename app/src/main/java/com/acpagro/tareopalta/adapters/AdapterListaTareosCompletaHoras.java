package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.acpagro.tareopalta.PantallaListaHorasPersonal;
import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.DetalleTareo;
import com.acpagro.tareopalta.modelo.Tareo;

import java.util.Arrays;
import java.util.List;

public class AdapterListaTareosCompletaHoras extends ArrayAdapter<Tareo> {
    private Activity contexto;
    String codigo_tareo_sgte = "null";

    //VARIABLES DIALOG TIME
    CharSequence[] listaTiempoDialog;
    CharSequence[] listaTiempoDialogString;
    int selectedT = 0;
    int buffKeyT = 0;
    int hora;
    int minutos;

    private String m_Text = "";
    AlertDialog alertDialog;

    public AdapterListaTareosCompletaHoras(Activity context){
        super(context, R.layout.item_tareo_horas, Tareo.listaTareoPantallaSQLiteCompleta);
        this.contexto = context;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_tareo_horas, null);

        final Tareo objA = Tareo.listaTareoPantallaSQLiteCompleta.get(position);

        TextView nombre_consumidor = (TextView)item.findViewById(R.id.nombre_consumidor);
        TextView nombre_actividad = (TextView)item.findViewById(R.id.nombre_actividad);
        TextView nombre_labor = (TextView)item.findViewById(R.id.nombre_labor);
        TextView item_fecha = (TextView)item.findViewById(R.id.item_fecha);
        TextView txt_total_trabajadores = (TextView)item.findViewById(R.id.txt_total_trabajadores);
        TextView txt_horas = (TextView)item.findViewById(R.id.txt_horas);
        final LinearLayout ly_tareo = (LinearLayout)item.findViewById(R.id.ly_tareo);
        final LinearLayout ly_cabecera = (LinearLayout)item.findViewById(R.id.ly_cabecera);
        Button btn_todos = (Button)item.findViewById(R.id.btn_todos);
        Button btn_individual = (Button)item.findViewById(R.id.btn_individual);

        ly_cabecera.setBackgroundColor(Color.parseColor("#2d2d2d"));
        nombre_consumidor.setText(objA.getNOMBRECONSUMIDOR());
        nombre_actividad.setText(objA.getNOMBREACTIVIDAD());
        nombre_labor.setText(objA.getNOMBRELABOR()  + " | " + objA.getOBSERVACION());
        txt_total_trabajadores.setText("Personas : "+ objA.getTOTALTRABAJADORES());
        txt_horas.setText("Horas: " + objA.getHORAS());
        item_fecha.setText(objA.getFECHA().substring(8, 10) + "/" + objA.getFECHA().substring(5, 7) + "/" + objA.getFECHA().substring(0, 4) + " - " + objA.getFECHA().substring(11, 19));
        btn_todos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                /*Bundle parametros = new Bundle();
                Intent pAP = new Intent(getContext(), PantallaAsistenciaPersonal.class);
                parametros.putString("p_id_tareo", objA.getIDTAREO());
                parametros.putString("p_consumidor", objA.getNOMBRECONSUMIDOR());
                parametros.putString("p_actividad", objA.getNOMBREACTIVIDAD());
                parametros.putString("p_labor", objA.getNOMBRELABOR());
                parametros.putString("p_fechaYhora", item_fecha.getText().toString());
                pAP.putExtras(parametros);
                getContext().startActivity(pAP);*/
                alertDialog = dialogAgregarCantidadHoras(objA.getIDTAREO(), objA.getHORAS());
                alertDialog.show();
            }
        });

        btn_individual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle parametros = new Bundle();
                Intent pAP = new Intent(getContext(), PantallaListaHorasPersonal.class);
                parametros.putString("p_id_tareo", objA.getIDTAREO());
                pAP.putExtras(parametros);
                getContext().startActivity(pAP);
            }
        });
        return item;
    }

    public AlertDialog dialogAgregarCantidadHoras(final String idtareo, String horas){
        final AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        LayoutInflater inflater = contexto.getLayoutInflater();

        //texto = "";

        View v = inflater.inflate(R.layout.item_dialog_horas, null);
        final EditText txt_dialog_cant = (EditText) v.findViewById(R.id.txt_dialog_cant);

        txt_dialog_cant.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        txt_dialog_cant.setText(horas);
        txt_dialog_cant.setSelection(txt_dialog_cant.getText().length());
        builder.setTitle("Ingresa cantidad Horas");
        //builder.setMessage("Tareo seleccionado: " + contenido);
        //builder.setPositiveButton(Html.fromHtml("<b><i>ACEPTAR</i><b>"), new DialogInterface.OnClickListener(){
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                if(txt_dialog_cant.getText().toString().trim() == null || txt_dialog_cant.getText().toString().trim().equals("") || txt_dialog_cant.getText().toString().trim().equals(" ") || txt_dialog_cant.getText().toString().trim().equals("0")){
                    Log.i("NULO", "NULO");
                }else{
                    Tareo obj = new Tareo();
                    Log.i("EDT", txt_dialog_cant.getText().toString());
                    obj.modificarCantidadHorasTareoSQLite(txt_dialog_cant.getText().toString(), idtareo);
                    new TareaCargarListaTareosCompleta().execute();
                    new TareaActualizar().execute(txt_dialog_cant.getText().toString(), idtareo);
                }
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i){

            }
        });
        builder.setView(v);
        return  builder.create();
    }

    private class TareaCargarListaTareosCompleta extends AsyncTask<String, Void, Void> {
        //ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            Tareo objA = new Tareo();
            objA.cargarListaTareoPantallaListaSQLite();
            return null;
        }

        protected void onPostExecute(Void result) {
            notifyDataSetChanged();
        }
    }

    private class TareaActualizar extends AsyncTask<String, Void, Void> {
        //ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String horas = params[0];
            String idtareo = params[1];
            DetalleTareo objA = new DetalleTareo();
            objA.modificarCantidadHorasDetalleTareoSQLite(horas, idtareo);
            return null;
        }

        protected void onPostExecute(Void result) {
            notifyDataSetChanged();
        }
    }

    private void dialogInputHoras(){
        //txt_dialog_cant
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("Ingrese cantidad de Horas:");


        final EditText input = new EditText(contexto);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        input.setBackground(ContextCompat.getDrawable(contexto, R.drawable.estilo_caja_texto));
        TableLayout.LayoutParams params = new TableLayout.LayoutParams();
        params.setMargins(16, 5, 16, 5);
        input.setLayoutParams(params);
        input.requestFocus();
        builder.setView(input);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void dialogHoras(){
        List<String> list = Arrays.asList("0:30","1:00", "1:30", "2:00", "2:30", "3:00", "3:30", "4:00", "4:30", "5:00", "6:00", "7:00", "8:00");
        final int [] listHora = {0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8};
        final int [] listMinutos = {30, 0, 30, 0, 30, 0, 30, 0, 30, 0, 0, 0, 0};
        final List<String> listString = Arrays.asList("0:30 minutos","1:00 hora/s", "1:30 hora/s", "2:00 hora/s", "2:30 hora/s", "3:00 hora/s", "3:30 hora/s", "4:00 hora/s", "4:30 hora/s", "5:00 hora/s", "6:00 hora/s", "7:00 hora/s", "8:00 hora/s");

        // add buffer value
        listaTiempoDialog = list.toArray(new CharSequence[list.size()]);
        listaTiempoDialogString = listString.toArray(new CharSequence[listString.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("¿Determine cantidad de horas?");
        builder.setSingleChoiceItems(listaTiempoDialogString, selectedT,
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buffKeyT = which;
                    }
                })
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //txtTiempo.setText(listString.get(buffKeyT));
                                selectedT = buffKeyT;

                                //new TareaVerificarValidesHorarioElegidoYSubtotalesDR().execute(dr_hora_inicio, dr_hora_fin, id_campo_seleccionado, nombreDia);
                            }
                        }
                )
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getActivity(),"Cancel click",Toast.LENGTH_SHORT).show();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }
}
