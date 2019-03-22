package com.example.minichallenge;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_TAKE_PICTURE = 0;
    ImageView img;
    SQLiteDatabase db;
    EditText idQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        idQ = (EditText) findViewById(R.id.etID);
        Button btnCamera = (Button)findViewById(R.id.button);
        Button btnPhoto = (Button)findViewById(R.id.btnDisplay);
        img = (ImageView) findViewById(R.id.camera_image);
        db = openOrCreateDatabase("Photos", MODE_PRIVATE, null);
        String query = "CREATE TABLE IF NOT EXISTS Photos ( "
                +  "  id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +  "  photo BLOB NOT NULL "
                +  ");";
        db.execSQL(query);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(picIntent, REQ_CODE_TAKE_PICTURE);
                }
        });


        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentID = Integer.parseInt(idQ.getText().toString());

                Cursor cr;
                byte[] getBMP = null;
                Bitmap bitmap = null;
                cr = db.rawQuery("SELECT photo FROM Photos WHERE id = " + currentID + ";", null);
                if (cr.moveToFirst()) {
                    getBMP = cr.getBlob(cr.getColumnIndex("photo"));
                }
                cr.close();
                bitmap = BitmapFactory.decodeByteArray(getBMP, 0, getBMP.length);
                img.setImageBitmap(bitmap);

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQ_CODE_TAKE_PICTURE && resultCode == RESULT_OK){
            super.onActivityResult(requestCode,resultCode,data);
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(bmp);
            byte[] bmpArray;
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            bmpArray = bStream.toByteArray();
            ContentValues cv = new ContentValues();
            cv.put("Photos", bmpArray);
            db.insert("Photos", null, cv);
        }
    }
}