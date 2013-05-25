package util;

/**
 * MineSweeper beta 0.1 by NainDesign
 * Programme permettant de jouer au démineur en mode console.
 * Actuellement, aucune sécurité n'existe au niveau des inputs de l'utilisateur, merci de faire attention sous peine de voir le jeu s'arrêter.
 */
import java.util.Scanner;
public class MineSweeper {
	private static Scanner scanner = new Scanner(System.in);
	// initialisation avant le main pour permettre de le modifier dans les méthodes auxilliaires //
	private static boolean boom = false;
	public static void main(String[] args){
		// création d'une grille avec une méthode qui demandera à l'utilisateur la taille et la difficulté désirée //
		Grid grid = newGame();
		// boucle de jeu tant que toutes les cases non minées ne sont pas découvertes ou qu'une mine n'a pas explosé //
		do{
			playTurn(grid);
		}while((boom == false)&& (grid.getRemaining() >0));
		// en cas de sortie de boucle, c'est que le jeu est fini, affichage du résultat //
		finishGame(grid);
	}
	/**
	 * Méthode affichant le résultat en fin de partie, sous la forme d'un message suivit de la grille entièrement révélée.
	 * @param grid La grille de jeu, pour affichage.
	 */
	private static void finishGame(Grid grid){
		if(boom){
			System.out.println("Vous avez explosé!");
			printGridFinal(grid);
		} else {
			System.out.println("Vous avez gagné!");
			printGridFinal(grid);
		}
	}
	/**
	 * Méthode permettant de jouer un coup, sois en testant une case soit en posant/enlevant un drapeau
	 * @param grid La grille de jeu
	 */
	private static void playTurn(Grid grid){
		int x = 0;
		int y = 0;
		int choice = 0;
		// Menu pour choisir l'action de jeu //
		do{
			System.out.println("Que voulez vous faire?");
			System.out.println("1) Tester une case");
			System.out.println("2) Planter/enlever un drapeau");
			System.out.print("Votre choix:");
			choice = scanner.nextInt();
			System.out.println("...");
		}while((choice < 1)||(choice>2));
		// Choix de la case de jeu //
		do{
			System.out.println("Entrez les coordonées de la case.");
			System.out.print("x:");
			x = scanner.nextInt() -1;
			System.out.print("y:");
			y = scanner.nextInt() -1;
			System.out.println("...");
		}while((x<0)||(x>=grid.getX())||(y<0)||(y>=grid.getY()));
		// Résolution de l'action désirée //
		if(choice == 1){
			boom = grid.gridTurn(x, y);
		}else grid.flagCell(x, y);
		// Impression de l'état de la grille //
		printGrid(grid);
	}
	/**
	 * Impression de la grille dans son état d'exploration actuel
	 * @param toprint la grille à imprimer
	 */
	private static void printGrid(Grid toprint){
		printSeparator(toprint.getX());
		for(int y = 0; y< toprint.getY(); y++){
			for(int x = 0; x< toprint.getX();x++){
				if(toprint.getGrid()[x][y].getFlag()){
					System.out.print("F ");
				} else if(toprint.getGrid()[x][y].getClicked()){
					System.out.print(toprint.getGrid()[x][y].getAround() + " ");
				} else System.out.print(". ");
			}
			System.out.println();
		}
		printSeparator(toprint.getX());
	}
	/**
	 * Impression complète de la grille avec les mines
	 * @param toprint la grille à imprimer
	 */
	private static void printGridFinal(Grid toprint){
		printSeparator(toprint.getX());
		for(int y = 0; y< toprint.getY(); y++){
			for(int x = 0; x< toprint.getX();x++){
				if(toprint.getGrid()[x][y].getMined()){
					System.out.print("* ");
				} else
					System.out.print(toprint.getGrid()[x][y].getAround()+ " ");
			}
			System.out.println();
		}
		printSeparator(toprint.getX());
	}
	/**
	 * Méthode qui demande à l'utilisateur la taille et la difficulté désirée et renvoie une grille prête à jouer
	 * @return la grille telle que voulue par l'utilisateur
	 */
	public static Grid newGame(){
		int x = -1;
		int y = -1;
		int mines = -1;
		while((x<1)||(y<1)){
			System.out.println("Entrez les dimensions de la grille (minimum 2 cases)");
			System.out.print("x:");
			x = scanner.nextInt();
			System.out.print("y:");
			y = scanner.nextInt();
		}
		while((mines<1) || (mines> (x*y)-1)){
			System.out.print("Entrez le nombre de mines de la grille (minimum 1):");
			mines = scanner.nextInt();
		}
		return new Grid(x, y, mines);
	}
	/**
	 * Méthode imprimant une ligne de traits d'union
	 * @param int longueur de la ligne
	 */
	private static void printSeparator(int lenght){
		for(int x = 0; x<lenght; x++){
			System.out.print("- ");
		}
		System.out.println();
	}
}
class Grid {
	private int dimX;
	private int dimY;
	private int mines;
	private Cell[][] mainGrid;
	private int turn;
	private int remaining;
	/**
	 * Constructeur de nouvelle grille WARNING La grille elle-même n'est initialisée qu'après le premier coup /WARNING
	 * L'entrée doit vérifier ((x>0) && (y > 0) && (mins>0) && (mins<x*y))
	 * @param x Nombre de colonnes de la grille
	 * @param y Nombre de lignes de la grille
	 * @param mins = nombre de mines
	 */
	public Grid(int x, int y, int mins){
		this.dimX = x;
		this.dimY = y;
		this.mines = mins;
		this.mainGrid = new Cell[dimX][dimY];
		for(int a = 0; a<this.dimX; a++){
			for(int b=0; b<this.dimY; b++){
				this.mainGrid[a][b] = new Cell();
			}
		}
		this.turn = 0;
		this.remaining = x*y -mins;
	}
	/**
	 * getter de la dimension X de la grille
	 * @return la dimension voulue
	 */
	public int getX(){
		return this.dimX;
	}
	/**
	 * getter de la dimension Y de la grille
	 * @return la dimension voulue
	 */
	public int getY(){
		return this.dimY;
	}
	/**
	 * getter du nombre de mines de la grille
	 * @return le nombre désiré
	 */
	public int getMines(){
		return this.mines;
	}
	/**
	 * getter du nombre de coups joués sur la grille
	 * @return le nombre désiré
	 */
	public int getTurn(){
		return this.turn;
	}
	/**
	 * getter de la grille
	 * @return la grille
	 */
	public Cell[][] getGrid(){
		return this.mainGrid;
	}
	/**
	 * Méthode à utiliser pour jouer un coup sur la grille, permet également d'initaliser le placement des mines au premier coup.
	 * L'entrée doit vérifier ((x < dimX) && (y<dimY) && (x<=0) && (y>=0))
	 * @param x coordonée x de la case jouée
	 * @param y coornonée y de la case jouée
	 * @return true si il y a une mine
	 */
	public boolean gridTurn(int x, int y){
		this.turn += 1;
		if(this.turn == 1){
			mineGrid(x, y);
		}
		if(this.mainGrid[x][y].getFlag()){
			return false;
		}else if(this.mainGrid[x][y].getMined()){
			return this.mainGrid[x][y].getMined();
		} else {
			click(x, y);
			return this.mainGrid[x][y].getMined();
		}
	}
	/**
	 * Remplis la grille de mines
	 */
	private void mineGrid(int x, int y){
		int minedCells = 0;
		while(minedCells < this.mines){
			int tempX = (int)(Math.random()*this.dimX);
			int tempY = (int)(Math.random()*this.dimY);
			if(!this.mainGrid[tempX][tempY].getMined() && ((tempX != x)&&(tempY != y))){
				this.mainGrid[tempX][tempY].setMined();
				minedCells++ ;
			}
		}
		calculateAround();
	}
	/**
	 * Place ou enlève un drapeau sur une case si elle n'est pas cliquée
	 * L'entrée doit vérifier (x < dimX) && (y<dimY) && (x<=0) && (y>=0) && !this.mainGrid[x][y].getClicked()
	 * @param x coordonée horizontale
	 * @param y coordonée verticale
	 */
	public void flagCell(int x, int y){
		this.mainGrid[x][y].toggleFlag();
	}
	/**
	 * Calcule le nombre de mines autour de chaque case du tableau
	 */
	private void calculateAround(){
		// Navigation sur l'axe des x //
		for(int x = 0; x<this.dimX; x++){
			// Navigation sur l'axe des y //
			for(int y = 0; y<this.dimY; y++){
				int minesVoisines = 0;
				// Navigation autour de la case, sur l'axe des x //
				for(int a = x-1; a<=x+1; a++){
					// Navigation autour de la case, sur l'axe des y //
					for(int b = y-1; b<=y+1;b++){
						// Vérifie que la case voisine (a,b) sois dans le tableau et différente de (x,y) //
						if((a>=0) && (b>=0) && (a<this.dimX) && (b<this.dimY) && this.mainGrid[a][b].getMined()){
							// ajoute 1 à la valeur around de la case (x,y)
							minesVoisines++;
						}
					}
				}
				this.mainGrid[x][y].setAround(minesVoisines);
			}
		}
	}
	/**
	 * Méthode qui indique qu'une case a été cliquée et propage cela aux cases alentours si il n'y a pas de mine à proximité
	 * @param x coordonée x de la case à cliquer
	 * @param y coordonée y de la case à cliquer
	 */
	private void click(int x, int y){
		this.mainGrid[x][y].setClicked();
		this.remaining += -1;
		if(this.mainGrid[x][y].getAround() == 0){
			// Navigation autour de la case, sur l'axe des x //
			for(int a = x-1; a<=x+1; a++){
				// Navigation autour de la case, sur l'axe des y //
				for(int b = y-1; b<=y+1;b++){
					// Vérifie que la case voisine (a,b) sois dans le tableau, différente de (x,y), non minée et non cliquée //
					if((a>=0) && (b>=0)&& (a<this.dimX)&& (b<this.dimY) && !this.mainGrid[a][b].getClicked()){
						if(!(this.mainGrid[a][b].getAround() == 0)){
							this.mainGrid[a][b].setClicked();
							this.remaining += -1;
						}else if((this.mainGrid[a][b].getAround()==0)){
							click(a, b);
						}
					}
				}
			}
		}
	}
	public int getRemaining(){
		return this.remaining;
	}
}
class Cell{
	private boolean mined;
	private boolean clicked;
	private boolean flag;
	private int around;
	/**
	 * Constructeur de case de base définisant la valeur minée et indiquand que la case n'est pas encore cliquée
	 * WARNING: ne place pas de mine et ne calcule donc pas le nombre de mines environantes, cela se fait au niveau de la grille.
	 */
	public Cell(){
		this.mined = false;
		this.clicked = false;
		this.flag = false;
		this.around = 0;
	}
	/**
	 * Getter pour savoir si une case est minée.
	 * @return true si la case est minée
	 */
	public boolean getMined(){
		return this.mined;
	}
	/**
	 * Getter pour savoir si une case est cliquée.
	 * @return true si la case est cliquée.
	 */
	public boolean getClicked(){
		return this.clicked;
	}
	/**
	 * Getter pour connaître le nombre de mines environnantes
	 * @return le nombre de mines autour de la case
	 */
	public int getAround(){
		return this.around;
	}
	/**
	 * Set pour indiquer le nombre de mines autour de la case
	 * @param arnd le nombre de mines autour de la case
	 */
	public void setAround(int arnd){
		this.around = arnd;
	}
	/**
	 * Set pour indique qu'une case a été cliquée. Aucun paramètre nécessaire vu que cette action est irréversible.
	 */
	public void setClicked(){
		this.clicked = true;
	}
	/**
	 * Set pour indiquer qu'une case est minée. Action irréversbile
	 */
	public void setMined(){
		this.mined = true;
	}
	/**
	 * Getter pour le statut flag d'une case.
	 */
	public boolean getFlag(){
		return this.flag;
	}
	/**
	 * change le statut flag d'une case. Action réversible.
	 */
	public void toggleFlag(){
		this.flag = !this.flag;
	}
}