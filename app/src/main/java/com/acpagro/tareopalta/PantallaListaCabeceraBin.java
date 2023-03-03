package com.acpagro.tareopalta;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acpagro.tareopalta.adapters.AdapterListaBinCabecera;
import com.acpagro.tareopalta.modelo.BinCabecera;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.ServiceTarea;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PantallaListaCabeceraBin extends AppCompatActivity {

    public static AlertDialog alertDialog;
    private Button btn_nuevo_viaje;
    public static TextView item_total_cabeceras;
    public static AdapterListaBinCabecera adapter;
    public static ListView lv_cabecera;
    public static String FECHAHOY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_lista_cabecera_bin);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        FECHAHOY = dateFormat.format(date);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Viajes - "+ FECHAHOY);

        btn_nuevo_viaje = findViewById(R.id.btn_nuevo_viaje);
        item_total_cabeceras = findViewById(R.id.item_total_cabeceras);
        lv_cabecera = findViewById(R.id.lv_cabecera);
        adapter = new AdapterListaBinCabecera(this);
        lv_cabecera.setAdapter(adapter);

        btn_nuevo_viaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = dialogNuevoViaje(PantallaListaCabeceraBin.this);
                alertDialog.show();
            }
        });

        //cargarCabeceras();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                finish();
                return true;
            case R.id.action_configurar_impresora:
                startActivity(new Intent(this, ConexionImpresora.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_config_impresora, menu);
        return true;
    }

    public static void cargarCabeceras(){
        BinCabecera objBC = new BinCabecera();
        objBC.cargarListaCabecerasHoy();
        adapter.notifyDataSetChanged();
        item_total_cabeceras.setText(""+BinCabecera.suma_viajes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ON", "RESUME_CAB_BIN");
        if(MiAplicacionTareo.LEE_PDA.equalsIgnoreCase("SI")){
            this.startService(new Intent(this, ServiceTarea.class));//INICIA EL SERVICIO
            //getContext().stopService(new Intent(getContext(), ServiceTarea.class));
        }else{
            if(MiAplicacionTareo.TIPO_USUARIO.equalsIgnoreCase("MAQUINARIA")){
                this.startService(new Intent(this, ServiceTarea.class));//INICIA EL SERVICIO
                //stopService(new Intent(PantallaMenuPrincipalLee.this, ServiceTarea.class));
            }
        }

        cargarCabeceras();
    }

    public static AlertDialog dialogNuevoViaje(Activity context){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(R.layout.item_dialog_cabecera_bin, null);
        final EditText edt_placa = v.findViewById(R.id.edt_placa);
        final EditText edt_chofer = v.findViewById(R.id.edt_chofer);
        final EditText edt_cod_mon_1 = v.findViewById(R.id.edt_cod_mon_1);
        final EditText edt_cod_mon_2 = v.findViewById(R.id.edt_cod_mon_2);
        Button btn_aceptar = v.findViewById(R.id.btn_aceptar);
        Button btn_cancelar = v.findViewById(R.id.btn_cancelar);

        edt_placa.requestFocus();

        edt_placa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(String.valueOf(s).length()==7){
                    edt_chofer.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_cod_mon_1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    edt_cod_mon_2.requestFocus();
                    return true;
                }
                return false;
            }
        });

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_placa.getText().toString().trim().length()!=0){
                    if(edt_chofer.getText().toString().trim().length()!=0){
                        if(edt_cod_mon_1.getText().toString().trim().length()!=0){
                            alertDialog.dismiss();
                            BinCabecera bc = new BinCabecera();
                            bc.agregarBinCabecera(context, edt_placa.getText().toString().trim(), edt_chofer.getText().toString().trim(), MiAplicacionTareo.IDUSUARIO, edt_cod_mon_1.getText().toString().trim(), edt_cod_mon_2.getText().toString().trim());
                            bc.cargarListaCabecerasHoy();
                            adapter.notifyDataSetChanged();

                            Bundle parametros = new Bundle();
                            Intent pR = new Intent(context, PantallaDetalleBinLectura.class);
                            parametros.putString("p_idcabecera", BinCabecera.IDCABECERA_NEW);
                            parametros.putString("p_placa", BinCabecera.PLACA_NEW);
                            parametros.putString("p_chofer", BinCabecera.CHOFER_NEW);
                            parametros.putString("p_fecha", FECHAHOY);
                            pR.putExtras(parametros);
                            context.startActivity(pR);
                        }else{
                            edt_cod_mon_1.requestFocus();
                            Toast.makeText(context, "Debes ingresar al menos un CÓDIGO de Montacarguista (Maquinaria)", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        edt_chofer.requestFocus();
                        Toast.makeText(context, "Debes ingresar el nombre del chofer para guardar el viaje.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    edt_placa.requestFocus();
                    Toast.makeText(context, "Debes ingresar la placa para guardar el viaje.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        builder.setTitle("NUEVO VIAJE");
        builder.setView(v);
        return  builder.create();
    }
}