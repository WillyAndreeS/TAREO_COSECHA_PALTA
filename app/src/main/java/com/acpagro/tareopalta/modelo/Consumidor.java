package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.acpagro.tareopalta.datos.SQLite;

import java.util.ArrayList;

public class Consumidor extends SQLite {
    private String IDEMPRESA;
    private String IDCONSUMIDOR;
    private String CONSUMIDOR;
    private String IDCULTIVO;
    private String CULTIVO;

    public static ArrayList<Consumidor> listaConsumidorHost = new ArrayList<Consumidor>();
    public static ArrayList<Consumidor> listaConsumidorSQLite = new ArrayList<Consumidor>();
    public static ArrayList<String> listaConsumidorSQLiteString = new ArrayList<String>();

    public Consumidor() {
    }

    public Consumidor(String IDEMPRESA, String IDCONSUMIDOR, String CONSUMIDOR, String IDCULTIVO, String CULTIVO) {
        this.IDEMPRESA = IDEMPRESA;
        this.IDCONSUMIDOR = IDCONSUMIDOR;
        this.CONSUMIDOR = CONSUMIDOR;
        this.IDCULTIVO = IDCULTIVO;
        this.CULTIVO = CULTIVO;
    }

    public String getIDEMPRESA() {
        return IDEMPRESA;
    }

    public void setIDEMPRESA(String IDEMPRESA) {
        this.IDEMPRESA = IDEMPRESA;
    }

    public String getIDCONSUMIDOR() {
        return IDCONSUMIDOR;
    }

    public void setIDCONSUMIDOR(String IDCONSUMIDOR) {
        this.IDCONSUMIDOR = IDCONSUMIDOR;
    }

    public String getCONSUMIDOR() {
        return CONSUMIDOR;
    }

    public void setCONSUMIDOR(String CONSUMIDOR) {
        this.CONSUMIDOR = CONSUMIDOR;
    }

    public String getIDCULTIVO() {
        return IDCULTIVO;
    }

    public void setIDCULTIVO(String IDCULTIVO) {
        this.IDCULTIVO = IDCULTIVO;
    }

    public String getCULTIVO() {
        return CULTIVO;
    }

    public void setCULTIVO(String CULTIVO) {
        this.CULTIVO = CULTIVO;
    }

    public long agregarConsumidorSQLite(String IDEMPRESA, String IDCONSUMIDOR, String CONSUMIDOR, String IDCULTIVO, String CULTIVO){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("IDEMPRESA", IDEMPRESA);
        values.put("IDCONSUMIDOR", IDCONSUMIDOR);
        values.put("CONSUMIDOR", CONSUMIDOR);
        values.put("IDCULTIVO", IDCULTIVO);
        values.put("CULTIVO", CULTIVO);

        long resultado = db.insert("CONSUMIDOR", null, values);
        //db.close();
        return resultado;
    }

    public void cargarListaConsumidoresSQLite2(){ // BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        //String sql = "select IDEMPRESA, IDCONSUMIDOR, CONSUMIDOR, IDCULTIVO, CULTIVO, CASE (substr(CONSUMIDOR,4, 2)) \n" +
        String sql = "select IDEMPRESA, IDCONSUMIDOR, CONSUMIDOR, IDCULTIVO, CULTIVO from CONSUMIDOR where IDCULTIVO = '0006' order by 1 ";
        Cursor resultado = bd.rawQuery(sql, null);
        listaConsumidorSQLite.clear();
        listaConsumidorSQLiteString.clear();
        while (resultado.moveToNext()){
            Consumidor objC = new Consumidor();
            objC.setIDEMPRESA(resultado.getString(0));
            objC.setIDCONSUMIDOR(resultado.getString(1));
            objC.setCONSUMIDOR(resultado.getString(2));
            objC.setIDCULTIVO(resultado.getString(3));
            objC.setCULTIVO(resultado.getString(4));
            listaConsumidorSQLite.add(objC);
            listaConsumidorSQLiteString.add(new String(resultado.getString(2)));
        }
        bd.close();
    }

    public void agregar_ConsumidoresSQLite(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for(Consumidor consumidor : Consumidor.listaConsumidorHost){
                values.put("IDEMPRESA", consumidor.getIDEMPRESA());
                values.put("IDCONSUMIDOR", consumidor.getIDCONSUMIDOR());
                values.put("CONSUMIDOR", consumidor.getCONSUMIDOR());
                values.put("IDCULTIVO", consumidor.getIDCULTIVO());
                values.put("CULTIVO", consumidor.getCULTIVO());
                db.insert("CONSUMIDOR", null, values);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public void cargarListaConsumidoresSQLite(){ // BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select * from CONSUMIDOR where IDCULTIVO = '"+MiAplicacionTareo.IDCULTIVO+"' order by 1 ";
        Cursor resultado = bd.rawQuery(sql, null);
        listaConsumidorSQLite.clear();
        listaConsumidorSQLiteString.clear();
        while (resultado.moveToNext()){
            Consumidor objC = new Consumidor();
            objC.setIDEMPRESA(resultado.getString(0));
            objC.setIDCONSUMIDOR(resultado.getString(1));
            objC.setCONSUMIDOR(resultado.getString(2));
            objC.setIDCULTIVO(resultado.getString(3));
            objC.setCULTIVO(resultado.getString(4));
            listaConsumidorSQLite.add(objC);
            listaConsumidorSQLiteString.add(new String(resultado.getString(2)));
        }
        bd.close();
    }

    public boolean verificarConsumidor(String idconsumidor){
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select * from CONSUMIDOR where IDCONSUMIDOR = '"+ idconsumidor +"'";
        Cursor resultado = bd.rawQuery(sql, null);
        boolean respuesta = false;

        while (resultado.moveToNext()){
            respuesta = true;
        }
        return respuesta;
    }

    public long limpiarTablaConsumidorSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("CONSUMIDOR", "IDCONSUMIDOR != 'X'", null);
        return 1;
    }
}
