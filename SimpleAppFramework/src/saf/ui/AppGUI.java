package saf.ui;


import java.util.ArrayList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import saf.controller.AppFileController;
import saf.AppTemplate;
import static saf.settings.AppPropertyType.*;
import static saf.settings.AppStartupConstants.FILE_PROTOCOL;
import static saf.settings.AppStartupConstants.PATH_IMAGES;
import saf.components.AppStyleArbiter;

/**
 * This class provides the basic user interface for this application,
 * including all the file controls, but not including the workspace,
 * which would be customly provided for each app.
 * 
 * @author Richard McKenna
 * @author Vincent Cramer
 * @version 1.1
 */
public class AppGUI implements AppStyleArbiter {
    // THIS HANDLES INTERACTIONS WITH FILE-RELATED CONTROLS
    protected AppFileController fileController;

    // THIS IS THE APPLICATION WINDOW
    protected Stage primaryStage;

    // THIS IS THE STAGE'S SCENE GRAPH
    protected Scene primaryScene;

    // THIS PANE ORGANIZES THE BIG PICTURE CONTAINERS FOR THE
    // APPLICATION AppGUI
    protected BorderPane appPane;
    
    // THIS IS THE TOP TOOLBAR AND ITS CONTROLS
    protected HBox fileToolbarPane;
    protected Button newButton;
    protected Button loadButton;
    protected Button saveButton;
    protected Button exitButton;
    
    // HERE ARE OUR DIALOGS
    protected AppYesNoCancelDialogSingleton yesNoCancelDialog;
    
    protected String appTitle;
    
    
    //Update Information
    //-------------------------------------------------------
    //Name: Vincent Cramer
    //Description:  Added the below VBoxes, HBoxes, and 
    //              buttons before the constructor. They were 
    //              originally in Workspace, but were moved 
    //              here. Note that numButtons refers to the 
    //              11 buttons that don't relate to files 
    //              (like loading/saving) and that don't 
    //              relate to the program itself (like new 
    //              creation/exit). Also added Rectangle2D
    //              bounds, which allows program to scale to 
    //              user's screen.
    
    VBox exportToolbar;
    VBox checkBoxes;
    
    HBox editToolbar;
    HBox viewToolbar;
    HBox topToolbar;
    
    CheckBox snapCheckBox;
    CheckBox gridCheckBox;
    
    Button saveAsButton;
    
    Button zoomInButton;
    Button zoomOutButton;
    Button exportPhotoButton;
    Button exportCodeButton;
    Button resizeButton;
    Button selectButton;
    Button addClassButton;
    Button addInterfaceButton;
    Button undoButton;
    Button redoButton;
    Button removeButton;
    
    public Rectangle2D bounds;
    
    
    int numButtons = 11;
    
    /**
     * This constructor initializes the file toolbar for use.
     * 
     * @param initPrimaryStage The window for this application.
     * 
     * @param initAppTitle The title of this application, which
     * will appear in the window bar.
     * 
     * @param app The app within this gui is used.
     */
    public AppGUI(  Stage initPrimaryStage, 
		    String initAppTitle, 
		    AppTemplate app){
	// SAVE THESE FOR LATER
	primaryStage = initPrimaryStage;
	appTitle = initAppTitle;
	       
        Screen screen = Screen.getPrimary();
        bounds = screen.getVisualBounds();
        
        // INIT THE TOOLBAR
        initFileToolbar(app);
		
        
        initEditToolbar(app);
        initViewToolbar(app);
        
        
        
        // AND FINALLY START UP THE WINDOW (WITHOUT THE WORKSPACE)
        initWindow();
        
    }
    
    /**
     * Accessor method for getting the application pane, within which all
     * user interface controls are ultimately placed.
     * 
     * @return This application GUI's app pane.
     */
    public BorderPane getAppPane() { return appPane; }
    
    /**
     * Accessor method for getting this application's primary stage's,
     * scene.
     * 
     * @return This application's window's scene.
     */
    public Scene getPrimaryScene() { return primaryScene; }
    
