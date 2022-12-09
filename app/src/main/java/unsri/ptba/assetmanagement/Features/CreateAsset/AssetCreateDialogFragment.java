package unsri.ptba.assetmanagement.Features.CreateAsset;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import unsri.ptba.assetmanagement.Database.DatabaseQueryClass;
import unsri.ptba.assetmanagement.R;
import unsri.ptba.assetmanagement.Util.Config;


@RequiresApi(api = Build.VERSION_CODES.O)
public class AssetCreateDialogFragment extends DialogFragment {

    private static AssetCreateListener assetCreateListener;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private EditText assetNameEditText;
    private static EditText assetSnEditText;
    private static EditText assetTagEditText;
    private Button createButton;
    private Button cancelButton;
    private ImageView addSnImage;
    private ImageView addTagImage;

    //Permission Code
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 2001;


    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;

    private String assetNameString = "";
    private String assetSn = "";
    private String assetTagString = "";
    private String createdAtString = "";

    public AssetCreateDialogFragment() {
        // Required empty public constructor
    }

    public static AssetCreateDialogFragment newInstance(String title, AssetCreateListener listener){
        assetCreateListener = listener;
        AssetCreateDialogFragment assetCreateDialogFragment = new AssetCreateDialogFragment();
        Bundle args = new Bundle();
//        args.putString("title", title);
        assetCreateDialogFragment.setArguments(args);

        assetCreateDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);

        return assetCreateDialogFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_asset_create_dialog, container, false);

        assetNameEditText = view.findViewById(R.id.assetNameEditText);
        assetSnEditText = view.findViewById(R.id.assetSnEditText);
        assetTagEditText = view.findViewById(R.id.assetTagEditText);
        createButton = view.findViewById(R.id.createButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        addSnImage = view.findViewById(R.id.addSnImage);
        addTagImage = view.findViewById(R.id.addTagImage);

        DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(getContext());
        assetNameEditText.setText(databaseQueryClass.getLastAssetName());

        String title = getArguments().getString(Config.TITLE);
        getDialog().setTitle(title);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assetNameString = assetNameEditText.getText().toString();
                assetSn = assetSnEditText.getText().toString();
                assetTagString = assetTagEditText.getText().toString();
                createdAtString = dtf.format(LocalDateTime.now());

                Asset asset = new Asset(-1, assetNameString, assetSn, assetTagString, createdAtString, createdAtString, null);

                long id = databaseQueryClass.insertAsset(asset);

                if(id>0){
                    asset.setId(id);
                    assetCreateListener.onAssetCreated(asset);
                    getDialog().dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        addSnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent OCRActivity = new Intent(getContext(), unsri.ptba.assetmanagement.Features.OCRActivity.class);
                OCRActivity.putExtra("textDestination", "sn");
                getContext().startActivity(OCRActivity);
            }
        });

        addTagImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent OCRActivity = new Intent(getContext(), unsri.ptba.assetmanagement.Features.OCRActivity.class);
                OCRActivity.putExtra("textDestination", "tag");
                startActivity(OCRActivity);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            //noinspection ConstantConditions
            dialog.getWindow().setLayout(width, height);
        }
    }

    public static void sendSNResult(String result, Context c) {
        assetSnEditText.setText(result);
    }
    public static void sendTagResult(String result, Context c) {
        assetTagEditText.setText(result);
    }

}
