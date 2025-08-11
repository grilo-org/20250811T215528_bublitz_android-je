package br.jus.tre_mt.caixa1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import dao.DBAdapter;

public class ActFoto extends AppCompatActivity {


    //GPS
    private static final int REQUEST_CODE = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 2000;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    Location loc;
    //GPS


    private final int CAMERA = 1;
    private final int GALLERY = 2;

    //1 = Foto tirada pela camera do dispositivo. Salva data de registro, latitude e longitude
    //2 - Foto da galeria
    private int acaoBtnMedia = 2;

    final Context ctx = this;

    String tipo_midia;
    static File imageFile = null;

    private Long id;
    private int v, targetW, targetH;
    private ImageView imgFoto;

    //GPS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case REQUEST_CODE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    }
                }
            }
        }
    }
    //GPS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_foto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        id = b.getLong("id");
        v = b.getInt("v");


        imgFoto = (ImageView) findViewById(R.id.imgFoto);
        if (imageFile != null) {
            setPic();
        }
        targetW = imgFoto.getWidth();
        targetH = imgFoto.getHeight();

        //GPS
        //check permission runtime
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_CODE);

        }else {

            //if permission is granted
            buildLocationRequest();
            buildLocationCallback();

            //Create FusedLocationProviderClient
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        }

        //GPS
    }

    public void usar(View v) {

        //stop request location updates
        if (ActivityCompat.checkSelfPermission(ActFoto.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ActFoto.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ActFoto.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //add message if necessary

            } else {
                // No explanation needed, we can request the permission.
                //permission
                ActivityCompat.requestPermissions(ActFoto.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

                return;
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        } else {

            fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        }
        //stop request location updates

        if (imageFile != null) {


            DBAdapter db = new DBAdapter(ctx);
            db.open();

            if (acaoBtnMedia == 1) {

                db.salvaMidia(id, imageFile.getPath(), new Timestamp(System.currentTimeMillis()).toString(), loc);

            } else
            {
                db.salvaMidia(id, imageFile.getPath());
            }

           db.close();
           imageFile = null;
           finish();

        }else
            Toast.makeText(ctx, "Selecione um arquivo ou tire uma foto!", Toast.LENGTH_LONG).show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageName = "btv_f_" + id + "_" + v + ".png";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        storageDir.mkdirs();
        File image = new File(storageDir, imageName);
        return image;
    }


    public void galeria(View v) {

        if (verificarPermissaoAcessoMidia() == 0) {

            tipo_midia = MediaStore.Images.Media.DATA;
            //Intent takeMidiaIntent = new Intent(Intent.ACTION_PICK);
            Intent takeMidiaIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(takeMidiaIntent.setType("image/*"), "Selecione uma imagem")
                    , GALLERY);

        }
        else
        {
            ActivityCompat.requestPermissions(ActFoto.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

    }

    public void foto(View v) {

        if (verificarPermissaoAcessoMidia() == 0) {

            //Start request location updates
            // Here, thisActivity is the current activity
            if (ActivityCompat.checkSelfPermission(ActFoto.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(ActFoto.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(ActFoto.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //add message if necessary

                } else {
                    // No explanation needed, we can request the permission.
                    //permission
                    ActivityCompat.requestPermissions(ActFoto.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

                    return;
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {

                if(locationRequest == null && locationCallback == null) {
                    buildLocationRequest();
                    buildLocationCallback();
                    //Create FusedLocationProviderClient
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                }

                // Permission has already been granted
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                        locationCallback, Looper.myLooper());
            }
            //Start request location updates

            /*if (ActivityCompat.checkSelfPermission(ActFoto.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(ActFoto.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {

                //permission
                ActivityCompat.requestPermissions(ActFoto.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

                return;
            }

            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                    locationCallback, Looper.myLooper());*/
            //Start request location updates

            acaoBtnMedia = 1;


            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                imageFile = null;
                try {
                    imageFile = createImageFile();
                } catch (IOException ex) {
                }
                if (imageFile != null) {

                    Uri photoURI = FileProvider.getUriForFile(
                            ActFoto.this,
                            "br.jus.tre_mt.caixa1.fileprovider",
                            imageFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAMERA);
                }
            }
        }
        else
        {
            ActivityCompat.requestPermissions(ActFoto.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

    }

    public int verificarPermissaoAcessoMidia() {

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        return permissionCheck;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CHECK_SETTINGS) {

            if (requestCode == CAMERA && resultCode == RESULT_OK) {

                setPic();

            } else if (requestCode == GALLERY && resultCode == RESULT_OK) {
                try {

                    Uri uri = data.getData();
                    String path = RealPathUtil.getRealPath(getApplicationContext(), uri, tipo_midia);
                    imageFile = new File(path);
                    setPic();

                } catch (Exception e) {
                }
            }
        }
    }


    private void setPic() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        if (targetH == 0) {
            targetH = 200;
        }
        if (targetW == 0) {
            targetW = 200;
        }
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath(), bmOptions);
        imgFoto.setImageBitmap(bitmap);
    }

    //GPS
    private void buildLocationCallback() {

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()){
                    loc = location;
                }
            }
        };
    }


    private void buildLocationRequest() {

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);

        //Request that user activate GPS
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(ActFoto.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


    }

    //GPS
}
