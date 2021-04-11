package com.example.lize.workers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.lize.R;
import com.example.lize.adapters.FolderAdapter;
import com.example.lize.adapters.NoteAdapter;
import com.example.lize.data.Folder;
import com.example.lize.data.Note;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;
    private NoteHostFragment noteHostFragment;
    private boolean cardViewType = true;

    // Constructor actividad principal
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtenemos los componentes y registramos Listeners
        this.topAppBar = findViewById(R.id.ambito_material_toolbar);
        this.noteHostFragment = (NoteHostFragment) getFragmentManager().findFragmentById(R.id.notes_host_fragment);

        // TopAppBar menu Item (search, sandwich) Listener
        topAppBar.setOnMenuItemClickListener((item) -> {
            switch (item.getItemId()) {

                case R.id.search:
                    break;

                case R.id.sandwich:
                    cardViewType = !cardViewType;
                    item.setIcon((cardViewType) ? R.drawable.ic_baseline_table_rows_24 :
                            R.drawable.ic_baseline_view_module_24);
                    noteHostFragment.changeNotesView();
                    break;

                default:
                    return false;
            }
            return true;
        });
    }

}