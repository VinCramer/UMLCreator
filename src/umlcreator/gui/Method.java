
package umlcreator.gui;

import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author Vincent Cramer
 */
public class Method {

    public static String getARGUMENT_TYPE() {
        return ARGUMENT_TYPE;
    }

    public static void setARGUMENT_TYPE(String ARGUMENT_TYPE) {
        ARGUMENT_TYPE = ARGUMENT_TYPE;
    }

    public static String getARGUMENT_PREFIX() {
        return ARGUMENT_PREFIX;
    }

    public static void setARGUMENT_PREFIX(String ARGUMENT_PREFIX) {
        ARGUMENT_PREFIX = ARGUMENT_PREFIX;
    }
    private String visibility;
    private String name;
    private String returnType;
    private BooleanProperty isStatic;
    private BooleanProperty isAbstract;
    
    private ArrayList<String> arguments;
    
    //we assume that each argument is an int
    private static String ARGUMENT_TYPE = "int";
    
    //we'll use this when listing each argument in a method
    private static String ARGUMENT_PREFIX = "arg";
    
    /**
     * Constructor for new methods in the selected class or interface. This will
     * be called when the "+" button is pressed in the method section, and will 
     * always default to these values.
     */
    public Method(){
        visibility = "public";
        name = "myMethod";
        returnType = "void";
        isStatic = new SimpleBooleanProperty(false);
        isAbstract = new SimpleBooleanProperty(false);
        arguments = new ArrayList();
    }
    
    /**
     * Returns a String representation of the method, which will be displayed 
     * in the class or interface selected in the UML
     * 
     * @return
     * Properly formatted String representing the method
     * 
     */
    public String toString(){
        
        //generic String to add info to
        String s = "";
        
        //We put a + if the method is public, # if protected, - if private. We 
        //default to public incase the user enters some other String
        if(getVisibility().equals("private")){
            s+="-";
        }
        else if(getVisibility().equals("protected")){
            s+="#";
        }
        else{
            s+="+";
        }
        
        //add static symbol if necessary
        if(isIsStatic()){
            s+="$";
        }
        
        s+=getName();
        
        s+="(";
        
        
        
        if(!arguments.isEmpty()){
            for(int i=0;i<getArguments().size();i++){
                s+=getARGUMENT_PREFIX() + (i+1) + ": " + getArguments().get(i) + ", ";
            }
            //remove the extra comma and space at the end
            s=s.substring(0,s.length()-2);
        }
        
        s+="): "+ getReturnType();
        
        if(isIsAbstract()){
            s+=" {abstract}";
        }
        
        return s;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public boolean isIsStatic() {
        return isStatic.get();
    }

    public void setIsStatic(boolean isStatic) {
        this.isStatic.set(isStatic);
    }

    public boolean isIsAbstract() {
        return isAbstract.get();
    }

    public void setIsAbstract(boolean isAbstract) {
        this.isAbstract.set(isAbstract);
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }

    public void setArguments(ArrayList<String> arguments) {
        this.arguments = arguments;
    }
    
    public BooleanProperty getIsAbstractBooleanProperty(){
        return isAbstract;
    }
    
    public BooleanProperty getIsStaticBooleanProperty(){
        return isStatic;
    }

    /**
     * This method set the argument type at the given index. Additionally, if 
     * the user enters null or the empty string, we interpret that to mean that 
     * the user wants to remove that argument, so we remove that value from our 
     * list.
     * 
     * @param pos
     * Position of argument to be set or removed
     * 
     * @param type 
     * Type of argument
     */
    public void setArgType(int pos, String type){
        if(type.equals("") || type == null){
            arguments.remove(pos);
        }
        else{
            arguments.set(pos,type);
        }
        
    }
    
    public void addArgument(String newArg){
        arguments.add(newArg);
    }
    
}
