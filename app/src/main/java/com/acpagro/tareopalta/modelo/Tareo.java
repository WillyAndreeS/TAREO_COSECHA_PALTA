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
import java.util.List;

public class Tareo extends SQLite {
    private String IDTAREO;
    private String FECHA;
    private String IDUSUARIO;
    private String IDCONSUMIDOR;
    private String IDACTIVIDAD;
    private String IDLABOR;
    private String IDSUBLABOR;
    private String TIPOLABOR;
    private String HORAS;
    private String OBSERVACION;
    private String IDTURNO;
    private String IDGRUPO;

    private String DNI;
    private String NOMBREUSUARIO;
    private String NOMBRECONSUMIDOR;
    private String NOMBREACTIVIDAD;
    private String NOMBRELABOR;
    private String NOMBRESUBLABOR;
    private String TOTALTRABAJADORES;
    private String GRUPO;

    private String SINCRONIZADO;

    public static ArrayList<Tareo> listaTareoSQLite = new ArrayList<Tareo>();
    public static ArrayList<Tareo> listaTareoPantallaSQLite = new ArrayList<Tareo>();//Sin Tareos de Limpieza
    public static ArrayList<Tareo> listaTareoPantallaSQLiteCompleta = new ArrayList<Tareo>();
    public static List<String> listaTareoSQLiteSubir = new ArrayList<>();

    public static ArrayList<String> listaStrTareosImportar = new ArrayList<String>();
    public static ArrayList<Tareo> listaObjTareosImportar = new ArrayList<Tareo>();

    public Tareo() {
    }

    public Tareo(String IDTAREO, String FECHA, String IDUSUARIO, String IDCONSUMIDOR, String IDACTIVIDAD, String IDLABOR, String TIPOLABOR, String HORAS) {
        this.IDTAREO = IDTAREO;
        this.FECHA = FECHA;
        this.IDUSUARIO = IDUSUARIO;
        this.IDCONSUMIDOR = IDCONSUMIDOR;
        this.IDACTIVIDAD = IDACTIVIDAD;
        this.IDLABOR = IDLABOR;
        this.TIPOLABOR = TIPOLABOR;
        this.HORAS = HORAS;
    }

    public Tareo(String IDTAREO, String FECHA, String IDUSUARIO,
                 String DNI, String NOMBREUSUARIO, String IDCONSUMIDOR,
                 String NOMBRECONSUMIDOR, String IDACTIVIDAD, String NOMBREACTIVIDAD,
                 String IDLABOR, String NOMBRELABOR, String TIPOLABOR,
                 String TOTALTRABAJADORES, String HORAS, String OBSERVACION) {
        this.IDTAREO = IDTAREO;
        this.FECHA = FECHA;
        this.IDUSUARIO = IDUSUARIO;
        this.IDCONSUMIDOR = IDCONSUMIDOR;
        this.IDACTIVIDAD = IDACTIVIDAD;
        this.IDLABOR = IDLABOR;
        this.DNI = DNI;
        this.NOMBREUSUARIO = NOMBREUSUARIO;
        this.NOMBRECONSUMIDOR = NOMBRECONSUMIDOR;
        this.NOMBREACTIVIDAD = NOMBREACTIVIDAD;
        this.NOMBRELABOR = NOMBRELABOR;
        this.TIPOLABOR = TIPOLABOR;
        this.TOTALTRABAJADORES = TOTALTRABAJADORES;
        this.HORAS = HORAS;
        this.OBSERVACION = OBSERVACION;
    }

    public Tareo(String IDTAREO, String FECHA, String IDUSUARIO,
                 String DNI, String NOMBREUSUARIO, String IDCONSUMIDOR,
                 String NOMBRECONSUMIDOR, String IDACTIVIDAD, String NOMBREACTIVIDAD,
                 String IDLABOR, String NOMBRELABOR, String TIPOLABOR,
                 String TOTALTRABAJADORES, String HORAS, String OBSERVACION, String SINCRONIZADO) {
        this.IDTAREO = IDTAREO;
        this.FECHA = FECHA;
        this.IDUSUARIO = IDUSUARIO;
        this.IDCONSUMIDOR = IDCONSUMIDOR;
        this.IDACTIVIDAD = IDACTIVIDAD;
        this.IDLABOR = IDLABOR;
        this.DNI = DNI;
        this.NOMBREUSUARIO = NOMBREUSUARIO;
        this.NOMBRECONSUMIDOR = NOMBRECONSUMIDOR;
        this.NOMBREACTIVIDAD = NOMBREACTIVIDAD;
        this.NOMBRELABOR = NOMBRELABOR;
        this.TIPOLABOR = TIPOLABOR;
        this.TOTALTRABAJADORES = TOTALTRABAJADORES;
        this.HORAS = HORAS;
        this.OBSERVACION = OBSERVACION;
        this.SINCRONIZADO = SINCRONIZADO;
    }

