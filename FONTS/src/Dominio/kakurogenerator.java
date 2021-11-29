package Dominio;


import javax.swing.text.BadLocationException;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;

public class kakurogenerator {

    /* k: Kakuro genera y devuelve la clase
    *  n: altura del kakuro
    *  m: anchura del kakuro
    *  p: probabilidad de que la celda sea negra
    *  maxCellNum: limite de celdas blancas consecutivas */
    private Kakuro k;
    private final int n;
    private final int m;
    private final int p;
    private final int maxCellNum;

    public kakurogenerator(Kakuro k) throws BadLocationException {
        this.k = k;
        n = k.getAltura();
        m = k.getAnchura();
        String d = String.valueOf(k.getDificultad());
        /* determinamos la probabilidad de que una celda pueda ser negra y
           maxCellNum segun la dificultad del kakuro */
        if (d.equals("FACIL")) {
            p = 4;
            maxCellNum = 7;
        } else if (d.equals("INTERMEDIO")) {
            p = 4;
            maxCellNum = 8;
        } else {
            p = 3;
            maxCellNum = 9;
        }
        // inicializamos k con el valor de Celdas blancas vacías
        initialiseMatrixKakuro(k);
        // generamos el kakuro
        generate();
        // guardamos el kakuro en un fichero de texto
        writeBoard();
    }

    public Kakuro getGeneratedKakuro() { return this.k; }

    public void initialiseMatrixKakuro(Kakuro k) {
        /* inicializamos cada Celda para no acceder
           a un valor null en el algoritmo */
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                k.createCell(i,j,"?");
    }

    public void generate() throws BadLocationException {
        /*  incializamos la arista superior e izquierda con celdas negras vacías
            ya que ninguna de estas puede ser blanca */
        k.createCell(0,0,"*");
        for (int j = 1; j < m; j++) k.createCell(0,j,"*");
        for (int i = 1; i < n; i++) k.createCell(i,0,"*");

        /* estas variables nos permitiran contar cuantas celdas blancas seguidas
           por fila y por columna tenemos para no superar la maxNumCell */
        int[] rowWhiteCells = new int[n];
        int[] colWhiteCells = new int[m];

        /* rellenamos el kakuro de celdas blancas y negras */
        fillKakuro(1,1, rowWhiteCells, colWhiteCells);
        /* determinamos cuales de las celdas tienen que ser de tipo suma */
        fillSums(0,0);
        /* calculamos el valor de las sumas para las celdas de tipo suma */
        setSums();
    }

