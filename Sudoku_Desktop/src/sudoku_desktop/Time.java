/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku_desktop;

/**
 *
 * @author Nasko_Nastolen
 */
public class Time
{
    int seconds;
    
    public Time()
    {
        setSeconds(0);
    }
    
    public Time(int seconds)
    {
        setSeconds(seconds);
    }
    
    public void setSeconds(int seconds)
    {
        if(seconds > 0)
            this.seconds = seconds;
    }
    
    public int getTimeInSeconds()
    {
        return seconds;
    }
    
    public String getTimeInHMS()
    {
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds / 60) % 60, seconds - (seconds / 60) * 60);
    }
    
    public void makeTick()
    {
        seconds++;
    }
    
    public void reset()
    {
        seconds = 0;
    }
    
    public static void main(String[] args) {
        Time t = new Time();
        for (int i = 0; i < 7202; i++) {
            t.makeTick();
        }
        System.out.println(t.getTimeInSeconds() + "\n" + t.getTimeInHMS());
    }
}
