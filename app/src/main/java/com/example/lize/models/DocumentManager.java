package com.example.lize.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lize.data.Audio;
import com.example.lize.data.Document;
import com.example.lize.data.Image;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.onegravity.rteditor.api.RTApi.getApplicationContext;

public class DocumentManager {
    private static final String TAG = "DocumentManager";
    private static DocumentManager documentManager = null;

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private StorageReference mStorageRef = storage.getReference();

    private Context context;
    private StorageTask mUploadTask;
    private Map<String, ArrayList<Image>> imagesNote;
    private Map<String, ArrayList<Document>> documentsNote;
    private Map<String, ArrayList<Audio>> audiosNote;

    private synchronized static void createInstance() {
        if (documentManager == null) {
            documentManager = new DocumentManager();
        }
    }

    public static DocumentManager getInstance() {
        if (documentManager == null) createInstance();
        return documentManager;
    }

    public DocumentManager() {
        imagesNote = new HashMap<>();
        documentsNote = new HashMap<>();
        audiosNote = new HashMap<>();
    }

    public void setContext(Context context){
        this.context = context;
    }

    public ArrayList<Document> getDocuments(String documentsID) {

        if (documentsNote.containsKey(documentsID)) {
            return documentsNote.get(documentsID);

        } else {
            ArrayList<Document> docs = new ArrayList<>();
            documentsNote.put(documentsID, docs);
            DocumentReference notasRef = db.collection("documents").document(documentsID);
            final long  MEGABYTE = 8192 * 1024;
            notasRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("files");
                    ArrayList<String> files = (ArrayList<String>) group;
                    for(String doc :files){
                        StorageReference singleDoc = mStorageRef.child(doc);

                        singleDoc.getBytes(MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                File file = new File(context.getFilesDir(),doc);
                                FileOutputStream outputStream = null;
                                try {

                                    outputStream = context.openFileOutput(doc,Context.MODE_PRIVATE);
                                    outputStream.write(bytes);
                                    Document f = new Document(Uri.fromFile(file));
                                    f.setName(doc);
                                    f.setId(doc);
                                    documentsNote.get(documentsID).add(f);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }finally {
                                    try {
                                        outputStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                    }
                }

            });

            return documentsNote.get(documentsID);
        }
    }

