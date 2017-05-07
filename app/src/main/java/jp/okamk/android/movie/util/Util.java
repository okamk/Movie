package jp.okamk.android.movie.util;

import java.text.DecimalFormat;

public class Util {
    public static String getSize(long size) {
        double dsize = ((double) size);
        dsize = dsize / 1024;
        dsize = dsize / 1024;
        DecimalFormat exFormat1 = new DecimalFormat("0.000MB");
        return exFormat1.format(dsize);
    }

    public static String getDuration(long duration) {
        long minute = (duration / 1000) / 60;
        long second = (duration / 1000) % 60;
        return String.format("%d:%02d", minute, second);
    }
}
