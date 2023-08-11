package hn.uth.proyectopm1;

// RatingActivity.java
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

public class RatingActivity extends AppCompatActivity {
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        ratingBar = findViewById(R.id.ratingBar);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = ratingBar.getRating();
                String productId = getIntent().getStringExtra("product_id");

                // Envía la calificación de vuelta a la actividad anterior (PedidoAdapter)
                Intent resultIntent = new Intent();
                resultIntent.putExtra("rating", rating);
                resultIntent.putExtra("product_id", productId);
                setResult(Activity.RESULT_OK, resultIntent);

                finish(); // Cierra la RatingActivity
            }
        });
    }

}
