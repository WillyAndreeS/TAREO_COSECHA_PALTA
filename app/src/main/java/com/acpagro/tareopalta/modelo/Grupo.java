package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.acpagro.tareopalta.datos.SQLite;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;

public class Grupo extends SQLite {
    private String IDGRUPO;//001_2020-04-07_13:15:16
    private String NUM_GRUPO_DESC;//001
    private String NUMERO;//1
    private String COPIA_DE_ID;//null
    private String COPIA_DE_DESC;//null
    private String FECHAREGISTRO;//2020-04-07_13:15:16
    private String ESTADO;//1

    public static ArrayList<Grupo> listaGrupos = new ArrayList<>();

    public void cargarFechaHoy(){
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        DateFormat dateFormatText = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        dateFormat.format(date);
        //tv_fecha.setText(dateFormatText.format(date));
        //fecha = dateFormat.format(date);
    }

    public long agregarGrupoSQLite(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //DateFormat dateFormat_f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        DateFormat dateFormat_f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String fecha_hoy = dateFormat.format(date);
        String formatoNumero = String.format("%%0%dd", 2);
        String result = String.format(formatoNumero, 2);//005
        Formatter obj = new Formatter();
        int numero_desc;
        long grabado = 0;

        SQLiteDatabase dbw = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        SQLiteDatabase bdr = this.getReadableDatabase();
        String sql = "SELECT " +
                    "    NUM_GRUPO_DESC, " +
                    "(select count(*) from GRUPO WHERE DATE(FECHAREGISTRO)=DATE('"+fecha_hoy+"')) ULTIMO_NUMERO "+
                    "FROM GRUPO \n" +
                    "WHERE ESTADO = 1 AND DATE(FECHAREGISTRO)=DATE('"+fecha_hoy+"') AND COPIA_DE_ID IS NULL \n" +
                    "ORDER BY NUMERO DESC LIMIT 1";
        Cursor cursor = bdr.rawQuery(sql, null);
        if(cursor.getCount()!=0){//Ya hay registros no inicia en 001
            while (cursor.moveToNext()){
                int num_desc_anterior = cursor.getInt(0)+1;
                String nuevo_desc = String.valueOf(obj.format("%03d", num_desc_anterior));
                values.put("IDGRUPO", nuevo_desc+"_"+dateFormat.format(date));//002_2020-04-08
                values.put("NUM_GRUPO_DESC", nuevo_desc);
                values.put("NUMERO", String.valueOf(cursor.getInt(1)+1));
                values.put("FECHAREGISTRO", dateFormat_f.format(date));
                grabado = dbw.insert("GRUPO", null, values);
            }
        }else{//DEBE INICIAR EN 001
            values.put("IDGRUPO", "001"+"_"+dateFormat.format(date));//001_2020-04-08
            values.put("NUM_GRUPO_DESC", String.valueOf(obj.format("%03d", 1)));
            values.put("NUMERO", String.valueOf(1));
            values.put("FECHAREGISTRO", dateFormat_f.format(date));
            grabado = dbw.insert("GRUPO", null, values);
        }
        //db.close();
        return grabado;
    }

