import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;

/**
 * RunesReaperUI Class
 * This class represents the main application for the RunesReaper game.
 * It extends the JavaFX Application class and manages the game's UI and logic.
 *
 * The game is a Minesweeper-like game with a magical theme, where players uncover
 * runes (cells) while avoiding fire traps and collecting gems.
 * 
 * Collected gems can be used to buy potions and spells which help the user throughout the game.
 * Life Potions save the user from dying when user clicks on a Fire cell (mine) 
 * Clairvoyance Spells (hints) can be used to reveal one mine adjacent to a cell, or in the cell itself if there is any.
 */
public class RunesReaperUI extends Application {
	
	//Game state and data
	private int GRID_SIZE = 17;
	private int CELL_SIZE = 40;
	private int NUM_FIRE_RUNES = 30;
	private boolean[][] fires;//Tracks whether cell at a position has fire
	private boolean[][] revealed;//Tracks whether cell at a position is revealed
	private int gemCount = 0;
	private int hintsCount = 0;
	private int potionCount = 0;
	private boolean[][] flagged;//Tracks whether cell at a position is flagged
	private int cellsOpened = 0;
	private boolean isClairvoyant = false;
	private Random random = new Random();
	private Timeline timeline;
	private int secondsElapsed = 0;

	//UI components
	private Stage primaryStage;
	private BorderPane gameLayout;
	private int WINDOW_WIDTH = 800;
	private int WINDOW_HEIGHT = 900;
	private Label timerLabel;
	private Label cellsOpenedLabel;
	private Button[][] cells;
	private String gemsLabelValue = "Gems: ";
	private Label gemsLabel = new Label(gemsLabelValue + gemCount);
	private String hintsLabelValue = "Clairvoyance: ";
	private Button hintsLabel = new Button(hintsLabelValue + hintsCount);
	private String potionLabelValue = "Life Potions: ";
	private Label potionLabel = new Label(potionLabelValue + potionCount);
	private Button[][] gemButtons = new Button[GRID_SIZE][GRID_SIZE];
	private Image flagImage;	

    /**
     * The main entry point.
     * Sets up the primary stage and shows the start screen.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        //Disabling window resizing so that animations look consistent
        primaryStage.setResizable(false);
        primaryStage.setMaximized(false);
        //Calls the showStartScreen() function which is defined on line 79 to display the start screen
        showStartScreen(); 
    }

    /**
     * Displays the start screen of the game.
     * This method sets up the initial UI with the game logo, play button, and about button.
     */
    private void showStartScreen() {
    	//Calls reset function which is defined in Line 128
    	reset();
            	
    	//Creates a vertical box layout
        VBox startLayout = new VBox(20);
        startLayout.setAlignment(Pos.CENTER);
        
        //Creates an ImageView to display logo image
        Image logo = new Image("img/logo.png"); 
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(500); 
        logoView.setPreserveRatio(true);

        //Creates "PLAY" button
        Button playButton = new Button("PLAY");
        //Sets action to show the Game screen on click (action)
        playButton.setOnAction(e -> showGameScreen());
        //Adds CSS class "button1" for styling
        playButton.getStyleClass().add("button1");
        
        //Creates "ABOUT" button
        Button aboutButton = new Button("ABOUT");
        //Sets event listener to show the About screen on action (click)
        aboutButton.setOnAction(e -> showAboutDialog());
        //Adds CSS class "button1" for styling
        aboutButton.getStyleClass().add("button1");

        //Adds the logo, play button, and about button to the layout
        startLayout.getChildren().addAll(logoView,playButton, aboutButton);
        
        //Creates a new Scene and adds startLayout and specified size
        Scene startScene = new Scene(startLayout, WINDOW_WIDTH, WINDOW_HEIGHT);
        //Adds external CSS file for styling
        startScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                
        //Sets the created startScene as the current scene of the main window
        primaryStage.setScene(startScene);
        //Sets the title of window
        primaryStage.setTitle("RunesReaper");

        //Setting up a custom cursor by calling this function define in Line TODO
        setWandCursor();
        
        //Displays the window
        primaryStage.show();
    }
    
    /**
     * Resets all game counters to their initial values.
     * This method is called when starting a new game or returning to the start screen.
     */
    private void reset() {
    	cellsOpened = 0;
    	gemCount = 0;
    	gemsLabel.setText(gemsLabelValue + gemCount);
    	hintsCount = 0;
        hintsLabel.setText(hintsLabelValue + hintsCount);
    	potionCount = 0;
        potionLabel.setText(potionLabelValue + potionCount);
        
        //Stops and refreshes the Timer
        stopTimer();
	}

