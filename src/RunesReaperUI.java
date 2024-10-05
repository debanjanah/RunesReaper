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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RunesReaperUI extends Application {
	
	//Defining class level variables
	private Stage primaryStage;
	//Setting up the total size of the SQUARE grid (length of each side) 
    private int GRID_SIZE = 17; 
    private int CELL_SIZE = 40;
    //Setting up size of window
    private int WINDOW_WIDTH = 800; 
    private int WINDOW_HEIGHT = 900;
    //Setting up variables for Timer Implementation
    private Timeline timeline; 
    private int secondsElapsed = 0;
    private Label timerLabel;
    //Setting up variables for Cell interaction
    private Label cellsOpenedLabel; 
    private int cellsOpened = 0;
    
    private static final int NUM_RUNES = 30;
    private Button[][] cells;
    private boolean[][] runes;
    private boolean[][] revealed;
    
    private int gemCount = 0;
    private Label gemsLabel = new Label("Gems: "+ gemCount);
    private Random random = new Random();
    private Button[][] gemButtons = new Button[GRID_SIZE][GRID_SIZE]; // To store gem buttons separately from cells

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        //Calls the showStartScreen() function which we defined on line 40 to display the start screen
        showStartScreen(); 
    }

    //showStartScreen() initializes and displays the Start screen of the game
    private void showStartScreen() {
            	
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
        
        //Setting up a custom cursor
        Image image = new Image("img/wand.png");
        //Sets this custom cursor for the start scene
        startScene.setCursor(new ImageCursor(image));
        
        //Sets the created startScene as the current scene of the main window
        primaryStage.setScene(startScene);
        //Sets the title of window
        primaryStage.setTitle("RunesReaper");
        //Displays the window
        primaryStage.show();
    }
    
    //Creates and displays an "About" screen
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
        HowToPlayText.setText("🧙‍♂️ Welcome, dear Mage! 🧙‍♂️\n");
        HowToPlayText.getStyleClass().add("text2");
        
        Text howToPlayContent = new Text(
        		"Your mission is to navigate this magical terrain,\n"+
                "while collecting precious gems and avoiding dangerous fire traps!\n\n" +
                "The Runes and the Firefield:\n" +
                "The game board represents the Rune cells filled with a magical Firefield.\n" +
                "Beware of hidden fire traps scattered across the field.\n" +
                "Triggering a Fire trap will instantly ruin everything.\n" +
                "Click on Rune cells to reveal what's beneath.\n" +
                "Be cautious! Each click could unveil a gems or a dangerous fire!\n" +
                "Open all Rune cells to win!!.\n\n" +
                "Gems and Special Abilities:\n" +
                "💎 Collect gems to buy Special Abilities. More gems means more power!\n" +
                "🧪 Potions: Use it and no Fire can harm you!.\n" +
                "🔮 Clairvoyance: Freeze Fire cells around your current position.\n"
            );
        howToPlayContent.getStyleClass().add("text2");
        
        //Creates "HOME" button that goes back to the Start/Home screen
        Button homeButton = new Button("Home");
        //Sets event listener to show the Start/Home screen on action (click)
        homeButton.setOnAction(e -> showStartScreen());
        homeButton.getStyleClass().add("button1");

        aboutLayout.getChildren().addAll(logoView,HowToPlayText,howToPlayContent,homeButton);

        //Creates a new Scene with the aboutLayout of the same specified size and adds external CSS for styling
        Scene aboutScene = new Scene(aboutLayout, WINDOW_WIDTH, WINDOW_HEIGHT);
        aboutScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        //Sets the wand image as custom cursor
        Image image = new Image("img/wand.png");
        aboutScene.setCursor(new ImageCursor(image));
        ImageCursor.getBestSize(50,50);

        //Sets the current scene of the primary stage to about scene
        primaryStage.setScene(aboutScene);
        primaryStage.setTitle("About RunesReaper");
    }

    //Initializes and displays the Game screen
    private void showGameScreen() {
    	cells = new Button[GRID_SIZE][GRID_SIZE];
        runes = new boolean[GRID_SIZE][GRID_SIZE];
        revealed = new boolean[GRID_SIZE][GRID_SIZE];
        
    	//Resets Cells Opened Counter to ZERO
    	cellsOpened = 0;
    	gemCount = 0;
    	
    	//Creates a BorderPane called "gameLayout"
        BorderPane gameLayout = new BorderPane();

        //Adds gameLayout BorderPane to the scene
        Scene gameScene = new Scene(gameLayout, WINDOW_WIDTH, WINDOW_HEIGHT);        
        gameScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        //Sets up the wand image as custom cursor
        Image image = new Image("img/wand.png");
        gameScene.setCursor(new ImageCursor(image));

        //Creates the top bar of the game screen by calling createTopBar() function defined in Line 202
        HBox topBar = createTopBar();
        //Sets the top bar at the top of the BorderPane
        gameLayout.setTop(topBar);
        //--

        //Creates new GridPane layout to organize the tiles in a grid created by calling createGameGrid() function which is defined in Line 224
        GridPane gameGrid = createGameGrid();
        gameGrid.setPadding(new Insets(20));
        //Centers the tiles to the scene
        gameLayout.setCenter(gameGrid);
        //--

        //Creates the top bar of the game screen by calling createBottomBar() function defined in Line 237
        HBox bottomBar = createBottomBar();
        //Sets the bottom bar to the bottom of the gameLayout BorderPane
        gameLayout.setBottom(bottomBar);
        //--        

        //Sets the game scene as the current scene on the primary stage
        primaryStage.setScene(gameScene);
    	
    	initializeGame();

        //Starts the timer
        startTimer();
    }
    
    //Creates Top Bar
    private HBox createTopBar() {
    	
    	//Creates a new horizontal box layout with 20px spacing between children elements aligned in the center
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER);
        //Adds padding of 20 pixels on all sides of the HBox
        topBar.setPadding(new Insets(20));

        //Creates a Label to display the number of gems, initially set to ZERO
        //Label gemsLabel = new Label("Gems: "+ gemCount);
        gemsLabel.getStyleClass().add("info1");
        
        //Creates a button to display the number of potions, initially set to ZERO, functionality to "use potions" to be added later
        Button potionLabel = new Button("Potions: 0");
        potionLabel.getStyleClass().add("button1");
        
        //Creates a Button to display the number of clairvoyance (hints),functionality to "use clairvoyance" to be added later
        Button hintsLabel = new Button("Clairvoyance: 0");
        hintsLabel.getStyleClass().add("button1");
        
        //Creates a Label to display the elapsed time, starting from 0 seconds
        timerLabel = new Label("Time: 0s");
        timerLabel.getStyleClass().add("info1");

        //Adds all created labels and buttons to the HBox
        topBar.getChildren().addAll(gemsLabel, potionLabel, hintsLabel, timerLabel);
        
        //Returns the fully constructed HBox to be used as the top bar
        return topBar;
    }
    
    //Creates the Bottom bar
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
        
        //Adds Cells Opened Counter, "HOME" and "SHOP" buttons to the bottom bar
        bottomBar.getChildren().addAll(cellsOpenedLabel,homeButton,shopButton);
        
        //Returns the fully constructed HBox to be used as the bottom bar
        return bottomBar;
    }
    
    private GridPane createGameGrid() {
        GridPane gameGrid = new GridPane();
        gameGrid.setAlignment(Pos.CENTER);
        //Sets 2px width gap between columns
        gameGrid.setHgap(2);
        //Sets 2px width gap between rows
        gameGrid.setVgap(2);
        
		
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
                    
                    // Create gem button
                    Button gemButton = createGemButton(row, col);
                    gemButtons[row][col] = gemButton;
                    
                    // Create StackPane to hold both buttons
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

    //Creates cells
    private Button createCell(int row, int col) {
        Button cell = new Button();
        //Sets the size of the cell
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);

        cell.setMinSize(CELL_SIZE, CELL_SIZE);
        cell.setMaxSize(CELL_SIZE, CELL_SIZE);
        
        //Sets event listener to call cellClick() function with the clicked button (cell) as an argument defined on Line 300
        cell.setOnAction(e -> cellClick(row, col));
        //Adds CSS class "game-cell" for styling
        cell.getStyleClass().add("game-cell");
        //Returns created cell
        return cell;
    }
    
    private void initializeGame() {
        Random random = new Random();
        int runesPlaced = 0;
        int maxAttempts = GRID_SIZE*GRID_SIZE*2; 

        for (int i = 0; i < maxAttempts && runesPlaced < NUM_RUNES; i++) {
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);
            
            if (!runes[row][col] && cells[row][col] != null) {
                runes[row][col] = true;
                runesPlaced++;
            }
        }
        
        System.out.println("Runes placed: " + runesPlaced);
    }
   
 // Add this method to create gem buttons
    private Button createGemButton(int row, int col) {
    	Button gemButton = new Button();
        gemButton.setPrefSize(CELL_SIZE, CELL_SIZE);
        gemButton.setMinSize(CELL_SIZE, CELL_SIZE);
        gemButton.setMaxSize(CELL_SIZE, CELL_SIZE);
        gemButton.setVisible(false);
        gemButton.setStyle("-fx-background-color: transparent;");
        
        ImageView gemView = new ImageView(new Image("img/gem.png"));
        gemView.setFitWidth(20);
        gemView.setFitHeight(20);
        gemButton.setGraphic(gemView);
        
        gemButton.setOnAction(e -> {
            collectGem(row, col);
            e.consume();
        });
        
        return gemButton;
    }
    
 // Add this method to spawn gems in adjacent cells
    private void spawnGemsInAdjacentCells(int centerRow, int centerCol) {
    	// First, remove any existing gems
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (gemButtons[row][col] != null) {
                    gemButtons[row][col].setVisible(false);
                }
            }
        }
        
        // Determine number of gems (0-3)
        int numGems = random.nextInt(4);
        if (numGems == 0) return;
        
        // Get list of adjacent cells
        int[][] adjacentCells = new int[8][2];
        int validAdjacentCells = 0;
        
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; // Skip the center cell
                
                int newRow = centerRow + i;
                int newCol = centerCol + j;
                
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
        
        // Spawn gems
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

    // Add this method to handle gem collection
    private void collectGem(int row, int col) {
    	if (gemButtons[row][col] != null && gemButtons[row][col].isVisible()) {
            gemCount++;
            gemsLabel.setText("Gems: " + gemCount);
            gemButtons[row][col].setVisible(false);
        }
        // Optional: Add visual/sound effect for gem collection
    }

    //Function to be called when a cell is clicked
    private void cellClick(int row, int col) {
    	// Check if there's a visible gem on this cell
        if (gemButtons[row][col] != null && gemButtons[row][col].isVisible()) {
            return; // Don't process cell click if there's a gem
        }
    	
    	if (revealed[row][col]) return;
    	
    	revealed[row][col] = true;
    	//Increases the count of number of cells that has been clicked or "opened"
        cellsOpened++;
        updateCellsOpenedLabel();
        
        if (runes[row][col]) {
            cells[row][col].setText("R");
            cells[row][col].getStyleClass().add("rune-cell");  //TBD add CSS
            gameOver(false);
        } else {
            int adjacentRunes = countAdjacentRunes(row, col);
            if (adjacentRunes > 0) {
                cells[row][col].setText(String.valueOf(adjacentRunes));
                cells[row][col].getStyleClass().add("number-cell");  //TBD add CSS
            } else {
                cells[row][col].setText("");
                cells[row][col].getStyleClass().add("empty-cell");  //TBD add CSS
                revealAdjacentCells(row, col);
            }
         // Spawn gems in adjacent cells
            spawnGemsInAdjacentCells(row, col);
        }
   	
        //Disables the button so it can't be clicked again
        cells[row][col].setDisable(true);   
       
       if (checkWinCondition()) {
           gameOver(true);
       }
    }
    
    private void updateCellsOpenedLabel() {
    	//Updates the label that shows the current count of opened cells
        cellsOpenedLabel.setText("Cells Opened: " + cellsOpened);
    }
    
    private int countAdjacentRunes(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < GRID_SIZE && newCol >= 0 && newCol < GRID_SIZE) {
                    if (runes[newRow][newCol]) count++;
                }
            }
        }
        return count;
    }

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

    private boolean checkWinCondition() {//alternate win logic: win if runesplaced+cells opened=total number of cells
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (cells[row][col] != null && !runes[row][col] && !revealed[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

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
                    if (runes[row][col]) {
                        cells[row][col].setText("R");
                        cells[row][col].getStyleClass().add("rune-cell");
                    }
                }
            }
        }
        
        //TBD add dialog box?
        System.out.println(win ? "You Win!" : "Game Over!");
        // Show the game over popup
        showGameOverPopup(win);

        //Stops and refreshes the Timer
        stopTimer();
    }

 // Add this method to create and show game over popups
    private void showGameOverPopup(boolean win) {
        // Create the popup stage
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);
        
        VBox popupVBox = new VBox(15);
        popupVBox.setAlignment(Pos.CENTER);
        popupVBox.setPadding(new Insets(20));
        
        // Create title text
        Text titleText = new Text(win ? "Victory!" : "Game Over!");
        titleText.getStyleClass().add("popup-title");
        
        // Create content text
        Text contentText;
        if (win) {
            contentText = new Text("Time: " + secondsElapsed);
        } else {
            contentText = new Text("Better Luck Next Time!");
        }
        contentText.getStyleClass().add("popup-content");
        
        // Create buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button restartButton = new Button("Play Again!");
        restartButton.getStyleClass().add("button1");
        restartButton.setOnAction(e -> {
            popupStage.close();
            gemsLabel.setText("Gems: 0");
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
        popupVBox.getChildren().addAll(titleText, contentText, buttonBox);
        
        // Create the scene and show the popup
        Scene popupScene = new Scene(popupVBox);
        popupScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        Image image = new Image("img/wand.png");
        popupScene.setCursor(new ImageCursor(image));

        popupStage.setScene(popupScene);
        popupStage.show();
    }
    
	/*
	 * Timer Implementation 
	 * 1) Check whether a Timeline is created already and create a new Timeline only if it is not have been created 
	 * 2) Stop the Timeline before starting it again.
	 */
    
    //Creates the Timeline and checking for previously created ones
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
    
    //Method for stopping the Timer
    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    public static void main(String[] args) {
    	//Launch the application
        launch(args);
    }
}
