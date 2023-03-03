package com.acpagro.tareopalta;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.acpagro.tareopalta.adapters.AdapterListaTotalesgrupo;
import com.acpagro.tareopalta.modelo.TicketCosecha;
import com.acpagro.tareopalta.modelo.TicketPersona;

public class PantallaReporteRendimientoGrupos extends AppCompatActivity {
    private ListView lv_asignaciones;
    private TextView txt_conteo;
    AdapterListaTotalesgrupo adapter;
    private Filter filter;
    public static int conteo_total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_reporte_rendimiento_grupos);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Rendimiento por Grupo");

        txt_conteo = (TextView) findViewById(R.id.txt_conteo);
        lv_asignaciones = (ListView)findViewById(R.id.lv_asignaciones);
        lv_asignaciones.setTextFilterEnabled(true);

        lv_asignaciones.setTextFilterEnabled(true);
        /*adapter = new AdapterListaTotalesgrupo(this);
        filter = adapter.getFilter();
        lv_asignaciones.setAdapter(adapter);*/

        new TareaGetRendimientosGrupo().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cargarListaAsignacionesHoy(){
        TicketPersona obj = new TicketPersona();
        obj.cargarListaTicketPersonaAsignadosConteoJabasGrupo();
        //txt_conteo.setText(""+obj.conteoAsignacionesHoy());
        adapter.notifyDataSetChanged();
    }

    private class TareaGetRendimientosGrupo extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            conteo_total=0;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            TicketPersona obj = new TicketPersona();
            TicketCosecha objC = new TicketCosecha();
            obj.cargarListaTicketPersonaAsignadosConteoJabasGrupo();
            conteo_total = objC.getConteoPantallaLecturaPDA();
            return true;
        }

        protected void onPostExecute(Boolean result) {
            //adapter.notifyDataSetChanged();
            adapter = new AdapterListaTotalesgrupo(PantallaReporteRendimientoGrupos.this);
            lv_asignaciones.setAdapter(adapter);
            //CONTEO_JABAS = result;
            txt_conteo.setText(""+conteo_total + " B.");
        }

    }
}
