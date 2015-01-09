package utils;

import java.util.HashSet;
import java.util.Set;

public class Needle {
	
	private Set<Seed> seeds;
	
	public Needle()
	{
		seeds = new HashSet<Seed>();
	}
	
	public Set<Seed> getSeeds()
	{
		return seeds;
	}
	
	public void addSeed(Seed seed)
	{
		seeds.add(seed);
	}
}