    public boolean fillKakuro(int i, int j, int[] rowWhiteCells, int[] colWhiteCells) {
        /* estas variables nos marcaran si la celda actual tiene que ser de un color
           obligatoriamente */
        boolean whiteForced = false;
        boolean blackForced = false;

        /* el kakuro se recorre de izquierda a derecha y de arriba a bajo */

        // si llegamos al final del kakuro hemos rellenado el kakuro con exito
        if (i == n) return true;
        // si llegamos al final de una fila pasamos a la siguiente
        else if (j == m) return fillKakuro(i+1, 1, rowWhiteCells, colWhiteCells);
        else {
            /* estas variables guardan el valor actual de celdas blancas consecutivas
               en la fila y columna de la celda actual por si modificamos este valor
               y tenemos que restaurarlo en caso de backtracking */
            int rowLast = rowWhiteCells[i - 1];
            int colLast = colWhiteCells[j - 1];

            // si marcando la celda negra no generamos una suma de 1 celda blanca
            if (!hasSum1(i,j)) {

                Random r = new Random();
                /* si llegamos a la ultima fila/columna y la anterior celda es negra
                   tenemos que forzar la actual a negra para no crear una suma de 1 celda */
                if ((j == m-1 && k.isNegra(i, j-1)) ||
                        (i == n-1 && k.isNegra(i-1, j))) {
                    blackForced = true;
                    k.createCell(i, j, "*");
                    rowWhiteCells[i - 1] = 0;
                    colWhiteCells[j - 1] = 0;
                } else if (r.nextInt(10) <= p) {
                    /* si llegamos aqui significa que la celda puede ser blanca o negra
                       por lo tanto lo determinamos segun la probabilidad p */
                    k.createCell(i, j, "*");
                    rowWhiteCells[i - 1] = 0;
                    colWhiteCells[j - 1] = 0;
                } else {
                    // si no le ha tocado ser negra la dejamos blanca
                    rowWhiteCells[i - 1]++;
                    colWhiteCells[j - 1]++;
                }
            } else {
                // tenemos que marcar la celda obligatoriamente blanca

                /* si llegamos a la ultima fila/columna y la anterior celda es negra
                   tenemos que forzar la actual a negra para no crear una suma de 1 celda
                   pero como ya forzamos que sea blanca, tenemos que hacer backtracking */
                if ((j == m-1 && k.isNegra(i, j-1)) ||
                        (i == n-1 && k.isNegra(i-1, j)))
                    //tendria que ser blanca y negra a la vez
                    return false;

                whiteForced = true;
                rowWhiteCells[i - 1]++;
                colWhiteCells[j - 1]++;
            }

            /* una vez determinamos el color de la celda tenemos que mirar que si es:
                - blanca:
                    - no superamos el limite de celdas blancas consecutivas en una suma (maxCellNum)
                - negra:
                    - no creamos una fila/columna entera de celdas negras
                    - no perdemos la conectividad del kakuro
                    - no creamos un muro vertical de celdas negras que nos separa el kakuro de manera
                        que en un futuro implique mucho backtracking para no perder la conectividad
                    - no creamos sumas de 1 celda blanca (por el backtracking)
            */
            if (k.isBlanca(i,j)) {

                // miramos que no se supere maxCellNum
                boolean[] check = check(i,j,rowLast,colLast,rowWhiteCells,colWhiteCells,whiteForced);

                /* si la celda no podia ser blanca y no hemos podido cambiarla a negra
                   significa que esa celda no tiene solucion valida y hacemos backtracking */
                if (!check[0]) return false;

                /* si la hemos podido cambiar a negra significa que forzamos que sea negra
                   ya que no puede ser blanca */
                if (check[1]) blackForced = true;
            }

            boolean connected;

            if (k.isNegra(i,j)) {
                /* en este check miramos:
                    - que no se cree la fila/columna de celdas negras
                    - a partir de la fila del medio del kakuro, si tenemos un muro, intentamos cortarlo
                    - que no haya sumas de 1 celda */
                if ((i >= n/2 && hasWall(i-1,j)) || (i == n-2 && colBlack(i,j))
                        || (j == m-2 && rowBlack(i,j)) || hasSum1(i,j)) {
                    /* en caso de que se cumpla qualquiera de las 3 condiciones de arriba, forzamos la celda
                       a blanca */
                    k.createCell(i, j, "?");
                    if (blackForced) {
                        /* si estaba forzada a negra, no hay solucion valida, recuperamos los valores
                         anteriores de rowWhiteCells y colWhiteCells y hacemos backtracking */
                        rowWhiteCells[i - 1] = rowLast;
                        colWhiteCells[j - 1] = colLast;
                        return false;
                    } else {
                        whiteForced = true;
                        rowWhiteCells[i - 1] = rowLast+1;
                        colWhiteCells[j - 1] = colLast+1;
                        // tras marcarla como celda blanca comprovamos que no supere maxCellNum
                        boolean[] check = check(i,j,rowLast,colLast,rowWhiteCells,colWhiteCells,true);
                        if (!check[0]) return false;
                    }
                }
                // en este punto, la celda es negra y miramos si se mantiene la conectividad del kakuro
                connected = findConnected(k, new boolean[n][m]);
                // si perdemos la conectividad, marcamos la celda como blanca y miramos si se puede continuar
                if (!connected) {
                    k.createCell(i, j, "?");
                    if (blackForced) {
                        /* si estaba forzada a negra, no hay solucion valida, recuperamos los valores
                         anteriores de rowWhiteCells y colWhiteCells y hacemos backtracking */
                        rowWhiteCells[i - 1] = rowLast;
                        colWhiteCells[j - 1] = colLast;
                        return false;
                    }
                    // la ponemos blanca
                    rowWhiteCells[i - 1] = rowLast+1;
                    colWhiteCells[j - 1] = colLast+1;
                    whiteForced = true;

                    // tras marcarla como celda blanca comprovamos que no supere maxCellNum
                    boolean[] check = check(i,j,rowLast,colLast,rowWhiteCells,colWhiteCells,whiteForced);
                    if (!check[0]) return false;

                    /* si llegamos a este punto, hemos podido marcar la celda blanca cumpliendo con todas
                    las restricciones y podemos proceder a la siguiente iteracion */
                    return fillKakuro(i,j+1, rowWhiteCells, colWhiteCells);
                }
            }

            /* llegados a este punto, tenemos un kakuro conexo y cumplimos con maxCellNum,
               tenemos que mirar si se puede continuar con la recursividad, por lo tanto
               pasamos a la siguiente iteracion */
            boolean valid = fillKakuro(i, j + 1, rowWhiteCells, colWhiteCells);

            /* si no es valida intentaremos cambiar el color de la celda, y si no es posible
               volveremos a la iteracion anterior */
            if (!valid) {
                if (k.isNegra(i,j)) {
                    // si era negra, la intentamos poner blanca
                    k.createCell(i, j, "?");
                    if (blackForced){
                        // si no podemos cambiar el color volvemos atras
                        rowWhiteCells[i - 1] = rowLast;
                        colWhiteCells[j - 1] = colLast;
                        return false;
                    }
                    rowWhiteCells[i - 1] = rowLast+1;
                    colWhiteCells[j - 1] = colLast+1;

                    // tras marcarla como celda blanca comprovamos que no supere maxCellNum
                    boolean[] check = check(i, j, rowLast, colLast, rowWhiteCells, colWhiteCells, true);
                    if (!check[0]) return false;
                } else {
                    // si era blanca, la intentamos poner negra

                    /* en este check miramos:
                        - que no se cree la fila/columna de celdas negras
                        - a partir de la fila del medio del kakuro, si tenemos un muro, intentamos cortarlo
                        - que no haya sumas de 1 celda */
                    boolean hasWall = hasWall(i-1,j);
                    if (whiteForced || (i >= n/2 && hasWall) || (i == n-2 && colBlack(i,j)) ||
                            (j == m-2 && rowBlack(i,j)) || hasSum1(i,j)) {
                        /* en caso de que se cumpla qualquiera de las 3 condiciones de arriba,
                           tenemos que volver atras */
                        rowWhiteCells[i - 1] = rowLast;
                        colWhiteCells[j - 1] = colLast;
                        return false;
                    }

                    // la marcamos como negra
                    k.createCell(i, j, "*");
                    rowWhiteCells[i - 1] = 0;
                    colWhiteCells[j - 1] = 0;

                    // miramos que aun tengamos una componente conexa
                    connected = findConnected(k,new boolean[n][m]);
                    if (!connected) {
                        // si no es conexo no puede ser negra ni blanca, volvemos atras
                        k.createCell(i, j, "?");
                        rowWhiteCells[i - 1] = rowLast;
                        colWhiteCells[j - 1] = colLast;
                        return false;
                    }
                }
                /* si llegamos a este punto significa que hemos podido cambiar el color
                   de la celda y podemos pasar a la siguiente iteracion */
                return fillKakuro(i, j+1, rowWhiteCells, colWhiteCells);
            }
            // devolvemos que la iteracion es, efectivamente, valida
            return true;
        }
    }

