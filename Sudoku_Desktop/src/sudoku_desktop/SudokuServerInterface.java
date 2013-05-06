/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku_desktop;

import java.rmi.Remote;

/**
 *
 * @author Nasko_Nastolen
 */
public interface SudokuServerInterface extends Remote
{
    public int[][] generateSudoku();//generates a new sudoku puzzle
    public int[][] getSolution(int[][] current);//returns the solution of current sudoku puzzle
}
