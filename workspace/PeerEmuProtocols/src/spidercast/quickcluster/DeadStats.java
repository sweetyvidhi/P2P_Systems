/*
 * Copyright (c) 2003-2005 The BISON Project
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2 as published by
 * the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package spidercast.quickcluster;

import gossip.descriptor.DescriptorSimAge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import peeremu.config.Configuration;
import peeremu.core.CommonState;
import peeremu.core.Control;
import peeremu.core.Linkable;
import peeremu.core.Network;
import peeremu.core.Node;
import peeremu.util.IncrementalFreq;





/**
 * Prints several statistics about the node degrees in the graph.
 */
public class DeadStats implements Control
{
  // --------------------------------------------------------------------------
  // Parameter
  // --------------------------------------------------------------------------
  private int pid;



  // --------------------------------------------------------------------------
  // Initialization
  // --------------------------------------------------------------------------
  /**
   * Standard constructor that reads the configuration parameters. Invoked by
   * the simulation engine.
   * 
   * @param name the configuration prefix for this class
   */
  public DeadStats(String name)
  {
    pid = Configuration.getPid(name+"."+PAR_PROTOCOL);
  }



  /**
   * Prints statistics about node degree. The format of the output is specified
   * by {@value #PAR_METHOD}. See also the rest of the configuration parameters.
   * 
   * @return always false
   */

  public static ArrayList<Integer> monitoredNodes = new ArrayList<Integer>(Arrays.asList(106,442,909));

  public boolean execute()
  {
    IncrementalFreq stats = new IncrementalFreq();
    HashMap<Integer, Collection<Node>> bag = new HashMap();
    for (int i = 0; i<Network.size(); i++)
    {
      Node node = Network.get(i);
      if (node.isUp())
      {
        Linkable l = (Linkable) node.getProtocol(pid);
        for (int j=l.degree()-1; j>=0; j--)
        {
          DescriptorSimAge peerDescr = (DescriptorSimAge) l.getNeighbor(j);
          Node peer = peerDescr.getNode();
          if (!peer.isUp())
            stats.add((int) peer.getID());

          if (monitoredNodes.contains((int)peer.getID()))
          {
            Collection<Node> nodesPointingAtThisDeadNode = bag.get((int)peer.getID());
            if (nodesPointingAtThisDeadNode == null)
            {
              nodesPointingAtThisDeadNode = new Vector<Node>();
              bag.put((int)peer.getID(), nodesPointingAtThisDeadNode);
            }
            nodesPointingAtThisDeadNode.add(node);
          }
        }
      }
    }
//    System.out.println("\nDistinct dead links at "+CommonState.getTime());
//    stats.print(System.out);
    System.out.println("Total dead links at "+CommonState.getTime()+": "+stats.getN()+"\n");
    
    for (Map.Entry<Integer, Collection<Node>> entry: bag.entrySet())
    {
      System.out.print("Node "+entry.getKey()+"-"+(Network.get(entry.getKey()).isUp()?"U":"D")+" known by");
      for (Node nodePointingAtThisDeadNode: entry.getValue())
        System.out.print(" "+nodePointingAtThisDeadNode.getID()+"-"+(nodePointingAtThisDeadNode.isUp()?"U":"D"));
      System.out.println();
    }
      
    return false;
  }
}
