package com.acpagro.tareopalta.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.acpagro.tareopalta.ConexionImpresora;
import com.acpagro.tareopalta.PantallaDetalleBinLectura;
import com.acpagro.tareopalta.PantallaListaCabeceraBin;
import com.acpagro.tareopalta.R;
import com.acpagro.tareopalta.modelo.BinCabecera;
import com.acpagro.tareopalta.modelo.BinDetalle;
import com.acpagro.tareopalta.modelo.MiAplicacionTareo;
import com.acpagro.tareopalta.util.UIHelper;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AdapterListaBinCabecera extends ArrayAdapter<BinCabecera> {
    private Activity contexto;
    public static AlertDialog alertDialog;
    ProgressDialog pDialog;
    LinearLayout ly_formato;
    private UIHelper helper;

    public AdapterListaBinCabecera(Activity context){
        super(context, R.layout.item_lista_cabecera_bin, BinCabecera.listaCabeceras);
        this.contexto = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        item = this.contexto.getLayoutInflater().inflate(R.layout.item_lista_cabecera_bin, null);

        final BinCabecera objA = BinCabecera.listaCabeceras.get(position);
        TextView item_salida = item.findViewById(R.id.item_salida);
        TextView item_placa = item.findViewById(R.id.item_placa);
        TextView item_bines = item.findViewById(R.id.item_bines);
        ImageButton ibtnImprimir = item.findViewById(R.id.ibtnImprimir);
        LinearLayout item_fila = item.findViewById(R.id.item_fila);

        item_salida.setText(objA.getSALIDA());
        item_placa.setText(objA.getPLACA());
        item_bines.setText(objA.getBINES());

        ibtnImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(objA.getBINES())>0){
                    alertDialog = dialogImprimir(contexto, objA.getIDCABECERA(), PantallaListaCabeceraBin.FECHAHOY, objA.getPLACA(), objA.getCHOFER(), objA.getBINES());
                    alertDialog.show();
                }else{
                    DateFormat dateFormat2 = new SimpleDateFormat("YYMMddHHmmss");
                    Date date = new Date();
                    String fechaHoy = dateFormat2.format(date);
                    Toast.makeText(contexto, "Debes iniciar la lectura para imprimir.|"+fechaHoy, Toast.LENGTH_SHORT).show();
                }

            }
        });

        item_fila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle parametros = new Bundle();
                Intent pR = new Intent(contexto, PantallaDetalleBinLectura.class);
                parametros.putString("p_idcabecera", objA.getIDCABECERA());
                parametros.putString("p_placa", objA.getPLACA());
                parametros.putString("p_chofer", objA.getCHOFER());
                parametros.putString("p_fecha", PantallaListaCabeceraBin.FECHAHOY);
                pR.putExtras(parametros);
                contexto.startActivity(pR);
            }
        });

        return item;
    }

    public AlertDialog dialogImprimir(Activity context, String IDCABECERA, String FECHAHOY, String PLACA, String CHOFER, String BINES){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(R.layout.item_dialog_impresion_bin, null);
        final TextView tv_fecha = v.findViewById(R.id.tv_fecha);
        final TextView tv_placa = v.findViewById(R.id.tv_placa);
        final TextView tv_chofer = v.findViewById(R.id.tv_chofer);
        final TextView tv_bines = v.findViewById(R.id.tv_bines);
        final TextView tv_cabecera = v.findViewById(R.id.tv_cabecera);
        final ImageView img_codebar = v.findViewById(R.id.img_codebar);
        ly_formato = v.findViewById(R.id.ly_formato);
        Button btn_aceptar = v.findViewById(R.id.btn_aceptar);
        Button btn_cancelar = v.findViewById(R.id.btn_cancelar);

        tv_cabecera.setText(IDCABECERA);
        tv_fecha.setText(FECHAHOY);
        tv_placa.setText(PLACA);
        tv_chofer.setText(CHOFER);
        tv_bines.setText(""+BINES);
        MiAplicacionTareo.generarCodigo128(IDCABECERA, img_codebar);
        //MiAplicacionTareo.generarCodigo128(IDCABECERA, img_codebar);

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TareaImprimir().execute("1");
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        builder.setTitle("IMPRIMIR ETIQUETA");
        builder.setView(v);
        return  builder.create();
    }

    private class TareaImprimir extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Imprimiendo...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String cantidad = params[0];

            MiAplicacionTareo.cargar_preferenciasIMPRESORA(contexto);

            if(MiAplicacionTareo.IMPRESORA.equalsIgnoreCase("ZEBRA")){
                Connection connection = new BluetoothConnection(""+MiAplicacionTareo.MAC_BT);//ZEBRA 03
                try {
                    connection.open();
                    ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
                    Bitmap image = getBitmapFromView(ly_formato);
                    //Bitmap imagenf = rotate2(image, 90);

                    for(int i = 0; i< Integer.parseInt(cantidad); i++){
                        //printer.printImage(new ZebraImageAndroid(image), 30, 30, 750, 1500, false);
                        printer.printImage(new ZebraImageAndroid(image), 10, 10, 750, 375, false);
                    }
                    //printer.printImage(new ZebraImageAndroid(imagenf), 30, 30, 750, 1460, false);
                    connection.close();
                    return true;
                } catch (ConnectionException e) {
                    helper.showErrorDialogOnGuiThread(e.getMessage());
                    return false;
                } catch (ZebraPrinterLanguageUnknownException e) {
                    helper.showErrorDialogOnGuiThread(e.getMessage());
                    return false;
                } finally {
                    return true;
                }
            }else if(MiAplicacionTareo.IMPRESORA.equalsIgnoreCase("BIXOLON")){
                try{
                    Bitmap image = getBitmapFromView(ly_formato);
                    //Bitmap imagenf = rotate2(image, 90);
                    for(int i = 0; i< Integer.parseInt(cantidad); i++){
                        //ConexionImpresora.getPrinterInstance().printImagePersonality(image, 750, ConexionImpresora.getPrinterInstance().ALIGNMENT_CENTER, 60);//Para etiqueta pallet modo Rotado
                        ConexionImpresora.getPrinterInstance().printImagePersonality(image, 800, ConexionImpresora.getPrinterInstance().ALIGNMENT_CENTER, 40);
                    }

                    return  true;
                }catch (Exception e){
                    e.printStackTrace();
                    return  false;
                }

                //return true;
            }else{
                return false;
            }
        }

        protected void onPostExecute(Boolean rpta) {
            pDialog.dismiss();
            if(!rpta){
                Toast.makeText(contexto, "DEBES SELECCIONAR UNA IMPRESORA, ANTES DE ENVIAR A IMPRIMIR; SELECIONALA EN EL MENÚ DE ARRIBA 'CONFIGURAR IMPRESORA'. RECUERDA ACTIVAR TU BLUETOOTH Y VINCULARLA CORRECTAMENTE.", Toast.LENGTH_LONG).show();
            }else{
                alertDialog.dismiss();
            }
        }
    }

    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

}
