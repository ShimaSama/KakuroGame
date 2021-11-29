package Dominio;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;

public class kakurosolver{
    //Scanner scanner;
    private final int n;
    private final int m;
    private Kakuro k;
    private int solutions;
    private Kakuro kSolved;
    private boolean help;

    //CONSTRUCTORA

    public kakurosolver(Kakuro k){
        this.k = k;
        n = k.getAltura();
        m = k.getAnchura();
        solutions = 0;
        help = false;
        }

    //MODIFICADORES

    /* devuelve si el Kakuro contenido en el fichero con nombre file tiene
       solución, y en caso de que la tenga, la imprime por pantalla */
    public int Solve() {
        /* inicializamos las sumas y candidatos de las celdas blancas del kakuro
        * dado en la constructora */
        initKakuro();

        // guardamos el estado inicial para que kSolved no quede a null
        kSolved = deepCopy();

        // empezamos la recursion con la primera celda dada
        int[] next = nextCell();
        recursive_sol(next[0],next[1], new int[2]);
        return solutions;
    }

    /* función recursiva que para el estado actual de kakuro prueba de poner
    un número de sus candidatos en la celda i,j. celdaHelp solo se usa si el
     parametro help del solver es true, en ese caso celdaHelp contendrá al acabar
    la ejecución recursiva la posición de la primera celda con la que hemos
    empezado la recursión
     */
    private Boolean recursive_sol(int i, int j, int[] celdaHelp) {
        boolean valid = false;
        // si hemos llegado al final del kakuro tenemos una solucion
        if (i == n){
            solutions++;
            // guardamos el estado actual que contiene una solucion
            kSolved = deepCopy();
            /* miramos si hay mas de una solucion volviendo al estado anterior
            si no tenemos >1 soluciones
             */
            return solutions > 1;
        }

        // si no llegamos al final entramos entramos a la recursion
        else {

            int sumColAux = k.getCellSumaC(i,j).getValue();
            int sumRowAux = k.getCellSumaF(i,j).getValue();
            valid = false;

            /* Variables auxiliares:
               restoreRow: este bitset guardará todos los candidatos de la fila i
                           que vayamos descartando de esta celda, para cuando sea
                           necesario restaurar el estado anterior, en caso de que
                           volvamos a la recursiva anterior sin haber encontrado
                           una solución.
               restoreCol: el mismo concepto que restoreRow pero para los candidatos
                           de la columna j
             */
            BitSet restoreRow = new BitSet(9);
            BitSet restoreCol = new BitSet(9);

            /* generamos la intersección de los candidatos para la fila i y la
               columna j, dando los candidatos posibles de la celda i,j
             */
            BitSet intersection = (BitSet) k.getCellCandidatesC(i,j).clone();
            intersection.and(k.getCellCandidatesF(i,j));

            /* num simplemente nos servirá para guardar el número que probamos
            actualmente y no tener que ir haciendo +1 con el indice de intersection
             */
            int num;

            /* probamos para cada candidato de la intersección si hay una solución */
            for ( int candidate = intersection.nextSetBit(0); candidate >= 0;
                candidate = intersection.nextSetBit(1+candidate)) {

                num = candidate+1;
                if (valid) return true;

                /* miramos que con el candidato escogido no nos pasemos de la
                suma, si nos pasamos volvemos al estado anterior
                 */
                if (sumRowAux - num < 0 || sumColAux - num < 0){
                    k.getCellCandidatesC(i,j).or(restoreCol);
                    k.getCellCandidatesF(i,j).or(restoreRow);
                    return false;
                }


                // si no nos pasamos de la suma actualizamos el estado actual
                k.clearCellCandidateFPos(i,j,num-1);
                k.clearCellCandidateCPos(i,j,num-1);
                k.getCellSumaC(i,j).addToValue(-num);
                k.getCellSumaF(i,j).addToValue(-num);

                /* si con el número actual no habria solución lo descartamos y
                probamos el siguiente
                 */
                int[] checkSum = checkSum(i,j,num,restoreCol,restoreRow);
                if (checkSum[0] < 0) {
                    restoreCol.set(num - 1);
                    restoreRow.set(num - 1);
                    continue;
                }
                // si puede haber solución acabamos de actualizar la celda
                k.setCellValue(i,j,String.valueOf(num));

                // escogemos la siguiente celda para seguir buscando solución
                int[] next = new int[2];
                if (checkSum[0] >= 0 && checkSum[1] != -1){
                    next[0] = checkSum[1];
                    next[1] = checkSum[2];
                }
                else next = nextCell();

                // vamos al estado siguiente con la celda escogida
                valid = recursive_sol(next[0],next[1], new int[2]);

                /* si hemos encontrado una solucion guardamos una celda
                para dar en el help
                 */
                if (help && valid && solutions >= 1){
                    celdaHelp[0] = i;
                    celdaHelp[1] = j;
                    help = false;
                }

                /* si no hay solución posible para este estado restauramos
                todos los valores que nos devuelven al estado anterior
                 */
                if (!valid) {
                    k.setCellValue(i,j,"?");
                    k.getCellSumaC(i,j).addToValue(num);
                    k.getCellSumaF(i,j).addToValue(num);

                    /*puede ser que num cumpla checkSum pero aun asi no sea valido*/
                    k.setCellCandidateCPos(i,j,num-1);
                    k.setCellCandidateFPos(i,j,num-1);

                    /*si hacemos backtracking de esta posicion restauramos los sets*/
                    k.getCellCandidatesC(i,j).or(restoreCol);
                    k.getCellCandidatesF(i,j).or(restoreRow);
                }

                // para asegurarnos de que no nos pasamos del indice en intersection
                if (candidate == 8) break;
            }

            /* si ningún candidato cumple checkSum necesitamos restaurar igualmente
            el estado anterior y volveremos
             */
            if (!valid){
                k.getCellCandidatesC(i,j).or(restoreCol);
                k.getCellCandidatesF(i,j).or(restoreRow);
            }
            return valid;
        }
    }

