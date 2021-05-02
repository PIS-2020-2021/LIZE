package com.example.lize.models;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lize.adapters.DatabaseAdapter;
import com.example.lize.data.Ambito;
import com.example.lize.data.Folder;
import com.example.lize.data.Note;
import com.example.lize.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";

    private final MutableLiveData<User> mUserSelected;
    private final MutableLiveData<Ambito> mAmbitoSelected;
    private final MutableLiveData<Folder> mFolderSelected;
    private final MutableLiveData<Note> mNoteSelected;
    private final MutableLiveData<String> mToast;

    public MainViewModel() {
        mUserSelected = new MutableLiveData<>();
        mAmbitoSelected = new MutableLiveData<>();
        mFolderSelected = new MutableLiveData<>();
        mNoteSelected = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
        DatabaseAdapter da = DatabaseAdapter.getInstance();
        da.setListener(new UserBuilder());
        da.initFireBase();
        da.getUser();           // Build User process
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
     * Selects the application user mUserSelected
     * @param user Usuario Logeado y cargado de base de datos
     */
    public void selectUser(User user){
        setToast("User " + user.getMail() + " selected.");
        mUserSelected.setValue(user);
        selectAmbito(Ambito.BASE_AMBITO_NAME);
    }

    /**
     * Selects an Ambito of the current user mUserSelected
     * @param ambitoName Ambito seleccionado
     */
    public void selectAmbito(String ambitoName) {
        try {
            for (Ambito ambito : mUserSelected.getValue().getAmbitos()) {
                if (ambito.getName().equals(ambitoName)) {
                    setToast("Ambito " + ambitoName + " selected.");
                    mAmbitoSelected.setValue(ambito);
                    selectFolder(Folder.BASE_FOLDER_NAME);
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
     * Selects a Folder of the current Ambito mAmbitoSelected
     * @param folderName Carpeta Seleccionada
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

    /* Selects a Note of the current Folder mFolderSelected. */
    public void selectNote(String noteID) {
        try{
            for (Note note : mFolderSelected.getValue().getNotes()) {
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

    public void addAmbito(String ambitoName, int ambitoColor) {
        for (Ambito ambito: mUserSelected.getValue().getAmbitos())
            if (ambito.getName().equals(ambitoName)) {
                Log.w(TAG, "Failed to create ambito " + ambitoName + ": ambito already exists. ");
                return;
            }

        Ambito newAmbito = new Ambito(ambitoName, ambitoColor);     // Creamos un nuevo Ámbito
        mUserSelected.getValue().addAmbito(newAmbito);              // Añadimos (y asignamos) ese Ámbito al Usuario registrado
        mUserSelected.setValue(mUserSelected.getValue());           // Actualizamos la colección del Usuario registrado
        DatabaseAdapter.getInstance().saveAmbito(newAmbito);        // Guardamos el Ámbito en DB
        setToast("Ambito " + ambitoName + " correctly created.");   // Creamos Toast informativo
    }

    public void addFolder(String folderName) {
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

        Folder folder = new Folder(folderName);                     // Creamos una nueva Folder
        mAmbitoSelected.getValue().putFolder(folder);               // Añadimos (y asignamos) esa Folder al Ámbito seleccionado
        mAmbitoSelected.setValue(mAmbitoSelected.getValue());       // Actualizamos colección del Ámbito seleccionado
        setToast("Folder " + folderName + " correctly created.");   // Creamos Toast informativo
    }

    public void addNote(String noteName, String text_plain, String text_html) {
        Note newNote = new Note(noteName, text_plain, text_html);   // Creamos una nueva Nota
        newNote.setFolderTAG(mFolderSelected.getValue().getName()); // Asignamos esa Nota a la Carpeta seleccionada
        mAmbitoSelected.getValue().putNote(newNote);                // Añadimos esa Nota a la Carpeta BASE del Ámbito y a la Carpeta asignada
        mFolderSelected.setValue(mFolderSelected.getValue());       // Actualizamos colección de la carpeta seleccionada
        DatabaseAdapter.getInstance().saveNote(newNote);            // Guardamos la Nota en DB
        setToast("Note " + noteName + " correctly created.");       // Creamos Toast Informativo
    }

    public void editNote(String title, String plainText, String htmlText){
        Note selected = mNoteSelected.getValue();                   // Editamos la Nota seleccionada
        selected.setTitle(title);
        selected.setText_plain(plainText);
        selected.setText_html(htmlText);
        mNoteSelected.setValue(mNoteSelected.getValue());           // Actualizamos la Nota seleccionada
        mFolderSelected.setValue(mFolderSelected.getValue());       // Actualizamos colección de la carpeta seleccionada
        DatabaseAdapter.getInstance().saveNote(selected);           // Guardamos la Nota en DB
        setToast("Note " + title + " correctly edited.");           // Creamos Toast Informativo
    }

    public void setToast(String s) {
        Log.w(TAG, s);
        mToast.setValue(s);
    }

    /** TODO: Métodos para editar la Nota desde la NoteActivity! + DataBase connection: note.saveNote()
     public void addImageOnNote(String imageURL);
     public void addDocumentOnNote(String documentURL);
     public void addAudioOnNote(String audioURL); */


    /* BUILDER PATTERN FOR DATABASE INTERFACE */
    protected class UserBuilder implements DatabaseAdapter.vmInterface{

        private User currentUser;
        private int loadingCounter;

        public UserBuilder(){
            Log.w("UserBuilder", "Beginning user building process...");
            currentUser = null;
            loadingCounter = 0;
        }

        @Override
        public void setUser(User user) {
            currentUser = user;
            Log.w("UserBuilder", "Step 1 succes: user correctly loaded from Database.");
            DatabaseAdapter.getInstance().getAmbitos();
        }

        @Override
        public void setUserAmbitos(ArrayList<Ambito> userAmbitos) {
            if (currentUser != null) {
                currentUser.setAmbitos(userAmbitos);
                Log.w("UserBuilder", "Step 2 succes: ambitos of user " + currentUser.getSelfID() + " correctly loaded from Database.");
                for (Ambito ambito : userAmbitos) DatabaseAdapter.getInstance().getNotes(ambito.getSelfID());
            } else Log.w("UserBuilder", "Step 2 failure. User " + currentUser.getSelfID() + " it's unitiallized.");
        }

        @Override
        public void setAmbitoNotes(String ambitoID, ArrayList<Note> ambitoNotes) {
            if (currentUser.getAmbitos().isEmpty())
                Log.w("UserBuilder", "Step 3 failure: Ambito " + ambitoID + " it's unitiallized.");
            else{
                for (Ambito ambito : currentUser.getAmbitos()) {
                    if (ambito.getSelfID().equals(ambitoID)) {
                        for (Note note : ambitoNotes) ambito.putNote(note);
                        loadingCounter++;
                        break;
                    }
                }

                // If notes for all user ambitos have been set, call the owner class for setting the user
                if (loadingCounter == currentUser.getAmbitos().size()) {
                    Log.w("UserBuilder", "Step 3 succes: all notes of user " + currentUser.getSelfID() + " correctly loaded from Database.");
                    selectUser(currentUser);
                }
            }
        }

        @Override
        public void setToast(String s) {
            MainViewModel.this.setToast(s);
        }
    }
}
