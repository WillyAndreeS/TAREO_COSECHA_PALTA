package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.acpagro.tareopalta.datos.SQLite;

import java.util.ArrayList;
import java.util.List;

public class Administrador  extends SQLite {
    private String numero;
    private String correo;
    private String nombre;

    public static List<Administrador> listaNumerosAdministradores = new ArrayList<Administrador>();

    public Administrador(String numero, String correo, String nombre) {
        this.numero = numero;
        this.correo = correo;
        this.nombre = nombre;
    }

    public Administrador() {
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public long agregarAdministradorSQLite(String NUMERO, String CORREO, String NOMBRE){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NUMERO", NUMERO);
        values.put("CORREO", CORREO);
        values.put("NOMBRE", NOMBRE);
        long resultado = db.insert("ADMINISTRADOR", null, values);
        //db.close();
        return resultado;
    }

    public void agregar_AdministradorSQLite(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for(Administrador adm : Administrador.listaNumerosAdministradores){
                values.put("NUMERO", adm.getNumero());
                values.put("CORREO", adm.getCorreo());
                values.put("NOMBRE", adm.getNombre());
                db.insert("ADMINISTRADOR", null, values);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public void cargarListaNumerosAdministrador(){ //BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select * from ADMINISTRADOR order by 1";
        Cursor resultado = bd.rawQuery(sql, null);
        listaNumerosAdministradores.clear();
        while (resultado.moveToNext()){
            listaNumerosAdministradores.add(new Administrador(resultado.getString(0), resultado.getString(1), resultado.getString(2)));
        }
        bd.close();
    }

    public long limpiarTablaAdministradorSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("ADMINISTRADOR", null, null);
        return 1;
    }
}
