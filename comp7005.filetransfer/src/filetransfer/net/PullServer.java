package filetransfer.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.Set;

import filetransfer.logic.Protocol;

public abstract class PullServer extends Server
{
    private Set<String> refusedConnections;

    // public interface: constructors

    /**
     * instantiates a server.
     *
     * @param listenPort port to bind the server to; connection requests
     *                   received on this port will be accepted.
     * @throws IOException when the server socket fails to bind to the given
     *                     port.
     */
    public PullServer(int listenPort) throws IOException
    {
        super(listenPort);
        this.refusedConnections = new LinkedHashSet<>();
    }

    // public interface: server methods

    /**
     * may be called within the pull request handler to refuse the connection.
     *
     * @param remote socket that is connected to the remote host.
     * @param request request received from the remote host used to identify
     *   which request was refused.
     */
    public void refuseConnection(Socket remote,String request)
    {
        refusedConnections.add(remote.getInetAddress()+request);
    }


    public boolean isConnectionRefused(Socket remote,String request)
    {
        return refusedConnections.remove(remote.getInetAddress()+request);
    }

    // protected interface: callbacks

    protected abstract String onPullRequest(Socket remote,String request);

    @Override
    protected void onAccept(Socket socket)
    {
        try
        {
            // get the input and output streams
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());

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

            // handle the pull request
            String response;
            try
            {
                response = onPullRequest(socket,request);
                System.out.println(">>>"+response);
            }

            // exception occurred in the handler; throw
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }

            // tell client we are refusing their connection
            if(isConnectionRefused(socket,request))
            {
                os.writeUTF(Protocol.CONTROL_REFUSE_CONNECTION);
                os.writeUTF("");
            }

            // send the null response back to the client
            else if(response == null)
            {
                os.writeUTF(Protocol.CONTROL_NULL);
                os.writeUTF("");
            }

            // send the non-null response back to the client
            else for(int cursor = 0; cursor < response.length();)
                {
                    String segment = response.substring(cursor,Math.min(response.length(),cursor+Protocol.SEGMENT_LENGTH));
                    cursor += segment.length();

                    // send segment header
                    if(cursor < response.length())
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

            // wait for client to close connection
            NetUtils.waitForClosure(socket);
        }

        // the pull request was cancelled
        catch(IOException e)
        {
            // do nothing;
        }

        // close the bt socket regardless of how we exit
        finally
        {
            try
            {
                socket.getOutputStream().close();
                socket.getInputStream().close();
                socket.close();
            }
            catch(IOException e)
            {
                // do nothing
            }
        }
    }
}
