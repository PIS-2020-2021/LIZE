package com.example.lize.adapters;

import android.util.Log;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.annotation.NonNull;
import com.example.lize.data.Ambito;
import com.example.lize.data.Note;
import com.example.lize.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


public class DatabaseAdapter {
    public static final String TAG = "DatabaseAdapter";

    public final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference mStorageRef = storage.getReference();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;

    private static DatabaseAdapter databaseAdapter;  // Singleton implementation
    private LoaderInterface loader;
    private SaverInterface saver;

    /**
     * Metodo para establecer el Loader Listener
     * @param loader Loader Listener
     */
    public void setLoaderListener(LoaderInterface loader) {
        this.loader = loader;
    }

    /**
     * Metodo para establecer el Saver Listener
     * @param saver Saver Listener
     */
    public void setSaverListener(SaverInterface saver) {
        this.saver = saver;
    }

    /**
     * Constructor de la clase
     */
    public DatabaseAdapter() {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    /**
     * Singleton de la clase
     * @return Instancia del Data Base Adapter
     */
    public static DatabaseAdapter getInstance() {
        if (databaseAdapter == null){
            synchronized (DatabaseAdapter.class) {
                if (databaseAdapter == null) {
                    databaseAdapter = new DatabaseAdapter();
                }
            }
        }
        return databaseAdapter;
    }

    /**
     * Interfaz del Loader para reconstruir la jerarquía del modelo
     */
    public interface LoaderInterface {
        void getUserResult(User user);
        void getAmbitoCollectionResult(String userID, ArrayList<Ambito> userAmbitos);
        void getNoteCollectionResult(String ambitoID, ArrayList<Note> ambitoNotes);
        void setToast(String s);
    }


    /**
     * Interfaz del Saver para guaradr la jerarquía del modelo
     */
    public interface SaverInterface {
        void saveUserResult(String userID, boolean result);
        void saveAmbitoResult(String ambitoID, boolean result);
        void saveNoteResult(String noteID, boolean result);
        void setToast(String s);
    }


    /**
     * Metodo para inicializar el Firebase
     */
    public void initFireBase() {
        user = mAuth.getCurrentUser();
        if (user == null) {
            mAuth.signInAnonymously()
                    // OnCompleteListener: when sign in, Authentication successful
                    .addOnCompleteListener((Executor) this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            if (loader != null) loader.setToast("Authentication successful.");
                            user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            if(loader != null) loader.setToast("Authentication failed.");
                        }
                    });
        } else {
            if (loader != null) loader.setToast("Authentication with current user.");
        }
    }


    /**
     * Metodo para conseguir un usuario de la Collección Users
     */
    public void getUser() {
        DocumentReference userRef = db.collection("users").document(user.getUid());
        Log.d(TAG, "Getting current user document...");

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Log.d(TAG, document.getId() + " => " + document.getData());
                User user = new User(document.getString("mail"), document.getString("password"),
                        document.getString("first"), document.getString("last"));
                user.setSelfID(document.getString("selfID"));

                if (loader != null) loader.getUserResult(user);
            } else Log.d(TAG, "Error getting documents: ", task.getException());
        });
    }


    /**
     * Metodo para conseguir un ambito de la Collección Ambitos
     */
    public void getAmbitos() {
        Query ambitosRef = db.collection("ambitos").whereEqualTo("userID", user.getUid());
        Log.d(TAG, "Getting current user ambitos collection...");

        ambitosRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Ambito> userAmbitos = new ArrayList<>();

                // Por cada resultado, creamos el ambito a partir de los datos de DB y lo añadimos
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    Ambito ambito = new Ambito(document.getString("name"), document.getLong("color").intValue());
                    ambito.setUserID(document.getString("userID"));
                    ambito.setSelfID(document.getString("selfID"));
                    ambito.setPosition(document.getLong("position").intValue());
                    userAmbitos.add(ambito);
                }
                Collections.sort(userAmbitos, (Ambito a1, Ambito a2) -> a1.getPosition() - a2.getPosition());
                if (loader != null) loader.getAmbitoCollectionResult(user.getUid(), userAmbitos);

            } else Log.d(TAG, "Error getting documents: ", task.getException());
        });
    }


    /**
     * Metodo para conseguir una nota de la Collección Notes
     */
    public void getNotes(String ambitoID){
        Query notasRef = db.collection("notes").whereEqualTo("ambitoID", ambitoID);
        Log.d(TAG, "Getting ambito " + ambitoID + "'s notes collection...");

        notasRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Note> ambitoNotes = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    Note note = new Note(document.getString("title"), document.getString("text_plain"), document.getString("text_html"));
                    note.setAmbitoID(document.getString("ambitoID"));
                    note.setFolderTAG(document.getString("folderTAG"));
                    note.setSelfID(document.getString("selfID"));
                    note.setLastUpdate(document.getDate("lastUpdate"));
                    note.setDocumentsID(document.getString("documentsID"));
                    note.setImagesID(document.getString("imagesID"));
                    note.setHaveImages(document.getBoolean("images"));
                    note.setHaveDocuments(document.getBoolean("documents"));
                    note.setHaveAudios(document.getBoolean("audios"));
                    note.setAudiosID(document.getString("audiosID"));
                    ambitoNotes.add(note);
                }
                if (loader != null) loader.getNoteCollectionResult(ambitoID, ambitoNotes);

            } else Log.d(TAG, "Error getting documents: ", task.getException());
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
                } else Log.d(TAG, "Error saving user " + user.getSelfID(), task.getException());
                if (saver != null) saver.saveUserResult(user.getSelfID(), task.isSuccessful()); // ViewModel callback
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
        ambitoData.put("position", ambito.getPosition());


        ambitoRef.set(ambitoData).addOnCompleteListener(new OnCompleteListener(){
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "Ambito" + ambito.getSelfID() + " correctly saved.");
                    //for (Note note: ambito.getNotes()) saveNote(note);
                } else Log.d(TAG, "Error saving ambito " + ambito.getSelfID(), task.getException());
                if (saver != null) saver.saveAmbitoResult(ambito.getSelfID(), task.isSuccessful()); // ViewModel callback
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
        notesData.put("text_plain", note.getText_plain());
        notesData.put("text_html", note.getText_html());
        notesData.put("selfID", note.getSelfID());
        notesData.put("ambitoID", note.getAmbitoID());
        notesData.put("folderTAG", note.getFolderTAG());
        notesData.put("lastUpdate", note.getLastUpdate());
        notesData.put("documentsID",note.getDocumentsID());
        notesData.put("imagesID",note.getImagesID());
        notesData.put("documents",note.getHaveDocuments());
        notesData.put("images",note.getHaveImages());
        notesData.put("audiosID",note.getAudiosID());
        notesData.put("audios",note.getHaveAudios());

        noteRef.set(notesData).addOnCompleteListener(new OnCompleteListener(){
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "Note" + note.getSelfID() + " correctly saved.");
                } else Log.d(TAG, "Error saving note " + note.getSelfID(), task.getException());
                if (saver != null) saver.saveNoteResult(note.getSelfID(), task.isSuccessful()); // ViewModel callback
            }
        });
    }


    /**
     * Eliminamos una nota de FireBase.
     * @param notaID ID del documento correspondiente a la nota a eliminar
     */
    public void deleteNote(String notaID) {
        db.collection("notes").document(notaID).delete()
            .addOnSuccessListener(aVoid -> Log.d(TAG, "Nota Eliminada Correctamente"))
                .addOnFailureListener(e -> Log.w(TAG, "Error al Eliminar la nota", e));
    }

    /**
     * Eliminamos las imagenes asociadas a una imageID
     * @param imagesID ID del array de imagenes asociada a la DB
     */
    public void deleteImages(String imagesID) {
        DocumentReference imagesRef = db.collection("images").document(imagesID);
        imagesRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                try {
                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("images");
                    for (String stringAux: group) {
                        StorageReference singleDoc = mStorageRef.child(stringAux);
                        singleDoc.delete();
                    }
                } catch (NullPointerException exception) {
                    Log.w(TAG, "Failed to get Collection of images of  " + imagesID + ": null pointer exception.");
                }
            } else Log.d(TAG, "Error al eliminar la coleccion de imagenes: ", task.getException());

            imagesRef.delete().addOnSuccessListener(aVoid -> Log.d(TAG, "Documentos de la Nota Eliminados Correctamente"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error al Eliminar Documentos de " + imagesID, e));
        });

    }

    /**
     * Eliminamos las imagenes asociadas a una imageID
     * @param documentsID ID del array de imagenes asociada a la DB
     */
    public void deleteDocuments(String documentsID) {
        DocumentReference documentsRef = db.collection("documents").document(documentsID);
        documentsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("files");
                    for (String stringAux: group) {
                        StorageReference singleDoc = mStorageRef.child(stringAux);
                        singleDoc.delete();
                    }
                } catch (NullPointerException exception) {
                    Log.w(TAG, "Failed to get Collection of images of  " + documentsID + ": null pointer exception.");
                }
            } else Log.d(TAG, "Error al eliminar la coleccion de imagenes: ", task.getException());

            documentsRef.delete().addOnSuccessListener(aVoid -> Log.d(TAG, "Documentos de la Nota Eliminados Correctamente"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error al Eliminar Documentos de " + documentsID, e));
        });

    }

    /**
     * Metodo para eliminar Audios de la Colección Audios
     * @param audiosID
     */
    public void deleteAudios(String audiosID) {
        DocumentReference documentsRef = db.collection("audios").document(audiosID);
        documentsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("files");
                    for (String stringAux: group) {
                        StorageReference singleDoc = mStorageRef.child(stringAux);
                        singleDoc.delete();
                    }
                } catch (NullPointerException exception) {
                    Log.w(TAG, "Failed to get Collection of audios of  " + audiosID + ": null pointer exception.");
                }
            } else Log.d(TAG, "Error al eliminar la coleccion de audios: ", task.getException());

            documentsRef.delete().addOnSuccessListener(aVoid -> Log.d(TAG, "Documentos de la Nota Eliminados Correctamente"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error al Eliminar Documentos de " + audiosID, e));
        });

    }

    /**
     * Eliminamos la subcolección de notas cuyo valor del campo "folderTAG" sea el pasado por parámetro.
     * Obtenemos cada documento de la subcolección y lo eliminamos.
     * @param folderTAG valor del campo "folderTAG" de la subcolección de notas de la colección "notes".
     */
    public void deleteFolder(String folderTAG) {
        Query notasRef = db.collection("notes").whereEqualTo("folderTAG", folderTAG);
        notasRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        deleteNote(document.getId());
                    }
                    Log.d(TAG, "Colección de Notas de " + folderTAG + " eliminado correctamente");
                } catch (NullPointerException exception) {
                    Log.w(TAG, "Failed to get Collection of notes of  " + folderTAG + ": null pointer exception.");
                }
            } else Log.d(TAG, "Error al eliminar la coleccion de notas: ", task.getException());
        });
    }

    /**
     * Eliminamos un Ámbito de Firebase. También eliminamos la subcolección de Notas cuyo valor del
     * campo "ambitoID" sea el pasado por parámetro. Obtenemos cada documento de la subcolección y lo eliminamos.
     * @param ambitoID ID del documento correspondiente al Ámbito a eliminar de la colección "ambitos".
     */
    public void deleteAmbito(String ambitoID) {
        Query notasRef = db.collection("notes").whereEqualTo("ambitoID", ambitoID);
        notasRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        deleteNote(document.getId());
                        if ((boolean) document.get("images")) deleteImages((String) document.get("imagesID"));
                        if ((boolean) document.get("documents")) deleteDocuments((String) document.get("documentsID"));
                    }
                    Log.d(TAG, "Colección de Notas de " + ambitoID + " eliminado correctamente");
                } catch (NullPointerException exception) {
                    Log.w(TAG, "Failed to get Collection of notes of  " + ambitoID + ": null pointer exception.");
                }
            } else {
                Log.d(TAG, "Error al eliminar la coleccion de notas: ", task.getException());
            }
        });

        db.collection("ambitos").document(ambitoID).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Ambito Eliminado Correctamente"))
                .addOnFailureListener(e -> Log.w(TAG, "Error al Eliminar el Ambito " + ambitoID, e));
    }

    /**
     * Eliminamos el Usuario registrado de Firebase.
     *
     * También eliminamos la subcolección de Ámbitos del Usuario mediante el método
     * {@link #deleteAmbito(String)}, eliminando también las Notas de cada Ámbito.
     *
     * IMPORTANTE: ese Usuario queda eliminado del Firebase.Authentication, de modo que no se podrá
     * acceder mediante la cuenta asociada a su UID.
     */
    public void deleteUser() {
        Query ambitosRef = db.collection("ambitos").whereEqualTo("userID", user.getUid());
        ambitosRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try{
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        deleteAmbito(document.getId());
                    }
                    Log.d(TAG, "Colección de Ambitos de " + user.getUid() + " eliminado correctamente");
                } catch (NullPointerException exception) {
                    Log.w(TAG, "Failed to get Collection of ambitos of  " + user.getDisplayName() + ": null pointer exception.");
                }
            } else Log.d(TAG, "Error al eliminar la coleccion de ambitos: ", task.getException());
        });

        db.collection("users").document(mAuth.getCurrentUser().getUid()).delete()
            .addOnSuccessListener(aVoid -> Log.d(TAG, "Usuario Eliminado Correctamente"))
            .addOnFailureListener(e -> Log.w(TAG, "Error al Eliminar el Usuario " + user, e));

        user.delete();
    }


}