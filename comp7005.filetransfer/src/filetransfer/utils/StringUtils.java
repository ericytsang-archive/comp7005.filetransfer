package filetransfer.utils;

/**
 * Created by Eric on 9/25/2015.
 */
public abstract class StringUtils
{
    public static boolean isUnsignedNumber(String string)
    {
        try
        {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseUnsignedInt(string);
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    }
}
