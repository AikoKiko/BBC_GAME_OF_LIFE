import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.awt.image.DataBufferInt;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class GameOfLife extends Canvas implements Runnable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int frameSize = 360;
	public static String title = "Game of Life";
	
	public int gridSize = 50;
	public double generationSpeed = 60.0;
	public BufferedImage image;
	public int[] pixels;
	
	public boolean[] cGrid;
	public boolean[] pGrid;
	
	public Random rand = new Random();
	
	public GameOfLife() {
		Dimension dim =  new Dimension(frameSize, frameSize);
		setMinimumSize(dim);
		setMaximumSize(dim);
		setPreferredSize(dim);
		image = new BufferedImage(gridSize, gridSize, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	}
	
	public void start() {
		cGrid = new boolean[pixels.length];
		pGrid = new boolean[pixels.length];
		
		for(int it = 0; it < cGrid.length; it++) {
			cGrid[it] = rand.nextInt(100)/100.0 > 0.8 ? true:false;
		}
	
		try {
			BufferedImage im = ImageIO.read(GameOfLife.class.getResource("/bbc.png"));
			int[] pixelsOfImage = im.getRGB(0,0, im.getWidth(),im.getHeight(), null,0, im.getHeight());
			
			for(int it = 0; it < pixelsOfImage.length; it++) {
				cGrid[it] = (pixelsOfImage[it] & 0xff) < 125 ? true : false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Thread(this).start();
	}
	
	public static void main(String[] args) {
		JFrame frame  =  new JFrame();
		frame.setTitle(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GameOfLife gol = new GameOfLife();
		frame.add(gol);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		gol.start();
	}

	@Override
	public void run() {
		
		double frameCut = 1000000000.0 / generationSpeed;
		
		long currentTime = System.nanoTime();
		long previousTime = currentTime;
		long passedTime = 0;
		double unprocessedTime = 0.0;
		long frameCounter = System.currentTimeMillis();
		int generation = 0;
		while(true) {
			previousTime = currentTime;
			currentTime = System.nanoTime();
			passedTime = currentTime - previousTime;

			unprocessedTime += passedTime;
			
			if(unprocessedTime > frameCut) {
				unprocessedTime = 0;
				update();
				
				if(System.currentTimeMillis() - frameCounter >= 1000) {
					frameCounter = System.currentTimeMillis();
					System.out.println("Currently generating : " + generation + "th generation");
				}
				generation++;
				render();
			}	
		}
	}
	private void update() {
		for(int i = 0; i < pixels.length; i++)
			pGrid[i] = cGrid[i];
		for(int row = 0; row < gridSize; row++)
			for(int column = 0; column < gridSize; column++) {
				int res = 0;
				int[][] dirs = new int[][] {{-1, 0},{1, 0},{0, -1},{0, 1},{-1, -1},{1, 1},{-1, 1},{1,-1}};
				for(int[] dir: dirs) {
					int newRow = row + dir[0];
					int newColumn = column + dir[1];
					if(newRow < 0 || newRow >= gridSize || newColumn < 0 || newColumn >= gridSize)
						continue;
					res = (pGrid[newRow + gridSize * newColumn]) ? res + 1:res + 0;
				}
				if(!(pGrid[row + gridSize*column] && (res == 3 || res == 2)))
					cGrid[row + gridSize*column] = false;
				if(!pGrid[row + gridSize*column] && res == 3)
					cGrid[row + gridSize*column] = true;
			}
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics graph = bs.getDrawGraphics();
		for(int it = 0; it < pixels.length; it++) {
//			pixels[it] = rand.nextInt(0xffffff);
			pixels[it] = 0;
			
		}

		for(int it = 0; it < pixels.length; it++) {
			pixels[it] = cGrid[it] ? 0xffffff : 0;
			
		}
		
		graph.drawImage(image,0,0,frameSize, frameSize, null);
		graph.dispose();
		bs.show();
//		bs.dispose();
	}

}