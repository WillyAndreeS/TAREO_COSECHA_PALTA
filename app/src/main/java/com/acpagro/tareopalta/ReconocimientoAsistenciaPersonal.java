    package com.acpagro.tareopalta;

    import android.app.ProgressDialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
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
    import com.acpagro.tareopalta.modelo.Administrador;
    import com.acpagro.tareopalta.modelo.Asistencia;
    import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
    import com.acpagro.tareopalta.modelo.Reconocimiento;
    import com.acpagro.tareopalta.modelo.SimilarityClassifier;
    import com.acpagro.tareopalta.modelo.Tareo;
    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;
    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.google.android.material.snackbar.Snackbar;
    import com.google.zxing.integration.android.IntentIntegrator;
    import com.google.zxing.integration.android.IntentResult;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.util.HashMap;
    import java.util.Map;

    import libs.mjn.prettydialog.PrettyDialog;
    import libs.mjn.prettydialog.PrettyDialogCallback;

    public class ReconocimientoAsistenciaPersonal extends AppCompatActivity {
        private EditText editText, edt_codigo;
        private TextView txt_consumidor, txt_actividad_labor, fecha_hora;
        public static TextView txt_cantidad_personas;
        private Button btn_salir;
        private ListView lv_trabajadores_asistencia;
        private RelativeLayout rl_asistencia;
        //private ImageButton ibtnLector;
        private String IDTAREO, CONSUMIDOR, ACTIVIDAD, LABOR, FECHAYHORA, IDCONSUMIDOR;
        private FloatingActionButton fab_camara;
        private final static int GET_FACE = 1;
        public static HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>(); //saved Faces
        private ImageButton imb_reconocimiento;

        AdapterListaTrabajadoresAsistenciaTareo adapter;

        //VARIABLES DIALOG IMPORTAR
        CharSequence[] listaTareosDisponiblesParaImportarAsistencia;
        int selected = 0;
        int buffKey = 0;

        public static String MODULO_SELECCIONADO = "";
        private String actividad_labor;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_reconocimiento_asistencia_personal);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            //Para que apareca la flecha hacia atr??s
            androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Asist. Rec. Facial");

            Bundle parametros = this.getIntent().getExtras();
            if(parametros!= null){
                IDTAREO = parametros.getString("p_id_tareo");//IDTAREO
                CONSUMIDOR = parametros.getString("p_consumidor");//CONSUMIDOR
                IDCONSUMIDOR = parametros.getString("p_idconsumidor");//IDCONSUMIDOR
                ACTIVIDAD = parametros.getString("p_actividad");//ACT
                LABOR = parametros.getString("p_labor");//LABOR
                FECHAYHORA = parametros.getString("p_fechaYhora");//FYH
                actividad_labor = parametros.getString("p_idactividad")+"-"+parametros.getString("p_idlabor");//p_idactividad
            }else{
                finish();
                Toast.makeText(this, "Int??ntalo nuevamente e ingresa al Tareo para a??adir asistencia. ", Toast.LENGTH_SHORT).show();
            }


            //edt_dni = (EditText)findViewById(R.id.edt_dni);
            //fab_camara = findViewById(R.id.fab_camara);
            imb_reconocimiento = findViewById(R.id.imb_reconocimiento);
            editText = (EditText)findViewById(R.id.editText);
            edt_codigo = (EditText)findViewById(R.id.edt_codigo);
            txt_consumidor = (TextView)findViewById(R.id.txt_consumidor);
            txt_actividad_labor = (TextView)findViewById(R.id.txt_actividad_labor);
            //ibtnLector = (ImageButton)findViewById(R.id.ibtnLector);
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

            imb_reconocimiento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(ReconocimientoAsistenciaPersonal.this, ReconocimientoCamara.class), GET_FACE);
                }
            });



            adapter = new AdapterListaTrabajadoresAsistenciaTareo(ReconocimientoAsistenciaPersonal.this);
            lv_trabajadores_asistencia.setAdapter(adapter);//Se llena el ListView

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

            visualizarcantidadPersonas(IDTAREO);
            cargarListaTrabajadores(IDTAREO);
            new TareaCargarRostrosGuardadosSQLite().execute();//SQLite Sincronizados

        }

        @Override
        protected void onResume() {
            super.onResume();
            MiAplicacionTareo.cargar_preferenciasLogin(ReconocimientoAsistenciaPersonal.this);
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
            //txt_cantidad_personas.setText("Personal Registrado: " + Asistencia.listaAsistenciaSQLite.size());
            txt_cantidad_personas.setText("" + Asistencia.listaAsistenciaSQLite.size());
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
            //txt_cantidad_personas.setText("Personal Registrado: " + asistencia.cantidadPersonalxTareo(idTareo));
            txt_cantidad_personas.setText("" + asistencia.cantidadPersonalxTareo(idTareo));
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
                    Snackbar.make(rl_asistencia, "No se encontraron asistencias el d??a de hoy para importar!", Snackbar.LENGTH_LONG).show();
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
                Toast.makeText(ReconocimientoAsistenciaPersonal.this, "Se ha importado la Asistencia seleccionada!", Toast.LENGTH_SHORT).show();
                new TareaCargarListaTrabajadoresAsisPorTareo().execute(IDTAREO);
            }
        }

        public void grabar(String dni){
            if(dni.length() == 8 && MiAplicacionTareo.esNumero(dni)){
                if(IDTAREO == null || IDTAREO.trim().length()==0 || IDTAREO.trim().equalsIgnoreCase("-")){
                    finish();
                    Toast.makeText(ReconocimientoAsistenciaPersonal.this, "No se carg?? correctamente el C??DIGO DE TAREO, por favor ingresa nuevamente al TAREO.", Toast.LENGTH_SHORT).show();
                }else{
                    guardarAsistenciaSQLite(IDTAREO, dni);
                }
                //cargar_preferencia_idtareo
                //guardarAsistenciaSQLite(cargar_preferencia_idtareo(), dni);
            }else{
                Toast.makeText(ReconocimientoAsistenciaPersonal.this, "DNI INCORRECTO", Toast.LENGTH_SHORT).show();
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
                esperaSegundosYAbrirLectorCamara(3);
                //Toast.makeText(PantallaAsistenciaPersonal.this, "Trabajador ya ha sido asignado a TAREO.", Toast.LENGTH_SHORT).show();
            }else if(r == 600){//NO ENCONTRADO EN LA LISTA DE PERSONAL SINCRONIZADA (PUEDE ESTAR CESADO, NUEVO EN FICHAS O NO ENCONTRADO, POR ENDE SE DEBE COMPROBAR EN L??NEA
                //getConsultaPersonalNoEncontrado(DNI);
                Toast.makeText(ReconocimientoAsistenciaPersonal.this, "CESADO / OTRA PLANILLA (ERG)", Toast.LENGTH_SHORT).show();
            }else{//Se grabo correctamente!
                cambiarColorBackgroundExito();
                esperaSegundosYAbrirLectorCamara(3);
                visualizarcantidadPersonas(IDTAREO);
                cargarListaTrabajadores(IDTAREO);
            }
        }

        private void getConsultaPersonalNoEncontrado(final String dni){
            ProgressDialog pDialog;
            pDialog = new ProgressDialog(ReconocimientoAsistenciaPersonal.this);
            pDialog.setMessage("Verificando en l??nea...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();

            Log.i("DNI", "|"+dni);
            RequestQueue request;
            request = Volley.newRequestQueue(this);
            String url = MiAplicacionTareo.URL_SERVICE + "getConsultaCesadoNuevo";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pDialog.dismiss();
                    Log.i("Response", response);
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray jsonArray = json.getJSONArray("resultado");
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonData = jsonArray.getJSONObject(i);
                            Log.i("ESTADO", jsonData.getString("ESTADO"));
                            if(jsonData.getString("ESTADO").equalsIgnoreCase("CESADO")){
                                dialogPersonalizado("CESADO", "El dni ingresado["+dni+"] corresponde a "+jsonData.getString("PERSONAL")+", no puede ser tareado ya que est?? CESADO en la fecha: "+jsonData.getString("FECHA_CESE"));

                                cambiarColorBackgroundError();

                                Administrador objAdm = new Administrador();
                                objAdm.cargarListaNumerosAdministrador();
                                enviarCorreoObservado(dni, jsonData.getString("PERSONAL"), MiAplicacionTareo.NOMBRES, "CESADO");
                                for(int j=0; j<Administrador.listaNumerosAdministradores.size(); j++){
                                    new TareaEnviarSMS().execute(Administrador.listaNumerosAdministradores.get(j).getNumero(), "(CESADO) "+ jsonData.getString("PERSONAL") + " - "+dni+", AUX: " + MiAplicacionTareo.NOMBRES);
                                }

                            }else if(jsonData.getString("ESTADO").equalsIgnoreCase("FICHAS")){//Es nuevo
                                guardarAsistenciaSQLiteNuevoFichas(IDTAREO, dni);
                                dialogPersonalizado("NUEVO", "El dni ingresado["+dni+"] perteneciente a "+jsonData.getString("PERSONAL")+", se ha agregado correctamente");
                            }else{//NO_EXISTE
                                dialogPersonalizado("NO ENCONTRADO", "El dni ingresado["+dni+"]  no se encuentra en los registros del PERSONAL ACP ");
                            }
                        }

                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            Log.i("ERROR-VOLLEY", "ERROR");
                            Toast.makeText(ReconocimientoAsistenciaPersonal.this, "ERROR DE CONEXI??N. Por favor int??ntalo nuevamente.", Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("dni", dni);
                    return params;
                }
            };
            request.add(postRequest);
        }

        private void guardarAsistenciaSQLiteNuevoFichas(String IDTAREO, String DNI){
            Asistencia objA = new Asistencia();
            Long r = objA.agregarAsistenciaSQLiteNuevoFichas(IDTAREO, DNI);
            Log.i("LONG_ASISTENCIA", ""+r);
            if(r == 500){//OBSERVADO
                dialogObservado(DNI);
                cambiarColorBackgroundError();

                Administrador objAdm = new Administrador();
                objAdm.cargarListaNumerosAdministrador();
                if(Asistencia.MOTIVO_OBSERVADO.trim().length() != 0){
                    //enviarCorreoObservado(DNI, Asistencia.NOMBREOBSERVADO, MiAplicacionTareo.NOMBRES, "observado");
                    for(int i=0; i<Administrador.listaNumerosAdministradores.size(); i++){
                        //new TareaEnviarSMS().execute(Administrador.listaNumerosAdministradores.get(i).getNumero(), "(OBS. POR "+ Asistencia.MOTIVO_OBSERVADO+") "+ Asistencia.NOMBREOBSERVADO + " - "+DNI+", AUX: " + MiAplicacionTareo.NOMBRES);
                    }
                    //Envia
                }
            }else{//Se grabo correctamente!
                cambiarColorBackgroundExito();
                cargarListaTrabajadores(IDTAREO);
            }
        }

        public void dialogPersonalizado(String TITULO, String MENSAJE){
            PrettyDialog pDialog = new PrettyDialog(this);
            pDialog.setCancelable(false);
            pDialog
                    .setIconTint(R.color.pdlg_color_red)
                    .setTitle(TITULO)
                    .setMessage(MENSAJE)
                    .addButton(
                            "ACEPTAR",
                            R.color.pdlg_color_white,
                            R.color.colorPrimary,
                            new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    pDialog.dismiss();
                                }
                            }
                    )
                    .setAnimationEnabled(true)
                    .show();
        }


        public void dialogObservado(String DNI){
            final PrettyDialog pDialog = new PrettyDialog(this);
            pDialog.setCancelable(false);
            pDialog
                    .setIconTint(R.color.pdlg_color_red)
                    .setTitle("OBSERVADO")
                    .setMessage("Personal con DNI "+ DNI +" est?? en la lista de OBSERVADOS (" + Asistencia.MOTIVO_OBSERVADO + ")")
                    .addButton(
                            "ACEPTAR",
                            R.color.pdlg_color_white,
                            R.color.colorPrimary,
                            new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    esperaSegundosYAbrirLectorCamara(3);
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
                    .setMessage("Personal con DNI "+ DNI +" no est?? autorizado para trabajar en el " +MODULO_SELECCIONADO)
                    .addButton(
                            "ACEPTAR",
                            R.color.pdlg_color_white,
                            R.color.colorPrimary,
                            new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    esperaSegundosYAbrirLectorCamara(3);
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
            /*final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run(){
                    IntentIntegrator integrator = new IntentIntegrator(PantallaAsistenciaPersonal.this);
                    //integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_39, IntentIntegrator.CODE_128, IntentIntegrator.QR_CODE);
                    integrator.setPrompt("Enfoca - C??digo DNI");
                    integrator.setCameraId(0);
                    ///integrator.setOrientationLocked(true);
                    integrator.setBeepEnabled(true);
                    integrator.setBarcodeImageEnabled(false);
                    integrator.initiateScan();
                }
            }, segundos * 1000);*/
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(requestCode == GET_FACE){
                if(resultCode == RESULT_OK){
                    grabar(data.getStringExtra("dni"));
                    Toast.makeText(ReconocimientoAsistenciaPersonal.this, data.getStringExtra("dni"), Toast.LENGTH_SHORT).show();
                }
            }else{
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

                Toast.makeText(ReconocimientoAsistenciaPersonal.this, texto.substring(10, primerEspacio) + " " + texto.substring(segundoLetra, segundoEspacio) + ", "+ texto.substring(tercerLetra, tercerEspacio) + " || "+ nuevo_dni, Toast.LENGTH_LONG).show();

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

            AlertDialog.Builder builder = new AlertDialog.Builder(ReconocimientoAsistenciaPersonal.this);
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

        public String cargar_preferencia_idtareo(){
            SharedPreferences prefs = getSharedPreferences("PreferenciasTareoACP2", Context.MODE_PRIVATE);
            String idtareo  = prefs.getString("IDTAREO", "-");
            return idtareo;
        }


        private class TareaCargarRostrosGuardadosSQLite extends AsyncTask<String, Void, HashMap<String, SimilarityClassifier.Recognition> > {
            ProgressDialog pDialog;
            @Override
            protected void onPreExecute() {
                pDialog = new ProgressDialog(ReconocimientoAsistenciaPersonal.this);
                pDialog.setMessage("Cargando reconocimientos SQLite...");
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected HashMap<String,SimilarityClassifier.Recognition> doInBackground(String... params) {
                Reconocimiento obj = new Reconocimiento();
                obj.cargarReconocimientosSQLite();
                return Reconocimiento.reconocimientosSQLite;
            }

            protected void onPostExecute(HashMap<String,SimilarityClassifier.Recognition> result){
                pDialog.dismiss();
                registered = result;
                Toast.makeText(ReconocimientoAsistenciaPersonal.this, "Reconocimientos SQLite cargados correctamente.", Toast.LENGTH_SHORT).show();
            }
        }


    }
