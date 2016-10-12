package smartandrodev.te;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import multiplemediapicker.CallBackMediaPicker;
import multiplemediapicker.MediaPicker;

public class MainActivity extends AppCompatActivity {

    MediaPicker instance = MediaPicker.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance.PickMedia(this, true, new CallBackMediaPicker() {
            @Override
            public void onClick(String[] paths, boolean isImage) {
                for (String xxx : paths) {
                    System.out.println("PATH" + xxx);
                }
            }
        });
    }
}
