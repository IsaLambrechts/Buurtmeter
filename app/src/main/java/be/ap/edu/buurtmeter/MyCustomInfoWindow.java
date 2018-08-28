package be.ap.edu.buurtmeter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import static android.support.v4.app.ActivityCompat.startActivityForResult;


public class MyCustomInfoWindow extends MarkerInfoWindow {

    private static final int CAM_REQUEST=1313;
    private static ImageView imageView;
    /**
     * @param layoutResId layout that must contain these ids: bubble_title,bubble_description,
     *                    bubble_subdescription, bubble_image
     * @param mapView
     */
    public MyCustomInfoWindow(int layoutResId, MapView mapView, Activity activity, Bitmap image) {
        super(layoutResId, mapView);
        TextView txtView = mView.findViewById(R.id.title);
        TextView description = mView.findViewById(R.id.bubble_description);
        //Button btn = (Button)(mView.findViewById(R.id.addPicture));
        imageView = (ImageView) mView.findViewById(R.id.bubble_image);
//        btn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "Button clicked", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(activity, intent,CAM_REQUEST, new Bundle());
//                // Show picture in marker
//            }
//        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(activity, intent,CAM_REQUEST, new Bundle());
            }
        });

    }

//    public MyCustomInfoWindow(MapView mapView) {
//        super(R.layout.bonuspack_bubble, mapView);
//        TextView txtView = mView.findViewById(R.id.title);
//        TextView description = mView.findViewById(R.id.bubble_subdescription);
//        Button btn = (Button)(mView.findViewById(R.id.bubble_moreinfo));
//        btn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "Button clicked", Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    @Override
    public void onOpen(Object item){
        super.onOpen(item);
        mView.findViewById(R.id.bubble_image).setVisibility(View.VISIBLE);
    }

    public static void activityResult(int requestCode, int resultCode, Intent data){

        System.out.println("activity");
        if(requestCode == 1313){
            if(resultCode == Activity.RESULT_OK) {
                System.out.println("ok");
               Bitmap bmp = (Bitmap) data.getExtras().get("data");
               imageView.setImageBitmap(bmp);
            }
        }
    }


}
