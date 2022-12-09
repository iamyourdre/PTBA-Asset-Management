package unsri.ptba.assetmanagement.Features.ShowAssetList;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import unsri.ptba.assetmanagement.Database.DatabaseQueryClass;
import unsri.ptba.assetmanagement.Features.Addons.AssetDatePeriod;
import unsri.ptba.assetmanagement.Features.Addons.AssetSort;
import unsri.ptba.assetmanagement.Features.Addons.DatetimeFormatter;
import unsri.ptba.assetmanagement.Features.CreateAsset.Asset;
import unsri.ptba.assetmanagement.Features.CreateAsset.AssetCreateDialogFragment;
import unsri.ptba.assetmanagement.Features.CreateAsset.AssetCreateListener;
import unsri.ptba.assetmanagement.R;
import unsri.ptba.assetmanagement.Util.Config;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AssetListActivity extends AppCompatActivity implements AssetCreateListener {

    FragmentManager fm = getSupportFragmentManager();

    private DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(this);
    private DatetimeFormatter dtf = new DatetimeFormatter();
    private AssetSort assetSort = new AssetSort();
    private AssetDatePeriod assetDatePeriod = new AssetDatePeriod();

    final Calendar myCalendar= Calendar.getInstance();

    private List<Asset> assetList = new ArrayList<>();

    private TextView assetListEmptyTextView;
    private RecyclerView recyclerView;
    private AssetListRecyclerViewAdapter assetListRecyclerViewAdapter;
    private EditText searchEditText;
    private ImageView clearSearch;
    private EditText datepicker1EditText;
    private EditText datepicker2EditText;
    private ImageView clearDate1Image;
    private ImageView clearDate2Image;
    private ImageView filterImage;
    private Toolbar toolbar3;

    private String searchString;

    private static final int STORAGE_PERMISSION_CODE = 101;
    String storagePermission[];

    public AssetListActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_list);
        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar1);
        Logger.addLogAdapter(new AndroidLogAdapter());

        recyclerView = (RecyclerView) findViewById(R.id.assetRecyclerView);
        assetListEmptyTextView = (TextView) findViewById(R.id.emptyAssetListTextView);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        datepicker1EditText = (EditText) findViewById(R.id.datepicker1EditText);
        datepicker2EditText = (EditText) findViewById(R.id.datepicker2EditText);
        clearDate1Image = (ImageView) findViewById(R.id.clearDate1Image);
        clearDate2Image = (ImageView) findViewById(R.id.clearDate2Image);
        filterImage = (ImageView) findViewById(R.id.filterImage);
        toolbar3 = (Toolbar) findViewById(R.id.toolbar3);
        clearSearch = (ImageView) findViewById(R.id.clearSearch);

        assetList.addAll(databaseQueryClass.getAllAsset());

        assetListRecyclerViewAdapter = new AssetListRecyclerViewAdapter(this, null);
        assetListRecyclerViewAdapter.setAssetList(assetSort.sortByIdDesc(assetList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(assetListRecyclerViewAdapter);

        viewVisibility();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        searchEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                datepicker1EditText.setText("");
                datepicker2EditText.setText("");
                searchString = searchEditText.getText().toString();
                if (!searchString.isEmpty()){
                    toolbar3.setVisibility(View.GONE);
                } else {
                    toolbar3.setVisibility(View.VISIBLE);
                }
                List<Asset> tempSearchResult = new ArrayList<>();
//                for(Asset e : databaseQueryClass.getAllAsset()){
                for(Asset e : assetList){
                    if(e.getName().toLowerCase(Locale.ROOT).contains(searchString.toLowerCase(Locale.ROOT)) || e.getAssetSn().toLowerCase(Locale.ROOT).contains(searchString.toLowerCase(Locale.ROOT)) || e.getAssetTag().toLowerCase(Locale.ROOT).contains(searchString.toLowerCase(Locale.ROOT))){
                        tempSearchResult.add(e);
                    }
                }
                assetListRecyclerViewAdapter.setAssetList(tempSearchResult);
                assetListRecyclerViewAdapter.notifyDataSetChanged();
                viewVisibility();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEditText.setText("");
                toolbar3.setVisibility(View.VISIBLE);
                assetList = new ArrayList<>();
                assetList.addAll(databaseQueryClass.getAllAsset());
                assetListRecyclerViewAdapter.setAssetList(assetSort.sortByIdDesc(assetList));
//                assetListRecyclerViewAdapter.setAssetList(assetList);
                assetListRecyclerViewAdapter.notifyDataSetChanged();
                viewVisibility();
            }
        });

        clearDate1Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datepicker1EditText.setText("");
                assetList = assetDatePeriod.getAssetFromTo(datepicker1EditText.getText().toString(), datepicker2EditText.getText().toString(), getApplicationContext());
                assetListRecyclerViewAdapter.setAssetList(assetList);
                assetListRecyclerViewAdapter.notifyDataSetChanged();
            }
        });

        clearDate2Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datepicker2EditText.setText("");
                assetList = assetDatePeriod.getAssetFromTo(datepicker1EditText.getText().toString(), datepicker2EditText.getText().toString(), getApplicationContext());
                assetListRecyclerViewAdapter.setAssetList(assetList);
                assetListRecyclerViewAdapter.notifyDataSetChanged();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAssetCreateDialog();
            }
        });

        filterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu dropDownMenu = new PopupMenu(v.getContext(), filterImage);
                dropDownMenu.getMenuInflater().inflate(R.menu.dropdown_menu, dropDownMenu.getMenu());
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int sortId = menuItem.getItemId();
                        if (menuItem.getTitle().toString().equalsIgnoreCase("Nama Aset")){
                            assetListRecyclerViewAdapter.setAssetList(assetSort.sortByName(assetList));
                            assetListRecyclerViewAdapter.notifyDataSetChanged();
                            viewVisibility();
                        } else if (menuItem.getTitle().toString().equalsIgnoreCase("Serial Number")){
                            assetListRecyclerViewAdapter.setAssetList(assetSort.sortBySN(assetList));
                            assetListRecyclerViewAdapter.notifyDataSetChanged();
                            viewVisibility();
                        } else if (menuItem.getTitle().toString().equalsIgnoreCase("Asset Tag")){
                            assetListRecyclerViewAdapter.setAssetList(assetSort.sortByTag(assetList));
                            assetListRecyclerViewAdapter.notifyDataSetChanged();
                            viewVisibility();
                        } else if (menuItem.getTitle().toString().equalsIgnoreCase("Terakhir Ditambahkan")){
                            assetListRecyclerViewAdapter.setAssetList(assetSort.sortByCreatedAt(assetList));
                            assetListRecyclerViewAdapter.notifyDataSetChanged();
                            viewVisibility();
                        } else if (menuItem.getTitle().toString().equalsIgnoreCase("Terakhir Diperbarui")){
                            assetListRecyclerViewAdapter.setAssetList(assetSort.sortByUpdatedAt(assetList));
                            assetListRecyclerViewAdapter.notifyDataSetChanged();
                            viewVisibility();
                        }
                        return true;
                    }
                });
                dropDownMenu.show();
            }
        });

        DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel(datepicker1EditText);
                assetList = assetDatePeriod.getAssetFromTo(datepicker1EditText.getText().toString(), datepicker2EditText.getText().toString(), getApplicationContext());
                assetListRecyclerViewAdapter.setAssetList(assetList);
                assetListRecyclerViewAdapter.notifyDataSetChanged();
