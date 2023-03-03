package com.acpagro.tareopalta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.acpagro.tareopalta.adapters.AdapterListaTareosCompletaTareo;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.Tareo;

public class PantallaTareoAsistencia extends AppCompatActivity {
    private ListView lv_tareos;
    private ImageButton btn_nuevotareo;

    AdapterListaTareosCompletaTareo adapterListaTareosCompleta;
    public static String IDGRUPO, DESCRIPCION_GRUPO, TITULO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_tareo_asistencia);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        //Para que apareca la flecha hacia atrás
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setTitle("Seleccione Tareo (Asistencia)");

        Bundle parametros = this.getIntent().getExtras();
        if(parametros!=null){
            IDGRUPO = parametros.getString("p_idgrupo");//p_idgrupo
            DESCRIPCION_GRUPO = parametros.getString("p_desc");//p_desc
            TITULO = "GRUPO "+DESCRIPCION_GRUPO;
        }else{
            TITULO = "Seleccione Tareo (Asistencia)";
        }
        actionBar.setTitle(TITULO);

        lv_tareos = (ListView)findViewById(R.id.lv_tareos);
        btn_nuevotareo = (ImageButton) findViewById(R.id.btn_nuevotareo);

        adapterListaTareosCompleta = new AdapterListaTareosCompletaTareo(PantallaTareoAsistencia.this);
        lv_tareos.setAdapter(adapterListaTareosCompleta);//Se llena el ListView

        btn_nuevotareo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent p;
                if(MiAplicacionTareo.IDUSUARIO.equalsIgnoreCase("0")){
                    Toast.makeText(PantallaTareoAsistencia.this, "Este usuario no puede crear tareos!", Toast.LENGTH_SHORT).show();
                }else{
                    p = new Intent(PantallaTareoAsistencia.this, PantallaTareos.class);
                    startActivity(p);
                }
            }
        });


    }

    private void llenarListView(){
        adapterListaTareosCompleta.notifyDataSetChanged();
    }

    public void dialogAdvertenciaListaVacia(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PantallaTareoAsistencia.this);
        builder.setTitle("Sin tareos");
        builder.setMessage("Debes crear antes un Tareo en la sección [Tareos]");
        builder.setIcon(R.drawable.ic_tareo_02);
        builder.setPositiveButton("ENTENDIDO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                Intent tareos = new Intent(PantallaTareoAsistencia.this, PantallaTareos.class);
                startActivity(tareos);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
    protected void onPause(){
        super.onPause();
        Log.i("ON", "PAUSE");
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.i("ON", "START");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("ON", "RESUME");
        //new TareaCargarListaTareosCompleta().execute();
        /*Tareo objA = new Tareo();
        objA.cargarListaTareoPantallaListaSQLite();
        if(Tareo.listaTareoPantallaSQLite.size() > 0){
            llenarListView();
        }*/
        Tareo objA = new Tareo();
        if(IDGRUPO!=null){
            objA.cargarListaTareoPantallaListaSQLite_2(IDGRUPO);
        }else{
            objA.cargarListaTareoPantallaListaSQLite();
        }
        llenarListView();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
