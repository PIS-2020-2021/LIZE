package com.example.lize.workers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lize.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.io.InputStream;
import java.util.ArrayList;


public class CarouselFragment extends Fragment     {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private CarouselView carouselView;
    private ArrayList<Uri> imagesUris;
    private ArrayList<Bitmap> images;
    private View root;
    public static final int PICK_IMAGE = 1;

    public void initArrays(){
        imagesUris = new ArrayList< >();
        images = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_carousel, container, true);

        carouselView = root.findViewById(R.id.carouselView);

        return root;

    }

    // Carousel de imagenes de las notas
    private void init_carousel() {
        ImageListener imageListener = (position, imageView) -> {
            imageView.setImageBitmap(images.get(position));
            registerForContextMenu(imageView);
        };

        carouselView.setPageCount(images.size());
        carouselView.setImageListener(imageListener);

        if (images.isEmpty()) {
            carouselView.setVisibility(View.GONE);
        }else{
            carouselView.setVisibility(View.VISIBLE);
        }
    }


    //Intent para seleccionar una imagen del dispositivo e insertarlo en el Carrusel
    public void selectImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        pickIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        Intent chooserIntent = Intent.createChooser(getIntent, "Selecciona una imagen");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    //Método para el tratamiento de los permisos de la aplicación
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(getContext(), "Permission Denied!",Toast.LENGTH_SHORT);

            }
        }
    }

    // Tratamiento de los datos de Intents de imagenes y documentos.
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContext().getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        images.add(0,bitmap);
                        init_carousel();
                        imagesUris.add(0,selectedImageUri);

                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    }
                }
            }
    }

    // Context Menu para estilos de texto e imagenes
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(2, v.getId(), 0, "Eliminar");

    }

    //Esta función realiza las opciones de eliminar documento e imagen en función del elemento seleccionado
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Eliminar")) {
            imagesUris.remove(carouselView.getCurrentItem());
            images.remove(carouselView.getCurrentItem());
            init_carousel();
        }
        return true;
    }


}
