
package umlcreator.gui;

import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import properties_manager.PropertiesManager;
import saf.AppTemplate;
import saf.components.AppWorkspaceComponent;
import saf.ui.AppGUI;
import umlcreator.controller.EditController;
import umlcreator.data.DataManager;
import umlcreator.data.Draggable;
import umlcreator.data.DraggableClass;
import umlcreator.data.UMLCreatorState;
import umlcreator.file.FileManager;

/**
 *
 * @author Vincent Cramer
 */
public class Workspace extends AppWorkspaceComponent{

    AppTemplate app;
    
    AppGUI gui;
    
    double screenHeight, screenWidth, rowWidth, rowHeight, columnWidth, 
            columnHeight, cellWidth, cellHeight;
    
    RowConstraints rowConstraints;
    ColumnConstraints columnConstraints;
    
    TextField classTextField, packageTextField;
        
    ComboBox parentComboBox;
    
    GridPane designRenderer, componentToolbar;
    
    Scale scale;
    
    FileManager fileManager;
    DataManager dataManager;
    EditController editController;
    
    Label methodLabel, classNameLabel, packageLabel, varLabel, parentLabel;  
    
    HBox workspaceHBox, varHBox, methodHBox, classHBox, packageHBox, parentHBox;
    
    ScrollPane designRendererScrollPane, methodScrollPane, varScrollPane;

    ArrayList<StackPane> userMadePanes;
    
    boolean snap;
    
    Button exportPhotoButton, zoomInButton, zoomOutButton, exportCodeButton, 
            resizeButton, selectButton, addClassButton, addInterfaceButton, 
            undoButton, redoButton, removeButton, addMethodButton, 
            removeMethodButton, addVarButton, removeVarButton;
    
    ArrayList<Rectangle> rectangles;
    
    TableView variableTable, methodTable;
    
    CheckBox gridCheckBox, snapCheckBox;
    
    TableColumn firstVC, secondVC, thirdVC, fourthVC, fifthVC, 
            firstMC, secondMC, thirdMC, fourthMC, fifthMC;
    
    /**
     * Constructor for the user's entire workspace
     * 
     * @param initApp 
     * The app template we use to access the gui and other features
     */
    public Workspace(AppTemplate initApp){
        
        //making the workspace visible to the user
        app = initApp;
        gui = app.getGUI();
        Screen screen = Screen.getPrimary();
        
        //getting the user's screen dimensions, which allows us to make the size
        //of icons and sections proportional to whatever screen they're using
        Rectangle2D bounds = screen.getVisualBounds();
        screenHeight = bounds.getHeight();
        screenWidth = bounds.getWidth();
        
        userMadePanes = new ArrayList();
        
        //dialogue box in case there's an error
        PropertiesManager propsSingleton = PropertiesManager.
                getPropertiesManager();

        //setting up the sections of the user's work area
        //design renderer is where the UML diagram will be, and componentToolbar
        //is the top part with all of the buttons
        designRenderer = new GridPane();
        componentToolbar = new GridPane();
        
        //adjusting toolbars to screen's dimensions
        componentToolbar.setPrefWidth(screenWidth/3);
        componentToolbar.setPrefHeight(screenHeight*.9 - 20);

        designRenderer.setPrefWidth(screenWidth*(2.0/3.0));
        designRenderer.setPrefHeight(screenHeight*.9);
        designRenderer.setMinHeight(screenHeight*.9);
        designRenderer.setMinWidth(screenWidth*(2.0/3.0));
        
        //initializing our file and data managers to handle user functions and 
        //input
        fileManager = (FileManager) app.getFileComponent();
	dataManager = (DataManager) app.getDataComponent();
        
        //initalizing the scale - to be changed with zoom functions
        scale = new Scale(1,1);
        
        //do not snap to grid by default
        snap = false;
        
        
        //creating the part the user will actually see, as well as the 
        //editController
        AppGUI ag = initApp.getGUI();
        editController = new EditController(app);
        workspace = new Pane();
        
        
        
        //adding in all stlying and components for the GUI, as well as the 
        //handlers for the various buttons.
        setUpGUI();
        setUpHandlers(ag);
        initStyle();
        
        //Divides the working area into a grid, which the user will be 
        //able to snap to. The left 2/3 of the screen is for editing 
        //information, and the right 1/3 is for viewing information about the 
        //current class or interface. The top 1/10 is for displaying buttons.
        rowHeight = (screenHeight*.2);
        rowWidth = (screenWidth*(2.0/3.0));
        columnHeight = (screenHeight*.9);
        columnWidth = (rowWidth/5.0);
        
        rowConstraints = new RowConstraints(rowHeight);
        columnConstraints = new ColumnConstraints(columnWidth);
        
        //this divides the designRenderer into a 5x5 grid
        designRenderer.getRowConstraints().addAll(rowConstraints,rowConstraints,
                rowConstraints,rowConstraints,rowConstraints);
        designRenderer.getColumnConstraints().addAll(columnConstraints,
                columnConstraints,columnConstraints,columnConstraints,
                columnConstraints);
        
        //setting up dimensions for each cell in 5x5 grid
        cellWidth = columnWidth;
        cellHeight = rowHeight;
        
        //adding in the background color and rectangles to each cell in the grid
        rectangles = new ArrayList();
        for(int g=0;g<25;g++){
            Rectangle tempR = new Rectangle(cellWidth,cellHeight);
            tempR.getStyleClass().add("grid_cell_off");
            Color c = Color.web("#FFFFCC");
            tempR.setFill(c);
            rectangles.add(tempR);
        }
        
        //inserting rectangles into our pane
        int i,j;
        for(i=0;i<5;i++){
            for(j=0;j<5;j++){
                Rectangle rect = rectangles.get(j+(5*i));
                designRenderer.add(rect,i,j); 
            }
        }
        

        //used for aligning the elements in front of the user
        workspaceHBox = new HBox();
        workspaceHBox.getChildren().addAll(designRendererScrollPane, 
                componentToolbar);
        
        //lets user actually see everything
        workspace.getChildren().add(workspaceHBox);
    }
    
