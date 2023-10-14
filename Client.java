import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;


public class Client {

    public  String ipAdress;
    public  String questionType;
    public  String domain;
    public  int port = 53;

    public Client(String ipAdress, int port, String domain, String questionType){

        this.port = port;
        this.ipAdress = ipAdress;
        this.domain = domain;
        this.questionType = questionType;
    }

    String get_ipAddress(){
        return this.ipAdress;
    }

    public static void output(byte[] queryAnswer, int qnameLength) throws UnknownHostException, UnsupportedEncodingException{

        //System.out.println(Arrays.toString(queryAnswer));
        int ANCOUNT  = queryAnswer[6] << 8; //shiftting 6th to left
            ANCOUNT += queryAnswer[7];

            int s = 12;

            while(queryAnswer[s] != 0){
                s += queryAnswer[s]+1;
            }
            s+=5;
        for(int i = 0; i < ANCOUNT; i++){

            if(Integer.toBinaryString(queryAnswer[s]).charAt(0) == '1' && Integer.toBinaryString(queryAnswer[s]).charAt(1) == '1'){
                s += 2;
            }else{
                while(queryAnswer[s] != 0){
                    s += queryAnswer[s]+1;
                }s+=1;
            }
            byte[] type = new byte[2];
            type[0] = queryAnswer[s+0];
            type[1] = queryAnswer[s+1];

            int typeTmp = convert(type);

            String QuestionTypes[] = {"A", "NS", "MD", "MF", "CNAME", "SOA", "MB", "MG", "NULL", "WKS", "WKS", "PTR", "HINFO", "MINFO", "MX", "TXT"};
            String c = QuestionTypes[typeTmp-1];
            s += 4;

            byte[] ttl = new byte[4];
            ttl[0] = queryAnswer[s+0];
            ttl[1] = queryAnswer[s+1];
            ttl[2] = queryAnswer[s+2];
            ttl[3] = queryAnswer[s+3];

            //converting byte to integer
            int ttlTmp = (((ttl[0] & 0xff) << 24) | ((ttl[1] & 0xff) << 16) | ((ttl[2] & 0xff) << 8) | (ttl[3] & 0xff));
            s +=4;
            byte[] lendata = new byte[2];
            lendata[0] = queryAnswer[s];
            lendata[1] = queryAnswer[s+1];

            int lendataTmp = convert(lendata);

            byte[] rdata = new byte[lendataTmp];
            s+=2;
            for(int j = 0; j < lendataTmp; j++){
                rdata[j] = queryAnswer[s+j];
            }

            s += lendataTmp;

            if(c.equals("A")){
            InetAddress tmpIp = InetAddress.getByAddress(rdata);
            String myData = tmpIp.toString();

            System.out.println("Answer (TYPE="+ c + ", TTL="+ ttlTmp +", DATA=\""+ myData +"\")");

            }else{
                String myData1 = new String(Arrays.copyOfRange(rdata, 1, rdata.length), "UTF-8");
                System.out.println("Answer (TYPE="+ c + ", TTL="+ ttlTmp + ", DATA=\""+ myData1 +"\")");
            }

        }

    }

    public static short convert(byte[] byteData){
        return (short)(((byteData[0] & 0xff) << 8) + (byteData[1] & 0xFF));
    }
    public static void main(String [] args) throws UnsupportedEncodingException{

        if(args.length == 2 || args.length == 3){

            Question question;
            Header header;
            Client client;
            Random rd = new Random();
            short randomId = (short) rd.nextInt(Short.MAX_VALUE + 1); //generating random ID
            String argDefault = null;

            if(args.length == 3){
                argDefault = args[2];
            }else{
                argDefault = "A";
            }
            client = new Client(args[0], 53, args[1], argDefault);

            header = new Header((short)randomId, (short)0b0000000100000000,(short)1, (short)0, (short)0, (short)0 );

            int bufferLength = client.domain.length();

            //removing dots from domain and replace them by length og each part
            ByteBuffer byteB = ByteBuffer.allocate(bufferLength+2); //
            String[] domainParts = client.domain.split("\\.");

            for(int j = 0 ; j < domainParts.length ; j++){
                    byteB.put((byte)domainParts[j].length());
                for(int k = 0; k < domainParts[j].length(); k++){
                    byteB.put((byte)domainParts[j].charAt(k));
                }
            }

                byteB.put((byte)0x00);
                byteB.rewind();

                byte[] tmp = byteB.array();

            if(client.questionType.equals("A")){

                     question = new Question(tmp, (short)1, (short)1);
            }else{
                     question = new Question(tmp, (short)16, (short)1);
            }


            Short lenght =(short) (header.length() + question.length());

            ByteBuffer msg = ByteBuffer.allocate(lenght + 4);

            msg.put(header.bytebuffer.array());
            msg.put(question.QNAME);
            msg.putShort(question.QTYPE);
            msg.putShort(question.QCLASS);

            Query q = new Query();

            try{
                byte[] msgByte = msg.array();
                short lenghtMsg = (short)msgByte.length;
                System.out.println("\nQuestion (NS="+  args[0] + ", NAME="+ args[1] + ", TYPE="+ argDefault+")"); //j'ai remplacé args[2] par argdefault
                byte [] queryAnswer = q.query(msgByte,client, lenghtMsg);
                output(queryAnswer, tmp.length);

            }catch(Exception e){
                System.out.println(e);
            }

        }else{
            System.out.println("Run the program like that :");
            System.out.println("java Client <name server IP> <domain name to query> [question type],");
        }
    }
}
