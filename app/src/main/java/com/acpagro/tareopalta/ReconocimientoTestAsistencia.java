package com.acpagro.tareopalta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acpagro.tareopalta.adapters.AdapterListaTrabajadoresAsistenciaTareo;
import com.acpagro.tareopalta.modelo.Administrador;
import com.acpagro.tareopalta.modelo.Asistencia;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.Reconocimiento;
import com.acpagro.tareopalta.modelo.SimilarityClassifier;
import com.acpagro.tareopalta.modelo.Usuario;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class ReconocimientoTestAsistencia extends AppCompatActivity {
    ListView lv_trabajadores_asistencia;
    RelativeLayout rl_asistencia;
    private String IDTAREO, CONSUMIDOR, ACTIVIDAD, LABOR, FECHAYHORA, IDCONSUMIDOR;
    private String actividad_labor;
    private String DNI_DETECTADO="", MODULO_SELECCIONADO = "";
    public static TextView txt_conteo, tv_distancia;
    AdapterListaTrabajadoresAsistenciaTareo adapter;

    private String nombre="";
    private int identificado=0;
    private boolean estadoReconocer = true;
    FaceDetector detector;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    //ImageView face_preview;
    Interpreter tfLite;
    CameraSelector cameraSelector;
    boolean start=true,flipX=false;
    Context context=ReconocimientoTestAsistencia.this;
    int cam_face=CameraSelector.LENS_FACING_BACK; //Default Back Camera
    //int cam_face=CameraSelector.LENS_FACING_FRONT; //Default Front Camera

    int[] intValues;
    int inputSize=112;  //Input size for model
    boolean isModelQuantized=false;
    float[][] embeedings;
    float IMAGE_MEAN = 128.0f;
    float IMAGE_STD = 128.0f;
    int OUTPUT_SIZE=192; //Output size of model
    private static int SELECT_PICTURE = 1;
    ProcessCameraProvider cameraProvider;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    String modelFile="mobile_face_net.tflite"; //model name//face_detection_back, mobile_face_net
    private HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>(); //saved Faces


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconocimiento_test_asistencia);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_cerrar_blanco);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Asistencia TestRF");
        lv_trabajadores_asistencia = findViewById(R.id.lv_trabajadores_asistencia);
        rl_asistencia = findViewById(R.id.rl_asistencia);
        tv_distancia = findViewById(R.id.tv_distancia);
        txt_conteo = findViewById(R.id.txt_conteo);
        adapter = new AdapterListaTrabajadoresAsistenciaTareo(ReconocimientoTestAsistencia.this);
        lv_trabajadores_asistencia.setAdapter(adapter);//Se llena el ListView

        Bundle parametros = this.getIntent().getExtras();
        if(parametros!= null){
            IDTAREO = parametros.getString("p_id_tareo");//IDTAREO
            CONSUMIDOR = parametros.getString("p_consumidor");//CONSUMIDOR
            IDCONSUMIDOR = parametros.getString("p_idconsumidor");//IDCONSUMIDOR
            ACTIVIDAD = parametros.getString("p_actividad");//ACT
            LABOR = parametros.getString("p_labor");//LABOR
            FECHAYHORA = parametros.getString("p_fechaYhora");//FYH
            actividad_labor = parametros.getString("p_idactividad")+"-"+parametros.getString("p_idlabor");//p_idactividad
        }else{
            finish();
            Toast.makeText(this, "Inténtalo nuevamente e ingresa al Tareo para añadir asistencia. ", Toast.LENGTH_SHORT).show();
        }

        //registered=readFromSP();//12/11/2021
        //new TareaCargarRostrosGuardados().execute();//JSON Preferencias
        new TareaCargarRostrosGuardadosSQLite().execute();//SQLite Sincronizados

        //Camera Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            }
        }

        //Load model
        try {
            /*Interpreter.Options obj = new Interpreter.Options().setUseNNAPI(true).setNumThreads(4);
            tfLite = new Interpreter(loadModelFile(ReconocimientoTestAsistencia.this, modelFile), obj);*/
            tfLite=new Interpreter(loadModelFile(ReconocimientoTestAsistencia.this, modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Initialize Face Detector
        FaceDetectorOptions highAccuracyOpts = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                .build();
        detector = FaceDetection.getClient(highAccuracyOpts);

        cameraBind();

        cargarListaTrabajadores(IDTAREO);

    }

    //Para volver atras
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                finish();
                return true;
            case R.id.action_rotar_camara:
                if (cam_face==CameraSelector.LENS_FACING_BACK) {
                    cam_face = CameraSelector.LENS_FACING_FRONT;
                    flipX=true;
                }
                else {
                    cam_face = CameraSelector.LENS_FACING_BACK;
                    flipX=false;
                }
                cameraProvider.unbindAll();
                cameraBind();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_asistencia_test, menu);
        return true;
    }

    public void grabar(String nombre){
        String dni = nombre.trim().replace("-", "");
        if(dni.length() == 8 && MiAplicacionTareo.validarNumero(dni)){
            if(IDTAREO == null || IDTAREO.trim().length()==0 || IDTAREO.trim().equalsIgnoreCase("-")){
                finish();
                Toast.makeText(ReconocimientoTestAsistencia.this, "No se cargó correctamente el CÓDIGO DE TAREO, por favor ingresa nuevamente al TAREO.", Toast.LENGTH_SHORT).show();
            }else{
                guardarAsistenciaSQLite(IDTAREO, dni);
            }
            //cargar_preferencia_idtareo
            //guardarAsistenciaSQLite(cargar_preferencia_idtareo(), dni);
        }else{
            DNI_DETECTADO = "";
            Toast.makeText(ReconocimientoTestAsistencia.this, "ROSTRO NO RECONOCIBLE", Toast.LENGTH_SHORT).show();
            cambiarColorBackgroundError();
        }
    }

    private void guardarAsistenciaSQLite(String IDTAREO, String DNI){
        Asistencia objA = new Asistencia();
        Long r = objA.agregarAsistenciaSQLite(IDTAREO, DNI);
        Log.i("LONG_ASISTENCIA", ""+r);
        if(r == 500){//OBSERVADO
            //Toast.makeText(PantallaAsistenciaPersonal.this, "OBSERVADO, NO SE ADMITE PERSONAL OBSERVADO!! - " + DNI, Toast.LENGTH_SHORT).show();
            dialogObservado(DNI);
            cambiarColorBackgroundError();

            Administrador objAdm = new Administrador();
            objAdm.cargarListaNumerosAdministrador();
            //enviarCorreoObservado(DNI, Asistencia.NOMBREOBSERVADO, MiApplicacionACP.NOMBRES, "observado");
            if(Asistencia.MOTIVO_OBSERVADO.trim().length() != 0){
                enviarCorreoObservado(DNI, Asistencia.NOMBREOBSERVADO, MiAplicacionTareo.NOMBRES, "observado");
                for(int i=0; i<Administrador.listaNumerosAdministradores.size(); i++){
                    new TareaEnviarSMS().execute(Administrador.listaNumerosAdministradores.get(i).getNumero(), "(OBS. POR "+ Asistencia.MOTIVO_OBSERVADO+") "+ Asistencia.NOMBREOBSERVADO + " - "+DNI+", AUX: " + MiAplicacionTareo.NOMBRES);
                }
                //Envia
            }
        }else if(r == 400){//CAMBIO DE MODULO
            dialogCambio(DNI);
            cambiarColorBackgroundExito();

            Administrador objAdm = new Administrador();
            objAdm.cargarListaNumerosAdministrador();
            enviarCorreoObservado(DNI, Asistencia.NOMBREOBSERVADO, MiAplicacionTareo.NOMBRES, "cambio");
            for(int i=0; i<Administrador.listaNumerosAdministradores.size(); i++){
                new TareaEnviarSMS().execute(Administrador.listaNumerosAdministradores.get(i).getNumero(), "(CAMBIO DE MODULO) "+ Asistencia.NOMBREOBSERVADO + " - "+DNI+" NO ESTA AUTORIZADO PARA TRABAJAR EN EL "+ MODULO_SELECCIONADO +", ENVIADO POR: " + MiAplicacionTareo.NOMBRES);
            }
            cargarListaTrabajadores(IDTAREO);
        }else if(r == -1){//es que no se grabo!
            cambiarColorBackgroundError();
        }else if(r == 600){//NO ENCONTRADO EN LA LISTA DE PERSONAL SINCRONIZADA (PUEDE ESTAR CESADO, NUEVO EN FICHAS O NO ENCONTRADO, POR ENDE SE DEBE COMPROBAR EN LÍNEA
            //getConsultaPersonalNoEncontrado(edt_dni.getText().toString().trim());
            Toast.makeText(ReconocimientoTestAsistencia.this, "CESADO / OTRA PLANILLA (ERG)", Toast.LENGTH_SHORT).show();
        }else{//Se grabo correctamente!
            cambiarColorBackgroundExito();
            cargarListaTrabajadores(IDTAREO);
        }
    }

    public void cargarListaTrabajadores(String idTareo){
        Asistencia objA = new Asistencia();
        objA.obtenerListaTrabajadoresAsistenciaPorTareo(idTareo, "4", "DESC");
        txt_conteo.setText(""+Asistencia.listaAsistenciaSQLite.size());
        adapter.notifyDataSetChanged();
    }

    public void cambiarColorBackgroundError(){
        android.os.Vibrator v = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
        long [] patron = {0, 300, 300, 300};
        v.vibrate(patron, -1);

        rl_asistencia.setBackgroundColor(Color.parseColor("#ff0000"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_asistencia.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }, 700);
    }

    public void cambiarColorBackgroundExito(){
        android.os.Vibrator v = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(400);

        rl_asistencia.setBackgroundColor(Color.parseColor("#6dff4d"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_asistencia.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }, 700);
    }

    public void dialogObservado(String DNI){
        final PrettyDialog pDialog = new PrettyDialog(this);
        pDialog.setCancelable(false);
        pDialog
                .setIconTint(R.color.pdlg_color_red)
                .setTitle("OBSERVADO")
                .setMessage("Personal con DNI "+ DNI +" está en la lista de OBSERVADOS (" + Asistencia.MOTIVO_OBSERVADO + ")")
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

    public void dialogCambio(String DNI){
        final PrettyDialog pDialog = new PrettyDialog(this);
        pDialog.setCancelable(false);
        pDialog
                .setIconTint(R.color.pdlg_color_red)
                .setTitle("CAMBIO NO AUTORIZADO")
                .setMessage("Personal con DNI "+ DNI +" no está autorizado para trabajar en el " +MODULO_SELECCIONADO)
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

    private class TareaEnviarSMS extends AsyncTask<String, Void, Boolean> {
        //ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String numero = params[0];
            String mensaje = params[1];
            try {
                SmsManager sms;
                sms = SmsManager.getDefault();
                sms.sendTextMessage(numero, null, mensaje, null, null);
                Log.i("-------------", "-------------");
                Log.i("SMS:", mensaje);
                Log.i("SMS Enviado", ""+ numero);
                return true;
            } catch (Exception e) {
                Log.i("-------------", "-------------");
                Log.i("SMS:", mensaje);
                Log.i("SMS NO Enviado", ""+ numero);
                return false;
                //e.printStackTrace();
            }
        }

        protected void onPostExecute(Boolean result) {
            if(result){
                //enviado
            }else{
                //NOENVIADO
            }
        }
    }

    private void enviarCorreoObservado(final String dniObservado, final String nombreObservado, final String nombreCaporal, final String motivo){
        RequestQueue request;
        request = Volley.newRequestQueue(this);
        String url = MiAplicacionTareo.URL_SERVICE + "enviarCorreoArandano";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                Log.i("Response", response);
                try {
                    JSONObject json = new JSONObject(response);
                    String resultado = json.getString("resultado");
                    Log.i("RESULTADO ENVIO: ", resultado);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("ERROR-VOLLEY", "ERROR");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("dniObservado", dniObservado);
                params.put("nombreObservado", nombreObservado);
                params.put("nombreCaporal", nombreCaporal);
                params.put("motivo", motivo);
                return params;
            }
        };
        request.add(postRequest);
    }

    public String cargar_preferencia_idtareo(){
        SharedPreferences prefs = getSharedPreferences("PreferenciasTareoACP2", Context.MODE_PRIVATE);
        String idtareo  = prefs.getString("IDTAREO", "-");
        return idtareo;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void cameraBind() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        previewView=findViewById(R.id.previewView);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this in Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        cameraSelector = new CameraSelector.Builder().requireLensFacing(cam_face).build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(640, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //Latest frame is shown
                        .build();

        Executor executor = Executors.newSingleThreadExecutor();
        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {

                InputImage image = null;


                @SuppressLint("UnsafeExperimentalUsageError")
                // Camera Feed-->Analyzer-->ImageProxy-->mediaImage-->InputImage(needed for ML kit face detection)

                Image mediaImage = imageProxy.getImage();

                if (mediaImage != null) {
                    image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                    System.out.println("Rotation "+imageProxy.getImageInfo().getRotationDegrees());
                }

                System.out.println("ANALYSIS");

                //Process acquired image to detect faces
                Task<List<Face>> result =
                        detector.process(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<Face>>() {
                                            @Override
                                            public void onSuccess(List<Face> faces) {
                                                if(faces.size()!=0) {
                                                    Face face = faces.get(0); //Get first face from detected faces
                                                    System.out.println(face);

                                                    //mediaImage to Bitmap
                                                    Bitmap frame_bmp = toBitmap(mediaImage);

                                                    int rot = imageProxy.getImageInfo().getRotationDegrees();

                                                    //Adjust orientation of Face
                                                    Bitmap frame_bmp1 = rotateBitmap(frame_bmp, rot, flipX, false);

                                                    //Get bounding box of face
                                                    RectF boundingBox = new RectF(face.getBoundingBox());

                                                    //Crop out bounding box from whole Bitmap(image)
                                                    Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);


                                                    //Scale the acquired Face to 112*112 which is required input for model
                                                    Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);

                                                    if(start)
                                                        //recognizeImage(scaled); //Send scaled bitmap to create face embeddings.
                                                        recognizeImage_2(scaled); //Send scaled bitmap to create face embeddings.
                                                    System.out.println(boundingBox);
                                                    try {
                                                        Thread.sleep(100);  //Camera preview refreshed every 100 millisec(adjust as required)
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        })
                                .addOnCompleteListener(new OnCompleteListener<List<Face>>() {
                                    @Override
                                    public void onComplete(@NonNull Task<List<Face>> task) {

                                        imageProxy.close(); //v.important to acquire next frame for analysis
                                    }
                                });


            }
        });

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);
    }

    public void recognizeImage(final Bitmap bitmap) {
        // set Face to Preview
        //face_preview.setImageBitmap(bitmap);

        //Create ByteBuffer to store normalized image
        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);
        imgData.order(ByteOrder.nativeOrder());
        intValues = new int[inputSize * inputSize];

        //get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        imgData.rewind();

        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                int pixelValue = intValues[i * inputSize + j];
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((byte) ((pixelValue >> 16) & 0xFF));
                    imgData.put((byte) ((pixelValue >> 8) & 0xFF));
                    imgData.put((byte) (pixelValue & 0xFF));
                } else { // Float model
                    imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);

                }
            }
        }
        //imgData is input to our model
        Object[] inputArray = {imgData};

        Map<Integer, Object> outputMap = new HashMap<>();

        embeedings = new float[1][OUTPUT_SIZE]; //output of model will be stored in this variable
        outputMap.put(0, embeedings);
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap); //Run model

        float distance = Float.MAX_VALUE;
        String id = "0";
        String label = "?";

        //Compare new face with saved Faces.
        if (registered.size() > 0) {
            final Pair<String, Float> nearest = findNearest(embeedings[0]);//Find closest matching face
            if (nearest != null) {
                final String name = nearest.first;
                label = name;
                distance = nearest.second;
                //if(distance<1.000f) { //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                if(distance<1.000f) { //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                    //if(distance<0.700f) { //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                    //reco_name.setText(name + " D(" + distance+")");
                    tv_distancia.setText(name+" D("+distance+")");
                    if(!nombre.equalsIgnoreCase(name)){
                        identificado++;

                        if(identificado==1){
                            //Hablar
                            Log.i("NOMBRE", "|"+name);
                            if(estadoReconocer){
                                //MiAplicacionTareo.leer_voz("Encontrado "+name);
                                MiAplicacionTareo.leer_voz("Encontrado");
                                grabar(name);
                            }
                        }
                    }else{
                        identificado=0;
                    }
                }else {
                    identificado=0;
                    //reco_name.setText("Desconocido D("+distance+")");
                    tv_distancia.setText("DESC. D("+distance+")");
                }
                System.out.println("nearest: " + name + " - distance: " + distance);


            }
        }


    }

    public void recognizeImage_2(final Bitmap bitmap) {//Prueba Con mascarilla
        // set Face to Preview
        //face_preview.setImageBitmap(bitmap);

        //Create ByteBuffer to store normalized image
        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);
        imgData.order(ByteOrder.nativeOrder());
        intValues = new int[inputSize * inputSize];

        //get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        imgData.rewind();

        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                int pixelValue = intValues[i * inputSize + j];
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((byte) ((pixelValue >> 16) & 0xFF));
                    imgData.put((byte) ((pixelValue >> 8) & 0xFF));
                    imgData.put((byte) (pixelValue & 0xFF));
                } else { // Float model
                    imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);

                }
            }
        }
        //imgData is input to our model
        Object[] inputArray = {imgData};

        Map<Integer, Object> outputMap = new HashMap<>();

        embeedings = new float[1][OUTPUT_SIZE]; //output of model will be stored in this variable
        outputMap.put(0, embeedings);
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap); //Run model

        float distance = Float.MAX_VALUE;
        String id = "0";
        String label = "?";

        //Compare new face with saved Faces.
        if (registered.size() > 0) {

            final Pair<String, Float> nearest = findNearest(embeedings[0]);//Find closest matching face

            if (nearest != null) {

                final String name = nearest.first;
                label = name;
                distance = nearest.second;
                //if(distance<1.000f) { //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                if(distance<MiAplicacionTareo.distancia_minima_reconocimiento) { //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                    //if(distance<0.700f) { //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                    //reco_name.setText(name + " D(" + distance+")");
                    tv_distancia.setText(name+" D("+distance+")");

                    if(!nombre.equalsIgnoreCase(name)){
                        identificado++;
                        if(identificado == 1){
                            Log.i("NOMBRE", "|"+name);
                            if(estadoReconocer){
                                MiAplicacionTareo.leer_voz("Encontrado");//TEST mancajima
                                /*if(name.trim().equalsIgnoreCase("46978207")){
                                    MiAplicacionTareo.leer_voz("Maricona detectada");
                                }else{
                                    MiAplicacionTareo.leer_voz("Encontrado test");
                                }*/
                                grabar(name);
                                //identificado = 0;
                            }
                            nombre = name;
                        }

                    }else{
                         identificado = 0;
                    }


                    /*if(!nombre.equalsIgnoreCase(name)){
                        identificado++;
                        if(identificado==1){
                            //Hablar
                            Log.i("NOMBRE", "|"+name);
                            if(estadoReconocer){
                                MiAplicacionTareo.leer_voz("Encontrado");
                                grabar(name);
                            }
                        }
                    }else{
                        identificado=0;
                    }*/


                }else {
                    identificado=0;
                    //reco_name.setText("Desconocido D("+distance+")");
                    tv_distancia.setText("DESC. D("+distance+")");
                }
                System.out.println("Más cercano: [" + name + "] - distancia: [" + distance+"]");
            }else{
                identificado=0;
            }
        }


    }

    //Compare Faces by distance between face embeddings
    private Pair<String, Float> findNearest(float[] emb) {
        Pair<String, Float> ret = null;
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet()) {
            final String name = entry.getKey();
            final float[] knownEmb = ((float[][]) entry.getValue().getExtra())[0];

            float distance = 0;
            for (int i = 0; i < emb.length; i++) {
                float diff = emb[i] - knownEmb[i];
                distance += diff*diff;
            }
            distance = (float) Math.sqrt(distance);
            if (ret == null || distance < ret.second) {
                ret = new Pair<>(name, distance);
            }
        }
        return ret;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private static Bitmap getCropBitmapByCPU(Bitmap source, RectF cropRectF) {
        Bitmap resultBitmap = Bitmap.createBitmap((int) cropRectF.width(), (int) cropRectF.height(), Bitmap.Config.ARGB_8888);
        Canvas cavas = new Canvas(resultBitmap);

        // draw background
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.WHITE);
        cavas.drawRect(//from  w w  w. ja v  a  2s. c  om
                new RectF(0, 0, cropRectF.width(), cropRectF.height()),
                paint);

        Matrix matrix = new Matrix();
        matrix.postTranslate(-cropRectF.left, -cropRectF.top);

        cavas.drawBitmap(source, matrix, paint);
        if (source != null && !source.isRecycled()) {
            source.recycle();
        }
        return resultBitmap;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int rotationDegrees, boolean flipX, boolean flipY) {
        Matrix matrix = new Matrix();

        // Rotate the image back to straight.
        matrix.postRotate(rotationDegrees);

        // Mirror the image along the X or Y axis.
        matrix.postScale(flipX ? -1.0f : 1.0f, flipY ? -1.0f : 1.0f);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // Recycle the old bitmap if it has changed.
        if (rotatedBitmap != bitmap) {
            bitmap.recycle();
        }
        return rotatedBitmap;
    }

    //IMPORTANT. If conversion not done ,the toBitmap conversion does not work on some devices.
    private static byte[] YUV_420_888toNV21(Image image) {

        int width = image.getWidth();
        int height = image.getHeight();
        int ySize = width*height;
        int uvSize = width*height/4;

        byte[] nv21 = new byte[ySize + uvSize*2];

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); // U
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); // V

        int rowStride = image.getPlanes()[0].getRowStride();
        assert(image.getPlanes()[0].getPixelStride() == 1);

        int pos = 0;

        if (rowStride == width) { // likely
            yBuffer.get(nv21, 0, ySize);
            pos += ySize;
        }
        else {
            long yBufferPos = -rowStride; // not an actual position
            for (; pos<ySize; pos+=width) {
                yBufferPos += rowStride;
                yBuffer.position((int) yBufferPos);
                yBuffer.get(nv21, pos, width);
            }
        }

        rowStride = image.getPlanes()[2].getRowStride();
        int pixelStride = image.getPlanes()[2].getPixelStride();

        assert(rowStride == image.getPlanes()[1].getRowStride());
        assert(pixelStride == image.getPlanes()[1].getPixelStride());

        if (pixelStride == 2 && rowStride == width && uBuffer.get(0) == vBuffer.get(1)) {
            // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
            byte savePixel = vBuffer.get(1);
            try {
                vBuffer.put(1, (byte)~savePixel);
                if (uBuffer.get(0) == (byte)~savePixel) {
                    vBuffer.put(1, savePixel);
                    vBuffer.position(0);
                    uBuffer.position(0);
                    vBuffer.get(nv21, ySize, 1);
                    uBuffer.get(nv21, ySize + 1, uBuffer.remaining());

                    return nv21; // shortcut
                }
            }
            catch (ReadOnlyBufferException ex) {
                // unfortunately, we cannot check if vBuffer and uBuffer overlap
            }

            // unfortunately, the check failed. We must save U and V pixel by pixel
            vBuffer.put(1, savePixel);
        }

        // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
        // but performance gain would be less significant

        for (int row=0; row<height/2; row++) {
            for (int col=0; col<width/2; col++) {
                int vuPos = col*pixelStride + row*rowStride;
                nv21[pos++] = vBuffer.get(vuPos);
                nv21[pos++] = uBuffer.get(vuPos);
            }
        }

        return nv21;
    }

    private Bitmap toBitmap(Image image) {
        byte[] nv21=YUV_420_888toNV21(image);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        //System.out.println("bytes"+ Arrays.toString(imageBytes));

        //System.out.println("FORMAT"+image.getFormat());

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    //Save Faces to Shared Preferences.Conversion of Recognition objects to json string
    private void insertToSP(HashMap<String, SimilarityClassifier.Recognition> jsonMap,boolean clear) {
        if(clear)
            jsonMap.clear();
        else
            jsonMap.putAll(readFromSP());
        String jsonString = new Gson().toJson(jsonMap);
//        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : jsonMap.entrySet())
//        {
//            System.out.println("Entry Input "+entry.getKey()+" "+  entry.getValue().getExtra());
//        }
        SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("map", jsonString);
        //System.out.println("Input josn"+jsonString.toString());
        editor.apply();
        Toast.makeText(context, "Recognitions Saved", Toast.LENGTH_SHORT).show();
    }

    //Load Faces from Shared Preferences.Json String to Recognition object
    private HashMap<String,SimilarityClassifier.Recognition> readFromSP(){
        SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
        String defValue = new Gson().toJson(new HashMap<String, SimilarityClassifier.Recognition>());
        String json=sharedPreferences.getString("map",defValue);
        System.out.println("Output json"+json.toString());
        TypeToken<HashMap<String,SimilarityClassifier.Recognition>> token = new TypeToken<HashMap<String,SimilarityClassifier.Recognition>>() {};
        HashMap<String,SimilarityClassifier.Recognition> retrievedMap=new Gson().fromJson(json,token.getType());
        System.out.println("Output map"+retrievedMap.toString());

        //During type conversion and save/load procedure,format changes(eg float converted to double).
        //So embeddings need to be extracted from it in required format(eg.double to float).
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : retrievedMap.entrySet())
        {
            float[][] output=new float[1][OUTPUT_SIZE];
            ArrayList arrayList= (ArrayList) entry.getValue().getExtra();
            arrayList = (ArrayList) arrayList.get(0);
            for (int counter = 0; counter < arrayList.size(); counter++) {
                output[0][counter]= ((Double) arrayList.get(counter)).floatValue();
            }
            entry.getValue().setExtra(output);

            System.out.println("Entry output "+entry.getKey()+" "+entry.getValue().getExtra() );

        }
//        System.out.println("OUTPUT"+ Arrays.deepToString(outut));
        Toast.makeText(context, "Reconocimientos cargados", Toast.LENGTH_SHORT).show();
        return retrievedMap;
    }

    private class TareaCargarRostrosGuardados extends AsyncTask<String, Void, HashMap<String,SimilarityClassifier.Recognition> > {//JSON Preferencia Map
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ReconocimientoTestAsistencia.this);
            pDialog.setMessage("Cargando reconocimientos..");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected HashMap<String,SimilarityClassifier.Recognition> doInBackground(String... params) {
            SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
            String defValue = new Gson().toJson(new HashMap<String, SimilarityClassifier.Recognition>());
            String json=sharedPreferences.getString("map",defValue);
            System.out.println("Output json"+json.toString());
            TypeToken<HashMap<String,SimilarityClassifier.Recognition>> token = new TypeToken<HashMap<String,SimilarityClassifier.Recognition>>() {};
            HashMap<String,SimilarityClassifier.Recognition> retrievedMap=new Gson().fromJson(json,token.getType());
            System.out.println("Output map"+retrievedMap.toString());
            for (Map.Entry<String, SimilarityClassifier.Recognition> entry : retrievedMap.entrySet())
            {
                float[][] output=new float[1][OUTPUT_SIZE];
                ArrayList arrayList= (ArrayList) entry.getValue().getExtra();
                arrayList = (ArrayList) arrayList.get(0);
                for (int counter = 0; counter < arrayList.size(); counter++) {
                    output[0][counter]= ((Double) arrayList.get(counter)).floatValue();
                }
                entry.getValue().setExtra(output);
                System.out.println("Entry output "+entry.getKey()+" "+entry.getValue().getExtra() );
            }
            return retrievedMap;
        }

        protected void onPostExecute(HashMap<String,SimilarityClassifier.Recognition> result){
            pDialog.dismiss();
            registered = result;
            Toast.makeText(context, "Reconocimientos cargados", Toast.LENGTH_SHORT).show();
        }
    }

    private class TareaCargarRostrosGuardadosSQLite extends AsyncTask<String, Void, HashMap<String,SimilarityClassifier.Recognition> > {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ReconocimientoTestAsistencia.this);
            pDialog.setMessage("Cargando reconocimientos SQLite...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected HashMap<String,SimilarityClassifier.Recognition> doInBackground(String... params) {
            Reconocimiento obj = new Reconocimiento();
            obj.cargarReconocimientosSQLite();
            return Reconocimiento.reconocimientosSQLite;
        }

        protected void onPostExecute(HashMap<String,SimilarityClassifier.Recognition> result){
            pDialog.dismiss();
            registered = result;
            Toast.makeText(context, "Reconocimientos SQLite cargados correctamente.", Toast.LENGTH_SHORT).show();
        }
    }

}