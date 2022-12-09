package unsri.ptba.assetmanagement.Features.Addons;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DatetimeFormatter {

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private DateTimeFormatter idDtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public String now() {
        return dtf.format(LocalDateTime.now());
    }

    public String dtf2IdDtf(String date){
        return idDtf.format(dtf.parse(date));
    }

    public String idDtf2Dtf(String date){
        String newDate = (dtf.format(idDtf.parse(date)));
        return newDate;
    }

    public String dtf2IdDtf(LocalDateTime now) {
        String newDate = (idDtf.format(now));
        return newDate;
    }
}
