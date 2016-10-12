package multiplemediapicker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.futuremind.recyclerviewfastscroll.FastScroller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import smartandrodev.mediapicker.R;
/**
 * Created by Prashant Maheshwari  on 10/10/2016.
 */
public class PickerActivityMain extends AppCompatActivity {

    private MediaAdapter mediaAdapter;

    private int headerBackgroundColor = R.color.black;
    private int headerTextColor = R.color.white;
    private int headerBackButtonResource = R.drawable.ic_arrow_back;
    private int selectionImageResource = R.drawable.ic_right_iv;
    private int handleColor = R.color.transparent_half;
    private int numberOfColoms = 3;
    private int maxSelectionLimit = 0;
    private boolean isSingleSelection = false;
    private boolean isImage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker_activity_main);
        getIntentData();
        initViews();
        LoadMedia();
      //  selectionBackColor();
        statusBar();
    }

    public void statusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this,headerBackgroundColor));
        }
    }

   /* public void selectionBackColor() {


        String colorHex = "#" + Integer.toHexString(ContextCompat.getColor(this,  R.color.colorAccent) & 0x00ffffff);
    }*/

    public void getIntentData() {

        isImage = getIntent().getBooleanExtra("isTypeImage", true);
        headerBackgroundColor = getIntent().getIntExtra("headerColorResource", R.color.black);
        headerTextColor = getIntent().getIntExtra("headerTextColorResource", R.color.white);
        headerBackButtonResource = getIntent().getIntExtra("headerBackButtonResource", R.drawable.ic_arrow_back);
        selectionImageResource = getIntent().getIntExtra("selectionImageResource", R.drawable.ic_arrow_back);
        numberOfColoms = getIntent().getIntExtra("numberOfColoms", 3);
        maxSelectionLimit = getIntent().getIntExtra("maxSelectionLimit", 3);
        isSingleSelection = getIntent().getBooleanExtra("isSingleSelection", false);
        handleColor = getIntent().getIntExtra("handleColor", R.color.transparent_half);
    }

    RelativeLayout top_rl;
    ImageButton header_left_ib;
    TextView header_title_tv, header_right_tv;
    RecyclerView genric_rv;
    TextView no_media_tv;
    FastScroller fastScroller;

    public void initViews() {
        if (null != getSupportActionBar()) {
            getSupportActionBar().hide();
        }

        mHandler = new Handler();

        mediaAdapter = new MediaAdapter(this, R.drawable.ic_camera_big, selectionImageResource, numberOfColoms, true, maxSelectionLimit, isSingleSelection, isImage);
        mediaAdapter.add(new BinPath(""));
        fastScroller = (FastScroller) findViewById(R.id.fastscroll);
        fastScroller.setHandleColor(getResources().getColor(handleColor));

        top_rl = (RelativeLayout) findViewById(R.id.top_rl);
        top_rl.setBackgroundColor(getResources().getColor(headerBackgroundColor));
        header_title_tv = (TextView) findViewById(R.id.header_title_tv);
        header_title_tv.setTextColor(getResources().getColor(headerTextColor));

        header_right_tv = (TextView) findViewById(R.id.header_right_tv);
        header_right_tv.setTextColor(getResources().getColor(headerTextColor));

        no_media_tv = (TextView) findViewById(R.id.no_media_tv);
        header_left_ib = (ImageButton) findViewById(R.id.header_left_ib);
        header_left_ib.setImageResource(headerBackButtonResource);

        genric_rv = (RecyclerView) findViewById(R.id.genric_rv);

        GridLayoutManager manager = new GridLayoutManager(this, numberOfColoms);

        genric_rv.setLayoutManager(manager);
        genric_rv.setAdapter(mediaAdapter);
        if (isImage) {
            header_title_tv.setText("0 " + getResources().getString(R.string.photos_selected));
        } else {
            header_title_tv.setText("0 " + getResources().getString(R.string.videos_selected));
        }

        mediaAdapter.setOnItemClickAdapter(new OnItemClickAdapter() {
            @Override
            public void onClick(int idOnclick, int position, Object obj) {

                if (position != 0 && null != obj) {
                    mediaAdapter.changeSelection(obj, position);
                } else if (position == 0) {

                    CaptureRecord();
                }

                int count = mediaAdapter.getSelected().size();
                if (isImage) {

                    if (count > 0) {
                        header_title_tv.setText(count + " " + (count == 1 ? getResources().getString(R.string
                                .photo_selected) : getResources().getString(R.string.photos_selected)));

                        header_right_tv.setEnabled(true);

                    } else {

                        header_right_tv.setEnabled(false);
                        header_title_tv.setText("0 " + getResources().getString(R.string.photos_selected));
                    }
                } else {
                    if (count > 0) {
                        header_title_tv.setText(count + " " + (count == 1 ? getResources().getString(R.string
                                .video_selected) : getResources().getString(R.string.videos_selected)));
                        header_right_tv.setEnabled(true);

                    } else {

                        header_right_tv.setEnabled(false);
                        header_title_tv.setText("0 " + getResources().getString(R.string.videos_selected));
                    }

                }
            }
        });

        header_right_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaAdapter.getSelected().size() > 0) {
                    String[] allPath = new String[mediaAdapter.getSelected().size()];
                    for (int i = 0; i < allPath.length; i++) {
                        allPath[i] = mediaAdapter.getSelected().get(i).sdcardPath;
                    }
                    MediaPicker.getInstance().PublishValues(true, allPath);
                    finish();
                }

            }

        });

        header_left_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickerActivityMain.this.finish();
            }
        });
        fastScroller.setRecyclerView(genric_rv);
    }

    private Handler mHandler;

    public void LoadMedia() {

        new Thread() {

            @Override
            public void run() {
                Looper.prepare();
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        int permissionCheck1 = ContextCompat.checkSelfPermission(PickerActivityMain.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (permissionCheck1 == 0) {
                            startMediaListing();

                        } else {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                                ActivityCompat.requestPermissions(PickerActivityMain.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest
                                                .permission.WRITE_EXTERNAL_STORAGE},
                                        REQ_CODE_READ_WRITE_PER);

                            }

                        }

                    }
                });
                Looper.loop();
            }

            ;

        }.start();

    }

    final int REQ_CODE_READ_WRITE_PER = 101;
    final int REQ_CODE_CAMERA_OPEN = 102;

    private final int REQUEST_IMAGE_CAPTURE = 103;
    private final int REQUEST_VIDEO_CAPTURE = 104;

    private void startMediaListing() {

        new Thread(new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {

                Cursor cursor = null;
                if (isImage) {
                    String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
                    String orderBy = MediaStore.Images.Media._ID + " desc";

                    cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            columns, null, null, orderBy);
                } else {
                    String[] columns = {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID};
                    String orderBy = MediaStore.Video.Media._ID + " desc";

                    cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            columns, null, null, orderBy);
                }

                final boolean mediaAvailable = null != cursor && cursor.getCount() > 0;
                runOnUiThread(new Runnable() {
                    public void run() {
                        no_media_tv.setVisibility(mediaAvailable ? View.GONE : View.VISIBLE);
                    }
                });
                if (mediaAvailable) {
                    while (cursor.moveToNext()) {
                        final BinPath binPath;
                        if (isImage) {
                            final String sdcardPath = cursor.getString(cursor.getColumnIndex(MediaStore
                                    .Images.Media.DATA));
                            System.out.println("PATH IMAGE: " + sdcardPath);
                            File file = new File(sdcardPath);
                            if (!file.exists() || file.length() <= 0 || !isValidIMAGE(sdcardPath)) {
                                continue;
                            }
                            binPath = new BinPath(sdcardPath);
                        } else {
                            final String sdcardPath = cursor.getString(cursor.getColumnIndex
                                    (MediaStore.Video.Media.DATA));

                            System.out.println("PATH VIDEO: " + sdcardPath);
                            File file = new File(sdcardPath);
                            if (!file.exists() || file.length() <= 0 || !isValidVIDEO(sdcardPath)) {
                                continue;
                            }
                            binPath = new BinPath(sdcardPath);
                        }
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                mediaAdapter.add(binPath);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    String supportedImage[] = new String[]{".png", ".PNG", ".jpg", ".JPG", ".gif", ".GIF", ".bmp", ".BMP"};

    public boolean isValidIMAGE(String path) {
        boolean isSuppoted = false;
        for (String formats : supportedImage) {
            if (path.endsWith(formats)) {
                isSuppoted = true;
                break;
            }
        }
        return isSuppoted;
    }

    String supportedVideo[] = new String[]{".3gp", ".3GP", ".mp4", ".MP4"};

    public boolean isValidVIDEO(String path) {
        boolean isSuppoted = false;
        for (String formats : supportedVideo) {
            if (path.endsWith(formats)) {
                isSuppoted = true;
                break;
            }
        }
        return isSuppoted;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (RESULT_OK == resultCode && null != mCaptureFile) {
                    String[] allPath = new String[1];
                    allPath[0] = mCaptureFile.getPath();
                    MediaPicker.getInstance().PublishValues(isImage, allPath);
                    finish();
                }
                break;

            case REQUEST_VIDEO_CAPTURE:
                if (RESULT_OK == resultCode) {
                    Intent intent = new Intent();
                    String[] allPath = new String[1];
                    allPath[0] = getPath(data.getData());
                    MediaPicker.getInstance().PublishValues(isImage, allPath);
                    finish();

                }

                break;

            default:
                break;
        }

        super.

                onActivityResult(requestCode, resultCode, data);

    }

    private String getPath(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        return filePath;
    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_READ_WRITE_PER: {

                boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                if (!showRationale && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    return;
                }

                if (!showRationale && permissions.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LoadMedia();

                } else {
                    showDefaultTwoButon(this, "Please give requiered permissions", 0, REQ_CODE_READ_WRITE_PER);

                }
            }
            break;
            case REQ_CODE_CAMERA_OPEN: {

                boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                if (!showRationale && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    return;
                }

                if (!showRationale && permissions.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CaptureRecord();
                } else {
                    showDefaultTwoButon(this, "Please give requiered permissions", 0, REQ_CODE_CAMERA_OPEN);

                }
            }

            break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void showDefaultTwoButon(Context mContext, String message, int title, final int REQ_CODE) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        if (0 != title) {
            dialogBuilder.setTitle(title);
        }
        if (!message.equals("")) {
            dialogBuilder.setMessage(message);
        } else {
            dialogBuilder.setMessage("Unknown Error");
        }
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (REQ_CODE) {
                            case REQ_CODE_READ_WRITE_PER: {
                                LoadMedia();

                            }
                            break;
                            case REQ_CODE_CAMERA_OPEN: {
                                CaptureRecord();
                            }
                            break;
                        }
                    }
                });
        dialogBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

    private File mCaptureFile = null;

    public void CaptureRecord() {

        int permissionCheck1 = ContextCompat.checkSelfPermission(PickerActivityMain.this,
                Manifest.permission.CAMERA);
        if (permissionCheck1 == 0) {

            if (isImage) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        mCaptureFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (null == mCaptureFile) {
                        // Toast.makeText(v.getContext(), "Unable to launch camera!", Toast.LENGTH_SHORT)
                        // .show();
                        return;
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCaptureFile));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Toast.makeText(this, "Unable to launch camera!", Toast.LENGTH_SHORT).show();

                }
            } else {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                } else {

                    Toast.makeText(this, "POST", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                ActivityCompat.requestPermissions(PickerActivityMain.this,
                        new String[]{Manifest.permission.CAMERA},
                        REQ_CODE_CAMERA_OPEN);

            }

        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );

        return image;
    }

}
