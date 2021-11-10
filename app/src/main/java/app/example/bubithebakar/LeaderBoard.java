package app.example.bubithebakar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import app.example.bubithebakar.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LeaderBoard extends AppCompatActivity {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference();
    private final ArrayList<User> users = new ArrayList<>();
    private ListView lvUsers;
    private BottomNavigationView btnNav;
    private Intent intent;
    private SharedPreferences mPrefs;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_leaderboard);

        lvUsers = findViewById(R.id.lvUsers);
        btnNav = findViewById(R.id.btnNav);

        GlobalFunctions globalFunction = new GlobalFunctions(this);
        globalFunction.checkWifi();
        globalFunction.hideSystemUI();

        user = readUser();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dst : dataSnapshot.getChildren()) {
                    User u = dst.getValue(User.class);
                    users.add(u);
                }
                refreshListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref.child("Users").addValueEventListener(postListener);

        btnNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_data:
                        intent = new Intent(LeaderBoard.this, Data.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_dayData:
                        intent = new Intent(LeaderBoard.this, DataHistory.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_logout:
                        Intent myIntent = new Intent(LeaderBoard.this, LoginScreen.class);
                        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember", "no");
                        editor.apply();
                        startActivity(myIntent);
                        break;
                }
                return false;
            }
        });

    }
    void refreshListView() {
        LeaderBoardAdapter arrayAdapter = new LeaderBoardAdapter(this, R.layout.row, users,user);
        arrayAdapter.notifyDataSetChanged();
        lvUsers.setAdapter(arrayAdapter);
    }
    private User readUser() {
        mPrefs = getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("user", "");
        User user = gson.fromJson(json, User.class);
        return user;
    }
}