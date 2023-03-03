package com.acpagro.tareopalta;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.acpagro.tareopalta.adapters.AdapterListaTrabajadoresAsistenciaTareo;
import com.acpagro.tareopalta.adapters.AdapterListaTrabajadoresPretareo;
import com.acpagro.tareopalta.modelo.Administrador;
import com.acpagro.tareopalta.modelo.Asistencia;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.Tareo;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class PretareoPersonal extends AppCompatActivity {

    private EditText edt_dni,editText, edt_codigo;
    private TextView txt_consumidor, txt_actividad_labor, fecha_hora, txt_cantidad_personas;
    private Button btn_salir;
    private ListView lv_trabajadores_asistencia;
    private RelativeLayout rl_asistencia;
    private ImageButton ibtnLector;
    private String IDTAREO, CONSUMIDOR, ACTIVIDAD, LABOR, FECHAYHORA, IDCONSUMIDOR;

    AdapterListaTrabajadoresPretareo adapter;

    //VARIABLES DIALOG IMPORTAR
    CharSequence[] listaTareosDisponiblesParaImportarAsistencia;
    int selected = 0;
    int buffKey = 0;

    public static String MODULO_SELECCIONADO = "";
    private String actividad_labor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pretareo_personal);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        //Para que apareca la flecha hacia atrás
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Asistencia de Personal");

        Bundle parametros = this.getIntent().getExtras();
        IDTAREO = parametros.getString("p_id_tareo");//IDTAREO
        CONSUMIDOR = parametros.getString("p_consumidor");//CONSUMIDOR
        IDCONSUMIDOR = parametros.getString("p_idconsumidor");//IDCONSUMIDOR
        ACTIVIDAD = parametros.getString("p_actividad");//ACT
        LABOR = parametros.getString("p_labor");//LABOR
        FECHAYHORA = parametros.getString("p_fechaYhora");//FYH
        actividad_labor = parametros.getString("p_idactividad")+"-"+parametros.getString("p_idlabor");//p_idactividad


        edt_dni = (EditText)findViewById(R.id.edt_dni);
        editText = (EditText)findViewById(R.id.editText);
        edt_codigo = (EditText)findViewById(R.id.edt_codigo);
        txt_consumidor = (TextView)findViewById(R.id.txt_consumidor);
        txt_actividad_labor = (TextView)findViewById(R.id.txt_actividad_labor);
        ibtnLector = (ImageButton)findViewById(R.id.ibtnLector);
        fecha_hora = (TextView)findViewById(R.id.fecha_hora);
        rl_asistencia = (RelativeLayout)findViewById(R.id.rl_asistencia);
        lv_trabajadores_asistencia = (ListView)findViewById(R.id.lv_trabajadores_asistencia);
        txt_cantidad_personas = (TextView)findViewById(R.id.txt_cantidad_personas);

        txt_consumidor.setText(CONSUMIDOR);
        txt_actividad_labor.setText(ACTIVIDAD + " - [" + LABOR + "]");
        fecha_hora.setText(FECHAYHORA);

        btn_salir = (Button)findViewById(R.id.btn_salir);

        //Log.i("ACTIVIDAD_LABORA", ""+actividad_labor);

        btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /*edt_dni.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    String cod = edt_dni.getText().toString().trim();
                    if(cod.length() ==8){
                        if(MiAplicacionTareo.validarNumero(cod)){
                            grabar();
                        }

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run(){
                                edt_dni.setText("");
                                edt_dni.requestFocus();
                            }
                        }, 300);

                    }

                    return true;
                }
                return false;
            }
        });*/


        edt_dni.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(String.valueOf(charSequence).length() == 8){
                    if(validarNumero(edt_dni.getText().toString())){//Si es numero el dni
                        grabar();
                        edt_dni.requestFocus();
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
                IntentIntegrator integrator = new IntentIntegrator(PretareoPersonal.this);
                //integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_39, IntentIntegrator.CODE_128, IntentIntegrator.QR_CODE);
                integrator.setPrompt("Enfoca - Código DNI");
                integrator.setCameraId(0);
                ///integrator.setOrientationLocked(true);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
        //Asistencia.listaAsistenciaSQLite.clear();
        adapter = new AdapterListaTrabajadoresPretareo(PretareoPersonal.this);
        lv_trabajadores_asistencia.setAdapter(adapter);//Se llena el ListView
        //adapter.notifyDataSetChanged();
        //new TareaCargarListaTrabajadoresAsisPorTareo().execute(IDTAREO);

        //Toast.makeText(PantallaAsistenciaPersonal.this, ""+CONSUMIDOR, Toast.LENGTH_LONG).show();

        String cadenaDondeBuscar = CONSUMIDOR;
        String loQueQuieroBuscar = "M5";
        String[] palabras = loQueQuieroBuscar.split("\\W+");
        for (String palabra : palabras) {
            if (cadenaDondeBuscar.contains(palabra)) {
                //Toast.makeText(PantallaAsistenciaPersonal.this, "ENCONTRO "+palabra, Toast.LENGTH_LONG).show();
                MODULO_SELECCIONADO = "MODULO_05";
            }else{
                MODULO_SELECCIONADO = "MODULO_04";
            }
        }

        /*if(esLimpieza()){
            edt_codigo.setVisibility(View.VISIBLE);
            guardarAsistenciaAuxiliarLimpiezaSQLite(IDTAREO, MiApplicacionACP.DNIUSUARIO);
        }else{
            edt_codigo.setVisibility(View.GONE);
        }*/


        visualizarcantidadPersonas(IDTAREO);
        cargarListaTrabajadores(IDTAREO);

    }

    public boolean validarNumero(String cadena) {
        if (cadena.matches("[0-9]*")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean esLimpieza(){
        if(actividad_labor.trim().equalsIgnoreCase("A07-007")){//Cosecha- Limpieza
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_asistencia, menu);
        return true;
    }

    public void cargarListaTrabajadores(String idTareo){
        Asistencia objA = new Asistencia();
        objA.obtenerListaTrabajadoresAsistenciaPorTareo(idTareo, "4", "DESC");
        adapter.notifyDataSetChanged();
    }

    private class TareaCargarListaTrabajadoresAsisPorTareo extends AsyncTask<String, Void, Boolean> {
        //ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String idTareo = params[0];
            Asistencia objA = new Asistencia();
            return objA.obtenerListaTrabajadoresAsistenciaPorTareo(idTareo, "4", "DESC");//ORDEN POR FECHA DESC
        }

        protected void onPostExecute(Boolean result) {
            if(result){
                adapter.notifyDataSetChanged();
            }else{
                adapter.notifyDataSetChanged();
            }

        }
    }

    private void visualizarcantidadPersonas(String idTareo){
        Asistencia asistencia = new Asistencia();
        txt_cantidad_personas.setText("Personal Registrado: " + asistencia.cantidadPersonalxTareo(idTareo));
    }

    private class TareaCargarListaTareosQueAsistenciaPuedeImportar extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String idTareo = params[0];
            Tareo obj = new Tareo();
            obj.cargarListaTareoImportarAsistenciaSQLite(IDTAREO);
            return null;
        }

        protected void onPostExecute(Void result) {
            if(Tareo.listaStrTareosImportar.size() > 0){
                dialogTareosAsistenciaImportar();
            }else{
                Snackbar.make(rl_asistencia, "No se encontraron asistencias el día de hoy para importar!", Snackbar.LENGTH_LONG).show();
            }

        }
    }

    private class TareaImportarAsistenciaAOtroTareo extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String idTareoInicio = params[0];
            String idTareoFinal = params[1];
            Asistencia obj = new Asistencia();
            obj.importarListaTrabajadoresAsistenciaAOtroTareo(idTareoInicio, idTareoFinal);
            return null;
        }

        protected void onPostExecute(Void result) {
            Toast.makeText(PretareoPersonal.this, "Se ha importado la Asistencia seleccionada!", Toast.LENGTH_SHORT).show();
            new TareaCargarListaTrabajadoresAsisPorTareo().execute(IDTAREO);
        }
    }

    public void grabarAsistenciaYAsignacion(String IDTAREO){
        String dni = edt_dni.getText().toString().trim();
        String codigo = edt_codigo.getText().toString().trim();

        if(codigo.length() == 9 && (String.valueOf(codigo.charAt(0)).equalsIgnoreCase("L"))){
            if(dni.length() == 8){
                //Guardar Asistencia y Asignacion
                Asistencia objA = new Asistencia();
                Long r = objA.agregarAsistenciaSQLite(IDTAREO, dni);
                Log.i("LONG_ASISTENCIA", ""+r);
                if(r == 500){//OBSERVADO
                    dialogObservado(dni);

                    Administrador objAdm = new Administrador();
                    objAdm.cargarListaNumerosAdministrador();
                    if(Asistencia.MOTIVO_OBSERVADO.trim().length() != 0){
                        enviarCorreoObservado(dni, Asistencia.NOMBREOBSERVADO, MiAplicacionTareo.NOMBRES, "observado");
                        for(int i=0; i<Administrador.listaNumerosAdministradores.size(); i++){
                            new TareaEnviarSMS().execute(Administrador.listaNumerosAdministradores.get(i).getNumero(), "(OBS. POR "+ Asistencia.MOTIVO_OBSERVADO+") "+ Asistencia.NOMBREOBSERVADO + " - "+dni+", AUX: " + MiAplicacionTareo.NOMBRES);
                        }
                    }

                    cambiarColorBackgroundError();
                    edt_codigo.setText("");
                    edt_dni.setText("");
                    edt_dni.requestFocus();

                }else if(r == 400){//CAMBIO DE MODULO
                    dialogCambio(dni);

                    Administrador objAdm = new Administrador();
                    objAdm.cargarListaNumerosAdministrador();
                    enviarCorreoObservado(dni, Asistencia.NOMBREOBSERVADO, MiAplicacionTareo.NOMBRES, "cambio");
                    for(int i=0; i<Administrador.listaNumerosAdministradores.size(); i++){
                        new TareaEnviarSMS().execute(Administrador.listaNumerosAdministradores.get(i).getNumero(), "(CAMBIO DE MODULO) "+ Asistencia.NOMBREOBSERVADO + " - "+dni+" NO ESTA AUTORIZADO PARA TRABAJAR EN EL "+ MODULO_SELECCIONADO +", ENVIADO POR: " + MiAplicacionTareo.NOMBRES);
                    }

                    if(r == -1){//es que no se grabo!
                        objA.eliminarAsistenciaSQLite(IDTAREO, dni);
                        cambiarColorBackgroundError();
                        edt_codigo.setText("");
                        edt_dni.setText("");
                        edt_dni.requestFocus();

                    }else{//Se grabo correctamente!
                        cambiarColorBackgroundExito();
                        edt_codigo.setText("");
                        edt_dni.setText("");
                        edt_dni.requestFocus();
                    }
                    visualizarcantidadPersonas(IDTAREO);
                    cargarListaTrabajadores(IDTAREO);
                }else if(r == -1){//es que no se grabo!
                    cambiarColorBackgroundError();
                    edt_codigo.setText("");
                    edt_dni.setText("");
                    edt_dni.requestFocus();
                }else{//Se grabo correctamente!

                    if(r == -1){//es que no se grabo!
                        objA.eliminarAsistenciaSQLite(IDTAREO, dni);
                        cambiarColorBackgroundError();
                        edt_codigo.setText("");
                        edt_dni.setText("");
                        edt_dni.requestFocus();

                    }else{//Se grabo correctamente!
                        cambiarColorBackgroundExito();
                        edt_codigo.setText("");
                        edt_dni.setText("");
                        edt_dni.requestFocus();
                    }
                    visualizarcantidadPersonas(IDTAREO);
                    cargarListaTrabajadores(IDTAREO);
                }
            }
        }
    }

    public void grabar(){
        String dni = edt_dni.getText().toString().trim();
        if(dni.length() == 8 && MiAplicacionTareo.esNumero(dni)){
            guardarAsistenciaSQLite(IDTAREO, dni);
        }else{
            edt_dni.setText("");
            edt_dni.requestFocus(R.id.edt_dni);
            Toast.makeText(PretareoPersonal.this, "DNI INCORRECTO", Toast.LENGTH_SHORT).show();
            cambiarColorBackgroundError();
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
            edt_dni.setText("");
            edt_dni.requestFocus();

            Administrador objAdm = new Administrador();
            objAdm.cargarListaNumerosAdministrador();
            //enviarCorreoObservado(DNI, Asistencia.NOMBREOBSERVADO, MiApplicacionACP.NOMBRES, "observado");
            if(Asistencia.MOTIVO_OBSERVADO.trim().length() != 0){
                enviarCorreoObservado(DNI, Asistencia.NOMBREOBSERVADO, MiAplicacionTareo.NOMBRES, "observado");
                for(int i=0; i<Administrador.listaNumerosAdministradores.size(); i++){
                    new TareaEnviarSMS().execute(Administrador.listaNumerosAdministradores.get(i).getNumero(), "(OBS. POR "+ Asistencia.MOTIVO_OBSERVADO+") "+ Asistencia.NOMBREOBSERVADO + " - "+DNI+", AUX: " + MiAplicacionTareo.NOMBRES);
                }
                //Envia
            }

        }else if(r == 400){//CAMBIO DE MODULO
            dialogCambio(DNI);
            cambiarColorBackgroundExito();
            edt_dni.setText("");
            edt_dni.requestFocus();

            Administrador objAdm = new Administrador();
            objAdm.cargarListaNumerosAdministrador();
            enviarCorreoObservado(DNI, Asistencia.NOMBREOBSERVADO, MiAplicacionTareo.NOMBRES, "cambio");
            for(int i=0; i<Administrador.listaNumerosAdministradores.size(); i++){
                new TareaEnviarSMS().execute(Administrador.listaNumerosAdministradores.get(i).getNumero(), "(CAMBIO DE MODULO) "+ Asistencia.NOMBREOBSERVADO + " - "+DNI+" NO ESTA AUTORIZADO PARA TRABAJAR EN EL "+ MODULO_SELECCIONADO +", ENVIADO POR: " + MiAplicacionTareo.NOMBRES);
            }
            visualizarcantidadPersonas(IDTAREO);
            cargarListaTrabajadores(IDTAREO);
        }else if(r == -1){//es que no se grabo!
            cambiarColorBackgroundError();
            edt_dni.setText("");
            edt_dni.requestFocus();
//            esperaSegundosYAbrirLectorCamara(3);
            //Toast.makeText(PantallaAsistenciaPersonal.this, "Trabajador ya ha sido asignado a TAREO.", Toast.LENGTH_SHORT).show();
        }else{//Se grabo correctamente!
            cambiarColorBackgroundExito();
            edt_dni.setText("");
            edt_dni.requestFocus();
//            esperaSegundosYAbrirLectorCamara(3);
            visualizarcantidadPersonas(IDTAREO);
            cargarListaTrabajadores(IDTAREO);
        }
    }


    private void guardarAsistenciaAuxiliarLimpiezaSQLite(String IDTAREO, String DNI){
        Asistencia objA = new Asistencia();
        Long r = objA.agregarAsistenciaSQLite(IDTAREO, DNI);
        Log.i("LONG_ASISTENCIA", ""+r);
        edt_dni.setText("");
        edt_dni.requestFocus();
    }


    public void grabarAsignacionTicket(){
        String codigo = edt_codigo.getText().toString().trim();
        String dni = edt_dni.getText().toString().trim();
        if(codigo.length() == 9 && (String.valueOf(codigo.charAt(0)).equalsIgnoreCase("L"))){
            if(dni.length() == 8){
                Log.i("CODIGOOOO", codigo.substring(1,7));
                guardarAsignacionTicketSQLite(codigo.substring(1,7), dni);
            }else{
                edt_dni.setText("");
                edt_dni.requestFocus(R.id.edt_dni);
                Toast.makeText(PretareoPersonal.this, "DNI INCORRECTO", Toast.LENGTH_SHORT).show();
                cambiarColorBackgroundError();
            }
        }else{
            edt_codigo.setText("");
            edt_codigo.requestFocus(R.id.edt_codigo);
            cambiarColorBackgroundError();
            Toast.makeText(PretareoPersonal.this, "CÓDIGO INCORRECTO", Toast.LENGTH_SHORT).show();
        }
    }


    private void guardarAsignacionTicketSQLite(String IDTICKET, String IDPERSONALGENERAL){
        /*TicketPersona objTP = new TicketPersona();
        Long r = objTP.agregarTicketPersonaSQLite(IDTICKET, IDTAREO, IDPERSONALGENERAL);
        Log.i("LONG_IDPERSONAL", ""+r);
        if(r == -1){//es que no se grabo!
            cambiarColorBackgroundError();
            edt_dni.setText("");
            edt_codigo.setText("");
            edt_codigo.requestFocus();
        }else{//Se grabo correctamente!
            cambiarColorBackgroundExito();
            edt_dni.setText("");
            edt_codigo.setText("");
            edt_codigo.requestFocus();
        }*/
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

    public void dialogCambio(String DNI){
        final PrettyDialog pDialog = new PrettyDialog(this);
        pDialog.setCancelable(false);
        pDialog
                .setIconTint(R.color.pdlg_color_red)
                .setTitle("CAMBIO NO AUTORIZADO")
                .setMessage("Personal con DNI "+ DNI +" no está autorizado para trabajar en el " +MODULO_SELECCIONADO)
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

    public void cambiarColorBackgroundError(){
        android.os.Vibrator v = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
        long [] patron = {0, 300, 300, 300};
        v.vibrate(patron, -1);

        rl_asistencia.setBackgroundColor(Color.parseColor("#ff0000"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_asistencia.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }, 700);
    }

    public void cambiarColorBackgroundExito(){
        android.os.Vibrator v = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(400);

        rl_asistencia.setBackgroundColor(Color.parseColor("#6dff4d"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_asistencia.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }, 700);
    }

    public void esperaSegundosYAbrirLectorCamara(int segundos){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run(){
                IntentIntegrator integrator = new IntentIntegrator(PretareoPersonal.this);
                //integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_39, IntentIntegrator.CODE_128, IntentIntegrator.QR_CODE);
                integrator.setPrompt("Enfoca - Código DNI");
                integrator.setCameraId(0);
                ///integrator.setOrientationLocked(true);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        }, segundos * 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Escaneo Cancelado");
                Toast.makeText(this, "Escaneo Cancelado", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Escaneado");
                /*edt_dni.setText(obtenerDniDeAcuerdoACodigo(result.getFormatName(), result.getContents()));
                Log.i("TIPO CODIGO",obtenerDniDeAcuerdoACodigo(result.getFormatName(), result.getContents()));*/

                if(edt_dni.getText().toString().length()<8){
                    edt_dni.setText(result.getContents());
                }else{
                    edt_codigo.setText(result.getContents());
                }


            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public String obtenerDniDeAcuerdoACodigo(String TIPO_CODE, String texto){
        String nuevo_dni = "";
        if(TIPO_CODE.equalsIgnoreCase("PDF_417")){
            nuevo_dni = texto.substring(2, 10);

            int letra = 0;//pos de ultima letra encontrada
            int espacio = 0;//pos de ultimo espacio
            int pos = 0;//contador
            int posL = 0;//contador
            int primerEspacio = 0;
            int segundoEspacio = 0;
            int tercerEspacio = 0;
            int primerLetra = 0;
            int segundoLetra = 0;
            int tercerLetra = 0;
            for(int i = 0; i< texto.length(); i++){
                if(texto.charAt(i) == ' '){
                    //Log.i("TIPO POS ESPACIO", ""+i);
                    if(i-1 == letra){
                        //Log.i("TIPO POS ESPACIO", ""+i);
                        if(pos == 0){
                            primerEspacio = i;
                        }else if(pos == 1){
                            segundoEspacio = i;
                        }else if(pos == 2){
                            tercerEspacio = i;
                        }
                        pos++;
                    }
                    espacio = i;
                }else{
                    if(i-1 == espacio){
                        //Log.i("TIPO POS LETRA__", ""+i);
                        if(posL == 0){
                            primerLetra = i;
                        }else if(posL == 1){
                            segundoLetra = i;
                        }else if(posL == 2){
                            tercerLetra = i;
                        }
                        posL++;
                    }
                    //Log.i("TIPO POS LETRA__", ""+i);
                    letra = i;
                }
            }
            //String[] output = texto.split(" ");
            //Log.i("SPLIT", output[0] + "/" + output[1] + "/" + output[2]);
            /*Log.i("APATERNO", texto.substring(10, primerEspacio));
            Log.i("AMATERNO", texto.substring(segundoLetra, segundoEspacio));
            Log.i("NOMBRE", texto.substring(tercerLetra, tercerEspacio));*/

            Toast.makeText(PretareoPersonal.this, texto.substring(10, primerEspacio) + " " + texto.substring(segundoLetra, segundoEspacio) + ", "+ texto.substring(tercerLetra, tercerEspacio) + " || "+ nuevo_dni, Toast.LENGTH_LONG).show();

        }else{
            nuevo_dni = texto;
        }
        return nuevo_dni;
    }

    private void reproducirPitido(){
        MediaPlayer mp = MediaPlayer.create(this, R.raw.pitido_scanner);
        mp.start();
    }

    private void dialogTareosAsistenciaImportar(){
        listaTareosDisponiblesParaImportarAsistencia = Tareo.listaStrTareosImportar.toArray(new CharSequence[Tareo.listaStrTareosImportar.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(PretareoPersonal.this);
        builder.setTitle("Elige Tareo con Asistencia a Importar!");
        builder.setSingleChoiceItems(listaTareosDisponiblesParaImportarAsistencia, selected,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {
                        //set to buffKey instead of selected
                        //(when cancel not save to selected)
                        buffKey = which;
                    }
                })
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //String id_tareo = Tareo.listaObjTareosImportar
                                //id_departamento = Departamento.listaDepartamentos.get(buffKey).getId_departamento();
                                //txtDepartamento.setText("Departamento: " + listaTareosDisponiblesParaImportarAsistencia[buffKey]);
                                new TareaImportarAsistenciaAOtroTareo().execute(Tareo.listaObjTareosImportar.get(buffKey).getIDTAREO(), IDTAREO);
                                selected = buffKey;
                                //new TareaCargarProvinciasPorDepartamento().execute(Departamento.listaDepartamentos.get(buffKey).getId_departamento());
                            }
                        }
                )
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener()
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


    //Para volver atras
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                finish();
                return true;
            case R.id.action_importar_asistencia: //hago un case por si en un futuro agrego mas opciones
                /*if(esLimpieza()){
                    Toast.makeText(PantallaAsistenciaPersonal.this, "Labor LIMPIEZA: Debe asignar manualmente cada trabajador a un ticket.", Toast.LENGTH_SHORT).show();
                }else{
                    new TareaCargarListaTareosQueAsistenciaPuedeImportar().execute(IDTAREO);
                }*/
                new TareaCargarListaTareosQueAsistenciaPuedeImportar().execute(IDTAREO);
                return true;
            /*case R.id.action_eliminar_datos_asistencia: //hago un case por si en un futuro agrego mas opciones
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
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
}
