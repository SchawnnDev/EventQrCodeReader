package fr.schawnndev.qrcodereader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import fr.schawnndev.qrcodereader.backend.BackendServer;
import fr.schawnndev.qrcodereader.backend.tasks.Singleton;
import fr.schawnndev.qrcodereader.data.model.JsonScan;

public class MainActivity extends AppCompatActivity implements BarcodeReaderFragment.BarcodeReaderListener {

    private final String[] scanValues = new String[]{"firstName", "lastName", "hasPaid", "alreadyScanned", "toPay"};
    private BarcodeReaderFragment mBarcodeReaderFragment;
    private boolean isRequesting = false;
    private boolean flashActivated = false;
    private List<String> scanned = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBarcodeReaderFragment = attachBarcodeReaderFragment();

        findViewById(R.id.activeFlash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean toSet = !flashActivated;
                mBarcodeReaderFragment.setUseFlash(toSet);
                ((Button)v).setText(toSet ? "Désactiver le flash" : "Activer le flash");
                flashActivated = toSet;
            }
        });

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

        if (barcode.displayValue.isEmpty() || barcode.displayValue.length() < 5) return;

        mBarcodeReaderFragment.playBeep();

        if (BackendServer.getLoggedInUser() == null) {
            Toast.makeText(getApplicationContext(), "No user logged in.", Toast.LENGTH_SHORT).show();

            return;
        }

        Toast.makeText(getApplicationContext(), barcode.displayValue, Toast.LENGTH_SHORT).show();

        if (isRequesting || scanned.contains(barcode.displayValue))
            return;

        isRequesting = true;

        final String qrCode = barcode.displayValue;
        String url = BackendServer.getScanUrl(BackendServer.getLoggedInUser().getApiKey(), BackendServer.getLoggedInUser().getEmail(), qrCode);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response == null || !response.has("success") || !response.getBoolean("success")) {
                                isRequesting = false;
                                return;
                            }

                            for (String val : scanValues) {
                                if (!response.has(val)) {
                                    isRequesting = false;
                                    return;
                                }
                            }

                            startTimer(qrCode);

                            showDialog(new JsonScan(response.getString("firstName"), response.getString("lastName"),
                                    response.getBoolean("hasPaid"), response.getBoolean("alreadyScanned"), response.getDouble("toPay")));

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

    private void showDialog(JsonScan scan) {
        mBarcodeReaderFragment.pauseScanning();
        final Dialog dialog = new Dialog(MainActivity.this);
        final NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_design);

        ((TextView) dialog.findViewById(R.id.firstName)).setText(scan.getFirstName());
        ((TextView) dialog.findViewById(R.id.name)).setText(scan.getLastName());
        ((TextView) dialog.findViewById(R.id.toPay)).setText(format.format(scan.getToPay()));

        TextView hasPaid = dialog.findViewById(R.id.hasPaid);
        hasPaid.setText(scan.isHasPaid() ? "Oui" : "Non");
        hasPaid.setBackgroundColor(scan.isHasPaid() ? Color.GREEN : Color.RED);

        TextView alreadyScanned = dialog.findViewById(R.id.alreadyScanned);
        alreadyScanned.setText(scan.isAlreadyScanned() ? "Oui" : "Non");
        alreadyScanned.setBackgroundColor(scan.isAlreadyScanned() ? Color.GREEN : Color.RED);

        Button closeBtn = dialog.findViewById(R.id.close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                mBarcodeReaderFragment.resumeScanning();
            }
        });

        dialog.show();
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
