package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.acpagro.tareopalta.datos.SQLite;

import java.util.ArrayList;

public class Usuario extends SQLite {
    private String IDUSUARIO;
    private String DNI;
    private String CLAVE;
    private String IDCULTIVO;
    private String LEE_PDA;
    private String TIPO_USUARIO;

    private String CULTIVO;
    private String USUARIO;
    private String TAREO;
    private String ESTADOUSUARIO;

    public static ArrayList<Usuario> listaUsuariosHost = new ArrayList<Usuario>();
    public static ArrayList<Usuario> listaUsuariosSQLite = new ArrayList<Usuario>();
    public static ArrayList<String> listaUsuariosSQLiteString = new ArrayList<String>();

    public Usuario() {
    }

    public Usuario(String IDUSUARIO, String DNI, String CLAVE, String IDCULTIVO, String LEE_PDA, String TIPO_USUARIO) {
        this.IDUSUARIO = IDUSUARIO;
        this.DNI = DNI;
        this.CLAVE = CLAVE;
        this.IDCULTIVO = IDCULTIVO;
        this.LEE_PDA = LEE_PDA;
        this.TIPO_USUARIO = TIPO_USUARIO;
    }

    public Usuario(String IDUSUARIO, String CULTIVO, String DNI, String USUARIO, String TAREO, String ESTADOUSUARIO, String LEE_PDA, String TIPO_USUARIO) {
        this.IDUSUARIO = IDUSUARIO;
        this.DNI = DNI;
        this.LEE_PDA = LEE_PDA;
        this.TIPO_USUARIO = TIPO_USUARIO;
        this.CULTIVO = CULTIVO;
        this.USUARIO = USUARIO;
        this.TAREO = TAREO;
        this.ESTADOUSUARIO = ESTADOUSUARIO;
    }

    public String getCULTIVO() {
        return CULTIVO;
    }

    public void setCULTIVO(String CULTIVO) {
        this.CULTIVO = CULTIVO;
    }

    public String getUSUARIO() {
        return USUARIO;
    }

    public void setUSUARIO(String USUARIO) {
        this.USUARIO = USUARIO;
    }

    public String getTAREO() {
        return TAREO;
    }

    public void setTAREO(String TAREO) {
        this.TAREO = TAREO;
    }

    public String getESTADOUSUARIO() {
        return ESTADOUSUARIO;
    }

    public void setESTADOUSUARIO(String ESTADOUSUARIO) {
        this.ESTADOUSUARIO = ESTADOUSUARIO;
    }

    public String getTIPO_USUARIO() {
        return TIPO_USUARIO;
    }

    public void setTIPO_USUARIO(String TIPO_USUARIO) {
        this.TIPO_USUARIO = TIPO_USUARIO;
    }

    public String getIDUSUARIO() {
        return IDUSUARIO;
    }

    public void setIDUSUARIO(String IDUSUARIO) {
        this.IDUSUARIO = IDUSUARIO;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public String getCLAVE() {
        return CLAVE;
    }

    public void setCLAVE(String CLAVE) {
        this.CLAVE = CLAVE;
    }

    public String getIDCULTIVO() {
        return IDCULTIVO;
    }

    public void setIDCULTIVO(String IDCULTIVO) {
        this.IDCULTIVO = IDCULTIVO;
    }

    public String getLEE_PDA() {
        return LEE_PDA;
    }

    public void setLEE_PDA(String LEE_PDA) {
        this.LEE_PDA = LEE_PDA;
    }

    public long agregarUsuarioSQLite(String idusuario, String dni, String clave, String idcultivo, String LEE_PDA, String TIPO_USUARIO){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("IDUSUARIO", idusuario);
        values.put("DNI", dni);
        values.put("CLAVE", clave);
        values.put("IDCULTIVO", idcultivo);
        values.put("LEE_PDA", LEE_PDA);
        values.put("TIPO_USUARIO", TIPO_USUARIO);

        long resultado = db.insert("USUARIO", null, values);
        //db.close();
        return resultado;
    }

    public void agregar_UsuarioSQLite(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for(Usuario u : Usuario.listaUsuariosHost){
                values.put("IDUSUARIO", u.getIDUSUARIO());
                values.put("DNI", u.getDNI());
                values.put("CLAVE", u.getCLAVE());
                values.put("IDCULTIVO", u.getIDCULTIVO());
                values.put("LEE_PDA", u.getLEE_PDA());
                values.put("TIPO_USUARIO", u.getTIPO_USUARIO());
                db.insert("USUARIO", null, values);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public void cargarListaUsuarios(){ //BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select * from USUARIO WHERE  DNI='00000000'";
        Cursor resultado = bd.rawQuery(sql, null);
        listaUsuariosSQLite.clear();
        while (resultado.moveToNext()){
            Usuario objU = new Usuario();
            objU.setIDUSUARIO(resultado.getString(0));
            objU.setDNI(resultado.getString(1));
            objU.setCLAVE(resultado.getString(2));
            objU.setIDCULTIVO(resultado.getString(3));
            listaUsuariosSQLite.add(objU);

            Log.i("USUARIOA", resultado.getString(0));
        }
        Log.i("USUARIOA", "SIN_RESULT");
        bd.close();
    }

    public boolean iniciarSesion(String dni_login){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select U.IDUSUARIO, U.DNI, U.CLAVE, U.IDCULTIVO, P.NOMBRES, U.LEE_PDA, U.TIPO_USUARIO FROM USUARIO U INNER JOIN PERSONALGENERAL P ON (U.DNI = P.DNI) WHERE U.DNI = '"+dni_login+"'";
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor.moveToNext()){
            MiAplicacionTareo.IDUSUARIO = cursor.getString(0);
            MiAplicacionTareo.DNIUSUARIO = cursor.getString(1);
            MiAplicacionTareo.CLAVE = cursor.getString(2);
            MiAplicacionTareo.IDCULTIVO = cursor.getString(3);
            MiAplicacionTareo.NOMBRES = cursor.getString(4);
            MiAplicacionTareo.LEE_PDA = cursor.getString(5);
            MiAplicacionTareo.TIPO_USUARIO = cursor.getString(6);
            return true;
        }
        return false;
    }

    public long limpiarTablaUsuarioSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("USUARIO", null, null);
        return 1;
    }
}
