//code from University of Washington CSS 161
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Connect4 {

	private static JButton buttons[] = new JButton[42]; //create 9 buttons

    private static minmaxag Agent;


	public static void main (String[] args)
	{
        Agent = new minmaxag(6, 7, 4);
		gamePanel(); //launch game
        
	}
	
	private static void gamePanel(){
		JFrame frame = new JFrame ("Tic Tac Toe");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);


		JPanel panel = new JPanel(); //creating a panel with a box like a tic tac toe board
		panel.setLayout (new GridLayout (6, 7));
		panel.setBorder (BorderFactory.createLineBorder (Color.gray, 3));
		panel.setBackground (Color.white);

		for(int i=0; i<=41; i++){ //placing the button onto the board
			buttons[i] = new MyButton(i);
			panel.add(buttons[i]);			
		}

		frame.getContentPane().add (panel);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(500, 500);// set frame size and let teh game begin
	}

	public static int xOrO=0; // used for counting

	private static class MyButton extends JButton 
	implements ActionListener {//creating own button class
		
		int again=1000;//set again at 1000 so we don't make the mistake we can play again
		boolean win=false; // there is not a win
		String letter; // x or o
        int ID;
		public MyButton() {	// creating blank board
			super();
			letter=" ";
			setFont(new Font("Dialog", 1, 60));
			setText(letter);
			addActionListener(this);
            ID = 0;
		}
        public MyButton(int num) {    // creating blank board
            super();
            letter=" ";
            setFont(new Font("Dialog", 1, 60));
            setText(letter);
            addActionListener(this);
            ID = num;
        }
        public int getID(){
            return ID;
        }
		public void actionPerformed(ActionEvent e) { // placing x or o's
            
            if((xOrO%2)==0 && getText().equals(" ") && win==false){
                letter="X";
                xOrO=xOrO+1;
                System.out.println(letter + "\n"+xOrO);
                //setText(letter);
             
                int pos = ID;
                boolean out = false;
                while((pos + 7) < 42 && !out){
                  if(buttons[pos + 7].getText().equals(" "))
                     pos += 7;
                  else
                   out = true;
                }
             
                //setText(letter);
                buttons[pos].setText(letter);
            }
            
            for(int i = 0; i < 42; i++){
                
                if(straightup(i) || straightdown(i) || left(i) || right(i) ||
                   diagleftup(i) || diagrightup(i) || diagrightdown(i) || diagleftdown(i))
                    win = true;
            }
            
            if(win == true){ // if the game ends let the user know who wins and give option to play again
                again=JOptionPane.showConfirmDialog(null, letter + " wins the game!  Do you want to play again?",letter + "won!",JOptionPane.YES_NO_OPTION);
                
            } else if(xOrO >= 42 && win == false){//tie game, announce and ask if the user want to play again
                again=JOptionPane.showConfirmDialog(null, "The game was tie!  Do you want to play again?","Tie game!",JOptionPane.YES_NO_OPTION);
                win=true;
            }
            
            if(again==JOptionPane.YES_OPTION && win==true){ // if the user want to play again clear all the button and start over
                clearButtons();
                win=false;
            }
            else if(again==JOptionPane.NO_OPTION){
                System.exit(0); // exit game if the user do not want to play again
            }
            
        if((xOrO%2)==1 && win==false) {

            String state = "";
            for(int i = 0; i <= 41; i++){
                if(buttons[i].getText().equals(" "))
                   state += "_";
                else
                   state += buttons[i].getText();
            }
			
            //********Code for minimax goes here
            System.out.println("computer thinking, this may take a while");
            System.out.println(state);
            int index = 0;
            index = Agent.move(state);
            int pos = index;
            //**** end of code for minimax;
            boolean out = false;
            while((pos + 7) < 42 && !out){
               if(buttons[pos + 7].getText().equals(" "))
                  pos += 7;
               else
                  out = true;
            }
             
            //setText(letter);
            //buttons[pos].setText(letter);
            
            
            if(buttons[pos].getText().equals(" ")){
               letter = "O";
               buttons[pos].setText("O"); //minimax agent always play o
               xOrO += 1;
            }
            
            
        }
            
            
            for(int i = 0; i < 42; i++){
        
                if(straightup(i) || straightdown(i) || left(i) || right(i) ||
                   diagleftup(i) || diagrightup(i) || diagrightdown(i) || diagleftdown(i))
                    win = true;
            }
			
			if(win == true){ // if the game ends let the user know who wins and give option to play again
				again=JOptionPane.showConfirmDialog(null, letter + " wins the game!  Do you want to play again?",letter + "won!",JOptionPane.YES_NO_OPTION);
				
			} else if(xOrO >= 42 && win == false){//tie game, announce and ask if the user want to play again
				again=JOptionPane.showConfirmDialog(null, "The game was tie!  Do you want to play again?","Tie game!",JOptionPane.YES_NO_OPTION);
				win=true;
			}	
			
			if(again==JOptionPane.YES_OPTION && win==true){ // if the user want to play again clear all the button and start over
					clearButtons();			
					win=false;
			}
			else if(again==JOptionPane.NO_OPTION){
				System.exit(0); // exit game if the user do not want to play again
			}




		}

	}
	
	public static void clearButtons(){
		
		for(int i=0; i<=41; i++){// clear all 8 buttons
			buttons[i].setText(" ");						
		}
		xOrO=0; // reset the count
		
	}
    public static boolean straightup(int b){
        int [] list = new int[4];
        int val = b;
        boolean filled = true;
        for(int i = 0; i< 4; i++){
            if (val  >= 0){
                list[i] = val;
                val -= 7;
            }
            else{filled = false;}
        }
        if (filled == false)
            return false;
        else{
            if(buttons[list[0]].getText() == buttons[list[1]].getText() &&
               buttons[list[1]].getText() == buttons[list[2]].getText() &&
               buttons[list[2]].getText() == buttons[list[3]].getText() &&
               !buttons[list[3]].getText().equals(" "))
                return true;
            else
                return false;
        }
   }
    public static boolean straightdown(int b){
        int [] list = new int[4];
        int val = b;
        boolean filled = true;
        for(int i = 0; i< 4; i++){
            if (val  < 42){
                list[i] = val;
                val += 7;
            }
            else{filled = false;}
        }
        if (filled == false)
            return false;
        else{
            if(buttons[list[0]].getText() == buttons[list[1]].getText() &&
               buttons[list[1]].getText() == buttons[list[2]].getText() &&
               buttons[list[2]].getText() == buttons[list[3]].getText()&&
               !buttons[list[3]].getText().equals(" "))
                return true;
            else
                return false;
        }
    }
    public static int findlevel(int b)
    {
        
        int level = 0;
        
        for(int i = 6; i < 42; i+=7)
            if(b > i)
                level += 1;
        
        return level;
                
    }
    public static boolean left(int b){
        int [] list = new int[4];
        int val = b;
        boolean filled = true;
        int level = findlevel(b);
        
        for(int i = 0; i< 4; i++){
            if (findlevel(val) == level && val >= 0){
                list[i] = val;
                val--;
            }
            else{filled = false;}
        }
        if (filled == false)
            return false;
        else{
            if(buttons[list[0]].getText() == buttons[list[1]].getText() &&
               buttons[list[1]].getText() == buttons[list[2]].getText() &&
               buttons[list[2]].getText() == buttons[list[3]].getText() &&
               !buttons[list[3]].getText().equals(" "))
                return true;
            else
                return false;
        }
    }
    public static boolean right(int b){
        int [] list = new int[4];
        int val = b;
        boolean filled = true;
        int level = findlevel(b);
        
        for(int i = 0; i< 4; i++){
            if (findlevel(val) == level && val < 42){
                list[i] = val;
                val++;
            }
            else{filled = false;}
        }
        if (filled == false)
            return false;
        else{
            if(buttons[list[0]].getText() == buttons[list[1]].getText() &&
               buttons[list[1]].getText() == buttons[list[2]].getText() &&
               buttons[list[2]].getText() == buttons[list[3]].getText() &&
               !buttons[list[3]].getText().equals(" "))
                return true;
            else
                return false;
        }
    }
    public static boolean diagleftup(int b){
        int [] list = new int[4];
        int levela = 0;
        int levelb = 1;
        int val = b;
        boolean filled = true;
        for(int i = 0; i< 4; i++){
            if (val  >= 0 && Math.abs(levela - levelb) == 1){
                levela = findlevel(val);
                list[i] = val;
                val -= 8;
                levelb = findlevel(val);
            }
            else{filled = false;}
        }
        if (filled == false)
            return false;
        else{
            if(buttons[list[0]].getText() == buttons[list[1]].getText() &&
               buttons[list[1]].getText() == buttons[list[2]].getText() &&
               buttons[list[2]].getText() == buttons[list[3]].getText() &&
               !buttons[list[3]].getText().equals(" "))
                return true;
            else
                return false;
        }
    }
    public static boolean diagrightdown(int b){
        int [] list = new int[4];
        int val = b;
        int levela = 0;
        int levelb = 1;
        boolean filled = true;
        for(int i = 0; i< 4; i++){
            if (val  < 42 && Math.abs(levela - levelb) == 1){
                levela = findlevel(val);
                list[i] = val;
                val += 8;
                levelb = findlevel(val);
            }
            else{filled = false;}
        }
        if (filled == false)
            return false;
        else{
            if(buttons[list[0]].getText() == buttons[list[1]].getText() &&
               buttons[list[1]].getText() == buttons[list[2]].getText() &&
               buttons[list[2]].getText() == buttons[list[3]].getText() &&
               !buttons[list[3]].getText().equals(" "))
                return true;
            else
                return false;
        }
    }
    public static boolean diagleftdown(int b){
        int [] list = new int[4];
        int levela = 0;
        int levelb = 1;
        int val = b;
        boolean filled = true;
        for(int i = 0; i< 4; i++){
            if (val  < 42 && Math.abs(levela - levelb) == 1){
                levela = findlevel(val);
                list[i] = val;
                val += 6;
                levelb = findlevel(val);
            }
            else{filled = false;}
        }
        if (filled == false)
            return false;
        else{
            if(buttons[list[0]].getText() == buttons[list[1]].getText() &&
               buttons[list[1]].getText() == buttons[list[2]].getText() &&
               buttons[list[2]].getText() == buttons[list[3]].getText() &&
               !buttons[list[3]].getText().equals(" "))
                return true;
            else
                return false;
            
        }
    }
    public static boolean diagrightup(int b){
        int [] list = new int[4];
        int levela = 0;
        int levelb = 1;
        int val = b;
        boolean filled = true;
        for(int i = 0; i< 4; i++){
            if (val  >= 0 && Math.abs(levela - levelb) == 1){
                levela = findlevel(val);
                list[i] = val;
                val -= 6;
                levelb = findlevel(val);
            }
            else{filled = false;}
        }
        if (filled == false)
            return false;
        else{
            if(buttons[list[0]].getText() == buttons[list[1]].getText() &&
               buttons[list[1]].getText() == buttons[list[2]].getText() &&
               buttons[list[2]].getText() == buttons[list[3]].getText() &&
               !buttons[list[3]].getText().equals(" "))
                return true;
            else
                return false;
        }
    }
            
    

}