//                viewVisibility();
            }
        };
        datepicker1EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AssetListActivity.this, date1, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel(datepicker2EditText);
                assetList = assetDatePeriod.getAssetFromTo(datepicker1EditText.getText().toString(), datepicker2EditText.getText().toString(), getApplicationContext());
                assetListRecyclerViewAdapter.setAssetList(assetList);
                assetListRecyclerViewAdapter.notifyDataSetChanged();
            }
        };
        datepicker2EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AssetListActivity.this, date2, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

    }

    private void updateLabel(EditText des){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.getDefault());
        des.setText(dateFormat.format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_export){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AssetListActivity.this);
            alertDialogBuilder.setMessage("Ekspor semua aset ke .CSV ?");
            alertDialogBuilder.setPositiveButton("Ya",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            String filename = "PTBA - "+dtf.dtf2IdDtf(dtf.now());
                            arrayToCSV( false, filename);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AssetListActivity.this);
                            alertDialogBuilder.setMessage(Html.fromHtml("<b>"+filename+".csv"+"</b>"+" berhasil disimpan di folder PTBA."));
                            alertDialogBuilder.setNegativeButton("Oke",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    });
//            alertDialogBuilder.setNegativeButton("Tidak",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            String filename = "PTBA - "+dtf.dtf2IdDtf(dtf.now());
//                            arrayToCSV( false, filename);
//
//                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AssetListActivity.this);
//                            alertDialogBuilder.setMessage(Html.fromHtml("<b>"+filename+".csv"+"</b>"+" berhasil disimpan di folder PTBA."));
//                            alertDialogBuilder.setNegativeButton("Oke",new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//
//                            AlertDialog alertDialog = alertDialogBuilder.create();
//                            alertDialog.show();
//                        }
//                    });

            alertDialogBuilder.setNeutralButton("Batal",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void arrayToCSV(boolean importDeleted, String filename) {

        java.io.File root = android.os.Environment.getExternalStorageDirectory();

        java.io.File dir = new java.io.File(root.getAbsolutePath() + "/PTBA");
        dir.mkdirs();
        java.io.File file = new java.io.File(dir, filename+".csv");

        List<Asset> assetListExport = new ArrayList<>();
        List<Asset> assetDeletedExport = new ArrayList<>();
        assetListExport = assetList;

        if(importDeleted){
            assetDeletedExport.addAll(databaseQueryClass.getAssetDeleted());
            try {
                String createdAt;
                String updatedAt;
                String deletedAt;
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);
                pw.println("ASSET NAME,SERIAL NUMBER,ASSET TAG,CREATED AT,UPDATED AT,DELETED AT");
                for(int i=0;i<assetListExport.size();i++){
                    createdAt = (assetListExport.get(i).getCreatedAt()!=null? dtf.dtf2IdDtf(assetListExport.get(i).getCreatedAt()) :" ");
                    updatedAt = ((assetListExport.get(i).getUpdatedAt().compareTo(assetListExport.get(i).getCreatedAt())>0?dtf.dtf2IdDtf(assetListExport.get(i).getUpdatedAt()):" "));
                    deletedAt = " ";
                    pw.println('"'+assetListExport.get(i).getName()+'"'+","+'"'+assetListExport.get(i).getAssetSn()+'"'+","+'"'+assetListExport.get(i).getAssetTag()+'"'+","+(createdAt)+","+(updatedAt)+","+(deletedAt));

                }
                for(int i=0;i<assetDeletedExport.size();i++){
                    createdAt = (assetDeletedExport.get(i).getCreatedAt()!=null? dtf.dtf2IdDtf(assetDeletedExport.get(i).getCreatedAt()) :" ");
                    updatedAt = ((assetDeletedExport.get(i).getUpdatedAt().compareTo(assetDeletedExport.get(i).getCreatedAt())>0? dtf.dtf2IdDtf(assetDeletedExport.get(i).getUpdatedAt()) :" "));
                    deletedAt = (dtf.dtf2IdDtf(assetDeletedExport.get(i).getDeletedAt()));
                    pw.println(assetDeletedExport.get(i).getName()+","+assetDeletedExport.get(i).getAssetSn()+","+assetDeletedExport.get(i).getAssetTag()+","+(createdAt)+","+(updatedAt)+","+(deletedAt));
                }
                pw.flush();
                pw.close();
                f.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i(TAG, "******* File not found. Did you" +
                        " add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            try {
                String createdAt;
                String updatedAt;
                String deletedAt;
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);
                pw.println("ASSET NAME,SERIAL NUMBER,ASSET TAG,CREATED AT,UPDATED AT");
                for(int i=0;i<assetListExport.size();i++){
                    createdAt = (assetListExport.get(i).getCreatedAt()!=null? dtf.dtf2IdDtf(assetListExport.get(i).getCreatedAt()) :" ");
                    updatedAt = ((assetListExport.get(i).getUpdatedAt().compareTo(assetListExport.get(i).getCreatedAt())>0?dtf.dtf2IdDtf(assetListExport.get(i).getUpdatedAt()):" "));
//                deletedAt = " ";
                    pw.println('"'+assetListExport.get(i).getName()+'"'+","+'"'+assetListExport.get(i).getAssetSn()+'"'+","+'"'+assetListExport.get(i).getAssetTag()+'"'+","+(createdAt)+","+(updatedAt));

                }
//            for(int i=0;i<assetDeletedExport.size();i++){
//                createdAt = (assetDeletedExport.get(i).getCreatedAt()!=null? dtf.dtf2IdDtf(assetDeletedExport.get(i).getCreatedAt()) :" ");
//                updatedAt = ((assetDeletedExport.get(i).getUpdatedAt().compareTo(assetDeletedExport.get(i).getCreatedAt())>0?assetDeletedExport.get(i).getUpdatedAt():" "));
//                deletedAt = (assetDeletedExport.get(i).getDeletedAt()!=null? dtf.dtf2IdDtf(assetDeletedExport.get(i).getDeletedAt()) :" ");
//                pw.println(assetDeletedExport.get(i).getName()+","+assetDeletedExport.get(i).getAssetSn()+","+assetDeletedExport.get(i).getAssetTag()+","+(createdAt)+","+(updatedAt)+","+(deletedAt));
//            }
                pw.flush();
                pw.close();
                f.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i(TAG, "******* File not found. Did you" +
                        " add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void viewVisibility() {
        if(assetList.isEmpty())
            assetListEmptyTextView.setVisibility(View.VISIBLE);
        else
            assetListEmptyTextView.setVisibility(View.GONE);
    }

    private void openAssetCreateDialog() {
        AssetCreateDialogFragment assetCreateDialogFragment = AssetCreateDialogFragment.newInstance("", this);
        assetCreateDialogFragment.show(getSupportFragmentManager(), Config.CREATE_ASSET);
    }

//    private void filterAssetDialog() {
//        AssetFilterDialogFragment assetFilterDialogFragment = AssetFilterDialogFragment.filterInstance("", this);
//        assetFilterDialogFragment.show(getSupportFragmentManager(), Config.FILTER_ASSET);
//    }

    @Override
    public void onAssetCreated(Asset asset) {
        assetList.add(0, asset);
        assetListRecyclerViewAdapter.notifyDataSetChanged();
        viewVisibility();
        Logger.d(asset.getName());
    }


    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
        else {

        }
    }

}
