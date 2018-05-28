
package umlcreator.controller;

import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import properties_manager.PropertiesManager;
import saf.AppTemplate;
import static saf.settings.AppPropertyType.SAVE_COMPLETED_MESSAGE;
import static saf.settings.AppPropertyType.SAVE_COMPLETED_TITLE;
import static saf.settings.AppPropertyType.SAVE_ERROR_MESSAGE;
import static saf.settings.AppPropertyType.SAVE_ERROR_TITLE;
import static saf.settings.AppPropertyType.SAVE_WORK_TITLE;
import saf.ui.AppMessageDialogSingleton;
import umlcreator.data.DataManager;
import umlcreator.data.Draggable;
import umlcreator.data.DraggableClass;
import umlcreator.data.UMLCreatorState;
import umlcreator.gui.Workspace;

/**
 *
 * @author Vincent Cramer
 */
public class EditController {
    
    AppTemplate app;
        
    DataManager dataManager;
    
    /**
     * Constructor for editController, which assists DataManger and Workspace in 
     * handling user input
     * 
     * @param initApp 
     * AppTemplate we use to initialize local dataManager
     */
    public EditController(AppTemplate initApp){
        app = initApp;
	dataManager = (DataManager)app.getDataComponent();
    }

    /**
     * This method will take a snapshot of the user's UML diagram in the 
     * workspace
     */
    public void processSnapshot() {
        //gets the workspace and writes an image
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        Pane p = workspace.getWorkspace();
        WritableImage image = p.snapshot(new SnapshotParameters(),null);

        //created for use with dialog box
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        //allows user to choose where to save the picture.
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("./images/"));
        fc.setTitle(props.getProperty(SAVE_WORK_TITLE));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Picture "
                + "(*.png)","*.png"));

        //the given file directory as a File object
        File selectedFile = fc.showSaveDialog(app.getGUI().getWindow());
            
        //try to save picture to given directory, if it doesn't work, inform 
        //user
        try{
            ImageIO.write(SwingFXUtils.fromFXImage(image,null),
                    "png",selectedFile);
            
            AppMessageDialogSingleton dialog = 
                    AppMessageDialogSingleton.getSingleton();
            
            dialog.show(props.getProperty(SAVE_COMPLETED_TITLE),
                    props.getProperty(SAVE_COMPLETED_MESSAGE));
            }
        catch(IOException ioe){
            AppMessageDialogSingleton dialog = 
                    AppMessageDialogSingleton.getSingleton();
            
	    dialog.show(props.getProperty(SAVE_ERROR_TITLE), 
                    props.getProperty(SAVE_ERROR_MESSAGE));    
            }
        catch(IllegalArgumentException iae){
            AppMessageDialogSingleton dialog = 
                    AppMessageDialogSingleton.getSingleton();
            
	    dialog.show(props.getProperty(SAVE_ERROR_TITLE), 
                    props.getProperty(SAVE_ERROR_MESSAGE));
        }
    }

    /**
     * Processes the selection of a node in the workspace by the user
     */
    public void processSelectionTool() {
        
        //first we change the cursor
	Scene scene = app.getGUI().getPrimaryScene();
	scene.setCursor(Cursor.DEFAULT);
        
        //then we change the state
        dataManager.setState(UMLCreatorState.SELECTING_PANE);
        
        //lastly, we will update the workspace with relevant info about the 
        //newly selected pane
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
	workspace.reloadWorkspace();
    }

    /**
     * Processes the user's mouse presses
     * 
     * @param x
     * x coordinate of the user's mouse at the time of the press
     * 
     * @param y 
     * y coordinate of the user's mouse at the time of the press
     */
    public void processMousePressed(double x, double y) {
        DataManager dataManager = (DataManager)app.getDataComponent();
        Workspace workspace = (Workspace)app.getWorkspaceComponent(); 
        
        if(dataManager.isInState(UMLCreatorState.SELECTING_PANE)){
            Scene scene = app.getGUI().getPrimaryScene();
            StackPane sp = dataManager.getTopPane(x,y);
            
            //if the pane is not null, we'll consider that the user likely wants
            //to move the pane, and we'll also update the information displayed 
            //to be relevant to the currently selected node
            if(sp!=null){
                scene.setCursor(Cursor.MOVE);
                dataManager.setState(UMLCreatorState.DRAGGING_PANE);
                workspace.updateComponentToolbar(sp);
            }
            
            //otherwise, if the user somehow selects a null pane, we won't 
            //update anything, and we'll reload the workspace
            else{
                scene.setCursor(Cursor.DEFAULT);
		dataManager.setState(UMLCreatorState.DRAGGING_NOTHING);
		app.getWorkspaceComponent().reloadWorkspace();
                
            }
        }
        
    }

    /**
     * Processes the clicking and dragging of the user's mouse
     * 
     * @param x
     * x coordinate of the user's mouse when dragging
     * 
     * @param y 
     * y coordinate of the user's mouse when dragging
     */
    public void processMouseDragged(double x, double y) {
        DataManager dataManager = (DataManager)app.getDataComponent();
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        
        if(dataManager.isInState(UMLCreatorState.DRAGGING_PANE)){
            StackPane sp = dataManager.getSelectedPane();
            workspace.move(sp,x,y);
        }
        
    }

    /**
     * Processes the release of the mouse
     * 
     * @param x
     * x coordinate of the user's mouse at the time of release
     * 
     * @param y 
     * y coordinate of the user's mouse at the time of release
     */
    public void processMouseReleased(double x, double y) {
        DataManager dataManager = (DataManager)app.getDataComponent();
        
        if(dataManager.isInState(UMLCreatorState.DRAGGING_PANE)){
            dataManager.setState(UMLCreatorState.SELECTING_PANE);
	    Scene scene = app.getGUI().getPrimaryScene();
	    scene.setCursor(Cursor.DEFAULT);
	    app.getGUI().updateToolbarControls(false);
        }
        
        else if(dataManager.isInState(UMLCreatorState.DRAGGING_NOTHING)){
            dataManager.setState(UMLCreatorState.SELECTING_PANE);
        }
        
    }
    
}
