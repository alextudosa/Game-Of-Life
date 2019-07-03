package com.example.gameoflife;

import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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

        final EditText addFileName = findViewById(R.id.fileNameInput);
        updateSpinnerOnDataChange();

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

        Button saveButton = findViewById(R.id.SaveBtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String getFileName = addFileName.getText().toString();
                saveMatrixInFile(getFileName, matrixState0.length, matrixState0[0].length);
                clearSpinnerOnDataChange();

            }
        });



        final Spinner listOfFilesInSpinner = (Spinner) findViewById(R.id.ListBtn);




        listOfFilesInSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                createInitialMatrix0();
                setValueFirstSetOfButtons();
                int i=1, j=1;
                String selectedPathInSpinner = listOfFilesInSpinner.getSelectedItem().toString();
                String selectedPathInSpinnerWithExtension = selectedPathInSpinner + ".txt";
  //              Log.w("Create File", "Path is:  " + searchForFilesInDirectory(selectedPathInSpinner));
                String filePath = searchForFilesInDirectory(selectedPathInSpinnerWithExtension);
                try {
                    BufferedReader text = new BufferedReader(new FileReader(filePath));
                    String line;
                    while((line = text.readLine()) != null){

                        String[] values = line.split(",");

                        for (String val : values){
                            int valPosition = Integer.parseInt(val);
                            matrixState0[i][j] = valPosition;
//                            Log.w("Create File", "Path is: " +i + "" + j + " " + matrixState0[i][j]);
                            j++;
                        }
                        j=1;
                        i++;
                    }

                    text.close();
                    setValueFirstSetOfButtonsOnLoad();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private String searchForFilesInDirectory(String path){
        String allPath = "";

        File pathToDir = new File(Environment.getExternalStorageDirectory() + File.separator,"GameOfLifeModels");
        File filesDir = new File(String.valueOf(pathToDir));
        File[] files = filesDir.listFiles();
        for (int i=0; i<files.length; i++){
            if (files[i].getName().equals(path)){
                allPath = files[i].getPath();
            }
        }


        return allPath;
    }


    private void updateSpinnerOnDataChange(){

        Spinner listOfFilesInSpinner = (Spinner) findViewById(R.id.ListBtn);
        ArrayAdapter<String> adapter;
        listOfFilesCreated();


        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, fileNamesForSpinner);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listOfFilesInSpinner.setAdapter(adapter);
    }

    private void clearSpinnerOnDataChange(){

        Spinner listOfFilesInSpinner = (Spinner) findViewById(R.id.ListBtn);
        ArrayAdapter<String> adapter;
        listOfFilesCreated();

        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, fileNamesForSpinner);
        adapter.clear();
        listOfFilesInSpinner.setAdapter(adapter);
        updateSpinnerOnDataChange();

    }

    private List<String> fileNamesForSpinner = new ArrayList<>();

    private void listOfFilesCreated(){

        File path = new File(Environment.getExternalStorageDirectory() + File.separator,"GameOfLifeModels");
        File filesDir = new File(String.valueOf(path));
        if (filesDir.exists()) {
            File[] files = filesDir.listFiles();

            for (int i = 0; i < files.length; i++) {
                    String nameWithExtension = files[i].getName();
                    String nameWithoutExtension = nameWithExtension.substring(0, nameWithExtension.length() - 4);
                    fileNamesForSpinner.add(nameWithoutExtension);

            }
        }

    }

    private void saveMatrixInFile(String fileName, int line, int col){

        String createFileName = fileName + ".txt";


          File myWorkingDir = new File(Environment.getExternalStorageDirectory() + File.separator,"GameOfLifeModels");
        File file = new File(myWorkingDir + File.separator, createFileName);


        try {

            if(!myWorkingDir.exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream createFile = new FileOutputStream(file);

         //   BufferedWriter writeInFile = new BufferedWriter(new FileWriter(file));

            for(int i = 1; i < line -1; i++){
                for (int j = 1; j < col -1; j++){
                    createFile.write((matrixState0[i][j] + ((j == matrixState0[0].length-2) ? "\n" : ",")).getBytes());
                }

            }

            createFile.close();
            Toast.makeText(this, "Saved",Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "File Not Found",Toast.LENGTH_SHORT).show();
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Error Saving",Toast.LENGTH_SHORT).show();
//            Log.w("Create File", "Failed to write in " + file.toString());
        }



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



    private void setValueFirstSetOfButtonsOnLoad() {

        for (int i = 1; i < matrixState0.length-1; i++) {
            for (int j = 1; j < matrixState0[0].length - 1; j++) {


                if (matrixState0[i][j] == 1) {
                    buttonsCreated[i - 1][j - 1].setBackgroundColor(Color.parseColor("#000000"));
                }else{
                        buttonsCreated[i - 1][j - 1].setBackgroundColor(Color.parseColor("#eeeeee"));
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
        int rowLenght = matrixState1.length;
        int colLenght = matrixState1[0].length;

        if( i == 1  && j == 1){ //for  point 00

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[rowLenght][rowLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[rowLenght][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[rowLenght][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i+1][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i + 1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i + 1][j + 1]) {
                count++;
            }

        }else if( i == 1  && j != 1 && j != colLenght){ //top line

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[rowLenght][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[rowLenght][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[rowLenght][j + 1]) {
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

        }else if (j == 1 && i != 1 && i != rowLenght){ // left column

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i-1][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i+1][colLenght]) {
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

        }else if( i == rowLenght  && j != 1 && j != colLenght){  //bottom line

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[1][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[1][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i - 1][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i - 1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i - 1][j + 1]) {
                count++;
            }
        }else if( j == colLenght && i != rowLenght  && i != 1){ // right column

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i - 1][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i + 1][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i - 1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i + 1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i - 1][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i + 1][j - 1]) {
                count++;
            }

        }else if( i == rowLenght && j == 0 ){ //for point 50

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i - 1][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[rowLenght][rowLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[1][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[1][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[1][colLenght + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i - 1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i - 1][j + 1]) {
                count++;
            }
        }else if( i == rowLenght && j == colLenght ) { //for point 55

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[1][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[1][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[rowLenght][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[rowLenght - 1][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i - 1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i][j - 1]) {
                count++;
            }

        }else if( i == 1 && j == colLenght ){ //point 05

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[rowLenght][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[rowLenght][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[rowLenght][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[1][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i + 1][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i + 1][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] != matrixState0[i + 1][j]) {
                count++;
            }
        }else {

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

        }

        return count;
    }


    private int searchAliveCellsForAliveCells(int i, int j) {

        int count = 0;

        int rowLenght = matrixState1.length;
        int colLenght = matrixState1[0].length;

        if( i == 1  && j == 1){ //for  point 00

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[rowLenght][rowLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[rowLenght][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[rowLenght][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i+1][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i + 1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i + 1][j + 1]) {
                count++;
            }

        }else if( i == 1  && j != 1 && j != colLenght){ //top line

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[rowLenght][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[rowLenght][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[rowLenght][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i + 1][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i + 1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i + 1][j + 1]) {
                count++;
            }

        }else if (j == 1 && i != 1 && i != rowLenght){ // left column

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i-1][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i+1][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i + 1][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i + 1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i + 1][j + 1]) {
                count++;
            }

        }else if( i == rowLenght  && j != 1 && j != colLenght){  //bottom line

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[1][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[1][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i - 1][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i - 1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i - 1][j + 1]) {
                count++;
            }
        }else if( j == colLenght && i != rowLenght  && i != 1){ // right column

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i - 1][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i + 1][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i - 1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i + 1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i - 1][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i + 1][j - 1]) {
                count++;
            }

        }else if( i == rowLenght && j == 0 ){ //for point 50

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i - 1][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[rowLenght][rowLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[1][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[1][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[1][colLenght + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i - 1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i - 1][j + 1]) {
                count++;
            }
        }else if( i == rowLenght && j == colLenght ) { //for point 55

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[1][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[1][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[rowLenght][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[rowLenght - 1][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][j + 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i - 1][j]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][j - 1]) {
                count++;
            }

        }else if( i == 1 && j == colLenght ){ //point 05

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[rowLenght][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[rowLenght][colLenght]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[rowLenght][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[1][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i + 1][1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i + 1][j - 1]) {
                count++;
            }

            if (matrixState0[i][j] == 0 && matrixState0[i][j] == matrixState0[i + 1][j]) {
                count++;
            }
        }else {

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
        }

        return count;
    }
}










