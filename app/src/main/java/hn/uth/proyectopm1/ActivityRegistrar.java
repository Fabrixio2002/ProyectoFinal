package hn.uth.proyectopm1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityRegistrar extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText editTextEmail,editTextPassword;
    Button btn_Registrar;

    EditText editTextCelular;
    private RadioGroup radioGroupRoles;


    private String selectedRol; // Variable para almacenar la opción seleccionada
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        editTextCelular=findViewById(R.id.editTextCelular);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail=findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);
        btn_Registrar=findViewById(R.id.btn_Registrar);
        radioGroupRoles = findViewById(R.id.radioGroupRoles);



        btn_Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegistrarUsuario();

            }
        });


        // Escucha el evento de selección del RadioGroup
        radioGroupRoles.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Obtiene el ID del RadioButton seleccionado
                if (checkedId == R.id.radioButtonCliente) {
                    selectedRol = "Cliente";
                } else if (checkedId == R.id.radioButtonRepartidor) {
                    selectedRol = "Repartidor";
                }
            }
        });



    }



    public void abrirNuevoActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

//Este metodo registra el metodo ademas de que guarda el email en la base de datos
    private void RegistrarUsuario() {
        //Obtenbiendo los valores con un metodo que tenemos.
        String email = getEmailFromInput();
        String password = getPasswordFromInput();
// Código en tu primera actividad



//Validaciones
        if (password.isEmpty()) {
            // El campo de contraseña está vacío
            Toast.makeText(ActivityRegistrar.this, "Ingrese una contraseña", Toast.LENGTH_SHORT).show();
            return; // Salir del método sin iniciar sesión
        }
        if (email.isEmpty()) {
            // El campo de contraseña está vacío
            Toast.makeText(ActivityRegistrar.this, "Ingrese Su Correo", Toast.LENGTH_SHORT).show();
            return; // Salir del método sin iniciar sesión
        }
        //usamos un metodo con un objeto instanciado de firebase.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // El registro fue exitoso
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Mandando Correo de Verificacion usando un metodo de firebase
                            user.sendEmailVerification();
                            //Guardando Base de datos le usuario
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Usuario");                            String email = user.getEmail();
                            String descripcion = "Agrega Descripcion";
                            String fotoUrl = "URL de la foto del usuario";
                            String celular = String.valueOf(editTextCelular.getText());
                            String latitud="Ingrese Latitud";
                            String longitud="Ingrese Longitud";
                            Intent intent = new Intent(getApplicationContext(), SelectLocationActivity.class);
                            startActivity(intent);

                            usersRef.push().setValue(new Usuario(email, descripcion, fotoUrl,celular,latitud,longitud,selectedRol))
                                    .addOnCompleteListener(saveTask  -> {
                                        if (saveTask.isSuccessful()) {
                                            System.out.println("Usuario registrado con éxito en la base de datos.");
                                        } else {
                                            System.err.println("Error al registrar el usuario: " + saveTask.getException().getMessage());
                                        }
                                    });



                            editTextPassword.setText("");
                            editTextEmail.setText("");
                        } else {
                            // El registro falló
                            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegistrar.this);
                            builder.setTitle("Algo salio Mal :(");
                            builder.setMessage("Error Al Crear Cuenta Verifica Tus Datos");
                            builder.setPositiveButton("Entendido", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });


    }

    private String getEmailFromInput() {
        EditText emailEditText = findViewById(R.id.editTextEmail);
        String email = emailEditText.getText().toString().trim();  // Elimina espacios en blanco al inicio y al final

        return email;
    }

    private String getPasswordFromInput() {
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        String password = passwordEditText.getText().toString().trim();  // Elimina espacios en blanco al inicio y al final

        return password;
    }





}

