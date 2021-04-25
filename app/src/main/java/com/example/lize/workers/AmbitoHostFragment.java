package com.example.lize.workers;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
            mAmbitosAdapter = new AmbitosAdapter(mContext, user.getUserAmbitos());
            mAmbitosAdapter.registerAmbitoListener(this);
            dataViewModel.selectAmbito(mContext.getResources().getString(R.string.ambit_default_name));
            mAmbitosRecyclerView.swapAdapter(mAmbitosAdapter,false);
            mAmbitosAdapter.notifyDataSetChanged();
        });

    }

    /**
     * Añadimos un nuevo ambito al DataSet del MainViewModel
     * @param ambitoName Nombre del nuevo ambito a crear
     */
    public void addAmbito(String ambitoName){ dataViewModel.addAmbito(ambitoName); }


    @Override
    public void onAmbitoSelected(Ambito a) {
        dataViewModel.selectAmbito(a.getAmbitoName());
    }
}