    @Override
    public void reloadWorkspace() {
        
    }
    
    /**
     * Method which provides the handlers for each button on the top part of the 
       screen
     *   
     * @param ag 
     * Used for accessing buttons and checboxes
     * 
     */
    private void setUpHandlers(AppGUI ag) {
  
        initializeButtonsAndCheckBoxes(ag);

        addTopRowActions();
        addRightSideActions();
        addWorkspaceActions();
        
        
    }
    
    /**
     * Adds listeners to user's work area, such as mouse presses and drags
     */
    private void addWorkspaceActions(){
        
        
        workspace.setOnMousePressed(e->{
            editController.processMousePressed(e.getX(),e.getY());
        });
        
        workspace.setOnMouseDragged(e->{
            editController.processMouseDragged(e.getX(),e.getY());
        });
        
        workspace.setOnMouseReleased(e->{
            editController.processMouseReleased(e.getX(),e.getY());
        });
    }
    
    /**
     * Adds listeners to the top row of buttons on screen
     */
    private void addTopRowActions(){
        
        addClassButton.setOnAction(e->{
            dataManager.addNewClass();
        });
        
        
        exportPhotoButton.setOnAction(e->{
            editController.processSnapshot();
        });
        
        zoomInButton.setOnAction(e->{
            if(scale.getX()<2){
                scale.setX(scale.getX()*1.1);
                scale.setY(scale.getY()*1.1);
                
                //TODO - change scale of everything in view!
            }
        });
        
        zoomOutButton.setOnAction(e->{
            if(scale.getX()>0.5){
                scale.setX(scale.getX()*(1/1.10));
                scale.setY(scale.getY()*(1/1.10));
                
                //TODO - change scale of everything in view!
            }
        });
        
        //TODO - find API to add undo support
        undoButton.setOnAction(e->{
            
        });
        
        //TODO - find API to add redo support
        redoButton.setOnAction(e->{
            
        });

        snapCheckBox.setOnAction((e) -> {
            boolean selected = snapCheckBox.isSelected();
            snap=selected;
        });
        
        gridCheckBox.setOnAction(e->{
            boolean selected = gridCheckBox.isSelected();
            renderGrid(selected);
        });
        
        
        selectButton.setOnAction(e->{
            editController.processSelectionTool();
        });
        
        removeButton.setOnAction(e->{
            //if we've selected a pane, remove that pane. Otherwise, do nothing.
            if(dataManager.isInState(UMLCreatorState.SELECTING_PANE)){
                userMadePanes.remove(dataManager.getSelectedPane());
                workspace.getChildren().remove(dataManager.getSelectedPane());
            }
        });
        
    }
    
