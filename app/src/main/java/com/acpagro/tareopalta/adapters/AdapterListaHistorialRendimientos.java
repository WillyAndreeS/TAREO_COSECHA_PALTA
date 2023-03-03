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

import com.acpagro.tareopalta.PantallaRendimientos;
import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.DetalleTareo;

public class AdapterListaHistorialRendimientos extends ArrayAdapter<DetalleTareo> {
    private Activity contexto;

    public AdapterListaHistorialRendimientos(Activity context){
        super(context, R.layout.item_lista_historial_rendimientos, DetalleTareo.listaDetalleTareoSQLite);
        this.contexto = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_lista_historial_rendimientos, null);

        final DetalleTareo objDA = DetalleTareo.listaDetalleTareoSQLite.get(position);

        TextView item_fechaYhora = (TextView)item.findViewById(R.id.item_fechaYhora);
        TextView item_rendimiento = (TextView)item.findViewById(R.id.item_rendimiento);
        ImageButton ibtnEliminarRendimiento = (ImageButton)item.findViewById(R.id.ibtnEliminarRendimiento);
        item_fechaYhora.setText(objDA.getFECHAREGISTRO().substring(11, 19));
        item_rendimiento.setText(objDA.getRENDIMIENTO());

        if(objDA.getSINCRONIZADO().equalsIgnoreCase("1")){
            ibtnEliminarRendimiento.setVisibility(View.GONE);
        }else{
            ibtnEliminarRendimiento.setVisibility(View.VISIBLE);
        }

        ibtnEliminarRendimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAdvertenciaSincronizar(objDA.getIDTAREO(), objDA.getIDSUBLABOR(), objDA.getIDPERSONALGENERAL(), objDA.getFECHAREGISTRO(), objDA.getRENDIMIENTO());
            }
        });

        return item;
    }

    public void eliminarDetalleTareoSegunIdDniFechReg(String IDTAREO, String IDSUBLABOR, String DNI, String FECHAREGISTRO){
        //eliminarDetalleTareoSQLite
        DetalleTareo objDA = new DetalleTareo();
        Log.i("FECHA", FECHAREGISTRO);
        Long r = objDA.eliminarDetalleTareoSQLite(IDTAREO, DNI, FECHAREGISTRO);

        obtenerDatosYListaDetalleTareo(IDTAREO, IDSUBLABOR, DNI);
    }

    public void obtenerDatosYListaDetalleTareo(String IDTAREO, String IDSUBLABOR, String DNI){
        DetalleTareo dAsis = new DetalleTareo();
        dAsis.obtenerDatosUsuarioYListaDetalleTareo(IDTAREO, IDSUBLABOR, DNI);

        PantallaRendimientos.txtTotal.setText(""+DetalleTareo.sumatoria);
        notifyDataSetChanged();

    }

    public void dialogAdvertenciaSincronizar(final String IDTAREO, final String IDSUBLABOR, final String DNI, final String FECHAREGISTRO, String RENDIMIENTO){
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("¿Eliminar?");
        builder.setMessage("Rendimiento: " + RENDIMIENTO);
        builder.setIcon(R.drawable.ic_eliminar_negro);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
//                eliminarDetalleTareoSegunIdDniFechReg(IDTAREO, IDSUBLABOR, DNI, FECHAREGISTRO);
                eliminarDetalleTareoSegunIdDniFechReg(IDTAREO, IDSUBLABOR, DNI, FECHAREGISTRO);
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

}
