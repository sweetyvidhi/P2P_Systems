package mehyar2;

import peeremu.core.Descriptor;

public class Message
{
  public enum Type
  {
    RQST,
    RESP,
    RESP_ACK;
  }
  public Type type;
  public double value;
  public int round;
  public Descriptor sender;
}
