import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
   
	//screen stuff
	final int WIDTH = 1920;
	final int HEIGHT = 1080;
	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;
	private PlaySound daySong;
	private PlaySound nightSong;
	private boolean gameover = false;
	
	//assets
	private BufferedImage background;
	private BufferedImage build;
	
	//time
	private double timer = 0;
	private int tick = 0;
	private int day = 1;
	
	//monsters
	private Monster monsters[];
	private int monstersSpawned = 0;
	private boolean night = false;
	private Defense defenses = new Defense();
	
	//castle
	private Room rooms[];
	private int selectedRoom = -1;
	private int wood = 20;
	private int stone = 20;
	private int food = 5;
	private int worked = 20;
	private int people = 20;
	private int gold = 0;
	private int hp = 100;
	
   
	
	public Game() 
	{
		//setting variables
		try {
			background = ImageIO.read(new File("src/main/resources/Flattened Bg.PNG"));
			build = ImageIO.read(new File ("src/main/resources/Build.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		daySong = new PlaySound ("song");
		nightSong = new PlaySound ("song");
		daySong.play();
		
		rooms = new Room[12];
		monsters = new Monster[20];
		
		for (int i=0;i<12;i++)
		{
			rooms[i] = null;
		}
		for (int i=0;i<20;i++)
		{
			monsters[i] = null;
		}
		
		updatePop ();
		
		//setting frame
		frame = new JFrame("Untitled 1");
      
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
   
        
//reading clicks. Change in the game from clicking goes here
private class MouseControl extends MouseAdapter
{ 
	
	
	public void mouseClicked(MouseEvent e) {
		
		int x = e.getX();
		int y = e.getY();
		
		System.out.println(x + ", " + y);
		
		if (gameover)
		{
			running = false;
		}
		
		if (selectedRoom == -1)
		{
			if (x > 1291 && x < 1520)//left column
			{
				if (y > 477 && y < 565)//first row
				{
					selectedRoom = 0;
				}
				else if (y > 590 && y < 683)//second row
				{
					selectedRoom = 2;
				}
				else if (y > 710 && y < 799)//third row
				{
					selectedRoom = 4;
				}
				else if (y > 830 && y < 918)//fourth row
				{
					selectedRoom = 6;
				}
			}
			else if  (x > 1575 && x < 1780)//right column
			{
				if (y > 477 && y < 565)//first row
				{
					selectedRoom = 1;
				}
				else if (y > 590 && y < 683)//second row
				{
					selectedRoom = 3;
				}
				else if (y > 710 && y < 799)//third row
				{
					selectedRoom = 5;
				}
				else if (y > 830 && y < 918)//fourth row
				{
					selectedRoom = 7;
				}
			}
		}
		else
		{
			if (x > 1637 && x < 1690 && y > 554 && y < 594)
			{
				selectedRoom = -1;
			}
			else if (x > 1262 && x < 1358 && y > 695 && y < 732)
			{
				if (rooms[selectedRoom] == null)
				{
					if (wood >= 15 && stone >= 15)
					{
						wood -= 15;
						stone -= 15;
						makeRoom ("throne");
						selectedRoom = -1;
					}
				}
			}
			else if (x > 1586 && x < 1682 && y > 710 && y < 751)
			{
				if (rooms[selectedRoom] == null)
				{
					makeRoom ("mine");
					selectedRoom = -1;
				}
			}
			else if (x > 1262 && x < 1358 && y > 782 && y < 820)
			{
				if (rooms[selectedRoom] == null)
				{
					makeRoom ("barracks");
					selectedRoom = -1;
				}
			}
			else if (x > 1588 && x < 1681 && y > 808 && y < 845)
			{
				if (rooms[selectedRoom] == null)
				{
					if (wood >= 10 && stone >= 10)
					{
						wood -= 10;
						stone -= 10;
						makeRoom ("armory");
						selectedRoom = -1;
					}
				}
				makeRoom ("mill");
				selectedRoom = -1;
			}
			else if (x > 1263 && x < 1357 && y > 864 && y < 901)
			{
				if (rooms[selectedRoom] == null)
				{
					if (wood >= 75 && stone >= 100)
					{
						wood -= 75;
						stone -= 100;
						makeRoom ("armory");
						selectedRoom = -1;
					}
				}
			}
			else if (x > 1587 && x < 1682 && y > 897 && y < 933)
			{
				if (rooms[selectedRoom] == null)
				{
					makeRoom ("farm");
					selectedRoom = -1;
				}
			}
			else if (x > 1268 && x < 1398 && y > 981 && y < 1020)
			{
				makeRoom ("");
				selectedRoom = -1;
			}
		}
	
	}
	
	
}
   
	//fps and image updating
	long desiredFPS = 60;
	long desiredDeltaLoop = (1000*1000*1000)/desiredFPS;
    
	boolean running = true;
	
	
	//Runs time forward, sets the frames
	public void run()
	{
      
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
      	
      	frame.dispose();
      	daySong.stop();
      	nightSong.stop();
      	
	}
   
	
	//makes the frame
	private void render() 
	{
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);
		render(g);
		g.dispose();
		bufferStrategy.show();
	}
   
   
	//game updates. Change in the  game based on time goes here
	protected void update (int deltaTime)
	{
		
		timer += (deltaTime/4);
		
		if (timer >= 10)
		{
			tick ++;
			
			if (tick % 1000 == 0)
			{
				if (night == false)
				{
					night = true;
					daySong.stop();
					nightSong.play();
				}
				else
				{
					night = false;
					nightSong.stop();
					daySong.play();
					day++;
					monstersSpawned = 0;
					
					updateRoom ("barracks");
					if (people > food)
					{
						people = (people-food)/2;
						food = 0;
					}
					else
					{
						food -= people;
					}
					
					updatePop();
				}
				
			}
			
			if (tick % 20 == 0)
			{
				updateRoom ("throne");
				
				defend ();
			}
			
			if (tick % 100 == 0)
			{
				updateRoom ("farm");
				updateRoom ("mine");
				updateRoom ("mill");
			}
			
			updateMonsters();
			
			if (night)
			{
				switch (day) {
				
				case 1:
					if (tick % 100 == 0 && monstersSpawned < 3)
					{
						makeMonster ("goblin");
					}
					break;
				case 2:
					if (tick % 100 == 0 && monstersSpawned < 5)
					{
						makeMonster ("goblin");
					}
					break;
				case 3:
					if (tick % 125 == 0)
					{
						makeMonster ("goblin");
					}
					break;
				case 4:
					if (tick % 100 == 0)
					{
						makeMonster ("goblin");
					}
					break;
				case 5:
					if (tick % 50 == 0 && monstersSpawned < 9)
					{
						makeMonster ("goblin");
					}
					else if (tick % 100 == 0 && monstersSpawned < 10)
					{
						makeMonster ("chimeraGooseMan");
					}
					break;
				}
			}
			
			timer -= 10;
		}
	}
   
   
	//draws images to the screen. Any changes that affect the screen go here.
	protected void render(Graphics2D g){
		
		boolean temp;
		
		temp = g.drawImage(background, 0, 0, WIDTH, HEIGHT, null);
		
		for (int i=0;i<20;i++)
		{
			if (monsters[i] != null)
			{
				g.drawImage(monsters[i].getSprite(), monsters[i].getX(), 881, 64, 64, null);
			}
		}
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
		
		if (night)
		{
			g.drawString("Night " + day, 100, 75);
		}
		else
		{
			g.drawString("Day " + day, 100, 75);
		}
		
		g.drawString("Health: " + hp, 200, 75);
		g.drawString("Wood: " + wood, 300, 75);
		g.drawString("Stone: " + stone, 400, 75);
		g.drawString("Gold: " + gold, 500, 75);
		g.drawString("Food: " + food, 600, 75);
		g.drawString("Knights: " + defenses.getKNum(), 700, 75);
		g.drawString("Archers: " + defenses.getANum(), 800, 75);
		g.drawString("Peasants: " + (people-worked) + "/" + people, 900, 75);
		
		if (selectedRoom != -1)
			g.drawImage(build, (WIDTH/2), (HEIGHT/2), 750, 500, null);
	}
	
	
	//creates a monster
	private void makeMonster (String name)
	{
		int i=0;
		
		for (i=0;i<20;i++)
		{
			if (monsters[i] == null)
			{
				monsters[i] = new Monster(name);
				monstersSpawned++;
				return;
			}
		}
		
		monsters[0] = null;
		monsters[0] = new Monster (name);
		monstersSpawned++;
	}
   
	
	//updates a room, adding necessary materials to the player inv
	private void updateRoom (String roomType)
	{
		int i=0;
		
		for (i=0;i<12;i++)
		{
			if (rooms[i] != null)
			{
				if (rooms[i].getName() == roomType)
				{
					wood += rooms[i].getWood();
					stone += rooms[i].getStone();
					gold += rooms[i].getGold();
					people += rooms[i].getPeople();
					food += rooms[i].getFood();
				}
			}
		}
		
		return;
	}
	
	
	//lets the defenses hit  the monsters
	private void defend ()
	{
		int i=0, j=0;
		boolean dead = false;
		
		for (i=0;i<defenses.getKNum();i++)
		{
			for (j=0;j<20;j++)
			{
				if (monsters[j] != null)
				{
					if (monsters[j].getX() >= 723)
					{
						dead = monsters[j].dealDamage(defenses.getKDam());
						
						if (dead)
							monsters[j] = null;
						
						break;
					}
				}
			}
		}
		
		
		for (i=0;i<defenses.getANum();i++)
		{
			for (j=0;j<20;j++)
			{
				if (monsters[j] != null)
				{
					dead = monsters[j].dealDamage(defenses.getADam());
					
					if (dead)
						monsters[j] = null;
					
					break;
				}
			}
		}
		
		return;
	}

	
	//lets the monsters hit the defenses (and move)
	private void updateMonsters ()
	{
		int i=0, temp=0;
		
		for (i=0;i<20;i++)
		{
			if (monsters[i] != null)
			{
				temp = monsters[i].update();
				
				if (temp != 0)
				{
					if (defenses.getKNum() > 0)
					{
						defenses.attack(temp);
						updatePop ();
					}
					else
					{
						hp -= temp;
						if (hp <= 0)
						{
							gameover = true;
						}
					}
				}
			}
		}
	}
	
	
	//builds a room
	private void makeRoom (String roomType)
	{
		if (selectedRoom != -1)
		{
			if (rooms[selectedRoom] == null)
			{
				rooms[selectedRoom] = new Room (roomType);
			}
			else
			{
				rooms[selectedRoom] = null;
				rooms[selectedRoom] = new Room (roomType);
			}
		}
		
		updatePop ();
		
		return;
	}
	
	
	//updates the working population
	private void updatePop ()
	{
		int i=0, temp=0;
		
		worked = 0;
		
		for (i=0;i<defenses.getANum();i++)
		{
			if (worked < people)
				worked++;
			else
			{
				defenses.reduce("archer");
			}
		}
		
		for (i=0;i<defenses.getKNum();i++)
		{
			if (worked < people)
				worked++;
			else
			{
				defenses.reduce("knight");
			}
		}
		
		for (i=0;i<12;i++)
		{
			if (rooms[i] != null)
			{
				temp = rooms[i].getWorkers();
				
				if (worked + temp <= people)
				{
					rooms[i].isWorked(true);
					worked += temp;
				}
				else
				{
					rooms[i].isWorked(false);
				}
			}
		}
		
		return;
	}
	
	
	@Override
	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		return false;
	}

}
