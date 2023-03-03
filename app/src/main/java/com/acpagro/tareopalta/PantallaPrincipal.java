package com.acpagro.tareopalta;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acpagro.tareopalta.datos.Http;
import com.acpagro.tareopalta.datos.HttpPostValues;
import com.acpagro.tareopalta.modelo.APKInstall;
import com.acpagro.tareopalta.modelo.Actividad;
import com.acpagro.tareopalta.modelo.Administrador;
import com.acpagro.tareopalta.modelo.Asistencia;
import com.acpagro.tareopalta.modelo.BinCabecera;
import com.acpagro.tareopalta.modelo.BinDetalle;
import com.acpagro.tareopalta.modelo.Consumidor;
import com.acpagro.tareopalta.modelo.DetalleTareo;
import com.acpagro.tareopalta.modelo.Grupo;
import com.acpagro.tareopalta.modelo.HoraPersonal;
import com.acpagro.tareopalta.modelo.HoraTareo;
import com.acpagro.tareopalta.modelo.Labor;
import com.acpagro.tareopalta.modelo.LecturaMaquinaria;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.PersonalGeneral;
import com.acpagro.tareopalta.modelo.Reconocimiento;
import com.acpagro.tareopalta.modelo.SalidaAsistencia;
import com.acpagro.tareopalta.modelo.SalidaPersonal;
import com.acpagro.tareopalta.modelo.ServiceTarea;
import com.acpagro.tareopalta.modelo.SubLabor;
import com.acpagro.tareopalta.modelo.Tareo;
import com.acpagro.tareopalta.modelo.TicketCosecha;
import com.acpagro.tareopalta.modelo.TicketPersona;
import com.acpagro.tareopalta.modelo.Usuario;
import com.acpagro.tareopalta.modelo.Valvula;
import com.acpagro.tareopalta.modelo.Variedad;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huxq17.download.Pump;
import com.huxq17.download.message.DownloadListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import io.github.kobakei.materialfabspeeddial.FabSpeedDial;
import io.github.kobakei.materialfabspeeddial.FabSpeedDialMenu;
import ir.mtajik.android.advancedPermissionsHandler.PermissionHandlerActivity;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class PantallaPrincipal extends PermissionHandlerActivity implements View.OnClickListener {

    LinearLayout menu_salida, menu_tareos, menu_asistencia, menu_rendimientos, menu_bines, menu_lectura_tickets, menu_asignacion_horas, menu_sincronizar, menu_transferir, menu_lectura_jabas, menu_asignacion_tickets, menu_registro_jabero, menu_registro_viaje, menu_asistentes_cosecha;
    TextView txt_nombreCompletoUsuarioLogueado, item_menu_id_telefono;

    RelativeLayout rl_informacion;
    int info = 0;
    Button btnAceptar;

    //ParaDialogSincronizar
    AlertDialog dialogSincronizando;
    Button btn_cancelar, btn_aceptar;
    ProgressBar progressBar1, progressBar2, progressBar3, progressBar4, progressBar5, progressBar6;
    ImageView img_estado_sinc_d1, img_estado_sinc_d2, img_estado_sinc_d3, img_estado_sinc_d4, img_estado_sinc_d5, img_estado_sinc_d6;
    FloatingActionButton fablimpiarTablasDeMovimiento;
    private FabSpeedDial fab;
    private ProgressDialog pDialog;

    //Request
    RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        //Para que apareca la flecha hacia atrás
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_salir_blanco);
        //actionBar.setIcon(R.drawable.ic_alarma);
        actionBar.setDisplayHomeAsUpEnabled(true);

        MiAplicacionTareo.cargar_preferenciasLogin(PantallaPrincipal.this);
        if(MiAplicacionTareo.TIPO_USUARIO.equalsIgnoreCase("MAQUINARIA")){
            setContentView(R.layout.activity_pantalla_maquinaria);
            menu_lectura_tickets = (LinearLayout)findViewById(R.id.menu_lectura_tickets);
            menu_lectura_tickets.setOnClickListener(this);
            actionBar.setTitle("MAQ. Palta ACP");
        }else{
            if (MiAplicacionTareo.LEE_PDA.equalsIgnoreCase("SI")){
                setContentView(R.layout.activity_pantalla_cosecha);
                menu_asignacion_tickets = (LinearLayout)findViewById(R.id.menu_asignacion_tickets);
                menu_lectura_tickets = (LinearLayout)findViewById(R.id.menu_lectura_tickets);
                menu_asistentes_cosecha = (LinearLayout)findViewById(R.id.menu_asistentes_cosecha);
                menu_bines = (LinearLayout)findViewById(R.id.menu_bines);
                menu_asignacion_tickets.setOnClickListener(this);
                menu_lectura_tickets.setOnClickListener(this);
                menu_asistentes_cosecha.setOnClickListener(this);
                menu_bines.setOnClickListener(this);
                actionBar.setTitle("Cosecha ACP");
            }else{
                setContentView(R.layout.activity_pantalla_principal);
                actionBar.setTitle("Tareo ACP");
            }
            menu_salida = (LinearLayout)findViewById(R.id.menu_salida);
            menu_salida.setOnClickListener(this);
        }



        rl_informacion = (RelativeLayout)findViewById(R.id.rl_informacion);
        btnAceptar = (Button)findViewById(R.id.btnAceptar);

        menu_tareos = (LinearLayout)findViewById(R.id.menu_tareos);
        menu_asistencia = (LinearLayout)findViewById(R.id.menu_asistencia);
        menu_asignacion_horas = (LinearLayout)findViewById(R.id.menu_asignacion_horas);
        menu_sincronizar = (LinearLayout)findViewById(R.id.menu_sincronizar);
        menu_transferir = (LinearLayout)findViewById(R.id.menu_transferir);
        menu_lectura_jabas = (LinearLayout)findViewById(R.id.menu_rendimientos);

        fablimpiarTablasDeMovimiento = (FloatingActionButton)findViewById(R.id.fablimpiarTablasDeMovimiento);
        fab = (FabSpeedDial)findViewById(R.id.fab);

        txt_nombreCompletoUsuarioLogueado = (TextView)findViewById(R.id.txt_nombreCompletoUsuarioLogueado);

        if(!MiAplicacionTareo.IDUSUARIO.equalsIgnoreCase("0")){
            fab.setVisibility(View.GONE);
        }

        item_menu_id_telefono = (TextView)findViewById(R.id.item_menu_id_telefono);


        menu_tareos.setOnClickListener(this);
        menu_asistencia.setOnClickListener(this);
        menu_asignacion_horas.setOnClickListener(this);
        menu_sincronizar.setOnClickListener(this);
        menu_transferir.setOnClickListener(this);
        menu_lectura_jabas.setOnClickListener(this);

        fablimpiarTablasDeMovimiento.setOnClickListener(this);

        txt_nombreCompletoUsuarioLogueado.setText(MiAplicacionTareo.NOMBRES);
        request = Volley.newRequestQueue(this);

        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        item_menu_id_telefono.setText(id);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation= AnimationUtils.loadAnimation(PantallaPrincipal.this, R.anim.slide_abajo_arriba);
                rl_informacion.startAnimation(animation);
                rl_informacion.setVisibility(View.GONE);
                info = 0;
            }
        });

        fab.setMenu(menuOpcionesAdministradorFAB());

        fab.addOnMenuItemClickListener(new FabSpeedDial.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionButton fab, TextView label, int itemId) {
                switch (itemId){
                    case 1:
                        //Log.i("OPCION", "1");
                        dialogDescargar(getResources().getString(R.string.enlace_descarga));
                        break;
                    case 2:
                        new TareaLimpiarTablasDeMovimiento().execute();
                        break;
                    case 3:
                        startActivity(new Intent(PantallaPrincipal.this, NuevosUsuariosExtra.class));
                        //generarBackupBDEnLocal();
                        break;
                    case 4:
                        TicketCosecha obj = new TicketCosecha();
                        obj.eliminarLecturasHoy();
                        break;
                        //generarBackupBDYSubirloServidor();
                }
            }
        });
        fab.addOnStateChangeListener(new FabSpeedDial.OnStateChangeListener() {
            @Override
            public void onStateChange(boolean open) {
                //Toast.makeText(getActivity(), "Open: " + open, Toast.LENGTH_SHORT).show();
            }
        });

        //Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS};
        boolean stickyMode = true;
        askForPermission(permissions , stickyMode, new PermissionHandlerActivity.PermissionCallBack() {
            @Override
            public void onPermissionsGranted() {
                Log.i("TAG", "onPermissionsGranted: ");
            }

            @Override
            public void onPermissionsDenied(String[] permissions) {
                Log.i("TAG", "onPermissionsDenied: ");
            }
        });
    }



    private FabSpeedDialMenu menuOpcionesAdministradorFAB(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);

        FabSpeedDialMenu menu = new FabSpeedDialMenu(this);
        menu.add("Descargar última versión").setIcon(R.drawable.ic_descargar_white);
        menu.add("Vaciar Tablas de Movimiento").setIcon(R.drawable.ic_action_eliminar);
        menu.add("Habilitar Usuarios").setIcon(R.drawable.ic_person_add_white);
        //menu.add("Exportar Base de Datos").setIcon(R.drawable.ic_action_import);
        //menu.add("Subir BD a Servidor").setIcon(R.drawable.ic_subir_data);
        if(fechaHoy.equalsIgnoreCase("2021-07-07")){
            menu.add("Eliminar BALDES "+dateFormat2.format(date)).setIcon(R.drawable.ic_person_add_white);
        }


        return menu;
    }

    //Para volver atras
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                dialogAdvertenciaSalir();
                return true;
            case R.id.action_ver_horas:
                startActivity(new Intent(PantallaPrincipal.this, PantallaListaExcesoHoras.class));
                return true;
            case R.id.action_salida:
                startActivity(new Intent(PantallaPrincipal.this, PantallaRegistrarSalida.class));
                return true;
            case R.id.action_info:
                if(info == 0){
                    Animation animation= AnimationUtils.loadAnimation(PantallaPrincipal.this, R.anim.slide_arriba_abajo);
                    rl_informacion.startAnimation(animation);
                    rl_informacion.setVisibility(View.VISIBLE);
                    info = 1;
                }else{
                    Animation animation= AnimationUtils.loadAnimation(PantallaPrincipal.this, R.anim.slide_abajo_arriba);
                    rl_informacion.startAnimation(animation);
                    rl_informacion.setVisibility(View.GONE);
                    info = 0;
                }
                return true;
            case R.id.action_subir:
                if(determinarConexion()){
                    dialogAdvertenciaSubirBDBackup();
                }else{
                    Toast.makeText(PantallaPrincipal.this, "No dispone de Conexión a Internet", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class TareaLimpiarTablasDeMovimiento extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(PantallaPrincipal.this);
            pDialog.setMessage("Autenticando..");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            Tareo objA = new Tareo();
            DetalleTareo objDA = new DetalleTareo();
            Asistencia objAsi = new Asistencia();
            SalidaPersonal objSP = new SalidaPersonal();
            HoraPersonal objHP = new HoraPersonal();
            Grupo objG = new Grupo();
            TicketCosecha ticketCosecha = new TicketCosecha();
            TicketPersona ticketPersona = new TicketPersona();
            BinCabecera bc = new BinCabecera();
            BinDetalle bd = new BinDetalle();
            LecturaMaquinaria lm = new LecturaMaquinaria();
            SalidaAsistencia sa = new SalidaAsistencia();
            Reconocimiento re = new Reconocimiento();

            objA.limpiarTablaTareoSQLite();
            objDA.limpiarTablaDetalleTareoSQLite();
            objAsi.limpiarTablaAsistenciaSQLite();
            objSP.limpiarTablaSalidaSQLite();
            objHP.limpiarTablaHoraPersonalSQLite();
            objG.limpiarTablaGrupoSQLite();
            ticketCosecha.limpiarTablaTicketCosechaSQLite();
            ticketPersona.limpiarTablaTicketPersonaSQLite();
            bc.limpiarTablaBinCabeceraSQLite();
            bd.limpiarTablaBinDetalleSQLite();
            lm.limpiarTablaLecturaMaquinariaSQLite();
            sa.limpiarTablaSalidaAsistenciaSQLite();
            re.limpiarTablaReconocimientosSQLite();

            return true;
        }

        protected void onPostExecute(Boolean result){
            pDialog.dismiss();
            if(result){
                Toast.makeText(PantallaPrincipal.this, "Se vaciaron tablas de Movimiento", Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(PantallaPrincipal.this, "Error", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void generarBackupBDEnLocal(){
        try {
            File nuevaCarpeta = new File(Environment.getExternalStorageDirectory(), "bk_tareo_palta");
            if (!nuevaCarpeta.exists()) {
                nuevaCarpeta.mkdir();
            }
            try {
                /*File file = new File(nuevaCarpeta, "micky_bdprueba" + ".txt");
                file.createNewFile();*/
                String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
                Date date = new Date();

                String BD_NOMBRE = "bd_tareo_acp_palta";//Nombre de bd oculto
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();
                FileChannel source=null;
                FileChannel destination=null;
                String currentDBPath = "/data/"+ "com.acpagro.tareo" +"/databases/"+BD_NOMBRE;
                String nombreBD = BD_NOMBRE + "_" + dateFormat.format(date) + "_" + id;
                String backupDBPath = nombreBD +".sqlite";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(nuevaCarpeta, backupDBPath);

                //File backupDB = new File(sd, backupDBPath);
                try {
                    source = new FileInputStream(currentDB).getChannel();
                    destination = new FileOutputStream(backupDB).getChannel();
                    destination.transferFrom(source, 0, source.size());
                    source.close();
                    destination.close();
                    Toast.makeText(this, "Copia de BD creada Correctamente!", Toast.LENGTH_LONG).show();
                    Log.i("DESTINO", String.valueOf(destination));
                } catch(IOException e) {
                    //e.printStackTrace();
                }

            } catch (Exception ex) {
                Log.e("Error", "ex: " + ex);
            }
        } catch (Exception e) {
            Log.e("Error", "e: " + e);
        }
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("No se pudo leer completamente el archivo!"+file.getName());
        }

        is.close();
        return bytes;
    }

    public void subirBackupBDAServidor(final String nombre, File bd) throws IOException {
        final ProgressDialog pDialog;
        pDialog = new ProgressDialog(PantallaPrincipal.this);
        pDialog.setMessage("Subiendo BD..");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();

        //File file = new File(fileName);
        byte[] bytes = loadFile(bd);
        byte[] encoded = Base64.encode(bytes, 1);
        final String encodedString = new String(encoded);

        String url = MiAplicacionTareo.URL_SERVICE_SUBIDA + "setSaveBackupBD";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                Log.i("Response", response);
                if(response.equalsIgnoreCase("exito")){
                    Toast.makeText(PantallaPrincipal.this, "2. Copia de BD Subida correctamente!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PantallaPrincipal.this, "2. Error en el servidor!", Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Toast.makeText(PantallaPrincipal.this, "Error al transferir la información!", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("nombre", nombre);
                params.put("bd", encodedString);
                params.put("idcultivo", MiAplicacionTareo.IDCULTIVO);
                return params;
            }
        };
        request.add(postRequest);
    }

    private void generarBackupBDYSubirloServidor(){
        try {
            File nuevaCarpeta = new File(Environment.getExternalStorageDirectory(), "bk_tareo_palta");
            if (!nuevaCarpeta.exists()) {
                nuevaCarpeta.mkdir();
            }
            try {
                /*File file = new File(nuevaCarpeta, "micky_bdprueba" + ".txt");
                file.createNewFile();*/
                String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
                Date date = new Date();

                String BD_NOMBRE = "bd_tareo_acp_palta";//Nombre de bd oculto
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();
                FileChannel source=null;
                FileChannel destination=null;
                String currentDBPath = "/data/"+ "com.acpagro.tareopalta" +"/databases/"+BD_NOMBRE;
                //String nombreBD = BD_NOMBRE + "_" + dateFormat.format(date) + "_" + id;
                String nombreBD = MiAplicacionTareo.IDCULTIVO + "_" + dateFormat.format(date) + "_" + id;
                String backupDBPath = nombreBD +".sqlite";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(nuevaCarpeta, backupDBPath);

                //File backupDB = new File(sd, backupDBPath);
                try {
                    source = new FileInputStream(currentDB).getChannel();
                    destination = new FileOutputStream(backupDB).getChannel();
                    destination.transferFrom(source, 0, source.size());
                    source.close();
                    destination.close();
                    Toast.makeText(this, "1. Copia de BD creada!", Toast.LENGTH_SHORT).show();
                    Log.i("DESTINO", String.valueOf(destination));
                    subirBackupBDAServidor(nombreBD, backupDB);//Metodo que sube el backup generado
                } catch(IOException e) {
                    //e.printStackTrace();
                }

            } catch (Exception ex) {
                Log.e("Error", "ex: " + ex);
            }
        } catch (Exception e) {
            Log.e("Error", "e: " + e);
        }
    }

    public void dialogExcesoHoras(int conteo){
        final PrettyDialog pDialog = new PrettyDialog(this);
        pDialog.setCancelable(false);
        pDialog
                .setIcon(
                        R.drawable.ic_menu_horas,     // icon resource
                        R.color.pdlg_color_green,      // icon tint
                        new PrettyDialogCallback() {   // icon OnClick listener
                            @Override
                            public void onClick() {
                                // Do what you gotta do
                            }
                        })
                //.setIconTint(R.color.pdlg_color_red)
                .setTitle("EXCESO DE HORAS")
                .setMessage("Al parecer existe [ " + conteo + " ] trabajadores con un exceso de horas, por favor distribuyé de manera correcta las horas para posteriormente enviar la información.")
                .addButton(
                        "VER PERSONAS",
                        R.color.pdlg_color_white,
                        R.color.colorPrimary,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(PantallaPrincipal.this, PantallaListaExcesoHoras.class));
                                //pDialog.dismiss();
                            }
                        }
                )
                .addButton(
                        "CORREGIR HORAS",
                        R.color.pdlg_color_white,
                        R.color.colorNegroBajo,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(PantallaPrincipal.this, PantallaSeleccionTareoHoras.class));
                                //pDialog.dismiss();
                            }
                        }
                )
                .addButton(
                        "OK, SALIR!",
                        R.color.pdlg_color_white,
                        R.color.colorRojoIntenso,
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

    public void dialogTransferenciaExitosa(){
        final PrettyDialog pDialog = new PrettyDialog(this);
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
                .setTitle("ÉXITO")
                .setMessage("La transferencia de datos se ha realizado correctamente!.")

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

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ON", "RESUME_PRINCIPAL");
        if(MiAplicacionTareo.LEE_PDA.equalsIgnoreCase("SI")){
            this.startService(new Intent(this, ServiceTarea.class));//INICIA EL SERVICIO
            //getContext().stopService(new Intent(getContext(), ServiceTarea.class));
        }else{
            if(MiAplicacionTareo.TIPO_USUARIO.equalsIgnoreCase("MAQUINARIA")){
                this.startService(new Intent(this, ServiceTarea.class));//INICIA EL SERVICIO
                //stopService(new Intent(PantallaMenuPrincipalLee.this, ServiceTarea.class));
            }
        }
    }

    private String enviarXML_Http(){
        final TicketPersona objTPersona = new TicketPersona();
        final TicketCosecha objTCosecha = new TicketCosecha();
        final Tareo objTareo = new Tareo();
        final DetalleTareo objDTareo = new DetalleTareo();
        final Asistencia objAsistencia = new Asistencia();
        final SalidaPersonal objSalidaPersonal = new SalidaPersonal();
        final HoraPersonal objHoraPersonal = new HoraPersonal();
        final BinCabecera objC = new BinCabecera();
        final BinDetalle objD = new BinDetalle();
        final LecturaMaquinaria objLM = new LecturaMaquinaria();
        final SalidaAsistencia objSA = new SalidaAsistencia();

        final StringBuilder detalleTicketPersona = new StringBuilder();
        final StringBuilder detalleTicketCosecha = new StringBuilder();
        final StringBuilder tareos = new StringBuilder();
        final StringBuilder detalleTareo = new StringBuilder();
        final StringBuilder asistencia = new StringBuilder();
        final StringBuilder salidas = new StringBuilder();
        final StringBuilder horaPersonal = new StringBuilder();
        final StringBuilder bincabecera = new StringBuilder();
        final StringBuilder bindetalle = new StringBuilder();
        final StringBuilder detalleLMaquinaria = new StringBuilder();
        final StringBuilder salidaAsistencia = new StringBuilder();

        objTPersona.cargarListaTicketPersonaSQLite();
        objTCosecha.cargarListaTicketCosechaSQLite();
        objTareo.cargarListaTareoSQLite();
        objDTareo.obtenerRendimientosTareoXML();
        objAsistencia.obtenerListaAsistenciaTodasASubir();
        objSalidaPersonal.obtenerListaSalidasTodasASubir();
        objHoraPersonal.obtenerListaHoraPersonalASubir();
        objC.cargarXMLBinCabecera();
        objD.cargarXMLBinDetalle();
        objLM.cargarXMLMaquinariaDetalle();
        objSA.obtenerListaSalidaAsistenciaTodasASubir();

        //if (TicketPersona.listaTicketPersonaSQLiteSubir.size() > 0 && TicketCosecha.listaTicketCosechaSQLiteSubir.size() > 0 && Asistencia.listaAsistenciaSQLiteSubir.size() > 0) {

        detalleTicketPersona.append("<TICKETPERSONA>");
        for (String ticket : TicketPersona.listaTicketPersonaSQLiteSubir) {
            detalleTicketPersona.append(ticket);
        }
        detalleTicketPersona.append("</TICKETPERSONA>");

        detalleTicketCosecha.append("<TICKETCOSECHA>");
        for (String ticket : TicketCosecha.listaTicketCosechaSQLiteSubir) {
            detalleTicketCosecha.append(ticket);
        }
        detalleTicketCosecha.append("</TICKETCOSECHA>");

        tareos.append("<TAREO>");
        for (String tareo : Tareo.listaTareoSQLiteSubir) {
            tareos.append(tareo);
        }
        tareos.append("</TAREO>");

        detalleTareo.append("<DETTAREO>");
        for (String detTareo : DetalleTareo.listaDetalleTareoResumenSQLiteSubir) {
            detalleTareo.append(detTareo);
        }
        detalleTareo.append("</DETTAREO>");

        asistencia.append("<ASISTENCIA>");
        for (String asis : Asistencia.listaAsistenciaSQLiteSubir) {
            asistencia.append(asis);
        }
        asistencia.append("</ASISTENCIA>");

        salidas.append("<SALIDAPERSONAL>");
        for (String sal : SalidaPersonal.listaSalidasSQLiteSubir) {
            salidas.append(sal);
        }
        salidas.append("</SALIDAPERSONAL>");

        horaPersonal.append("<HORAPERSONAL>");
        for (String hp : HoraPersonal.listaPerCHManIngSQLiteSubir) {
            horaPersonal.append(hp);
        }
        horaPersonal.append("</HORAPERSONAL>");

        bincabecera.append("<BINCABECERA>");
        for (String hp : BinCabecera.listaBinCabeceraSQLiteSubir) {
            bincabecera.append(hp);
        }
        bincabecera.append("</BINCABECERA>");

        bindetalle.append("<BINDETALLE>");
        for (String hp : BinDetalle.listaBinDetalleSQLiteSubir) {
            bindetalle.append(hp);
        }
        bindetalle.append("</BINDETALLE>");

        detalleLMaquinaria.append("<LECTURAS_MAQUINARIA>");
        for (String hp : LecturaMaquinaria.listaLMaquinariaSubir) {
            detalleLMaquinaria.append(hp);
        }
        detalleLMaquinaria.append("</LECTURAS_MAQUINARIA>");

        salidaAsistencia.append("<SALIDA_ASISTENCIA>");
        for (String hp : SalidaAsistencia.listaSalidaAsistenciaSubirXML) {
            salidaAsistencia.append(hp);
        }
        salidaAsistencia.append("</SALIDA_ASISTENCIA>");

        Log.i("STRINGBUILDER----", "--------------------------------");

        Log.i("STRINGBUILDER-ASIS", String.valueOf(asistencia));
        Log.i("STRINGBUILDER-TAR", String.valueOf(tareos));
        Log.i("STRINGBUILDER-DETAR", String.valueOf(detalleTareo));
        Log.i("STRINGBUILDER-IDUSU", MiAplicacionTareo.IDUSUARIO);
        Log.i("STRINGBUILDER-SALID", String.valueOf(salidas));
        Log.i("STRINGBUILDER-HORPE", String.valueOf(horaPersonal));
        Log.i("STRINGBUILDER-TPER", String.valueOf(detalleTicketPersona));
        Log.i("STRINGBUILDER-TCOS", String.valueOf(detalleTicketCosecha));
        Log.i("STRINGBUILDER-BCAB", String.valueOf(bincabecera));
        Log.i("STRINGBUILDER-BDET", String.valueOf(bindetalle));
        Log.i("STRINGBUILDER-LMAQ", String.valueOf(detalleLMaquinaria));
        Log.i("STRINGBUILDER-SALASI", String.valueOf(salidaAsistencia));

        String RPTA = "FALSE";

        Vector<HttpPostValues> parametros = new Vector<>();
        parametros.addElement(new HttpPostValues("Asistencia", asistencia.toString()));
        parametros.addElement(new HttpPostValues("Tareo", tareos.toString()));
        parametros.addElement(new HttpPostValues("DetalleTareo", detalleTareo.toString()));
        parametros.addElement(new HttpPostValues("IdUsuario", MiAplicacionTareo.IDUSUARIO));
        parametros.addElement(new HttpPostValues("SalidaPersonal", salidas.toString()));
        parametros.addElement(new HttpPostValues("HoraPersonal", horaPersonal.toString()));
        parametros.addElement(new HttpPostValues("TicketPersona", detalleTicketPersona.toString()));
        parametros.addElement(new HttpPostValues("TicketCosecha", detalleTicketCosecha.toString()));
        parametros.addElement(new HttpPostValues("BinCabecera", bincabecera.toString()));
        parametros.addElement(new HttpPostValues("BinDetalle", bindetalle.toString()));
        parametros.addElement(new HttpPostValues("LecturaMaquinaria", detalleLMaquinaria.toString()));
        parametros.addElement(new HttpPostValues("SalidaAsistencia", salidaAsistencia.toString()));

        try {
            String resultado = new Http().enviarPost(MiAplicacionTareo.URL_SERVICE_SUBIDA_DATOS, parametros);
            Log.i("RESPONSE", "|"+resultado);
            JSONObject json = new JSONObject(resultado);
            RPTA = json.getString("resultado");
        }catch (Exception e){
            if(e != null){
                RPTA=""+e.getMessage();
            }else{
                RPTA="ERROR_DESCONOCIDO";
            }
            //e.printStackTrace();
        }
        return RPTA;
    }

    private class TareaEnviarDataXMLHtml extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            MiAplicacionTareo.ENVIANDO=1;
            super.onPreExecute();
            pDialog = new ProgressDialog(PantallaPrincipal.this);
            pDialog.setMessage("Enviando...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return enviarXML_Http();
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            Log.i("RESULT_ASYNTASK", result);
            MiAplicacionTareo obj = new MiAplicacionTareo();
            if (result.equalsIgnoreCase("true")) {
                obj.modificarEstadoSincronizado("2", "1");//1
                dialogTransferenciaExitosa();
            }else if(result.equalsIgnoreCase("FALSE")){
                obj.modificarEstadoSincronizado("2", "0");
                Toast.makeText(PantallaPrincipal.this, "Ocurrió un inconveniente al transferir la información, vuelve a intentarlo! - FALSE", Toast.LENGTH_LONG).show();
            }else{
                obj.modificarEstadoSincronizado("2", "0");
                Toast.makeText(PantallaPrincipal.this, "Ocurrió un inconveniente al transferir la información, vuelve a intentarlo! - ERROR", Toast.LENGTH_LONG).show();
            }

            MiAplicacionTareo.ENVIANDO=0;

        }
    }

    public void dialogAdvertenciaTransferir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PantallaPrincipal.this);
        builder.setTitle("¿Transferir datos al servidor?");
        builder.setMessage("Se subirá la data registrada hasta el momento en el dispositivo.");
        builder.setIcon(R.drawable.ic_correcto);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //subirData();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String fechaHoy = dateFormat.format(date);
                HoraPersonal objHP = new HoraPersonal();
                int x = objHP.conteoCumpleConElNivelDeHorasPermitido(""+fechaHoy);
                if(x != 0){
                    dialogExcesoHoras(x);
                    //Toast.makeText(PantallaMenuPrincipal.this, "Existen " + x + " personas que tienen más horas de las permitidas, por favor distribuya de manera correcta las horas." , Toast.LENGTH_LONG).show();
                }else{
                    if(MiAplicacionTareo.hayConexion(PantallaPrincipal.this)){
                        if(MiAplicacionTareo.ENVIANDO==0){
                            new TareaEnviarDataXMLHtml().execute();
                        }else{
                            Toast.makeText(PantallaPrincipal.this, "Espere, enviando en segundo plano... " + x, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(PantallaPrincipal.this, "Verifica tu conexión a internet... " + x, Toast.LENGTH_SHORT).show();
                    }
                    //subirData();

                    //Toast.makeText(PantallaPrincipal.this, "Subiendo... " + x, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean determinarConexion(){
        ConnectivityManager cm = (ConnectivityManager)PantallaPrincipal.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void onClick(View view) {
        Intent p;
        //menu_tareos, menu_asistencia, menu_asignacion, menu_rendimientos, menu_reportes, menu_sincronizar, menu_transferir;
        switch (view.getId()){
            case R.id.menu_tareos:
                if(MiAplicacionTareo.IDUSUARIO.equalsIgnoreCase("0")){
                    Toast.makeText(PantallaPrincipal.this, "Este usuario no puede crear tareos!", Toast.LENGTH_SHORT).show();
                }else{
                    p = new Intent(PantallaPrincipal.this, PantallaTareos.class);
                    startActivity(p);
                }

                break;
            case R.id.menu_asistencia:
                /*if(MiAplicacionTareo.LEE_PDA.equalsIgnoreCase("NO")){

                }else{
                    Bundle parametros = new Bundle();
                    p = new Intent(PantallaPrincipal.this, PantallaGruposLista.class);
                    parametros.putString("p_menu", "2");
                    p.putExtras(parametros);
                    startActivity(p);
                }*/

                startActivity(new Intent(this, PantallaTareoAsistencia.class));

                //startActivity(new Intent(this, PantallaTareoAsistencia.class));

                /*p = new Intent(PantallaPrincipal.this, PantallaGruposLista.class);
                startActivity(p);*/
                break;
            case R.id.menu_rendimientos:
                /*Bundle parametros2 = new Bundle();
                p = new Intent(PantallaPrincipal.this, PantallaGruposLista.class);
                parametros2.putString("p_menu", "3");
                p.putExtras(parametros2);
                startActivity(p);*/

                startActivity(new Intent(this, PantallaListaTareos.class));
                break;
            case R.id.menu_asignacion_horas:
                /*Bundle parametros3 = new Bundle();
                p = new Intent(PantallaPrincipal.this, PantallaGruposLista.class);
                parametros3.putString("p_menu", "4");
                p.putExtras(parametros3);
                startActivity(p);*/

                startActivity(new Intent(this, PantallaSeleccionTareoHoras.class));
                break;
            case R.id.menu_sincronizar:
                if(determinarConexion()){
                    dialogAdvertenciaSincronizar();
                }else{
                    Toast.makeText(PantallaPrincipal.this, "No dispone de Conexión a Internet", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_transferir:
                if(determinarConexion()){
                    dialogAdvertenciaTransferir();
                }else{
                    Toast.makeText(PantallaPrincipal.this, "No dispone de Conexión a Internet", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_asignacion_tickets:
                startActivity(new Intent(PantallaPrincipal.this, PantallaAsignacionTickets.class));

                break;
            case R.id.menu_asistentes_cosecha:
                //startActivity(new Intent(PantallaPrincipal.this, Gui_Asistentes_cosecha.class));
                //startActivity(new Intent(this, PantallaTareoAsistencia.class));

                if(MiAplicacionTareo.LEE_PDA.equalsIgnoreCase("NO")){
                    startActivity(new Intent(this, PantallaTareoAsistencia.class));
                }else{
                    Bundle parametros = new Bundle();
                    p = new Intent(PantallaPrincipal.this, PantallaGruposLista.class);
                    parametros.putString("p_menu", "2");
                    p.putExtras(parametros);
                    startActivity(p);
                }

                break;
            case R.id.menu_bines:
                startActivity(new Intent(PantallaPrincipal.this, PantallaListaCabeceraBin.class));
                break;
            case R.id.menu_lectura_tickets:
                if(MiAplicacionTareo.TIPO_USUARIO.equalsIgnoreCase("MAQUINARIA")){
                    startActivity(new Intent(PantallaPrincipal.this, LecturaPortabinMaquinaria.class));
                }else{
                    startActivity(new Intent(PantallaPrincipal.this, PantallaLecturaTickets.class));
                }


                break;
            case R.id.menu_salida:
                startActivity(new Intent(PantallaPrincipal.this, PantallaSalidaAsistencia.class));
                break;
            case R.id.fablimpiarTablasDeMovimiento:
                new TareaLimpiarTablasDeMovimiento().execute();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pantalla_principal, menu);
        return true;
    }

    public void dialogAdvertenciaSalir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PantallaPrincipal.this);
        builder.setTitle("¿Salir?");
        builder.setMessage("Salir del Menú Principal");
        builder.setIcon(R.drawable.ic_salir);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(MiAplicacionTareo.LEE_PDA.equalsIgnoreCase("SI")){
                    stopService(new Intent(PantallaPrincipal.this, ServiceTarea.class));
                }else{
                    if(MiAplicacionTareo.TIPO_USUARIO.equalsIgnoreCase("MAQUINARIA")){
                        stopService(new Intent(PantallaPrincipal.this, ServiceTarea.class));
                    }
                }
                finish();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        dialogAdvertenciaSalir();
    }

    public void dialogAdvertenciaSubirBDBackup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PantallaPrincipal.this);
        builder.setTitle("¿Subir Backup de BD Actual?");
        builder.setMessage("Se creará una copia de la BD actual, y se enviará al servidor.");
        builder.setIcon(R.drawable.ic_menu_sincronizar_02);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                generarBackupBDYSubirloServidor();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void mostrarCheckOErrorDialogSincronizar(Boolean estado_pb, Boolean estado_img, ProgressBar pb, ImageView img){
        if(estado_pb){
            pb.setVisibility(View.VISIBLE);
            img.setVisibility(View.INVISIBLE);
        }else{
            if(estado_img){//Exito
                pb.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);
                img.setImageResource(R.drawable.ic_correcto);
            }else{
                pb.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);
                img.setImageResource(R.drawable.ic_error);
            }
        }
    }

    private class TareaAgregarDatosObtenidosDeServicesASQLite extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(PantallaPrincipal.this);
            pDialog.setTitle("Grabando datos en BD Local...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            Consumidor objC = new Consumidor();
            Actividad objA = new Actividad();
            Labor objL = new Labor();
            SubLabor objS = new SubLabor();
            PersonalGeneral objPG = new PersonalGeneral();
            Usuario objU = new Usuario();
            Administrador objAdm = new Administrador();
            HoraTareo objHoraT = new HoraTareo();
            Variedad objV = new Variedad();
            Reconocimiento objR = new Reconocimiento();

            objC.limpiarTablaConsumidorSQLite();
            objA.limpiarTablaActividadSQLite();
            objL.limpiarTablaLaborSQLite();
            objPG.limpiarTablaPersonalGeneralSQLite();
            objU.limpiarTablaUsuarioSQLite();
            objAdm.limpiarTablaAdministradorSQLite();
            objHoraT.limpiarTablaHoraTareoSQLite();
            objS.limpiarTablaSubLaborSQLite();
            objV.limpiarTablaVariedadSQLite();
            objR.limpiarTablaReconocimientosSQLite();

            objC.agregar_ConsumidoresSQLite();
            objA.agregar_ActividadSQLite();
            objS.agregar_SublaboresSQLite();
            objL.agregar_LaborSQLite();
            objPG.agregar_PersonalGeneralSQLite();
            objU.agregar_UsuarioSQLite();
            objAdm.agregar_AdministradorSQLite();
            objHoraT.agregar_HoraTareoSQLite();
            objV.agregar_VariedadSQLite();
            objR.agregarReconocimientosMasivoServidor();
            return true;
        }

        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            if(result){
                //guardar_preferencias(fechaHoy);
                Toast.makeText(PantallaPrincipal.this, "Sincronización Exitosa", Toast.LENGTH_LONG).show();
                //Usuario obj = new Usuario();
                //obj.cargarListaUsuarios();
            }else{
                Toast.makeText(PantallaPrincipal.this, "Ocurrió un error al sincronizar!, vuelve a intentarlo!!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sincronizandoHoraTareo(){
        String url = MiAplicacionTareo.URL_SERVICE + "getHoraTareoPalta";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                Log.i("ResponseHT", response);
                try {
                    HoraTareo.listaHoraTareo.clear();
                    Variedad.listaVariedad.clear();
                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray = json.getJSONArray("resultado");
                    JSONArray jsonArrayV = json.getJSONArray("variedades");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Log.i("HORATAREO", jsonData.getString("DIA") +"-"+jsonData.getString("HORAS"));

                        HoraTareo.listaHoraTareo.add(new HoraTareo(
                                jsonData.getString("IDCULTIVO").trim(),
                                jsonData.getString("DIA").trim(),
                                jsonData.getString("HORAS"),
                                jsonData.getString("FECHAREGISTRO").trim(),
                                jsonData.getString("IDUSUARIO").trim()
                        ));
                    }

                    for(int i = 0; i < jsonArrayV.length(); i++){
                        JSONObject jsonData = jsonArrayV.getJSONObject(i);
                        Log.i("VARIEDAD", jsonData.getString("IDVARIEDAD") +"-"+jsonData.getString("DESCRIPCION"));
                        Variedad.listaVariedad.add(new Variedad(
                                jsonData.getString("IDCULTIVO").trim(),
                                jsonData.getString("IDVARIEDAD").trim(),
                                jsonData.getString("DESCRIPCION").trim()
                        ));
                    }

                    mostrarCheckOErrorDialogSincronizar(false, true, progressBar6, img_estado_sinc_d6);
                    new TareaAgregarDatosObtenidosDeServicesASQLite().execute();

                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar6, img_estado_sinc_d6);
                    }
                }
        );
        request.add(postRequest);
    }

    private void sincronizandoValvulas(){
        String url = MiAplicacionTareo.URL_SERVICE + "getValvulasPalta";//Arandano
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                Log.i("ResponseValvulas", response);
                try {
                    Valvula.listaValvulas.clear();
                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray = json.getJSONArray("resultado");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Log.i("VALVULA", jsonData.getString("VALVULA"));

                        Valvula.listaValvulas.add(new Valvula(
                                jsonData.getString("NROVALVULA").trim(),
                                jsonData.getString("VALVULA").trim(),
                                jsonData.getString("IDCONSUMIDOR").trim()
                        ));
                    }

                    if(Valvula.listaValvulas.isEmpty()){
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar5, img_estado_sinc_d5);
                    }else{
                        sincronizandoHoraTareo();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar4, img_estado_sinc_d4);
                    }
                }
        );
        request.add(postRequest);
    }

    private void sincronizandoAdministradores(){//Para los telefonos
        String url = MiAplicacionTareo.URL_SERVICE + "getAdministradoresPalta";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                Log.i("Response", response);
                try {
                    Administrador.listaNumerosAdministradores.clear();
                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray = json.getJSONArray("resultado");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Log.i("ADMINISTRADOR", jsonData.getString("NUMERO"));

                        Administrador.listaNumerosAdministradores.add(new Administrador(
                                jsonData.getString("NUMERO"),
                                jsonData.getString("CORREO"),
                                jsonData.getString("NOMBRE")
                        ));
                    }

                    if(Administrador.listaNumerosAdministradores.isEmpty()){
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar5, img_estado_sinc_d5);
                    }else{
                        sincronizandoValvulas();
                    }


                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar6, img_estado_sinc_d6);
                    }
                }
        );
        request.add(postRequest);
    }

    private void sincronizandoUsuario(){
        String url = MiAplicacionTareo.URL_SERVICE + "getUsuariosPalta";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                //Log.i("Response", response);
                try {
                    Usuario.listaUsuariosHost.clear();

                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray = json.getJSONArray("resultado");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Log.i("USUARIOS", jsonData.getString("DNI"));

                        Usuario.listaUsuariosHost.add(new Usuario(
                                jsonData.getString("IDUSUARIO"),
                                jsonData.getString("DNI"),
                                jsonData.getString("CLAVE"),
                                jsonData.getString("IDCULTIVO"),
                                jsonData.getString("LEE_PDA"),
                                jsonData.getString("TIPO_USUARIO")
                        ));
                    }

                    if(Usuario.listaUsuariosHost.isEmpty()){
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar6, img_estado_sinc_d6);
                    }else{
                        //mostrarCheckOErrorDialogSincronizar(false, true, progressBar6, img_estado_sinc_d6);
                        //new TareaAgregarDatosObtenidosDeServicesASQLite().execute();
                        sincronizandoAdministradores();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar6, img_estado_sinc_d6);
                    }
                }
        );
        request.add(postRequest);
    }

    private void sincronizandoPersonalGeneral_bk(){//07/02/2022
        String url = MiAplicacionTareo.URL_SERVICE + "getPersonalPalta";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                //Log.i("Response", response);
                try {
                    PersonalGeneral.listaPersonalGeneralHost.clear();

                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray = json.getJSONArray("resultado");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Log.i("PERSONALGENERAL", jsonData.getString("NOMBRES"));

                        PersonalGeneral.listaPersonalGeneralHost.add(new PersonalGeneral(
                                jsonData.getString("CODIGO"),
                                jsonData.getString("DNI"),
                                jsonData.getString("NOMBRES"),
                                jsonData.getString("OBSERVADO"),
                                jsonData.getString("MODULO"),
                                jsonData.getString("MOTIVO")
                        ));
                    }

                    if(PersonalGeneral.listaPersonalGeneralHost.isEmpty()){
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar5, img_estado_sinc_d5);
                    }else{
                        mostrarCheckOErrorDialogSincronizar(false, true, progressBar5, img_estado_sinc_d5);
                        sincronizandoUsuario();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar5, img_estado_sinc_d5);
                    }
                }
        );
        request.add(postRequest);
    }

    private void sincronizandoPersonalGeneral(){
        String url = MiAplicacionTareo.URL_SERVICE + "getPersonalPalta_2";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                //Log.i("Response", response);
                try {
                    PersonalGeneral.listaPersonalGeneralHost.clear();
                    Reconocimiento.listaReconocimientosServidor.clear();

                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray = json.getJSONArray("resultado");
                    JSONArray jsonArray2 = json.getJSONArray("reconocimientos");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Log.i("PERSONALGENERAL", jsonData.getString("NOMBRES"));

                        PersonalGeneral.listaPersonalGeneralHost.add(new PersonalGeneral(
                                jsonData.getString("CODIGO"),
                                jsonData.getString("DNI"),
                                jsonData.getString("NOMBRES"),
                                jsonData.getString("OBSERVADO"),
                                jsonData.getString("MODULO"),
                                jsonData.getString("MOTIVO")
                        ));
                    }

                    for(int i = 0; i < jsonArray2.length(); i++){
                        JSONObject jsonData = jsonArray2.getJSONObject(i);
                        Log.i("RECONOCIMIENTOS", jsonData.getString("IDCODIGOGENERAL"));
                        Reconocimiento.listaReconocimientosServidor.add(
                                new Reconocimiento(
                                        ""+jsonData.getString("IDCODIGOGENERAL"),
                                        ""+jsonData.getString("DISTANCE"),
                                        ""+jsonData.getString("EXTRA_FACE"),
                                        ""+jsonData.getString("ID_OPTIONAL"),
                                        ""+jsonData.getString("TITLE_OPTIONAL")
                                )
                        );
                    }

                    if(PersonalGeneral.listaPersonalGeneralHost.isEmpty()){
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar5, img_estado_sinc_d5);
                    }else{
                        mostrarCheckOErrorDialogSincronizar(false, true, progressBar5, img_estado_sinc_d5);
                        sincronizandoUsuario();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar5, img_estado_sinc_d5);
                    }
                }
        );
        request.add(postRequest);
    }

    private void sincronizandoSubLabores(){
        String url = MiAplicacionTareo.URL_SERVICE + "getSubLaboresPalta";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                //Log.i("Response", response);
                try {
                    SubLabor.listaSubLaborHost.clear();
                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray = json.getJSONArray("resultado");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Log.i("SUBLABOR", jsonData.getString("SUBLABOR"));

                        SubLabor.listaSubLaborHost.add(new SubLabor(
                                jsonData.getString("IDACTIVIDAD"),
                                jsonData.getString("IDLABOR"),
                                jsonData.getString("IDSUBLABOR"),
                                jsonData.getString("SUBLABOR"),
                                jsonData.getString("GRUPO"),
                                jsonData.getString("UNIDAD")
                        ));
                    }

                    if(SubLabor.listaSubLaborHost.isEmpty()){
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar4, img_estado_sinc_d4);
                    }else{
                        mostrarCheckOErrorDialogSincronizar(false, true, progressBar4, img_estado_sinc_d4);
                        sincronizandoPersonalGeneral();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar4, img_estado_sinc_d4);
                    }
                }
        );
        request.add(postRequest);
    }

    private void sincronizandoLabores(){
        String url = MiAplicacionTareo.URL_SERVICE + "getLaboresPalta";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                //Log.i("Response", response);
                try {
                    Labor.listaLaborHost.clear();

                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray = json.getJSONArray("resultado");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Log.i("LABOR", jsonData.getString("LABOR"));

                        Labor.listaLaborHost.add(new Labor(
                                jsonData.getString("IDEMPRESA"),
                                jsonData.getString("IDACTIVIDAD"),
                                jsonData.getString("IDLABOR"),
                                jsonData.getString("LABOR"),
                                jsonData.getString("ALIAS")
                        ));
                    }

                    if(Labor.listaLaborHost.isEmpty()){
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar3, img_estado_sinc_d3);
                    }else{
                        mostrarCheckOErrorDialogSincronizar(false, true, progressBar3, img_estado_sinc_d3);
                        sincronizandoSubLabores();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar3, img_estado_sinc_d3);
                    }
                }
        );
        request.add(postRequest);
    }

    private void sincronizandoActividades(){
        //String url = MiAplicacionTareo.URL_SERVICE + "getActividadesPalta";
        String url = MiAplicacionTareo.URL_SERVICE + "getActividadesPalta";
        Log.i("ACTIVIDAD", "ACTIVIDAD");

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                //Log.i("Response", response);
                try {
                    Actividad.listaActividadHost.clear();

                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray = json.getJSONArray("resultado");
                    Log.i("ACTIVIDAD", "ACTIVIDAD");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Log.i("ACTIVIDAD", jsonData.getString("ACTIVIDAD"));

                        Actividad.listaActividadHost.add(new Actividad(
                                jsonData.getString("IDEMPRESA"),
                                jsonData.getString("IDACTIVIDAD"),
                                jsonData.getString("ACTIVIDAD"),
                                jsonData.getString("ALIAS")
                        ));
                    }

                    if(Actividad.listaActividadHost.isEmpty()){
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar2, img_estado_sinc_d2);
                    }else{
                        mostrarCheckOErrorDialogSincronizar(false, true, progressBar2, img_estado_sinc_d2);
                        sincronizandoLabores();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar2, img_estado_sinc_d2);
                    }
                }
        );
        request.add(postRequest);
    }

    private void sincronizandoConsumidores(){
        String url = MiAplicacionTareo.URL_SERVICE + "getConsumidoresPalta";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                //Log.i("Response", response);
                try {
                    Consumidor.listaConsumidorHost.clear();

                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray = json.getJSONArray("resultado");

                    Consumidor.listaConsumidorHost.add(new Consumidor("001",
                            "S-M-T-",
                            "PRETAREO",
                            "0006",
                            "PALTO"));

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Log.i("CONSUMIDOR", jsonData.getString("CONSUMIDOR"));

                        Consumidor.listaConsumidorHost.add(new Consumidor(
                                jsonData.getString("IDEMPRESA"),
                                jsonData.getString("IDCONSUMIDOR"),
                                jsonData.getString("CONSUMIDOR"),
                                jsonData.getString("IDCULTIVO"),
                                jsonData.getString("CULTIVO")
                        ));

                    }

                    if(Consumidor.listaConsumidorHost.isEmpty()){
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar1, img_estado_sinc_d1);
                    }else{
                        mostrarCheckOErrorDialogSincronizar(false, true, progressBar1, img_estado_sinc_d1);
                        sincronizandoActividades();
                    }

                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mostrarCheckOErrorDialogSincronizar(false, false, progressBar1, img_estado_sinc_d1);
                    }
                }
        );
        request.add(postRequest);
    }

    private String generarCodigoTareo() {
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Date date = new Date();
        return id+"_"+dateFormat.format(date);
    }

    private void guardarTareosCosecha() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Tareo objA = new Tareo();

        objA.cargarListaTareoPantallaListaSQLite();
        if (Tareo.listaTareoPantallaSQLite.size() > 0) {
            Log.i("estado", "lista llena");
            MiAplicacionTareo.CODIGOTAREOCOSECHA = objA.obtenerTareoCosecha();
        } else{
            Log.i("estado", "lista vacia");
            Log.i("estado", dateFormat.format(date));

            objA.agregarTareoSQLite(generarCodigoTareo() +"_1", dateFormat.format(date), MiAplicacionTareo.IDUSUARIO, "S-M-T-", "A53", "030", "005", "O", "", "0.0", "01");
            objA.agregarTareoSQLite(generarCodigoTareo() +"_2", dateFormat.format(date), MiAplicacionTareo.IDUSUARIO, "S-M-T-", "A09", "001", "001", "O", "", "0.0", "01");
            String codigoBinero = generarCodigoTareo() +"_3";
            objA.agregarTareoSQLite(codigoBinero, dateFormat.format(date), MiAplicacionTareo.IDUSUARIO, "S-M-T-", "A07", "050", "001", "O", "", "0.0", "01");
            Asistencia asistencia = new Asistencia();
            Long r = asistencia.agregarAsistenciaSQLite(codigoBinero, MiAplicacionTareo.DNIUSUARIO);
            objA.agregarTareoSQLite(generarCodigoTareo() +"_4", dateFormat.format(date), MiAplicacionTareo.IDUSUARIO, "S-M-T-", "A03", "014", "002", "O", "", "0.0", "01");
            MiAplicacionTareo.CODIGOTAREOCOSECHA = generarCodigoTareo() +"_5";
            objA.agregarTareoSQLite(MiAplicacionTareo.CODIGOTAREOCOSECHA, dateFormat.format(date), MiAplicacionTareo.IDUSUARIO, "S-M-T-", "A07", "001", "001", "O", "", "0.0", "01");
        }
    }

    public AlertDialog dialogSincronizando(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(PantallaPrincipal.this);
        builder.setCancelable(false);
        LayoutInflater inflater = PantallaPrincipal.this.getLayoutInflater();

        View v = inflater.inflate(R.layout.item_sincronizar, null);

        progressBar1 = (ProgressBar)v.findViewById(R.id.progressBar1);
        progressBar2 = (ProgressBar)v.findViewById(R.id.progressBar2);
        progressBar3 = (ProgressBar)v.findViewById(R.id.progressBar3);
        progressBar4 = (ProgressBar)v.findViewById(R.id.progressBar4);
        progressBar5 = (ProgressBar)v.findViewById(R.id.progressBar5);
        progressBar6 = (ProgressBar)v.findViewById(R.id.progressBar6);

        img_estado_sinc_d1 = (ImageView)v.findViewById(R.id.img_estado_sinc_d1);
        img_estado_sinc_d2 = (ImageView)v.findViewById(R.id.img_estado_sinc_d2);
        img_estado_sinc_d3 = (ImageView)v.findViewById(R.id.img_estado_sinc_d3);
        img_estado_sinc_d4 = (ImageView)v.findViewById(R.id.img_estado_sinc_d4);
        img_estado_sinc_d5 = (ImageView)v.findViewById(R.id.img_estado_sinc_d5);
        img_estado_sinc_d6 = (ImageView)v.findViewById(R.id.img_estado_sinc_d6);

        btn_cancelar = (Button)v.findViewById(R.id.btn_cancelar);
        btn_aceptar = (Button)v.findViewById(R.id.btn_aceptar);

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSincronizando.dismiss();
            }
        });

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSincronizando.dismiss();
            }
        });

        sincronizandoConsumidores();

        builder.setView(v);
        return  builder.create();
    }

    public void dialogAdvertenciaSincronizar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PantallaPrincipal.this);
        builder.setTitle("¿Sincronizar?");
        builder.setMessage("Se actualizarán las tablas maestras");
        builder.setIcon(R.drawable.ic_correcto);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialogSincronizando = dialogSincronizando();
                dialogSincronizando.show();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class TareaAgregarDatosObtenidosDeServicesASQLite_Lee extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(PantallaPrincipal.this);
            pDialog.setTitle("Grabando datos en BD Local...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            Consumidor objC = new Consumidor();
            Actividad objA = new Actividad();
            Labor objL = new Labor();
            SubLabor objS = new SubLabor();
            PersonalGeneral objPG = new PersonalGeneral();
            Usuario objU = new Usuario();
            Administrador objAdm = new Administrador();
            HoraTareo objHoraT = new HoraTareo();

            objC.limpiarTablaConsumidorSQLite();
            objA.limpiarTablaActividadSQLite();
            objL.limpiarTablaLaborSQLite();
            objPG.limpiarTablaPersonalGeneralSQLite();
            objU.limpiarTablaUsuarioSQLite();
            objAdm.limpiarTablaAdministradorSQLite();
            objHoraT.limpiarTablaHoraTareoSQLite();
            objS.limpiarTablaSubLaborSQLite();

            objC.agregar_ConsumidoresSQLite();
            objA.agregar_ActividadSQLite();
            objS.agregar_SublaboresSQLite();
            objL.agregar_LaborSQLite();
            objPG.agregar_PersonalGeneralSQLite();
            objU.agregar_UsuarioSQLite();
            objAdm.agregar_AdministradorSQLite();
            objHoraT.agregar_HoraTareoSQLite();
            return true;
        }

        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            if(result){
                //guardar_preferencias(fechaHoy);
                Toast.makeText(PantallaPrincipal.this, "Sincronización Exitosa", Toast.LENGTH_LONG).show();
                //Usuario obj = new Usuario();
                //obj.cargarListaUsuarios();
            }else{
                Toast.makeText(PantallaPrincipal.this, "Ocurrió un error al sincronizar!, vuelve a intentarlo!!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void dialogDescargar(final String LINK_DESCARGA){
        AlertDialog.Builder builder = new AlertDialog.Builder(PantallaPrincipal.this);
        builder.setTitle("Información");
        builder.setMessage("¿Descargar última versión disponible en el servidor?");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_descargar_black);
        builder.setPositiveButton("DESCARGAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                descargar(LINK_DESCARGA);
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void descargar(String URLDescarga){
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Descargando...");
//        progressDialog.setMessage("Downloading now...");
        pDialog.setProgress(0);
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setProgress(0);
        pDialog.show();
        Pump.newRequest(URLDescarga)
                .threadNum(1)
                .listener(new DownloadListener() {

                    @Override
                    public void onProgress(int progress) {
                        pDialog.setProgress(progress);
                    }

                    @Override
                    public void onSuccess() {
                        pDialog.dismiss();
                        String apkPath = getDownloadInfo().getFilePath();
                        APKInstall.with(PantallaPrincipal.this).from(apkPath)
//                                        .forceInstall();
                                .install();
                        Toast.makeText(PantallaPrincipal.this, "Descarga Finalizada!.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed() {
                        pDialog.dismiss();
                        Toast.makeText(PantallaPrincipal.this, "Descarga ha fallado!.", Toast.LENGTH_SHORT).show();
                    }
                })
                //Optionally,Set whether to repeatedly download the downloaded file,default false.
                .forceReDownload(true)
                //Optionally,Set how many threads are used when downloading,default 3.
                .threadNum(3)
                .setRetry(3, 200)
                .submit();
    }
}
