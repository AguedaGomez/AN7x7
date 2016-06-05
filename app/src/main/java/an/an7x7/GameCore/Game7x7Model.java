package an.an7x7.GameCore;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import an.an7x7.Model.LineChecker;
import an.an7x7.Model.Square;

/**
 * Created by NachoLR on 10/05/2016.
 */
public class Game7x7Model {

    public enum State {
        ON_GAME,
        SQUARES_APPEAR,
        SQUARES_DESAPPEAR,
        SQUARE_SELECTED,
        END_GAME;
    }

    private static final int PURPLE = Color.rgb(153,51,255);
    private static final int BLUE = Color.rgb(120,230,247);
    private static final int YELLOW = Color.rgb(255,204,0);
    private static final int GREEN = Color.rgb(183,233,45);
    private static final int RED = Color.rgb(255,0,0);
    public static final int GRAY = Color.rgb(242, 240, 240);



    public Square[][] allSquares;
    public int [] squaresPreview;
    public int selectRow, selectColumn;
    public State state;
    public int differencePositionBigSquare = 9, differenceSideBigSquare = 18;
    public int difPosBigTransparentSquare = 10,  difSideBigTransparentSquare = 20;
    public int level = 6;

    private Random randomPosition, randomColor;
    private int[] colors  = {PURPLE, BLUE, YELLOW, GREEN, RED};
    private List<String> availablePositions;
    private LineChecker lineChecker;
    private int newSquaresCounter = 0;



    public Game7x7Model(){

        allSquares = new Square[7][7];
        squaresPreview = new int [6];
        availablePositions = new ArrayList<String>();
        randomPosition = new Random();
        randomColor = new Random();
        lineChecker = new LineChecker();
        state = State.ON_GAME;
        startLevel();

    }

    private void startLevel() {
        for (int row = 0; row < 7; row++)
            for (int column = 0; column < 7; column++) {
                allSquares[row][column] = new Square(row,column);
                String position = "" + row  + column;
                availablePositions.add(position);
            }
        nextColor();
        state = State.SQUARES_APPEAR;
    }

    public void update(float deltaTime) {
       switch (state) {
           case ON_GAME:
               updateGame(deltaTime);
               break;
           case SQUARES_APPEAR:
               updateSquaresAppear(deltaTime);
               break;
           case SQUARES_DESAPPEAR:
               updateSquaresDesappear(deltaTime);
               break;
       }
    }

    private void updateSquaresDesappear(float deltaTime) {
        if (difPosBigTransparentSquare <= 0 && difSideBigTransparentSquare <= 0) {
            eraseLine();
            difPosBigTransparentSquare=10;
            difSideBigTransparentSquare=20;
            if(newSquaresCounter == level){ // si ya ha creado los colores de la ronda, sigue jugando
            state = State.ON_GAME;
            }else{ // si no, es que la linea se ha eliminado al generar un cuadrado nuevo y tienen que salir aún algunos más.
                state = State.SQUARES_APPEAR;
            }
        }
        else {
            difPosBigTransparentSquare-=1;
            difSideBigTransparentSquare-=2;

        }

    }

    private void updateSquaresAppear(float deltaTime) {
        if (differencePositionBigSquare <= 0 && differenceSideBigSquare <= 0) { // Cuando termine la transición de mayor tamaño al tamaño definitivo.
            if (newSquaresCounter == level) { // Si ya ha creado los cuadrados segun el nivel
                state = State.ON_GAME;
                nextColor(); // Cargamos los colores de la ronda siguiente
                newSquaresCounter = 0;
            }
            else {              // si aun no ha creado los cuadrados segun el nivel
                if(availablePositions.isEmpty()){ // si no hay posiciones disponibles se acaba el juego
                    state = State.END_GAME;
                    Log.d("TEST", "FIN JUEGO");
                }
                else{
                    createSquareRandom(newSquaresCounter);
                    newSquaresCounter++;
                }
            }
            differenceSideBigSquare = 20;
            differencePositionBigSquare = 10;
        }
        else {
            differenceSideBigSquare-=4;
            differencePositionBigSquare-=2;

        }
    }



    private void updateGame(float deltaTime) {
        if (availablePositions.isEmpty()) {
            state = State.END_GAME;
            Log.d("TEST", "FIN JUEGO");
        }

    }


