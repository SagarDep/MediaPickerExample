package multiplemediapicker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import smartandrodev.mediapicker.R;

/**
 * Created by Prashant Maheshwari  on 10/10/2016.
 */
public class MediaPicker {
    private static MediaPicker _instance;

    private CallBackMediaPicker callBackMediaPicker;
    private int handleColor= R.color.transparent_half;
    private int headerBackGroundColorResource = R.color.black;
    private int headerTextColorResource = R.color.white;
    private int headerBackButtonResource = R.drawable.ic_arrow_back;
    private int selectionImageResource = R.drawable.ic_right_iv;
    private int numberOfColoms = 3;
    private int maxSelectionLimit =0;
    private boolean isSingleSelection = false;

    public static MediaPicker getInstance() {
        if (_instance == null) {
            _instance = new MediaPicker();
        }


        return _instance;
    }

    public void setHeaderBackgroundColor(int headerColorResource_) {
        if (0==headerColorResource_) {
            throw new IllegalArgumentException("Header Color cannot be 0");
        }
        this.headerBackGroundColorResource = headerColorResource_;
    }
    public void setHandleColor(int handleColor_) {
        this.handleColor = handleColor_;
    }



    public void setHeaderTitleColor(int headerTextColorResource_) {
        this.headerTextColorResource = headerTextColorResource_;
    }

    public void setHeaderBackButton(int headerBackButtonResource_) {
        this.headerBackButtonResource = headerBackButtonResource_;
    }

    public void setSelectedImage(int selectionImageResource_) {
        this.selectionImageResource = selectionImageResource_;
    }

    public void setNumberOfColoums( int numberOfColoms_) {

        if (numberOfColoms_ > 4) {

            return;
        }
        this.numberOfColoms = numberOfColoms_;
    }

    public void setMaximumSelectionLimit(int maxSelectionLimit_) {

        this.maxSelectionLimit = maxSelectionLimit_;
    }

    public void setSingleSelectionOn(boolean isSingleSelection_) {
        this.isSingleSelection = isSingleSelection_;
    }

    public void PickMedia(Context ctx, boolean isImageView, CallBackMediaPicker CallBackMediaPicker_) {
        this.callBackMediaPicker = CallBackMediaPicker_;
        Intent intent = new Intent(ctx, PickerActivityMain.class);
        intent.putExtra("isTypeImage", isImageView);
        intent.putExtra("headerColorResource", headerBackGroundColorResource);
        intent.putExtra("headerTextColorResource", headerTextColorResource);
        intent.putExtra("headerBackButtonResource", headerBackButtonResource);
        intent.putExtra("selectionImageResource", selectionImageResource);
        intent.putExtra("numberOfColoms", numberOfColoms);
        intent.putExtra("maxSelectionLimit", maxSelectionLimit);
        intent.putExtra("isSingleSelection", isSingleSelection);
        intent.putExtra("handleColor", handleColor);

        ctx.startActivity(intent);
    }
//
//    public void PickMedia(Context ctx, boolean isImageView, int headerColorResource, CallBackMediaPicker CallBackMediaPicker_) {
//        this.callBackMediaPicker = CallBackMediaPicker_;
//        Intent intent = new Intent(ctx, PickerActivityMain.class);
//        intent.putExtra("isTypeImage", isImageView);
//
//
//        ctx.startActivity(intent);
//    }

    public void PublishValues(boolean isImageView, String[] paths) {
        if (callBackMediaPicker != null) {
            callBackMediaPicker.onClick(paths, isImageView);
        }

    }

    BroadcastReceiver myBr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
}
