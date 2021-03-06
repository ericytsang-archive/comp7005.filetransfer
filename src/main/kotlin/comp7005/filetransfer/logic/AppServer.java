package comp7005.filetransfer.logic;

import com.teamhoe.reliableudp.ServerSocket;
import com.teamhoe.reliableudp.SocketInputStream;
import com.teamhoe.reliableudp.SocketOutputStream;
import comp7005.filetransfer.net.NetUtils;
import comp7005.filetransfer.net.Server;
import org.json.JSONArray;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ProgressMonitor;

/**
 * listens to a specified port on the host, and accepts any connection requests
 *   received from it.
 *
 * handles requests from remote hosts to fulfill application level requests.
 *
 * when a new connection is created, the {@code onAccept} template method is
 *   invoked; subclasses of this class should override this method.
 *
 * @file    ListItem.java
 *
 * @program comp7005.filetransfer.jar
 *
 * @class   AppServer
 *
 * @date    2015-10-01T09:24:29-0800
 *
 * @author  Eric Tsang
 */
public class AppServer extends Server
{
    // constants: network operation IDs indicate network operation to performed

    /**
     * indicates to the server that we want to pull the files in a directory.
     */
    private static final int TYPE_PULL_DIR_FILES = 0;

    /**
     * indicates to the server that we want to download a file.
     */
    private static final int TYPE_PULL_FILE = 1;

    /**
     * indicates to the server that we want to upload a file.
     */
    private static final int TYPE_PUSH_FILE = 2;

    // constants: protocol parameters

    /**
     * the maximum number of bytes to send per segment when transferring a file.
     */
    private static final int MAX_FILE_SEGMENT_SIZE = 1024;

    // public interface: network operations & associated handlers

    /**
     * pulls the list of files that exists in the directory on the remote host.
     *
     * @method  setRemoteDirectory
     *
     * @date    2015-10-01T09:10:11-0800
     *
     * @author  Eric Tsang
     *
     * @param   remoteAddress the address of the remote host to connect to.
     * @param   progressMonitor updated to display the operation's progress.
     * @param   directoryPath path to the directory on the remote host.
     *
     * @return  the list of files that exists in the directory on the remote
     *   host.
     *
     * @throws  IOException thrown when an IOException occurs.
     */
    static List<JsonableFile> pullDirectoryFiles(InetSocketAddress remoteAddress,ProgressMonitor progressMonitor,String directoryPath) throws IOException
    {

        // perform the pull
        try(ServerSocket serverSocket = ServerSocket.Companion.make(7001))
        {
            // connect to the remote address
            progressMonitor.setProgress(0);
            SocketOutputStream sos = serverSocket.connect(remoteAddress,null);
            SocketInputStream sis = serverSocket.accept(null,null);

            // get handles to the streams
            progressMonitor.setProgress(1);
            DataOutputStream os = new DataOutputStream(sos);

            // send the request & directory path
            progressMonitor.setProgress(2);
            os.writeInt(TYPE_PULL_DIR_FILES);
            NetUtils.sendString(sos,directoryPath);

            // read the server's json response
            progressMonitor.setProgress(3);
            String response = NetUtils.readString(sis);

            // parse the received response string and return
            sis.close();
            sos.close();
            progressMonitor.setProgress(4);
            return JsonableUtils.fromJsonArray(JsonableFile.class,new JSONArray(response));
        }
    }

