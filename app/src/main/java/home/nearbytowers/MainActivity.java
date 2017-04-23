package home.nearbytowers;

import android.content.Context;
import android.os.Build;
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

    ListView listView;
    Button refreshButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listitemsview);
        refreshButton = (Button) findViewById(R.id.refresh);

        final SingleItemAdapter singleItemAdapter = new SingleItemAdapter(this);
        listView.setAdapter(singleItemAdapter);

        refreshButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                singleItemAdapter.clearItems();
                singleItemAdapter.notifyDataSetChanged();
                try{
                    List<SingleItem> singleItems = populatCellInfo();
                    singleItemAdapter.addAllItem(singleItems);
                }catch (Throwable throwable){
                    singleItemAdapter.addItem(new SingleItem(throwable.getMessage(),"error","error"));
                }

                singleItemAdapter.notifyDataSetChanged();

            }
        });
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
                        singleItem = new SingleItem(cellInfoCdma.getCellIdentity().toString(),"CDMA"
                                ,String.valueOf(cellInfoCdma.getCellSignalStrength().getLevel()));
                    }else if(cellInfo instanceof CellInfoGsm){
                        CellInfoGsm cellInfoGsm = (CellInfoGsm)cellInfo;
                        singleItem = new SingleItem(cellInfoGsm.getCellIdentity().toString(),"GSM"
                                ,String.valueOf(cellInfoGsm.getCellSignalStrength().getLevel()));
                    }else if(cellInfo instanceof CellInfoLte){
                        CellInfoLte cellInfoLte = (CellInfoLte)cellInfo;
                        singleItem = new SingleItem(cellInfoLte.getCellIdentity().toString(),"LTE"
                                ,String.valueOf(cellInfoLte.getCellSignalStrength().getLevel()));
                    }else if(cellInfo instanceof CellInfoWcdma){
                        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma)cellInfo;
                        singleItem = new SingleItem(cellInfoWcdma.toString(),"wcdma"
                                ,cellInfoWcdma.getCellSignalStrength().toString());
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
        name.setText(singleItem.getName());
        type.setText(singleItem.getType());
        cid.setText(singleItem.getCid());

        return row;
    }
}
