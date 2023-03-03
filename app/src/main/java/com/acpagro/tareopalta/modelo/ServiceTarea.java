package com.acpagro.tareopalta.modelo;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.acpagro.tareopalta.datos.Http;
import com.acpagro.tareopalta.datos.HttpPostValues;

import org.json.JSONObject;
import java.util.Vector;

public class ServiceTarea extends Service {
    private Context thisContext = this;
    //private final int tiempo_segundos = 20000;//15 segundos
    private final int tiempo_segundos = 60000 * 3;//3 Minutos
    LocationManager locationManager;

    public Handler handler = null;
    public static Runnable runnable = null;
    MiAplicacionTareo obj;

    @Override
    public void onCreate(){
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                //Toast.makeText(thisContext, "Ejeción cada 60 Segundos, Iniciando GPS", Toast.LENGTH_SHORT).show();
                //obtenerUbicacion();
                obj = new MiAplicacionTareo();
                if(obj.getConteoDatosParaTransferir()>0){
                    if(MiAplicacionTareo.hayConexion(getApplicationContext())){
                        if(MiAplicacionTareo.ENVIANDO==0){
                            //Toast.makeText(getApplicationContext(), "ENVIAADO", Toast.LENGTH_SHORT).show();
                            new TareaEnviarDataXMLHtml().execute();
                        }
                    }
                }else{
                    Log.i("CONTEO>0", "NO");
                }
                handler.postDelayed(runnable, tiempo_segundos);
            }
        };

        handler.postDelayed(runnable, tiempo_segundos);
        //obtenerUbicacion();


    }

    @Override
    public int onStartCommand(Intent intent, int flag, int idProcess){
        /*mediaPlayer = MediaPlayer.create(thisContext, R.raw.dejala_que_vuelva);
        mediaPlayer.start();*/

        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        if(handler!= null && runnable != null){
            Log.i("SERVICE", "DESTROY NO_NULL HANDLER-RUNNABLE");
            handler.removeCallbacks(runnable);
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //INICIO NUEVA TRANSFERENCIA================================================================================================================================================================

    private class TareaEnviarDataXMLHtml extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MiAplicacionTareo.cargar_preferenciasLogin(thisContext);
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

        Log.i("STRINGBUILDER----", "--------------------------------");
        Log.i("STRINGBUILDER-ASIS", String.valueOf(asistencia));
        Log.i("STRINGBUILDER-TAR", String.valueOf(tareos));
        Log.i("STRINGBUILDER-DETAR", String.valueOf(detalleTareo));
        Log.i("STRINGBUILDER-IDUSU", MiAplicacionTareo.IDUSUARIO);
        Log.i("STRINGBUILDER-SALID", String.valueOf(salidas));
        Log.i("STRINGBUILDER-HORPE", String.valueOf(horaPersonal));
        Log.i("STRINGBUILDER-TPER", String.valueOf(detalleTicketPersona));
        Log.i("STRINGBUILDER-TCOS", String.valueOf(detalleTicketCosecha));
        Log.i("STRINGBUILDER-BCAB", String.valueOf(bincabecera));
        Log.i("STRINGBUILDER-BDET", String.valueOf(bindetalle));
        Log.i("STRINGBUILDER-LMAQ", String.valueOf(detalleLMaquinaria));
        Log.i("STRINGBUILDER-SALASI", String.valueOf(salidaAsistencia));

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
            e.printStackTrace();
        }

        return RPTA;
    }

    //FIN NUEVA TRANSFERENCIA===================================================================================================================================================================

}
