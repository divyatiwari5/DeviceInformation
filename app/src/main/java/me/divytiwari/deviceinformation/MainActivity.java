package me.divytiwari.deviceinformation;


import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    Button btnsubmit;
    String IMEI, IMSI, kernel, country, lang, androidVersion;
    TextView txtinfoview;

    final int REQUEST_PERMISSION_SIGNAL = 1;
    final List<String> pms_arrays = Arrays.asList(Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE);
    final List<String> pms_arrays_expl = Arrays.asList("Internet: For Internet",
            "Write External Storage: Just for Fun");
    List<String> pms_reqd_with_expl = new ArrayList<String>();
    List<String> pms_reqd = new ArrayList<String>();
    String expl = "We need following permissions for this app. Please grant them: ";
    boolean never_show_again = false;

    JSONObject device_info = new JSONObject();
    Locale locale_obj;

    private void askAllPermissions() {
        for (int i = 0; i < pms_arrays.size(); i++) {

            // Check if we already have permission
            if (ContextCompat.checkSelfPermission(MainActivity.this, pms_arrays.get(i)) != PackageManager.PERMISSION_GRANTED) {

                // Check if we have to show an explanation
                if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, pms_arrays.get(i)))
                    pms_reqd.add(pms_arrays.get(i));
                else {
                    // We have to show explanation. Add permission to pms_reqd_with_expl and prepare expl with proper message
                    pms_reqd_with_expl.add(pms_arrays.get(i));
                    expl = expl + '\n' + pms_arrays_expl.get(i);
                }
            }
        }

        // Ask for permission with explanation
        if (pms_reqd_with_expl.size() > 0) {
            final String[] ask_pms = pms_reqd_with_expl.toArray(new String[0]);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Need Permissions");
            builder.setMessage(expl);
            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(MainActivity.this, ask_pms, REQUEST_PERMISSION_SIGNAL);
                }
            });
            builder.setNegativeButton("Close App", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    // TODO Close the app
                }
            });
            builder.show();

        }

        // Ask for permission without explanation (You are asking for first time)
        if (pms_reqd.size() > 0) {
            final String[] ask_pms = pms_reqd.toArray(new String[0]);
            // For any one permission, user has said "Never Ask Again"
            ActivityCompat.requestPermissions(MainActivity.this, ask_pms, REQUEST_PERMISSION_SIGNAL);
        }

    }

    public void askForPermission(final String permission) {
        int pos = pms_arrays.indexOf(permission);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("We need permission other wise app will not run. \n " + pms_arrays_expl.get(pos));
        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, REQUEST_PERMISSION_SIGNAL);
            }
        });
        builder.setNegativeButton("Close App", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                // TODO Close the app
            }
        });

        builder.show();
    }

    public void gotoSetting(final String permission) {
        int pos = pms_arrays.indexOf(permission);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("We need permission other wise app will not run. \n Goto Setting to give permission" + pms_arrays_expl.get(pos));
        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                MainActivity.this.startActivity(intent);

            }
        });
        builder.setNegativeButton("Close App", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                // TODO Close the app
            }
        });

        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout2);
        txtinfoview = (TextView) findViewById(R.id.textSetInformation);

        askAllPermissions();

        locale_obj = getResources().getConfiguration().locale;
        country = locale_obj.getDisplayCountry();
        lang = locale_obj.getDisplayLanguage();
        Toast.makeText(this, "Country: " + country + "Language:" + lang, Toast.LENGTH_SHORT).show();

        btnsubmit = (Button) findViewById(R.id.btnfetch);

        kernel = System.getProperty("os.version");
        Toast.makeText(this, "Kernel version:" + kernel, Toast.LENGTH_SHORT).show();

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        try {
            manager.getMemoryInfo(mi);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

        long availableMem = mi.availMem / 1048576;
        Toast.makeText(this, "Available Memory:" + availableMem + "MB", Toast.LENGTH_SHORT).show();

        String android_id = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);


        try {
            String info = getHardwareInfo().toString();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject info = null;
                try {
                    info = getHardwareInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                txtinfoview.setText(info.toString());

            }
        });

    }

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return formatSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return formatSize(totalBlocks * blockSize);
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    private String getScreenResolution() {
        DisplayMetrics dm = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);

        Point realSize = new Point();
        try {
            Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
        } catch (Exception ex) {

        }

        int mWidthPixels = realSize.x;
        int mHeightPixels = realSize.y;

        return String.valueOf(mHeightPixels) + "x" + String.valueOf(mWidthPixels);
    }

    private String readCPUinfo(int code)
    {
        String filename;
        switch (code) {
            case 1:
                filename = "/proc/cpuinfo";
                break;
            case 2:
                filename = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";
                break;
            case 3:
                filename = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
                break;
            default:
                return null;
        }
        try {
            Scanner scanner = new Scanner(new File(filename));
            while (scanner.hasNextLine()) {
                String[] val = scanner.nextLine().split(": ");
                switch (code){
                    case 1:
                        if (val.length > 1){
                            if (val[0].trim().equals("CPU implementer"))
                                return val[1].trim();
                        }
                        break;
                    case 2:
                        if (val.length == 1)
                            return val[0].trim();
                    case 3:
                        if (val.length == 1)
                            return val[0].trim();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";

    }

    private double getScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);

        Point realSize = new Point();
        try {
            Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
        } catch (Exception ex) {

        }

        int mWidthPixels = realSize.x;
        int mHeightPixels = realSize.y;

        double x = Math.pow(mWidthPixels / dm.xdpi, 2);
        double y = Math.pow(mHeightPixels / dm.ydpi, 2);
        return Math.sqrt(x + y);
    }

    private JSONObject getHardwareInfo() throws JSONException {
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wInfo = wm.getConnectionInfo();


        String networkOperator = tel.getNetworkOperator();
        int mcc = 0, mnc = 0;

        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));
        }

        device_info.put("kernel", kernel);

        JSONObject locale_json = new JSONObject();
        locale_json.put("country", locale_obj.getCountry());
        locale_json.put("language", locale_obj.getLanguage());
        device_info.put("locale", locale_json);
        device_info.put("os_incremental", Build.VERSION.INCREMENTAL);
        device_info.put("cpu_core", Runtime.getRuntime().availableProcessors());
        device_info.put("mnc", mnc);
        device_info.put("sdk", Build.VERSION.SDK_INT);
        device_info.put("os_version", Build.VERSION.RELEASE);
        device_info.put("brand", Build.BRAND);
        device_info.put("product", Build.PRODUCT);
        // TODO Round off
        device_info.put("screen_size", getScreenSize());
        device_info.put("mcc", mcc);
        device_info.put("fingerprint", Build.FINGERPRINT);
        device_info.put("manufacturer", Build.MANUFACTURER);
        device_info.put("local_tz_name", TimeZone.getDefault().getID().toString());
        device_info.put("country", locale_obj.getDisplayCountry());
        device_info.put("carrier", tel.getNetworkOperatorName());
        device_info.put("os_codename", Build.VERSION.CODENAME);
        device_info.put("model", Build.MODEL);
        device_info.put("resolution", getScreenResolution());
        device_info.put("hardware", Build.HARDWARE);
        // TODO use this method for all other display
        device_info.put("dpi", this.getResources().getDisplayMetrics().densityDpi);
        device_info.put("device_type", Build.PRODUCT);
        device_info.put("device", Build.DEVICE);
        device_info.put("serial", Build.SERIAL);
        device_info.put("cpu_arch", readCPUinfo(1));

        JSONObject cpu_freq = new JSONObject();
        cpu_freq.put("min", readCPUinfo(2));
        cpu_freq.put("max", readCPUinfo(3));
        device_info.put("cpu_frequency", cpu_freq);

        JSONObject internal_memory_json = new JSONObject();
        internal_memory_json.put("available", getAvailableInternalMemorySize());
        internal_memory_json.put("shown", getTotalInternalMemorySize());
        device_info.put("internal_memory", internal_memory_json);

        device_info.put("mainboard",  Build.BOARD);

        JSONObject ram_json = new JSONObject();
        ram_json.put("available", "HI");
        ram_json.put("shown", "HI");
        device_info.put("ram", ram_json);

        device_info.put("bluetooth", "Somedata");
        device_info.put("private_ip", "Somedata");
        device_info.put("timezone", "Somedata");
        device_info.put("android_id", "Somedata");
        device_info.put("network", "Somedata");
        device_info.put("display", "Somedata");
        device_info.put("User-Agent", "Somedata");

        JSONObject gpu_json = new JSONObject();
        gpu_json.put("version", "sj");
        gpu_json.put("vendor", "sj");
        gpu_json.put("name", "sj");
        gpu_json.put("opengl_version", "sj");
        device_info.put("gpu", gpu_json);

        device_info.put("mac", wInfo.getMacAddress());
        device_info.put("build", "");
        device_info.put("adid", "");
        device_info.put("imei", "");
        device_info.put("local_tz", TimeZone.getDefault().toString());
        device_info.put("name", "");
        device_info.put("local_timezone", "");
        device_info.put("cpu", "");
        device_info.put("processor", "");

        return device_info;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_SIGNAL) {

            if (grantResults.length > 0){
                // If grantResults has some permissions in it

                for (int i=0; i<grantResults.length; i++) {
                    // For every permission
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        // permissions[i] was Granted
                        Toast.makeText(this, "Permission Granted: " + permissions[i], Toast.LENGTH_SHORT).show();
                    }else{
                        // permissions[i] was Denied (Disable the buttions and show warnings)
                        Toast.makeText(this, "Permission Denied: " + permissions[i], Toast.LENGTH_SHORT).show();
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissions[i])) {
                            askForPermission(permissions[i]);
                        } else {
                            gotoSetting(permissions[i]);
                        }
                    }
                }
            }
        }
    }
}