    /* esta funcion se encarga de mirar si podemos marcar la celda i,j blanca, en caso contrario,
       decide que hacer con la celda y si es necesario hacer backtracking */
    private boolean[] check(int i, int j, int rowLast, int colLast, int[] rowWhiteCells,
                            int[] colWhiteCells, boolean whiteForced){
        /*
        res[0] -> indica si podemos continuar con la iteracion o si tenemos que hacer backtracking
        res[1] -> indica si hemos tenido que forzar el cambio a negra para poder continuar
         */
        boolean[] res = new boolean[2];
        res[0] = true;
        res[1] = false;

        /* miramos si se supera el limite de celdas blancas consecutivas en la fila/columna
           y si estamos en la ultima fila/columna y estamos creando una suma de 1 celda */
        if (rowWhiteCells[i-1] > maxCellNum || colWhiteCells[j-1] > maxCellNum ||
                (j == m-1 && k.isNegra(i, j-1)) || (i == n-1 && k.isNegra(i-1, j))) {

            if (whiteForced) {
                /* no puede ser blanca, pero al ser blanca forzada tenemos que hacer backtracking,
                   por tanto restauramos la celda y avisamos que es necesario el backtracking */
                rowWhiteCells[i - 1] = rowLast;
                colWhiteCells[j - 1] = colLast;
                res[0] = false;
                return res;
            }
            /* si no era una celda blanca forzada, la marcamos como negra y avisamos que le hemos
               cambiado el color y ahora es una negra forzada */
            res[1] = true;
            k.createCell(i,j,"*");
            rowWhiteCells[i-1] = 0;
            colWhiteCells[j-1] = 0;
            return res;
        }
        // si la celda blanca pasa todos los checks, devolvemos que no es necesario hacer ningun cambio
        return res;
    }

