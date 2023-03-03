package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Log;

import com.acpagro.tareopalta.PantallaAsistenciaPersonal;
import com.acpagro.tareopalta.datos.SQLite;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalidaAsistencia extends SQLite {
    private String DNI, FECHA, IDUSUARIO, FECHAREGISTRO, ESTADO, SINCRONIZADO;
    private String NOMBRE_COMPLETO, HORA;
    public static List<String> listaSalidaAsistenciaSubirXML = new ArrayList<>();
    public static ArrayList<SalidaAsistencia> listaSalida = new ArrayList<>();

    public void obtenerListaSalidaAsistenciaTodasASubir(){
        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","2");//ENVIANDO
        bd.update("SALIDA_ASISTENCIA", cv, "SINCRONIZADO = '0'", null);
        String sql = "select DNI, IDUSUARIO, FECHA, FECHAREGISTRO, IDTELEFONO FROM SALIDA_ASISTENCIA WHERE SINCRONIZADO = '2'";
        Cursor resultado = bd.rawQuery(sql, null);
        listaSalidaAsistenciaSubirXML.clear();
        while (resultado.moveToNext()){
            String items = "<Item DNI=\"" + resultado.getString(0)
                    + "\" IDUSUARIO=\"" + resultado.getString(1)
                    + "\" FECHA=\"" + resultado.getString(2).replace("-","")
                    + "\" FECHAREGISTRO=\"" + resultado.getString(3).replace("-","")
                    + "\" IDTELEFONO=\"" + resultado.getString(4) + "\" />";
            listaSalidaAsistenciaSubirXML.add(items);
        }
    }

    public long limpiarTablaSalidaAsistenciaSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("SALIDA_ASISTENCIA", null, null);
        return 1;
    }

    public long agregarSalidaAsistenciaSQLite(String DNI, String IDUSUARIO, Context context){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String idtelefono = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        String fechaHoy_ = dateFormat2.format(date);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("DNI", DNI);
        values.put("IDUSUARIO", IDUSUARIO);
        values.put("IDTELEFONO", idtelefono);
        values.put("FECHA", fechaHoy);
        values.put("FECHAREGISTRO", fechaHoy_);
        long resultado = db.insert("SALIDA_ASISTENCIA", null, values);
        db.close();
        return resultado;
    }

    public long eliminarSalidaAsistenciaSQLite(String DNI, String FECHA){
        SQLiteDatabase bd = this.getReadableDatabase();
        long r = bd.delete("SALIDA_ASISTENCIA", "DNI = '"+DNI+"' AND FECHA = '"+FECHA+"'" , null);
        Log.i("DELETE", ""+r);
        return 1;
    }

    public boolean obtenerListaSalidaAsistenciaDelDia(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);

        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select a.DNI, " +
                "IFNULL(p.NOMBRES, 'NO REGISTRADO') as NOMBRES, " +
                "a.FECHA, " +
                "a.IDUSUARIO, " +
                "a.FECHAREGISTRO, " +
                "a.ESTADO, " +
                "a.SINCRONIZADO, " +
                "strftime('%H:%M:%S', a.FECHAREGISTRO) HORA " +
                "from SALIDA_ASISTENCIA a LEFT JOIN PERSONALGENERAL p ON (a.DNI = p.DNI) " +
                "where a.FECHA = '"+fechaHoy+"' order by 2 DESC";//4 DESC
        Cursor resultado = bd.rawQuery(sql, null);
        listaSalida.clear();
        while (resultado.moveToNext()){
            listaSalida.add(new SalidaAsistencia(
                    ""+resultado.getString(0),
                    ""+resultado.getString(2),
                    ""+resultado.getString(3),
                    ""+resultado.getString(4),
                    ""+resultado.getString(5),
                    ""+resultado.getString(6),
                    ""+resultado.getString(1),
                    ""+resultado.getString(7)
            ));
            Log.i("SALIDA_ASISTENCIA", resultado.getString(0)+ "//" + resultado.getString(1) + "//" + resultado.getString(2) + "//" + resultado.getString(3));
        }
        bd.close();

        if(listaSalida.size() != 0){
            return true;
        }else{
            return false;
        }
    }

    public SalidaAsistencia(String DNI, String FECHA, String IDUSUARIO, String FECHAREGISTRO, String ESTADO, String SINCRONIZADO, String NOMBRE_COMPLETO, String HORA) {
        this.DNI = DNI;
        this.FECHA = FECHA;
        this.IDUSUARIO = IDUSUARIO;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.ESTADO = ESTADO;
        this.SINCRONIZADO = SINCRONIZADO;
        this.NOMBRE_COMPLETO = NOMBRE_COMPLETO;
        this.HORA = HORA;
    }

    public SalidaAsistencia() {
    }

    public String getHORA() {
        return HORA;
    }

    public void setHORA(String HORA) {
        this.HORA = HORA;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public String getFECHA() {
        return FECHA;
    }

    public void setFECHA(String FECHA) {
        this.FECHA = FECHA;
    }

    public String getIDUSUARIO() {
        return IDUSUARIO;
    }

    public void setIDUSUARIO(String IDUSUARIO) {
        this.IDUSUARIO = IDUSUARIO;
    }

    public String getFECHAREGISTRO() {
        return FECHAREGISTRO;
    }

    public void setFECHAREGISTRO(String FECHAREGISTRO) {
        this.FECHAREGISTRO = FECHAREGISTRO;
    }

    public String getESTADO() {
        return ESTADO;
    }

    public void setESTADO(String ESTADO) {
        this.ESTADO = ESTADO;
    }

    public String getSINCRONIZADO() {
        return SINCRONIZADO;
    }

    public void setSINCRONIZADO(String SINCRONIZADO) {
        this.SINCRONIZADO = SINCRONIZADO;
    }

    public String getNOMBRE_COMPLETO() {
        return NOMBRE_COMPLETO;
    }

    public void setNOMBRE_COMPLETO(String NOMBRE_COMPLETO) {
        this.NOMBRE_COMPLETO = NOMBRE_COMPLETO;
    }
}
