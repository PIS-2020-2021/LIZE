package com.example.lize.workers;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.adapters.AmbitosAdapter;
import com.example.lize.data.Ambito;
import com.example.lize.data.User;
import com.example.lize.models.MainViewModel;
import com.example.lize.utils.Preferences;

public class MoveHostFragment{}
/*
        extends Fragment {

    private RecyclerView moveAmbitosRecyclerView;
    private RecyclerView moveFoldersRecyclerView;
    private TextView moveText;
    private Button moveBtn;

    private RecyclerView.LayoutManager mAmbitosManager;
    private MoveHostFragment.MoveAdapter mAdapter;
    private Context mContext;

    private MainViewModel dataViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.popup_move, container,false);
        this.moveAmbitosRecyclerView = root.findViewById(R.id.move_note_ambitos);
        this.moveFoldersRecyclerView = root.findViewById(R.id.move_note_folders);
        this.moveText = root.findViewById(R.id.move_text);
        this.moveBtn = root.findViewById(R.id.move_button);
        this.mContext = root.getContext();

        this.mAmbitosManager = new LinearLayoutManager(mContext);
        this.moveAmbitosRecyclerView.setLayoutManager(mAmbitosManager);
        this.moveFoldersRecyclerView.setLayoutManager(mAmbitosManager);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState){
        super.onViewCreated(root, savedInstanceState);
        this.mContext = root.getContext();

        this.dataViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Actualizamos la lista de Ámbitos cuando se seleccione un Usuario
        dataViewModel.getUserSelected().observe(getViewLifecycleOwner(), (@NonNull User user) ->{
            mAmbitosAdapter = new AmbitosAdapter(mContext, user.getAmbitos());
            mAmbitosAdapter.registerAmbitoListener(this);
            mAmbitosRecyclerView.swapAdapter(mAmbitosAdapter, false);
            mAmbitosAdapter.notifyDataSetChanged();
        });

        dataViewModel.getAmbitoSelected().observe(getViewLifecycleOwner(), (@NonNull Ambito ambito) ->{
            Preferences.setSelectedTheme(ambito.getColor());
        });

        //Seteamos los componentes para poder elegir el orden de los Ambitos
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mAmbitosRecyclerView);
    }


    public class MoveAdapter {

    }
}*/
