package com.acpagro.tareopalta;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acpagro.tareopalta.adapters.AdapterListaAsignacionTicket;
import com.acpagro.tareopalta.modelo.Administrador;
import com.acpagro.tareopalta.modelo.Asistencia;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.TicketPersona;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class PantallaAsignacionTickets extends AppCompatActivity {
    private EditText edt_codigo, edt_codigo_dni;
    RelativeLayout rl_asignacion;
    public static ListView lv_asignaciones;
    public static TextView txt_consumidor, txt_actividad_labor, fecha_hora, txt_ticket, txt_dni, txt_conteo;
    private ImageButton ibtnLimpiar, ibtnLector;
    String caracter;
    AdapterListaAsignacionTicket adapter;
    public static String DNI_ULTIMO_AGREGADO = "";
    public static boolean PINTAR_ULTIMO_AGREGADO_LISTA = false;

    TicketPersona objTP;
    RequestQueue requestA;
    private Filter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_asignacion_tickets);

        final Activity activity = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Asignación de Grupos");

        requestA = Volley.newRequestQueue(this);

        txt_consumidor = (TextView)findViewById(R.id.txt_consumidor);
        txt_actividad_labor = (TextView)findViewById(R.id.txt_actividad_labor);
        fecha_hora = (TextView)findViewById(R.id.fecha_hora);
        lv_asignaciones = (ListView)findViewById(R.id.lv_asignaciones);
        lv_asignaciones.setTextFilterEnabled(true);

        lv_asignaciones.setTextFilterEnabled(true);
        adapter = new AdapterListaAsignacionTicket(this);
        filter = adapter.getFilter();
        lv_asignaciones.setAdapter(adapter);

        txt_ticket = (TextView)findViewById(R.id.txt_ticket);
        txt_dni = (TextView)findViewById(R.id.txt_dni);
        txt_conteo = (TextView)findViewById(R.id.txt_conteo);

        edt_codigo_dni = (EditText)findViewById(R.id.edt_codigo_dni);
        edt_codigo = (EditText)findViewById(R.id.edt_codigo);
        rl_asignacion = (RelativeLayout)findViewById(R.id.rl_asignacion);
        ibtnLimpiar = (ImageButton) findViewById(R.id.ibtnLimpiar);
        ibtnLector = (ImageButton) findViewById(R.id.ibtnLector);

        edt_codigo_dni.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    objTP = new TicketPersona();
                    String cod = edt_codigo_dni.getText().toString().trim();

                    if(cod.length() ==8){
                        if (txt_ticket.getText() != ""){
                            if(MiAplicacionTareo.validarNumero(cod)){
                                /*if(txt_dni.getText().length()==8){
                                    Toast.makeText(PantallaAsignacionTickets.this, "Aún no has asignado un TICKET al DNI leído!", Toast.LENGTH_SHORT).show();
                                    cambiarColorBackgroundError();
                                }else{*/
                                    txt_dni.setText(cod);
                                    if(txt_dni.getText().toString().length() == 8 && txt_ticket.getText().toString().length() == 5){
                                        Log.i("GRABADO", " OKI");
                                        if(objTP.buscarDni(txt_dni.getText().toString().trim()) == 0){
                                            //guardarAsistenciaSQLite(MiAplicacionTareo.CODIGOTAREOCOSECHA.trim(), txt_dni.getText().toString().trim());//AGREGADO POR SEGUNDO
                                            guardarAsignacionTicketSQLite(txt_ticket.getText().toString().trim(), txt_dni.getText().toString().trim());
                                        }else {
                                            Toast.makeText(PantallaAsignacionTickets.this, "DNI ya fue asignado", Toast.LENGTH_SHORT).show();
                                            cambiarColorBackgroundError();
                                            txt_dni.setText("");
                                            edt_codigo_dni.setText("");
                                        }

                                        txt_dni.setText("");
                                        edt_codigo_dni.requestFocus();
//                                        txt_ticket.setText("");
                                    }
//                                }

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run(){
                                        edt_codigo_dni.setText("");
                                        edt_codigo_dni.requestFocus();
                                    }
                                }, 300);

                            }
                        }else{
                            Toast.makeText(PantallaAsignacionTickets.this, "Primero debe leer un TICKET", Toast.LENGTH_SHORT).show();
                            cambiarColorBackgroundError();
                            txt_dni.setText("");
                            edt_codigo_dni.setText("");
                            edt_codigo_dni.requestFocus();
                        }

                    }else if(cod.length() == 5){
                        /*if(txt_ticket.getText().length()==5){
                            Toast.makeText(PantallaAsignacionTickets.this, "Aún no has asignado un DNI al ticket!", Toast.LENGTH_SHORT).show();
                            cambiarColorBackgroundError();
                        }else{*/
                            txt_ticket.setText(cod);
                            if(txt_dni.getText().toString().length() == 8 && txt_ticket.getText().toString().length() == 5){
                                Log.i("GRABADO", " OKI2");
                                if(objTP.buscarDni(txt_dni.getText().toString().trim()) == 0) {
                                    //guardarAsistenciaSQLite(MiAplicacionTareo.CODIGOTAREOCOSECHA.trim(), txt_dni.getText().toString().trim());//AGREGADO POR SEGUNDO
                                    guardarAsignacionTicketSQLite(txt_ticket.getText().toString().trim(), txt_dni.getText().toString().trim());
                                } else {
                                    Toast.makeText(PantallaAsignacionTickets.this, "DNI ya fue asignado", Toast.LENGTH_SHORT).show();
                                    cambiarColorBackgroundError();
                                    txt_dni.setText("");
                                    edt_codigo_dni.setText("");
                                    edt_codigo_dni.requestFocus();
                                }
                                txt_dni.setText("");
                                edt_codigo_dni.requestFocus();
//                                txt_ticket.setText("");
                            }
//                        }
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run(){
                                edt_codigo_dni.setText("");
                                edt_codigo_dni.requestFocus();
                            }
                        }, 300);
                    }
                    return true;
                }
                return false;
            }
        });

        ibtnLector.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(PantallaAsignacionTickets.this);
                //integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_39, IntentIntegrator.CODE_128, IntentIntegrator.QR_CODE);
                integrator.setPrompt("Enfoca Código");
                integrator.setCameraId(0);
                ///integrator.setOrientationLocked(true);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        ibtnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_dni.setText("");
                txt_ticket.setText("");
                edt_codigo_dni.setText("");
                edt_codigo_dni.requestFocus();
            }
        });

        edt_codigo_dni.requestFocus();
        cargarListaAsignacionesHoy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MiAplicacionTareo.cargar_preferenciasLogin(PantallaAsignacionTickets.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.act_menu_buscar_trabajador_asignacion, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /*if (TextUtils.isEmpty(newText)) {
                    lv_asignaciones.clearTextFilter();
                } else {
                    lv_asignaciones.setFilterText(newText);
                }*/
                filter.filter(newText);
                return true;
            }
        });

        return true;
    }

    // Es para el evento de la camara, esperando un resultado.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Escaneo Cancelado");
                Toast.makeText(this, "Escaneo Cancelado", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Escaneado");
                /*if(edt_codigo.getText().toString().length()<9){
                    edt_codigo.setText(result.getContents());
                }else{
                    edt_dni.setText(result.getContents());
                }*/
                String cod = result.getContents().trim();
                objTP = new TicketPersona();

                if(cod.length() ==8){
                    if (txt_ticket.getText() != ""){
                        if(MiAplicacionTareo.validarNumero(cod)){
                            txt_dni.setText(cod);
                            if(txt_dni.getText().toString().length() == 8 && txt_ticket.getText().toString().length() == 5){
                                Log.i("GRABADO", " OKI");
                                if(objTP.buscarDni(txt_dni.getText().toString().trim()) == 0){
                                    //guardarAsistenciaSQLite(MiAplicacionTareo.CODIGOTAREOCOSECHA.trim(), txt_dni.getText().toString().trim());//AGREGADO POR SEGUNDO
                                    guardarAsignacionTicketSQLite(txt_ticket.getText().toString().trim(), txt_dni.getText().toString().trim());
                                }else {
                                    Toast.makeText(PantallaAsignacionTickets.this, "DNI ya fue asignado", Toast.LENGTH_SHORT).show();
                                    cambiarColorBackgroundError();
                                    txt_dni.setText("");
                                    edt_codigo_dni.setText("");
                                }

                                txt_dni.setText("");
                                edt_codigo_dni.requestFocus();
                            }

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run(){
                                    edt_codigo_dni.setText("");
                                    edt_codigo_dni.requestFocus();
                                }
                            }, 300);

                        }
                    }else{
                        Toast.makeText(PantallaAsignacionTickets.this, "Primero debe leer un TICKET", Toast.LENGTH_SHORT).show();
                        cambiarColorBackgroundError();
                        txt_dni.setText("");
                        edt_codigo_dni.setText("");
                        edt_codigo_dni.requestFocus();
                    }

                }else if(cod.length() == 5){
                    txt_ticket.setText(cod);
                    if(txt_dni.getText().toString().length() == 8 && txt_ticket.getText().toString().length() == 5){
                        Log.i("GRABADO", " OKI2");
                        if(objTP.buscarDni(txt_dni.getText().toString().trim()) == 0) {
                            guardarAsignacionTicketSQLite(txt_ticket.getText().toString().trim(), txt_dni.getText().toString().trim());
                        } else {
                            Toast.makeText(PantallaAsignacionTickets.this, "DNI ya fue asignado", Toast.LENGTH_SHORT).show();
                            cambiarColorBackgroundError();
                            txt_dni.setText("");
                            edt_codigo_dni.setText("");
                            edt_codigo_dni.requestFocus();
                        }
                        txt_dni.setText("");
                        edt_codigo_dni.requestFocus();
                    }
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run(){
                            edt_codigo_dni.setText("");
                            edt_codigo_dni.requestFocus();
                        }
                    }, 300);
                }




                /*if(cod.length() ==8){
                    if(MiAplicacionTareo.validarNumero(cod)){
                        txt_dni.setText(cod);
                        if(txt_dni.getText().toString().length() == 8 && txt_ticket.getText().toString().length() == 9){
                            Log.i("GRABADO", " OKI");
                            //guardarAsistenciaSQLite(MiAplicacionTareo.CODIGOTAREOCOSECHA.trim(), txt_dni.getText().toString().trim());
                            guardarAsignacionTicketSQLite(txt_ticket.getText().toString().trim().substring(1,7), txt_dni.getText().toString().trim());
                            txt_dni.setText("");
                            txt_ticket.setText("");
                        }
                    }
                }else if(cod.length() == 9){
                    if(String.valueOf(cod.charAt(0)).equalsIgnoreCase(caracter)){
                        txt_ticket.setText(cod);
                        if(txt_dni.getText().toString().length() == 8 && txt_ticket.getText().toString().length() == 9){
                            Log.i("GRABADO", " OKI2");
                            //guardarAsistenciaSQLite(MiAplicacionTareo.CODIGOTAREOCOSECHA.trim(), txt_dni.getText().toString().trim());
                            guardarAsignacionTicketSQLite(txt_ticket.getText().toString().trim().substring(1,7), txt_dni.getText().toString().trim());
                            txt_dni.setText("");
                            txt_ticket.setText("");
                        }
                    }
                }*/

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void cambiarColorBackgroundError(){
        rl_asignacion.setBackgroundColor(Color.parseColor("#ff0000"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_asignacion.setBackgroundColor(Color.parseColor("#00ffffff"));
            }
        }, 700);
    }

    public void cambiarColorBackgroundExito(){
        rl_asignacion.setBackgroundColor(Color.parseColor("#6dff4d"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_asignacion.setBackgroundColor(Color.parseColor("#00ffffff"));
            }
        }, 700);
    }

    private void cargarListaAsignacionesHoy(){
        TicketPersona obj = new TicketPersona();
        obj.cargarListaTicketPersonaAsignados();
        txt_conteo.setText(""+obj.conteoAsignacionesHoy());
        adapter.notifyDataSetChanged();
    }

    private void guardarAsignacionTicketSQLite(String IDTICKET, String IDPERSONALGENERAL){
        TicketPersona objTP = new TicketPersona();
        //Long r = objTP.agregarTicketPersonaSQLite(IDTICKET, IDTAREO, IDPERSONALGENERAL);
        Long r = objTP.agregarTicketPersonaSQLiteSimple(IDTICKET, IDPERSONALGENERAL, MiAplicacionTareo.IDUSUARIO);
        Log.i("LONG_IDPERSONAL", ""+r);
        if(r == -1){//es que no se grabo!
            cambiarColorBackgroundError();
            /*edt_dni.setText("");
            edt_codigo.setText("");
            edt_codigo.requestFocus();*/
            edt_codigo_dni.setText("");
            edt_codigo_dni.requestFocus();

            //esperaSegundosYAbrirLectorCamara(3);
            //Toast.makeText(PantallaAsignacionTickets.this, "Ticket ya ha sido asignado anteriormente.", Toast.LENGTH_SHORT).show();
        }else{//Se grabo correctamente!
            cambiarColorBackgroundExito();
            /*edt_dni.setText("");
            edt_codigo.setText("");
            edt_codigo.requestFocus();*/
            edt_codigo_dni.setText("");
            edt_codigo_dni.requestFocus();

            /*objTP = new TicketPersona();
            txt_conteo.setText(""+objTP.conteoAsignacionesHoy());*/

            DNI_ULTIMO_AGREGADO=IDPERSONALGENERAL;
            PINTAR_ULTIMO_AGREGADO_LISTA=true;
            cargarListaAsignacionesHoy();
            //new TareaCargarLista().execute();

            //esperaSegundosYAbrirLectorCamara(3);
            //Toast.makeText(PantallaAsignacionTickets.this, "Grabado!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
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
    public void dialogObservado(String DNI){
        final PrettyDialog pDialog = new PrettyDialog(this);
        pDialog.setCancelable(false);
        pDialog
                .setIconTint(R.color.pdlg_color_red)
                .setTitle("OBSERVADO")
                .setMessage("Personal con DNI "+ DNI +" está en la lista de OBSERVADOS (" + Asistencia.MOTIVO_OBSERVADO + ")")
                .addButton(
                        "ACEPTAR",
                        R.color.pdlg_color_white,
                        R.color.colorPrimary,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
//                                esperaSegundosYAbrirLectorCamara(3);
                                pDialog.dismiss();
                            }
                        }
                )
                .setAnimationEnabled(true)
                .show();
    }

    private void enviarCorreoObservado(final String dniObservado, final String nombreObservado, final String nombreCaporal, final String motivo){
        RequestQueue request;
        request = Volley.newRequestQueue(this);
        String url = MiAplicacionTareo.URL_SERVICE + "enviarCorreoArandano";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                Log.i("Response", response);
                try {
                    JSONObject json = new JSONObject(response);
                    String resultado = json.getString("resultado");
                    Log.i("RESULTADO ENVIO: ", resultado);
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("ERROR-VOLLEY", "ERROR");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("dniObservado", dniObservado);
                params.put("nombreObservado", nombreObservado);
                params.put("nombreCaporal", nombreCaporal);
                params.put("motivo", motivo);
                return params;
            }
        };
        request.add(postRequest);
    }

    private class TareaEnviarSMS extends AsyncTask<String, Void, Boolean> {
        //ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String numero = params[0];
            String mensaje = params[1];
            try {
                SmsManager sms;
                sms = SmsManager.getDefault();
                sms.sendTextMessage(numero, null, mensaje, null, null);
                Log.i("-------------", "-------------");
                Log.i("SMS:", mensaje);
                Log.i("SMS Enviado", ""+ numero);
                return true;
            } catch (Exception e) {
                Log.i("-------------", "-------------");
                Log.i("SMS:", mensaje);
                Log.i("SMS NO Enviado", ""+ numero);
                return false;
                //e.printStackTrace();
            }
        }

        protected void onPostExecute(Boolean result) {
            if(result){
                //enviado
            }else{
                //NOENVIADO
            }
        }
    }

    private void guardarAsistenciaSQLite(String IDTAREO, String DNI){
        Asistencia objA = new Asistencia();
        Long r = objA.agregarAsistenciaSQLite(IDTAREO, DNI);
        Log.i("LONG_ASISTENCIA", ""+r);
        if(r == 500){//OBSERVADO
            //Toast.makeText(PantallaAsistenciaPersonal.this, "OBSERVADO, NO SE ADMITE PERSONAL OBSERVADO!! - " + DNI, Toast.LENGTH_SHORT).show();
            dialogObservado(DNI);
            cambiarColorBackgroundError();

            Administrador objAdm = new Administrador();
            objAdm.cargarListaNumerosAdministrador();
            //enviarCorreoObservado(DNI, Asistencia.NOMBREOBSERVADO, MiApplicacionACP.NOMBRES, "observado");
            if(Asistencia.MOTIVO_OBSERVADO.trim().length() != 0){
                enviarCorreoObservado(DNI, Asistencia.NOMBREOBSERVADO, MiAplicacionTareo.NOMBRES, "observado");
                for(int i=0; i<Administrador.listaNumerosAdministradores.size(); i++){
                    new PantallaAsignacionTickets.TareaEnviarSMS().execute(Administrador.listaNumerosAdministradores.get(i).getNumero(), "(OBS. POR "+ Asistencia.MOTIVO_OBSERVADO+") "+ Asistencia.NOMBREOBSERVADO + " - "+DNI+", AUX: " + MiAplicacionTareo.NOMBRES);
                }
                //Envia
            }

        }else if(r == -1){//es que no se grabo!
            cambiarColorBackgroundError();
//            esperaSegundosYAbrirLectorCamara(3);
            //Toast.makeText(PantallaAsistenciaPersonal.this, "Trabajador ya ha sido asignado a TAREO.", Toast.LENGTH_SHORT).show();
        }else{//Se grabo correctamente!
            cambiarColorBackgroundExito();
//            esperaSegundosYAbrirLectorCamara(3);
//            visualizarcantidadPersonas(IDTAREO);
//            cargarListaTrabajadores(IDTAREO);
        }
    }

}