    /**
     * invoked to handle a connection that has issued a pull directory files
     *   request.
     *
     * @method  handlePullDirectoryFiles
     *
     * @date    2015-10-01T09:15:40-0800
     *
     * @author  Eric Tsang
     *
     * @param   sis input stream to read from
     * @param   sos the connection that has issued the request.
     *
     * @throws  IOException thrown when an IOException occurs.
     */
    @SuppressWarnings("ThrowFromFinallyBlock")
    private void handlePullDirectoryFiles(SocketInputStream sis,SocketOutputStream sos) throws IOException
    {
        // read the path from the socket
        String path = NetUtils.readString(sis);
        File directory = path.equals(".")
            ? new File(path).getAbsoluteFile().getParentFile()
            : new File(path);

        // add all the files in the specified directory to a list to be returned
        LinkedList<JsonableFile> files = new LinkedList<>();
        //noinspection ConstantConditions
        for(File file : directory.listFiles())
        {
            files.add(new JsonableFile(file));
        }

        // put parent directory as element in list to be returned if it exists
        File parentFile = directory.getParentFile();
        if(parentFile != null)
        {
            files.addFirst(new JsonableFile(parentFile.isDirectory(),parentFile.getAbsolutePath(),".."));
        }

        // serialize the list of files, and send it back, and wait for the
        // connection to close before closing ourselves and returning
        NetUtils.sendString(sos,JsonableUtils.toJsonArray(files).toString());
    }

    /**
     * downloads the specified file from the server.
     *
     * @method  pullFile
     *
     * @date    2015-10-01T08:58:48-0800
     *
     * @author  Eric Tsang
     *
     * @param   remoteAddress the address of the remote host to connect to.
     * @param   progressMonitor updated to display the operation's progress.
     * @param   remoteFilePath path to file on the remote server.
     * @param   directory local directory to save the pulled file to.
     *
     * @throws  IOException thrown when an IOExceptoin occurs.
     */
    public static void pullFile(InetSocketAddress remoteAddress,ProgressMonitor progressMonitor,String remoteFilePath,File directory) throws IOException
    {
        // perform the pull; download a file from the server
        try(ServerSocket serverSocket = ServerSocket.Companion.make(7001))
        {
            // connect to the remote address
            SocketOutputStream sos = serverSocket.connect(remoteAddress,null);
            SocketInputStream sis = serverSocket.accept(remoteAddress,null);

            // get handles to the streams
            DataInputStream is = new DataInputStream(sis);
            DataOutputStream os = new DataOutputStream(sos);

            // send the request & remoteFilePath
            os.writeInt(TYPE_PULL_FILE);
            NetUtils.sendString(sos,remoteFilePath);

            // read the size of the file
            long fileSize = is.readLong();
            String fileName = NetUtils.readString(sis);

            // read the contents of the file until its empty
            File file = new File(directory,fileName);
            try(FileOutputStream fos = new FileOutputStream(file))
            {
                long totalBytesRead = 0;
                int readResult;
                byte[] fileData = new byte[MAX_FILE_SEGMENT_SIZE];
                do
                {
                    // read the packet
                    readResult = is.readInt();
                    int segmentSize = Math.max(0,readResult);
                    is.readFully(fileData,0,segmentSize);

                    // write received bytes into the file
                    fos.write(fileData,0,segmentSize);

                    // update total bytes read & the progress monitor
                    progressMonitor.setProgress((int) (((float) totalBytesRead)/((float) fileSize)*100.0));
                    totalBytesRead += segmentSize;

                    // stop the download if it is cancelled
                    if(progressMonitor.isCanceled())
                    {
                        sis.close();
                        sos.close();
                        break;
                    }
                }
                while(readResult != -1);
            }
            sis.close();
            sos.close();
        }
    }

    /**
     * invoked to handle a connection that has issued a pull request.
     *
     * @method  handlePullFile
     *
     * @date    2015-10-01T09:01:52-0800
     *
     * @author  Eric Tsang
     *
     * @param   sos the connection that has issued the request.
     */
    @SuppressWarnings("ThrowFromFinallyBlock")
    private void handlePullFile(SocketInputStream sis,SocketOutputStream sos) throws IOException
    {
        // get references to the streams
        DataOutputStream os = new DataOutputStream(sos);

        // read the path from the socket
        String path = NetUtils.readString(sis);

        // send the file size, then file name
        File fileToSend = new File(path);
        os.writeLong(fileToSend.length());
        NetUtils.sendString(sos,fileToSend.getName());

        // read the contents of the file until its empty
        try(FileInputStream fis = new FileInputStream(fileToSend))
        {
            int readResult;
            byte[] fileData = new byte[MAX_FILE_SEGMENT_SIZE];
            do
            {
                // gather data to create packet
                readResult = fis.read(fileData);
                int segmentSize = Math.max(0,readResult);

                // send the packet
                os.writeInt(readResult);
                os.write(fileData,0,segmentSize);
            }
            while(readResult != -1);
        }
    }

