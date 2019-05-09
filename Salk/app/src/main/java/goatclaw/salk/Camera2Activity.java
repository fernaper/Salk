package goatclaw.salk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Camera2Activity extends AppCompatActivity {
    private Size previewsize;
    private Size jpegSizes[] = null;
    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder previewBuilder;
    private CameraCaptureSession previewSession;
    private Button btnAction;
    private EditText etPalabraRestante;
    private EditText etPalabraCorreta;
    private ImageView pictogram;

    private String[] words;
    private String word;
    private int position;
    private byte[] imageBytes;


    private final String URL_NEURONAL_NETWORK = "http://88.0.109.140:5500/check_frame";
    private final String URL_DATABASE = "http://92.176.178.247:5754/";

    private static HashMap<String, String> responseNN;
    private static HashMap<String, String> responseDB;

    private Context ctx;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    final int REQUEST_WRITE_STORAGE = 1;
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        ctx = getApplicationContext();
        position = -1;

        //Todo: Llamada a la API de barral

        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        textureView = (TextureView) findViewById(R.id.textureview);
        etPalabraCorreta = (EditText)  findViewById(R.id.etWordChecked);
        etPalabraRestante = (EditText)  findViewById(R.id.etWordNotChecked);
        pictogram = (ImageView) findViewById(R.id.pictogram);
        btnAction = (Button) findViewById(R.id.btnAction);
        Button back = (Button) findViewById(R.id.btnBack);


        textureView.setSurfaceTextureListener(surfaceTextureListener);

        int textureViewWidth = (int) (screenWidth*0.8);
        int textureViewHeight = (int) (screenHeight*0.8);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(textureViewWidth, textureViewHeight);

        //Para el prototipo se cogen algunos valores a pincho. Cambian de un móvil a otro
        layoutParams.setMargins((screenWidth-textureViewWidth)/2 - 30,  screenHeight - (36+textureViewHeight),
                            (screenWidth-textureViewWidth)/2 - 30, 36);

        textureView.setLayoutParams(layoutParams);

        int diff = new Random().nextInt(4);
        diff += SettingsActivity.getLevel()*6;



        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position == -1) { //primera iteración
                    etPalabraCorreta.setText("");

                    int diff = new Random().nextInt(4);
                    diff += SettingsActivity.getLevel()*6;
                    diff = diff == 0?1:diff;

                    final int difficulty = diff;


                    if(SettingsActivity.getLevel() == 2) { //hard level
                    //Se quiere coger una frase en vez de palabras
                    //TODO ver que pasa si se devuele una frase

                        RequestQueue queueDatabase = Volley.newRequestQueue(ctx);
                        StringRequest databaseRequest = new StringRequest(Request.Method.POST, URL_DATABASE + "get_phrase", new Response.Listener<String>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onResponse(String response) {


                                Log.i("PETITION_DB", response);

                                ObjectMapper mapper = new ObjectMapper();
                                try {
                                    responseDB = mapper.readValue(response, new TypeReference<Map<String, String>>() {
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                words = null;
                                word = responseDB.get("word");
                                position++;
                                etPalabraRestante.setText(word);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("PETITION_DB", error.toString());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("user", SettingsActivity.getUsername());
                                params.put("language", SettingsActivity.getLanguage());
                                params.put("difficulty", "" + SettingsActivity.getLevel());
                                Log.i("PETITION_DB", "getParams: " + difficulty);
                                return params;
                            }
                        };
                        queueDatabase.add(databaseRequest);


                    }else {


                        if (words == null) {

                            RequestQueue queueDatabase = Volley.newRequestQueue(ctx);
                            StringRequest databaseRequest = new StringRequest(Request.Method.POST, URL_DATABASE + "get_word", new Response.Listener<String>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onResponse(String response) {


                                    Log.i("PETITION_DB", response);

                                    ObjectMapper mapper = new ObjectMapper();
                                    try {
                                        responseDB = mapper.readValue(response, new TypeReference<Map<String, String>>() {
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                    words = responseDB.get("word").split(" ");
                                    for (int i = 0; i < words.length; i++)
                                        Log.i("PETITION_DB", words[i]);
                                    word = words[0];
                                    words = removeIndex(words, 0);
                                    btnAction.setText("Check");
                                    position++;
                                    etPalabraRestante.setText(word);
                                    int id = getResources().getIdentifier("goatclaw.salk:drawable/" + word.substring(0, 1), null, null);
                                    pictogram.setImageResource(id);


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("PETITION_DB", error.toString());
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("user", SettingsActivity.getUsername());
                                    params.put("language", SettingsActivity.getLanguage());
                                    params.put("difficulty", "" + difficulty);
                                    Log.i("PETITION_DB", "getParams: " + difficulty);
                                    return params;
                                }
                            };
                            queueDatabase.add(databaseRequest);
                        } else {
                            word = words[0];
                            words = removeIndex(words, 0);
                            btnAction.setText("Check");
                            position++;
                            etPalabraRestante.setText(word);
                            int id = getResources().getIdentifier("goatclaw.salk:drawable/" + word.substring(0, 1), null, null);
                            pictogram.setLayoutParams(new TableRow.LayoutParams(200, 200));
                            pictogram.setImageResource(id);

                        }
                    }
                    /*
                    String[] words = {"boa", "raca", "chundasvinto", "rufus", "hola", "vida", "soja", "cabra"};
                    int rnd = new Random().nextInt(words.length);
                    palabra = words[rnd];
                    */

                }else if(position < word.length()){ //queda palabra

                    btnAction.setEnabled(false);
                    char aux = word.charAt(position);

                    while(aux == ' '){
                        position++;
                        aux = word.charAt(position);
                        etPalabraCorreta.setText(etPalabraCorreta.getText()+" ");
                        etPalabraRestante.setText(word.substring(position));
                    }
                    final char letra = aux;

                    getPicture(letra); //extraer foto

                    //esperar a que haya foto
                    while(imageBytes == null);

                    //enviar y esperar la respuesta de la API
                    final String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    RequestQueue queueNN = Volley.newRequestQueue(ctx);
                    // Request a string response from the provided URL_NEURONAL_NETWORK.
                    StringRequest neuronalNetworkRequest = new StringRequest(Request.Method.POST, URL_NEURONAL_NETWORK, new Response.Listener<String>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.

                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                responseNN = mapper.readValue(response, new TypeReference<Map<String, String>>(){});
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast toast;
                            Log.i("Letra", "Comprobamos " + responseNN.get("prediction") + " frente a " + letra);
                            if(responseNN.get("prediction").equals(""+letra)){
                                Log.i("Letra", "Grande niño");
                                position++;
                                etPalabraCorreta.setText(etPalabraCorreta.getText()+""+letra);
                                etPalabraRestante.setText(word.substring(position));

                                if(position == word.length()){ //Acierta toda la palabra
                                    toast = Toast.makeText(ctx, "Muy bien", Toast.LENGTH_LONG);
                                    etPalabraCorreta.setText("Muy bien");
                                    addWord(word);
                                    position = -1;
                                    btnAction.setText("Siguiente");
                                }else{
                                    toast = Toast.makeText(ctx, "Correcto", Toast.LENGTH_LONG);
                                    int pos = position;
                                    if( SettingsActivity.getLevel() != 2){
                                        int id = getResources().getIdentifier("goatclaw.salk:drawable/" + word.charAt(pos), null, null);
                                        while(word.charAt(pos) == ' '){
                                            pos++;
                                            id = getResources().getIdentifier("goatclaw.salk:drawable/" + word.charAt(pos), null, null);
                                        }
                                        pictogram.setImageResource(id);
                                    }
                                }
                            }else{
                                toast = Toast.makeText(ctx, "Incorrecto, vuelva a intentarlo", Toast.LENGTH_LONG);
                                Log.i("Letra", "Nice try");
                            }
                            btnAction.setEnabled(true);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                            Log.i("PETITION",  response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("PETITION",  error.toString());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("frame", encodedImage);
                            params.put("letter", "" + letra);
                            return params;
                        }
                    };

                    // Add the request to the RequestQueue.
                    queueNN.add(neuronalNetworkRequest);

                }else { //Just in case!
                    //Aquí no debería entrar
                    etPalabraCorreta.setText("Muy bien");
                    position = -1;
                    btnAction.setText("Start");
                }
                /*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
                pictogram.setLayoutParams(layoutParams);*/
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void addWord(final String palabra){
        RequestQueue queueDatabase = Volley.newRequestQueue(this);


        StringRequest myReq = new StringRequest(Request.Method.PUT, URL_DATABASE+"record_success", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ObjectMapper mapper = new ObjectMapper();
                Log.i("PETITION_DB",  response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("PETITION_DB",  error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user", SettingsActivity.getUsername());
                params.put("difficulty", ""+SettingsActivity.getLevel());
                params.put("word",palabra);
                return params;
            }
        };
        queueDatabase.add(myReq);
    }


    void getPicture(final char letra) {
        if (cameraDevice == null) {
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640, height = 480;
            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder capturebuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            capturebuilder.addTarget(reader.getSurface());
            capturebuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            capturebuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();

                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        imageBytes = new byte[buffer.capacity()];
                        buffer.get(imageBytes);

                        //ConnectAPI connection = new ConnectAPI();
                        //connection.sendImage(bytes, ctx, letra);
                        //save(bytes);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                    finally {
                        if(image!=null)
                            image.close();
                    }
                }

                void save(byte[] bytes) {
                    File file12 = getOutputMediaFile();
                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file12);
                        outputStream.write(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (outputStream != null)
                                outputStream.close();
                        } catch (Exception e) {
                        }
                    }
                }
            };

            HandlerThread handlerThread = new HandlerThread("takepicture");
            handlerThread.start();
            final Handler handler = new Handler(handlerThread.getLooper());
            reader.setOnImageAvailableListener(imageAvailableListener, handler);
            final CameraCaptureSession.CaptureCallback previewSSession = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                }

                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    startCamera();
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(capturebuilder.build(), previewSSession, handler);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, handler);

        } catch (Exception e) {
        }
    }


    private String[] removeIndex(String[] array, int index) {

        String[] result = new String[array.length - 1];
        if(array.length == 1)
            return null;

        int count = 0;
        for (int i = 0; i < array.length; i++) {
            if(i != index){
                result[count] = array[i];
                count++;
            }
        }

        return result;
    }

    public void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String camerId = manager.getCameraIdList()[1];//[1] for FRONT camera
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(camerId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            previewsize = map.getOutputSizes(SurfaceTexture.class)[0];
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
                openCamera();
                return;
            }
            manager.openCamera(camerId, stateCallback, null);
        }catch (Exception e)
        {
        }
    }

    private TextureView.SurfaceTextureListener surfaceTextureListener=new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private CameraDevice.StateCallback stateCallback=new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice=camera;
            startCamera();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
        }
        @Override
        public void onError(CameraDevice camera, int error) {
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraDevice!=null)
        {
            cameraDevice.close();
        }
    }

    void  startCamera()
    {
        if(cameraDevice==null||!textureView.isAvailable()|| previewsize==null)
        {
            return;
        }
        SurfaceTexture texture=textureView.getSurfaceTexture();
        if(texture==null)
        {
            return;
        }
        texture.setDefaultBufferSize(previewsize.getWidth(),previewsize.getHeight());
        Surface surface=new Surface(texture);
        try
        {
            previewBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        }catch (Exception e)
        {
        }
        previewBuilder.addTarget(surface);
        try
        {
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    previewSession=session;
                    getChangedPreview();
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            },null);
        }catch (Exception e)
        {
        }
    }

    void getChangedPreview()
    {
        if(cameraDevice==null)
        {
            return;
        }
        previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        HandlerThread thread=new HandlerThread("changed Preview");
        thread.start();
        Handler handler=new Handler(thread.getLooper());
        try
        {
            previewSession.setRepeatingRequest(previewBuilder.build(), null, handler);
        }catch (Exception e){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStorageDirectory(),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

}