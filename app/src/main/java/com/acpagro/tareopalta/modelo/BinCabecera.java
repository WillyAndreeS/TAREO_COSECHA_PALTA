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

public class BinCabecera extends SQLite {
    private String IDCABECERA, PLACA, CHOFER, IDCULTIVO, IDUSUARIO, FECHAREGISTRO, ESTADO, SINCRONIZADO, IDTELEFONO, UNIDAD_COD_SIS1, UNIDAD_COD_SIS2;
    public static int suma_viajes;
    public static String IDCABECERA_NEW, PLACA_NEW, CHOFER_NEW;
    private String BINES, SALIDA;
    public static ArrayList<BinCabecera> listaCabeceras = new ArrayList<>();
    public static List<String> listaBinCabeceraSQLiteSubir = new ArrayList<>();

    public BinCabecera(String IDCABECERA, String PLACA, String CHOFER, String IDCULTIVO, String IDUSUARIO, String FECHAREGISTRO, String ESTADO, String SINCRONIZADO, String IDTELEFONO) {
        this.IDCABECERA = IDCABECERA;
        this.PLACA = PLACA;
        this.CHOFER = CHOFER;
        this.IDCULTIVO = IDCULTIVO;
        this.IDUSUARIO = IDUSUARIO;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.ESTADO = ESTADO;
        this.SINCRONIZADO = SINCRONIZADO;
        this.IDTELEFONO = IDTELEFONO;
    }

    public BinCabecera(String IDCABECERA, String PLACA, String CHOFER, String IDCULTIVO, String IDUSUARIO, String FECHAREGISTRO, String ESTADO, String SINCRONIZADO, String IDTELEFONO, String BINES, String SALIDA) {
        this.IDCABECERA = IDCABECERA;
        this.PLACA = PLACA;
        this.CHOFER = CHOFER;
        this.IDCULTIVO = IDCULTIVO;
        this.IDUSUARIO = IDUSUARIO;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.ESTADO = ESTADO;
        this.SINCRONIZADO = SINCRONIZADO;
        this.IDTELEFONO = IDTELEFONO;
        this.BINES = BINES;
        this.SALIDA = SALIDA;
    }

    public BinCabecera() {
    }

    public String getUNIDAD_COD_SIS1() {
        return UNIDAD_COD_SIS1;
    }

    public void setUNIDAD_COD_SIS1(String UNIDAD_COD_SIS1) {
        this.UNIDAD_COD_SIS1 = UNIDAD_COD_SIS1;
    }

    public String getUNIDAD_COD_SIS2() {
        return UNIDAD_COD_SIS2;
    }

    public void setUNIDAD_COD_SIS2(String UNIDAD_COD_SIS2) {
        this.UNIDAD_COD_SIS2 = UNIDAD_COD_SIS2;
    }

    public String getBINES() {
        return BINES;
    }

    public void setBINES(String BINES) {
        this.BINES = BINES;
    }

    public String getSALIDA() {
        return SALIDA;
    }

    public void setSALIDA(String SALIDA) {
        this.SALIDA = SALIDA;
    }

    public String getIDCABECERA() {
        return IDCABECERA;
    }

    public void setIDCABECERA(String IDCABECERA) {
        this.IDCABECERA = IDCABECERA;
    }

    public String getPLACA() {
        return PLACA;
    }

    public void setPLACA(String PLACA) {
        this.PLACA = PLACA;
    }

    public String getCHOFER() {
        return CHOFER;
    }

    public void setCHOFER(String CHOFER) {
        this.CHOFER = CHOFER;
    }

    public String getIDCULTIVO() {
        return IDCULTIVO;
    }

    public void setIDCULTIVO(String IDCULTIVO) {
        this.IDCULTIVO = IDCULTIVO;
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

    public long limpiarTablaBinCabeceraSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("VIAJE_BINES_CABECERA", null, null);
        return 1;
    }

    public long modificarEstadoASincronizadoBinCabeceraSQLite(){
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","1");
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.update("VIAJE_BINES_CABECERA", cv, "SINCRONIZADO = '0'", null);
        return 1;
    }

