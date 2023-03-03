package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.acpagro.tareopalta.datos.SQLite;

import java.util.ArrayList;

public class HoraTareo extends SQLite {
    private String IDCULTIVO;
    private String DIA;
    private String HORAS;
    private String FECHAREGISTRO;
    private String IDUSUARIO;

    public static ArrayList<HoraTareo> listaHoraTareo = new ArrayList<>();

    public HoraTareo(String IDCULTIVO, String DIA, String HORAS, String FECHAREGISTRO, String IDUSUARIO) {
        this.IDCULTIVO = IDCULTIVO;
        this.DIA = DIA;
        this.HORAS = HORAS;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.IDUSUARIO = IDUSUARIO;
    }

    public HoraTareo() {
    }

    public String getIDCULTIVO() {
        return IDCULTIVO;
    }

    public void setIDCULTIVO(String IDCULTIVO) {
        this.IDCULTIVO = IDCULTIVO;
    }

    public String getDIA() {
        return DIA;
    }

    public void setDIA(String DIA) {
        this.DIA = DIA;
    }

    public String getHORAS() {
        return HORAS;
    }

    public void setHORAS(String HORAS) {
        this.HORAS = HORAS;
    }

    public String getFECHAREGISTRO() {
        return FECHAREGISTRO;
    }

    public void setFECHAREGISTRO(String FECHAREGISTRO) {
        this.FECHAREGISTRO = FECHAREGISTRO;
    }

    public String getIDUSUARIO() {
        return IDUSUARIO;
    }

    public void setIDUSUARIO(String IDUSUARIO) {
        this.IDUSUARIO = IDUSUARIO;
    }

    public long agregarHoraTareoSQLite(String IDCULTIVO, String DIA, String HORAS, String FECHAREGISTRO, String IDUSUARIO){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("IDCULTIVO", IDCULTIVO);
        values.put("DIA", DIA);
        values.put("HORAS", HORAS);
        values.put("FECHAREGISTRO", FECHAREGISTRO);
        values.put("IDUSUARIO", IDUSUARIO);
        long resultado = db.insert("HORATAREO", null, values);
        db.close();
        return resultado;
    }

    public void agregar_HoraTareoSQLite(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for(HoraTareo ht : HoraTareo.listaHoraTareo){
                values.put("IDCULTIVO", ht.getIDCULTIVO());
                values.put("DIA", ht.getDIA());
                values.put("HORAS", ht.getHORAS());
                values.put("FECHAREGISTRO", ht.getFECHAREGISTRO());
                values.put("IDUSUARIO", ht.getIDUSUARIO());
                db.insert("HORATAREO", null, values);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public long limpiarTablaHoraTareoSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("HORATAREO", null, null);
        return 1;
    }

    public double getHoraTareoHoy(String nombredia){
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT HORAS FROM HORATAREO WHERE IDCULTIVO = '0006' AND UPPER(DIA) = UPPER('"+nombredia+"')";
        Cursor resultado = bd.rawQuery(sql, null);
        double hora = 9.5;
        String horaS = "9.5";
        while (resultado.moveToNext()){
            hora = resultado.getDouble(0);
            horaS = resultado.getString(0);
            Log.i("GETHORA", resultado.getString(0));
        }
        bd.close();

        if(horaS.trim().length() == 0 || horaS.trim().equalsIgnoreCase("null")){
            hora = 9.5;
        }

        return hora;
    }

}
