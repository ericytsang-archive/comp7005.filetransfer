package filetransfer.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

import filetransfer.logic.Protocol;

/**
 * Created by Eric on 9/28/2015.
 */
public class PullClient extends Client
{
    private String request;
    private String response;

    public PullClient(SocketAddress remoteAddress)
    {
        super(remoteAddress);
    }

    @Override
    protected void onConnect(Client client,Socket socket) throws IOException,SecurityException
    {
        try
        {
            // get the streams from the socket
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());

            // send pull request
            for(int cursor = 0; cursor < request.length(); )
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

            loop:
            while(true)
            {
                // read packet
                String segmentHeader = is.readUTF();
                String segmentBody = is.readUTF();

                // handle packet
                switch(segmentHeader)
                {
                case Protocol.CONTROL_REFUSE_CONNECTION:
                    throw new SecurityException("connection refused by remote host application code");
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

            response = stb != null ? stb.toString() : null;
            System.out.println("<<<"+response);
        }
        finally
        {
            socket.close();
        }
    }

    public String pull(String request) throws IOException
    {
        this.request = request;
        connect();
        return response;
    }
}
