import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class Logger 
{
    public static boolean PRINT_LOGS = true;

    public static class LoggerUtil 
    {

        public static String idListToCsv(List<Integer> idList) {
            StringBuilder csv = new StringBuilder();

            for (int i = 0; i < idList.size() - 1; i++) {
                csv.append(Integer.toString(idList.get(i)) + ", ");
            }
            csv.append(Integer.toString(idList.get(idList.size() - 1)));

            return csv.toString();
        }

        public static void writeLog(FileWriter logger, String message) throws IOException {
            logger.write(message);
            if (PRINT_LOGS) {
                System.out.print(message);
            }
        }
    }
    
    public static void logTcpConnectionInitiated(int srcPeerId, int destPeerId) throws IOException {
        FileWriter logger = new FileWriter("./log_peer_" + Integer.toString(srcPeerId) + ".log", true);

        LoggerUtil.writeLog(logger, "[" + LocalDateTime.now() + "]: Peer [" 
            + Integer.toString(srcPeerId) + "] makes a connection to Peer ["
            + Integer.toString(destPeerId) + "].\n");
        logger.close();
    }

    public static void logTcpConnectionIncoming(int srcPeerId, int destPeerId) throws IOException {
        FileWriter logger = new FileWriter("./log_peer_" + Integer.toString(srcPeerId) + ".log",true);

        LoggerUtil.writeLog(logger, "[" + LocalDateTime.now() + "]: Peer [" 
            + Integer.toString(srcPeerId) + "] is connected from Peer ["
            + Integer.toString(destPeerId) + "].\n");
        logger.close();
    }

    public static void logChangePreferredNeighbors(int peerId, List<Integer> preferredNeighborIdList) throws IOException {
        FileWriter logger = new FileWriter("./log_peer_" + Integer.toString(peerId) + ".log", true);

        LoggerUtil.writeLog(logger, "[" + LocalDateTime.now() + "]: Peer [" 
            + Integer.toString(peerId) + "] has the preferred neighbors ["
            + LoggerUtil.idListToCsv(preferredNeighborIdList) + "].\n");
        logger.close();
    }

    public static void logChangeOptimisticallyUnchokedNeighbor(int srcPeerId, int neighborPeerId) throws IOException {
        FileWriter logger = new FileWriter("./log_peer_" + Integer.toString(srcPeerId) + ".log", true);

        LoggerUtil.writeLog(logger, "[" + LocalDateTime.now() + "]: Peer [" 
            + Integer.toString(srcPeerId) + "] has the optimistically unchoked neighbor ["
            + Integer.toString(neighborPeerId) + "].\n");
        logger.close();
    }

    public static void logUnchokedNeighbor(int srcPeerId, int neighborPeerId) throws IOException {
        FileWriter logger = new FileWriter("./log_peer_" + Integer.toString(srcPeerId) + ".log", true);

        LoggerUtil.writeLog(logger, "[" + LocalDateTime.now() + "]: Peer [" 
            + Integer.toString(srcPeerId) + "] is unchoked by ["
            + Integer.toString(neighborPeerId) + "].\n");
        logger.close();
    }

    public static void logChokeNeighbor(int srcPeerId, int neighborPeerId) throws IOException {
        FileWriter logger = new FileWriter("./log_peer_" + Integer.toString(srcPeerId) + ".log", true);

        LoggerUtil.writeLog(logger, "[" + LocalDateTime.now() + "]: Peer [" 
            + Integer.toString(srcPeerId) + "] is choked by ["
            + Integer.toString(neighborPeerId) + "].\n");
        logger.close();
    }

    public static void logReceiveHaveMessage(int srcPeerId, int destPeerId) throws IOException {
        FileWriter logger = new FileWriter("./log_peer_" + Integer.toString(srcPeerId) + ".log", true);

        LoggerUtil.writeLog(logger, "[" + LocalDateTime.now() + "]: Peer [" 
            + Integer.toString(srcPeerId) + "] received the ‘have’ message from ["
            + Integer.toString(destPeerId) + "].\n");
        logger.close();
    }

    public static void logReceiveInterestedMessage(int srcPeerId, int destPeerId) throws IOException {
        FileWriter logger = new FileWriter("./log_peer_" + Integer.toString(srcPeerId) + ".log", true);

        LoggerUtil.writeLog(logger, "[" + LocalDateTime.now() + "]: Peer [" 
            + Integer.toString(srcPeerId) + "] received the ‘interested’ message from ["
            + Integer.toString(destPeerId) + "].\n");
        logger.close();
    }

    public static void logReceiveNotInterestedMessage(int srcPeerId, int destPeerId) throws IOException {
        FileWriter logger = new FileWriter("./log_peer_" + Integer.toString(srcPeerId) + ".log", true);

        LoggerUtil.writeLog(logger, "[" + LocalDateTime.now() + "]: Peer [" 
            + Integer.toString(srcPeerId) + "] received the ‘not interested’ message from ["
            + Integer.toString(destPeerId) + "].\n");
        logger.close();
    }

    public static void logDownloadedPiece(int srcPeerId, int destPeerId, int pieceId, int pieceCount) throws IOException {
        FileWriter logger = new FileWriter("./log_peer_" + Integer.toString(srcPeerId) + ".log", true);

        LoggerUtil.writeLog(logger, "[" + LocalDateTime.now() + "]: Peer [" 
            + Integer.toString(srcPeerId) + "] has downloaded the piece ["
            + Integer.toString(pieceId) + "] from ["
            + Integer.toString(destPeerId) + "]. Now the number of pieces it has is ["
            + Integer.toString(pieceCount) + "].\n");
        logger.close();
    }

    public static void logDownloadComplete(int peerId) throws IOException {
        FileWriter logger = new FileWriter("./log_peer_" + Integer.toString(peerId) + ".log", true);

        LoggerUtil.writeLog(logger, "[" + LocalDateTime.now() + "]: Peer [" 
            + Integer.toString(peerId) + "] has downloaded the complete file.");
        logger.close();
    }
}
