package hn.uth.proyectopm1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ActivityHomeR extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ImageView imageView;
    private static final int REQUEST_CODE_ACTUALIZAR_PERFIL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_r);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        imageView = findViewById(R.id.userImageView);
        TextView button = findViewById(R.id.prueba);

        if (firebaseUser != null) {
            String email = firebaseUser.getEmail();
            button.setText(email);

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Usuario");
            Query query = usersRef.orderByChild("email").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Usuario user = snapshot.getValue(Usuario.class);
                        if (user != null) {
                            String imageUrl = user.getFotoUrl();
                            if (imageUrl != null) {
                                Glide.with(ActivityHomeR.this).clear(imageView);
                                Glide.with(ActivityHomeR.this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.perfil)
                                        .error(R.drawable.perfil)
                                        .into(imageView);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar el error de la consulta
                }
            });
        }

    }
    public void PedidosList(View view) {
        Intent intent = new Intent(ActivityHomeR.this, ActivityListPedidosR.class);
        startActivityForResult(intent, REQUEST_CODE_ACTUALIZAR_PERFIL);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ACTUALIZAR_PERFIL && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("imagenActualizada")) {
                String imageUrl = data.getStringExtra("imagenActualizada");
                if (imageUrl != null) {
                    Glide.with(ActivityHomeR.this).clear(imageView);
                    Glide.with(ActivityHomeR.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.perfil)
                            .error(R.drawable.perfil)
                            .into(imageView);
                }
            }
        }
    }
    public void PantallaPerfil(View view) {
        Intent intent = new Intent(ActivityHomeR.this, ActivityPerfil.class);
        startActivityForResult(intent, REQUEST_CODE_ACTUALIZAR_PERFIL);
    }
    public void Historial(View view) {
        Intent intent = new Intent(ActivityHomeR.this, HistorialPedidosR.class);
        startActivityForResult(intent, REQUEST_CODE_ACTUALIZAR_PERFIL);
    }
    public void salir(View view) {
        firebaseAuth.signOut();
        Intent intent = new Intent(ActivityHomeR.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // No realizar ninguna acción, lo que deshabilitará el botón de retroceso
    }

}