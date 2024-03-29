package com.app.barcode.scanner.Customer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;
import com.app.barcode.scanner.DataHelper;
import com.app.barcode.scanner.Scan.IntentIntegrator;
import com.app.barcode.scanner.Scan.IntentResult;
import com.app.barcode.scanner.Scan.MainScan;

public class Scanner extends Activity {
    /** Called when the activity is first created. */
    private String upc;
    DataHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //jendela tanpa title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //memulai pemindaian QRCode
        IntentIntegrator.initiateScan(this);
    }
    // cek hasil dari QRCode
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        dbHelper = new DataHelper(this);
        switch(requestCode) {
            case IntentIntegrator.REQUEST_CODE: {
                if (resultCode != RESULT_CANCELED) {
                    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    // apabila ada hasil dari pemindaian
                    if (scanResult != null) {
                        // ambil isi dari QRCode
                        upc = scanResult.getContents();
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        Cursor a = db.rawQuery("SELECT * FROM kendaraan WHERE barcode='" + upc + "'", null);
                        a.moveToFirst();
                        if (a.getCount() > 0) {
                            Toast.makeText(getApplicationContext(), "Barcode sedang digunakan!", Toast.LENGTH_LONG).show();
                            MainScan.disini.RefreshList();
                            finish();
                        } else {
                            kirimkan();
                        }
                    }
                    break;
                }
            }
        }
    }

    private void kirimkan(){
        Intent in = new Intent(Scanner.this, TambahCustomer.class);
        in.putExtra("barcode",upc);
        startActivity(in);
    }
}
