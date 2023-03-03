package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import com.acpagro.tareopalta.datos.SQLite;

public class Valvula extends SQLite {
    private String IDVALVULA;
    private String DESCRIPCION;
    private String IDCONSUMIDOR;//S4 M8 T8

    public static ArrayList<Valvula> listaValvulas = new ArrayList<Valvula>();
    public static ArrayList<String> listaValvulasSQLiteString = new ArrayList<String>();

    public static ArrayList<Valvula> listaValvulasDialog = new ArrayList<Valvula>();
    public static ArrayList<String> listaValvulasSQLiteStringDialog = new ArrayList<String>();

    public Valvula(String IDVALVULA, String DESCRIPCION, String IDCONSUMIDOR) {
        this.IDVALVULA = IDVALVULA;
        this.DESCRIPCION = DESCRIPCION;
        this.IDCONSUMIDOR = IDCONSUMIDOR;
    }

    public Valvula() {
    }

    public String getIDVALVULA() {
        return IDVALVULA;
    }

    public void setIDVALVULA(String IDVALVULA) {
        this.IDVALVULA = IDVALVULA;
    }

    public String getDESCRIPCION() {
        return DESCRIPCION;
    }

    public void setDESCRIPCION(String DESCRIPCION) {
        this.DESCRIPCION = DESCRIPCION;
    }

    public String getIDCONSUMIDOR() {
        return IDCONSUMIDOR;
    }

    public void setIDCONSUMIDOR(String IDCONSUMIDOR) {
        this.IDCONSUMIDOR = IDCONSUMIDOR;
    }

    public long agregarValvulaSQLite(String IDVALVULA, String DESCRIPCION, String IDCONSUMIDOR){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("IDVALVULA", IDVALVULA);
        values.put("DESCRIPCION", DESCRIPCION);
        values.put("IDCONSUMIDOR", IDCONSUMIDOR);

        long resultado = db.insert("VALVULA", null, values);
        //db.close();
        return resultado;
    }

    public void agregar_ValvulaSQLite(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for(Valvula val : Valvula.listaValvulas){
                values.put("IDVALVULA", val.getIDVALVULA());
                values.put("DESCRIPCION", val.getDESCRIPCION());
                values.put("IDCONSUMIDOR", val.getIDCONSUMIDOR());
                db.insert("VALVULA", null, values);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public void cargarListaValvulasPorConsumidorSQLite(String IDCONSUMIDOR){ // BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select * from VALVULA where IDCONSUMIDOR = '"+IDCONSUMIDOR+"' order by DESCRIPCION ";
        Cursor resultado = bd.rawQuery(sql, null);
        listaValvulas.clear();
        listaValvulasSQLiteString.clear();
        while (resultado.moveToNext()){
            Valvula objV = new Valvula();
            objV.setIDVALVULA(resultado.getString(0));
            objV.setDESCRIPCION(resultado.getString(1));
            objV.setIDCONSUMIDOR(resultado.getString(2));
            listaValvulas.add(objV);
            listaValvulasSQLiteString.add(new String(resultado.getString(1)));
            Log.i("VALVULA", resultado.getString(1));
        }
        listaValvulasSQLiteString.add("VALVULA DE APOYO");
        bd.close();
    }

    public void cargarListaValvulasPorConsumidorSQLiteDialog(String IDCONSUMIDOR){ // BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select * from VALVULA where IDCONSUMIDOR = '"+IDCONSUMIDOR+"' order by DESCRIPCION ";
        Cursor resultado = bd.rawQuery(sql, null);
        listaValvulasDialog.clear();
        listaValvulasSQLiteStringDialog.clear();
        while (resultado.moveToNext()){
            Valvula objV = new Valvula();
            objV.setIDVALVULA(resultado.getString(0));
            objV.setDESCRIPCION(resultado.getString(1));
            objV.setIDCONSUMIDOR(resultado.getString(2));
            listaValvulasDialog.add(objV);
            listaValvulasSQLiteStringDialog.add(new String(resultado.getString(1)));
        }
        bd.close();
    }

    public void cargarListaValvulasTodasSQLite(){ // BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select DISTINCT IDVALVULA, DESCRIPCION, IDCONSUMIDOR  from VALVULA GROUP BY IDVALVULA order by DESCRIPCION";
        Cursor resultado = bd.rawQuery(sql, null);
        listaValvulasDialog.clear();
        listaValvulasSQLiteStringDialog.clear();
        while (resultado.moveToNext()){
            Valvula objV = new Valvula();
            objV.setIDVALVULA(resultado.getString(0));
            objV.setDESCRIPCION(resultado.getString(1));
            objV.setIDCONSUMIDOR(resultado.getString(2));
            listaValvulasDialog.add(objV);
            listaValvulasSQLiteStringDialog.add(new String(resultado.getString(1)));
        }
        bd.close();
    }

    public long limpiarTablaValvulasSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("VALVULA", null, null);
        return 1;
    }
}
