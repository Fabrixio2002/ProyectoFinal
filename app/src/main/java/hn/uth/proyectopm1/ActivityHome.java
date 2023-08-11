package hn.uth.proyectopm1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ActivityHome extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ImageView imageView;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "mi_canal_de_notificacion";
   private String email;
    private static final int REQUEST_CODE_ACTUALIZAR_PERFIL = 1;
   private String estado;
  private   String correoUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.userImageView);
        TextView button = findViewById(R.id.prueba);

        if (firebaseUser != null) {
             email = firebaseUser.getEmail();
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
                                Glide.with(ActivityHome.this).clear(imageView);
                                Glide.with(ActivityHome.this)
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

        DatabaseReference pedidoRef = FirebaseDatabase.getInstance().getReference("pedidos");

        // Agregar un listener para obtener datos del nodo "pedido"
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterar a través de los pedidos y obtener los datos
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Pedido pedido = snapshot.getValue(Pedido.class);
                    if (pedido != null) {
                        // Aquí puedes trabajar con los datos del pedido
                         estado = pedido.getEstado();
                         correoUsuario = pedido.getCorreoUsuario();
                        // ... y otros campos que tengas en tu modelo Pedido
                        if ("En Curso".equals(estado) && email.equals(correoUsuario)) {
                            mostrarNotificacion();
                        }

                        if ("En Cola".equals(estado) && email.equals(correoUsuario)) {
                            // Aquí puedes agregar alguna lógica adicional o mostrar otra notificación si deseas
                            mostrarNotificacion2();
                        }

                        // Verificar si el estado del pedido es "Completado" y si el correo del usuario es el mismo que el del usuario actual
                        if ("Completado".equals(estado) && email.equals(correoUsuario)) {
                            mostrarNotificacion3();
                            // Aquí puedes agregar alguna lógica adicional o mostrar otra notificación si deseas
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




    public void Ubicacion(View view) {
        Intent intent = new Intent(getApplicationContext(), SelectLocationActivity2.class);
        startActivity(intent);
    }

    public void salir(View view) {
        firebaseAuth.signOut();
        Intent intent = new Intent(ActivityHome.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void Pantallacomprar(View view) {
        Intent intent = new Intent(ActivityHome.this, ActivityList.class);
        startActivity(intent);
    }

    public void PantallaPerfil(View view) {
        Intent intent = new Intent(ActivityHome.this, ActivityPerfil.class);
        startActivityForResult(intent, REQUEST_CODE_ACTUALIZAR_PERFIL);
    }



    public void PedidosList(View view) {
        Intent intent = new Intent(ActivityHome.this, ActivityListPedidosUser.class);
        startActivityForResult(intent, REQUEST_CODE_ACTUALIZAR_PERFIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ACTUALIZAR_PERFIL && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("imagenActualizada")) {
                String imageUrl = data.getStringExtra("imagenActualizada");
                if (imageUrl != null) {
                    Glide.with(ActivityHome.this).clear(imageView);
                    Glide.with(ActivityHome.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.perfil)
                            .error(R.drawable.perfil)
                            .into(imageView);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Usuario");
        Query query = usersRef.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usuario user = snapshot.getValue(Usuario.class);
                    if (user != null) {
                        String imageUrl = user.getFotoUrl();
                        if (imageUrl != null) {
                            Glide.with(ActivityHome.this).clear(imageView);
                            Glide.with(ActivityHome.this)
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

    private void mostrarNotificacion() {
        // Crear el canal de notificación (solo necesario en Android 8.0 y superior)
        createNotificationChannel();

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.pochoclo) // Reemplaza con el ícono de tu notificación
                .setContentTitle("¡Pedido Aceptado!")
                .setContentText("Tu pedido esta en curso :D " +
                        "Puedes verlo en tu Historial")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Mostrar la notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void mostrarNotificacion2() {
        // Crear el canal de notificación (solo necesario en Android 8.0 y superior)
        createNotificationChannel();

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.pochoclo) // Reemplaza con el ícono de tu notificación
                .setContentTitle("¡Pedido EN Lista De Espera!")
                .setContentText("Tu pedido esta en espera :D " +
                        "Puedes verlo en tu Historial")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Mostrar la notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    private void mostrarNotificacion3() {
        // Crear el canal de notificación (solo necesario en Android 8.0 y superior)
        createNotificationChannel();

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.pochoclo) // Reemplaza con el ícono de tu notificación
                .setContentTitle("¡Pedido Terminado!")
                .setContentText("Tu Pedido Fue Entregado")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Mostrar la notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    // Crea el canal de notificación (solo necesario en Android 8.0 y superior)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Mi Canal";
            String description = "Descripción del canal";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    }
