package fr.schawnndev.qrcodereader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.vision.barcode.Barcode;
import com.notbytes.barcode_reader.BarcodeReaderFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.schawnndev.qrcodereader.backend.BackendServer;
import fr.schawnndev.qrcodereader.backend.tasks.Singleton;
import fr.schawnndev.qrcodereader.data.model.JsonScan;
import fr.schawnndev.qrcodereader.data.model.JsonStats;

public class MainActivity extends AppCompatActivity implements BarcodeReaderFragment.BarcodeReaderListener {

    private final String[] scanValues = new String[]{"firstName", "lastName", "hasPaid", "alreadyScanned", "toPay"};
    private final String[] statsValues = new String[]{"ticketsCount", "scannedTicketsCount", "payedTicketsCount", "scannedPayedTicketsCount"};
    private BarcodeReaderFragment mBarcodeReaderFragment;
    private boolean isRequesting = false;
    private boolean isRequestingStats = false;
    private List<String> scanned = new ArrayList<>();
    private final Pattern regexBase64 = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBarcodeReaderFragment = attachBarcodeReaderFragment();

        ((Switch)findViewById(R.id.flashActivated)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBarcodeReaderFragment.setUseFlash(isChecked);
            }
        });

        ((Switch)findViewById(R.id.scanActivated)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) mBarcodeReaderFragment.resumeScanning();
                else mBarcodeReaderFragment.pauseScanning();
            }
        });

        startStatsTimer(BackendServer.getLoggedInUser().getApiKey(), BackendServer.getLoggedInUser().getEmail());

    }

    private BarcodeReaderFragment attachBarcodeReaderFragment() {
        final FragmentManager supportFragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        BarcodeReaderFragment fragment = BarcodeReaderFragment.newInstance(true, false);
        fragment.setListener(this);
        fragmentTransaction.replace(R.id.fm_container, fragment);
        fragmentTransaction.commitAllowingStateLoss();
        return fragment;
    }

    @Override
    public void onScanned(Barcode barcode) {
        Log.e("[Test]", "onScanned: " + barcode.displayValue);

        String qrCodeValue = barcode.displayValue;

        Matcher m = regexBase64.matcher(qrCodeValue);
        if (qrCodeValue.isEmpty() || qrCodeValue.length() < 5 || !m.find()) return;

        mBarcodeReaderFragment.playBeep();

        if (BackendServer.getLoggedInUser() == null) {
            Toast.makeText(getApplicationContext(), "No user logged in.", Toast.LENGTH_SHORT).show();

            return;
        }

        if (isRequesting || scanned.contains(qrCodeValue))
            return;

        isRequesting = true;

        final String qrCode = qrCodeValue;
        String url = BackendServer.getScanUrl(BackendServer.getLoggedInUser().getApiKey(), BackendServer.getLoggedInUser().getEmail(), qrCode);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response == null || !response.has("success") || !response.getBoolean("success")) {
                                isRequesting = false;

                                if(response.has("error"))
                                    Toast.makeText(getApplicationContext(), response.getString("error"), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for (String val : scanValues) {
                                if (!response.has(val)) {
                                    isRequesting = false;
                                    return;
                                }
                            }

                            startTimer(qrCode);

                            showScanInfos(new JsonScan(response.getString("firstName"), response.getString("lastName"),
                                    response.getBoolean("hasPaid"), response.getBoolean("alreadyScanned"), response.getDouble("toPay"), response.getString("lastScanDate")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            isRequesting = false;
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        startTimer(qrCode);
                        isRequesting = false;
                    }
                });

        Singleton.getInstance(this).addToRequestQueue(jsonObjectRequest);


    }

    private void startTimer(final String qrCode) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                scanned.remove(qrCode);
            }
        }, 4 * 1000L);
    }

    private void startStatsTimer(final String apiKey, final String email)
    {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                if(isRequestingStats) return;

                String url = BackendServer.getStatsUrl(apiKey, email);

                isRequestingStats = true;

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    if (response == null || !response.has("success") || !response.getBoolean("success")) {
                                        isRequestingStats = false;
                                        return;
                                    }

                                    for (String val : statsValues) {
                                        if (!response.has(val)) {
                                            isRequestingStats = false;
                                            return;
                                        }
                                    }

                                    showStatsInfos(new JsonStats(response.getInt("ticketsCount"), response.getInt("scannedTicketsCount"),
                                            response.getInt("payedTicketsCount"), response.getInt("scannedPayedTicketsCount")));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    isRequestingStats = false;
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                isRequestingStats = false;
                            }
                        });

                Singleton.getInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);

            }
        }, 5 * 1000L);
    }

    private void showScanInfos(JsonScan scan) {
        Intent intent = new Intent(MainActivity.this, TicketInfosActivity.class);
        intent.putExtra("firstName", scan.getFirstName());
        intent.putExtra("lastName", scan.getLastName());
        intent.putExtra("toPay", scan.getToPay());
        intent.putExtra("hasPaid", scan.isHasPaid());
        intent.putExtra("alreadyScanned", scan.isAlreadyScanned());
        intent.putExtra("lastScanDate", scan.getLastScanDate());
        MainActivity.this.startActivity(intent);
    }

    private void showStatsInfos(JsonStats stats)
    {
        ((TextView)findViewById(R.id.ticketsScanned)).setText(stats.getScannedTicketsCount() + "/" + stats.getTicketsCount());
        ((TextView)findViewById(R.id.ticketsPayed)).setText(stats.getPayedTicketsCount() + "/" + stats.getTicketsCount());
        ((TextView)findViewById(R.id.ticketsPayedScanned)).setText(stats.getScannedPayedTicketsCount() + "/" + stats.getScannedTicketsCount());
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {

    }
}
