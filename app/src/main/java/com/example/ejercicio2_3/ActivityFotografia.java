package com.example.ejercicio2_3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ejercicio2_3.Configuraciones.SQLiteConexion;
import com.example.ejercicio2_3.Configuraciones.Transacciones;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityFotografia extends AppCompatActivity {

    Button btnSave,btnGalery;
    FloatingActionButton btnFoto;
    ImageView foto;
    EditText txtDescription;
    Bitmap imagen;

    static final int PETICION_ACCESO_CAM = 100;
    static final int TAKE_PIC_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotografia);

        btnSave = (Button) findViewById(R.id.btnguardarfoto);
        btnGalery = (Button) findViewById(R.id.btnverfotos);
                btnFoto = (FloatingActionButton) findViewById(R.id.botontomar);
        foto = (ImageView) findViewById(R.id.imageft);
        txtDescription = (EditText) findViewById(R.id.txtdescrip);

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                guardarDatos();
            }
        });

        btnGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ActivityListar.class);
                startActivity(intent);
            }
        });


    }

    private void guardarDatos() {
        try {
            fotografia(imagen);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            MediaStore.Images.Media.insertImage(getContentResolver(), imagen, imageFileName , "yourDescription");

            Intent intent = new Intent(this, ActivityFotografia.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            limpiar();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Debe de tomarse una foto ",Toast.LENGTH_LONG).show();
        }


    }

    private void fotografia( Bitmap bitmap) {

        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] ArrayFoto  = stream.toByteArray();

        ContentValues valores = new ContentValues();

        valores.put(Transacciones.descripcion,txtDescription.getText().toString());
        valores.put(String.valueOf(Transacciones.foto),ArrayFoto);

        Long resultado = db.insert(Transacciones.tblFotografias, Transacciones.id, valores);

        Toast.makeText(getApplicationContext(), "Registro ingreso con exito, Codigo " + resultado.toString()
                ,Toast.LENGTH_LONG).show();

        db.close();
    }

    private void limpiar() {
        txtDescription.setText("");
    }

    private void permisos() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PETICION_ACCESO_CAM);
        }else{
            tomarFoto();
        }
    }

    private void tomarFoto() {
        Intent takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takepic.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takepic,TAKE_PIC_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PETICION_ACCESO_CAM)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                tomarFoto();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Se necesitan permisos de acceso a la camara",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Byte [] arreglo;

        if(requestCode == TAKE_PIC_REQUEST && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            imagen = (Bitmap) extras.get("data");
            foto.setImageBitmap(imagen);
        }

    }

}