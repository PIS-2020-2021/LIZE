package com.example.lize.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.data.Audio;

import com.example.lize.models.DocumentManager;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
    private final ArrayList<Audio> localDataSet;
    private final Context parentContext;
    private final playerInterface listener;
    private boolean clicked = true;
    private int sessionID = -1;
    private HashMap<Integer,Integer> stateReproduction;

    /**
     * Initialize the dataset of the Adapter.
     *
      * by RecyclerView.
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


        public TextView getTextView() {
            return textView;
        }

        public LinearLayout getLayout() {
            return audioLayout;
        }

        public ImageButton getPlayButton() {return playButton;}

        public BarVisualizer getProgressBar(){return barVisualizer;}

        public ImageButton getRemoveButton(){return removeButton;}
    }



    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_attach_audio, viewGroup, false);

        return new ViewHolder(view);
    }

    private void playAudio(int position) {
        listener.startPlaying(position);
    }

    public interface playerInterface{
        void startPlaying(int position);
        void pausePlaying(int position);
        void removeAudio(int position);
    }

    private void pausePlaying(int position){
        listener.pausePlaying(position);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        BarVisualizer barVisualizer = viewHolder.getProgressBar();

        ImageButton playButton = viewHolder.getPlayButton();
        playButton.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_24);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stateReproduction.get(position) == 0) {

                    //current value in the text view
                    stateReproduction.put(position,1);
                    playAudio(position);
                    playButton.setBackgroundResource(R.drawable.ic_baseline_pause_circle_filled_24);
                    if (sessionID != -1){
                        try{
                            barVisualizer.show();
                            barVisualizer.setAudioSessionId(sessionID);
                    }catch (IllegalStateException e){

                        }
                    }
                }else if(stateReproduction.get(position) == 1){
                    stateReproduction.put(position,0);
                    barVisualizer.hide();
                    playButton.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_24);
                    pausePlaying(position);
                }


        }});

        ImageButton removeButton = viewHolder.getRemoveButton();
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(parentContext)
                        .setTitle("Are you sure ?")
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeAudio(position);
                                Log.d("MainActivity", "Sending atomic bombs to Jupiter");
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        long millis = localDataSet.get(position).getDuration();
        viewHolder.getTextView().setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        ));
    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (localDataSet != null) {
            return localDataSet.size();
        }
        return 0;
    }
    public void addAudio(Audio audio){
        localDataSet.add(audio);
        stateReproduction.put(localDataSet.indexOf(audio),0);
        notifyDataSetChanged();
    }
    public void removeAudio(int position) {
        stateReproduction.remove(position);
        listener.removeAudio(position);
        localDataSet.remove(position);
        notifyDataSetChanged();
    }

    public Audio getAudio(int position){
        return localDataSet.get(position);
    }

    public void setSessionID(int sessionID){
        this.sessionID = sessionID;
    }

    public void setStateReproduction(int position){
        stateReproduction.put(position,0);
    }
}
