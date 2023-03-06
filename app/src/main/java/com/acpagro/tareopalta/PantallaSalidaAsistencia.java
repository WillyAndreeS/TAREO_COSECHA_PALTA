package com.acpagro.tareopalta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acpagro.tareopalta.adapters.AdapterListaSalidaAsistencia;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.SalidaAsistencia;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PantallaSalidaAsistencia extends AppCompatActivity {
    public static TextView edt_dni;
    public static ImageButton ibtnLector;
    public static ListView lv_trabajadores_salida;
    public static AdapterListaSalidaAsistencia adapter;
    public static TextView txt_conteo;
    public static RelativeLayout rl_salida_asistencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_salida_asistencia);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        //Para que apareca la flecha hacia atrás
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Salida "+dateFormat.format(date));

        rl_salida_asistencia = findViewById(R.id.rl_salida_asistencia);
        txt_conteo = findViewById(R.id.txt_conteo);
        edt_dni = findViewById(R.id.edt_dni);
        ibtnLector = findViewById(R.id.ibtnLector);
        lv_trabajadores_salida = findViewById(R.id.lv_trabajadores_salida);
        adapter = new AdapterListaSalidaAsistencia(this);
        lv_trabajadores_salida.setAdapter(adapter);


        cargarLista(this);

        edt_dni.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(String.valueOf(charSequence).length() == 8){
                    if(MiAplicacionTareo.validarNumero(edt_dni.getText().toString())){//Si es numero el dni
                        grabar();
                    }else{
                        edt_dni.setText("");
                        edt_dni.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        ibtnLector.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(PantallaSalidaAsistencia.this);
                //integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_39, IntentIntegrator.CODE_128, IntentIntegrator.QR_CODE);
                integrator.setPrompt("Enfoca - DNI / FOTOCHECK");
                integrator.setCameraId(0);
                integrator.setOrientationLocked(true);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        edt_dni.requestFocus();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MiAplicacionTareo.cargar_preferenciasLogin(PantallaSalidaAsistencia.this);
    }

    public static void cargarLista(Context context){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        SalidaAsistencia obj = new SalidaAsistencia();
        if(obj.obtenerListaSalidaAsistenciaDelDia()){
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(context, "Sin registros por mostrar a la fecha "+dateFormat.format(date), Toast.LENGTH_SHORT).show();
        }
        edt_dni.setText("");
        edt_dni.requestFocus(R.id.edt_dni);
        txt_conteo.setText(""+SalidaAsistencia.listaSalida.size());
    }

    public void grabar(){
        String dni = edt_dni.getText().toString().trim();
        SalidaAsistencia obj = new SalidaAsistencia();
        if(dni.length() == 8 && MiAplicacionTareo.esNumero(dni)){
            long p = obj.agregarSalidaAsistenciaSQLite(dni, MiAplicacionTareo.IDUSUARIO, PantallaSalidaAsistencia.this);
            if(p == -1){
                edt_dni.setText("");
                edt_dni.requestFocus(R.id.edt_dni);
                Toast.makeText(PantallaSalidaAsistencia.this, "DNI YA SE HA REGISTRADO.", Toast.LENGTH_SHORT).show();
                cambiarColorBackgroundError();
            }else{
                cargarLista(PantallaSalidaAsistencia.this);
                reproducirPitido();
                cambiarColorBackgroundExito();
            }
        }else{
            edt_dni.setText("");
            edt_dni.requestFocus(R.id.edt_dni);
            Toast.makeText(PantallaSalidaAsistencia.this, "DNI INCORRECTO", Toast.LENGTH_SHORT).show();
            cambiarColorBackgroundError();
            //cambiarColorBackgroundError();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("SalidaAsistencia", "Escaneo Cancelado");
                Toast.makeText(this, "Escaneo Cancelado", Toast.LENGTH_LONG).show();
            } else {
                Log.d("SalidaAsistencia", "Escaneado");
                edt_dni.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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

    public void cambiarColorBackgroundError(){
        android.os.Vibrator v = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
        long [] patron = {0, 300, 300, 300};
        v.vibrate(patron, -1);

        rl_salida_asistencia.setBackgroundColor(Color.parseColor("#ff0000"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_salida_asistencia.setBackgroundColor(Color.parseColor("#00ffffff"));
            }
        }, 500);
    }

    private void reproducirPitido(){
        MediaPlayer mp = MediaPlayer.create(this, R.raw.pitido_scanner);
        mp.start();
    }

    public void cambiarColorBackgroundExito(){
        /*android.os.Vibrator v = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(400);*/

        rl_salida_asistencia.setBackgroundColor(Color.parseColor("#6dff4d"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_salida_asistencia.setBackgroundColor(Color.parseColor("#00ffffff"));
            }
        }, 500);
    }
}