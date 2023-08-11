package hn.uth.proyectopm1;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {
    private List<Pedido> pedidos;
    private String selectedProductId;

    private Activity activity; // Agregar este campo para almacenar la referencia a la Activity
    static final int RATING_REQUEST_CODE = 1;
    public PedidoAdapter(Activity activity, List<Pedido> pedidos) {
        this.activity = activity;
        this.pedidos = pedidos;
        notifyDataSetChanged();
    }

    // Método para obtener la lista de pedidos
    public List<Pedido> getPedidos() {
        return pedidos;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        holder.bind(pedido);
        String estado = pedido.getEstado();
        String id= pedido.getId();

        // Establecer la imagen correspondiente al estado
        if ("En Curso".equals(estado)) {
            holder.Imagen.setImageResource(R.drawable.curso);
            holder.btnValorar.setVisibility(View.INVISIBLE);


        } else if ("En Cola".equals(estado)) {
            holder.Imagen.setImageResource(R.drawable.cola);
            holder.btnValorar.setVisibility(View.INVISIBLE);
            holder.btnUbicacion.setVisibility(View.INVISIBLE);

        } else if ("Completado".equals(estado)) {
            holder.Imagen.setImageResource(R.drawable.finalizado);
            holder.btnUbicacion.setVisibility(View.INVISIBLE);
            holder.btnValorar.setVisibility(View.VISIBLE);

        } else {
            // Estado desconocido o imagen predeterminada en caso de que no haya coincidencia
            holder.Imagen.setImageResource(R.drawable.pochoclo);
            holder.btnValorar.setVisibility(View.INVISIBLE);
        }
        holder.btnValorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtén el ID del pedido
                String productId = pedido.getId();
                Toast.makeText(activity, ""+productId, Toast.LENGTH_SHORT).show();
                // Inicia la RatingActivity con startActivityForResult y envía el ID del pedido
                Intent ratingIntent = new Intent(activity, RatingActivity.class);
                ratingIntent.putExtra("product_id", productId);
                activity.startActivityForResult(ratingIntent, RATING_REQUEST_CODE);
            }
        });





    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewEstado;
        private TextView textViewGastoTotal;
        private Button btnUbicacion;

        private ImageView Imagen;
        private TextView textViewCorreo;
        private TextView textViewCelular;

        private TextView textViewPedidoId;

        private Button btnValorar;
        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEstado = itemView.findViewById(R.id.textViewEstado);
            textViewGastoTotal = itemView.findViewById(R.id.textViewGastoTotal);
            btnUbicacion = itemView.findViewById(R.id.btnUbicacion);
            Imagen=itemView.findViewById(R.id.imageViewImagen);
            textViewCorreo=itemView.findViewById(R.id.textViewCorreo);
            textViewCelular=itemView.findViewById(R.id.textViewCelular);
            textViewPedidoId=itemView.findViewById(R.id.textViewPedidoId);
            btnValorar=itemView.findViewById(R.id.btnValorar);
        }



        public void bind(Pedido pedido) {
            // Asigna los detalles del pedido a los TextViews
            textViewEstado.setText("Estado: " + pedido.getEstado());
            textViewGastoTotal.setText("Gasto Total: " + pedido.getGastoTotal()+" Lps");
            textViewCelular.setText("Celular: "+ pedido.getCelular());
            textViewCorreo.setText("Repartidor: "+ pedido.getCorreoRepartidor());
            textViewPedidoId.setText("N° Pedido: "+pedido.getId());

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



        }

    }
    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
        notifyDataSetChanged(); // Actualizar el adaptador después de cambiar los datos
    }




}
