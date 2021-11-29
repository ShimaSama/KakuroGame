package Dominio;
import java.util.Scanner;

public class KakuroValidator {

    private static Kakuro k;
    private int n;
    private int m;

    public KakuroValidator(Kakuro k) {
        this.k = k;
        this.n = k.getAltura();
        this.m = k.getAnchura();
    }

    public int validate() {

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if(k.isBlanca(i,j) && k.getCellValue(i,j).equals("?")){
                    return 2;
                }
                if (k.isBlanca(i,j) && (Integer.parseInt(k.getCellValue(i,j)) > 9
                                || Integer.parseInt(k.getCellValue(i,j) ) < 1))
                    return 0;

            }
        }
        // begin validation
        // check if anything left blank
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (k.getCellValue(i,j).equals("?")) return 0;
            }
        }
        // check top left box
        if (k.isBlanca(0,0)) return 0;

        // check all sums and duplicates within sums
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j ++) {
                if (k.isSuma(i,j)) {
                    if (!checkSum(i, j) || !checkDuplicates(i, j)) return 0;
                }
            }
        }
        // if we are here it is validated
        return 1;
    }

    private boolean checkSum(int a, int b) {

        String F, C;
        F = C = null;
        String[] res = {};
        boolean hasRow = false;
        boolean hasCol = false;

        if (k.getCellValue(a,b).contains("C")) {
            hasCol = true;
            String split = k.getCellValue(a,b).split("C")[1];
            if(split.contains("F")) {
                hasRow = true;
                res  = split.split("F");
                F = res[1];
                C = res[0];
            }
            else C = split;
        }
        else {
            hasRow = true;
            F = k.getCellValue(a,b).split("F")[1];
        }
        int row, col;
        row = col = -1;
        if(hasRow) row = Integer.parseInt(F);
        if(hasCol) col = Integer.parseInt(C);

        // check row sum
        if(hasRow) {
            int rowSum = 0;
            if (b < m) {
                int t = b + 1;
                if (k.isBlanca(a,t)) {
                    rowSum = Integer.parseInt(k.getCellValue(a,t));
                    while (t < m-1 && k.isBlanca(a,t+1)){
                        rowSum += Integer.parseInt(k.getCellValue(a,t+1));
                        t++;
                    }
                }
            }
            else row = rowSum;
            if (row != rowSum) return false;
        }

        // check col sum
        if(hasCol) {
            int colSum = 0;
            if (a < n) {
                int l = a + 1;
                if (k.isBlanca(l,b)) {
                    colSum = Integer.parseInt(k.getCellValue(l,b));
                    while (l < n-1 && k.isBlanca(l+1,b)) {
                        colSum += Integer.parseInt(k.getCellValue(l+1,b));
                        l++;
                    }
                }
            } else col = colSum;
            if (col != colSum) return false;
        }
        return true;
    }

    private boolean checkDuplicates(int a, int b) {
        String rowSum = "";
        String colSum = "";
        int i = a;
        int j = b;
        int w = a;
        int v = b;

        if (k.isSuma(i,j)){
            while (k.isBlanca(i,j)) {
                rowSum += "" + k.getCellValue(i,j);
                j++;
            }
            while (k.isBlanca(w,v)) {
                colSum += "" + k.getCellValue(w,v);
                w++;
            }

            for (int z = 0; z < rowSum.length(); z++) {
                char currentTest = rowSum.charAt(z);
                for (int x = 0; x < rowSum.length(); x++) {
                    if (rowSum.charAt(x) == currentTest && x != z) return false;
                }
            }
            for (int z = 0; z < colSum.length(); z++) {
                char currentTest = colSum.charAt(z);
                for (int x = 0; x < colSum.length(); x++) {
                    if (colSum.charAt(x) == currentTest && x != z) return false;
                }
            }
        }
        return true;
    }
}