package unsri.ptba.assetmanagement.Features.Addons;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import unsri.ptba.assetmanagement.Features.CreateAsset.Asset;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AssetSort {

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AssetSort(){

    }

    public List sortByIdDesc(List objList){
        Collections.sort(objList, new Comparator<Asset>(){
            @Override
            public int compare(Asset o1, Asset o2){
                return o1.getId() < o2.getId() ? 1 : -1;
            }
        });
        return objList;
    }

    public List sortByName(List objList){
        Collections.sort(objList, new Comparator<Asset>(){
            @Override
            public int compare(Asset o1, Asset o2){
                if(o1.getName() == o2.getName())
                    return 0;
                return o1.getName().compareTo(o2.getName());
            }
        });

        return objList;
    }

    public List sortBySN(List objList){
        Collections.sort(objList, new Comparator<Asset>(){
            @Override
            public int compare(Asset o1, Asset o2){
                if(o1.getAssetSn() == o2.getAssetSn())
                    return 0;
                return o1.getAssetSn().compareTo(o2.getAssetSn());
            }
        });

        return objList;
    }

    public List sortByTag(List objList){
        Collections.sort(objList, new Comparator<Asset>(){
            @Override
            public int compare(Asset o1, Asset o2){
                if(o1.getAssetTag() == o2.getAssetTag())
                    return 0;
                return o1.getAssetTag().compareTo(o2.getAssetTag());
            }
        });

        return objList;
    }

    public List sortByCreatedAt(List objList){
        Collections.sort(objList, new Comparator<Asset>(){
            @Override
            public int compare(Asset o1, Asset o2){
                LocalDateTime date1 = LocalDateTime.parse(o1.getCreatedAt(), dtf);
                LocalDateTime date2 = LocalDateTime.parse(o2.getCreatedAt(), dtf);
                return date1.isAfter(date2)==true?-1:1;
            }
        });
        return objList;
    }

    public List sortByUpdatedAt(List objList){
        Collections.sort(objList, new Comparator<Asset>(){
            @Override
            public int compare(Asset o1, Asset o2){
                LocalDateTime date1 = LocalDateTime.parse(o1.getUpdatedAt(), dtf);
                LocalDateTime date2 = LocalDateTime.parse(o2.getUpdatedAt(), dtf);
                return date1.isAfter(date2)==true?-1:1;
            }
        });
        return objList;
    }
}
