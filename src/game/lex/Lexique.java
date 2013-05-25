package game.lex;

import game.GameType;
import game.Playable;
import game.lex.en.LexEN;
import game.lex.fr.LexFR;

abstract public class Lexique implements LexEN, LexFR{

	public Lexique(){
		
	}
	
	abstract public GameType getGameType();
	
}
