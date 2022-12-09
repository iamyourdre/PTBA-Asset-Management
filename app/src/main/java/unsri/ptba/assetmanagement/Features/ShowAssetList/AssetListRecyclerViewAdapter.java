package unsri.ptba.assetmanagement.Features.ShowAssetList;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import unsri.ptba.assetmanagement.Database.DatabaseQueryClass;
import unsri.ptba.assetmanagement.Features.CreateAsset.Asset;
import unsri.ptba.assetmanagement.Features.UpdateAssetInfo.AssetUpdateDialogFragment;
import unsri.ptba.assetmanagement.Features.UpdateAssetInfo.AssetUpdateListener;
import unsri.ptba.assetmanagement.R;
import unsri.ptba.assetmanagement.Util.Config;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AssetListRecyclerViewAdapter extends RecyclerView.Adapter<CustomViewHolder> {

    private Context context;
    private List<Asset> assetList;
    private DatabaseQueryClass databaseQueryClass;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AssetListRecyclerViewAdapter(Context context, List<Asset> assetList) {
        this.context = context;
        this.assetList = assetList;
        databaseQueryClass = new DatabaseQueryClass(context);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public void setAssetList(List<Asset> assetList) {
        this.assetList = assetList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.asset_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final int itemPosition = position;
        final Asset asset = assetList.get(position);

        holder.assetNameTextView.setText(asset.getName());
        holder.assetSnTextView.setText(String.valueOf(asset.getAssetSn()));
//        holder.createdAtTextView.setText(asset.getCreatedAt());
        holder.updatedAtTextView.setText(asset.getUpdatedAt());
//        holder.deletedAtTextView.setText(asset.getDeletedAt());
//        holder.createdAtTextView.setText(dtformat.dtf2IdDtf(asset.getCreatedAt()));
//        holder.updatedAtTextView.setText(dtformat.dtf2IdDtf(asset.getUpdatedAt()));
//        holder.deletedAtTextView.setText(dtformat.dtf2IdDtf(asset.getDeletedAt()));
        holder.assetTagTextView.setText(asset.getAssetTag());

        holder.crossButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Anda yakin ingin menghapus aset ini?");
                        alertDialogBuilder.setPositiveButton("Ya",
                                new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        deleteAsset(asset);
                                    }
                                });

                alertDialogBuilder.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        holder.editButtonImageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                AssetUpdateDialogFragment assetUpdateDialogFragment = AssetUpdateDialogFragment.newInstance(asset.getId(), itemPosition, new AssetUpdateListener() {
                    @Override
                    public void onAssetInfoUpdated(Asset asset, int position) {
                        assetList.set(position, asset);
                        notifyDataSetChanged();
                    }
                });
                assetUpdateDialogFragment.show(((AssetListActivity) context).getSupportFragmentManager(), Config.UPDATE_ASSET);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void deleteAsset(Asset asset) {
        asset.setDeletedAt(dtf.format(LocalDateTime.now()));
        long count = databaseQueryClass.updateAssetInfo(asset);

        if(count>0){
            assetList.remove(asset);
            notifyDataSetChanged();
            ((AssetListActivity) context).viewVisibility();
            Toast.makeText(context, "Aset berhasil dihapus", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(context, "Terjadi kesalahan! Aset gagal dihapus.", Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return assetList.size();
    }
}
