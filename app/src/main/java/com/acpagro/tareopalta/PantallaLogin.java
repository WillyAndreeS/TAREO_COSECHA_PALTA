package com.acpagro.tareopalta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acpagro.tareopalta.datos.SQLite;
import com.acpagro.tareopalta.modelo.APKInstall;
import com.acpagro.tareopalta.modelo.Actividad;
import com.acpagro.tareopalta.modelo.Administrador;
import com.acpagro.tareopalta.modelo.Consumidor;
import com.acpagro.tareopalta.modelo.HoraTareo;
import com.acpagro.tareopalta.modelo.Labor;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.PersonalGeneral;
import com.acpagro.tareopalta.modelo.SubLabor;
import com.acpagro.tareopalta.modelo.TicketCosecha;
import com.acpagro.tareopalta.modelo.Usuario;
import com.acpagro.tareopalta.modelo.Valvula;
import com.acpagro.tareopalta.modelo.Variedad;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.huxq17.download.Pump;
import com.huxq17.download.message.DownloadListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.github.kobakei.materialfabspeeddial.FabSpeedDial;
import io.github.kobakei.materialfabspeeddial.FabSpeedDialMenu;
import ir.mtajik.android.advancedPermissionsHandler.PermissionHandlerActivity;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;

public class PantallaLogin extends PermissionHandlerActivity implements View.OnClickListener, TextWatcher {
    EditText edt_dni_login;
    Button btn_aceptar_login, btn_salir_login;
    ImageView backgroundLogin;
    FloatingActionButton fab_sinronizar_login;
    private ImageButton imb_reconocimiento;
    private FabSpeedDial fabsd;
    private AlertDialog alertDialog;

