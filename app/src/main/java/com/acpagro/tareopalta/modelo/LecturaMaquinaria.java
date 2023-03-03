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

public class LecturaMaquinaria extends SQLite {
    //IDPORTABIN VARCHAR(50), IDTRAZABILIDAD VARCHAR(50), IDBIN VARCHAR(50), IDUSUARIO INT, FECHAREGISTRO DATETIME, ESTADO BIT DEFAULT 1, SINCRONIZADO CHAR(1) DEFAULT '0', IDTELEFONO VARCHAR(50)
    private String IDPORTABIN, IDTRAZABILIDAD, IDBIN, IDUSUARIO, FECHAREGISTRO, ESTADO, SINCRONIZADO, IDTELEFONO;
    private String CANTIDAD;
    public static int suma_bines = 0;
    public static ArrayList<LecturaMaquinaria> listaResumen = new ArrayList<>();
    public static ArrayList<String> listaLMaquinariaSubir = new ArrayList<>();

    public long limpiarTablaLecturaMaquinariaSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("LECTURA_MAQUINARIA", null, null);
        return 1;
    }

    public void cargarXMLMaquinariaDetalle(){// BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","2");//ENVIANDO
        bd.update("LECTURA_MAQUINARIA", cv, "SINCRONIZADO = '0'", null);
        String sql = "select IDPORTABIN, IDTRAZABILIDAD, IDBIN, IDUSUARIO, FECHAREGISTRO, IDTELEFONO from LECTURA_MAQUINARIA WHERE SINCRONIZADO = '2' order by 1 ";
        Cursor resultado = bd.rawQuery(sql, null);
        listaLMaquinariaSubir.clear();
        while (resultado.moveToNext()){
            String items = "<Item IDPORTABIN=\"" + resultado.getString(0) +
                    "\" IDTRAZABILIDAD=\"" + resultado.getString(1) +
                    "\" IDBIN=\"" + resultado.getString(2) +
                    "\" IDUSUARIO=\"" + resultado.getString(3) +
                    "\" FECHAREGISTRO=\"" + resultado.getString(4).replace("-","") +
                    "\" IDTELEFONO=\"" + resultado.getString(5) + "\" />";
            listaLMaquinariaSubir.add(items);
        }
        bd.close();
    }

    public long agregarLecturaMaquinaria(Context context, String IDPORTABIN, String IDTRAZABILIDAD, String IDBIN, String IDUSUARIO){//Lectura con PDA Maquinaria
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        String idtelefono = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        SQLiteDatabase db = this.getWritableDatabase();

        int conteo = 0;
        String sql = "SELECT COUNT(*) FROM LECTURA_MAQUINARIA WHERE DATE(FECHAREGISTRO) = '"+dateFormat2.format(date)+"' AND IDTRAZABILIDAD='"+IDTRAZABILIDAD+"' AND IDBIN='"+IDBIN+"';";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()){
            conteo = cursor.getInt(0);
        }


        long resultado = -1;
        if(conteo == 0){
            ContentValues values = new ContentValues();
            values.put("IDPORTABIN", IDPORTABIN);
            values.put("IDTRAZABILIDAD", IDTRAZABILIDAD);
            values.put("IDBIN", IDBIN);
            values.put("IDUSUARIO", IDUSUARIO);
            values.put("FECHAREGISTRO", fechaHoy);
            values.put("IDTELEFONO", idtelefono);
            resultado = db.insert("LECTURA_MAQUINARIA", null, values);
        }


        /*ContentValues values = new ContentValues();
        values.put("IDPORTABIN", IDPORTABIN);
        values.put("IDTRAZABILIDAD", IDTRAZABILIDAD);
        values.put("IDBIN", IDBIN);
        values.put("IDUSUARIO", IDUSUARIO);
        values.put("FECHAREGISTRO", fechaHoy);
        values.put("IDTELEFONO", idtelefono);
        long resultado = db.insert("LECTURA_MAQUINARIA", null, values);*/
        db.close();
        return resultado;
    }

    public void cargarResumenLecturaMaquinaria(){// BD SQLITE
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT B.IDPORTABIN, COUNT(*) BINES FROM LECTURA_MAQUINARIA B WHERE DATE(B.FECHAREGISTRO) = '"+fechaHoy+"' GROUP BY B.IDPORTABIN";
        Cursor resultado = bd.rawQuery(sql, null);
        listaResumen.clear();
        suma_bines = 0;
        while (resultado.moveToNext()){
            suma_bines += resultado.getInt(1);
            listaResumen.add(new LecturaMaquinaria(
                    resultado.getString(0),
                    resultado.getString(1)
            ));
            Log.i("ITEM", ""+resultado.getString(0) + "/" + resultado.getCount());
        }
        bd.close();
    }

    public LecturaMaquinaria(String IDPORTABIN, String IDTRAZABILIDAD, String IDBIN, String IDUSUARIO, String FECHAREGISTRO, String ESTADO, String SINCRONIZADO, String IDTELEFONO) {
        this.IDPORTABIN = IDPORTABIN;
        this.IDTRAZABILIDAD = IDTRAZABILIDAD;
        this.IDBIN = IDBIN;
        this.IDUSUARIO = IDUSUARIO;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.ESTADO = ESTADO;
        this.SINCRONIZADO = SINCRONIZADO;
        this.IDTELEFONO = IDTELEFONO;
    }

    public LecturaMaquinaria() {
    }

    public LecturaMaquinaria(String IDPORTABIN, String CANTIDAD) {
        this.IDPORTABIN = IDPORTABIN;
        this.CANTIDAD = CANTIDAD;
    }

    public String getCANTIDAD() {
        return CANTIDAD;
    }

    public void setCANTIDAD(String CANTIDAD) {
        this.CANTIDAD = CANTIDAD;
    }

    public String getIDPORTABIN() {
        return IDPORTABIN;
    }

    public void setIDPORTABIN(String IDPORTABIN) {
        this.IDPORTABIN = IDPORTABIN;
    }

    public String getIDTRAZABILIDAD() {
        return IDTRAZABILIDAD;
    }

    public void setIDTRAZABILIDAD(String IDTRAZABILIDAD) {
        this.IDTRAZABILIDAD = IDTRAZABILIDAD;
    }

    public String getIDBIN() {
        return IDBIN;
    }

    public void setIDBIN(String IDBIN) {
        this.IDBIN = IDBIN;
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
}
