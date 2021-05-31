package com.example.lize.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lize.R;
import com.example.lize.data.Image;
import com.example.lize.data.Note;
import com.example.lize.models.DocumentManager;
import com.example.lize.workers.NoteHostFragment;
import com.google.android.material.card.MaterialCardView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Adaptador del RecyclerView de Notas, en la actividad principal. Enlaza los datos del dataSet
 * de Notas, con el correspondiente CardNote (ViewHolder). Podemos distinguir dos tipos de ViewHolders;
 * <ul>
 *      <li>cardNotes con alto height {@link R.dimen#cardnote_layout_height_high} (cardNoteType = true)</li>
 *      <li>cardNotes con bajo height {@link R.dimen#cardnote_layout_height_high} (cardNoteType = false) </li>
 * </ul>
 * Puesto que solo el height es modificado, nos basta con un único tipo de ViewHolder {@link CardNote}.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.CardNote> implements Filterable {

    private final Context mContext;
    private final ArrayList<Note> mNotesData;
    private final ArrayList<Note> mNotesSearch;
    private NoteAdapter.CardNoteListener customListener;
    private boolean cardNoteType;


    /* Custom CardNote onClick Listener */
    public interface CardNoteListener{
        void onCardNoteClicked(NoteAdapter.CardNote cardNote);
        boolean onCardNoteSelected(NoteAdapter.CardNote cardNote);
        void onCardNoteMoved(NoteAdapter.CardNote cardNote);
        void onCardNoteCopy(NoteAdapter.CardNote cardNote);
        void onCardNoteDelete(NoteAdapter.CardNote cardNote);
    }

    /**
     * Method for registering a CardNote onClick listener
     * @param listener Observer which knows when the chip is clicked.
     */
    public void registerCardNoteListener(NoteAdapter.CardNoteListener listener){
        customListener = listener;
    }

    /**
     * Constructor que pasa el listado de notas i el contexto.
     * @param context contexto de la app
     * @param notesData ArrayList conteniendo la información de las notas.
     */
    public NoteAdapter(Context context, ArrayList<Note> notesData, boolean cardNoteType) {
        this.mNotesData = notesData;
        this.mNotesSearch = (ArrayList<Note>) this.mNotesData.clone();
        this.mContext = context;
        this.cardNoteType = cardNoteType;
    }

    /**
     * Modificamos el cardNoteType, y actualizamos el RecicleView llamando a {@link #notifyDataSetChanged()}
     */
    public void changeCardNoteType(boolean newCardNoteType){
        if (cardNoteType != newCardNoteType) {
            cardNoteType = newCardNoteType;
            super.notifyDataSetChanged();
        }
    }

    /**
     * Generador de ViewHolders de notas.
     * @param parent ViewGroup correspondiente al RecycleView - contenedor de notas.
     * @param viewType Tipo de vista del ViewHolder
     * @return El nuevo ViewHolder
     */
    @NonNull
    @Override
    public CardNote onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardNote(LayoutInflater.from(mContext).inflate(R.layout.note_card, parent, false));
    }

    /**
     * Método requerido que permite enlazar los datos de la nota con el correspondiente ViewHolder.
     * Modificamos la altura del Holder según el valor de cardNoteType.
     * @param holder ViewHolder a quien pasar los datos.
     * @param position Position del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull CardNote holder, int position) {
        holder.bindTo(mNotesData.get(position), cardNoteType);  // Enlazamos la nota de la posición con el ViewHolder
    }

    /**
     * Método que requiere el adaptador para determinar el tamaño del dataSet
     * @return tamaño DataSet
     */
    @Override
    public int getItemCount() {
        return mNotesData.size();
    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        //Runs on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Note> filteredList = new ArrayList<>();

            if(constraint.toString().isEmpty()){
                filteredList.addAll(mNotesSearch);
            } else{
                for(Note note: mNotesSearch){
                    if(note.getTitle().toLowerCase().contains(constraint.toString().toLowerCase()) |
                    note.getText_plain().contains(constraint.toString().toLowerCase())){
                        filteredList.add(note);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        //Runs on a UI thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mNotesData.clear();
            mNotesData.addAll((Collection<? extends Note>) results.values);
            notifyDataSetChanged();
        }
    };

    /**
     * ViewHolder class que se corresponde con los Cards de las notas
     */
    public class CardNote extends RecyclerView.ViewHolder{

        private final MaterialCardView holder;
        private final TextView mMetadataText;
        private final TextView mTitleNote;
        private final TextView mTextNote;
        private final ImageView mMediaNote;
        private final View mButtonGroup, mTextGroup;

        private String mNoteID;

        /**
         * Constructor del ViewHolder correspondiente al layout de note_card
         * @param itemView rootview del fichero note_card.xml
         */
        public CardNote(@NonNull View itemView) {
            super(itemView);

            // Inicializamos los componentes del layout
            holder = (MaterialCardView) itemView;
            mMetadataText = (TextView) itemView.findViewById(R.id.note_metadata);
            mTitleNote = (TextView) itemView.findViewById(R.id.note_title);
            mTextNote = (TextView) itemView.findViewById(R.id.note_body);
            mMediaNote = (ImageView) itemView.findViewById(R.id.note_media);

            ImageButton mMoveBtn = itemView.findViewById(R.id.note_move_btn);
            ImageButton mCopyBtn = itemView.findViewById(R.id.note_copy_btn);
            ImageButton mDeleteBtn = itemView.findViewById(R.id.note_delete_btn);

            mTextGroup = itemView.findViewById(R.id.text_group);
            mButtonGroup = itemView.findViewById(R.id.button_group);

            if (customListener != null){
                holder.setOnClickListener((v) -> customListener.onCardNoteClicked(this));
                holder.setOnLongClickListener((v) -> customListener.onCardNoteSelected(this));
                mMoveBtn.setOnClickListener((v) -> customListener.onCardNoteMoved(this));
                mCopyBtn.setOnClickListener((v) -> customListener.onCardNoteCopy(this));
                mDeleteBtn.setOnClickListener((v) -> customListener.onCardNoteDelete(this));
            }
        }

        public String getNoteID(){ return mNoteID; }

        /**
         * Método para <b>enlazar</b> los datos de la nota con el Card de este objeto ViewHolder.
         * @param currentNote nota actual
         */
        public void bindTo(Note currentNote, boolean cardNoteType) {
            mTitleNote.setText(currentNote.getTitle());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mTextNote.setText(Html.fromHtml(currentNote.getText_html(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                mTextNote.setText(Html.fromHtml(currentNote.getText_html()));
            }

            DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

            String metadata = "";
            if (currentNote.getFolderTAG() != null) metadata += currentNote.getFolderTAG();
            if (0 < metadata.length() && currentNote.getLastUpdate() != null)
                metadata += " - " + dateFormat.format(currentNote.getLastUpdate());
            else if(currentNote.getLastUpdate() != null)
                metadata += dateFormat.format(currentNote.getLastUpdate());

            mMetadataText.setText(metadata);
            mNoteID = currentNote.getSelfID();

            ViewGroup.LayoutParams params = mTextGroup.getLayoutParams();

            // Grid Layout
            if (cardNoteType){
                params.width = mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_text_width_low);
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                mTextGroup.setLayoutParams(params);

                mMediaNote.setImageDrawable(null);
                mMediaNote.setVisibility(View.GONE);

            } else{ // Linear Layout

                if (currentNote.getHaveImages()) {
                    params.width = mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_text_width_high_image);
                    params.height = mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_text_group_height);
                    mTextGroup.setLayoutParams(params);

                    /*Bitmap bitmap = BitmapFactory.decodeFile(DocumentManager.getInstance()
                            .selectImageFromArray(currentNote.getImagesID(), 0));*/
                    //mMediaNote.setImageBitmap(bitmap);

                    Glide.with(mContext).load(DocumentManager.getInstance().selectImageFromArray(
                            currentNote.getImagesID(), 0)).into(mMediaNote);

                    mMediaNote.setVisibility(View.VISIBLE);

                }else{
                    params.width = mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_text_width_high_no_image);
                    params.height = mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_text_group_height);
                    mTextGroup.setLayoutParams(params);

                    mMediaNote.setVisibility(View.GONE);
                }
            }
        }

        public void select() {
            holder.setChecked(!holder.isChecked());
            if ((holder.isChecked())){
                holder.setCardElevation(mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_selected_elevation));
                holder.setStrokeWidth(mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_selected_stroke_width));
                showButtonGroup();
            } else{
                hideButtonGroup();
                holder.setCardElevation(mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_default_elevation));
                holder.setStrokeWidth(mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_default_stroke_width));
            }
        }

        public boolean isSelected() { return holder.isChecked(); }

        public void reset() {
            mButtonGroup.setAlpha(0.0f);
            mButtonGroup.setVisibility(View.GONE);
            holder.setCardElevation(mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_default_elevation));
            holder.setStrokeWidth(mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_default_stroke_width));
            holder.setChecked(false);
        }

        /* CardNote animation for showing the button Group */
        private void showButtonGroup(){
            int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) mButtonGroup.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
            int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            mButtonGroup.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
            final int targetHeight = mButtonGroup.getMeasuredHeight();

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            mButtonGroup.getLayoutParams().height = 1;
            mButtonGroup.setVisibility(View.VISIBLE);

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    mButtonGroup.getLayoutParams().height = interpolatedTime == 1
                            ? ViewGroup.LayoutParams.WRAP_CONTENT : (int)(targetHeight * interpolatedTime);
                    mButtonGroup.setAlpha(interpolatedTime);
                    mButtonGroup.requestLayout();
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            // Expansion speed of 1dp/ms
            a.setDuration(4 * (int)(targetHeight / mButtonGroup.getContext().getResources().getDisplayMetrics().density));
            mButtonGroup.startAnimation(a);
        }

        /* CardNote animation for hidding the button Group */
        private void hideButtonGroup(){
            final int initialHeight = mButtonGroup.getMeasuredHeight();

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if(interpolatedTime == 1){
                        mButtonGroup.setAlpha(0.0f);
                        mButtonGroup.setVisibility(View.GONE);

                    }else{
                        mButtonGroup.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                        mButtonGroup.setAlpha(1.0f - interpolatedTime);
                        mButtonGroup.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            // Collapse speed of 1dp/ms
            a.setDuration(4 * (int)(initialHeight / mButtonGroup.getContext().getResources().getDisplayMetrics().density));
            mButtonGroup.startAnimation(a);
        }

    }
}
