/**
 * 
 */
package spidercast.workload;


/**
 * @author vinaysetty
 *
 */
public class InterestGenerator
{
  
  public  static boolean[][] zipfInterestMatrix(int nodeNumber,
      int topicNumber, double zipf, int min, int max, double alpha) {
    /**
     * nodeNumber and topicNumber is the length of the matrix
     * zipf is alpha value of the zip distribution,
     * zipf<0.00001 means uniform distribution
     * min and max is the interval of topic number for each node
     * alpha is the parameter for zipf
     */
    boolean[][] interest =new boolean[nodeNumber][topicNumber];
    Dist d=new Dist(max-min);
    Dist t_d=new Dist(topicNumber-1);
    for(int v=0;v<nodeNumber;v++){
      int T_n;
      if(Math.abs(zipf)>0.00001)
        T_n=min+d.zipf(zipf);//generateZip
      else
        T_n=min+d.uniform();
      for(int i=0; i<T_n; i++){
        int t;
        do{
          t=t_d.zipf(alpha);//Random distribution;
        }while(interest[v][t]);
          interest[v][t]=true;
      }
    }
    return interest;
  }
  
  

  public  static boolean[][] uniformInterestMatrix(int nodeNumber,
      int topicNumber, int min, int max) {
    /**
     * nodeNumber and topicNumber is the length of the matrix
     * zipf is alpha value of the zip distribution,
     * zipf<0.00001 means uniform distribution
     * min and max is the interval of topic number for each node
     */
    boolean[][] interest =new boolean[nodeNumber][topicNumber];
    Dist d=new Dist(max-min);
    Dist t_d=new Dist(topicNumber-1);
    for(int v=0;v<nodeNumber;v++){
      int T_n;
     
        T_n=min+d.uniform();
      for(int i=0; i<T_n; i++){
        int t;
        do{
          t=t_d.uniform();//Random distribution;
        }while(interest[v][t]);
          interest[v][t]=true;
      }
    }
    return interest;
  }
  
//  public  boolean[][] uniformInterestMatrix(int nodeNumber,
//      int topicNumber, double zipf, int min, int max) {
//    /**
//     * nodeNumber and topicNumber is the length of the matrix
//     * zipf is alpha value of the zip distribution,
//     * zipf<0.00001 means uniform distribution
//     * min and max is the interval of topic number for each node
//     */
//    boolean[][] interest =new boolean[nodeNumber][topicNumber];
//    Dist d=new Dist(max-min);
//    Dist t_d=new Dist(topicNumber);
//    for(int v=0;v<nodeNumber;v++){
//      int T_n;
//      if(Math.abs(zipf)>0.00001)
//        T_n=min+d.zipf(zipf);//generateZip
//      else
//        T_n=min+d.uniform();
//      for(int i=0; i<=T_n; i++){  
//        int t;
//        do{
//          t=t_d.uniform();//Random distribution;
//        }while(interest[v][t-1]);
//          interest[v][t-1]=true;
//      }
//    }
//    return interest;
//  }

  public  static boolean[][] exponInterestMatrix(int nodeNumber,
      int topicNumber, double zipf, int min, int max) {
    /**
     * nodeNumber and topicNumber is the length of the matrix
     * zipf is alpha value of the zip distribution,
     * zipf<0.00001 means uniform distribution
     * min and max is the interval of topic number for each node
     */
    boolean[][] interest =new boolean[nodeNumber][topicNumber];
    Dist d=new Dist(max-min);
    Dist t_d=new Dist(topicNumber-1);
    for(int v=0;v<nodeNumber;v++){
      int T_n;
      if(Math.abs(zipf)>0.00001)
        T_n=min+d.zipf(zipf); //generateZip
      else
        T_n=min+d.uniform();
      for(int i=0; i<T_n; i++){
        int t;
        do{
          t=t_d.expon(); //Random distribution;
        }while(interest[v][t]);
          interest[v][t]=true;
      }
    }
    return interest;
  }
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    boolean [][] interest = InterestGenerator.uniformInterestMatrix(1000, 5, 25, 25);
    for(int i = 0 ; i < interest.length ; ++i)
    {
       for(int j = 0 ; j < interest[i].length ; ++j)
         System.out.print(interest[i][j] + " ");
       System.out.println("");
    }
  }
}
