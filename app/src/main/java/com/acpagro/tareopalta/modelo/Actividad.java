package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.acpagro.tareopalta.datos.SQLite;

import java.util.ArrayList;

public class Actividad extends SQLite {
    private String IDEMPRESA;
    private String IDACTIVIDAD;
    private String ACTIVIDAD;
    private String ALIAS;

    public static ArrayList<Actividad> listaActividadHost = new ArrayList<Actividad>();
    public static ArrayList<Actividad> listaActividadSQLite = new ArrayList<Actividad>();
    public static ArrayList<String> listaActividadSQLiteString = new ArrayList<String>();

    public Actividad() {
    }

    public Actividad(String IDEMPRESA, String IDACTIVIDAD, String ACTIVIDAD, String ALIAS) {
        this.IDEMPRESA = IDEMPRESA;
        this.IDACTIVIDAD = IDACTIVIDAD;
        this.ACTIVIDAD = ACTIVIDAD;
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

    public String getACTIVIDAD() {
        return ACTIVIDAD;
    }

    public void setACTIVIDAD(String ACTIVIDAD) {
        this.ACTIVIDAD = ACTIVIDAD;
    }

    public String getALIAS() {
        return ALIAS;
    }

    public void setALIAS(String ALIAS) {
        this.ALIAS = ALIAS;
    }

    public long agregarActividadSQLite(String IDEMPRESA, String IDACTIVIDAD, String ACTIVIDAD, String ALIAS){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("IDEMPRESA", IDEMPRESA);
        values.put("IDACTIVIDAD", IDACTIVIDAD);
        values.put("ACTIVIDAD", ACTIVIDAD);
        values.put("ALIAS", ALIAS);

        long resultado = db.insert("ACTIVIDAD", null, values);
        //db.close();
        return resultado;
    }

    public void agregar_ActividadSQLite(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for(Actividad a : Actividad.listaActividadHost){
                values.put("IDEMPRESA", a.getIDEMPRESA());
                values.put("IDACTIVIDAD", a.getIDACTIVIDAD());
                values.put("ACTIVIDAD", a.getACTIVIDAD());
                values.put("ALIAS", a.getALIAS());
                db.insert("ACTIVIDAD", null, values);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public void cargarListaActividadesSQLite(){ // BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select * from ACTIVIDAD order by 1";
        Cursor resultado = bd.rawQuery(sql, null);
        listaActividadSQLite.clear();
        listaActividadSQLiteString.clear();
        while (resultado.moveToNext()){
            Actividad objA = new Actividad();
            objA.setIDEMPRESA(resultado.getString(0));
            objA.setIDACTIVIDAD(resultado.getString(1));
            objA.setACTIVIDAD(resultado.getString(2));
            objA.setALIAS(resultado.getString(3));
            listaActividadSQLite.add(objA);
            listaActividadSQLiteString.add(new String(resultado.getString(2)));
        }
        bd.close();
    }

    public long limpiarTablaActividadSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("ACTIVIDAD", "IDACTIVIDAD != 'X'", null);
        return 1;
    }
}
