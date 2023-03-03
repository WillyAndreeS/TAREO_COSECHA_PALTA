package com.acpagro.tareopalta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.acpagro.tareopalta.modelo.Actividad;
import com.acpagro.tareopalta.modelo.Consumidor;
import com.acpagro.tareopalta.modelo.HoraTareo;
import com.acpagro.tareopalta.modelo.Labor;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.SubLabor;
import com.acpagro.tareopalta.modelo.Tareo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PantallaTareos extends AppCompatActivity implements View.OnClickListener{

    private TextView edt_fecha, edt_codigo, edt_consumidor, edt_actividad, edt_labor, txt_titulo_labor, edt_sublabor, edt_valvula;
    private EditText edt_observacion, edt_horas;
    private Button btn_grabar, btn_salir;

    private RadioButton rbDiurno, rbNocturno;

    DateFormat dateFormat;

    //VARIABLES DIALOG CONSUMIDOR
    CharSequence[] listaConsumidoresDialog;
    int selected = 0;
    int buffKey = 0;
    String id_consumidor_seleccionado;

    //VARIABLES DIALOG ACTIVIDAD
    CharSequence[] listaActividadesDialog;
    int selectedA = 0;
    int buffKeyA = 0;
    String id_actividad_seleccionado = "X";

    //VARIABLES DIALOG LABOR
    CharSequence[] listaLaboresDialog;
    int selectedL = 0;
    int buffKeyL = 0;
    String id_labor_seleccionado;

    //VARIABLES DIALOG SUBLABOR
    CharSequence[] listaSubLaboresDialog;
    int selectedSL = 0;
    int buffKeySL = 0;
    String id_sublabor_seleccionado;

    //VARIABLES DIALOG VALVULA
    CharSequence[] listaValvulaDialog;
    int selectedV = 0;
    int buffKeyV = 0;
    String id_valvula_seleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_tareos);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        //Para que apareca la flecha hacia atrás
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Registro de Tareo");

        edt_codigo = (TextView)findViewById(R.id.edt_codigo);
        edt_horas = (EditText)findViewById(R.id.edt_horas);
        edt_fecha = (TextView)findViewById(R.id.edt_fecha);
        edt_consumidor = (TextView)findViewById(R.id.edt_consumidor);
        edt_actividad = (TextView)findViewById(R.id.edt_actividad);
        edt_labor = (TextView)findViewById(R.id.edt_labor);
        edt_sublabor = (TextView)findViewById(R.id.edt_sublabor);
//        edt_valvula = (TextView) findViewById(R.id.edt_valvula);
        txt_titulo_labor = (TextView)findViewById(R.id.txt_titulo_labor);
        this.rbDiurno = (RadioButton) findViewById(R.id.rbDiurno);
        this.rbNocturno = (RadioButton) findViewById(R.id.rbNocturno);
//        edt_observacion = (EditText) findViewById(R.id.edt_observacion);

        edt_fecha.setOnClickListener(this);
        edt_consumidor.setOnClickListener(this);
        edt_actividad.setOnClickListener(this);
        edt_labor.setOnClickListener(this);
        edt_sublabor.setOnClickListener(this);
//        edt_valvula.setOnClickListener(this);

        edt_codigo.setText(generarCodigoTareo());

        btn_grabar = (Button)findViewById(R.id.btn_grabar);
        btn_salir = (Button)findViewById(R.id.btn_salir);

        btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_grabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String consumidor = edt_consumidor.getText().toString();
                String actividad = edt_actividad.getText().toString();
                String labor = edt_labor.getText().toString();
                String sublabor = edt_sublabor.getText().toString();
                String turno = "";
                if (rbDiurno.isChecked()){
                    turno = "01";
                } else{
                    turno = "03";
                }

//                String observacion = edt_observacion.getText().toString();

                /*if(!id_actividad_seleccionado.equalsIgnoreCase("A07")){
                    if(consumidor.isEmpty() || actividad.isEmpty() || labor.isEmpty()){
                        Toast.makeText(PantallaTareos.this, "Faltan campos por seleccionar!", Toast.LENGTH_SHORT).show();
                    }else{
                        grabarTareo(id_consumidor_seleccionado, id_actividad_seleccionado, id_labor_seleccionado, "");
                    }
                }else{*/
                    if(consumidor.isEmpty() || actividad.isEmpty() || sublabor.isEmpty()){
                        Toast.makeText(PantallaTareos.this, "Faltan campos por seleccionar!", Toast.LENGTH_SHORT).show();
                    }else{
                        grabarTareo(id_consumidor_seleccionado, id_actividad_seleccionado, id_labor_seleccionado, id_sublabor_seleccionado, "", edt_horas.getText().toString(), turno);
                    }
