import java.util.HashMap;
import java.util.Map;

public enum MessageType 
{
    choke(0),
    unchoke(1),
    interested(2),
    not_interested(3),
    have(4),
    bitfield(5),
    request(6),
    piece(7);

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