package unsri.ptba.assetmanagement.Features;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

import unsri.ptba.assetmanagement.Features.CreateAsset.AssetCreateDialogFragment;
import unsri.ptba.assetmanagement.R;

public class OCRActivity extends Activity {

    private ImageView mPreviewIv;

    //Permission Code
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 2001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreviewIv  = findViewById(R.id.imageIv);

        //camera permission
        cameraPermission = new String[] {Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permission
        storagePermission = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        showImageImportDialog();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.addImage:
//                showImageImportDialog();
//                break;
//
//            case R.id.about:
//                dialogAbout();
//                break;
//        }
//        return true;
//    }

    private void showImageImportDialog() {
//        String[] items = {"Camera", "Gallery"};
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//
//        dialog.setTitle("Select Image");
//        dialog.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == 0) {
                    if (!checkCameraPermission()) {
                        //camera permission not allowed, request it
                        requestCameraPermission();
                    } else {
                        //permission allowed, take picture
                        pickCamera();
                    }
//                }

//                if (which == 1) {
//                    if (!checkStoragePermission()) {
//                        //storage permission not allowed, request it
//                        requestStoragePermission();
//                    } else {
//                        //permission allowed, take picture
//                        pickGallery();
//                    }
//                }
//            }
//        });
//        dialog.create().show();
    }

    private void pickGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        //intent to take image from camera, it will also be save to storage to get high quality image
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPick"); //title of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text"); //title of the picture
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (writeStorageAccepted) {
                        pickGallery();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //handle image result
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent intent = getIntent();
        String destination = intent.getStringExtra("textDestination");

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //got image from gallery now crop it
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON) //enable image guid lines
                        .start(this);
            }

            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //got image from camera now crop it
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON) //enable image guid lines
                        .start(this);
            }
        }

        //get cropped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri(); //get image uri
                //set image to image view
                mPreviewIv.setImageURI(resultUri);

                //get drawable bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    //get text from sb until there is no text
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append(" ");
                    }
                    if(Objects.equals(destination, "sn")){
                        AssetCreateDialogFragment.sendSNResult(filterSnText(sb.toString()),  getBaseContext());
                    } else if(Objects.equals(destination, "tag")){
                        AssetCreateDialogFragment.sendTagResult(filterTagText(sb.toString()),  getBaseContext());
                    }
                    finish();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //if there is any error show it
                Exception error = result.getError();
                Toast.makeText(this, " "+error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String filterSnText(String sample) {

        String tempSample = "";

        sample = sample.toUpperCase();
        String [] headSide = {
                "SERIALNUMBER: ",
                "SERIAL NUMBER: ",
                "S/N: ",
                "SIN: ",
                "SLN: ",
                "SERLALNUMBER: ",
                "SERLAL NUMBER: ",
                "SERLAINUMBER: ",
                "SERLAI NUMBER: ",
                "SN: ",

                "SERIALNUMBER ",
                "SERIAL NUMBER ",
                "S/N ",
                "SERLALNUMBER ",
                "SERLAL NUMBER ",
                "SERLAINUMBER ",
                "SERLAI NUMBER ",

                "SERIALNUMBER:",
                "SERIAL NUMBER:",
                "S/N:",
                "SIN:",
                "SLN:",
                "SERLALNUMBER:",
                "SERLAL NUMBER:",
                "SERLAINUMBER:",
                "SERLAI NUMBER:",
                "SN:",

                "SERIALNUMBER.",
                "SERIAL NUMBER.",
                "S/N.",
                "SIN.",
                "SLN.",
                "SERLALNUMBER.",
                "SERLAL NUMBER.",
                "SERLAINUMBER.",
                "SERLAI NUMBER.",
                "SN."
        };
        boolean checkHeadPointer = false;
        int posHeadPointer = 0;

        sample=sample.replaceAll(" - ", "");
        sample=sample.replaceAll(" -", "");
        sample=sample.replaceAll("- ", "");
        sample=sample.replaceAll("-", "");
        sample=sample.replaceAll("\n", " ");

        // put head pointer
        for(int i=0;i<headSide.length;i++) {
            if (sample.contains(headSide[i])) {
                sample=sample.replaceAll(headSide[i], "@#");
                checkHeadPointer = true;
            }
        }

        // remove head
        if(checkHeadPointer) {
            while(posHeadPointer<=sample.length()) {
                if(sample.charAt(posHeadPointer) == '@') {
                    posHeadPointer++;
                    if(sample.charAt(posHeadPointer) == '#') {
                        break;
                    }
                }
                posHeadPointer++;

            }
            for(int i=posHeadPointer+1;i<sample.length();i++) {
                tempSample += sample.charAt(i);
            }
        }
        sample = "";

        // get sn
        for(int i=0;i<tempSample.length();i++) {
            if(tempSample.charAt(i)!=' ') {
                sample += tempSample.charAt(i);
            } else {
                break;
            }
        }
        return sample;
    }

    public static String filterTagText(String sample) {

        String tempSample = "";

        sample = sample.toUpperCase();
        String [] headSide = {
                "ASSET TAG : ",
                "ASSET TAG: ",
                "ASSET TAG:",
                "ASSETTAG: "

        };
        boolean checkHeadPointer = false;
        int posHeadPointer = 0;

        // put head pointer
        for(int i=0;i<headSide.length;i++) {
            if (sample.contains(headSide[i])) {
                sample=sample.replaceAll(headSide[i], "@#");
                checkHeadPointer = true;
            }
        }

        // remove head
        if(checkHeadPointer) {
            while(posHeadPointer<=sample.length()) {
                if(sample.charAt(posHeadPointer) == '@') {
                    posHeadPointer++;
                    if(sample.charAt(posHeadPointer) == '#') {
                        break;
                    }
                }
                posHeadPointer++;

            }
            for(int i=posHeadPointer+1;i<sample.length();i++) {
                tempSample += sample.charAt(i);
            }
        } else {
            tempSample = sample;
        }

        sample = "";

        // get sn
        for(int i=0;i<tempSample.length();i++) {
            if(tempSample.charAt(i)!=' ') {
                sample += tempSample.charAt(i);
            } else {
                break;
            }
        }

        return sample;
    }

}