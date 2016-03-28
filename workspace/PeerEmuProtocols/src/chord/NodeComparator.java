package chord;

import java.util.Comparator;
import java.math.*;
import peeremu.core.*;

public class NodeComparator implements Comparator {

  public int pid = 0;

  public NodeComparator(int pid) {
    this.pid = pid;
  }

  public int compare(Object arg0, Object arg1) {
    int one = ((ChordProtocol) ((Node) arg0).getProtocol(pid)).chordId;
    int two = ((ChordProtocol) ((Node) arg1).getProtocol(pid)).chordId;
    if(one < two)
      return -1;
    else if(one>two)
      return 1;
    else
      return 0;
  }

}