    /**
     * Adds listeners to editing section of the screen on the right hand side
     */
    private void addRightSideActions(){
        
        //adds variable to the currently selected pane
        addVarButton.setOnAction(e->{
            if(dataManager.isInState(UMLCreatorState.SELECTING_PANE)){
                StackPane sp = dataManager.getSelectedPane();
                Var newVar = new Var();
                //cast to Draggable for now - could be DraggableClass or 
                //DraggableInterface
                Draggable selected= (Draggable)sp.getChildren().get(0);
                if(selected instanceof DraggableClass){
                    DraggableClass selectedClass = (DraggableClass)selected;
                    selectedClass.addVar(newVar);
                }
                else{
                    //TODO - update for DraggableInterface!
                }
                
                String varString = newVar.toString();
                Label newLabel = new Label(varString);
                newLabel.getStyleClass().add("uml_label");
                
                //gets holder box
                VBox holder = (VBox)sp.getChildren().get(1);
                VBox varBox = (VBox)holder.getChildren().get(1);
                varBox.getChildren().add(newLabel);
           }
        }); 
       
        //adds method to the currently selected pane
        addMethodButton.setOnAction(e->{
           if(dataManager.isInState(UMLCreatorState.SELECTING_PANE)){
                StackPane sp = dataManager.getSelectedPane();
                Method newMethod = new Method();
               
                //cast to Draggable for now - could be DraggableClass or 
                //DraggableInterface
                Draggable selected= (Draggable)sp.getChildren().get(0);
                if(selected instanceof DraggableClass){
                    DraggableClass selectedClass = (DraggableClass)selected;
                    selectedClass.addMethod(newMethod);
                }
                else{
                    //TODO - update for DraggableInterface!
                }
                
                String methodString = newMethod.toString();
                Label newLabel = new Label(methodString);
                newLabel.getStyleClass().add("uml_label");
                
                //gets holder box
                VBox holder = (VBox)sp.getChildren().get(1);
                VBox methodBox = (VBox)holder.getChildren().get(2);
                methodBox.getChildren().add(newLabel);
           }
        });
    }

    
    @Override
    public void initStyle() {
        
        //adds styling to various elements from our css file
        methodLabel.getStyleClass().add("table_label");
        classNameLabel.getStyleClass().add("label");
        packageLabel.getStyleClass().add("label");
        varLabel.getStyleClass().add("table_label");
        parentLabel.getStyleClass().add("label");
        
        designRenderer.getStyleClass().add("design_renderer");
        componentToolbar.getStyleClass().add("component_toolbar");
        designRendererScrollPane.getStyleClass().add("design_renderer");
    }

    /**
     * Method to set up the area the user will work in and the editing section
     */
    private void setUpGUI() {
        
        //call helper methods to set up their respective parts of the screen
        setUpEditingSection();
        setUpWorkArea();
    }
    
    
    
