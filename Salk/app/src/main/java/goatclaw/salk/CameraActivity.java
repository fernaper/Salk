package goatclaw.salk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.hardware.camera2.*; //todo mirar para acualizar a camera2
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.List;

public class CameraActivity extends Activity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Bitmap imageTaken;

    public static boolean t = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        Log.e("Resolution", ""+preview.getWidth()+" "+preview.getHeight());

        // Create an instance of Camera
        //todo revisar el tamaño de la cámara
        mCamera = getCameraInstance();
        //scaleCamera(370,380);

        // Create our Preview view and set it as the content of our activity.

        setCameraDisplayOrientation(this,Camera.CameraInfo.CAMERA_FACING_FRONT);
        mPreview = new CameraPreview(this, mCamera);
        preview.addView(mPreview);
        Log.e("Resolution", ""+preview.getWidth()+" "+preview.getHeight());

        Button btnTake = (Button) findViewById(R.id.btnAction);
        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void scaleCamera(int w, int h) {

        Camera.Parameters params = mCamera.getParameters();
        // Check what resolutions are supported by your camera
        List<Camera.Size> sizes = params.getSupportedPictureSizes();

        // Iterate through all available resolutions and choose one.
        // The chosen resolution will be stored in mSize.
        Camera.Size mSize;
        params.setPictureSize(w, h);
        for (Camera.Size size : sizes) {
            if (similarResolution(size, w, h)) {
                mSize = size;
                params.setPictureSize(mSize.width, mSize.height);
                break;
            }
        }
        mCamera.setParameters(params);
    }

    public static Camera getCameraInstance(){
            Camera c = null;
        try {
            c =  Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.e("CamNull", "getCameraInstance: null", e);
        }
        if(c == null)
            Log.i("CamNull", "null");

        return c; // returns null if camera is unavailable
    }

    private static boolean similarResolution(Camera.Size size, int w, int h) {
        if(size.width - 100 < w && size.width + 100 > w)
            if(size.height - 100 < h && size.height + 100 > h){
                Log.e("ChangeResolution", ""+w+" "+h+ "....."+size.width+" "+size.height);
                return true;

            }

        Log.e("NOTChangeResolution", ""+w+" "+h + "....."+size.width+" "+size.height);
        return false;

    }

    public void setCameraDisplayOrientation(Activity activity, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        mCamera.setDisplayOrientation(result);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
             imageBitmap = (Bitmap) extras.get("data");
            ConnectAPI conection = (ConnectAPI) new ConnectAPI();
            conection.sendImage(imageBitmap);
        }
    }*/
}