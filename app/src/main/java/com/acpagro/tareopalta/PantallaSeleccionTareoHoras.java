package com.acpagro.tareopalta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.acpagro.tareopalta.adapters.AdapterListaTareosCompletaHoras;
import com.acpagro.tareopalta.modelo.Tareo;

public class PantallaSeleccionTareoHoras extends AppCompatActivity {

    private ListView lv_tareos;
    public static String IDGRUPO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_lista_tareos);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Para que apareca la flecha hacia atrás
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Seleccione Tareo (Horas)");

        /*Bundle parametros = this.getIntent().getExtras();
        if(parametros!=null){
            IDGRUPO = parametros.getString("p_idgrupo");
        }else {
            finish();
        }*/

        lv_tareos = (ListView)findViewById(R.id.lv_tareos);
        //new TareaCargarListaTareosCompleta().execute();
        Tareo objA = new Tareo();
        objA.cargarListaTareoPantallaListaSQLite();
        //objA.cargarListaTareoPantallaListaSQLite_2(IDGRUPO);
        if(Tareo.listaTareoPantallaSQLite.size() > 0){
            llenarListView();
        }else{
            //dialogAdvertenciaListaVacia();
        }
    }

    public void dialogAdvertenciaListaVacia(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PantallaSeleccionTareoHoras.this);
        builder.setTitle("Sin tareos");
        builder.setMessage("Debes crear antes un Tareo en la sección [Tareos]");
        builder.setIcon(R.drawable.ic_tareo_02);
        builder.setPositiveButton("ENTENDIDO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                Intent tareos = new Intent(PantallaSeleccionTareoHoras.this, PantallaTareos.class);
                startActivity(tareos);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void llenarListView(){
        AdapterListaTareosCompletaHoras adapterListaTareosCompleta = new AdapterListaTareosCompletaHoras(PantallaSeleccionTareoHoras.this);
        lv_tareos.setAdapter(adapterListaTareosCompleta);//Se llena el ListView
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
        finish();
    }
}