    /**
     * Helper method which sets up the right side of the screen, which contains 
     * the labels, buttons, and other things to edit classes, their methods, and
     * their variables.
     */
    private void setUpEditingSection(){
        
        //calling helper functions to create each individual part of the editing
        //section
        setUpMethodSection();
        setUpVarSection();
        setUpClassSection();
        setUpPackageSection();
        setUpParentSection();
        
        //below we work on properly spacing each element of the editing section
        
        //this was from my original work, and the magic numbers were to get a 
        //close approximation to the visual aid provided to us. These nubmers 
        //may change to more meaningful variables.
        RowConstraints r0 = new RowConstraints();
        r0.setPercentHeight(2);
        RowConstraints r1 = new RowConstraints();
        r1.setPercentHeight(10);
        RowConstraints r2 = new RowConstraints();
        r2.setPercentHeight(7);
        RowConstraints r3 = new RowConstraints();
        r3.setPercentHeight(7);
        RowConstraints r4 = new RowConstraints();
        r4.setPercentHeight(5);
        RowConstraints r5 = new RowConstraints();
        r5.setPercentHeight(20);
        RowConstraints r6 = new RowConstraints();
        r6.setPercentHeight(10);
        RowConstraints r7 = new RowConstraints();
        r7.setPercentHeight(20);
        
        //adding the sections from top to bottom of the screen in the designated
        //order
        componentToolbar.getRowConstraints().addAll(r0,r1,r2,r3,r4,r5,r0,r6,r7);
        componentToolbar.add(classHBox, 0, 1);
        componentToolbar.add(packageHBox, 0, 2);
        componentToolbar.add(parentHBox, 0, 3);
        componentToolbar.add(varHBox, 0, 4);
        componentToolbar.add(varScrollPane, 0,5);
        componentToolbar.add(methodHBox, 0, 7);
        componentToolbar.add(methodScrollPane, 0, 8);
        
        //insets were used to help with spacing, and applied to each HBox
        Insets spacing = new Insets(0,0,0,10);
        
        componentToolbar.setMargin(classHBox,spacing);
        componentToolbar.setMargin(packageHBox, spacing);
        componentToolbar.setMargin(parentHBox, spacing);
        componentToolbar.setMargin(varHBox, spacing);
        componentToolbar.setMargin(varScrollPane, spacing);
        componentToolbar.setMargin(methodHBox, spacing);
        componentToolbar.setMargin(methodScrollPane, spacing);
    }
    
    /**
     * Helper method for setting up the class editing section of the right side 
     * of the screen
     */
    private void setUpClassSection(){
        
        //setting up HBox to hold all elements
        classHBox = new HBox(35);
        
        //clearly labeling class section for user
        classNameLabel = new Label("Class Name:");
        
        //initalizing text fields for user input with default prompt text
        classTextField = new TextField();
        classTextField.setPromptText("Class");
        
        //adding all elements to HBox
        classHBox.getChildren().addAll(classNameLabel, classTextField);
        
        
        
    }
    
    /**
     * Helper method for setting up the package editing section of the right 
     * side of the screen
     */
    private void setUpPackageSection(){
        
        //setting up HBox to hold all elements
        packageHBox = new HBox(65);
        
        //labeling the package section
        packageLabel = new Label("Package:");
        
        //initalizing text fields for user input with default prompt text
        packageTextField = new TextField();
        packageTextField.setPromptText("Package");
        
        //adding in all elements
        packageHBox.getChildren().addAll(packageLabel, packageTextField);

    }
    
    /**
     * Helper method for setting up the parent editing section of the right side
     * of the screen
     */
    private void setUpParentSection(){
        
        //HBox to hold all elements
        parentHBox = new HBox(80);

        //label to label area for user to edit
        parentLabel = new Label("Parent:");
        
        //provides a drop down menu, allowing the user to show inheritance with 
        //any of the other classes or interfaces in their UML
        parentComboBox = new ComboBox();
        parentComboBox.setValue("Object");
        parentComboBox.getItems().add("Object");
        parentComboBox.setEditable(true);
        
        //adding all elements
        parentHBox.getChildren().addAll(parentLabel, parentComboBox);
    }
    
