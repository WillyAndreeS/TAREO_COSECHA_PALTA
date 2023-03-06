package com.acpagro.tareopalta;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acpagro.tareopalta.adapters.AdapterListaBinDetalle;
import com.acpagro.tareopalta.datos.Http;
import com.acpagro.tareopalta.datos.HttpPostValues;
import com.acpagro.tareopalta.modelo.Administrador;
import com.acpagro.tareopalta.modelo.Asistencia;
import com.acpagro.tareopalta.modelo.BinCabecera;
import com.acpagro.tareopalta.modelo.BinDetalle;
import com.acpagro.tareopalta.modelo.DetalleTareo;
import com.acpagro.tareopalta.modelo.HoraPersonal;
import com.acpagro.tareopalta.modelo.LecturaMaquinaria;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.SalidaAsistencia;
import com.acpagro.tareopalta.modelo.SalidaPersonal;
import com.acpagro.tareopalta.modelo.ServiceTarea;
import com.acpagro.tareopalta.modelo.Tareo;
import com.acpagro.tareopalta.modelo.TicketCosecha;
import com.acpagro.tareopalta.modelo.TicketPersona;
import com.acpagro.tareopalta.util.UIHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class PantallaDetalleBinLectura extends AppCompatActivity {
    public static String IDCABECERA, PLACA, CHOFER, FECHAHOY;
    public static TextView tv_placa, tv_chofer, tv_trazabilidad, tv_bin, item_total_detalle;
    private EditText edt_codigo;
    public static ListView lv_detalle;
    RelativeLayout rl_lectura;
    public static AdapterListaBinDetalle adapter;
    public static AlertDialog alertDialog;
    private ImageButton ibtnImprimir;
    ProgressDialog pDialog;
    private UIHelper helper;
    LinearLayout ly_formato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_detalle_bin_lectura);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Lectura de Bines");

        Bundle parametros = this.getIntent().getExtras();
        if(parametros!= null){
            IDCABECERA = parametros.getString("p_idcabecera");
            PLACA = parametros.getString("p_placa");
            CHOFER = parametros.getString("p_chofer");
            FECHAHOY = parametros.getString("p_fecha");
        }else{
            finish();
        }

        tv_placa = findViewById(R.id.tv_placa);
        tv_chofer = findViewById(R.id.tv_chofer);
        tv_trazabilidad = findViewById(R.id.tv_trazabilidad);
        tv_bin = findViewById(R.id.tv_bin);
        item_total_detalle = findViewById(R.id.item_total_detalle);
        ibtnImprimir = findViewById(R.id.ibtnImprimir);
        edt_codigo = findViewById(R.id.edt_codigo);
        lv_detalle = findViewById(R.id.lv_detalle);
        rl_lectura = findViewById(R.id.rl_lectura);
        adapter = new AdapterListaBinDetalle(this);
        lv_detalle.setAdapter(adapter);

        tv_placa.setText(PLACA);
        tv_chofer.setText(CHOFER);

        edt_codigo.requestFocus();

        edt_codigo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    String cod = edt_codigo.getText().toString().trim();
                    if(cod.length() == 7){//P000003 CÓDIGO DE BIN
                        if(tv_trazabilidad.getText().toString().trim().length()!=0){//Bacan ya s eha leido primero la trazabilidad
                            tv_bin.setText(cod);
                            guardar(tv_trazabilidad.getText().toString().trim(), tv_bin.getText().toString().trim());
                        }else{
                            Toast.makeText(PantallaDetalleBinLectura.this, "PRIMERO DEBES LEER UN CÓDIGO DE TRAZABILIDAD", Toast.LENGTH_SHORT).show();
                            cambiarColorBackgroundError();
                            limpiarETConEspera();
                        }

                    }else if(cod.length() > 11){//Mínimo 12 caracteres
                        tv_trazabilidad.setText(cod);
                        limpiarETConEspera();
                    }else{
                        Toast.makeText(PantallaDetalleBinLectura.this, "Código no es válido, debes leer un CÓDIGO DE TRAZABILIDAD O UN CÓDIGO DE BIN", Toast.LENGTH_SHORT).show();
                        edt_codigo.setText("");
                        cambiarColorBackgroundError();
                    }


                    return true;
                }
                return false;
            }
        });

        ibtnImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BinDetalle.suma_bines>0){
                    alertDialog = dialogImprimir(PantallaDetalleBinLectura.this);
                    alertDialog.show();
                }else{
                    Toast.makeText(PantallaDetalleBinLectura.this, "Debes iniciar la lectura para imprimir.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        cargarDetalle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_config_impresora, menu);
        return true;
    }

    public static void cargarDetalle(){
        BinDetalle objBD = new BinDetalle();
        objBD.cargarListaDetalleBines(IDCABECERA);
        adapter.notifyDataSetChanged();
        item_total_detalle.setText(""+BinDetalle.suma_bines);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MiAplicacionTareo.cargar_preferenciasLogin(PantallaDetalleBinLectura.this);
    }

    public void guardar(String CODIGOTRAZABILIDAD, String CODIGOBIN){
        String consumidor = "", valvula = "", variedad = "";
        consumidor = CODIGOTRAZABILIDAD.substring(0, (CODIGOTRAZABILIDAD.length() - 6));
        valvula = CODIGOTRAZABILIDAD.substring(consumidor.length(), (CODIGOTRAZABILIDAD.length() - 3));
        variedad = CODIGOTRAZABILIDAD.substring((consumidor.length()+3), (CODIGOTRAZABILIDAD.length()));

        BinDetalle bd = new BinDetalle();
        Long r = bd.agregarLecturaBin(PantallaDetalleBinLectura.this, IDCABECERA, tv_bin.getText().toString().trim(), consumidor, variedad, valvula, MiAplicacionTareo.IDUSUARIO);

        if(r == -1){
            cambiarColorBackgroundError();
        }else{
            reproducirPitido();
            cambiarColorBackgroundExito();

            cargarDetalle();
        }

        Log.i("LOG", "COD_TRAZA "+CODIGOTRAZABILIDAD+"|"+CODIGOBIN);
        tv_trazabilidad.setText("");
        tv_bin.setText("");
        edt_codigo.setText("");
        edt_codigo.requestFocus();
    }

    public void limpiarETConEspera(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                edt_codigo.setText("");
                edt_codigo.requestFocus();
            }
        }, 300);
    }

    public void cambiarColorBackgroundError(){
        rl_lectura.setBackgroundColor(Color.parseColor("#ff0000"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_lectura.setBackgroundColor(Color.parseColor("#ffffff"));
                edt_codigo.requestFocus();
            }
        }, 400);
    }

    public void cambiarColorBackgroundExito(){
        rl_lectura.setBackgroundColor(Color.parseColor("#6dff4d"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_lectura.setBackgroundColor(Color.parseColor("#ffffff"));
                edt_codigo.requestFocus();
            }
        }, 400);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                finish();
                return true;
            case R.id.action_configurar_impresora:
                startActivity(new Intent(PantallaDetalleBinLectura.this, ConexionImpresora.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public AlertDialog dialogImprimir(Activity context){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(R.layout.item_dialog_impresion_bin, null);
        final TextView tv_fecha = v.findViewById(R.id.tv_fecha);
        final TextView tv_placa = v.findViewById(R.id.tv_placa);
        final TextView tv_chofer = v.findViewById(R.id.tv_chofer);
        final TextView tv_bines = v.findViewById(R.id.tv_bines);
        final TextView tv_cabecera = v.findViewById(R.id.tv_cabecera);
        final ImageView img_codebar = v.findViewById(R.id.img_codebar);
        ly_formato = v.findViewById(R.id.ly_formato);
        Button btn_aceptar = v.findViewById(R.id.btn_aceptar);
        Button btn_cancelar = v.findViewById(R.id.btn_cancelar);

        tv_cabecera.setText(IDCABECERA);
        tv_fecha.setText(FECHAHOY);
        tv_placa.setText(PLACA);
        tv_chofer.setText(CHOFER);
        tv_bines.setText(""+BinDetalle.suma_bines);
        MiAplicacionTareo.generarCodigo128(IDCABECERA, img_codebar);

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String sms = "VIAJE PALTO: "+dateFormat2.format(date)+" / \n";
                sms=sms+""+CHOFER+"("+PLACA+")\n"+BinDetalle.contenido_sms+"TOTAL => "+BinDetalle.suma_bines+" BIN.";
                enviarSms(sms);*/

                new TareaImprimir().execute("1");
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        builder.setTitle("IMPRIMIR ETIQUETA");
        builder.setView(v);
        return  builder.create();
    }

    private void reproducirPitido(){
        MediaPlayer mp = MediaPlayer.create(this, R.raw.pitido_scanner);
        mp.start();
    }

    private class TareaImprimir extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PantallaDetalleBinLectura.this);
            pDialog.setTitle("Imprimiendo...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String cantidad = params[0];

            MiAplicacionTareo.cargar_preferenciasIMPRESORA(PantallaDetalleBinLectura.this);
            if(MiAplicacionTareo.IMPRESORA.equalsIgnoreCase("ZEBRA")){
                Connection connection = new BluetoothConnection(""+MiAplicacionTareo.MAC_BT);//ZEBRA 03
                try {
                    connection.open();
                    ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
                    Bitmap image = getBitmapFromView(ly_formato);
                    //Bitmap imagenf = rotate2(image, 90);|

                    for(int i = 0; i< Integer.parseInt(cantidad); i++){
                        //printer.printImage(new ZebraImageAndroid(image), 30, 30, 750, 1500, false);
                        printer.printImage(new ZebraImageAndroid(image), 10, 10, 750, 375, false);
                    }
                    //printer.printImage(new ZebraImageAndroid(imagenf), 30, 30, 750, 1460, false);
                    DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    String sms = "VIAJE PALTO: "+dateFormat2.format(date)+" / \n";
                    sms=sms+""+CHOFER+"("+PLACA+")\n"+BinDetalle.contenido_sms+"TOTAL => "+BinDetalle.suma_bines+" BIN.";
                    enviarSms(sms);

                    connection.close();
                    return true;
                } catch (ConnectionException e) {
                    helper.showErrorDialogOnGuiThread(e.getMessage());
                    return false;
                } catch (ZebraPrinterLanguageUnknownException e) {
                    helper.showErrorDialogOnGuiThread(e.getMessage());
                    return false;
                } finally {
                    return true;
                }
            }else if(MiAplicacionTareo.IMPRESORA.equalsIgnoreCase("BIXOLON")){
                try{
                    Bitmap image = getBitmapFromView(ly_formato);
                    //Bitmap imagenf = rotate2(image, 90);
                    for(int i = 0; i< Integer.parseInt(cantidad); i++){
                        ConexionImpresora.getPrinterInstance().printImagePersonality(image, 750, ConexionImpresora.getPrinterInstance().ALIGNMENT_CENTER, 50);
                    }
                    return  true;
                }catch (Exception e){
                    //e.printStackTrace();
                    return  false;
                }

                //return true;
            }else{
                return false;
            }
        }

        protected void onPostExecute(Boolean rpta) {
            pDialog.dismiss();
            if(!rpta){
                Toast.makeText(PantallaDetalleBinLectura.this, "DEBES SELECCIONAR UNA IMPRESORA, ANTES DE ENVIAR A IMPRIMIR; SELECIONALA EN EL MENÚ DE ARRIBA 'CONFIGURAR IMPRESORA'. RECUERDA ACTIVAR TU BLUETOOTH Y VINCULARLA CORRECTAMENTE.", Toast.LENGTH_LONG).show();
            }else{
                alertDialog.dismiss();

                MiAplicacionTareo obj = new MiAplicacionTareo();
                if(obj.getConteoDatosParaTransferir()>0){
                    if(MiAplicacionTareo.hayConexion(getApplicationContext())){
                        if(MiAplicacionTareo.ENVIANDO==0){
                            //Toast.makeText(getApplicationContext(), "ENVIAADO", Toast.LENGTH_SHORT).show();
                            new TareaEnviarDataXMLHtml().execute();
                        }
                    }
                }



            }
        }
    }

    //INICIO NUEVA TRANSFERENCIA================================================================================================================================================================

    private class TareaEnviarDataXMLHtml extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MiAplicacionTareo.cargar_preferenciasLogin(PantallaDetalleBinLectura.this);
            MiAplicacionTareo.ENVIANDO=1;
        }

        @Override
        protected String doInBackground(String... params) {
            return enviarXML_Http();
        }

        protected void onPostExecute(String result) {
            Log.i("RESULT_ASYNTASK_SERV", result);
            MiAplicacionTareo obj = new MiAplicacionTareo();
            if (result.equalsIgnoreCase("true")) {
                obj.modificarEstadoSincronizado("2", "1");//1
                //Toast.makeText(thisContext, "200", Toast.LENGTH_SHORT).show();
            }else if(result.equalsIgnoreCase("FALSE")){
                obj.modificarEstadoSincronizado("2", "0");
                //Toast.makeText(thisContext, "400", Toast.LENGTH_SHORT).show();
            }else{
                obj.modificarEstadoSincronizado("2", "0");
                //Toast.makeText(thisContext, "500", Toast.LENGTH_SHORT).show();
            }
            MiAplicacionTareo.ENVIANDO=0;

        }
    }

    private String enviarXML_Http(){
        final TicketPersona objTPersona = new TicketPersona();
        final TicketCosecha objTCosecha = new TicketCosecha();
        final Tareo objTareo = new Tareo();
        final DetalleTareo objDTareo = new DetalleTareo();
        final Asistencia objAsistencia = new Asistencia();
        final SalidaPersonal objSalidaPersonal = new SalidaPersonal();
        final HoraPersonal objHoraPersonal = new HoraPersonal();
        final BinCabecera objC = new BinCabecera();
        final BinDetalle objD = new BinDetalle();
        final LecturaMaquinaria objLM = new LecturaMaquinaria();
        final SalidaAsistencia objSA = new SalidaAsistencia();

        final StringBuilder detalleTicketPersona = new StringBuilder();
        final StringBuilder detalleTicketCosecha = new StringBuilder();
        final StringBuilder tareos = new StringBuilder();
        final StringBuilder detalleTareo = new StringBuilder();
        final StringBuilder asistencia = new StringBuilder();
        final StringBuilder salidas = new StringBuilder();
        final StringBuilder horaPersonal = new StringBuilder();
        final StringBuilder bincabecera = new StringBuilder();
        final StringBuilder bindetalle = new StringBuilder();
        final StringBuilder detalleLMaquinaria = new StringBuilder();
        final StringBuilder salidaAsistencia = new StringBuilder();

        objTPersona.cargarListaTicketPersonaSQLite();
        objTCosecha.cargarListaTicketCosechaSQLite();
        objTareo.cargarListaTareoSQLite();
        objDTareo.obtenerRendimientosTareoXML();
        objAsistencia.obtenerListaAsistenciaTodasASubir();
        objSalidaPersonal.obtenerListaSalidasTodasASubir();
        objHoraPersonal.obtenerListaHoraPersonalASubir();
        objC.cargarXMLBinCabecera();
        objD.cargarXMLBinDetalle();
        objLM.cargarXMLMaquinariaDetalle();
        objSA.obtenerListaSalidaAsistenciaTodasASubir();

        detalleTicketPersona.append("<TICKETPERSONA>");
        for (String ticket : TicketPersona.listaTicketPersonaSQLiteSubir) {
            detalleTicketPersona.append(ticket);
        }
        detalleTicketPersona.append("</TICKETPERSONA>");

        detalleTicketCosecha.append("<TICKETCOSECHA>");
        for (String ticket : TicketCosecha.listaTicketCosechaSQLiteSubir) {
            detalleTicketCosecha.append(ticket);
        }
        detalleTicketCosecha.append("</TICKETCOSECHA>");

        tareos.append("<TAREO>");
        for (String tareo : Tareo.listaTareoSQLiteSubir) {
            tareos.append(tareo);
        }
        tareos.append("</TAREO>");

        detalleTareo.append("<DETTAREO>");
        for (String detTareo : DetalleTareo.listaDetalleTareoResumenSQLiteSubir) {
            detalleTareo.append(detTareo);
        }
        detalleTareo.append("</DETTAREO>");

        asistencia.append("<ASISTENCIA>");
        for (String asis : Asistencia.listaAsistenciaSQLiteSubir) {
            asistencia.append(asis);
        }
        asistencia.append("</ASISTENCIA>");

        salidas.append("<SALIDAPERSONAL>");
        for (String sal : SalidaPersonal.listaSalidasSQLiteSubir) {
            salidas.append(sal);
        }
        salidas.append("</SALIDAPERSONAL>");

        horaPersonal.append("<HORAPERSONAL>");
        for (String hp : HoraPersonal.listaPerCHManIngSQLiteSubir) {
            horaPersonal.append(hp);
        }
        horaPersonal.append("</HORAPERSONAL>");

        bincabecera.append("<BINCABECERA>");
        for (String hp : BinCabecera.listaBinCabeceraSQLiteSubir) {
            bincabecera.append(hp);
        }
        bincabecera.append("</BINCABECERA>");

        bindetalle.append("<BINDETALLE>");
        for (String hp : BinDetalle.listaBinDetalleSQLiteSubir) {
            bindetalle.append(hp);
        }
        bindetalle.append("</BINDETALLE>");

        detalleLMaquinaria.append("<LECTURAS_MAQUINARIA>");
        for (String hp : LecturaMaquinaria.listaLMaquinariaSubir) {
            detalleLMaquinaria.append(hp);
        }
        detalleLMaquinaria.append("</LECTURAS_MAQUINARIA>");

        salidaAsistencia.append("<SALIDA_ASISTENCIA>");
        for (String hp : SalidaAsistencia.listaSalidaAsistenciaSubirXML) {
            salidaAsistencia.append(hp);
        }
        salidaAsistencia.append("</SALIDA_ASISTENCIA>");

        String RPTA = "FALSE";

        Vector<HttpPostValues> parametros = new Vector<>();
        parametros.addElement(new HttpPostValues("Asistencia", asistencia.toString()));
        parametros.addElement(new HttpPostValues("Tareo", tareos.toString()));
        parametros.addElement(new HttpPostValues("DetalleTareo", detalleTareo.toString()));
        parametros.addElement(new HttpPostValues("IdUsuario", MiAplicacionTareo.IDUSUARIO));
        parametros.addElement(new HttpPostValues("SalidaPersonal", salidas.toString()));
        parametros.addElement(new HttpPostValues("HoraPersonal", horaPersonal.toString()));
        parametros.addElement(new HttpPostValues("TicketPersona", detalleTicketPersona.toString()));
        parametros.addElement(new HttpPostValues("TicketCosecha", detalleTicketCosecha.toString()));
        parametros.addElement(new HttpPostValues("BinCabecera", bincabecera.toString()));
        parametros.addElement(new HttpPostValues("BinDetalle", bindetalle.toString()));
        parametros.addElement(new HttpPostValues("LecturaMaquinaria", detalleLMaquinaria.toString()));
        parametros.addElement(new HttpPostValues("SalidaAsistencia", salidaAsistencia.toString()));

        try {
            String resultado = new Http().enviarPost(MiAplicacionTareo.URL_SERVICE_SUBIDA_DATOS, parametros);
            Log.i("RESPONSE", "|"+resultado);
            JSONObject json = new JSONObject(resultado);
            RPTA = json.getString("resultado");
        }catch (Exception e){
            if(e != null){
                RPTA=""+e.getMessage();
            }else{
                RPTA="ERROR_DESCONOCIDO";
            }
            //e.printStackTrace();
        }

        return RPTA;
    }

    //FIN NUEVA TRANSFERENCIA===================================================================================================================================================================


    public void enviarSms(String MENSAJE){
        Administrador objAdm = new Administrador();
        objAdm.cargarListaNumerosAdministrador();
        for(int i=0; i<Administrador.listaNumerosAdministradores.size(); i++){
            new TareaEnviarSMS().execute(Administrador.listaNumerosAdministradores.get(i).getNumero(), ""+MENSAJE);
        }
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
                //e.printStackTrace();
                return false;
                //
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

    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public Bitmap rotate2(Bitmap original, float degrees){
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        return Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
    }
}