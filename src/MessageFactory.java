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
}