    /* nextCell devuelve la celda con menos candidatos de todas las vacias, si
       vemos que hay una con 1 candidato devolvemos esa sin acabar de recorrer
       el kakuro entero
     */
    private int[] nextCell(){
        int[] res= new int[2];
        res[0] = res[1] = -1;

        // auxiliar para saber cual es el tamaño mas pequeño de candidatos
        int min_size = 10;
        boolean done = true;

        /* recorremos el kakauro mirando el nº de candidatos de cada celda
        vacia y guardamos la posicion del que tenga menos
         */
        for (int i = 1; i < n; i++) //no fem mai la primera fila
            for (int j = 0; j < m; j++){
                if (k.isEmpty(i,j)) {
                    done = false;
                    BitSet inter = (BitSet) k.getCellCandidatesC(i, j).clone();
                    inter.and(k.getCellCandidatesF(i,j));
                    if (inter.cardinality() < min_size){
                        min_size = inter.cardinality();
                        res[0] = i;
                        res[1] = j;
                        //mirar si es pot deixar 1 o ha de ser 0
                        if (min_size <= 1) return res;
                    }
                }
            }
        /* si llegamos con done true significa que no quedan celdas vacias y
        por tanto tenemos una solución
         */
        if (done) {
            res[0] = n;
            res[1] = m;
        }
        return res;
    }

