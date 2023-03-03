package com.acpagro.tareopalta.modelo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;

import com.acpagro.tareopalta.datos.SQLite;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Locale;

public class MiAplicacionTareo extends SQLite{
    public static String IDUSUARIO;
    public static String DNIUSUARIO;
    public static String CLAVE;
    public static String IDCULTIVO;
    public static String NOMBRES;
    public static String LEE_PDA;
    public static String TIPO_USUARIO;
    public static String CODIGOTAREOCOSECHA;

    /*public static String URL_SERVICE = "http://190.223.54.4/acp/index.php/sincronizarmovil/";
    public static String URL_SERVICE_SUBIDA = "http://190.223.54.4/acp/index.php/loadmovil/";
    public static String URL_SERVICE_SUBIDA_DATOS = "http://190.223.54.4/acp/index.php/loadmovil/setLoadmovilPalta_v2";//TRANSFERENCIA AUTOMATICA Y MANUAL / ACTUAL: setLoadmovilPalta_v5
    public static String URL_SERVICE_REPORTES = "http://190.223.54.4/acp/index.php/tareomovilarandano/";
    public static String URL_SERVICE_REPORTES_GRAFICOS = "http://190.223.54.4/acp/index.php/reportetareo/";*/

    public static String URL_SERVICE = "https://web.acpagro.com/acp/index.php/sincronizarmovil/";
    public static String URL_SERVICE_SUBIDA = "https://web.acpagro.com/acp/index.php/loadmovil/";
    public static String URL_SERVICE_SUBIDA_DATOS = "https://web.acpagro.com/acp/index.php/loadmovil/setLoadmovilPalta_v2";//TRANSFERENCIA AUTOMATICA Y MANUAL / ACTUAL: setLoadmovilPalta_v5
    public static String URL_SERVICE_REPORTES = "https://web.acpagro.com/acp/index.php/tareomovilarandano/";
    public static String URL_SERVICE_REPORTES_GRAFICOS = "https://web.acpagro.com/acp/index.php/reportetareo/";

    public static Double KG_JABA = 1.50;
    public static int CONTADOR_PROGRESS;
    public static String id_consumidor_seleccionado="";
    public static String consumidor_seleccionado="";
    public static int posicion_valvula=0;
    public static int state_block = 0;
    public static int ENVIANDO=0;

    public static Boolean ESTADO_IMPRESORA=false;
    public static String IMPRESORA = "";
    public static String MAC_BT = "";

    //public static float distancia_minima_reconocimiento = 1.000f;
    public static float distancia_minima_reconocimiento = 0.800f;


    public static TextToSpeech textToSpeech;

