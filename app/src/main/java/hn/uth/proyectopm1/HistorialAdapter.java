package hn.uth.proyectopm1;



import android.app.Notification;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.PedidoViewHolder> {
    private List<Pedido> pedidos;

    public HistorialAdapter(List<Pedido> pedidos) {
        this.pedidos = pedidos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemhistorial, parent, false);
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

        private ImageView Imagen;
        private TextView textViewCorreo;
        private TextView textViewCelular;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEstado = itemView.findViewById(R.id.textViewEstado);
            textViewGastoTotal = itemView.findViewById(R.id.textViewGastoTotal);
            Imagen = itemView.findViewById(R.id.imageViewImagen);
            textViewCorreo = itemView.findViewById(R.id.textViewCorreo);
            textViewCelular = itemView.findViewById(R.id.textViewCelular);
        }


        public void bind(Pedido pedido) {
            // Asigna los detalles del pedido a los TextViews
            textViewEstado.setText("Estado: " + pedido.getEstado());
            textViewGastoTotal.setText("Gasto Total: " + pedido.getGastoTotal());
            textViewCelular.setText("Celular: " + pedido.getCelular());
            textViewCorreo.setText("Correo: " + pedido.getCorreoUsuario());
            // Configura el botón para la acción de ver la ubicación del pedido



        }
    }




    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
        notifyDataSetChanged(); // Actualizar el adaptador después de cambiar los datos
    }
}

