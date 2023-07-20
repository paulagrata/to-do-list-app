package edu.lewisu.cs.example.todonotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Collections;

public class MainActivity extends AppCompatActivity implements ToDoListAdapter.TodoListAdapterOnClickHandler {
    private ToDoListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String mUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView=findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseApp.initializeApp(this);
        mFirebaseAuth = FirebaseAuth.getInstance();


        ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                (result) -> {
                    if (result.getResultCode() == RESULT_OK) {
                        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                        if (firebaseUser != null){
                            mUserId = firebaseUser.getUid();
                            setAdapter();
                        }
                    }
                    if (result.getResultCode() == RESULT_CANCELED) {
                        finish();
                    }
                });


        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if(user != null){
            mUserId = user.getUid();
        }
        setAdapter();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    mUserId = user.getUid();
                }else{
                    Intent signInIntent = AuthUI.getInstance().
                            createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setTheme(R.style.Theme_ToDoNotify)
                            .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build()))
                            .build();
                    signInLauncher.launch(signInIntent);
                }
            }
        };


        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("uid", mUserId);
                startActivity(intent);
            }
        });

        Intent showNotification = new Intent(MainActivity.this,NotificationAlertReceiver.class);
        showNotification.setAction(NotificationAlertReceiver.ACTION_REVIEW_REMINDERS);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,showNotification,PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+5000,60000, notifyPendingIntent);

    }

    @Override
    public void onClick(int position) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("uid", mUserId);
        DatabaseReference ref = mAdapter.getRef(position);
        String id = ref.getKey();
        detailIntent.putExtra("ref", id);
        startActivity(detailIntent);
    }


    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.sign_out) {
            AuthUI.getInstance().signOut(this);
            return true;
        } else if (item.getItemId() == R.id.notify){
            NotificationUtils.remindUser(this);
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void setAdapter(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query query = firebaseDatabase.getReference().child("to_do").orderByChild("uid").equalTo(mUserId);

        FirebaseRecyclerOptions<ToDo> options =
                new FirebaseRecyclerOptions.Builder<ToDo>()
                        .setQuery(query, ToDo.class)
                        .build();
        mAdapter = new ToDoListAdapter(options, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening(); //previously in onResume
    }
}