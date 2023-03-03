package com.acpagro.tareopalta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.acpagro.tareopalta.adapters.AdapterListaBuscarTrabajador;
import com.acpagro.tareopalta.modelo.Asistencia;

public class PantallaListaTrabajadoresSalidaSelect extends AppCompatActivity {

    ListView lv_trab_asis_select;

    AdapterListaBuscarTrabajador adapter;
    //String IDTAREO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_lista_trabajadores_salida_select);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Para que apareca la flecha hacia atrás
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Buscar Trabajador");

        lv_trab_asis_select = (ListView)findViewById(R.id.lv_trab_asis_select);
        lv_trab_asis_select.setTextFilterEnabled(true);

        Asistencia.listaAsistenciaSQLite.clear();
        adapter = new AdapterListaBuscarTrabajador(PantallaListaTrabajadoresSalidaSelect.this);
        lv_trab_asis_select.setAdapter(adapter);//Se llena el ListView
        adapter.notifyDataSetChanged();

        //new TareaCargarListaTrabajadoresAsisPorTareo().execute(IDTAREO);
        cargarlistaTrabajadoresHoy();
    }

    public void cargarlistaTrabajadoresHoy(){
        Asistencia objA = new Asistencia();
        objA.obtenerListaAsistenciaDelDia();
        adapter.notifyDataSetChanged();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.act_menu_buscar_trabajador, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    lv_trab_asis_select.clearTextFilter();
                } else {
                    lv_trab_asis_select.setFilterText(newText);
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