    // hasSum1 mira que no creemos una suma de 1 celda blanca, ya que estas sumas son triviales
    private boolean hasSum1(int i, int j) {
        return ((k.isBlanca(i-1, j) && k.isNegra(i-2, j)) ||
                (k.isBlanca(i, j-1) && k.isNegra(i, j-2)));
    }

    // rowBlack devuelve cierto si ve que tenemos una fila de celdas negras
    private boolean rowBlack(int i, int j) {
        for (int jj = 1; jj < j; jj++)
            if (k.isBlanca(i, jj)) return false;
        return true;
    }

    // colBlack devuelve cierto si ve que tenemos una columna de celdas negras
    private boolean colBlack(int i, int j) {
        for (int ii = 1; ii < i; ii++)
            if (k.isBlanca(ii, j)) return false;
        return true;
    }

    /* Uno de los principales factores que fuerza un backtracking muy grande es el hecho de que
       aparezca un "muro" en el kakuro. Un muro lo definimos de la siguiente manera: un camino
       de celdas negras que va des de la primera fila hasta la ultima, separando el kakuro en dos.
       Este camino puede tener cualquier forma, mientras separe dos componentes conexas de celdas
       blancas, es decir, tambien puede ser diagonal.

       El problema de este muro es que si llega a las ultimas filas del kakuro, como el kakuro no
       puede tener mas de una componente conexa, no permitira que el muro se genere, pero para llegar
       a las celdas negras que lo componen y conseguir romper esa cadena de celdas negras puede
       significar una cantidad de backtracking muy grande. Por lo tanto hemos implementado una funcion
       que detecta si tenemos un muro para intentar romperlo lo antes posible en fillKakuro().
     */
    private boolean hasWall(int i, int j) {
        boolean[][] visited = new boolean[n][m];
        findWall(i,j, visited);
        /* si tenemos una celda negra visitada en la fila 1 (ya que la 0 es toda negra)
           significa que hemos podido llegar a esa celda desde abajo y por lo tanto tenemos un muro */
        for (int jj = 1; jj < m; jj++)
            if (k.isNegra(1, jj) && visited[1][jj]) return true;
        return false;
    }

    /* findWall es un DFS que recorre el posible muro de abajo a arriba y marca como visitadas
       las celdas negras del posible muro */
    private void findWall(int i, int j, boolean visited[][]) {
        if (i < 1 || j < 0 || j == m || visited[i][j] || k.isBlanca(i,j)) return;
        visited[i][j] = true;
        findWall(i - 1, j, visited);
        findWall(i - 1, j - 1, visited);
        findWall(i - 1, j + 1, visited);
        findWall(i, j + 1, visited);
        findWall(i, j - 1, visited);
    }

    /* findConnected mira si tenemos una unica componente de celdas blancas
       en el kakuro, si hay mas de una perdemos la conectividad y devuelve falso */
    private boolean findConnected(Kakuro k, boolean[][] visited) {
        int i = 1;
        int j = 1;
        while(k.isNegra(i,j)) {
            if (j < m-1) j++;
            else {
                i++;
                j = 1;
            }
        }
        isConnected(i,j,k,visited);
        /* si existe una celda blanca que no haya sido visitada significa que
           desde la celda donde hemos empezado a buscar sus celdas blancas
           vecinas no hemos podido acceder a esa celda y, consecutivamente el kakuro
           no es conexo */
        for (i = 1; i < n; i++)
            for (j = 1; j < m; j++)
                if (!visited[i - 1][j - 1] && k.isBlanca(i, j)) return false;
        return true;
    }

