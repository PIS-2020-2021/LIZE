package com.example.lize.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lize.R;
import com.example.lize.data.Ambito;
import java.util.ArrayList;

public class AmbitosAdapter extends RecyclerView.Adapter<AmbitosAdapter.AmbitoHolder> {

    private final Context mContext;
    private final ArrayList<Ambito> mAmbitos;
    private final ArrayList<AmbitosAdapter.AmbitoListener> ambitoListeners;

    /* Custom Ambito onClick Listener */
    public interface AmbitoListener{
        void onAmbitoSelected(String ambitoName);
        void onAmbitoHold(View view);
    }

    /**
     * Method for registering a Ambito onClick listener
     * @param listener Observer which knows when the recyclerViewAmbito is clicked.
     */
    public void registerAmbitoListener(AmbitosAdapter.AmbitoListener listener){ ambitoListeners.add(listener); }

    /**
     * Constructor que pasa el listado de ambitos i el contexto.
     * @param context contexto de la App
     * @param ambitos ArrayList de Ambitos
     */
    public AmbitosAdapter(Context context, ArrayList<Ambito> ambitos){
        this.mAmbitos = ambitos;
        this.mContext = context;
        this.ambitoListeners = new ArrayList<>();
    }


    /**
     * Generador de ViewHolders de ambitos
     * @param parent ViewGroup correspondiente a RecyclerView - contenedor de ambitos
     * @param viewType viewType de vista de ViewHolder
     * @return El nuevo ViewHolder
     */
    @NonNull
    @Override
    public AmbitoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new AmbitoHolder(LayoutInflater.from(mContext).inflate(R.layout.ambito_card, parent, false));
        /*
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row,parent,false); //Inflating the layout
        AmbitoHolder vhItem = new AmbitoHolder(v,viewType); //Creating ViewHolder and passing the object of type view
        return vhItem; // Returning the created object
        */
    }

    /**
     * Método requerido que permite enlazar los datos del ambito con el correspondiente ViewHolder.
     * @param holder ViewHolder a quien pasr los datos;
     * @param position Position del Adapter
     */
    @Override
    public void onBindViewHolder(@NonNull AmbitoHolder holder, int position) {
        // Obtenemos los parámetros del Layout correspondiente al holder y modificamos el height
        Ambito currentAmbito = mAmbitos.get(position);
        holder.bindTo(currentAmbito);
    }

    /**
     * Metodo que necesita el adaptador para determinar el tamaño del dataSet
     * @return tamaño del DataSet
     */
    @Override
    public int getItemCount() {
        return mAmbitos.size();
    }


    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them
    public class AmbitoHolder extends RecyclerView.ViewHolder {

        private final TextView mTitleAmbito;

        /**
         * Constructor del ViewHolder correspondiete al layout de ambito_card
         * @param itemView rootView del fichero ambito_card.xml
         */
        public AmbitoHolder(@NonNull View itemView) {
            super(itemView);
            //Inicializamos los componentes del Layout
            mTitleAmbito = (TextView) itemView.findViewById(R.id.ambito_name);
            itemView.setOnClickListener((a)->{
                for (AmbitosAdapter.AmbitoListener listener : ambitoListeners)
                    listener.onAmbitoSelected(mTitleAmbito.getText().toString());
            });

            itemView.setOnLongClickListener(v -> {
                for(AmbitoListener listener : ambitoListeners)
                    listener.onAmbitoHold(v);
                return false;
            });
        }

        public TextView getmTitleAmbito() {
            return mTitleAmbito;
        }

        /**
         * Método para enlazar los datos del ambito con el RecyclerView de este objeto ViewHolder
         * @param currentAmbito Ambito actual
         */
        public void bindTo(Ambito currentAmbito){
            mTitleAmbito.setText(currentAmbito.getName());
        }

    }










}
