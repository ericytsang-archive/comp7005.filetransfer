package filetransfer.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import filetransfer.logic.Protocol;

/**
 * provides static access to miscellaneous helper functions related to
 *   networking.
 *
 * @method  affirm
 *
 * @class   NetUtils
 *
 * @date    2015-09-29T20:44:36-0800
 *
 * @author  Eric Tsang
 */
public class NetUtils
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

    /**
     * waits for the passed socket to be closed by the remote host before
     *   returning.
     *
     * @method  waitForClosure
     *
     * @date    2015-09-29T20:24:41-0800
     *
     * @author  Eric Tsang
     *
     * @param   socket [description]
     */
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

    /**
     * reads a string from the passed socket. this method is created because
     *   readUTF would fail to read strings that are longer than
     *   Integer.MAX_VALUE. strings read from the socket must be sent with the
     *   sendString() function.
     *
     * @method  readString
     *
     * @date    2015-09-29T20:37:42-0800
     *
     * @author  Eric Tsang
     *
     * @param   socket socket to read the string from.
     *
     * @return  the string read from the socket.
     *
     * @throws  IOException thrown when one occurs...
     */
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

    /**
     * writes a string to the passed socket. this method is created because
     *   writeUTF would fail to write strings that are longer than
     *   Integer.MAX_VALUE. strings sent to the socket must be read with the
     *   readString() function.
     *
     * @method  sendString
     *
     * @date    2015-09-29T20:43:32-0800
     *
     * @author  Eric Tsang
     *
     * @param   socket socket to send the string through.
     * @param   string string to send through the socket.
     *
     * @throws  IOException thrown when one occurs...
     */
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