    public void cargarXMLBinCabecera(){// BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","2");//ENVIANDO
        bd.update("VIAJE_BINES_CABECERA", cv, "SINCRONIZADO = '0'", null);

        String sql = "select IDCABECERA, PLACA, CHOFER, IDCULTIVO, IDUSUARIO, FECHAREGISTRO, IDTELEFONO, UNIDAD_COD_SIS1, UNIDAD_COD_SIS2 from VIAJE_BINES_CABECERA WHERE SINCRONIZADO = '2' order by 1 ";
        Cursor resultado = bd.rawQuery(sql, null);
        listaBinCabeceraSQLiteSubir.clear();
        while (resultado.moveToNext()){
            String items = "<Item IDCABECERA=\"" + resultado.getString(0) +
                    "\" PLACA=\"" + resultado.getString(1) +
                    "\" CHOFER=\"" + resultado.getString(2) +
                    "\" IDCULTIVO=\"" + resultado.getString(3) +
                    "\" IDUSUARIO=\"" + resultado.getString(4) +
                    "\" FECHAREGISTRO=\"" + resultado.getString(5).replace("-","") +
                    "\" IDTELEFONO=\"" + resultado.getString(6) +
                    "\" UNIDAD_COD_SIS1=\"" + resultado.getString(7) +
                    "\" UNIDAD_COD_SIS2=\"" + resultado.getString(8) + "\" />";
            listaBinCabeceraSQLiteSubir.add(items);
        }
        bd.close();
    }

    public long agregarBinCabecera(Context context, String PLACA, String CHOFER, String IDUSUARIO, String UNIDAD_COD_SIS1, String UNIDAD_COD_SIS2){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        DateFormat dateFormat2 = new SimpleDateFormat("YYMMddHHmmss");
        //DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        String idtelefono = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        //IDCABECERA_NEW = idtelefono+dateFormat2.format(date);
        IDCABECERA_NEW = IDUSUARIO+"_"+dateFormat2.format(date);
        PLACA_NEW = PLACA.toUpperCase();
        CHOFER_NEW = CHOFER.toUpperCase();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("IDCABECERA", IDCABECERA_NEW);
        values.put("PLACA", PLACA.toUpperCase());
        values.put("CHOFER", CHOFER.toUpperCase());
        values.put("IDCULTIVO", "0006");
        values.put("IDUSUARIO", IDUSUARIO);
        values.put("FECHAREGISTRO", fechaHoy);
        values.put("IDTELEFONO", idtelefono);
        values.put("UNIDAD_COD_SIS1", UNIDAD_COD_SIS1);
        values.put("UNIDAD_COD_SIS2", UNIDAD_COD_SIS2);
        long resultado = db.insert("VIAJE_BINES_CABECERA", null, values);
        return resultado;
    }

    public int getConteoPantallaLecturaPDASintransferir(){// BD SQLITE
        int conteo =0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT COUNT(*) AS CONTEO FROM VIAJE_BINES_CABECERA TC WHERE DATE(TC.FECHAREGISTRO, 'localtime') = '"+fechaHoy+"' AND SINCRONIZADO NOT IN('1')";
        Cursor resultado = bd.rawQuery(sql, null);
        while (resultado.moveToNext()){
            conteo = resultado.getInt(0);
        }
        bd.close();
        TicketCosecha.conteoLecturasSinTransferir = conteo;
        return conteo;
    }

    public void cargarListaCabecerasHoy(){// BD SQLITE
        suma_viajes = 0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        Log.i("TAREO", "ENTRO");
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT " +
                "C.IDCABECERA, IFNULL(C.PLACA, '') AS PLACA, C.CHOFER, C.IDCULTIVO, C.IDUSUARIO, " +
                "strftime('%d/%m/%Y', DATE(C.FECHAREGISTRO)) FECHAREGISTRO, C.ESTADO, " +
                "C.SINCRONIZADO, C.IDTELEFONO, " +
                "(SELECT COUNT(*) FROM VIAJE_BINES_DETALLE D WHERE D.IDCABECERA=C.IDCABECERA) AS BINES, " +
                "IFNULL((SELECT TIME(D.FECHAREGISTRO) FROM VIAJE_BINES_DETALLE D WHERE D.IDCABECERA=C.IDCABECERA ORDER BY D.FECHAREGISTRO DESC LIMIT 1), '--:--') AS SALIDA " +
                "FROM VIAJE_BINES_CABECERA C WHERE DATE(C.FECHAREGISTRO) = DATE('"+fechaHoy+"') ORDER BY C.FECHAREGISTRO DESC;";
        Cursor resultado = bd.rawQuery(sql, null);
        Log.i("TAREO", "CURSOR"+resultado.getCount());
        listaCabeceras.clear();
        while (resultado.moveToNext()){
            listaCabeceras.add(new BinCabecera(
                    ""+resultado.getString(0),
                    ""+resultado.getString(1),
                    ""+resultado.getString(2),
                    ""+resultado.getString(3),
                    ""+resultado.getString(4),
                    ""+resultado.getString(5),
                    ""+resultado.getString(6),
                    ""+resultado.getString(7),
                    ""+resultado.getString(8),
                    ""+resultado.getString(9),
                    ""+resultado.getString(10)
            ));
            suma_viajes = suma_viajes + resultado.getInt(9);
        }
        bd.close();
    }
}
