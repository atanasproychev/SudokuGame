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
public class SudokuUI extends javax.swing.JFrame /*implements Runnable*/
{

    /**
     * Creates new form Sudoku_UI
     */
    private int selectedCell;
    /*Moves Stacks*/
    private Stack<String> moves;
    private Stack<String> redoMoves;
    private File saveFileName;
    private int seconds;
    private boolean gameStarted;
    private SudokuSolver puzzle;
    private JLabel[][] cell;
    private final Color DEFAULT_BACKGROUND;
    private final Color FILLED_BACKGROUND;
    private final Color SOLVED_BY_COMPUTER_FOREGROUND;
    private final Color DEFAULT_FOREGROUND;
    private final Font DEFAULT_FONT;
    private final Dimension DEFAULT_DIMENSION;
    
    private boolean hintMode;
    
    private boolean bruteForceStop = false;
    
    private int totalscore;
    private Random generator;
//    private Thread timer;
//    private Time time;
    
    
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
        SOLVED_BY_COMPUTER_FOREGROUND = new Color(255, 0, 0);
        DEFAULT_FOREGROUND = new Color(0, 0, 0);
        DEFAULT_FONT = new Font("Calibri", 0, 17);
        DEFAULT_DIMENSION = new Dimension(32, 32);
        initCells();
        moves = new Stack();
        redoMoves = new Stack();
        gameStarted = false;
        generator = new Random();
        puzzle = new SudokuSolver();
//        timer = new Thread();
//        time = new Time();
//        lblTime1 = new Clock();
//        /*lblTime.startClock();
//        lblTime.suspendClock();*/
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
    
//    public void run()
//    {
//        time.reset();
//        lblTime.setText(time.getTimeInHMS());
//        do
//        {
//            try
//            {
//                lblTime.setText(time.getTimeInHMS());
//                time.makeTick();
//                Thread.sleep(1000);
//            }
//            catch(InterruptedException e)
//            {
//                System.out.println("Time Problem!!!");
//            }
//        }
//        while(true);
//    }
    
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
                    if(puzzle.getActual(i, j) == 0)
                        puzzle.setPossible(i, j, "");
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
    lblTime1 = lblTime = new Clock();
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

    lblTime1.setText("jLabel13");
    lblTime1.setMaximumSize(new java.awt.Dimension(150, 14));
    jToolBar2.add(lblTime1);

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
            else if(save == JOptionPane.YES_OPTION)
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
                cell[i][j].setForeground(DEFAULT_FOREGROUND);
//                actual[i][j] = 0;
//                possible[i][j] = "";
            }
        puzzle.resetActual();
        puzzle.resetPossible();
    }
    
    public void setBoard(boolean isSolved)
    {
        int value;
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
            {
                value = puzzle.getActual(i, j);
                if(isSolved)
                {
                    if(cell[i][j].getBackground() == FILLED_BACKGROUND)
                        continue;
                    cell[i][j].setForeground(SOLVED_BY_COMPUTER_FOREGROUND);
                    if(cell[i][j].getText().isEmpty())
                        setCell(i, j, value);
                    continue;
                }
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
//        lblTime.suspendClock();
////        lblTime.stopClock();
////        lblTime.startClock();
//        lblTime.resumeClock(0);
//        levelMe
        long started = System.currentTimeMillis();
        puzzle.getPuzzle(1);
        System.out.printf("Time: %d sec.", (System.currentTimeMillis() - started)/1000);
        setBoard(false);
        setToolTips();
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
        setBoard(false);
        gameStarted = true;
        //System.out.println(printMatrix(actual));
    }
    
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
        setBoard(true);
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
        
    public void puzzleSolved()
    {
        /*stopTimer
         * text
         */
        lblState.setText("Поздравления!");
        JOptionPane.showMessageDialog(this, "Поздравления!\nСудокуто е решено!", "Браво", JOptionPane.INFORMATION_MESSAGE);
    }
        
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
        
    private int randomNumberBetween(int first, int second)
    {
        return first + generator.nextInt(second - first + 1);
    }
    
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
//        int [][] test = new int [9][9];
//        test[6][4] = 5;
//        test[3][2] = 2;
//        System.out.println(s.printMatrix(test));
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
  private Clock lblTime;
  private javax.swing.JLabel lblTime1;
  private javax.swing.JMenu menuEdit;
  private javax.swing.JMenu menuFile;
  private javax.swing.JMenu menuHelp;
  private javax.swing.JMenu menuLevel;
  private javax.swing.JTextArea txtActivities;
  // End of variables declaration//GEN-END:variables
}