    // isConnected hace un DFS marcando todas las celdas blancas visitadas
    private void isConnected(int i, int j, Kakuro k, boolean[][] visited) {
        if (i < 1 || i >= n || j < 1 || j >= m || visited[i-1][j-1] || k.isNegra(i,j)) return;
        visited[i-1][j-1] = true;
        isConnected(i + 1, j, k, visited);
        isConnected(i, j + 1, k, visited);
        isConnected(i - 1, j, k, visited);
        isConnected(i, j - 1, k, visited);
    }

    /* fillSums detecta las celdas negras que tienen que ser sumas y llena las celdas blancas
       con numeros aleatorios cumpliendo las reglas del kakuro: no tener valores repetidos
       en una fila/columna */
    private boolean fillSums(int i, int j){
        if (i >= n) return true;
        if (j >= m) return fillSums(i+1,0);

        int index = 0;
        // creamos un bitset auxiliar para los valores candidatos para la actual fila y columna
        BitSet rowAux = new BitSet(9);
        BitSet colAux = new BitSet(9);
        if (k.isNegra(i,j)){
            // si tenemos una celda blanca adyacente a la negra, la negra es suma

            if (j < m-1 && k.isBlanca(i,j+1)){
                // hay una suma de fila

                // creamos un bitset para los valores candidatos para la actual fila
                BitSet canF = new BitSet(9);
                // inicialmente consideramos todos los candidatos validos {1,..,9}
                canF.set(0,9);
                if (i < n-1 && k.isBlanca(i+1,j)){
                    // hay tambine una suma de columna

                    // creamos un bitset para los valores candidatos para la actual columna
                    BitSet canC = new BitSet(9);
                    // inicialmente consideramos todos los candidatos validos {1,..,9}
                    canC.set(0,9);
                    k.setCellValue(i,j,"C0F0");
                    k.setCellCandidatesF(i, j, canF);
                    k.setCellCandidatesC(i,j,canC);
                }
                else{
                    k.setCellValue(i,j,"F0");
                    k.setCellCandidatesF(i, j, canF);
                }
            }
            else if (i < n-1 && k.isBlanca(i+1,j)){
                // hay una suma de columna

                // creamos un bitset para los valores candidatos para la actual columna
                BitSet canC = new BitSet(9);
                // inicialmente consideramos todos los candidatos validos {1,..,9}
                canC.set(0,9);
                k.setCellValue(i,j,"C0");
                k.setCellCandidatesC(i,j,canC);
            }
        }
        else{
            // cogemos los canidadatos de la celda anterior
            BitSet candidatesF = k.getCellCandidatesF(i,j-1);
            BitSet candidatesC = k.getCellCandidatesC(i-1,j);
            k.setCellCandidatesF(i, j, candidatesF);
            k.setCellCandidatesC(i, j, candidatesC);

            // miramos si con la interseccion queda algun candidato disponible
            BitSet intersection = (BitSet) candidatesC.clone();
            intersection.and(candidatesF);

            // si no queda ninguno significa que hay que hacer backtracking
            if (intersection.cardinality() <= 0) return false;

            // miramos si para todos los numeros que pueden ir en la celda alguno genera solucion
            boolean valid = false;
            while (!valid && intersection.cardinality() > 0) {
                // elegimos un candidato disponible aleatorio
                int rnd = new Random().nextInt(intersection.cardinality());
                index = intersection.nextSetBit(rnd);
                if (index < 0) index = intersection.previousSetBit(rnd);
                /* nos guardamos el candidato en una auxiliar en caso que haga falta hacer
                   backtracking */
                candidatesC.clear(index);
                colAux.set(index);
                candidatesF.clear(index);
                rowAux.set(index);
                k.setCellValue(i, j, Integer.toString(index + 1));

                // actualizamos la interseccion
                intersection = (BitSet) candidatesC.clone();
                intersection.and(candidatesF);

                valid = fillSums(i, j + 1);
                if (valid) return valid;
                else k.setCellValue(i, j, "?");
            }
            //si no hem pogut posar cap numero restaurem els candidats en aquest punt i anem endarrere
            if (!valid){
                candidatesC.or(colAux);
                candidatesF.or(rowAux);
            }
            return valid;
        }

        boolean valid = fillSums(i,j+1);
        return valid;
    }

