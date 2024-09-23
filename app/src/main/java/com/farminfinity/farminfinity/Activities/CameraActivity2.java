package com.farminfinity.farminfinity.Activities;

import static androidx.core.content.FileProvider.getUriForFile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.farminfinity.farminfinity.Helper.MyLocation;
import com.farminfinity.farminfinity.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.jar.Pack200;

public class CameraActivity2 extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private PreviewView viewFinder;
    private TextView tvWarning;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvCaptureDate;
    private Button btnTakePhoto;
    private LinearLayout viewNotes;
    public static String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        // Initialize views
        btnTakePhoto = (Button) findViewById(R.id.image_capture_button);
        viewFinder = (PreviewView) findViewById(R.id.viewFinder);
        tvWarning = (TextView) findViewById(R.id.gps_warning);
        tvLatitude = (TextView) findViewById(R.id.latitude);
        tvLongitude = (TextView) findViewById(R.id.longitude);
        tvCaptureDate = (TextView) findViewById(R.id.capture_date);
        viewNotes = (LinearLayout) findViewById(R.id.ll_notes);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });
        // Check app permissions
        checkRequestPermissions();
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider, ImageCapture imageCapture) {
        // Image analysis
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder().setTargetResolution(new Size(1920, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                int rotation = image.getImageInfo().getRotationDegrees();
                Toast.makeText(CameraActivity2.this, String.valueOf(rotation), Toast.LENGTH_SHORT).show();
                image.close();
            }
        });

        /*OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                Toast.makeText(CameraActivity.this, Integer.toString(orientation), Toast.LENGTH_SHORT).show();
                if(orientation > 135){
                    int rotation = Surface.ROTATION_180;
                    imageAnalysis.setTargetRotation(rotation);
                    imageCapture.setTargetRotation(rotation);
                }
            }
        };
        orientationEventListener.enable();*/

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
    }

    private void startCamera() {
        imageCapture = new ImageCapture.Builder().build();
        // Show notes
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
        Date currentTime = Calendar.getInstance().getTime();
        tvCaptureDate.setText("TIME: " + simpleDateFormat.format(currentTime));
        // Initialize camera provider
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        // After requesting a CameraProvider, verify that its initialization succeeded when the view is created.
        // The following code shows how to do this:
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                // Bind the camera image stream to preview(View)
                bindPreview(cameraProvider, imageCapture);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
        // Get GPS location
        // MyLocation myLocation = new MyLocation();
        // myLocation.getLocation(this, locationResult);
    }

    private void takePhoto() {
        if (imageCapture == null)
            return;
        // Create time stamped name and MediaStore entry.
        /*String name = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.US)
                .format(System.currentTimeMillis());
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        }
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(getContentResolver()
                        , MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        , values).build();*/

        imageCapture.takePicture(ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        super.onCaptureSuccess(image);
                        Bitmap bitmapOriginal = imageProxyToBitmap(image);
                        /*WatermarkText watermarkText1 = new WatermarkText(tvWarning)
                                .setPositionX(0)
                                .setPositionY(0)
                                .setTextColor(Color.RED)
                                .setBackgroundColor(Color.WHITE)
                                .setTextAlpha(255)
                                .setRotation(0)
                                .setTextSize(40);
                        WatermarkText watermarkText2 = new WatermarkText(tvCaptureDate)
                                .setPositionX(0)
                                .setPositionY(0)
                                .setTextColor(Color.BLACK)
                                .setTextAlpha(255)
                                .setRotation(0)
                                .setTextSize(40);;

                        List<WatermarkText> waterMarks = new ArrayList<WatermarkText>();
                        waterMarks.add(watermarkText1);
                        waterMarks.add(watermarkText2);

                        Bitmap bitmapEdited = WatermarkBuilder
                                .create(MainActivity.this, bitmapOriginal)
                                .loadWatermarkTexts(waterMarks)
                                .getWatermark()
                                .getOutputImage();*/

                        // Get view in bitmap
                        viewNotes.setDrawingCacheEnabled(true);
                        viewNotes.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                        viewNotes.buildDrawingCache();
                        //Bitmap bitmapPictureNotes = viewNotes.getDrawingCache();
                        Bitmap bitmapPictureNotes = Bitmap.createBitmap(viewNotes.getDrawingCache());
                        // Draw text using canvas
                        Bitmap bitmapEdited = drawTextToBitmap(bitmapOriginal, bitmapPictureNotes);
                        //saveImageToGallery(bitmapEdited);
                        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmapEdited, "Title", null);
                        setResultOk(Uri.parse(path));
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                    }
                }
        );
        //cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture, imageAnalysis, preview);
    }

    private void captureVideo() {

    }

    private void setResultOk(Uri imagePath) {
        Intent intent = new Intent();
        intent.putExtra("path", imagePath);
        setResult(RESULT_OK, intent);
        finish();//finishing activity
    }

    private Bitmap drawTextToBitmap(Bitmap bitmapBackground, Bitmap bitmapOverlay) {
        /*String mText = "Hello" + "\n" + "World";
        android.graphics.Bitmap.Config bitmapConfig = bitmapBackground.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are immutable,
        // so we need to convert it to mutable one
        bitmapBackground = bitmapBackground.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmapBackground);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(206, 21, 21));
        // text size in pixels
        paint.setTextSize((int) (100));
        // text shadow
        //paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(mText, 0, mText.length(), bounds);
        int x = (bitmapBackground.getWidth() - bounds.width()) / 6;
        int y = (bitmapBackground.getHeight() + bounds.height()) / 5;

        canvas.drawText(mText, 10, 100, paint);
        return bitmapBackground;*/

        // Combine two bitmap into one new bitmap
        Bitmap newBitmap = Bitmap.createBitmap(bitmapBackground.getWidth(), bitmapBackground.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bitmapBackground, new Matrix(), null);
        Rect rect = new Rect(0, 0, bitmapOverlay.getWidth() * 2, bitmapOverlay.getHeight() * 2);
        canvas.drawBitmap(bitmapOverlay, null, rect, null);
        return newBitmap;
    }

    private void saveImageToGallery(Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.US)
                .format(System.currentTimeMillis());
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_" + timeStamp + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Pictures/CameraX-Image");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Objects.requireNonNull(fos);

                Toast.makeText(this, "Image Saved In Gallery", Toast.LENGTH_SHORT).show();
            } else {
                // Save image to gallery
                String savedImageURL = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Bird", "Image of bird");
                // Parse the gallery image url to uri
                Uri savedImageURI = Uri.parse(savedImageURL);
                Toast.makeText(this, "Image saved to internal!!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Image not saved \n" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap imageProxyToBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        buffer.rewind();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        return BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);
    }

    private MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
        @Override
        public void gotLocation(final Location location) {
            // Do something
            try {
                tvWarning.setVisibility(View.GONE);
                tvLatitude.setVisibility(View.VISIBLE);
                tvLongitude.setVisibility(View.VISIBLE);
                tvLatitude.setText("LATITUDE: " + location.getLatitude());
                tvLongitude.setText("LONGITUDE: " + location.getLongitude());
            } catch (Exception e) {
                Toast.makeText(CameraActivity2.this, "Could not get location.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    };

    private void checkRequestPermissions() {
        // below line is use to request
        // permission in the current activity.
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Dexter.withActivity(this)
                    // below line is use to request the number of
                    // permissions which are required in our app.
                    // below is the list of permissions
                    .withPermissions(Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                    // after adding permissions we are
                    // calling an with listener method.
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            // this method is called when all permissions are granted
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                // do you work now
                                startCamera();
                            }
                            // check for permanent denial of any permission
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                // permission is denied permanently,
                                // we will show user a dialog message.
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            // this method is called when user grants some
                            // permission and denies some of them.
                            permissionToken.continuePermissionRequest();
                        }
                    }).withErrorListener(new PermissionRequestErrorListener() {
                        // this method is use to handle error
                        // in runtime permissions
                        @Override
                        public void onError(DexterError error) {
                            // we are displaying a toast message for error message.
                            Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                        }
                    })
                    // below line is use to run the permissions
                    // on same thread and to check the permissions
                    .onSameThread().check();

        } else {
            Dexter.withActivity(this)
                    // below line is use to request the number of
                    // permissions which are required in our app.
                    // below is the list of permissions
                    .withPermissions(Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    // after adding permissions we are
                    // calling an with listener method.
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            // this method is called when all permissions are granted
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                // do you work now
                                startCamera();
                            }
                            // check for permanent denial of any permission
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                // permission is denied permanently,
                                // we will show user a dialog message.
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            // this method is called when user grants some
                            // permission and denies some of them.
                            permissionToken.continuePermissionRequest();
                        }
                    }).withErrorListener(new PermissionRequestErrorListener() {
                        // this method is use to handle error
                        // in runtime permissions
                        @Override
                        public void onError(DexterError error) {
                            // we are displaying a toast message for error message.
                            Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                        }
                    })
                    // below line is use to run the permissions
                    // on same thread and to check the permissions
                    .onSameThread().check();
        }
    }

    // below is the shoe setting dialog
    // method which is use to display a
    // dialogue message.
    private void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity2.this);

        // below line is the title
        // for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this method is called on click on positive
                // button and on clicking shit button we
                // are redirecting our user from our app to the
                // settings page of our app.
                dialog.cancel();
                // below is the intent from which we
                // are redirecting our user.
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this method is called when
                // user click on negative button.
                dialog.cancel();
            }
        });
        // below line is used
        // to display our dialog
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            tvWarning.setText("The GPS feature is not open.");
        } else {
            // Get GPS location
            tvWarning.setText("Waiting for a valid signal.");
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(this, locationResult);
        }
    }
}