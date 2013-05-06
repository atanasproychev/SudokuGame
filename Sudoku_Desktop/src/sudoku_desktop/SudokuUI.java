/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku_desktop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Formatter;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Nasko_Nastolen
 */
public class SudokuUI extends javax.swing.JFrame implements Runnable
{

    /**
     * Creates new form Sudoku_UI
     */
    private int selectedCell;
    /*Moves Stacks*/
    private Stack<String> moves;
    private Stack<String> redoMoves;
    private File saveFileName;
//    private int[][] actual;
//    private int[][] actualBackup;
    private int seconds;
    private boolean gameStarted;
    private SudokuSolver puzzle;
    private JLabel[][] cell;
    private final Color DEFAULT_BACKGROUND;
    private final Color FILLED_BACKGROUND;
    private final Font DEFAULT_FONT;
    private final Dimension DEFAULT_DIMENSION;
    
//    private String[][] possible;
    private boolean hintMode;
    
    private boolean bruteForceStop = false;
//    private Stack<int[][]> actualStack;
//    private Stack<String[][]> possibleStack;
    
    private int totalscore;
    private Random generator;
    private Thread timer;
    private Time time;
    
    
    public SudokuUI() {
        //
        initComponents();
        ButtonGroup levelMenu = new ButtonGroup();
        levelMenu.add(btnEasy);
        levelMenu.add(btnMedium);
        levelMenu.add(btnDifficult);
        levelMenu.add(btnVeryDifficult);
        DEFAULT_BACKGROUND = new Color(255, 255, 255);
        FILLED_BACKGROUND = new Color(180, 180, 180);
        DEFAULT_FONT = new Font("Calibri", 0, 17);
        DEFAULT_DIMENSION = new Dimension(32, 32);
//        actual = new int[9][9];
//        actualBackup = new int[9][9];
        initCells();
//        possible = new String[9][9];
        moves = new Stack();
        redoMoves = new Stack();
        gameStarted = false;
        generator = new Random();
//        actualStack = new Stack();
//        possibleStack = new Stack();
        puzzle = new SudokuSolver();
        timer = new Thread();
        time = new Time();
    }
    
