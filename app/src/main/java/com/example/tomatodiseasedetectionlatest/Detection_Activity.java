package com.example.tomatodiseasedetectionlatest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Detection_Activity extends AppCompatActivity {
    private int mInputSize = 224;
    private String mModelPath = "model_unquant.tflite";
    private String mLabelPath = "labels.txt";
    private Classifier classifier;
    protected Interpreter tflite;
    private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private int imageSizeX;
    private int imageSizeY;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private Bitmap bitmap;
    private List<String> labels;
    String encodedImage;
    ImageView imageView;
    Uri imageuri;
    Button buclassify, bugallery, bucamera,busave,bucancel;
    TextView classitext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);
        imageView = (ImageView) findViewById(R.id.image);
        buclassify = (Button) findViewById(R.id.classify);
        bugallery = (Button) findViewById(R.id.gallery);
        bucamera = (Button) findViewById(R.id.camera);
        busave =(Button) findViewById(R.id.busave);
        bucancel = (Button)findViewById(R.id.bucancel);
        classitext = (TextView) findViewById(R.id.classifytext);

        bucancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        busave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Disease = classitext.getText().toString();
//                Toast.makeText(Detection_Activity.this, encodedImage, Toast.LENGTH_LONG).show();
                StringRequest request = new StringRequest(Request.Method.POST, "https://tamatodiseasedetection.000webhostapp.com/uploadImage.php", new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Detection_Activity.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Detection_Activity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected  Map<String,String> getParams() throws AuthFailureError{
                        Map<String,String>params = new HashMap<>();
                        params.put("image",encodedImage);
                        params.put("diseasename",Disease);
                        return  params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(Detection_Activity.this);
                requestQueue.add(request);
                if (bitmap!=null){
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress((Bitmap.CompressFormat.JPEG),100,bytes);
                    byte[] byteArray = bytes.toByteArray();


                    Intent intent = new Intent(Detection_Activity.this,Save_Activity.class);
                    intent.putExtra("Image",byteArray);
                    intent.putExtra("DiseaseDiagnosed",Disease);
                    startActivity(intent);
                }
            }
        });

        bucamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermissions();
            }
        });

        bugallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 12);
            }
        });

        try {
            initClassifier();
            initViews();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        buclassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buclassify.setVisibility(View.GONE);
                bugallery.setVisibility(View.GONE);
                bucamera.setVisibility(View.GONE);
                busave.setVisibility(View.VISIBLE);
                bucancel.setVisibility(View.VISIBLE);

                List<Classifier.Recognition> result = classifier.recognizeImage(bitmap);
                classitext.setText(result.get(0).toString());
            }
        });
    }
    private void initClassifier() throws IOException{
        classifier = new Classifier(getAssets(),mModelPath,mLabelPath,mInputSize);
    }

    private void initViews(){
        findViewById(R.id.image);
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, CAMERA_PERM_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permissions is Required to Use Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openCamera(){
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera,CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE){
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);

            imageStore(bitmap);
        }

        else if (requestCode==12 && resultCode==RESULT_OK && data!=null){
            imageuri = data.getData();
            try {
                InputStream inputStream =getContentResolver().openInputStream(imageuri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
                imageStore(bitmap);

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
        }
    }

    private void imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

        byte[] imageBytes = stream.toByteArray();
        encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}