    public void onTouch(int cB, int rB) {

        if (rB >=0) { // sólo responder si se toca dentro del tablero

            if (allSquares[rB][cB].getColor() != GRAY) {
                if (state == State.SQUARE_SELECTED) {
                    state = State.ON_GAME;
                }
                else {

                    state = State.SQUARE_SELECTED;
                    selectRow = rB;
                    selectColumn = cB;

                    findTargetableLocations();
                }

            }

            else {
                if (state == State.SQUARE_SELECTED) {
                    if (allSquares[rB][cB].selectable) {

                        int currentColor = allSquares[selectRow][selectColumn].getColor();
                        allSquares[selectRow][selectColumn].setColor(GRAY);

                        availablePositions.add("" + selectRow + selectColumn);
                        allSquares[rB][cB].setColor(currentColor);
                        availablePositions.remove("" + rB + cB);

                        if (lineChecker.checkForLine(rB, cB, allSquares)) {
                            state = State.SQUARES_DESAPPEAR;

                        } // revisar si este movimiento puntua.

                        else {
                            state = State.SQUARES_APPEAR;
                        }
                    }
                }
            }

        }

    }

    private void eraseLine() {
        for (int row = 0; row < 7; row++)
            for (int column = 0; column < 7; column++){
                if(allSquares[row][column].erasable){
                    allSquares[row][column].setColor(GRAY);
                    availablePositions.add("" + row + column);
                }

            }
    }

    public void findTargetableLocations(){
        for (int row = 0; row < 7; row++)
            for (int column = 0; column < 7; column++){
                allSquares[row][column].selectable = false;
                allSquares[row][column].visited = false;
            }
        visitNeighbours(selectRow, selectColumn);
    }

    private void visitNeighbours(int sRow, int sColumn) {

        //Marco como visitado
        allSquares[sRow][sColumn].visited = true;

        //compruebo el cuadrado superior
        if (sRow > 0) { // si es == 0 no tiene superior
            if (!allSquares[sRow - 1][sColumn].visited) { // si el superior aun no ha sido visitado
                if (allSquares[sRow - 1][sColumn].getColor() == GRAY) { // si el superior está vacío
                    allSquares[sRow - 1][sColumn].selectable = true;
                    visitNeighbours(sRow - 1, sColumn); //llamada "recursiva"
                } else {
                    allSquares[sRow - 1][sColumn].selectable = false;
                }
            }
        }

        //compruebo el cuadrado inferior
        if (sRow < 6) { // si es == 6 no tiene inferior
            if (!allSquares[sRow + 1][sColumn].visited) { // si el inferior aun no ha sido visitado
                if (allSquares[sRow + 1][sColumn].getColor() == GRAY) { // si el inferior está vacío
                    allSquares[sRow + 1][sColumn].selectable = true;
                    visitNeighbours(sRow + 1, sColumn); //llamada "recursiva"
                } else {
                    allSquares[sRow + 1][sColumn].selectable = false;
                }
            }
        }

        //compruebo el cuadrado derecho
        if(sColumn  < 6 ){ // si es == 6 no tiene cuadrado a su derecha
            if(!allSquares[sRow][sColumn + 1].visited) { // si el derecho aun no ha sido visitado
                if (allSquares[sRow][sColumn + 1].getColor() == GRAY) { // si el derecho está vacío
                    allSquares[sRow ][sColumn + 1].selectable = true;
                    visitNeighbours(sRow  ,sColumn + 1); //llamada "recursiva"
                } else {
                    allSquares[sRow][sColumn + 1].selectable = false;
                }
            }
        }

        //compruebo el cuadrado izquierdo
        if(sColumn  > 0 ){ // si es == 0 no tiene cuadrado a su izquierda
            if(!allSquares[sRow][sColumn - 1].visited) { // si el izquierdo aun no ha sido visitado
                if (allSquares[sRow][sColumn - 1].getColor() == GRAY) { // si el izquierdo está vacío
                    allSquares[sRow ][sColumn - 1].selectable = true;
                    visitNeighbours(sRow  ,sColumn - 1); //llamada "recursiva"
                } else {
                    allSquares[sRow][sColumn - 1].selectable = false;
                }
            }
        }
    }


    private void createSquareRandom(int numberSquad) {

        int pos = randomPosition.nextInt(availablePositions.size()-0);
        String position = availablePositions.get(pos);
        int row =  Character.getNumericValue(position.charAt(0));
        int column = Character.getNumericValue(position.charAt(1));
        selectColumn = column;
        selectRow = row;

        allSquares[row][column].setColor(squaresPreview[numberSquad]);
        availablePositions.remove(pos);

        if(lineChecker.checkForLine(row,column,allSquares)){
            state = State.SQUARES_DESAPPEAR;
        }


    }

    private void nextColor() {
        for (int i = 0; i < level; i++) { // ************ CAMBIAR EN FUNCION DEL NIVEL ***********
            squaresPreview[i]=colors[randomColor.nextInt(5 - 0)];
        }

    }


    public void restartGame() {
        state = State.ON_GAME;
        availablePositions.clear();
        startLevel();
    }
}
