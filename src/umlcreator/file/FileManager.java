
package umlcreator.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import saf.components.AppDataComponent;
import saf.components.AppFileComponent;
import umlcreator.data.DataManager;
import umlcreator.data.Draggable;
import umlcreator.data.DraggableClass;
import umlcreator.data.DraggableInterface;
import umlcreator.gui.Method;
import umlcreator.gui.Var;

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
    
    
    /**
     * Saves the app's data to a JSON file in the designated file path
     * 
     * @param data
     * The app's information
     * 
     * @param filePath
     * Location of the JSON file
     * 
     * @throws IOException 
     * Thrown when program doesn't have permission to write in the designated 
     * file path
     */
    @Override
    public void saveData(AppDataComponent data, String filePath) 
            throws IOException {
        
        DataManager dataManager = (DataManager)data;
        ArrayList<StackPane> panes = dataManager.getPanes();
        
        //we'll save this as 2 sub arrays: classes and interfaces

        JsonArrayBuilder classArrayBuilder = Json.createArrayBuilder();
        JsonArrayBuilder interfaceArrayBuilder = Json.createArrayBuilder();
        
        for(StackPane sp:panes){
            Draggable drag = (Draggable)sp.getChildren().get(0);
            double x = sp.getLayoutX();
            double y = sp.getLayoutY();
            if(drag instanceof DraggableClass){
                DraggableClass dc = (DraggableClass)drag;
                JsonObject tempJso = makeDraggableClassJsonObject(dc,x,y);
                classArrayBuilder.add(tempJso);
            }
            else{
                DraggableInterface di = (DraggableInterface)drag;
                JsonObject tempJso = makeDraggableInterfaceJsonObject(di,x,y);
                interfaceArrayBuilder.add(tempJso);
            }
        }
        
        JsonArray classArray = classArrayBuilder.build();
        JsonArray interfaceArray = interfaceArrayBuilder.build();
        
        JsonObject classJso = Json.createObjectBuilder().add("classes",
                classArray).build();
        JsonObject interfaceJso = Json.createObjectBuilder().add("interfaces",
                interfaceArray).build();
        
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder()
                .add(classJso)
                .add(interfaceJso);
        
        JsonArray jsArray = arrayBuilder.build();
        
        //hold both of the sub arrays (class, then interface)
        JsonObject dataManagerJSO = Json.createObjectBuilder()
                .add("panes",jsArray)
                .build();
        
        //use pretty printing to set up writer
        Map<String, Object> properties = new HashMap<>(1);
	properties.put(JsonGenerator.PRETTY_PRINTING, true);
	JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
	StringWriter sw = new StringWriter();
	JsonWriter jsonWriter = writerFactory.createWriter(sw);
	jsonWriter.writeObject(dataManagerJSO);
	jsonWriter.close();
        
        //actually write to file
        OutputStream os = new FileOutputStream(filePath);
	JsonWriter jsonFileWriter = Json.createWriter(os);
	jsonFileWriter.writeObject(dataManagerJSO);
	String prettyPrinted = sw.toString();
	PrintWriter pw = new PrintWriter(filePath);
	pw.write(prettyPrinted);
	pw.close();
    }
    
    /**
     * Provides a JsonObject representation of the provided DraggableClass
     * 
     * @param dc
     * The DraggableClass itself
     * 
     * @param x
     * The x position of the class in the workspace
     * 
     * @param y
     * The y position of the class in the workspace
     * 
     * @return 
     * A JsonObject representation of the class made by the user
     */
    private JsonObject makeDraggableClassJsonObject(DraggableClass dc, double x,
            double y){
        Label nameLabel = (Label)dc.getNameBox().getChildren().get(0);
        String className = nameLabel.getText();
        
        boolean isAbstract = dc.getIsAbstract();
        
        //get the string representation of the methods and variables
        ArrayList<String> methodStrings = new ArrayList();
        ArrayList<Method> methodList = dc.getMethodList();
        ArrayList<String> varStrings = new ArrayList();
        ArrayList<Var> varList = dc.getVariableList();
        for(Method m:methodList){
            methodStrings.add(m.toString());
        }
        for(Var v:varList){
            varStrings.add(v.toString());
        }
        
        boolean hasAPI = dc.hasAPIPane();
        boolean hasParent = dc.hasParent();
        boolean hasInterface = dc.hasInterface();
        StackPane apiPane;
        ArrayList<String> apiList = new ArrayList();
        StackPane parentPane;
        String parentName = "";
        ArrayList<String> childList = new ArrayList();
        ArrayList<StackPane> interfacePanes;
        ArrayList<String> interfaceList = new ArrayList();
        
        if(hasAPI){
            apiPane = dc.getAPIPane();
            VBox v = (VBox)apiPane.getChildren().get(1);
            for(Object o:v.getChildren()){
                Label apiLabel = (Label)o;
                apiList.add(apiLabel.getText());
            }
        }
        if(hasParent){
            parentPane = dc.getParentPane();
            DraggableClass parentDC = (DraggableClass)
                    parentPane.getChildren().get(0);
            Label tempParentLabel = (Label)parentDC.getNameBox().getChildren()
                    .get(0);
            parentName = tempParentLabel.getText();
        }
        
        if(hasInterface){
            interfacePanes = dc.getImplementPanes();
            for(StackPane intPane: interfacePanes){
                DraggableInterface di = (DraggableInterface)
                        intPane.getChildren().get(0);
                Label iLabel = di.getNameLabel();
                String intName = iLabel.getText();
                intName = intName.replaceAll("<<","");
                intName = intName.replaceAll(">>","");
                interfaceList.add(intName);
            }
        }
        
        //need to save, for each class:
        //x/y position
        //name of class
        //if it's abstract
        //all methods
        //all variables
        //if it has api
            //if so, what apis
        //if it's a parent - not really necessary
            //if so, a reference to child - not really necessary
        //if it has a parent
            //if so, a reference to parent
        //if it has interfaces
            //if so, what interfaces
        
        //method, var, etc. should be json arrays, and added to this builder
        JsonArrayBuilder methodArrayBuilder = Json.createArrayBuilder();
        for(String s:methodStrings){
            JsonObject jso = Json.createObjectBuilder().add("method",s).build();
            methodArrayBuilder.add(jso);
        }
        
        JsonArray methodArray = methodArrayBuilder.build();
        
        JsonArrayBuilder varArrayBuilder = Json.createArrayBuilder();
        for(String s:varStrings){
            JsonObject jso = Json.createObjectBuilder().add("var",s).build();
            varArrayBuilder.add(jso);
        }
        JsonArray varArray = varArrayBuilder.build();
        
        JsonArrayBuilder apiArrayBuilder = Json.createArrayBuilder();
        for(String s:apiList){
            JsonObject jso = Json.createObjectBuilder().add("api",s).build();
            apiArrayBuilder.add(jso);
        }
        JsonArray apiArray = apiArrayBuilder.build();
        
        JsonArrayBuilder intArrayBuilder = Json.createArrayBuilder();
        for(String s:interfaceList){
            JsonObject jso = Json.createObjectBuilder().add("interface",s).build();
            intArrayBuilder.add(jso);
        }
        
        JsonArray intArray = intArrayBuilder.build();
        
        //put all of the parts of the class into an object
        JsonObject jso = Json.createObjectBuilder()
                .add("x",x)
                .add("y",y)
                .add("name",className)
                .add("isAbstract",isAbstract)
                .add("methods",methodArray)
                .add("variables",varArray)
                .add("hasAPI",hasAPI)
                .add("APIs",apiArray)
                .add("hasParent",hasParent)
                .add("Parent",parentName)
                .add("hasInterface",hasInterface)
                .add("Interfaces",intArray)
                .build();
        
        return jso;
    }
    
    /**
     * Provides a JsonObject representation of the given DraggableInterface
     * 
     * @param di
     * The DraggableInterface itself
     * 
     * @param x
     * The x position of the interface in the workspace
     * 
     * @param y
     * The y position of the interface in the workspace
     * 
     * @return
     * A JsonObject representation of the interface made by the user
     */
    private JsonObject makeDraggableInterfaceJsonObject(DraggableInterface di, 
            double x, double y){
        
        Label nameLabel = (Label)di.getNameBox().getChildren().get(0);
        String intName = nameLabel.getText();
        intName = intName.replaceAll("<<","");
        intName = intName.replaceAll(">>","");
        
        
        
        boolean isAbstract = di.getIsAbstract();
        
        //get the string representation of the methods and variables
        ArrayList<String> methodStrings = new ArrayList();
        ArrayList<Method> methodList = di.getMethodList();
        ArrayList<String> varStrings = new ArrayList();
        ArrayList<Var> varList = di.getVariableList();
        for(Method m:methodList){
            methodStrings.add(m.toString());
        }
        for(Var v:varList){
            varStrings.add(v.toString());
        }
        
        boolean hasAPI = di.hasAPIPane();
        boolean hasParent = di.hasParent();
        StackPane apiPane;
        ArrayList<String> apiList = new ArrayList();
        ArrayList<StackPane> parentPanes = new ArrayList();
        ArrayList<String> parentNames = new ArrayList();
        ArrayList<String> childList = new ArrayList();
        
        
        if(hasAPI){
            apiPane = di.getAPIPane();
            VBox v = (VBox)apiPane.getChildren().get(1);
            for(Object o:v.getChildren()){
                Label apiLabel = (Label)o;
                apiList.add(apiLabel.getText());
            }
        }
        if(hasParent){
            parentPanes = di.getParentPanes();
            for(StackPane s:parentPanes){
                DraggableInterface parentDI = (DraggableInterface)
                        s.getChildren().get(0);
                Label tempParentLabel = (Label)parentDI.getNameBox().getChildren()
                    .get(0);
                String parentName = tempParentLabel.getText();
                parentName = parentName.replaceAll("<<","");
                parentName = parentName.replaceAll(">>","");
                parentNames.add(parentName);
            }
            
        }
        
        
        
        //need to save, for each Dinterface:
        //x/y position
        //name of interface
        //if it's abstract
        //all methods
        //all variables
        //if it has api
            //if so, what apis
        //if it's a parent(s)
            //if so, a reference to parents
        //if it has children - not really
            //if so, a refernce to children - not really
        
        //method, var, etc. should be json arrays, and added to this builder
        JsonArrayBuilder methodArrayBuilder = Json.createArrayBuilder();
        for(String s:methodStrings){
            JsonObject jso = Json.createObjectBuilder().add("method",s).build();
            methodArrayBuilder.add(jso);
        }
        
        JsonArray methodArray = methodArrayBuilder.build();
        
        JsonArrayBuilder varArrayBuilder = Json.createArrayBuilder();
        for(String s:varStrings){
            JsonObject jso = Json.createObjectBuilder().add("var",s).build();
            varArrayBuilder.add(jso);
        }
        JsonArray varArray = varArrayBuilder.build();
        
        JsonArrayBuilder apiArrayBuilder = Json.createArrayBuilder();
        for(String s:apiList){
            JsonObject jso = Json.createObjectBuilder().add("api",s).build();
            apiArrayBuilder.add(jso);
        }
        JsonArray apiArray = apiArrayBuilder.build();
        
        JsonArrayBuilder parentArrayBuilder = Json.createArrayBuilder();
        for(String s:parentNames){
            JsonObject jso = Json.createObjectBuilder().add("Parent",s).build();
            parentArrayBuilder.add(jso);
        }
        JsonArray parentArray = parentArrayBuilder.build();
        //put all interface information into 1 object    
        JsonObject jso = Json.createObjectBuilder()
                .add("x",x)
                .add("y",y)
                .add("name",intName)
                .add("isAbstract",isAbstract)
                .add("methods",methodArray)
                .add("variables",varArray)
                .add("hasAPI",hasAPI)
                .add("APIs",apiArray)
                .add("hasParent",hasParent)
                .add("Parents",parentArray)
                .build();
        
        return jso;
    }

    @Override
    public void loadData(AppDataComponent data, String filePath) 
            throws IOException {
        
    }

    /**
     * Exports the user made classes and interfaces into functioning Java files 
     * in the designated file path.
     * 
     * @param data
     * The app information
     * 
     * @param filePath
     * Location of the files
     * 
     * @throws IOException 
     * Thrown if the program doesn't have permission to write to the given file 
     * path
     */
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
                
                //[.] == string literal of period
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
