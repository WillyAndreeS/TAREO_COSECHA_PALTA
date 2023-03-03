package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acpagro.tareopalta.PantallaAsignacionTickets;
import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.Asistencia;
import com.acpagro.tareopalta.modelo.TicketPersona;

import java.util.ArrayList;

public class AdapterListaAsignacionTicket extends ArrayAdapter<TicketPersona> {
    private Activity contexto;

    public ArrayList<TicketPersona> orig;
    //public ArrayList<TicketPersona> listaTicketPersona;

    public AdapterListaAsignacionTicket(Activity context){
            super(context, R.layout.item_lista_trabajadores_asignacion, TicketPersona.listaTicketPersonaSQLite);
        this.contexto = context;
        //listaTicketPersona = TicketPersona.listaTicketPersonaSQLite;
    }

    //para buscar implementación==========================

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<TicketPersona> results = new ArrayList<TicketPersona>();
                if (orig == null)
                    orig = TicketPersona.listaTicketPersonaSQLite;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final TicketPersona g : orig) {
                            if(g.getIDPERSONALGENERAL().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                    g.getNOMBRES().toLowerCase().contains(constraint.toString().toLowerCase())){
                                results.add(g);
                            }
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                //listaTicketPersona = (ArrayList<TicketPersona>) results.values;
                TicketPersona.listaTicketPersonaSQLite = (ArrayList<TicketPersona>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return TicketPersona.listaTicketPersonaSQLite.size();
    }

    @Override
    public TicketPersona getItem(int position) {
        return TicketPersona.listaTicketPersonaSQLite.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //====================================================

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_lista_trabajadores_asignacion, null);

        final TicketPersona objTP = TicketPersona.listaTicketPersonaSQLite.get(position);

        TextView item_conteo = (TextView)item.findViewById(R.id.item_conteo);
        TextView item_nombres = (TextView)item.findViewById(R.id.item_nombres);
        TextView item_dni = (TextView)item.findViewById(R.id.item_dni);
        TextView item_ticket = (TextView)item.findViewById(R.id.item_ticket);
        LinearLayout item_fila_persona = (LinearLayout)item.findViewById(R.id.item_fila_persona);
        LinearLayout fila = (LinearLayout)item.findViewById(R.id.fila);
        LinearLayout ly_contenedor = (LinearLayout)item.findViewById(R.id.ly_contenedor);

        item_conteo.setText(objTP.getNUMERO());
        item_dni.setText(objTP.getIDPERSONALGENERAL());
        item_nombres.setText(objTP.getNOMBRES());
        item_ticket.setText(objTP.getIDTICKET());

        if(position<TicketPersona.listaTicketPersonaSQLite.size()-1){
            if(objTP.getIDTICKET().equalsIgnoreCase(TicketPersona.listaTicketPersonaSQLite.get(position+1).getIDTICKET())){
                fila.setVisibility(View.GONE);
            }else{
                fila.setVisibility(View.VISIBLE);
            }
        }

        if(objTP.getIDPERSONALGENERAL().equalsIgnoreCase(PantallaAsignacionTickets.DNI_ULTIMO_AGREGADO)){
            ly_contenedor.setBackgroundColor(Color.parseColor("#f0f8ff"));
            PantallaAsignacionTickets.lv_asignaciones.smoothScrollToPosition(position);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ly_contenedor.setBackgroundColor(Color.parseColor("#00ffffff"));
                    PantallaAsignacionTickets.PINTAR_ULTIMO_AGREGADO_LISTA=false;
                    PantallaAsignacionTickets.DNI_ULTIMO_AGREGADO="";
                }
            }, 300);
        }

        item_fila_persona.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialogAdvertenciaEliminar(objTP.getIDTICKET(), objTP.getIDPERSONALGENERAL());
                return false;
            }
        });

        return item;
    }

    public void dialogAdvertenciaEliminar(String IDTICKET, String IDPERSONALGENERAL){
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("¿Eliminar?");
        builder.setMessage("Ticket: " + IDTICKET + " | DNI: " + IDPERSONALGENERAL);
        builder.setIcon(R.drawable.ic_eliminar_negro);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                TicketPersona obj = new TicketPersona();
                obj.eliminarTicketPersonaAsignacionSQLite(IDTICKET, IDPERSONALGENERAL);

                Asistencia objDA = new Asistencia();

                Long r = objDA.eliminarAsistenciaCosechaSQLite(IDPERSONALGENERAL);

                obj.cargarListaTicketPersonaAsignados();
                PantallaAsignacionTickets.txt_conteo.setText(""+obj.conteoAsignacionesHoy());
                notifyDataSetChanged();
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
