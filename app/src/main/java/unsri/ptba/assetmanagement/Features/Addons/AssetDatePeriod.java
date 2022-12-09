package unsri.ptba.assetmanagement.Features.Addons;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import unsri.ptba.assetmanagement.Database.DatabaseQueryClass;
import unsri.ptba.assetmanagement.Features.CreateAsset.Asset;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AssetDatePeriod {

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AssetDatePeriod(){

    }

    public List getAssetFromTo(String dateFrom, String dateTo, Context context){

        DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(context);
        List<Asset> assetList = new ArrayList<>();
        assetList.addAll(databaseQueryClass.getAllAsset());
        List<Asset> newList = new ArrayList<>();

        if (dateFrom.isEmpty()){
            dateFrom = "2020-01-01 00:00:00";
        } else {
            dateFrom = dateFrom + " 00:00:00";
        }

        if (dateTo.isEmpty()){
            dateTo = LocalDateTime.now().format(dtf);
        } else {
            dateTo = dateTo + " 23:59:00";
        }

        LocalDateTime date1 = LocalDateTime.parse(dateFrom, dtf);
        LocalDateTime date2 = LocalDateTime.parse(dateTo, dtf);

//        Toast.makeText(context, date2.toString(), Toast.LENGTH_SHORT).show();

        for(Asset e : assetList){
            LocalDateTime eTime = LocalDateTime.parse(e.getCreatedAt(), dtf);
            if(eTime.isAfter(date1) && eTime.isBefore(date2)){
                newList.add(e);
            }
        }
        return newList;
    }

}
