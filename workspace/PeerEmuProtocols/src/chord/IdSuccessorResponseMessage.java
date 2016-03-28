package chord;

import chord.IdSuccessorQueryMessage.Type;

public class IdSuccessorResponseMessage
{
  public int id;
  public ChordDescriptor sender;
  public ChordDescriptor successor;
  public Type msgType;
}
