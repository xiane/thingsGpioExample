package odroid.hardkernel.com.rfid

import com.galarzaa.androidthings.Rc522
import com.google.android.things.pio.PeripheralManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException

class RFID @Throws(IOException::class) constructor() {
    private var rc522: Rc522

    init {
        val manager = PeripheralManager.getInstance()
        val spiDevice = manager.openSpiDevice(BoardDefaults.spiPort)
        val reset = manager.openGpio(BoardDefaults.resetPin)
        rc522 = Rc522(spiDevice, reset)
        rc522.setDebugging(true)
    }

    private suspend fun requestCard(): Boolean {
        return withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            rc522.stopCrypto()
            while (true) {
                delay(50)

                if (!rc522.request()) continue
                if (!rc522.antiCollisionDetect()) continue
                break
            }
            rc522.selectTag(rc522.uid)
        }
    }

    @Throws(SecurityException::class)
    private suspend fun authenticateCard(sector: Int, block: Int): Byte {
        return withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            val address = Rc522.getBlockAddress(sector, block)
            val key = byteArrayOf(
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte()
            )
            val result = rc522.authenticateCard(Rc522.AUTH_A, address, key)
            if (!result) {
                throw SecurityException("Authentication failed")
            }
            address
        }
    }

    fun uid():String {
        return rc522.uidString
    }

    @Throws(IOException::class, SecurityException::class)
    suspend fun read(sector: Int, block: Int): String {
        if (!requestCard()) {
            throw IOException("Unknown error")
        }

        val address = authenticateCard(sector, block)
        val buffer = ByteArray(16)
        val result = rc522.readBlock(address, buffer)
        if (!result) {
            throw IOException("Could not read block.")
        }
        val resultsText = "Sector read successfully: " + Rc522.dataToHexString(buffer)
        rc522.stopCrypto()
        return resultsText
    }

    @Throws(IOException::class, SecurityException::class)
    suspend fun write(sector: Int, block: Int, input: String): String {
        if (!requestCard()) {
            throw IOException("Unknown error")
        }

        val address = authenticateCard(sector, block)
        val buffer = ByteArray(16)
        var rawInput = input
        if (rawInput.length < 32) {
            rawInput = "0".repeat(32 - rawInput.length) + rawInput
        }
        for (i in 0..15) {
            val subString = rawInput.substring(i * 2, i * 2 + 2)
            buffer[i] = str2Hex(subString)
        }
        val result = rc522.writeBlock(address, buffer)
        if (!result) {
            throw IOException("Could not write to sector.")
        }
        rc522.stopCrypto()

        return "Sector written successfully"
    }

    private fun str2Hex(word: String): Byte {
        return word.toInt(16).toByte()
    }
}