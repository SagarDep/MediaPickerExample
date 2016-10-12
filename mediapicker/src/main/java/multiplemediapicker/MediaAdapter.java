package multiplemediapicker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;

import java.util.ArrayList;
import java.util.List;

import smartandrodev.mediapicker.R;

/**
 * Created by prashantm on 8/31/2016.
 */
public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    int mCameraIconRes;
    private int mSelectedIV;
    private int mImgWidth = 0;
    private boolean mAllowMultiplePick = false;
    private ArrayList<BinPath> mLuminousGalleries = new ArrayList<BinPath>();
    private boolean isImageViewer;
    private OnItemClickAdapter onItemClickAdapter;

    private boolean isSingleSelection = false;

    public MediaAdapter(Context context, int camResId, int selectedID, int numberOfRows, boolean mAllowMultiplePick, int maxSelectionLimit, boolean isSingleSelection, boolean isImageViewer) {
        this.mContext = context;

        this.mSelectedIV = selectedID;
        this.mCameraIconRes = camResId;
        this.mImgWidth = getWidth((Activity) context) / numberOfRows;
        this.mAllowMultiplePick = mAllowMultiplePick;
        this.isImageViewer = isImageViewer;
        this.maxSelectionLimit = maxSelectionLimit;

        this.isSingleSelection = isSingleSelection;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent,
                false);

        v.setLayoutParams(new AbsListView.LayoutParams(mImgWidth, mImgWidth));
        return new GViewHolder(v);
    }

    public void setOnItemClickAdapter(OnItemClickAdapter onItemClickAdapter) {
        this.onItemClickAdapter = onItemClickAdapter;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {

        final GViewHolder holder = (GViewHolder) holder1;

        holder.imgQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onItemClickAdapter) {
                    onItemClickAdapter.onClick(0, position, holder);
                }

            }
        });

        if (position != 0) {
            holder.imgQueue_zero.setVisibility(View.GONE);
            final boolean selected = mAllowMultiplePick && mLuminousGalleries.get(position).isSeleted;
            holder.imgQueueMultiSelected.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);

            if (isImageViewer) {

                if (mLuminousGalleries.get(position).sdcardPath.endsWith(".gif")
                        || mLuminousGalleries.get(position).sdcardPath.endsWith(".GIF")) {
                    Glide.with(holder.imgQueue.getContext())
                            .load("file://" + mLuminousGalleries.get(position).sdcardPath)
                            .asGif()
                            .thumbnail(0.2f)
                            .placeholder(R.drawable.grey_loader)
                            .centerCrop()
                            .into(holder.imgQueue);
                } else {

                    Glide.with(holder.imgQueue.getContext())
                            .load("file://" + mLuminousGalleries.get(position).sdcardPath)
                            .asBitmap().sizeMultiplier(0.5f)
                            .thumbnail(0.2f)
                            .placeholder(R.drawable.grey_loader)
                            .centerCrop()
                            .into(holder.imgQueue);
                }


                holder.mPlayButton.setVisibility(View.GONE);

            } else {
                BitmapPool bitmapPool = Glide.get(holder.imgQueue.getContext()).getBitmapPool();
                int microSecond = 1000000;// 6th second as an example
                VideoBitmapDecoder videoBitmapDecoder = new VideoBitmapDecoder(microSecond);
                FileDescriptorBitmapDecoder fileDescriptorBitmapDecoder = new FileDescriptorBitmapDecoder(videoBitmapDecoder, bitmapPool, DecodeFormat.PREFER_ARGB_8888);
                Glide.with(holder.imgQueue.getContext())
                        .load("file://" + mLuminousGalleries.get(position).sdcardPath)
                        .asBitmap()
                        .placeholder(R.drawable.grey_loader)
                        .override(80, 80)// Example
                        .videoDecoder(fileDescriptorBitmapDecoder)
                        .into(holder.imgQueue);

                holder.mPlayButton.setVisibility(View.VISIBLE);
            }

        } else {
            holder.imgQueueMultiSelected.setVisibility(View.INVISIBLE);
            holder.imgQueue.setImageBitmap(null);
            holder.mPlayButton.setVisibility(View.GONE);
            holder.imgQueue_zero.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mLuminousGalleries.size();
    }

    public BinPath getItem(int position) {
        return mLuminousGalleries.get(position);
    }

    class GViewHolder extends RecyclerView.ViewHolder {

        ImageView imgQueue;
        ImageView imgQueueMultiSelected;
        ImageView mPlayButton;
        ImageView imgQueue_zero;

        public GViewHolder(View itemView) {
            super(itemView);

            imgQueue = (ImageView) itemView.findViewById(R.id.imgQueue);
            mPlayButton = (ImageView) itemView.findViewById(R.id.play_iv);
            imgQueueMultiSelected = (ImageView) itemView.findViewById(R.id.imgQueueMultiSelected);
            imgQueue_zero = (ImageView) itemView.findViewById(R.id.imgQueue_zero);
            imgQueue_zero.setImageResource(mCameraIconRes);
            imgQueueMultiSelected.setImageResource(mSelectedIV);
        }
    }

    public int getWidth(Activity act) {
        Display display = act.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public void setAllOk(boolean selection) {

        for (int i = 1; i < mLuminousGalleries.size(); i++) {
            mLuminousGalleries.get(i).isSeleted = selection;

        }

        notifyDataSetChanged();
    }

    public boolean isAllSelected() {
        boolean isAllSelected = true;

        for (int i = 1; i < mLuminousGalleries.size(); i++) {
            if (!mLuminousGalleries.get(i).isSeleted) {
                isAllSelected = false;
                break;
            }
        }

        return isAllSelected;
    }

    public boolean isAnySelected() {
        boolean isAnySelected = false;

        for (int i = 1; i < mLuminousGalleries.size(); i++) {
            if (mLuminousGalleries.get(i).isSeleted) {
                isAnySelected = true;
                break;
            }
        }

        return isAnySelected;
    }

    public List<BinPath> getSelected() {
        List<BinPath> galleries = new ArrayList<BinPath>();

        for (int i = 1; i < mLuminousGalleries.size(); i++) {
            if (mLuminousGalleries.get(i).isSeleted) {
                galleries.add(mLuminousGalleries.get(i));
            }
        }

        return galleries;
    }

    public void addAll(ArrayList<BinPath> files) {

        try {
            this.mLuminousGalleries.clear();
            this.mLuminousGalleries.addAll(files);

        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    public void add(BinPath luminousGallery) {
        mLuminousGalleries.add(luminousGallery);
        notifyDataSetChanged();
    }

    int maxSelectionLimit = 0;

    public void changeSelection(Object holder, int position) {

        final GViewHolder ViewHolder = (GViewHolder) holder;
        if (isSingleSelection) {
            setAllOk(false);
        }

        if (mLuminousGalleries.get(position).isSeleted) {
            mLuminousGalleries.get(position).isSeleted = false;
        } else {
            if (maxSelectionLimit != 0) {
                if (getSelected().size() >= maxSelectionLimit) {

                    Toast.makeText(mContext, "You can't select more than " + maxSelectionLimit + " media at a time", Toast.LENGTH_LONG).show();

                } else {
                    mLuminousGalleries.get(position).isSeleted = true;
                }

            } else {
                mLuminousGalleries.get(position).isSeleted = true;
            }

        }

        ViewHolder.imgQueueMultiSelected.setSelected(mLuminousGalleries.get(position).isSeleted);
        ViewHolder.imgQueueMultiSelected.setVisibility(mAllowMultiplePick && mLuminousGalleries.get
                (position).isSeleted ? View.VISIBLE
                : View.INVISIBLE);
    }
}
