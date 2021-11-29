package Dominio;

public class CtrlCreateValidator {
    private static Kakuro k;
    private int n;
    private int m;

    public CtrlCreateValidator(Kakuro k){
        this.k = k;
        n = k.getAltura();
        m = k.getAnchura();

    }
    public boolean Validate(){

        //mirar si tiene la primera fila/columna negra
        for(int i=0; i<n; i++) if(k.isBlanca(i,0)) return false;
        for(int j=0; j<m; j++) if(k.isBlanca(0,j)) return false;

        //mirar si cuando hay suma hay una blanca al lado
        for(int i=0; i<n; i++){
            for(int j=0; j<m; j++){
                   if(k.isSuma(i,j)){
                       String[] aux = Sum(i,j);
                       //hay una suma de fila
                       if (aux[0]!=null){
                           if (j==m-1) return false; //la celda esta en la ultima columna
                           else if (!k.isBlanca(i,j+1)) return false; //no hya una cleda blanca para introducir suma
                       }
                       if (aux[1]!=null){
                           if (i==n-1) return false; //la celda esta en la ultima fila
                           else if (!k.isBlanca(i+1,j)) return false; //no hya una cleda blanca para introducir suma
                       }
                   }
                   //mirar si es blanca que tenga una suma fila/columna
                else if(k.isBlanca(i,j)){
                    //fila
                    boolean rowFound = false;
                    int auxj = j;
                    while(!rowFound && auxj>=0){
                        if(k.isSuma(i,auxj)){ //encontramos una suma
                            String[] aux = Sum(i,auxj);
                            if(aux[0]!=null)rowFound=true; //miramos si es de fila
                        }
                        auxj--;
                    }
                    if(!rowFound) return false;
                    //columna
                    boolean colFound = false;
                    int auxi = i;
                    while(!colFound && auxi>=0){
                        if(k.isSuma(auxi,j)){ //encontramos una suma
                            String[] aux = Sum(auxi,j);
                            if(aux[1]!=null) colFound=true; //miramos si es de columna
                        }
                        auxi--;
                    }
                    if(!colFound)return false;
                   }
            }
        }

        return true;
    }
    //Devuelve en la pos 0 la sumaF y en la 1 la sumaC
    public String[] Sum(int a, int b) {
        String[] res = {null, null};
        if (k.getCellValue(a,b).contains("C")) {
            res[1] = k.getCellValue(a,b).split("C")[1];
            if(res[1].contains("F")) {
                res = res[1].split("F");
                String aux2 = res[1];
                res[1] = res[0];
                res[0] = aux2;
            }
        }
        else if (k.getCellValue(a,b).contains("F")){
            res[0] = k.getCellValue(a,b).split("F")[1];
        }
        return res;
    }
}