//                }
            }
        });

        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        edt_fecha.setText(dateFormat.format(date).toString());

        Consumidor objC = new Consumidor();
        objC.cargarListaConsumidoresSQLite();

        Actividad objA = new Actividad();
        objA.cargarListaActividadesSQLite();

        HoraTareo objHT = new HoraTareo();
        double horaLimite;
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date datee = new Date();
        String dayOfWeek = simpledateformat.format(datee);

        String dia = MiAplicacionTareo.diaAbrevitura(dayOfWeek);
        Log.i("DIA", dia);
        horaLimite = objHT.getHoraTareoHoy(dia);
        edt_horas.setText(String.valueOf(horaLimite));
    }

    private String generarCodigoTareo() {
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Date date = new Date();
        return id+"_"+dateFormat.format(date);
    }

    public void grabarTareo(final String id_consu, final String id_act, final String id_lab, final String id_sublab, final String observacion, final String horas, final String idturno){
        final Tareo objA = new Tareo();
        Log.i("Observacion" , observacion);
        if(PantallaTareoAsistencia.IDGRUPO!= null){
            objA.agregarTareoSQLite_2(generarCodigoTareo(), edt_fecha.getText().toString(), MiAplicacionTareo.IDUSUARIO, id_consu, id_act, id_lab, id_sublab, "O", observacion.toUpperCase(), horas, idturno, PantallaTareoAsistencia.IDGRUPO);//OTRA
        }else{
            objA.agregarTareoSQLite(generarCodigoTareo(), edt_fecha.getText().toString(), MiAplicacionTareo.IDUSUARIO, id_consu, id_act, id_lab, id_sublab, "O", observacion.toUpperCase(), horas, idturno);//OTRA
        }

        //objA.agregarTareoSQLite(generarCodigoTareo(), MiApplicacionACP.IDUSUARIO, id_consu, id_act, id_lab);
        Toast.makeText(PantallaTareos.this, "Tareo agregado CORRECTAMENTE!!", Toast.LENGTH_SHORT).show();
        edt_codigo.setText(generarCodigoTareo());
        edt_consumidor.setText("");
        edt_actividad.setText("");
        edt_labor.setText("");
        edt_sublabor.setText("");
//        edt_valvula.setText("");
        finish();
    }

    public void dialogAdvertenciaGrabar(final String id_consu, final String id_act, final String id_lab){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PantallaTareos.this);
        builder.setTitle("¿Grabar?");
        builder.setMessage("Se grabará un nuevo Tareo con los datos seleccionados");
        builder.setIcon(R.drawable.ic_correcto);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
    //Para volver atras
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void dialogConsumidor(){
        listaConsumidoresDialog = Consumidor.listaConsumidorSQLiteString.toArray(new CharSequence[Consumidor.listaConsumidorSQLiteString.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elige Consumidor");
        builder.setIcon(R.drawable.ic_consumidor);
        builder.setItems(listaConsumidoresDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                id_consumidor_seleccionado = Consumidor.listaConsumidorSQLite.get(i).getIDCONSUMIDOR();
                edt_consumidor.setText(listaConsumidoresDialog[i]);
                selected = i;
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void dialogActividad(){
        listaActividadesDialog = Actividad.listaActividadSQLiteString.toArray(new CharSequence[Actividad.listaActividadSQLiteString.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elige Actividad");
        builder.setIcon(R.drawable.ic_actividad);
        builder.setItems(listaActividadesDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                id_actividad_seleccionado = Actividad.listaActividadSQLite.get(i).getIDACTIVIDAD();
                edt_actividad.setText(listaActividadesDialog[i]);
                selectedA = i;

                SubLabor objL = new SubLabor();
                objL.cargarListaSubLaboresSQLite(id_actividad_seleccionado);

                Log.i("IDACTI", "|"+id_actividad_seleccionado);
                edt_sublabor.setText("");
                /*if(id_actividad_seleccionado.equalsIgnoreCase("A07")){
                    edt_labor.setEnabled(false);
                    edt_labor.setVisibility(View.GONE);
                    txt_titulo_labor.setVisibility(View.GONE);
                }else{
                    edt_labor.setEnabled(true);
                    edt_labor.setVisibility(View.VISIBLE);
                    txt_titulo_labor.setVisibility(View.VISIBLE);
                }*/
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void dialogLabor(){
        listaLaboresDialog = Labor.listaLaborSQLiteString.toArray(new CharSequence[Labor.listaLaborSQLiteString.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elige Labor");
        builder.setIcon(R.drawable.ic_labor);
        builder.setItems(listaLaboresDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                id_labor_seleccionado = Labor.listaLaborSQLite.get(i).getIDLABOR();
                edt_labor.setText(listaLaboresDialog[i]);
                selectedL = i;

                SubLabor objSL = new SubLabor();
                objSL.cargarListaSubLaboresSQLite(id_actividad_seleccionado);
                Log.i("labor222:","|"+id_actividad_seleccionado+ "-" +id_labor_seleccionado);
                edt_sublabor.setText("");
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void dialogSubLabor(){
        listaSubLaboresDialog = SubLabor.listaSubLaborSQLiteString.toArray(new CharSequence[SubLabor.listaSubLaborSQLiteString.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("Elige SubLabor");
        builder.setTitle("Elige SubLabor");
        builder.setIcon(R.drawable.ic_sublabor);
        builder.setItems(listaSubLaboresDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                id_sublabor_seleccionado = SubLabor.listaSubLaborSQLite.get(i).getIDSUBLABOR();
                id_labor_seleccionado = SubLabor.listaSubLaborSQLite.get(i).getIDLABOR();

                edt_sublabor.setText(listaSubLaboresDialog[i]);
                selectedSL = i;

                Log.i("SUB_", "|"+id_sublabor_seleccionado+"|"+id_labor_seleccionado);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edt_fecha:
                dialogPickerFecha();

                break;
            case R.id.edt_consumidor:
                if(Consumidor.listaConsumidorSQLite.isEmpty()){
                    Toast.makeText(this, "DEBES SINCRONIZAR LA DATA PRIMERO!!", Toast.LENGTH_LONG).show();
                }else{
                    dialogConsumidor();
                }
                break;
            case R.id.edt_actividad:
                if(Actividad.listaActividadSQLite.isEmpty()){
                    Toast.makeText(this, "DEBES SINCRONIZAR LA DATA PRIMERO!!", Toast.LENGTH_LONG).show();
                }else{
                    dialogActividad();
                }
                break;
            /*case R.id.edt_labor:
                if(edt_actividad.getText().toString().isEmpty()){
                    Toast.makeText(this, "DEBES SELECCIONAR UNA ACTIVIDAD PRIMERO!!", Toast.LENGTH_LONG).show();
                }else{
                    dialogLabor();
                }
                break;*/
            case R.id.edt_sublabor:
                if(edt_actividad.getText().toString().isEmpty()){
                    Toast.makeText(this, "DEBES SELECCIONAR UNA ACTIVIDAD PRIMERO!!", Toast.LENGTH_LONG).show();
                }else{
                    //dialogLabor();
                    dialogSubLabor();
                }
                break;

        }
    }


    public void dialogPickerFecha(){
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(PantallaTareos.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        //SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                        //SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(year, monthOfYear, dayOfMonth-1);
                        //String dayOfWeek = simpledateformat.format(date);
                        edt_fecha.setText(year + "-" + diaOMes(monthOfYear+1) + "-" + diaOMes(dayOfMonth) + " 00:00:00");
                        //edt_fecha.setText(dayOfWeek);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public String diaOMes(int doM){
        String rpta = "";
        switch (doM){
            case 0:
                rpta = "00";
                break;
            case 1:
                rpta = "01";
                break;
            case 2:
                rpta = "02";
                break;
            case 3:
                rpta = "03";
                break;
            case 4:
                rpta = "04";
                break;
            case 5:
                rpta = "05";
                break;
            case 6:
                rpta = "06";
                break;
            case 7:
                rpta = "07";
                break;
            case 8:
                rpta = "08";
                break;
            case 9:
                rpta = "09";
                break;
            default:
                rpta = String.valueOf(doM);
        }

        return rpta;
    }
}
