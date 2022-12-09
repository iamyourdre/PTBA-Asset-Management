package unsri.ptba.assetmanagement.Features.UpdateAssetInfo;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import unsri.ptba.assetmanagement.Database.DatabaseQueryClass;
import unsri.ptba.assetmanagement.Features.CreateAsset.Asset;
import unsri.ptba.assetmanagement.R;
import unsri.ptba.assetmanagement.Util.Config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RequiresApi(api = Build.VERSION_CODES.O)
public class AssetUpdateDialogFragment extends DialogFragment {

    private static long assetId;
    private static int assetItemPosition;
    private static AssetUpdateListener assetUpdateListener;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Asset mAsset;

    private EditText assetNameEditText;
    private EditText assetSnEditText;
    private EditText assetTagEditText;
    private EditText createdAtEditText;
    private EditText updatedAtEditText;
    private Button updateButton;
    private Button cancelButton;

    private String assetNameString = "";
    private String assetSnString = "";
    private String assetTagString = "";
    private String updatedAtString = "";

    private DatabaseQueryClass databaseQueryClass;

    public AssetUpdateDialogFragment() {
        // Required empty public constructor
    }

    public static AssetUpdateDialogFragment newInstance(long id, int position, AssetUpdateListener listener){
        assetId = id;
        assetItemPosition = position;
        assetUpdateListener = listener;
        AssetUpdateDialogFragment assetUpdateDialogFragment = new AssetUpdateDialogFragment();
        Bundle args = new Bundle();
//        args.putString("title", "Update asset information");
        assetUpdateDialogFragment.setArguments(args);

        assetUpdateDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);

        return assetUpdateDialogFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_asset_update_dialog, container, false);

        databaseQueryClass = new DatabaseQueryClass(getContext());

        assetNameEditText = view.findViewById(R.id.assetNameEditText);
        assetSnEditText = view.findViewById(R.id.assetSnEditText);
        assetTagEditText = view.findViewById(R.id.assetTagEditText);
        createdAtEditText = view.findViewById(R.id.createdAtEditText);
        updatedAtEditText = view.findViewById(R.id.updateAtEditText);
        updateButton = view.findViewById(R.id.updateAssetInfoButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        String title = getArguments().getString(Config.TITLE);
        getDialog().setTitle(title);

        mAsset = databaseQueryClass.getAssetById(assetId);

        if(mAsset !=null){
            assetNameEditText.setText(mAsset.getName());
            assetSnEditText.setText(mAsset.getAssetSn());
            assetTagEditText.setText(mAsset.getAssetTag());
            createdAtEditText.setText(mAsset.getCreatedAt());
            updatedAtEditText.setText((mAsset.getUpdatedAt().compareTo(mAsset.getCreatedAt())>0?mAsset.getUpdatedAt():"-"));

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assetNameString = assetNameEditText.getText().toString();
                    assetSnString = assetSnEditText.getText().toString();
                    assetTagString = assetTagEditText.getText().toString();
                    updatedAtString = dtf.format(LocalDateTime.now());

                    mAsset.setName(assetNameString);
                    mAsset.setAssetSn(assetSnString);
                    mAsset.setAssetTag(assetTagString);
                    mAsset.setUpdatedAt(updatedAtString);

                    long id = databaseQueryClass.updateAssetInfo(mAsset);

                    if(id>0){
                        assetUpdateListener.onAssetInfoUpdated(mAsset, assetItemPosition);
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

        }

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

}
