package com.example.lize.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lize.data.Audio;
import com.example.lize.data.Document;
import com.example.lize.data.Image;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onegravity.rteditor.api.RTApi.getApplicationContext;

public class DocumentManager {
    private static final String TAG = "DocumentManager";
    private static DocumentManager documentManager = null;

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final StorageReference mStorageRef = storage.getReference();

    private Context context;
    private StorageTask mUploadTask;
    private final Map<String, ArrayList<Image>> imagesNote;
    private final Map<String, ArrayList<Document>> documentsNote;
    private final Map<String, ArrayList<Audio>> audiosNote;

    /**
     * Metodo para crear una instancia de la clase
     */
    private synchronized static void createInstance() {
        if (documentManager == null) documentManager = new DocumentManager();
    }

    /**
     * Metodo para obteenr la instancia de la clase
     * @return instancia de DocumentManager
     */
    public static DocumentManager getInstance() {
        if (documentManager == null) createInstance();
        return documentManager;
    }

    /**
     * Constructor de la clase
     */
    public DocumentManager() {
        imagesNote = new HashMap<>();
        documentsNote = new HashMap<>();
        audiosNote = new HashMap<>();
    }

    /**
     * Metodo para establecer el conetxto
     * @param context Contexto de la clase
     */
    public void setContext(Context context){ this.context = context; }

