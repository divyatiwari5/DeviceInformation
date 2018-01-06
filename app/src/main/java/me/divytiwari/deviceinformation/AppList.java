package me.divytiwari.deviceinformation;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AppList extends AppCompatActivity {
    ArrayList<AppData> appDataArrayList = new ArrayList<AppData>();
    String packageName, appTitle, version, app;
    int versionCode;
    int sdkversion;
    long updateTime, installTime;
    List<PackageInfo> pack;

    JSONObject appinfo = new JSONObject();
    JSONArray jsonArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        try {
            AsyncTask asyncTask = (AsyncTask) new AsyncTask();
            asyncTask.execute();
        } catch (Exception ex) {
            Toast.makeText(this, "Error: " + ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Error:", ex.getLocalizedMessage());
        }

    }


    private class AsyncTask extends android.os.AsyncTask<Void, Void, Void> {

        ProgressDialog pd = new ProgressDialog(AppList.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Loading Apps Data");
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
               // Thread.sleep(7000);
                displayInstalledApps();

            } catch (Exception ex) {
                Toast.makeText(AppList.this, "Error:" + ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Error:", ex.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pd.dismiss();

            ListView listView = findViewById(R.id.applist);
            final ListAdapter adapter = new ListAdapter(AppList.this, R.layout.layout_appinfo, R.id.applist, (ArrayList<AppData>) appDataArrayList);
            listView.setAdapter(adapter);

            Toast.makeText(AppList.this, "yayy", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayInstalledApps() {
         pack=getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
        PackageInfo p;

        for (int i1 =0; i1<pack.size(); i1++) {
            p=pack.get(i1);
            Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
            appTitle = p.applicationInfo.loadLabel(getPackageManager()).toString();
            version = p.versionName;
            versionCode = p.versionCode;
            updateTime = p.lastUpdateTime;
            installTime = p.firstInstallTime;
            packageName = p.packageName;
            sdkversion = p.applicationInfo.targetSdkVersion;
            AppData appData = new AppData(appTitle,icon);
            appDataArrayList.add(appData);

            try{

                JSONObject info = new JSONObject();
                info.put("App Title:", appTitle);
                info.put("Version:", version);
                info.put("Version Code:", versionCode);
                info.put("Install Time:", installTime);
                info.put("Update Time:", updateTime);
                info.put("Package Name:", packageName);
                info.put("SDK Version:", sdkversion);
                appinfo.put("Info:", info);
              //   app = appinfo.toString();
            }catch (JSONException e){
                e.printStackTrace();
            }

        //    Log.e("info", app.toString());

        }
    }


        private JSONObject getData() throws JSONException {
            for (int i1 = 0; i1 < pack.size(); i1++) {
                appinfo.put("App Title:", appTitle);
                appinfo.put("Version:", version);
                appinfo.put("Version Code:", versionCode);
                appinfo.put("Install Time:", installTime);
                appinfo.put("Update Time:", updateTime);
                appinfo.put("Package Name:", packageName);
                appinfo.put("SDK Version:", sdkversion);
            }
            return appinfo;
        }

    }
