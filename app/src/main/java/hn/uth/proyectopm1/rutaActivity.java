package hn.uth.proyectopm1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class rutaActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private double latitud;
    private double longitud;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta);
        Intent intent = getIntent();
        latitud = intent.getDoubleExtra("latitud", 0);
        longitud = intent.getDoubleExtra("longitud", 0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Verifica los permisos de ubicación y solicita la ubicación actual.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionActual();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // Método para obtener la ubicación actual del dispositivo.
    private void obtenerUbicacionActual() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Obtener la ubicación actual del dispositivo.
                    double latitudActual = location.getLatitude();
                    double longitudActual = location.getLongitude();

                    // Agregar un marcador en la ubicación actual.
                    LatLng ubicacionActual = new LatLng(latitudActual, longitudActual);
                    mMap.addMarker(new MarkerOptions().position(ubicacionActual).title("Ubicación Actual"));

                    // Trazar la ruta hacia el destino.
                    trazarRuta(ubicacionActual, new LatLng(latitud, longitud));
                }
            }
        });
    }

    // Método para trazar la ruta desde la ubicación actual hacia el destino.
    private void trazarRuta(LatLng origen, LatLng destino) {
        // Crear el objeto PolylineOptions para la ruta.
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(origen)
                .add(destino)
                .color(Color.BLUE)
                .width(8);

        // Agregar la Polyline al mapa.
        Polyline rutaPolyline = mMap.addPolyline(polylineOptions);

        // Crear un objeto LatLngBounds para ajustar la cámara para que la ruta sea visible.
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(origen);
        builder.include(destino);
        LatLngBounds bounds = builder.build();

        // Mover la cámara para que la ruta sea visible en el mapa.
        int padding = 100; // Margen en píxeles alrededor de la ruta
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Agrega un marcador en el destino y mueve la cámara.
        LatLng destinoLatLng = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().position(destinoLatLng).title("Destino"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinoLatLng, 15f));
    }
}
