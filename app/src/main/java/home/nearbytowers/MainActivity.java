package home.nearbytowers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_ACCESS_COARSE_LOCATION = 123;
    ListView listView;
    Button refreshButton;
    SingleItemAdapter singleItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listitemsview);
        refreshButton = (Button) findViewById(R.id.refresh);

        singleItemAdapter = new SingleItemAdapter(this);
//        final SingleItemAdapter singleItemAdapterFinal = singleItemAdapter;
        listView.setAdapter(singleItemAdapter);
        final Context context = this;
        final Activity activity = this;
        refreshButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                singleItemAdapter.clearItems();
                singleItemAdapter.notifyDataSetChanged();
                try{

                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    MY_ACCESS_COARSE_LOCATION);

                            // MY_ACCESS_COARSE_LOCATION is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                    }
                    else{
                        List<SingleItem> singleItems = populatCellInfo();
                        singleItemAdapter.addAllItem(singleItems);
                    }

                }catch (Throwable throwable){
                    singleItemAdapter.addItem(new SingleItem(throwable.getMessage(),"error","error"));
                }

                singleItemAdapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    List<SingleItem> singleItems = populatCellInfo();
                    singleItemAdapter.addAllItem(singleItems);

                } else {
                    singleItemAdapter.addItem(new SingleItem("no permission", "no permission", "no permission"));
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            default:
                singleItemAdapter.addItem(new SingleItem("no permission", "no permission", "no permission"));
                break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
        singleItemAdapter.notifyDataSetChanged();
    }

    public List<SingleItem> populatCellInfo(){
        List<SingleItem> allItems = new ArrayList<>();
        TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
            List<CellInfo> allCellInfo = telephonyManager.getAllCellInfo();
            if(allCellInfo != null && allCellInfo.size() >0){
                for(CellInfo cellInfo: allCellInfo){
                    SingleItem singleItem = null;
                    if(cellInfo instanceof CellInfoCdma){
                        CellInfoCdma cellInfoCdma = (CellInfoCdma)cellInfo;
                        singleItem = new SingleItem(cellInfoCdma.toString(),"CDMA"
                                ,String.valueOf(cellInfoCdma.getCellIdentity()));
                    }else if(cellInfo instanceof CellInfoGsm){
                        CellInfoGsm cellInfoGsm = (CellInfoGsm)cellInfo;
                        singleItem = new SingleItem(cellInfoGsm.toString(),"GSM"
                                ,String.valueOf(cellInfoGsm.getCellIdentity()));
                    }else if(cellInfo instanceof CellInfoLte){
                        CellInfoLte cellInfoLte = (CellInfoLte)cellInfo;
                        singleItem = new SingleItem(cellInfoLte.toString(),"LTE"
                                ,String.valueOf(cellInfoLte.getCellIdentity()));
                    }else if(cellInfo instanceof CellInfoWcdma){
                        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma)cellInfo;
                        singleItem = new SingleItem(cellInfoWcdma.toString(),"wcdma"
                                ,cellInfoWcdma.getCellIdentity().toString());
                    }else {
                        singleItem = new SingleItem("UNKNOWN","UNKNOWN"
                                ,"UNKNOWN");
                    }
                    allItems.add(singleItem);
                }
            }
            else{
                SingleItem singleItem = new SingleItem("NA","NA"
                        ,"NA");
                allItems.add(singleItem);
            }
        return  allItems;

    }

}

class SingleItem{
    private String name;
    private String type;
    private String cid;

    public SingleItem(String name, String type, String cid) {
        this.name = name;
        this.type = type;
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCid() {
        return cid;
    }
}

class SingleItemAdapter extends BaseAdapter {

    private List<SingleItem> items;
    private Context context;

    public SingleItemAdapter(Context context) {
        this.items = new ArrayList<>();
        this.context = context;
    }
    public void clearItems(){
        items.clear();
    }
    public void addItem(SingleItem item){
        this.items.add(item);
    }

    public void addAllItem(List<SingleItem> singleItems) {
        this.items.addAll(singleItems);
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.single_item, parent, false);

        TextView name = (TextView) row.findViewById(R.id.name);
        TextView type = (TextView) row.findViewById(R.id.type);
        TextView cid = (TextView) row.findViewById(R.id.cid);

        SingleItem singleItem = items.get(position);
        name.setText("Detail: " + singleItem.getName());
        type.setText("Type: " + singleItem.getType());
        cid.setText("Cid: " +   singleItem.getCid());

        return row;
    }
}
