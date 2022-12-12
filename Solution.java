public class Solution{


    public static double getEntropy(int nCat1, int nCat2){
        if(nCat1 == 0 || nCat2 == 0)
            return 0.0;
        double pCat1 = (double)nCat1 / (nCat1 + nCat2);
        double pCat2 = (double)nCat2 / (nCat1 + nCat2);
        return -((pCat1) * (Math.log(pCat1) / Math.log(2))) - ((pCat2) * (Math.log(pCat2) / Math.log(2)));
    }

    /*
    public static double infoGain(int[] left, int leftSize, int[] right, int rightSize, int parentSize){
        double lWeight = (double) leftSize / parentSize;
        double rWeight = (double) rightSize / parentSize;

        int lTrue = 0;
        int lFalse = 0;
        for(int i = 0; i < leftSize; i++)
        {
            if()
        }
    }
*/
    public static int[] getBestSeparation(int[][] features, boolean[] labels){
        int[] answer = {0,0};
        double bestEntropy = -1;
        for(int i = 0; i < features.length; i++)
        {
            for(int j = 0; j < features[i].length; j++)
            {
                int left[] = new int[features.length];
                int leftSize = 0;
                int leftTrue = 0;
                int leftFalse = 0;

                int right[] = new int[features.length];
                int rightSize = 0;
                int rightTrue = 0;
                int rightFalse = 0;

                for(int k = 0; k < features.length; k++) {
                    //left
                    if (features[k][j] < features[i][j]) {
                        left[leftSize] = features[k][j];
                        leftSize++;
                        if (labels[k]) {
                            leftTrue++;
                        } else {
                            leftFalse++;
                        }
                    }

                    //right
                    if (features[k][j] >= features[i][j]) {
                        right[leftSize] = features[k][j];
                        rightSize++;
                        if (labels[k]) {
                            rightTrue++;
                        } else {
                            rightFalse++;
                        }
                    }
                }

                if(rightSize == 0 && leftSize == 0)
                    break;

                double entropy = getEntropy(leftTrue, leftFalse) * ((double)leftSize / (leftSize + rightSize)) + getEntropy(rightTrue, rightFalse) * ((double)rightSize / (rightSize + leftSize));
                if(entropy < bestEntropy || bestEntropy == -1)
                {
                    bestEntropy = entropy;
                    answer = new int[]{j, features[i][j]};
                }
            }
        }

        return answer;
    }

    public static void main(String[] args){

    }
}