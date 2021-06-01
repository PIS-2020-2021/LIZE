package com.example.lize.workers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class AmbitoHostFragment extends Fragment implements AmbitosAdapter.AmbitoListener {

    private RecyclerView mAmbitosRecyclerView;
    private RecyclerView.LayoutManager mAmbitosManager;
    private AmbitosAdapter mAmbitosAdapter;
    private Context mContext;

    private AmbitosAdapter.AmbitoHolder lastAmbitoSel;
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
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
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

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
                                                        ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            try {
                dataViewModel.getUserSelected().getValue().swapAmbitos(fromPosition, toPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            } catch (NullPointerException nullPointerException) {
                Log.w("AmbitoHostFragment", "Failed to drag an drop Ambitos: null user.");
                Log.w("AmbitoHostFragment", "Exception message: " + nullPointerException.getMessage());
            }
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //Nothing to do it here
        }
    };

    /**
     * Seleccionamos un ambito del DataSet del MainViewModel, y aplicamos un cambio de tema de la app.
     * @param ambitoHolder Holder del ambito seleccionado.
     */
    @Override
    public void onAmbitoSelected(AmbitosAdapter.AmbitoHolder ambitoHolder) {
        dataViewModel.selectAmbito(ambitoHolder.getmTitleAmbito().getText().toString());
    }

    /**
     * Seleccionamos un ambito del Dataset a editar y aplicamos los cambios
     * @param ambitoHolder Holder del ambito seleccionado.
     */
    @Override
    public void onEditAmbitoSelected(AmbitosAdapter.AmbitoHolder ambitoHolder) {
        Intent intent = new Intent(mContext, EditAmbitoActivity.class);
        intent.putIntegerArrayListExtra("Ambitos", dataViewModel.getUserSelected().getValue().getColorsTaken());
        intent.putExtra("OldColor", ambitoHolder.getAmbitomColor());
        intent.putExtra("OldName", ambitoHolder.getmTitleAmbito().getText());
        intent.putExtra("SelfID", ambitoHolder.getmAmbitoID());
        requireActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_EDIT_AMBITO);
    }

    /**
     * Ambito a eleminar del Dataset
     * @param ambitoHolder Holder del ambito seleccionado.
     */
    @Override
    public void onAmbitoDelete(AmbitosAdapter.AmbitoHolder ambitoHolder) {
        dataViewModel.deleteAmbito(ambitoHolder.getmAmbitoID());
    }

}