    public String getIDTURNO() {
        return IDTURNO;
    }

    public void setIDTURNO(String IDTURNO) {
        this.IDTURNO = IDTURNO;
    }

    public String getGRUPO() {
        return GRUPO;
    }

    public void setGRUPO(String GRUPO) {
        this.GRUPO = GRUPO;
    }

    public String getIDSUBLABOR() {
        return IDSUBLABOR;
    }

    public void setIDSUBLABOR(String IDSUBLABOR) {
        this.IDSUBLABOR = IDSUBLABOR;
    }

    public String getNOMBRESUBLABOR() {
        return NOMBRESUBLABOR;
    }

    public void setNOMBRESUBLABOR(String NOMBRESUBLABOR) {
        this.NOMBRESUBLABOR = NOMBRESUBLABOR;
    }

    public String getSINCRONIZADO() {
        return SINCRONIZADO;
    }

    public void setSINCRONIZADO(String SINCRONIZADO) {
        this.SINCRONIZADO = SINCRONIZADO;
    }

    public String getOBSERVACION() {
        return OBSERVACION;
    }

    public void setOBSERVACION(String OBSERVACION) {
        this.OBSERVACION = OBSERVACION;
    }

    public String getHORAS() {
        return HORAS;
    }

    public void setHORAS(String HORAS) {
        this.HORAS = HORAS;
    }

    public String getTOTALTRABAJADORES() {
        return TOTALTRABAJADORES;
    }

    public void setTOTALTRABAJADORES(String TOTALTRABAJADORES) {
        this.TOTALTRABAJADORES = TOTALTRABAJADORES;
    }

    public String getTIPOLABOR() {
        return TIPOLABOR;
    }