    /* checkSum nos hará todas las podas que hemos implementado para no seguir
       con el candidato actual si sabemos que no habría solución para este
     */
    private int[] checkSum(int i, int j, int num, BitSet restoreCAfter, BitSet restoreRAfter) {
        /* FILAS */

        /* Variables auxiliares:
        restoreRow: guardaremos en este BitSet los valores que vayamos sacando
                    de los candidatos reales de la fila i, para restaurarlos al
                    final
         sumaRowaux: entero auxiliar con el valor de sumaF para poder ir modificandolo
                     y que no afecte al valor real guardado en las celdas
         correct:   indica si tenemos un estado correcto hasta el momento, con el que
                    podriamos encontrar solución
         last:      indica si la celda en la que nos encontramos es la última de la
                    fila o la columna
         done:      si hemos acabado de iterar por las celdas de la fila o columna
         cellsLeft: celdas restantes vacias en la fila o columna
         hasNext:   indica si solo queda una celda vacia en la fila o la columna
         */
        BitSet restoreRow = new BitSet(9);
        int sumRowaux = k.getCellSumaF(i,j).getValue();

        boolean correct = true;
        boolean last = true;
        boolean done = false;
        int cellsLeft = 0;
        boolean hasNext = false;

        /* valores de next:
          next[0]:  <0 si no encontrariamos solución para este número en esta celda,
                    >=0 en caso contrario.
          next[1]:  si hasNext, posición i de la celda restante vacía, -1 si !hasNext.
          next[2]:  si hasNext, posición j de la celda restante vacía, -1 si !hasNext.
         */
        int[] next = new int[3]; //si hay 1 cellsLeft esta sera la posicion de dicha cell si no es -1,-1
        next[1] = next[2] = -1;
        next[0] = 1;

        // para no estar llamando cada vez
        BitSet candidatesRow = k.getCellCandidatesF(i,j);

        // nos colocamos en la primera celda de la suma de fila
        int jj = j;
        while(jj>0 && !k.isSuma(i,jj)) jj--;

        // miramos para todas las celdas de la suma de fila
        for (jj = jj+1; jj < m && !done; jj++) {
            // si estamos en la celda actual no la tratamos
            if (jj == j) continue;

            // si llegamos a una negra hemos acabado la suma de fila
            if (k.isNegra(i,jj)) break;

            // si tenemos una celda vacia de la suma miramos lo siguiente
            else if (k.isEmpty(i,jj)) {
                /* actualizamos el numero de celdas vacias restantes y guardamos
                la posicion de esta celda vacia por si acaso fuese la última de
                la fila.
                 */
                cellsLeft++;
                next[1] = i;
                next[2] = jj;
                // como hemos encontrado una celda vacia sabemos que !last
                last = false;

                /* si nos quedamos sin candidatos de fila significa que con
                los que quedaban no podiamos llegar a la suma por lo tanto el
                numero que estamos mirando no valdra
                 */
                if (candidatesRow.cardinality() <= 0) break;

                // si aun quedan candidatos cogemos el mayor de todos
                int l = candidatesRow.previousSetBit(8)+1;
                /* actualizamos la suma auxiliar para ver si podremos cumplirla
                con el número actual y los candidatos más grandes
                 */
                sumRowaux -= l;

                // si cumplicmos la suma no hace falta seguir mirando
                if (sumRowaux <= 0 && cellsLeft > 1) done = true;
                /* quitamos el candidato que ya hemos mirado por si no
                hemos acabado de mirar que podamos cumplir sumaF
                 */
                candidatesRow.clear(l-1);
                restoreRow.set(l-1);
            }
        }
        // restauramos los candidatos borrados
        k.getCellCandidatesF(i,j).or(restoreRow);

        // para no tener que llamar cada vez
        int sum = k.getCellSumaF(i,j).getValue();

        // si somos la última celda tenemos que cumplir la suma para ser correct
        if (last){
            if (sum != 0) correct = false;
        }

        /*si no es last y la suma ya esta cumplida num no es valido ni lo será
        cualquier candidato siguiente a él ya que serán demasiado grandes
         */
        else if (sum <= 0){
            /* quitamos todos los candidatos y los guardamos en el BitSet de
            restore de recursive_sol que nos hemos pasado por parametro
             */
            for (int can = candidatesRow.nextSetBit(num-1); can >= 0; can =
                    candidatesRow.nextSetBit(1+can)) {
                candidatesRow.clear(can);
                restoreRAfter.set(can);
                //para no hacer overflow en 1+can
                if (can == 8) break;
            }
            correct = false;
        }

        /* si solo queda una celda tenemos que mirar que la suma que queda
           sea un candidato para que lo probemos en la siguiente recursiva
         */
        else if (cellsLeft == 1){
            hasNext = true;
            if (!k.getCellCandidatesF(i,j).get(sum-1)) correct = false;
            else{
                for (int can = candidatesRow.nextSetBit(sum); can >= 0; can =
                        candidatesRow.nextSetBit(1+can)) {
                    candidatesRow.clear(can);
                    restoreRAfter.set(can);
                    //para no hacer overflow en 1+can
                    if (can == 8) break;
                }
            }
        }

        /* si no hemos podido cumplir la suma el número actual es demasiado
        pequeño y no es correct.
        Si no es correct por cualquier cosa restauramos el estado anterior
        menos el número en candidatos de fila y volvemos
         */
        if (sumRowaux > 0 || !correct) {
            k.setCellCandidateCPos(i,j,num-1);
            k.getCellSumaF(i,j).addToValue(num);
            k.getCellSumaC(i,j).addToValue(num);
            // -1 porque no habrà nunca solucion
            next[0] = -1;
            return next;
        }


        /* COLUMNAS */

        /* aplicamos exactamente el mismo metodo que en filas pero
        ahora lo hacemos para la columna
         */
        // ponemos las variables necesarias al estado inicial
        last = true;
        done = false;
        correct = true;
        int SumaCaux = k.getCellSumaC(i,j).getValue();
        BitSet restoreCol = new BitSet(9);
        cellsLeft = 0;
        BitSet candidatesCol = k.getCellCandidatesC(i,j);

        int ii = i;
        while(ii>0 && !k.isSuma(ii,j)) ii--;

        for (ii = ii+1; ii < n && !done; ii++) {
            if (ii == i) continue;
            if (k.isNegra(ii,j)) break;
            else if (k.isEmpty(ii,j)) {
                cellsLeft++;
                /* guardamos la celda solo si no ahbiamos encontrado una
                celda final en la fila
                 */
                if (!hasNext) {
                    next[1] = ii;
                    next[2] = j;
                }

                last = false;
                if (candidatesCol.cardinality() <= 0) break;
                int l = candidatesCol.previousSetBit(8)+1;

                SumaCaux -= l;
                if (SumaCaux <= 0 && cellsLeft > 1) done = true;
                candidatesCol.clear(l-1);
                restoreCol.set(l-1);
            }
        }

        k.getCellCandidatesC(i,j).or(restoreCol);

        sum =  k.getCellSumaC(i,j).getValue();
        //si estamos en el ultimo numero de la fila miramos que cumple la suma
        if (last){
            if (sum != 0) correct = false;
        }

        /*si no es last i la suma ya esta cumplida num no es valido ni lo sera
        cualquier candidato siguiente a el ya que seran demasiado grandes
         */

        else if (sum <= 0){
            for (int can = candidatesCol.nextSetBit(num-1); can >= 0;
                 can = candidatesCol.nextSetBit(1+can)) {
                candidatesCol.clear(can);
                restoreCAfter.set(can);
                if (can == 8) break; //para no hacer overflow en 1+can
            }
            correct = false;
        }
        /* si solo queda una celda miramos si tenemos como candidato lo que
        queda de suma para probarlo en la siguiente recursiva
         */
        else if (cellsLeft == 1){
            hasNext = true;
            if (!k.getCellCandidatesC(i,j).get(sum-1)) correct = false;
            else{
                /*for (int can = candidatesCol.nextSetBit(num-1); can >= 0;
                     can = candidatesCol.nextSetBit(1+can)) {
                    candidatesCol.clear(can);
                    restoreCAfter.set(can);
                    if (can == 8) break; //para no hacer overflow en 1+can
                }*/
            }
        }

        /* restauramos con los mismos criterios de no cumplir suma y correct
        pero sin restaurar el número en candidatos de columna en vez de fila
         */
        if (SumaCaux > 0 || !correct) {
            k.setCellCandidateFPos(i,j,num-1);
            k.getCellSumaF(i,j).addToValue(num);
            k.getCellSumaC(i,j).addToValue(num);
            next[0] = -1;
            return next;
        }
        if (!hasNext) next[1] = next[2] = -1;
        return next;
    }

