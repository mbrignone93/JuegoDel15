/*
 * Fifteen Game
 * 
 * Desarrollador Maximiliano Brignone
 * 
 * El juego del 15 es un juego de deslizamiento de piezas que presentan 
 * un determinado orden inicial dentro de una cajita cuadrada. Tiene su 
 * origen en Estados Unidos, en el siglo XIX. 
 * Es una cajita formada por 16 casillas de las cuales sólo quince 
 * están ocupadas. Todas las fichas están colocadas en orden numérico, 
 * excepto la 14 y la 15, que tienen sus posiciones intercambiadas. 
 * El juego consiste en maniobrar todas las fichas para corregir el error 
 * que hay en la fila inferior de la cajita, de manera que todas las fichas 
 * queden en orden consecutivo.
 * 
 * © 26/09/2018
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class FifteenGame extends JPanel{
	
	private int size;
	private int nbTiles;
	private int dimension;
	private static final Color FOREGROUND_COLOR = new Color(239, 83,80);
	private static final Random RANDOM = new Random();
	private int[] tiles;
	private int tileSize;
	private int blankPos;
	private int margin;
	private int gridSize;
	private boolean gameOver;
	
	public FifteenGame(int size, int dim, int mar){
		this.size = size;
		dimension = dim;
		margin = mar;
		
		nbTiles = size * size - 1;
		tiles = new int[size * size];
		
		gridSize = (dim - 2 * margin);
		tileSize = gridSize / size;
		
		setPreferredSize(new Dimension(dimension, dimension + margin));
		setBackground(Color.WHITE);
		setFont(new Font("Sanserif", Font.BOLD, 60));
		
		gameOver = true;
		
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if (gameOver){
					newGame();
				} else {
					int ex = e.getX() - margin;
					int ey = e.getY() - margin;
					
					if (ex < 0 || ex > gridSize || ey < 0 || ey > gridSize)
						return;
					
					int c1 = ex / tileSize;
					int r1 = ey / tileSize;
					
					int c2 = blankPos % size;
					int r2 = blankPos / size;
					
					int clickPos = r1 * size + c1;
					
					int dir = 0;
					
					if (c1 == c2 && Math.abs(r1 - r2)> 0)
						dir = (r1 - r2) > 0 ? size: -size;
						else if (r1 == r2 && Math.abs(c1 - c2) > 0)
							dir = (c1 - c2) > 0 ? 1 : -1;
					
					if (dir != 0){
						do {
							int newBlankPos = blankPos + dir;
							tiles[blankPos] = tiles[newBlankPos];
							blankPos = newBlankPos;
						} while(blankPos != clickPos);
						
						tiles[blankPos] = 0;
					}
					
					gameOver = isSolved();
				}
				
				repaint();
			}
		});
		
		newGame();
	}
	
	private void newGame(){
		do{
			reset();
			shuffle();
			
		} while(!isSolvable());
		
		gameOver = false;
	}
	
	private void reset(){
		for (int i = 0; i < tiles.length; i++){
			tiles[i] = (i + 1) % tiles.length;
		}
		
		blankPos = tiles.length - 1;
	}
	
	private void shuffle(){
		int n = nbTiles;
		
		while (n > 1)
		{
			int r = RANDOM.nextInt(n--);
			int tmp = tiles[r];
			tiles[r] = tiles[n];
			tiles[n] = tmp;
		}
	}
	
	private boolean isSolvable(){
		int countInversions = 0;
		
		for (int i = 0; i < nbTiles; i++)
		{
			for (int j = 0; j < i; j++){
				if (tiles[j] > tiles[i])
					countInversions++;
			}
		}
		
		return countInversions % 2 == 0;
	}
	
	private boolean isSolved(){
		if (tiles[tiles.length - 1] != 0){
			return false;
		}
		
		for (int i = nbTiles - 1; i >= 0; i--){
			if (tiles[i] != i + 1)
				return false;
		}
		
		return true;
	}
	
	private void drawGrid(Graphics2D g){
		for (int i = 0; i < tiles.length; i++){
			int r = i / size;
			int c = i % size;
			
			int x = margin + c * tileSize;
			int y = margin + r * tileSize;
			
			if (tiles[i] == 0){
				if (gameOver){
					g.setColor(FOREGROUND_COLOR);
					drawCenteredString(g, "✓", x, y);
				}
				
				continue;
			}
			
			g.setColor(getForeground());
			g.fillRoundRect(x, y, tileSize, tileSize, 25, 25);
			g.setColor(Color.BLACK);
			g.drawRoundRect(x, y, tileSize, tileSize, 25, 25);
			g.setColor(Color.WHITE);
			
			drawCenteredString(g, String.valueOf(tiles[i]), x, y);
		}
	}
	
	private void drawCenteredString(Graphics2D g, String s, int x, int y){
		FontMetrics fm = g.getFontMetrics();
		int asc = fm.getAscent();
		int desc = fm.getDescent();
		g.drawString(s, x + (tileSize - fm.stringWidth(s)) / 2, 
				y + (asc + (tileSize - (asc + desc)) / 2));
	}
	
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		drawGrid(g2D);
	}
	
	public static void main(String[] args){
		SwingUtilities.invokeLater(() -> {
			 JFrame frame = new JFrame();
			 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			 frame.setTitle("Fifteen Game");
			 frame.setResizable(false);
			 frame.add(new FifteenGame(4, 550, 30), BorderLayout.CENTER);
			 frame.pack();
			 frame.setLocationRelativeTo(null);
			 frame.setVisible(true);
			 Image im = Toolkit.getDefaultToolkit().getImage("15.png");
			 frame.setIconImage(im);
		});
	}
}