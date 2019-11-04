package fr.schawnndev.qrcodereader;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import fr.schawnndev.qrcodereader.data.model.JsonScan;

public class TicketInfosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_infos);

        JsonScan scan = new JsonScan();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            scan.setFirstName(extras.getString("firstName"));
            scan.setLastName(extras.getString("lastName"));
            scan.setToPay(extras.getDouble("toPay"));
            scan.setHasPaid(extras.getBoolean("hasPaid"));
            scan.setAlreadyScanned(extras.getBoolean("alreadyScanned"));
            scan.setLastScanDate(extras.getString("lastScanDate"));
        } else return;

        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle("Scan: "+scan.getFullName());

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);

        ((TextView) findViewById(R.id.firstName)).setText(scan.getFirstName());
        ((TextView) findViewById(R.id.name)).setText(scan.getLastName());
        ((TextView) findViewById(R.id.toPay)).setText(format.format(scan.getToPay()));

        TextView hasPaid = findViewById(R.id.hasPaid);
        hasPaid.setText(scan.isHasPaid() ? "Oui" : "Non");
        hasPaid.setBackgroundColor(scan.isHasPaid() ? Color.rgb(	119, 221, 119) : Color.rgb(	255, 105, 97));

        TextView alreadyScanned = findViewById(R.id.alreadyScanned);

        if (!scan.isAlreadyScanned())
            alreadyScanned.setVisibility(View.INVISIBLE);
        else if(!scan.getLastScanDate().equals("0"))
            alreadyScanned.setText(String.format("Ce billet a déjà été scanné le %s.", scan.getLastScanDate()));
    }

}
