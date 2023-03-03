package com.acpagro.tareopalta;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acpagro.tareopalta.datos.Http;
import com.acpagro.tareopalta.datos.HttpPostValues;
import com.acpagro.tareopalta.modelo.FileImageRF;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.modelo.Reconocimiento;
import com.acpagro.tareopalta.modelo.SimilarityClassifier;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ReconocimientoTest extends AppCompatActivity {
    private String nombre="";
    private int identificado=0;
    private Uri selectedImageUri;//USADO EN ASYNTASK
    private Bitmap scaled_bm;
    private ProgressDialog pDialog;
    private ArrayList<FileImageRF> itemFiles = new ArrayList<>();
    private int conteo = 0;

    private boolean estadoReconocer = true;
    FaceDetector detector;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    ImageView face_preview;
    Interpreter tfLite;
    TextView reco_name,preview_info;
    ImageButton add_face;
    CameraSelector cameraSelector;
    boolean start=true,flipX=false;
    Context context=ReconocimientoTest.this;
    int cam_face=CameraSelector.LENS_FACING_BACK; //Default Back Camera

    int[] intValues;
    int inputSize=112;//Input size for model
    boolean isModelQuantized=false;
    float[][] embeedings;
    float IMAGE_MEAN = 128.0f;
    float IMAGE_STD = 128.0f;
    int OUTPUT_SIZE=192; //Output size of model
    private static int SELECT_PICTURE = 1;
    ProcessCameraProvider cameraProvider;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    private static String APP_DIRECTORY = "fotos_reconocimiento/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "fotos_reconocimiento";
    String mPath;

    String modelFile="mobile_face_net.tflite"; //model name
    private HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>(); //saved Faces
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconocimiento_test);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_cerrar_blanco);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Agregar Rostro");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //registered=readFromSP(); //Load saved faces from memory when app starts
        face_preview =findViewById(R.id.imageView);
        reco_name =findViewById(R.id.textView);
        preview_info =findViewById(R.id.textView2);
        add_face=findViewById(R.id.add_face);
        add_face.setVisibility(View.INVISIBLE);

        face_preview.setVisibility(View.INVISIBLE);
        preview_info.setText("Reconocimiento de Rostro");

        new TareaCargarRostrosGuardadosSQLite().execute();
        //Camera Permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        add_face.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFace();
            }
        }));


        //Load model
        try {
            tfLite=new Interpreter(loadModelFile(ReconocimientoTest.this, modelFile));
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

        recorrerListaArchivos();
    }

    private void addFace() {//Agregar Rostro
        start=false;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Ingresa DNI");

            // Set up the input
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        //input.setInputType(InputType.TYPE_CLASS_TEXT );
        builder.setView(input);

            // Set up the buttons
        builder.setPositiveButton("AGREGAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(context, input.getText().toString(), Toast.LENGTH_SHORT).show();

                //Create and Initialize new object with Face embeddings and Name.
                SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition("0", "", -1f);
                result.setExtra(embeedings);
                Log.i("EXTRA_AGREGAR", "|"+embeedings.toString());
                Log.i("EXTRA_AGREGAR", "|"+result.getExtra().toString());

                /*String jsonString = new Gson().toJson(result);
                Log.i("EXTRA_AGREGAR_JSON", "|"+jsonString);*/

                registered.put( input.getText().toString().trim(),result);
                addFaceSQLitePerson(input.getText().toString().trim(), new Gson().toJson(result));//Agregar a SQLite
                start=true;
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                start=true;
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void addFaceSQLitePerson(String idcodigogeneral_person, String json_result){//mancajima 07/02/2022
        try {
            JSONObject obj = new JSONObject(json_result);
            /*Log.i("SAVE_DNI", ""+idcodigogeneral_person);
            Log.i("SAVE_KEY_DIST", ""+obj.getString("distance"));
            Log.i("SAVE_KEY_EX", ""+obj.getString("extra"));
            Log.i("SAVE_KEY_ID", ""+obj.getString("id"));
            Log.i("SAVE_KEY_TIT", ""+obj.getString("title"));*/
            Reconocimiento objR = new Reconocimiento();
            long rpta = objR.agregarReconocimiento(idcodigogeneral_person, obj.getString("distance"), obj.getString("extra"), obj.getString("id"), obj.getString("title"));
            Toast.makeText(ReconocimientoTest.this, "RESPUESTA: "+rpta, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            String error = e.getMessage();
            if(error.isEmpty()){
                error = "No encontrado, NULL";
            }
            Toast.makeText(ReconocimientoTest.this, "Error: "+error, Toast.LENGTH_LONG).show();
        }
    }

    private void addFacePruebaAlgoritmo(String nombre) {//Agregar Rostro
        SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition("0", "", -1f);
        result.setExtra(embeedings);
        /*Log.i("DATOS_ID", "|"+result.getId());
        Log.i("DATOS_TITLE", "|"+result.getTitle());
        Log.i("DATOS_DIST", "|"+result.getDistance());
        Log.i("DATOS_NAME", "|"+nombre);*/
        registered.put( nombre, result);
        addFaceSQLitePerson(nombre.trim(), new Gson().toJson(result));//Agregar a SQLite
        start=true;
    }

    private class TareaCargarRostrosGuardadosSQLite extends AsyncTask<String, Void, HashMap<String,SimilarityClassifier.Recognition> > {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ReconocimientoTest.this);
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

    private  void clearnameList() {
        AlertDialog.Builder builder =new AlertDialog.Builder(context);
        builder.setTitle("¿Quieres borrar todos los reconocimientos?");
        builder.setPositiveButton("Eliminar todo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registered.clear();
                Toast.makeText(context, "Reconocimientos eliminados", Toast.LENGTH_SHORT).show();
            }
        });
        insertToSP(registered,true);
        builder.setNegativeButton("Cancelar",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updatenameListview() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(registered.isEmpty()) {
            builder.setTitle("¡No se agregaron rostros!");
            builder.setPositiveButton("OK",null);
        }
        else{
            builder.setTitle("Seleccione Reconocimiento para eliminar:");

            // add a checkbox list
            String[] names= new String[registered.size()];
            boolean[] checkedItems = new boolean[registered.size()];
            int i=0;
            for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet()) {
                //System.out.println("NAME"+entry.getKey());
                names[i]=entry.getKey();
                checkedItems[i]=false;
                i=i+1;
            }

            builder.setMultiChoiceItems(names, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    // user checked or unchecked a box
                    //Toast.makeText(MainActivity.this, names[which], Toast.LENGTH_SHORT).show();
                    checkedItems[which]=isChecked;
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // System.out.println("status:"+ Arrays.toString(checkedItems));
                    for(int i=0;i<checkedItems.length;i++)
                    {
                        //System.out.println("status:"+checkedItems[i]);
                        if(checkedItems[i])
                        {
                            Toast.makeText(ReconocimientoTest.this, names[i], Toast.LENGTH_SHORT).show();
                            registered.remove(names[i]);
                        }

                    }
                    Toast.makeText(context, "Reconocimientos actualizados", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancelar", null);

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void displaynameListview() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // System.out.println("Registered"+registered);
        if(registered.isEmpty())
            builder.setTitle("¡No se agregaron caras!");
        else
            builder.setTitle("Reconocimientos:");

        // add a checkbox list
        String[] names= new String[registered.size()];
        boolean[] checkedItems = new boolean[registered.size()];
        int i=0;
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet())
        {
            //System.out.println("NAME"+entry.getKey());
            names[i]=entry.getKey();
            checkedItems[i]=false;
            i=i+1;

        }
        builder.setItems(names,null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
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
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(640, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //Latest frame is shown
                        .build();

        Executor executor = Executors.newSingleThreadExecutor();
        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {

                InputImage image = null;


                @SuppressLint({"UnsafeExperimentalUsageError", "UnsafeOptInUsageError"})
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

                                                    //Ajustar orientación de rostro
                                                    Bitmap frame_bmp1 = rotateBitmap(frame_bmp, rot, flipX, false);
                                                    
                                                    //Obtener cuadro delimitador de rostro
                                                    RectF boundingBox = new RectF(face.getBoundingBox());

                                                    //Crop out bounding box from whole Bitmap(image)
                                                    Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);


                                                    //Scale the acquired Face to 112*112 which is required input for model
                                                    Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);

                                                    if(start)
                                                        recognizeImage(scaled); //Send scaled bitmap to create face embeddings.
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
        face_preview.setImageBitmap(bitmap);

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
                    reco_name.setText(name + " D(" + distance+")");

                    if(!nombre.equalsIgnoreCase(name)){
                        identificado++;

                        if(identificado==1){
                            //Hablar
                            Log.i("NOMBRE", "|"+name);
                            if(estadoReconocer){
                                MiAplicacionTareo.leer_voz("HOLA "+name);
                            }
                        }
                    }else{
                        identificado=0;
                    }


                }else {
                    identificado=0;
                    reco_name.setText("Desconocido D("+distance+")");
                }
                System.out.println("nearest: " + name + " - distance: " + distance);


            }
        }


//            final int numDetectionsOutput = 1;
//            final ArrayList<SimilarityClassifier.Recognition> recognitions = new ArrayList<>(numDetectionsOutput);
//            SimilarityClassifier.Recognition rec = new SimilarityClassifier.Recognition(
//                    id,
//                    label,
//                    distance);
//
//            recognitions.add( rec );

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
        Bitmap newBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), bm.getConfig());
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bm, 0, 0, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);


        int width = newBitmap.getWidth();
        int height = newBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(newBitmap, 0, 0, width, height, matrix, false);
        newBitmap.recycle();
        return resizedBitmap;
    }

    public Bitmap getResizedBitmap_bk(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
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
        Log.i("JSONSTRING: ", "|"+jsonString);
        //System.out.println("Input josn"+jsonString.toString());
        editor.apply();
        Toast.makeText(context, "Recognitions Saved", Toast.LENGTH_SHORT).show();
    }

    //Load Faces from Shared Preferences.Json String to Recognition object
    private HashMap<String, SimilarityClassifier.Recognition> readFromSP(){
        SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
        String defValue = new Gson().toJson(new HashMap<String, SimilarityClassifier.Recognition>());
        //Log.i("_defValueJSON", ""+defValue);
        String json=sharedPreferences.getString("map",defValue);
        System.out.println("Output json"+json.toString());
        TypeToken<HashMap<String,SimilarityClassifier.Recognition>> token = new TypeToken<HashMap<String,SimilarityClassifier.Recognition>>() {};
        HashMap<String,SimilarityClassifier.Recognition> retrievedMap=new Gson().fromJson(json,token.getType());
        System.out.println("Output map"+retrievedMap.toString());


        //Iteración obtención de keys MANCAJIMA
        try {
            JSONObject json_ = new JSONObject(json);
            Iterator<String> iter = json_.keys();
            while(iter.hasNext()){
                String key = iter.next();
                Log.i("KEY", ""+key);

                JSONObject obj = json_.getJSONObject(key);
                Log.i("KEY_DIST", ""+obj.getString("distance"));
                Log.i("KEY_EX", ""+obj.getString("extra"));
                Log.i("KEY_ID", ""+obj.getString("id"));
                Log.i("KEY_TIT", ""+obj.getString("title"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


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

    //Load Photo from phone storage
    private void loadphoto()
    {
        estadoReconocer = false;
        start=false;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Log.i("selectedImageUri", "|"+getFileName(data.getData()) );
                Log.i("selectedImageUriPATH", "|"+data.getData().getPath() );
                Log.i("selectedImageUriDIR", "|"+getDirectorio(data.getData()) );
                Log.i("selectedImageDATA", "|"+data.getData() );
                Uri selectedImageUri = data.getData();
                try {
                    InputImage impphoto=InputImage.fromBitmap(getBitmapFromUri(selectedImageUri),0);
                    detector.process(impphoto).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                        @Override
                        public void onSuccess(List<Face> faces) {

                            if(faces.size()!=0) {
                                //recognize.setText("Recognize");
                                //estadoReconocer=false;
                                add_face.setVisibility(View.VISIBLE);
                                reco_name.setVisibility(View.INVISIBLE);
                                face_preview.setVisibility(View.VISIBLE);
                                preview_info.setText("1.Traiga la cara a la vista de la cámara.\n\n2.La vista previa de tu rostro aparecerá aquí.\n\n3.Haga clic en el botón Agregar para guardar la cara.");
                                //preview_info.setText("1.Bring Face in view of Camera.\n\n2.Your Face preview will appear here.\n\n3.Click Add button to save face.");
                                Face face = faces.get(0);
                                System.out.println(face);

                                //write code to recreate bitmap from source
                                //Write code to show bitmap to canvas

                                Bitmap frame_bmp= null;
                                try {
                                    frame_bmp = getBitmapFromUri(selectedImageUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Bitmap frame_bmp1 = rotateBitmap(frame_bmp, 0, flipX, false);

                                //face_preview.setImageBitmap(frame_bmp1);


                                RectF boundingBox = new RectF(face.getBoundingBox());
                                Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);
                                Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);
                                // face_preview.setImageBitmap(scaled);

                                recognizeImage(scaled);
                                addFace();
                                System.out.println(boundingBox);
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            start=true;
                            Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show();
                        }
                    });

                    face_preview.setImageBitmap(getBitmapFromUri(selectedImageUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public String getDirectorio(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    private void crear_acceder_directorio() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
            boolean isDirectoryCreated = file.exists();

            if(!isDirectoryCreated)
                isDirectoryCreated = file.mkdirs();

            if(isDirectoryCreated){//Directorio existe
                Log.i("DIRECTORIO", "EXISTE");
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Long timestamp = System.currentTimeMillis() / 10000;
                String imageName = timestamp.toString() + ".png";

                mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY + File.separator + imageName;

                File newFile = new File(mPath);
            }
        }catch (Exception e){
            Log.i("DIRECTORIO_E", "|"+e.getMessage());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reconocimiento, menu);
        return true;
    }

    //Para volver atras
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_agregar:
                estadoReconocer=false;
                start=true;
                //recognize.setText("Recognize");
                add_face.setVisibility(View.VISIBLE);
                reco_name.setVisibility(View.INVISIBLE);
                face_preview.setVisibility(View.VISIBLE);
                preview_info.setText("1.Traiga la cara a la vista de la cámara.\n\n2.La vista previa de tu rostro aparecerá aquí.\n\n3.Haga clic en el botón Agregar para guardar la cara.");


                return  true;
            case R.id.action_reconocer:
                estadoReconocer=true;
                //recognize.setText("Add Face");
                add_face.setVisibility(View.INVISIBLE);
                reco_name.setVisibility(View.VISIBLE);
                face_preview.setVisibility(View.INVISIBLE);
                preview_info.setText("\n    Reconocimiento de Rostro:");
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
            case R.id.action_lista:
                displaynameListview();
                return true;
            case R.id.action_actualizar:
                updatenameListview();
                return true;
            case R.id.action_guardar_rec:
                insertToSP(registered,false);
                return true;
            case R.id.action_cargar_rec:
                registered.putAll(readFromSP());
                return true;
            case R.id.action_limpiar:
                clearnameList();
                return true;
            case R.id.action_importar_foto:
                loadphoto();
                return true;
            case R.id.action_algoritmo:
                //getPosArchivoAlgoritmo(0);
                //new TareacargaAlgoritmo().execute();
                pre_ejecucion_algoritmo();
                return true;
            case R.id.action_transferir_reconocimientos:
                new TareaEnviarReconocimientosProcesadosXMLHtml().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class TareaEnviarReconocimientosProcesadosXMLHtml extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReconocimientoTest.this);
            pDialog.setTitle("Transfiriendo reconocimientos procesados al servidor...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return enviarXMLReconocimientos_Http();
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            Log.i("RESULT_ASYNTASK", result);
            Reconocimiento objC = new Reconocimiento();
            if (result.trim().equalsIgnoreCase("TRUE")) {
                //pDialog.dismiss();
                objC.modificarEstadoASincronizadoReconocimiento();
                Toast.makeText(ReconocimientoTest.this, "Éxito! - 200", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(ReconocimientoTest.this, "Ocurrió un inconveniente al transferir la información, vuelve a intentarlo! - 100", Toast.LENGTH_LONG).show();
            }

        }
    }

    private String enviarXMLReconocimientos_Http(){//07/02/2022 mancajima
        final Reconocimiento objC = new Reconocimiento();
        final StringBuilder detalleRec = new StringBuilder();
        objC.cargarListaReconocimientoSQLiteUpload();

        detalleRec.append("<RECONOCIMIENTOS>");
        for (String reconocimiento : Reconocimiento.listaReconocimientoUpload) {
            detalleRec.append(reconocimiento);
        }
        detalleRec.append("</RECONOCIMIENTOS>");

        Log.i("STRINGBUILDER----", "--------------------------------");
        Log.i("STRINGBUILDER-TICKETCOS", String.valueOf(detalleRec));
        Log.i("STRINGBUILDER-IDUSUARIO", "1");

        String RPTA = "FALSE";
        Vector<HttpPostValues> parametros = new Vector<>();
        parametros.addElement(new HttpPostValues("IdUsuario", "1"));
        parametros.addElement(new HttpPostValues("Reconocimientos", detalleRec.toString()));

        try {
            String resultado = new Http().enviarPost(MiAplicacionTareo.URL_SERVICE_SUBIDA + "setLoadMovilReconocimientosTareo", parametros);
            Log.i("RESPONSE", resultado);
            JSONObject json = new JSONObject(resultado);
            RPTA = json.getString("resultado");
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

    private void pre_ejecucion_algoritmo(){
        conteo=0;
        pDialog = new ProgressDialog(ReconocimientoTest.this);
        pDialog.setMessage("Cargando data...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();

        itemFiles.clear();
        File f = new File(Environment.getExternalStorageDirectory() + "/Download/");
        File[] files = f.listFiles();

        //Hacemos un Loop por cada fichero para extraer el nombre de cada uno
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isDirectory() && files[i].getName().trim().length()>=12) {
                Log.i("ALGORITMO_FILE", "|" + files[i].getName());
                itemFiles.add(new FileImageRF(files[i], files[i].getName()) );
            }
        }

        if(itemFiles.size()>0){
            ejecutar_algoritmo();
        }else{
            pDialog.dismiss();
        }
    }

    private void ejecutar_algoritmo(){
        pDialog.setMessage("Ejecutando reconocimiento de imagen: "+conteo);

        if(itemFiles.size()>0){//Todavia quedan elementos

            File file = itemFiles.get(0).getFile();
            String nombre = file.getName().substring(0, file.getName().length()-4);//sin extension, sólo DNI
            selectedImageUri = Uri.fromFile(file);

            try {
                InputImage impphoto=InputImage.fromBitmap(getBitmapFromUri(selectedImageUri),0);
                detector.process(impphoto).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                    @Override
                    public void onSuccess(List<Face> faces) {

                        if(faces.size()!=0) {
                            Face face = faces.get(0);
                            System.out.println(face);

                            Bitmap frame_bmp= null;
                            try {
                                frame_bmp = getBitmapFromUri(selectedImageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Bitmap frame_bmp1 = rotateBitmap(frame_bmp, 0, flipX, false);

                            RectF boundingBox = new RectF(face.getBoundingBox());
                            Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);

                            System.out.println(boundingBox);
                            scaled_bm = getResizedBitmap(cropped_face, 112, 112);
                            new TareaRecognizeImage().execute(nombre.trim());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        start=true;
                        Log.i("ALGORITMO", "FALSE_Failed_to_add");
                        pDialog.dismiss();
                    }
                });
                //return true;

            } catch (IOException e) {
                e.printStackTrace();
                if(pDialog != null){
                    pDialog.dismiss();
                }

            }

        }else{
            pDialog.dismiss();
        }



    }

    private class TareaRecognizeImage extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            face_preview.setVisibility(View.VISIBLE);
            face_preview.setImageBitmap(scaled_bm);

            pDialog.setMessage("Obteniendo datos del rostro: "+conteo);
            /*pDialog = new ProgressDialog(ReconocimientoTest.this);
            pDialog.setMessage("Ejecutando reconocimiento de imagen...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();*/
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String nombre_ = params[0].trim();
            ByteBuffer imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);
            imgData.order(ByteOrder.nativeOrder());
            intValues = new int[inputSize * inputSize];

            scaled_bm.getPixels(intValues, 0, scaled_bm.getWidth(), 0, 0, scaled_bm.getWidth(), scaled_bm.getHeight());

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

            SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition("0", "", -1f);
            result.setExtra(embeedings);

            //mancajima
            Log.i("DATOS_ID", "|"+conteo+"|"+result.getId());
            Log.i("DATOS_TITLE", "|"+result.getTitle());
            Log.i("DATOS_NAME", "|"+nombre_);
            Log.i("DATOS_DIST", "|"+result.getDistance());
            Log.i("DATOS_EXT", "|"+embeedings.length);
            Log.i("DATOS_EMBE", "X|"+1);
            Log.i("DATOS_EMBE", "Y|"+OUTPUT_SIZE);

            registered.put( nombre_, result);
            //addFaceSQLitePerson(nombre_.trim(), new Gson().toJson(result));//Agregar a SQLite ALGORITMO
            try {
                JSONObject obj = new JSONObject(new Gson().toJson(result));
                Reconocimiento objR = new Reconocimiento();
                long rpta = objR.agregarReconocimiento(nombre_, obj.getString("distance"), obj.getString("extra"), obj.getString("id"), obj.getString("title"));
            } catch (JSONException e) {
                String error = e.getMessage();
                if(error.isEmpty()){
                    error = "No encontrado, NULL";
                }
            }//

            start=true;

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        }

        protected void onPostExecute(Boolean result){
            Log.i("ALGORITMO", ""+(conteo++));
            if(itemFiles.size()>1){
                itemFiles.remove(0);
                ejecutar_algoritmo();
            }else{
                itemFiles.remove(0);
                ejecutar_algoritmo();
            }

            //pDialog.dismiss();
        }
    }


    private class TareacargaAlgoritmo extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            face_preview.setVisibility(View.VISIBLE);

            pDialog = new ProgressDialog(ReconocimientoTest.this);
            pDialog.setMessage("Cargando fotografías..");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected Boolean doInBackground(String... params) {
            int conteo = 0;
            List<String> item = new ArrayList<>();
            File f = new File(Environment.getExternalStorageDirectory() + "/Download/");
            File[] files = f.listFiles();

            //for (int i = 0; i < files.length; i++) {
            for (int i = 0; i < 1; i++) {
                File file = files[i];
                String nombre = file.getName().substring(0, file.getName().length()-4);//sin extension, sólo DNI
                selectedImageUri = Uri.fromFile(file);

                if(file.getName().trim().length()>=8){
                    try {
                        InputImage impphoto=InputImage.fromBitmap(getBitmapFromUri(selectedImageUri),0);
                        detector.process(impphoto).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                            @Override
                            public void onSuccess(List<Face> faces) {
                                if(faces.size()!=0) {
                                    Face face = faces.get(0);
                                    System.out.println(face);

                                    Bitmap frame_bmp= null;
                                    try {
                                        frame_bmp = getBitmapFromUri(selectedImageUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Bitmap frame_bmp1 = rotateBitmap(frame_bmp, 0, flipX, false);

                                    RectF boundingBox = new RectF(face.getBoundingBox());
                                    Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);
                                    //Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);
                                    scaled_bm = getResizedBitmap(cropped_face, 112, 112);

                                    //recognizeImage(scaled);

                                    recognizeImageV2(scaled_bm);
                                    Log.i("ALGORITMO", ""+nombre);
                                    addFacePruebaAlgoritmo(nombre);

                                    System.out.println(boundingBox);
                                    /*try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }*/
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                start=true;
                                Log.i("ALGORITMO", "FALSE_Failed_to_add");
                            }
                        });
                        //return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        //return false;
                    }

                    conteo++;
                }


            }


            if(conteo != 0){
                return true;
            }else{
                return false;
            }
        }

        protected void onPostExecute(Boolean result){
            MiAplicacionTareo.guardar_preferenciaLogin(ReconocimientoTest.this);
            pDialog.dismiss();
            if(result){
                face_preview.setImageBitmap(scaled_bm);
                //face_preview.setImageBitmap(getBitmapFromUri(selectedImageUri));
            } else{
                Toast.makeText(ReconocimientoTest.this, "Ocurrió un error al cargar la data de reconocimiento!!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void recognizeImageV2(Bitmap bitmap){
        //Create ByteBuffer to store normalized image
        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);
        imgData.order(ByteOrder.nativeOrder());
        intValues = new int[inputSize * inputSize];

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
    }

    private void getPosArchivoAlgoritmo(int pos){
        List<String> item = new ArrayList<>();
        //Defino la ruta donde busco los ficheros
        File f = new File(Environment.getExternalStorageDirectory() + "/Download/");
        //Creo el array de tipo File con el contenido de la carpeta
        File[] files = f.listFiles();

        File file = files[pos];
        String nombre = file.getName().substring(0, file.getName().length()-4);


        Uri selectedImageUri = Uri.fromFile(file);
        try {
            InputImage impphoto=InputImage.fromBitmap(getBitmapFromUri(selectedImageUri),0);
            detector.process(impphoto).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                @Override
                public void onSuccess(List<Face> faces) {

                    if(faces.size()!=0) {
                        face_preview.setVisibility(View.VISIBLE);
                        Face face = faces.get(0);
                        System.out.println(face);

                        Bitmap frame_bmp= null;
                        try {
                            frame_bmp = getBitmapFromUri(selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Bitmap frame_bmp1 = rotateBitmap(frame_bmp, 0, flipX, false);

                        RectF boundingBox = new RectF(face.getBoundingBox());
                        Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);
                        Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);

                        recognizeImage(scaled);
                        //addFace();
                        //Log.i("getName_", ""+nombre);
                        addFacePruebaAlgoritmo(nombre);
                        System.out.println(boundingBox);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    start=true;
                    Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show();
                }
            });
            face_preview.setImageBitmap(getBitmapFromUri(selectedImageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void recorrerListaArchivos(){
        List<String> item = new ArrayList<>();
        //Defino la ruta donde busco los ficheros
        File f = new File(Environment.getExternalStorageDirectory() + "/Download/");
        //Creo el array de tipo File con el contenido de la carpeta
        File[] files = f.listFiles();

        //Hacemos un Loop por cada fichero para extraer el nombre de cada uno
        for (int i = 0; i < files.length; i++) {
            //Sacamos del array files un fichero
            File file = files[i];
            Log.i("getName", "|"+file.getName());
            //Si es directorio...
            if (file.isDirectory())
                item.add(file.getName() + "/");
                //Si es fichero...
            else
                item.add(file.getName());
        }
    }
}