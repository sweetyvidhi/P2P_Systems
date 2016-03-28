package thesis.two;

import peeremu.core.Descriptor;

public class Suspicion
{
    public Descriptor desc;
    public double suspvalue;
    public long  lastmsgtime;
    public long esttime;
    public boolean first;
    public double initval;
    public double value;
    public enum State
    {
      ALIVE,
      DEADFIRST,
      DEAD
    }
    public State state;
    public int version;
}
