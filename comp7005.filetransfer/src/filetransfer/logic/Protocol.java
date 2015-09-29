package filetransfer.logic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;

public class Protocol
{
    // constants: packet control characters
    public static final String CONTROL_CONTINUE = "a";
    public static final String CONTROL_EOT = "b";
    public static final String CONTROL_NULL = "c";
    public static final String CONTROL_REFUSE_CONNECTION = "d";

    // constants: packet preferences
    public static final int SEGMENT_LENGTH = Integer.MAX_VALUE-100;
}
