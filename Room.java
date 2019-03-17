
public class Room {
	
	private String name;
	private int level = 0;
	private int wood = 0;
	private int stone = 0;
	private int food = 0;
	private int people = 0;
	private int gold = 0;
	
	
	
	public Room (String input)
	{
		
		name = input;
		
		if (name == "Throne")
		{
			wood = 1;
			stone = 1;
		}
		else if (name == "Barracks")
		{
			people = 5;
		}
		else if (name == "mine")
		{
			stone = 5;
			gold = 1;
		}
		else if (name == "kitchen")
		{
			food = 5;
		}
		else if (name == "mill")
		{
			wood = 5;
		}
		else if (name == "armory")
		{
			
		}
		else if (name == "blacksmith")
		{
			
		}
		
	}
	
	
	public String getName ()
	{
		return name;
	}
	
	
	public int getWood ()
	{
		return wood;
	}
	
	
	public int getStone ()
	{
		return stone;
	}
	
	
	public int getFood ()
	{
		return food;
	}
	
	
	public int getPeople ()
	{
		return people;
	}
	
	
	public int getGold ()
	{
		return gold;
	}
	
	
	public void upgrade ()
	{
		
	}

}
