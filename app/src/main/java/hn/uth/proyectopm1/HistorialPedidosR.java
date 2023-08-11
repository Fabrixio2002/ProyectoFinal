package hn.uth.proyectopm1;


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

public class HistorialPedidosR extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistorialAdapter pedidoAdapter;
    private DatabaseReference pedidosRef;
    private String correoUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_pedidos_r);

        recyclerView = findViewById(R.id.recyclerViewPedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pedidoAdapter = new HistorialAdapter(new ArrayList<>());
        recyclerView.setAdapter(pedidoAdapter);

        pedidosRef = FirebaseDatabase.getInstance().getReference().child("pedidos");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            correoUsuario = firebaseUser.getEmail();
        }

        cargarPedidosEnCola();
    }

    private void cargarPedidosEnCola() {
        // Obtener pedidos en curso
        pedidosRef.orderByChild("estado").equalTo("En Curso").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Pedido> pedidosEnCurso = new ArrayList<>();

                for (DataSnapshot pedidoSnapshot : dataSnapshot.getChildren()) {
                    Pedido pedido = pedidoSnapshot.getValue(Pedido.class);
                    pedidosEnCurso.add(pedido);
                }

                // Obtener pedidos completados
                pedidosRef.orderByChild("estado").equalTo("Completado").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Pedido> pedidosCompletados = new ArrayList<>();

                        for (DataSnapshot pedidoSnapshot : dataSnapshot.getChildren()) {
                            Pedido pedido = pedidoSnapshot.getValue(Pedido.class);
                            pedidosCompletados.add(pedido);
                        }

                        // Unir ambas listas en una sola lista de pedidos ordenados
                        List<Pedido> pedidosOrdenados = new ArrayList<>();
                        pedidosOrdenados.addAll(pedidosEnCurso);
                        pedidosOrdenados.addAll(pedidosCompletados);

                        // Mostrar los pedidos en el RecyclerView
                        pedidoAdapter.setPedidos(pedidosOrdenados);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(HistorialPedidosR.this, "Error al cargar los pedidos completados", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HistorialPedidosR.this, "Error al cargar los pedidos en curso", Toast.LENGTH_SHORT).show();
            }
        });
    }



}