    /**
     * Accessor method for getting this application's window,
     * which is the primary stage within which the full GUI will be placed.
     * 
     * @return This application's primary stage (i.e. window).
     */    
    public Stage getWindow() { return primaryStage; }

    /**
     * This method is used to activate/deactivate toolbar buttons when
     * they can and cannot be used so as to provide foolproof design.
     * 
     * @param saved Describes whether the loaded Page has been saved or not.
     */
    public void updateToolbarControls(boolean saved) {
        // THIS TOGGLES WITH WHETHER THE CURRENT COURSE
        // HAS BEEN SAVED OR NOT
        saveButton.setDisable(saved);

        // ALL THE OTHER BUTTONS ARE ALWAYS ENABLED
        // ONCE EDITING THAT FIRST COURSE BEGINS
	newButton.setDisable(false);
        loadButton.setDisable(false);
	exitButton.setDisable(false);

        // NOTE THAT THE NEW, LOAD, AND EXIT BUTTONS
        // ARE NEVER DISABLED SO WE NEVER HAVE TO TOUCH THEM
    }

    /****************************************************************************/
    /* BELOW ARE ALL THE PRIVATE HELPER METHODS WE USE FOR INITIALIZING OUR AppGUI */
    /****************************************************************************/
    
    /**
     * This function initializes all the buttons in the toolbar at the top of
     * the application window. These are related to file management.
     */
    private void initFileToolbar(AppTemplate app) {
        fileToolbarPane = new HBox(5);

        fileToolbarPane.setPrefHeight(bounds.getHeight()/10);
        fileToolbarPane.setPrefWidth(bounds.getWidth()/3);
        
        
        // HERE ARE OUR FILE TOOLBAR BUTTONS, NOTE THAT SOME WILL
        // START AS ENABLED (false), WHILE OTHERS DISABLED (true)
        newButton = initChildButton(fileToolbarPane,	NEW_ICON.toString(),	    NEW_TOOLTIP.toString(),	false,false);
        
        loadButton = initChildButton(fileToolbarPane,	LOAD_ICON.toString(),	    LOAD_TOOLTIP.toString(),	false,false);
        
        saveButton = initChildButton(fileToolbarPane,	SAVE_ICON.toString(),	    SAVE_TOOLTIP.toString(),	true,false);
        

        //Update Information
        //------------------------------------------
        //Name: Vincent Cramer
        //Description:  Added the saveAsButton, both
        //              export buttons, and the 
        //              export toolbar
        saveAsButton = initChildButton(fileToolbarPane, SAVE_AS_ICON.toString(), SAVE_AS_TOOLTIP.toString(), false,false);
        
        exportCodeButton = initChildButton(fileToolbarPane, EXPORT_CODE_ICON.toString(), EXPORT_CODE_TOOLTIP.toString(), false,true);
       
        exportPhotoButton = initChildButton(fileToolbarPane, EXPORT_PHOTO_ICON.toString(), EXPORT_PHOTO_TOOLTIP.toString(), false,true);

        exportToolbar = new VBox();
        exportToolbar.getChildren().addAll(exportCodeButton,exportPhotoButton);
        fileToolbarPane.getChildren().add(exportToolbar);
        
        
        exitButton = initChildButton(fileToolbarPane,	EXIT_ICON.toString(),	    EXIT_TOOLTIP.toString(),	false, false);
        
	// AND NOW SETUP THEIR EVENT HANDLERS
        fileController = new AppFileController(app);
        newButton.setOnAction(e -> {
            fileController.handleNewRequest();
        });
        loadButton.setOnAction(e -> {
            fileController.handleLoadRequest();
        });
        saveButton.setOnAction(e -> {
            fileController.handleSaveRequest();
        });
        exitButton.setOnAction(e -> {
            fileController.handleExitRequest();
        });
        
        //Update Information
        //------------------------------------------
        //Name: Vincent Cramer
        //Description:  Added the button with "Save 
        //              As" functionality here, to 
        //              keep it with similar button
        //              functions.
        saveAsButton.setOnAction(e -> {
            fileController.handleSaveAsRequest();
        });
        
        
        
        
    }

