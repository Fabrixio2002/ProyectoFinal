package hn.uth.proyectopm1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ActivityAgregarProducto extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    Button btn_GuardarP;
    Button btn_fotoP;
    EditText ET_NombreP;
    EditText ET_PrecioP;
    EditText ET_DesP;
    ImageView IV_Producto;
    String idP;
    private static final int REQUEST_IMAGE_PICK = 1;
    private Uri imageUri;
    private boolean isDataAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        btn_GuardarP = findViewById(R.id.btn_GuardarP);
        ET_NombreP = findViewById(R.id.ET_NombreP);
        ET_PrecioP = findViewById(R.id.ET_PrecioP);
        ET_DesP = findViewById(R.id.ET_DesP);
        btn_fotoP = findViewById(R.id.btn_fotoP);
        IV_Producto = findViewById(R.id.IV_Producto);

        btn_GuardarP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Nombre = ET_NombreP.getText().toString();
                String Precio = ET_PrecioP.getText().toString();
                String Des = ET_DesP.getText().toString();
                String foto = "Aqui Ingrese Su foto";
                int cantidad=0;
                if (!isDataAdded) {
                    agregarProducto(Nombre, Precio, Des, foto,cantidad);
                    isDataAdded = true;
                } else {
                    Toast.makeText(ActivityAgregarProducto.this, "El dato ya ha sido agregado. Ahora puedes subir la imagen.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_fotoP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataAdded) {
                    openGallery();
                } else {
                    Toast.makeText(ActivityAgregarProducto.this, "Agrega primero el dato del producto.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void agregarProducto(String nombre, String precio, String descripcion, String fotoUrl,  int cantidad) {
        String productoId = mDatabase.child("productos").push().getKey();
        idP = productoId;

        Producto producto = new Producto(nombre, precio, descripcion, fotoUrl,cantidad);

        mDatabase.child("productos").child(productoId).setValue(producto);

        Toast.makeText(this, "Dato Guardado Correctamente", Toast.LENGTH_SHORT).show();
    }

    private void uploadImageToFirebaseStorage(Uri imageUri, String productoId) {
        if (imageUri != null) {
            StorageReference imageRef = storageReference.child("fotos_productos").child(productoId + ".jpg");

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            DatabaseReference productoRef = FirebaseDatabase.getInstance().getReference("productos").child(productoId);
                            productoRef.child("fotoUrl").setValue(uri.toString())
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(ActivityAgregarProducto.this, "Foto del producto actualizada", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ActivityAgregarProducto.this, "Error al guardar la URL de la imagen", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        });
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(ActivityAgregarProducto.this, "Error al subir la imagen al almacenamiento", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(ActivityAgregarProducto.this, "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            IV_Producto.setImageURI(selectedImageUri);
            uploadImageToFirebaseStorage(selectedImageUri, idP);
        }
    }
}