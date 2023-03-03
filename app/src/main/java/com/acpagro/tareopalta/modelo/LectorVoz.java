package com.acpagro.tareopalta.modelo;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class LectorVoz {
    public  static TextToSpeech textToSpeech;
    public static void iniciarLectorVoz(Context context){
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    //t1.setLanguage (Locale.ENGLISH);
                    textToSpeech.setLanguage (Locale.getDefault());
                }
            }
        } );
    }

    public static void finalizarLectorVoz(){
        if (textToSpeech != null) {
            textToSpeech.stop ();
            textToSpeech.shutdown ();
        }
    }

    public static void leer_voz(String texto){
        if(textToSpeech!=null){
            textToSpeech.speak ( texto , TextToSpeech.QUEUE_FLUSH , null );
        }
    }
}
