package com.example.gameoflife;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[][] buttonsCreated = new Button[6][6];
    private int[][] matrixState0 = new int[8][8];
    private int[][] matrixState1 = new int[6][6];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LinearLayout layoutButtonsLine1 = (LinearLayout) findViewById(R.id.buttonContainer1);
        LinearLayout layoutButtonsLine2 = (LinearLayout) findViewById(R.id.buttonContainer2);
        LinearLayout layoutButtonsLine3 = (LinearLayout) findViewById(R.id.buttonContainer3);
        LinearLayout layoutButtonsLine4 = (LinearLayout) findViewById(R.id.buttonContainer4);
        LinearLayout layoutButtonsLine5 = (LinearLayout) findViewById(R.id.buttonContainer5);
        LinearLayout layoutButtonsLine6 = (LinearLayout) findViewById(R.id.buttonContainer6);


        createButtonsForLayout(layoutButtonsLine1, 0);
        createButtonsForLayout(layoutButtonsLine2, 1);
        createButtonsForLayout(layoutButtonsLine3, 2);
        createButtonsForLayout(layoutButtonsLine4, 3);
        createButtonsForLayout(layoutButtonsLine5, 4);
        createButtonsForLayout(layoutButtonsLine6, 5);


        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                String buttonID = Integer.toString(i) + Integer.toString(j);
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttonsCreated[i][j] = findViewById(resID);

                setValueFirstSetOfButtons();

                buttonsCreated[i][j].setOnClickListener(this);
            }
        }



        Button nextStepButton = findViewById(R.id.nextStepBtn);
        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                setColorButtonsNextStep();


            }
        });
    }



    @Override
    public void onClick(View v) {
        Button pushedButton = (Button) v;

        int theIDPushedButton = pushedButton.getId();

        for (int i = 0; i < 6; i++){
            for (int j = 0; j < 6; j++){
                int theIDBoardButton = buttonsCreated[i][j].getId();
                if (theIDBoardButton == theIDPushedButton){
                    matrixState0[i+1][j+1] = 1;
                    pushedButton.setBackgroundColor(Color.parseColor("#000000"));
                }
            }
        }
    }

    private void createInitialMatrix0() {

        int stateInactive = 0;
        for (int i = 0; i < matrixState0.length; i++) {
            for (int j = 0; j < matrixState0[0].length; j++) {
                matrixState0[i][j] = stateInactive;

            }
        }
    }

    private void createEmptyMatrix1() {

        int stateInactive = 0;
        for (int i = 0; i < matrixState1.length; i++) {
            for (int j = 0; j < matrixState1[0].length; j++) {
                matrixState1[i][j] = stateInactive;
            }
        }
    }


    private void resetMatrix1() {

        int stateInactive = 0;
        for (int i = 0; i < matrixState1.length; i++) {
            for (int j = 0; j < matrixState1[0].length; j++) {
                matrixState1[i][j] = stateInactive;
            }
        }
    }

    private void moveValueFromMatrix1ToMatrix0() {

        for (int i = 1; i < matrixState0.length-1; i++) {
            for (int j = 1; j < matrixState0[0].length -1; j++) {
                matrixState0[i][j] = matrixState1[i - 1][j - 1];
            }
        }


    }

    private void createButtonsForLayout(LinearLayout selectedLayout, int lineOfMatrix) {
        for (int i = 0; i < 6; i++) {
            final Button myButtons = new Button(this);
            selectedLayout.addView(myButtons);
            String buttonID = Integer.toString(lineOfMatrix) + Integer.toString(i);
            myButtons.setId(Integer.parseInt(buttonID));
            myButtons.setText(buttonID);
            myButtons.setBackgroundColor(Color.parseColor("#eeeeee"));
            myButtons.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));


        }
    }

    private void setValueFirstSetOfButtons() {

        createInitialMatrix0();

        for (int i = 1; i < matrixState0.length; i++) {
            for (int j = 1; j < matrixState0[0].length; j++) {
                if (matrixState0[i][j] == 1) {
                    buttonsCreated[i - 1][j - 1].setBackgroundColor(Color.parseColor("#000000"));
                }
            }
        }
    }


    private void setColorButtonsNextStep() {

        cellBornOrSurvive();

        for (int i = 0; i < matrixState1.length; i++) {
            for (int j = 0; j < matrixState1[0].length; j++) {
                if (matrixState1[i][j] == 1) {

                    buttonsCreated[i][j].setBackgroundColor(Color.parseColor("#000000"));
                }else{

                    buttonsCreated[i][j].setBackgroundColor(Color.parseColor("#eeeeee"));
                }
            }
        }

        moveValueFromMatrix1ToMatrix0();
        resetMatrix1();
    }


    private void cellBornOrSurvive() {

        int countAliveCellsForDeadCells = 0;
        int countAliveCellsForAliveCells = 0;
        createEmptyMatrix1();

        for (int i = 1; i < matrixState0.length - 1; i++) {
            for (int j = 1; j < matrixState0[0].length - 1; j++) {
                countAliveCellsForDeadCells = searchAliveCellsForDeadCells(i, j);

                if (countAliveCellsForDeadCells == 3) {
                    matrixState1[i - 1][j - 1] = 1;

                }

                countAliveCellsForAliveCells = searchAliveCellsForAliveCells(i, j);

                if (countAliveCellsForAliveCells == 2 || countAliveCellsForAliveCells == 3) {
                    matrixState1[i - 1][j - 1] = 1;

                }


            }
        }

    }


    private int searchAliveCellsForDeadCells(int i, int j) {

        int count = 0;


        if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i - 1][j - 1]) {
            count++;
        }

        if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i - 1][j]) {
            count++;
        }

        if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i - 1][j + 1]) {
            count++;
        }

        if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i][j - 1]) {
            count++;
        }

        if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i][j + 1]) {
            count++;
        }

        if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i + 1][j - 1]) {
            count++;
        }

        if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i + 1][j]) {
            count++;
        }

        if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i + 1][j + 1]) {
            count++;
        }

        return count;
    }


    private int searchAliveCellsForAliveCells(int i, int j) {

        int count = 0;


        if (matrixState0[i][j] == 1 && matrixState0[i][j] == matrixState0[i - 1][j - 1]) {
            count++;
        }

        if (matrixState0[i][j] == 1 && matrixState0[i][j] == matrixState0[i - 1][j]) {
            count++;
        }

        if (matrixState0[i][j] == 1 && matrixState0[i][j] == matrixState0[i - 1][j + 1]) {
            count++;
        }

        if (matrixState0[i][j] == 1 && matrixState0[i][j] == matrixState0[i][j - 1]) {
            count++;
        }

        if (matrixState0[i][j] == 1 && matrixState0[i][j] == matrixState0[i][j + 1]) {
            count++;
        }

        if (matrixState0[i][j] == 1 && matrixState0[i][j] == matrixState0[i + 1][j - 1]) {
            count++;
        }

        if (matrixState0[i][j] == 1 && matrixState0[i][j] == matrixState0[i + 1][j]) {
            count++;
        }

        if (matrixState0[i][j] == 1 && matrixState0[i][j] == matrixState0[i + 1][j + 1]) {
            count++;
        }

        return count;
    }
}










