import java.io.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution{

    static final int MAX_DEPTH = -1; // USE -1 TO IGNORE MAX DEPTH
    private static final String COMMA_DELIMITER = ",";

    private static Node root;

    public static class Node{
        public int index;
        public double threshold;
        public Node left;
        public Node right;
        public double infoGain;
        public boolean value;

        public Node(int index, double threshold, Node left, Node right, double infoGain) {
            this.index = index;
            this.threshold = threshold;
            this.left = left;
            this.right = right;
            this.infoGain = infoGain;
        }

        public Node(boolean value){
            this.value = value;
            right = null;
            left = null;
        }

        public String toString(){
            if(isLeaf())
            {
                return "LEAF: \t value = " + value;
            }
            return "NODE: \t index = " + index + "\t\t threshold = " + threshold + "\t\t infogain = " + infoGain;
        }

        public boolean isLeaf(){
            return left == null && right == null;
        }

        public void print(StringBuilder buffer, String prefix, String childrenPrefix, int depth) {
            buffer.append(prefix);
            buffer.append(this);
            buffer.append('\n');

            if(depth == 0)
                return;

            if(!isLeaf())
            {
                left.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ", depth - 1);
                right.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ", depth - 1);
            }

        }
    }


    public static double getEntropy(int nCat1, int nCat2){
        if(nCat1 == 0 || nCat2 == 0)
            return 0.0;
        double pCat1 = (double)nCat1 / (nCat1 + nCat2);
        double pCat2 = (double)nCat2 / (nCat1 + nCat2);
        return -((pCat1) * (Math.log(pCat1) / Math.log(2))) - ((pCat2) * (Math.log(pCat2) / Math.log(2)));
    }


    public static int[] getBestSeparation(int[][] features, boolean[] labels){
        int[] answer = {0,0};
        double bestInfoGain = -1;
        for(int i = 0; i < features.length; i++) //sorok iteralasa := i
        {
            for(int j = 0; j < features[i].length; j++) //oszlopok iteralasa := j
            {
                double infoGain = informationGain(features, j, labels, features[i][j]);
                if(infoGain > bestInfoGain)
                {
                    bestInfoGain = infoGain;
                    answer = new int[]{j, features[i][j]};
                }
            }
        }
        lastSeparationInfoGain = bestInfoGain;
        return answer;
    }

    public static double informationGain(int[][] data, int idx, boolean[] labels, int threshold){

        int leftSize = 0;
        int leftTrue = 0;
        int leftFalse = 0;

        int rightSize = 0;
        int rightTrue = 0;
        int rightFalse = 0;

        for(int k = 0; k < data.length; k++) {
            //left
            if (data[k][idx] <= threshold) {
                leftSize++;
                if (labels[k]) {
                    leftTrue++;
                } else {
                    leftFalse++;
                }
            }

            //right
            if (data[k][idx] > threshold) {
                rightSize++;
                if (labels[k]) {
                    rightTrue++;
                } else {
                    rightFalse++;
                }
            }
        }

        double infoGain = getEntropy(leftFalse + rightFalse, leftTrue + rightTrue) - (getEntropy(leftTrue, leftFalse) * ((double)leftSize / (leftSize + rightSize)) + getEntropy(rightTrue, rightFalse) * ((double)rightSize / (rightSize + leftSize)));

        if(rightSize == 0 && leftSize == 0)
            infoGain = 0;

        return infoGain;
    }

    public static double lastSeparationInfoGain = 0;
    public static Node buildTree(int[][] dataset, boolean[] labels, int depth){
        if(depth < MAX_DEPTH || MAX_DEPTH == -1)
        {
            int[] bestSeparation = getBestSeparation(dataset, labels);
            if(lastSeparationInfoGain > 0)
            {
                //Separate data to list
                List<int[]> left = new ArrayList<>();
                List<int[]> right = new ArrayList<>();
                List<Boolean> leftLabelsList = new ArrayList<>();
                List<Boolean> rightLabelsList = new ArrayList<>();
                for(int i = 0; i < dataset.length; i++)
                {
                    if(dataset[i][bestSeparation[0]] <= bestSeparation[1])
                    {
                        leftLabelsList.add(labels[i]);
                        left.add(dataset[i]);
                    }
                    else
                    {
                        rightLabelsList.add(labels[i]);
                        right.add(dataset[i]);
                    }
                }
                //Convert list to array
                int[][] leftDataSet = new int[left.size()][left.get(0).length];
                left.toArray(leftDataSet);

                boolean[] leftLabels = new boolean[leftLabelsList.size()];
                for(int i = 0; i < leftLabelsList.size(); i++)
                {
                    leftLabels[i] = Boolean.TRUE.equals(leftLabelsList.get(i));
                }

                int[][] rightDataSet = new int[right.size()][right.get(0).length];
                right.toArray(rightDataSet);

                boolean[] rightLabels = new boolean[rightLabelsList.size()];
                for(int i = 0; i < rightLabelsList.size(); i++)
                {
                    rightLabels[i] = Boolean.TRUE.equals(rightLabelsList.get(i));
                }

                Node leftNode = buildTree(leftDataSet, leftLabels, depth + 1);
                Node rightNode = buildTree(rightDataSet, rightLabels, depth + 1);
                return new Node(bestSeparation[0], bestSeparation[1], leftNode, rightNode, lastSeparationInfoGain);

            }
        }

        return new Node(labels[0]); //Feltetelezve hogy csak 1 fele ertek lehet a labels-ben a 0 infogain miatt
    }

    public static boolean evaluate(int[] data, Node node){
        //Terminal case
        if(node.isLeaf())
            return node.value;

        //Select
        if(data[node.index] <= node.threshold)
        {
            return evaluate(data, node.left);
        }
        else
        {
            return evaluate(data, node.right);
        }
    }

    public void infoGainTest(){
        int leftFalse = 498;
        int leftTrue = 498;
        int leftSize = 996;

        int rightFalse = 492;
        int rightTrue = 572;
        int rightSize = 1064;

        double parentGain = getEntropy(leftFalse + rightFalse, leftTrue + rightTrue);
        double childGain = (getEntropy(leftTrue, leftFalse) * ((double)leftSize / (leftSize + rightSize)) + getEntropy(rightTrue, rightFalse) * ((double)rightSize / (rightSize + leftSize)));
        double infoGain = parentGain - childGain;
        System.out.println(parentGain);
        System.out.println(childGain);
        System.out.println(infoGain);
    }

    public static void testSeparation(){
        int[][] tabla = {{5}, {2}, {4}, {3},{0}, {1}};
        boolean[] bool = {true, true, true, true, true, true};
        int[] eredmeny = getBestSeparation(tabla, bool);
        System.out.println(eredmeny[0]);
        System.out.println(eredmeny[1]);
        System.out.println(lastSeparationInfoGain);
    }




    public static void main(String[] args) throws IOException {

        //Read
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("train.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Load
        int[][] features = new int[records.size()][records.get(0).size() - 2];
        boolean[] labels = new boolean[records.size()];
        for(int i = 0; i < records.size(); i++)
        {
            for(int j = 0; j < records.get(i).size() - 2; j++)
            {
                features[i][j] = Integer.parseInt(records.get(i).get(j));
            }

            int num = Integer.parseInt(records.get(i).get(records.get(i).size() - 1));
            if(num == 1)
            {
                labels[i] = true;
            }
            else
            {
                labels[i] = false;
            }
        }

        //Build tree
        root = buildTree(features, labels, 0);

/*
        StringBuilder buffer = new StringBuilder();
        root.print(buffer, "", "", 100000);
        System.out.println(buffer);

 */
        //Read
        List<List<String>> testRecords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("test.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                testRecords.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int resultsNumber = Math.min(testRecords.size(), testRecords.size());

        //Load
        int[][] testData = new int[resultsNumber][testRecords.get(0).size()];
        for(int i = 0; i < resultsNumber; i++)
        {
            for(int j = 0; j < testRecords.get(i).size(); j++)
            {
                testData[i][j] = Integer.parseInt(testRecords.get(i).get(j));
            }

        }

        //Evaluate
        int[] results = new int[testRecords.size()];
        for(int i = 0; i < resultsNumber; i++)
        {
            if(evaluate(testData[i],root))
                results[i] = 1;
            else
                results[i] = 0;
        }

        List<String[]> out = new ArrayList<>();
        for(int i = 0; i < results.length; i++)
        {
            out.add(new String[]{String.valueOf(results[i])});
        }

        CSVPrinter printer = new CSVPrinter();
        printer.writeToCsvFile(out, new File("results.csv"));


    }

    //Code stolen from here:
    //https://mkyong.com/java/how-to-export-data-to-csv-file-java/
    static class CSVPrinter {
        // if quote = true, all fields are enclosed in double quotes
        public String convertToCsvFormat(
                final String[] line) {

            return Stream.of(line)                              // convert String[] to stream
                    .map(l -> formatCsvField(l))                // format CSV field
                    .collect(Collectors.joining(COMMA_DELIMITER));    // join with a separator

        }

        // put your extra login here
        private String formatCsvField(final String field) {
            return field;
        }

        private void writeToCsvFile(List<String[]> list, File file) throws IOException {

            List<String> collect = list.stream()
                    .map(this::convertToCsvFormat)
                    .collect(Collectors.toList());

            // CSV is a normal text file, need a writer
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (String line : collect) {
                    bw.write(line);
                    bw.newLine();
                }
            }

        }
    }
}