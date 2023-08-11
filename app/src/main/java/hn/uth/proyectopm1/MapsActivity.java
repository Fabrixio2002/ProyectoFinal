package hn.uth.proyectopm1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private double destinationLat;
    private double destinationLng;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        destinationLat = intent.getDoubleExtra("latitud", 0);
        destinationLng = intent.getDoubleExtra("longitud", 0);
        Button btnTerminarPedidos = findViewById(R.id.btnTerminarPedidos);
        Intent intent2 = getIntent();
        String id = intent2.getStringExtra("id");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnTerminarPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nuevoEstado = "Completado"; // Reemplaza esto con el nuevo estado deseado

                // Obtenemos una referencia a la base de datos
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("pedidos"); // Reemplaza "nombre_de_la_tabla" con el nombre de la referencia a tu nodo de pedidos

                // Actualizamos el estado del pedido
                ref.child(id).child("estado").setValue(nuevoEstado);
                Toast.makeText(MapsActivity.this, "HAS TERMINADO EL PEDIDO", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MapsActivity.this, ActivityHomeR.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng destinationLocation = new LatLng(destinationLat, destinationLng);
        mMap.addMarker(new MarkerOptions().position(destinationLocation).title("Destino"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destinationLocation));

        // Verificar y solicitar permisos de ubicación
        verificarPermisosYObtenerUbicacion();
    }

    private void verificarPermisosYObtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no se tienen los permisos de ubicación, solicitarlos en tiempo de ejecución
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Si los permisos ya están concedidos, obtener la ubicación actual y trazar la ruta
            obtenerUbicacionYTrazarRuta();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, obtener la ubicación actual y trazar la ruta
                obtenerUbicacionYTrazarRuta();
            } else {
                // Permiso denegado, manejar esta situación según tus necesidades
            }
        }
    }

    private void obtenerUbicacionYTrazarRuta() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Ubicación actual del usuario
                            LatLng originLocation = new LatLng(location.getLatitude(), location.getLongitude());

                            // Trazar la ruta desde la ubicación actual hasta el destino
                            trazarRuta(originLocation, new LatLng(destinationLat, destinationLng));
                        }
                    }
                });
    }

    private void trazarRuta(LatLng origin, LatLng destination) {
        // Generar la URL para abrir la aplicación de Google Maps con las direcciones
        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude);

        // Abrir la aplicación de Google Maps con las direcciones
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
