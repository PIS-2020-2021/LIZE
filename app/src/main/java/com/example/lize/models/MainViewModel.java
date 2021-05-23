package com.example.lize.models;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lize.adapters.DatabaseAdapter;
import com.example.lize.data.Ambito;
import com.example.lize.data.Folder;
import com.example.lize.data.Note;
import com.example.lize.data.User;

import java.util.ArrayList;
import java.util.Date;


public class MainViewModel extends ViewModel{

    private static final String TAG = "MainViewModel";

    private final MutableLiveData<User> mUserSelected;          // Contiene el Usuario Seleccionado
    private final MutableLiveData<Ambito> mAmbitoSelected;      // Contiene el Ambito Seleccionado
    private final MutableLiveData<Folder> mFolderSelected;      // Contiene la Carpeta Seleccionada
    private final MutableLiveData<Note> mNoteSelected;          // Contiene la Nota Seleccionada

    private final MutableLiveData<String> mToast;               // Toast informativo

    public MainViewModel() {
        mUserSelected = new MutableLiveData<>();
        mAmbitoSelected = new MutableLiveData<>();
        mFolderSelected = new MutableLiveData<>();
        mNoteSelected = new MutableLiveData<>();
        mToast = new MutableLiveData<>();

        // Enlazamos con la base de datos, reconstruyendo la jerarquía del modelo a partir del Usuario Registrado
        DatabaseAdapter da = DatabaseAdapter.getInstance();
        da.setLoaderListener(new UserBuilder());
        da.initFireBase();
        da.getUser();
    }

    public MutableLiveData<User> getUserSelected() { return mUserSelected; }

    public MutableLiveData<Ambito> getAmbitoSelected() {
        return mAmbitoSelected;
    }

    public MutableLiveData<Folder> getFolderSelected() {
        return mFolderSelected;
    }

    public MutableLiveData<Note> getNoteSelected() {
        return mNoteSelected;
    }

    public MutableLiveData<String> getToast() {
        return mToast;
    }

