package com.acpagro.tareopalta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acpagro.tareopalta.adapters.AdapterListaTrabajadoresSalida;
import com.acpagro.tareopalta.modelo.DetalleTareo;
import com.acpagro.tareopalta.modelo.SalidaPersonal;

import java.util.Arrays;
import java.util.List;

public class PantallaRegistrarSalida extends AppCompatActivity implements View.OnClickListener{

    ListView lv_lista_salidas_hoy;
    ImageButton ibtnBuscarTrabajador;
    RelativeLayout rl_registrar_salida;
    EditText edt_dni;
    TextView txt_dni_nombre, txtComboHora, txt_dni_oculto;
    Button btn_grabar, btn_salir;
    private final static int SELECCIONAR_TRABAJADOR = 1;

    //VARIABLES DIALOG TIME
    CharSequence[] listaTiempoDialog;
    CharSequence[] listaTiempoDialogString;
    String horaSalidaSeleccionada = "";

    AdapterListaTrabajadoresSalida adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_registrar_salida);

        //Para que apareca la flecha hacia atrás
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Registrar Salida");

        lv_lista_salidas_hoy = (ListView)findViewById(R.id.lv_lista_salidas_hoy);
        ibtnBuscarTrabajador = (ImageButton) findViewById(R.id.ibtnBuscarTrabajador);
        ibtnBuscarTrabajador.setOnClickListener(this);
        edt_dni = (EditText) findViewById(R.id.edt_dni);
        rl_registrar_salida = (RelativeLayout) findViewById(R.id.rl_registrar_salida);
        txt_dni_nombre = (TextView) findViewById(R.id.txt_dni_nombre);
        txt_dni_oculto = (TextView) findViewById(R.id.txt_dni_oculto);
        txtComboHora = (TextView) findViewById(R.id.txtComboHora);
        txtComboHora.setOnClickListener(this);
        btn_grabar = (Button) findViewById(R.id.btn_grabar);
        btn_grabar.setOnClickListener(this);
        btn_salir = (Button) findViewById(R.id.btn_salir);
        btn_salir.setOnClickListener(this);


        edt_dni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(String.valueOf(charSequence).length() == 8){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_dni.getWindowToken(), 0);
                    //new TareaVerificarSiEstaEnAsistencia().execute(IDTAREO, edt_dni.getText().toString());
                    obteneNombreTrabajador(edt_dni.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        adapter = new AdapterListaTrabajadoresSalida(PantallaRegistrarSalida.this);
        lv_lista_salidas_hoy.setAdapter(adapter);//Se llena el ListView

        cargarListaSalidasHoy();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECCIONAR_TRABAJADOR) {
            if (resultCode == RESULT_OK) {
                edt_dni.setText(data.getStringExtra("dni"));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtnBuscarTrabajador:
                startActivityForResult(new Intent(PantallaRegistrarSalida.this, PantallaListaTrabajadoresSalidaSelect.class) , SELECCIONAR_TRABAJADOR);
                break;
            case R.id.txtComboHora:
                dialogHoraSalida();
                break;
            case R.id.btn_grabar:
                grabar();
                break;
            case R.id.btn_salir:
                finish();
                break;
        }
    }


    private void obteneNombreTrabajador(String dni){
        DetalleTareo dAsis = new DetalleTareo();
        boolean rpta = dAsis.obtenerNombreUsuario(dni);

        if(edt_dni.getText().toString().trim() == null || edt_dni.getText().toString().trim().equals("") || edt_dni.getText().toString().trim().equals(" ")){
            Log.i("Nulo", edt_dni.getText().toString());
        }else{
            if(rpta){
                Log.i("No Nulo", edt_dni.getText().toString());
                txt_dni_nombre.setText(edt_dni.getText().toString().trim() + " : " + DetalleTareo.nombre);
                txt_dni_oculto.setText(edt_dni.getText().toString().trim());
                edt_dni.setText("");
                edt_dni.requestFocus();
            }else{
                //Falso
                edt_dni.setText("");
                txt_dni_oculto.setText("");
                txtComboHora.setText("");
                txt_dni_nombre.setText("");
                edt_dni.requestFocus(R.id.edt_dni);
                cambiarColorBackgroundError();
            }

        }
    }

    private void dialogHoraSalida(){
        final List<String> list = Arrays.asList(
                "8:00","8:30","9:00",
                "9:30", "10:00","10:30",
                "11:00","11:30", "12:00",
                "12:30", "13:00","13:30",
                "14:00","14:30","15:00",
                "15:30", "16:00","16:30",
                "17:00"
        );
        final int [] listHora = {0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7};
        final int [] listMinutos = {30, 0, 30, 0, 30, 0, 30, 0, 30, 0, 0, 0};
        final List<String> listString = Arrays.asList(
                "8:00 AM","8:30 AM", "9:00 AM",
                "9:30 AM", "10:00 AM", "10:30 AM",
                "11:00 AM", "11:30 AM", "12:00 PM",
                "12:30 PM", "1:00 PM", "1:30 PM",
                "2:00 PM", "2:30 PM", "3:00 PM",
                "3:30 PM", "4:00 PM", "4:30 PM",
                "5:00 PM");

        // add buffer value
        listaTiempoDialog = list.toArray(new CharSequence[list.size()]);
        listaTiempoDialogString = listString.toArray(new CharSequence[listString.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Hora de salida?");
        builder.setItems(listaTiempoDialogString,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        txtComboHora.setText(listString.get(which));
                        horaSalidaSeleccionada = list.get(which);
                        //Log.d("HORA SELECCIONADA", list.get(which));
                    }
                })
                .setCancelable(true)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getActivity(),"Cancel click",Toast.LENGTH_SHORT).show();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void grabar(){
        String dni = txt_dni_oculto.getText().toString().trim();
        if(dni.length() == 8 && txtComboHora.getText().length()!=0){
            guardarSalidaSQLite(dni, horaSalidaSeleccionada);
        }else{
            edt_dni.setText("");
            txt_dni_oculto.setText("");
            txtComboHora.setText("");
            txt_dni_nombre.setText("");
            edt_dni.requestFocus(R.id.edt_dni);
            //Toast.makeText(PantallaRegistrarSalida.this, "DNI INCORRECTO", Toast.LENGTH_SHORT).show();
            cambiarColorBackgroundError();
        }
    }

    private void reproducirPitido(){
        MediaPlayer mp = MediaPlayer.create(this, R.raw.pitido_scanner);
        mp.start();
    }

    private void guardarSalidaSQLite(String DNI, String HORA){
        SalidaPersonal objA = new SalidaPersonal();
        Long r = objA.agregarSalida(DNI, HORA);
        Log.i("LONG_SALIDA", ""+r);
        if(r == -1){//es que no se grabo!
            cambiarColorBackgroundError();
            edt_dni.setText("");
            txt_dni_nombre.setText("");
            txtComboHora.setText("");
            txt_dni_oculto.setText("");
            edt_dni.requestFocus();
            //esperaSegundosYAbrirLectorCamara(3);
            //Toast.makeText(PantallaAsistenciaPersonal.this, "Trabajador ya ha sido asignado a TAREO.", Toast.LENGTH_SHORT).show();
        }else{//Se grabo correctamente!
            reproducirPitido();
            cambiarColorBackgroundExito();
            edt_dni.setText("");
            txt_dni_nombre.setText("");
            txtComboHora.setText("");
            txt_dni_oculto.setText("");
            edt_dni.requestFocus();
            cargarListaSalidasHoy();
            //cargarListaTrabajadores(IDTAREO);
        }
    }

    public void cargarListaSalidasHoy(){
        SalidaPersonal objA = new SalidaPersonal();
        objA.listarSalidasHoy();
        adapter.notifyDataSetChanged();
    }

    public void cambiarColorBackgroundError(){
        android.os.Vibrator v = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
        long [] patron = {0, 300, 300, 300};
        v.vibrate(patron, -1);

        rl_registrar_salida.setBackgroundColor(Color.parseColor("#ff0000"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_registrar_salida.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }, 700);
    }

    public void cambiarColorBackgroundExito(){
        android.os.Vibrator v = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(400);

        rl_registrar_salida.setBackgroundColor(Color.parseColor("#6dff4d"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_registrar_salida.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }, 700);
    }
}
