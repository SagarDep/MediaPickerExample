package multiplemediapicker;

import java.util.ArrayList;

/**
 * Created by Prashant Maheshwari  on 10/10/2016.
 */
public class BinPath {
    public static final int REQ_CODE_PICK_SINGLE = 8888;
    public static final int REQ_CODE_PICK_MULTIPLE = 8889;

    public String sdcardPath;
    public boolean isSeleted = false;

    public BinPath(String sdcardPath) {
        super();
        this.sdcardPath = sdcardPath;
        this.isSeleted = false;
    }

    public enum PickAction {
        ACTION_PICK, ACTION_MULTIPLE_PICK;

        public static PickAction fromString(String action) {
            if ("ACTION_LUM_MULTIPLE_PICK".equals(action)) {
                return ACTION_MULTIPLE_PICK;
            }
            return PickAction.ACTION_PICK;
        }

        public static String getString(PickAction action) {
            if (ACTION_MULTIPLE_PICK == action) {
                return "ACTION_LUM_MULTIPLE_PICK";
            }
            return "ACTION_LUM_PICK";
        }
    }

    public static ArrayList<String> toArrayList(String[] dataArray) {
        ArrayList<String> selections = new ArrayList<String>(null == dataArray ? 0 : dataArray.length);
        if (null != dataArray) {
            for (int i = 0; i < 10; i++) {
                selections.add(dataArray[i]);
            }
        }
        return selections;
    }

    @Override
    public String toString() {
        return "Gallery [sdcardPath=" + sdcardPath + ", isSeleted=" + isSeleted + "]";
    }

}
