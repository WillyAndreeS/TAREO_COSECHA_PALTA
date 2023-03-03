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

public class TicketPersona extends SQLite {
    private String IDTICKET;
    private String IDTAREO;
    private String IDPERSONALGENERAL;
    private String FECHAREGISTRO;//Esta por defecto
    private String NOMBRES;
    private String NUMERO;//NUMERO QUE APARECE EN LA LISTA APARA SABER EL NRO DE ASIGNACION
    private String ID_REGISTRA;//IDUSURIO QUE HACE LOS REGISTROS

    private String IDJABERO;
    private String IDACOPIADOR;
    private String IDCOSECHADOR;
    private String SINCRONIZADO;

    public static ArrayList<TicketPersona> listaTicketPersonaSQLite = new ArrayList<>();
    public static ArrayList<TicketPersona> listaTicketPersonaSQLiteExtras = new ArrayList<>();
    public static ArrayList<TicketPersona> datosAsignacion = new ArrayList<>();
    public static List<String> listaTicketPersonaSQLiteSubir = new ArrayList<>();

    public TicketPersona(String NOMBRES, String IDJABERO, String IDACOPIADOR, String IDCOSECHADOR) {
        this.NOMBRES = NOMBRES;
        this.IDJABERO = IDJABERO;
        this.IDACOPIADOR = IDACOPIADOR;
        this.IDCOSECHADOR = IDCOSECHADOR;
    }

    public long agregarTicketPersonaSQLite(String IDTICKET, String IDTAREO, String IDPERSONALGENERAL){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("IDTICKET", IDTICKET);
        values.put("IDTAREO", IDTAREO);
        values.put("IDPERSONALGENERAL", IDPERSONALGENERAL);
        long resultado = db.insert("TICKETPERSONA", null, values);
        db.close();
        return resultado;
    }

    public long agregarTicketPersonaSQLiteSimple(String IDTICKET, String IDPERSONALGENERAL, String ID_REGISTRA){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("IDTICKET", IDTICKET);
        values.put("IDPERSONALGENERAL", IDPERSONALGENERAL);
        values.put("ID_REGISTRA", ID_REGISTRA);

        long resultado = db.insert("TICKETPERSONA", null, values);
        db.close();
        return resultado;
    }

    public void cargarListaTicketPersonaSQLite(){// BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","2");//ENVIANDO
        bd.update("TICKETPERSONA", cv, "SINCRONIZADO = '0'", null);

        String sql = "select IDTICKET, IDTAREO, IDPERSONALGENERAL, DATETIME(FECHAREGISTRO, 'localtime') as FECHAREGISTRO, ID_REGISTRA from TICKETPERSONA WHERE SINCRONIZADO = '2' order by 1 ";
        Cursor resultado = bd.rawQuery(sql, null);
        listaTicketPersonaSQLite.clear();
        listaTicketPersonaSQLiteSubir.clear();
        while (resultado.moveToNext()){
            TicketPersona objTP = new TicketPersona();
            objTP.setIDTICKET(resultado.getString(0));
            objTP.setIDTAREO(resultado.getString(1));
            objTP.setIDPERSONALGENERAL(resultado.getString(2));
            objTP.setFECHAREGISTRO(resultado.getString(3));
            listaTicketPersonaSQLite.add(objTP);
            String items = "<Item IDTAREO=\"" + resultado.getString(1) + "\" IDCODIGO=\"" + resultado.getString(0) + "\" DNI=\"" + resultado.getString(2) + "\" FECHAREGISTRO=\"" + resultado.getString(3).replace("-","") + "\" GRUPO=\"" + "NO"  + "\" IDUSUARIO=\"" + resultado.getString(4)  + "\" />";
            listaTicketPersonaSQLiteSubir.add(items);
        }
        bd.close();
    }

