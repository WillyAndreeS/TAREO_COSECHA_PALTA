package com.acpagro.tareopalta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

public class NuevosUsuariosExtra extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevos_usuarios_extra);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_cerrar_blanco);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Agregar Usuario");

        mostrarFragment(new FragAgregarUsuario(), this);
    }

    public static void mostrarFragment(Fragment fragmento, FragmentActivity context){
        FragmentTransaction fragmentTransaction = context.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contenedor, fragmento);
        fragmentTransaction.commit();
    }

    //Para volver atras
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
}