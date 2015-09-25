package filetransfer.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    /**
     * the server's single server socket used to accept new connections from.
     */
    private ServerSocket serverSocket;

    /**
     * observer that is notified about server events.
     */
    private Listener listener;

    // public interface: constructors

    /**
     * instantiates a server.
     *
     * @param listener     observer object that is notified of server events.
     * @param listenPort   port to bind the server to; connection requests
     *   received on this port will be accepted.
     *
     * @throws IOException when the server socket fails to bind to the given
     *   port.
     */
    public Server(Listener listener,int listenPort) throws IOException
    {
        // initialize instance data
        this.listener = listener;
        this.serverSocket = new ServerSocket(listenPort);

        // start the accept thread
        new AcceptThread().start();
    }

    // public interface: server methods

    /**
     * observer that is can be registered with instances of this class, and is
     * notified when a new connection is accepted by the server.
     */
    public interface Listener
    {
        void onAccept(Socket newSocket);
    }

    /**
     * closes the server socket, so it won't accept anymore new connections.
     *
     * @throws IOException when the server socket is already closed.
     */
    public void stopServer() throws IOException
    {
        serverSocket.close();
    }

    // private instances: threads

    /**
     * accepts any new connection requests forever.
     */
    private class AcceptThread extends Thread
    {
        public void run()
        {
            // accept any new connections until the socket is closed by another
            // thread
            while(true)
            {
                // accept a connection...
                try
                {
                    listener.onAccept(serverSocket.accept());
                }

                // server socket closed by another thread; stop accepting
                // connections.
                catch(IOException e)
                {
                    break;
                }
            }
        }
    }
}
