
package umlcreator.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import umlcreator.file.FileManager;

/**
 *
 * @author Vincent Cramer
 */
public class Var {
    private String visibility;
    private BooleanProperty isStatic;
    private String type;
    private BooleanProperty isFinal;
    private String name;
    
    /**
     * Constructor for new variables in the selected class or interface. This 
     * will be called when the "+" button is pressed in the variable section, 
     * and will always default to these values.
     */
    public Var(){
        name = "myVar";
        visibility = "public";
        type = "int";
        isFinal = new SimpleBooleanProperty(false);
        isStatic = new SimpleBooleanProperty(false);
    }
    
    /**
     * Returns a String representation of the variable, which will be displayed 
     * in the class or interface selected in the UML
     * 
     * @return
     * Properly formatted String representing the variable
     */
    @Override
    public String toString(){
        
        //generic empty String to add info to
        String s = "";
        
        //provide the proper symbol depending on visibility: + for public, # for
        //protected, - for private. We will default to public in case the user 
        //enters some other type of visibility not recognized.
        if(getVisibility().equals("private")){
            s+="-";
        }
        else if(getVisibility().equals("protected")){
            s+="#";
        }
        else{
            s+="+";
        }
        
        //add the static symbol if necessary
        if(isStatic.get()){
            s+="$";
        }
        
        //if field is private, proper coding style says the name should be 
        //capitalized
        if(getIsFinal()){
            s+=getName().toUpperCase();
        }
        else{
            s+=getName();
        }
        
        //add the other information
        s+=": " + getType();
        
        return s;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        if(visibility.equals("protected") || visibility.equals("private") || 
                visibility.equals("public")){
            this.visibility = visibility;
        }
    }

    public boolean getIsStatic() {
        return isStatic.get();
    }

    public void setIsStatic(boolean isStatic) {
        this.isStatic.set(isStatic);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    

    public void setIsFinal(boolean isFinal) {
        this.isFinal.set(isFinal);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method returns an observable boolean value that corresponds to the 
     * isStatic field. This method is used in the updateComponentToolbar 
     * function in workspace, which relies on this cumbersome API. This method 
     * servers to simplify things somewhat.
     * 
     * @return 
     * True observable value if it is static, false otherwise
     */
    public ObservableValue<Boolean> getIsStaticObservableValue() {
        return isStatic;
    }
    
    /**
     * This method returns an observable boolean value that corresponds to the 
     * isFinal field. This method is used in the updateComponentToolbar 
     * function in workspace, which relies on this cumbersome API. This method 
     * servers to simplify things somewhat.
     * 
     * @return 
     * True observable value if it is final, false otherwise
     */
    public ObservableValue<Boolean> getIsFinalObservableValue(){
        ObservableValue<Boolean> falseBool = new SimpleBooleanProperty(false);
        ObservableValue<Boolean> trueBool = new SimpleBooleanProperty(false);
        if(isFinal.get()){
            return trueBool;
        }
        return falseBool;
    }
    
    public boolean getIsFinal(){
        return isFinal.get();
    }
    
    
    public BooleanProperty getIsStaticBooleanProperty(){
        return isStatic;
    }
    
    public BooleanProperty getIsFinalBooleanProperty(){
        return isFinal;
    }
    
    /**
     * Provides a formatted String that will be used by the FileManager when 
     * exporting to functional Java code. Since Java practice dictates that only
     * final variables are valid in interfaces, we'll assume the user knows this
     * and won't need 2 methods for class and interface.
     * 
     * @return 
     * Formatted String representation of the Var object just like a field in a 
     * Java class or interface.
     */
    public String toExportString(){
       String s = "";
       s+=visibility+" ";
       if(isStatic.get()){
           s+=" static " ;
       }
       if(isFinal.get()){
           s+=" final ";
       }
       s+=type + " " + name;
       
       //only assign value if it's final
       if(isFinal.get()){
           s+=" = ";
           if(type.equals("int")){
               s+=FileManager.DUMMY_INT+";";
           }
           else if(type.equals("double")){
               s+=FileManager.DUMMY_DOUBLE+";";
           }
           else if(type.equals("boolean")){
               s+=FileManager.DUMMY_BOOLEAN+";";
           }
           else if(type.equals("char")){
               s+=FileManager.DUMMY_CHAR+";";
           }
           else if(type.equals("long")){
               s+=FileManager.DUMMY_LONG+";";
           }
           else if(type.equals("byte")){
               s+=FileManager.DUMMY_BYTE+";";
           }
           else if(type.equals("String")){
               s+=FileManager.DUMMY_STRING+";";
           }
           else{
               s+="new " + type + "();";
           }
       }
       else{
           s+=";";
       }
       
       s+="\n";
       return s;
    }
    
    
}