    // INITIALIZE THE WINDOW (i.e. STAGE) PUTTING ALL THE CONTROLS
    // THERE EXCEPT THE WORKSPACE, WHICH WILL BE ADDED THE FIRST
    // TIME A NEW Page IS CREATED OR LOADED
    private void initWindow() {
        // SET THE WINDOW TITLE
        primaryStage.setTitle(appTitle);

        // GET THE SIZE OF THE SCREEN
        //Screen screen = Screen.getPrimary();
        //Rectangle2D bounds = screen.getVisualBounds();

        // AND USE IT TO SIZE THE WINDOW
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());

        // ADD THE TOOLBAR ONLY, NOTE THAT THE WORKSPACE
        // HAS BEEN CONSTRUCTED, BUT WON'T BE ADDED UNTIL
        // THE USER STARTS EDITING A COURSE
        appPane = new BorderPane();
        
        topToolbar = new HBox(15);//number in () =  pixel spacing
        topToolbar.setPrefWidth(bounds.getWidth());
        topToolbar.setPrefHeight(bounds.getHeight()/10);
        
        topToolbar.getChildren().addAll(fileToolbarPane,editToolbar,viewToolbar);
        
        topToolbar.getStyleClass().add("hBox");
        
        //Update Information
        //------------------------------------------
        //Name: Vincent Cramer
        //Description:  Changed the top of the 
        //              appPane to topToolbar to 
        //              support the addition of new 
        //              buttons such as save as.
        appPane.setTop(topToolbar);
        
        primaryScene = new Scene(appPane);
        
        // SET THE APP ICON
	PropertiesManager props = PropertiesManager.getPropertiesManager();
        String appIcon = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(APP_LOGO);
        primaryStage.getIcons().add(new Image(appIcon));

