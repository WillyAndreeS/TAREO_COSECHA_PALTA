package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.acpagro.tareopalta.PantallaAsignacionTickets;
import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.TicketPersona;
import com.acpagro.tareopalta.modelo.Usuario;

import java.util.ArrayList;


public class AdapterListaUsuarios extends ArrayAdapter<Usuario> {
    private Activity contexto;

    public ArrayList<Usuario> orig;
    //public ArrayList<TicketPersona> listaTicketPersona;

    public AdapterListaUsuarios(Activity context){
        super(context, R.layout.item_lista_usuarios, Usuario.listaUsuariosHost);
        this.contexto = context;
        //listaTicketPersona = TicketPersona.listaTicketPersonaSQLite;
    }

    //para buscar implementación==========================

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Usuario> results = new ArrayList<Usuario>();
                if (orig == null)
                    orig = Usuario.listaUsuariosHost;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final Usuario g : orig) {
                            if(g.getDNI().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                    g.getUSUARIO().toLowerCase().contains(constraint.toString().toLowerCase())){
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
                Usuario.listaUsuariosHost = (ArrayList<Usuario>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return Usuario.listaUsuariosHost.size();
    }

    @Override
    public Usuario getItem(int position) {
        return Usuario.listaUsuariosHost.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //====================================================

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_lista_usuarios, null);

        final Usuario objU = Usuario.listaUsuariosHost.get(position);

        TextView item_nombres = (TextView)item.findViewById(R.id.item_nombres);
        TextView item_dni = (TextView)item.findViewById(R.id.item_dni);
        TextView item_lee_pda = (TextView)item.findViewById(R.id.item_lee_pda);
        LinearLayout item_fila_persona = (LinearLayout)item.findViewById(R.id.item_fila_persona);

        item_dni.setText(objU.getDNI()+" - " + objU.getESTADOUSUARIO());
        item_nombres.setText(objU.getUSUARIO());
        item_lee_pda.setText(objU.getLEE_PDA());

        item_fila_persona.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //dialogAdvertenciaEliminar(objTP.getIDTICKET(), objTP.getIDPERSONALGENERAL());
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
