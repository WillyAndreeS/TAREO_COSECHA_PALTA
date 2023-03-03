package com.acpagro.tareopalta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acpagro.tareopalta.adapters.AdapterListaHistorialRendimientos;
import com.acpagro.tareopalta.adapters.AdapterListaRendimientoGrupo;
import com.acpagro.tareopalta.adapters.AdapterListaRendimientoResumen;
import com.acpagro.tareopalta.modelo.DetalleTareo;

public class PantallaRendimientos extends AppCompatActivity {

    public static TextView txt_consumidor, txt_actividad_labor, fecha_hora, edtNombre, txtDni;
    public static LinearLayout ly_dni;
    public static TextView txtTotal;
    public static EditText edt_dni;
    public static ImageButton ibtnLector, ibtnlimpiar;
    public static Button btn_consultar, btn_nuevo_rend;
    public static RelativeLayout rl_rendimientos;
    public static ListView lv_historial;
    public static String cadena_dialog;

    public static String texto = "";
    public static String IDTAREO = "", CONSUMIDOR, ACTIVIDAD, LABOR, FECHAHORA, GRUPO="", IDSUBLABOR="", SUBLABOR="", IDCONSUMIDOR = "";
    public static boolean touch_boton = false;

    //AdapterListaHistorialRendimientos adapter;
    public static AdapterListaRendimientoGrupo adapter;
    private final static int SELECCIONAR_TRABAJADOR = 1;
    public final static int ACTUALIZAR_RENDIMIENTOS = 2;

