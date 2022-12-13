public class Solution{

    static final int MAX_DEPTH = 10;

    public class Node{
        public int index;
        public double threshold;
        public Node left;
        public Node right;
        public double infoGain;
        public double value;

        public Node(int index, double threshold, Node left, Node right, double infoGain, double value) {
            this.index = index;
            this.threshold = threshold;
            this.left = left;
            this.right = right;
            this.infoGain = infoGain;
            this.value = value;
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
            if (data[k][idx] < threshold) {
                leftSize++;
                if (labels[k]) {
                    leftTrue++;
                } else {
                    leftFalse++;
                }
            }

            //right
            if (data[k][idx] >= threshold) {
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
/*
    public static Node buildTree(int[][] dataset, int depth){
        if(depth >= MAX_DEPTH)
        {

        }


    }
*/
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

    public static void main(String[] args){

    }
}