    /**
     * Helper method for setting up the part of the right side consisting of 
     * editing the methods
     */
    private void setUpMethodSection(){
        //this section sets up the button on the right part of the screen where 
        //the user edits their selected class or interface
        removeMethodButton = new Button("",new ImageView
            ("file:./images/removeFromClass.png"));
        removeMethodButton.setTooltip(new Tooltip("Remove a method"));
        removeMethodButton.setMinHeight(30);
        removeMethodButton.setMaxHeight(30);
        
        addMethodButton = new Button("",new ImageView
            ("file:./images/addToClass.png"));
        addMethodButton.setTooltip(new Tooltip("Add a method"));
        addMethodButton.setMinHeight(30);
        addMethodButton.setMaxHeight(30);
        
        //seeting up HBox to hold all elements of method section
        methodHBox = new HBox(10);
        
        //clearly labeling the section so user knows where to edit methods
        methodLabel = new Label("Methods:");

        //this sets up the ability to see attributes about each method 
        //in a class, as well as being able to edit that information
        methodTable = new TableView();
        
        //provides headers for each column in the method table
        firstMC = new TableColumn(" ↓ Name");
        secondMC = new TableColumn(" ↓ Return");
        thirdMC = new TableColumn(" ↓ Static");
        fourthMC = new TableColumn(" ↓ Abstract");
        fifthMC = new TableColumn(" ↓ Access");
        
        //sets a minimum width to prevent them from looking bad on smaller 
        //screens
        firstMC.setMinWidth(100);
        secondMC.setMinWidth(100);
        thirdMC.setMinWidth(100);
        fourthMC.setMinWidth(100);
        fifthMC.setMinWidth(100);
        
        //this will be used in editing methods
        firstMC.setCellValueFactory(new PropertyValueFactory<>("methodName"));
        secondMC.setCellValueFactory(new PropertyValueFactory<>("returnType"));
        thirdMC.setCellValueFactory(new PropertyValueFactory<>("isStatic"));
        fourthMC.setCellValueFactory(new PropertyValueFactory<>("isAbstract"));
        
        //note that access == visibility in method class
        //access was given as a name due to it being shorter, but visibility is 
        //clearer in code
        fifthMC.setCellValueFactory(new PropertyValueFactory<>("access"));
        
        //adds all of the columns into the method table
        methodTable.getColumns().addAll(firstMC, secondMC, thirdMC, fourthMC,
                fifthMC);
        
        //first step in making both sections scrollable
        methodScrollPane = new ScrollPane(methodTable);
        
        //based on initial assignment, this keeps the size of the method section
        //in line with expectations
        methodScrollPane.setPrefHeight(screenHeight/5);
        
        //this lets us display a scrollbar only if we need to, for example, if 
        //there's too many method arguments to fit on screen at once
        
        methodScrollPane.setHbarPolicy(AS_NEEDED);
        methodScrollPane.setVbarPolicy(AS_NEEDED);
        
        //this places all of the components into separate boxes for each section
        //of the right side
        methodHBox.getChildren().addAll(methodLabel, addMethodButton, 
                removeMethodButton);
        
        
    }
    
    /**
     * Helper method for setting up the part of the right side consisting of 
     * editing the variables
     */
    private void setUpVarSection(){
        
        //setting up the buttons for user to add/remove variables
        addVarButton = new Button("",new ImageView
        ("file:./images/addToClass.png"));
        addVarButton.setTooltip(new Tooltip("Add a variable"));
        addVarButton.setMinHeight(30);
        addVarButton.setMaxHeight(30);
        
        removeVarButton = new Button("",new ImageView
            ("file:./images/removeFromClass.png"));
        removeVarButton.setTooltip(new Tooltip("Remove a variable"));
        removeVarButton.setMinHeight(30);
        removeVarButton.setMaxHeight(30);
        
        //setting up area where variable info will be held
        varHBox = new HBox(10);
        
        //label to show user which area they can edit variables
        varLabel = new Label("Variables:");
        
        //this sets up the ability to see attributes about each variable 
        //in a class, as well as being able to edit that information
        variableTable = new TableView();
        
        //provides the headers for each column in th variable table
        firstVC = new TableColumn<>(" ↓ Name");
        secondVC = new TableColumn<>(" ↓ Type");
        thirdVC = new TableColumn<>(" ↓ Static");
        //note that access == visibility in the variable class, but access was 
        //given as a field name because it's shorter
        fourthVC = new TableColumn<>(" ↓ Access");
        fifthVC = new TableColumn<>(" ↓ Final");
        
        //need a default width, or else the columns look too narrow on smaller 
        //screens
        firstVC.setMinWidth(100);
        secondVC.setMinWidth(100);
        thirdVC.setMinWidth(100);
        fourthVC.setMinWidth(100);
        fifthVC.setMinWidth(100);
        
        //used in editing Var information later
        firstVC.setCellValueFactory(new PropertyValueFactory<>("name"));
        secondVC.setCellValueFactory(new PropertyValueFactory<>("type"));
        thirdVC.setCellValueFactory(new PropertyValueFactory<>("isStatic"));
        fourthVC.setCellValueFactory(new PropertyValueFactory<>("visibility"));
        fifthVC.setCellValueFactory(new PropertyValueFactory<>("isFinal"));
        
        //need to ensure that the user can change their information
        variableTable.setEditable(true);
        firstVC.setEditable(true);
        secondVC.setEditable(true);
        thirdVC.setEditable(true);
        fourthVC.setEditable(true);
        fifthVC.setEditable(true);
        
        //adds all of the columns into the table
        variableTable.getColumns().addAll(firstVC, secondVC, thirdVC, fourthVC,
                fifthVC);
        
        //beginning of scroll functionality.
        varScrollPane = new ScrollPane(variableTable);
        
        //based on initial assignment, we want the varible section to be about 
        //a fifth of the height of the screen
        varScrollPane.setPrefHeight(screenHeight/5);
        
        //this lets us display a scrollbar only if we need to, for example, if 
        //there's too many method arguments to fit on screen at once
        varScrollPane.setHbarPolicy(AS_NEEDED);
        varScrollPane.setVbarPolicy(AS_NEEDED);
        
        //adding elements to HBox
        varHBox.getChildren().addAll(varLabel, addVarButton, removeVarButton);
    }
    
