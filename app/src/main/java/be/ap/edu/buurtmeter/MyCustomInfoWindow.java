package be.ap.edu.buurtmeter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.app.ActivityCompat.startActivityForResult;


public class MyCustomInfoWindow extends MarkerInfoWindow {

    private static ImageView imageView;

    public MyCustomInfoWindow(int layoutResId, MapView mapView, Activity activity) {
        super(layoutResId, mapView);
        TextView txtView = mView.findViewById(R.id.title);
        TextView description = mView.findViewById(R.id.bubble_description);
        imageView = (ImageView) mView.findViewById(R.id.bubble_image);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence cameraOptions[] = new CharSequence[] {"Take a picture", "Load from Photo Album"};
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("Make a choice");
                alert.setItems(cameraOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(activity,takePicture, 0, new Bundle());//zero can be replaced with any action code
                        } else if(i == 1) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(activity, pickPhoto , 1, new Bundle());//one can be replaced with any action code
                        }
                    }
                });
                alert.show();
            }
        });

    }

    @Override
    public void onOpen(Object item){
        super.onOpen(item);
        mView.findViewById(R.id.bubble_image).setVisibility(View.VISIBLE);
    }

    public static void activityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(bmp);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    imageView.setImageURI(selectedImage);
                }
                break;
        }
    }


}
