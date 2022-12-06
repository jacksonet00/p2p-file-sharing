import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

// the message factory decodes and generates messages
// and handshake message for the P2P communication.
public class MessageFactory 
{
    public static byte[] genHandshakeMessage(int peerId) throws IOException
    {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        String handshakeHeader = "P2PFILESHARINGPROJ";

        byte[] zeroBits = new byte[10];

        ByteBuffer peerIdBuffer = ByteBuffer.allocate(4);
        peerIdBuffer.putInt(peerId);
        byte[] peerIdBytes = peerIdBuffer.array();

        outputStream.write(handshakeHeader.getBytes());
        outputStream.write(zeroBits);
        outputStream.write(peerIdBytes);

        outputStream.close();

        return outputStream.toByteArray();
    }

    public byte[] genChokeMessage() throws IOException 
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        byte[] messageType = new byte[1];
        messageType[0] = 0;

        byte[] messageLength;
        ByteBuffer messageLengthBuffer = ByteBuffer.allocate(4);
        messageLengthBuffer.putInt(messageType.length);
        messageLength = messageLengthBuffer.array();
        
        outputStream.write(messageLength);
        outputStream.write(messageType);

        outputStream.close();

        return outputStream.toByteArray();
    }

    public byte[] genUnchokeMessage() throws IOException 
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        byte[] messageType = new byte[1];
        messageType[0] = 1;

        byte[] messageLength;
        ByteBuffer messageLengthBuffer = ByteBuffer.allocate(4);
        messageLengthBuffer.putInt(messageType.length);
        messageLength = messageLengthBuffer.array();
        
        outputStream.write(messageLength);
        outputStream.write(messageType);

        outputStream.close();

        return outputStream.toByteArray();
    }

    public static byte[] genInterestedMessage() throws IOException 
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        byte[] messageType = new byte[1];
        messageType[0] = 2;

        byte[] messageLength;
        ByteBuffer messageLengthBuffer = ByteBuffer.allocate(4);
        messageLengthBuffer.putInt(messageType.length);
        messageLength = messageLengthBuffer.array();
        
        outputStream.write(messageLength);
        outputStream.write(messageType);

        outputStream.close();

        return outputStream.toByteArray();
    }

    public static byte[] genUninterestedMessage() throws IOException 
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        byte[] messageType = new byte[1];
        messageType[0] = 3;

        byte[] messageLength;
        ByteBuffer messageLengthBuffer = ByteBuffer.allocate(4);
        messageLengthBuffer.putInt(messageType.length);
        messageLength = messageLengthBuffer.array();
        
        outputStream.write(messageLength);
        outputStream.write(messageType);

        outputStream.close();

        return outputStream.toByteArray();
    }

    public static byte[] genHaveMessage(int pieceIndex) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] messageType = new byte[1];
        messageType[0] = 4;

        byte[] messagePayload;
        ByteBuffer messagePayloadBuffer = ByteBuffer.allocate(4);
        messagePayloadBuffer.putInt(pieceIndex);
        messagePayload = messagePayloadBuffer.array();

        byte[] messageLength;
        ByteBuffer messageLengthBuffer = ByteBuffer.allocate(4);
        messageLengthBuffer.putInt(messageType.length + messagePayload.length);
        messageLength = messageLengthBuffer.array();

        outputStream.write(messageLength);
        outputStream.write(messageType);
        outputStream.write(messagePayload);

        return outputStream.toByteArray();
    }

    public static byte[] genBitfieldMessage(byte[] messagePayload) throws IOException 
    {
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        byte[] messageType = new byte[1];
        messageType[0] = 5;
        byte[] messageLength;
        ByteBuffer messageLengthBuffer = ByteBuffer.allocate(4);
        messageLengthBuffer.putInt(messageType.length + messagePayload.length);
        messageLength = messageLengthBuffer.array();
        

        outputStream.write(messageLength);
        outputStream.write(messageType);
        outputStream.write(messagePayload);

        outputStream.close();

        return outputStream.toByteArray();
    }

    public byte[] genRequestMessage(int pieceIndex) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] messageType = new byte[1];
        messageType[0] = 6;

        byte[] messagePayload;
        ByteBuffer messagePayloadBuffer = ByteBuffer.allocate(4);
        messagePayloadBuffer.putInt(pieceIndex);
        messagePayload = messagePayloadBuffer.array();

        byte[] messageLength;
        ByteBuffer messageLengthBuffer = ByteBuffer.allocate(4);
        messageLengthBuffer.putInt(messageType.length + messagePayload.length);
        messageLength = messageLengthBuffer.array();

        outputStream.write(messageLength);
        outputStream.write(messageType);
        outputStream.write(messagePayload);

        return outputStream.toByteArray();
    }

    public byte[] pieceMessage(int pieceIndex, byte[] pieceContent) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] messageType = new byte[1];
        messageType[0] = 7;

        byte[] _pieceIndex;
        ByteBuffer pieceIndexBuffer = ByteBuffer.allocate(4);
        pieceIndexBuffer.putInt(pieceIndex);
        _pieceIndex = pieceIndexBuffer.array();

        byte[] messageLength;
        ByteBuffer messageLengthBuffer = ByteBuffer.allocate(4);
        messageLengthBuffer.putInt(messageType.length + _pieceIndex.length + pieceContent.length);
        messageLength = messageLengthBuffer.array();

        outputStream.write(messageLength);
        outputStream.write(messageType);
        outputStream.write(_pieceIndex);
        outputStream.write(pieceContent);

        return outputStream.toByteArray();
    }

    /* After handshaking, each peer can send a stream of actual messages. An actual message 
    consists  of  4-byte  message  length  field,  1-byte  message  type  field,  and  a  message 
    payload with variable size. The 4-byte message length specifies the message length in bytes. It does not include the 
    length of the message length field itself. The 1-byte message type field specifies the type of the message. 
    There are eight types of messages.  */
    public static byte[] decodeMessage(byte[] message) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // first, check the fierst 4 bytes to see how long payload is

        // then, check for what the message type is.
        if (message[4] < 8) {
            int _messageType = message[4];
            MessageType messageType = MessageType.valueOf(_messageType);

            if (messageType == MessageType.CHOKE) 
            {
                // choke message has no payload.
                // To choke those neighbors, peer A sends ‘choke’ messages to them 
                // and stop sending pieces.
            }
            else if (messageType == MessageType.UNCHOKE)
            {
                // unchoke message has no payload.
            }
            else if (messageType == MessageType.INTERESTED) 
            {
                // interested message has no payload.
            }
            else if (messageType == MessageType.NOT_INTERESTED) 
            {
                // not interested message has no payload.
            }
            else if (messageType == MessageType.HAVE) 
            {
                // ‘have’ messages have a payload that contains a 4-byte piece index field.  
            }
            else if (messageType == MessageType.BITFIELD) 
            {
            }
            else if (messageType == MessageType.REQUEST) 
            {
                /* ‘request’ messages have a payload which consists of a 4-byte piece index field. Note 
                that ‘request’ message payload defined here is different from that of BitTorrent. We don’t 
                divide a piece into smaller subpieces.  */
            }
            else if (messageType == MessageType.PIECE) 
            {
                /* ‘piece’ messages have a payload which consists of a 4-byte piece index field and the 
                content of the piece.  */
            }
            else 
            {
                System.out.println("Message not of valid type");
            }
        }
        else {
            // handshake
        }
        //outputStream.write(messageLength);
        //outputStream.write(_pieceIndex);

        return outputStream.toByteArray();
        
    }
}
