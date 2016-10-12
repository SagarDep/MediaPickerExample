package smartandrodev.te;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import multiplemediapicker.CallBackMediaPicker;
import multiplemediapicker.MediaPicker;

/**
 * Created by Prashant Maheshwari  on 10/10/2016.
 */
public class MainActivity extends AppCompatActivity {

    MediaPicker instance = MediaPicker.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance.setHeaderBackgroundColor(R.color.colorPrimary);
        // below functions to customize media picker
      /*  instance.setNumberOfColoums(2);

        instance.setHandleColor(R.color.colorPrimary);
        instance.setMaximumSelectionLimit(15);
        instance.setHeaderTitleColor(R.color.white);
        instance.setSingleSelectionOn(true);*/
    }

    public void imagesPick(View v) {

        instance.PickMedia(this, true, new CallBackMediaPicker() {
            @Override
            public void onClick(String[] paths, boolean isImage) {
                for (String path : paths) {
                    System.out.println("image PATH : " + path);
                }
            }
        });
    }

    public void videoPick(View v) {
        instance.PickMedia(this, false, new CallBackMediaPicker() {
            @Override
            public void onClick(String[] paths, boolean isImage) {
                for (String path : paths) {
                    System.out.println("Video PATH : " + path);
                }
            }
        });
    }
}
