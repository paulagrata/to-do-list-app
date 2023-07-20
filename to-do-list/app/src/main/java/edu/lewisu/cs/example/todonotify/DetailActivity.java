package edu.lewisu.cs.example.todonotify;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailActivity extends AppCompatActivity {

    private ToDo mToDo;
    private EditText mTitleField;
    private Spinner mPrioritySpinner;
    private CheckBox mCompleteCheckBox;
    private Button mAddEditButton;
    private String mUserId;
    private String mRef;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleField = findViewById(R.id.title_field);
        mTitleField.addTextChangedListener(new TitleListener());

        mPrioritySpinner = findViewById(R.id.spinner);
        mPrioritySpinner.setOnItemSelectedListener(new PrioritySelect());

        mCompleteCheckBox = findViewById(R.id.complete_checkbox);
        mCompleteCheckBox.setOnClickListener(new CompleteChangeListener());

        mAddEditButton = findViewById(R.id.add_edit_button);

        mUserId = getIntent().getStringExtra("uid"); //this is used when initializing the ToDo

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = getIntent().getStringExtra("ref");

        mToDo = new ToDo(mUserId);


        if (mRef != null) {
            mDatabaseReference = mFirebaseDatabase.getReference().child("to_do").child(mRef);

            //set up listener
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    mToDo = snapshot.getValue(ToDo.class);
                    setUi();
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            };

            //attach listener to database ref
            mDatabaseReference.addValueEventListener(listener);

        } else {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseReference = mFirebaseDatabase.getReference("to_do");
            mAddEditButton.setOnClickListener(new OnAddButtonClick());
        }


    }

    private void setUi(){
        if(mToDo != null) {
            //set components to display detail information
            mTitleField.setText(mToDo.getTitle());
            mPrioritySpinner.setSelection(mToDo.getPriority());
            mCompleteCheckBox.setChecked(mToDo.isComplete());
            mAddEditButton.setText(R.string.update);
            mAddEditButton.setOnClickListener(new OnUpdateButtonClick());
        }
    }


    private class TitleListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mToDo.setTitle(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class CompleteChangeListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(mCompleteCheckBox.isChecked()){
                mToDo.setComplete(true);
            }else{
                mToDo.setComplete(false);
            }
        }
    }

    private class PrioritySelect implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(mToDo != null)
                mToDo.setPriority(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    private class OnAddButtonClick implements View.OnClickListener{
        @Override
        public void onClick(View v){
            mDatabaseReference.push().setValue(mToDo);
            finish();
        }
    }

    private class OnUpdateButtonClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            mDatabaseReference.setValue(mToDo);
            finish();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.delete_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.delete) {
            mDatabaseReference.removeValue();
            finish();
        }else{
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}