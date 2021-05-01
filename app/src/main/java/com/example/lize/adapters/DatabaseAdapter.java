package com.example.lize.adapters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lize.data.Ambito;
import com.example.lize.data.Note;
import com.example.lize.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;


public class DatabaseAdapter {
    public static final String TAG = "DatabaseAdapter";

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;

    private static DatabaseAdapter databaseAdapter;  // Singleton implementation

    private vmInterface listener;

    public void setListener(vmInterface listener) {
        this.listener = listener;
    }

    public DatabaseAdapter() {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    public static DatabaseAdapter getInstance() {
        if (databaseAdapter == null){
            synchronized (DatabaseAdapter.class){
                if (databaseAdapter == null){
                    databaseAdapter = new DatabaseAdapter();
                }
            }
        }
        return databaseAdapter;
    }

    public interface vmInterface {
        void setUser(User user);
        void setUserAmbitos(ArrayList<Ambito> userAmbitos);
        void setAmbitoNotes(String ambitoID, ArrayList<Note> ambitoNotes);
        void setToast(String s);
    }

    /* Firebase sign in */
    public void initFireBase() {
        user = mAuth.getCurrentUser();
        if (user == null) {
            mAuth.signInAnonymously()
                    // OnCompleteListener: when sign in, Authentication successful
                    .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");
                                if(listener != null) listener.setToast("Authentication successful.");
                                user = mAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                                if(listener != null) listener.setToast("Authentication failed.");

                            }
                        }
                    });
        } else {
            if(listener != null) listener.setToast("Authentication with current user.");
        }
    }

    // Method for getting a base User from the 'users' collection
    public void getUser() {
        DocumentReference userRef = DatabaseAdapter.db.collection("users").document(user.getUid());
        Log.d(TAG, "Getting current user document...");
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    User user = new User(document.getString("mail"), document.getString("password"),
                            document.getString("first"), document.getString("last"));
                    user.setSelfID(document.getString("selfID"));

                    if(listener != null) listener.setUser(user);
                } else Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    // Method for getting an Ambito from the 'ambitos' collection
    public void getAmbitos() {
        Query ambitosRef = DatabaseAdapter.db.collection("ambitos").whereEqualTo("userID", user.getUid());
        Log.d(TAG, "Getting current user ambitos collection...");
        ambitosRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Ambito> userAmbitos = new ArrayList<>();
                    // Por cada resultado, creamos el ambito a partir de los datos de DB y lo añadimos
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Ambito ambito = new Ambito(document.getString("name"), document.getLong("color").intValue());
                        ambito.setUserID(document.getString("userID"));
                        ambito.setSelfID(document.getString("selfID"));
                        userAmbitos.add(ambito);
                    }
                    if(listener != null) listener.setUserAmbitos(userAmbitos);
                } else Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    // Method for getting a Note from the 'notes' collection
    public void getNotes(String ambitoID){
        Query notasRef = DatabaseAdapter.db.collection("notes").whereEqualTo("ambitoID", ambitoID);
        Log.d(TAG, "Getting ambito " + ambitoID + "'s notes collection...");
        notasRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Note> ambitoNotes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Note note = new Note(document.getString("title"), document.getString("text_plain"), document.getString("text_html"));
                        note.setAmbitoID(document.getString("ambitoID"));
                        note.setFolderTAG(document.getString("folderTAG"));
                        note.setSelfID(document.getString("selfID"));
                        ambitoNotes.add(note);
                    }
                    if(listener != null) listener.setAmbitoNotes(ambitoID, ambitoNotes);
                } else Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Guardamos un nuevo User en FireBase.
     * Creamos un nuevo documento (en BaseDatos) con el ID del Usuario y guardamos ese Usuario.
     * @param user Usuario a guardar/modificar
     */
    public void saveUser(User user) {
        DocumentReference userRef = db.collection("users").document(user.getSelfID());
        Log.d(TAG, "Saving user with ID: " + userRef.getId());

        Map<String, Object> userData = new HashMap<>();
        userData.put("mail", user.getMail());
        userData.put("password", user.getPassword());
        userData.put("first", user.getFirst());
        userData.put("last", user.getLast());
        userData.put("selfID", user.getSelfID());

        userRef.set(userData).addOnCompleteListener(new OnCompleteListener(){
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User" + user.getSelfID() + " correctly saved.");
                    for (Ambito ambito: user.getAmbitos()) saveAmbito(ambito);
                }else Log.d(TAG, "Error saving user " + user.getSelfID(), task.getException());
            }
        });
    }

    /**
     * Guardamos un ambito en FireBase.
     * Si ya está, se sobreescribe el contenido, pero si no está se crea un nuevo documento (en BaseDatos)
     * Y guardamos ese ambito en el documento
     * @param ambito Ambito a guardar/modificar
     */
    public void saveAmbito(Ambito ambito) {
        DocumentReference ambitoRef;
        if(ambito.getSelfID() == null) {
            ambitoRef = db.collection("ambitos").document();
            ambito.setSelfID(ambitoRef.getId());
        } else {
            ambitoRef = db.collection("ambitos").document(ambito.getSelfID());
        }
        Log.d(TAG, "Saving ambito with ID: " + ambitoRef.getId());

        Map<String, Object> ambitoData = new HashMap<>();
        ambitoData.put("name", ambito.getName());
        ambitoData.put("color", ambito.getColor());
        ambitoData.put("selfID", ambito.getSelfID());
        ambitoData.put("userID", ambito.getUserID());

        ambitoRef.set(ambitoData).addOnCompleteListener(new OnCompleteListener(){
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "Ambito" + ambito.getSelfID() + " correctly saved.");
                    for (Note note: ambito.getNotes()) saveNote(note);
                } else Log.d(TAG, "Error saving ambito " + ambito.getSelfID(), task.getException());
            }
        });
    }

    /**
     * Guardamos una nota en FireBase.
     * Si ya está, se sobreescribe el contenido, pero si no está se crea un nuevo documento (en BaseDatos)
     * Y guardamos esa nota en el documento
     * @param note Nota a guardar/modificar
     */
    public void saveNote(Note note) {
        DocumentReference noteRef;
        if(note.getSelfID() == null){
            noteRef= db.collection("notes").document();
            note.setSelfID(noteRef.getId());
        } else {
            noteRef= db.collection("notes").document(note.getSelfID());
        }
        Log.d(TAG, "Saving note with ID: " + noteRef.getId());

        Map<String, Object> notesData = new HashMap<>();
        notesData.put("title", note.getTitle());
        notesData.put("text", note.getText_plain());
        notesData.put("selfID", note.getSelfID());
        notesData.put("ambitoID", note.getAmbitoID());
        notesData.put("folderTAG", note.getFolderTAG());

        noteRef.set(notesData).addOnCompleteListener(new OnCompleteListener(){
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) Log.d(TAG, "Note" + note.getSelfID() + " correctly saved.");
                else Log.d(TAG, "Error saving note " + note.getSelfID(), task.getException());
            }
        });
    }

    public void saveNoteWithFile(String id, String description, String userid, String path) {
    /*  Uri file = Uri.fromFile(new File(path));
        StorageReference storageRef = storage.getReference();
        StorageReference audioRef = storageRef.child("audio"+File.separator+file.getLastPathSegment());
        UploadTask uploadTask = audioRef.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return audioRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    saveDocument(id, description, userid, downloadUri.toString());
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");
            }
        }); */
    }

}