package com.acpagro.tareopalta.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.acpagro.tareopalta.datos.SQLite;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Reconocimiento extends SQLite {
    private String IDCODIGOGENERAL;
    private String DISTANCE;
    private String EXTRA_FACE;
    private String ID_OPTIONAL;
    private String TITLE_OPTIONAL;
    public static List<String> listaReconocimientoUpload = new ArrayList<>();
    public static HashMap<String, SimilarityClassifier.Recognition> reconocimientosSQLite = new HashMap<>();
    public static ArrayList<Reconocimiento> listaReconocimientosServidor = new ArrayList<>();

    public Reconocimiento(String IDCODIGOGENERAL, String DISTANCE, String EXTRA_FACE, String ID_OPTIONAL, String TITLE_OPTIONAL) {
        this.IDCODIGOGENERAL = IDCODIGOGENERAL;
        this.DISTANCE = DISTANCE;
        this.EXTRA_FACE = EXTRA_FACE;
        this.ID_OPTIONAL = ID_OPTIONAL;
        this.TITLE_OPTIONAL = TITLE_OPTIONAL;
    }

    public Reconocimiento() {
    }

    public void agregarReconocimientosMasivoServidor(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for(Reconocimiento r : Reconocimiento.listaReconocimientosServidor){
                values.put("IDCODIGOGENERAL", r.getIDCODIGOGENERAL());
                values.put("DISTANCE", r.getDISTANCE());
                values.put("EXTRA_FACE", r.getEXTRA_FACE());
                values.put("ID_OPTIONAL", r.getID_OPTIONAL());
                values.put("TITLE_OPTIONAL", r.getTITLE_OPTIONAL());
                values.put("FECHAREGISTRO", dateFormat.format(date));
                values.put("SINCRONIZADO", "1");
                db.insert("RECOGNITION", null, values);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public long agregarReconocimiento(String IDCODIGOGENERAL, String DISTANCE, String EXTRA_FACE, String ID_OPTIONAL, String TITLE_OPTIONAL){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        //SQLiteDatabase bd = this.getReadableDatabase();
        SQLiteDatabase bd = this.getWritableDatabase();
        String sql = "SELECT COUNT(*) FROM RECOGNITION WHERE IDCODIGOGENERAL = '"+IDCODIGOGENERAL+"'";
        Cursor cursor = bd.rawQuery(sql, null);
        int conteo = 0;
        while (cursor.moveToNext()){//ITERAR ELEMENTO POR ELEMENTO, Y CARGARLO EN EL ARRAY DE RECONOCIMIENTOS
            conteo = cursor.getInt(0);
        }

        long resultado;

        if(conteo > 0){
            ContentValues cv = new ContentValues();
            cv.put("EXTRA_FACE",EXTRA_FACE);
            cv.put("ID_OPTIONAL",ID_OPTIONAL);
            cv.put("TITLE_OPTIONAL",TITLE_OPTIONAL);
            cv.put("FECHAREGISTRO",dateFormat.format(date).toString());
            cv.put("SINCRONIZADO","0");
            resultado = bd.update("RECOGNITION", cv, "IDCODIGOGENERAL = '"+IDCODIGOGENERAL+"'", null);
            //bd.close();
        }else{

            //SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("IDCODIGOGENERAL", IDCODIGOGENERAL);
            values.put("DISTANCE", DISTANCE);
            values.put("EXTRA_FACE", EXTRA_FACE);
            values.put("ID_OPTIONAL", ID_OPTIONAL);
            values.put("TITLE_OPTIONAL", TITLE_OPTIONAL);
            values.put("FECHAREGISTRO", dateFormat.format(date).toString());
            resultado = bd.insert("RECOGNITION", null, values);
            //db.close();
        }
        bd.close();
        return resultado;
    }

    public void cargarReconocimientosSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT IDCODIGOGENERAL, DISTANCE, EXTRA_FACE, ID_OPTIONAL, TITLE_OPTIONAL, FECHAREGISTRO FROM RECOGNITION order by 1 ";
        Cursor resultado = bd.rawQuery(sql, null);
        reconocimientosSQLite.clear();
        while (resultado.moveToNext()){//ITERAR ELEMENTO POR ELEMENTO, Y CARGARLO EN EL ARRAY DE RECONOCIMIENTOS
            float[][] output=new float[1][192];//192 OUTPUT_SIZE
            String extra = resultado.getString(2).replace("[[", "").replace("]]", "");
            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(extra.split(",")));
            for (int counter = 0; counter < arrayList.size(); counter++) {
                output[0][counter]= Float.parseFloat(arrayList.get(counter));
            }

            SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition(resultado.getString(3), resultado.getString(4), resultado.getFloat(1));
            result.setExtra(output);
            reconocimientosSQLite.put(resultado.getString(0), result);
            /*String nuevo = new Gson().toJson(result);
            Log.i("NUEVO", ""+nuevo);*/
        }
        bd.close();
    }

    public void cargarListaReconocimientoSQLiteUpload(){// BD SQLITE
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "SELECT IDCODIGOGENERAL, DISTANCE, EXTRA_FACE, ID_OPTIONAL, TITLE_OPTIONAL, FECHAREGISTRO FROM RECOGNITION WHERE SINCRONIZADO = '0' order by 1 ";
        Cursor resultado = bd.rawQuery(sql, null);
        listaReconocimientoUpload.clear();
        while (resultado.moveToNext()){
            String items = "<Item IDCODIGOGENERAL=\"" + resultado.getString(0)
                    + "\" DISTANCE=\"" + resultado.getString(1)
                    + "\" EXTRA_FACE=\"" + resultado.getString(2)
                    + "\" ID_OPTIONAL=\"" + resultado.getString(3)
                    + "\" TITLE_OPTIONAL=\"" + resultado.getString(4)
                    + "\" FECHAREGISTRO=\"" + resultado.getString(5).replace("-","")
                    + "\" IDTELEFONO=\"" + "012LJLJ1NL109821"
                    + "\" IDCODIGOGENERAL_REGISTRA=\"" + "47870132"
                    + "\" EMPRESA=\"" + "ACP" + "\" />";
                    //+ "\" IDCODIGOGENERAL_REGISTRA=\"" + MiAplicacionTareo.DNIUSUARIO + "\" />";
            listaReconocimientoUpload.add(items);
            //Log.i("TAREOANTERIOR", resultado.getString(0)+"//"+resultado.getString(1)+"//"+resultado.getString(2)+"//"+resultado.getString(3)+"//"+resultado.getString(4)+"//"+resultado.getString(5));
        }
        bd.close();
    }

    public long modificarEstadoASincronizadoReconocimiento(){
        ContentValues cv = new ContentValues();
        cv.put("SINCRONIZADO","1");
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.update("RECOGNITION", cv, "SINCRONIZADO = '0'", null);
        return 1;
    }

    public long limpiarTablaReconocimientosSQLite(){
        SQLiteDatabase bd = this.getReadableDatabase();
        bd.delete("RECOGNITION", null, null);
        return 1;
    }

    public String getIDCODIGOGENERAL() {
        return IDCODIGOGENERAL;
    }

    public void setIDCODIGOGENERAL(String IDCODIGOGENERAL) {
        this.IDCODIGOGENERAL = IDCODIGOGENERAL;
    }

    public String getDISTANCE() {
        return DISTANCE;
    }

    public void setDISTANCE(String DISTANCE) {
        this.DISTANCE = DISTANCE;
    }

    public String getEXTRA_FACE() {
        return EXTRA_FACE;
    }

    public void setEXTRA_FACE(String EXTRA_FACE) {
        this.EXTRA_FACE = EXTRA_FACE;
    }

    public String getID_OPTIONAL() {
        return ID_OPTIONAL;
    }

    public void setID_OPTIONAL(String ID_OPTIONAL) {
        this.ID_OPTIONAL = ID_OPTIONAL;
    }

    public String getTITLE_OPTIONAL() {
        return TITLE_OPTIONAL;
    }

    public void setTITLE_OPTIONAL(String TITLE_OPTIONAL) {
        this.TITLE_OPTIONAL = TITLE_OPTIONAL;
    }
}
