package com.example.lize.workers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.adapters.AmbitosAdapter;
import com.example.lize.data.Ambito;
import com.example.lize.data.User;
import com.example.lize.models.MainViewModel;

public class AmbitoHostFragment extends Fragment implements AmbitosAdapter.AmbitoListener {

    private RecyclerView mAmbitosRecyclerView;
    private RecyclerView.LayoutManager mAmbitosManager;
    private AmbitosAdapter mAmbitosAdapter;
    private Context mContext;

    private MainViewModel dataViewModel;

    /** Inicializa el fragment contenedor de Ambitos. */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ambitos_host_view, container,false);
        this.mAmbitosRecyclerView = root.findViewById(R.id.ambito_recycler_view);
        this.mAmbitosManager = new LinearLayoutManager(mAmbitosRecyclerView.getContext());
        this.mAmbitosRecyclerView.setLayoutManager(mAmbitosManager);
        return root;
    }

    /** Recuperamos la actividad que contiene este Fragmento para poder enlazarlo al NoteViewModel */
    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState){
        super.onViewCreated(root, savedInstanceState);
        this.mContext = root.getContext();

        this.dataViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        dataViewModel.getUserSelected().observe(getViewLifecycleOwner(), (User user) ->{
            mAmbitosAdapter = new AmbitosAdapter(mContext, user.getAmbitos());
            mAmbitosAdapter.registerAmbitoListener(this);
            mAmbitosRecyclerView.swapAdapter(mAmbitosAdapter, false);
            mAmbitosAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Añadimos un nuevo ambito al DataSet del MainViewModel
     * @param ambitoName Nombre del nuevo ambito a crear
     */
    public void addAmbito(String ambitoName, int ambitoColor){
        dataViewModel.addAmbito(ambitoName, ambitoColor);
    }


    public void deleteAmbito(String ambitoID){

    }


    @Override
    public void onAmbitoSelected(String ambitoName) {
        dataViewModel.selectAmbito(ambitoName);
    }

    @Override
    public void onAmbitoHold(View view) {
        showMenuAmbitos(view);
    }


    @SuppressLint("RestrictedApi")
    public void showMenuAmbitos(View view){
        //noinspection RestrictedApi
        MenuBuilder menuBuilder = new MenuBuilder(getContext());
        MenuInflater inflater = new MenuInflater(getContext());
        inflater.inflate(R.menu.ambito_menu, menuBuilder);
        //noinspection RestrictedApi
        MenuPopupHelper optionsMenu = new MenuPopupHelper(getContext(), menuBuilder, view);
        //noinspection RestrictedApi
        optionsMenu.setForceShowIcon(true);

        // Set Item Click Listener
        //noinspection RestrictedApi
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.edit_ambito:
                        //TODO: EditarAmbito
                        return true;

                    case R.id.delete_ambito:
                        //TODO: EliminarAmbito
                        return true;

                    default:
                        return false;

                }
            }

            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu){}
        });

        optionsMenu.show();
    }


}
