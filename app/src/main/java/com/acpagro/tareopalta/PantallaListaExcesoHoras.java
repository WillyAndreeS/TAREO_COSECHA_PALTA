package com.acpagro.tareopalta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.acpagro.tareopalta.adapters.AdapterListaHorasTotal;
import com.acpagro.tareopalta.modelo.HoraPersonal;
import com.acpagro.tareopalta.modelo.HoraTareo;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PantallaListaExcesoHoras extends AppCompatActivity {

    private ListView lv_horas_exceso;
    AdapterListaHorasTotal adapter;
    public static double horaLimite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_lista_exceso_horas);

        //Para que apareca la flecha hacia atrás
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Horas Total Hoy");

        lv_horas_exceso = (ListView)findViewById(R.id.lv_horas_exceso);

        HoraTareo objHT = new HoraTareo();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date date = new Date();
        String dayOfWeek = simpledateformat.format(date);
        horaLimite = objHT.getHoraTareoHoy(MiAplicacionTareo.diaAbrevitura(dayOfWeek));
        /*if(dayOfWeek.equalsIgnoreCase("miércoles")){
            horaLimite = MiApplicacionACP.HORALIMITE_MIERCOLES;
        }else{
            horaLimite = MiApplicacionACP.HORALIMITE_LUNAVIE;
        }*/

        Log.i("LIMITE Y DIA", horaLimite + "/" + dayOfWeek);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Date date = new Date();
        String fechaRegistro = dateFormat.format(date);

        HoraPersonal hp = new HoraPersonal();
        hp.listarPersonalHorasPorFecha(fechaRegistro);
        adapter = new AdapterListaHorasTotal(this);
        lv_horas_exceso.setAdapter(adapter);

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

}