    /* generamos un BitSet de candidatos como el especificado en kakuro, sabiendo
    que tenemos una suma de tamaño nCells, que tiene que sumar Suma.
    Los candidatos son de l a 10, siendo l >=0.
    parSum indica la suma parcial a cada llamada recursiva y Candidates los candidatos
    cogidos hasta ahora
     */
    private void generateSet(int l, int parSum, int Sum,
                             TreeSet<Integer> Candidates, BitSet res, int nCells) {
        // si cumplimos la suma para el tamaño especificado tenemos set candidato
        if (parSum == Sum && Candidates.size() == nCells) {
            // guardamos todos los candidatos en el BitSet y seguimos mirando
            Iterator<Integer> it = Candidates.iterator();
            while (it.hasNext())
                res.set(it.next()-1);
            return;
        }
        /* miramos mientras no tengamos mas candidatos que el tamaño permitido
         */
        if (Candidates.size() < nCells) {
            // miramos por cada numero de l a 10
            for (int num = l; num < 10; num++) {
                /* si nos pasamos de la suma para el numero actual ninguno de
                los que quedan nos valdra como candidato
                 */
                if (parSum + num > Sum) break;

                /* si no nos pasamos miramos si con este numero podemos hacer
                un set de candidatos final
                 */
                Candidates.add(num);
                generateSet( num + 1, parSum + num,
                        Sum, Candidates, res, nCells);
                /* quitamos el numero que hemos probado de los candidatos y
                seguimos para probar todas las combinaciones posibles
                 */
                Candidates.remove(Candidates.last());
            }
        }
    }

