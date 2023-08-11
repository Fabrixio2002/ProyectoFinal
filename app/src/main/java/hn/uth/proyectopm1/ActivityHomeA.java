package hn.uth.proyectopm1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class ActivityHomeA extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_a);
        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void Pantallacomprar(View view) {
        Intent intent = new Intent(ActivityHomeA.this, ActivityAgregarProducto.class);
        startActivity(intent);
    }

    public void PantallaList(View view) {
        Intent intent = new Intent(ActivityHomeA.this, ActivityVerAdmin.class);
        startActivity(intent);
    }
    public void VerConsola(View view) {
        String url = "https://console.firebase.google.com/u/0/project/_/database/data?hl=es-419&_gl=1*15qbcdo*_ga*MjEzMTU3NDA3Mi4xNjg3OTE5OTE0*_ga_CW55HF8NVT*MTY5MTU0MDc4MS44Ny4xLjE2OTE1NDA5MTUuMC4wLjA.";

        Intent intent = new Intent(ActivityHomeA.this, WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    public void SalirA(View view) {
        firebaseAuth.signOut();
        Intent intent = new Intent(ActivityHomeA.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // No realizar ninguna acción, lo que deshabilitará el botón de retroceso
    }


}