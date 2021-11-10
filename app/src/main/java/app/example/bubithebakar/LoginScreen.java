package app.example.bubithebakar;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import app.example.bubithebakar.R;

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
import com.google.gson.Gson;

/*
    TODO

    code arrangement (not using the option - yadani)
    user guide?

    Done:

    current user highlight in leaderboard
    remove bottom buttons home button,back button
    auto wifi detection (show no wifi screen when no wifi)
    change date format
    hebrew username only!
    better interface
    make global functions

    clear sharedpref:

    File sharedPreferenceFile = new File("/data/data/"+ getPackageName()+ "/shared_prefs/");
        File[] listFiles = sharedPreferenceFile.listFiles();
        for (File file : listFiles) {
            file.delete();
        }

        {
    {
  "rules": {
    ".read": "auth.uid == 'LA3t4ciEUqPXBq9AtfC1gcnuLmc2'",
    ".write": "auth.uid == 'LA3t4ciEUqPXBq9AtfC1gcnuLmc2'"
  }
}
}
 */
/**
 * This activity is the login screen for the user where he can log-in with his user and password or register.
 * When you log-in to the account the activity reads from the database if the data that was typed is correct.
 * When you register the activity writes the new user's data in the firebase.
 */
public class LoginScreen extends AppCompatActivity {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference();
    private EditText etUserName, etPassword, etDUserName, etDPassword, etDPasswordVerify;
    private RelativeLayout btnLogin, btnRegister, btnDRegister;
    private CheckBox cbRemember;
    private SharedPreferences mPrefs;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    /**
     * With this function you can do the following things: login and register an account.
     * This function starts when the activity is opened.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        signIn("talba225@gmail.com","taywanNumberOn3");

        setContentView(R.layout.lo_login_screen);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        cbRemember = findViewById(R.id.cbRemember);

        GlobalFunctions globalFunction = new GlobalFunctions(this);
        globalFunction.checkWifi();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query q = ref.child("Users").orderByValue();
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (etUserName.getText().toString().length() != 0 && etPassword.getText().toString().length() != 0) {
                            for (DataSnapshot dst : dataSnapshot.getChildren()) {
                                User u = dst.getValue(User.class);
                                if (u.getUserName().equals(etUserName.getText().toString()) && u.getPassword().equals(etPassword.getText().toString())) {
                                    if (cbRemember.isChecked()) {
                                        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("remember", "yes");
                                        editor.apply();
                                    } else {
                                        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("remember", "no");
                                        editor.apply();
                                    }
                                    insertUser(u);
                                    Intent myIntent = new Intent(LoginScreen.this, Data.class);
                                    showToastMessage("נכנס!", 3000);
                                    startActivity(myIntent);
                                    return;
                                }
                            }
                            showToastMessage("שם משתמש או סיסמה לא נכונים", 1000);
                            etUserName.getText().clear();
                            etPassword.getText().clear();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog d = new Dialog(LoginScreen.this);
                d.setContentView(R.layout.d_register);
                d.show();
                etDUserName = d.findViewById(R.id.etUserName);
                etDPassword = d.findViewById(R.id.etPassword);
                etDPasswordVerify = d.findViewById(R.id.etPasswordVerify);
                btnDRegister = d.findViewById(R.id.btnRegister);
                btnDRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etDUserName.getText().toString().length() != 0 && etDPassword.getText().toString().length() != 0) {
                            if (etDPassword.getText().toString().length() < 8) {
                                showToastMessage("הסיסמה חייבת להכיל לפחות 8 תווים", 1000);
                                return;
                            }
                            Query q = ref.child("Users").orderByValue();
                            q.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean error = false;
                                    for (DataSnapshot dst : dataSnapshot.getChildren()) {
                                        User u = dst.getValue(User.class);
                                        if (u.getUserName().equals(etDUserName.getText().toString())) {
                                            showToastMessage("שם המשתמש כבר קיים במערכת", 1000);
                                            etDUserName.getText().clear();
                                            etDPassword.getText().clear();
                                            etDPasswordVerify.getText().clear();
                                            error = true;
                                        } else if (!etDPassword.getText().toString().equals(etDPasswordVerify.getText().toString())) {
                                            showToastMessage("אימות סיסמה אינו נכון", 1000);
                                            etDUserName.getText().clear();
                                            etDPassword.getText().clear();
                                            etDPasswordVerify.getText().clear();
                                            error = true;
                                        }
                                    }
                                    if (!error) {
                                        User u = new User(etDUserName.getText().toString(), etDPassword.getText().toString());
                                        ref.child("Users").child(etDUserName.getText().toString()).setValue(u);
                                        insertUser(u);
                                        Intent myIntent = new Intent(LoginScreen.this, Data.class);
                                        myIntent.putExtra("user", u);
                                        startActivity(myIntent);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
            }
        });
    }
/*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

        }
    }
*/
    /**
     * This function shows a toast message.
     * It is called every time the programmer wants to put a toast in the screen.
     *
     * @param text     is the text of the toast.
     * @param duration is the time duration of the toast.
     */
    public void showToastMessage(String text, int duration) {
        final Toast toast = Toast.makeText(LoginScreen.this, text, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duration);
    }

    private void insertUser(User user) {
        mPrefs = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("user", json);
        prefsEditor.commit();
    }

    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END sign_in_with_email]
    }
}
