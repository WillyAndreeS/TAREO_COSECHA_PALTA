package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.acpagro.tareopalta.PantallaSalidaAsistencia;
import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.Asistencia;
import com.acpagro.tareopalta.modelo.SalidaAsistencia;

public class AdapterListaSalidaAsistencia extends ArrayAdapter<SalidaAsistencia> {
    private Activity contexto;

    public AdapterListaSalidaAsistencia(Activity context){
        super(context, R.layout.item_lista_salida_asistencia, SalidaAsistencia.listaSalida);
        this.contexto = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_lista_salida_asistencia, null);

        final SalidaAsistencia objA = SalidaAsistencia.listaSalida.get(position);

        TextView item_dni = (TextView)item.findViewById(R.id.item_dni);
        TextView item_nombres = (TextView)item.findViewById(R.id.item_nombres);
        TextView item_hora = (TextView)item.findViewById(R.id.item_hora);
        LinearLayout item_fila_persona = item.findViewById(R.id.item_fila_persona);

        item_dni.setText(objA.getDNI());
        item_hora.setText(objA.getHORA());
        item_nombres.setText(objA.getNOMBRE_COMPLETO());

        item_fila_persona.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(objA.getSINCRONIZADO().equalsIgnoreCase("0")){
                    dialogAdvertenciaEliminarSalida(objA.getDNI(), objA.getFECHA(), objA.getNOMBRE_COMPLETO());
                }else{
                    Toast.makeText(contexto, "Registro ya ha sido transferido, no se puede eliminar.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        item_fila_persona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(objA.getSINCRONIZADO().equalsIgnoreCase("0")){
                    Toast.makeText(contexto, "Mantener presionado para eliminar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return item;
    }

    public void dialogAdvertenciaEliminarSalida(final String DNI, final String FECHA, final String NOMBRE){
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("¿Eliminar SALIDA?");
        builder.setMessage(DNI + "  |  " + NOMBRE);
        builder.setIcon(R.drawable.ic_eliminar_negro);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SalidaAsistencia obj = new SalidaAsistencia();
                obj.eliminarSalidaAsistenciaSQLite(DNI, FECHA);
                PantallaSalidaAsistencia.cargarLista(contexto);
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

    public void eliminarAsistencia(String IDTAREO, String DNI, String FECHAREGISTRO){
        //eliminarDetalleTareoSQLite
        Asistencia objDA = new Asistencia();
        Log.i("FECHA", FECHAREGISTRO);
        Long r = objDA.eliminarAsistenciaSQLite(IDTAREO, DNI, FECHAREGISTRO);
//        Long r = objDA.eliminarAsistenciaSQLite(IDTAREO, DNI);

        cargarListaTrabajadores(IDTAREO);
    }

    public void cargarListaTrabajadores(String idTareo){
        Asistencia objA = new Asistencia();
        objA.obtenerListaTrabajadoresAsistenciaPorTareo(idTareo, "4", "DESC");
        notifyDataSetChanged();
    }

}

