package com.acpagro.tareopalta;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.acpagro.tareopalta.adapters.AdapterListaUsuarios;
import com.acpagro.tareopalta.datos.Http;
import com.acpagro.tareopalta.datos.HttpPostValues;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.PersonalGeneral;
import com.acpagro.tareopalta.modelo.Usuario;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

import static com.acpagro.tareopalta.modelo.MiAplicacionTareo.validarNumero;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragAgregarUsuario extends Fragment {
    public static SwipeRefreshLayout swipeActualizarLista;
    private ListView lv_usuarios;
    private EditText edt_dni;
    private TextView txt_Dni_Nombre;
    private ImageButton ibtn_agregar;
    public static int ACTUALIZARLISTA = 0;
    private RadioButton rb_si;
    private RadioButton rb_no;
    RequestQueue request;
    AdapterListaUsuarios adapter;
    private String RESPUESTA_CULTIVO;

    public FragAgregarUsuario() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag_agregar_usuario, container, false);

        swipeActualizarLista = (SwipeRefreshLayout)v.findViewById(R.id.swipeActualizarLista);
        lv_usuarios = (ListView)v.findViewById(R.id.lv_usuarios);
        edt_dni = (EditText)v.findViewById(R.id.edt_dni);
        rb_si = (RadioButton)v.findViewById(R.id.rb_si);
        rb_no = (RadioButton)v.findViewById(R.id.rb_no);
        txt_Dni_Nombre = (TextView)v.findViewById(R.id.txt_Dni_Nombre);
        ibtn_agregar = (ImageButton)v.findViewById(R.id.ibtn_agregar);
        adapter = new AdapterListaUsuarios(getActivity());
        lv_usuarios.setAdapter(adapter);


        edt_dni.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(String.valueOf(charSequence).length() == 8){
                    if(validarNumero(edt_dni.getText().toString())){//Si es numero el dni
                        PersonalGeneral obj = new PersonalGeneral();
                        boolean rpta = obj.getPersonalgeneralPorDNI(edt_dni.getText().toString().trim());
                        if(rpta){
                            txt_Dni_Nombre.setText(PersonalGeneral.listaPersonalGeneralSQLite.get(0).getDNI() + " - " + PersonalGeneral.listaPersonalGeneralSQLite.get(0).getNOMBRES());
                            edt_dni.setText("");
                            edt_dni.requestFocus();
                        }else{
                            txt_Dni_Nombre.setText(edt_dni.getText().toString()+ "- PERSONAL NO ENCONTRADO");
                            edt_dni.setText("");
                            edt_dni.requestFocus();
                        }

                    }else{
                        edt_dni.setText("");
                        edt_dni.requestFocus();
                    }

                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_dni.getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        swipeActualizarLista.setRefreshing(true);
        refreshItems();

        swipeActualizarLista.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        //getListaUsuarios();
        ibtn_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_Dni_Nombre.getText().toString().length()==0){
                    Toast.makeText(getActivity(), "Ingresa un DNI para crear usuario.", Toast.LENGTH_SHORT).show();
                }else {
                    new TareaEnviarDataUsuarioHtml().execute();
                }
            }
        });

        return v;
    }

    public void refreshItems() {
        ACTUALIZARLISTA = 1;
        getListaUsuarios();
    }

    public static void onItemsLoadComplete() {
        ACTUALIZARLISTA = 0;
        swipeActualizarLista.setRefreshing(false);
    }

    private void getListaUsuarios(){//Para los telefonos
        request = Volley.newRequestQueue(getActivity());
        String url = MiAplicacionTareo.URL_SERVICE_REPORTES + "getUsuariosMovilPorCultivo";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                if(ACTUALIZARLISTA == 1){
                    onItemsLoadComplete();
                }
                Log.i("Response", response);
                try {
                    Usuario.listaUsuariosHost.clear();
                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray = json.getJSONArray("resultado");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Log.i("USUARIO", jsonData.getString("USUARIO"));

                        Usuario.listaUsuariosHost.add(new Usuario(
                                jsonData.getString("IDUSUARIO"),
                                jsonData.getString("CULTIVO"),
                                jsonData.getString("DNI"),
                                jsonData.getString("USUARIO"),
                                jsonData.getString("TAREO"),
                                jsonData.getString("ESTADOUSUARIO"),
                                jsonData.getString("LEE_PDA"),
                                jsonData.getString("TIPO_USUARIO")
                        ));
                    }

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(ACTUALIZARLISTA == 1){
                            onItemsLoadComplete();
                        }
                        Toast.makeText(getActivity(), "Ocurrió un error al conectar!, vuelve a intentarlo!! - 500", Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idCultivo", "0006");
                return params;
            }
        };
        request.add(postRequest);
    }

    private class TareaEnviarDataUsuarioHtml extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Agregando...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return enviarUsuario_Http();
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            Log.i("RESULT_ASYNTASK", result);

            if (result.equalsIgnoreCase("true")) {
                txt_Dni_Nombre.setText("");
                rb_no.setChecked(true);
                swipeActualizarLista.setRefreshing(true);
                refreshItems();
                dialogPersonalizado("ÉXITO", "Usuario agregado correctamente");

                swipeActualizarLista.setRefreshing(true);
                refreshItems();
            }else if(result.equalsIgnoreCase("FALSE")){
                if(!RESPUESTA_CULTIVO.equalsIgnoreCase("ERROR")){
                    txt_Dni_Nombre.setText("");
                    rb_no.setChecked(true);
                    dialogPersonalizado("ADVERTENCIA", "Personal ya cuenta con Usuario en el cultivo de "+RESPUESTA_CULTIVO);
                    //Toast.makeText(getActivity(), "Personal ya cuenta con Usuario en el cultivo de "+RESPUESTA_CULTIVO, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(), "Ocurrió un inconveniente al transferir la información, vuelve a intentarlo! - FALSE", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getActivity(), "Ocurrió un inconveniente al transferir la información, vuelve a intentarlo! - ERROR", Toast.LENGTH_LONG).show();
            }

        }
    }

    private String enviarUsuario_Http(){
        String LEE_PDA="";
        if(rb_si.isChecked()){
            LEE_PDA="SI";
        }else{
            LEE_PDA="NO";
        }

        String RPTA = "FALSE";
        //Log.i("ini_response", RPTA);
        Vector<HttpPostValues> parametros = new Vector<>();
        parametros.addElement(new HttpPostValues("dni", txt_Dni_Nombre.getText().toString().substring(0,8)));
        parametros.addElement(new HttpPostValues("idcultivo", "0006"));
        parametros.addElement(new HttpPostValues("genera_tareo", "NO"));
        parametros.addElement(new HttpPostValues("lee_pda", LEE_PDA));
        parametros.addElement(new HttpPostValues("tipo_usuario", "NORMAL"));

        try {
            String resultado = new Http().enviarPost(MiAplicacionTareo.URL_SERVICE_REPORTES + "setUsuarioMovil", parametros);
            Log.i("RESPONSE", resultado);
            JSONObject json = new JSONObject(resultado);
            RPTA = json.getString("resultado");
            RESPUESTA_CULTIVO = json.getString("cultivo");
            //Log.i("try", RPTA);
        }catch (Exception e){
            if(e != null){
                RPTA=""+e.getMessage();
            }else{
                RPTA="ERROR_DESCONOCIDO";
            }
            e.printStackTrace();
        }
        //Log.i("RESPONSE_FINAL", RPTA);
        return RPTA;
    }

    public void dialogPersonalizado(String titulo, String mensaje){
        PrettyDialog pDialog = new PrettyDialog(getActivity());
        pDialog.setCancelable(false);
        //pDialog.openOptionsMenu();
        pDialog
                .setIcon(
                        R.drawable.ic_correcto,     // icon resource
                        R.color.pdlg_color_green,      // icon tint
                        new PrettyDialogCallback() {   // icon OnClick listener
                            @Override
                            public void onClick() {
                                // Do what you gotta do
                            }
                        })
                //.setIconTint(R.color.pdlg_color_red)
                .setTitle(titulo)
                .setMessage(mensaje)

                .addButton(
                        "ACEPTAR",
                        R.color.pdlg_color_white,
                        R.color.colorPrimary,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                pDialog.dismiss();
                            }
                        }
                )
                .setAnimationEnabled(true)
                .show();
    }

}
