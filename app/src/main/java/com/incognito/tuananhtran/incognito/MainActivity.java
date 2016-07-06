package com.incognito.tuananhtran.incognito;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private Uri fileUri;
    private static String photoPathname;

    private static  Intent cameraIntent;

    private static String TAG = "TEST";
    ImageButton upload;


    @Override
    public void onRequestPermissionsResult(int requestCode, String [] permissions,
                                           int [] grantResults ){
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // Permission has been granted by the user, so repeat the same code as in the else
                    cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); //-> create a file to save img
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); //-> set img filename


                    // --> Start the Image capture intent
                    startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            default:
                super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsmenu(true);
        setContentView(R.layout.activity_main);
        upload  = (ImageButton) findViewById(R.id.upload);


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // --> write the code for the camera
                // --> 0 - CHECK THAT API >= 23, OTHERWISE DON'T EVEN BOTHER TO DO THIS,
                //    remember that older versions require permissions upon installation, not at
                //    run time.
                if (Build.VERSION.SDK_INT >= 23) {
                    //--> 1 - CHECK THAT THE CAMERA PERMISSION EXISTS:
                    int permitted = ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.CAMERA);

                    if(permitted != PackageManager.PERMISSION_GRANTED){

                        //--> 2- CHECK THE USER HAS NOT CLICKED ON NEVER SHOW PERMISSION REQUEST AGAIN
                        if(!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                                showMessageOkCancel("You need to allow access to your camera",
                                        new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which){

                                                if(Build.VERSION.SDK_INT >= 23) { //--> You have to add this
                                                        // check otherwise requestPermissions complains
                                                        // about the API version
                                                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                                                            REQUEST_CODE_ASK_PERMISSIONS);
                                                }
                                            }
                                        });
                        }


                        //--> request a permission, when the user get back to you
                        // you will process it in the on RequestPermission results
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                                REQUEST_CODE_ASK_PERMISSIONS);
                    }else{
                        // permission has been granted continue as usual
                        cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); //-> create a file to save img
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); //-> set img filename


                        // --> Start the Image capture intent
                        startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                    }
                }


            }
        });



    }

    void showMessageOkCancel(String message,  DialogInterface.OnClickListener okListener){
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public  void onResume(){
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
       super.onCreateOptionsMenu(menu);
       getMenuInflater().inflate(R.menu.bottom_bar,menu);
        return true;
    }

    // Create a file Uri for saving an image

    private  static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    // Create a file for saving an image

    private static File getOutputMediaFile(int type){
        // 0 - Check that SD card is mounted:
        String state = Environment.getExternalStorageState();
        boolean file_creation = true;

        Log.d(TAG, "beginning before checking if SD is mounted");
        if((!Environment.MEDIA_MOUNTED.equals(state))){
            return null;     //--> returns null if external storage is not mounted
        }
        Log.d(TAG, "It pass the SD mounted test");
        // 1- Create a photo directory for my application
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Incognito_Pictures");

        // 2- Check that the directory exists, if not create a new one

        if(!mediaStorageDir.exists()) {
            Log.d("MyCameraApp", "The directory doesn't exist, creating a new one");
            file_creation = mediaStorageDir.mkdir();

            if(!file_creation){
                Log.d("MyCameraApp", "It already exists" + file_creation);
               // return null;
            }

        }

        // 3- Create the media File
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        if(type == MEDIA_TYPE_IMAGE){ //--> if it's an image
           // photoPathname = mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg";
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");

        }else{
            return null;
        }
        return mediaFile;

    }






}