    public long copiarGrupoSQLite(String IDGRUPO){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //DateFormat dateFormat_f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        DateFormat dateFormat_f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String fecha_hoy = dateFormat.format(date);
        String formatoNumero = String.format("%%0%dd", 2);
        String result = String.format(formatoNumero, 2);//005
        Formatter obj = new Formatter();
        int numero_desc;
        long grabado = 0;
        CopiaTareo.listaCopia.clear();

        String NUM_COPIA="", IDGRUPO_="";

        SQLiteDatabase dbw = this.getWritableDatabase();
        ContentValues values;

        SQLiteDatabase bdr = this.getReadableDatabase();
        String sql = "SELECT " +
                "    NUM_GRUPO_DESC, " +
                "(select count(*) from GRUPO WHERE DATE(FECHAREGISTRO)=DATE('"+fecha_hoy+"') AND COPIA_DE_ID='"+IDGRUPO+"')+1 NUMERO_COPIA,"+
                "(select count(*) from GRUPO WHERE DATE(FECHAREGISTRO)=DATE('"+fecha_hoy+"')) ULTIMO_NUMERO "+
                "FROM GRUPO \n" +
                "WHERE ESTADO = 1 AND DATE(FECHAREGISTRO)=DATE('"+fecha_hoy+"') AND IDGRUPO='"+IDGRUPO+"' \n" +
                "ORDER BY NUMERO DESC LIMIT 1";
        Cursor cursor = bdr.rawQuery(sql, null);
        while (cursor.moveToNext()){//INSERTO EL GRUPO
            values = new ContentValues();
            NUM_COPIA=cursor.getString(1);
            IDGRUPO_=cursor.getString(0)+"_"+cursor.getString(1)+"_"+dateFormat.format(date);
            values.put("IDGRUPO", IDGRUPO_);//002_2020-04-08
            values.put("NUM_GRUPO_DESC", cursor.getString(0)+"_"+cursor.getString(1));
            values.put("NUMERO", String.valueOf(cursor.getInt(2)+1));
            values.put("COPIA_DE_ID", IDGRUPO);
            values.put("COPIA_DE_DESC", cursor.getString(0));
            values.put("FECHAREGISTRO", dateFormat_f.format(date));
            grabado = dbw.insert("GRUPO", null, values);
        }

        sql = "select \n" +
                "    IDTAREO||'_"+NUM_COPIA+"', \n" +
                "    FECHA, IDUSUARIO, IDCONSUMIDOR, IDACTIVIDAD, \n" +
                "    IDLABOR, IDSUBLABOR, TIPOLABOR, HORAS, \n" +
                "    SINCRONIZADO, OBSERVACION, IDTURNO, \n" +
                "    IDGRUPO, IDTAREO \n" +
                "from \n" +
                "    TAREO WHERE IDGRUPO='"+IDGRUPO+"'";
        cursor = bdr.rawQuery(sql, null);
        while (cursor.moveToNext()){//INSERTO EL GRUPO
            values = new ContentValues();
            values.put("IDTAREO", cursor.getString(0));//002_2020-04-08
            values.put("FECHA", dateFormat_f.format(date));
            values.put("IDUSUARIO", cursor.getString(2));
            values.put("IDCONSUMIDOR", cursor.getString(3));
            values.put("IDACTIVIDAD", cursor.getString(4));
            values.put("IDLABOR", cursor.getString(5));
            values.put("IDSUBLABOR", cursor.getString(6));
            values.put("TIPOLABOR", cursor.getString(7));
            values.put("HORAS", cursor.getString(8));
            values.put("OBSERVACION", cursor.getString(10));
            values.put("IDTURNO", cursor.getString(11));
            values.put("IDGRUPO", IDGRUPO_);
            grabado = dbw.insert("TAREO", null, values);


            sql = "SELECT IDTAREO, IDPERSONALGENERAL, FECHAREGISTRO FROM ASISTENCIA WHERE IDTAREO='"+cursor.getString(13)+"'";
            Cursor cursor_asis = bdr.rawQuery(sql, null);
            while (cursor_asis.moveToNext()){
                values = new ContentValues();
                values.put("IDTAREO", cursor.getString(0));
                values.put("IDPERSONALGENERAL", cursor_asis.getString(1));
                values.put("FECHAREGISTRO", dateFormat_f.format(date));
                grabado = dbw.insert("ASISTENCIA", null, values);
            }

        }

        //db.close();
        return grabado;
    }

    public void cargarListaGrupoSQLite(){// BD SQLITE
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT " +
                "IDGRUPO, " +
                "NUM_GRUPO_DESC, " +
                "NUMERO, " +
                "COPIA_DE_ID, " +
                "COPIA_DE_DESC, " +
                "FECHAREGISTRO, " +
                "ESTADO " +
                "FROM GRUPO " +
                "WHERE DATE(FECHAREGISTRO)=DATE('"+fechaHoy+"') ORDER BY FECHAREGISTRO-- DESC";
        //"WHERE TAREO.IDUSUARIO = '"+MiAplicacionTareo.IDUSUARIO+"' AND  DATE(TAREO.FECHA, 'localtime')=(SELECT DATE(date('now'))) ";

        Cursor resultado = bd.rawQuery(sql, null);
        Log.i("GRUPO", "CURSOR ASIST"+resultado.getCount());
        listaGrupos.clear();

        while (resultado.moveToNext()){
            listaGrupos.add(new Grupo(""+resultado.getString(0),
                    ""+resultado.getString(1),
                    ""+resultado.getString(2),
                    ""+resultado.getString(3),
                    ""+resultado.getString(4),
                    ""+resultado.getString(5),
                    ""+resultado.getString(6)));
        }
        bd.close();
    }

    public long limpiarTablaGrupoSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("GRUPO", null, null);
        return 1;
    }

    public Grupo(String IDGRUPO, String NUM_GRUPO_DESC, String NUMERO, String COPIA_DE_ID, String COPIA_DE_DESC, String FECHAREGISTRO, String ESTADO) {
        this.IDGRUPO = IDGRUPO;
        this.NUM_GRUPO_DESC = NUM_GRUPO_DESC;
        this.NUMERO = NUMERO;
        this.COPIA_DE_ID = COPIA_DE_ID;
        this.COPIA_DE_DESC = COPIA_DE_DESC;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.ESTADO = ESTADO;
    }

    public Grupo() {
    }

    public String getIDGRUPO() {
        return IDGRUPO;
    }

    public void setIDGRUPO(String IDGRUPO) {
        this.IDGRUPO = IDGRUPO;
    }

    public String getNUM_GRUPO_DESC() {
        return NUM_GRUPO_DESC;
    }

    public void setNUM_GRUPO_DESC(String NUM_GRUPO_DESC) {
        this.NUM_GRUPO_DESC = NUM_GRUPO_DESC;
    }

    public String getNUMERO() {
        return NUMERO;
    }

    public void setNUMERO(String NUMERO) {
        this.NUMERO = NUMERO;
    }

    public String getCOPIA_DE_ID() {
        return COPIA_DE_ID;
    }

    public void setCOPIA_DE_ID(String COPIA_DE_ID) {
        this.COPIA_DE_ID = COPIA_DE_ID;
    }

    public String getCOPIA_DE_DESC() {
        return COPIA_DE_DESC;
    }

    public void setCOPIA_DE_DESC(String COPIA_DE_DESC) {
        this.COPIA_DE_DESC = COPIA_DE_DESC;
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
}
