package hn.uth.proyectopm1;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PedidoAdapterR extends RecyclerView.Adapter<PedidoAdapterR.PedidoViewHolder> {
    private List<Pedido> pedidos;
    private static final String CHANNEL_ID = "pedido_channel_id";

    public PedidoAdapterR(List<Pedido> pedidos) {
        this.pedidos = pedidos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedidor, parent, false);
        return new PedidoViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        holder.bind(pedido);
        String estado = pedido.getEstado();

        // Establecer la imagen correspondiente al estado
        if ("En Curso".equals(estado)) {
            holder.Imagen.setImageResource(R.drawable.curso);
        } else if ("En Cola".equals(estado)) {
            holder.Imagen.setImageResource(R.drawable.cola);
        } else if ("Completado".equals(estado)) {
            holder.Imagen.setImageResource(R.drawable.finalizado);
        } else {
            // Estado desconocido o imagen predeterminada en caso de que no haya coincidencia
            holder.Imagen.setImageResource(R.drawable.pochoclo);
        }

    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewEstado;
        private TextView textViewGastoTotal;
        private Button btnUbicacion;

        private Button btnAceptar;
        private ImageView Imagen;
        private TextView textViewCorreo;
        private TextView textViewCelular;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEstado = itemView.findViewById(R.id.textViewEstado);
            textViewGastoTotal = itemView.findViewById(R.id.textViewGastoTotal);
            btnUbicacion = itemView.findViewById(R.id.btnUbicacion);
            Imagen = itemView.findViewById(R.id.imageViewImagen);
            textViewCorreo = itemView.findViewById(R.id.textViewCorreo);
            textViewCelular = itemView.findViewById(R.id.textViewCelular);
            btnAceptar = itemView.findViewById(R.id.btnValorar);
        }


        public void bind(Pedido pedido) {
            // Asigna los detalles del pedido a los TextViews
            textViewEstado.setText("Estado: " + pedido.getEstado());
            textViewGastoTotal.setText("Gasto Total: " + pedido.getGastoTotal());
            textViewCelular.setText("Celular: " + pedido.getCelular());
            textViewCorreo.setText("Correo: " + pedido.getCorreoUsuario());
            // Configura el botón para la acción de ver la ubicación del pedido
            btnUbicacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double latitud = Double.parseDouble(pedido.getLatitud());
                    double longitud = Double.parseDouble(pedido.getLongitud());
                    Intent rutaIntent = new Intent(itemView.getContext(), rutaActivity.class);
                    rutaIntent.putExtra("latitud", latitud);
                    rutaIntent.putExtra("longitud", longitud);
                    itemView.getContext().startActivity(rutaIntent);
                }
            });

            btnAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String correoUsuario = currentUser.getEmail();
                    // Busca el pedido en la base de datos basado en su ID
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("pedidos");
                    Query query = usersRef.orderByChild("id").equalTo(pedido.getId());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                DatabaseReference usuarioRef = userSnapshot.getRef();
                                usuarioRef.child("estado").setValue("En Curso");
                                usuarioRef.child("correoRepartidor").setValue(correoUsuario)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                double latitud = Double.parseDouble(pedido.getLatitud());
                                                double longitud = Double.parseDouble(pedido.getLongitud());
                                                String id=pedido.getId();
                                                Intent mapaIntent = new Intent(itemView.getContext(), MapsActivity.class);
                                                mapaIntent.putExtra("latitud", latitud);
                                                mapaIntent.putExtra("longitud", longitud);
                                                mapaIntent.putExtra("id", id);

                                                itemView.getContext().startActivity(mapaIntent);


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




    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
        notifyDataSetChanged(); // Actualizar el adaptador después de cambiar los datos
    }


}

