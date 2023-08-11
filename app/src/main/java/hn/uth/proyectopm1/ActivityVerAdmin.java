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

import hn.uth.proyectopm1.Pedido;

public class ActivityVerAdmin extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdaptadorAdmin pedidoAdapter;
    private DatabaseReference pedidosRef;
    private String correoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_admin);

        recyclerView = findViewById(R.id.recyclerViewPedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pedidoAdapter = new AdaptadorAdmin(this, new ArrayList<>()); // Pasar la referencia de la actividad y la lista vacía
        recyclerView.setAdapter(pedidoAdapter);

        pedidosRef = FirebaseDatabase.getInstance().getReference().child("pedidos");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            correoUsuario = firebaseUser.getEmail();
        }

        cargarTodosLosPedidos();
    }

    private void cargarTodosLosPedidos() {
        pedidosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Pedido> pedidos = new ArrayList<>();

                for (DataSnapshot pedidoSnapshot : dataSnapshot.getChildren()) {
                    Pedido pedido = pedidoSnapshot.getValue(Pedido.class);
                    pedidos.add(pedido);
                }
                pedidoAdapter.setPedidos(pedidos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ActivityVerAdmin.this, "Error al cargar los pedidos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
