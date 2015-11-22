package simpleworkflow.engine.persistence.sql.util;

/**
 * @author XingGu_Liu
 */
public class HexUtil {

    public static String toHexString(byte[] val) {
        return toHexString(val, 0, val.length);
    }

    public static String toHexString(byte[] val, int offset, int length) {
        long lVal = 0;
        int cnt = length / 8;
        int startIndex = offset;
        StringBuilder hexStr = new StringBuilder();

        for(int i = 0; i < cnt; i++) {

            lVal =
                    ((((long)val[startIndex]) << 56) & 0xFF00000000000000L) +
                            ((((long)val[startIndex + 1]) << 48) & 0x00FF000000000000L) +
                            ((((long)val[startIndex + 2]) << 40) & 0x0000FF0000000000L) +
                            ((((long)val[startIndex + 3]) << 32) & 0x000000FF00000000L) +
                            ((((long)val[startIndex + 4]) << 24) & 0x00000000FF000000L) +
                            ((((long)val[startIndex + 5]) << 16) & 0x0000000000FF0000L) +
                            ((((long)val[startIndex + 6]) << 8) &  0x000000000000FF00L) +
                            ((((long)val[startIndex + 7]) ) & 0x00000000000000FFL) ;
            hexStr.append(toHexString(lVal));

            startIndex += 8;
        }

        for(; startIndex < length; startIndex++) {
            hexStr.append(toHexString(val[startIndex]));
        }

        return hexStr.toString();
    }

    public static String toHexString(long val) {
        StringBuilder hexStr = new StringBuilder(Long.toHexString(val));

        for(int i = hexStr.length(); i < 16; i++) {
            hexStr.insert(0, '0');
        }

        return hexStr.toString();
    }

}
