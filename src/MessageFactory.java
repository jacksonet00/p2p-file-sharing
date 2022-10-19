import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageFactory {
    public static byte[] genHandshakeMessage(int peerId) throws IOException {

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

    public byte[] genChokeMessage() throws IOException {
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

    public byte[] genUnchokeMessage() throws IOException {
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

    public byte[] genInterestedMessage() throws IOException {
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

    public byte[] genUninterestedMessage() throws IOException {
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

    public byte[] genHaveMessage(int pieceIndex) throws IOException{
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

    public static byte[] genBitfieldMessage(byte[] messagePayload) throws IOException {
        
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

    public byte[] genRequestMessage(int pieceIndex) throws IOException{
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

    public byte[] pieceMessage(int pieceIndex, byte[] pieceContent) throws IOException{
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
}
