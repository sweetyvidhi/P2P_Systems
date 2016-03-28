package thesis.three;

import java.util.LinkedList;
import java.util.Queue;

import peeremu.core.Descriptor;

public class Suspicion
{
    public Descriptor desc;
    public boolean first=true;
    public boolean second=true;
    public double initval=-1;
    public double value=-1;
    public enum State
    {
      ALIVE,
      DEADFIRST,
      DEAD
    }
    public State state=State.ALIVE;
    public int version;
    public Queue<Double> window = new LinkedList<Double>();
    public double sum=0.0;
    public double sum_variance=0.0;
    public double mean=0.0;
    public double variance=0.0;
    public int num=0;  
    public long  lastmsgtime=0;
    public long esttime=0;
    public double suspvalue=0;
}
