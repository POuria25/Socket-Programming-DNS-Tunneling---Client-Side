import java.nio.ByteBuffer;
import java.io.*;


public class Header {

    public  ByteBuffer bytebuffer;
    
    public Header(short ID, short Flage, 
        short QDCOUNT, short ANCOUNT, short NSCOUNT, short ARCOUNT){
              
        this.bytebuffer = ByteBuffer.allocate(12);
        this.bytebuffer.putShort(ID);
        this.bytebuffer.putShort(Flage);
        this.bytebuffer.putShort(QDCOUNT);
        this.bytebuffer.putShort(NSCOUNT);
        this.bytebuffer.putShort(ARCOUNT);
        
    }

    public int length(){            
        
        return bytebuffer.array().length;
    }
}
