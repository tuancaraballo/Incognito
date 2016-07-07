package com.incognito.tuananhtran.incognito;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;

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
    ImageView image;


    //--> 4 - START THE CAMERA INTENT HERE AFTER THE USER HAS ACCEPTED THE PERMISSION TO USE HIS CAMERA
    @Override
    public void onRequestPermissionsResult(int requestCode, String [] permissions,
                                           int [] grantResults ){
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("MyCameraApp",  "GOT TO OnRequest PermissionResult!");
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
        image = (ImageView) findViewById(R.id.photo);
        fileUri = null;

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // --> write the code for the camera
                // --> 0 - CHECK THAT API >= 23, OTHERWISE DON'T EVEN BOTHER TO DO THIS,
                //    remember that older versions require permissions upon installation, not at
                //    run time.
                Log.d(TAG, " CLICKED ON THE BUTTON");
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

                        //--> 3 - REQUEST THE PERMISSION: THIS FUNCTION WILL POP UP A DIALOG BOX
                        // ASKING THE USER FOR PERMISSION. NOTICE THAT THE SAME CODE YOU HAVE
                        // BELOW UNDER THE ELSE YOU SHOULD HAVE IT ON REQUESTPERMISSIONRESULT
                        // because it's a call back so you don't when the user will respond.
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

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
       // image.setImageDrawable(Drawable.createFromPath(photoPathname));
        Log.d("MyCameraApp",  "GOT TO ACTIVITY RESULT!");
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                //-->Image captured and saved to fileUri specified in the intent
                Log.d("MyCameraApp",  "RESULT_OK!");
                image.setImageDrawable(Drawable.createFromPath(photoPathname));
//                Toast.makeText(this, "Image saved to:\n" +
//                        data.getData(), Toast.LENGTH_LONG).show();
            } else if(resultCode == RESULT_CANCELED){
                Log.d("MyCameraApp",  "RESULT_CANCELED!");
                // user cancelled the image capture
            }else{
                Log.d("MyCameraApp",  "CAMERA FAILED");
               // image capture failed, advise  user
            }
        }
    }

    /*
       Description: This is a message to display upon the rationale of the user.
     */
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





    public static File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            //This is also the case if the directory already existed
            Log.i("wall-splash", "Directory not created");
        }
        return file;
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
        File photosDirectory;


        if((!Environment.MEDIA_MOUNTED.equals(state))){
            return null;     //--> returns null if external storage is not mounted
        }
        Log.d(TAG, "It pass the SD mounted test");

//      1- Create a photo directory for my application
        photosDirectory = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"IncognitoPhotos");
        // 2- Check that the directory exists, if not create a new one
        if(!photosDirectory.exists()) {
            Log.d("MyCameraApp", "The directory doesn't exist, creating a new one");
            //file_creation = photosDirectory.mkdirs();

            if(!photosDirectory.mkdirs()){
           // if (!photosDirectory.mkdirs()){
                Log.d("MyCameraApp", "FILE CREATION FAILED  " );
                //return null;
            }else{
                Log.d("MyCameraApp", "FILE CREATION SUCCEEDED ");
            }
        }

        // 3- Create the media File
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        if(type == MEDIA_TYPE_IMAGE){ //--> if it's an image
            photoPathname = photosDirectory.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg";
            mediaFile = new File(photoPathname);

        }else{
            return null;
        }
        return mediaFile;

    }






}
