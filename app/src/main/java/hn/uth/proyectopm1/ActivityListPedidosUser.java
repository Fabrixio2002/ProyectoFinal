package hn.uth.proyectopm1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.common.net.InternetDomainName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityListPedidosUser extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PedidoAdapter pedidoAdapter;
    private DatabaseReference pedidosRef;
    private String correoUsuario;
    static final int RATING_REQUEST_CODE = 1;

    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private   String productId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pedidos_user);
// Dentro del método onCreate
        // Obtén la referencia a la base de datos
        recyclerView = findViewById(R.id.recyclerViewPedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pedidoAdapter = new PedidoAdapter((Activity) this,new ArrayList<>());
        recyclerView.setAdapter(pedidoAdapter);
         productId = getIntent().getStringExtra("product_id");

        pedidosRef = FirebaseDatabase.getInstance().getReference().child("pedidos");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            correoUsuario = firebaseUser.getEmail();
        }

        cargarPedidosEnCola();
    }

    private void cargarPedidosEnCola() {
        // Obtener el correo del usuario actual
        String correoUsuarioActual = correoUsuario; // Debes implementar este método para obtener el correo del usuario actual que ha iniciado sesión

        // Filtrar los pedidos por el correo del usuario actual y por el estado "En Curso"
        pedidosRef.orderByChild("estado").equalTo("En Curso").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Pedido> pedidosEnCursoUsuario = new ArrayList<>();

                for (DataSnapshot pedidoSnapshot : dataSnapshot.getChildren()) {
                    Pedido pedido = pedidoSnapshot.getValue(Pedido.class);
                    if (pedido.getCorreoUsuario().equals(correoUsuarioActual)) {
                        pedidosEnCursoUsuario.add(pedido);
                    }
                }

                // Obtener los pedidos "En Cola"
                pedidosRef.orderByChild("estado").equalTo("En Cola").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Pedido> pedidosEnColaUsuario = new ArrayList<>();

                        for (DataSnapshot pedidoSnapshot : dataSnapshot.getChildren()) {
                            Pedido pedido = pedidoSnapshot.getValue(Pedido.class);
                            if (pedido.getCorreoUsuario().equals(correoUsuarioActual)) {
                                pedidosEnColaUsuario.add(pedido);
                            }
                        }

                        // Obtener los pedidos "Completados"
                        pedidosRef.orderByChild("estado").equalTo("Completado").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<Pedido> pedidosCompletadosUsuario = new ArrayList<>();

                                for (DataSnapshot pedidoSnapshot : dataSnapshot.getChildren()) {
                                    Pedido pedido = pedidoSnapshot.getValue(Pedido.class);
                                    if (pedido.getCorreoUsuario().equals(correoUsuarioActual)) {
                                        pedidosCompletadosUsuario.add(pedido);
                                    }
                                }

                                // Unir las listas en el orden requerido
                                List<Pedido> pedidosOrdenados = new ArrayList<>();
                                pedidosOrdenados.addAll(pedidosEnCursoUsuario);
                                pedidosOrdenados.addAll(pedidosEnColaUsuario);
                                pedidosOrdenados.addAll(pedidosCompletadosUsuario);

                                // Mostrar los pedidos en el RecyclerView
                                pedidoAdapter.setPedidos(pedidosOrdenados);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(ActivityListPedidosUser.this, "Error al cargar los pedidos completados", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ActivityListPedidosUser.this, "Error al cargar los pedidos en cola", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ActivityListPedidosUser.this, "Error al cargar los pedidos en curso", Toast.LENGTH_SHORT).show();
            }


        });

    }
    // Resto del código de ActivityListPedidosUser

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RATING_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Obtén la calificación del intent
            float rating = data.getFloatExtra("rating", 0);
            String productId = data.getStringExtra("product_id");
            Toast.makeText(this, ""+productId, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, ""+productId, Toast.LENGTH_SHORT).show();
            // Guarda la calificación en la base de datos
            DatabaseReference calificacionRef = databaseRef.child("pedidos").child(productId).child("calificacion");
            calificacionRef.setValue(rating);

            // Muestra un mensaje con la calificación recibida
            Toast.makeText(this, "Calificación Enviada: " + rating, Toast.LENGTH_SHORT).show();
        }
    }





}





