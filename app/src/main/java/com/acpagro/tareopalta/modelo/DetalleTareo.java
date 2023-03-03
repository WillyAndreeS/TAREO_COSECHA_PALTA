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

public class DetalleTareo extends SQLite {
    private String IDTAREO;
    private String IDPERSONALGENERAL;
    private String NOMBRES;
    private String RENDIMIENTO;
    private String IDSUBLABOR;
    private String FECHAREGISTRO;
    private String IDVALVULA;
    private String IDCONSUMIDOR;
    private String SINCRONIZADO;

    public static String nombre;
    public static double sumatoria;
    public static double total;

    public static ArrayList<DetalleTareo> listaDetalleTareoSQLite = new ArrayList<DetalleTareo>();
    public static ArrayList<DetalleTareo> listaDetalleTareoResumenSQLite = new ArrayList<DetalleTareo>();
    public static List<String> listaDetalleTareoResumenSQLiteSubir = new ArrayList<>();

    public DetalleTareo(String IDTAREO, String IDPERSONALGENERAL, String RENDIMIENTO, String IDSUBLABOR, String FECHAREGISTRO, String SINCRONIZADO) {
        this.IDTAREO = IDTAREO;
        this.IDPERSONALGENERAL = IDPERSONALGENERAL;
        this.RENDIMIENTO = RENDIMIENTO;
        this.IDSUBLABOR = IDSUBLABOR;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.SINCRONIZADO = SINCRONIZADO;
    }

    public DetalleTareo(String IDTAREO, String IDPERSONALGENERAL, String NOMBRES, String RENDIMIENTO, String IDSUBLABOR, String FECHAREGISTRO, String SINCRONIZADO) {
        this.IDTAREO = IDTAREO;
        this.IDPERSONALGENERAL = IDPERSONALGENERAL;
        this.NOMBRES = NOMBRES;
        this.RENDIMIENTO = RENDIMIENTO;
        this.IDSUBLABOR = IDSUBLABOR;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.SINCRONIZADO = SINCRONIZADO;
    }

    public DetalleTareo(String IDTAREO, String IDPERSONALGENERAL, String NOMBRES, String RENDIMIENTO, String IDSUBLABOR, String FECHAREGISTRO, String IDVALVULA, String IDCONSUMIDOR, String SINCRONIZADO) {
        this.IDTAREO = IDTAREO;
        this.IDPERSONALGENERAL = IDPERSONALGENERAL;
        this.NOMBRES = NOMBRES;
        this.RENDIMIENTO = RENDIMIENTO;
        this.IDSUBLABOR = IDSUBLABOR;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.IDVALVULA = IDVALVULA;
        this.IDCONSUMIDOR = IDCONSUMIDOR;
        this.SINCRONIZADO = SINCRONIZADO;
    }

    public String getSINCRONIZADO() {
        return SINCRONIZADO;
    }