    /**
     * Selecciona un Ámbito del Usuario logueado mUserSelected.
     * @param ambitoName Ambito seleccionado
     * @throws NullPointerException Si el Usuario logueado no ha sido correctamente cargado de DB.
     */
    public void selectAmbito(String ambitoName) {
        try {
            for (Ambito ambito : mUserSelected.getValue().getAmbitos()) {
                if (ambito.getName().equals(ambitoName)) {
                    setToast("Ambito " + ambitoName + " selected.");
                    mAmbitoSelected.setValue(ambito);
                    mFolderSelected.setValue(null);
                    return;
                }
            }
            Log.w(TAG, "Failed to select ambito " + ambitoName + ": invalid ID.");

        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to select ambito " + ambitoName + ": null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    /**
     * Selecciona una Carpeta del Ámbito actual mAmbitoSelected
     * @param folderName Carpeta Seleccionada
     * @throws NullPointerException Si el Ámbito actual no ha sido correctamente seleccionado.
     */
    public void selectFolder(String folderName) {
        try{
            Folder folder = mAmbitoSelected.getValue().getFolder(folderName);
            if (folder != null) {
                setToast("Folder " + folderName + " selected.");
                mFolderSelected.setValue(folder);
            } else Log.w(TAG, "Failed to select folder " + folder.getName()  + ": invalid Name.");

        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to select folder " + folderName + ": null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    /**
     * Deselecciona la Carpeta mFolderSelected del Ámbito actual, actualizando su lista de notas general.
     */
    public void deselectFolder(){
        try {
            Folder selectedFolder = mFolderSelected.getValue();
            if (selectedFolder != null) {
                setToast("Folder " + selectedFolder.getName() + " deselected.");
                mFolderSelected.setValue(null);

            } else Log.w(TAG, "Failed to deselect current Folder: no Folder selected");

        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to deselect current folder: null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    /**
     * Selecciona una Nota del Ámbito actual mAmbitoSelected.
     * @param noteID ID de la Nota seleccionada.
     * @throws NullPointerException Si el Ámbito actual no ha sido correctamente seleccionado.
     */
    public void selectNote(String noteID) {
        try{
            for (Note note :mAmbitoSelected.getValue().getNotes()) {
                if (note.getSelfID().equals(noteID)) {
                    setToast("Note " + note.getTitle() + " selected.");
                    mNoteSelected.setValue(note);
                    return;
                }
            }
            Log.w(TAG, "Failed to select note " + noteID + ": invalid ID.");

        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to select note " + noteID + ": null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    /**
     * Añadimos un nuevo Ámbito a la lista de Ámbitos del Usuario logueado.
     * @param ambitoName Nombre del Ámbito a crear. Debe ser distinto a los demás Ámbitos del Usuario.
     * @param ambitoColor Color del Ámbito a crear. Debe ser distinto a los demás Ámbitos del Usuario.
     * @throws NullPointerException Si el Usuario logueado no ha sido correctamente seleccionado.
     */
    public void addAmbito(String ambitoName, int ambitoColor) {
        try {
            for (Ambito ambito : mUserSelected.getValue().getAmbitos())
                if (ambito.getName().equals(ambitoName)) {
                    Log.w(TAG, "Failed to create ambito " + ambitoName + ": ambito already exists. ");
                    return;
                }

            Ambito newAmbito = new Ambito(ambitoName, ambitoColor);     // Creamos un nuevo Ámbito
            mUserSelected.getValue().addAmbito(newAmbito);              // Añadimos ese Ámbito al Usuario registrado
            mUserSelected.setValue(mUserSelected.getValue());           // Actualizamos la colección de Ámbitos del Usuario registrado
            DatabaseAdapter.getInstance().saveAmbito(newAmbito);        // Guardamos el Ámbito en DB
            setToast("Ambito " + ambitoName + " correctly created.");   // Creamos Toast informativo

        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to add ambito " + ambitoName + ": null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    /**
     * Actualizamos el contenido del Ambito editado
     * @param ambitoName Nombre del Ambito editado.
     * @param ambitoColor Color del Ámbito editado.
     * @throws NullPointerException Si la Nota Editada no ha sido correctamente seleccionada.
     */
    public void editAmbito(String ambitoID, String ambitoName, int ambitoColor){
        try {
            Ambito selected = null;
            for (Ambito ambito : mUserSelected.getValue().getAmbitos()){
                if(ambito.getSelfID().equals(ambitoID)){
                    selected = ambito;                                     // Editamos el Ambito seleccionado
                }
            }
            selected.setName(ambitoName);
            selected.setColor(ambitoColor);
            if(mAmbitoSelected.getValue().getSelfID().equals(ambitoID)) mAmbitoSelected.setValue(selected);     // Actualizamos el Ambito editado
            DatabaseAdapter.getInstance().saveAmbito(selected);                                                 // Guardamos el Ambito en DB
            setToast("Ambito " + ambitoName + " correctly edited.");                                            // Creamos Toast Informativo

        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to edit ambito " + ambitoName + ": null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    /**
     * Actualizamos la posición de los Ambitos en el RecyclerView
     * @throws NullPointerException Si la Nota Editada no ha sido correctamente seleccionada.
     */
    public void savePositionAmbitos(){
        try {
            for (Ambito ambito : mUserSelected.getValue().getAmbitos()){
                DatabaseAdapter.getInstance().saveAmbito(ambito);
            }
        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to save positions: null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }


    /**
     * Añadimos una nueva Carpeta a la lista de Carpetas del Ámbito actual.
     * @param folderName Nombre de la carpeta a crear. Debe ser no nula y distinta a las demás Carpetas del Ámbito.
     * @throws NullPointerException Si el Ámbito actual no ha sido correctamente seleccionado.
     */
    public void addFolder(String folderName) {
        try{
            if (folderName == null || (folderName != null && folderName.length() == 0)) {
                Log.w(TAG, "Failed to create folder with no name. ");
                setToast("Failed to create folder with no name. ");
                return;
            }

            if (mAmbitoSelected.getValue().getFolder(folderName) != null) {
                Log.w(TAG, "Failed to create folder " + folderName + ": folder already exists. ");
                setToast("Folder " + folderName + " already exists.");
                return;
            }
            mAmbitoSelected.getValue().addFolder(folderName);           // Añadimos una nueva Carpeta al Ámbito seleccionado
            mAmbitoSelected.setValue(mAmbitoSelected.getValue());       // Actualizamos la colección de carpetas del Ámbito seleccionado
            setToast("Folder " + folderName + " correctly created.");   // Creamos Toast informativo

        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to add folder " + folderName + ": null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    /**
     * Añadimos una nueva Nota a la lista de Notas del Ámbito actual (también en la Carpeta actual)
     * @param noteName Título de la Nota a añadir.
     * @param text_plain Texto plano de la Nota a añadir.
     * @param text_html Texto en formato HTML de la Nota a añadir.
     * @throws NullPointerException Si el Ámbito actual no ha sido correctamente seleccionado.
     */
    public void addNote(String noteName, String text_plain, String text_html) {
        try{
            Note newNote = new Note(noteName, text_plain, text_html);   // Creamos una nueva Nota

            if (mFolderSelected.getValue() != null)                     // Si tenemos una carpeta seleccionada, la asignamos a la Nota
                newNote.setFolderTAG(mFolderSelected.getValue().getName());

            mAmbitoSelected.getValue().addNote(newNote);                // Añadimos esa Nota al Ámbito seleccionado
            mFolderSelected.setValue(mFolderSelected.getValue());       // Actualizamos la colección de Notas de la Folder seleccionada
            DatabaseAdapter.getInstance().saveNote(newNote);            // Guardamos la Nota en DB
            setToast("Note " + noteName + " correctly created.");       // Creamos Toast Informativo

        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to add note " + noteName + ": null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    /**
     * Actualizamos el contenido de la Nota editada del Ámbito actual.
     * @param title Título de la Nota editada.
     * @param plainText Texto plano de la Nota editada.
     * @param htmlText Texto en formato HTML de la Nota editada.
     * @throws NullPointerException Si la Nota Editada no ha sido correctamente seleccionada.
     */
    public void editNote(String title, String plainText, String htmlText){
        try {
            Note selected = mNoteSelected.getValue();                   // Editamos la Nota seleccionada
            selected.setTitle(title);
            selected.setText_plain(plainText);
            selected.setText_html(htmlText);
            selected.setLastUpdate(new Date());
            mNoteSelected.setValue(selected);                           // Actualizamos la Nota editada
            mFolderSelected.setValue(mFolderSelected.getValue());       // Actualizamos la colección de Notas de la Folder seleccionada
            DatabaseAdapter.getInstance().saveNote(selected);           // Guardamos la Nota en DB
            setToast("Note " + title + " correctly edited.");           // Creamos Toast Informativo

        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to edit note " + title + ": null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    /* Deletes a Note of the current Folder mFolderSelected. */
    //TODO:Eliminar bien las notas de todas las carpetas
    public void deleteNote(String noteID) {
        try{
            for (Folder folder : mAmbitoSelected.getValue().getFolders()){
                for(Note note : folder.getNotes()){
                    if(note.getSelfID().equals(noteID)){
                        folder.getNotes().remove(note);
                    }
                }
            }

            Log.w(TAG, "Failed to select note " + noteID + ": invalid ID.");
        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to select note " + noteID + ": null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    /**
     * Deletes a Folder of the current Ambito mAmbitoSelected
     * @param folderName Carpeta Seleccionada
     */
    public void deleteFolder(String folderName) {
        DatabaseAdapter.getInstance().deleteFolder(folderName);
    }

    private void setToast(String s) {
        Log.w(TAG, s);
        mToast.setValue(s);
    }

    /* BUILDER PATTERN FOR DATABASE INTERFACE */
    protected class UserBuilder implements DatabaseAdapter.LoaderInterface{

        private User currentUser;
        private int loadingCounter;

        public UserBuilder(){
            Log.w("UserBuilder", "Beginning user building process...");
            currentUser = null;
            loadingCounter = 0;
        }

        @Override
        public void getUserResult(User user) {
            currentUser = user;
            Log.w("UserBuilder", "Step 1 succes: user correctly loaded from Database.");
            DatabaseAdapter.getInstance().getAmbitos();
        }

        @Override
        public void getAmbitoCollectionResult(String userID, ArrayList<Ambito> userAmbitos) {
            if (currentUser != null && currentUser.getSelfID().equals(userID)) {
                currentUser.setAmbitos(userAmbitos);
                Log.w("UserBuilder", "Step 2 succes: ambitos of user " + currentUser.getSelfID() + " correctly loaded from Database.");
                for (Ambito ambito : userAmbitos) DatabaseAdapter.getInstance().getNotes(ambito.getSelfID());
            } else Log.w("UserBuilder", "Step 2 failure. User " + currentUser.getSelfID() + " it's unitiallized.");
        }

        @Override
        public void getNoteCollectionResult(String ambitoID, ArrayList<Note> ambitoNotes) {
            if (currentUser.getAmbitos().isEmpty())
                Log.w("UserBuilder", "Step 3 failure: Ambito " + ambitoID + " it's unitiallized.");
            else{
                for (Ambito ambito : currentUser.getAmbitos()) {
                    if (ambito.getSelfID().equals(ambitoID)) {
                        for (Note note : ambitoNotes) ambito.addNote(note);
                        loadingCounter++;
                        break;
                    }
                }

                // If notes for all user ambitos have been set, call the owner class for setting the user
                if (loadingCounter == currentUser.getAmbitos().size()) {
                    Log.w("UserBuilder", "Step 3 succes: all notes of user " + currentUser.getSelfID() + " correctly loaded from Database.");
                    setToast("User " + currentUser.getMail() + " correctly logged.");
                    mUserSelected.setValue(currentUser);
                    selectAmbito(Ambito.BASE_AMBITO_NAME);
                }
            }
        }

        @Override
        public void setToast(String s) {
            MainViewModel.this.setToast(s);
        }
    }
}
