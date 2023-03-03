package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.acpagro.tareopalta.datos.SQLite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoraPersonal extends SQLite {
    private String IDTAREO;
    private String DNI;
    private String HORAS;

    private String FECHAREGISTRO;
    private String SINCRONIZADO;

    private String NOMBRE;

    public static ArrayList<HoraPersonal> listaPersonalConHorasManualmenteIngresadas = new ArrayList<HoraPersonal>();
    public static ArrayList<HoraPersonal> listaPersonalHorasPorFecha = new ArrayList<HoraPersonal>();
    public static List<String> listaPerCHManIngSQLiteSubir = new ArrayList<>();


    public HoraPersonal(String IDTAREO, String DNI, String HORAS, String FECHAREGISTRO, String SINCRONIZADO) {
        this.IDTAREO = IDTAREO;
        this.DNI = DNI;
        this.HORAS = HORAS;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.SINCRONIZADO = SINCRONIZADO;
    }

    public HoraPersonal() {
    }

    public String getNOMBRE() {
        return NOMBRE;
    }

    public void setNOMBRE(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }

    public String getIDTAREO() {
        return IDTAREO;
    }

    public void setIDTAREO(String IDTAREO) {
        this.IDTAREO = IDTAREO;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
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

    public String getSINCRONIZADO() {
        return SINCRONIZADO;
    }

    public void setSINCRONIZADO(String SINCRONIZADO) {
        this.SINCRONIZADO = SINCRONIZADO;
    }

    public long agregarHoraPersonal(String IDTAREO, String DNI, String HORAS){
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select IDTAREO, DNI, HORAS, DATE(FECHAREGISTRO, 'localtime') as FECHAREGISTRO FROM HORAPERSONAL WHERE IDTAREO = '"+IDTAREO+"' AND DNI = '"+DNI+"'";
        Cursor resultado = bd.rawQuery(sql, null);
        int conteo = 0;
        while (resultado.moveToNext()){
            conteo++;
        }
        //Log.i("CONTEO", ""+conteo);

        if(conteo == 0){
            //Inserta Uno nuevo a la tabla HORAPERSONAL
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("IDTAREO", IDTAREO);
            values.put("DNI", DNI);
            values.put("HORAS", HORAS);
            db.insert("HORAPERSONAL", null, values);
            db.close();
            Log.i("INSERTA", "INSERCION");
        }else{
            //Actuualiza
            ContentValues cv = new ContentValues();
            cv.put("HORAS", HORAS);
            cv.put("SINCRONIZADO", "0");
            bd.update("HORAPERSONAL", cv, "IDTAREO == '" + IDTAREO + "' AND DNI = '"+ DNI + "'", null);
            Log.i("ACTUALIZA", "ACTUALIZANDO");
        }
        bd.close();
        return 1;
    }

    public void obtenerListaHoraPersonalASubir(){
        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","2");//ENVIANDO
        bd.update("HORAPERSONAL", cv, "SINCRONIZADO = '0'", null);
        String sql = "select IDTAREO, DNI, HORAS, DATE(FECHAREGISTRO, 'localtime') as FECHAREGISTRO FROM HORAPERSONAL WHERE SINCRONIZADO = '2'";
        Cursor resultado = bd.rawQuery(sql, null);
        listaPerCHManIngSQLiteSubir.clear();
        while (resultado.moveToNext()){
            String items = "<Item IDTAREO=\"" + resultado.getString(0)
                    + "\" DNI=\"" + resultado.getString(1).replace("-","")
                    + "\" HORAS=\"" + resultado.getString(2) + "\" />";

            listaPerCHManIngSQLiteSubir.add(items);
        }
    }

    public long limpiarTablaHoraPersonalSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("HORAPERSONAL", "IDTAREO != '0'", null);
        return 1;
    }

    public long modificarEstadoASincronizadoTablaHoraPersonalSQLite(){
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","1");
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.update("HORAPERSONAL", cv, "SINCRONIZADO = '0'", null);
        return 1;
    }

    public long modificarCantidadHorasPersonalSQLite(String IDTAREO, String DNI, String horas){
        ContentValues cv = new ContentValues();
        cv.put("HORAS", horas);
        cv.put("SINCRONIZADO", "0");
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.update("HORAPERSONAL", cv, "IDTAREO == '" + IDTAREO + "' AND DNI = '"+ DNI + "'", null);
        return 1;
    }


      /*    public static String tablaHorasPersonal =
    "CREATE TABLE HORAPERSONAL(
        IDTAREO VARCHAR(50),
        DNI VARCHAR(8),
        HORAS NUMERIC(10,2),
        FECHAREGISTRO DATETIME DbEFAULT CURRENT_TIMESTAMP,
        SINCRONIZADO CHAR(1) DEFAULT '0',
        PRIMARY KEY (IDTAREO, DNI))";
*/

    //PERSONALGENERAL(CODIGO VARCHAR(20) PRIMARY KEY, DNI VARCHAR(8), NOMBRES VARCHAR(200))

    //IDTAREO VARCHAR(50), IDPERSONALGENERAL VARCHAR(20), FECHAREGISTRO DATETIME DEFAULT CURRENT_TIMESTAMP, SINCRONIZADO CHAR(1) DEFAULT '0', UNIQUE(IDTAREO, IDPERSONALGENERAL)
    //IDTAREO VARCHAR(50) PRIMARY KEY, FECHA DATETIME DEFAULT CURRENT_TIMESTAMP, IDUSUARIO INT, IDCONSUMIDOR VARCHAR(50), IDACTIVIDAD VARCHAR(10), IDLABOR VARCHAR(10), TIPOLABOR CHAR(1), HORAS NUMERIC(10,2) DEFAULT 9.5, SINCRONIZADO CHAR(1) DEFAULT '0', OBSERVACION VARCHAR(150)
    public void listarHorasPersonal(String id_tareo){
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT T.IDTAREO, A.IDPERSONALGENERAL, IFNULL(PG.NOMBRES, 'NO REGISTRADO') as NOMBRES,  CASE WHEN HP.DNI IS NOT NULL THEN HP.HORAS ELSE T.HORAS END AS HORAS FROM ASISTENCIA A INNER JOIN TAREO T ON (T.IDTAREO = A.IDTAREO) LEFT JOIN PERSONALGENERAL PG ON (A.IDPERSONALGENERAL = PG.DNI) " +
                " LEFT JOIN HORAPERSONAL HP ON(A.IDTAREO = HP.IDTAREO AND A.IDPERSONALGENERAL = HP.DNI) WHERE T.IDTAREO = '"+id_tareo+"'";

        Cursor resultado = bd.rawQuery(sql, null);
        listaPersonalConHorasManualmenteIngresadas.clear();
        while (resultado.moveToNext()){
            HoraPersonal objDA = new HoraPersonal();
            objDA.setIDTAREO(resultado.getString(0));
            objDA.setDNI(resultado.getString(1));
            objDA.setNOMBRE(resultado.getString(2));
            objDA.setHORAS(resultado.getString(3));
            listaPersonalConHorasManualmenteIngresadas.add(objDA);
            Log.i("HORA_PERSONAL", resultado.getString(0)+ "//" + resultado.getString(1) + "//" + resultado.getString(2) + "//" + resultado.getString(3));
        }
        bd.close();
    }

    public void listarPersonalHorasPorFecha(String fecha){
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT " +
                "T.IDTAREO, " +
                "A.IDPERSONALGENERAL, " +
                "IFNULL(PG.NOMBRES, 'NO REGISTRADO') as NOMBRES,  " +
                "SUM(CASE WHEN HP.DNI IS NOT NULL THEN HP.HORAS ELSE T.HORAS END) AS HORAS " +
                "FROM ASISTENCIA A INNER JOIN TAREO T ON (T.IDTAREO = A.IDTAREO) " +
                "LEFT JOIN PERSONALGENERAL PG ON (A.IDPERSONALGENERAL = PG.DNI) " +
                "LEFT JOIN HORAPERSONAL HP ON(A.IDTAREO = HP.IDTAREO AND A.IDPERSONALGENERAL = HP.DNI) " +
                "WHERE DATE(T.FECHA) = '"+fecha+"'" +
                "GROUP BY A.IDPERSONALGENERAL";

        Cursor resultado = bd.rawQuery(sql, null);
        listaPersonalHorasPorFecha.clear();
        while (resultado.moveToNext()){
            HoraPersonal objDA = new HoraPersonal();
            objDA.setIDTAREO(resultado.getString(0));
            objDA.setDNI(resultado.getString(1));
            objDA.setNOMBRE(resultado.getString(2));
            objDA.setHORAS(resultado.getString(3));
            listaPersonalHorasPorFecha.add(objDA);
            Log.i("HORA_PERSONAL", resultado.getString(0)+ "//" + resultado.getString(1) + "//" + resultado.getString(2) + "//" + resultado.getString(3));
        }
        bd.close();
    }

    public int conteoCumpleConElNivelDeHorasPermitido(String fecha){
        HoraTareo objHT = new HoraTareo();

        double horaLimite;
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date date = new Date();
        String dayOfWeek = simpledateformat.format(date);

        horaLimite = objHT.getHoraTareoHoy(MiAplicacionTareo.diaAbrevitura(dayOfWeek));
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT " +
                "T.IDTAREO, " +
                "A.IDPERSONALGENERAL, " +
                "IFNULL(PG.NOMBRES, 'NO REGISTRADO') as NOMBRES,  " +
                "SUM(CASE WHEN HP.DNI IS NOT NULL THEN HP.HORAS ELSE T.HORAS END) AS HORAS " +
                "FROM ASISTENCIA A INNER JOIN TAREO T ON (T.IDTAREO = A.IDTAREO) " +
                "LEFT JOIN PERSONALGENERAL PG ON (A.IDPERSONALGENERAL = PG.DNI) " +
                "LEFT JOIN HORAPERSONAL HP ON(A.IDTAREO = HP.IDTAREO AND A.IDPERSONALGENERAL = HP.DNI) " +
                "WHERE DATE(T.FECHA) = '"+fecha+"'" +
                "GROUP BY A.IDPERSONALGENERAL";
        Cursor resultado = bd.rawQuery(sql, null);
        int conteo = 0;
        while (resultado.moveToNext()){
            if(!(Double.parseDouble(resultado.getString(3)) <= horaLimite)){
                conteo++;
            }
        }
        bd.close();
        return conteo;
    }

    /*public int conteoCumpleConElNivelDeHorasPermitidoBK(String fecha){
        double horaLimite;
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date date = new Date();
        String dayOfWeek = simpledateformat.format(date);
        if(dayOfWeek.equalsIgnoreCase("miércoles")){
            horaLimite = MiApplicacionACP.HORALIMITE_MIERCOLES;
        }else{
            horaLimite = MiApplicacionACP.HORALIMITE_LUNAVIE;
        }

        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT " +
                "T.IDTAREO, " +
                "A.IDPERSONALGENERAL, " +
                "IFNULL(PG.NOMBRES, 'NO REGISTRADO') as NOMBRES,  " +
                "SUM(CASE WHEN HP.DNI IS NOT NULL THEN HP.HORAS ELSE T.HORAS END) AS HORAS " +
                "FROM ASISTENCIA A INNER JOIN TAREO T ON (T.IDTAREO = A.IDTAREO) " +
                "LEFT JOIN PERSONALGENERAL PG ON (A.IDPERSONALGENERAL = PG.DNI) " +
                "LEFT JOIN HORAPERSONAL HP ON(A.IDTAREO = HP.IDTAREO AND A.IDPERSONALGENERAL = HP.DNI) " +
                "WHERE DATE(T.FECHA) = '"+fecha+"'" +
                "GROUP BY A.IDPERSONALGENERAL";
        Cursor resultado = bd.rawQuery(sql, null);
        int conteo = 0;
        while (resultado.moveToNext()){
            if(!(Double.parseDouble(resultado.getString(3)) <= horaLimite)){
                conteo++;
            }
        }
        bd.close();
        return conteo;
    }*/
}