    //Request
    private RequestQueue request;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_login);
        SQLite.aplicacion=this;
        imb_reconocimiento = findViewById(R.id.imb_reconocimiento);
        edt_dni_login = (EditText)findViewById(R.id.edt_dni_login);
        btn_aceptar_login = (Button)findViewById(R.id.btn_aceptar_login);
        btn_salir_login = (Button)findViewById(R.id.btn_salir_login);
        backgroundLogin = (ImageView)findViewById(R.id.backgroundLogin);
        fab_sinronizar_login = (FloatingActionButton)findViewById(R.id.fab_sinronizar_login);

        btn_aceptar_login.setOnClickListener(this);
        btn_salir_login.setOnClickListener(this);
        fab_sinronizar_login.setOnClickListener(this);
        imb_reconocimiento.setOnClickListener(this);

        edt_dni_login.addTextChangedListener(this);

        edt_dni_login.requestFocus();
        fondo_pantalla();

        fabsd = findViewById(R.id.fab);
        fabsd.setMenu(menuOpcionesMapaFab());
        /*fabsd.addOnMenuItemClickListener(new Function3<FloatingActionButton, TextView, Integer, Unit>() {
            @Override
            public Unit invoke(FloatingActionButton floatingActionButton, TextView textView, Integer integer) {
                switch (integer){
                    case 1:
                        dialogDescargar(getResources().getString(R.string.enlace_descarga));
                        break;
                    case 2:
                        alertDialog = dialogMostrarQRDescarga();
                        alertDialog.show();
                        break;
                }

                return null;
            }
        });*/

        fabsd.addOnMenuItemClickListener(new FabSpeedDial.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionButton fab, TextView label, int itemId) {
                switch (itemId){
                    case 1:
                        dialogDescargar(getResources().getString(R.string.enlace_descarga));
                        break;
                    case 2:
                        alertDialog = dialogMostrarQRDescarga();
                        alertDialog.show();
                        break;
                }
            }
        });

        //Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS};
        boolean stickyMode = true;
        askForPermission(permissions , stickyMode, new PermissionHandlerActivity.PermissionCallBack() {
            @Override
            public void onPermissionsGranted() {
                Log.i("TAG", "onPermissionsGranted: ");
            }

            @Override
            public void onPermissionsDenied(String[] permissions) {
                Log.i("TAG", "onPermissionsDenied: ");
            }
        });
    }

    public void dialogDescargar(final String LINK_DESCARGA){
        AlertDialog.Builder builder = new AlertDialog.Builder(PantallaLogin.this);
        builder.setTitle("Información");
        builder.setMessage("¿Descargar última versión disponible en el servidor ("+LINK_DESCARGA+")?");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_descargar_black);
        builder.setPositiveButton("DESCARGAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                descargar(LINK_DESCARGA);
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void descargar(String URLDescarga){
        ProgressDialog pDialog;
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Descargando...");
        pDialog.setProgress(0);
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setProgress(0);
        pDialog.show();
        Pump.newRequest(URLDescarga)
                .threadNum(1)
                .listener(new DownloadListener() {
                    @Override
                    public void onProgress(int progress) {
                        pDialog.setProgress(progress);
                    }

                    @Override
                    public void onSuccess() {
                        pDialog.dismiss();
                        String apkPath = getDownloadInfo().getFilePath();
                        APKInstall.with(PantallaLogin.this).from(apkPath)
//                                        .forceInstall();
                                .install();
                        Toast.makeText(PantallaLogin.this, "Descarga Finalizada!.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed() {
                        pDialog.dismiss();
                        Toast.makeText(PantallaLogin.this, "Descarga ha fallado!.", Toast.LENGTH_SHORT).show();
                    }
                })
                //Optionally,Set whether to repeatedly download the downloaded file,default false.
                .forceReDownload(true)
                //Optionally,Set how many threads are used when downloading,default 3.
                .threadNum(3)
                .setRetry(3, 200)
                .submit();
    }

    private FabSpeedDialMenu menuOpcionesMapaFab(){
        FabSpeedDialMenu menu = new FabSpeedDialMenu(PantallaLogin.this);
        menu.add("Descargar Última Versión").setIcon(R.drawable.ic_descargar_white);
        menu.add("Mostrar QR de descarga").setIcon(R.drawable.ic_menu_qr_white);

        return menu;
    }

    public AlertDialog dialogMostrarQRDescarga(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(PantallaLogin.this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.item_dialog_qr, null);
        ImageView img_qr = v.findViewById(R.id.img_qr);
        generarCodigoQR(img_qr, getResources().getString(R.string.enlace_descarga));
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){

            }
        });
        builder.setView(v);
        return  builder.create();
    }

    private void generarCodigoQR(ImageView iv, String codigo){
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(codigo, BarcodeFormat.QR_CODE, 400, 400);
            Bitmap bitmap = createBitmap(bitMatrix);
            iv.setImageBitmap(bitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }
    }

    public Bitmap createBitmap(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //Log.i("LOG -Antes de",charSequence+"//Start:"+i+"//Count:"+i1+"//After:"+i2);
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(String.valueOf(charSequence).length() == 8){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edt_dni_login.getWindowToken(), 0);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        //Log.i("LOG -Despues de",""+editable);
    }

    private void fondo_pantalla(){
        //Definir fondo de pantalla
        Glide.with(this)
                .load(R.drawable.img_fondo_palta_05)
                //.centerCrop()
                //.placeholder(R.drawable.loading_spinner)
                .into(backgroundLogin);
    }

    private String retorna_dni(){
        return edt_dni_login.getText().toString();
    }



    private class   TareaIniciarSesion extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(PantallaLogin.this);
            pDialog.setMessage("Autenticando..");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Usuario objS = new Usuario();
            return objS.iniciarSesion(retorna_dni());
        }

        protected void onPostExecute(Boolean result){
            MiAplicacionTareo.guardar_preferenciaLogin(PantallaLogin.this);
            pDialog.dismiss();
            if(result){
                edt_dni_login.setText("");
                /*if(MiAplicacionTareo.TIPO_USUARIO.equalsIgnoreCase("SUPERVISOR")){
                    Intent p = new Intent(PantallaLogin.this, PantallaSupervisor.class);
                    startActivity(p);
                }else{*/
                    Intent p = new Intent(PantallaLogin.this, PantallaPrincipal.class);
                    startActivity(p);
//                }

            } else{
                Toast.makeText(PantallaLogin.this, "Usuario incorrecto!!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sincronizar(){
        pDialog = new ProgressDialog(PantallaLogin.this);
        pDialog.setTitle("Obteniendo datos en BD Local...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();

        request = Volley.newRequestQueue(this);
        String url = MiAplicacionTareo.URL_SERVICE + "sincronizarInformacionPalta";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                //Log.i("Response", response);
                try {
                    Consumidor.listaConsumidorHost.clear();
                    Actividad.listaActividadHost.clear();
                    Labor.listaLaborHost.clear();
                    SubLabor.listaSubLaborHost.clear();
                    PersonalGeneral.listaPersonalGeneralHost.clear();
                    Usuario.listaUsuariosHost.clear();
                    Administrador.listaNumerosAdministradores.clear();
                    HoraTareo.listaHoraTareo.clear();
                    Valvula.listaValvulas.clear();

                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArrayC = json.getJSONArray("consumidores");
                    JSONArray jsonArrayA = json.getJSONArray("actividades");
                    JSONArray jsonArrayL = json.getJSONArray("labores");
                    JSONArray jsonArraySL = json.getJSONArray("sublabores");
                    JSONArray jsonArrayPG = json.getJSONArray("personal_general");
                    JSONArray jsonArrayU = json.getJSONArray("usuarios");
                    JSONArray jsonArrayAD = json.getJSONArray("administradores");
                    JSONArray jsonArrayHT = json.getJSONArray("hora_tareo");
                    JSONArray jsonArrayVA = json.getJSONArray("variedades");
                    JSONArray jsonArrayV = json.getJSONArray("valvulas");

                    Consumidor.listaConsumidorHost.add(new Consumidor("001",
                            "S-M-T-",
                            "PRETAREO",
                            "0006",
                            "PALTO"));
                    for(int i = 0; i < jsonArrayC.length(); i++){
                        JSONObject jsonData = jsonArrayC.getJSONObject(i);
                        Log.i("CONSUMIDOR", jsonData.getString("CONSUMIDOR"));
                        Consumidor.listaConsumidorHost.add(new Consumidor(
                                jsonData.getString("IDEMPRESA"),
                                jsonData.getString("IDCONSUMIDOR"),
                                jsonData.getString("CONSUMIDOR"),
                                jsonData.getString("IDCULTIVO"),
                                jsonData.getString("CULTIVO")
                        ));

                    }

                    for(int i = 0; i < jsonArrayA.length(); i++){
                        JSONObject jsonData = jsonArrayA.getJSONObject(i);
                        Log.i("ACTIVIDAD", jsonData.getString("ACTIVIDAD"));

                        Actividad.listaActividadHost.add(new Actividad(
                                jsonData.getString("IDEMPRESA"),
                                jsonData.getString("IDACTIVIDAD"),
                                jsonData.getString("ACTIVIDAD"),
                                jsonData.getString("ALIAS")
                        ));
                    }

                    for(int i = 0; i < jsonArrayL.length(); i++){
                        JSONObject jsonData = jsonArrayL.getJSONObject(i);
                        Log.i("LABOR", jsonData.getString("LABOR"));
                        Labor.listaLaborHost.add(new Labor(
                                jsonData.getString("IDEMPRESA"),
                                jsonData.getString("IDACTIVIDAD"),
                                jsonData.getString("IDLABOR"),
                                jsonData.getString("LABOR"),
                                jsonData.getString("ALIAS")
                        ));
                    }

                    for(int i = 0; i < jsonArraySL.length(); i++){
                        JSONObject jsonData = jsonArraySL.getJSONObject(i);
                        Log.i("SUBLABOR", jsonData.getString("SUBLABOR"));
                        SubLabor.listaSubLaborHost.add(new SubLabor(
                                jsonData.getString("IDACTIVIDAD"),
                                jsonData.getString("IDLABOR"),
                                jsonData.getString("IDSUBLABOR"),
                                jsonData.getString("SUBLABOR"),
                                jsonData.getString("GRUPO"),
                                jsonData.getString("UNIDAD")
                        ));
                    }

                    for(int i = 0; i < jsonArrayPG.length(); i++){
                        JSONObject jsonData = jsonArrayPG.getJSONObject(i);
                        Log.i("PERSONALGENERAL", jsonData.getString("NOMBRES"));

                        PersonalGeneral.listaPersonalGeneralHost.add(new PersonalGeneral(
                                jsonData.getString("CODIGO"),
                                jsonData.getString("DNI"),
                                jsonData.getString("NOMBRES"),
                                jsonData.getString("OBSERVADO"),
                                jsonData.getString("MODULO"),
                                jsonData.getString("MOTIVO")
                        ));
                    }

                    for(int i = 0; i < jsonArrayU.length(); i++){
                        JSONObject jsonData = jsonArrayU.getJSONObject(i);
                        Log.i("USUARIOS", jsonData.getString("DNI"));
                        Usuario.listaUsuariosHost.add(new Usuario(
                                jsonData.getString("IDUSUARIO"),
                                jsonData.getString("DNI"),
                                jsonData.getString("CLAVE"),
                                jsonData.getString("IDCULTIVO"),
                                jsonData.getString("LEE_PDA"),
                                jsonData.getString("TIPO_USUARIO")
                        ));
                    }

                    for(int i = 0; i < jsonArrayAD.length(); i++){
                        JSONObject jsonData = jsonArrayAD.getJSONObject(i);
                        Log.i("ADMINISTRADOR", jsonData.getString("NUMERO"));
                        Administrador.listaNumerosAdministradores.add(new Administrador(
                                jsonData.getString("NUMERO"),
                                jsonData.getString("CORREO"),
                                jsonData.getString("NOMBRE")
                        ));
                    }
                    Log.i("HORATAREO", "Estado de ingreso a la sincronización" + jsonArrayHT.length());
                    for(int i = 0; i < jsonArrayHT.length(); i++){
                        JSONObject jsonData = jsonArrayHT.getJSONObject(i);
                        Log.i("HORATAREO", jsonData.getString("DIA"));
                        HoraTareo.listaHoraTareo.add(new HoraTareo(
                                jsonData.getString("IDCULTIVO").trim(),
                                jsonData.getString("DIA").trim(),
                                jsonData.getString("HORAS"),
                                jsonData.getString("FECHAREGISTRO").trim(),
                                jsonData.getString("IDUSUARIO").trim()
                        ));
                    }

                    for(int i = 0; i < jsonArrayVA.length(); i++){
                        JSONObject jsonData = jsonArrayVA.getJSONObject(i);
                        Log.i("VARIEDAD", jsonData.getString("IDVARIEDAD"));
                        Variedad.listaVariedad.add(new Variedad(
                                jsonData.getString("IDCULTIVO").trim(),
                                jsonData.getString("IDVARIEDAD").trim(),
                                jsonData.getString("DESCRIPCION").trim()
                        ));
                    }

                    for(int i = 0; i < jsonArrayV.length(); i++){
                        JSONObject jsonData = jsonArrayV.getJSONObject(i);
                        Log.i("VALVULA", jsonData.getString("VALVULA"));
                        Valvula.listaValvulas.add(new Valvula(
                                jsonData.getString("NROVALVULA").trim(),
                                jsonData.getString("VALVULA").trim(),
                                jsonData.getString("IDCONSUMIDOR").trim()
                        ));
                    }

                    new TareaAgregarDatosObtenidosDeServicesASQLite().execute();

                } catch (JSONException e) {
                    pDialog.dismiss();
                    Toast.makeText(PantallaLogin.this, "Ocurrió un inconveniente al sincronizar - 500", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Toast.makeText(PantallaLogin.this, "Ocurrió un inconveniente al sincronizar - 400", Toast.LENGTH_SHORT).show();
                    }
                }
        ) /*{
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("IdEmpresa", "001");
                params.put("IdCultivo", "0006");
                return params;
            }
        }*/;
        request.add(postRequest);
    }

    private class TareaAgregarDatosObtenidosDeServicesASQLite extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            pDialog.setTitle("Grabando datos en BD Local...");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Consumidor objC = new Consumidor();
            Actividad objA = new Actividad();
            Labor objL = new Labor();
            SubLabor objS = new SubLabor();
            PersonalGeneral objPG = new PersonalGeneral();
            Usuario objU = new Usuario();
            Administrador objAdm = new Administrador();
            HoraTareo objHoraT = new HoraTareo();
            Variedad objVa = new Variedad();

            objC.limpiarTablaConsumidorSQLite();
            objA.limpiarTablaActividadSQLite();
            objL.limpiarTablaLaborSQLite();
            objPG.limpiarTablaPersonalGeneralSQLite();
            objU.limpiarTablaUsuarioSQLite();
            objAdm.limpiarTablaAdministradorSQLite();
            objHoraT.limpiarTablaHoraTareoSQLite();
            objS.limpiarTablaSubLaborSQLite();
            objVa.limpiarTablaVariedadSQLite();

            objC.agregar_ConsumidoresSQLite();
            objA.agregar_ActividadSQLite();
            objS.agregar_SublaboresSQLite();
            objL.agregar_LaborSQLite();
            objPG.agregar_PersonalGeneralSQLite();
            objU.agregar_UsuarioSQLite();
            objAdm.agregar_AdministradorSQLite();
            objHoraT.agregar_HoraTareoSQLite();
            objVa.agregar_VariedadSQLite();
            return true;
        }

        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            if(result){
                Toast.makeText(PantallaLogin.this, "Sincronización Exitosa", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(PantallaLogin.this, "Ocurrió un error al sincronizar!, vuelve a intentarlo!!", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Metodos Evento Click

    private void login_ingresar(){
        if(edt_dni_login.getText().toString().isEmpty()){
            edt_dni_login.setError("Debes escribir tu DNI");
        }else if(edt_dni_login.getText().toString().length() < 8){
            edt_dni_login.setError("DNI Incorrecto!");
        }else {
            Usuario obj = new Usuario();
            obj.cargarListaUsuarios();//Verifica si esta el admin
            if(Usuario.listaUsuariosSQLite.size()==0){
                obj.agregarUsuarioSQLite("0","00000000","admin","0006","NO","NORMAL");
            }
            new TareaIniciarSesion().execute();
        }
    }

    private void login_salir(){
        System.exit(0);
//        finish();
    }

    private void login_sincronizar(){
        if(MiAplicacionTareo.hayConexion(PantallaLogin.this)){
            sincronizar();
        }else{
            Toast.makeText(PantallaLogin.this, "No dispones de conexión a internet, verifica y vuelve a intentarlo.", Toast.LENGTH_SHORT).show();
        }
    }

    //Fin Metodos Event Click

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_aceptar_login:
                login_ingresar();
                break;

            case R.id.btn_salir_login:
                login_salir();
                break;

            case R.id.fab_sinronizar_login:
                login_sincronizar();
                break;
            case R.id.imb_reconocimiento:
                startActivity(new Intent(PantallaLogin.this, ReconocimientoTest.class));
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MiAplicacionTareo.iniciarLectorVoz(PantallaLogin.this);
    }
}
