
package umlcreator.data;

import java.util.ArrayList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import saf.AppTemplate;
import saf.components.AppDataComponent;
import static umlcreator.data.UMLCreatorState.SELECTING_PANE;
import static umlcreator.data.UMLCreatorState.STARTING_CLASS;
import static umlcreator.data.UMLCreatorState.STARTING_INTERFACE;
import umlcreator.gui.Workspace;

/**
 *
 * @author Vincent Cramer
 */
public class DataManager implements AppDataComponent{
    
    private AppTemplate app;
    
    private TextField className, packageName;
    private ComboBox parentComboBox;
    
    private StackPane selectedPane;
    
    private Effect highlightedEffect;
    
    public static final String YELLOW_HEX = "#EEEE00";
    public static final Paint HIGHLIGHTED_COLOR = Paint.valueOf(YELLOW_HEX);
    public static final int HIGHLIGHTED_STROKE_THICKNESS = 3;
    
    public static final String DEFAULT_PACKAGE_NAME = "dummy.package";
    public static final String DEFAULT_PARENT_VALUE = "dummyParent";
    public static final String DEFAULT_CLASS_NAME = "dummyClass";
    public static final String DEFAULT_INTERFACE_NAME = "dummyInterface";

    private ArrayList<StackPane> panes;
    
    private UMLCreatorState state;


    /**
     * Creates the DataManger, which allows the user to manipulate the Workspace
     * 
     * @param initApp 
     * The AppTemplate that is created with the launching of the project
     */
    public DataManager(AppTemplate initApp){
        app=initApp;
        
        //add highlight effect, used to show a pane is selected
        DropShadow dropShadowEffect = new DropShadow();
	dropShadowEffect.setOffsetX(0.0f);
	dropShadowEffect.setOffsetY(0.0f);
	dropShadowEffect.setSpread(1.0);
	dropShadowEffect.setColor(Color.YELLOW);
	dropShadowEffect.setBlurType(BlurType.GAUSSIAN);
	dropShadowEffect.setRadius(5);
	highlightedEffect = dropShadowEffect;
        
        panes = new ArrayList();
    }
    
    /**
     * Clears all information about the currently selected items and UML diagram
     */
    @Override
    public void reset() {

    }
    
    /**
     * Creates a new interface, with a default name of dummyInterface
     */
    public void addNewInterface(){
        DraggableInterface newInterface = new DraggableInterface();
        
        newInterface.start(100,100);
        StackPane stackPane = new StackPane();
        stackPane.setMinWidth(100);
        stackPane.setMinHeight(100);
        
        //add the default class name 
        Label nameLabel = new Label("<<dummyInterface>>");
        nameLabel.getStyleClass().add("uml_label");
        newInterface.setNameLabel(nameLabel);
        newInterface.setNameString("dummyInterface");
        
        //store label in field to change later if necessary, and add styling
        newInterface.getNameBox().getChildren().add(nameLabel);
        newInterface.getNameBox().getStyleClass().add("rect_vbox");
        newInterface.getNameBox().setMinHeight(newInterface.getHeight()/3.0);
        
        //apply stlying and size restrictions on the method and variable areas
        newInterface.getMethodBox().setMinHeight(newInterface.getHeight()/3.0);
        newInterface.getMethodBox().getStyleClass().add("rect_vbox");
        newInterface.getVarBox().getStyleClass().add("rect_vbox");
        newInterface.getVarBox().setMinHeight(newInterface.getHeight()/3.0);
        
        //place all of the parts into an HBox to display in top-down order
        newInterface.getHolderBox().getChildren().addAll(
                newInterface.getNameBox(),newInterface.getVarBox(),
                newInterface.getMethodBox());
        
        //want the UMLs as stackPanes so they can overlap
        stackPane.getChildren().addAll(newInterface,newInterface.getHolderBox());
        
        initNewInterface(stackPane);
        setSelectedPane(stackPane);
    }

    /**
     * Creates a new class. The default name is dummyClass.
     *  
     */
    public void addNewClass() {
        
        DraggableClass newClass = new DraggableClass();
        
        newClass.start(100,100);
        StackPane stackPane = new StackPane();
        stackPane.setMinWidth(100);
        stackPane.setMinHeight(100);
        
        //add the default class name 
        Label nameLabel = new Label("dummyClass");
        nameLabel.getStyleClass().add("uml_label");
        newClass.setNameLabel(nameLabel);
        
        
        //store label in field to change later if necessary, and add styling
        newClass.getNameBox().getChildren().add(nameLabel);
        newClass.getNameBox().getStyleClass().add("rect_vbox");
        newClass.getNameBox().setMinHeight(newClass.getHeight()/3.0);
        
        //apply stlying and size restrictions on the method and variable areas
        newClass.getMethodBox().setMinHeight(newClass.getHeight()/3.0);
        newClass.getMethodBox().getStyleClass().add("rect_vbox");
        newClass.getVarBox().getStyleClass().add("rect_vbox");
        newClass.getVarBox().setMinHeight(newClass.getHeight()/3.0);
        
        //place all of the parts into an HBox to display in top-down order
        newClass.getHolderBox().getChildren().addAll(newClass.getNameBox(),
                newClass.getVarBox(),newClass.getMethodBox());
        
        //want the UMLs as stackPanes so they can overlap
        stackPane.getChildren().addAll(newClass,newClass.getHolderBox());
        
        initNewClass(stackPane);
        setSelectedPane(stackPane);
        
    }
    