    public void initCells()
    {
        cell = new JLabel[9][9];
        GridLayout layout = new GridLayout(9, 9);
        cellsPanel.setLayout(layout);
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
            {
                cell[i][j] = new JLabel();
                cell[i][j].setBackground(DEFAULT_BACKGROUND);
                cell[i][j].setFont(DEFAULT_FONT); // NOI18N
                cell[i][j].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                cell[i][j].setVerticalAlignment(javax.swing.SwingConstants.CENTER);
                cell[i][j].setOpaque(true);
                cell[i][j].setPreferredSize(DEFAULT_DIMENSION);
                cell[i][j].setText("");
                setRightBorder(i, j);
                final int temp1 = i;
                final int temp2 = j;
                cell[i][j].addMouseListener(new MouseAdapter() {
                                public void mousePressed(MouseEvent e)
                                {
                                    if(selectedCell != 0)
                                    {
                                        int row = selectedCell / 10;
                                        int col = selectedCell % 10;
                                        setRightBorder(row, col);
                                    }
                                    cell[temp1][temp2].setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 148), 2));
                                    selectedCell = temp1 * 10 + temp2;
                                }
                        });
                cellsPanel.add(cell[i][j]);
            }
    }
    
    public void run()
    {
        time.reset();
        do
        {
            try
            {
                lblTime.setText(time.getTimeInHMS());
                time.makeTick();
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                System.out.println("Time Problem!!!");
            }
        }
        while(true);
    }
    
    public void setRightBorder(int row, int col)
    {
        if(row % 3 == 0 && col % 3 == 0)
        {
            cell[row][col].setBorder(BorderFactory.createMatteBorder(2, 2, 1, 1, new Color(0, 0, 0)));
        }
        else if(row % 3 == 0)
        {
            cell[row][col].setBorder(BorderFactory.createMatteBorder(2, 1, 1, 1, new Color(0, 0, 0)));
        }
        else if(col % 3 == 0)
        {
            cell[row][col].setBorder(BorderFactory.createMatteBorder(1, 2, 1, 1, new Color(0, 0, 0)));
        }
        else
        {
            cell[row][col].setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
    }
    
    public void setCell(int row, int col, int value)
    {
//        actual[row][col] = value;
        puzzle.setActual(row, col, value);
        if(value == 0)
        {
            for(int i = 0; i < 9; i++)
                for(int j = 0; j < 9; j++)
                {
//                    if(actual[i][j] == 0)
//                        possible[i][j] = "";
                    if(puzzle.getActual(row, col) == 0)
                        puzzle.setPossible(row, col, "");
                }
        }
        else
//            possible[row][col] = String.valueOf(value);
            puzzle.setPossible(row, col, String.valueOf(value));
        if(value == 0)
            cell[row][col].setText("");
        else
            cell[row][col].setText(String.valueOf(value));
    }
    
    public void setToolTips()
    {
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                if(!puzzle.getPossible(i, j).equals(cell[i][j].getToolTipText()))
                    cell[i][j].setToolTipText(puzzle.getPossible(i, j));
    }
    
    public void setToolTip(int row, int column)
    {
        cell[row][column].setToolTipText(puzzle.getPossible(row, column));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        lblChooseNumber = new javax.swing.JLabel();
        lbl1 = new javax.swing.JLabel();
        lbl2 = new javax.swing.JLabel();
        lbl3 = new javax.swing.JLabel();
        lbl4 = new javax.swing.JLabel();
        lbl5 = new javax.swing.JLabel();
        lbl6 = new javax.swing.JLabel();
        lbl7 = new javax.swing.JLabel();
        lbl8 = new javax.swing.JLabel();
        lbl9 = new javax.swing.JLabel();
        lblErase = new javax.swing.JLabel();
        jToolBar2 = new javax.swing.JToolBar();
        lblState = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        lblTime = new javax.swing.JLabel();
        lblActivities = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtActivities = new javax.swing.JTextArea();
        btnHint = new javax.swing.JButton();
        btnSolve = new javax.swing.JButton();
        cellsPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        btnNewGame = new javax.swing.JMenuItem();
        btnOpen = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        btnSave = new javax.swing.JMenuItem();
        btnSaveAs = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        btnExit = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        btnUndo = new javax.swing.JMenuItem();
        btnRedo = new javax.swing.JMenuItem();
        menuLevel = new javax.swing.JMenu();
        btnEasy = new javax.swing.JRadioButtonMenuItem();
        btnMedium = new javax.swing.JRadioButtonMenuItem();
        btnDifficult = new javax.swing.JRadioButtonMenuItem();
        btnVeryDifficult = new javax.swing.JRadioButtonMenuItem();
        menuHelp = new javax.swing.JMenu();
        btnAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sudoku");
        setPreferredSize(new java.awt.Dimension(480, 500));
        setResizable(false);

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setRollover(true);

        lblChooseNumber.setText("Изберете номер:");
        jToolBar1.add(lblChooseNumber);

        lbl1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl1.setText("1");
        lbl1.setMaximumSize(new java.awt.Dimension(20, 20));
        lbl1.setMinimumSize(new java.awt.Dimension(20, 20));
        lbl1.setPreferredSize(new java.awt.Dimension(20, 20));
        lbl1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl1MousePressed(evt);
            }
        });
        jToolBar1.add(lbl1);

        lbl2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl2.setText("2");
        lbl2.setMaximumSize(new java.awt.Dimension(20, 20));
        lbl2.setMinimumSize(new java.awt.Dimension(20, 20));
        lbl2.setPreferredSize(new java.awt.Dimension(20, 20));
        lbl2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl2MousePressed(evt);
            }
        });
        jToolBar1.add(lbl2);

        lbl3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl3.setText("3");
        lbl3.setMaximumSize(new java.awt.Dimension(20, 20));
        lbl3.setMinimumSize(new java.awt.Dimension(20, 20));
        lbl3.setPreferredSize(new java.awt.Dimension(20, 20));
        lbl3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl3MousePressed(evt);
            }
        });
        jToolBar1.add(lbl3);

        lbl4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl4.setText("4");
        lbl4.setMaximumSize(new java.awt.Dimension(20, 20));
        lbl4.setMinimumSize(new java.awt.Dimension(20, 20));
        lbl4.setPreferredSize(new java.awt.Dimension(20, 20));
        lbl4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl4MousePressed(evt);
            }
        });
        jToolBar1.add(lbl4);

        lbl5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl5.setText("5");
        lbl5.setMaximumSize(new java.awt.Dimension(20, 20));
        lbl5.setMinimumSize(new java.awt.Dimension(20, 20));
        lbl5.setPreferredSize(new java.awt.Dimension(20, 20));
        lbl5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl5MousePressed(evt);
            }
        });
        jToolBar1.add(lbl5);

        lbl6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl6.setText("6");
        lbl6.setMaximumSize(new java.awt.Dimension(20, 20));
        lbl6.setMinimumSize(new java.awt.Dimension(20, 20));
        lbl6.setPreferredSize(new java.awt.Dimension(20, 20));
        lbl6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl6MousePressed(evt);
            }
        });
        jToolBar1.add(lbl6);

        lbl7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl7.setText("7");
        lbl7.setMaximumSize(new java.awt.Dimension(20, 20));
        lbl7.setMinimumSize(new java.awt.Dimension(20, 20));
        lbl7.setPreferredSize(new java.awt.Dimension(20, 20));
        lbl7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl7MousePressed(evt);
            }
        });
        jToolBar1.add(lbl7);

        lbl8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl8.setText("8");
        lbl8.setMaximumSize(new java.awt.Dimension(20, 20));
        lbl8.setMinimumSize(new java.awt.Dimension(20, 20));
        lbl8.setPreferredSize(new java.awt.Dimension(20, 20));
        lbl8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl8MousePressed(evt);
            }
        });
        jToolBar1.add(lbl8);

        lbl9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl9.setText("9");
        lbl9.setMaximumSize(new java.awt.Dimension(20, 20));
        lbl9.setMinimumSize(new java.awt.Dimension(20, 20));
        lbl9.setPreferredSize(new java.awt.Dimension(20, 20));
        lbl9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl9MousePressed(evt);
            }
        });
        jToolBar1.add(lbl9);

        lblErase.setText("Изчисти");
        lblErase.setMaximumSize(new java.awt.Dimension(41, 20));
        lblErase.setMinimumSize(new java.awt.Dimension(41, 20));
        lblErase.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblEraseMousePressed(evt);
            }
        });
        jToolBar1.add(lblErase);

        jToolBar2.setRollover(true);

        lblState.setText("jLabel12");
        lblState.setMaximumSize(new java.awt.Dimension(200, 14));
        jToolBar2.add(lblState);
        jToolBar2.add(jSeparator3);

        lblTime.setText("jLabel13");
        lblTime.setMaximumSize(new java.awt.Dimension(150, 14));
        jToolBar2.add(lblTime);

        lblActivities.setText("Ходове");

        txtActivities.setEditable(false);
        txtActivities.setColumns(20);
        txtActivities.setRows(5);
        txtActivities.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtActivities.setName(""); // NOI18N
        jScrollPane1.setViewportView(txtActivities);
        txtActivities.getAccessibleContext().setAccessibleName("");

        btnHint.setText("Подсказка");
        btnHint.setName("btnHint"); // NOI18N
        btnHint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHintActionPerformed(evt);
            }
        });

        btnSolve.setText("Реши го...");
        btnSolve.setName("btnSolvePuzzle"); // NOI18N
        btnSolve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolveActionPerformed(evt);
            }
        });

        cellsPanel.setPreferredSize(new java.awt.Dimension(300, 300));
        cellsPanel.setRequestFocusEnabled(false);

        javax.swing.GroupLayout cellsPanelLayout = new javax.swing.GroupLayout(cellsPanel);
        cellsPanel.setLayout(cellsPanelLayout);
        cellsPanelLayout.setHorizontalGroup(
            cellsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 303, Short.MAX_VALUE)
        );
        cellsPanelLayout.setVerticalGroup(
            cellsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 317, Short.MAX_VALUE)
        );

        menuFile.setText("Файл");

        btnNewGame.setText("Нова игра");
        btnNewGame.setToolTipText("");
        btnNewGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewGameActionPerformed(evt);
            }
        });
        menuFile.add(btnNewGame);

        btnOpen.setText("Зареди...");
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        menuFile.add(btnOpen);
        menuFile.add(jSeparator1);

        btnSave.setText("Запиши");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        menuFile.add(btnSave);

        btnSaveAs.setText("Запиши като...");
        btnSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveAsActionPerformed(evt);
            }
        });
        menuFile.add(btnSaveAs);
        menuFile.add(jSeparator2);

        btnExit.setText("Изход");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        menuFile.add(btnExit);

        jMenuBar1.add(menuFile);

        menuEdit.setText("Редактиране");

        btnUndo.setText("Ход назад");
        btnUndo.setEnabled(false);
        btnUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUndoActionPerformed(evt);
            }
        });
        menuEdit.add(btnUndo);

        btnRedo.setText("Ход напред");
        btnRedo.setEnabled(false);
        btnRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRedoActionPerformed(evt);
            }
        });
        menuEdit.add(btnRedo);

        jMenuBar1.add(menuEdit);

        menuLevel.setText("Ниво");

        btnEasy.setSelected(true);
        btnEasy.setText("Лесно");
        menuLevel.add(btnEasy);

        btnMedium.setText("Средно");
        menuLevel.add(btnMedium);

        btnDifficult.setText("Трудно");
        menuLevel.add(btnDifficult);

        btnVeryDifficult.setText("Много трудно");
        menuLevel.add(btnVeryDifficult);

        jMenuBar1.add(menuLevel);

        menuHelp.setText("Помощ");

        btnAbout.setText("Относно");
        menuHelp.add(btnAbout);

        jMenuBar1.add(menuHelp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnHint)
                        .addGap(94, 94, 94)
                        .addComponent(btnSolve)
                        .addGap(165, 165, 165))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cellsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblActivities))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblActivities)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addComponent(cellsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnHint)
                    .addComponent(btnSolve))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewGameActionPerformed
        // TODO add your handling code here:
        if(gameStarted)
        {
            int save = JOptionPane.showConfirmDialog(null, "Започвате нова игра.\nИскате ли да запишете текущата?", "Запис?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(save == JOptionPane.CANCEL_OPTION)
                return;
            else if(save == JOptionPane.YES_OPTION);
                saveGame(false);
        }
        startGame();
    }//GEN-LAST:event_btnNewGameActionPerformed

    public void clearBoard()
    {
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
            {
                cell[i][j].setText("");
                cell[i][j].setBackground(DEFAULT_BACKGROUND);
//                actual[i][j] = 0;
//                possible[i][j] = "";
            }
        puzzle.resetActual();
        puzzle.resetPossible();
    }
    
    public void setBoard()
    {
        int value;
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
            {
                value = puzzle.getActual(i, j);
                if(value != 0)
                {
                    setCell(i, j, value);
                    cell[i][j].setBackground(FILLED_BACKGROUND);
                }
                else
                {
                    setCell(i, j, 0);
                    cell[i][j].setBackground(DEFAULT_BACKGROUND);
                }
            }
    }
    
    public void startGame()
    {
        saveFileName = null;
        txtActivities.setText("");
        seconds = 0;
        clearBoard();
        gameStarted = true;
        timer.start();
        setToolTips();
//        puzzle.getPuzzle(1);
        setBoard();
    }
    
    public void saveGame(boolean saveAs)
    {
        if(!gameStarted)
        {
            JOptionPane.showMessageDialog(null, "Играта още не е започнала!", "Информация", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser saveChooser = new JFileChooser();
        saveChooser.setFileFilter(new FileNameExtensionFilter("Sudoku File", "sudo"));
        //File saveTo = new File
        if(saveFileName == null || saveAs)
        {
            int result = saveChooser.showSaveDialog(this);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                if(saveChooser.getSelectedFile().exists())
                    JOptionPane.showMessageDialog(saveChooser, "Файлът съществува и ще бъде презаписан!", "Грешка", JOptionPane.INFORMATION_MESSAGE);
                saveFileName = new File(saveChooser.getSelectedFile().getAbsolutePath() + ".sudo");
            }   
            else 
            {
                return;
            }
        }
        StringBuilder sudokuToString = new StringBuilder();
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                sudokuToString.append(cell[i][j].getText());
        System.out.println(sudokuToString);
        Formatter write = null;
        try
        {
            write = new Formatter(saveFileName);
            write.format("%s", sudokuToString);
        }catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, "Грешка при записа на файл!", "Грешка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally
        {
            if(write != null)
                write.close();
        }
    }
    
    public void openGame()
    {
        JFileChooser openChooser = new JFileChooser();
        openChooser.setFileFilter(new FileNameExtensionFilter("Sudoku File", "sudo"));
        int result = openChooser.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION)
        {
            saveFileName = openChooser.getSelectedFile();
        }   
        else 
        {
            return;
        }
        Scanner read = null;
        char[] stringToSudoku;
        try
        {
            read = new Scanner(saveFileName);
            String temp = read.nextLine();
            stringToSudoku = temp.toCharArray();
        }catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, "Грешка при четене на файл!", "Грешка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally
        {
            if(read != null)
                read.close();
        }
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                puzzle.setActual(i, j, stringToSudoku[i * 9 + j] - '0');
        setBoard();
        gameStarted = true;
        //System.out.println(printMatrix(actual));
    }
    
    /*public boolean isMoveValid(int row, int col, int value)
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
    }*/
    
    public void setActivity(String activity)
    {
        String oldText = txtActivities.getText();
        txtActivities.setText(activity + "\n" + oldText);
    }
    
    public void wantSave()
    {
        int result = JOptionPane.showConfirmDialog(null, "Искате ли да запишете текущата игра?", "Запис?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(result == JOptionPane.YES_OPTION)
            saveGame(false);
    }
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveGame(false);
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveAsActionPerformed
        saveGame(true);
    }//GEN-LAST:event_btnSaveAsActionPerformed

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        wantSave();
        openGame();
    }//GEN-LAST:event_btnOpenActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        wantSave();
        //exit();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnHintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHintActionPerformed
        hintMode = true;
        try
        {
            puzzle.solvePuzzle();
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(this, "Не може да се направи следващ ход.\nМоля, върнете един ход назад.", "Грешка", JOptionPane.ERROR_MESSAGE);
            //throw new Exception("Invalid Move");
        }
    }//GEN-LAST:event_btnHintActionPerformed

    private void btnSolveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolveActionPerformed
//        actualStack = new Stack();
//        possibleStack = new Stack();
        bruteForceStop = false;
        hintMode = false;
        try
        {
            if(!puzzle.solvePuzzle())
                puzzle.solvePuzzleByBruteForce();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Това судоку не може да се реши!", "Грешка", JOptionPane.ERROR_MESSAGE);
        }
        if(!puzzle.isPuzzleSolved())
            JOptionPane.showMessageDialog(this, "Судокуто не е решено!", "Грешка", JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_btnSolveActionPerformed

    private void btnUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUndoActionPerformed
        String move = moves.pop();
        int row = Integer.parseInt(String.valueOf(move.charAt(0)));
        int col = Integer.parseInt(String.valueOf(move.charAt(1)));
        //System.out.println(move + " " + row + " " + col);
        redoMoves.push(move);
        btnRedo.setEnabled(true);
        setCell(row, col, 0);
        setActivity(String.format("Променена стойност на (%d, %d).", row + 1, col + 1));
        if(moves.empty())
        {
            btnUndo.setEnabled(false);
        }
    }//GEN-LAST:event_btnUndoActionPerformed

    private void btnRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRedoActionPerformed
        String move = redoMoves.pop();
        int row = Integer.parseInt(String.valueOf(move.charAt(0)));
        int col = Integer.parseInt(String.valueOf(move.charAt(1)));
        int value = Integer.parseInt(String.valueOf(move.charAt(2)));
        moves.push(move);
        btnUndo.setEnabled(true);
        setCell(row, col, value);
        setActivity(String.format("Променена стойност на (%d, %d).", row, col));
        if(redoMoves.empty())
        {
            btnRedo.setEnabled(false);
        }
    }//GEN-LAST:event_btnRedoActionPerformed

    private void lbl1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl1MousePressed
        setValueInSelectedCell(1);
    }//GEN-LAST:event_lbl1MousePressed

    private void lbl2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl2MousePressed
        setValueInSelectedCell(2);
    }//GEN-LAST:event_lbl2MousePressed

    private void lbl3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl3MousePressed
        setValueInSelectedCell(3);
    }//GEN-LAST:event_lbl3MousePressed

    private void lbl4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl4MousePressed
        setValueInSelectedCell(4);
    }//GEN-LAST:event_lbl4MousePressed

    private void lbl5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl5MousePressed
        setValueInSelectedCell(5);
    }//GEN-LAST:event_lbl5MousePressed

    private void lbl6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl6MousePressed
        setValueInSelectedCell(6);
    }//GEN-LAST:event_lbl6MousePressed

    private void lbl7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl7MousePressed
        setValueInSelectedCell(7);
    }//GEN-LAST:event_lbl7MousePressed

    private void lbl8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl8MousePressed
        setValueInSelectedCell(8);
    }//GEN-LAST:event_lbl8MousePressed

    private void lbl9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl9MousePressed
        setValueInSelectedCell(9);
    }//GEN-LAST:event_lbl9MousePressed

    private void lblEraseMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEraseMousePressed
        setValueInSelectedCell(0);
    }//GEN-LAST:event_lblEraseMousePressed

    public void setValueInSelectedCell(int value)
    {
        if(selectedCell == 0)
            return;
        int row = selectedCell / 10;
        int col = selectedCell % 10;
        if(value == 0)
        {
            setCell(row, col, value);
            moves.push(String.format("%d%d%d", row, col, value));
            setActivity(String.format("Изтрита стойност на (%d, %d).", row + 1, col + 1));
            return;
        }
        if(puzzle.isMoveValid(row, col, value))
        {
            setCell(row, col, value);
            moves.push(String.format("%d%d%d", row, col, value));
            btnUndo.setEnabled(true);
            setActivity(String.format("На (%d, %d) е добавена стойност %d.", row + 1, col + 1, value));
            setRightBorder(row, col);
            selectedCell = 0;
        }
        if(puzzle.isPuzzleSolved())
            puzzleSolved();
    }
    
    /*public boolean isPuzzleSolved()
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
    }*/
    
    /*public boolean solvePuzzle() throws Exception
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
            puzzleSolved();
            return true;
        }
        else
            return false;
    }*/
    
    public void puzzleSolved()
    {
        /*stopTimer
         * text
         */
        lblState.setText("Поздравления!");
        JOptionPane.showMessageDialog(this, "Поздравления!\nСудокуто е решено!", "Браво", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /*public String calculatePossibleValues(int row, int col) throws Exception
    {
        String possibleValues;//System.out.println(possible[row][col]);
        if(puzzle.getPossible(row, col) == null || puzzle.getPossible(row, col).equals(""))
            possibleValues = "123456789";
        else
            possibleValues = puzzle.getPossible(row, col);
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
    }*/
    
    /*public boolean checkRowsAndColumns() throws Exception
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
                        JOptionPane.showMessageDialog(this, "Не може да се направи следващ ход.\nМоля, върнете един ход назад.", "Грешка", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                        System.out.println("Possible[i][j], i, j" + puzzle.getPossible(i, j) + ", " + i + ", " + j);
                        throw new Exception("Invalid Move");
                    }
                    //setToolTip
                    if(puzzle.getPossible(i, j).length() == 1)
                    {
                        actual[i][j] = Integer.parseInt(puzzle.getPossible(i, j));
                        setCell(i, j, actual[i][j]);
                        moves.push(String.format("%d%d%d", i, j, actual[i][j]));
                        setActivity(String.format("На (%d, %d) е добавена стойност %d.", i + 1, j + 1, actual[i][j]));
                        changes = true;
                        if(hintMode)
                            return true;
                        totalscore += 1;
                    }
                }
            }
        setToolTips();
        return changes;
    }*/
    
    /*public boolean lookForLoneRangersInMinigrids()
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
                            if(actual[i + k][j + p] == 0 && puzzle.getPossible(i + k, j + p).indexOf(n) != -1)
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
                        setCell(rowPos, colPos, n);
                        moves.push(String.format("%d%d%d", rowPos, colPos, n));
                        setActivity(String.format("На (%d, %d) е добавена стойност %d.", rowPos + 1, colPos + 1, n));
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
                    if(actual[i][j] == 0 && puzzle.getPossible(i, j).indexOf(n) != -1)
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
                    setCell(rowPos, colPos, n);
                    moves.push(String.format("%d%d%d", rowPos, colPos, n));
                    setActivity(String.format("На (%d, %d) е добавена стойност %d.", rowPos + 1, colPos + 1, n));
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
                    if(actual[i][j] == 0 && puzzle.getPossible(i, j).indexOf(n) != -1)
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
                    setCell(rowPos, colPos, n);
                    moves.push(String.format("%d%d%d", rowPos, colPos, n));
                    setActivity(String.format("На (%d, %d) е добавена стойност %d.", rowPos + 1, colPos + 1, n));
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
                if(actual[i][j] == 0 && puzzle.getPossible(i, j).length() == 2)//if cell have two possible values
                {   
                    int startRow = i - (i % 3);//scan the minigrid
                    int startCol = j - (j % 3);
                    for(int k = startRow; k <= startRow + 2; k++)
                    {
                        for(int p = startCol; p <= startCol + 2; p++)
                        {
                            if((i != k && j != p) && (puzzle.getPossible(k, p).equals(puzzle.getPossible(i, j))))
                            {
                                //found the twins
                                for(int kk = startRow; kk <= startRow + 2; kk++)//remove from all the other possibles in this minigrid
                                {
                                    for(int pp = startCol; pp <= startCol + 2; pp++)
                                    {
                                        if(actual[kk][pp] == 0 && !puzzle.getPossible(kk, pp).equals(puzzle.getPossible(i, j)))
                                        {
                                            String originalPossibleValues = puzzle.getPossible(kk, pp);
                                            //remove first and second twin number
                                            possible[kk][pp] = puzzle.getPossible(kk, pp).replaceFirst(String.valueOf(puzzle.getPossible(i, j).charAt(0)), "");
                                            possible[kk][pp] = puzzle.getPossible(kk, pp).replaceFirst(String.valueOf(puzzle.getPossible(i, j).charAt(1)), "");
                                            if(!originalPossibleValues.equals(puzzle.getPossible(kk, pp)))
                                                changes = true;
                                            if(puzzle.getPossible(kk, pp).equals(""))
                                                throw new Exception("Invalid Move");
                                            if(puzzle.getPossible(kk, pp).length() == 1)
                                            {
                                                setCell(kk, pp, Integer.parseInt(puzzle.getPossible(kk, pp)));
                                                moves.push(String.format("%d%d%s", kk, pp, puzzle.getPossible(kk, pp)));
                                                setActivity(String.format("На (%d, %d) е добавена стойност %s.", kk + 1, pp + 1, puzzle.getPossible(kk, pp)));
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
                if(actual[i][j] == 0 && puzzle.getPossible(i, j).length() == 2)//if cell have two possible values
                {   
                    for(int p = j + 1; p < 9; p++)
                    {
                        if(puzzle.getPossible(i, p).equals(puzzle.getPossible(i, j)))
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
                                        setCell(i, pp, Integer.parseInt(possible[i][pp]));
                                        moves.push(String.format("%d%d%s", i, pp, possible[i][pp]));
                                        setActivity(String.format("На (%d, %d) е добавена стойност %s.", i + 1, pp + 1, possible[i][pp]));
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
                                        setCell(kk, j, Integer.parseInt(possible[kk][j]));
                                        moves.push(String.format("%d%d%s", kk, j, possible[kk][j]));
                                        setActivity(String.format("На (%d, %d) е добавена стойност %s.", kk + 1, j + 1, possible[kk][j]));
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
                                    setToolTip(k, p);
                                    if(!originalPossibleValues.equals(possible[k][p]))
                                        changes = true;
                                    if(possible[k][p].equals(""))
                                        throw new Exception("Invalid Move");
                                    if(possible[k][p].length() == 1)
                                    {
                                        setCell(k, p, Integer.parseInt(possible[k][p]));
                                        setToolTip(k, p);
                                        moves.push(String.format("%d%d%s", k, p, possible[k][p]));
                                        setActivity(String.format("На (%d, %d) е добавена стойност %s.", k + 1, p + 1, possible[k][p]));
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
                                setToolTip(i, p);
                                if(!originalPossibleValues.equals(possible[i][p]))
                                    changes = true;
                                if(possible[i][p].equals(""))
                                    throw new Exception("Invalid Move");
                                if(possible[i][p].length() == 1)
                                {
                                    setCell(i, p, Integer.parseInt(possible[i][p]));
                                    setToolTip(i, p);
                                    moves.push(String.format("%d%d%s", i, p, possible[i][p]));
                                    setActivity(String.format("На (%d, %d) е добавена стойност %s.", i + 1, p + 1, possible[i][p]));
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
                                setToolTip(k, j);
                                if(!originalPossibleValues.equals(possible[k][j]))
                                    changes = true;
                                if(possible[k][j].equals(""))
                                    throw new Exception("Invalid Move");
                                if(possible[k][j].length() == 1)
                                {
                                    setCell(k, j, Integer.parseInt(possible[k][j]));
                                    setToolTip(k, j);
                                    moves.push(String.format("%d%d%s", k, j, possible[k][j]));
                                    setActivity(String.format("На (%d, %d) е добавена стойност %s.", k + 1, j + 1, possible[k][j]));
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
            setCell(coordinates[0], coordinates[1], value);
            moves.push(String.format("%d%d%d", coordinates[0], coordinates[1], value));
            setActivity(String.format("На (%d, %d) е добавена стойност %d.", coordinates[0] + 1, coordinates[1] + 1, value));
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
                setActivity("Грешен ход! Връщане...");
                totalscore += 5;
                actual = actualStack.pop();
                System.out.println(printMatrix(actual));
                possible = possibleStack.pop();
            }
        }
    }*/
    
    public String printMatrix(int[][] matrix)
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
                result = String.format("%s%d", result, matrix[i][j]);
            }
            result = String.format("%s\n", result);
        }
        return result;
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
    
    /*private String generateNewPuzzle(int level)
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
            return "";
        }
        
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
        
        JOptionPane.showMessageDialog(this, "Заредено!!!", "Браво", JOptionPane.INFORMATION_MESSAGE);
        
        createEmptyCells(numberOfEmptyCells);
        
        setBoard();
        
        StringBuilder sudokuToString = new StringBuilder();
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                sudokuToString.append(actual[i][j]);
        
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!verify the puzzle has only one solution!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        
        //verify the puzzle has only one solution
        int tries = 0;
        VERIFY:
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
                        setBoard();
                        System.out.println(tries);
                        tries++;
                    }
                    else
                    {
                        solvePuzzleByBruteForce();
                        break VERIFY;
                    }
                }
                else
                    break VERIFY;
            }
            catch (Exception e)
            {
                return "";
            }
            
            if(tries > 50)
                return "";
        }
        while(true);
        return sudokuToString.toString();//score?
    }*/
    
    private int randomNumberBetween(int first, int second)
    {
        return first + generator.nextInt(second - first + 1);
    }
    
    /*private void createEmptyCells(int empty)
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
            System.out.println("result " + result);
            if(!result.isEmpty())
            {
                System.out.println("totalscore " + totalscore);
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
            System.out.println("count " + count);
            count++;
        }
        while(count < 500000);
        return result;
    }*/

