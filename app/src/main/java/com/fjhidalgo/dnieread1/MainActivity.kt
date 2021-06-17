package com.fjhidalgo.dnieread1

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.tsenger.androsmex.data.CANSpecDO
import es.gob.jmulticard.jse.provider.DnieLoadParameter
import es.gob.jmulticard.jse.provider.DnieProvider
import java.io.ByteArrayInputStream
import java.security.KeyStore
import java.security.Security
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private var _myNfcAdapter : NfcAdapter? = null
    private var _can: CANSpecDO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _can = CANSpecDO("124096", "", "")
        enableReaderMode()
    }


    override fun onTagDiscovered(tag: Tag?) {
        try {
            val p = DnieProvider()
            Security.insertProviderAt(p, 1)
            val keyStore = KeyStore.getInstance(DnieProvider.KEYSTORE_PROVIDER_NAME)
            val loadParameter = DnieLoadParameter.getBuilder(_can!!.canNumber, tag).build()
            keyStore.load(loadParameter)
            val data = loadParameter.mrtdCardInfo.dataGroup1
            val date = SimpleDateFormat("yyMMdd").parse(data.dateOfBirth)
            val data2 = loadParameter.mrtdCardInfo.dataGroup2
            // Generamos el Bitmap a partir de los
//            val j2k = J2kStreamDecoder()
//            val bis = ByteArrayInputStream(data2.imageBytes)

            Log.e("Name", data.name)
            Log.e("Surname", data.surname)
            Log.e("Issuer", data.issuer)
            Log.e("DocNumber", data.docNumber)
            Log.e("Birth", data.dateOfBirth)
            Log.e("Expiry", data.dateOfExpiry)
            Log.e("DocType", data.docType)
            Log.e("Nacionalidad", data.nationality)
            Log.e("OptData", data.optData)
            Log.e("Sex", data.sex)
        } catch (e: Exception) {
            Toast.makeText(this, "Error en la lectura del Dnie", Toast.LENGTH_LONG).show()
        }
    }

    private fun enableReaderMode() {
        _myNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val options = Bundle()
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 1000)
        _myNfcAdapter!!.enableReaderMode(
            this,
            this,
            NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                    NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B,
            options
        )
    }
}