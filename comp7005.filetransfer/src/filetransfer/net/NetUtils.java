package filetransfer.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import filetransfer.logic.Protocol;

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

    public static String readString(Socket socket) throws IOException
    {
        // get the streams
        DataInputStream is = new DataInputStream(socket.getInputStream());

        // read & return the string
        StringBuilder stb = new StringBuilder();
        loop : while(true)
        {
            // read packet
            String segmentHeader = is.readUTF();
            String segmentBody = is.readUTF();

            // handle packet
            switch(segmentHeader)
            {
            case Protocol.CONTROL_CONTINUE:
                stb.append(segmentBody);
                break;
            case Protocol.CONTROL_EOT:
                stb.append(segmentBody);
                break loop;
            default:
                throw new RuntimeException("unknown segment header");
            }
        }
        return stb.toString();
    }

    public static void sendString(Socket socket,String string) throws IOException
    {
        // get the streams
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        // send the string
        for(int cursor = 0; cursor < string.length();)
        {
            String segment = string.substring(cursor,Math.min(string.length(),cursor+Protocol.SEGMENT_LENGTH));
            cursor += segment.length();

            // send segment header
            if(cursor < string.length())
            {
                os.writeUTF(Protocol.CONTROL_CONTINUE);
            }
            else
            {
                os.writeUTF(Protocol.CONTROL_EOT);
            }

            // send segment body
            os.writeUTF(segment);
        }
    }
}
