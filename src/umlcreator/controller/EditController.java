
package umlcreator.controller;

import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
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
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Picture (*.png)","*.png"));

        //the given file directory as a File object
        File selectedFile = fc.showSaveDialog(app.getGUI().getWindow());
            
        //try to save picture to given directory, if it doesn't work, inform 
        //user
        try{
            ImageIO.write(SwingFXUtils.fromFXImage(image,null),"png",selectedFile);
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show(props.getProperty(SAVE_COMPLETED_TITLE),props.getProperty(SAVE_COMPLETED_MESSAGE));
            }
        catch(IOException ioe){
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
	    dialog.show(props.getProperty(SAVE_ERROR_TITLE), props.getProperty(SAVE_ERROR_MESSAGE));    
            }
        catch(IllegalArgumentException iae){
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
	    dialog.show(props.getProperty(SAVE_ERROR_TITLE), props.getProperty(SAVE_ERROR_MESSAGE));
        }
    }
    
}
