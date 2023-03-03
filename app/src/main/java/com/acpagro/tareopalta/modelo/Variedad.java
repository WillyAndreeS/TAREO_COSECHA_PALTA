package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.acpagro.tareopalta.datos.SQLite;

import java.util.ArrayList;

public class Variedad extends SQLite {
    private String IDCULTIVO;
    private String IDVARIEDAD;
    private String DESCRIPCION;

    public static ArrayList<Variedad> listaVariedad = new ArrayList<>();
    public static ArrayList<Variedad> variedadEncontrada = new ArrayList<>();

    public Variedad(String IDCULTIVO, String IDVARIEDAD, String DESCRIPCION) {
        this.IDCULTIVO = IDCULTIVO;
        this.IDVARIEDAD = IDVARIEDAD;
        this.DESCRIPCION = DESCRIPCION;
    }

    public Variedad() {
    }

    public String getIDCULTIVO() {
        return IDCULTIVO;
    }

    public void setIDCULTIVO(String IDCULTIVO) {
        this.IDCULTIVO = IDCULTIVO;
    }

    public String getIDVARIEDAD() {
        return IDVARIEDAD;
    }

    public void setIDVARIEDAD(String IDVARIEDAD) {
        this.IDVARIEDAD = IDVARIEDAD;
    }

    public String getDESCRIPCION() {
        return DESCRIPCION;
    }

    public void setDESCRIPCION(String DESCRIPCION) {
        this.DESCRIPCION = DESCRIPCION;
    }

    public void agregar_VariedadSQLite(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for(Variedad var : Variedad.listaVariedad){
                values.put("IDCULTIVO", var.getIDCULTIVO());
                values.put("IDVARIEDAD", var.getIDVARIEDAD());
                values.put("DESCRIPCION", var.getDESCRIPCION());
                db.insert("VARIEDAD_CULTIVO", null, values);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public String getVariedadPorID(String IDVARIEDAD){ // BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select IDCULTIVO, IDVARIEDAD, DESCRIPCION FROM VARIEDAD_CULTIVO WHERE IDCULTIVO='0006' AND IDVARIEDAD='"+IDVARIEDAD+"'";
        Cursor resultado = bd.rawQuery(sql, null);
        variedadEncontrada.clear();
        variedadEncontrada.clear();
        while (resultado.moveToNext()){
            variedadEncontrada.add(new Variedad(
                    ""+resultado.getString(0),
                    ""+resultado.getString(1),
                    ""+resultado.getString(2)
            ));
        }
        bd.close();

        if(variedadEncontrada.size()!=0){
            return variedadEncontrada.get(0).getDESCRIPCION();
        }else{
            return IDVARIEDAD;
        }
    }

    public long limpiarTablaVariedadSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("VARIEDAD_CULTIVO", null, null);
        return 1;
    }

    public boolean verificarVariedad(String idVariedad){
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select * from VARIEDAD_CULTIVO where IDVARIEDAD = '"+ idVariedad +"'";
        Cursor resultado = bd.rawQuery(sql, null);
        boolean respuesta = false;

        while (resultado.moveToNext()){
            respuesta = true;
        }
        return respuesta;
    }

}
