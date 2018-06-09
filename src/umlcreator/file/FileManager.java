
package umlcreator.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import saf.components.AppDataComponent;
import saf.components.AppFileComponent;
import umlcreator.data.DataManager;
import umlcreator.data.Draggable;
import umlcreator.data.DraggableClass;
import umlcreator.data.DraggableInterface;

/**
 *
 * @author Vincent Cramer
 */
public class FileManager implements AppFileComponent{
    
    public static final String DUMMY_STRING="\"\"";
    public static final int DUMMY_INT=1;
    public static final double DUMMY_DOUBLE=1.0;
    public static final char DUMMY_CHAR = '\'';
    public static final boolean DUMMY_BOOLEAN=false;
    public static final long DUMMY_LONG = 1;
    public static final byte DUMMY_BYTE = 1;
    public CharSequence stringSequence= "String";
    public CharSequence intSequence = "int";
    public CharSequence doubleSequence = "double";
    public CharSequence booleanSequence = "boolean";
    public CharSequence longSequence = "long";
    public CharSequence charSequence = "char";

    @Override
    public void saveData(AppDataComponent data, String filePath) 
            throws IOException {
        
    }

    @Override
    public void loadData(AppDataComponent data, String filePath) 
            throws IOException {
        
    }

    @Override
    public void exportData(AppDataComponent data, String filePath) 
            throws IOException {
        DataManager dataManager = (DataManager)data;
        ArrayList<StackPane> panes = dataManager.getPanes();
        
        //project folder
        File file = new File(filePath);
        file.mkdir();
        
        //source code folder
        String srcLocation = file.getPath()+"\\src";
        File src = new File(srcLocation);
        src.mkdir();
        
        PrintWriter pw;
        
        DraggableClass dc;
        DraggableInterface di;
        Draggable drag;
            
        String packageName, name, baseLocation;
        boolean nestedPackage;

        File f;
        
        //iterate through each pane, and if there's a package, we need to create
        //a folder for that class/interface
        for(StackPane sp:panes){
            drag = (Draggable)sp.getChildren().get(0);
            dc = null;
            di = null;
            nestedPackage = false;
            
            if(drag instanceof DraggableClass){
                dc = (DraggableClass)drag;
                packageName = dc.getPackageName();
                name = ((Label)(dc.getNameBox().getChildren().get(0))).getText();
            }
            else{
                di = (DraggableInterface)drag;
                packageName = di.getPackageName();
                name = ((Label)(di.getNameBox().getChildren().get(0))).getText();
                name = name.replaceAll("<<","");
                name = name.replaceAll(">>","");
            }
            
            
            if(packageName.contains(".")){
                nestedPackage=true;
            }
            
            if(nestedPackage){
                //TODO - finish this!
                //string literal of period
                String[] parts = packageName.split("[.]");
                baseLocation = srcLocation+ "\\";
                for(String s: parts){
                    File tempFile = new File(baseLocation+s);
                    if(!tempFile.exists()){
                        tempFile.mkdir();
                    }
                    baseLocation+=s + "\\";
                }
            }
            else{
                baseLocation = srcLocation;
            }
            if(!nestedPackage && !packageName.equals("")){
                baseLocation = srcLocation + "\\" + packageName;
                File tempFile = new File(baseLocation);
                    if(!tempFile.exists()){
                        tempFile.mkdir();
                    }
            }
            
            
            f = new File(baseLocation + "\\"  + name+".java");
            pw = new PrintWriter(f);
            if(dc != null){
                
                pw.write(dc.toExportString());
                pw.close();
            }
            else{
                pw.print(di.toExportString());
                pw.close();
            }
            
        }
        
    }

    @Override
    public void importData(AppDataComponent data, String filePath) 
            throws IOException {
        
    }
    
}
