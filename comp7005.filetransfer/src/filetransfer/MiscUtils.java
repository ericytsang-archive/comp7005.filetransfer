package filetransfer;

/**
 * provides static access to miscellaneous helper functions.
 *
 * @file    ListItem.java
 *
 * @program comp7005.filetransfer.jar
 *
 * @class   MuscUtils
 *
 * @date    2015-10-01T09:35:34-0800
 *
 * @author  Eric Tsang
 */
public class MiscUtils
{
    /**
     * makes sure that the passed expression is true; the function will throw a
     *   runtime exception
     *
     * @method  affirm
     *
     * @date    2015-09-29T20:34:16-0800
     *
     * @author  Eric Tsang
     *
     * @param   b when false, the function will throw a runtime exception.
     */
    public static void affirm(boolean b)
    {
        if(!b) throw new RuntimeException("affirmation failed!");
    }
}