    // Devuelve en la pos 0 la sumaF y en la 1 la sumaC
    public String[] Sum(int a, int b) {
        String[] res = {null, null};
        // miramos si hay sumaC y la guardamos
        if (k.getCellValue(a,b).contains("C")) {
            res[1] = k.getCellValue(a,b).split("C")[1];
            // miramos si tambien hay sumaF y la guardamos
            if(res[1].contains("F")) {
                res = res[1].split("F");
                String aux2 = res[1];
                res[1] = res[0];
                res[0] = aux2;
            }
        }
        // miramos si no hay sumaC pero si sumaF
        else if (k.getCellValue(a,b).contains("F")){
            res[0] = k.getCellValue(a,b).split("F")[1];
        }
        return res;
    }

    // devuelve un clon del Kakuro k del solver, para no hacer Shallow Copy
    private Kakuro deepCopy() {
        Kakuro copy = new Kakuro(n,m,String.valueOf(k.getDificultad()));
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                copy.createCell(i, j, k.getCellValue(i, j));
        return copy;
    }

    /* pasa por parametro el kakuro sobre el que se requiere ayuda, devuelve
       el mismo kakuro pero con una celda mas rellenada
     */
    public Kakuro help(Kakuro toHelp){
        // preparamos el solve para resolve el kakuro pasado
        help = true;
        k = toHelp;
        initKakuro();

        /* guardamos una copia del kakuro pasado en el estado original para
           poder guardar sobre esta la celda nueva dada
         */
        Kakuro initialState = deepCopy();

        // generamos la solución para saber que número va a cada celda
        int[] next = nextCell();
        recursive_sol(next[0], next[1], next);

        /* asignamos a la celda dada por recursive_sol el número que tiene en la
        solución y devolvemos el kakuro
         */
        initialState.setCellValue(next[0], next[1], kSolved.getCellValue(next[0], next[1]));
        return initialState;
    }

