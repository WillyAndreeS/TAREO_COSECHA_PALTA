package com.acpagro.tareopalta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.acpagro.tareopalta.adapters.AdapterListaSubLabores;
import com.acpagro.tareopalta.modelo.SubLabor;

public class PantallaSeleccionSubLabor extends AppCompatActivity {
    private ListView lv_consumidor;
    public static String idtareo="";
    public static String consumidor="";
    public static String idactividad="";
    public static String actividad="";
    public static String labor="";
    public static String fechahora="";
    public static String idlabor= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_seleccion_sub_labor);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Elige SubLabor");

        Bundle parametros = this.getIntent().getExtras();
        SubLabor objSubLabor = new SubLabor();
        objSubLabor.cargarListaSubLaboresSQLite(parametros.getString("p_id_actividad"));

        idtareo = parametros.getString("p_id_tareo") ;
        consumidor = parametros.getString("p_consumidor") ;
        idactividad = parametros.getString("p_id_actividad") ;
        actividad = parametros.getString("p_actividad") ;
        labor = parametros.getString("p_labor") ;
        fechahora = parametros.getString("p_fechaYhora") ;
        idlabor = parametros.getString("p_idlabor");

        lv_consumidor = (ListView)findViewById(R.id.lv_consumidor);

        AdapterListaSubLabores adapterListaSubLabores = new AdapterListaSubLabores(this);
        lv_consumidor.setAdapter(adapterListaSubLabores);
    }

    //Para volver atras
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}
