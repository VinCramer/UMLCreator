
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
import javafx.scene.shape.Rectangle;
import saf.AppTemplate;
import saf.components.AppDataComponent;
import static umlcreator.data.UMLCreatorState.SELECTING_PANE;
import static umlcreator.data.UMLCreatorState.STARTING_CLASS;
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
	dropShadowEffect.setRadius(15);
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
                newClass.getMethodBox(),newClass.getVarBox());
        
        //want the UMLs as stackPanes so they can overlap
        stackPane.getChildren().addAll(newClass.getHolderBox());
        
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
    }
    
    /**
     * Handles adding our new class to the user's work area
     * 
     * @param newClass
     * The newly created class that will be added to the workspace
     */
    public void initNewClass(StackPane newClass){
        
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
	
        //change state
        state=STARTING_CLASS;
        
        //update the editing section with the default values 
        workspace.setClassTextField(DEFAULT_CLASS_NAME);
        workspace.setPackageTextField(DEFAULT_PACKAGE_NAME);
        workspace.setParentComboBox(DEFAULT_PARENT_VALUE);
        
        
        workspace.addPaneToWorkspace(newClass);
        
        
    }
    
}
