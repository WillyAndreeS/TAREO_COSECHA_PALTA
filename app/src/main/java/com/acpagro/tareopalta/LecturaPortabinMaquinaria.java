package com.acpagro.tareopalta;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acpagro.tareopalta.adapters.AdapterListaBinDetalle;
import com.acpagro.tareopalta.adapters.AdapterListaResumenMaquinaria;
import com.acpagro.tareopalta.modelo.BinDetalle;
import com.acpagro.tareopalta.modelo.Consumidor;
import com.acpagro.tareopalta.modelo.LecturaMaquinaria;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.ServiceTarea;
import com.acpagro.tareopalta.modelo.TicketPersona;
import com.acpagro.tareopalta.modelo.Variedad;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LecturaPortabinMaquinaria extends AppCompatActivity {
    private String FECHAHOY;
    private EditText edt_codigo;
    private ImageButton ibtnLimpiar;
    public static TextView txt_portabin, txt_trazabilidad, txt_bin, item_total_detalle;
    private LinearLayout ly_lectura;
    private String consumidor, valvula, variedad;
    private ListView lv_detalle;
    public static AdapterListaResumenMaquinaria adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectura_portabin_maquinaria);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        FECHAHOY = dateFormat.format(date);

        edt_codigo = findViewById(R.id.edt_codigo);
        ibtnLimpiar = findViewById(R.id.ibtnLimpiar);
        txt_portabin = findViewById(R.id.txt_portabin);
        txt_trazabilidad = findViewById(R.id.txt_trazabilidad);
        txt_bin = findViewById(R.id.txt_bin);
        ly_lectura = findViewById(R.id.ly_lectura);
        item_total_detalle = findViewById(R.id.item_total_detalle);
        lv_detalle = findViewById(R.id.lv_detalle);
        adapter = new AdapterListaResumenMaquinaria(this);
        lv_detalle.setAdapter(adapter);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Lectura - "+ FECHAHOY);

        edt_codigo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    String cod = edt_codigo.getText().toString().trim();
                    try {
                        if(cod.length() > 9 && cod.length()<17){//MÍNIMO 10 DÍGITOS Y MAX 16, ENTONCES PUEDE SER CÓDIGO DE TRAZABILIDAD (EJM MIN: 04PA001001, MAX: GACP0803PA001001)
                            Variedad objV = new Variedad();
                            Consumidor objC = new Consumidor();
                            consumidor = cod.substring(0, (cod.length() - 6));
                            valvula = cod.substring(consumidor.length(), (cod.length() - 3));
                            variedad = cod.substring((consumidor.length()+3), (cod.length()));

                            if ( objC.verificarConsumidor(consumidor) && MiAplicacionTareo.validarNumero(valvula) && objV.verificarVariedad(variedad) ){//CÓDIGO CORRECTO DE TRAZABILIDAD
                                if(txt_portabin.getText().toString().trim().length()!=0){//Ya hay lectura de portabin
                                    edt_codigo.setText("");
                                    txt_trazabilidad.setText(cod);
                                    cambiarBackgroundTextView(txt_trazabilidad);
                                }else{
                                    Toast.makeText(LecturaPortabinMaquinaria.this, "Debes leer primero 1)CÓDIGO PORTABIN, luego 2) CÓDIGO DE TRAZABILIDAD.", Toast.LENGTH_SHORT).show();
                                    edt_codigo.setText("");
                                    cambiarColorBackgroundError();
                                }


                            }else{
                                edt_codigo.setText("");
                                cambiarColorBackgroundError();
                            }
                        }else if(cod.length()==7){//P000003
                            if(MiAplicacionTareo.validarNumero(cod.substring(1, cod.length())) && cod.substring(0, 1).equalsIgnoreCase("P")){//Entonces es un código de BIN Ejm: P000003
                                if(txt_portabin.getText().toString().length()!=0 && txt_trazabilidad.getText().toString().trim().length()!=0){//Dato 1 y Dato 2 estan leidos, puede leer dato 3
                                    txt_bin.setText(cod);
                                    cambiarBackgroundTextView(txt_bin);

                                    /*txt_trazabilidad.setText("");
                                    txt_bin.setText("");
                                    edt_codigo.setText("");
                                    Toast.makeText(LecturaPortabinMaquinaria.this, "GRABADO", Toast.LENGTH_SHORT).show();
                                    cambiarColorBackgroundExito();*/
                                    //Puee grabar, pues ya tiene los 3 datos =======================GRABAR
                                    guardarLectura();

                                }else{
                                    Toast.makeText(LecturaPortabinMaquinaria.this, "Debes leer primero 1)CÓDIGO PORTABIN, 2)CÓDIGO DE TRAZABILIDAD, luego 3) CÓDIGO DE BIN.", Toast.LENGTH_SHORT).show();
                                    edt_codigo.setText("");
                                    cambiarColorBackgroundError();
                                }


                            }else{
                                if(cod.substring(0, 2).equalsIgnoreCase("PB") || cod.substring(0, 2).equalsIgnoreCase("MQ")|| cod.substring(0, 2).equalsIgnoreCase("RA") || cod.substring(0, 2).equalsIgnoreCase("BI")){//Entonces es un código de PORTABIN Ejm: "PBCOS77", "PBCOS78","PBCOS79","PBCOS80","MQCA","PBCOS81","PBCOS82","PBCOS83","PBCOS84","PBCOS85","PBCOS86"
                                    txt_portabin.setText(cod);
                                    edt_codigo.setText("");
                                    cambiarBackgroundTextView(txt_portabin);
                                }else{//Error de código
                                    edt_codigo.setText("");
                                    cambiarColorBackgroundError();
                                }
                            }

                        }else{//TAMBIÉN ES UN CÓDIGO DE PORTABIN
                            if(cod.substring(0, 2).equalsIgnoreCase("PB") || cod.substring(0, 2).equalsIgnoreCase("MQ")|| cod.substring(0, 2).equalsIgnoreCase("RA") || cod.substring(0, 2).equalsIgnoreCase("BI")){//Entonces es un código de PORTABIN Ejm: "PBCOS77", "PBCOS78","PBCOS79","PBCOS80","MQCA","PBCOS81","PBCOS82","PBCOS83","PBCOS84","PBCOS85","PBCOS86"
                                txt_portabin.setText(cod);
                                edt_codigo.setText("");
                                cambiarBackgroundTextView(txt_portabin);
                            }else{//Error de código
                                edt_codigo.setText("");
                                cambiarColorBackgroundError();
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        edt_codigo.setText("");
                        cambiarColorBackgroundError();
                    }

                    return true;
                }
                return false;
            }
        });

        ibtnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_portabin.setText("");
                txt_trazabilidad.setText("");
                txt_bin.setText("");
                edt_codigo.setText("");
                edt_codigo.requestFocus();
            }
        });

        edt_codigo.requestFocus();
        cargarDetalle();

    }

    public static void cargarDetalle(){
        LecturaMaquinaria objBD = new LecturaMaquinaria();
        objBD.cargarResumenLecturaMaquinaria();
        adapter.notifyDataSetChanged();
        item_total_detalle.setText(""+LecturaMaquinaria.suma_bines);
    }

    public void guardarLectura(){
        LecturaMaquinaria bd = new LecturaMaquinaria();
        Long r = bd.agregarLecturaMaquinaria(LecturaPortabinMaquinaria.this, txt_portabin.getText().toString().trim(), txt_trazabilidad.getText().toString().trim(), txt_bin.getText().toString().trim(), MiAplicacionTareo.IDUSUARIO);

        if(r == -1){
            cambiarColorBackgroundError();
        }else{
            cambiarColorBackgroundExito();
            cargarDetalle();
        }

        txt_trazabilidad.setText("");
        txt_bin.setText("");
        edt_codigo.setText("");
        edt_codigo.requestFocus();
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

    private void cambiarBackgroundTextView(TextView tv){
        tv.setBackgroundColor(Color.parseColor("#F5F557"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                edt_codigo.requestFocus();
                tv.setBackgroundColor(Color.parseColor("#00ffffff"));
            }
        }, 400);
    }

    public void cambiarColorBackgroundError(){
        ly_lectura.setBackgroundColor(Color.parseColor("#ff0000"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ly_lectura.setBackgroundColor(Color.parseColor("#00ffffff"));
                edt_codigo.requestFocus();
            }
        }, 400);
    }

    public void cambiarColorBackgroundExito(){
        ly_lectura.setBackgroundColor(Color.parseColor("#6dff4d"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ly_lectura.setBackgroundColor(Color.parseColor("#00ffffff"));
                edt_codigo.requestFocus();
            }
        }, 400);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ON", "RESUME_PORTABINMAQ");
        if(MiAplicacionTareo.LEE_PDA.equalsIgnoreCase("SI")){
            this.startService(new Intent(this, ServiceTarea.class));//INICIA EL SERVICIO
        }else{
            if(MiAplicacionTareo.TIPO_USUARIO.equalsIgnoreCase("MAQUINARIA")){
                this.startService(new Intent(this, ServiceTarea.class));//INICIA EL SERVICIO
            }
        }
    }
}