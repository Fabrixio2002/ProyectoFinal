package hn.uth.proyectopm1;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdaptadorAdmin extends RecyclerView.Adapter<AdaptadorAdmin.PedidoViewHolder> {
    private List<Pedido> pedidos;

    private Activity activity;
    static final int RATING_REQUEST_CODE = 1;

    public AdaptadorAdmin(Activity activity, List<Pedido> pedidos) {
        this.activity = activity;
        this.pedidos = pedidos;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido2, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        holder.bind(pedido);
        String estado = pedido.getEstado();

        if ("En Curso".equals(estado)) {
            holder.Imagen.setImageResource(R.drawable.curso);
        } else if ("En Cola".equals(estado)) {
            holder.Imagen.setImageResource(R.drawable.cola);
        } else if ("Completado".equals(estado)) {
            holder.Imagen.setImageResource(R.drawable.finalizado);
        } else {
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
        private TextView textViewPedidoId;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEstado = itemView.findViewById(R.id.textViewEstado);
            textViewGastoTotal = itemView.findViewById(R.id.textViewGastoTotal);
            Imagen = itemView.findViewById(R.id.imageViewImagen);
            textViewCorreo = itemView.findViewById(R.id.textViewCorreo);
            textViewCelular = itemView.findViewById(R.id.textViewCelular);
            textViewPedidoId = itemView.findViewById(R.id.textViewPedidoId);
        }

        public void bind(Pedido pedido) {
            textViewEstado.setText("Estado: " + pedido.getEstado());
            textViewGastoTotal.setText("Gasto Total: " + pedido.getGastoTotal());
            textViewCelular.setText("Celular: " + pedido.getCelular());
            textViewCorreo.setText("Usuario: " + pedido.getCorreoUsuario());
            textViewPedidoId.setText("N° Pedido: " + pedido.getId());
        }
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
        notifyDataSetChanged();
    }
}
