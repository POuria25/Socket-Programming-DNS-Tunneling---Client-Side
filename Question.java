import java.nio.ByteBuffer;

public class Question {

    public byte[] QNAME;
    public Short QTYPE;
    public Short QCLASS;
    public ByteBuffer byteBuffer;

    public Question(byte[] QNAME, Short QTYPE, Short QCLASS){
        byte b = 0;
        this.QNAME = QNAME;
        this.QTYPE = QTYPE;
        this.QCLASS = QCLASS;
        this.byteBuffer = ByteBuffer.allocate(QNAME.length+2+2);
        this.byteBuffer.put(this.QNAME);
        //this.byteBuffer.put(b);
        this.byteBuffer.putShort(this.QTYPE);
        this.byteBuffer.putShort(this.QCLASS);
        this.byteBuffer.rewind();

    }

    public int length() {

        return QNAME.length;
    }
}