    /**
     * Displays the about dialog, which contains information about the game and how to play.
     */
    private void showAboutDialog() {
    	
    	//Creates a new vertical box layout with 20px spacing between elements which will be aligned in the center
    	VBox aboutLayout = new VBox(20);
    	aboutLayout.setAlignment(Pos.CENTER);
        
    	//Displays logo image
        Image logo = new Image("img/logo.png"); 
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(500); 
        logoView.setPreserveRatio(true);
        
        //Creates a new Text and sets the content of the HowToPlayText
        Text HowToPlayText = new Text();
        HowToPlayText.setText("Welcome, dear Mage!");
        HowToPlayText.getStyleClass().add("title-light");        
        Text howToPlayContent1 = new Text(
        		"Your mission is to navigate this magical terrain, while collecting precious\n"+
                "gems and avoiding dangerous fire traps!"
            );
        howToPlayContent1.getStyleClass().add("content");
        Text howToPlayContent2 = new Text("The Runes and the Firefield");
        howToPlayContent2.setStyle("-fx-font-weight: bold;");
        howToPlayContent2.getStyleClass().add("content");
        Text howToPlayContent3 = new Text(        		
                "The game board represents the Rune cells filled with a dangerous Firefield.\n" +
                "Beware of hidden fire traps scattered across the field.\n" +
                "Triggering a Fire trap will instantly ruin everything.\n" +
                "Click on Rune cells to reveal what's beneath.\n" +
                "Be cautious! Each click could unveil a gems or a dangerous fire!\n" +
                "Right-click to set a mark on a possible fire rune!\n" +
                "Open all Rune cells to win!!"
            );
        howToPlayContent3.getStyleClass().add("content");
        
        //Displays text image
        Image text = new Image("img/text.png"); 
        ImageView textView = new ImageView(text);
        textView.setFitWidth(527); 
        textView.setPreserveRatio(true);
        
        //Creates "HOME" button that goes back to the Start/Home screen
        Button homeButton = new Button("Home");
        //Sets event listener to show the Start/Home screen on action (click)
        homeButton.setOnAction(e -> showStartScreen());
        homeButton.getStyleClass().add("button1");

        aboutLayout.getChildren().addAll(logoView,HowToPlayText,howToPlayContent1,howToPlayContent2,howToPlayContent3,textView,homeButton);

        //Creates a new Scene with the aboutLayout of the same specified size and adds external CSS for styling
        Scene aboutScene = new Scene(aboutLayout, WINDOW_WIDTH, WINDOW_HEIGHT);
        aboutScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        //Sets the current scene of the primary stage to about scene
        primaryStage.setScene(aboutScene);
        primaryStage.setTitle("About RunesReaper");

        //Sets the wand image as custom cursor
        setWandCursor();
    }

    /**
     * Initializes and displays the main game screen.
     * This method sets up the game grid, initializes game state, and starts the timer.
     */
    private void showGameScreen() {
    	cells = new Button[GRID_SIZE][GRID_SIZE];
    	fires = new boolean[GRID_SIZE][GRID_SIZE];
        revealed = new boolean[GRID_SIZE][GRID_SIZE];
        flagged = new boolean[GRID_SIZE][GRID_SIZE];

        reset();
        
    	//Creates a BorderPane called "gameLayout"
        gameLayout = new BorderPane();

        //Adds gameLayout BorderPane to the scene
        Scene gameScene = new Scene(gameLayout, WINDOW_WIDTH, WINDOW_HEIGHT);        
        gameScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        //Creates the top bar of the game screen by calling createTopBar() function defined in Line 202
        HBox topBar = createTopBar();
        //Sets the top bar at the top of the BorderPane
        gameLayout.setTop(topBar);
        //--
                
        //Creates new GridPane layout to organize the tiles in a grid created by calling createGameGrid() function which is defined in Line TODO
        GridPane gameGrid = createGameGrid();
        gameGrid.setPadding(new Insets(20));
        //Centers the tiles to the scene
        gameLayout.setCenter(gameGrid);
        //--

        //Creates the top bar of the game screen by calling createBottomBar() function defined in Line TODO
        HBox bottomBar = createBottomBar();
        //Sets the bottom bar to the bottom of the gameLayout BorderPane
        gameLayout.setBottom(bottomBar);
        //--        

        //Sets the game scene as the current scene on the primary stage
        primaryStage.setScene(gameScene);
    	
    	initializeGame();

        //Starts the timer
        startTimer();
        
        //Sets up the wand image as custom cursor
        setWandCursor();
    }
    
    /**
     * Creates the top bar of the game screen, which contains game information and controls.
     *
     * @return An HBox containing the top bar elements.
     */
    private HBox createTopBar() {
    	
    	//Creates a new horizontal box layout with 20px spacing between children elements aligned in the center
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER);
        //Adds padding of 20 pixels on all sides of the HBox
        topBar.setPadding(new Insets(20));

