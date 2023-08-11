package hn.uth.proyectopm1;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {
    private List<Producto> productos;
    private List<Producto> productosSeleccionados;
    private OnPedidoClickListener pedidoClickListener;
    private List<EditText> cantidadEditTextList = new ArrayList<>();

    public ProductoAdapter() {
        productos = new ArrayList<>();
        productosSeleccionados = new ArrayList<>();
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
        notifyDataSetChanged();
    }

    public void setOnPedidoClickListener(OnPedidoClickListener listener) {
        this.pedidoClickListener = listener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductoViewHolder holder, final int position) {
        final Producto producto = productos.get(position);
        holder.nombreTextView.setText(producto.getNombre());
        holder.precioTextView.setText(String.valueOf(producto.getPrecio()));
        holder.descripcionTextView.setText(producto.getDescripcion());
        Picasso.get().load(producto.getFotoUrl()).into(holder.fotoImageView);
        holder.etCantidad.setText(String.valueOf(producto.getCantidad()));
        cantidadEditTextList.add(holder.etCantidad);

        holder.btnSeleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productosSeleccionados.contains(producto)) {
                    productosSeleccionados.remove(producto);
                    // Cambia el color del botón cuando se deseleccione el producto
                    holder.btnSeleccionar.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.colorButtonNormal));
                    holder.btnSeleccionar.setText("Agregar");
                }
                else {
                    productosSeleccionados.add(producto);
                    // Cambia el color del botón cuando se seleccione el producto
                    holder.btnSeleccionar.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.colorButtonSelected));
                    holder.btnSeleccionar.setText("Quitar");
                }
            }
        });

        holder.etCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String cantidadStr = editable.toString();
                int cantidad;

                try {
                    cantidad = Integer.parseInt(cantidadStr);
                    if (cantidad < 0) {
                        // Mostrar un mensaje de error si la cantidad es menor a 0
                        return;
                    } else if (cantidad == 0 && productosSeleccionados.contains(producto)) {
                        // Mostrar un mensaje de error si la cantidad es igual a 0 y el producto está seleccionado
                        pedidoClickListener.onProductoSeleccionadoConCantidadCero();
                        return;
                    }
                } catch (NumberFormatException e) {
                    // Mostrar un mensaje de error si la cantidad no es un número válido
                    return;
                }

                // Si la cantidad es válida y mayor a 0, asignarla al producto
                producto.setCantidad(cantidad);
            }
        });


    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public List<Producto> getProductosSeleccionados() {
        return productosSeleccionados;
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreTextView;
        public TextView precioTextView;
        public TextView descripcionTextView;
        public ImageView fotoImageView;
        public Button btnSeleccionar;

        public EditText etCantidad;
        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            precioTextView = itemView.findViewById(R.id.precioTextView);
            descripcionTextView = itemView.findViewById(R.id.descripcionTextView);
            fotoImageView = itemView.findViewById(R.id.fotoImageView);
            btnSeleccionar = itemView.findViewById(R.id.btnSeleccionar);
             etCantidad = itemView.findViewById(R.id.etCantidad);

        }
    }

    public interface OnPedidoClickListener {
        void onPedidoClick(List<Producto> productos);
        void onProductoSeleccionadoConCantidadCero();

    }
}
