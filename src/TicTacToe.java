import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TicTacToe extends JComponent implements MouseListener {
    //Instance variables (game dimensions)
    private JFrame gui;
    private final int DIMENSION = 500;
    private final int boardSize = 5; // The board will be size boardSize x boardSize
    private final int spaceLen = DIMENSION / boardSize;

    //Instance variables (menu features)
    private Font f1 = new Font("Arial", Font.BOLD, 45); // Title
    private Font f2 = new Font("Arial", Font.BOLD, 30);
    private Font f3 = new Font("Arial", Font.PLAIN, 20);
    private Font tile = new Font("Arial", Font.BOLD, 60); 

    //Instance variables (gameplay elements)
    private boolean gameStart = false, gameEnd = false, circleWin = false, crossWin = false;
    private String startGameStr = "Press anywhere to start";
    private String[][] board = new String[boardSize][boardSize];
    private boolean circle = true;

    //Strings
    private final String title = "Tic-Tac-Toe";
    private final String credits = "Created by Jared Tjahjadi";
    private final String circleWonStr = "O wins!";
    private final String crossWonStr = "X wins!";
    private final String tieStr = "Tie!";

    //Default constructor
    public TicTacToe() {
        gui = new JFrame(title); // Makes new window, sets title to "Tic-Tac-Toe"
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensures game can close
        gui.setPreferredSize(new Dimension(DIMENSION + 5, DIMENSION + 30)); // Sets window size
        gui.setResizable(false); // Window cannot be resized
        gui.getContentPane().add(this); // Adds this class to GUI
        gui.pack();
        gui.setLocationRelativeTo(null); // Window opens in center of computer screen
        gui.setVisible(true); // Window is always visible
        gui.addMouseListener(this); // Program will listen to mouse clicks
    }

    private boolean rowWin(String str, int row) {
        for(int i = 0; i < boardSize; i++) if(board[i][row] == null || !board[i][row].equals(str)) return false;
        return true;
    }

    private boolean colWin(String str, int col) {
        for(int i = 0; i < boardSize; i++) if(board[col][i] == null || !board[col][i].equals(str)) return false;
        return true;
    }
    
    private boolean diagWin(String str) {
        for(int i = 0; i < boardSize; i++) if(board[i][i] == null || !board[i][i].equals(str)) return false;
        return true;
    }

    private boolean antiDiagWin(String str) {
        for(int i = boardSize - 1; i >= 0; i--) if(board[i][boardSize - 1 - i] == null || !board[i][boardSize - 1 - i].equals(str)) return false;
        return true;
    }

    //The game is a tie if all spaces are filled and a winner has not already been determined
    private boolean isTie() {
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i]. length; j++) {
                if(board[i][j] == null) return false;
            }
        }
        return true;
    }

    public void paintComponent(Graphics g) {
        if(!gameStart) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, DIMENSION, DIMENSION);
            g.setColor(Color.BLACK);
            g.setFont(f1);
            centerAlignString(g, title, f1);
            g.setFont(f2);
            FontMetrics m = g.getFontMetrics(f2);
            g.drawString(credits, (DIMENSION - m.stringWidth(credits)) / 2, (int) ((DIMENSION - m.getHeight()) / 1.5));
            g.setFont(f3);
            m = g.getFontMetrics(f3);
            g.drawString(startGameStr, (DIMENSION - g.getFontMetrics(f3).stringWidth(startGameStr)) / 2, (DIMENSION - m.getHeight()));
        }
        else {
            //Setting up tic-tac-toe board
            g.setColor(Color.WHITE); // Background color
            g.fillRect(0, 0, DIMENSION, DIMENSION); // Clear the screen
            g.setColor(Color.BLACK); // Line color
            for(int i = 1; i < boardSize; i++) {
                g.drawLine(i * (DIMENSION / boardSize), 0, i * (DIMENSION / boardSize), DIMENSION);
                g.drawLine(0, i * (DIMENSION / boardSize), DIMENSION, i * (DIMENSION / boardSize));
            }

            //Tile placement
            g.setFont(tile); // Font for tiles
            for(int i = 0; i < board.length; i++) {
                for(int j = 0; j < board[i].length; j++) if(board[i][j] != null)
                    g.drawString(board[i][j], (int) (((spaceLen * j) + spaceLen * (j + 1)) / 2) - 20, ((int) ((spaceLen * i) + (spaceLen * (i + 1))) / 2) + 20);
            }

            //Displaying result string
            if(circleWin) {
                g.setColor(Color.RED);
                g.setFont(f1);
                centerAlignString(g, circleWonStr, f1);
            }
            else if(crossWin) {
                g.setColor(Color.RED);
                g.setFont(f1);
                centerAlignString(g, crossWonStr, f1);
            }
            else if(isTie()) {
                g.setColor(Color.RED);
                g.setFont(f1);
                centerAlignString(g, tieStr, f1);
            }
        }
    }

    private void centerAlignString(Graphics g, String str, Font f) {
        FontMetrics metrics = g.getFontMetrics(f);
        g.drawString(str, (DIMENSION - metrics.stringWidth(str)) / 2, ((DIMENSION - metrics.getHeight()) / 2));
    }

    public void start(final int ticks){
        Thread gameThread = new Thread(){
            public void run() {
                while(true){
                    loop();
                    try { Thread.sleep(1000 / ticks); }
                    catch(Exception e) { e.printStackTrace(); }
                }
            }
        };	
        gameThread.start();
    }

    private void loop() { repaint(); }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(!gameStart) gameStart = true; // Deals with first click
        else if(!gameEnd) {
            int x = (e.getX() / spaceLen), y = (e.getY() / spaceLen); // Get X and Y of click

            // Handling when players clicks on tile
            for(int i = 0; i < board.length; i++) {
                for(int j = 0; j < board[i].length; j++) {
                    if(board[y][x] == null) {
                        board[y][x] = circle ? "O" : "X";
                        circle = !circle;
                    }
                }
            }

            // Determining winner or if the game tied; if either happen, then the game ends
            circleWin = rowWin("O", x) || colWin("O", y) || diagWin("O") || antiDiagWin("O");
            crossWin = rowWin("X", x) || colWin("X", y) || diagWin("X") || antiDiagWin("X");
            gameEnd = circleWin || crossWin || isTie();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        TicTacToe ttt = new TicTacToe();
        ttt.start(60);
    }
}