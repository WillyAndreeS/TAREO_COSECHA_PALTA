package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.acpagro.tareopalta.datos.SQLite;

import java.util.ArrayList;

public class Labor extends SQLite {
    private String IDEMPRESA;
    private String IDACTIVIDAD;
    private String IDLABOR;
    private String LABOR;
    private String ALIAS;

    public static ArrayList<Labor> listaLaborHost = new ArrayList<Labor>();
    public static ArrayList<Labor> listaLaborSQLite = new ArrayList<Labor>();
    public static ArrayList<String> listaLaborSQLiteString = new ArrayList<String>();

    public Labor() {
    }

    public Labor(String IDEMPRESA, String IDACTIVIDAD, String IDLABOR, String LABOR, String ALIAS) {
        this.IDEMPRESA = IDEMPRESA;
        this.IDACTIVIDAD = IDACTIVIDAD;
        this.IDLABOR = IDLABOR;
        this.LABOR = LABOR;
        this.ALIAS = ALIAS;
    }

    public String getIDEMPRESA() {
        return IDEMPRESA;
    }

    public void setIDEMPRESA(String IDEMPRESA) {
        this.IDEMPRESA = IDEMPRESA;
    }

    public String getIDACTIVIDAD() {
        return IDACTIVIDAD;
    }

    public void setIDACTIVIDAD(String IDACTIVIDAD) {
        this.IDACTIVIDAD = IDACTIVIDAD;
    }

    public String getIDLABOR() {
        return IDLABOR;
    }

    public void setIDLABOR(String IDLABOR) {
        this.IDLABOR = IDLABOR;
    }

    public String getLABOR() {
        return LABOR;
    }

    public void setLABOR(String LABOR) {
        this.LABOR = LABOR;
    }

    public String getALIAS() {
        return ALIAS;
    }

    public void setALIAS(String ALIAS) {
        this.ALIAS = ALIAS;
    }

    public long agregarLaborSQLite(String IDEMPRESA, String IDACTIVIDAD, String IDLABOR, String LABOR, String ALIAS){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("IDEMPRESA", IDEMPRESA);
        values.put("IDACTIVIDAD", IDACTIVIDAD);
        values.put("IDLABOR", IDLABOR);
        values.put("LABOR", LABOR);
        values.put("ALIAS", ALIAS);

        long resultado = db.insert("LABOR", null, values);
        //db.close();
        return resultado;
    }

    public void cargarListaLaboresSQLite(String IDACTIVIDAD){ // BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select * from LABOR where IDACTIVIDAD = '"+ IDACTIVIDAD+"' order by 1";
        Cursor resultado = bd.rawQuery(sql, null);
        listaLaborSQLite.clear();
        listaLaborSQLiteString.clear();
        while (resultado.moveToNext()){
            Labor objL = new Labor();
            objL.setIDEMPRESA(resultado.getString(0));
            objL.setIDACTIVIDAD(resultado.getString(1));
            objL.setIDLABOR(resultado.getString(2));
            objL.setLABOR(resultado.getString(3));
            objL.setALIAS(resultado.getString(4));

            Log.i("VerificarLabor:", resultado.getString(2));
            listaLaborSQLite.add(objL);
            listaLaborSQLiteString.add(new String(resultado.getString(3)));
        }
        bd.close();
    }

    public void agregar_LaborSQLite(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for(Labor l : Labor.listaLaborHost){
                values.put("IDEMPRESA", l.getIDEMPRESA());
                values.put("IDACTIVIDAD", l.getIDACTIVIDAD());
                values.put("IDLABOR", l.getIDLABOR());
                values.put("LABOR", l.getLABOR());
                values.put("ALIAS", l.getALIAS());
                db.insert("LABOR", null, values);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public long limpiarTablaLaborSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("LABOR", "IDLABOR != 'X'", null);
        return 1;
    }
}
