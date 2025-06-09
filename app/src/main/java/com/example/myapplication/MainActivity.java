package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Intent intent = new Intent(this, MainActivity2.class);
//        intent.putExtra("keyName", "effi");
//        intent.putExtra("keyLast", "profus");
//        startActivity(intent);

        currentUser = null;
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
    }

    public void register(RegisterCallBack callBack){
        EditText email = findViewById(R.id.registerEmail);
        EditText password = findViewById(R.id.registerPassword);
        EditText phone = findViewById(R.id.editTextPhone);
        EditText address = findViewById(R.id.editTextTextPostalAddress);

        String emailS = email.getText().toString();
        String passwordS = password.getText().toString();
        String phoneS = phone.getText().toString();
        String addressS = address.getText().toString();

        User user = new User(emailS, passwordS, phoneS, addressS);

        if (emailS.isEmpty() || passwordS.isEmpty() || phoneS.isEmpty() || addressS.isEmpty()) {
            Toast.makeText(this, "Email, password, phone number, address must not be empty", Toast.LENGTH_SHORT).show();
            callBack.onRegisterResult(false);
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailS, passwordS)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = user;
                            writeToDB(user, passwordS);
                            Toast.makeText(MainActivity.this, "Register Ok!", Toast.LENGTH_LONG).show();
                            callBack.onRegisterResult(true);
                        } else {
                            Toast.makeText(MainActivity.this, "Register Failed, Try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void login(LoginCallback callback){
        EditText email = findViewById(R.id.loginEmail);
        EditText password = findViewById(R.id.loginPassword);

        String emailS = email.getText().toString();
        String passwordS = password.getText().toString();

        if (emailS.isEmpty() || passwordS.isEmpty()) {
            Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show();
            callback.onLoginResult(false);
            return;
        }

        mAuth.signInWithEmailAndPassword(emailS, passwordS)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            readFromDB(passwordS, user -> {
                                Toast.makeText(MainActivity.this, "Login successful for: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                callback.onLoginResult(true);
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "Login Failed, Try again", Toast.LENGTH_LONG).show();
                            callback.onLoginResult(false);
                        }
                    }
                });

    }

    public void writeToDB(User user, String keyPassword){
        myRef.child(keyPassword).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "DB write successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "DB write failed", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void readFromDB(String password, UserFetchCallback callback) {
        DatabaseReference userRef = myRef.child(password);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        currentUser = user;
                        callback.onUserFetched(user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }


}