    public void setSINCRONIZADO(String SINCRONIZADO) {
        this.SINCRONIZADO = SINCRONIZADO;
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

    public String getRENDIMIENTO() {
        return RENDIMIENTO;
    }

    public void setRENDIMIENTO(String RENDIMIENTO) {
        this.RENDIMIENTO = RENDIMIENTO;
    }

    public String getIDSUBLABOR() {
        return IDSUBLABOR;
    }

    public void setIDSUBLABOR(String IDSUBLABOR) {
        this.IDSUBLABOR = IDSUBLABOR;
    }

    public String getFECHAREGISTRO() {
        return FECHAREGISTRO;
    }

    public void setFECHAREGISTRO(String FECHAREGISTRO) {
        this.FECHAREGISTRO = FECHAREGISTRO;
    }

    public String getIDVALVULA() {
        return IDVALVULA;
    }

    public void setIDVALVULA(String IDVALVULA) {
        this.IDVALVULA = IDVALVULA;
    }

    public String getIDCONSUMIDOR() {
        return IDCONSUMIDOR;
    }

    public void setIDCONSUMIDOR(String IDCONSUMIDOR) {
        this.IDCONSUMIDOR = IDCONSUMIDOR;
    }

    public DetalleTareo() {
    }

    public long agregarDetalleTareoSQLite(String IDTAREO, String IDPERSONALGENERAL, Double RENDIMIENTO, String IDSUBLABOR, String IDVALVULA, String IDCONSUMIDOR){
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "";
        if(!IDPERSONALGENERAL.equals("")){
            sql = "SELECT COUNT(*) FROM ASISTENCIA WHERE IDTAREO = '"+IDTAREO+"' AND IDPERSONALGENERAL = '"+IDPERSONALGENERAL+"'";
        }else{
            sql = "SELECT COUNT(*) FROM ASISTENCIA WHERE IDTAREO = '"+IDTAREO+"' ";
        }

        Cursor resultado = bd.rawQuery(sql, null);
        int conteo = 0;//Cuando no se encuentra en la bd
        while (resultado.moveToNext()){
            conteo= resultado.getInt(0);
        }

        if (IDPERSONALGENERAL.length()==3) {
            conteo=1;
        }

        if(conteo > 0 ){
            ContentValues values = new ContentValues();
            values.put("IDTAREO", IDTAREO);
            values.put("IDPERSONALGENERAL", IDPERSONALGENERAL);
            values.put("RENDIMIENTO", RENDIMIENTO);
            values.put("IDSUBLABOR", IDSUBLABOR);
            values.put("IDVALVULA", IDVALVULA);
            values.put("IDCONSUMIDOR", IDCONSUMIDOR);
            //long resultado = db.insert("DETALLETAREO", null, values);
            return db.insert("DETALLETAREO", null, values);
        }
        else{
            return 500;
        }

    }


    public boolean obtenerNombreUsuario(String dni){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        int conteo = 0;

        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT DNI, NOMBRES, (SELECT COUNT(*) FROM ASISTENCIA WHERE DATE(FECHAREGISTRO, 'localtime') = '"+fechaHoy+"' and IDPERSONALGENERAL = '"+dni+"') AS CONTEO FROM PERSONALGENERAL WHERE DNI = '"+dni+"'";
        Cursor resultado = bd.rawQuery(sql, null);
        nombre = "No registrado";//Cuando no se encuentra en la bd
        while (resultado.moveToNext()){
            nombre = resultado.getString(1);
            conteo = resultado.getInt(2);
            Log.i("NOMBREUSER_CONTEO", nombre + "/" + conteo);
        }

        bd.close();

        if(conteo != 0){
            return true;
        }else{
            return false;
        }
    }

    public void obtenerDatosUsuarioYListaDetalleTareo(String id_tareo, String id_sublabor, String dni){
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT DNI, NOMBRES FROM PERSONALGENERAL WHERE DNI = '"+dni+"'";
        Cursor resultado = bd.rawQuery(sql, null);
        nombre = "No registrado";//Cuando no se encuentra en la bd
        while (resultado.moveToNext()){
            nombre = resultado.getString(1);
        }

        if(!dni.equals("")){
            sql = "SELECT IDTAREO, IDPERSONALGENERAL, RENDIMIENTO, IDSUBLABOR, DATETIME(FECHAREGISTRO, 'localtime') AS FECHA, SINCRONIZADO " +
                    "FROM DETALLETAREO " +
                    "WHERE IDTAREO = '"+id_tareo+"' AND IDSUBLABOR = '" + id_sublabor + "' AND IDPERSONALGENERAL = '" + dni + "' AND RENDIMIENTO > 0 ORDER BY 4 ";
        }else{
            sql = "SELECT IDTAREO, IDPERSONALGENERAL, RENDIMIENTO, IDSUBLABOR, DATETIME(FECHAREGISTRO, 'localtime') AS FECHA, SINCRONIZADO " +
                    "FROM DETALLETAREO WHERE IDTAREO = '"+id_tareo+"' AND IDSUBLABOR = '" + id_sublabor + "' AND RENDIMIENTO > 0 ";
        }

        Cursor resultado2 = bd.rawQuery(sql, null);
        listaDetalleTareoSQLite.clear();

        sumatoria = 0.0;
        while (resultado2.moveToNext()){
            DetalleTareo objDA = new DetalleTareo();
            objDA.setIDTAREO(resultado2.getString(0));
            objDA.setIDPERSONALGENERAL(resultado2.getString(1));
            objDA.setRENDIMIENTO(resultado2.getString(2));
            objDA.setIDSUBLABOR(resultado2.getString(3));
            objDA.setFECHAREGISTRO(resultado2.getString(4));
            objDA.setSINCRONIZADO(resultado2.getString(5));
            listaDetalleTareoSQLite.add(objDA);
            sumatoria+= resultado2.getDouble(2);
        }
        bd.close();
    }

    public boolean verificarSiEstaEnAsistencia(String IDTAREO, String IDPERSONALGENERAL){
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM ASISTENCIA WHERE IDTAREO = '"+IDTAREO+"' AND IDPERSONALGENERAL = '"+IDPERSONALGENERAL+"'";
        Log.i("CONSULTA", ""+ sql);
        Cursor resultado = bd.rawQuery(sql, null);
        int conteo = 0;//Cuando no se encuentra en la bd
        while (resultado.moveToNext()){
            conteo= resultado.getInt(0);
        }

        if(conteo == 1){
            return true;
        }else{
            return false;
        }
    }

    public void obtenerListaResumida(String id_tareo, String id_sublabor, String grupo){

        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "";
        if(grupo.equals("SI")){
            sql = "select TA.IDTAREO, 'GRUPO' AS IDPERSONALGENERAL, '' AS NOMBRES, " +
                    "IFNULL(SUM(DT.RENDIMIENTO), 0) AS RENDTOTAL, " +
                    "DATETIME(TA.FECHA, 'localtime') as FECHA, TA.SINCRONIZADO " +
                    "FROM TAREO TA LEFT JOIN DETALLETAREO DT ON TA.IDTAREO=DT.IDTAREO " +
                    "WHERE TA.IDTAREO = '"+id_tareo+"' AND DT.IDSUBLABOR = '"+id_sublabor+"' " +
                    "GROUP BY TA.IDTAREO ORDER BY 3";
        }else{
            sql = "SELECT TA.IDTAREO, AST.IDPERSONALGENERAL, IFNULL(PG.NOMBRES, 'NO REGISTRADO') as NOMBRES, " +
                    "IFNULL( SUM(DT.RENDIMIENTO) , 0) as RENDTOTAL, DATETIME(TA.FECHA, 'localtime') as FECHA, TA.SINCRONIZADO " +
                    "FROM TAREO TA " +
                    "LEFT JOIN ASISTENCIA AST ON TA.IDTAREO=AST.IDTAREO " +
                    "LEFT JOIN PERSONALGENERAL PG ON AST.IDPERSONALGENERAL = PG.DNI " +
                    "LEFT JOIN DETALLETAREO DT ON TA.IDTAREO=DT.IDTAREO AND AST.IDPERSONALGENERAL = DT.IDPERSONALGENERAL " +
                    "WHERE TA.IDTAREO = '"+id_tareo+"' GROUP BY TA.IDTAREO, AST.IDPERSONALGENERAL ORDER BY 3";//4 DESC
        }

        Cursor resultado = bd.rawQuery(sql, null);
        listaDetalleTareoResumenSQLite.clear();
        total = 0.0;
        while (resultado.moveToNext()){
            DetalleTareo objDA = new DetalleTareo();
            objDA.setIDTAREO(resultado.getString(0));
            objDA.setIDPERSONALGENERAL(resultado.getString(1));
            objDA.setNOMBRES(resultado.getString(2));
            objDA.setRENDIMIENTO(resultado.getString(3));
            //objDA.setIDSUBLABOR(resultado.getString(4));
            objDA.setFECHAREGISTRO(resultado.getString(4));
            listaDetalleTareoResumenSQLite.add(objDA);
            total+= (resultado.getDouble(3));
        }
        bd.close();
    }

    public void obtenerListaResumidaTodasDetTareosSQLiteParaSubir(){
        String dni="";
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select d.IDTAREO, " +
                "d.IDPERSONALGENERAL, " +
                "IFNULL(p.NOMBRES, 'NO REGISTRADO') as NOMBRES, " +
                "SUM(d.RENDIMIENTO) as RENDTOTAL, " +
                "d.IDSUBLABOR, " +
                "DATETIME(d.FECHAREGISTRO, 'localtime') as FECHA " +
                "from DETALLETAREO d LEFT JOIN PERSONALGENERAL p ON (d.IDPERSONALGENERAL = p.DNI)  WHERE d.SINCRONIZADO != '1'" +
                " group by d.IDTAREO, d.IDPERSONALGENERAL order by 1 ";
        Cursor resultado = bd.rawQuery(sql, null);
        listaDetalleTareoResumenSQLiteSubir.clear();
        sumatoria = 0.0;
        while (resultado.moveToNext()){
            DetalleTareo objDA = new DetalleTareo();
            objDA.setIDTAREO(resultado.getString(0));
            objDA.setIDPERSONALGENERAL(resultado.getString(1));
            objDA.setNOMBRES(resultado.getString(2));
            objDA.setRENDIMIENTO(resultado.getString(3));
            objDA.setIDSUBLABOR(resultado.getString(4));
            objDA.setFECHAREGISTRO(resultado.getString(5));
            /*if(objDA.getIDPERSONALGENERAL().length()==0){
                dni=objDA.getIDSUBLABOR();
            }else{
                dni=objDA.getIDPERSONALGENERAL();
            }*/
            String items = "<Item IDTAREO=\"" + resultado.getString(0) + "\" IDPERSONALGENERAL=\"" + resultado.getString(1) + "\" RENDIMIENTO=\"" + resultado.getString(3) + "\" IDSUBLABOR=\"" + resultado.getString(4) + "\" FECHAREGISTRO=\"" + resultado.getString(5).replace("-","")  + "\" />";
            //String items = "<Item IDTAREO=\"" + resultado.getString(0) + "\" IDPERSONALGENERAL=\"" + resultado.getString(1) + "\" RENDIMIENTO=\"" + resultado.getString(3) + "\" FECHAREGISTRO=\"" + resultado.getString(5).replace("-","")  + "\" />";
            listaDetalleTareoResumenSQLiteSubir.add(items);
            Log.i("DNI_RENDSUM", resultado.getString(1)+ "//" + resultado.getString(3) + "//" + resultado.getString(2) + "//" + resultado.getString(5).replace("-",""));
        }
        bd.close();
    }
//

    public void obtenerRendimientosTareoXML(){//SIN AGRUPAR
        SQLiteDatabase bd = this.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","2");//ENVIANDO
        bd.update("DETALLETAREO", cv, "SINCRONIZADO = '0'", null);

        String sql = "select d.IDTAREO, " +
                "d.IDPERSONALGENERAL, " +
                "IFNULL(p.NOMBRES, 'NO REGISTRADO') as NOMBRES, " +
                "d.RENDIMIENTO AS RENDTOTAL/*SUM(d.RENDIMIENTO) as RENDTOTAL*/, " +
                "d.IDSUBLABOR, " +
                "(STRFTIME('%Y-%m-%d %H:%M:%f', FECHAREGISTRO, 'localtime')) as FECHA, " +
                "d.IDVALVULA, " +
                "d.IDCONSUMIDOR " +
                "from DETALLETAREO d LEFT JOIN PERSONALGENERAL p ON (d.IDPERSONALGENERAL = p.DNI)  WHERE d.SINCRONIZADO = '2'" +
                " --group by d.IDTAREO, d.IDPERSONALGENERAL order by 1 ";
        Cursor resultado = bd.rawQuery(sql, null);
        listaDetalleTareoResumenSQLiteSubir.clear();
        while (resultado.moveToNext()){
            DetalleTareo objDA = new DetalleTareo();
            objDA.setIDTAREO(resultado.getString(0));
            objDA.setIDPERSONALGENERAL(resultado.getString(1));
            objDA.setNOMBRES(resultado.getString(2));
            objDA.setRENDIMIENTO(resultado.getString(3));
            objDA.setIDSUBLABOR(resultado.getString(4));
            objDA.setFECHAREGISTRO(resultado.getString(5));
            objDA.setIDVALVULA(resultado.getString(6));
            objDA.setIDCONSUMIDOR(resultado.getString(7));
            String items = "<Item IDTAREO=\"" + resultado.getString(0) + "\" IDPERSONALGENERAL=\"" + resultado.getString(1) + "\" RENDIMIENTO=\"" + resultado.getString(3) + "\" IDSUBLABOR=\"" + resultado.getString(4) + "\" FECHAREGISTRO=\"" + resultado.getString(5).replace("-","") + "\" IDVALVULA=\"" + resultado.getString(6) + "\" IDCONSUMIDOR=\"" + resultado.getString(7) + "\" />";
            //String items = "<Item IDTAREO=\"" + resultado.getString(0) + "\" IDPERSONALGENERAL=\"" + resultado.getString(1) + "\" RENDIMIENTO=\"" + resultado.getString(3) + "\" FECHAREGISTRO=\"" + resultado.getString(5).replace("-","")  + "\" />";
            listaDetalleTareoResumenSQLiteSubir.add(items);
            Log.i("DNI_RENDSUM", resultado.getString(1)+ "//" + resultado.getString(3) + "//" + resultado.getString(2) + "//" + resultado.getString(5).replace("-",""));
        }
        bd.close();
    }



    public long limpiarTablaDetalleTareoSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("DETALLETAREO", "IDTAREO != '0'", null);
        return 1;
    }

