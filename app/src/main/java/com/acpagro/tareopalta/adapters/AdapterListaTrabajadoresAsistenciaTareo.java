package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.acpagro.tareopalta.PantallaAsistenciaPersonal;
import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.ReconocimientoAsistenciaPersonal;
import com.acpagro.tareopalta.ReconocimientoTestAsistencia;
import com.acpagro.tareopalta.modelo.Asistencia;

public class AdapterListaTrabajadoresAsistenciaTareo extends ArrayAdapter<Asistencia> {
    private Activity contexto;

    public AdapterListaTrabajadoresAsistenciaTareo(Activity context){
        super(context, R.layout.item_lista_trabajadores_asistencia, Asistencia.listaAsistenciaSQLite);
        this.contexto = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_lista_trabajadores_asistencia, null);

        final Asistencia objA = Asistencia.listaAsistenciaSQLite.get(position);

        TextView item_dni = (TextView)item.findViewById(R.id.item_dni);
        TextView item_nombres = (TextView)item.findViewById(R.id.item_nombres);
        ImageButton ibtnEliminarAsistencia = (ImageButton) item.findViewById(R.id.ibtnEliminarAsistencia);

        item_dni.setText(objA.getIDPERSONALGENERAL());
        item_nombres.setText(objA.getNOMBRES());

        ibtnEliminarAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAdvertenciaEliminarAsistencia(objA.getIDTAREO(), objA.getIDPERSONALGENERAL(), objA.getNOMBRES(), objA.getFECHAREGISTRO());
                //Toast.makeText(contexto, ""+objA.getNOMBRES()+" // "+objA.getFECHAREGISTRO(), Toast.LENGTH_SHORT).show();
            }
        });

        return item;
    }

    public void dialogAdvertenciaEliminarAsistencia(final String IDTAREO, final String DNI, String NOMBRE, final String FECHAREGISTRO){
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("¿Eliminar?");
        builder.setMessage(DNI + "  |  " + NOMBRE);
        builder.setIcon(R.drawable.ic_eliminar_negro);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                eliminarAsistencia(IDTAREO, DNI, FECHAREGISTRO);
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

        if(PantallaAsistenciaPersonal.txt_cantidad_personas != null){
            //PantallaAsistenciaPersonal.txt_cantidad_personas.setText("Personal Registrado: " + Asistencia.listaAsistenciaSQLite.size());
            PantallaAsistenciaPersonal.txt_cantidad_personas.setText("" + Asistencia.listaAsistenciaSQLite.size());
        }else if(ReconocimientoTestAsistencia.txt_conteo != null){
            ReconocimientoTestAsistencia.txt_conteo.setText(""+Asistencia.listaAsistenciaSQLite.size());
        }else if(ReconocimientoAsistenciaPersonal.txt_cantidad_personas != null){
            ReconocimientoAsistenciaPersonal.txt_cantidad_personas.setText(""+Asistencia.listaAsistenciaSQLite.size());
        }

        /*if(PantallaAsistenciaPersonal.MODULO_SELECCIONADO == null){
            PantallaAsistenciaPersonal.txt_cantidad_personas.setText("Personal Registrado: " + Asistencia.listaAsistenciaSQLite.size());
        }else{
            ReconocimientoTestAsistencia.txt_conteo.setText(""+Asistencia.listaAsistenciaSQLite.size());
        }*/
    }

}

