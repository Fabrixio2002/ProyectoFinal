package hn.uth.proyectopm1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Button btn_Registrar,btn_InicioS;
    TextView Edit_Contra;
    private FirebaseAuth mAuth;
    private static final String SHARED_PREFS_KEY = "hn.uth.proyectopm1.shared_prefs";
    private static final String EMAIL_KEY = "saved_email";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Edit_Contra=findViewById(R.id.Edit_Contra);
        btn_Registrar=(Button) findViewById(R.id.btn_Registrar);
        btn_InicioS=(Button) findViewById(R.id.btn_InicioS);
        ////Botones click accion

        // Recuperar el correo electrónico guardado en Preferencias Compartidas
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString(EMAIL_KEY, "");

        // Establecer el correo electrónico guardado en el campo de correo electrónico
        EditText emailEditText = findViewById(R.id.editTextUsername);
        emailEditText.setText(savedEmail);
        FirebaseUser currentUser = mAuth.getCurrentUser();


        btn_Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityRegistrar.class);
                startActivity(intent);

            }
        });
        btn_InicioS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    IniciarSesion();


            }
        });

        btn_InicioS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IniciarSesion();

            }
        });

        Edit_Contra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            RestablecerContra();
            }
        });

    }

    private String getEmailFromInput() {
        EditText emailEditText = findViewById(R.id.editTextUsername);
        String email = emailEditText.getText().toString().trim();  // Elimina espacios en blanco al inicio y al final


        return email;
    }

    private String getPasswordFromInput() {
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        String password = passwordEditText.getText().toString().trim();  // Elimina espacios en blanco al inicio y al final


        return password;
    }


    private void IniciarSesion() {
        String email = getEmailFromInput();
        String password = getPasswordFromInput();

        if (password.isEmpty()) {
            // Mostrar mensaje de error o realizar alguna acción
            return;
        }

        if (email.isEmpty()) {
            // Mostrar mensaje de error o realizar alguna acción
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Verificar si el correo electrónico ha sido verificado
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                // El correo electrónico ha sido verificado, proceder con el inicio de sesión
                                // Guardar el correo electrónico en las Preferencias Compartidas
                                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(EMAIL_KEY, email);
                                editor.apply();
                                // El correo electrónico ha sido verificado, proceder con el inicio de sesión
                                // Resto del código para acceder a la base de datos y verificar el rol del usuario
                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Usuario");
                                Query query = usersRef.orderByChild("email").equalTo(email);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // Obtener el primer resultado, ya que el correo electrónico debe ser único
                                            DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();

                                            // Obtiene el valor del campo "rol"
                                            String rol = userSnapshot.child("rol").getValue(String.class);

                                            // Verifica el rol del usuario
                                            if (rol.equals("Cliente")) {
                                                // El usuario tiene rol de cliente
                                                Intent intent = new Intent(getApplicationContext(), ActivityHome.class);
                                                startActivity(intent);
                                            } else if (rol.equals("Repartidor")) {
                                                // El usuario tiene rol de repartidor
                                                Intent intent = new Intent(getApplicationContext(), ActivityHomeR.class);
                                                startActivity(intent);
                                            } else if (rol.equals("Administrador")) {
                                                // El usuario tiene rol de repartidor
                                                Intent intent = new Intent(getApplicationContext(), ActivityHomeA.class);
                                                startActivity(intent);
                                            }
                                            else {
                                                // El rol no es reconocido o no está definido correctamente
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setTitle("Rol desconocido");
                                                builder.setMessage("El rol del usuario no está definido correctamente.");
                                                builder.setPositiveButton("Aceptar", null);
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            }
                                        } else {
                                            // No se encontró información del usuario en la base de datos
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                            builder.setTitle("Usuario no encontrado");
                                            builder.setMessage("No se encontró información del usuario en la base de datos.");
                                            builder.setPositiveButton("Aceptar", null);
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Ocurrió un error al acceder a la base de datos
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        builder.setTitle("Error de base de datos");
                                        builder.setMessage("Ocurrió un error al acceder a la base de datos.");
                                        builder.setPositiveButton("Aceptar", null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                });

                            } else {
                                // El correo electrónico no ha sido verificado, mostrar mensaje de error
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Correo no verificado");
                                builder.setMessage("Por favor, verifica tu correo electrónico antes de iniciar sesión.");
                                builder.setPositiveButton("Aceptar", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();

                                // Cerrar sesión para evitar que el usuario acceda sin verificar el correo
                                mAuth.signOut();
                            }
                        } else {
                            // El inicio de sesión falló
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Algo salió mal :(");
                            builder.setMessage("Error al iniciar sesión");
                            builder.setPositiveButton("Aceptar", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
    }





    private void RestablecerContra() {
        String email = getEmailFromInput();

        // Verificar si el EditText está vacío
        if (TextUtils.isEmpty(email)) {
            // Mostrar un mensaje de error al usuario
            Toast.makeText(MainActivity.this, "Por favor, ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Restablecer Contraseña");
        builder.setMessage("Hemos enviado un Correo para Restablecer tu Contraseña :D");
        builder.setPositiveButton("Aceptar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }




}