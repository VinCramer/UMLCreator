
package umlcreator.gui;

import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.util.Callback;
import javax.swing.event.HyperlinkEvent;
import properties_manager.PropertiesManager;
import saf.AppTemplate;
import saf.components.AppWorkspaceComponent;
import saf.ui.AppGUI;
import umlcreator.controller.EditController;
import umlcreator.data.DataManager;
import umlcreator.data.Draggable;
import umlcreator.data.DraggableClass;
import umlcreator.data.DraggableInterface;
import umlcreator.data.UMLCreatorState;
import static umlcreator.data.UMLCreatorState.SELECTING_PANE;
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
        
    ComboBox parentComboBox, interfaceComboBox;
    
    GridPane designRenderer, componentToolbar;
    
    Scale scale;
    
    FileManager fileManager;
    DataManager dataManager;
    EditController editController;
    
    Label methodLabel, classNameLabel, packageLabel, varLabel, parentLabel, 
        interfaceLabel;  
    
    HBox workspaceHBox, varHBox, methodHBox, classHBox, packageHBox, parentHBox,
        interfaceHBox;
    
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
    
    TableColumn <Var, String> firstVC, secondVC, fourthVC;
    TableColumn <Var, Boolean> thirdVC, fifthVC;
    TableColumn <Method, String> firstMC, secondMC, fifthMC;
    TableColumn <Method, Boolean> thirdMC, fourthMC;
    
    
    ArrayList<String> primitives;
    
    
    /**
     * Constructor for the user's entire workspace
     * 
     * @param initApp 
     * The app template we use to access the gui and other features
     */
    public Workspace(AppTemplate initApp){
        
        //define the types of data which are primitives, used later
        primitives = new ArrayList();
        primitives.add("char");
        primitives.add("int");
        primitives.add("double");
        primitives.add("float");
        primitives.add("boolean");
        primitives.add("short");
        primitives.add("long");
        primitives.add("byte");
        
        
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
            updateComponentToolbar(dataManager.getSelectedPane());
        });
        
        addInterfaceButton.setOnAction(e-> {
            dataManager.addNewInterface();
            updateComponentToolbar(dataManager.getSelectedPane());
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
                StackPane tempPane = dataManager.getSelectedPane();
                Draggable d = (Draggable)tempPane.getChildren().get(0);
                
                //remove the pane from user's view and list of all 
                //class/interfaces
                userMadePanes.remove(tempPane);
                workspace.getChildren().remove(dataManager.getSelectedPane());
                
                DraggableClass dc = null;
                DraggableInterface di = null;
                
                if(d instanceof DraggableClass){
                    dc = (DraggableClass)d;
                }
                else{
                    di = (DraggableInterface)d;
                }
                
                //TODO - need to remove parent-child lines if either parent or 
                //child is removed
                
                //need to remove api lines and api pane if hosting 
                //class/interface is removed
                if(dc != null){
                    if(dc.hasAPIPane()){
                        workspace.getChildren().remove(dc.getAPIPane());
                        workspace.getChildren().remove(dc.getAPILine());
                    }
                }
                else{
                    if(di.hasAPIPane()){
                        workspace.getChildren().remove(di.getAPILine());
                        workspace.getChildren().remove(di.getAPIPane());
                    }
                }
                
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
                
                String varString = newVar.toString();
                Label newLabel = new Label(varString);
                newLabel.getStyleClass().add("uml_label");
                
                
                Draggable selected= (Draggable)sp.getChildren().get(0);
                if(selected instanceof DraggableClass){
                    DraggableClass selectedClass = (DraggableClass)selected;
                    selectedClass.addVar(newVar);
                    selectedClass.addVarLabel(newLabel);
                }
                else{
                    DraggableInterface selectedInterface = (DraggableInterface)
                        selected;
                    selectedInterface.addVar(newVar);
                    selectedInterface.addVarLabel(newLabel);
                }
                
                
                
                //gets holder box
                VBox holder = (VBox)sp.getChildren().get(1);
                VBox varBox = (VBox)holder.getChildren().get(1);
                varBox.getChildren().add(newLabel);
                
                updateComponentToolbar(dataManager.getSelectedPane());
           }
        }); 
       
        //adds method to the currently selected pane
        addMethodButton.setOnAction(e->{
           if(dataManager.isInState(UMLCreatorState.SELECTING_PANE)){
                
                StackPane sp = dataManager.getSelectedPane();
                Method newMethod = new Method();
               
                String methodString = newMethod.toString();
                Label newLabel = new Label(methodString);
                newLabel.getStyleClass().add("uml_label");
                
                Draggable selected= (Draggable)sp.getChildren().get(0);
                if(selected instanceof DraggableClass){
                    DraggableClass selectedClass = (DraggableClass)selected;
                    selectedClass.addMethod(newMethod);
                    selectedClass.addMethodLabel(newLabel);
                }
                else{
                    DraggableInterface selectedInterface = (DraggableInterface)
                        selected;
                    selectedInterface.addMethod(newMethod);
                    selectedInterface.addMethodLabel(newLabel);
                }
                
                
                
                //gets holder box
                VBox holder = (VBox)sp.getChildren().get(1);
                VBox methodBox = (VBox)holder.getChildren().get(2);
                methodBox.getChildren().add(newLabel);
                
                updateComponentToolbar(dataManager.getSelectedPane());
           }
        });
        
        removeVarButton.setOnAction(e->{
            if(dataManager.isInState(SELECTING_PANE)){
                StackPane sp = dataManager.getSelectedPane();
                Draggable selected= (Draggable)sp.getChildren().get(0);
                
                //no variables to remove
                if(variableTable.getSelectionModel().getSelectedItem()==null){
                    return;
                }
                
                //gets us the row/variable user wants to remove
                Var toBeRemoved = (Var)variableTable.getSelectionModel().
                        getSelectedItem();
                
                
                if(selected instanceof DraggableClass){
                    DraggableClass selectedClass = (DraggableClass)selected;
                    
                    int index = selectedClass.getVariableList().indexOf
                        (toBeRemoved);
                    
                    if(toBeRemoved != null){
                        selectedClass.removeVar(toBeRemoved);
                        selectedClass.removeVarLabel(index);
                        selectedClass.getVarBox().getChildren().remove(index);
                        
                    }
                }
                
                else{
                    DraggableInterface selectedInterface = (DraggableInterface)
                        selected;
                    int index = selectedInterface.getVariableList().indexOf
                        (toBeRemoved);
                    
                    if(toBeRemoved != null){
                        selectedInterface.removeVar(toBeRemoved);
                        selectedInterface.removeVarLabel(index);
                        selectedInterface.getVarBox().getChildren().
                            remove(index);
                        
                    }
                }
                
                
                updateComponentToolbar(dataManager.getSelectedPane());
            }
        });
        
        removeMethodButton.setOnAction(e->{
            if(dataManager.isInState(SELECTING_PANE)){
                StackPane sp = dataManager.getSelectedPane();
                Draggable selected= (Draggable)sp.getChildren().get(0);
                
                //no methods to remove
                if(methodTable.getSelectionModel().getSelectedItem()==null){
                    return;
                }
                
                //gets us the row/method user wants to remove
                Method toBeRemoved = (Method)methodTable.getSelectionModel().
                        getSelectedItem();
                
                
                if(selected instanceof DraggableClass){
                    DraggableClass selectedClass = (DraggableClass)selected;
                    
                    int index = selectedClass.getMethodList().indexOf
                        (toBeRemoved);
                    
                    if(toBeRemoved != null){
                        selectedClass.removeMethod(toBeRemoved);
                        selectedClass.removeMethodLabel(index);
                        selectedClass.getMethodBox().getChildren().remove(index);
                        
                    }
                }
                else{
                    DraggableInterface selectedInterface = (DraggableInterface)
                        selected;
                    
                    int index = selectedInterface.getMethodList().indexOf
                        (toBeRemoved);
                    
                    if(toBeRemoved != null){
                        selectedInterface.removeMethod(toBeRemoved);
                        selectedInterface.removeMethodLabel(index);
                        selectedInterface.getMethodBox().getChildren().
                                remove(index);
                        
                    }
                }
                
                updateComponentToolbar(dataManager.getSelectedPane());
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
        setUpParentAndInterfaceSection();
        
        //below we work on properly spacing each element of the editing section
        
        
        RowConstraints topBuffer = new RowConstraints();
        topBuffer.setPercentHeight(2);
        
        RowConstraints normalRC = new RowConstraints();
        normalRC.setPercentHeight(7);
        RowConstraints tableRC = new RowConstraints();
        tableRC.setPercentHeight(20);
        
        
        //adding the sections from top to bottom of the screen in the designated
        //order
        componentToolbar.getRowConstraints().addAll(topBuffer,normalRC,normalRC,
                normalRC,normalRC,normalRC,tableRC,normalRC,tableRC);
        componentToolbar.add(classHBox, 0, 1);
        componentToolbar.add(packageHBox, 0, 2);
        componentToolbar.add(parentHBox, 0, 3);
        componentToolbar.add(interfaceHBox, 0, 4);
        componentToolbar.add(varHBox, 0, 5);
        componentToolbar.add(varScrollPane, 0,6);
        componentToolbar.add(methodHBox, 0, 7);
        componentToolbar.add(methodScrollPane, 0, 8);
        
        //insets were used to help with spacing, and applied to each HBox
        Insets spacing = new Insets(5,0,0,10);
        
        componentToolbar.setMargin(classHBox,spacing);
        componentToolbar.setMargin(packageHBox, spacing);
        componentToolbar.setMargin(parentHBox, spacing);
        componentToolbar.setMargin(interfaceHBox, spacing);
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
        classHBox = new HBox(87);
        
        //clearly labeling class section for user
        classNameLabel = new Label("Name:");
        
        //initalizing text fields for user input with default prompt text
        classTextField = new TextField();
        classTextField.setPromptText("Name");
        
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
     * Helper method for setting up the parent and interface editing section of 
     * the right side of the screen
     */
    private void setUpParentAndInterfaceSection(){
        
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
        
        interfaceHBox = new HBox(30);
        interfaceLabel = new Label("Implements:");
        
        interfaceComboBox = new ComboBox();
        interfaceComboBox.setValue("");
        interfaceComboBox.setEditable(true);
        
        interfaceHBox.getChildren().addAll(interfaceLabel,interfaceComboBox);
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
        VBox classBox = (VBox)holder.getChildren().get(0);
        
        
        ObservableList tempVarList = variableBox.getChildren();
        ObservableList tempMethodList = methodBox.getChildren();
        
        Draggable temp = (Draggable)sp.getChildren().get(0);
        
        DraggableClass draggableClass = null;
        DraggableInterface draggableInterface = null;
        
        if(temp instanceof DraggableClass){
            draggableClass = (DraggableClass)temp;
        }
        else{
            draggableInterface = (DraggableInterface)temp;
        }
        
        //if not null, that means this is a class pane
        if(draggableClass!=null){
            
            //fxcollections lets us convert from ArrayList to ObservableList
            variableTable.setItems(FXCollections.observableArrayList
                (draggableClass.getVariableList()));
        
            methodTable.setItems(FXCollections.observableArrayList
                (draggableClass.getMethodList()));
            
        }
        
        else{
            variableTable.setItems(FXCollections.observableArrayList
                (draggableInterface.getVariableList()));
        
            methodTable.setItems(FXCollections.observableArrayList
                (draggableInterface.getMethodList()));
        }
        
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
        
        //defining what type of cell the first variable column will be
        firstVC.setCellFactory(TextFieldTableCell.<Var>forTableColumn());
        
        //adding behavior to first variable column, which controls the 
        //variable's name
        firstVC.setOnEditCommit((CellEditEvent<Var, String> t)->{
            
            //these are used to update the correct variable and label in their 
            //respective lists
            int currentVarPosition = t.getTablePosition().getRow();
            Var tempVar;
            Label tempLabel;
            
            DraggableClass tempDraggableClass = null;
            DraggableInterface tempDraggableInterface = null;
        
            if(temp instanceof DraggableClass){
                tempDraggableClass = (DraggableClass)temp;
            }
            else{
                tempDraggableInterface = (DraggableInterface)temp;
            }
            
            if(tempDraggableClass != null){
                //update var and replace label w/ new toString
                tempVar = tempDraggableClass.getVar(currentVarPosition);
                tempVar.setName(t.getNewValue());
                tempLabel = tempDraggableClass.getVarLabel(currentVarPosition);
                tempLabel.setText(tempVar.toString());
                tempDraggableClass.setVariable(currentVarPosition,tempVar);
                tempDraggableClass.setVariableLabel(currentVarPosition,
                        tempLabel);
                
                variableBox.getChildren().set(currentVarPosition, tempLabel);
            }
            else{
                //update var and replace label w/ new toString
                tempVar = tempDraggableInterface.getVar(currentVarPosition);
                tempVar.setName(t.getNewValue());
                tempLabel = tempDraggableInterface.getVarLabel
                    (currentVarPosition);
                tempLabel.setText(tempVar.toString());
                tempDraggableInterface.setVariable(currentVarPosition,tempVar);
                tempDraggableInterface.setVariableLabel(currentVarPosition,
                        tempLabel);
                
                variableBox.getChildren().set(currentVarPosition, tempLabel);
            }  
        });
        
        secondVC.setCellFactory(TextFieldTableCell.<Var>forTableColumn());
        
        
        //handles the type of the variable
        secondVC.setOnEditCommit((CellEditEvent<Var, String> t)->{
            
            
            int currentVarPosition = t.getTablePosition().getRow();
            Var tempVar;
            Label tempLabel;
            
            DraggableClass tempDraggableClass = null;
            DraggableInterface tempDraggableInterface = null;
        
            if(temp instanceof DraggableClass){
                tempDraggableClass = (DraggableClass)temp;
            }
            else{
                tempDraggableInterface = (DraggableInterface)temp;
            }
            
            if(tempDraggableClass!=null){
                tempVar = tempDraggableClass.getVar(currentVarPosition);
                tempVar.setType(t.getNewValue());
                tempLabel = tempDraggableClass.getVarLabel(currentVarPosition);
                tempLabel.setText(tempVar.toString());
                
                tempDraggableClass.setVariable(currentVarPosition,tempVar);
                tempDraggableClass.setVariableLabel(currentVarPosition,
                    tempLabel);
                variableBox.getChildren().set(currentVarPosition, tempLabel);
                
                //if the type isn't primitive, we need to add an API and pane 
                //if necessary
                if(!primitives.contains(t.getNewValue())){
                    if(!tempDraggableClass.hasAPIPane()){
                        addApiPane(sp, tempDraggableClass, t.getNewValue());
                    }
                    else{
                        
                        //if the class already has an APIPane, we should check 
                        //and see if this data type is already included in that 
                        //pane. If so, we won't add anything to the pane.
                        StackPane apiPane = tempDraggableClass.getAPIPane();
                        VBox tempVBox = (VBox)apiPane.getChildren().get(1);
                        boolean found = false;
                        for(Object o: tempVBox.getChildren()){
                            Label l = (Label)o;
                            String currentAPI = l.getText();
                            if(currentAPI.equals(t.getNewValue())){
                                found=true;
                                break;
                            }
                        }
                        
                        //if the new variable type isn't primitive and isn't one
                        //of the listed APIs for this class
                        if(!found){
                            addAPI(tempDraggableClass, t.getNewValue());
                        }
                        
                    }
                }
                
            }
            
            else{
                tempVar = tempDraggableInterface.getVar(currentVarPosition);
                tempVar.setType(t.getNewValue());
                tempLabel = tempDraggableInterface.getVarLabel
                    (currentVarPosition);
                tempLabel.setText(tempVar.toString());
                
                tempDraggableInterface.setVariable(currentVarPosition,tempVar);
                tempDraggableInterface.setVariableLabel(currentVarPosition,
                    tempLabel);
                variableBox.getChildren().set(currentVarPosition, tempLabel);
                
                if(!primitives.contains(t.getNewValue())){
                    if(!tempDraggableInterface.hasAPIPane()){
                        addAPIPane(sp, tempDraggableInterface, t.getNewValue());
                    }
                    
                    else{
                        
                        //if the class already has an APIPane, we should check 
                        //and see if this data type is already included in that 
                        //pane. If so, we won't add anything to the pane.
                        StackPane apiPane = tempDraggableInterface.getAPIPane();
                        VBox tempVBox = (VBox)apiPane.getChildren().get(1);
                        boolean found = false;
                        for(Object o: tempVBox.getChildren()){
                            Label l = (Label)o;
                            String currentAPI = l.getText();
                            if(currentAPI.equals(t.getNewValue())){
                                found=true;
                                break;
                            }
                        }
                        
                        //if the new variable type isn't primitive and isn't one
                        //of the listed APIs for this class
                        if(!found){
                            addAPI(tempDraggableInterface, t.getNewValue());
                        }
                        
                    }
                    
                }
                
            }
        });
        
        //forces column to be checkbox, which is most intuitve for user
        thirdVC.setCellFactory(column -> new CheckBoxTableCell<>());
                
        thirdVC.setCellValueFactory(t -> {
            
            Var cellValue = t.getValue();
            BooleanProperty property = cellValue.getIsStaticBooleanProperty();
            
            //this updates our corresponding Var, but we still need to update 
            //the GUI to show the user their changes.
            property.addListener((observable, oldValue, newValue) -> 
                    cellValue.setIsStatic(newValue));
     
            DraggableClass tempDraggableClass = null;
            DraggableInterface tempDraggableInterface = null;
            if(temp instanceof DraggableClass){
                tempDraggableClass = (DraggableClass)temp;
            }
            else{
                tempDraggableInterface = (DraggableInterface)temp;
            }
            
            if(tempDraggableClass!=null){
                int i=0;
                Label l;
                
                //what we do here is re-do each and every variable label in this
                //node's variableBox, since we don't have access to the 
                //getTablePosition.getRow() like we do in other columns. We 
                //could attempt to find only the older version of the variable 
                //as a String, but that's an O(n) operation too and we'd need 
                //another method in Var to find the older toString result, since
                //the Var has already been changed. For simplicity's sake this 
                //works, even if it isn't completely optimized.
                for(i=0;i<variableBox.getChildren().size();i++){
                    l = new Label(tempDraggableClass.getVar(i).toString());
                    l.getStyleClass().add("uml_label");
                    tempDraggableClass.setVariableLabel(i,l);
                    variableBox.getChildren().set(i, l);
                }
            }
            
            else{
                int i=0;
                Label l;
                
                
                for(i=0;i<variableBox.getChildren().size();i++){
                    l = new Label(tempDraggableInterface.getVar(i).toString());
                    l.getStyleClass().add("uml_label");
                    tempDraggableInterface.setVariableLabel(i,l);
                    variableBox.getChildren().set(i, l);
                }
            }
            return property;
        });
                   
        fourthVC.setCellFactory(TextFieldTableCell.<Var>forTableColumn());
        
        //handles the variable's access (visibility, i.e. public/private/etc.)
        fourthVC.setOnEditCommit((CellEditEvent<Var, String > t) -> {
            int currentVarPosition = t.getTablePosition().getRow();
            Var tempVar;
            Label tempLabel;
            
            DraggableClass tempDraggableClass = null;
            DraggableInterface tempDraggableInterface = null;
        
            if(temp instanceof DraggableClass){
                tempDraggableClass = (DraggableClass)temp;
            }
            else{
                tempDraggableInterface = (DraggableInterface)temp;
            }
            
            if(tempDraggableClass!=null){
                tempVar = tempDraggableClass.getVar(currentVarPosition);
                tempVar.setVisibility(t.getNewValue());
                tempLabel = tempDraggableClass.getVarLabel(currentVarPosition);
                tempLabel.setText(tempVar.toString());
                
                tempDraggableClass.setVariable(currentVarPosition,tempVar);
                tempDraggableClass.setVariableLabel(currentVarPosition,
                        tempLabel);
                variableBox.getChildren().set(currentVarPosition, tempLabel);
            }
            
            else{
                tempVar = tempDraggableInterface.getVar(currentVarPosition);
                tempVar.setVisibility(t.getNewValue());
                tempLabel = tempDraggableInterface.getVarLabel
                    (currentVarPosition);
                tempLabel.setText(tempVar.toString());
                
                tempDraggableInterface.setVariable(currentVarPosition,tempVar);
                tempDraggableInterface.setVariableLabel(currentVarPosition,
                        tempLabel);
                variableBox.getChildren().set(currentVarPosition, tempLabel);
            }
            
        });
        
        //set the column to be full of CheckBoxes
        fifthVC.setCellFactory(column -> new CheckBoxTableCell<>());

        fifthVC.setCellValueFactory(t -> {
            
            Var cellValue = t.getValue();
            BooleanProperty property = cellValue.getIsFinalBooleanProperty();
            
            //updates Var; still need to update GUI
            property.addListener((observable, oldValue, newValue) -> 
                    cellValue.setIsFinal(newValue));
      
            DraggableClass tempDraggableClass = null;
            DraggableInterface tempDraggableInterface = null;
            if(temp instanceof DraggableClass){
                tempDraggableClass = (DraggableClass)temp;
            }
            else{
                tempDraggableInterface = (DraggableInterface)temp;
            }
            
            if(tempDraggableClass!=null){
                int i=0;
                Label l;
                
                //see thirdVC for more details on why we use this
                for(i=0;i<variableBox.getChildren().size();i++){
                    l = new Label(tempDraggableClass.getVar(i).toString());
                    l.getStyleClass().add("uml_label");
                    tempDraggableClass.setVariableLabel(i,l);
                    variableBox.getChildren().set(i, l);
                }
            }
            
            else{
                int i=0;
                Label l;
                
                for(i=0;i<variableBox.getChildren().size();i++){
                    l = new Label(tempDraggableInterface.getVar(i).toString());
                    l.getStyleClass().add("uml_label");
                    tempDraggableInterface.setVariableLabel(i,l);
                    variableBox.getChildren().set(i, l);
                }
            }
            return property;
        });
        
  
        //defining multiple types of cells at once
        firstMC.setCellFactory(TextFieldTableCell.<Method>forTableColumn());
        secondMC.setCellFactory(TextFieldTableCell.<Method>forTableColumn());
        thirdMC.setCellFactory(CheckBoxTableCell.<Method>forTableColumn
            (thirdMC));
        fourthMC.setCellFactory(CheckBoxTableCell.<Method>forTableColumn
            (fourthMC));
        fifthMC.setCellFactory(TextFieldTableCell.<Method>forTableColumn());
        
        //method name
        firstMC.setOnEditCommit((CellEditEvent<Method, String> t) -> {
            int currentMethodPosition = t.getTablePosition().getRow();
            Method tempMethod;
            Label tempLabel;
            
            DraggableClass tempDraggableClass = null;
            DraggableInterface tempDraggableInterface = null;
        
            if(temp instanceof DraggableClass){
                tempDraggableClass = (DraggableClass)temp;
            }
            else{
                tempDraggableInterface = (DraggableInterface)temp;
            }
            
            if(tempDraggableClass != null){
                //update method and replace label w/ new toString
                tempMethod = tempDraggableClass.
                        getMethod(currentMethodPosition);
                tempMethod.setName(t.getNewValue());
                
                tempLabel = tempDraggableClass.getMethodLabel
                    (currentMethodPosition);
                tempLabel.setText(tempMethod.toString());
                tempDraggableClass.setMethod(currentMethodPosition,tempMethod);
                tempDraggableClass.setMethodLabel(currentMethodPosition,
                        tempLabel);
                
                methodBox.getChildren().set(currentMethodPosition, tempLabel);
            }
            
            else{
                tempMethod = tempDraggableInterface.
                        getMethod(currentMethodPosition);
                tempMethod.setName(t.getNewValue());
                
                tempLabel = tempDraggableInterface.getMethodLabel
                    (currentMethodPosition);
                tempLabel.setText(tempMethod.toString());
                tempDraggableInterface.setMethod(currentMethodPosition,tempMethod);
                tempDraggableInterface.setMethodLabel(currentMethodPosition,
                        tempLabel);
                
                methodBox.getChildren().set(currentMethodPosition, tempLabel);
            }
        });
        
        //method return type
        secondMC.setOnEditCommit((CellEditEvent<Method, String> t) -> {
            int currentMethodPosition = t.getTablePosition().getRow();
            Method tempMethod;
            Label tempLabel;
            
            DraggableClass tempDraggableClass = null;
            DraggableInterface tempDraggableInterface = null;
        
            if(temp instanceof DraggableClass){
                tempDraggableClass = (DraggableClass)temp;
            }
            else{
                tempDraggableInterface = (DraggableInterface)temp;
            }
            
            if(tempDraggableClass != null){
                //update method and replace label w/ new toString
                tempMethod = tempDraggableClass.
                        getMethod(currentMethodPosition);
                tempMethod.setReturnType(t.getNewValue());
                
                tempLabel = tempDraggableClass.getMethodLabel
                    (currentMethodPosition);
                tempLabel.setText(tempMethod.toString());
                tempDraggableClass.setMethod(currentMethodPosition,tempMethod);
                tempDraggableClass.setMethodLabel(currentMethodPosition,
                        tempLabel);
                
                methodBox.getChildren().set(currentMethodPosition, tempLabel);
                
                
                //if the return type isn't primitive, we need to add an API and 
                //pane if necessary
                if(!primitives.contains(t.getNewValue())){
                    if(!tempDraggableClass.hasAPIPane()){
                        addApiPane(sp, tempDraggableClass, t.getNewValue());
                    }
                    else{
                        
                        //if the class already has an APIPane, we should check 
                        //and see if this data type is already included in that 
                        //pane. If so, we won't add anything to the pane.
                        StackPane apiPane = tempDraggableClass.getAPIPane();
                        VBox tempVBox = (VBox)apiPane.getChildren().get(1);
                        boolean found = false;
                        for(Object o: tempVBox.getChildren()){
                            Label l = (Label)o;
                            String currentAPI = l.getText();
                            if(currentAPI.equals(t.getNewValue())){
                                found=true;
                                break;
                            }
                        }
                        
                        //if the new return type isn't primitive and isn't one
                        //of the listed APIs for this class
                        if(!found){
                            addAPI(tempDraggableClass, t.getNewValue());
                        }
                        
                    }
                }
                
            }
            
            else{
                //update method and replace label w/ new toString
                tempMethod = tempDraggableInterface.
                        getMethod(currentMethodPosition);
                tempMethod.setReturnType(t.getNewValue());
                
                tempLabel = tempDraggableInterface.getMethodLabel
                    (currentMethodPosition);
                tempLabel.setText(tempMethod.toString());
                tempDraggableInterface.setMethod(currentMethodPosition,
                    tempMethod);
                tempDraggableInterface.setMethodLabel(currentMethodPosition,
                    tempLabel);
                
                methodBox.getChildren().set(currentMethodPosition, tempLabel);
                
                if(!primitives.contains(t.getNewValue())){
                    if(!tempDraggableInterface.hasAPIPane()){
                        addAPIPane(sp, tempDraggableInterface, t.getNewValue());
                    }
                    
                    else{
                        
                        //if the class already has an APIPane, we should check 
                        //and see if this data type is already included in that 
                        //pane. If so, we won't add anything to the pane.
                        StackPane apiPane = tempDraggableInterface.getAPIPane();
                        VBox tempVBox = (VBox)apiPane.getChildren().get(1);
                        boolean found = false;
                        for(Object o: tempVBox.getChildren()){
                            Label l = (Label)o;
                            String currentAPI = l.getText();
                            if(currentAPI.equals(t.getNewValue())){
                                found=true;
                                break;
                            }
                        }
                        
                        //if the new return type isn't primitive and isn't one
                        //of the listed APIs for this interface
                        if(!found){
                            addAPI(tempDraggableInterface, t.getNewValue());
                        }
                        
                    }
                }
            }
           
        });
        
        //static - checkbox
        thirdMC.setCellFactory(column -> new CheckBoxTableCell<>());
        
        thirdMC.setCellValueFactory(t -> {
            
            Method cellValue = t.getValue();
            BooleanProperty property = cellValue.getIsStaticBooleanProperty();
            
            //updates Method, GUI later
            property.addListener((observable, oldValue, newValue) -> 
                    cellValue.setIsStatic(newValue));

            DraggableClass tempDraggableClass = null;
            DraggableInterface tempDraggableInterface = null;
            if(temp instanceof DraggableClass){
                tempDraggableClass = (DraggableClass)temp;
            }
            else{
                tempDraggableInterface = (DraggableInterface)temp;
            }
            
            if(tempDraggableClass!=null){
                int i=0;
                Label l;
                
                //same as thirdVC & fifthVC, but with methodBox
                for(i=0;i<methodBox.getChildren().size();i++){
                    l = new Label(tempDraggableClass.getMethod(i).toString());
                    l.getStyleClass().add("uml_label");
                    tempDraggableClass.setMethodLabel(i,l);
                    methodBox.getChildren().set(i, l);
                }
            }
            
            else{
                int i=0;
                Label l;
                
                for(i=0;i<methodBox.getChildren().size();i++){
                    l = new Label(tempDraggableInterface.getMethod(i).toString());
                    l.getStyleClass().add("uml_label");
                    tempDraggableInterface.setMethodLabel(i,l);
                    methodBox.getChildren().set(i, l);
                }
            }
            
            return property;
        });
        
        //abstract - checkbox
        fourthMC.setCellFactory(column -> new CheckBoxTableCell<>());
        
        fourthMC.setCellValueFactory(t -> {
            
            Method cellValue = t.getValue();
            BooleanProperty property = cellValue.getIsAbstractBooleanProperty();
            
            //updates Method, GUI later
            property.addListener((observable, oldValue, newValue) -> 
                    cellValue.setIsStatic(newValue));
            
            DraggableClass tempDraggableClass = null;
            DraggableInterface tempDraggableInterface = null;
            if(temp instanceof DraggableClass){
                tempDraggableClass = (DraggableClass)temp;
            }
            else{
                tempDraggableInterface = (DraggableInterface)temp;
            }
            
            if(tempDraggableClass!=null){
                int i=0;
                Label l;
                
                //same as thirdVC & fifthVC, but with methodBox
                for(i=0;i<methodBox.getChildren().size();i++){
                    l = new Label(tempDraggableClass.getMethod(i).toString());
                    l.getStyleClass().add("uml_label");
                    tempDraggableClass.setMethodLabel(i,l);
                    methodBox.getChildren().set(i, l);
                }
            }
            
            else{
                int i=0;
                Label l;
                
                for(i=0;i<methodBox.getChildren().size();i++){
                    l = new Label(tempDraggableInterface.getMethod(i).toString());
                    l.getStyleClass().add("uml_label");
                    tempDraggableInterface.setMethodLabel(i,l);
                    methodBox.getChildren().set(i, l);
                }
            }
            return property;
        });
        
        //accessibility/visibility
        fifthMC.setOnEditCommit((CellEditEvent<Method, String> t) -> {
            int currentMethodPosition = t.getTablePosition().getRow();
            Method tempMethod;
            Label tempLabel;
            
            DraggableClass tempDraggableClass = null;
            DraggableInterface tempDraggableInterface = null;
        
            if(temp instanceof DraggableClass){
                tempDraggableClass = (DraggableClass)temp;
            }
            else{
                tempDraggableInterface = (DraggableInterface)temp;
            }
            
            if(tempDraggableClass != null){
                //update method and replace label w/ new toString
                tempMethod = tempDraggableClass.
                        getMethod(currentMethodPosition);
                tempMethod.setVisibility(t.getNewValue());
                
                tempLabel = tempDraggableClass.getMethodLabel
                    (currentMethodPosition);
                tempLabel.setText(tempMethod.toString());
                tempDraggableClass.setMethod(currentMethodPosition,tempMethod);
                tempDraggableClass.setMethodLabel(currentMethodPosition,
                        tempLabel);
                
                methodBox.getChildren().set(currentMethodPosition, tempLabel);
            }
            
            else{
                //update method and replace label w/ new toString
                tempMethod = tempDraggableInterface.
                        getMethod(currentMethodPosition);
                tempMethod.setVisibility(t.getNewValue());
                
                tempLabel = tempDraggableInterface.getMethodLabel
                    (currentMethodPosition);
                tempLabel.setText(tempMethod.toString());
                tempDraggableInterface.setMethod(currentMethodPosition,tempMethod);
                tempDraggableInterface.setMethodLabel(currentMethodPosition,
                        tempLabel);
                
                methodBox.getChildren().set(currentMethodPosition, tempLabel);
            }
        });
        
        //We'll let the user add as many arguments as they want, and expand the 
        //number of columns in the table so they can edit the type. For 
        //simplicity's sake, they cannot change the name of the arguments from 
        //the default, which is "arg" + the argument number, starting from 0.
        ChangeListener<Object> cl = new ChangeListener<Object>(){
            @Override
            public void changed(ObservableValue<? extends Object> observable, 
                    Object oldValue, Object newValue){
                
                //1 is max(right), 0 is min(left)
                if(methodScrollPane.getHvalue()==1){
                    
                    //we use this size variable to determine which column was 
                    //edited, if it's over a certain amount, it was an argument
                    int size = methodTable.getColumns().size();
                    TableColumn<Method,String> newTableColumn = new TableColumn
                            <>(" ↓ Arg"+(size-4));
                    
                    newTableColumn.setMinWidth(100);
                    
                    newTableColumn.setCellFactory(TextFieldTableCell.
                            <Method>forTableColumn());
                    
                    newTableColumn.setEditable(true);
                    
                    newTableColumn.setOnEditCommit(
                            (CellEditEvent<Method,String> t)->{
                        
                        Method tempMethod = (Method) t.getTableView().getItems()
                                .get(t.getTablePosition().getRow());

                        //if argument column
                        if(tempMethod.getArguments().size()<size-4){
                            
                            int j;
                            for(j=tempMethod.getArguments().size();
                                    j<size-4;j++){
                                tempMethod.addArgument(" ");
                            }
                        }
                        
                        int methodPosition=(t.getTablePosition().getRow()); 
                        
                        tempMethod.setArgType(size-5,t.getNewValue());
                        
                        String m =tempMethod.toString();
                        Label l = new Label(m);
                        
                        l.getStyleClass().add("uml_label");
                        
                        tempMethodList.set(methodPosition,l);
                        DraggableClass tempDraggableClass = null;
                        DraggableInterface tempDraggableInterface = null;
                        if(temp instanceof DraggableClass){
                            tempDraggableClass = (DraggableClass)temp;
                        }
                        else{
                            tempDraggableInterface = (DraggableInterface)temp;
                        }
                            
                        if(tempDraggableClass!=null){
                            tempDraggableClass.setMethod(methodPosition, 
                                    tempMethod);
                            tempDraggableClass.setMethodLabel(methodPosition,l);
                            
                            //if the type isn't primitive, we need to add an API
                            //and pane if necessary
                            if(!primitives.contains(t.getNewValue())){
                                if(!tempDraggableClass.hasAPIPane()){
                                    addApiPane(sp, tempDraggableClass, 
                                        t.getNewValue());
                                }
                                else{
                        
                                    //if the class already has an APIPane, we 
                                    //should check and see if this data type is 
                                    //already included in that pane. If so, we 
                                    //won't add anything to the pane.
                                    StackPane apiPane = tempDraggableClass
                                            .getAPIPane();
                                    VBox tempVBox = (VBox)apiPane.getChildren()
                                            .get(1);
                                    boolean found = false;
                                    for(Object o: tempVBox.getChildren()){
                                        Label tempLabel = (Label)o;
                                        String currentAPI = tempLabel.getText();
                                        if(currentAPI.equals(t.getNewValue())){
                                            found=true;
                                            break;
                                        }
                                    }
                        
                                    //if the new method arg type isn't primitive 
                                    //and isn't one of the listed APIs for this 
                                    //class
                                    if(!found){
                                        addAPI(tempDraggableClass, 
                                            t.getNewValue());
                                    }
                        
                                }
                            }
                        }
                        
                        else{
                            tempDraggableInterface.setMethod(methodPosition, 
                                tempMethod);
                            tempDraggableInterface.setMethodLabel(methodPosition
                                ,l);
                            
                            if(!primitives.contains(t.getNewValue())){
                                if(!tempDraggableInterface.hasAPIPane()){
                                    addAPIPane(sp, tempDraggableInterface, 
                                        t.getNewValue());
                                }
                                
                                else{
                        
                                    //if the class already has an APIPane, we 
                                    //should check and see if this data type is 
                                    //already included in that pane. If so, we 
                                    //won't add anything to the pane.
                                    StackPane apiPane = tempDraggableInterface
                                            .getAPIPane();
                                    VBox tempVBox = (VBox)apiPane.getChildren()
                                            .get(1);
                                    boolean found = false;
                                    for(Object o: tempVBox.getChildren()){
                                        Label tempLabel = (Label)o;
                                        String currentAPI = tempLabel.getText();
                                        if(currentAPI.equals(t.getNewValue())){
                                            found=true;
                                            break;
                                        }
                                    }
                        
                                    //if the new method arg type isn't primitive 
                                    //and isn't one of the listed APIs for this 
                                    //interface
                                    if(!found){
                                        addAPI(tempDraggableInterface, 
                                            t.getNewValue());
                                    }
                        
                                }
                                
                            }
                        }

                    });

                    methodTable.getColumns().add(newTableColumn);
                }
            }
        };
        
        methodScrollPane.hvalueProperty().addListener(cl);

        

        
        //lets user change the package of their class/interface. The use of 
        //periods denotes "levels", or nexted folders, which will be relevant 
        //when changing names of the class/interface and exporting the code.
        //Note that there is no GUI component to this field.
        packageTextField.textProperty().addListener((observable, oldValue, 
                newValue)->{
            
            //We need to get the selectedPane, becuase otherwise every single 
            //pane's field changes. May be library issue.
            if(dataManager.isInState(SELECTING_PANE)){
                StackPane localPane = dataManager.getSelectedPane();
                Draggable drag = (Draggable)localPane.getChildren().get(0);
                DraggableClass tempDraggableClass = null;
                
                //interface
                if(drag instanceof DraggableClass){
                    tempDraggableClass = (DraggableClass)drag;
                }
                
                if(tempDraggableClass!=null){
                    
                    tempDraggableClass.setPackageName(newValue);
                }
            }
            
            
        });

        //updates package name in right hand side
        if(draggableClass!=null){
            packageTextField.setText(draggableClass.getPackageName());
        }
        
        
        //let user change name of class/interface
        classTextField.textProperty().addListener((observable, oldValue, 
                newValue) -> {

            //We need to get the selectedPane, becuase otherwise every single 
            //pane's name field changes. May be library issue.
            if(dataManager.isInState(SELECTING_PANE)){
                StackPane localPane = dataManager.getSelectedPane();
                Draggable drag = (Draggable)localPane.getChildren().get(0);
                DraggableClass tempDraggableClass = null;
                DraggableInterface tempDraggableInterface = null;
                if(drag instanceof DraggableClass){
                    tempDraggableClass = (DraggableClass)drag;
                }
                else{
                    tempDraggableInterface = (DraggableInterface)drag;
                }
                
                if(tempDraggableClass!=null){
                    
                    Label newNameLabel = new Label(newValue);
                    newNameLabel.getStyleClass().add("uml_label");
                    tempDraggableClass.setNameLabel(newNameLabel);
                    tempDraggableClass.getNameBox().getChildren().set(0,
                        newNameLabel);
                }
                
                else{
                    tempDraggableInterface.setNameString(newValue);
                    Label newNameLabel = new Label("<<" + newValue + ">>");
                    newNameLabel.getStyleClass().add("uml_label");
                    tempDraggableInterface.setNameLabel(newNameLabel);
                    tempDraggableInterface.getNameBox().getChildren().set(0,
                            newNameLabel);
                }
            }
            
            
        });
        
        //updates right side with current class's name
        Label classLabel = (Label)classBox.getChildren().get(0);
        
        //remove the arrows - not a part of the name of an interface, just 
        //something to indicate to the user at a glance that a pane is an 
        //interface
        classTextField.setText(classLabel.getText().replaceAll("<<","").
                replaceAll(">>",""));
        
        
        
       
        
        //display a drop-down list of all potential parents. Note that as per 
        //the original design reqruirement, classes can have multiple parents, 
        //there is no way to remove inheritance, and interface are not inherited
        //(or extended) but are implemented.
        ArrayList<String> potentialParents = new ArrayList();
        for(StackPane p: userMadePanes){
            Draggable tempDrag = (Draggable)p.getChildren().get(0);
            VBox tempHolder = (VBox)p.getChildren().get(1);
            VBox nameHolder = (VBox)tempHolder.getChildren().get(0);
            Label tempNameLabel = (Label)nameHolder.getChildren().get(0);
            String tempName = tempNameLabel.getText();
            
            //determine if pane in loop is an interface or class
            DraggableInterface di = null;
            DraggableClass dc = null;
            if(tempDrag instanceof DraggableClass){
                dc = (DraggableClass)tempDrag;
            }
            else{
                di = (DraggableInterface)tempDrag;
            }
            
            //if selected pane is a class
            if(draggableClass != null){
                
                //Java rules:
                //classes can only extend 1 class at a time
                //classes cannot extend an interface
                //you cannot be your own parent.
                
                //if the intended parent isn't named the same as the child, and 
                //the intended parent isn't an interface
                if(!draggableClass.getNameLabel().getText().equals(tempName) && 
                    di==null){
                    potentialParents.add(tempName);
                }
            }
            
            //otherwise, selected pane is an interface
            else{
                
                //Java rules:
                //interfaces can extend multiple interfaces, but can't extend a 
                //class
                //interfaces cannot implements other interfaces
                
                //if the parent interface doesn't have the same name, and the 
                //parent isn't actually a class
                if(!draggableInterface.getNameLabel().getText().equals(tempName)
                    && dc == null){
                    tempName = tempName.replaceAll("<<", "");
                    tempName = tempName.replaceAll(">>","");
                    potentialParents.add(tempName);  
                }
            }
            
        }
        
        //we need to clear the suggestions list, then add all of the new valid 
        //options
        parentComboBox.getItems().clear();
        parentComboBox.getItems().addAll(potentialParents);
        
        
        parentComboBox.setOnAction((event -> {
            if(dataManager.isInState(SELECTING_PANE)){
                
                if(parentComboBox.getSelectionModel()!=null && 
                        parentComboBox.getSelectionModel().getSelectedItem()
                        !=null){
                    
                    String parentName = parentComboBox.getSelectionModel().
                            getSelectedItem().toString();
                    
                    if(parentName.equals("Object") || 
                            parentName.equals("dummyParent")){
                        return;
                    }
                    
                    
                    
                    StackPane localPane = dataManager.getSelectedPane();
                    Draggable drag = (Draggable)localPane.getChildren().get(0);
                    DraggableClass tempDraggableClass = null;
                    DraggableInterface tempDraggableInterface = null;
                    if(drag instanceof DraggableClass){
                        tempDraggableClass = (DraggableClass)drag;
                    }
                    else{
                        tempDraggableInterface = (DraggableInterface)drag;
                    }

                    if(tempDraggableClass!=null){
                        
                        //update referential line
                        if(tempDraggableClass.hasParent()){
                            //remove old parent line
                            workspace.getChildren().remove(tempDraggableClass
                                .getParentLine());
                            
                            //remove reference to child
                            DraggableClass parent = (DraggableClass)
                                tempDraggableClass.getParentPane().
                                getChildren().get(0);
                            
                            parent.setChildPane(null);
                        }
                        
                            
                        //loop through all classes to get the pane for 
                        //reference
                        for(StackPane pane : userMadePanes){
                            VBox tempHolder = (VBox)pane.getChildren().get(1);
                            VBox nameHolder = (VBox)tempHolder.getChildren()
                                .get(0);
                            Label tempNameLabel = (Label)nameHolder.
                                getChildren().get(0);
                            String tempParentName = tempNameLabel.getText();
                                
                            if(tempParentName.equals(parentName)){
                                    
                                //give child references to parent
                                tempDraggableClass.setParentPane(pane);
                                Line parentLine = new Line(sp.getLayoutX()+
                                    sp.getWidth()/2.0,
                                    sp.getLayoutY(),
                                    pane.getLayoutX()+pane.getWidth()/2.0,
                                    pane.getLayoutY()+pane.getHeight());
                                workspace.getChildren().add(parentLine);
                                tempDraggableClass.setParentLine(parentLine);
                                    
                                //need to have parent reference child too 
                                //for movement in workspace
                                Draggable parentDraggable = (Draggable)
                                    pane.getChildren().get(0);
                                    
                                DraggableClass parentDC = null;
                                    
                                if(parentDraggable instanceof DraggableClass){
                                    parentDC = (DraggableClass)parentDraggable;
                                }
                                    
                                if(parentDC!=null){
                                    parentDC.setChildPane(sp);
                                        
                                }
                                    
                                break;
                            }
                        }
                        
                    }
                    
                    //interface
                    else{
                        
                        //update referential line if necessary
                        if(tempDraggableInterface.hasParent()){
                            //remove old parent line
                            workspace.getChildren().remove(tempDraggableInterface
                                .getParentLine());
                            
                            //remove reference to child
                            DraggableInterface parent = (DraggableInterface)
                                tempDraggableInterface.getParentPane().
                                getChildren().get(0);
                            
                            parent.setChildPane(null);
                        }
                        
                        
                        //loop through all classes to get the pane for 
                        //reference
                        for(StackPane pane : userMadePanes){
                            VBox tempHolder = (VBox)pane.getChildren().get(1);
                            VBox nameHolder = (VBox)tempHolder.getChildren()
                                .get(0);
                            Label tempNameLabel = (Label)nameHolder.
                                getChildren().get(0);
                            String tempParentName = tempNameLabel.getText();
                                
                            
                            if(tempParentName.equals("<<" + parentName + ">>")){
                                    
                                //give child references to parent
                                tempDraggableInterface.setParentPane(pane);
                                Line parentLine = new Line(sp.getLayoutX()+
                                    sp.getWidth()/2.0,
                                    sp.getLayoutY(),
                                    pane.getLayoutX()+pane.getWidth()/2.0,
                                    pane.getLayoutY()+pane.getHeight());
                                workspace.getChildren().add(parentLine);
                                tempDraggableInterface.setParentLine(parentLine);
                                    
                                //need to have parent reference child too 
                                //for movement in workspace
                                Draggable parentDraggable = (Draggable)
                                    pane.getChildren().get(0);
                                    
                                DraggableInterface parentIC = null;
                                    
                                if(parentDraggable instanceof DraggableInterface){
                                    parentIC = (DraggableInterface)
                                        parentDraggable;
                                }
                                    
                                if(parentIC!=null){
                                    parentIC.setChildPane(sp);
                                        
                                }
                                    
                                break;
                            }
                        }
                    }
                    
                    
                }
            }
        }));
        
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
        
        Draggable draggable = (Draggable)sp.getChildren().get(0);
        
        //handle api and inheritance lines/panes
        if(draggable instanceof DraggableClass){
            DraggableClass dc = (DraggableClass)draggable;
            
            //handles api movement
            if(dc.hasAPIPane()){
                StackPane tempPane = dc.getAPIPane();
                Line tempLine = dc.getAPILine();
                tempPane.setLayoutX(sp.getLayoutX()-75);
                tempPane.setLayoutY(sp.getLayoutY()+sp.getHeight()/2.0
                        -tempLine.getStrokeWidth());
                
                tempLine.setStartX(sp.getLayoutX()-50);
                tempLine.setStartY(sp.getLayoutY()+sp.getHeight()/2.0);
                tempLine.setEndX(sp.getLayoutX());
                tempLine.setEndY(sp.getLayoutY()+sp.getHeight()/2.0);
            }
            
            //handle if the pane has a parent
            if(dc.hasParent()){
                dc.getParentLine().setStartX(sp.getLayoutX()+sp.getWidth()/2.0);
                dc.getParentLine().setStartY(sp.getLayoutY());
            }
            
            //handle if the pane has a child
            if(dc.hasChild()){
                StackPane childPane = dc.getChildPane();
                DraggableClass child = (DraggableClass)
                        childPane.getChildren().get(0);
                child.getParentLine().setEndX(sp.getLayoutX()+sp.getWidth()/2.0);
                child.getParentLine().setEndY(sp.getLayoutY()+sp.getHeight());
            }
            
        }
        
        else{
            DraggableInterface di = (DraggableInterface)draggable;
            
            //handles api movement
            if(di.hasAPIPane()){
                StackPane tempPane = di.getAPIPane();
                Line tempLine = di.getAPILine();
                tempPane.setLayoutX(sp.getLayoutX()-75);
                tempPane.setLayoutY(sp.getLayoutY()+sp.getHeight()/2.0
                        -tempLine.getStrokeWidth());
                
                tempLine.setStartX(sp.getLayoutX()-50);
                tempLine.setStartY(sp.getLayoutY()+sp.getHeight()/2.0);
                tempLine.setEndX(sp.getLayoutX());
                tempLine.setEndY(sp.getLayoutY()+sp.getHeight()/2.0);
            }
            
            //handle if the pane has a parent
            if(di.hasParent()){
                di.getParentLine().setStartX(sp.getLayoutX()+sp.getWidth()/2.0);
                di.getParentLine().setStartY(sp.getLayoutY());
            }
            
            //handle if the pane has a child
            if(di.hasChild()){
                StackPane childPane = di.getChildPane();
                DraggableInterface child = (DraggableInterface)
                        childPane.getChildren().get(0);
                child.getParentLine().setEndX(sp.getLayoutX()+sp.getWidth()/2.0);
                child.getParentLine().setEndY(sp.getLayoutY()+sp.getHeight());
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
    
    /**
     * Creates and displays a pane showing the external library dependencies of 
     * the class, with a line between the APIPane and the class pane.
     * 
     * @param sp
     * StackPane in workspace that new APIPane's position will be relative to
     * 
     * @param dc
     * The class which will contain references to the new APIPane
     * 
     * @param api
     * The name of the external library
     */
    public void addApiPane(StackPane sp, DraggableClass dc, String api){
        
        //need to add a line as well as a pane
        Line l = new Line(sp.getLayoutX()-50,sp.getLayoutY()+sp.getHeight()/2.0,
                sp.getLayoutX(),sp.getLayoutY()+sp.getHeight()/2.0);
        
        //make line visible to user
        workspace.getChildren().add(l);
        
        //add reference for later
        dc.setAPILine(l);
        
        //now create pane and everything inside of it
        StackPane apiPane = new StackPane();
        apiPane.getStyleClass().add("api_stack_pane");
        Rectangle r = new Rectangle(50,30);
        r.setFill(Color.WHITE);
        VBox apiVBox = new VBox();
        apiVBox.getStyleClass().add("rect_vbox");
        Label apiLabel = new Label(api);
        apiLabel.getStyleClass().add("uml_label");
        apiVBox.getChildren().add(apiLabel);
        apiPane.getChildren().addAll(r,apiVBox);
        
        //position properly
        apiPane.setLayoutX(sp.getLayoutX()-75);
        apiPane.setLayoutY(sp.getLayoutY()+sp.getHeight()/2.0
                -l.getStrokeWidth());
        
        //make visible to user
        workspace.getChildren().add(apiPane);
        
        //reference for later
        dc.setAPIPane(apiPane);
    }
    
    /**
     * Adds a library to the class's APIPane
     * 
     * @param dc
     * The class which is adding the external library
     * 
     * @param api 
     * The library itself
     */
    public void addAPI(DraggableClass dc, String api){
        Label l = new Label(api);
        l.getStyleClass().add("uml_label");
        VBox tempVBox = (VBox)dc.getAPIPane().getChildren().get(1);
        tempVBox.getChildren().add(l);
    }
    
    /**
     * Adds an api pane to the given interface
     * 
     * @param sp
     * The pane containing the interface
     * 
     * @param di
     * The interface itself
     * 
     * @param api 
     * The first external library that's added to the new api pane
     */
    public void addAPIPane(StackPane sp, DraggableInterface di, String api){
        
        //need to add a line as well as a pane
        Line l = new Line(sp.getLayoutX()-50,sp.getLayoutY()+sp.getHeight()/2.0,
                sp.getLayoutX(),sp.getLayoutY()+sp.getHeight()/2.0);
        
        //make line visible to user
        workspace.getChildren().add(l);
        
        //add reference for later
        di.setAPILine(l);
        
        //now create pane and everything inside of it
        StackPane apiPane = new StackPane();
        apiPane.getStyleClass().add("api_stack_pane");
        Rectangle r = new Rectangle(50,30);
        r.setFill(Color.WHITE);
        VBox apiVBox = new VBox();
        apiVBox.getStyleClass().add("rect_vbox");
        Label apiLabel = new Label(api);
        apiLabel.getStyleClass().add("uml_label");
        apiVBox.getChildren().add(apiLabel);
        apiPane.getChildren().addAll(r,apiVBox);
        
        //position properly
        apiPane.setLayoutX(sp.getLayoutX()-75);
        apiPane.setLayoutY(sp.getLayoutY()+sp.getHeight()/2.0
                -l.getStrokeWidth());
        
        //make visible to user
        workspace.getChildren().add(apiPane);
        
        //reference for later
        di.setAPIPane(apiPane);
    }
    
    /**
     * Adds another library to the interface's api pane
     * 
     * @param di
     * The interface that is using the external library
     * 
     * @param api 
     * The external library's name
     */
    public void addAPI(DraggableInterface di, String api){
        Label l = new Label(api);
        l.getStyleClass().add("uml_label");
        VBox tempVBox = (VBox)di.getAPIPane().getChildren().get(1);
        tempVBox.getChildren().add(l);
    }
    
}