    /**
     * Metodo para conseguir los documentos
     * @param documentsID ID de los documentos
     * @return Array con todos los documentos pedidos
     */
    public ArrayList<Document> getDocuments(String documentsID) {
        if (!documentsNote.containsKey(documentsID)) {
            ArrayList<Document> docs = new ArrayList<>();
            documentsNote.put(documentsID, docs);
            DocumentReference notasRef = db.collection("documents").document(documentsID);
            final long MEGABYTE = 8192 * 1024;

            notasRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("files");
                    ArrayList<String> files = (ArrayList<String>) group;

                    for (String doc : files) {
                        StorageReference singleDoc = mStorageRef.child(doc);
                        singleDoc.getBytes(MEGABYTE).addOnSuccessListener(bytes -> {
                            File file = new File(context.getFilesDir(), doc);
                            FileOutputStream outputStream = null;
                            try {
                                outputStream = context.openFileOutput(doc, Context.MODE_PRIVATE);
                                outputStream.write(bytes);
                                Document f = new Document(Uri.fromFile(file));
                                String base = doc.substring(0, doc.lastIndexOf("@"));
                                String ext = doc.substring(doc.lastIndexOf("."));

                                // Get Name (without @System.currentTime identifier
                                f.setName(base + ext);
                                f.setId(doc);
                                documentsNote.get(documentsID).add(f);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    outputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).addOnFailureListener(Throwable::printStackTrace);
                    }
                }
            });
        }
        return documentsNote.get(documentsID);
    }

    /**
     * Metodo para recuperar imagenes del Firebase Cloud
     * @param imagesID ID de las imagenes a recuperar
     */
    public void getImagesNote(String imagesID) {

        if (!imagesNote.containsKey(imagesID)) {
            ArrayList<Image> images = new ArrayList<>();
            imagesNote.put(imagesID, images);
            DocumentReference notasRef = db.collection("images").document(imagesID);
            notasRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("images");
                    ArrayList<String> imagenes = (ArrayList<String>) group;

                    for (String doc : imagenes) {
                        StorageReference singleDoc = mStorageRef.child(doc);
                        Image f = new Image(context.getCacheDir(), doc);
                        singleDoc.getFile(f).addOnSuccessListener(taskSnapshot -> {
                            // Local temp file has been created
                        }).addOnFailureListener(Throwable::printStackTrace);
                        f.setId(doc);
                        imagesNote.get(imagesID).add(f);
                    }
                }
            });
        }
        imagesNote.get(imagesID);
    }

    /**
     * Metodo para conseguir los audios de una Array de audios
     * @param AudiosID ID del Array de Audios
     * @return Array con los audios
     */
    public ArrayList<Audio> getAudios(String AudiosID) {

        if (!audiosNote.containsKey(AudiosID)) {
            ArrayList<Audio> audios = new ArrayList<>();
            DocumentReference notasRef = db.collection("audios").document(AudiosID);
            notasRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("files");
                    ArrayList<String> files = (ArrayList<String>) group;

                    for (String audio : files) {
                        String filename = context.getExternalCacheDir().getAbsolutePath() + File.separator + audio + ".3gp";
                        StorageReference singleDoc = mStorageRef.child(audio);

                        File file = new File(filename);
                        MediaPlayer mp = new MediaPlayer();
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

                        if (!(file.length() > 0)) {
                            singleDoc.getFile(file).addOnSuccessListener(taskSnapshot -> {
                                retriever.setDataSource(context, Uri.parse(Uri.fromFile(file).toString()));
                                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                int millSecond = Integer.parseInt(duration);
                                Audio newAudio = new Audio(audio, filename, millSecond);
                                audios.add(newAudio);

                                // Local temp file has been created
                            }).addOnFailureListener(Throwable::printStackTrace);

                        } else{
                            retriever.setDataSource(context, Uri.parse(Uri.fromFile(file).toString()));
                            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            int millSecond = Integer.parseInt(duration);
                            audios.add(new Audio(audio, filename, millSecond));
                        }
                    }
                }
            });

            audiosNote.put(AudiosID, audios);
        }
        return audiosNote.get(AudiosID);
    }

    //*******************
    //   ADD TO CLOUD
    //*******************


    /**
     * Metodo para añadir una imagen al Firebase Cloud
     * @param imagesID ID de la imagen
     * @param image imagen a subir
     * @return nuevo ID de la imagen
     */
    public String addImageToCloud(String imagesID, Image image) {
        DocumentReference noteRef;

        if (imagesID == null) {
            noteRef = db.collection("images").document();
            imagesID = noteRef.getId();
        } else noteRef = db.collection("images").document(imagesID);

        if (!imagesNote.containsKey(imagesID)) {
            ArrayList<Image> images = new ArrayList<>();
            imagesNote.put(imagesID, images);
        }
        imagesNote.get(imagesID).add(image);

        if (imagesNote.get(imagesID).size() == 1) {
            String ref = image.getId();             // "-img-" + notas.get(DocumentsID).getBitmaps().indexOf(image);
            uploadImage(ref, image);
            Map<String, Object> noteDocuments = new HashMap<>();
            ArrayList<String> imagenes = new ArrayList<>();
            imagenes.add(ref);
            noteDocuments.put("imagesID", imagesID);
            noteDocuments.put("images", imagenes);
            String finalDocumentsID = imagesID;
            noteRef.set(noteDocuments).addOnCompleteListener(task -> {
                if (task.isSuccessful()) Log.d(TAG, "Note " + finalDocumentsID + "Document correctly saved.");
                else Log.d(TAG, "Error saving note " + finalDocumentsID, task.getException());
            });

        } else {
            uploadImage(image.getId(), image);
            DocumentReference notasRef = db.collection("images").document(imagesID);
            notasRef.update("files", FieldValue.arrayUnion(image.getId())).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful())
                    Log.d(TAG, "Image " + image.getId() + " correctly saved.");
                else Log.d(TAG, "Error saving image " + image.getId(), task1.getException());
            });
        }

        return imagesID;
    }

    /**
     * Metodo para añadir un documento al Firebase Cloud
     * @param DocumentsID ID del documento
     * @param doc Documento a subir
     * @return nuevo ID del cdocuemnto
     */
    public String addDocumentToCloud(String DocumentsID, Document doc) {
        DocumentReference noteRef;

        if (DocumentsID == null) {
            noteRef = db.collection("documents").document();
            DocumentsID = noteRef.getId();
        } else noteRef = db.collection("documents").document(DocumentsID);


        if (documentsNote.containsKey(DocumentsID)) documentsNote.get(DocumentsID).add(doc);
        else {
            ArrayList<Document> docs = new ArrayList<>();
            docs.add(doc);
            documentsNote.put(DocumentsID, docs);
            //notas.get(DocumentsID).getDocuments().add(doc);
        }

        if (documentsNote.get(DocumentsID).size() == 1) {
            String ref = doc.getId();                   // "-img-" + notas.get(DocumentsID).getBitmaps().indexOf(image);
            uploadDocument(ref, doc);

            Map<String, Object> noteDocuments = new HashMap<>();
            ArrayList<String> files = new ArrayList<>();
            files.add(ref);
            noteDocuments.put("DocumentsID", DocumentsID);
            noteDocuments.put("files", files);
            String finalDocumentsID = DocumentsID;
            noteRef.set(noteDocuments).addOnCompleteListener(task -> {
                if (task.isSuccessful()) Log.d(TAG, "Note " + finalDocumentsID + "Document correctly saved.");
                else Log.d(TAG, "Error saving note " + finalDocumentsID, task.getException());
            });

        } else {
            uploadDocument(doc.getId(), doc);
            DocumentReference notasRef = db.collection("documents").document(DocumentsID);
            notasRef.update("files", FieldValue.arrayUnion(doc.getId())).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) Log.d(TAG, "Document " + doc.getId() + " correctly saved.");
                else Log.d(TAG, "Error saving document " + doc.getId(), task1.getException());
            });
        }

        return DocumentsID;
    }

    /**
     * Metodo para añadir un audio al Firebase Cloud
     * @param AudiosID Id del Array de audios
     * @param audio Audio a añadir
     * @return Nuevo ID del Audio
     */
    public String addAudioToCloud(String AudiosID, Audio audio) {
        DocumentReference noteRef;
        if (AudiosID == null) {
            noteRef = db.collection("audios").document();
            AudiosID = noteRef.getId();
        } else noteRef = db.collection("audios").document(AudiosID);

        if (audiosNote.containsKey(AudiosID)) audiosNote.get(AudiosID).add(audio);
        else {
            ArrayList<Audio> audios = new ArrayList<>();
            audios.add(audio);
            audiosNote.put(AudiosID, audios);
        }

        if (audiosNote.get(AudiosID).size() == 1) {
            String ref = audio.getID();// "-img-" + notas.get(DocumentsID).getBitmaps().indexOf(image);
            uploadAudio(audio);
            Map<String, Object> noteAudios = new HashMap<>();
            ArrayList<String> files = new ArrayList<>();
            files.add(ref);
            noteAudios.put("audiosID", AudiosID);
            noteAudios.put("files", files);
            String finalDocumentsID = AudiosID;
            noteRef.set(noteAudios).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) Log.d(TAG, "Note " + finalDocumentsID + "Document correctly saved.");
                    else Log.d(TAG, "Error saving note " + finalDocumentsID, task.getException());
                }
            });

        } else {
            uploadAudio(audio);
            DocumentReference notasRef = db.collection("documents").document(AudiosID);
            notasRef.update("files", FieldValue.arrayUnion(audio.getID())).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) Log.d(TAG, "Audio " + audio.getID() + " correctly saved.");
                else Log.d(TAG, "Error saving audio " + audio.getID(), task1.getException());
            });
        }
        return AudiosID;
    }

    //*******************
    //      UPLOADS
    //*******************

    /**
     * Metodo para subir las imagenes al layout de las notas rectangulares
     * @param ref Referencia para subir
     * @param image Imagen
     */
    private void uploadImage(String ref, Image image) {
        final Boolean[] success = {false};
        StorageReference file = mStorageRef.child(ref);
        mUploadTask = file.putFile(Uri.fromFile(image)).addOnSuccessListener(taskSnapshot -> success[0] = true);
    }

    /**
     * Metodo para subir un documento al layout de las notas
     * @param ref Referencia a subir
     * @param document Documento
     */
    private void uploadDocument(String ref, Document document) {
        final Boolean[] success = {false};
        StorageReference file = mStorageRef.child(ref);
        mUploadTask = file.putFile(document.getUrl()).addOnSuccessListener(taskSnapshot -> success[0] = true);
    }

    /**
     * Metodo para subir un audio al layout de las notas
     * @param audio Audio a subir
     */
    private void uploadAudio(Audio audio) {
        final Boolean[] success = {false};
        StorageReference file = mStorageRef.child(audio.getID());
        mUploadTask = file.putFile(Uri.fromFile(new File(audio.getAddress()))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { success[0] = true; }
        });
    }

    //*******************
    //      COPY
    //*******************

    /**
     * Método para copiar las imágenes de una Nota (por referencia) y devolver el nuevo imagesID
     * de la colección "images". Permite hacer un 'deep clone' de una Nota.
     * @param imagesID ID de las imágenes de la Nota a copiar
     * @return nuevo imageID de las imágenes de la Nota duplicada.
     */
    public String copyImages(String imagesID) {
        Image copyImage;
        String newImageID = null;
        for (int i = 0; i < imagesArraySize(imagesID); i++) {
            try {
                copyImage = BitmapToImage(BitmapFactory.decodeFile(selectImageFromArray(imagesID, i)));
                newImageID = addImageToCloud(newImageID, copyImage);
            } catch (IOException exc) {
                Log.e(TAG, "Couldn't copy image...");
            }
        }
        return newImageID;
    }

    /**
     * Método para copiar los Documentos de una Nota (por referencia) y devolver el nuevo documentID
     * de la colección "documents". Permite hacer un 'deep clone' de una Nota.
     * @param documentsID ID de los docus de la Nota a copiar
     * @return nuevo documentID de las docus de la Nota duplicada.
     */
    public String copyDocuments(String documentsID) {
        String newDocuID = null;

        for (Document copyDocu : getDocuments(documentsID)) {

            // Change @System.currentMillis identifier
            String doc = copyDocu.getId();
            String base = doc.substring(0, doc.lastIndexOf("@"));
            String ext = doc.substring(doc.lastIndexOf("."));
            Document newDocument = new Document(copyDocu.getUrl());
            newDocument.setName(copyDocu.getName());
            newDocument.setId(base + "@" + System.currentTimeMillis() + ext);
            newDocuID = addDocumentToCloud(newDocuID, newDocument);
        }
        return newDocuID;
    }

    /**
     * Método para copiar los Audios de una Nota (por referencia) y devolver el nuevo audiosID
     * de la colección "audios". Permite hacer un 'deep clone' de una Nota.
     * @param audiosID ID de las audios de la Nota a copiar
     * @return nuevo audioID de los audios de la Nota duplicada.
     */
    public String copyAudios(String audiosID) {
        String newAudioID = null;

        for (Audio copyAudio : getAudios(audiosID)) {

            // Change @System.currentMillis identifier
            Audio newAudioFile = new Audio(String.valueOf(System.currentTimeMillis()),
                    copyAudio.getAddress(), copyAudio.getDuration());

            newAudioID = addAudioToCloud(newAudioID, newAudioFile);
        }
        return newAudioID;
    }

    //*******************
    //      REMOVES
    //*******************

    /**
     * Metodo para borrar una imegen de una nota
     * @param imagesID ID de la imagen a borrar
     * @param currentItem Item en el qu eestamos trabajando
     */
    public void removeImageFromNote(String imagesID, int currentItem) {
        String ref = imagesNote.get(imagesID).get(currentItem).getId();         //+ "-img-" + (currentItem);
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
                notasRef.update(noteDocuments).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) Log.d(TAG, "Note Image " + imagesID + "Document correctly update.");
                    else Log.d(TAG, "Error updating image note " + imagesID, task1.getException());
                });
                StorageReference singleDoc = mStorageRef.child(ref);
                singleDoc.delete();
            }
        });
    }

    /**
     * Metodo ara eliminar un documentos de la una nota
     * @param documentsID ID del documento a borrar
     * @param currentItem Item en el que estamos trabajando
     */
    public void removeDocumentFromNote(String documentsID, Document currentItem) {
        String ref = currentItem.getId();
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
                notasRef.update(noteDocuments).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) Log.d(TAG, "Note Image " + documentsID + "Document correctly update.");
                    else Log.d(TAG, "Error updating image note " + documentsID, task1.getException());
                });
                StorageReference singleDoc = mStorageRef.child(ref);
                singleDoc.delete();
            }
        });
    }

    /**
     * Metodo para borrar un audio de una nota
     * @param AudiosID ID del Array de audios
     * @param currentItem Item en el que etsamos trabajando
     */
    public void removeAudioFromNote(String AudiosID, Audio currentItem) {
        String ref = currentItem.getID();
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
                notasRef.update(noteAudios).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) Log.d(TAG, "Note Image " + AudiosID + "Document correctly update.");
                    else Log.d(TAG, "Error updating image note " + AudiosID, task1.getException());
                });
                StorageReference singleDoc = mStorageRef.child(ref);
                singleDoc.delete();

            }
        });
    }


    /**
     * Metodo para saber si un Array de Imagenes contiene imagenes
     * @param imagesID ID del array de imagenes que queremos saber si esta vacio
     * @return True si contiene imagenes, False si no
     */
    public boolean arrayImagesEmpty(String imagesID) {
        if (imagesNote.containsKey(imagesID)) return imagesNote.get(imagesID).isEmpty();
        else return true;
    }

    /**
     * Metodo para saber si un Array de Documentos contiene documentos
     * @param documentsID ID del array de documentos que queremos saber si esta vacio
     * @return True si contiene documentos, False si no
     */
    public boolean arrayDocumentEmpty(String documentsID) {
        if (documentsNote.containsKey(documentsID))  return documentsNote.get(documentsID).isEmpty();
        else return true;
    }

    /**
     * Metodo para saber si un Array de Audios contiene audios
     * @param AudiosID ID del array de Audios que queremos saber si esta vacio
     * @return True si contiene Audios, False si no
     */
    public boolean arrayAudiosEmpty(String AudiosID) {
        if (audiosNote.containsKey(AudiosID)) return audiosNote.get(AudiosID).isEmpty();
        else return true;
    }

    //*****************
    //     ARRAYS
    //*****************

    /**
     * Metodo para conseguir una imagen de su Array de imagenes
     * @param imagesID Id del Array de imagenes
     * @param position Posicion de la imagen en el Array
     * @return Path de la imagen
     */
    public String selectImageFromArray(String imagesID,int position) { return imagesNote.get(imagesID).get(position).getPath(); }

    /**
     * Metodo para saber el tamaño de un Array de imagenes
     * @param imagesID ID del Array de imagenes
     * @return Tamaño del Array de imagenes
     */public int imagesArraySize(String imagesID) {
        if (imagesNote.containsKey(imagesID)) return imagesNote.get(imagesID).size();
        return 0;
    }

    /**
     * Metodo para saber el tamaño de un Array de documentos
     * @param documentsID ID del Array de documentos
     * @return Tamaño del Array de documentos
     */
    public int documentArraySize(String documentsID) { return documentsNote.get(documentsID).size(); }

    /**
     * Metodo para transformar un Bitmap en una Imagen
     * @param bitmap Bitmap a transformar
     * @return Imagen resultante
     * @throws IOException Excepción debia a un fallo de lectura del OutputStream
     */
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
}