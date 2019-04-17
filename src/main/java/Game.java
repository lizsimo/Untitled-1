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

import java.util.Map;
import java.util.HashMap;


public class Game implements Runnable, ImageObserver{
   
	//screen stuff
	final int WIDTH = 1280;
	final int HEIGHT = 720;
	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;
	private PlaySound daySong;
	private PlaySound nightSong;
	private boolean gameover = false;
	
	//assets
	private BufferedImage background;
	private BufferedImage build;
	private BufferedImage gameOverImg;
	private final int buildMenuWidth = WIDTH / 2;
	private final int buildMenuHeight = HEIGHT / 2;
	
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
	private int silver = 0;
	private int hp = 100;

	// Left, Top, Right, Bottom
	private int[][] selectorPositions = new int[][] {
		{859, 	317, 	1012, 	378},
		{1027, 	317, 	1179, 	378},
		{859,	395,	1012,	455},
		{1027,	395,	1179,	455},
		{859,	475,	1012,	533},
		{1027,	475,	1179,	533},
		{859,	551,	1012,	611},
		{1027,	551,	1179,	611}
	};

	private int[][] buildMenuSelectorPositions = new int[][] {
		{579,	292,	657,	317},
		{856,	304,	934,	326},
		{579, 	357,	657,	381},
		{856,	375,	934,	397},
		{579, 	415,	657,	438},
		{856,	437,	934,	462},
	};
	private final String[] buildNames = new String[]{
		"throne",
		"mines",
		"barracks",
		"mill",
		"armory",
		"farm"
	};
	private final Map<String, BuildRequirement> buildReqs = new HashMap<String, BuildRequirement>() {
		{
			put("throne", new BuildRequirement(15, 15, 0, 0, 0));
			put("mines", new BuildRequirement(0, 0, 5, 0, 0));
			put("barracks", new BuildRequirement(10, 10, 5, 0, 0));
			put("mill", new BuildRequirement(0, 0, 10, 0, 0));
			put("armory", new BuildRequirement(75, 100, 3, 0, 0));
			put("farm", new BuildRequirement(0, 0, 5, 0, 0));
		}
	};
	
   
	
	public Game() 
	{
		//setting variables
		try {
			background = ImageIO.read(new File("src/main/resources/Flattened Bg.PNG"));
			build = ImageIO.read(new File ("src/main/resources/Build.jpg"));
			gameOverImg = ImageIO.read(new File("src/main/resources/GameOverImg.png"));
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
      
		canvas.addMouseListener(new MouseController());
      
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
      
		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();
      
		canvas.requestFocus();
	}
   
        
	//reading clicks. Change in the game from clicking goes here
	public class MouseController extends MouseAdapter
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
				for (int i = 0; i < selectorPositions.length; i++) {
					if (x > selectorPositions[i][0] && x < selectorPositions[i][2] && y > selectorPositions[i][1] && y < selectorPositions[i][3]) {
						selectedRoom = i;
					}
				}
			}
			else
			{
				int buildOption = -1;
				for (int i = 0; i < buildMenuSelectorPositions.length; i++) {
					if (x > buildMenuSelectorPositions[i][0] && x < buildMenuSelectorPositions[i][2] && y > buildMenuSelectorPositions[i][1] && y < buildMenuSelectorPositions[i][3]) {
						buildOption = i;
					}
				}
				if (buildOption == -1) {
					selectedRoom = -1;
				} else {
					String buildName = buildNames[buildOption];
					BuildRequirement req = buildReqs.get(buildName);
					if (req.getWood() > wood) {
						System.out.println("You don't have enough wood");
					} else if (req.getStone() > stone) {
						System.out.println("You don't have enough stone");
					} else if (req.getPeasants() > people) {
						System.out.println("You don't have enough people");
					} else if (req.getSilver() > silver) {
						System.out.println("You don't have enough silver");
					} else if (req.getGold() > gold) {
						System.out.println("You don't have enough gold");
					} else {
						wood -= req.getWood();
						stone -= req.getStone();
						people -= req.getPeasants();
						silver -= req.getSilver();
						gold -= req.getGold();
						makeRoom(buildName);
					}
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
		if (hp <= 0) {
			g.drawImage(gameOverImg, 0, 0, WIDTH, HEIGHT, null);
			return;
		}
		
		boolean temp;
		
		temp = g.drawImage(background, 0, 0, WIDTH, HEIGHT, null);
		
		for (int i=0;i<20;i++)
		{
			if (monsters[i] != null)
			{
				g.drawImage(monsters[i].getSprite(), monsters[i].getX(), 500, 64, 64, null);
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
			g.drawImage(build, ((WIDTH - buildMenuWidth)/2), ((HEIGHT- buildMenuHeight)/2), buildMenuWidth, buildMenuHeight, null);
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