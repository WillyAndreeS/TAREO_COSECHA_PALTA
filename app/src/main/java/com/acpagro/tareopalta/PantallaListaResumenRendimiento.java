package com.acpagro.tareopalta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.acpagro.tareopalta.adapters.AdapterListaHistorialRendimientos;
import com.acpagro.tareopalta.adapters.AdapterListaRendimientoResumen;
import com.acpagro.tareopalta.modelo.DetalleTareo;

public class PantallaListaResumenRendimiento extends AppCompatActivity {

    ListView lv_resumen;

    String IDTAREO, GRUPO, IDSUBLABOR, DNI;
    private TextView txt_total_rend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_lista_resumen_rendimiento);

        //Para que apareca la flecha hacia atrás
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Resumen Rendimiento");

        Bundle parametros = this.getIntent().getExtras();
        IDTAREO = PantallaRendimientos.IDTAREO;
        DNI = parametros.getString("p_dni");
        IDSUBLABOR = PantallaRendimientos.IDSUBLABOR;

        lv_resumen = (ListView)findViewById(R.id.lv_resumen);
        txt_total_rend = (TextView)findViewById(R.id.txt_total_rend);
        new TareaCargarListaResumenRendimientoPorTareo().execute(IDTAREO, IDSUBLABOR, DNI);
    }

    //Para volver atras
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                Intent intent = getIntent();
                intent.putExtra("dni", "000");
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private class TareaCargarListaResumenRendimientoPorTareo extends AsyncTask<String,  Void, Void> {
        //ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String idtareo = params[0];
            String idsublabor = params[1];
            String dni = params[2];
            DetalleTareo dAsis = new DetalleTareo();
            dAsis.obtenerDatosUsuarioYListaDetalleTareo(idtareo, idsublabor, dni);
            return null;
        }

        protected void onPostExecute(Void result) {
            txt_total_rend.setText(""+Math.round(DetalleTareo.sumatoria*100.00)/100.00);
            llenarListView();

        }
    }

    private void llenarListView(){
        AdapterListaHistorialRendimientos adapter = new AdapterListaHistorialRendimientos(PantallaListaResumenRendimiento.this);
        lv_resumen.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("dni", "000");
        setResult(RESULT_OK, intent);
        finish();
    }
}
