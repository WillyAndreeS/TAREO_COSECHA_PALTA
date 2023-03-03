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

public class SalidaPersonal extends SQLite {
    private String dni;
    private String fecha;
    private String hora;

    private String nombre;

    public static ArrayList<SalidaPersonal> listaPersonalConSalidaHoy = new ArrayList<SalidaPersonal>();
    public static List<String> listaSalidasSQLiteSubir = new ArrayList<>();

    public SalidaPersonal(String dni, String fecha, String hora, String nombre) {
        this.dni = dni;
        this.fecha = fecha;
        this.hora = hora;
        this.nombre = nombre;
    }

    public SalidaPersonal(String dni, String fecha, String hora) {
        this.dni = dni;
        this.fecha = fecha;
        this.hora = hora;
    }

    public SalidaPersonal() {
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long agregarSalida(String DNI, String HORA){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("DNI", DNI);
        //values.put("FECHA", FECHA);
        values.put("HORA", HORA);
        long resultado = db.insert("SALIDAPERSONAL", null, values);
        db.close();
        return resultado;
    }

    public boolean listarSalidasHoy(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);

        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select a.DNI, " +
                "IFNULL(p.NOMBRES, 'NO REGISTRADO') as NOMBRES, " +
                "DATE(a.FECHAREGISTRO, 'localtime') as FECHA, " +
                "a.HORA " +
                "from SALIDAPERSONAL a LEFT JOIN PERSONALGENERAL p ON (a.DNI = p.DNI) " +
                "where DATE(a.FECHAREGISTRO, 'localtime') = '"+fechaHoy+"'";//4 DESC
        //"where DATE(a.FECHAREGISTRO, 'localtime') = '"+fechaHoy+"' GROUP BY a.IDPERSONALGENERAL";//4 DESC
        //"where DATE(a.FECHAREGISTRO, 'localtime') = (SELECT DATE(date('now'))) GROUP BY a.IDPERSONALGENERAL";//no funcionaba en la noche correctamete la fecha
        //DATE(TAREO.FECHA, 'localtime')=(SELECT DATE(date('now')))
        Cursor resultado = bd.rawQuery(sql, null);
        listaPersonalConSalidaHoy.clear();
        while (resultado.moveToNext()){
            SalidaPersonal objSP = new SalidaPersonal();
            objSP.setDni(resultado.getString(0));
            objSP.setNombre(resultado.getString(1));
            objSP.setFecha(resultado.getString(2));
            objSP.setHora(resultado.getString(3));
            listaPersonalConSalidaHoy.add(objSP);
            Log.i("SALIDA", resultado.getString(0)+ "//" + resultado.getString(1) + "//" + resultado.getString(2) + "//" + resultado.getString(3).replace("-",""));
        }
        bd.close();

        if(listaPersonalConSalidaHoy.size() != 0){
            return true;
        }else{
            return false;
        }
    }

    public void obtenerListaSalidasTodasASubir(){
        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","2");//ENVIANDO
        bd.update("SALIDAPERSONAL", cv, "SINCRONIZADO = '0'", null);
        String sql = "select DNI, DATE(FECHAREGISTRO, 'localtime') as FECHA, HORA FROM SALIDAPERSONAL WHERE SINCRONIZADO = '2'";
        Cursor resultado = bd.rawQuery(sql, null);
        listaSalidasSQLiteSubir.clear();
        while (resultado.moveToNext()){
            String items = "<Item DNI=\"" + resultado.getString(0)
                    + "\" FECHA=\"" + resultado.getString(1).replace("-","")
                    + "\" HORA=\"" + resultado.getString(2) + ":00\" />";

            listaSalidasSQLiteSubir.add(items);
            //Log.i("TAREOID", resultado.getString(0)+ "//" + resultado.getString(1) + "//" + resultado.getString(2) + "//" + resultado.getString(3).replace("-",""));
        }
    }

    public long limpiarTablaSalidaSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("SALIDAPERSONAL", "DNI != '0'", null);
        return 1;
    }

    public long modificarEstadoASincronizadoTablaSalidaPersonalSQLite(){
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","1");
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.update("SALIDAPERSONAL", cv, "SINCRONIZADO = '0'", null);
        return 1;
    }
}