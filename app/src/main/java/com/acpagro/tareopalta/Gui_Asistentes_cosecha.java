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
import com.acpagro.tareopalta.adapters.AdapterPretareo;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.Tareo;

public class Gui_Asistentes_cosecha extends AppCompatActivity {

    private ListView lv_tareos;
    private ImageButton btn_nuevotareo;

    AdapterPretareo adapterListaTareosCompleta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui__asistentes_cosecha);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        //Para que apareca la flecha hacia atrás
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Asistentes a Cosecha");



        lv_tareos = (ListView)findViewById(R.id.lv_tareos);
        btn_nuevotareo = (ImageButton) findViewById(R.id.btn_nuevotareo);

        adapterListaTareosCompleta = new AdapterPretareo(Gui_Asistentes_cosecha.this);
        lv_tareos.setAdapter(adapterListaTareosCompleta);//Se llena el ListView

        btn_nuevotareo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent p;
                if(MiAplicacionTareo.IDUSUARIO.equalsIgnoreCase("0")){
                    Toast.makeText(Gui_Asistentes_cosecha.this, "Este usuario no puede crear tareos!", Toast.LENGTH_SHORT).show();
                }else{
                    p = new Intent(Gui_Asistentes_cosecha.this, PantallaTareos.class);
                    startActivity(p);
                }
            }
        });
    }
    private void llenarListView(){
        adapterListaTareosCompleta.notifyDataSetChanged();
    }

    public void dialogAdvertenciaListaVacia(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Gui_Asistentes_cosecha.this);
        builder.setTitle("Sin tareos");
        builder.setMessage("Debes crear antes un Tareo en la sección [Tareos]");
        builder.setIcon(R.drawable.ic_tareo_02);
        builder.setPositiveButton("ENTENDIDO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                Intent tareos = new Intent(Gui_Asistentes_cosecha.this, PantallaTareos.class);
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
        Tareo objA = new Tareo();
        objA.cargarListaTareoPantallaListaSQLite();
        if(Tareo.listaTareoPantallaSQLite.size() > 0){
            llenarListView();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}

