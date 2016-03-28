package thesis.two;

import peeremu.core.Descriptor;
public class Message
{
  Message()
  {
    
  }
  Message(Type t)
  {
    type = t;
  }
  public enum Type
  {
    BCAST,
    GOSSIP,
    CALC,
    CLEANUP
  }
  private Type type;
  public Descriptor desc;
  public Type getType()
  {
    return type;
  }
  public void setType(Type type)
  {
    this.type = type;
  }
}
