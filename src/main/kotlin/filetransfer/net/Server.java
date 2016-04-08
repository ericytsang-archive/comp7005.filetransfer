package filetransfer.net;

import com.teamhoe.reliableudp.ServerSocket;
import com.teamhoe.reliableudp.SocketInputStream;
import com.teamhoe.reliableudp.SocketOutputStream;

import java.io.IOException;
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
    private ServerSocket serverSocket;

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
     * @return  new instance of Server
     */
    public Server()
    {
        // initialize instance data
        try
        {
            this.serverSocket = ServerSocket.Companion.make(null);
            this.serverSocket.close();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    // public interface: server methods

    /**
     * binds the server to port {@code listenPort}.
     *
     * @method  start
     *
     * @date    2015-09-29T18:06:09-0800
     *
     * @author  Eric Tsang
     *
     * @param   listenPort port to bind the server to; connection requests
     *   received on this port will be accepted.
     *
     * @throws  AlreadyBoundException if the server fails to bind to port {@code
     *   listenPort} because the server is already bound to a port.
     * @throws  PortOccupiedException if the server fails to bind to port {@code
     *   listenPort} because the port is in use.
     */
    public void start(int listenPort) throws AlreadyBoundException, PortOccupiedException
    {
        try
        {
            // create a new socket if it is already closed
            if(serverSocket.isClosed())
            {
                serverSocket = ServerSocket.Companion.make(listenPort);
            }

            // todo: bind the socket
            // serverSocket.bind(new InetSocketAddress(listenPort));

            // start the accept thread
            new AcceptThread().start();
        }
        catch(IOException e)
        {
            if(serverSocket.isBound())
            {
                throw new AlreadyBoundException();
            }
            else
            {
                throw new PortOccupiedException();
            }
        }
    }

    /**
     * closes the server socket, so it won't accept anymore new connections.
     *
     * @method  stop
     *
     * @date    2015-09-29T18:30:52-0800
     *
     * @author  Eric Tsang
     *
     * @throws  IOException thrown when the server was already stopped.
     */
    public void stop() throws IOException
    {
        if(!serverSocket.isClosed())
        {
            serverSocket.close();
        }
        else
        {
            throw new IOException("already closed");
        }
    }

    /**
     * returns the underlying server socket.
     *
     * @method  getServerSocket
     *
     * @date    2015-09-29T18:30:52-0800
     *
     * @author  Eric Tsang
     *
     * @return  the underlying server socket.
     */
    public ServerSocket getServerSocket()
    {
        return serverSocket;
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
     * @param   sis the socket that was just accepted by the server.
     */
    protected abstract void onAccept(SocketInputStream sis,SocketOutputStream sos);

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
                        try
                        {
                            SocketInputStream sis = serverSocket.accept(null,null);
                            try
                            {
                                Thread.sleep(10);
                            }
                            catch(InterruptedException e)
                            {
//                                nothing
                            }
                            SocketOutputStream sos = serverSocket.connect(sis.getRemoteAddress(),null);
                            synchronized(waitingThreadCount)
                            {
                                waitingThreadCount.decrementAndGet();
                                waitingThreadCount.notify();
                            }
                            onAccept(sis,sos);
                        }

                        // IOException occurred. perhaps server socket is
                        // closed.
                        catch(IOException e)
                        {
                            AcceptThread.this.interrupt();
                        }
                    }).start();
                }
            }
        }
    }
}