//    @Override
//    public String toString()
//    {
//        return printMatrix(actual);
//    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SudokuUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SudokuUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SudokuUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SudokuUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        /*java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SudokuUI().setVisible(true);
            }
        });*/
        SudokuUI s = new SudokuUI();
        //System.out.println(s.isMoveValid(4, 4, 5));
        s.setVisible(true);
        /*s.setActivity("Първо");
        s.setActivity("Второ");
        s.setActivity("Трето");
        String pat = "526398741";
        for(int i=0;i<9;i++)
        {
            pat = pat.replaceFirst("" + (i + 1) , "");
        }
        System.out.println(pat);
        //s.saveGame(false);*/
        int [][] test = new int [9][9];
        test[6][4] = 5;
        test[3][2] = 2;
        System.out.println(s.printMatrix(test));
//        System.out.println(s.getPuzzle(1));
//        System.out.println(s.toString());
//        System.out.println(s.getPuzzle(2));
//        System.out.println(s.toString());
//        System.out.println(s.getPuzzle(3));
//        System.out.println(s.toString());
//        System.out.println(s.getPuzzle(4));
//        System.out.println(s.toString());
//        System.out.println(s.generateNewPuzzle(2));
//        System.out.println(s.randomizePossibleValues("123456789"));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem btnAbout;
    private javax.swing.JRadioButtonMenuItem btnDifficult;
    private javax.swing.JRadioButtonMenuItem btnEasy;
    private javax.swing.JMenuItem btnExit;
    private javax.swing.JButton btnHint;
    private javax.swing.JRadioButtonMenuItem btnMedium;
    private javax.swing.JMenuItem btnNewGame;
    private javax.swing.JMenuItem btnOpen;
    private javax.swing.JMenuItem btnRedo;
    private javax.swing.JMenuItem btnSave;
    private javax.swing.JMenuItem btnSaveAs;
    private javax.swing.JButton btnSolve;
    private javax.swing.JMenuItem btnUndo;
    private javax.swing.JRadioButtonMenuItem btnVeryDifficult;
    private javax.swing.JPanel cellsPanel;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lbl4;
    private javax.swing.JLabel lbl5;
    private javax.swing.JLabel lbl6;
    private javax.swing.JLabel lbl7;
    private javax.swing.JLabel lbl8;
    private javax.swing.JLabel lbl9;
    private javax.swing.JLabel lblActivities;
    private javax.swing.JLabel lblChooseNumber;
    private javax.swing.JLabel lblErase;
    private javax.swing.JLabel lblState;
    private javax.swing.JLabel lblTime;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenu menuLevel;
    private javax.swing.JTextArea txtActivities;
    // End of variables declaration//GEN-END:variables
}
