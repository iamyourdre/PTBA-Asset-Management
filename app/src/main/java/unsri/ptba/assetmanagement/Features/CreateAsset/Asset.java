package unsri.ptba.assetmanagement.Features.CreateAsset;

import java.util.Locale;

public class Asset {
    private long id;
    private String assetName;
    private String assetSn;
    private String assetTag;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public Asset(int id, String assetName, String assetSn, String assetTag, String createdAt, String updatedAt, String deletedAt) {
        this.id = id;
        this.assetName = assetName;
        this.assetSn = assetSn;
        this.assetTag = assetTag;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return assetName;
    }

    public void setName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetSn() {
        return assetSn;
    }

    public void setAssetSn(String assetSn) {
        this.assetSn = assetSn;
    }

    public String getAssetTag() {
        return assetTag;
    }

    public void setAssetTag(String assetTag) {
        this.assetTag = assetTag;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean contains(String str) {
        str = str.toLowerCase(Locale.ROOT);
        return (getName().toLowerCase().startsWith(str)||getName().toLowerCase(Locale.ROOT).endsWith(str));
    }
}
