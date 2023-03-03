package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.acpagro.tareopalta.datos.SQLite;

import java.util.ArrayList;

public class SubLabor extends SQLite {
    private String IDACTIVIDAD;
    private String IDLABOR;
    private String IDSUBLABOR;
    private String SUBLABOR;
    private String GRUPO;
    private String UNIDAD;

    public static ArrayList<SubLabor> listaSubLaborHost = new ArrayList<SubLabor>();
    public static ArrayList<SubLabor> listaSubLaborSQLite = new ArrayList<SubLabor>();
    public static ArrayList<String> listaSubLaborSQLiteString = new ArrayList<String>();

    public SubLabor() {
    }

    public SubLabor(String IDACTIVIDAD, String IDLABOR, String IDSUBLABOR, String SUBLABOR, String GRUPO, String UNIDAD) {
        this.IDACTIVIDAD = IDACTIVIDAD;
        this.IDLABOR = IDLABOR;
        this.IDSUBLABOR = IDSUBLABOR;
        this.SUBLABOR = SUBLABOR;
        this.GRUPO = GRUPO;
        this.UNIDAD = UNIDAD;
    }

    public String getIDLABOR() {
        return IDLABOR;
    }

    public void setIDLABOR(String IDLABOR) {
        this.IDLABOR = IDLABOR;
    }

    public String getUNIDAD() {
        return UNIDAD;
    }

    public void setUNIDAD(String UNIDAD) {
        this.UNIDAD = UNIDAD;
    }

    public String getIDACTIVIDAD() {
        return IDACTIVIDAD;
    }

    public void setIDACTIVIDAD(String IDACTIVIDAD) {
        this.IDACTIVIDAD = IDACTIVIDAD;
    }

    public String getIDSUBLABOR() {
        return IDSUBLABOR;
    }

    public void setIDSUBLABOR(String IDSUBLABOR) {
        this.IDSUBLABOR = IDSUBLABOR;
    }

    public String getSUBLABOR() {
        return SUBLABOR;
    }

    public void setSUBLABOR(String SUBLABOR) {
        this.SUBLABOR = SUBLABOR;
    }

    public String getGRUPO() {
        return GRUPO;
    }

    public void setGRUPO(String GRUPO) {
        this.GRUPO = GRUPO;
    }

    public long agregarSubLaborSQLite(String IDACTIVIDAD, String IDSUBLABOR, String SUBLABOR, String GRUPO, String UNIDAD, String IDLABOR){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("IDACTIVIDAD", IDACTIVIDAD);
        values.put("IDSUBLABOR", IDSUBLABOR);
        values.put("SUBLABOR", SUBLABOR);
        values.put("GRUPO", GRUPO);
        values.put("UNIDAD", UNIDAD);
        values.put("IDLABOR", IDLABOR);

        long resultado = db.insert("SUBLABOR", null, values);
        //db.close();
        return resultado;
    }

    public void agregar_SublaboresSQLite(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for(SubLabor l : SubLabor.listaSubLaborHost){
                values.put("IDACTIVIDAD", l.getIDACTIVIDAD());
                values.put("IDSUBLABOR", l.getIDSUBLABOR());
                values.put("SUBLABOR", l.getSUBLABOR());
                values.put("GRUPO", l.getGRUPO());
                values.put("UNIDAD", l.getUNIDAD());
                values.put("IDLABOR", l.getIDLABOR());
                db.insert("SUBLABOR", null, values);
                Log.i("insertsub", l.getSUBLABOR());
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public void cargarListaSubLaboresSQLite(String IDACTIVIDAD){ // BD SQLITE

        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select * from SUBLABOR where IDACTIVIDAD = '" + IDACTIVIDAD + "' order by 1";
        Cursor resultado = bd.rawQuery(sql, null);
        listaSubLaborSQLite.clear();
        listaSubLaborSQLiteString.clear();

        Log.i("CONTEO", IDACTIVIDAD+"|"+resultado.getCount());
        while (resultado.moveToNext()){
            SubLabor objL = new SubLabor();

            objL.setIDACTIVIDAD(resultado.getString(0));
            objL.setIDLABOR(resultado.getString(1));
            objL.setIDSUBLABOR(resultado.getString(2));
            objL.setSUBLABOR(resultado.getString(3));
            objL.setGRUPO(resultado.getString(4));
            objL.setUNIDAD(resultado.getString(5));

            String grupo = "";
            if (resultado.getString(4).equals("SI")){
                grupo = "GRUPAL";
            }else{
                grupo = "INDIVIDUAL";
            }

            listaSubLaborSQLite.add(objL);
            Log.i("sublabor:",resultado.getString(3) + resultado.getString(4));
            listaSubLaborSQLiteString.add(new String(resultado.getString(3) + " - " +grupo));
        }
        //IDEMPRESA VARCHAR(50), IDACTIVIDAD VARCHAR(10), IDLABOR VARCHAR(10), LABOR VARCHAR (100), ALIAS VARCHAR(100)//LABOR
        //IDACTIVIDAD VARCHAR(5), IDSUBLABOR VARCHAR(5), SUBLABOR VARCHAR (100), GRUPO VARCHAR(2), UNIDAD VARCHAR(5)//SUBLABOR
        /*if(resultado.getColumnCount()==0){
            sql="SELECT IDACTIVIDAD, IDLABOR, LA";
        }
*/
        bd.close();
    }
    public long limpiarTablaSubLaborSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("SUBLABOR", "IDSUBLABOR != 'X'", null);
        return 1;
    }
}
