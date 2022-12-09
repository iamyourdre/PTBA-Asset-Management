package unsri.ptba.assetmanagement.Features.UpdateAssetInfo;

import unsri.ptba.assetmanagement.Features.CreateAsset.Asset;

public interface AssetUpdateListener {
    void onAssetInfoUpdated(Asset asset, int position);
}