    public static void iniciarLectorVoz(Context context){
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    //t1.setLanguage (Locale.ENGLISH);
                    MiAplicacionTareo.textToSpeech.setLanguage (Locale.getDefault());
                }
            }
        } );
    }

    public static void finalizarLectorVoz(){
        if (textToSpeech != null) {
            textToSpeech.stop ();
            textToSpeech.shutdown ();
        }
    }

    public static void leer_voz(String texto){
        try {
            if(textToSpeech!=null){
                textToSpeech.speak ( texto , TextToSpeech.QUEUE_FLUSH , null );
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static boolean esNumero(String cadena){
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }

    public static boolean validarNumero(String cadena) {
        if (cadena.matches("[0-9]*")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean hayConexion(Context contexto){
        ConnectivityManager connectivityManager = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static void generarCodigo128(String codigo, ImageView imv){
        //codigo = "_5ZNDCOTBGNCMOI";
        try {
            MultiFormatWriter multi = new MultiFormatWriter();
            BitMatrix bitMatrix = new Code128Writer().encode(codigo, BarcodeFormat.CODE_128, 1450, 160);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imv.setImageBitmap(bitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }
    }

    public static void cargar_preferenciasIMPRESORA(Context contexto){
        Log.i("PREFERENCIAS", "PRINCIPAL");
        SharedPreferences prefs = contexto.getSharedPreferences("preferenciasIMPRESORA", Context.MODE_PRIVATE);
        MiAplicacionTareo.IMPRESORA = prefs.getString("IMPRESORA", "ZEBRA");
        MiAplicacionTareo.MAC_BT = prefs.getString("MAC_IMPRESORA", "CC:78:AB:9F:83:FC");//MAX_BT_ZEBRA_PLANTA POR DEFECTO
    }

    public static void guardar_preferenciaLogin(Context context){
        SharedPreferences prefs = context.getSharedPreferences("PreferenciasTareoAraACP",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("P_IDUSUARIO", MiAplicacionTareo.IDUSUARIO);
        editor.putString("P_DNIUSUARIO", MiAplicacionTareo.DNIUSUARIO);
        editor.putString("P_CLAVE", MiAplicacionTareo.CLAVE);
        editor.putString("P_IDCULTIVO", MiAplicacionTareo.IDCULTIVO);
        editor.putString("P_CODIGOTAREOCOSECHA", MiAplicacionTareo.CODIGOTAREOCOSECHA);
        editor.putString("P_NOMBRES", MiAplicacionTareo.NOMBRES);
        editor.putString("P_LEE_PDA", MiAplicacionTareo.LEE_PDA);
        editor.putString("P_TIPO_USUARIO", MiAplicacionTareo.TIPO_USUARIO);
        editor.commit();
    }

    public static void cargar_preferenciasLogin(Context context){
        SharedPreferences prefs = context.getSharedPreferences("PreferenciasTareoAraACP", Context.MODE_PRIVATE);
        MiAplicacionTareo.IDUSUARIO = prefs.getString("P_IDUSUARIO", "");
        MiAplicacionTareo.DNIUSUARIO = prefs.getString("P_DNIUSUARIO", "");
        MiAplicacionTareo.CODIGOTAREOCOSECHA = prefs.getString("P_CODIGOTAREOCOSECHA", "");
        MiAplicacionTareo.CLAVE = prefs.getString("P_CLAVE", "");
        MiAplicacionTareo.IDCULTIVO = prefs.getString("P_IDCULTIVO", "");
        MiAplicacionTareo.NOMBRES = prefs.getString("P_NOMBRES", "");
        MiAplicacionTareo.LEE_PDA = prefs.getString("P_LEE_PDA", "");
        MiAplicacionTareo.TIPO_USUARIO = prefs.getString("P_TIPO_USUARIO", "");
    }

    public static void vibrate(int duration, Activity activity)
    {
        Vibrator vibs = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        vibs.vibrate(duration);
    }

    public static String diaAbrevitura(String diaNombre){
        String diaN = diaNombre.substring(0, 3).toLowerCase();
        String resultado = "";
        switch (diaN){
            case "lun":
                resultado = "LUN";
                break;
            case "mar":
                resultado = "MAR";
                break;
            case "mie":
                resultado = "MIE";
                break;
            case "mié":
                resultado = "MIE";
                break;
            case "jue":
                resultado = "JUE";
                break;
            case "vie":
                resultado = "VIE";
                break;
            case "sáb":
                resultado = "SAB";
                break;
            case "dom":
                resultado = "DOM";
                break;
            case "mon":
                resultado = "LUN";
                break;
            case "tue":
                resultado = "MAR";
                break;
            case "wed":
                resultado = "MIE";
                break;
            case "thu":
                resultado = "JUE";
                break;
            case "fri":
                resultado = "VIE";
                break;
            case "sat":
                resultado = "SAB";
                break;
            case "sun":
                resultado = "DOM";
                break;
        }

        return resultado;
    }

    public static String horaOMin(int hOm){
        String rpta = "";
        switch (hOm){
            case 0:
                rpta = "00";
                break;
            case 1:
                rpta = "01";
                break;
            case 2:
                rpta = "02";
                break;
            case 3:
                rpta = "03";
                break;
            case 4:
                rpta = "04";
                break;
            case 5:
                rpta = "05";
                break;
            case 6:
                rpta = "06";
                break;
            case 7:
                rpta = "07";
                break;
            case 8:
                rpta = "08";
                break;
            case 9:
                rpta = "09";
                break;
            default:
                rpta = String.valueOf(hOm);
        }
        return rpta;
    }

    public int getConteoDatosParaTransferir(){// BD SQLITE
        int conteo=0;
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql ="select " +
                "(select count(*) from TAREO WHERE SINCRONIZADO = '0') AS CONTEO1, " +
                "(select count(*) from TICKETPERSONA WHERE SINCRONIZADO = '0') AS CONTEO2, " +
                "(select count(*) from TICKETCOSECHA WHERE SINCRONIZADO = '0') AS CONTEO3, " +
                "(SELECT COUNT(*) FROM DETALLETAREO WHERE SINCRONIZADO = '0') AS CONTEO4, " +
                "(SELECT COUNT(*) FROM ASISTENCIA WHERE SINCRONIZADO = '0') AS CONTEO5, " +
                "(SELECT COUNT(*) FROM SALIDAPERSONAL WHERE SINCRONIZADO = '0') AS CONTEO6, " +
                "(SELECT COUNT(*) FROM HORAPERSONAL WHERE SINCRONIZADO = '0') AS CONTEO7, " +
                "(SELECT COUNT(*) FROM VIAJE_BINES_CABECERA WHERE SINCRONIZADO = '0') AS CONTEO8, " +
                "(SELECT COUNT(*) FROM VIAJE_BINES_DETALLE WHERE SINCRONIZADO = '0') AS CONTEO9, " +
                "(SELECT COUNT(*) FROM LECTURA_MAQUINARIA WHERE SINCRONIZADO = '0') AS CONTEO10, " +
                "(SELECT COUNT(*) FROM SALIDA_ASISTENCIA WHERE SINCRONIZADO = '0') AS CONTEO11";
        Cursor resultado = bd.rawQuery(sql, null);
        while (resultado.moveToNext()){
            conteo=resultado.getInt(0)+resultado.getInt(1)+resultado.getInt(2)+resultado.getInt(3)+resultado.getInt(4)+resultado.getInt(5)+resultado.getInt(6)+resultado.getInt(7)+resultado.getInt(8)+resultado.getInt(9)+resultado.getInt(10);
        }
        bd.close();
        return conteo;
    }

    public long modificarEstadoSincronizado(String old_, String new_){
        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues cv;
        cv = new ContentValues();
        cv.put("SINCRONIZADO",new_);
        bd.update("TICKETPERSONA", cv, "SINCRONIZADO = '"+old_+"'", null);
        bd.update("TICKETCOSECHA", cv, "SINCRONIZADO = '"+old_+"'", null);
        bd.update("TAREO", cv, "SINCRONIZADO = '"+old_+"'", null);
        bd.update("DETALLETAREO", cv, "SINCRONIZADO = '"+old_+"'", null);
        bd.update("ASISTENCIA", cv, "SINCRONIZADO = '"+old_+"'", null);
        bd.update("SALIDAPERSONAL", cv, "SINCRONIZADO = '"+old_+"'", null);
        bd.update("HORAPERSONAL", cv, "SINCRONIZADO = '"+old_+"'", null);
        bd.update("VIAJE_BINES_CABECERA", cv, "SINCRONIZADO = '"+old_+"'", null);
        bd.update("VIAJE_BINES_DETALLE", cv, "SINCRONIZADO = '"+old_+"'", null);
        bd.update("LECTURA_MAQUINARIA", cv, "SINCRONIZADO = '"+old_+"'", null);
        bd.update("SALIDA_ASISTENCIA", cv, "SINCRONIZADO = '"+old_+"'", null);
        return 1;
    }
}
