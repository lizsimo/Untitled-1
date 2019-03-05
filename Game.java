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
   
	final int WIDTH = 1500;
	final int HEIGHT = 700;
   
	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;
	
	private double x = -5000;
	private double timer = 0;
	private boolean room0 = false, room1 = false;
	private int wood = 15;
	private int stone = 15;
	private int hp = 100;
	private BufferedImage image;
   
	public Game(){
		frame = new JFrame("Basic Game");
      
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
		
		if (e.getX() >= 1052 && e.getX() <= 1209)
		{
			if (e.getY() >= 356 && e.getY() <= 387)
			{
				if (wood >= 10 && stone >= 10)
				{
					wood -= 10;
					stone -= 10;
					room0 = true;
				}
			}
			else if (e.getY() >=402 && e.getY() <= 438)
			{
				if (wood >= 10 && stone >= 10)
				{
					wood -= 10;
					stone -= 10;
					room1 = true;
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
         
      		try {
      			render();
      		} catch (IOException e1) {
      			// TODO Auto-generated catch block
      			e1.printStackTrace();
      		}
         
      		lastUpdateTime = currentUpdateTime;
      		currentUpdateTime = System.nanoTime();
      		update((int) ((currentUpdateTime - lastUpdateTime)/(1000*1000)));
         
      		endLoopTime = System.nanoTime();
      		deltaLoop = endLoopTime - beginLoopTime;
           
      		if(deltaLoop > desiredDeltaLoop){
      			//Do nothing. We are already late.
      		}else{
      			try{
      				Thread.sleep((desiredDeltaLoop - deltaLoop)/(1000*1000));
      			}catch(InterruptedException e){
      				//Do nothing
      			}
      		}
      	}
	}
   
	private void render() throws IOException {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);
		render(g);
		g.dispose();
		bufferStrategy.show();
	}
   
   
	protected void update(int deltaTime){
		
		x += deltaTime * 0.2;
		timer ++;
		
		if (timer > 100)
		{
			timer = 0;
			
			if (room0)
			{
				wood++;
				stone++;
			}
			if (room1)
			{
				wood++;
				stone++;
			}
		}
		
		if(x > 550){
			x = -450;
			hp -= 5;
		}
	}
   
   
	protected void render(Graphics2D g) throws IOException{
		String loc = System.getProperty("user.dir") + "\\" + "conceptBg" + ".jpg";
		image = ImageIO.read(new File(loc));
		
		g.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
		g.setColor(Color.RED);
		g.fillRect((int)x, 550, 50, 50);
		g.drawString("Health: " + hp, 25, 75);
		g.drawString("Wood: " + wood, 25, 100);
		g.drawString("Stone: " + stone, 25, 125);
	}
   
   


	@Override
	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		// TODO Auto-generated method stub
		return false;
	}

}
