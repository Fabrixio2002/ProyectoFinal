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

public class ActivityListPedidosR extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PedidoAdapterR pedidoAdapter;
    private DatabaseReference pedidosRef;
    private String correoUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pedidos_r);

        recyclerView = findViewById(R.id.recyclerViewPedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pedidoAdapter = new PedidoAdapterR(new ArrayList<>());
        recyclerView.setAdapter(pedidoAdapter);

        pedidosRef = FirebaseDatabase.getInstance().getReference().child("pedidos");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            correoUsuario = firebaseUser.getEmail();
        }

        cargarPedidosEnCola();
    }

    private void cargarPedidosEnCola() {
        pedidosRef.orderByChild("estado").equalTo("En Cola").addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(ActivityListPedidosR.this, "Error al cargar los pedidos", Toast.LENGTH_SHORT).show();
            }
        });
    }


}