    public void setTIPOLABOR(String TIPOLABOR) {
        this.TIPOLABOR = TIPOLABOR;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public String getNOMBREUSUARIO() {
        return NOMBREUSUARIO;
    }

    public void setNOMBREUSUARIO(String NOMBREUSUARIO) {
        this.NOMBREUSUARIO = NOMBREUSUARIO;
    }

    public String getNOMBRECONSUMIDOR() {
        return NOMBRECONSUMIDOR;
    }

    public void setNOMBRECONSUMIDOR(String NOMBRECONSUMIDOR) {
        this.NOMBRECONSUMIDOR = NOMBRECONSUMIDOR;
    }

    public String getNOMBREACTIVIDAD() {
        return NOMBREACTIVIDAD;
    }

    public void setNOMBREACTIVIDAD(String NOMBREACTIVIDAD) {
        this.NOMBREACTIVIDAD = NOMBREACTIVIDAD;
    }

    public String getNOMBRELABOR() {
        return NOMBRELABOR;
    }

    public void setNOMBRELABOR(String NOMBRELABOR) {
        this.NOMBRELABOR = NOMBRELABOR;
    }

    public String getIDTAREO() {
        return IDTAREO;
    }

    public void setIDTAREO(String IDTAREO) {
        this.IDTAREO = IDTAREO;
    }

    public String getFECHA() {
        return FECHA;
    }

    public void setFECHA(String FECHA) {
        this.FECHA = FECHA;
    }

    public String getIDUSUARIO() {
        return IDUSUARIO;
    }

    public void setIDUSUARIO(String IDUSUARIO) {
        this.IDUSUARIO = IDUSUARIO;
    }

    public String getIDCONSUMIDOR() {
        return IDCONSUMIDOR;
    }

    public void setIDCONSUMIDOR(String IDCONSUMIDOR) {
        this.IDCONSUMIDOR = IDCONSUMIDOR;
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

    public String getIDGRUPO() {
        return IDGRUPO;
    }

    public void setIDGRUPO(String IDGRUPO) {
        this.IDGRUPO = IDGRUPO;
    }

    /*public long agregarTareoSQLite(String IDTAREO, String FECHA, String IDUSUARIO, String IDCONSUMIDOR, String IDACTIVIDAD, String IDLABOR, String TIPOLABOR){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("IDTAREO", IDTAREO);
        values.put("FECHA", FECHA);
        values.put("IDUSUARIO", IDUSUARIO);
        values.put("IDCONSUMIDOR", IDCONSUMIDOR);
        values.put("IDACTIVIDAD", IDACTIVIDAD);
        values.put("IDLABOR", IDLABOR);
        values.put("TIPOLABOR", TIPOLABOR);
        long resultado = db.insert("TAREO", null, values);
        //db.close();
        return resultado;
    }*/

    public long agregarTareoSQLite(String IDTAREO, String FECHA, String IDUSUARIO, String IDCONSUMIDOR, String IDACTIVIDAD, String IDLABOR, String IDSUBLABOR, String TIPOLABOR, String OBSERVACION, String HORAS, String IDTURNO){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("IDTAREO", IDTAREO);
        values.put("FECHA", FECHA);
        values.put("IDUSUARIO", IDUSUARIO);
        values.put("IDCONSUMIDOR", IDCONSUMIDOR);
        values.put("IDACTIVIDAD", IDACTIVIDAD);
        values.put("IDLABOR", IDLABOR);
        values.put("IDSUBLABOR", IDSUBLABOR);
        values.put("TIPOLABOR", TIPOLABOR);
        values.put("OBSERVACION", OBSERVACION);
        values.put("HORAS", HORAS);
        values.put("IDTURNO", IDTURNO);

        long resultado = db.insert("TAREO", null, values);
        //db.close();

        return resultado;
    }

    public long modificarTareoSQLite(String IDTAREO, String IDCONSUMIDOR, String IDACTIVIDAD, String IDLABOR, String IDSUBLABOR, String TIPOLABOR, String HORAS, String IDTURNO){
        ContentValues cv = new ContentValues();
        cv.put("IDCONSUMIDOR",IDCONSUMIDOR);
        cv.put("IDACTIVIDAD",IDACTIVIDAD);
        cv.put("IDLABOR",IDLABOR);
        cv.put("IDSUBLABOR",IDSUBLABOR);
        cv.put("TIPOLABOR",TIPOLABOR);
        cv.put("HORAS",HORAS);
        cv.put("IDTURNO",IDTURNO);
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.update("TAREO", cv, "IDTAREO = '"+IDTAREO+"'", null);
        return 1;
    }

    public long agregarTareoSQLite_2(String IDTAREO, String FECHA, String IDUSUARIO, String IDCONSUMIDOR, String IDACTIVIDAD, String IDLABOR, String IDSUBLABOR, String TIPOLABOR, String OBSERVACION, String HORAS, String IDTURNO, String IDGRUPO){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("IDTAREO", IDTAREO);
        values.put("FECHA", FECHA);
        values.put("IDUSUARIO", IDUSUARIO);
        values.put("IDCONSUMIDOR", IDCONSUMIDOR);
        values.put("IDACTIVIDAD", IDACTIVIDAD);
        values.put("IDLABOR", IDLABOR);
        values.put("IDSUBLABOR", IDSUBLABOR);
        values.put("TIPOLABOR", TIPOLABOR);
        values.put("OBSERVACION", OBSERVACION);
        values.put("HORAS", HORAS);
        values.put("IDTURNO", IDTURNO);
        values.put("IDGRUPO", IDGRUPO);

        long resultado = db.insert("TAREO", null, values);
        //db.close();
        return resultado;
    }

    public void cargarListaTareoSQLite(){// BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","2");//ENVIANDO
        bd.update("TAREO", cv, "SINCRONIZADO = '0'", null);

        String sql = "select IDTAREO, FECHA, IDUSUARIO, IDCONSUMIDOR, IDACTIVIDAD, IDLABOR , HORAS, OBSERVACION, IDSUBLABOR, IDTURNO, IDGRUPO from TAREO WHERE SINCRONIZADO = '2' order by 1 ";
        Cursor resultado = bd.rawQuery(sql, null);
        listaTareoSQLite.clear();
        listaTareoSQLiteSubir.clear();
        while (resultado.moveToNext()){
            Tareo objA = new Tareo();
            objA.setIDTAREO(resultado.getString(0));
            objA.setFECHA(resultado.getString(1));
            objA.setIDUSUARIO(resultado.getString(2));
            objA.setIDCONSUMIDOR(resultado.getString(3));
            objA.setIDACTIVIDAD(resultado.getString(4));
            objA.setIDLABOR(resultado.getString(5));
            objA.setHORAS(resultado.getString(6));
            objA.setIDSUBLABOR(resultado.getString(8));
            objA.setIDTURNO(resultado.getString(9));
            listaTareoSQLite.add(objA);
            String items = "<Item IDTAREO=\"" + resultado.getString(0)
                    + "\" FECHAREGISTRO=\"" + resultado.getString(1).replace("-","")
                    + "\" IDUSUARIO=\"" + resultado.getString(2)
                    + "\" IDCONSUMIDOR=\"" + resultado.getString(3)
                    + "\" IDACTIVIDAD=\"" + resultado.getString(4)
                    + "\" IDLABOR=\"" + resultado.getString(5)
                    + "\" HORAS=\"" + resultado.getString(6)
                    + "\" IDSUBLABOR=\"" + resultado.getString(8)
                    + "\" IDTURNO=\"" + resultado.getString(9)
                    + "\" IDGRUPO=\"" + resultado.getString(10)
                    + "\" OBSERVACION=\"" + resultado.getString(7) + "\" />";
            listaTareoSQLiteSubir.add(items);
            //Log.i("TAREOANTERIOR", resultado.getString(0)+"//"+resultado.getString(1)+"//"+resultado.getString(2)+"//"+resultado.getString(3)+"//"+resultado.getString(4)+"//"+resultado.getString(5));
        }
        bd.close();
    }

    public void cargarListaTareoPantallaListaSQLite(){// BD SQLITE
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);

        Log.i("TAREO", "ENTRO ASIST");
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT " +
                "TAREO.IDTAREO, " +
                "TAREO.FECHA, " +
                "TAREO.IDUSUARIO, " +
                "USUARIO.DNI, " +
                "TAREO.IDCONSUMIDOR, " +
                "CONSUMIDOR.CONSUMIDOR, " +
                "TAREO.IDACTIVIDAD, " +
                "ACTIVIDAD.ACTIVIDAD, " +
                "TAREO.IDLABOR, " +
                "LABOR.LABOR," +
                "TAREO.TIPOLABOR, " +
                "(SELECT COUNT(*) FROM ASISTENCIA WHERE IDTAREO = TAREO.IDTAREO) AS TOTALTRABAJADORES, " +
                "TAREO.HORAS, " +
                "IFNULL(TAREO.OBSERVACION, ' - ') as OBSERVACION, " +
                "TAREO.SINCRONIZADO, " +
                "TAREO.IDSUBLABOR, " +
                "SUBLABOR.SUBLABOR, " +
                "SUBLABOR.GRUPO, " +
                "TAREO.IDGRUPO, " +
                "TAREO.IDTURNO  " +
                "FROM TAREO INNER JOIN USUARIO ON (TAREO.IDUSUARIO = USUARIO.IDUSUARIO ) " +
                //"INNER JOIN PERSONALGENERAL ON (USUARIO.DNI = PERSONALGENERAL.DNI) " +
                "INNER JOIN CONSUMIDOR ON (TAREO.IDCONSUMIDOR = CONSUMIDOR.IDCONSUMIDOR) " +
                "INNER JOIN ACTIVIDAD ON (TAREO.IDACTIVIDAD = ACTIVIDAD.IDACTIVIDAD) " +
                "INNER JOIN LABOR ON (TAREO.IDACTIVIDAD=LABOR.IDACTIVIDAD AND TAREO.IDLABOR = LABOR.IDLABOR ) " +
                "LEFT JOIN SUBLABOR ON (TAREO.IDACTIVIDAD = SUBLABOR.IDACTIVIDAD AND LABOR.IDLABOR = SUBLABOR.IDLABOR AND TAREO.IDSUBLABOR = SUBLABOR.IDSUBLABOR) " +
                "WHERE TAREO.IDUSUARIO = '"+MiAplicacionTareo.IDUSUARIO+"' AND  DATE(TAREO.FECHA, 'localtime')= DATE('" +fechaHoy +"') ORDER BY TAREO.FECHA DESC";
        //"WHERE TAREO.IDUSUARIO = '"+MiAplicacionTareo.IDUSUARIO+"' AND  DATE(TAREO.FECHA, 'localtime')=(SELECT DATE(date('now'))) ";

        Cursor resultado = bd.rawQuery(sql, null);
        Log.i("TAREO", "CURSOR ASIST"+resultado.getCount());
        listaTareoPantallaSQLite.clear();//Sin tareos de limpieza
        listaTareoPantallaSQLiteCompleta.clear();

        while (resultado.moveToNext()){
            Tareo objA = new Tareo();
            objA.setIDTAREO(resultado.getString(0));
            objA.setFECHA(resultado.getString(1));
            objA.setIDUSUARIO(resultado.getString(2));
            objA.setDNI(resultado.getString(3));
            objA.setIDCONSUMIDOR(resultado.getString(4));
            objA.setNOMBRECONSUMIDOR(resultado.getString(5));
            objA.setIDACTIVIDAD(resultado.getString(6));
            objA.setNOMBREACTIVIDAD(resultado.getString(7));
            objA.setIDLABOR(resultado.getString(8));
            objA.setNOMBRELABOR(resultado.getString(9));
            objA.setTIPOLABOR(resultado.getString(10));
            objA.setTOTALTRABAJADORES(resultado.getString(11));
            objA.setHORAS(resultado.getString(12));
            objA.setIDTURNO(resultado.getString(19));
            objA.setOBSERVACION(resultado.getString(13));
            objA.setSINCRONIZADO(resultado.getString(14));
            objA.setIDSUBLABOR(resultado.getString(15));
            objA.setNOMBRESUBLABOR(resultado.getString(16));
            objA.setGRUPO(resultado.getString(17));
            objA.setIDGRUPO(resultado.getString(18));

            //if(!resultado.getString(10).equalsIgnoreCase("L")){
            listaTareoPantallaSQLite.add(objA);
            //}
            listaTareoPantallaSQLiteCompleta.add(objA);
        }
        bd.close();
    }

    public void cargarListaTareoPantallaListaSQLite_2(String IDGRUPO){// BD SQLITE POR GRUPO
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);

        Log.i("TAREO", "ENTRO ASIST");
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT " +
                "TAREO.IDTAREO, " +
                "TAREO.FECHA, " +
                "TAREO.IDUSUARIO, " +
                "USUARIO.DNI, " +
                "TAREO.IDCONSUMIDOR, " +
                "CONSUMIDOR.CONSUMIDOR, " +
                "TAREO.IDACTIVIDAD, " +
                "ACTIVIDAD.ACTIVIDAD, " +
                "TAREO.IDLABOR, " +
                "LABOR.LABOR," +
                "TAREO.TIPOLABOR, " +
                "(SELECT COUNT(*) FROM ASISTENCIA WHERE IDTAREO = TAREO.IDTAREO) AS TOTALTRABAJADORES, " +
                "TAREO.HORAS, " +
                "IFNULL(TAREO.OBSERVACION, ' - ') as OBSERVACION, " +
                "TAREO.SINCRONIZADO, " +
                "TAREO.IDSUBLABOR, " +
                "SUBLABOR.SUBLABOR, " +
                "SUBLABOR.GRUPO, " +
                "TAREO.IDGRUPO, " +
                "TAREO.IDTURNO  " +
                "FROM TAREO INNER JOIN USUARIO ON (TAREO.IDUSUARIO = USUARIO.IDUSUARIO ) " +
                //"INNER JOIN PERSONALGENERAL ON (USUARIO.DNI = PERSONALGENERAL.DNI) " +
                "INNER JOIN CONSUMIDOR ON (TAREO.IDCONSUMIDOR = CONSUMIDOR.IDCONSUMIDOR) " +
                "INNER JOIN ACTIVIDAD ON (TAREO.IDACTIVIDAD = ACTIVIDAD.IDACTIVIDAD) " +
                "INNER JOIN LABOR ON (TAREO.IDACTIVIDAD=LABOR.IDACTIVIDAD AND TAREO.IDLABOR = LABOR.IDLABOR ) " +
                "LEFT JOIN SUBLABOR ON (TAREO.IDACTIVIDAD = SUBLABOR.IDACTIVIDAD AND LABOR.IDLABOR = SUBLABOR.IDLABOR AND TAREO.IDSUBLABOR = SUBLABOR.IDSUBLABOR) " +
                "WHERE TAREO.IDUSUARIO = '"+MiAplicacionTareo.IDUSUARIO+"' AND  DATE(TAREO.FECHA, 'localtime')= DATE('" +fechaHoy +"') AND TAREO.IDGRUPO='"+IDGRUPO+"' ORDER BY TAREO.FECHA DESC";
        //"WHERE TAREO.IDUSUARIO = '"+MiAplicacionTareo.IDUSUARIO+"' AND  DATE(TAREO.FECHA, 'localtime')=(SELECT DATE(date('now'))) ";

        Cursor resultado = bd.rawQuery(sql, null);
        Log.i("TAREO", "CURSOR ASIST"+resultado.getCount());
        listaTareoPantallaSQLite.clear();//Sin tareos de limpieza
        listaTareoPantallaSQLiteCompleta.clear();

        while (resultado.moveToNext()){
            Tareo objA = new Tareo();
            objA.setIDTAREO(resultado.getString(0));
            objA.setFECHA(resultado.getString(1));
            objA.setIDUSUARIO(resultado.getString(2));
            objA.setDNI(resultado.getString(3));
            objA.setIDCONSUMIDOR(resultado.getString(4));
            objA.setNOMBRECONSUMIDOR(resultado.getString(5));
            objA.setIDACTIVIDAD(resultado.getString(6));
            objA.setNOMBREACTIVIDAD(resultado.getString(7));
            objA.setIDLABOR(resultado.getString(8));
            objA.setNOMBRELABOR(resultado.getString(9));
            objA.setTIPOLABOR(resultado.getString(10));
            objA.setTOTALTRABAJADORES(resultado.getString(11));
            objA.setHORAS(resultado.getString(12));
            objA.setIDTURNO(resultado.getString(19));
            objA.setOBSERVACION(resultado.getString(13));
            objA.setSINCRONIZADO(resultado.getString(14));
            objA.setIDSUBLABOR(resultado.getString(15));
            objA.setNOMBRESUBLABOR(resultado.getString(16));
            objA.setGRUPO(resultado.getString(17));
            objA.setIDGRUPO(resultado.getString(18));

            //if(!resultado.getString(10).equalsIgnoreCase("L")){
            listaTareoPantallaSQLite.add(objA);
            //}
            listaTareoPantallaSQLiteCompleta.add(objA);
        }
        bd.close();
    }




