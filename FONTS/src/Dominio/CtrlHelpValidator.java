package Dominio;

import java.util.HashSet;
import java.util.Scanner;

public class CtrlHelpValidator {

    private static Kakuro k;
    private int n;
    private int m;

    public CtrlHelpValidator(Kakuro k) {
        this.k = k;
        this.n = k.getAltura();
        this.m = k.getAnchura();
    }

    public boolean validate() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                if (k.isSuma(i, j))
                    if (!checkSum(i, j)) return false;
        return true;
    }

    private boolean checkSum(int i, int j) {
        String sum = k.getCellValue(i,j);
        int C, F;
        C = F = -1;
        if (sum.contains("C")) {
            String split = sum.split("C")[1];
            if (split.contains("F")) {
                String[] split2 = split.split("F");
                C = Integer.parseInt(split2[0]);
                F = Integer.parseInt(split2[1]);
            } else {
                C = Integer.parseInt(split);
            }
        } else {
            F = Integer.parseInt(sum.split("F")[1]);
        }

        if (F != -1) {
            int jj = j;
            int sumF = 0;
            int value;
            HashSet<Integer> values = new HashSet<>();
            while(++jj < m && k.isBlanca(i, jj)) {
                if (!k.isEmpty(i,jj)) {
                    value = Integer.parseInt(k.getCellValue(i,jj));
                    if (!values.add(value)) return false;
                    sumF += value;
                }
            }
            if (sumF > F) return false;
        }

        if (C != -1) {
            int ii = i;
            int sumC = 0;
            int value;
            HashSet<Integer> values = new HashSet<>();
            while(++ii < n && k.isBlanca(ii, j)) {
                if (!k.isEmpty(ii,j)) {
                    value = Integer.parseInt(k.getCellValue(ii, j));
                    if (!values.add(value)) return false;
                    sumC += value;
                }
            }
            if (sumC > C) return false;
        }
        return true;
    }
}