    public int buscarDni(String dni){// BD SQLITE
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        int respuesta = 0;
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT COUNT(*) CANTIDAD FROM TICKETPERSONA WHERE DATE(FECHAREGISTRO, 'localtime') = '"+fechaHoy+"' AND IDPERSONALGENERAL ='"+ dni +"'";
        Cursor resultado = bd.rawQuery(sql, null);

        while (resultado.moveToNext()){
            respuesta = resultado.getInt(0);
        }
        bd.close();
        return respuesta;
    }

    public void cargarListaTicketPersonaAsignados(){// BD SQLITE
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);

        SQLiteDatabase bd = this.getReadableDatabase();
        String sql="SELECT TP.IDTICKET, TP.IDTAREO, TP.IDPERSONALGENERAL, DATETIME(TP.FECHAREGISTRO, 'localtime') as FECHAREGISTRO, IFNULL(PG.NOMBRES, 'NO REGISTRADO') FROM TICKETPERSONA TP LEFT JOIN PERSONALGENERAL PG ON(TP.IDPERSONALGENERAL=PG.DNI) WHERE DATE(TP.FECHAREGISTRO, 'localtime') = '"+fechaHoy+"' /*AND TP.SINCRONIZADO!='2'*/ ORDER BY TP.IDTICKET DESC, TP.FECHAREGISTRO DESC";
        //String sql = "select IDTICKET, IDTAREO, IDPERSONALGENERAL, DATETIME(FECHAREGISTRO, 'localtime') as FECHAREGISTRO from TICKETPERSONA WHERE SINCRONIZADO != '1' order by 1 ";
        Cursor resultado = bd.rawQuery(sql, null);
        listaTicketPersonaSQLite.clear();
        int i=resultado.getCount();
        while (resultado.moveToNext()){

            listaTicketPersonaSQLite.add(new TicketPersona(
                    resultado.getString(0),
                    resultado.getString(2),
                    resultado.getString(3),
                    resultado.getString(4),
                    ""+i
            ));
            i--;
            Log.i("ASIGNACION", resultado.getString(4));
        }
        bd.close();
    }

    public void cargarListaTicketPersonaAsignadosConteoJabasGrupo(){// BD SQLITE
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);

        SQLiteDatabase bd = this.getReadableDatabase();
        String sql="SELECT \n" +
                "    TP.IDTICKET, \n" +
                "    TP.IDTAREO, \n" +
                "    TP.IDPERSONALGENERAL,\n" +
                //"    DATETIME(TP.FECHAREGISTRO, 'localtime') as FECHAREGISTRO, \n" +
                "    DATETIME(TP.FECHAREGISTRO) as FECHAREGISTRO, \n" +
                "    IFNULL(PG.NOMBRES, 'NO REGISTRADO'),\n" +
                //"    (SELECT COUNT(*) AS CONTEO FROM TICKETCOSECHA TC WHERE DATE(TC.FECHAREGISTRO, 'localtime') ='"+fechaHoy+"' AND TC.IDTICKET=TP.IDTICKET) AS CONTEO\n" +
                "    (SELECT COUNT(*) AS CONTEO FROM TICKETCOSECHA TC WHERE DATE(TC.FECHAREGISTRO) ='"+fechaHoy+"' AND TC.IDTICKET=TP.IDTICKET) AS CONTEO\n" +
                "FROM TICKETPERSONA TP \n" +
                "    LEFT JOIN PERSONALGENERAL PG ON(TP.IDPERSONALGENERAL=PG.DNI) \n" +
                "WHERE DATE(TP.FECHAREGISTRO) = '"+fechaHoy+"' ORDER BY TP.IDTICKET DESC, TP.FECHAREGISTRO DESC";
        /*sql = "SELECT \n" +
                "\tTC.IDTICKET,\n" +
                "\tNULL IDTAREO,\n" +
                "\tIFNULL(MAX(TP.IDPERSONALGENERAL),'SIN_ASIGNAR') IDPERSONALGENERAL,\n" +
                "\tDATETIME(MAX(TP.FECHAREGISTRO)) as FECHAREGISTRO,\n" +
                "\tIFNULL((CASE WHEN TP.IDPERSONALGENERAL ISNULL THEN 'SIN ASIGNAR' ELSE PG.NOMBRES END), 'NO REGISTRADO') COSECHADOR,\n" +
                "\t--IFNULL(PG.NOMBRES, 'NO REGISTRADO') COSECHADOR,\n" +
                "\tCOUNT(*) CONTEO\n" +
                "FROM TICKETCOSECHA TC \n" +
                "LEFT JOIN TICKETPERSONA TP ON(TC.IDTICKET=TP.IDTICKET)\n" +
                "LEFT JOIN PERSONALGENERAL PG ON(TP.IDPERSONALGENERAL=PG.DNI)\n" +
                "WHERE DATE(TC.FECHAREGISTRO) ='"+fechaHoy+"'\n" +
                "GROUP BY TC.IDTICKET";*/
        Cursor resultado = bd.rawQuery(sql, null);
        listaTicketPersonaSQLite.clear();
        int i=resultado.getCount();
        while (resultado.moveToNext()){

            listaTicketPersonaSQLite.add(new TicketPersona(
                    ""+resultado.getString(0),
                    ""+resultado.getString(2),
                    //""+resultado.getString(3),//Aqui pondre el conteo, para no crear otro constructor
                    ""+resultado.getString(5),//Aqui pondre el conteo, para no crear otro constructor
                    ""+resultado.getString(4),
                    ""+i
            ));
            i--;
            Log.i("ASIGNACION", resultado.getString(4));
        }
        bd.close();
    }

    public boolean cargarListaTicketPersonaAsignadosV2(){// EXTRAS
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql="SELECT TP.IDTICKET, TP.IDTAREO, TP.IDPERSONALGENERAL, DATETIME(TP.FECHAREGISTRO, 'localtime') as FECHAREGISTRO, IFNULL(PG.NOMBRES, 'NO REGISTRADO') FROM TICKETPERSONA TP LEFT JOIN PERSONALGENERAL PG ON(TP.IDPERSONALGENERAL=PG.DNI) WHERE DATE(TP.FECHAREGISTRO, 'localtime') = '"+dateFormat.format(new Date())+"' AND TP.SINCRONIZADO='2' ORDER BY TP.FECHAREGISTRO DESC";
        Cursor resultado = bd.rawQuery(sql, null);
        listaTicketPersonaSQLiteExtras.clear();
        int i=resultado.getCount();
        while (resultado.moveToNext()){
            listaTicketPersonaSQLiteExtras.add(new TicketPersona(
                    resultado.getString(0),
                    resultado.getString(2),
                    resultado.getString(3),
                    resultado.getString(4),
                    ""+i
            ));
            i--;
            //Log.i("ASIGNACION", resultado.getString(4));
        }
        bd.close();
        return true;
    }

    public void getAsignacion(String IDTICKET){// BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        //String sql="SELECT TP.IDTICKET, TP.IDPERSONALGENERAL, DATETIME(TP.FECHAREGISTRO, 'localtime') as FECHAREGISTRO, IFNULL(PG.NOMBRES, 'NO REGISTRADO') FROM TICKETPERSONA TP LEFT JOIN PERSONALGENERAL PG ON(TP.IDPERSONALGENERAL=PG.DNI) WHERE TP.IDTICKET = '"+IDTICKET+"'";
        String sql = "SELECT \n" +
                "    TP.IDTICKET, \n" +
                "    TP.IDPERSONALGENERAL AS IDCOSECHADOR, \n" +
                "    DATETIME(TP.FECHAREGISTRO, 'localtime') as FECHAREGISTRO, \n" +
                "    IFNULL(PG.NOMBRES, 'NO REGISTRADO') AS NOMBRES,\n" +
                "    IFNULL(TC.JABERO, 'SIN_JABERO') AS IDJABERO, \n" +
                "    (SELECT DNI FROM USUARIO WHERE IDUSUARIO='"+MiAplicacionTareo.IDUSUARIO+"') AS IDACOPIADOR \n" +
                "FROM \n" +
                "    TICKETPERSONA TP LEFT JOIN \n" +
                "    PERSONALGENERAL PG ON(TP.IDPERSONALGENERAL=PG.DNI) LEFT JOIN \n" +
                "    TICKETCOSECHA TC ON(TP.IDTICKET=substr(TC.IDTICKET,2,6)) WHERE TP.IDTICKET = '"+IDTICKET+"'\n" +
                "    GROUP BY TP.IDTICKET ";
        Cursor resultado = bd.rawQuery(sql, null);
        datosAsignacion.clear();
        while (resultado.moveToNext()){
            datosAsignacion.add(new TicketPersona(
                    "NOMBRES: "+resultado.getString(3),
                    ""+resultado.getString(4),
                    ""+MiAplicacionTareo.DNIUSUARIO,
                    ""+resultado.getString(1)
            ));
            Log.i("DATOS", resultado.getString(0));
        }
        bd.close();

        if(datosAsignacion.size()==0){
            datosAsignacion.add(new TicketPersona("NOMBRES: NO_ENCONTRADO", "NO_ENCONTRADO", "NO_ENCONTRADO", "NO_ENCONTRADO"));
        }
    }

    public long limpiarTablaTicketPersonaSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("TICKETPERSONA", "IDTICKET != '0'", null);
        return 1;
    }

    public long eliminarTicketPersonaSQLite(String IDTAREO, String IDPERSONALGENERAL){
        SQLiteDatabase bd = this.getReadableDatabase();
        long r = bd.delete("TICKETPERSONA", "IDTAREO = '"+IDTAREO+"' AND IDPERSONALGENERAL = '"+IDPERSONALGENERAL+"'" , null);
        Log.i("DELETE", ""+r);
        return 1;
    }

    public long eliminarTicketPersonaAsignacionSQLite(String IDTICKET, String IDPERSONALGENERAL){
        SQLiteDatabase bd = this.getReadableDatabase();
        long r = bd.delete("TICKETPERSONA", "IDTICKET = '"+IDTICKET+"' AND IDPERSONALGENERAL = '"+IDPERSONALGENERAL+"'" , null);
        Log.i("DELETE", ""+r);
        return 1;
    }

    public long eliminarTicketPersonaAsignacionAsistenciaSQLite(String IDPERSONALGENERAL){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        SQLiteDatabase bd = this.getReadableDatabase();
        long r = bd.delete("TICKETPERSONA", "DATE(FECHAREGISTRO, 'localtime') = '"+fechaHoy+"' AND IDPERSONALGENERAL = '"+IDPERSONALGENERAL+"'" , null);
        Log.i("DELETE", ""+r);
        return 1;
    }

    public long modificarEstadoASincronizadoTicketPersonaSQLite(){
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","1");
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.update("TICKETPERSONA", cv, "SINCRONIZADO = '0'", null);
        return 1;
    }

    public int conteoAsignacionesHoy(){// BD SQLITE
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select count(*) as CONTEO from TICKETPERSONA where DATE(FECHAREGISTRO, 'localtime') = '"+fechaHoy+"' AND SINCRONIZADO!='2'";
        Cursor resultado = bd.rawQuery(sql, null);
        int conteo = 0;
        while (resultado.moveToNext()){
            conteo = resultado.getInt(0);
        }
        bd.close();
        return conteo;
    }

    public int conteoAsignacionesHoyExtras(){// BD SQLITE
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select count(*) as CONTEO from TICKETPERSONA where DATE(FECHAREGISTRO, 'localtime') = '"+fechaHoy+"' AND SINCRONIZADO='2'";
        Cursor resultado = bd.rawQuery(sql, null);
        int conteo = 0;
        while (resultado.moveToNext()){
            conteo = resultado.getInt(0);
        }
        bd.close();
        return conteo;
    }

    public void agregar_TicketPersonaNubeSQLite(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for(TicketPersona tp : TicketPersona.listaTicketPersonaSQLiteExtras){
                values.put("IDTICKET", tp.getIDTICKET());
                values.put("IDTAREO", tp.getIDTAREO());
                values.put("IDPERSONALGENERAL", tp.getIDPERSONALGENERAL());
                values.put("FECHAREGISTRO", tp.getFECHAREGISTRO());
                values.put("SINCRONIZADO", "2");
                db.insert("TICKETPERSONA", null, values);
                Log.i("ETIQUETA_ASI", tp.getIDTICKET()+"/"+tp.getFECHAREGISTRO()+"/"+tp.getIDPERSONALGENERAL());
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public long limpiarTablaTicketPersonaSQLiteDataNube(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("TICKETPERSONA", "SINCRONIZADO = '2'", null);
        return 1;
    }

    public TicketPersona() {
    }

    public TicketPersona(String IDTICKET, String IDPERSONALGENERAL, String FECHAREGISTRO) {
        this.IDTICKET = IDTICKET;
        this.IDPERSONALGENERAL = IDPERSONALGENERAL;
        this.FECHAREGISTRO = FECHAREGISTRO;
    }

    public String getID_REGISTRA() {
        return ID_REGISTRA;
    }

    public void setID_REGISTRA(String ID_REGISTRA) {
        this.ID_REGISTRA = ID_REGISTRA;
    }

    public String getSINCRONIZADO() {
        return SINCRONIZADO;
    }

    public void setSINCRONIZADO(String SINCRONIZADO) {
        this.SINCRONIZADO = SINCRONIZADO;
    }

    public String getIDJABERO() {
        return IDJABERO;
    }

    public void setIDJABERO(String IDJABERO) {
        this.IDJABERO = IDJABERO;
    }

    public String getIDACOPIADOR() {
        return IDACOPIADOR;
    }

    public void setIDACOPIADOR(String IDACOPIADOR) {
        this.IDACOPIADOR = IDACOPIADOR;
    }

    public String getIDCOSECHADOR() {
        return IDCOSECHADOR;
    }

    public void setIDCOSECHADOR(String IDCOSECHADOR) {
        this.IDCOSECHADOR = IDCOSECHADOR;
    }

    public TicketPersona(String IDTICKET, String IDPERSONALGENERAL, String FECHAREGISTRO, String NOMBRES, String NUMERO) {
        this.IDTICKET = IDTICKET;
        this.IDPERSONALGENERAL = IDPERSONALGENERAL;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.NOMBRES = NOMBRES;
        this.NUMERO = NUMERO;
    }

    public TicketPersona(String IDTICKET, String IDTAREO, String IDPERSONALGENERAL, String FECHAREGISTRO, String NOMBRES, String SINCRONIZADO) {
        this.IDTICKET = IDTICKET;
        this.IDTAREO = IDTAREO;
        this.IDPERSONALGENERAL = IDPERSONALGENERAL;
        this.FECHAREGISTRO = FECHAREGISTRO;
        this.NOMBRES = NOMBRES;
        this.SINCRONIZADO = SINCRONIZADO;
    }

    public String getNUMERO() {
        return NUMERO;
    }

    public void setNUMERO(String NUMERO) {
        this.NUMERO = NUMERO;
    }

    public String getIDTAREO() {
        return IDTAREO;
    }

    public void setIDTAREO(String IDTAREO) {
        this.IDTAREO = IDTAREO;
    }

    public String getIDTICKET() {
        return IDTICKET;
    }

    public void setIDTICKET(String IDTICKET) {
        this.IDTICKET = IDTICKET;
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

    public String getNOMBRES() {
        return NOMBRES;
    }

    public void setNOMBRES(String NOMBRES) {
        this.NOMBRES = NOMBRES;
    }
}