    /* setSums calcula el valor de la suma segun los valores puestos en las celdas blancas en la
       funcion fillSums, sencillamente suma los valores de las celdas blancas consecutivas */
    private void setSums(){
        for (int i = 0; i < n; i++){
            for (int j = 0; j < m; j++) {
                if (k.isNegra(i, j)) {
                    int row = addRow(i, j);
                    int col = addCol(i, j);
                    if (row != 0) {
                        if (col != 0) {
                            k.setCellValue(i, j, "C" + col + "F" + row);
                        } else k.setCellValue(i, j, "F" + row);
                    } else if (col != 0) k.setCellValue(i, j, "C" + col);
                    else k.setCellValue(i, j, "*");
                }
            }
        }
        for (int i = 0; i<n; i++)
            for (int j = 0; j < m; j++){
                if(k.isBlanca(i,j)) k.setCellValue(i,j,"?");
            }
    }

    /*  devuelve la suma de uno de los conjuntos de celdas blancas
        consecutivas en una fila */
    public int addRow(int row, int col) {
        // sum: suma total de la fila
        int sum = 0;
        // inBounds: nos dice si seguimos consultando celdas blancas
        boolean inBounds = true;
        for (int j = col + 1; j < m && inBounds; j++) {
            if (k.isBlanca(row,j))
                sum += Integer.parseInt(k.getCellValue(row,j));
            else inBounds = false;
        }
        return sum;
    }

    /*  devuelve la suma de uno de los conjuntos de celdas blancas
        consecutivas en una columna */
    public int addCol(int row, int col) {
        // sum: suma total de la columna
        int sum = 0;
        // inBounds: nos dice si seguimos consultando celdas blancas
        boolean inBounds = true;
        for (int i = row + 1; (i < n) && inBounds; i++) {
            if (k.isBlanca(i,col))
                sum += Integer.parseInt(k.getCellValue(i,col));
            else inBounds = false;
        }
        return sum;
    }

    public void writeBoard(){
        try {
            BufferedWriter output;
            String folder = "Kakuros";
            File directory = new File(folder);
            /*  almacenamos los kakuros en un directorio llamado Kakuros por
                tanto, si no está creado el directorio lo creamos */
            if (!directory.exists()) directory.mkdir();
            File file = new File(folder + "/Board" + n + "x"
                    + m + k.getDificultad() + ".txt");
            output = new BufferedWriter(new FileWriter(file));

            output.write(n + "," + m);
            output.newLine();
            for(int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (j == 0) output.write(k.getCellValue(i,j));
                    else output.write("," + k.getCellValue(i,j));
                }
                output.newLine();
            }
            output.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // imprime le kakuro por consola
    public void printBoard() {
        System.out.println(n + "," + m);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (j == 0) System.out.print(k.getCellValue(i,j));
                else System.out.print("," + k.getCellValue(i,j));
            }
            System.out.println();
        }
    }

    public static void main(String args[]) throws BadLocationException {

        Scanner s = new Scanner(System.in).useDelimiter(",|\\n| ");
        int altura = s.nextInt();
        int anchura = s.nextInt();
        int dif = s.nextInt();
        String d = null;
        if (dif == 1) d = "FACIL";
        else if (dif == 2) d = "INTERMEDIO";
        else if (dif ==3 ) d = "DIFICIL";
        else System.out.println("Aprende a usar el generator crack");
        //int seed = s.nextInt();

        /*int altura = Integer.parseInt(args[0]);
        int anchura = Integer.parseInt(args[1]);;
        int dif = Integer.parseInt(args[2]);
        String d = null;
        if (dif == 1) d = "FACIL";
        else if (dif == 2) d = "INTERMEDIO";
        else if (dif ==3 ) d = "DIFICIL";
        else System.out.println("dificulty is a value in {1,2,3}");
        int seed = Integer.parseInt(args[3]);*/

        //Timestamp begining = new Timestamp(System.currentTimeMillis());
        Kakuro k = new Kakuro(altura, anchura, d);
        kakurogenerator kg = new kakurogenerator(k);
        kg.printBoard();
        //Timestamp end = new Timestamp(System.currentTimeMillis());
        //System.out.println("Time: " + (end.getTime() - begining.getTime()) + " ms.");
    }
}