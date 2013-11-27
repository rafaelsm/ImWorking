package br.com.rads.imworking;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;

public class WriteTagActivity extends Activity {

    private static final String TAG = "savepoint_tag";

    private Context context;
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private IntentFilter[] tagFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tag);

        this.context = this.getApplicationContext();

        //pega o adapter
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //Nâo deixa rodar novamente a activity
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //filtro para encontrar a tag
        IntentFilter discovery = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter nfcDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

        tagFilters = new IntentFilter[]{discovery, nfcDetected, techDetected};

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {

            //Se nâo esta habiliatado leva a tela de Configurações para habilitar
            if (!nfcAdapter.isEnabled()) {
                new AlertDialog.Builder(this).setPositiveButton(getString(R.string.button_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent settings = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(settings);
                    }
                }).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setTitle(getString(R.string.dialog_title_warning)).setMessage(getString(R.string.dialog_enable_nfc)).show();
            }

            nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, tagFilters, null);

        } else {
            Toast.makeText(this, getString(R.string.toast_champs_need_nfc), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            //Valida se a tag pode ser escrita
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (supportechTechs(detectedTag.getTechList())) {

                if (writeableTag(detectedTag)) {
                    WriteResponse wr = writeTag(getTagAsNdef(), detectedTag);
                    String message = (wr.getStatus() == 1? "Success: " : "Failed: ") + wr.getMessage();
                    Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(context, getString(R.string.tag_not_supported), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean supportechTechs(String[] techList) {

        boolean ultralight = false;
        boolean nfcA = false;
        boolean ndef = false;

        for (String tech : techList) {
            if (tech.equals("android.nfc.tech.MifareUltralight")) {
                ultralight = true;
            } else if (tech.equals("android.nfc.tech.NfcA")) {
                nfcA = true;
            } else if (tech.equals("android.nfc.tech.Ndef")) {
                ndef = true;
            }
        }

        return ultralight && nfcA && ndef;

    }

    private boolean writeableTag(Tag tag) {

        try {

            Ndef ndef = Ndef.get(tag);

            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    Toast.makeText(context, getString(R.string.tag_read_only), Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return false;
                }

                ndef.close();
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, getString(R.string.tag_fail_to_read), Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private NdefMessage getTagAsNdef() {
        String uniqueID = "check.in.check.out";
        byte[] uriField = uniqueID.getBytes(Charset.forName("US-ASCII"));
        byte[] payload = new byte[uriField.length + 1];
        System.arraycopy(uriField, 0, payload, 1, uriField.length);

        NdefRecord rtdUriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);

        return new NdefMessage(new NdefRecord[]{rtdUriRecord, NdefRecord.createApplicationRecord("br.com.rads.save")});
    }

    private WriteResponse writeTag(NdefMessage message, Tag tag) {

        int size = message.toByteArray().length;
        String mess;

        try {

            Ndef ndef = Ndef.get(tag);

            if (ndef != null) {
                ndef.connect();

                //tag read only
                if (!ndef.isWritable()) {
                    return new WriteResponse(0, getString(R.string.tag_read_only));
                }

                //tag nao tem mais espaco
                if (ndef.getMaxSize() < size) {
                    mess = "Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size + " bytes";
                    return new WriteResponse(0, mess);
                }

                ndef.writeNdefMessage(message);

                mess = "Wrote message to pre-formatted tag";
                return new WriteResponse(1, mess);

            } else {

                NdefFormatable format = NdefFormatable.get(tag);

                if (format != null) {
                    format.connect();
                    format.format(message);
                    mess = "Formatted tag and wrote message";
                    return new WriteResponse(1, mess);
                } else {
                    mess = "Tag doesn't support NDEF.";
                    return new WriteResponse(0, mess);
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
            mess = "Failed to format tag.";
            return new WriteResponse(0, mess);
        } catch (FormatException e) {
            e.printStackTrace();
            mess = "Failed to write tag";
            return new WriteResponse(0,mess);
        }

    }

    private class WriteResponse {

        int status;
        String message;

        WriteResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}
