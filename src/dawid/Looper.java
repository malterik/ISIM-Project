/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dawid;

import utils.Config;
import utils.Seed;
import utils.Voxel;

/**
 *
 * @author testo-san
 */
public class Looper {
    
    public static Voxel [][][] body;
    public static Seed[] seeds = new Seed[Config.numberOfSeeds];
    
    /**
	 * The Solver Class implements different algorithms to optimize the times a radiation seed stay in the body
	 * @param body
	 * The body of the patient
	 * @param seeds
	 * The seeds which should be optimized
	 */
	
    public Looper(Voxel [][][] body, Seed[] seeds) {
		this.body = body;
		this.seeds = seeds;
	}
    
}
