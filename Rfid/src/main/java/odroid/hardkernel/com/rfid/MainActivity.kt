package odroid.hardkernel.com.rfid

import android.hardkernel.com.rfid.R
import android.hardkernel.com.rfid.databinding.ActivityMainBinding
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    private inner class SeekBarChangeListener(view: TextView, format: String) :
        OnSeekBarChangeListener {
        private val targetView: TextView
        private val textFormat: String

        init {
            targetView = view
            textFormat = format
        }

        private fun changeCurrent(progress: Int) {
            targetView.text = String.format(textFormat, progress)
        }

        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            changeCurrent(i)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {
            changeCurrent(seekBar.progress)
        }
    }

    private fun fixAddressUI(on: Boolean) {
        binding.sector.isEnabled = !on
        binding.block.isEnabled = !on
    }

    private fun readyToRFID(on: Boolean) {
        fixAddressUI(on)
        binding.readButton.isEnabled = !on
        binding.writeButton.isEnabled = !on
        if (on) {
            binding.tagUid.visibility = View.GONE
            binding.tagResults.visibility = View.GONE
        } else {
            binding.tagUid.visibility = View.VISIBLE
            binding.tagResults.visibility = View.VISIBLE
        }
    }

    private lateinit var  rfid: RFID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rfid = RFID()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.readButton.setOnClickListener {
            binding.readButton.text = getString(R.string.reading)
            readyToRFID(true)
            CoroutineScope(Dispatchers.IO).launch {
                var result:String? = null
                try {
                    result = rfid.read(binding.sector.progress, binding.block.progress)
                } catch (e: Exception) {
                    result = e.message
                    e.printStackTrace()
                } finally {
                    runOnUiThread {
                        binding.tagResults.text = result
                        binding.readButton.setText(R.string.read)
                        binding.tagUid.text = getString(R.string.tag_uid, rfid.uid())
                        readyToRFID(false)
                    }
                }
            }
        }
        binding.writeButton.setOnClickListener {
            binding.writeButton.text = getString(R.string.writing)
            readyToRFID(true)
            binding.writeValue.isEnabled = false
            CoroutineScope(Dispatchers.IO).launch {
                var result:String? = null
                try {
                    result = rfid.write(
                        binding.sector.progress,
                        binding.block.progress,
                        binding.writeValue.text.toString()
                    )
                } catch (e: Exception) {
                    result = e.message
                    e.printStackTrace()
                } finally {
                    runOnUiThread {
                        binding.tagResults.text = result
                        binding.writeButton.setText(R.string.write)
                        binding.tagUid.text = getString(R.string.tag_uid, rfid.uid())
                        readyToRFID(false)
                        binding.writeValue.isEnabled = true
                    }
                }
            }
        }
        binding.sector.setOnSeekBarChangeListener(
            SeekBarChangeListener(
                binding.sectorCurrent,
                "current Sector: %d"
            )
        )
        binding.block.setOnSeekBarChangeListener(
            SeekBarChangeListener(
                binding.blockCurrent,
                "Current Block: %d"
            )
        )
        getString(R.string.unknown_error)
    }
}