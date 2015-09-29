package filetransfer.net;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Eric on 9/28/2015.
 */
public abstract class Client
{
    private final SocketAddress remoteAddress;

    public Client(SocketAddress remoteAddress)
    {
        this.remoteAddress = remoteAddress;
    }

    /**
     * makes this client object try to connect to the remote host.
     *
     * @throws IOException when an error occurs during the connect, or network
     *   operations.
     * @throws SecurityException when the remote host refuses the connection
     *   from application level code.
     */
    protected void connect() throws IOException,SecurityException
    {
        // connect to the remote device, and open streams
        Socket socket = new Socket();
        socket.connect(remoteAddress);

        // do whatever you need to do
        onConnect(this,socket);
    }

    /**
     * invoked when the client seccessfully connects with the remote address.
     * @param client
     * @param socket
     */
    protected abstract void onConnect(Client client,Socket socket) throws IOException;
}

