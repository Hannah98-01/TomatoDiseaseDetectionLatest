package com.example.tomatodiseasedetectionlatest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Bundle;

import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;



import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class Save_Activity extends AppCompatActivity {
    ImageView ImageShow;
    TextView Disease;
    EditText Zone;
    EditText PlantNo;
    Button btnsave;

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        ImageShow = findViewById(R.id.PassImage);
        Bundle ex = getIntent().getExtras();
        byte[] byteArray = ex.getByteArray("Image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageShow.setImageBitmap(bitmap);

        Disease = (TextView) findViewById(R.id.DiseaseDetected);
        String DiseaseDetected;
        DiseaseDetected = getIntent().getStringExtra("DiseaseDiagnosed");
        Disease.setText(DiseaseDetected);

        Zone = (EditText) findViewById(R.id.zone);
        PlantNo = (EditText) findViewById(R.id.PlantNo);
        btnsave = (Button) findViewById(R.id.savebtn);

        ActivityCompat.requestPermissions(Save_Activity.this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);



        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String DiseaseName = Disease.getText().toString();
                String area = Zone.getText().toString();
                String num = PlantNo.getText().toString();


                String path = getExternalFilesDir(null).toString() +"/"+ area + num+"Report.pdf";
                File file = new File(path);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Document document = new Document(PageSize.A4);
                try {
                    PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));

                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                document.open();
                try {
                    document.add(new Paragraph("Report Details",catFont));
                    document.add(new Paragraph("\n"));
                    document.add(new Paragraph("Disease Detected: "+DiseaseName));
                    document.add(new Paragraph("\n"));
                    document.add(new Paragraph("Zone Area: " + area));
                    document.add(new Paragraph("\n"));
                    document.add(new Paragraph("Plant No.: " + num));
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Pdf Created Successfully", Toast.LENGTH_SHORT).show();
                document.close();

                Intent intent = new Intent(Save_Activity.this,HomeActivity.class);
                startActivity(intent);
            }

        });
    }
}