    /**
     * Provides a highlight effect on the current selected pane
     * 
     * @param selected 
     * The most recent pane that the user has clicked on or generated
     */
    public void setSelectedPane(StackPane selected){
        if(selectedPane != null){
            unhighlightPane();
        }
        selectedPane = selected;
        highlightPane(selected);
        state =SELECTING_PANE;
    }
    
    /**
     * Adds an effect to a pane to highlight it for the user
     * 
     * @param toBeHighlighted 
     * The pane that the highlight effect will be applied to
     */
    public void highlightPane(StackPane toBeHighlighted){
        toBeHighlighted.setEffect(highlightedEffect);
        toBeHighlighted.getChildren().get(0).setEffect(highlightedEffect);
    }
    
    /**
     * Method which removes the highlight effect from the designated pane
     */
    public void unhighlightPane() {
	selectedPane.setEffect(null);
        selectedPane.getChildren().get(0).setEffect(null);
    }
    
    /**
     * Handles adding our new class to the user's work area
     * 
     * @param newClass
     * The newly created class that will be added to the workspace
     */
    public void initNewClass(StackPane newClass){
        
        if(selectedPane!=null){
            unhighlightPane();
        }
        
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
	
        //change state
        state=STARTING_CLASS;
        
        //update the editing section with the default values 
        workspace.setClassTextField(DEFAULT_CLASS_NAME);
        workspace.setPackageTextField(DEFAULT_PACKAGE_NAME);
        workspace.setParentComboBox(DEFAULT_PARENT_VALUE);
        
        
        workspace.addPaneToWorkspace(newClass);
        
        
    }
    
    /**
     * Deals with the addition of a new interface in the workspace
     * 
     * @param newInterface 
     * The interface & pane that will be added
     */
    public void initNewInterface(StackPane newInterface){
       if(selectedPane!=null){
            unhighlightPane();
        }
        
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
	
        //change state
        state=STARTING_INTERFACE;
        
        //update the editing section with the default values 
        workspace.setClassTextField(DEFAULT_INTERFACE_NAME);
        workspace.setPackageTextField(DEFAULT_PACKAGE_NAME);
        workspace.setParentComboBox(DEFAULT_PARENT_VALUE);
        
        
        workspace.addPaneToWorkspace(newInterface);
         
    }
    
    /**
     * Updates the state of the program
     * 
     * @param newState 
     * The new state of the program
     */
    public void setState(UMLCreatorState newState){
        state=newState;
    }
    
    /**
     * Boolean expression to see if the current state is the same state as the 
     * argument
     * 
     * @param testState
     * The state value that is compared to the actual current state
     * 
     * @return 
     * True if the argument state is the same as the current application, false 
     * otherwise
     */
    public boolean isInState(UMLCreatorState testState) {
	return state == testState;
    }
    
    /**
     * Accessor for selected pane
     * 
     * @return 
     * Pane that is currently being selected
     */
    public StackPane getSelectedPane(){
        return selectedPane;
    }
    
    /**
     * Returns the StackPane from the user's workspace that is "on top", or in 
     * front of the other ones if there are some that overlap where the user 
     * clicks.
     * 
     * @param x
     * x coordinate of where user clicked
     * 
     * @param y
     * y coordinate of where user clicked
     * 
     * @return 
     * The top most StackPane where the user clicked if there's 1 or more there,
     * or null if the user clicked somewhere without any Stack Panes.
     */
    public StackPane findTopPane(double x, double y){
        
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        ArrayList<StackPane> panes = workspace.getUserMadePanes();
        
        int i;
        double width,height;
        double rightEdge,leftEdge,topEdge,bottomEdge;
        StackPane tempPane;
        
        //we loop in reverse order because it's more likely for the user's newer
        //nodes to cover the older ones
        for(i=panes.size()-1;i>=0;i--){
            tempPane = panes.get(i);
            
            //we can use the pre defined getWidth and getHeight methods from our
            //DraggableClasses/DraggableInterfaces
            width = ((Draggable)(tempPane.getChildren().get(0))).getWidth();
            height = ((Draggable)(tempPane.getChildren().get(0))).getHeight();
            
            rightEdge = tempPane.getLayoutX()+width;
            leftEdge = tempPane.getLayoutX();
            bottomEdge = tempPane.getLayoutY()+height;
            topEdge = tempPane.getLayoutY();
            
            //if coordinates are within the edges of this node, that's our top
            if((x>leftEdge && x<rightEdge)&&(y<bottomEdge&&y>topEdge)){
                return tempPane;
            }
        }
        return null;
    }
    
    /**
     * Accesses the top pane from where the user clicked, if there is a pane 
     * there, and selects it
     * 
     * @param x
     * x coordinate of where user clicked
     * 
     * @param y 
     * y coordinate of where user clicked
     * 
     * @return
     * The top most pane where the user clicked, or null
     */
    public StackPane getTopPane(double x, double y){
        StackPane topPane = findTopPane(x,y);
        
        if(topPane == null){
            return null;
        }
        
        //already selected
        if(topPane.equals(selectedPane)){
            return topPane;
        }
        
        if(selectedPane!=null){
            unhighlightPane();
        }
        
        if(topPane!=null){
            highlightPane(topPane);
            selectedPane = topPane;
            ((Draggable)(topPane.getChildren().get(0))).start((int)x,(int)y);
            return selectedPane;
        }
        
        return null;
    }
    
    public ArrayList<StackPane> getPanes(){
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        return workspace.getUserMadePanes();
    }
    
}
