import java.io.*;
import java.util.ArrayList;
import java.util.Random;


/*
Miles Ring
CSCI4450
Dasgupta Fall 2017

Problem 2. K-means programming							(10 points)
Our objective in this question is to verify the effect of choosing the initial number of clusters,
given by parameter K, in the K-means algorithm.
For this, first implement the K-means algorithm.
To verify the effect of K, download the crime data set available on Blackboard.
The k-values you should use are: k = 2, 3, 4, 5, 6, 7, 8, 9 and 10.
For each value of k, write down the value of distortion
and the corresponding run-time. Put your results in a text file named q2.txt.
Source code of your K-means implementation and the data set should be provided in the homework submission directory too.
Your code will be tested.
Zero points (no partial credits) on this question if your K-means code does not compile or run.


 */
public class csci4450assign3Q2 {
    ArrayList<DataNode> dataPoints;
    ArrayList<Cluster> clusters;
    int iterations;
    Double[] mins;
    Double[] maxs;
    Random rand;
    ArrayList<String> toBeWritten = new ArrayList<>();


    public csci4450assign3Q2(){
        dataPoints = new ArrayList<>();
        clusters = new ArrayList<>();
        rand = new Random();

        mins = new Double[4];
        maxs = new Double[4];
        //default the mins and maxs
        for(int i=0;i<mins.length;++i){
            mins[i] = (double)Integer.MAX_VALUE;
            maxs[i] = 0.0;
        }

        //read in file given from blackboard
        ReadCSV();

        //find mins and maxes in data given
        for(DataNode node : dataPoints){
            for(int i=0;i<node.values.length;++i){
                if(node.values[i] < mins[i]){
                    mins[i] = node.values[i];
                }
                if(node.values[i] > maxs[i]){
                    maxs[i] = node.values[i];
                }
            }
        }


        //iterate k =2,3,4,5,6,7,8,9,10
        for(int k=2;k<11;k++) {
            iterations = 0;
            long startTime = System.nanoTime();
            clusters = KMeans(k);
            long endTime = System.nanoTime();
            long totalTime = (endTime-startTime);
            toBeWritten.add("K: "+k);
            //System.out.println("K: "+k);
            toBeWritten.add("Time taken: " + totalTime +" ns");
            //System.out.println("Time taken: " + totalTime +" ns");
            double totalDistortion = 0;
            for(DataNode node: dataPoints){
                totalDistortion+= node.distortion;
            }
            totalDistortion/=dataPoints.size();
            toBeWritten.add("Distortion: "+totalDistortion);
            //System.out.println("Distortion: "+totalDistortion);

        }

        WriteToFile();
    }


    /**
     * Reads in CSV file to get data points
     *
     */
    private void ReadCSV(){
        String csvFile = "hw3-crime_data.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            //throw away first line of column titles
            br.readLine();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] state = line.split(cvsSplitBy);

                DataNode node = new DataNode(
                        state[0],
                        Double.parseDouble(state[2]),
                        Integer.parseInt(state[3]),
                        Integer.parseInt(state[4]),
                        Double.parseDouble(state[5]));
                dataPoints.add(node);


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void WriteToFile(){
        BufferedWriter writer = null;
        try {
            File logFile = new File("q2.txt");

            writer = new BufferedWriter(new FileWriter(logFile));
            for(String str : toBeWritten){
                writer.write(str);
                writer.write("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {

            }
        }


    }
    private ArrayList<Cluster> KMeans(int k){
        ArrayList<Cluster> newClusters = new ArrayList<>();
        boolean hasChanged;

        //randomize new clusters
        for(int i=0; i<k; ++i){
            double randMurder = mins[0]+(maxs[0] - mins[0]) * rand.nextDouble();
            double randAssault = mins[1]+(maxs[1] - mins[1]) * rand.nextDouble();
            double randPop = mins[2]+(maxs[2] - mins[2]) * rand.nextDouble();
            double randRape = mins[3]+(maxs[3] - mins[3]) * rand.nextDouble();
            DataNode temp = new DataNode(
                    "",
                    randMurder,
                    randAssault,
                    randPop,
                    randRape);
            Cluster cluster = new Cluster();
            cluster.mean = temp.values;
            newClusters.add(cluster);
        }

        while(true){
            //increase iteration
            iterations++;
            //set loop end condition
            hasChanged = false;

            //run through each datanode in the datapoints list
            for(DataNode node : dataPoints) {
                //distance from nearest cluster to node
                double clusterDistance = Double.MAX_VALUE;
                Cluster nearest = null;
                Cluster alreadyIn = null;

                for(Cluster cluster : newClusters){
                    //distance of cluster to datanode
                    double distance = calcEuclid(new DataNode(
                            "",
                            cluster.mean[0],
                            cluster.mean[1],
                            cluster.mean[2],
                            cluster.mean[3]), node);

                    //checks if node is already in cluster
                    if(cluster.contains(node)){
                        alreadyIn = cluster;
                    }

                    //smallest distance so far
                    if(distance < clusterDistance){
                        nearest = cluster;
                        clusterDistance = distance;
                        node.distortion = distance;
                    }


                }
                //data node is not already in cluster
                if(alreadyIn != nearest){
                    //check in case node wasn't in cluster at all
                    if(alreadyIn != null) {
                        //remove datanode from current cluster
                        alreadyIn.removeData(node);
                    }

                    nearest.addData(node);
                    //break iteration upon loop exit
                    hasChanged = true;
                }
            }
            if(!hasChanged){
                break;
            }

            //move the cluster centers
            for(Cluster cluster : newClusters){
                cluster.computeMean();
            }

        }
        return newClusters;
    }

    /**
     * Calculates distance between nodes
     *
     * @param n1, first datanode
     * @param n2, second datanode
     * @return double, euclidian distance between nodes
     */
    private double calcEuclid(DataNode n1, DataNode n2){
        double sum=0;

        for(int i=0;i<n1.values.length; ++i){
            sum+=Math.pow(n1.values[i]-n2.values[i],2);
        }

        return Math.sqrt(sum);
    }

}


/**
 * contains all info for datapoint
 *
 */
class DataNode {
    String state;
    double murder;
    double assault;
    double urbanPop;
    double rape;
    double[] values;

    double distortion;

    public DataNode(String st, double mrdr, double ass, double urb, double rape) {
        state = st;
        murder = mrdr;
        assault = ass;
        urbanPop = urb;
        this.rape = rape;
        values = new double[]{murder, assault, urbanPop, this.rape};
        distortion = 0;
    }



}


class Cluster{
        ArrayList<DataNode> dataNodes;
        double[] mean;
        double[] sum;

        public Cluster(){
            dataNodes = new ArrayList<>();
            mean = new double[4];
            sum = new double[4];
        }

        public void addData(DataNode data){
            dataNodes.add(data);
            for(int i=0;i<data.values.length; ++i){
                sum[i]+=data.values[i];
            }
        }

        public void removeData(DataNode node){
            dataNodes.remove(node);
            for(int i=0;i<node.values.length;++i){
                sum[i]-=node.values[i];
            }
        }

        public void computeMean(){
            for(int i=0; i<sum.length; ++i){
                mean[i] = sum[i] / dataNodes.size();
            }
        }

        public boolean contains(DataNode node){
            return dataNodes.contains(node);
        }
}

