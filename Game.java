import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class Game implements Runnable, ImageObserver{
   
	final int WIDTH = 1920;
	final int HEIGHT = 1080;
   
	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;
	
	private double timer = 0;
	private Room rooms[];
	private int wood = 15;
	private int stone = 15;
	private int food = 0;
	private int people = 0;
	private int gold = 0;
	private int hp = 100;
	private BufferedImage background;
	private BufferedImage goblin;
	private boolean night = false;
   
	public Game() {
		
		try {
			background = ImageIO.read(new File(System.getProperty("user.dir") + "\\assets\\" + "conceptBg" + ".jpg"));
			goblin = ImageIO.read(new File(System.getProperty("user.dir") + "\\assets\\" + "Gob1" + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		rooms = new Room[12];
		
		for (int i=0;i<12;i++)
		{
			rooms[i] = null;
		}
		
		frame = new JFrame(" ");
      
		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setLayout(null);
      
		canvas = new Canvas();
		canvas.setBounds(0, 0, WIDTH, HEIGHT);
		canvas.setIgnoreRepaint(true);
      
		panel.add(canvas);
      
		canvas.addMouseListener(new MouseControl());
      
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
      
		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();
      
		canvas.requestFocus();
	}
   
        
private class MouseControl extends MouseAdapter{ 
	
	public void mouseClicked(MouseEvent e) {
		
		int x = e.getX();
		int y = e.getY();
		
		System.out.println(x + ", " + y);
		
		if (e.getX() >= 1052 && e.getX() <= 1209)
		{
			if (e.getY() >= 356 && e.getY() <= 387)
			{
				if (wood >= 10 && stone >= 10)
				{
					wood -= 10;
					stone -= 10;
					
				}
			}
			else if (e.getY() >=402 && e.getY() <= 438)
			{
				if (wood >= 10 && stone >= 10)
				{
					wood -= 10;
					stone -= 10;
					
				}
			}
		}
		
		
	}
	
}
   
	long desiredFPS = 60;
	long desiredDeltaLoop = (1000*1000*1000)/desiredFPS;
    
	boolean running = true;
   
	public void run(){
      
		long beginLoopTime;
		long endLoopTime;
      	long currentUpdateTime = System.nanoTime();
      	long lastUpdateTime;
      	long deltaLoop;
      
      	while(running){
      		beginLoopTime = System.nanoTime();
         
      		render();
         
      		lastUpdateTime = currentUpdateTime;
      		currentUpdateTime = System.nanoTime();
      		update((int) ((currentUpdateTime - lastUpdateTime)/(1000*1000)));
         
      		endLoopTime = System.nanoTime();
      		deltaLoop = endLoopTime - beginLoopTime;
           
      		if(deltaLoop > desiredDeltaLoop){
      			//Do nothing
      		}else{
      			try{
      				Thread.sleep((desiredDeltaLoop - deltaLoop)/(1000*1000));
      			}catch(InterruptedException e){
      				//Do nothing
      			}
      		}
      	}
	}
   
	private void render() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);
		render(g);
		g.dispose();
		bufferStrategy.show();
	}
   
   
	protected void update(int deltaTime){
		
		timer += (deltaTime/4);
		
		if (timer >= 1000)
		{
			for (int i=0;i<12;i++)
			{
				if (rooms[i] != null)
				{
					//update the rooms
				}
			}
			
			timer -= 1000;
		}
	}
   
   
	protected void render(Graphics2D g){
		
		g.drawImage(background, 0, 0, WIDTH, HEIGHT, null);
		g.drawImage(goblin, 100, 550, 64, 64, null);
		g.drawString("Health: " + hp, 25, 75);
		g.drawString("Wood: " + wood, 25, 100);
		g.drawString("Stone: " + stone, 25, 125);
	}
   
   


	@Override
	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		return false;
	}

}