    //hola
    //2
    private boolean comparar(String codigo_tareo){
        String sgte_segundos;
        String segundos = codigo_tareo.substring(codigo_tareo.length() - 2, codigo_tareo.length());
        if(segundos.equalsIgnoreCase("59")){
            sgte_segundos = "00";
        }else{
            sgte_segundos = String.valueOf(Integer.parseInt(segundos)+1);
        }

        return true;
    }



    public void cargarListaTareoImportarAsistenciaSQLite(String IDTAREO){// BD SQLITE
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);

        Log.i("TAREO", "ENTRO" + "-" + fechaHoy);
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT " +
                "TAREO.IDTAREO, " +
                "TAREO.FECHA, " +
                "TAREO.IDUSUARIO, " +
                "USUARIO.DNI, " +
                //"PERSONALGENERAL.NOMBRES, " +
                "TAREO.IDCONSUMIDOR, " +
                "CONSUMIDOR.CONSUMIDOR, " +
                "TAREO.IDACTIVIDAD, " +
                "ACTIVIDAD.ACTIVIDAD, " +
                "TAREO.IDLABOR, " +
                "LABOR.LABOR," +
                "TAREO.TIPOLABOR, " +
                "(SELECT COUNT(*) FROM ASISTENCIA WHERE IDTAREO = TAREO.IDTAREO) AS TOTALTRABAJADORES, " +
                "TAREO.HORAS " +
                //"FROM TAREO INNER JOIN USUARIO ON (TAREO.IDUSUARIO = USUARIO.IDUSUARIO)"+
                "FROM TAREO INNER JOIN USUARIO ON (TAREO.IDUSUARIO = USUARIO.IDUSUARIO ) " +
                //"INNER JOIN PERSONALGENERAL ON (USUARIO.DNI = PERSONALGENERAL.DNI) " +
                "INNER JOIN CONSUMIDOR ON (TAREO.IDCONSUMIDOR = CONSUMIDOR.IDCONSUMIDOR) " +
                "INNER JOIN ACTIVIDAD ON (TAREO.IDACTIVIDAD = ACTIVIDAD.IDACTIVIDAD) " +
                "INNER JOIN LABOR ON (TAREO.IDACTIVIDAD = LABOR.IDACTIVIDAD AND TAREO.IDLABOR = LABOR.IDLABOR ) " +
                "WHERE TAREO.IDUSUARIO = '"+MiAplicacionTareo.IDUSUARIO+"' AND  DATE(TAREO.FECHA, 'localtime')= DATE('" +fechaHoy +"') AND TAREO.IDTAREO != '" + IDTAREO + "' ";
        //"WHERE TAREO.IDUSUARIO = '"+MiAplicacionTareo.IDUSUARIO+"' AND DATE(TAREO.FECHA, 'localtime')=(SELECT DATE(date('now'))) AND TAREO.IDTAREO != '" + IDTAREO + "'";
        Cursor resultado = bd.rawQuery(sql, null);
        Log.i("TAREO", "CURSOR"+resultado.getCount());
        listaObjTareosImportar.clear();//Sin tareos de limpieza
        listaStrTareosImportar.clear();
        while (resultado.moveToNext()){
            //Log.i("TAREO", "HAY");
            Tareo objA = new Tareo();
            objA.setIDTAREO(resultado.getString(0));
            objA.setFECHA(resultado.getString(1));
            objA.setIDUSUARIO(resultado.getString(2));
            objA.setDNI(resultado.getString(3));
            //objA.setNOMBREUSUARIO(resultado.getString(4))
            objA.setIDCONSUMIDOR(resultado.getString(4));
            objA.setNOMBRECONSUMIDOR(resultado.getString(5));
            objA.setIDACTIVIDAD(resultado.getString(6));
            objA.setNOMBREACTIVIDAD(resultado.getString(7));
            objA.setIDLABOR(resultado.getString(8));
            objA.setNOMBRELABOR(resultado.getString(9));
            objA.setTIPOLABOR(resultado.getString(10));
            objA.setTOTALTRABAJADORES(resultado.getString(11));
            objA.setHORAS(resultado.getString(12));

            listaObjTareosImportar.add(objA);
            listaStrTareosImportar.add(resultado.getString(5)+ " | Trab: " + resultado.getString(11));
        }
        bd.close();
    }



    public long limpiarTablaTareoSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("TAREO", null, null);
        return 1;
    }

    public long eliminarTareo(String idTareo){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("TAREO", "IDTAREO = '" + idTareo + "'", null);
        return 1;
    }

    public long modificarEstadoASincronizadoTareoSQLite(){
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","1");
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.update("TAREO", cv, "SINCRONIZADO = '0'", null);
        return 1;
    }

    public long modificarCantidadHorasTareoSQLite(String horas, String IDTAREO){
        ContentValues cv = new ContentValues();
        cv.put("HORAS", horas);
        cv.put("SINCRONIZADO", "0");
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.update("TAREO", cv, "IDTAREO == '" + IDTAREO + "'", null);
        return 1;
    }

    public String obtenerTareoCosecha(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        String IDTAREO = "";
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT IDTAREO FROM TAREO WHERE IDACTIVIDAD = 'A07' AND IDLABOR = '001' AND IDSUBLABOR = '001' AND DATE(FECHA, 'localtime') = DATE('"+fechaHoy+"')";
        Cursor resultado = bd.rawQuery(sql, null);
        while (resultado.moveToNext()){
            IDTAREO = resultado.getString(resultado.getColumnIndex("IDTAREO"));
        }
        resultado.close();
        bd.close();
        return IDTAREO;
    }
}
