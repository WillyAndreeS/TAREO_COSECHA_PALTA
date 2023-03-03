package com.acpagro.tareopalta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.acpagro.tareopalta.adapters.AdapterListaHorasPersonal;
import com.acpagro.tareopalta.modelo.HoraPersonal;

public class PantallaListaHorasPersonal extends AppCompatActivity {

    ListView lv_horas_personal;
    String IDTAREO;

    AlertDialog alertDialog;
    AdapterListaHorasPersonal adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_lista_horas_personal);

        //Para que apareca la flecha hacia atrás
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Personal Horas");

        Bundle parametros = this.getIntent().getExtras();
        IDTAREO = parametros.getString("p_id_tareo");//IDTAREO

        lv_horas_personal = (ListView)findViewById(R.id.lv_horas_personal);

        HoraPersonal hp = new HoraPersonal();
        hp.listarHorasPersonal(IDTAREO);
        adapter = new AdapterListaHorasPersonal(this);
        lv_horas_personal.setAdapter(adapter);


        lv_horas_personal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                alertDialog = dialogAgregarCantidadHoras(IDTAREO, HoraPersonal.listaPersonalConHorasManualmenteIngresadas.get(i).getDNI(), HoraPersonal.listaPersonalConHorasManualmenteIngresadas.get(i).getHORAS());
                alertDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
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

    public AlertDialog dialogAgregarCantidadHoras(final String idtareo, final String dni, String horas){
        final AlertDialog.Builder builder = new AlertDialog.Builder(PantallaListaHorasPersonal.this);
        LayoutInflater inflater = getLayoutInflater();

        //texto = "";

        View v = inflater.inflate(R.layout.item_dialog_horas, null);
        final EditText txt_dialog_cant = (EditText) v.findViewById(R.id.txt_dialog_cant);

        txt_dialog_cant.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        txt_dialog_cant.setText(horas);
        txt_dialog_cant.setSelection(txt_dialog_cant.getText().length());
        builder.setTitle("Ingresa cantidad Horas");
        //builder.setMessage("Tareo seleccionado: " + contenido);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                if(txt_dialog_cant.getText().toString().trim() == null || txt_dialog_cant.getText().toString().trim().equals("") || txt_dialog_cant.getText().toString().trim().equals(" ")){
                    Log.i("NULO", "NULO");
                }else{
                    HoraPersonal obj = new HoraPersonal();
                    obj.agregarHoraPersonal(idtareo, dni, txt_dialog_cant.getText().toString());

                    obj.listarHorasPersonal(IDTAREO);
                    adapter.notifyDataSetChanged();
                    /*Tareo obj = new Tareo();
                    Log.i("EDT", txt_dialog_cant.getText().toString());
                    obj.modificarCantidadHorasTareoSQLite(txt_dialog_cant.getText().toString(), idtareo);
                    new TareaCargarListaTareosCompleta().execute();
                    new TareaActualizar().execute(txt_dialog_cant.getText().toString(), idtareo);*/
                }
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i){

            }
        });
        builder.setView(v);
        return  builder.create();
    }
}
