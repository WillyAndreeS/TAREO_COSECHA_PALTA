package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Log;

import com.acpagro.tareopalta.datos.SQLite;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketCosecha extends SQLite {
    private String IDTICKET;
    private String IDTAREO;
    private String IDCONSUMIDOR;
    private String IDVALVULA;
    private String FECHAREGISTRO;//Esta por defecto
    private String CONTEO;
    private String JABERO;
    private String ID_REGISTRA;
    private String VARIEDAD;
    public static ArrayList<TicketCosecha> listaTicketCosechaSQLite = new ArrayList<TicketCosecha>();
    public static ArrayList<TicketCosecha> listaRendimientoCosechadoresSQLite = new ArrayList<TicketCosecha>();
    public static ArrayList<TicketCosecha> listaTicketCosechaConteoSQLite = new ArrayList<TicketCosecha>();
    public static List<String> listaTicketCosechaSQLiteSubir = new ArrayList<>();

    private String DNI;
    private String NOMBRES;
    private String JABAS;
    private String KILOS;
    private String viajes;
    private String jabasCampo;
    private String totalviajes;

    private String CODIGO;//CODIGO UNICO (IDTELEFONO+IDGRUPO+YYYYMMDDHHmmss)

    private String SINCRONIZADO;

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

    public String getVARIEDAD() {
        return VARIEDAD;
    }

    public void setVARIEDAD(String VARIEDAD) {
        this.VARIEDAD = VARIEDAD;
    }

    public static int conteoPorViaje = 0;

    public TicketCosecha(String IDTICKET, String IDTAREO, String IDCONSUMIDOR, String IDVALVULA, String FECHAREGISTRO, String KILOS, String SINCRONIZADO) {
        this.IDTICKET = IDTICKET;
        this.IDTAREO = IDTAREO;
        this.IDCONSUMIDOR = IDCONSUMIDOR;
        this.IDVALVULA = IDVALVULA;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.KILOS = KILOS;
        this.SINCRONIZADO = SINCRONIZADO;
    }

    public void agregar_TicketCosechaNubeSQLite(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for(TicketCosecha tc : TicketCosecha.listaTicketCosechaSQLite){
                values.put("IDTICKET", tc.getIDTICKET());
                values.put("IDTAREO", tc.getIDTAREO());
                values.put("IDCONSUMIDOR", tc.getIDCONSUMIDOR());
                values.put("FECHAREGISTRO", tc.getFECHAREGISTRO());
                values.put("SINCRONIZADO", "2");
                db.insert("TICKETCOSECHA", null, values);
                Log.i("ETIQUETA", tc.getIDTICKET()+"/"+tc.getFECHAREGISTRO());
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public String getFechaExisteTicket(String IDTICKET){
        String FECHA = "NO_ENCONTRADO";
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT strftime('%d/%m/%Y', DATE(FECHAREGISTRO, 'localtime')) FROM TICKETCOSECHA WHERE IDTICKET='"+IDTICKET+"'";
        sql = "SELECT CASE WHEN SINCRONIZADO='2' THEN strftime('%d/%m/%Y', DATE(FECHAREGISTRO)) ELSE strftime('%d/%m/%Y', DATE(FECHAREGISTRO, 'localtime')) END FROM TICKETCOSECHA WHERE IDTICKET='"+IDTICKET+"'";
        Cursor resultado = bd.rawQuery(sql, null);
        while (resultado.moveToNext()){
            FECHA = resultado.getString(0);
        }

        bd.close();
        return FECHA;
    }

    public long limpiarTablaTicketCosechaSQLiteDataNube(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("TICKETCOSECHA", "SINCRONIZADO = '2'", null);
        return 1;
    }

    public long agregarTicketCosechaSQLite(String IDTICKET, String IDCONSUMIDOR, String VALVULA, String VARIEDAD, String ID_REGISTRA, Context context){//Lectura con PDA
        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        long resultado;
        try {
            values.put("IDTICKET", IDTICKET);
            values.put("IDCONSUMIDOR", IDCONSUMIDOR);
            values.put("IDVALVULA", VALVULA);
            values.put("VARIEDAD", VARIEDAD);
            values.put("ID_REGISTRA", ID_REGISTRA);
            values.put("FECHAREGISTRO", fechaHoy);
            values.put("CODIGO", IDTICKET+"_"+ID_REGISTRA+"_"+id+"_"+dateFormat2.format(date));
            resultado = db.insert("TICKETCOSECHA", null, values);
        }catch (Exception e){
            resultado = Long.parseLong("-1");
        }
        Log.i("RESULTADO", "|"+resultado);
        //long resultado = db.insert("TICKETCOSECHA", null, values);
        db.close();
        return resultado;
    }

    public void cargarListaTicketCosechaSQLite(){// BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","2");//ENVIANDO
        bd.update("TICKETCOSECHA", cv, "SINCRONIZADO = '0'", null);

        //String sql = "select IDTICKET, IDTAREO, IDCONSUMIDOR, DATETIME(FECHAREGISTRO, 'localtime') as FECHAREGISTRO, IDVIAJE, VARIEDAD, ID_REGISTRA, IFNULL(KG_DESMEDRO, 0) AS KG_DESMEDRO, IDVALVULA from TICKETCOSECHA WHERE SINCRONIZADO = '2' order by 1 ";
        String sql = "select IDTICKET, IDTAREO, IDCONSUMIDOR, FECHAREGISTRO as FECHAREGISTRO, IDVIAJE, VARIEDAD, ID_REGISTRA, IFNULL(KG_DESMEDRO, 0) AS KG_DESMEDRO, IDVALVULA, CODIGO from TICKETCOSECHA WHERE SINCRONIZADO = '2' order by 1 ";
        Cursor resultado = bd.rawQuery(sql, null);
        listaTicketCosechaSQLite.clear();
        listaTicketCosechaSQLiteSubir.clear();
        while (resultado.moveToNext()){
            TicketCosecha objTC = new TicketCosecha();
            objTC.setIDTICKET(resultado.getString(0));
            objTC.setIDTAREO(resultado.getString(1));
            objTC.setIDCONSUMIDOR(resultado.getString(2));
            objTC.setFECHAREGISTRO(resultado.getString(3));
            objTC.setVARIEDAD(resultado.getString(5));
            listaTicketCosechaSQLite.add(objTC);
            String items = "<Item IDETIQUETA=\"" + resultado.getString(0) +
                    "\" IDUSUARIO=\"" + resultado.getString(6) +
                    "\" VIAJE=\"" + resultado.getString(4) +
                    "\" FECHAREGISTRO=\"" + resultado.getString(3).replace("-","") +
                    "\" IDCONSUMIDOR=\"" + resultado.getString(2) +
                    "\" VARIEDAD=\"" + resultado.getString(5)  +
                    "\" KG_DESMEDRO=\"" + resultado.getString(7) +
                    "\" NROVALVULA=\"" + resultado.getString(8) +
                    "\" CODIGO=\"" + resultado.getString(9)  + "\" />";
            listaTicketCosechaSQLiteSubir.add(items);
        }
        bd.close();
    }

    public int getConteoPantallaLecturaPDA(){// BD SQLITE
        int conteo =0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        SQLiteDatabase bd = this.getReadableDatabase();
        //String sql = "SELECT COUNT(*) AS CONTEO FROM TICKETCOSECHA TC WHERE DATE(TC.FECHAREGISTRO, 'localtime') = '"+fechaHoy+"'";
        String sql = "SELECT COUNT(*) AS CONTEO FROM TICKETCOSECHA TC WHERE DATE(TC.FECHAREGISTRO) = '"+fechaHoy+"'";
        Cursor resultado = bd.rawQuery(sql, null);
        while (resultado.moveToNext()){
            conteo = resultado.getInt(0);
            //Log.i("CONTEO", ""+resultado.getString(0));
        }
        bd.close();
        return conteo;
    }

    public int getConteoPantallaLecturaPorGrupoPDA(String IDTICKET){// BD SQLITE
        int conteo =0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        SQLiteDatabase bd = this.getReadableDatabase();
        //String sql = "SELECT COUNT(*) AS CONTEO FROM TICKETCOSECHA TC WHERE DATE(TC.FECHAREGISTRO, 'localtime') = '"+fechaHoy+"' AND TC.IDTICKET='"+IDTICKET+"'";
        String sql = "SELECT COUNT(*) FROM TICKETCOSECHA TC WHERE DATE(TC.FECHAREGISTRO) = '"+fechaHoy+"' AND TC.IDTICKET='"+IDTICKET+"'";
        Cursor resultado = bd.rawQuery(sql, null);
        while (resultado.moveToNext()){
            conteo = resultado.getInt(0);
        }
        Log.i("CONTEOGRUPO", "|"+conteo );
        bd.close();
        return conteo;
    }

    public void cargarListaTicketCosechaConteoSQLite(String IDVIAJE){// BD SQLITE
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        Log.i("TAREO", "ENTRO "+IDVIAJE);
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT " +
                "substr(TC.IDTICKET,2,7) AS IDTICKET, " +
                "TC.IDVIAJE, " +
                "C.CONSUMIDOR, " +
                "TC.FECHAREGISTRO," +
                "COUNT(*) AS CONTEO, " +
                "TC.JABERO " +
                "FROM TICKETCOSECHA TC INNER JOIN CONSUMIDOR C ON(TC.IDCONSUMIDOR = C.IDCONSUMIDOR) " +
                "WHERE DATE(TC.FECHAREGISTRO, 'localtime') = '"+fechaHoy+"' AND TC.IDVIAJE = '"+IDVIAJE+"' GROUP BY TC.IDVIAJE, TC.IDCONSUMIDOR";
        Cursor resultado = bd.rawQuery(sql, null);
        listaTicketCosechaConteoSQLite.clear();
        //conteoPorViaje = 0;
        while (resultado.moveToNext()){
            /*if(!resultado.getString(5).equalsIgnoreCase("00000001")){
                conteoPorViaje += resultado.getInt(4);
            }*/
            //conteoPorViaje += resultado.getInt(4);
            listaTicketCosechaConteoSQLite.add(new TicketCosecha(resultado.getString(0), resultado.getString(2), resultado.getString(3), resultado.getString(4), resultado.getString(5)));
            Log.i("ITEM", ""+resultado.getString(0) + "/" + resultado.getCount());
        }
        bd.close();
    }

    public void cargarListaRendimientoCosechadoresSQLite(String ORDENAMIENTO){// BD SQLITE
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        //Log.i("FECHA", fechaHoy);
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql ="SELECT TP.IDPERSONALGENERAL, IFNULL(P.NOMBRES, 'NO REGISTRADO') , COUNT(TC.IDTICKET) AS JABAS, COUNT(TC.IDTICKET) * (1.5) AS KG " +
                "FROM TICKETPERSONA TP " +
                "LEFT JOIN TICKETCOSECHA TC ON TP.IDTICKET = substr(TC.IDTICKET,2,6) " +
                "LEFT JOIN PERSONALGENERAL P ON P.DNI = TP.IDPERSONALGENERAL " +
                "WHERE DATE(TC.FECHAREGISTRO, 'localtime')=DATE('"+fechaHoy+"') " +
                "GROUP BY TP.IDPERSONALGENERAL ORDER BY 3 "+ORDENAMIENTO;
        Cursor resultado = bd.rawQuery(sql, null);
        listaRendimientoCosechadoresSQLite.clear();
        while (resultado.moveToNext()){

            listaRendimientoCosechadoresSQLite.add(new TicketCosecha(
                    resultado.getString(0),
                    resultado.getString(1),
                    resultado.getString(2),
                    resultado.getString(3)));
            Log.i("ITEM", ""+resultado.getString(0) + "/" + resultado.getCount());
        }
        bd.close();
    }

    public int getConteoDatosParaTransferir(){// BD SQLITE
        int conteo1=0;
        int conteo2=0;
        int conteo3=0;
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql ="select (select count(*) from TICKETCOSECHA WHERE SINCRONIZADO = '0') AS CONTEO1, (SELECT COUNT(*) FROM TICKETPERSONA WHERE SINCRONIZADO = '0') AS CONTEO2, (SELECT COUNT(*) FROM REGISTRODEFECTOS WHERE SINCRONIZADO = '0') AS CONTEO2";
        Cursor resultado = bd.rawQuery(sql, null);
        listaRendimientoCosechadoresSQLite.clear();
        while (resultado.moveToNext()){
            conteo1=resultado.getInt(0);
            conteo2=resultado.getInt(1);
            conteo3=resultado.getInt(2);
            Log.i("CONTEO", ""+(conteo1+conteo2+conteo3));
        }
        bd.close();
        return (conteo1+conteo2+conteo3);
    }


    public void cargarListaRendimientoJaberosSQLite(String ORDENAMIENTO){// BD SQLITE
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql ="SELECT TC.JABERO, IFNULL(CASE WHEN TC.JABERO='00000000' THEN 'MERCADO NACIONAL' ELSE CASE WHEN TC.JABERO='00000001' THEN 'DESMEDRO' ELSE CASE WHEN TC.JABERO='00000002' THEN 'ADICIONAL' ELSE P.NOMBRES END END END, 'NO REGISTRADO') AS NOMBRES, " +
                " COUNT(TC.IDTICKET) AS JABAS, COUNT(TC.IDTICKET) * (1.5) AS KG\n" +
                " FROM TICKETCOSECHA TC\n" +
                " LEFT JOIN TICKETPERSONA TP ON substr(TC.IDTICKET,2,6) = TP.IDTICKET\n" +
                " LEFT JOIN PERSONALGENERAL P ON P.DNI = TC.JABERO\n" +
                " WHERE DATE(TC.FECHAREGISTRO, 'localtime')=DATE('"+fechaHoy+"')\n" +
                " GROUP BY TC.JABERO order by 3 "+ORDENAMIENTO;
        Cursor resultado = bd.rawQuery(sql, null);
        listaRendimientoCosechadoresSQLite.clear();
        while (resultado.moveToNext()){

            listaRendimientoCosechadoresSQLite.add(new TicketCosecha(
                    resultado.getString(0),
                    resultado.getString(1),
                    resultado.getString(2),
                    resultado.getString(3)));
            Log.i("ITEM", ""+resultado.getString(0) + "/" + resultado.getCount());
        }
        bd.close();
    }

    public void cargarListaTicketsSinAsignarSQLite(){// BD SQLITE
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql ="SELECT substr(TC.IDTICKET,2,6) AS TICKET, TC.JABERO, COUNT(substr(TC.IDTICKET,2,6)) AS TOTAL\n" +
                "FROM TICKETCOSECHA TC\n" +
                "LEFT JOIN TICKETPERSONA TP ON substr(TC.IDTICKET,2,6) = TP.IDTICKET\n" +
                "WHERE TP.IDTICKET IS NULL AND DATE(TC.FECHAREGISTRO, 'localtime')=DATE('"+fechaHoy+"')\n" +
                "GROUP BY substr(TC.IDTICKET,2,6)";
        Cursor resultado = bd.rawQuery(sql, null);
        listaRendimientoCosechadoresSQLite.clear();
        while (resultado.moveToNext()){

            listaRendimientoCosechadoresSQLite.add(new TicketCosecha(
                    ""+resultado.getString(1),
                    ""+resultado.getString(0),
                    ""+resultado.getString(2),
                    ""+(resultado.getInt(2)*1.5)));
            Log.i("ITEM", ""+resultado.getString(0) + "/" + resultado.getCount());
        }
        bd.close();
    }

    public long limpiarTablaTicketCosechaSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("TICKETCOSECHA", "IDTICKET != '0'", null);
        return 1;
    }

    public long eliminarLecturasHoy(){//Lecturas baldes Hoy
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("TICKETCOSECHA", "DATE(FECHAREGISTRO) = '"+fechaHoy+"'", null);
        return 1;
    }

    public long modificarEstadoASincronizadoTicketCosechaSQLite(){
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","1");
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.update("TICKETCOSECHA", cv, "SINCRONIZADO = '0'", null);
        return 1;
    }

    public int conteoTicketsLeidosHoy(){// BD SQLITE
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select count(*) as CONTEO from TICKETCOSECHA where DATE(FECHAREGISTRO) = '"+fechaHoy+"'";
        Cursor resultado = bd.rawQuery(sql, null);
        int conteo = 0;
        while (resultado.moveToNext()){
            conteo = resultado.getInt(0);
        }
        bd.close();
        return conteo;
    }

    public TicketCosecha() {

    }

    public TicketCosecha(String DNI, String NOMBRES, String JABAS, String KILOS) {
        this.DNI = DNI;
        this.NOMBRES = NOMBRES;
        this.JABAS = JABAS;
        this.KILOS = KILOS;
    }

    public TicketCosecha(String IDTICKET, String IDCONSUMIDOR, String FECHAREGISTRO, String CONTEO, String JABERO) {
        this.IDTICKET = IDTICKET;
        this.IDCONSUMIDOR = IDCONSUMIDOR;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.CONTEO = CONTEO;
        this.JABERO = JABERO;
    }



    public String getSINCRONIZADO() {
        return SINCRONIZADO;
    }

    public void setSINCRONIZADO(String SINCRONIZADO) {
        this.SINCRONIZADO = SINCRONIZADO;
    }

    public String getJABERO() {
        return JABERO;
    }

    public void setJABERO(String JABERO) {
        this.JABERO = JABERO;
    }

    public String getCONTEO() {
        return CONTEO;
    }

    public void setCONTEO(String CONTEO) {
        this.CONTEO = CONTEO;
    }

    public String getIDCONSUMIDOR() {
        return IDCONSUMIDOR;
    }

    public void setIDCONSUMIDOR(String IDCONSUMIDOR) {
        this.IDCONSUMIDOR = IDCONSUMIDOR;
    }

    public String getIDTICKET() {
        return IDTICKET;
    }

    public void setIDTICKET(String IDTICKET) {
        this.IDTICKET = IDTICKET;
    }

    public String getIDTAREO() {
        return IDTAREO;
    }

    public void setIDTAREO(String IDTAREO) {
        this.IDTAREO = IDTAREO;
    }

    public String getFECHAREGISTRO() {
        return FECHAREGISTRO;
    }

    public void setFECHAREGISTRO(String FECHAREGISTRO) {
        this.FECHAREGISTRO = FECHAREGISTRO;
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

    public String getJABAS() {
        return JABAS;
    }

    public void setJABAS(String JABAS) {
        this.JABAS = JABAS;
    }

    public String getKILOS() {
        return KILOS;
    }

    public void setKILOS(String KILOS) {
        this.KILOS = KILOS;
    }

    public String getIDVALVULA() {
        return IDVALVULA;
    }

    public void setIDVALVULA(String IDVALVULA) {
        this.IDVALVULA = IDVALVULA;
    }

    public String getID_REGISTRA() {
        return ID_REGISTRA;
    }

    public void setID_REGISTRA(String ID_REGISTRA) {
        this.ID_REGISTRA = ID_REGISTRA;
    }
}
