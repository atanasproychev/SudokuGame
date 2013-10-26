/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku_desktop;

import java.io.File;
import java.util.Random;
import java.util.Stack;
import javax.swing.JOptionPane;

/**
 *
 * @author Nasko_Nastolen
 */
public class SudokuSolver {

    private int selectedCell;
    private Stack<String> moves;
    private Stack<String> redoMoves;
    private File saveFileName;
    private int[][] actual;
    private int[][] state; // 0-empty; 1-filled originally; 2-filled by solver; 3-filled by user; 4-9,11-13 - filled by user but corrected by solver
    private int[][] actualBackup;
    private int seconds;
    private boolean gameStarted;
    
    private String[][] possible;
    private boolean hintMode;
    
    private boolean bruteForceStop = false;
    private Stack<int[][]> actualStack;
    private Stack<String[][]> possibleStack;
    
    private int totalscore;
    private Random generator;
    
    public SudokuSolver() {
        actual = new int[9][9];
        state = new int[9][9];
        actualBackup = new int[9][9];
        possible = new String[9][9];
        moves = new Stack();
        redoMoves = new Stack();
        gameStarted = false;
        generator = new Random();
        actualStack = new Stack();
        possibleStack = new Stack();
    }

    public int getActual(int row, int col)
    {
        return actual[row][col];
    }

    public int getState(int row, int col)
    {
        return state[row][col];
    }
    public void setActual(int row, int col, int value)
    {
        actual[row][col] = value;
    }

    public void setActual(String sudokuAsString)
    {
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                actual[i][j] = sudokuAsString.charAt(i * 9 + j) - '0';
    }
    
    public String getPossible(int row, int col)
    {
        return possible[row][col];
    }

    public void setPossible(int row, int col, String value)
    {
        possible[row][col] = value;
    }
    
