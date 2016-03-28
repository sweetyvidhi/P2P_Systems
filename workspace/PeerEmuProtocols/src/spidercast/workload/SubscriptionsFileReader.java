/**
 * 
 */
package spidercast.workload;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import peeremu.config.Configuration;





/**
 * @author vinaysetty
 * 
 */
public class SubscriptionsFileReader implements Topics
{
  private BufferedReader br = null;
  private Vector<String> subscriptions;
  private static String PAR_FILE = "file";
  private HashMap<Integer, Integer> topicIdMap;


  public SubscriptionsFileReader(String prefix)
  {
    String fileName = Configuration.getString(prefix+"."+PAR_FILE);
    topicIdMap = new HashMap<Integer, Integer>();
    init(fileName);
  }



  private void openFile(String sFile)
  {
    try
    {
      FileReader fr = new FileReader(sFile);
      br = new BufferedReader(fr);
    }
    catch (IOException e)
    {
      // catch possible io errors from readLine()
      System.out.println("IOException error");
      e.printStackTrace();
    }
  }



  @Override
  public Object getTopics(int nodeIndex)
  {
    Vector<Integer> topicMap = new Vector<Integer>();
    String subscription = subscriptions.get(nodeIndex);
    // First token is the subscriber id so we ignore it
    String[] tokens = subscription.split(",");
    for(int i = 0 ; i < tokens.length ; ++i)
    {
      topicMap.add(Integer.parseInt(tokens[i]));
    }
    return topicMap;
  }

  public Boolean[][] getTopicMatrix(int numNodes)
  {
    int uniqueid = 0;
    Boolean[][] topicMatrix = new Boolean[numNodes][topicIdMap.size()];
    for(int i = 0 ; i < numNodes ; ++i)
    {
      String subscription = subscriptions.get(i);
      String[] tokens = subscription.split(",");
      for(int j = 0 ; j < tokens.length ; ++j)
      {
        int topic = Integer.parseInt(tokens[j]);
        if(topicIdMap.containsKey(topic) == false)
        {
          topicIdMap.put(topic, uniqueid++);
        }
        int topicIndex = topicIdMap.get(topic);
        topicMatrix[i][topicIndex] = true;
      }
    }
    return topicMatrix;
  }

  /*
   * 
   * numTopics doesn't make sense here
   */
  public void init(String fileName)
  {
    if (br==null)
      openFile(fileName);
    // Do it only once just in case we already initialized it then just return
    if (subscriptions!=null)
      return;
    String line;
    subscriptions = new Vector<String>();
    try
    {
      while ((line = br.readLine())!=null)
      {
        subscriptions.add(line);
      }
      br.close();
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