    public String addImageToCloud(String imagesID, Image image) {
        DocumentReference noteRef;
        if (imagesID == null) {
            noteRef = db.collection("images").document();
            imagesID = noteRef.getId();
        } else {
            noteRef = db.collection("images").document(imagesID);
        }
        if (imagesNote.containsKey(imagesID)) {
            imagesNote.get(imagesID).add(image);
        } else {
            ArrayList<Image> images = new ArrayList<>();
            imagesNote.put(imagesID, images);
            imagesNote.get(imagesID).add(image);
        }


        if (imagesNote.get(imagesID).size() == 1) {
            String ref = image.getId();// "-img-" + notas.get(DocumentsID).getBitmaps().indexOf(image);
            uploadImage(ref, image);
            Map<String, Object> noteDocuments = new HashMap<>();
            ArrayList<String> imagenes = new ArrayList<>();
            imagenes.add(ref);
            noteDocuments.put("imagesID", imagesID);
            noteDocuments.put("images", imagenes);
            String finalDocumentsID = imagesID;
            noteRef.set(noteDocuments).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        Log.d(TAG, "Note " + finalDocumentsID + "Document correctly saved.");
                    else Log.d(TAG, "Error saving note " + finalDocumentsID, task.getException());
                }
            });
            return imagesID;

        } else {
             DocumentReference notasRef = db.collection("images").document(imagesID);

            String finalDocumentsID2 = imagesID;

            notasRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("images");
                    ArrayList<String> imagenes = (ArrayList<String>) group;
                    String ref = image.getId();//+ "-img-" + (notas.get(finalDocumentsID2).getBitmaps().indexOf(image)) ;

                    imagenes.add(ref);
                    uploadImage(ref, image);
                    Map<String, Object> noteDocuments = new HashMap<>();
                    noteDocuments.put("imagesID", finalDocumentsID2);
                    noteDocuments.put("images", imagenes);
                    String finalDocumentsID1 = finalDocumentsID2;
                    noteRef.set(noteDocuments).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful())
                            Log.d(TAG, "Note " + finalDocumentsID1 + "Document correctly saved.");
                        else Log.d(TAG, "Error saving note " + finalDocumentsID1, task1.getException());
                    });
                }
            });
            return imagesID;
        }
    }


    public String addDocumentToCloud(String DocumentsID, Document doc) {
        DocumentReference noteRef;
        if (DocumentsID == null) {
            noteRef = db.collection("documents").document();
            DocumentsID = noteRef.getId();
        } else {
            noteRef = db.collection("documents").document(DocumentsID);
        }
        if (documentsNote.containsKey(DocumentsID)) {
            documentsNote.get(DocumentsID).add(doc);
        } else {
            ArrayList<Document> docs = new ArrayList<>();
            docs.add(doc);
            documentsNote.put(DocumentsID, docs);
            //notas.get(DocumentsID).getDocuments().add(doc);
        }

        if (documentsNote.get(DocumentsID).size() == 1) {
            String ref = doc.getId();// "-img-" + notas.get(DocumentsID).getBitmaps().indexOf(image);
            uploadDocument(ref, doc);
            Map<String, Object> noteDocuments = new HashMap<>();
            ArrayList<String> files = new ArrayList<>();
            files.add(ref);
            noteDocuments.put("DocumentsID", DocumentsID);
            noteDocuments.put("files", files);
            String finalDocumentsID = DocumentsID;
            noteRef.set(noteDocuments).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        Log.d(TAG, "Note " + finalDocumentsID + "Document correctly saved.");
                    else Log.d(TAG, "Error saving note " + finalDocumentsID, task.getException());
                }
            });
            return DocumentsID;

        } else {
            DocumentReference notasRef = db.collection("documents").document(DocumentsID);

            String finalDocumentsID2 = DocumentsID;

            notasRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("files");
                    ArrayList<String> files = (ArrayList<String>) group;
                    String ref = doc.getId();//+ "-img-" + (notas.get(finalDocumentsID2).getBitmaps().indexOf(image)) ;

                    files.add(ref);
                    uploadDocument(ref, doc);
                    Map<String, Object> noteDocuments = new HashMap<>();
                    noteDocuments.put("DocumentsID", finalDocumentsID2);
                    noteDocuments.put("files", files);
                    String finalDocumentsID1 = finalDocumentsID2;
                    noteRef.set(noteDocuments).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful())
                            Log.d(TAG, "Note " + finalDocumentsID1 + "Document correctly saved.");
                        else Log.d(TAG, "Error saving note " + finalDocumentsID1, task1.getException());
                    });
                }
            });
            return DocumentsID;
        }

    }



    public ArrayList<Image> getImagesNote(String imagesID) {
        if (imagesNote.containsKey(imagesID)) {
            return imagesNote.get(imagesID);
        } else {
            ArrayList<Image> images = new ArrayList<>();
            imagesNote.put(imagesID, images);
            DocumentReference notasRef = db.collection("images").document(imagesID);

            notasRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("images");
                    ArrayList<String> imagenes = (ArrayList<String>) group;
                    for(String doc :imagenes){
                            StorageReference singleDoc = mStorageRef.child(doc);
                        Image f = null;
                        f = new Image(context.getCacheDir(),doc);

                        singleDoc.getFile(f).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Local temp file has been created
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                        f.setId(doc);
                        imagesNote.get(imagesID).add(f);

                    }
                    }

            });

        return imagesNote.get(imagesID);

        }
    }

    private Boolean uploadImage(String ref, Image image) {
        final Boolean[] success = {false};

        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        //byte[] data = baos.toByteArray();


        StorageReference file = mStorageRef.child(ref);
        mUploadTask = file.putFile(Uri.fromFile(image)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                success[0] = true;
            }
        });

        return success[0];
    }


    private Boolean uploadDocument(String ref, Document document) {
        final Boolean[] success = {false};

        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        //byte[] data = baos.toByteArray();


        StorageReference file = mStorageRef.child(ref);
        mUploadTask = file.putFile(document.getUrl()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                success[0] = true;
            }
        });

        return success[0];
    }



    public void removeImageFromNote(String imagesID, int currentItem) {
        String ref = imagesNote.get(imagesID).get(currentItem).getId();//+ "-img-" + (currentItem);
        DocumentReference notasRef = db.collection("images").document(imagesID);

        imagesNote.get(imagesID).remove(currentItem);
        notasRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                List<String> group = (List<String>) document.get("images");
                ArrayList<String> imagenes = (ArrayList<String>) group;
                imagenes.remove(ref);

                Map<String, Object> noteDocuments = new HashMap<>();
                noteDocuments.put("imagesID", imagesID);
                noteDocuments.put("images", imagenes);
                notasRef.update(noteDocuments).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Log.d(TAG, "Note Image " + imagesID + "Document correctly update.");
                        else Log.d(TAG, "Error updating image note " + imagesID, task.getException());
                    }      });
                StorageReference singleDoc = mStorageRef.child(ref);
                singleDoc.delete();

            }
        });
    }

    public void removeDocumentFromNote(String documentsID, Document currentItem) {
        String ref = currentItem.getId();//+ "-img-" + (currentItem);
        DocumentReference notasRef = db.collection("documents").document(documentsID);

        documentsNote.get(documentsID).remove(currentItem);
        notasRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                List<String> group = (List<String>) document.get("files");
                ArrayList<String> documents = (ArrayList<String>) group;
                documents.remove(ref);

                Map<String, Object> noteDocuments = new HashMap<>();
                noteDocuments.put("DocumentsID", documentsID);
                noteDocuments.put("files", documents);
                notasRef.update(noteDocuments).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Log.d(TAG, "Note Image " + documentsID + "Document correctly update.");
                        else Log.d(TAG, "Error updating image note " + documentsID, task.getException());
                    }      });
                StorageReference singleDoc = mStorageRef.child(ref);
                singleDoc.delete();

            }
        });
    }

    public boolean arrayImagesEmpty(String imagesID){
        if(imagesNote.containsKey(imagesID)){
            return imagesNote.get(imagesID).isEmpty();
        }else{
            return true;
        }

    }

    public boolean arrayDocumentEmpty(String documentsID){
        if(documentsNote.containsKey(documentsID)){
            return documentsNote.get(documentsID).isEmpty();
        }else{
            return true;
        }
    }
    public boolean arrayAudiosEmpty(String AudiosID){
        if(audiosNote.containsKey(AudiosID)){
            return audiosNote.get(AudiosID).isEmpty();
        }else{
            return true;
        }
    }


    public String selectImageFromArray(String imagesID,int position){
        return imagesNote.get(imagesID).get(position).getPath();
    }

    public int imagesArraySize(String imagesID){
        if(imagesNote.containsKey(imagesID)){
            return imagesNote.get(imagesID).size();
        }
        return 0;

    }

    public int documentArraySize(String documentsID){
        return documentsNote.get(documentsID).size();
    }

    public Image BitmapToImage(Bitmap bitmap) throws IOException {
        Image f = new Image(getApplicationContext().getCacheDir(), String.valueOf(System.currentTimeMillis()));
        f.setId(String.valueOf(System.currentTimeMillis()));
        f.createNewFile();

//Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return f;
    }

    public String addAudioToCloud(String AudiosID, Audio a) {
        DocumentReference noteRef;
        if (AudiosID == null) {
            noteRef = db.collection("audios").document();
            AudiosID = noteRef.getId();
        } else {
            noteRef = db.collection("audios").document(AudiosID);
        }
        if (audiosNote.containsKey(AudiosID)) {
            audiosNote.get(AudiosID).add(a);
        } else {
            ArrayList<Audio> audios = new ArrayList<>();
            audios.add(a);
            audiosNote.put(AudiosID, audios);
        }

        if(audiosNote.get(AudiosID).size() == 1) {
            String ref = a.getID();// "-img-" + notas.get(DocumentsID).getBitmaps().indexOf(image);
            uploadAudio(a);
            Map<String, Object> noteAudios = new HashMap<>();
            ArrayList<String> files = new ArrayList<>();
            files.add(ref);
            noteAudios.put("audiosID", AudiosID);
            noteAudios.put("files", files);
            String finalDocumentsID = AudiosID;
            noteRef.set(noteAudios).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        Log.d(TAG, "Note " + finalDocumentsID + "Document correctly saved.");
                    else Log.d(TAG, "Error saving note " + finalDocumentsID, task.getException());
                }
            });

        } else {
            DocumentReference notasRef = db.collection("audios").document(AudiosID);

            String finalDocumentsID2 = AudiosID;

            notasRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("files");
                    ArrayList<String> files = (ArrayList<String>) group;
                    String ref = a.getID();//+ "-img-" + (notas.get(finalDocumentsID2).getBitmaps().indexOf(image)) ;

                    files.add(ref);
                    uploadAudio(a);
                    Map<String, Object> noteAudios = new HashMap<>();
                    noteAudios.put("audiosID", finalDocumentsID2);
                    noteAudios.put("files", files);
                    String finalDocumentsID1 = finalDocumentsID2;
                    noteRef.set(noteAudios).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Log.d(TAG, "Note " + finalDocumentsID1 + "Document correctly saved.");
                            else Log.d(TAG, "Error saving note " + finalDocumentsID1, task.getException());
                        }      });
                }
            });
        }
        return AudiosID;

    }


    private Boolean uploadAudio(Audio audio) {
        final Boolean[] success = {false};

        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        //byte[] data = baos.toByteArray();


        StorageReference file = mStorageRef.child(audio.getID());
        mUploadTask = file.putFile(Uri.fromFile(new File(audio.getAddress()))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                success[0] = true;
            }
        });

        return success[0];
    }

    public void removeAudioFromNote(String AudiosID, Audio currentItem) {
        String ref = currentItem.getID();//+ "-img-" + (currentItem);
        DocumentReference notasRef = db.collection("audios").document(AudiosID);

        audiosNote.get(AudiosID).remove(currentItem);
        notasRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                List<String> group = (List<String>) document.get("files");
                ArrayList<String> audios = (ArrayList<String>) group;
                audios.remove(ref);

                Map<String, Object> noteAudios = new HashMap<>();
                noteAudios.put("audiosID", AudiosID);
                noteAudios.put("files", audios);
                notasRef.update(noteAudios).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Log.d(TAG, "Note Image " + AudiosID + "Document correctly update.");
                        else Log.d(TAG, "Error updating image note " + AudiosID, task.getException());
                    }      });
                StorageReference singleDoc = mStorageRef.child(ref);
                singleDoc.delete();

            }
        });
    }


    public ArrayList<Audio> getAudios(String AudiosID) {

        if (audiosNote.containsKey(AudiosID)) {
            return audiosNote.get(AudiosID);

        } else {
            ArrayList<Audio> audios = new ArrayList<>();

            DocumentReference notasRef = db.collection("audios").document(AudiosID);
            notasRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("files");
                    ArrayList<String> files = (ArrayList<String>) group;
                    for(String audio :files){
                        String filename = context.getExternalCacheDir().getAbsolutePath() + File.separator + audio + ".3gp";
                        StorageReference singleDoc = mStorageRef.child(audio);

                        File file = new File(filename);
                        if(!file.exists()){
                            singleDoc.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    // Local temp file has been created
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }

                        MediaPlayer mp = new MediaPlayer();
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(context,Uri.parse(Uri.fromFile(file).toString()));
                        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        int millSecond = Integer.parseInt(duration);




                        audios.add(new Audio(audio,filename,millSecond));
                }

            }});
            audiosNote.put(AudiosID, audios);
            return audiosNote.get(AudiosID);
        }
    }
}