        // NOW TIE THE SCENE TO THE WINDOW AND OPEN THE WINDOW
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }
    
    /**
     * This is a public helper method for initializing a simple button with
     * an icon and tooltip and placing it into a toolbar.
     * 
     * @param toolbar Toolbar pane into which to place this button.
     * 
     * @param icon Icon image file name for the button.
     * 
     * @param tooltip Tooltip to appear when the user mouses over the button.
     * 
     * @param disabled true if the button is to start off disabled, false otherwise.
     * 
     * @return A constructed, fully initialized button placed into its appropriate
     * pane container.
     */
    public Button initChildButton(Pane toolbar, String icon, String tooltip, boolean disabled, boolean isExport) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
	
	// LOAD THE ICON FROM THE PROVIDED FILE
        String imagePath = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(icon);
        Image buttonImage = new Image(imagePath);
	
	// NOW MAKE THE BUTTON
        Button button = new Button();
        button.setDisable(disabled);
        button.setGraphic(new ImageView(buttonImage));
        Tooltip buttonTooltip = new Tooltip(props.getProperty(tooltip));
        button.setTooltip(buttonTooltip);
	
        if(isExport){
            
            button.setPrefHeight(bounds.getHeight()/20);
            button.setPrefWidth(bounds.getWidth()/16);
        }
        else{
        button.setPrefHeight(bounds.getHeight()/10);
        button.setPrefWidth(bounds.getWidth()/16);
                }
        
	// PUT THE BUTTON IN THE TOOLBAR
        toolbar.getChildren().add(button);
	
	// AND RETURN THE COMPLETED BUTTON
        return button;
    }
    
    /**
     * This function specifies the CSS style classes for the controls managed
     * by this framework.
     */
    @Override
    public void initStyle() {
	fileToolbarPane.getStyleClass().add(CLASS_BORDERED_PANE);
	newButton.getStyleClass().add(CLASS_FILE_BUTTON);
	loadButton.getStyleClass().add(CLASS_FILE_BUTTON);
	saveButton.getStyleClass().add(CLASS_FILE_BUTTON);
	exitButton.getStyleClass().add(CLASS_FILE_BUTTON);
        //TODO: add other buttons here!
    }
    
    /**
     * This function creates the editing section of the top toolbar.
     * 
     * @param app 
     * The AppTemplate we adjust to add the editing section
     * 
     * Written by: Vincent Cramer
     */
    public void initEditToolbar(AppTemplate app){
        editToolbar = new HBox(10);
        editToolbar.setPrefHeight(bounds.getHeight()/10);
        editToolbar.setPrefWidth(bounds.getWidth()/2);
        
        
        selectButton = initChildButton(editToolbar, SELECT_ICON.toString(), SELECT_TOOLTIP.toString(), false, false);
        
        resizeButton = initChildButton(editToolbar, RESIZE_ICON.toString(), RESIZE_TOOLTIP.toString(), false, false);
        addInterfaceButton = initChildButton(editToolbar, ADD_INTERFACE_ICON.toString(), ADD_INTERFACE_TOOLTIP.toString(), false, false);
        addClassButton = initChildButton(editToolbar, ADD_CLASS_ICON.toString(), ADD_CLASS_TOOLTIP.toString(), false, false);
        removeButton = initChildButton(editToolbar, REMOVE_ICON.toString(), REMOVE_TOOLTIP.toString(), false, false);
        
        undoButton = initChildButton(editToolbar, UNDO_ICON.toString(), UNDO_TOOLTIP.toString(), true, false);
        
        redoButton = initChildButton(editToolbar, REDO_ICON.toString(), REDO_TOOLTIP.toString(), true, false);
        
        
    }
    
    /**
     * This function initializes the view section of the top toolbar.
     * 
     * @param app
     * The AppTemplate we adjust to add the view section
     * 
     * Written by: Vincent Cramer
     */
    public void initViewToolbar(AppTemplate app){
        viewToolbar = new HBox(10);
        viewToolbar.setPrefHeight(bounds.getHeight()/10);
        viewToolbar.setPrefWidth(bounds.getWidth()/5);
        
        checkBoxes = new VBox(bounds.getHeight()/20);
        
        
        zoomInButton = initChildButton(viewToolbar, ZOOM_IN_ICON.toString(), ZOOM_IN_TOOLTIP.toString(), false, false);
        
        zoomOutButton = initChildButton(viewToolbar, ZOOM_OUT_ICON.toString(), ZOOM_OUT_TOOLTIP.toString(), false, false);
        
        
        gridCheckBox = new CheckBox("Grid");
        snapCheckBox = new CheckBox("Snap");
        checkBoxes.getChildren().addAll(gridCheckBox,snapCheckBox);
        viewToolbar.getChildren().add(checkBoxes);
        
        
        
    }
    
    /**
     * Provides the miscellaneous buttons for the top toolbar.
     * @return 
     * ArrayList of general purpose buttons.
     * 
     * Written by: Vincent Cramer
     */
    public ArrayList<Button> getButtons(){
        ArrayList<Button> buttons = new ArrayList(numButtons);
        buttons.add(zoomInButton);
        buttons.add(zoomOutButton);
        buttons.add(exportPhotoButton);
        buttons.add(exportCodeButton);
        buttons.add(resizeButton);
        buttons.add(selectButton);
        buttons.add(addClassButton);
        buttons.add(addInterfaceButton);
        buttons.add(undoButton);
        buttons.add(redoButton);
        buttons.add(removeButton);
        return buttons;
    }
    
    /**
     * Provides the checkBoxes to allow the grid and snapping functionality in 
     * the top toolbar.
     * 
     * @return 
     * An ArrayList of the checkBoxes.
     * 
     * Written by: Vincent Cramer
     */
    public ArrayList<CheckBox> getCheckBoxes(){
        ArrayList<CheckBox> a = new ArrayList();
        a.add(gridCheckBox);
        a.add(snapCheckBox);
        return a;
    }
}