        //Creates a Label to display the number of gems, initially set to ZERO
        gemsLabel.getStyleClass().add("info1");
        
        //Creates a Label to display the number of potions
        potionLabel.getStyleClass().add("info1");
        
        //Creates a Button to display the number of clairvoyance (hints)
        hintsLabel.getStyleClass().add("button1");
        hintsLabel.setOnAction(e -> {
        	if (this.isClairvoyant) {
                this.disableClairvoyance();
                hintsCount++;
                this.hintsLabel.setText(hintsLabelValue + this.hintsCount);
                return;
            }
        	if(hintsCount <= 0) {
                showNoHintsPopup();
                return;
            }
            if (!this.isClairvoyant) {
                this.enableClairvoyance();
                this.hintsCount--;
                this.hintsLabel.setText(hintsLabelValue + this.hintsCount);
            }
        });
        
        //Creates a Label to display the elapsed time, starting from 0 seconds
        timerLabel = new Label("Time: 0s");
        timerLabel.getStyleClass().add("info1");

        //Adds all created labels and buttons to the HBox
        topBar.getChildren().addAll(timerLabel, gemsLabel, potionLabel, hintsLabel);
        
        //Returns the fully constructed HBox to be used as the top bar
        return topBar;
    }
    
    /**
     * Creates the bottom bar of the game screen, which contains additional game information and navigation buttons.
     *
     * @return An HBox containing the bottom bar elements.
     */
    private HBox createBottomBar() {    	
    	HBox bottomBar = new HBox(20);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(20));

        //Creates label to show the number of opened cells
        cellsOpenedLabel = new Label("Cells Opened: 0");
        cellsOpenedLabel.getStyleClass().add("info1");
        
        //Creates "HOME" button that goes back to the Start/Home screen
        Button homeButton = new Button("Home");
        homeButton.setOnAction(e -> showStartScreen());
        homeButton.getStyleClass().add("button1"); 
        
        //Creates "SHOP" button
        Button shopButton = new Button("Shop");
        shopButton.getStyleClass().add("button1");
        shopButton.setOnAction(e -> showShopPopup());
        
        //Adds Cells Opened Counter, "HOME" and "SHOP" buttons to the bottom bar
        bottomBar.getChildren().addAll(cellsOpenedLabel,homeButton,shopButton);
        
        //Returns the fully constructed HBox to be used as the bottom bar
        return bottomBar;
    }
    
    /**
     * Creates the main game grid.
     * This method sets up the circular grid of cells for the game.
     *
     * @return A GridPane containing the game cells.
     */
    private GridPane createGameGrid() {
        GridPane gameGrid = new GridPane();
        gameGrid.setAlignment(Pos.CENTER);
        //Sets 3px width gap between columns
        gameGrid.setHgap(3);
        //Sets 3px width gap between rows
        gameGrid.setVgap(3);
        
		
        /* Circular Grid Implementation 
		 * 1) A SQUARE grid is assumed with each side being of specified length GRID_SIZE 
		 * 2) Therefore a circle fitting inside this square will have a diameter of GRID_SIZE 
		 * 3) The radius of this circle = GRID_SIZE/2
		 * 4) For the grid to be in circular shape, all grid cells must lie within the circle 
		 * 5) Therefore distance of each cell from the center of the circle must be less than the radius 
		 * 6) For each cell postion (x,y) its distance from the center of circle is calculated using Pythagoras Theorem 
		 * 	  a) The length of sides of the triangle to calculate this distance are calculated by subtracting radius from x and y values  
		 * 	  b) A Cell is added only if distance is less than the radius
		 */
        
        //Calculates radius of circular grid
        int radius = GRID_SIZE / 2;
              
        //Nested loop for populating the grid, from 0 it counts up to GRID_SIZE (size of the grid)
     	//col: represents the number of columns
     	//row: represents the number of rows
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                //Calculates distance from center using Pythagoras Theorem 
                double distance = Math.sqrt(Math.pow(row - radius, 2) + Math.pow(col - radius, 2));
                //Checks if cell lies within the radius of circle, only then the cell is added
                if (distance < radius) {
                	//Creates a new Button object by calling createCell() function defined in LINE 283
                	Button cell = createCell(row, col);
                    cells[row][col] = cell;
                    
                    //Creates gem button
                    Button gemButton = createGemButton(row, col);
                    gemButtons[row][col] = gemButton;
                    
                    //Creates StackPane to hold both buttons
                    StackPane cellStack = new StackPane();
                    cellStack.getChildren().addAll(cell, gemButton);
                    
                    //Adds the created stack of cells to position
                    gameGrid.add(cellStack, col, row);
                }
            }
        }

        //Returns completed grid
        return gameGrid;
    }

    /**
     * Creates an individual cell button for the game grid.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return A Button representing the cell.
     */
    private Button createCell(int row, int col) {
        Button cell = new Button();
        //Sets the size of the cell
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);

        cell.setMinSize(CELL_SIZE, CELL_SIZE);
        cell.setMaxSize(CELL_SIZE, CELL_SIZE);
        
        //Sets event listener to call cellClick() function with the clicked button (cell) as an argument defined on Line TODO
        cell.setOnAction(e -> cellClick(row, col));
        
        cell.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                toggleFlag(row, col);
            }
        });
        
        //Adds CSS class "game-cell" for styling
        cell.getStyleClass().add("game-cell");
        //Returns created cell
        return cell;
    }
    
    /**
     * Toggles the flag on a cell.
     * This method is called when a cell is right-clicked.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     */
    private void toggleFlag(int row, int col) {
        if (revealed[row][col]) return; // Don't allow flagging of revealed cells

        flagged[row][col] = !flagged[row][col];
        
        if (flagged[row][col]) {
        	flagImage = new Image("img/flag.png");
            ImageView flagView = new ImageView(flagImage);
            flagView.setFitWidth(CELL_SIZE - 10);
            flagView.setFitHeight(CELL_SIZE - 10);
            cells[row][col].setGraphic(flagView);
        } else {
            cells[row][col].setGraphic(null);
        }
    }
    
    /**
     * Initializes the game state.
     * This method sets up and places the fire runes on the game grid.
     */
    private void initializeGame() {
        Random random = new Random();
        int firesPlaced;

        for (firesPlaced = 0; firesPlaced < NUM_FIRE_RUNES;) {
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);
            
            if (!fires[row][col] && cells[row][col] != null) {
            	fires[row][col] = true;
            	firesPlaced++;
            }
        }
        
        // Reset flagged array
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                flagged[row][col] = false;
            }
        }        
        System.out.println("Fires placed: " + firesPlaced);
    }
   
    /**
     * Creates Gem buttons on a cell
     * 
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return A Button representing the gem.
     */
    private Button createGemButton(int row, int col) {
    	Button gemButton = new Button();
        gemButton.setPrefSize(CELL_SIZE, CELL_SIZE);
        gemButton.setMinSize(CELL_SIZE, CELL_SIZE);
        gemButton.setMaxSize(CELL_SIZE, CELL_SIZE);
        gemButton.setVisible(false);
        gemButton.setStyle("-fx-background-color: transparent;");
        
        ImageView gemView = new ImageView(new Image("img/gem.png"));
        gemView.setFitWidth(CELL_SIZE - 7);
        gemView.setFitHeight(CELL_SIZE - 7);
        gemButton.setGraphic(gemView);
        fadeInImage(gemView);
        
        gemButton.setOnAction(e -> {
            collectGem(row, col);
            e.consume();
        });
        
        return gemButton;
    }

    /**
     * Enables Clairvoyance ability
     */
    private void enableClairvoyance()
    {
        this.isClairvoyant = true;        
        Scene currentScene = primaryStage.getScene();
        Image image = new Image("img/cwand.png");
        currentScene.setCursor(new ImageCursor(image));
    }

    /**
     * Disables Clairvoyance ability
     */
    private void disableClairvoyance()
    {
        this.isClairvoyant = false;
        setWandCursor();
    }
    
    /**
     * Creates gems in adjacent cells
     * 
     * @param centerRow The row of the cell.
     * @param centerCol The column of the cell.
     */
    private void spawnGemsInAdjacentCells(int centerRow, int centerCol) {
    	//First, remove any existing gems
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (gemButtons[row][col] != null) {
                    gemButtons[row][col].setVisible(false);
                }
            }
        }
        
        //Determine number of gems (0-3)
        int numGems = random.nextInt(4);
        if (numGems == 0) return;
        
        //Get list of adjacent cells
        int[][] adjacentCells = new int[8][2];
        int validAdjacentCells = 0;
        
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; // Skip the center cell
                
                int newRow = centerRow + i;
                int newCol = centerCol + j;
                
                //Check if the cell is valid and not revealed
                if (newRow >= 0 && newRow < GRID_SIZE && 
                    newCol >= 0 && newCol < GRID_SIZE && 
                    cells[newRow][newCol] != null &&
                    gemButtons[newRow][newCol] != null &&  // Add null check for gemButtons
                    !revealed[newRow][newCol]) {
                    adjacentCells[validAdjacentCells][0] = newRow;
                    adjacentCells[validAdjacentCells][1] = newCol;
                    validAdjacentCells++;
                }
            }
        }
        
        //Spawn gems
        for (int i = 0; i < numGems && i < validAdjacentCells; i++) {
            int index = random.nextInt(validAdjacentCells);
            int gemRow = adjacentCells[index][0];
            int gemCol = adjacentCells[index][1];
            
            if (gemButtons[gemRow][gemCol] != null) {  // Add null check
                gemButtons[gemRow][gemCol].setVisible(true);
            }
            
            // Swap the selected cell to the end and decrease valid count
            int[] temp = adjacentCells[index];
            adjacentCells[index] = adjacentCells[validAdjacentCells - 1];
            adjacentCells[validAdjacentCells - 1] = temp;
            validAdjacentCells--;
        }
    }

    /**
     * Handles the collection of a gem when clicked
     * 
     * @param row The row of the cell.
     * @param col The column of the cell.
     */
	private void collectGem(int row, int col) {
	    //Check if there's a visible gem at the specified position
	    if (gemButtons[row][col] != null && gemButtons[row][col].isVisible()) {    		
	        //Create a new ImageView for the animated gem
	        ImageView animatedGem = new ImageView(new Image("img/gem.png"));
	        //Set the size of the animated gem
	        animatedGem.setFitWidth(55);
	        animatedGem.setFitHeight(55);
	        //Set the initial X position of the gem
	        int xPos = 275;
	        //Set the initial position of the animated gem
	        animatedGem.setTranslateX(xPos);
	        animatedGem.setTranslateY(100);
	        //Add the animated gem to the game layout
	        gameLayout.getChildren().add(animatedGem);
	        
	        //Create a movement animation for the gem
	        TranslateTransition move = new TranslateTransition(Duration.millis(500), animatedGem);
	        move.setToX(xPos); // Keep X position constant
	        move.setToY(10); // Move gem upwards
	        
	        //Create a scale down animation for the gem
	        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(500), animatedGem);
	        scaleDown.setToX(0.5); // Scale to half the original width
	        scaleDown.setToY(0.5); // Scale to half the original height
	        
	        //Create a fade out animation for the gem
	        FadeTransition fade = new FadeTransition(Duration.millis(500), animatedGem);
	        fade.setFromValue(1.0); // Start fully opaque
	        fade.setToValue(0.0); // Fade to fully transparent
	        
	        //Combine all animations into a parallel transition
	        ParallelTransition parallelTransition = new ParallelTransition();
	        parallelTransition.getChildren().addAll(move, scaleDown, fade);
	        
	        //Define actions to perform after the animation completes
	        parallelTransition.setOnFinished(e -> {
	            //Remove the animated gem from the game layout
	            gameLayout.getChildren().remove(animatedGem);
	            //Increment the gem count
	            gemCount++;
	            //Update the gem count display
	            gemsLabel.setText(gemsLabelValue + gemCount);
	            //Hide the collected gem button
	            gemButtons[row][col].setVisible(false);
	        });
	        
	        //Start the animation sequence
	        parallelTransition.play();
	    }
	}
	
	/**
     * Handles action to be taken when a cell is clicked
     * 
     * @param row The row of the cell.
     * @param col The column of the cell.
     */
	private void cellClick(int row, int col) {
	    //Check if the cell is already revealed or flagged
	    if (revealed[row][col] || flagged[row][col]) return;
	    
	    //Check if there's a visible gem on this cell
	    if (gemButtons[row][col] != null && gemButtons[row][col].isVisible()) {
	        return; // Don't process cell click if there's a gem
	    }
	    
	    //Mark the cell as revealed
	    revealed[row][col] = true;
	    //Increment the count of opened cells
	    cellsOpened++;
	    //Update the display of opened cells
	    updateCellsOpenedLabel();
	    
	    //Check if the clicked cell contains a fire
	    boolean isFireSelected = fires[row][col];
	
	    if (isFireSelected) {
	        if(this.isClairvoyant) {
	            //If clairvoyant is active, reveal the fire as frozen
	            setFrozenFire(row, col);
	            this.disableClairvoyance();
	        }
	        else if (this.potionCount > 0) {
	            //If player has potions, use one to freeze the fire
	            setFrozenFire(row, col);
	            this.potionCount--;
	            this.potionLabel.setText(potionLabelValue + this.potionCount);
	            showUsedPotionPopup();
	        } else {
	            //If no protection, reveal fire and end game
	            setFire(row, col);
	            gameOver(false);
	        }
	    } else {
	        //If not a fire, count adjacent fires
	        int adjacentFires = countAdjacentFires(row, col);
	        if (adjacentFires > 0) {
	            //If there are adjacent fires, display the count
	            cells[row][col].setText(String.valueOf(adjacentFires));
	            cells[row][col].getStyleClass().add("number-cell");
	            
	            if(this.isClairvoyant) {
	                //If clairvoyant is active, reveal one adjacent fire
	                this.revealOneAdjacentFire(row, col);
	                this.disableClairvoyance();
	            }
	        } else {
	            //If no adjacent fires, reveal surrounding cells
	            revealAdjacentCells(row, col);
	        }
	        //Spawn gems in adjacent cells
	        spawnGemsInAdjacentCells(row, col);
	    }
	   	
	    //Disable the clicked cell to prevent further interactions
	    cells[row][col].setDisable(true);   
	   
	    //Check if the win condition is met
	    if (checkWinCondition()) {
	        gameOver(true);
	    }
	}
	
	/** 
	 * Sets a fire image on a cell
	 * @param row The row of the cell.
     * @param col The column of the cell.
	 */
	private void setFire(int row, int col) {
	    //Create an ImageView for the fire image
	    ImageView image = new ImageView(new Image("img/fire.png"));
	    //Set the size of the fire image (slightly smaller than the cell)
	    image.setFitWidth(CELL_SIZE - 10);
	    image.setFitHeight(CELL_SIZE - 10);                
	    //Set the fire image as the graphic for the cell
	    cells[row][col].setGraphic(image);
	    //Apply a fade-in animation to the fire image with function defined in Line TODO
	    fadeInImage(image);
	}
	
	/** 
	 * Sets a (disabled) frozen fire image on a cell
	 * @param row The row of the cell.
     * @param col The column of the cell.
	 */
	private void setFrozenFire(int row, int col) {
    	ImageView image = new ImageView(new Image("img/frozen.png"));
    	image.setFitWidth(CELL_SIZE - 10);  // Slightly smaller than cell
    	image.setFitHeight(CELL_SIZE - 10);                
        cells[row][col].setGraphic(image);
        cells[row][col].setStyle("-fx-background-color: #B7C9E2; -fx-border-color: #537eb9;");        
        fadeInImage(image);
	}
	
	/** 
	 * Applies fade in effect on an image
	 */
	private void fadeInImage(ImageView image) {
		// Create fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), image);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
	}

	/**
	 * Updates the label that shows the current count of opened cells
	 */
	private void updateCellsOpenedLabel() {
        cellsOpenedLabel.setText("Cells Opened: " + cellsOpened);
    }
    
	/**
	 * Counts adjacent fires
	 * @param row The row of the cell.
     * @param col The column of the cell.
	 */
    private int countAdjacentFires(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < GRID_SIZE && newCol >= 0 && newCol < GRID_SIZE) {
                    if (fires[newRow][newCol]) count++;
                }
            }
        }
        return count;
    }

    /**
	 * Reveals one adjacent fire when Clairvoyance is active
	 * @param row The row of the cell.
     * @param col The column of the cell.
	 */
    private void revealOneAdjacentFire(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < GRID_SIZE && newCol >= 0 && newCol < GRID_SIZE) {
                    if (fires[newRow][newCol]) {
                    	setFrozenFire(newRow, newCol);
                    	cells[newRow][newCol].setDisable(true);
                        return;
                    }
                }
            }
        }
    }

    /**
	 * Reveals adjacent cells recursively
	 * @param row The row of the cell.
     * @param col The column of the cell.
	 */
    private void revealAdjacentCells(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < GRID_SIZE && newCol >= 0 && newCol < GRID_SIZE) {
                    if (cells[newRow][newCol] != null && !revealed[newRow][newCol]) {
                    	cellClick(newRow, newCol);
                    }
                }
            }
        }
    }

    /**
	 * Checks if win condition is met
	 */
    private boolean checkWinCondition() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (cells[row][col] != null && !fires[row][col] && !revealed[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Handles Game Over situation
     * @param win Stores true if game is won false otherwise
     */
    private void gameOver(boolean win) {
    	// Remove all gems
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (gemButtons[row][col] != null) {
                    gemButtons[row][col].setVisible(false);
                }
            }
        }
        // Show all runes
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (cells[row][col] != null) {
                    cells[row][col].setDisable(true);
                    if (fires[row][col]) {
                        setFire(row,col);
                    }
                }
            }
        }
        
        System.out.println(win ? "You Win!" : "Game Over!");
        // Show the game over popup
        showGameOverPopup(win);
    }

    /**
     * Creates and shows game over pop-ups
     * @param win Stores true if game is won false otherwise
     */
    private void showGameOverPopup(boolean win) {
        // Create the popup stage
        Stage popupStage = new Stage();
        popupStage.setWidth(400);
        popupStage.setHeight(400);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);
        
        VBox popupVBox = new VBox(15);
        popupVBox.setAlignment(Pos.CENTER);
        popupVBox.setPadding(new Insets(20));
        
        Image winImage = new Image("img/win.png");
        Image overImage = new Image("img/over.png");
        ImageView imageView;
                
        // Create title text
        Text titleText = new Text(win ? "Victory!" : "Game Over!");
        titleText.getStyleClass().add("title");
        
        // Create content text
        Text contentText;
        if (win) {
            contentText = new Text("Congratulations! You've won in " + secondsElapsed + " seconds");
            imageView = new ImageView(winImage);
            imageView.setFitWidth(100); 
            imageView.setPreserveRatio(true);
        } else {
            contentText = new Text("Better Luck Next Time!");    
            imageView = new ImageView(overImage);
            imageView.setFitWidth(100); 
            imageView.setPreserveRatio(true);
        }
        contentText.getStyleClass().add("content");
        
        // Create buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button restartButton = new Button("Play Again!");
        restartButton.getStyleClass().add("button1");
        restartButton.setOnAction(e -> {
            popupStage.close();
            showGameScreen();
        });
        
        Button homeButton = new Button("Home");
        homeButton.getStyleClass().add("button1");
        homeButton.setOnAction(e -> {
            popupStage.close();
            showStartScreen();
        });
               
        buttonBox.getChildren().addAll(restartButton, homeButton);
        
        // Add all elements to the popup
        popupVBox.getChildren().addAll(imageView, titleText, contentText, buttonBox);
        
        // Create the scene and show the popup
        Scene popupScene = new Scene(popupVBox);
        popupScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        Image image = new Image("img/wand.png");
        popupScene.setCursor(new ImageCursor(image));

        popupStage.setScene(popupScene);
        setWandCursor();
        popupStage.show();
    }
    
    /**
     * Creates and shows Shop pop-up
     */
    private void showShopPopup() {
        // Create the popup stage
        Stage popupStage = new Stage();
        popupStage.setWidth(450);
        popupStage.setHeight(450);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);
        
        VBox popupVBox = new VBox(15);
        popupVBox.setAlignment(Pos.CENTER);
        popupVBox.setPadding(new Insets(20));
        
        // Create title text
        Text titleText = new Text("Enchanted Exchange!");
        titleText.getStyleClass().add("title");
        
        Label messageLabel = new Label("You can buy Spells and Potions!");
        
        Image potionImage = new Image("img/potion.gif");
        Image spellImage = new Image("img/wand.gif");        
        ImageView imageView = new ImageView("img/crystal.png");
        imageView.setFitWidth(100); 
        imageView.setPreserveRatio(true);

        Button hintButton = new Button("1 Clairvoyance Spell for 5 gems");
        hintButton.getStyleClass().add("button1");
        hintButton.setOnAction(e -> {
            if (gemCount < 5) {
            	messageLabel.setText("Sorry, you don't have enough Gems!");
            } else {
            	messageLabel.setText("You've bought a Clairvoyance Spell!");            	 
                imageView.setImage(spellImage);
                fadeInImage(imageView);
            	buySpell();
            }
            	
        });
        
        Button potionButton = new Button("1 Life Potion for 3 gems");
        potionButton.getStyleClass().add("button1");
        potionButton.setOnAction(e -> {
        	if (gemCount < 3) {
        		messageLabel.setText("Sorry, you don't have enough Gems!");
            } else {
            	messageLabel.setText("You've bought a Life Potion!");            	 
            	imageView.setImage(potionImage);
                fadeInImage(imageView);
            	buyPotion();
            }
        });
		// Add all elements to the popup
        popupVBox.getChildren().addAll(imageView, titleText, messageLabel, hintButton, potionButton);
        
        // Create the scene and show the popup
        Scene popupScene = new Scene(popupVBox);
        popupScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        Image image = new Image("img/wand.png");
        popupScene.setCursor(new ImageCursor(image));

        popupStage.setScene(popupScene);
        popupStage.show();
        setWandCursor();
    }
    
    /**
     * Handles actions when Life Potion is bought
     */
    private void buyPotion() {
    	ImageView animatedGem = new ImageView(new Image("img/potion.png"));
		animatedGem.setFitWidth(70);
        animatedGem.setFitHeight(70);
        int xPos = 415;
        animatedGem.setTranslateX(xPos);
        animatedGem.setTranslateY(100);
        //Adds the animated gem to the game layout
        gameLayout.getChildren().add(animatedGem);
        
        //Create movement animation
        TranslateTransition move = new TranslateTransition(Duration.millis(700), animatedGem);
        move.setToX(xPos);
        move.setToY(0);
        
        //Scale down animation
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(500), animatedGem);
        scaleDown.setToX(0.5);
        scaleDown.setToY(0.5);
        
        //Fade out animation
        FadeTransition fade = new FadeTransition(Duration.millis(500), animatedGem);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        
        //Combine all animations
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(move, scaleDown, fade);
        
        //After animation completes
        parallelTransition.setOnFinished(e -> {
			potionCount++;
			potionLabel.setText(potionLabelValue  + potionCount);
			gemCount = gemCount - 3;
			gemsLabel.setText(gemsLabelValue + gemCount);
        });
        
        //Start the animation sequence
        parallelTransition.play();
	}
      
    /**
     * Handles actions when Clairvoyance Spell (Hint) is bought
     */
    private void buySpell() {
    	ImageView animatedGem = new ImageView(new Image("img/spark.png"));
		animatedGem.setFitWidth(100);
        animatedGem.setFitHeight(100);
        int xPos = 550;
        animatedGem.setTranslateX(xPos);
        animatedGem.setTranslateY(100);
        //Adds the animated gem to the game layout
        gameLayout.getChildren().add(animatedGem);
        
        //Creates movement animation
        TranslateTransition move = new TranslateTransition(Duration.millis(700), animatedGem);
        move.setToX(xPos);
        move.setToY(0);
        
        //Scale down animation
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(500), animatedGem);
        scaleDown.setToX(0.5);
        scaleDown.setToY(0.5);
        
        //Fade out animation
        FadeTransition fade = new FadeTransition(Duration.millis(500), animatedGem);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        
        //Combine all animations
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(move, scaleDown, fade);
        
        //After animation completes
        parallelTransition.setOnFinished(e -> {                	
			hintsCount++;
        	hintsLabel.setText(hintsLabelValue + hintsCount);
        	gemCount = gemCount - 5;
        	gemsLabel.setText(gemsLabelValue + gemCount);
        });
        
        //Start the animation sequence
        parallelTransition.play();
	}
    
    /**
     * Sets a magic wand image as the cursor
     */
    private void setWandCursor() {
    	Scene currentScene = primaryStage.getScene();
        Image wandImage = new Image("img/wand.png");
        currentScene.setCursor(new ImageCursor(wandImage));
	}
    
    /**
     * Handles when there is no hints (Clairvoyance Spell) left but the hints button is clicked
     */
    private void showNoHintsPopup() {
        Stage popupStage = new Stage();
        popupStage.setWidth(350);
        popupStage.setHeight(350);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);
        
        VBox popupVBox = new VBox(15);
        popupVBox.setAlignment(Pos.CENTER);
        popupVBox.setPadding(new Insets(20));
        
        ImageView image = new ImageView("img/sorry.png");
        image.setFitWidth(100); 
        image.setPreserveRatio(true);
        
        Text titleText = new Text("Sorry!");
        titleText.getStyleClass().add("title-light");
        
        Text messageLabel = new Text("You don't have any Clairvoyance Spell!");
        messageLabel.getStyleClass().add("content");

        popupVBox.getChildren().addAll(image, titleText, messageLabel);

        Scene popupScene = new Scene(popupVBox);
        popupScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        Image wandImage = new Image("img/wand.png");
        popupScene.setCursor(new ImageCursor(wandImage));

        popupStage.setScene(popupScene);
        popupStage.show();
    }
    
    /**
     * Handles when a Life Potion is used when user clicks on Fire cell. 
     */
    private void showUsedPotionPopup() {
        Stage popupStage = new Stage();
        popupStage.setWidth(350);
        popupStage.setHeight(350);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);
        
        VBox popupVBox = new VBox(15);
        popupVBox.setAlignment(Pos.CENTER);
        popupVBox.setPadding(new Insets(20));
               
        ImageView image = new ImageView("img/potion.gif");
        image.setFitWidth(100); 
        image.setPreserveRatio(true);

        Text titleText = new Text("Life Potion used!");
        titleText.getStyleClass().add("title-light");
        
        Text messageLabel = new Text("You have " + potionCount +" Life Potions left!");
        messageLabel.getStyleClass().add("content");

        popupVBox.getChildren().addAll(image, titleText, messageLabel);

        Scene popupScene = new Scene(popupVBox);
        popupScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        Image wandImage = new Image("img/wand.png");
        popupScene.setCursor(new ImageCursor(wandImage));

        popupStage.setScene(popupScene);
        popupStage.show();
    }
    
	/**
	 * Timer Implementation 
	 * 1) Check whether a Timeline is created already and create a new Timeline only if it is not have been created 
	 * 2) Stop the Timeline before starting it again.
	 */
    private void startTimer() {
    	//Initializes the Timeline only if it's null
        if (timeline == null) {
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
                secondsElapsed++;
                timerLabel.setText("Time: " + secondsElapsed + "s");
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
        }
        
        //Stop any running timer before starting a new one
        timeline.stop();
        //Reset timer if needed
        secondsElapsed = 0; 
        timerLabel.setText("Time: " + secondsElapsed + "s");
        timeline.play();
    }
    
    /**
     * Stops the Timer
     */
    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * Launch the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}