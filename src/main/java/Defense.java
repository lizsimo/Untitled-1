
public class Defense {
	
	private int numArchers;
	private int archerDamage;
	private int numKnights;
	private int knightDamage;
	private int knightHealth;
	private int temp;
	
	
	public Defense ()
	{
		numArchers = 0;
		archerDamage = 1;
		numKnights = 5;
		knightHealth = 10;
		knightDamage = 5;
		temp = 0;
		
	}
	
	
	public int getADam ()
	{
		return archerDamage;
	}
	
	
	public int getKDam ()
	{
		return knightDamage;
	}
	
	
	public int getANum ()
	{
		return numArchers;
	}
	
	
	public int getKNum ()
	{
		return numKnights;
	}
	
	
	public void attack (int damage)
	{
		temp += damage;
		
		if (temp >= knightHealth)
		{
			numKnights--;
			temp = 0;
		}
	}
	
	
	public void recruit (String type)
	{
		if (type == "archer")
		{
			numArchers++;
		}
		else if (type == "knight")
		{
			numKnights++;
		}
	}
	
	
	public void upgrade (String up, int amount)
	{
		if (up == "aDam")
		{
			archerDamage += amount;
		}
		else if (up == "kDam")
		{
			knightDamage += amount;
		}
		else if (up == "kHP")
		{
			knightHealth += amount;
		}
	}
	
	
	public void reduce (String type)
	{
		if (type == "archer")
		{
			numArchers--;
		}
		else if (type == "knight")
		{
			numKnights--;
		}
	}
	
}
