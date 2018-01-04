package me.divytiwari.deviceinformation;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AppList extends AppCompatActivity {
    ArrayList<AppData> appDataArrayList = new ArrayList<AppData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        displayInstalledApps();
    }

    public void displayInstalledApps() {

        List<PackageInfo> pack=getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
        PackageInfo p;

        for (int i1 =0; i1<pack.size(); i1++) {
//            ResolveInfo info = (ResolveInfo);
            p=pack.get(i1);
            Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
            String appTitle = p.applicationInfo.loadLabel(getPackageManager()).toString();
            AppData appData = new AppData(appTitle,icon);
            appDataArrayList.add(appData);
        }



        ListView listView = findViewById(R.id.applist);
        ListAdapter adapter = new ListAdapter(this, R.layout.layout_appinfo, R.id.applist, (ArrayList<AppData>) appDataArrayList);
        listView.setAdapter(adapter);

    }
}