    public static AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_rendimientos);

        //Para que apareca la flecha hacia atrás
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Rendimientos");

        Bundle parametros = this.getIntent().getExtras();

        IDTAREO = parametros.getString("p_id_tareo");//IDTAREO
        CONSUMIDOR = parametros.getString("p_consumidor");//IDASISTENCIA
        IDCONSUMIDOR = parametros.getString("p_idconsumidor");//IDASISTENCIA
        ACTIVIDAD = parametros.getString("p_actividad");//ACTIVIDAD
        LABOR = parametros.getString("p_labor");//LABOR
        FECHAHORA = parametros.getString("p_fechaYhora");//FECHAHORA
        IDSUBLABOR = parametros.getString("p_id_sublabor");
        SUBLABOR = parametros.getString("p_sublabor");
        GRUPO = parametros.getString("p_grupo");

        lv_historial = (ListView)findViewById(R.id.lv_historial);
        ly_dni = (LinearLayout) findViewById(R.id.ly_dni);

        DetalleTareo.listaDetalleTareoSQLite.clear();
        adapter = new AdapterListaRendimientoGrupo(PantallaRendimientos.this);
        lv_historial.setAdapter(adapter);//Se llena el ListView

        txt_consumidor = (TextView)findViewById(R.id.txt_consumidor);
        txt_actividad_labor = (TextView)findViewById(R.id.txt_actividad_labor);
        fecha_hora = (TextView)findViewById(R.id.fecha_hora);

        rl_rendimientos = (RelativeLayout)findViewById(R.id.rl_rendimientos);

        edt_dni = (EditText)findViewById(R.id.edt_dni);
        edtNombre = (TextView)findViewById(R.id.edtNombre);
        txtDni = (TextView)findViewById(R.id.txtDni);
        txtTotal = (TextView)findViewById(R.id.txtTotal);

        ibtnLector = (ImageButton)findViewById(R.id.ibtnLector);

        String individual_grupal="";
        if(GRUPO.equalsIgnoreCase("SI")){
            individual_grupal="[GRUPAL]";
            ly_dni.setVisibility(View.GONE);
        }else{
            individual_grupal="[INDIVIDUAL]";
            ly_dni.setVisibility(View.VISIBLE);
        }

        txt_consumidor.setText(CONSUMIDOR);
        txt_actividad_labor.setText(ACTIVIDAD + " - " + LABOR+ " - " +SUBLABOR+individual_grupal);
        fecha_hora.setText(FECHAHORA);

        if(GRUPO.equals("SI")){
            obtenerDatosYListaDetalleTareo(IDTAREO, IDSUBLABOR, txtDni.getText().toString() );
            edt_dni.setEnabled(false);
            ibtnLector.setEnabled(false);
        }else{
            edt_dni.setEnabled(true);
            ibtnLector.setEnabled(true);
        }

        btn_consultar = (Button)findViewById(R.id.btn_consultar);
        btn_nuevo_rend = (Button)findViewById(R.id.btn_nuevo_rend);
        btn_nuevo_rend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GRUPO.equals("NO") && ( txtDni.getText().toString() == null || txtDni.getText().toString().equals("") || txtDni.getText().toString().equals(" ") ) ){
                    Toast.makeText(PantallaRendimientos.this, "Escribe o lee antes un DNI de trabajador!", Toast.LENGTH_SHORT).show();
                }else{
                    /*alertDialog = dialogAgregarRendimiento(PantallaRendimientos.this);
                    alertDialog.show();*/
                }
            }
        });

        adapter.notifyDataSetChanged();

        edt_dni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(String.valueOf(charSequence).length() == 8){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_dni.getWindowToken(), 0);

                    new TareaVerificarSiEstaEnAsistencia().execute(IDTAREO, edt_dni.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ibtnLector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle parametros = new Bundle();
                parametros.putString("p_id_tareo", IDTAREO);
                Intent pantallaLista = new Intent(PantallaRendimientos.this, PantallaListaTrabajadoresAsisSelect.class);
                pantallaLista.putExtras(parametros);

                startActivityForResult(pantallaLista, SELECCIONAR_TRABAJADOR);
            }
        });

        new TareaCargarListaResumenRendimientoPorTareo().execute(IDTAREO, IDSUBLABOR, GRUPO);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rendimientos, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SELECCIONAR_TRABAJADOR){
            if(resultCode == RESULT_OK){
                edt_dni.setText(data.getStringExtra("dni"));
            }
        }else if(requestCode == ACTUALIZAR_RENDIMIENTOS){
            if(resultCode == RESULT_OK){
                new TareaCargarListaResumenRendimientoPorTareo().execute(IDTAREO, IDSUBLABOR, GRUPO);
            }
        }
    }

    private void reproducirPitido(){
        MediaPlayer mp = MediaPlayer.create(this, R.raw.pitido_scanner);
        mp.start();
    }

    public static void cambiarColorBackgroundError(){
        rl_rendimientos.setBackgroundColor(Color.parseColor("#ff0000"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_rendimientos.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }, 700);
    }

    public static void cambiarColorBackgroundExito(){
        rl_rendimientos.setBackgroundColor(Color.parseColor("#6dff4d"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_rendimientos.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }, 700);
    }

    //Para volver atras
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                finish();
                return true;
            case R.id.action_ver_resumen:
                Bundle parametros = new Bundle();
                parametros.putString("p_id_tareo", IDTAREO);
                parametros.putString("p_id_sublabor", IDSUBLABOR);
                parametros.putString("p_grupo", GRUPO);
                Intent pantallaRes = new Intent(PantallaRendimientos.this, PantallaListaResumenRendimiento.class);
                pantallaRes.putExtras(parametros);
                startActivity(pantallaRes);
                return true;
            case R.id.action_ver_asistencia:
                Bundle param = new Bundle();
                Intent pAP = new Intent(PantallaRendimientos.this, PantallaAsistenciaPersonal.class);
                param.putString("p_id_tareo", IDTAREO);
                param.putString("p_consumidor", CONSUMIDOR);
                param.putString("p_actividad", ACTIVIDAD);
                param.putString("p_labor", LABOR);
                param.putString("p_fechaYhora", FECHAHORA);
                pAP.putExtras(param);
                startActivity(pAP);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void guardarNuevoRendimientoSQLite(String IDTAREO, String DNI, Double RENDIMIENTO, String IDSUBLABOR, String IDVALVULA, String IDCONSUMIDOR){
        DetalleTareo objDA = new DetalleTareo();

        String individual_grupal="";
        if(GRUPO.equalsIgnoreCase("SI")){
            //individual_grupal=IDSUBLABOR;
            individual_grupal="";
        }else{
            individual_grupal=DNI;
        }
        Long r = objDA.agregarDetalleTareoSQLite(IDTAREO, individual_grupal, RENDIMIENTO, IDSUBLABOR, IDVALVULA, IDCONSUMIDOR);
        Log.i("GUARDARREND", IDTAREO+"|"+individual_grupal+"|"+RENDIMIENTO+"|"+IDSUBLABOR);
        //Long r = objDA.agregarDetalleTareoSQLite(IDTAREO, DNI, RENDIMIENTO, IDSUBLABOR);

        Log.i("RETURNASYNTASK", ""+ r);
        if(r == 500){
            cambiarColorBackgroundError();
        }else{
            cambiarColorBackgroundExito();
            //obtenerDatosYListaDetalleTareo(IDTAREO, IDSUBLABOR, DNI);
            new TareaCargarListaResumenRendimientoPorTareo().execute(IDTAREO, IDSUBLABOR, GRUPO);
        }
    }


    private class TareaVerificarSiEstaEnAsistencia extends AsyncTask<String, Void, Boolean> {
        //ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String idTareo = params[0];
            String dni = params[1];
            DetalleTareo dAsis = new DetalleTareo();
            return dAsis.verificarSiEstaEnAsistencia(idTareo, dni);
        }

        protected void onPostExecute(Boolean result){
            if(result){//Esta en asistencia
                obtenerDatosYListaDetalleTareo(IDTAREO, IDSUBLABOR, edt_dni.getText().toString());
            }else{//No esta en asistencia
                Toast.makeText(PantallaRendimientos.this, "Dni ingresado no está registrado en la asistencia!", Toast.LENGTH_LONG).show();
                edt_dni.setText("");
            }
        }
    }

    public static void obtenerDatosYListaDetalleTareo(String idtareo, String idsublabor, String dni){
        DetalleTareo dAsis = new DetalleTareo();
        dAsis.obtenerDatosUsuarioYListaDetalleTareo(idtareo, idsublabor, dni);

        //txtTotal.setText(""+DetalleTareo.sumatoria);
        txtTotal.setText(""+Math.round(DetalleTareo.sumatoria*100.00)/100.00);
        if( edt_dni.getText().toString().trim() == null || edt_dni.getText().toString().trim().equals("") || edt_dni.getText().toString().trim().equals(" ") ){
            //Log.i("Nulo", edt_dni.getText().toString());

        }else{
            //Log.i("No Nulo", edt_dni.getText().toString());
            edtNombre.setText(edt_dni.getText().toString().trim() + " : " + DetalleTareo.nombre);
            txtDni.setText(edt_dni.getText().toString().trim());
            edt_dni.setText("");
            edt_dni.requestFocus();
        }
        adapter.notifyDataSetChanged();
    }

    public static class TareaCargarListaResumenRendimientoPorTareo extends AsyncTask<String,  Void, Void> {
        //ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String idtareo = params[0];
            String idsublabor = params[1];
            String grupo = params[2];
            DetalleTareo objDA = new DetalleTareo();
            objDA.obtenerListaResumida(idtareo, idsublabor, grupo);
            return null;
        }

        protected void onPostExecute(Void result) {
            if(DetalleTareo.listaDetalleTareoResumenSQLite.size() > 0){
                txtTotal.setText(""+DetalleTareo.total);
                //llenarListView();
                adapter.notifyDataSetChanged();
            }else{

            }

        }
    }

    private void llenarListView(){
        AdapterListaRendimientoGrupo adapter = new AdapterListaRendimientoGrupo(PantallaRendimientos.this);
        lv_historial.setAdapter(adapter);
    }

    /*public AlertDialog dialogAgregarRendimiento(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(PantallaRendimientos.this);
        LayoutInflater inflater = PantallaRendimientos.this.getLayoutInflater();

        texto = "";

        View v = inflater.inflate(R.layout.item_dialog_teclado, null);
        final TextView txt_dialog_cant = (TextView)v.findViewById(R.id.txt_dialog_cant);
        Button btn_aceptar = (Button)v.findViewById(R.id.btn_aceptar);
        Button btn_cancelar = (Button)v.findViewById(R.id.btn_cancelar);
        Numpad numpad = (Numpad) v.findViewById(R.id.num);
        numpad.setOnTextChangeListner((String text, int digits_remaining)->{

            texto = text.trim();
            txt_dialog_cant.setText(text.trim());
        });

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if(txt_dialog_cant.getText().toString().trim() == null || txt_dialog_cant.getText().toString().trim().equals("") || txt_dialog_cant.getText().toString().trim().equals(" ")){

                }else{
                    guardarNuevoRendimientoSQLite(IDTAREO, txtDni.getText().toString(), Integer.parseInt(txt_dialog_cant.getText().toString()), IDSUBLABOR);
                }
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        builder.setTitle("Insertar Nuevo Rend.");

        builder.setView(v);
        return  builder.create();
    }*/

    public static AlertDialog dialogAgregarRendimiento(Activity context, String DNI){
        touch_boton = true;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();

        texto = "";

        View v = inflater.inflate(R.layout.item_dialog_teclado, null);
        final TextView txt_dialog_cant = (TextView)v.findViewById(R.id.txt_dialog_cant);
        TextView one = (TextView)v.findViewById(R.id.one);
        TextView two = (TextView)v.findViewById(R.id.two);
        TextView three = (TextView)v.findViewById(R.id.three);
        TextView four = (TextView)v.findViewById(R.id.four);
        TextView five = (TextView)v.findViewById(R.id.five);
        TextView six = (TextView)v.findViewById(R.id.six);
        TextView seven = (TextView)v.findViewById(R.id.seven);
        TextView eight = (TextView)v.findViewById(R.id.eight);
        TextView nine = (TextView)v.findViewById(R.id.nine);
        TextView zero = (TextView)v.findViewById(R.id.zero);
        TextView point = (TextView)v.findViewById(R.id.point);
        FrameLayout delete_layout = (FrameLayout)v.findViewById(R.id.delete_layout);

        Button btn_aceptar = (Button)v.findViewById(R.id.btn_aceptar);
        Button btn_cancelar = (Button)v.findViewById(R.id.btn_cancelar);
        /*Numpad numpad = (Numpad) v.findViewById(R.id.num);
        numpad.setOnTextChangeListner((String text, int digits_remaining) -> {
            texto = text.trim();
            txt_dialog_cant.setText(text.trim());
        });*/

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadena_dialog = txt_dialog_cant.getText().toString();
                cadena_dialog = cadena_dialog + "1";
                txt_dialog_cant.setText(cadena_dialog);
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadena_dialog = txt_dialog_cant.getText().toString();
                cadena_dialog = cadena_dialog + "2";
                txt_dialog_cant.setText(cadena_dialog);
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadena_dialog = txt_dialog_cant.getText().toString();
                cadena_dialog = cadena_dialog + "3";
                txt_dialog_cant.setText(cadena_dialog);
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadena_dialog = txt_dialog_cant.getText().toString();
                cadena_dialog = cadena_dialog + "4";
                txt_dialog_cant.setText(cadena_dialog);
            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadena_dialog = txt_dialog_cant.getText().toString();
                cadena_dialog = cadena_dialog + "5";
                txt_dialog_cant.setText(cadena_dialog);
            }
        });
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadena_dialog = txt_dialog_cant.getText().toString();
                cadena_dialog = cadena_dialog + "6";
                txt_dialog_cant.setText(cadena_dialog);
            }
        });
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadena_dialog = txt_dialog_cant.getText().toString();
                cadena_dialog = cadena_dialog + "7";
                txt_dialog_cant.setText(cadena_dialog);
            }
        });
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadena_dialog = txt_dialog_cant.getText().toString();
                cadena_dialog = cadena_dialog + "8";
                txt_dialog_cant.setText(cadena_dialog);
            }
        });
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadena_dialog = txt_dialog_cant.getText().toString();
                cadena_dialog = cadena_dialog + "9";
                txt_dialog_cant.setText(cadena_dialog);
            }
        });
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadena_dialog = txt_dialog_cant.getText().toString();
                cadena_dialog = cadena_dialog + "0";
                txt_dialog_cant.setText(cadena_dialog);
            }
        });
        point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadena_dialog = txt_dialog_cant.getText().toString();
                int posicion = cadena_dialog.indexOf(".");//Si es -1 no encontro el punto
                if(posicion == -1){//No esta el punto
                    cadena_dialog = cadena_dialog + ".";
                    txt_dialog_cant.setText(cadena_dialog);
                }
            }
        });
        delete_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cadena_dialog.length() != 0){
                    cadena_dialog = txt_dialog_cant.getText().toString();
                    cadena_dialog = cadena_dialog.substring(0, cadena_dialog.length()-1);
                    txt_dialog_cant.setText(cadena_dialog);
                }
            }
        });

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                touch_boton = false;
                alertDialog.dismiss();
                if(txt_dialog_cant.getText().toString().trim() == null || txt_dialog_cant.getText().toString().trim().equals("") || txt_dialog_cant.getText().toString().trim().equals(" ")){
                    //Log.i("NULO", "NULO");
                }else{
                    guardarNuevoRendimientoSQLite(IDTAREO, DNI, Double.parseDouble(txt_dialog_cant.getText().toString()), IDSUBLABOR, "001", IDCONSUMIDOR);
                }
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                touch_boton = false;
                alertDialog.dismiss();
            }
        });
        builder.setTitle("Insertar Nuevo Rend.");
        builder.setView(v);
        return  builder.create();
    }



    @Override
    public void onBackPressed() {
        finish();
    }
}