    /**
     * Helper method for setting up the left part of the screen, which is where 
     * the user's UML diagram will go.
     */
    private void setUpWorkArea(){
        
        //initializing the designRendererScrollPane, which holds our 
        //designRenderer. May add scrolling functionality later.
        designRendererScrollPane = new ScrollPane();
        designRendererScrollPane.setContent(designRenderer);
        designRendererScrollPane.setFitToHeight(true);
        designRendererScrollPane.setFitToWidth(true);
        designRendererScrollPane.setHbarPolicy(AS_NEEDED);
        designRendererScrollPane.setVbarPolicy(AS_NEEDED);
    }

    /**
     * Helper method for initializing buttons and checboxes along the top of the
     * screen
     * 
     * @param ag 
     * Used for accessing buttons and checboxes
     */
    private void initializeButtonsAndCheckBoxes(AppGUI ag) {
        
        //list to store buttons
        ArrayList buttons = ag.getButtons();
        
        //initializing each button field
        zoomInButton = (Button)buttons.get(0);
        zoomOutButton = (Button)buttons.get(1);
        exportPhotoButton = (Button)buttons.get(2);
        exportCodeButton = (Button)buttons.get(3);
        resizeButton = (Button)buttons.get(4);
        selectButton = (Button)buttons.get(5);
        addClassButton = (Button)buttons.get(6);
        addInterfaceButton = (Button)buttons.get(7);
        undoButton = (Button)buttons.get(8);
        redoButton = (Button)buttons.get(9);
        removeButton = (Button)buttons.get(10);
        
        //same thing, but now with checkboxes
        ArrayList<CheckBox> checkBoxes = ag.getCheckBoxes();
        gridCheckBox = checkBoxes.get(0);
        snapCheckBox = checkBoxes.get(1); 
    }

    public TextField getClassTextField(){
        return classTextField;
    }
    
    public void setClassTextField(String s){
        classTextField.setText(s);
    }
    
    public void setPackageTextField(String s){
        packageTextField.setText(s);
    }
    
    public void setParentComboBox(String s){
        parentComboBox.setValue(s);
    }
    
    /**
     * Adds a newly created pane to the user's view
     * 
     * @param newPane 
     * Pane that will be added
     */
    public void addPaneToWorkspace(StackPane newPane){
        
        //change "zoom" of new pane if user is zoomed in or out
        newPane.setScaleX(scale.getX());
        newPane.setScaleY(scale.getY());
        
        //make pane visible to user
        workspace.getChildren().add(newPane);
        
        //store panes in list to make for easy removal later
        userMadePanes.add(newPane);
        
        //put focus on newest pane
        dataManager.setSelectedPane(newPane);
                
        //magic numbers - from the positioning given in previous parts, such as 
        //DraggableClass constructor
        newPane.setLayoutX(100);
        newPane.setLayoutY(300);
    }
    
    /**
     * Updates the method and variables parts of the program to display those 
     * values for the currently selected node, and allow user to update them 
     * and see their changes in real time.
     * 
     * @param sp 
     * The StackPane that contains the UML class or interface
     */
    public void updateComponentToolbar(StackPane sp){
        
        VBox holder = (VBox)sp.getChildren().get(1);
        VBox variableBox = (VBox)holder.getChildren().get(1);
        VBox methodBox = (VBox)holder.getChildren().get(2);
        
        variableTable.setEditable(true);
        methodTable.setEditable(true);
        
        firstVC.setEditable(true);
        secondVC.setEditable(true);
        thirdVC.setEditable(true);
        fourthVC.setEditable(true);
        fifthVC.setEditable(true);
        
        firstMC.setEditable(true);
        secondMC.setEditable(true);
        thirdMC.setEditable(true);
        fourthMC.setEditable(true);
        fifthMC.setEditable(true);
        
        //these are easy compared to the others
        fifthVC.setCellFactory(CheckBoxTableCell.forTableColumn(fifthVC));
        thirdVC.setCellFactory(CheckBoxTableCell.forTableColumn(thirdVC));
        
        //TODO - need to finish this method!
    }
    
