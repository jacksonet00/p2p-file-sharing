import java.util.HashMap;
import java.util.Map;

// this is an enum so we can store
// the data type names as actual data.
public enum MessageType 
{
    CHOKE(0),
    UNCHOKE(1),
    INTERESTED(2),
    NOT_INTERESTED(3),
    HAVE(4),
    BITFIELD(5),
    REQUEST(6),
    PIECE(7);

    private int value;

    private MessageType(int _value) 
    {
        value = _value;
    }

    private static Map<Integer, MessageType> map = new HashMap<Integer, MessageType>();

    static 
    {
        for (MessageType MessageType : MessageType.values()) 
        {
            map.put(MessageType.value, MessageType);
        }
    }

    public static MessageType valueOf(int messageType) 
    {
        return (MessageType) map.get(messageType);
    }

    public int getValue() 
    {
        return value;
    }
}