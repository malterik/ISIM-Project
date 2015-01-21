/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dawid;

import utils.Config;

/**
 *
 * @author testo-san
 */
public final class GlobalState implements Comparable<Object>{
    private double[] dwelltimes;
    private static GlobalState instance = null;

    protected GlobalState(){
        // Exists only to defeat instantiation
    }
    
    public static GlobalState getInstance(){
        if (instance==null){
            instance = new GlobalState();
        }
        return instance;
    }
    
    public GlobalState(double[] dwelltimes) {
        this.dwelltimes = dwelltimes;
    }

    @Override
    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public double[] getDwelltimes() {
        return dwelltimes;
    }
    
    public String getGlobal_Lowest_state_string() {
        String Global_Lowest_state_string = new String();
        for (int cc = 0; cc < Config.SAnumberOfSeeds; cc++) {
            Global_Lowest_state_string = Global_Lowest_state_string.concat(" " + cc + ") " + dwelltimes[cc]);
            }
        return Global_Lowest_state_string;
    }
    
    public void setDwelltimes(double[] dwelltimes_x) {
        this.dwelltimes = dwelltimes_x;
    }
    
}
