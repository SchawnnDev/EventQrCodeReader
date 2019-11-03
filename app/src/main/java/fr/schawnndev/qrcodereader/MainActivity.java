package fr.schawnndev.qrcodereader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.vision.barcode.Barcode;
import com.notbytes.barcode_reader.BarcodeReaderFragment;

import org.json.JSONObject;

import java.util.List;

import fr.schawnndev.qrcodereader.backend.BackendServer;
import fr.schawnndev.qrcodereader.backend.tasks.Singleton;

public class MainActivity extends AppCompatActivity implements BarcodeReaderFragment.BarcodeReaderListener {

    private BarcodeReaderFragment mBarcodeReaderFragment;
    private boolean isRequesting = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBarcodeReaderFragment = attachBarcodeReaderFragment();
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
        mBarcodeReaderFragment.playBeep();

        if(BackendServer.getLoggedInUser() == null)
        {
            Toast.makeText(getApplicationContext(), "No user logged in.", Toast.LENGTH_SHORT).show();

            return;
        }

        Toast.makeText(getApplicationContext(), barcode.displayValue, Toast.LENGTH_SHORT).show();

        if(isRequesting)
            return;

        isRequesting = true;

        String qrCode = barcode.displayValue;
        String url = BackendServer.getScanUrl(BackendServer.getLoggedInUser().getApiKey(), BackendServer.getLoggedInUser().getEmail(), qrCode);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null)
                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                        isRequesting = false;
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        isRequesting = false;
                    }
                });

        Singleton.getInstance(this).addToRequestQueue(jsonObjectRequest);


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
