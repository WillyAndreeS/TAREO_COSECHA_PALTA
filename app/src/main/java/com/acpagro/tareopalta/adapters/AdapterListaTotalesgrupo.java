package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acpagro.tareopalta.PantallaAsignacionTickets;
import com.acpagro.tareopalta.PantallaReporteRendimientoGrupos;
import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.TicketPersona;

import java.util.ArrayList;

public class AdapterListaTotalesgrupo extends ArrayAdapter<TicketPersona> {
    private Activity contexto;

    public ArrayList<TicketPersona> orig;
    //public ArrayList<TicketPersona> listaTicketPersona;

    public AdapterListaTotalesgrupo(Activity context){
            super(context, R.layout.item_lista_trabajadores_rend_grupo, TicketPersona.listaTicketPersonaSQLite);
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
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_lista_trabajadores_rend_grupo, null);

        final TicketPersona objTP = TicketPersona.listaTicketPersonaSQLite.get(position);

        TextView item_conteo = (TextView)item.findViewById(R.id.item_conteo);
        TextView item_nombres = (TextView)item.findViewById(R.id.item_nombres);
        TextView item_dni = (TextView)item.findViewById(R.id.item_dni);
        TextView item_ticket = (TextView)item.findViewById(R.id.item_ticket);
        TextView item_subtotal = (TextView)item.findViewById(R.id.item_subtotal);
        LinearLayout item_fila_persona = (LinearLayout)item.findViewById(R.id.item_fila_persona);
        LinearLayout fila = (LinearLayout)item.findViewById(R.id.fila);
        LinearLayout ly_subtotal = (LinearLayout)item.findViewById(R.id.ly_subtotal);
        LinearLayout ly_numero = (LinearLayout)item.findViewById(R.id.ly_numero);

        item_conteo.setText(objTP.getNUMERO());
        item_dni.setText(objTP.getIDPERSONALGENERAL());
        item_nombres.setText(objTP.getNOMBRES());
        item_ticket.setText(objTP.getIDTICKET());
        item_subtotal.setText(objTP.getFECHAREGISTRO()+" B.");
        ly_numero.setVisibility(View.GONE);

        if(position<TicketPersona.listaTicketPersonaSQLite.size()-1){
            if(objTP.getIDTICKET().equalsIgnoreCase(TicketPersona.listaTicketPersonaSQLite.get(position+1).getIDTICKET())){
                fila.setVisibility(View.GONE);
                ly_subtotal.setVisibility(View.GONE);
            }else{
                fila.setVisibility(View.VISIBLE);
                ly_subtotal.setVisibility(View.VISIBLE);
                PantallaReporteRendimientoGrupos.conteo_total+=Integer.parseInt(objTP.getFECHAREGISTRO());
            }
        }
        if(position==TicketPersona.listaTicketPersonaSQLite.size()-1){
            fila.setVisibility(View.VISIBLE);
            ly_subtotal.setVisibility(View.VISIBLE);
            PantallaReporteRendimientoGrupos.conteo_total+=Integer.parseInt(objTP.getFECHAREGISTRO());
        }

        /*item_fila_persona.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialogAdvertenciaEliminar(objTP.getIDTICKET(), objTP.getIDPERSONALGENERAL());
                return false;
            }
        });*/

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
