package hn.uth.proyectopm1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActivityList extends AppCompatActivity implements ProductoAdapter.OnPedidoClickListener {
    private RecyclerView recyclerView;
    private ProductoAdapter productoAdapter;
    private DatabaseReference productosRef;
    private DatabaseReference pedidosRef;
    private String correoUsuario;

    private  String latitud;
    private  String longitud;
    private  String celular;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private EditText editTextGastoTotal;

    private Button btn_calcular;
    double gastoTotal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productoAdapter = new ProductoAdapter();
        recyclerView.setAdapter(productoAdapter);
        productosRef = FirebaseDatabase.getInstance().getReference().child("productos");
        pedidosRef = FirebaseDatabase.getInstance().getReference().child("pedidos");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        editTextGastoTotal = findViewById(R.id.editTextGastoTotal);
        btn_calcular=findViewById(R.id.btn_calcular);



        if (firebaseUser != null) {
            correoUsuario = firebaseUser.getEmail();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference usuarioRef = databaseReference.child("Usuario");
            // Consulta para obtener los datos del usuario según su correo electrónico
            Query query = usuarioRef.orderByChild("email").equalTo(correoUsuario);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // El usuario fue encontrado en la base de datos
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            // Obtener los datos del usuario
                             latitud = userSnapshot.child("latitud").getValue(String.class);
                             longitud = userSnapshot.child("longitud").getValue(String.class);
                             celular=userSnapshot.child("celular").getValue(String.class);

                        }
                    } else {
                        // El usuario no fue encontrado en la base de datos
                        // Puedes manejar esta situación según tus necesidades
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Error al leer la base de datos
                    // Puedes manejar este error según tus necesidades
                }
            });

        }

        productosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Producto> productos = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Producto producto = snapshot.getValue(Producto.class);
                    productos.add(producto);
                }

                productoAdapter.setProductos(productos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ActivityList.this, "Error al cargar los productos", Toast.LENGTH_SHORT).show();
            }
        });

        productoAdapter.setOnPedidoClickListener(this);

        Button Pedir = findViewById(R.id.btn_fact);
        Pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarAccionPedido();
            }
        });

        btn_calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Producto> productosSeleccionados = productoAdapter.getProductosSeleccionados();

                if (productosSeleccionados.isEmpty()) {
                    Toast.makeText(ActivityList.this, "No has seleccionado ningún producto", Toast.LENGTH_SHORT).show();
                    return;
                }

                gastoTotal = calcularGastoTotal(productosSeleccionados);
                editTextGastoTotal.setText("Lps "+String.valueOf(gastoTotal)); // Actualizar el TextView
            }
        });

    }

    @Override
    public void onPedidoClick(List<Producto> productos) {
        realizarAccionPedido();
    }

    @Override
    public void onProductoSeleccionadoConCantidadCero() {
        Toast.makeText(this, "La cantidad del producto seleccionado debe ser mayor a 0", Toast.LENGTH_SHORT).show();

    }

    private void realizarAccionPedido() {
        List<Producto> productosSeleccionados = productoAdapter.getProductosSeleccionados();

        if (productosSeleccionados.isEmpty()) {
            Toast.makeText(ActivityList.this, "No has seleccionado ningún producto", Toast.LENGTH_SHORT).show();
            return;
        }

         gastoTotal = calcularGastoTotal(productosSeleccionados);


        // Validar si el gasto total es igual o menor a 0


        // Validar si algún producto seleccionado tiene cantidad igual a 0
        for (Producto producto : productosSeleccionados) {
            if (producto.getCantidad() == 0) {
                Toast.makeText(ActivityList.this, "No se pueden agregar productos con cantidad 0 al pedido", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String estado = "En Cola";
        String correoRepartidor = "Sin Asignar";
        String pedidoId = pedidosRef.push().getKey();
        float calificacion = 0.0F;
        Pedido pedido = new Pedido(correoUsuario, productosSeleccionados, gastoTotal, latitud, longitud, estado, celular, pedidoId, correoRepartidor, calificacion);
        pedidosRef.child(pedidoId).setValue(pedido);

        editTextGastoTotal.setText(String.valueOf(gastoTotal));

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ActivityList.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pedido_realizado, null);
        dialogBuilder.setView(dialogView);
        TextView textViewTotalGastado = dialogView.findViewById(R.id.textViewTotalGastado);
        textViewTotalGastado.setText(String.valueOf(gastoTotal));
        ImageView gifImageView = dialogView.findViewById(R.id.gifImageView);
        Glide.with(this).asGif().load(R.drawable.gifpedido).into(gifImageView);
        dialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Aquí puedes realizar alguna acción si el usuario presiona el botón "Aceptar"
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

    private double calcularGastoTotal(List<Producto> productosSeleccionados) {
        double gastoTotal = 0;

        for (Producto producto : productosSeleccionados) {
            String precioS = producto.getPrecio();
            int precio = Integer.parseInt(precioS);
            int cantidad = producto.getCantidad();
            gastoTotal += precio * cantidad;
        }

        return gastoTotal;
    }
}
