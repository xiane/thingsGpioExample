package odroid.hardkernel.com.Mcp2515;

import androidx.appcompat.app.AppCompatActivity;

import com.hardkernel.odroid.things.contrib.Mcp2515.Mcp2515;

import android.annotation.SuppressLint;
import android.hardkernel.com.Mcp2515.R;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    private Mcp2515 mcp2515;
    private TextView can_msg_txt;
    @SuppressLint("DefaultLocale")
    private final Mcp2515.MessageReceivedListener canMsgListener = message -> {
        StringBuilder canMsg = new StringBuilder();
        canMsg.append(String.format("id - 0x%02x\n", message.getId()));
        ByteBuffer buffer = message.getData();
        for (int i = 0; i < message.getDataLength(); i++)
            canMsg.append(String.format("data[%d] - 0x%02x\n", i, buffer.get(i)));

        can_msg_txt.setText(canMsg.toString());
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        can_msg_txt = findViewById(R.id.can_msg_txt);

        try {
            mcp2515 = new Mcp2515(BoardConfig.MCP2515_SPI, BoardConfig.MCP2515_INT);

            mcp2515.setListener(canMsgListener);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button testSendBtn = findViewById(R.id.test_send);
        testSendBtn.setOnClickListener(v -> mcp2515.sendTest());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mcp2515.close();
    }
}