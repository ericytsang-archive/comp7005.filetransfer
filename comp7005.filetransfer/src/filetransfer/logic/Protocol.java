package filetransfer.logic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;

public class Protocol
{
    public static final int PULL_DIRECTORY_FILES = 0;

    // constants: packet control characters
    public static final String CONTROL_CONTINUE = "a";
    public static final String CONTROL_EOT = "b";
    public static final String CONTROL_NULL = "c";
    public static final String CONTROL_REFUSE_CONNECTION = "d";

    // constants: packet preferences
    public static final int SEGMENT_LENGTH = Integer.MAX_VALUE-100;

    // constants: json keys
    public static final String KEY_TYPE = "0";
    public static final String KEY_PATH = "1";

    // constants: values for {@link KEY_PATH}
    public static final int TYPE_PULL_DIR_FILES = 0;

    public static String pull(Socket socket,String request) throws IOException
    {
        // get the streams from the socket
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        DataInputStream is = new DataInputStream(socket.getInputStream());

        // send pull request
        for(int cursor = 0; cursor < request.length();)
        {
            String segment = request.substring(cursor,Math.min(request.length(),cursor+Protocol.SEGMENT_LENGTH));
            cursor += segment.length();

            // sendSms segment header
            if(cursor < request.length())
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
        System.out.println(">>>"+request);

        // read request response
        StringBuilder stb = new StringBuilder();

        loop : while(true)
        {
            // read packet
            String segmentHeader = is.readUTF();
            String segmentBody = is.readUTF();

            // handle packet
            switch(segmentHeader)
            {
            case Protocol.CONTROL_REFUSE_CONNECTION:
                throw new ConnectException("connection refused by remote host");
            case Protocol.CONTROL_NULL:
                stb = null;
                break loop;
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

        String response = stb != null ? stb.toString() : null;
        System.out.println("<<<"+response);
        return response;
    }

    public static String readString(InputStream inputStream) throws IOException
    {
        // get the input and output streams
        DataInputStream is = new DataInputStream(inputStream);

        // read the pull request payload
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
        String request = stb.toString();
        System.out.println("<<<"+request);
        return request;
    }
}
