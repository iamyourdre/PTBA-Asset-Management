package unsri.ptba.assetmanagement.Features.ShowAssetList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import unsri.ptba.assetmanagement.R;

public class CustomViewHolder extends RecyclerView.ViewHolder {

    TextView assetNameTextView;
    TextView assetSnTextView;
    TextView assetTagTextView;
//    TextView createdAtTextView;
    TextView updatedAtTextView;
//    TextView deletedAtTextView;
    ImageView crossButtonImageView;
    ImageView editButtonImageView;

    public CustomViewHolder(View itemView) {
        super(itemView);

        assetNameTextView = itemView.findViewById(R.id.assetNameTextView);
        assetSnTextView = itemView.findViewById(R.id.assetSnTextView);
//        createdAtTextView = itemView.findViewById(R.id.createdAtTextView);
        updatedAtTextView = itemView.findViewById(R.id.updatedAtTextView);
//        deletedAtTextView = itemView.findViewById(R.id.deletedAtTextView);
        assetTagTextView = itemView.findViewById(R.id.assetTagTextView);
        crossButtonImageView = itemView.findViewById(R.id.crossImageView);
        editButtonImageView = itemView.findViewById(R.id.editImageView);
    }
}
