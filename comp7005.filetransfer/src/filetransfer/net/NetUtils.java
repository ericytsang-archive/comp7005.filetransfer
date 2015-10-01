package filetransfer.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import filetransfer.MiscUtils;

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
    // constants: packet control characters

    /**
     * indicates that there are more packets to follow.
     *
     * this is only used to transfer strings...
     */
    public static final String CONTROL_CONTINUE = "a";

    /**
     * indicates that this packet is the last packet of the network operation.
     *
     * this is only used to transfer strings...
     */
    public static final String CONTROL_EOT = "b";

    // constants: packet parameters

    /**
     * maximum number of characters allowed to be sent using the wruteUTF method
     *   before it is fragmented by application level code.
     */
    public static final int MAX_STRING_SEGMENT_LENGTH = Integer.MAX_VALUE-100;

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
            MiscUtils.affirm(socket.getInputStream().read() == -1);
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
            case CONTROL_CONTINUE:
                stb.append(segmentBody);
                break;
            case CONTROL_EOT:
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
            String segment = string.substring(cursor,Math.min(string.length(),cursor+MAX_STRING_SEGMENT_LENGTH));
            cursor += segment.length();

            // send segment header
            if(cursor < string.length())
            {
                os.writeUTF(CONTROL_CONTINUE);
            }
            else
            {
                os.writeUTF(CONTROL_EOT);
            }

            // send segment body
            os.writeUTF(segment);
        }
    }
}
