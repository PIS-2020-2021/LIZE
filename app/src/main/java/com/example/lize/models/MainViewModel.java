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
        DatabaseAdapter da = new DatabaseAdapter(new UserBuilder());
        da.getUser("user_002@gmail.com");
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

    public void selectUser(User user){
        setToast("User " + user.getMail() + " selected.");
        mUserSelected.setValue(user);
    }

    public void selectAmbito(String ambitoID) {
        try {
            for (Ambito ambito : mUserSelected.getValue().getUserAmbitos()) {
                if (ambito.getAmbitoName().equals(ambitoID)) {
                    setToast("Ambito " + ambitoID + " selected.");
                    mAmbitoSelected.setValue(ambito);
                    return;
                }
            }
            Log.w(TAG, "Failed to select ambito " + ambitoID + ": invalid ID.");

        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to select ambito " + ambitoID + ": null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    public void selectFolder(String folderID) {
        try{
            for (Folder folder : mAmbitoSelected.getValue().getAmbitoFolders()) {
                if (folder.getFolderName().equals(folderID)) {
                    setToast("Folder " + folderID + " selected.");
                    mFolderSelected.setValue(folder);
                    return;
                }
            }
            Log.w(TAG, "Failed to select folder " + folderID + ": invalid ID.");

        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to select folder " + folderID + ": null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    public void selectNote(String noteID) {
        try{
            for (Note note : mFolderSelected.getValue().getFolderNotes()) {
                if (note.getID().equals(noteID)) {
                    setToast("Note " + noteID + " selected.");
                    // TODO: Note Selection implementation (call DB?)
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
        Ambito ambito = new Ambito(ambitoName, ambitoColor);
        mUserSelected.getValue().addAmbito(ambito);
        mUserSelected.setValue(mUserSelected.getValue());
        // TODO: note database connection: ambito.saveAmbito();
    }

    public void addFolder(String folderName) {
        Folder folder = new Folder(folderName);
        mAmbitoSelected.getValue().addFolder(folder);
        mAmbitoSelected.setValue(mAmbitoSelected.getValue());
        // TODO: note database connection: folder.saveFolder();
    }

    public void addNote(String noteName, String noteText, String noteFolder) {
        Note note = new Note(noteName, noteText, noteFolder);
        mFolderSelected.getValue().addNote(note);
        mFolderSelected.setValue(mFolderSelected.getValue());
        // TODO: note database connection: note.saveNote();
    }

    public void setToast(String s) {
        Log.w(TAG, s);
        mToast.setValue(s);
    }

    /** TODO: Métodos para editar la Nota desde la NoteActivity! + DataBase connection: note.saveNote()
     public void editNoteText(String noteText);
     public void addImageOnNote(String imageURL);
     public void addDocumentOnNote(String documentURL);
     public void addAudioOnNote(String audioURL); */


    /* BUILDER PATTERN FOR DATABASE INTERFACE */
    protected class UserBuilder implements DatabaseAdapter.vmInterface{

        private User buildingUser;
        private int loadingCounter;

        public UserBuilder(){
            Log.w("UserBuilder", "Beginning user building process...");
            buildingUser = null;
            loadingCounter = 0;
        }

        @Override
        public void setUser(User user) {
            buildingUser = user;
            Log.w("UserBuilder", "Step 1 succes: user correctly loaded from Database.");
            DatabaseAdapter.databaseAdapter.getAmbitos(user.getMail());
        }

        @Override
        public void setUserAmbitos(String userID, ArrayList<Ambito> userAmbitos) {
            if (buildingUser != null) {
                buildingUser.setUserAmbitos(userAmbitos);
                Log.w("UserBuilder", "Step 2 succes: ambitos of user " + userID + " correctly loaded from Database.");
                for (Ambito ambito : userAmbitos) {
                    DatabaseAdapter.databaseAdapter.getNotes(userID, ambito.getAmbitoName());
                }
            } else Log.w("UserBuilder", "Step 2 failure. User " + userID + " it's unitiallized.");
        }

        @Override
        public void setAmbitoNotes(String userID, String ambitoID, ArrayList<Note> ambitoNotes) {
            if (buildingUser.getUserAmbitos().isEmpty())
                Log.w("UserBuilder", "Step 3 failure: Ambito " + ambitoID + " it's unitiallized.");
            else{
                for (Ambito ambito : buildingUser.getUserAmbitos()) {
                    if (ambito.getAmbitoName().equals(ambitoID)) {
                        Map<String, Folder> ambitoFolders = new HashMap<>();
                        for (Note note : ambitoNotes) {
                            String folderID = note.getFolderID();
                            if (ambitoFolders.get(folderID) == null)
                                ambitoFolders.put(folderID, new Folder(folderID));
                            ambitoFolders.get(folderID).addNote(note);
                        }
                        ambito.setAmbitoFolders(new ArrayList(ambitoFolders.values()));
                        loadingCounter++;
                        break;
                    }
                }

                // If notes for all user ambits have been set, call the owner class for setting the user
                if (loadingCounter == buildingUser.getUserAmbitos().size()) {
                    Log.w("UserBuilder", "Step 3 succes: all notes of user " + userID + " correctly loaded from Database.");
                    selectUser(buildingUser);
                }
            }
        }

        @Override
        public void setToast(String s) {
            MainViewModel.this.setToast(s);
        }
    }
}