    public void resetActual()
    {
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                actual[i][j] = 0;
            }
        }            
    }
    
    public void resetPossible()
    {
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                possible[i][j] = "";
            }
        }            
    }
    
    public boolean isMoveValid(int row, int col, int value)
    {
        if(value == 0)
            return true;
        for(int i = 0; i < 9; i++)
            if(actual[i][col] == value || actual[row][i] == value)
                return false;
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for(int k = 0; k <= 2; k++)
            for(int p = 0; p <= 2; p++)
                if(actual[startRow + k][startCol + p] == value)
                    return false;
        return true;
    }
    
    public boolean isPuzzleSolved()
    {
        String pattern;
        for(int i = 0; i < 9; i++) // check row by row
        {
            pattern = "123456789";
            for(int j = 0; j < 9; j++)
            {
                pattern = pattern.replaceFirst("" + actual[i][j] , "");
            }
            if(pattern.length() > 0)
                return false;
        }
        for(int i = 0; i < 9; i++) // check col by col
        {
            pattern = "123456789";
            for(int j = 0; j < 9; j++)
            {
                pattern = pattern.replaceFirst("" + actual[j][i] , "");
            }
            if(pattern.length() > 0)
                return false;
        }
        for(int j = 0; j < 9; j += 3) // check by minigrid
        {
            pattern = "123456789";
            for(int i = 0; i < 9; i += 3)
            {
                for(int p = 0; p <= 2; p++)
                    for(int k = 0; k <= 2; k++)
                        pattern = pattern.replaceFirst("" + actual[i + k][j + p] , "");
            }
            if(pattern.length() > 0)
                return false;
        }
        return true;
    }
    
    public boolean solvePuzzle() throws Exception
    {
        boolean changes = false;
        boolean exitLoop = false;
        try
        {
            do//look for triplets in columns
            {
                do//look for triplets in rows
                {
                    do//look for triplets in minigrids
                    {
                        do//look for twins in columns
                        {
                            do//look for twins in rows
                            {
                                do//look for twins in minigrids
                                {
                                    do//look for lone rangers in columns
                                    {
                                        do//look for lone rangers in rows
                                        {
                                            do//look for lone rangers in minigrids
                                            {
                                                do//column, row and minigrid elimination
                                                {
                                                    changes = checkRowsAndColumns();
                                                    System.out.println(changes);
                                                    if((hintMode && changes) || isPuzzleSolved())
                                                    {
                                                        exitLoop = true;
                                                        break;
                                                    }
                                                }while(changes);
                                                if(exitLoop)
                                                    break;
                                                //test for lone rangers in minigrids
                                                changes = lookForLoneRangersInMinigrids();
                                                System.out.println(changes);
                                                if((hintMode && changes) || isPuzzleSolved())
                                                {
                                                    exitLoop = true;
                                                    break;
                                                }
                                            }while(changes);
                                            if(exitLoop)
                                                break;
                                            //test for lone rangers in rows
                                            changes = lookForLoneRangersInRows();
                                            System.out.println(changes);
                                            if((hintMode && changes) || isPuzzleSolved())
                                            {
                                                exitLoop = true;
                                                break;
                                            }
                                        }while(changes);
                                        if(exitLoop)
                                            break;
                                        //test for lone rangers in columns
                                        changes = lookForLoneRangersInColumns();
                                        System.out.println(changes);
                                        if((hintMode && changes) || isPuzzleSolved())
                                        {
                                            exitLoop = true;
                                            break;
                                        }
                                    }while(changes);
                                    if(exitLoop)
                                        break;
                                    //test for twins in minigrids
                                    changes = lookForTwinsInMinigrids();
                                    System.out.println(changes);
                                    if((hintMode && changes) || isPuzzleSolved())
                                    {
                                        exitLoop = true;
                                        break;
                                    }
                                }while(changes);
                                if(exitLoop)
                                    break;
                                //test for twins in rows
                                changes = lookForTwinsInRows();
                                System.out.println(changes);
                                if((hintMode && changes) || isPuzzleSolved())
                                {
                                    exitLoop = true;
                                    break;
                                }
                            }while(changes);
                            if(exitLoop)
                                break;
                            //test for twins in columns
                            changes = lookForTwinsInColumns();
                            System.out.println(changes);
                            if((hintMode && changes) || isPuzzleSolved())
                            {
                                exitLoop = true;
                                break;
                            }
                        }while(changes);
                        if(exitLoop)
                            break;
                        //test for triplets in minigrids
                        changes = lookForTripletsInMinigrids();
                        System.out.println(changes);
                        if((hintMode && changes) || isPuzzleSolved())
                        {
                            exitLoop = true;
                            break;
                        }
                    }while(changes);
                    if(exitLoop)
                        break;
                    //test for triplets in rows
                    changes = lookForTripletsInRows();
                    System.out.println(changes);
                    if((hintMode && changes) || isPuzzleSolved())
                    {
                        exitLoop = true;
                        break;
                    }
                }while(changes);
                if(exitLoop)
                    break;
                //test for triplets in columns
                changes = lookForTripletsInColumns();
                System.out.println(changes);
                if((hintMode && changes) || isPuzzleSolved())
                {
                    exitLoop = true;
                    break;
                }
            }while(changes);
        }
        catch(Exception ex)
        {
            System.out.println("SolvePuzzle:\n");
            ex.printStackTrace();
            throw new Exception("Invalid Move");
        }
        if(isPuzzleSolved())
        {
//            puzzleSolved();////////////////////////////////////////////////////////////////////////
            return true;
        }
        else
            return false;
    }
    
    public String calculatePossibleValues(int row, int col) throws Exception
    {
        String possibleValues;//System.out.println(possible[row][col]);
        if(possible[row][col] == null || possible[row][col].equals(""))
            possibleValues = "123456789";
        else
            possibleValues = possible[row][col];
        //System.out.println("pos" + possibleValues + "end");
        for(int i = 0; i < 9; i++)
        {
            if(actual[i][col] != 0)
            {
                possibleValues = possibleValues.replaceFirst(String.valueOf(actual[i][col]) , "");
            }
        }
        for(int j = 0; j < 9; j++)
        {
            if(actual[row][j] != 0)
            {
                possibleValues = possibleValues.replaceFirst(String.valueOf(actual[row][j]) , "");
            }
        }
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for(int k = startRow; k <= startRow + 2; k++)
            for(int p = startCol; p <= startCol + 2; p++)
                if(actual[k][p] != 0)
                    possibleValues = possibleValues.replaceFirst(String.valueOf(actual[k][p]), "");
        if(possibleValues.equals(""))
        {
            System.out.println(row + ", " + col);
            throw new Exception("Invalid Move");
        }
            
        //System.out.println("pos" + possibleValues + "end");
        return possibleValues;
    }
    
    public boolean checkRowsAndColumns() throws Exception
    {
        System.out.println("checkRowsAndColumns()");
        boolean changes = false;
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
            {
                if(actual[i][j] == 0)
                {
                    try
                    {
                        possible[i][j] = calculatePossibleValues(i, j);
                    }catch(Exception ex)
                    {
//                        JOptionPane.showMessageDialog(this, "Не може да се направи следващ ход.\nМоля, върнете един ход назад.", "Грешка", JOptionPane.ERROR_MESSAGE);/////////////////////
                        ex.printStackTrace();
                        System.out.println("Possible[i][j], i, j" + possible[i][j] + ", " + i + ", " + j);
                        throw new Exception("Invalid Move");
                    }
                    //setToolTip
                    if(possible[i][j].length() == 1)
                    {
                        actual[i][j] = Integer.parseInt(possible[i][j]);
//                        setCell(i, j, actual[i][j]);
                        moves.push(String.format("%d%d%d", i, j, actual[i][j]));
//                        setActivity(String.format("На (%d, %d) е добавена стойност %d.", i + 1, j + 1, actual[i][j]));
                        changes = true;
                        if(hintMode)
                            return true;
                        totalscore += 1;
                    }
                }
            }
//        setToolTips();
        return changes;
    }
    
    public boolean lookForLoneRangersInMinigrids()
    {
        System.out.println("lookForLoneRangersInMinigrids()");
        boolean changes = false;
        boolean nextMiniGrid = false;
        int occurrence;
        int colPos, rowPos;
        colPos = 0;
        rowPos = 0;
        for(int n = 1; n <= 9; n++)//check for each number
        {
            for(int i = 0; i < 9; i += 3)//check minigrids
                for(int j = 0; j < 9; j += 3)
                {
                    nextMiniGrid = false;
                    //check in minigrid
                    occurrence = 0;
                    for(int k = 0; k <= 2; k++)
                    {
                        for(int p = 0; p <= 2; p++)
                        {
                            if(actual[i + k][j + p] == 0 && possible[i + k][j + p].indexOf(n) != -1)
                            {
                                occurrence++;
                                colPos = j + p;
                                rowPos = i + k;
                                if(occurrence > 1)
                                {
                                    nextMiniGrid = true;
                                    break;
                                }
                            }
                        }
                        if(nextMiniGrid)
                            break;
                    }
                    if(!nextMiniGrid && occurrence == 1)
                    {//the number is confirmed
//                        setCell(rowPos, colPos, n);
                        moves.push(String.format("%d%d%d", rowPos, colPos, n));
//                        setActivity(String.format("На (%d, %d) е добавена стойност %d.", rowPos + 1, colPos + 1, n));
                        changes = true;
                        if(hintMode)//if is in hint mode
                            return true;
                        totalscore += 2;
                    }
                }
        }
        return changes;
    }
    
    public boolean lookForLoneRangersInRows()
    {
        System.out.println("lookForLoneRangersInRows()");
        boolean changes = false;
        int occurrence;
        int colPos, rowPos;
        colPos = 0;
        rowPos = 0;
        for(int i = 0; i < 9; i++)//check by row
        {
            for(int n = 1; n <= 9; n++)//check for each number
            {
                occurrence = 0;
                for(int j = 0; j < 9; j++)
                {
                    if(actual[i][j] == 0 && possible[i][j].indexOf(n) != -1)
                    {
                        occurrence++;
                        if(occurrence > 1)
                        {
                            break;
                        }
                        colPos = j;
                        rowPos = i;
                    }
                }
                if(occurrence == 1)
                {//the number is confirmed
//                    setCell(rowPos, colPos, n);
                    moves.push(String.format("%d%d%d", rowPos, colPos, n));
//                    setActivity(String.format("На (%d, %d) е добавена стойност %d.", rowPos + 1, colPos + 1, n));
                    changes = true;
                    if(hintMode)//if is in hint mode
                        return true;
                    totalscore += 2;
                }
            }
        }
        return changes;
    }
    
    public boolean lookForLoneRangersInColumns()
    {
        System.out.println("lookForLoneRangersInColumns()");
        boolean changes = false;
        int occurrence;
        int colPos, rowPos;
        colPos = 0;
        rowPos = 0;
        for(int j = 0; j < 9; j++)//check by column
        {
            for(int n = 1; n <= 9; n++)//check for each number
            {
                occurrence = 0;
                for(int i = 0; i < 9; i++)
                {
                    if(actual[i][j] == 0 && possible[i][j].indexOf(n) != -1)
                    {
                        occurrence++;
                        if(occurrence > 1)
                        {
                            break;
                        }
                        colPos = j;
                        rowPos = i;
                    }
                }
                if(occurrence == 1)
                {//the number is confirmed
//                    setCell(rowPos, colPos, n);
                    moves.push(String.format("%d%d%d", rowPos, colPos, n));
//                    setActivity(String.format("На (%d, %d) е добавена стойност %d.", rowPos + 1, colPos + 1, n));
                    changes = true;
                    if(hintMode)//if is in hint mode
                        return true;
                    totalscore += 2;
                }
            }
        }
        return changes;
    }
    
    public boolean lookForTwinsInMinigrids() throws Exception
    {
        System.out.println("lookForTwinsInMinigrids()");
        boolean changes = false;
        
        for(int i = 0; i < 9; i++)//look for twins in each cell
            for(int j = 0; j < 9; j++)
            {
                if(actual[i][j] == 0 && possible[i][j].length() == 2)//if cell have two possible values
                {   
                    int startRow = i - (i % 3);//scan the minigrid
                    int startCol = j - (j % 3);
                    for(int k = startRow; k <= startRow + 2; k++)
                    {
                        for(int p = startCol; p <= startCol + 2; p++)
                        {
                            if((i != k && j != p) && (possible[k][p].equals(possible[i][j])))
                            {
                                //found the twins
                                for(int kk = startRow; kk <= startRow + 2; kk++)//remove from all the other possibles in this minigrid
                                {
                                    for(int pp = startCol; pp <= startCol + 2; pp++)
                                    {
                                        if(actual[kk][pp] == 0 && !possible[kk][pp].equals(possible[i][j]))
                                        {
                                            String originalPossibleValues = possible[kk][pp];
                                            //remove first and second twin number
                                            possible[kk][pp] = possible[kk][pp].replaceFirst(String.valueOf(possible[i][j].charAt(0)), "");
                                            possible[kk][pp] = possible[kk][pp].replaceFirst(String.valueOf(possible[i][j].charAt(1)), "");
                                            if(!originalPossibleValues.equals(possible[kk][pp]))
                                                changes = true;
                                            if(possible[kk][pp].equals(""))
                                                throw new Exception("Invalid Move");
                                            if(possible[kk][pp].length() == 1)
                                            {
//                                                setCell(kk, pp, Integer.parseInt(possible[kk][pp]));
                                                moves.push(String.format("%d%d%s", kk, pp, possible[kk][pp]));
//                                                setActivity(String.format("На (%d, %d) е добавена стойност %s.", kk + 1, pp + 1, possible[kk][pp]));
                                                if(hintMode)
                                                    return true;
                                                totalscore += 3;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        return changes;
    }
    
    public boolean lookForTwinsInRows() throws Exception
    {
        System.out.println("lookForTwinsInRows()");
        boolean changes = false;
        
        for(int i = 0; i < 9; i++)//for each row, in each column, look for twins
            for(int j = 0; j < 9; j++)
            {
                if(actual[i][j] == 0 && possible[i][j].length() == 2)//if cell have two possible values
                {   
                    for(int p = j + 1; p < 9; p++)
                    {
                        if(possible[i][p].equals(possible[i][j]))
                        {
                            //found the twins
                            for(int pp = 0; pp < 9; pp++)//remove twins from other possibles in this column
                            {
                                if(actual[i][pp] == 0 && p != pp && j != pp)
                                {
                                    String originalPossibleValues = possible[i][pp];
                                    //remove first and second twin number
                                    possible[i][pp] = possible[i][pp].replaceFirst(String.valueOf(possible[i][j].charAt(0)), "");
                                    possible[i][pp] = possible[i][pp].replaceFirst(String.valueOf(possible[i][j].charAt(1)), "");
                                    if(!originalPossibleValues.equals(possible[i][pp]))
                                        changes = true;
                                    if(possible[i][pp].equals(""))
                                    {
                                        System.out.println("i, j, p, pp " + i + ", " + j + ", " + p + ", " + pp);
                                        throw new Exception("Invalid Move");
                                    }
                                    if(possible[i][pp].length() == 1)
                                    {
//                                        setCell(i, pp, Integer.parseInt(possible[i][pp]));
                                        moves.push(String.format("%d%d%s", i, pp, possible[i][pp]));
//                                        setActivity(String.format("На (%d, %d) е добавена стойност %s.", i + 1, pp + 1, possible[i][pp]));
                                        if(hintMode)
                                            return true;
                                        totalscore += 3;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        return changes;
    }
    
    public boolean lookForTwinsInColumns() throws Exception
    {
        System.out.println("lookForTwinsInColumns()");
        boolean changes = false;
        
        for(int j = 0; j < 9; j++)//for each row, in each column, look for twins
            for(int i = 0; i < 9; i++)
            {
                if(actual[i][j] == 0 && possible[i][j].length() == 2)//if cell have two possible values
                {   
                    for(int k = i + 1; k < 9; k++)
                    {
                        if(possible[k][j].equals(possible[i][j]))
                        {
                            //found the twins
                            for(int kk = 0; kk < 9; kk++)//remove twins from other possibles in this row
                            {
                                if(actual[kk][j] == 0 && k != kk && i != kk)
                                {
                                    String originalPossibleValues = possible[kk][j];
                                    //remove first and second twin number
                                    possible[kk][j] = possible[kk][j].replaceFirst(String.valueOf(possible[i][j].charAt(0)), "");
                                    possible[kk][j] = possible[kk][j].replaceFirst(String.valueOf(possible[i][j].charAt(1)), "");
                                    if(!originalPossibleValues.equals(possible[kk][j]))
                                        changes = true;
                                    if(possible[kk][j].equals(""))
                                        throw new Exception("Invalid Move");
                                    if(possible[kk][j].length() == 1)
                                    {
//                                        setCell(kk, j, Integer.parseInt(possible[kk][j]));
                                        moves.push(String.format("%d%d%s", kk, j, possible[kk][j]));
//                                        setActivity(String.format("На (%d, %d) е добавена стойност %s.", kk + 1, j + 1, possible[kk][j]));
                                        if(hintMode)
                                            return true;
                                        totalscore += 3;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        return changes;
    }
    //triplets
    
    public boolean lookForTripletsInMinigrids() throws Exception
    {
        System.out.println("lookForTripletsInMinigrids()");
        boolean changes = false;
        
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++)
            {
                if(actual[i][j] == 0 && possible[i][j].length() == 3)//if cell have three possible values
                {
                    StringBuilder tripletsLocation = new StringBuilder();
                    tripletsLocation.append(i).append(j);
                    int startRow = i - (i % 3);//scan the minigrid
                    int startCol = j - (j % 3);
                    for(int k = startRow; k <= startRow + 2; k++)
                    {
                        for(int p = startCol; p <= startCol + 2; p++)
                        {
                            if((i != k && j != p) && ((possible[k][p].equals(possible[i][j])) || (possible[k][p].length() == 2 && possible[i][j].contains(String.valueOf(possible[k][p].charAt(0)))
                                                                                                                               && possible[i][j].contains(String.valueOf(possible[k][p].charAt(1))))))
                                tripletsLocation.append(k).append(p);
                        }
                    }
                    if(tripletsLocation.length() == 6)
                    {
                        for(int k = startRow; k <= startRow + 2; k++)
                        {
                            for(int p = startCol; p <= startCol + 2; p++)
                            {
                                if(actual[k][p] == 0 && k != Integer.parseInt(tripletsLocation.substring(0, 1))
                                                     && p != Integer.parseInt(tripletsLocation.substring(1, 2))
                                                     && k != Integer.parseInt(tripletsLocation.substring(2, 3))
                                                     && p != Integer.parseInt(tripletsLocation.substring(3, 4))
                                                     && k != Integer.parseInt(tripletsLocation.substring(4, 5))
                                                     && p != Integer.parseInt(tripletsLocation.substring(5, 6)))
                                {
                                    String originalPossibleValues = possible[k][p];
                                    possible[k][p] = possible[k][p].replaceFirst(String.valueOf(possible[i][j].charAt(0)), "");
                                    possible[k][p] = possible[k][p].replaceFirst(String.valueOf(possible[i][j].charAt(1)), "");
                                    possible[k][p] = possible[k][p].replaceFirst(String.valueOf(possible[i][j].charAt(2)), "");
//                                    setToolTip(k, p);
                                    if(!originalPossibleValues.equals(possible[k][p]))
                                        changes = true;
                                    if(possible[k][p].equals(""))
                                        throw new Exception("Invalid Move");
                                    if(possible[k][p].length() == 1)
                                    {
//                                        setCell(k, p, Integer.parseInt(possible[k][p]));
//                                        setToolTip(k, p);
                                        moves.push(String.format("%d%d%s", k, p, possible[k][p]));
//                                        setActivity(String.format("На (%d, %d) е добавена стойност %s.", k + 1, p + 1, possible[k][p]));
                                        if(hintMode)
                                            return true;
                                        totalscore += 4;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return changes;
    }
    
    public boolean lookForTripletsInRows() throws Exception
    {
        System.out.println("lookForTripletsInRows()");
        boolean changes = false;
        
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++)
            {
                if(actual[i][j] == 0 && possible[i][j].length() == 3)//if cell have three possible values
                {
                    StringBuilder tripletsLocation = new StringBuilder();
                    tripletsLocation.append(i).append(j);
                    for(int p = 0; p < 9; p++)
                    {
                        if((j != p) && ((possible[i][p].equals(possible[i][j])) || (possible[i][p].length() == 2 && possible[i][j].contains(String.valueOf(possible[i][p].charAt(0)))
                                                                                                                 && possible[i][j].contains(String.valueOf(possible[i][p].charAt(1))))))
                            tripletsLocation.append(i).append(p);
                    }
                    if(tripletsLocation.length() == 6)
                    {
                        for(int p = 0; p < 9; p++)
                        {
                            if(actual[i][p] == 0 && p != Integer.parseInt(tripletsLocation.substring(1, 2))
                                                 && p != Integer.parseInt(tripletsLocation.substring(3, 4))
                                                 && p != Integer.parseInt(tripletsLocation.substring(5, 6)))
                                                    
                            {
                                String originalPossibleValues = possible[i][p];
                                possible[i][p] = possible[i][p].replaceFirst(String.valueOf(possible[i][j].charAt(0)), "");
                                possible[i][p] = possible[i][p].replaceFirst(String.valueOf(possible[i][j].charAt(1)), "");
                                possible[i][p] = possible[i][p].replaceFirst(String.valueOf(possible[i][j].charAt(2)), "");
//                                setToolTip(i, p);
                                if(!originalPossibleValues.equals(possible[i][p]))
                                    changes = true;
                                if(possible[i][p].equals(""))
                                    throw new Exception("Invalid Move");
                                if(possible[i][p].length() == 1)
                                {
//                                    setCell(i, p, Integer.parseInt(possible[i][p]));
//                                    setToolTip(i, p);
                                    moves.push(String.format("%d%d%s", i, p, possible[i][p]));
//                                    setActivity(String.format("На (%d, %d) е добавена стойност %s.", i + 1, p + 1, possible[i][p]));
                                    if(hintMode)
                                        return true;
                                    totalscore += 4;
                                }
                            }
                        }
                    }
                }
            }
        }
        return changes;
    }
    
    public boolean lookForTripletsInColumns() throws Exception
    {
        System.out.println("lookForTripletsInColumns()");
        boolean changes = false;
        
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++)
            {
                if(actual[i][j] == 0 && possible[i][j].length() == 3)//if cell have three possible values
                {
                    StringBuilder tripletsLocation = new StringBuilder();
                    tripletsLocation.append(i).append(j);
                    for(int k = 0; k < 9; k++)
                    {
                        if((i != k) && ((possible[k][j].equals(possible[i][j])) || (possible[k][j].length() == 2 && possible[i][j].contains(String.valueOf(possible[k][j].charAt(0)))
                                                                                                                           && possible[i][j].contains(String.valueOf(possible[k][j].charAt(1))))))
                            tripletsLocation.append(k).append(j);
                    }
                    if(tripletsLocation.length() == 6)
                    {
                        for(int k = 0; k < 9; k++)
                        {
                            if(actual[k][j] == 0 && k != Integer.parseInt(tripletsLocation.substring(0, 1))
                                                 && k != Integer.parseInt(tripletsLocation.substring(2, 3))
                                                 && k != Integer.parseInt(tripletsLocation.substring(4, 5)))
                            {
                                String originalPossibleValues = possible[k][j];
                                possible[k][j] = possible[k][j].replaceFirst(String.valueOf(possible[i][j].charAt(0)), "");
                                possible[k][j] = possible[k][j].replaceFirst(String.valueOf(possible[i][j].charAt(1)), "");
                                possible[k][j] = possible[k][j].replaceFirst(String.valueOf(possible[i][j].charAt(2)), "");
//                                setToolTip(k, j);
                                if(!originalPossibleValues.equals(possible[k][j]))
                                    changes = true;
                                if(possible[k][j].equals(""))
                                    throw new Exception("Invalid Move");
                                if(possible[k][j].length() == 1)
                                {
//                                    setCell(k, j, Integer.parseInt(possible[k][j]));
//                                    setToolTip(k, j);
                                    moves.push(String.format("%d%d%s", k, j, possible[k][j]));
//                                    setActivity(String.format("На (%d, %d) е добавена стойност %s.", k + 1, j + 1, possible[k][j]));
                                    if(hintMode)
                                        return true;
                                    totalscore += 4;
                                }
                            }
                        }
                    }
                }
            }
        }
        return changes;
    }
    
    public int[] findCellWithFewestPossibleValues()
    {
        System.out.println("findCellWithFewestPossibleValues()");
        int min = 10;
        int[] coordinates = new int[2];
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
            {
                if(actual[i][j] == 0 && possible[i][j].length() < min)
                {//find cell with minimum possible values
                    min = possible[i][j].length();
                    coordinates[0] = i;
                    coordinates[1] = j;
                }
            }
        return coordinates;
    }
    
    public void solvePuzzleByBruteForce()
    {
        System.out.println("solvePuzzleByBruteForce()");
        int[] coordinates = new int[2];
        coordinates = findCellWithFewestPossibleValues();
        String possibleValues = possible[coordinates[0]][coordinates[1]];
        actualStack.push(actual);
        possibleStack.push(possible);
        totalscore += 5;
        possibleValues = randomizePossibleValues(possibleValues);
        for(int i = 0; i < possibleValues.length() - 1; i++)
        {
            //saves the move into stack
            int value = Integer.parseInt(String.valueOf(possibleValues.charAt(i)));
//            setCell(coordinates[0], coordinates[1], value);
            setActual(coordinates[0], coordinates[1], value);
            moves.push(String.format("%d%d%d", coordinates[0], coordinates[1], value));
            System.out.printf("%d%d%d", coordinates[0], coordinates[1], value);
//            setActivity(String.format("На (%d, %d) е добавена стойност %d.", coordinates[0] + 1, coordinates[1] + 1, value));
            try
            {
                if(solvePuzzle())
                {//stop recursion
                    bruteForceStop = true;
                    return;
                }
                else
                {
                    solvePuzzleByBruteForce();
                    if(bruteForceStop)
                        return;
                }
            }
            catch(Exception ex)
            {
//                setActivity("Грешен ход! Връщане...");
                totalscore += 5;
                actual = actualStack.pop();
//                System.out.println(printMatrix(actual));
                possible = possibleStack.pop();
            }
        }
    }
    
    private String randomizePossibleValues(String possible)
    {
        //StringBuilder result = new StringBuilder(possible.length());
//        System.out.println(possible);
        char[] result ;//= new char[possible.length()];
        result = possible.toCharArray();
        int j;
        char temp;
        for(int i = 0; i < possible.length(); i++)
        {
            j = generator.nextInt(possible.length());
            temp = result[i];
            result[i] = result[j];
            result[j] = temp;
        }
//        System.out.println(result);
        return String.copyValueOf(result);
    }
    
    private String generateNewPuzzle(int level)
    {
        int numberOfEmptyCells = 0;
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                actual[i][j] = 0;
                possible[i][j] = "";
            }
            
        }
        
        actualStack.clear();
        possibleStack.clear();
        //solves an empty grid
        try
        {
            if(!solvePuzzle())
            {
                solvePuzzleByBruteForce();
            }
        }
        catch (Exception e)
        {
            System.out.println("Exception in generateNewPuzzle");
            return "";
        }
        
        System.out.println("First:\n" + printMatrix());
        
        actualBackup = actual.clone();
        switch(level)
        {
            case 1:
                numberOfEmptyCells = randomNumberBetween(40, 45);
                break;
            case 2:
                numberOfEmptyCells = randomNumberBetween(46, 49);
                break;
            case 3:
                numberOfEmptyCells = randomNumberBetween(50, 53);
                break;
            case 4:
                numberOfEmptyCells = randomNumberBetween(54, 58);
                break;
        }
        
        actualStack.clear();
        possibleStack.clear();
        bruteForceStop = false;
        
//        JOptionPane.showMessageDialog(this, "Заредено!!!", "Браво", JOptionPane.INFORMATION_MESSAGE);
        
        createEmptyCells(numberOfEmptyCells);
        
//        setBoard();
        System.out.println("Matrix with empty cells:\n" + printMatrix());
        
        StringBuilder sudokuToString = new StringBuilder();
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                sudokuToString.append(actual[i][j]);
        
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!verify the puzzle has only one solution!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        
        //verify the puzzle has only one solution
        int tries = 0;
        do
        {            
            totalscore = 0;
            try
            {
                if(!solvePuzzle())
                {
                    if(level < 4)
                    {
                        sudokuToString = vacateAnotherPairOfCells(sudokuToString);
                        tries++;
                    }
                    else
                    {
                        solvePuzzleByBruteForce();
                        break;
                    }
                }
                else
                    break;
            }
            catch (Exception e)
            {
                return "";
            }
            
            if(tries > 50)
                return "";
        }
        while(true);
        System.out.println("sudokuToString:   " + sudokuToString);
        setActual(sudokuToString.toString());
        return sudokuToString.toString();//score?
    }
    
    private int randomNumberBetween(int first, int second)
    {
        return first + generator.nextInt(second - first + 1);
    }
    
    private void createEmptyCells(int empty)
    {
        int row, col;
        int[] emptyCells = new int[empty];
        for (int i = 0; i < empty / 2; i++)
        {
            boolean duplicate;
            do
            {
                duplicate = false;
                //get a cell in the first half of grid
                do
                {
                    row = randomNumberBetween(0, 4);////
                    col = randomNumberBetween(0, 8);
                }
                while(row == 4 && col > 4);//////
                for (int j = 0; j < i; j++)
                {
                    if(emptyCells[j] == row * 10 + col)
                    {
                        duplicate = true;
                        break;
                    }
                }

                if(!duplicate)
                {
                    emptyCells[i] = row * 10 + col;
                    actual[row][col] = 0;
                    possible[row][col] = "";

                    //symmetrical...
                    emptyCells[empty - 1 - i] = (8 - row) * 10 + (8 - col);
                    actual[8 - row][8 - col] = 0;
                    possible[8 - row][8 - col] = "";
                }
            }
            while(duplicate);
        }
    }
    
    private StringBuilder vacateAnotherPairOfCells(StringBuilder sudokuAsString)
    {
//        stringToSudoku[i * 9 + j]
        int row = 0, col = 0;
        boolean notFound = true;
        //look for a pair of cells to restore
        for (row = 0; row < 9 && notFound; row++)
        {
            for (col = 0; col < 9 && notFound; col++)
            {
                if(sudokuAsString.charAt(row * 9 + col) == '0')
                    notFound = false;
            }
        }
        
        //restore the value
        sudokuAsString.setCharAt(row * 9 + col, (char) actualBackup[row][col]);
        sudokuAsString.setCharAt((8 - row) * 9 + (8 - col), (char) actualBackup[8 - row][8 - col]);
        
        //look for another pair of cells to vacate
        notFound = true;
        for (row = 0; row < 9 && notFound; row++)
        {
            for (col = 0; col < 9 && notFound; col++)
            {
                if(sudokuAsString.charAt(row * 9 + col) != '0')
                    notFound = false;
            }
        }
        
        //remove the cell
        sudokuAsString.setCharAt(row * 9 + col, '0');
        sudokuAsString.setCharAt((8 - row) * 9 + (8 - col), '0');
        
        //reinitialize the board
        int counter = 0;
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                if((int) sudokuAsString.charAt(counter) != 0)
                {
                    actual[row][col] = (int) sudokuAsString.charAt(counter);
                    possible[row][col] = sudokuAsString.substring(counter, counter + 1);
                }
                else
                {
                    actual[row][col] = 0;
                    possible[row][col] = "";
                }
                counter++;
            }
        }
        return sudokuAsString;
    }
    
    public String getPuzzle(int level)
    {
        String result;
        int count = 0;
        MAKE_RESULT:
        do
        {
            result = generateNewPuzzle(level);
            System.out.println("Total Score: " + totalscore);
            if(!result.isEmpty())
            {
                switch (level) {
                    case 1:
                        if(totalscore >= 42 && totalscore <= 46)
                            break MAKE_RESULT;
                        break;
                    case 2:
                        if(totalscore >= 49 && totalscore <= 53)
                            break MAKE_RESULT;
                        break;
                    case 3:
                        if(totalscore >= 56 && totalscore <= 60)
                            break MAKE_RESULT;
                        break;
                    case 4:
                        if(totalscore >= 112 && totalscore <= 116)
                            break MAKE_RESULT;
                        break;
                }
            }
            count++;
        }
        while(count < 500000);
        System.out.printf("Final matrix:\n%s\n%d", printMatrix(), count);
        return result;
    }
    
    public String printMatrix()
    {
        String result = "";
        for(int i = 0; i < 9; i++)
        {
            if(i % 3 == 0)
                result = String.format("%s\n", result);
            for(int j = 0; j < 9; j++)
            {
                if(j % 3 == 0)
                    result = String.format("%s ", result);
                result = String.format("%s%d", result, actual[i][j]);
            }
            result = String.format("%s\n", result);
        }
        return result;
    }
    
    
    /*public static void main(String[] args) {
        // TODO code application logic here
    }*/
}
