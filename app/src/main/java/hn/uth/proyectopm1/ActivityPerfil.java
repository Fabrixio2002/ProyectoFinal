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

public class ActivityPerfil extends AppCompatActivity {

        private Button btn_guardar, btn_foto;
        private EditText ET_Descripcion, ET_Celular;
        private ImageView IV_perfil;
        private static final int PICK_IMAGE_REQUEST = 1;
        private FirebaseAuth mAuth;
        private StorageReference storageReference;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_perfil);

            btn_guardar = findViewById(R.id.btn_GuardarP);
            ET_Descripcion = findViewById(R.id.ET_NombreP);
            ET_Celular = findViewById(R.id.ET_PrecioP);
            btn_foto = findViewById(R.id.btn_fotoP);
            IV_perfil = findViewById(R.id.IV_Producto);

            mAuth = FirebaseAuth.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference("Imagenes");

            btn_guardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    String nuevoCel = ET_Celular.getText().toString();
                    String nuevaDes = ET_Descripcion.getText().toString();
                    String email = currentUser.getEmail();
                    actualizarPerfil(email, nuevaDes, nuevoCel);
                }
            });

            btn_foto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            });

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {
                String email = currentUser.getEmail();

                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Usuario");
                Query query = usersRef.orderByChild("email").equalTo(email);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                Usuario usuario = userSnapshot.getValue(Usuario.class);
                                if (usuario != null) {
                                    String descripcion = usuario.getDescripcion();
                                    String celular = usuario.getCelular();

                                    ET_Descripcion.setText(descripcion);
                                    ET_Celular.setText(celular);
                                    String fotoUrl = usuario.getFotoUrl();
                                    Glide.with(ActivityPerfil.this)
                                            .load(fotoUrl)
                                            .placeholder(R.drawable.perfil)
                                            .error(R.drawable.perfil)
                                            .into(IV_perfil);
                                }
                            }
                        } else {
                            // El usuario con el correo electrónico no existe en la base de datos
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Manejar el error si ocurre un problema al leer la base de datos
                        Toast.makeText(ActivityPerfil.this, "Error al obtener datos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        private void uploadImageToFirebaseStorage(Uri imageUri) {
            if (imageUri != null) {
                StorageReference imageRef = storageReference.child("fotos_perfil").child(System.currentTimeMillis() + ".jpg");

                imageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                String email = currentUser.getEmail();
                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Usuario");
                                Query query = usersRef.orderByChild("email").equalTo(email);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                            DatabaseReference usuarioRef = userSnapshot.getRef();
                                            usuarioRef.child("fotoUrl").setValue(uri.toString())
                                                    .addOnCompleteListener(updateTask -> {
                                                        if (updateTask.isSuccessful()) {
                                                            Intent intent = new Intent();
                                                            intent.putExtra("imagenActualizada", uri.toString());
                                                            setResult(Activity.RESULT_OK, intent);
                                                            finish();
                                                            Toast.makeText(ActivityPerfil.this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            // Error al guardar la URL de la imagen
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Manejar el error si ocurre un problema al leer la base de datos
                                    }
                                });
                            });
                        })
                        .addOnFailureListener(exception -> {
                            // Manejar el error si ocurre un problema al subir la imagen al Storage
                        });
            } else {
                // La imagen seleccionada es nula
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
                IV_perfil.setImageURI(selectedImageUri);
                uploadImageToFirebaseStorage(selectedImageUri);
            }
        }

        private void actualizarPerfil(String correo, String nuevaDescripcion, String nuevoCelular) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Usuario");
            Query query = usersRef.orderByChild("email").equalTo(correo);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        DatabaseReference usuarioRef = userSnapshot.getRef();
                        usuarioRef.child("descripcion").setValue(nuevaDescripcion);
                        usuarioRef.child("celular").setValue(nuevoCelular)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(ActivityPerfil.this, "Datos Actualizados", Toast.LENGTH_SHORT).show();
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
    }















