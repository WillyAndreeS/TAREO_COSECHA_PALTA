package com.acpagro.tareopalta;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.acpagro.tareopalta.PrinterControlBixolon.BixolonPrinter;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.bxl.config.editor.BXLConfigLoader;
import com.bxl.config.util.BXLBluetoothLE;
import com.bxl.config.util.BXLNetwork;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import jpos.JposException;

public class ConexionImpresora extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener, View.OnTouchListener, View.OnClickListener {
    private static BixolonPrinter bxlPrinter = null;
    private final int REQUEST_PERMISSION = 0;
    private final String DEVICE_ADDRESS_START = " (";
    private final String DEVICE_ADDRESS_END = ")";

    private final ArrayList<CharSequence> bondedDevices = new ArrayList<>();
    private ArrayAdapter<CharSequence> arrayAdapter;

    private int portType = BXLConfigLoader.DEVICE_BUS_BLUETOOTH;
    private String logicalName = "";
    private String address = "";

    private LinearLayout layoutModel;
    private LinearLayout layoutIPAddress;

    private RadioGroup radioGroupPortType;
    private RadioGroup radioGroupPrintType;
    private RadioButton radioBixolon;
    private RadioButton radioZebra;
    private TextView textViewBluetooth;
    private ListView listView;
    private EditText editTextIPAddress;
    private CheckBox checkBoxAsyncMode;

