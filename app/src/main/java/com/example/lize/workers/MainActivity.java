package com.example.lize.workers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.os.Bundle;

import com.example.lize.R;
import com.example.lize.adapters.NotesAdapter;
import com.example.lize.data.Note;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Member Variables
    private RecyclerView mRecyclerView;
    private ArrayList<Note> mNotesData;
    private NotesAdapter mAdapter;

    // Constructor actividad principal
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the RecyclerView
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        //Set the Layout Manager
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        //Initialize the ArrayLIst that will contain the data
        mNotesData = new ArrayList<>();

        //Initialize the adapter and set it ot the RecyclerView
        mAdapter = new NotesAdapter(this, mNotesData);
        mRecyclerView.setAdapter(mAdapter);

        //Get the data
        initializeData();
    }

    /**
     * Método para inicializar el dataSet de notas a partir del MOCKUP NOTE DATA, en strings.xml
     */
    private void initializeData() {
        //Get the resources from the XML file
        String[] notesNames = getResources().getStringArray(R.array.notes_title);
        String[] notesBody = getResources().getStringArray(R.array.notes_body);

        //Create the ArrayList of Notes objects with the titles and text data
        for(int i=0; i < notesNames.length; i++){
            mNotesData.add(new Note(notesNames[i], notesBody[i]));
        }

        //Notify the adapter of the change
        mAdapter.notifyDataSetChanged();
    }

}