package filetransfer.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * a server that is bound to a port, and will accept connection requests sent to
 *   the port until the server is stopped.
 *
 * @file    Server.java
 *
 * @program comp7005.filetransfer.jar
 *
 * @class   Server
 *
 * @date    2015-09-29T18:34:48-0800
 *
 * @author  Eric Tsang
 */
public abstract class Server
{
    /**
     * number of threads that are waiting for connection requests allowed
     */
    private static final int MAX_WAITING_ACCEPT_THREADS = 5;

    /**
     * keeps count of the number of threads that are waiting to accept a new
     *   connection.
     */
    private final AtomicInteger waitingThreadCount = new AtomicInteger(0);

    /**
     * the server's single server socket used to accept new connections from.
     */
    private final ServerSocket serverSocket;

    // public interface: constructors

    /**
     * instantiates a server that is bound to port {@code listenPort}.
     *
     * @method  Server
     *
     * @date    2015-09-29T18:06:09-0800
     *
     * @author  Eric Tsang
     *
     * @param   listenPort port to bind the server to; connection requests
     *   received on this port will be accepted.
     *
     * @return  new instance of Server
     *
     * @throws  IOException if the server fails to bind to port {@code
     *   listenPort}
     */
    public Server(int listenPort) throws IOException
    {
        // initialize instance data
        this.serverSocket = new ServerSocket(listenPort);

        // start the accept thread
        new AcceptThread().start();
    }

    // public interface: server methods

    /**
     * closes the server socket, so it won't accept anymore new connections.
     *
     * @method  stopServer
     *
     * @date    2015-09-29T18:30:52-0800
     *
     * @author  Eric Tsang
     *
     * @throws  IOException thrown when the server was already stopped.
     */
    public void stopServer() throws IOException
    {
        serverSocket.close();
    }

    // protected interface: callbacks

    /**
     * callback invoked when a new connection is accepted by the server.
     *
     * @method  onAccept
     *
     * @date    2015-09-29T18:31:40-0800
     *
     * @author  Eric Tsang
     *
     * @param   newSocket the socket that was just accepted by the server.
     */
    protected abstract void onAccept(Socket newSocket);

    // private instances: threads

    /**
     * accepts connection requests from the server's server socket until the
     *   server socket is closed.
     *
     * @class   AcceptThread
     *
     * @date    2015-09-29T18:32:12-0800
     *
     * @author  Eric Tsang
     */
    private class AcceptThread extends Thread
    {
        public void run()
        {
            // accept any new connections until the socket is closed by another
            // thread
            synchronized(waitingThreadCount)
            {
                while(!serverSocket.isClosed())
                {
                    // wait until there is less than maximum number of threads
                    // waiting before continuing to create another thread
                    try
                    {
                        while(waitingThreadCount.get() >= MAX_WAITING_ACCEPT_THREADS)
                        {
                            waitingThreadCount.wait();
                        }
                    }

                    // interrupted because IOException occurred. perhaps server
                    // socket is closed now.
                    catch(InterruptedException e)
                    {
                        // do nothing
                    }

                    // create the thread to accept the connection and such
                    waitingThreadCount.incrementAndGet();
                    new Thread(() ->
                    {
                        synchronized(waitingThreadCount)
                        {
                            try
                            {
                                Socket newSocket = serverSocket.accept();
                                waitingThreadCount.decrementAndGet();
                                waitingThreadCount.notify();
                                onAccept(newSocket);
                            }

                            // IOException occurred. perhaps server socket is
                            // closed.
                            catch(IOException e)
                            {
                                AcceptThread.this.interrupt();
                            }
                        }
                    }).start();
                }
            }
        }
    }
}
