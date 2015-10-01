package filetransfer.logic;

import org.json.JSONArray;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ProgressMonitor;

import filetransfer.net.NetUtils;
import filetransfer.net.Server;

public class AppServer extends Server
{
    // constants: exchange types
    public static final int TYPE_PULL_DIR_FILES = 0;
    public static final int TYPE_PULL_FILE = 1;
    public static final int TYPE_PUSH_FILE = 2;

    public static final int MAX_SEGMENT_SIZE = 1024;

    public static void pullFile(InetSocketAddress remoteAddress,ProgressMonitor progressMonitor,String path,File directory) throws IOException
    {
        // perform the pull; download a file from the server
        try(Socket socket = new Socket())
        {
            // connect to the remote address
            socket.connect(remoteAddress);

            // get handles to the streams
            DataInputStream is = new DataInputStream(socket.getInputStream());
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            // send the request & path
            os.writeInt(TYPE_PULL_FILE);
            NetUtils.sendString(socket,path);

            // read the size of the file
            long fileSize = is.readLong();
            String fileName = NetUtils.readString(socket);

            // read the contents of the file until its empty
            File file = new File(directory,fileName);
            try(FileOutputStream fos = new FileOutputStream(file))
            {
                long totalBytesRead = 0;
                boolean isEot = false;
                byte[] fileData = new byte[MAX_SEGMENT_SIZE];
                while(!isEot)
                {
                    // read the packet
                    isEot = is.readBoolean();
                    int bytesRead = is.read(fileData);

                    // write received bytes into the file
                    fos.write(fileData,0,bytesRead);

                    // update total bytes read & the progress monitor
                    progressMonitor.setProgress((int) (((float) totalBytesRead)/((float) fileSize)*100.0));
                    totalBytesRead += bytesRead;
                }
            }
        }
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    private void handlePullFile(Socket socket)
    {
        // handle a request to pull a file
        try
        {
            // get references to the streams
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            // read the path from the socket
            String path = NetUtils.readString(socket);

            // send the file size, then file name
            File fileToSend = new File(path);
            os.writeLong(fileToSend.length());
            NetUtils.sendString(socket,fileToSend.getName());

            // read the contents of the file until its empty
            try(FileInputStream fis = new FileInputStream(fileToSend))
            {
                boolean isEot;
                byte[] buffer = new byte[MAX_SEGMENT_SIZE];
                do
                {
                    // gather data to create packet. it is important to check
                    // that for bytes available after reading from the stream,
                    // because we want to communicate to the remote host if this
                    // packet is the last packet or not, and we don't want to
                    // send an empty packet.
                    int delta = fis.read(buffer);
                    isEot = fis.available() == 0;

                    // send the packet
                    os.writeBoolean(isEot);
                    os.write(buffer,0,delta);
                }
                while(!isEot);
            }

            // wait for connection to close before closing ourselves and returning
            NetUtils.waitForClosure(socket);
        }

        // we don't want an IOException to break the server...so we ignore it
        catch(IOException e)
        {
            // do nothing...
        }

        // close the socket once we're done with it
        finally
        {
            try
            {
                socket.close();
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void pushFile(InetSocketAddress remoteAddress,ProgressMonitor progressMonitor,String directory,File fileToSend) throws IOException
    {
        // perform the push
        try(Socket socket = new Socket())
        {
            // connect to the remote address
            socket.connect(remoteAddress);

            // get handles to the streams
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            // send the request & path
            os.writeInt(TYPE_PUSH_FILE);
            NetUtils.sendString(socket,directory);
            NetUtils.sendString(socket,fileToSend.getName());

            // read the contents of the file and send it all
            try(FileInputStream fis = new FileInputStream(fileToSend))
            {
                long bytesSent = 0;
                boolean isEot;
                byte[] buffer = new byte[MAX_SEGMENT_SIZE];
                do
                {
                    // gather data to create packet. it is important to check
                    // that for bytes available after reading from the stream,
                    // because we want to communicate to the remote host if this
                    // packet is the last packet or not, and we don't want to
                    // send an empty packet.
                    int delta = fis.read(buffer);
                    isEot = fis.available() == 0;

                    // send the packet
                    os.writeBoolean(isEot);
                    os.write(buffer,0,delta);

                    // update the progress monitor
                    bytesSent += delta;
                    progressMonitor.setProgress((int) (((float) bytesSent)/((float) fileToSend.length())*100.0));
                }
                while(!isEot);
            }

            // wait for connection to close before closing ourselves and returning
            NetUtils.waitForClosure(socket);
        }
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    public void handlePushFile(Socket socket)
    {
        // perform the pull
        try
        {
            // get handles to the streams
            DataInputStream is = new DataInputStream(socket.getInputStream());

            // read the size of the file
            String directory = NetUtils.readString(socket);
            String fileName = NetUtils.readString(socket);

            // read the contents of the file until its empty
            File file = new File(directory,fileName);
            try(FileOutputStream fos = new FileOutputStream(file))
            {
                boolean isEot = false;
                byte[] fileData = new byte[MAX_SEGMENT_SIZE];
                while(!isEot)
                {
                    // read the packet
                    isEot = is.readBoolean();
                    int bytesRead = is.read(fileData);

                    // write received bytes into the file
                    fos.write(fileData,0,bytesRead);
                }
            }
        }

        catch(IOException e)
        {
            // do nothing...
        }

        // close the socket
        finally
        {
            try
            {
                socket.close();
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static List<JsonableFile> pullDirectoryFiles(InetSocketAddress remoteAddress,ProgressMonitor progressMonitor,String path) throws IOException
    {

        // perform the pull
        try(Socket socket = new Socket())
        {
            // connect to the remote address
            progressMonitor.setProgress(0);
            socket.connect(remoteAddress);

            // get handles to the streams
            progressMonitor.setProgress(1);
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            // send the request & path
            progressMonitor.setProgress(2);
            os.writeInt(TYPE_PULL_DIR_FILES);
            NetUtils.sendString(socket,path);

            // read the server's json response
            progressMonitor.setProgress(3);
            String response = NetUtils.readString(socket);

            // parse the received response string and return
            progressMonitor.setProgress(4);
            return JsonableUtils.fromJsonArray(JsonableFile.class,new JSONArray(response));
        }
    }

    private void handlePullDirectoryFiles(Socket socket) throws IOException
    {
        // read the path from the socket
        String path = NetUtils.readString(socket);

        // add all the files in the specified directory to a list to be returned
        LinkedList<JsonableFile> files = new LinkedList<>();
        //noinspection ConstantConditions
        for(File file : new File(path).listFiles())
        {
            files.add(new JsonableFile(file));
        }

        // put parent directory as element in list to be returned if it exists
        File parentFile = new File(path).getAbsoluteFile().getParentFile();
        if(parentFile != null)
        {
            files.addFirst(new JsonableFile(parentFile.isDirectory(),parentFile.getAbsolutePath(),".."));
        }

        // serialize the list of files, and send it back, and wait for the
        // connection to close before closing ourselves and returning
        NetUtils.sendString(socket,JsonableUtils.toJsonArray(files).toString());
        NetUtils.waitForClosure(socket);
        socket.close();
    }

    /**
     * instantiates a server.
     *
     * @param listenPort port to bind the server to; connection requests
     *                   received on this port will be accepted.
     * @throws IOException when the server socket fails to bind to the given
     *                     port.
     */
    public AppServer(int listenPort) throws IOException
    {
        super(listenPort);
    }

    @Override
    protected void onAccept(Socket newSocket)
    {
        try
        {
            // get the input and output streams
            DataInputStream is = new DataInputStream(newSocket.getInputStream());

            int requestType = is.readInt();

            switch(requestType)
            {
            case TYPE_PULL_DIR_FILES:
                handlePullDirectoryFiles(newSocket);
                break;
            case TYPE_PULL_FILE:
                handlePullFile(newSocket);
                break;
            case TYPE_PUSH_FILE:
                handlePushFile(newSocket);
                break;
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
