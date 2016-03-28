package chord;

public class IdSuccessorQueryMessage
{
  public ChordDescriptor sender;
  public int id;
  public enum Type 
  {
    FIX_FINGERS,
    GENERAL;
  }
  public Type msgType;
  
}