    /**
     * pushes a local file to the remote server at the specified address.
     *
     * @method  pushFile
     *
     * @date    2015-10-01T09:02:54-0800
     *
     * @author  Eric Tsang
     *
     * @param   remoteAddress the address of the remote host to connect to.
     * @param   progressMonitor updated to display the operation's progress.
     * @param   directory directory on the remote server to save the file to.
     * @param   fileToSend local file to send to the remote server.
     *
     * @throws  IOException thrown when an IOExeption occurs.
     */
    public static void pushFile(InetSocketAddress remoteAddress,ProgressMonitor progressMonitor,String directory,File fileToSend) throws IOException
    {
        // perform the push
        try(ServerSocket serverSocket = ServerSocket.Companion.make(7001))
        {
            // connect to the remote address
            SocketOutputStream sos = serverSocket.connect(remoteAddress,null);
            SocketInputStream sis = serverSocket.accept(remoteAddress,null);
            sis.close();

            // get handles to the streams
            DataOutputStream os = new DataOutputStream(sos);

            // send the request & path
            os.writeInt(TYPE_PUSH_FILE);
            NetUtils.sendString(sos,directory);
            NetUtils.sendString(sos,fileToSend.getName());

            // read the contents of the file and send it all
            try(FileInputStream fis = new FileInputStream(fileToSend))
            {
                long bytesSent = 0;
                int readResult;
                byte[] buffer = new byte[MAX_FILE_SEGMENT_SIZE];
                do
                {
                    // gather data to create packet...if the upload is
                    // cancelled, tell the client that it is eof
                    readResult = !progressMonitor.isCanceled()
                            ? fis.read(buffer) : -1;
                    int segmentSize = Math.max(0,readResult);

                    // send the packet
                    os.writeInt(readResult);
                    os.write(buffer,0,segmentSize);

                    // update the progress monitor
                    bytesSent += segmentSize;
                    progressMonitor.setProgress((int) (((float) bytesSent)/((float) fileToSend.length())*100.0));
                }
                while(readResult != -1);
            }

            // wait for connection to close before closing ourselves and returning
            sis.close();
            sos.close();
        }
    }

    /**
     * invoked to handle a connection that has issued a push request.
     *
     * @method  handlePushFile
     *
     * @date    2015-10-01T09:05:25-0800
     *
     * @author  Eric Tsang
     *
     * @param   sis the connection that has issued the request.
     */
    @SuppressWarnings("ThrowFromFinallyBlock")
    private void handlePushFile(SocketInputStream sis) throws IOException
    {
        // get handles to the streams
        DataInputStream is = new DataInputStream(sis);

        // read the size of the file
        String directory = NetUtils.readString(sis);
        String fileName = NetUtils.readString(sis);

        // read the contents of the file until its empty
        File file = new File(directory,fileName);
        try(FileOutputStream fos = new FileOutputStream(file))
        {
            int readResult;
            byte[] fileData = new byte[MAX_FILE_SEGMENT_SIZE];
            do
            {
                // read the packet
                readResult = is.readInt();
                int segmentSize = Math.max(0,readResult);
                is.readFully(fileData,0,segmentSize);

                // write received bytes into the file
                fos.write(fileData,0,segmentSize);
            }
            while(readResult != -1);
        }
    }

    // protected interface: template method implementations

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
    @Override
    protected void onAccept(SocketInputStream sis,SocketOutputStream sos)
    {
        try
        {
            // get the input and output streams
            DataInputStream is = new DataInputStream(sis);

            int requestType = is.readInt();

            switch(requestType)
            {
            case TYPE_PULL_DIR_FILES:
                handlePullDirectoryFiles(sis,sos);
                break;
            case TYPE_PULL_FILE:
                handlePullFile(sis,sos);
                break;
            case TYPE_PUSH_FILE:
                handlePushFile(sis);
                break;
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            sos.close();
            sis.close();
        }
    }
}