    public long eliminarTareoDetalle(String idTareo){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("DETALLETAREO", "IDTAREO = '" + idTareo + "'", null);
        return 1;
    }

    public long eliminarDetalleTareoSQLite(String idtareo, String dni, String fecharegistro){
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select SINCRONIZADO, RENDIMIENTO from DETALLETAREO WHERE DATETIME(FECHAREGISTRO, 'localtime')= '"+fecharegistro+"'";
        Cursor resultado = bd.rawQuery(sql, null);
        String sincronizado = "";
        int rendimiento = 0;
        while (resultado.moveToNext()) {
            sincronizado = resultado.getString(0);
            rendimiento = resultado.getInt(1);
        }

        if(sincronizado.equalsIgnoreCase("1")){
            //update
            ContentValues cv = new ContentValues();
            cv.put("SINCRONIZADO","0");
            cv.put("RENDIMIENTO", -1 * rendimiento);
            bd.update("DETALLETAREO", cv, "DATETIME(FECHAREGISTRO, 'localtime')= '"+ fecharegistro +"'" , null);
        }else{
            //delete
            long r = bd.delete("DETALLETAREO", "DATETIME(FECHAREGISTRO, 'localtime')= '"+ fecharegistro +"'" , null);
        }

        return 1;
    }

    public long modificarEstadoASincronizadoDetalleTareoSQLite(){
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","1");
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.update("DETALLETAREO", cv, "SINCRONIZADO = '0'", null);
        return 1;
    }

    public long modificarCantidadHorasDetalleTareoSQLite(String horas, String IDTAREO){
        ContentValues cv = new ContentValues();
        cv.put("IDSUBLABOR", horas);
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.update("DETALLETAREO", cv, "IDTAREO == '" + IDTAREO + "'", null);
        return 1;
    }
}
