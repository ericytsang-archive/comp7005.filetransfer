package filetransfer.net;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Eric on 9/28/2015.
 */
public class NetUtils
{
    public static void affirm(boolean b)
    {
        if(!b)
            throw new RuntimeException("affirmation failed!");
    }

    public static void waitForClosure(Socket socket)
    {
        try
        {
            affirm(socket.getInputStream().read() == -1);
        }
        catch(IOException e)
        {
            // socket closed
        }
    }
}
