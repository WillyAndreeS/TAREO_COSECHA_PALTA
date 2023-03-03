package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.acpagro.tareopalta.datos.SQLite;

import java.util.ArrayList;

public class PersonalGeneral extends SQLite {
    private String CODIGO;
    private String DNI;
    private String NOMBRES;
    private String OBSERVADO;
    private String MODULO;
    private String MOTIVO;

    public static ArrayList<PersonalGeneral> listaPersonalGeneralHost = new ArrayList<PersonalGeneral>();
    public static ArrayList<PersonalGeneral> listaPersonalGeneralSQLite = new ArrayList<PersonalGeneral>();
    public static ArrayList<String> listaPersonalGeneralSQLiteString = new ArrayList<String>();

    public PersonalGeneral(String CODIGO, String DNI, String NOMBRES, String OBSERVADO, String MODULO, String MOTIVO) {
        this.CODIGO = CODIGO;
        this.DNI = DNI;
        this.NOMBRES = NOMBRES;
        this.OBSERVADO = OBSERVADO;
        this.MODULO = MODULO;
        this.MOTIVO = MOTIVO;
    }

    public PersonalGeneral() {
    }

    public String getMOTIVO() {
        return MOTIVO;
    }

    public void setMOTIVO(String MOTIVO) {
        this.MOTIVO = MOTIVO;
    }

    public String getMODULO() {
        return MODULO;
    }

    public void setMODULO(String MODULO) {
        this.MODULO = MODULO;
    }

    public String getOBSERVADO() {
        return OBSERVADO;
    }

    public void setOBSERVADO(String OBSERVADO) {
        this.OBSERVADO = OBSERVADO;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public String getNOMBRES() {
        return NOMBRES;
    }

    public void setNOMBRES(String NOMBRES) {
        this.NOMBRES = NOMBRES;
    }

    public long agregarPersonalGeneralSQLite(String CODIGO, String DNI, String NOMBRES, String OBSERVADO, String MODULO, String MOTIVO){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("CODIGO", CODIGO);
        values.put("DNI", DNI);
        values.put("NOMBRES", NOMBRES);
        values.put("OBSERVADO", OBSERVADO);
        values.put("MODULO", MODULO);
        values.put("MOTIVO", MOTIVO);

        long resultado = db.insert("PERSONALGENERAL", null, values);
        //db.close();
        return resultado;
    }

    public void agregar_PersonalGeneralSQLite(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for(PersonalGeneral pg : PersonalGeneral.listaPersonalGeneralHost){
                values.put("CODIGO", pg.getCODIGO());
                values.put("DNI", pg.getDNI());
                values.put("NOMBRES", pg.getNOMBRES());
                values.put("OBSERVADO", pg.getOBSERVADO());
                values.put("MODULO", pg.getMODULO());
                values.put("MOTIVO", pg.getMOTIVO());
                db.insert("PERSONALGENERAL", null, values);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public void cargarListaPERSONALGENERALSQLite(){ // BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select * from PERSONALGENERAL order by 1";
        Cursor resultado = bd.rawQuery(sql, null);
        listaPersonalGeneralSQLite.clear();
        listaPersonalGeneralSQLiteString.clear();
        while (resultado.moveToNext()){
            PersonalGeneral objL = new PersonalGeneral();
            objL.setCODIGO(resultado.getString(0));
            objL.setDNI(resultado.getString(1));
            objL.setNOMBRES(resultado.getString(2));
            objL.setOBSERVADO(resultado.getString(3));
            objL.setMODULO(resultado.getString(4));
            objL.setMOTIVO(resultado.getString(5));
            listaPersonalGeneralSQLite.add(objL);
            listaPersonalGeneralSQLiteString.add(new String(resultado.getString(3)));
        }
        bd.close();
    }

    public boolean getPersonalgeneralPorDNI(String DNI){
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select * from PERSONALGENERAL WHERE DNI='"+DNI+"'";
        Cursor resultado = bd.rawQuery(sql, null);
        listaPersonalGeneralSQLite.clear();
        while (resultado.moveToNext()){
            PersonalGeneral objL = new PersonalGeneral();
            objL.setCODIGO(resultado.getString(0));
            objL.setDNI(resultado.getString(1));
            objL.setNOMBRES(resultado.getString(2));
            objL.setOBSERVADO(resultado.getString(3));
            objL.setMODULO(resultado.getString(4));
            objL.setMOTIVO(resultado.getString(5));
            listaPersonalGeneralSQLite.add(objL);
        }
        bd.close();
        if(resultado.getCount()!=0){
            return true;
        }else{
            return false;
        }
    }

    public long limpiarTablaPersonalGeneralSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("PERSONALGENERAL", "DNI != '00000000'", null);
        return 1;
    }
}