    /**
     * Accessor for the panes that were created by the user
     * 
     * @return 
     * An ArrayList of StackPanes which were created by the user
     */
    public ArrayList<StackPane> getUserMadePanes(){
        return userMadePanes;
    }
    
    /**
     * Function that moves the position of a pane inside the workspace
     * 
     * @param sp
     * Pane that will be moved
     * 
     * @param x
     * x coordinate of new position of pane
     * 
     * @param y 
     * y coordinate of new position of pane
     */
    public void move(StackPane sp, double x, double y){
        
        if(!snap){
            sp.setLayoutX(x);
            sp.setLayoutY(y);
        }
        else{
            //even when snap is toggled, don't want to constantly snap - want 
            //some freedom to move. This checks to see if the pane is within a 
            //certain distance of a gridline, and if so, it will snap to it, 
            //otherwise it will not snap.
            if(x%cellWidth<=(cellWidth/5.0)){ 
               snapX(sp,x);
            }
            else{
                sp.setLayoutX(x);    
            }
            
            //same story with the y position
            if(y%cellHeight<=(cellHeight/5.0)){
                snapY(sp,y);
            }
            else{
                sp.setLayoutY(y);
            }
            
        }
    }


    /**
     * Snaps the given pane to the closest fifth of the screen in terms of width
     * (on x-axis)
     * 
     * @param s
     * The pane that will be snapped
     * 
     * @param x 
     * Current position of the pane
     */
    private void snapX(StackPane s, double x){
        //5 x-positions to snap to: 0, cellWidth, (2*cellWidth), (3*cellWidth), 
        //(4*cellWidth)
        
        if(x>=4*cellWidth){
            s.setLayoutX(4*cellWidth);
        }
        else if(x>=3*cellWidth){
            s.setLayoutX(3*cellWidth);
        }
        else if(x>=2*cellWidth){
            s.setLayoutX(2*cellWidth);
        }
        else if(x>=cellWidth){
            s.setLayoutX(cellWidth);
        }
        else{
            s.setLayoutX(0);
        }
    }
    
    /**
     * Snaps the given pane to the closest fifth of the screen in terms of 
     * height (on y-axis)
     * 
     * @param s
     * The pane that will be snapped
     * 
     * @param y 
     * Current position of the pane
     */
    private void snapY(StackPane s, double y){
        //5 y-positions to snap to: 0, cellHeight, (2*cellHeight), 
        //(3*cellHeight), (4*cellHeight)
        
        if(y>=4*cellHeight){
            s.setLayoutY(4*cellHeight);
        }
        else if(y>=3*cellHeight){
            s.setLayoutY(3*cellHeight);
        }
        else if(y>=2*cellHeight){
            s.setLayoutY(2*cellHeight);
        }
        else if(y>=cellHeight){
            s.setLayoutY(cellHeight);
        }
        else{
           s.setLayoutY(0); 
        }
    }
    
    /**
     * Toggles the display of the grid to the user
     * 
     * @param render 
     * Boolean which determines if the grid will be displayed (true) or not 
     * (false)
     */
    private void renderGrid(boolean render){
       //secret is that rectanges are always there - just add a styling to their
       //border when necessary
        
        
        if(render){
            for(int r=0;r<rectangles.size();r++){
                //below should work with eventual zoom implementation
                rectangles.get(r).setStroke(Color.BLACK);
                rectangles.get(r).setStrokeWidth(2*scale.getX());
            }
        }
        
        else{
            for(int r=0;r<rectangles.size();r++){
                rectangles.get(r).setStroke(Color.web("#FFFFCC"));
                rectangles.get(r).setStrokeWidth(0);
            }
        }
    }
}