    // devuelve si ha leido bien el input o no
    private boolean read(Scanner s){
        // nº de celdas que compone cada suma de fila
        int nCells = 0;

        /* en caso de que leamos un kakuro ya empezado guardamos los
        valores que ya han aparecido en cada suma de fila
         */
        Vector<Integer> partialSol = new Vector<Integer>();

        // Empezamos el escaneo y generacion de candidatos de fila
        for (int i = 0; i < n; i++) {
            String linea = s.nextLine();
            String[] holder = linea.split(",");

            for (int j = 0; j < m; j++) {
                k.createCell(i, j, holder[j]);

                // miramos si el elemento escaneado es suma de fila
                if (k.isSuma(i, j) && Sum(i, j)[0] != null) {
                    /* si es suma de fila miramos todos los elementos que la
                       componen */
                    // guardamos un puntero a sumaF con la suma encontrada
                    IntWrapper sumF = new IntWrapper(Integer.parseInt(Sum(i, j)[0]));
                    // y un puntero a candidatos de fila
                    BitSet canF = new BitSet(9);

                    while (++j < m) {
                        // seguimos generando el kakuro con los elementos de la suma
                        k.createCell(i, j, holder[j]);

                        if (k.isBlanca(i, j)) {
                            /* si leemos un kakuro a medias actualizamos los
                            numeros que han aparecido en la suma y el valor
                            del puntero
                             */
                            if (!k.isEmpty(i,j)){
                                int nAux = Integer.parseInt(holder[j]);
                                sumF.addToValue(-nAux);
                                partialSol.add(nAux-1);
                            }

                            /* asignamos a cada celda de la suma los punteros de
                            suma y candidatos
                             */
                            k.setCellSumaF(i, j, sumF);
                            k.setCellCandidatesF(i, j, canF);
                            nCells++;
                        }

                        /* si hemos cogido un elemento que no es celda blanca
                           significa que la suma ya ha acabado */
                        else {
                            break;
                        }
                    }

                    // queremos mirar esta posicion en la siguiente iteracion
                    j--;
                    /* generamos los candidatos de la suma en el puntero a
                    * candidatos ya dado a cada celda */
                    generateSet(1, 0, Integer.parseInt(Sum(i, j - nCells)[0]),
                            new TreeSet<>(), canF, nCells);

                    // si habia valores puestos los quitamos de los candidatos
                    for (Integer aux : partialSol) {

                        // ha puesto un numero que no era candidato
                        if (!canF.get(aux)){ return false;
                        }
                        else canF.clear(aux);
                    }
                    // ponemos los valores puestos a 0 para la siguiente suma
                    partialSol.clear();
                    nCells = 0;
                }
            }
        }

        // la misma lectura pero para actualizar las columnas
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (k.isSuma(i,j)) {
                    String[] sum = Sum(i, j);
                    // si es suma de columna actualizamos los punteros
                    if (sum[1] != null) {
                        nCells = 0;

                        /* punteros de candidatos y sumaC para las celdas de
                        la columna
                         */
                        IntWrapper sumC = new IntWrapper(Integer.parseInt(sum[1]));
                        BitSet canC = new BitSet(9);
                        // miramos para todas las celdas de sumaC
                        while (++i < n){
                            if (k.isNegra(i,j)) break;

                            /* si leemos un kakuro a medias actualizamos los
                            valores que ya han aparecido y la suma
                             */
                            if (k.isBlanca(i,j) && !k.isEmpty(i,j)){
                                int nAux = Integer.parseInt(k.getCellValue(i,j));
                                sumC.addToValue(-nAux);
                                partialSol.add(nAux-1);
                            }

                            nCells++;
                            /* asignamos los punteros de candidatos y suma a
                            cada celda de la columna
                             */
                            k.setCellSumaC(i,j,sumC);
                            k.setCellCandidatesC(i,j,canC);
                        }
                        /* generamos los candidatos en el puntero dado a cada celda
                        para que ya este actualizado
                         */
                        i = i - (nCells + 1);
                        generateSet(1, 0, Integer.parseInt(sum[1]), new TreeSet<>(),
                                canC, nCells);

                        // si habia valores puestos los quitamos de los candidatos
                        for (Integer aux : partialSol) {
                            //ha puesto un numero que no era candidato
                            if (!canC.get(aux)){
                                return false;
                            }
                            else canC.clear(aux);
                        }
                        // quitamos los valores que ya habian aparecido
                        partialSol.clear();
                    }
                }
            }
        }
        return true;
    }

    /* inicializa todas las sumas y candidatos de todas las celdas blancas del
       kakuro que tenemos en el solver como parametro y deja el kakuro listo para
       poder resolverlo
     */
    private boolean initKakuro(){
        /* exactamente igual que read pero sin crear celdas nuevas ya que
        asumimos que el kakuro es el del atributo k asi que los comentarios
        son mas cortos
         */

        int nCells = 0;
        Vector<Integer> partialSol = new Vector<Integer>();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {

                // miramos si el elemento escaneado es suma de fila
                if (k.isSuma(i, j) && Sum(i, j)[0] != null) {
                    /* si es suma de fila miramos todos los elementos que la
                       componen */
                    IntWrapper sumF = new IntWrapper(Integer.parseInt(Sum(i, j)[0]));
                    //puntero a candidatos de fila
                    BitSet canF = new BitSet(9);
                    while (++j < m) {

                        //si leemos un kakuro a medias actualizamos la información
                        if (k.isBlanca(i, j)) {
                            if (!k.isEmpty(i,j)){
                                int nAux = Integer.parseInt(k.getCellValue(i,j));
                                sumF.addToValue(-nAux);
                                partialSol.add(nAux-1);
                            }
                            // asignamos los punteros a la celda
                            k.setCellSumaF(i, j, sumF);
                            k.setCellCandidatesF(i, j, canF);
                            nCells++;
                        }
                        /* si hemos cogido un elemento que no es celda blanca
                           significa que la suma ya ha acabado */
                        else {
                            break;
                        }
                    }
                    // queremos mirar esta posicion en la siguiente iteracion
                    j--;

                    /* generamos los candidatos de la celda en el puntero debido */
                    generateSet(1, 0, Integer.parseInt(Sum(i, j - nCells)[0]),
                            new TreeSet<>(), canF, nCells);

                    // quitamos los valores ya puestos de los candidatos
                    for (Integer aux : partialSol) {

                        //ha puesto un numero que no era candidato
                        if (!canF.get(aux)){ return false;
                        }
                        else canF.clear(aux);
                    }
                    partialSol.clear();
                    nCells = 0;
                }
            }
        }


        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (k.isSuma(i,j)) {
                    String[] sum = Sum(i, j);
                    // miramos para cada sumaC
                    if (sum[1] != null) {
                        nCells = 0;
                        // punteros a sumaC i candidatos para las siguientes celdas
                        IntWrapper sumC = new IntWrapper(Integer.parseInt(sum[1]));
                        BitSet canC = new BitSet(9);

                        // miramos para cada celda de la sumaC
                        while (++i < n){

                            if (k.isNegra(i,j)) break;
                            /* si leemos un kakuro a medias actualizamos las
                            variables debidas
                             */
                            if (k.isBlanca(i,j) && !k.isEmpty(i,j)){
                                int nAux = Integer.parseInt(k.getCellValue(i,j));
                                sumC.addToValue(-nAux);
                                partialSol.add(nAux-1);
                            }
                            // asignamos los punteros a la celda
                            nCells++;
                            k.setCellSumaC(i,j,sumC);
                            k.setCellCandidatesC(i,j,canC);
                        }
                        /* generamos los candidatos de la celda en el puntero debido */
                        i = i - (nCells + 1);
                        generateSet(1, 0, Integer.parseInt(sum[1]), new TreeSet<>(),
                                canC, nCells);
                        // quitamos los valores ya puestos de los candidatos
                        for (Integer aux : partialSol) {
                            //ha puesto un numero que no era candidato
                            if (!canC.get(aux)){
                                return false;
                            }
                            else canC.clear(aux);
                        }
                        // borramos los valores ya puestos para la nueva suma
                        partialSol.clear();
                    }
                }
            }
        }
        return true;
    }

    // imprime el numero de soluciones, el tamaño del kakuro y una solucion si hay
    public void printBoard() {
        System.out.println(solutions);
        System.out.println(n + "," + m);
        if (solutions > 0) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (j == 0) System.out.print(kSolved.getCellValue(i,j));
                    else System.out.print(","+kSolved.getCellValue(i,j));
                }
                System.out.println();
            }
        }
    }




    public static void main (String args[]) throws BadLocationException {
        //Scanner s = new Scanner(System.in).useDelimiter(",|\\n");
        Scanner s = new Scanner(System.in);

        //System.out.println("Introduce n:");
        String linea = s.nextLine();
        String[] nm = linea.split(",");
        int altura = Integer.parseInt(nm[0]);
        //System.out.println("Introduce m:");
        int anchura = Integer.parseInt(nm[1]);
        //System.out.println("Choose dificulty between {FACIL, INTERMEDIO, DIFICIL}:");
        //String d = s.next().toUpperCase();
        Kakuro k = new Kakuro(altura, anchura, "DIFICIL");
        kakurosolver ks = new kakurosolver(k);

        Timestamp begining = new Timestamp(System.currentTimeMillis());
        //ks.Solve("Kakuros/Board" + altura + "x" + anchura + d + ".txt");
        ks.Solve();
        Timestamp end = new Timestamp(System.currentTimeMillis());
        ks.printBoard();
        System.out.println("Time: " + (end.getTime() - begining.getTime()) + " ms.");

    }
}
