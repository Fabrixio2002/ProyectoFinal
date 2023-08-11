package hn.uth.proyectopm1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SelectLocationActivity2 extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;


    private String userEmail; // Declarar la variable para el email del usuario
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location2);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        // ...
        // Obtener el email del usuario actualmente autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
        } else {
            // El usuario no ha iniciado sesión, puede que necesites llevarlo a la pantalla de inicio de sesión.
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Posición inicial del mapa (puedes establecer una ubicación predeterminada aquí)
        LatLng initialPosition = new LatLng(13.3034, -87.1899);

        // Marcador inicial en la posición seleccionada
        marker = mMap.addMarker(new MarkerOptions().position(initialPosition).draggable(true));

        // Mueve la cámara para que se muestre la posición inicial
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 15f));

        // Establece un listener para detectar el evento de arrastrar el marcador
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // Cuando el usuario termine de arrastrar el marcador, obtén la nueva ubicación
                LatLng newPosition = marker.getPosition();
                double latitude = newPosition.latitude; // Latitud
                double longitude = newPosition.longitude; // Longitud
                String latitudeString = String.valueOf(latitude);
                String longitudeString = String.valueOf(longitude);
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Usuario");
                Query query = usersRef.orderByChild("email").equalTo(userEmail);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            DatabaseReference usuarioRef = userSnapshot.getRef();
                            usuarioRef.child("latitud").setValue(latitudeString);
                            usuarioRef.child("longitud").setValue(longitudeString)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            // Ubicación actualizada exitosamente
                                            AlertDialog.Builder builder = new AlertDialog.Builder(SelectLocationActivity2.this);
                                            builder.setTitle("Actualizado :D");
                                            builder.setMessage("Ubicacion Cambiada");
                                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Redireccionar al MainActivity
                                                    Intent intent = new Intent(SelectLocationActivity2.this, ActivityHome.class);
                                                    startActivity(intent);
                                                    finish(); // Esto asegura que el usuario no pueda regresar a esta actividad presionando el botón "Atrás"
                                                }
                                            });
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        } else {
                                            // Error al actualizar los campos
                                        }



                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Error al leer la base de datos
                    }
                });
            }


        });
    }
}
