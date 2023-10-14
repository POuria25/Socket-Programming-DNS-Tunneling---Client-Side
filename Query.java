import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.Arrays;

public class Query{

    public byte[] query(byte[] bytesToSend, Client c, short lenghtMsg) throws IOException {
    //Initiate a new TCP connection with a Socket
    Socket socket = new Socket(c.get_ipAddress(),  53);
    OutputStream out = socket.getOutputStream();
    InputStream in = socket.getInputStream();
    socket.setSoTimeout(5000);
    byte[] b = new byte[2];
    ByteBuffer.wrap(b).order(ByteOrder.BIG_ENDIAN).asShortBuffer().put(lenghtMsg);
    out.write(b);
    out.flush();
    //send a query in the form of a byte array
    out.write(bytesToSend);
    out.flush();
    //isRetrive the reponse lenght, as described in RFC 1035 (4.2.2 TCP usage)
    byte[] lengthBuffer = new byte[2];
    in.read(lengthBuffer); //Verify it returns 2
    //convert bytes to length (data sent over the network is always big-endian)
    int length = ((lengthBuffer[0] & 0xff) << 8) | (lengthBuffer[1] & 0xff);
    //Retrieve the full reponse
    byte[] reponseBuffer = new byte[length];
    in.read(reponseBuffer); //Verify it returns the value of "lenght"
    in.close();
    socket.close();
    return reponseBuffer;
    }
}
