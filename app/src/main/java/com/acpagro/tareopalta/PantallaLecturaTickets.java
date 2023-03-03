package com.acpagro.tareopalta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acpagro.tareopalta.modelo.Consumidor;
import com.acpagro.tareopalta.modelo.LectorVoz;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.TicketCosecha;
import com.acpagro.tareopalta.modelo.Variedad;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PantallaLecturaTickets extends AppCompatActivity {

    private EditText edConsumidor, edt_codigo;
    private TextView txtConsumidor, txtValvula, txtVariedad, txt_ticket, contadorJabasLeidas, contadorJabasGrupo, idgrupo;
    private ImageButton ibtn_block, ibtnLector;
    private RelativeLayout rl_lectura;

    private String consumidor = "", valvula = "", variedad = "";
    private int CONTEO_JABAS=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_lectura_tickets_02);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Lectura Baldes 2");

        edConsumidor = (EditText)findViewById(R.id.edt_consumidor);
        edt_codigo = (EditText)findViewById(R.id.edt_codigo);
        txtConsumidor = (TextView)findViewById(R.id.txtConsumidor);
        contadorJabasGrupo = (TextView)findViewById(R.id.contadorJabasGrupo);
        contadorJabasLeidas = (TextView)findViewById(R.id.contadorJabasLeidas);
        txtValvula = (TextView)findViewById(R.id.txtValvula);
        txtVariedad = (TextView)findViewById(R.id.txtVariedad);
        txt_ticket = (TextView)findViewById(R.id.txt_ticket);
        idgrupo = (TextView)findViewById(R.id.idgrupo);
        ibtn_block = (ImageButton)findViewById(R.id.ibtn_block);
        rl_lectura = findViewById(R.id.rl_lectura);
        ibtnLector = findViewById(R.id.ibtnLector);

        LectorVoz.iniciarLectorVoz(this);

        edConsumidor.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){

                    String codigo = edConsumidor.getText().toString().trim().replaceAll("\\s","");
                    if(codigo.length() == 5){
                        if(txtConsumidor.getText().toString().trim().length()==0){
                            MiAplicacionTareo.vibrate(400, PantallaLecturaTickets.this);
                            Toast.makeText(PantallaLecturaTickets.this, "Debe leer Codigo de Bin Primero", Toast.LENGTH_SHORT).show();
//                            LectorVoz.leer_voz( "Error");

                            cambiarColorBackgroundError();
                        }else{
                            //txt_ticket.setText(edConsumidor.getText().toString().trim());
                            if (isNumeric(codigo)){
                                txt_ticket.setText(codigo);
                                idgrupo.setText(codigo);
                                grabar();
                            }else{
                                MiAplicacionTareo.vibrate(400, PantallaLecturaTickets.this);
                                Toast.makeText(PantallaLecturaTickets.this, "No es codigo de Grupo - 001", Toast.LENGTH_SHORT).show();

//                                LectorVoz.leer_voz( "Error");
                                cambiarColorBackgroundError();
                            }

                        }
                    }else if(codigo.length()>5 && codigo.length()<17){//15
                        consumidor = "";
                        valvula = "";
                        variedad = "";

                        Variedad objV = new Variedad();
                        Consumidor objC = new Consumidor();

                        //String bin = edConsumidor.getText().toString();
                        String bin = codigo;
                        consumidor = bin.substring(0, (bin.length() - 6));
                        if (objC.verificarConsumidor(consumidor)){
                            valvula = bin.substring(consumidor.length(), (bin.length() - 3));
                            if (isNumeric(valvula)){
                                variedad = bin.substring((consumidor.length()+3), (bin.length()));
                                if (objV.verificarVariedad(variedad)){
                                    txtConsumidor.setText("" + consumidor);
                                    txtValvula.setText("" + valvula);
                                    txtVariedad.setText("" + objV.getVariedadPorID(variedad));
                                }else{
                                    MiAplicacionTareo.vibrate(400, PantallaLecturaTickets.this);
                                    Toast.makeText(PantallaLecturaTickets.this, "No es codigo de Bin - 001", Toast.LENGTH_SHORT).show();

//                                    LectorVoz.leer_voz( "Error");
                                    cambiarColorBackgroundError();
                                }
                            }else{
                                MiAplicacionTareo.vibrate(400, PantallaLecturaTickets.this);
                                Toast.makeText(PantallaLecturaTickets.this, "No es codigo de Bin - 002", Toast.LENGTH_SHORT).show();

//                                LectorVoz.leer_voz( "Error");
                                cambiarColorBackgroundError();
                            }
                        }else{
                            MiAplicacionTareo.vibrate(400, PantallaLecturaTickets.this);
                            Toast.makeText(PantallaLecturaTickets.this, "No es codigo de Bin - 003", Toast.LENGTH_SHORT).show();
                            cambiarColorBackgroundError();

//                            LectorVoz.leer_voz( "Error");

                        }
                    }else {
                        MiAplicacionTareo.vibrate(400, PantallaLecturaTickets.this);
                        cambiarColorBackgroundError();
                        Toast.makeText(PantallaLecturaTickets.this, "Lectura Incorrecta", Toast.LENGTH_SHORT).show();
                    }

                    /*if (edConsumidor.length() == 5){
                        Toast.makeText(PantallaLecturaTickets.this, "Debe leer Codigo de Bin Primero", Toast.LENGTH_SHORT).show();
                        cambiarColorBackgroundError();
                    }else{
                        Variedad objVa = new Variedad();
                        if (!edConsumidor.getText().toString().equals("")){
                            String bin = edConsumidor.getText().toString();
                            consumidor = bin.substring(0, (bin.length() - 6));
                            valvula = bin.substring(consumidor.length(), (bin.length() - 3));
                            variedad = bin.substring((consumidor.length()+3), (bin.length()));
                            txtConsumidor.setText("Consumidor: " + consumidor);
                            txtValvula.setText("Valvula: " + valvula);
                            txtVariedad.setText("Variedad: " + objVa.getVariedadPorID(variedad));
                        }
                    }*/

                    edConsumidor.setText("");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run(){
                                /*edt_codigo.setText("");
                                edt_codigo.requestFocus();*/
                                edConsumidor.setText("");
                                edConsumidor.requestFocus();
                            }
                        }, 200);

                    return true;
                }
                return false;
            }
        });

        edt_codigo.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){

                    if (edt_codigo.length() > 5){
                        Variedad objV = new Variedad();
                        String bin = edConsumidor.getText().toString();
                        consumidor = bin.substring(0, (bin.length() - 6));
                        valvula = bin.substring(consumidor.length(), (bin.length() - 3));
                        variedad = bin.substring((consumidor.length()+3), (bin.length()));
                        txtConsumidor.setText("Consumidor: " + consumidor);
                        txtValvula.setText("Valvula: " + valvula);
                        txtVariedad.setText("Variedad: " + objV.getVariedadPorID(variedad));
                    }else if(edt_codigo.length()== 5){
                        grabar();
                    }
                    edConsumidor.setText("");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run(){
                            edt_codigo.setText("");
                            edt_codigo.requestFocus();
                        }
                    }, 200);

                    return true;
                }
                return false;
            }
        });

        ibtnLector.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(PantallaLecturaTickets.this);
                //integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_39, IntentIntegrator.CODE_128, IntentIntegrator.QR_CODE);
                integrator.setPrompt("Enfoca Código.");
                integrator.setCameraId(0);
                ///integrator.setOrientationLocked(true);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        edConsumidor.requestFocus();
        obtenerListaConteoJabas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MiAplicacionTareo.cargar_preferenciasLogin(PantallaLecturaTickets.this);
    }

    public void vibrate(int duration)
    {
        Vibrator vibs = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibs.vibrate(duration);
    }

    public void obtenerListaConteoJabas(){
        new TareaGetCOnteoYListaJabas().execute();
    }

    private class TareaGetCOnteoYListaJabas extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            TicketCosecha obj = new TicketCosecha();
            return obj.getConteoPantallaLecturaPDA();
        }

        protected void onPostExecute(Integer result) {
            CONTEO_JABAS = result;
            contadorJabasLeidas.setText(""+result + " B.");
            Double x = (double)Math.round(MiAplicacionTareo.KG_JABA*result * 100d) / 100d;
            /*contadorJabasLeidas.setText(""+TicketCosecha.conteoPorViaje + " J.");
            Double x = (double)Math.round(MiAplicacionTareo.KG_JABA*TicketCosecha.conteoPorViaje * 100d) / 100d;
            contadorKilos.setText(""+x + " KG.");
            adapter.notifyDataSetChanged();*/
            /*contadorJabasLeidas.setText(""+TicketCosecha.listaTicketCosechaConteoSQLite.size() + " J.");
            Double x = (double)Math.round(MiAplicacionTareo.KG_JABA*TicketCosecha.listaTicketCosechaConteoSQLite.size() * 100d) / 100d;
            contadorKilos.setText(""+x + " KG.");
            adapter.notifyDataSetChanged();*/
        }

    }

    private class TareoGetConteoPorGrupo extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            String p1 = params[0];
            TicketCosecha obj = new TicketCosecha();
            return obj.getConteoPantallaLecturaPorGrupoPDA(p1);
        }

        protected void onPostExecute(Integer result) {
            contadorJabasGrupo.setText(""+result+" B.");
        }

    }

    public void grabar(){

        String ticket = txt_ticket.getText().toString().trim();

        //if(edt_codigo.getText().toString().length() == 5) {
        if(ticket.length() == 5 && txtConsumidor.getText().toString().length()!=0) {
            guardarLecturaTicketSQLite(ticket, this.consumidor, this.valvula, this.variedad);
            cambiarColorBackgroundExito();
            txt_ticket.setText("");
        }else{
            /*Variedad objV = new Variedad();
            String bin = edConsumidor.getText().toString();
            consumidor = bin.substring(0, (bin.length() - 6));
            valvula = bin.substring(consumidor.length(), (bin.length() - 3));
            variedad = bin.substring((consumidor.length()+3), (bin.length()));
            txtConsumidor.setText("Consumidor: " + consumidor);
            txtValvula.setText("Valvula: " + valvula);
            //txtVariedad.setText("Variedad: " + variedad);
            txtVariedad.setText("Variedad: " + objV.getVariedadPorID(variedad));
            edt_codigo.requestFocus(R.id.edt_codigo);*/

            cambiarColorBackgroundError();
        }
    }

    private void guardarLecturaTicketSQLite(String IDTICKET, String IDCONSUMIDOR, String VALVULA, String VARIEDAD){
        TicketCosecha objTC = new TicketCosecha();
        Long r = Long.parseLong("-1") ;
        try {
            r = objTC.agregarTicketCosechaSQLite(IDTICKET, IDCONSUMIDOR, VALVULA, VARIEDAD, MiAplicacionTareo.IDUSUARIO, PantallaLecturaTickets.this);
        }catch (Exception e){
            r = Long.parseLong("-1");
        }

        //Log.i("LONG_IDTAREO", ""+r);
        if(r == -1){//es que no se grabo!
            cambiarColorBackgroundError();
            edt_codigo.setText("");
            edt_codigo.requestFocus();
        }/*else if(r == 500){//TICKET SIN ASIGNAR, ENTONCES NO SE GRABRAA!
            snackPersonalizado(rl_lectura, "ETIQUETA SIN ASIGNAR, INTENTA PRIMERO ASIGNAR ESTA ETIQUETA A UN COSECHADOR");
            reproducirError();
            cambiarColorBackgroundError();

            edt_codigo.setText("");
            edt_codigo.requestFocus();_
        }*/else{//Se grabo correctamente!
            cambiarColorBackgroundExito();
            edt_codigo.setText("");
            edt_codigo.requestFocus();
            CONTEO_JABAS++;
            contadorJabasLeidas.setText(""+CONTEO_JABAS + " B.");
            new TareoGetConteoPorGrupo().execute(IDTICKET);
            //Double x = (double)Math.round(MiAplicacionTareo.KG_JABA*CONTEO_JABAS * 100d) / 100d;
//            contadorKilos.setText(""+(MiAplicacionTareo.KG_JABA*CONTEO_JABAS) + " KG.");
        }
    }

    public void cambiarColorBackgroundExito(){
        rl_lectura.setBackgroundColor(Color.parseColor("#6dff4d"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_lectura.setBackgroundColor(Color.parseColor("#00ffffff"));
            }
        }, 200);
    }

    public void cambiarColorBackgroundError(){
        rl_lectura.setBackgroundColor(Color.parseColor("#ff0000"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_lectura.setBackgroundColor(Color.parseColor("#00ffffff"));
            }
        }, 200);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                finish();
                return true;
            case R.id.action_transferir:
                if(MiAplicacionTareo.hayConexion(PantallaLecturaTickets.this)){
//                    new TareaEnviarDataXMLHtml().execute();
                }else{
                    Toast.makeText(getApplicationContext(), "SIN CONEXIÓN A INTERNET, verifica y vuelve a intentarlo.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_reporte:
                startActivity(new Intent(PantallaLecturaTickets.this, PantallaReporteRendimientoGrupos.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lectura, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy()
    {
        LectorVoz.finalizarLectorVoz();
        super.onDestroy();
    }

    public boolean isNumeric(String cadena) {

        boolean resultado;

        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }

        return resultado;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Escaneo Cancelado");
                Toast.makeText(this, "Escaneo Cancelado", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Escaneado");
                String codigo = result.getContents().trim();
                if(codigo.length() == 5){
                    if(txtConsumidor.getText().toString().trim().length()==0){
                        MiAplicacionTareo.vibrate(400, PantallaLecturaTickets.this);
                        Toast.makeText(PantallaLecturaTickets.this, "Debe leer Codigo de Bin Primero", Toast.LENGTH_SHORT).show();
                        cambiarColorBackgroundError();
                    }else{
                        if (isNumeric(codigo)){
                            txt_ticket.setText(codigo);
                            idgrupo.setText(codigo);
                            grabar();
                        }else{
                            MiAplicacionTareo.vibrate(400, PantallaLecturaTickets.this);
                            Toast.makeText(PantallaLecturaTickets.this, "No es codigo de Grupo - 001", Toast.LENGTH_SHORT).show();
                            cambiarColorBackgroundError();
                        }

                    }
                }else if(codigo.length()>5 && codigo.length()<17){//15
                    consumidor = "";
                    valvula = "";
                    variedad = "";

                    Variedad objV = new Variedad();
                    Consumidor objC = new Consumidor();
                    String bin = codigo;
                    consumidor = bin.substring(0, (bin.length() - 6));
                    if (objC.verificarConsumidor(consumidor)){
                        valvula = bin.substring(consumidor.length(), (bin.length() - 3));
                        if (isNumeric(valvula)){
                            variedad = bin.substring((consumidor.length()+3), (bin.length()));
                            if (objV.verificarVariedad(variedad)){
                                txtConsumidor.setText("" + consumidor);
                                txtValvula.setText("" + valvula);
                                txtVariedad.setText("" + objV.getVariedadPorID(variedad));
                            }else{
                                MiAplicacionTareo.vibrate(400, PantallaLecturaTickets.this);
                                Toast.makeText(PantallaLecturaTickets.this, "No es codigo de Bin - 001", Toast.LENGTH_SHORT).show();
                                cambiarColorBackgroundError();
                            }
                        }else{
                            MiAplicacionTareo.vibrate(400, PantallaLecturaTickets.this);
                            Toast.makeText(PantallaLecturaTickets.this, "No es codigo de Bin - 002", Toast.LENGTH_SHORT).show();
                            cambiarColorBackgroundError();
                        }
                    }else{
                        MiAplicacionTareo.vibrate(400, PantallaLecturaTickets.this);
                        Toast.makeText(PantallaLecturaTickets.this, "No es codigo de Bin - 003", Toast.LENGTH_SHORT).show();
                        cambiarColorBackgroundError();
                    }
                }else {
                    MiAplicacionTareo.vibrate(400, PantallaLecturaTickets.this);
                    cambiarColorBackgroundError();
                    Toast.makeText(PantallaLecturaTickets.this, "Lectura Incorrecta", Toast.LENGTH_SHORT).show();
                }

                edConsumidor.setText("");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run(){
                                /*edt_codigo.setText("");
                                edt_codigo.requestFocus();*/
                        edConsumidor.setText("");
                        edConsumidor.requestFocus();
                    }
                }, 200);

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
