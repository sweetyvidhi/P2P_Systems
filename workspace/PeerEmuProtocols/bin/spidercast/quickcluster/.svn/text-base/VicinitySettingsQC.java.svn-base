/*
 * Created on Jun 28, 2011 by Spyros Voulgaris
 */
package spidercast.quickcluster;

import gossip.comparator.DescriptorComparator;
import gossip.descriptor.DescriptorAge;
import gossip.vicinity.VicinitySettings;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import peeremu.core.CommonState;
import peeremu.core.Descriptor;





public class VicinitySettingsQC extends VicinitySettings
{
  DescriptorComparator proxCmp;
  Comparator<Descriptor> gossipCmp;


  public VicinitySettingsQC(String prefix)
  {
    super(prefix);
    gossipCmp = new NewestFirst();
    //proxCmp =  (DescriptorComparator) Configuration.getInstance(prefix + "." + PAR_PROX_CPM);
    duplCmp = new OldestFirst();
  }



  public void setProxCmp(DescriptorComparator proxCmp)
  {
    this.proxCmp = proxCmp;
  }



  public Vector<Descriptor> selectProximal(Descriptor ref, Vector<Descriptor> pool, Vector<Descriptor> exclude, int howmany)
  {
    Vector<Descriptor> selected = new Vector<Descriptor>(howmany+1);

    // If we can fit the whole pool, no need to sort it.
    if (howmany<pool.size())
    {
      Collections.shuffle(pool, CommonState.r);
      proxCmp.setReference(ref);
      Collections.sort(pool, proxCmp);
    }

    // And now select the first 'howmany' from the sorted list,
    // excluding duplicates and descriptors in the 'exclude' list.
    for (Descriptor d: pool)
    {
      if (ref.equals(d))
        continue;

      assert (!selected.contains(d)): "selectProximal() expects a duplicate-free Descriptor list!!";

      if (exclude!=null&&exclude.contains(d))
        continue;
      try
      {
        selected.add((Descriptor) d.clone());
      }
      catch (CloneNotSupportedException e)
      {
        e.printStackTrace();
      }
      if (--howmany==0)
        break;
    }
    return selected;
  }



  @Override
  public Descriptor selectToGossip(Vector<Descriptor> pool, boolean remove)
  {
    if (pool.size()==0)
      return null;
    // XXX: make it more efficient: Just select the neighbor of HIGHEST age.
    Collections.shuffle(pool, CommonState.r);
    Collections.sort(pool, gossipCmp);
    if (remove)
      return pool.remove(pool.size()-1);
    else
      return pool.lastElement();
    // int r = CommonState.r.nextInt(pool.size());
    // return pool.remove(r);
  }



  public class NewestFirst implements Comparator<Descriptor>
  {
    @Override
    public int compare(Descriptor a, Descriptor b)
    {
      return ((DescriptorAge) a).getAge()-((DescriptorAge) b).getAge();
    }
  }

  public class OldestFirst implements Comparator<Descriptor>
  {
//    public int compare(Descriptor a, Descriptor b)
//    {
//      return -(((DescriptorAge) a).getAge()-((DescriptorAge) b).getAge());
//    }

//    public int compare(Descriptor a, Descriptor b)
//    {
//      return -(((DescriptorQC) a).getVersion()-((DescriptorQC) b).getVersion());
//    }


    public int compare(Descriptor a, Descriptor b)
    {
      int verA = ((DescriptorQC) a).getVersion();
      int verB = ((DescriptorQC) b).getVersion();

      if (verA > verB)       // ==> remove B
        return -1;
      else if (verA < verB)  // ==> remove A
        return +1;
      else //  verA==verB ==> check ages
      {
        int ageA = ((DescriptorAge) a).getAge();
        int ageB = ((DescriptorAge) b).getAge();
//        int ageDiff = -(((DescriptorAge) a).getAge()-((DescriptorAge) b).getAge());
//        System.err.println("ageDiff = "+ageDiff+" ageA="+ageA+" ageB="+ageB);
        if (ageA > ageB)  // ==> remove B
          return -1;
        else              // ==> remove A
          return +1;
      }
    }
  }
}
