package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Log;

import com.acpagro.tareopalta.datos.SQLite;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BinDetalle extends SQLite {
    private String IDDETALLE, IDCABECERA, IDBIN, IDCONSUMIDOR, IDVARIEDAD, IDVALVULA, IDUSUARIO, FECHAREGISTRO, ESTADO, SINCRONIZADO, IDTELEFONO;
    public static ArrayList<BinDetalle> listaDetalle = new ArrayList<>();
    private String BINES;
    public static int suma_bines;
    public static List<String> listaBinDetalleSQLiteSubir = new ArrayList<>();
    public static String contenido_sms="";

    public long limpiarTablaBinDetalleSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("VIAJE_BINES_DETALLE", null, null);
        return 1;
    }

    public long modificarEstadoASincronizadoBinDetalleSQLite(){
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","1");
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.update("VIAJE_BINES_DETALLE", cv, "SINCRONIZADO = '0'", null);
        return 1;
    }

    public void cargarXMLBinDetalle(){// BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","2");//ENVIANDO
        bd.update("VIAJE_BINES_DETALLE", cv, "SINCRONIZADO = '0'", null);
        String sql = "select IDDETALLE, IDCABECERA, IDBIN, IDCONSUMIDOR, IDVARIEDAD, IDVALVULA, IDUSUARIO, FECHAREGISTRO, IDTELEFONO from VIAJE_BINES_DETALLE WHERE SINCRONIZADO = '2' order by 1 ";
        Cursor resultado = bd.rawQuery(sql, null);
        listaBinDetalleSQLiteSubir.clear();
        while (resultado.moveToNext()){
            String items = "<Item IDDETALLE=\"" + resultado.getString(0) +
                    "\" IDCABECERA=\"" + resultado.getString(1) +
                    "\" IDBIN=\"" + resultado.getString(2) +
                    "\" IDCONSUMIDOR=\"" + resultado.getString(3) +
                    "\" IDVARIEDAD=\"" + resultado.getString(4) +
                    "\" IDVALVULA=\"" + resultado.getString(5) +
                    "\" IDUSUARIO=\"" + resultado.getString(6) +
                    "\" FECHAREGISTRO=\"" + resultado.getString(7).replace("-","") +
                    "\" IDTELEFONO=\"" + resultado.getString(8) + "\" />";
            listaBinDetalleSQLiteSubir.add(items);
        }
        bd.close();
    }

    public long agregarLecturaBin(Context context, String IDCABECERA, String IDBIN, String IDCONSUMIDOR, String IDVARIEDAD, String IDVALVULA, String IDUSUARIO){//Lectura con PDA
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd-HHmmss");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        String idtelefono = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("IDDETALLE", idtelefono+"_"+dateFormat2.format(date));
        values.put("IDCABECERA", IDCABECERA);
        values.put("IDBIN", IDBIN);
        values.put("IDCONSUMIDOR", IDCONSUMIDOR);
        values.put("IDVARIEDAD", IDVARIEDAD);
        values.put("IDVALVULA", IDVALVULA);
        values.put("IDUSUARIO", IDUSUARIO);
        values.put("FECHAREGISTRO", fechaHoy);
        values.put("IDTELEFONO", idtelefono);
        long resultado = db.insert("VIAJE_BINES_DETALLE", null, values);
        db.close();
        return resultado;
    }

    public void cargarListaDetalleBines(String IDCABECERA){// BD SQLITE
        contenido_sms = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT B.IDCONSUMIDOR, COUNT(*) BINES FROM VIAJE_BINES_DETALLE B WHERE B.IDCABECERA = '"+IDCABECERA+"' GROUP BY B.IDCONSUMIDOR";
        Cursor resultado = bd.rawQuery(sql, null);
        listaDetalle.clear();
        suma_bines = 0;
        while (resultado.moveToNext()){
            suma_bines += resultado.getInt(1);
            listaDetalle.add(new BinDetalle(
                    resultado.getString(0),
                    resultado.getString(1)
            ));
            contenido_sms = contenido_sms+resultado.getString(0)+" => "+resultado.getString(1)+" BIN.\n";
            Log.i("ITEM", ""+resultado.getString(0) + "/" + resultado.getCount());
        }
        bd.close();
    }

    public BinDetalle(String IDDETALLE, String IDCABECERA, String IDBIN, String IDCONSUMIDOR, String IDVARIEDAD, String IDVALVULA, String IDUSUARIO, String FECHAREGISTRO, String ESTADO, String SINCRONIZADO, String IDTELEFONO, String BINES) {
        this.IDDETALLE = IDDETALLE;
        this.IDCABECERA = IDCABECERA;
        this.IDBIN = IDBIN;
        this.IDCONSUMIDOR = IDCONSUMIDOR;
        this.IDVARIEDAD = IDVARIEDAD;
        this.IDVALVULA = IDVALVULA;
        this.IDUSUARIO = IDUSUARIO;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.ESTADO = ESTADO;
        this.SINCRONIZADO = SINCRONIZADO;
        this.IDTELEFONO = IDTELEFONO;
        this.BINES = BINES;
    }

    public BinDetalle(String IDCONSUMIDOR, String BINES) {
        this.IDCONSUMIDOR = IDCONSUMIDOR;
        this.BINES = BINES;
    }

    public BinDetalle() {
    }

    public String getIDDETALLE() {
        return IDDETALLE;
    }

    public void setIDDETALLE(String IDDETALLE) {
        this.IDDETALLE = IDDETALLE;
    }

    public String getIDCABECERA() {
        return IDCABECERA;
    }

    public void setIDCABECERA(String IDCABECERA) {
        this.IDCABECERA = IDCABECERA;
    }

    public String getIDBIN() {
        return IDBIN;
    }

    public void setIDBIN(String IDBIN) {
        this.IDBIN = IDBIN;
    }

    public String getIDCONSUMIDOR() {
        return IDCONSUMIDOR;
    }

    public void setIDCONSUMIDOR(String IDCONSUMIDOR) {
        this.IDCONSUMIDOR = IDCONSUMIDOR;
    }

    public String getIDVARIEDAD() {
        return IDVARIEDAD;
    }

    public void setIDVARIEDAD(String IDVARIEDAD) {
        this.IDVARIEDAD = IDVARIEDAD;
    }

    public String getIDVALVULA() {
        return IDVALVULA;
    }

    public void setIDVALVULA(String IDVALVULA) {
        this.IDVALVULA = IDVALVULA;
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

    public String getIDTELEFONO() {
        return IDTELEFONO;
    }

    public void setIDTELEFONO(String IDTELEFONO) {
        this.IDTELEFONO = IDTELEFONO;
    }

    public String getBINES() {
        return BINES;
    }

    public void setBINES(String BINES) {
        this.BINES = BINES;
    }
}