    private Button btnGuardarConfiguracion;
    private ProgressBar mProgressLarge;
    private FloatingActionButton fab_guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexion_impresora);

        //Para que apareca la flecha hacia atrás
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Configuración Impresora");

        layoutModel = findViewById(R.id.LinearLayout2);
        layoutIPAddress = findViewById(R.id.LinearLayout3);
        layoutIPAddress.setVisibility(View.GONE);

        radioGroupPortType = findViewById(R.id.radioGroupPortType);
        radioGroupPrintType = findViewById(R.id.radioGroupPrintType);
        radioGroupPortType.setOnCheckedChangeListener(this);
        radioGroupPrintType.setOnCheckedChangeListener(this);

        radioBixolon = (RadioButton)findViewById(R.id.radioBixolon);
        radioZebra = (RadioButton)findViewById(R.id.radioZebra);

        textViewBluetooth = findViewById(R.id.textViewBluetoothList);
        editTextIPAddress = findViewById(R.id.editTextIPAddr);
        editTextIPAddress.setText("192.168.0.1");

        checkBoxAsyncMode = findViewById(R.id.checkBoxAsyncMode);

        btnGuardarConfiguracion = findViewById(R.id.btnGuardarConfiguracion);
        btnGuardarConfiguracion.setOnClickListener(this);

        mProgressLarge = findViewById(R.id.progressBar1);
        mProgressLarge.setVisibility(ProgressBar.GONE);

        fab_guardar = (FloatingActionButton)findViewById(R.id.fab_guardar);
        fab_guardar.setOnClickListener(this);

        setPairedDevices();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, bondedDevices);
        listView = findViewById(R.id.listViewPairedDevices);
        listView.setAdapter(arrayAdapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(this);
        listView.setOnTouchListener(this);

        Spinner modelList = findViewById(R.id.spinnerModelList);

        ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(this, R.array.modelList, android.R.layout.simple_spinner_dropdown_item);
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelList.setAdapter(modelAdapter);
        modelList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                logicalName = (String) parent.getItemAtPosition(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                }
            }
        }

        bxlPrinter = new BixolonPrinter(this);

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

    private void setPairedDevices() {
        /*bondedDevices.clear();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDeviceSet = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedDeviceSet) {
            bondedDevices.add(device.getName() + DEVICE_ADDRESS_START + device.getAddress() + DEVICE_ADDRESS_END);
        }
        if (arrayAdapter != null) {
            arrayAdapter.notifyDataSetChanged();
        }*/
        new tarea_setPairedDevices().execute();
    }

    private class tarea_setPairedDevices extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            bondedDevices.clear();
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> bondedDeviceSet = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : bondedDeviceSet) {
                bondedDevices.add(device.getName() + DEVICE_ADDRESS_START + device.getAddress() + DEVICE_ADDRESS_END);
            }

            if (arrayAdapter != null) {
                return true;
            }else{
                return false;
            }
        }

        protected void onPostExecute(Boolean rpta) {
            arrayAdapter.notifyDataSetChanged();
        }
    }

    private void setBleDevices() {
        mHandler.obtainMessage(0).sendToTarget();
        BXLBluetoothLE.setBLEDeviceSearchOption(5, 1);
        new searchBLEPrinterTask().execute();
    }

    private void setNetworkDevices() {
        mHandler.obtainMessage(0).sendToTarget();
        BXLNetwork.setWifiSearchOption(5, 1);
        new searchNetworkPrinterTask().execute();
    }

    private class searchNetworkPrinterTask extends AsyncTask<Integer, Integer, Set<CharSequence>> {
        private String message;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Set<CharSequence> NetworkDeviceSet) {
            bondedDevices.clear();

            String[] items;
            if (NetworkDeviceSet != null && !NetworkDeviceSet.isEmpty()) {
                items = NetworkDeviceSet.toArray(new String[NetworkDeviceSet.size()]);
                for (int i = 0; (items != null) && (i < items.length); i++) {
                    bondedDevices.add(items[i]);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Can't found network devices. ", Toast.LENGTH_SHORT).show();
            }

            if (arrayAdapter != null) {
                arrayAdapter.notifyDataSetChanged();
            }

            if (message != null) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            mHandler.obtainMessage(1).sendToTarget();
        }

        @Override
        protected Set<CharSequence> doInBackground(Integer... params) {
            try {
                return BXLNetwork.getNetworkPrinters(ConexionImpresora.this, BXLNetwork.SEARCH_WIFI_ALWAYS);
            } catch (NumberFormatException | JposException e) {
                message = e.getMessage();
                return new HashSet<>();
            }
        }
    }

    private class searchBLEPrinterTask extends AsyncTask<Integer, Integer, Set<BluetoothDevice>> {
        private String message;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Set<BluetoothDevice> bluetoothDeviceSet) {
            bondedDevices.clear();

            if (bluetoothDeviceSet.size() > 0) {
                for (BluetoothDevice device : bluetoothDeviceSet) {
                    bondedDevices.add(device.getName() + DEVICE_ADDRESS_START + device.getAddress() + DEVICE_ADDRESS_END);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Can't found BLE devices. ", Toast.LENGTH_SHORT).show();
            }

            if (arrayAdapter != null) {
                arrayAdapter.notifyDataSetChanged();
            }

            if (message != null) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            mHandler.obtainMessage(1).sendToTarget();
        }

        @Override
        protected Set<BluetoothDevice> doInBackground(Integer... params) {
            try {
                return BXLBluetoothLE.getBLEPrinters(ConexionImpresora.this, BXLBluetoothLE.SEARCH_BLE_ALWAYS);
            } catch (NumberFormatException | JposException e) {
                message = e.getMessage();
                return new HashSet<>();
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.radioBT:
                portType = BXLConfigLoader.DEVICE_BUS_BLUETOOTH;
                textViewBluetooth.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                layoutIPAddress.setVisibility(View.GONE);

                setPairedDevices();
                break;
            case R.id.radioBLE:
                portType = BXLConfigLoader.DEVICE_BUS_BLUETOOTH_LE;
                textViewBluetooth.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                layoutIPAddress.setVisibility(View.GONE);

                setBleDevices();
                break;
            case R.id.radioWifi:
                portType = BXLConfigLoader.DEVICE_BUS_WIFI;
                layoutIPAddress.setVisibility(View.VISIBLE);
                textViewBluetooth.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                setNetworkDevices();
                break;
            case R.id.radioUSB:
                portType = BXLConfigLoader.DEVICE_BUS_USB;
                layoutIPAddress.setVisibility(View.GONE);
                textViewBluetooth.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP)
            listView.requestDisallowInterceptTouchEvent(false);
        else listView.requestDisallowInterceptTouchEvent(true);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String device = ((TextView) view).getText().toString();
        if(portType == BXLConfigLoader.DEVICE_BUS_WIFI)
        {
            editTextIPAddress.setText(device);
            address = device;
        }
        else {
            address = device.substring(device.indexOf(DEVICE_ADDRESS_START) + DEVICE_ADDRESS_START.length(), device.indexOf(DEVICE_ADDRESS_END));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGuardarConfiguracion:
                mHandler.obtainMessage(0).sendToTarget();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (portType == BXLConfigLoader.DEVICE_BUS_WIFI) {
                            address = editTextIPAddress.getText().toString();
                        }

                        //if (MainActivity.getPrinterInstance().printerOpen(portType, logicalName, address, checkBoxAsyncMode.isChecked())) {
                        Log.i("DATOS_BT", "|"+portType+"|"+logicalName+"|"+address);
                        if (getPrinterInstance().printerOpen(portType, logicalName, address, checkBoxAsyncMode.isChecked())) {
                            finish();
                        } else {
                            mHandler.obtainMessage(1, 0, 0, "Fail to printer open!!").sendToTarget();
                        }
                    }
                }).start();

                break;
            case R.id.fab_guardar:
                if(radioBixolon.isChecked()){
                    Log.i("DATOS_BT", "|"+portType+"|"+logicalName+"|"+address);
                    new TareaAbrirConexionBixolon().execute(String.valueOf(portType), logicalName, address);
                    /*if (getPrinterInstance().printerOpen(portType, logicalName, address, checkBoxAsyncMode.isChecked())) {
                        saveConfigPrint("BIXOLON", ""+address);
                        finish();
                    } else {
                        mHandler.obtainMessage(1, 0, 0, "Error al intentar establecer conexión con la impresora seleccionada.!!").sendToTarget();
                    }*/

                }else if(radioZebra.isChecked()){
                    //address
                    MiAplicacionTareo.ESTADO_IMPRESORA = true;
                    //saveConfigPrint("ZEBRA", "CC:78:AB:9F:83:FC");//Sólo tien una impresora ZEBRA
                    saveConfigPrint("ZEBRA", ""+address);//Sólo tien una impresora ZEBRA
                    Toast.makeText(ConexionImpresora.this, "CONFIGURACIÓN ESTABLECIDA - ZEBRA | "+address, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private class TareaAbrirConexionBixolon extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ConexionImpresora.this);
            pDialog.setTitle("Estableciendo conexión con impresora...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String puerto = params[0];
            String nombre = params[1];
            String direccion_mac = params[2];
            return getPrinterInstance().printerOpen(Integer.parseInt(puerto), nombre, direccion_mac, checkBoxAsyncMode.isChecked());
        }

        protected void onPostExecute(Boolean rpta) {
            pDialog.dismiss();
            if(rpta){
                MiAplicacionTareo.ESTADO_IMPRESORA = true;
                saveConfigPrint("BIXOLON", ""+address);
                finish();
            }else{
                MiAplicacionTareo.ESTADO_IMPRESORA = false;
                mHandler.obtainMessage(1, 0, 0, "Error al intentar establecer conexión con la impresora seleccionada.!!").sendToTarget();
            }
        }
    }



    public static BixolonPrinter getPrinterInstance()
    {
        return bxlPrinter;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getApplicationContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public final Handler mHandler = new Handler(new Handler.Callback() {
        @SuppressWarnings("unchecked")
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mProgressLarge.setVisibility(ProgressBar.VISIBLE);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    break;
                case 1:
                    String data = (String) msg.obj;
                    if (data != null && data.length() > 0) {
                        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                    }
                    mProgressLarge.setVisibility(ProgressBar.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    break;
            }
            return false;
        }
    });

    public void saveConfigPrint(String PRINT, String MAC_BT){
        Toast.makeText(ConexionImpresora.this, "CONFIGURACIÓN ESTABLECIDA - "+PRINT, Toast.LENGTH_LONG).show();
        SharedPreferences prefs = getSharedPreferences("preferenciasIMPRESORA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("IMPRESORA", ""+PRINT);
        editor.putString("MAC_IMPRESORA", ""+MAC_BT);
        editor.commit();
        finish();
    }

}
