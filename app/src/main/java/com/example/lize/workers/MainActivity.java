package com.example.lize.workers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.lize.R;
import com.example.lize.adapters.FolderAdapter;
import com.example.lize.adapters.NoteAdapter;
import com.example.lize.data.Folder;
import com.example.lize.data.Note;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // DataSet
    private ArrayList<Folder> mFoldersData;

    // RecyclerView de Notas y Carpetas
    private RecyclerView mFoldersRecyclerView;
    private FolderAdapter mFolderAdapter;

    // Constructor actividad principal
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}