package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.acpagro.tareopalta.PantallaAsistenciaPersonal;
import com.acpagro.tareopalta.datos.SQLite;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Asistencia extends SQLite {
    private String IDTAREO;
    private String IDPERSONALGENERAL;
    private String FECHAREGISTRO;

    private String NOMBRES;
    private String ESTADOSALIDA;


    public static ArrayList<Asistencia> listaAsistenciaSQLite = new ArrayList<Asistencia>();
    public static List<String> listaAsistenciaSQLiteSubir = new ArrayList<>();
    public static List<Asistencia> listaAsistenciaDia = new ArrayList<>();
    public static String NOMBREOBSERVADO = "";
    public static String MOTIVO_OBSERVADO = "";

    public Asistencia() {

    }

    public Asistencia(String IDTAREO, String IDPERSONALGENERAL, String NOMBRES, String FECHAREGISTRO) {
        this.IDTAREO = IDTAREO;
        this.IDPERSONALGENERAL = IDPERSONALGENERAL;
        this.NOMBRES = NOMBRES;
        this.FECHAREGISTRO = FECHAREGISTRO;
    }

    public Asistencia(String IDTAREO, String IDPERSONALGENERAL, String NOMBRES, String FECHAREGISTRO, String ESTADOSALIDA) {
        this.IDTAREO = IDTAREO;
        this.IDPERSONALGENERAL = IDPERSONALGENERAL;
        this.NOMBRES = NOMBRES;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.ESTADOSALIDA = ESTADOSALIDA;
    }

    public String getESTADOSALIDA() {
        return ESTADOSALIDA;
    }

    public void setESTADOSALIDA(String ESTADOSALIDA) {
        this.ESTADOSALIDA = ESTADOSALIDA;
    }

    public String getNOMBRES() {
        return NOMBRES;
    }

    public void setNOMBRES(String NOMBRES) {
        this.NOMBRES = NOMBRES;
    }

    public String getIDTAREO() {
        return IDTAREO;
    }

    public void setIDTAREO(String IDTAREO) {
        this.IDTAREO = IDTAREO;
    }

    public String getIDPERSONALGENERAL() {
        return IDPERSONALGENERAL;
    }

    public void setIDPERSONALGENERAL(String IDPERSONALGENERAL) {
        this.IDPERSONALGENERAL = IDPERSONALGENERAL;
    }

    public String getFECHAREGISTRO() {
        return FECHAREGISTRO;
    }

    public void setFECHAREGISTRO(String FECHAREGISTRO) {
        this.FECHAREGISTRO = FECHAREGISTRO;
    }

    public long agregarAsistenciaSQLite(String IDTAREO, String IDPERSONALGENERAL){
        SQLiteDatabase dbr = this.getReadableDatabase();
        String select = "SELECT OBSERVADO, NOMBRES, MODULO, MOTIVO FROM PERSONALGENERAL WHERE DNI = '"+ IDPERSONALGENERAL +"'";
        Cursor r = dbr.rawQuery(select, null);
        String observado = "NO";
        String MODULO = "";
        String MOTIVO = "";
        while (r.moveToNext()){
            observado = r.getString(0);//SI O NO
            NOMBREOBSERVADO = r.getString(1);//NOMBRES
            MODULO = r.getString(2);//MODULO
            MOTIVO_OBSERVADO = r.getString(3);//MOTIVO
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("MODULO", MODULO);
        long resultado = 100;//SE GRABA CORRECTAMENTE

        if(r.getCount()!=0){
            if(observado.trim().equalsIgnoreCase("SI")){
                resultado = 500;//Observado
            }else if(!(MODULO == null) && !(MODULO.isEmpty()) && !(MODULO.equalsIgnoreCase("null")) && !(MODULO.equalsIgnoreCase(""+ PantallaAsistenciaPersonal.MODULO_SELECCIONADO))){
                ContentValues values = new ContentValues();
                values.put("IDTAREO", IDTAREO);
                values.put("IDPERSONALGENERAL", IDPERSONALGENERAL);
                long rx = db.insert("ASISTENCIA", null, values);
                resultado = 400;//CAMBIO DE MODULO
            }else{
                //SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("IDTAREO", IDTAREO);
                values.put("IDPERSONALGENERAL", IDPERSONALGENERAL);
                resultado = db.insert("ASISTENCIA", null, values);
            }
        }else{
            resultado = 600;//NO ENCONTRADO EN LA LISTA DE PERSONAL SINCRONIZADA (PUEDE ESTAR CESADO, POR ENDE SE DEBE COMPROBAR EN LÍNEA)
        }


        //db.close();
        return resultado;
    }

    public long agregarAsistenciaSQLiteNuevoFichas(String IDTAREO, String IDPERSONALGENERAL){
        SQLiteDatabase dbr = this.getReadableDatabase();
        String select = "SELECT OBSERVADO, NOMBRES, MODULO, MOTIVO FROM PERSONALGENERAL WHERE DNI = '"+ IDPERSONALGENERAL +"'";
        Cursor r = dbr.rawQuery(select, null);
        String observado = "NO";
        while (r.moveToNext()){
            observado = r.getString(0);//SI O NO
            NOMBREOBSERVADO = r.getString(1);//NOMBRES
            MOTIVO_OBSERVADO = r.getString(3);//MOTIVO
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("MOTIVO_OBSERVADO", MOTIVO_OBSERVADO);
        long resultado = 100;//SE GRABA CORRECTAMENTE

        if(observado.trim().equalsIgnoreCase("SI")){
            resultado = 500;//Observado
        }else{
            //SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("IDTAREO", IDTAREO);
            values.put("IDPERSONALGENERAL", IDPERSONALGENERAL);
            resultado = db.insert("ASISTENCIA", null, values);
            db.close();
        }
        return resultado;
    }

    public long eliminarAsistenciaSQLite(String IDTAREO, String IDPERSONALGENERAL, String fecharegistro){
        SQLiteDatabase bd = this.getReadableDatabase();
        long r = bd.delete("ASISTENCIA", "IDTAREO = '"+IDTAREO+"' AND IDPERSONALGENERAL = '"+IDPERSONALGENERAL+"' AND DATETIME(FECHAREGISTRO, 'localtime')= DATETIME('"+ fecharegistro +"')" , null);
        //long d = bd.delete("DETALLETAREO", "IDTAREO = '"+IDTAREO+"' AND IDPERSONALGENERAL = '"+IDPERSONALGENERAL+"'" , null);

        Log.i("DELETE", IDTAREO+" "+r+" "+IDPERSONALGENERAL +" " +fecharegistro);
        return 1;
    }

    public long eliminarAsistenciaCosechaSQLite(String IDPERSONALGENERAL){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);

        SQLiteDatabase bd = this.getReadableDatabase();
        long r = bd.delete("ASISTENCIA", "IDPERSONALGENERAL = '"+IDPERSONALGENERAL+"' AND DATE(FECHAREGISTRO, 'localtime')= '"+ fechaHoy +"'" , null);
        //long d = bd.delete("DETALLETAREO", "IDTAREO = '"+IDTAREO+"' AND IDPERSONALGENERAL = '"+IDPERSONALGENERAL+"'" , null);
        return 1;
    }

    public long eliminarAsistenciaSQLite(String IDTAREO, String IDPERSONALGENERAL){
        SQLiteDatabase bd = this.getReadableDatabase();
        long r = bd.delete("ASISTENCIA", "IDTAREO = '"+IDTAREO+"' AND IDPERSONALGENERAL = '"+IDPERSONALGENERAL+"'" , null);
        Log.i("DELETE", ""+r);
        return 1;
    }

    public long eliminarTareoAsistencia(String idTareo){
        SQLiteDatabase bd = this.getReadableDatabase();
        long r = bd.delete("ASISTENCIA", "IDTAREO = '"+idTareo+"'" , null);
        return 1;
    }

    public boolean obtenerListaTrabajadoresAsistenciaPorTareo(String id_tareo, String campoOrdenar, String tipoOrden){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);

        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select a.IDTAREO, " +
                "a.IDPERSONALGENERAL, " +
                "IFNULL(p.NOMBRES, 'NO REGISTRADO') as NOMBRES, " +
                "(STRFTIME('%Y-%m-%d %H:%M:%f', a.FECHAREGISTRO, 'localtime')) as FECHA, " +
                //"DATETIME(a.FECHAREGISTRO, 'localtime') as FECHA, " +
                //"(SELECT COUNT(*) SALIDAPERSONAL s WHERE s.DNI = a.IDPERSONALGENERAL AND DATE(s.FECHAREGISTRO, 'localtime') = '"+fechaHoy+"') as CONTEO " +
                "s.DNI " +
                "from ASISTENCIA a LEFT JOIN PERSONALGENERAL p ON (a.IDPERSONALGENERAL = p.DNI) " +
                "LEFT JOIN SALIDAPERSONAL s ON(a.IDPERSONALGENERAL = s.DNI AND DATE(a.FECHAREGISTRO, 'localtime') = DATE(s.FECHAREGISTRO, 'localtime')) " +
                "where a.IDTAREO = '"+id_tareo+"' order by "+campoOrdenar + " " + tipoOrden;//4 DESC
        Cursor resultado = bd.rawQuery(sql, null);
        listaAsistenciaSQLite.clear();
        while (resultado.moveToNext()){
            Asistencia objA = new Asistencia();
            objA.setIDTAREO(resultado.getString(0));
            objA.setIDPERSONALGENERAL(resultado.getString(1));
            objA.setNOMBRES(resultado.getString(2));
            objA.setFECHAREGISTRO(resultado.getString(3));
            objA.setESTADOSALIDA(resultado.getString(4));
            listaAsistenciaSQLite.add(objA);
            Log.i("ASISTENCIA", resultado.getString(0)+ "//" + resultado.getString(1) + "//" + resultado.getString(2) + "//" + resultado.getString(3).replace("-","") + " // " +resultado.getString(4));
        }
        bd.close();

        if(listaAsistenciaSQLite.size() != 0){
            return true;
        }else{
            return false;
        }
    }

    public boolean obtenerListaAsistenciaDelDia(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);

        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select a.IDTAREO, " +
                "a.IDPERSONALGENERAL, " +
                "IFNULL(p.NOMBRES, 'NO REGISTRADO') as NOMBRES, " +
                "DATETIME(a.FECHAREGISTRO, 'localtime') as FECHA " +
                "from ASISTENCIA a LEFT JOIN PERSONALGENERAL p ON (a.IDPERSONALGENERAL = p.DNI) " +
                "where DATE(a.FECHAREGISTRO, 'localtime') = '"+fechaHoy+"' GROUP BY a.IDPERSONALGENERAL order by 3";//4 DESC
        //"where DATE(a.FECHAREGISTRO, 'localtime') = (SELECT DATE(date('now'))) GROUP BY a.IDPERSONALGENERAL";//no funcionaba en la noche correctamete la fecha
        //DATE(TAREO.FECHA, 'localtime')=(SELECT DATE(date('now')))
        Cursor resultado = bd.rawQuery(sql, null);
        listaAsistenciaSQLite.clear();
        while (resultado.moveToNext()){
            Asistencia objA = new Asistencia();
            objA.setIDTAREO(resultado.getString(0));
            objA.setIDPERSONALGENERAL(resultado.getString(1));
            objA.setNOMBRES(resultado.getString(2));
            objA.setFECHAREGISTRO(resultado.getString(3));
            listaAsistenciaSQLite.add(objA);
            Log.i("TAREOID", resultado.getString(0)+ "//" + resultado.getString(1) + "//" + resultado.getString(2) + "//" + resultado.getString(3).replace("-",""));
        }
        bd.close();

        if(listaAsistenciaSQLite.size() != 0){
            return true;
        }else{
            return false;
        }
    }

    public void obtenerListaAsistenciaTodasASubir(){
        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","2");//ENVIANDO
        bd.update("ASISTENCIA", cv, "SINCRONIZADO = '0'", null);
        //String sql = "select IDTAREO, IDPERSONALGENERAL, DATETIME(FECHAREGISTRO, 'localtime') as FECHA FROM ASISTENCIA WHERE SINCRONIZADO != '1'";
        String sql = "select IDTAREO, IDPERSONALGENERAL, (STRFTIME('%Y-%m-%d %H:%M:%f', FECHAREGISTRO, 'localtime')) as FECHA FROM ASISTENCIA WHERE SINCRONIZADO = '2'";
        Cursor resultado = bd.rawQuery(sql, null);
        listaAsistenciaSQLiteSubir.clear();
        while (resultado.moveToNext()){
            String items = "<Item IDTAREO=\"" + resultado.getString(0)
                    + "\" IDPERSONALGENERAL=\"" + resultado.getString(1).replace("-","")
                    + "\" FECHAREGISTRO=\"" + resultado.getString(2).replace("-","") + "\" />";
            listaAsistenciaSQLiteSubir.add(items);
            //Log.i("TAREOID", resultado.getString(0)+ "//" + resultado.getString(1) + "//" + resultado.getString(2) + "//" + resultado.getString(3).replace("-",""));
        }
    }

    public void importarListaTrabajadoresAsistenciaAOtroTareo(String id_tareo_inicio, String id_tareo_final){
        SQLiteDatabase bd = this.getReadableDatabase();
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select a.IDTAREO, " +
                "a.IDPERSONALGENERAL, " +
                "IFNULL(p.NOMBRES, 'NO REGISTRADO') as NOMBRES, " +
                "DATETIME(a.FECHAREGISTRO, 'localtime') as FECHA " +
                "from ASISTENCIA a LEFT JOIN PERSONALGENERAL p ON (a.IDPERSONALGENERAL = p.DNI) " +
                "where a.IDTAREO = '"+id_tareo_inicio+"' order by 3 ";
        Cursor resultado = bd.rawQuery(sql, null);
        while (resultado.moveToNext()){
            Log.i("TAREOID", resultado.getString(0)+ "//" + resultado.getString(1) + "//" + resultado.getString(2) + "//" + resultado.getString(3).replace("-",""));

            ContentValues values = new ContentValues();
            values.put("IDTAREO", id_tareo_final);
            values.put("IDPERSONALGENERAL", resultado.getString(1));
            long resultadoAgregar = db.insert("ASISTENCIA", null, values);
        }
        bd.close();
    }

    public long limpiarTablaAsistenciaSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("ASISTENCIA", null, null);
        return 1;
    }
    public long modificarEstadoASincronizadoTablaAsistenciaSQLite(){
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","1");
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.update("ASISTENCIA", cv, "SINCRONIZADO = '0'", null);
        return 1;
    }

    public String cantidadPersonalxTareo(String idTareo){
        String cantidad = "";
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT COUNT(*) CANTIDAD FROM ASISTENCIA WHERE IDTAREO = '" + idTareo + "'";
        Cursor resultado = bd.rawQuery(sql, null);
        while (resultado.moveToNext()){
            cantidad = resultado.getString(resultado.getColumnIndex("CANTIDAD"));
        }
        resultado.close();
        bd.close();
        return cantidad;
    }

}
