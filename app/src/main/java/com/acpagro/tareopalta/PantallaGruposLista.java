package com.acpagro.tareopalta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.acpagro.tareopalta.adapters.AdapterGrupo;
import com.acpagro.tareopalta.modelo.Grupo;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;

public class PantallaGruposLista extends AppCompatActivity {
    private Button btn_nuevo_grupo;
    private ListView lv_grupos;
    private LinearLayout ly_nuevotareo;
    AdapterGrupo adapterGrupo;
    private Grupo obj;
    public static String MENU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_grupos_lista);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        //Para que apareca la flecha hacia atrás
        ly_nuevotareo = findViewById(R.id.ly_nuevotareo);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String titulo = "Grupos";
        Bundle parametros = this.getIntent().getExtras();
        if(parametros!=null){
            MENU = parametros.getString("p_menu");
            if(MENU.equalsIgnoreCase("2")){
                titulo = "Grupos (TAREOS/COS)";
                ly_nuevotareo.setVisibility(View.VISIBLE);
            }else if(MENU.equalsIgnoreCase("3")){
                titulo = "Grupos (REND.)";
                ly_nuevotareo.setVisibility(View.GONE);
            }else if(MENU.equalsIgnoreCase("4")){
                titulo = "Grupos (HORAS)";
                ly_nuevotareo.setVisibility(View.GONE);
            }

        }else {
            finish();
        }
        actionBar.setTitle(titulo);
        lv_grupos = findViewById(R.id.lv_grupos);
        btn_nuevo_grupo = findViewById(R.id.btn_nuevo_grupo);

        adapterGrupo = new AdapterGrupo(this);
        lv_grupos.setAdapter(adapterGrupo);//Se llena el ListView

        btn_nuevo_grupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MiAplicacionTareo.IDUSUARIO.equalsIgnoreCase("0")){
                    Toast.makeText(PantallaGruposLista.this, "Este usuario no puede crear GRUPOS!", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(PantallaGruposLista.this);
                    builder.setTitle("¿AGREGAR NUEVO GRUPO?");
                    builder.setIcon(R.drawable.ic_consumidor_negro);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            obj = new Grupo();
                            obj.agregarGrupoSQLite();
                            obj.cargarListaGrupoSQLite();
                            adapterGrupo.notifyDataSetChanged();
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
        });
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
    protected void onResume() {
        super.onResume();
        Log.i("ON", "RESUME");
        //new TareaCargarListaTareosCompleta().execute();
        obj = new Grupo();
        obj.cargarListaGrupoSQLite();
        if(Grupo.listaGrupos.size() > 0){
            adapterGrupo.notifyDataSetChanged();
        }else{
            //dialogAdvertenciaListaVacia();
        }
    }
}
