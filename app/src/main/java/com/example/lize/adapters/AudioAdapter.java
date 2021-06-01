package com.example.lize.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lize.R;
import com.example.lize.data.Audio;
import com.example.lize.utils.Preferences;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
    private final ArrayList<Audio> localDataSet;
    private final Context parentContext;
    private final playerInterface listener;
    private int sessionID = -1;
    private final HashMap<Integer,Integer> stateReproduction;

    /**
     * Constructor de la clase
     */
    public AudioAdapter(Context current, playerInterface listener) {
        parentContext = current;
        localDataSet = new ArrayList<>();
        this.listener = listener;
        stateReproduction = new HashMap<>();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final LinearLayout audioLayout;
        private final ImageButton playButton;
        private final ImageButton removeButton;
        private final BarVisualizer barVisualizer;

        public ViewHolder(View view ) {
            super(view);
            textView = view.findViewById(R.id.audioTime);
            audioLayout = view.findViewById(R.id.audio_layout);
            playButton = view.findViewById(R.id.playButton);
            barVisualizer = view.findViewById(R.id.blast);
            removeButton = view.findViewById(R.id.removeAudioButton);
        }

        /**
         * Metodo para conseguir el TextView del tiempo del audio
         * @return TextView del audio
         */
        public TextView getTextView() { return textView; }

        /**
         * Metodo para conseguir el Layout del audio
         * @return Layout del audio
         */
        public LinearLayout getLayout() { return audioLayout; }

        /**
         * Metodo para conseguir el botón de Play del audio
         * @return Botón de Play del audio
         */
        public ImageButton getPlayButton() { return playButton; }

        /**
         * Metodo para conseguir la barra de prograso del audio
         * @return Barra de progreso del audio
         */
        public BarVisualizer getProgressBar() { return barVisualizer; }

        /**
         * Metodo para conseguir el botón de cerrar del audio
         * @return Botón de cerrar del audio
         */
        public ImageButton getRemoveButton() { return removeButton; }
    }


    // Create new views (invoked by the layout manager)

    /**
     * Metodo para crear nuevos Views, invocados por el manager del layout
     * @param viewGroup Grupo de vIews
     * @param viewType Tipo de View
     * @return ViewHolder resultante
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Preferences.applySelectedTheme((ContextThemeWrapper) parentContext);
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_attach_audio, viewGroup, false);

        return new ViewHolder(view);
    }

    /**
     * Interfaz del audio player
     */
    public interface playerInterface {
        void startPlaying(int position);
        void pausePlaying(int position);
        void removeAudio(int position);
    }

    /**
     * Metodo para reproducir el audio
     * @param position Posicion del audio en la que lo empezamos a reproducir
     */
    private void playAudio(int position) { listener.startPlaying(position); }

    /**
     * Metodo para pausar el audio
     * @param position Posicion del audio en la que lo hemos pausado
     */
    private void pausePlaying(int position){ listener.pausePlaying(position); }

    /**
     * Metodo para cambiar el contenido de una View, invocado por el manager del Layout
     * @param viewHolder ViewHolder a cambiar
     * @param position Posicion del audio
     */
    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        BarVisualizer barVisualizer = viewHolder.getProgressBar();
        ImageButton playButton = viewHolder.getPlayButton();
        playButton.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_24);

        playButton.setOnClickListener(view -> {
            if (stateReproduction.get(position) == 0) {
                //current value in the text view
                stateReproduction.put(position,1);
                playAudio(position);
                playButton.setBackgroundResource(R.drawable.ic_baseline_pause_circle_filled_24);
                if (sessionID != -1) {
                    try {
                        barVisualizer.show();
                        barVisualizer.setAudioSessionId(sessionID);
                    } catch (IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            } else if (stateReproduction.get(position) == 1) {
                stateReproduction.put(position,0);
                barVisualizer.hide();
                playButton.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_24);
                pausePlaying(position);
            }
        });

        ImageButton removeButton = viewHolder.getRemoveButton();
        removeButton.setOnClickListener(v -> new AlertDialog.Builder(parentContext)
                    .setTitle("¿Estás seguro que deseas eliminarlo?")
                    .setPositiveButton("Borrar", (dialog, which) -> {
                        removeAudio(position);
                        Log.d("MainActivity", "Sending atomic bombs to Jupiter");
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                    }).show());

        long millis = localDataSet.get(position).getDuration();
        viewHolder.getTextView().setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        ));
    }

    /**
     * Metod para conseguir el tamaño del Dataset
     * @return Tamaño del dataset
     */
    @Override
    public int getItemCount() { return localDataSet.size(); }

    /**
     * Metodo para añadir un audio al Dataset
     * @param audio Audio a añadir
     */
    public void addAudio(Audio audio){
        localDataSet.add(audio);
        stateReproduction.put(localDataSet.indexOf(audio),0);
        notifyDataSetChanged();
    }

    /**
     * Metodo para eliminar un audio del Dataset
     * @param position Posición del audio
     */
    public void removeAudio(int position) {
        stateReproduction.remove(position);
        listener.removeAudio(position);
        localDataSet.remove(position);
        notifyDataSetChanged();
    }

    /**
     * Metodo para conseguir un audio del Dataset
     * @param position Posición del audio
     */
    public Audio getAudio(int position) { return localDataSet.get(position); }

    /**
     * Metodo para establecer el sessionID
     * @param sessionID ID de la session
     */
    public void setSessionID(int sessionID) { this.sessionID = sessionID; }

    /**
     * Metodo para establecer el estado de reproduccion
     * @param position posicion de la reproduccion del audio
     */
    public void setStateReproduction(int position) { stateReproduction.put(position,0); }
}
