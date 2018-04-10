
package umlcreator.gui;

import java.util.ArrayList;

/**
 *
 * @author Vincent Cramer
 */
public class Method {
    private String visibility;
    private String name;
    private String returnType;
    private boolean isStatic;
    private boolean isAbstract;
    
    private ArrayList<String> argument;
    
    //we assume that each argument is an int
    public static final String ARGUMENT_TYPE = "int";
    
    //we'll use this when listing each argument in a method
    public static final String ARGUMENT_PREFIX = "arg";
    
    /**
     * Constructor for new methods in the selected class or interface. This will
     * be called when the "+" button is pressed in the method section, and will 
     * always default to these values.
     */
    public Method(){
        visibility = "public";
        name = "myMethod";
        returnType = "void";
        isStatic = false;
        isAbstract = false;
        argument = new ArrayList();
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
        if(visibility.equals("private")){
            s+="-";
        }
        else if(visibility.equals("protected")){
            s+="#";
        }
        else{
            s+="+";
        }
        
        //add static symbol if necessary
        if(isStatic){
            s+="$";
        }
        
        s+=name;
        
        s+="(";
        
        if(argument.isEmpty()){
            //do nothing
        }
        
        else{
            for(int i=0;i<argument.size();i++){
                s+=ARGUMENT_PREFIX + (i+1) + ": " + argument.get(i) + ", ";
            }
            //remove the extra comma and space at the end
            s=s.substring(0,s.length()-2);
        }
        
        s+="): "+ returnType;
        
        if(isAbstract){
            s+=" {abstract}";
        }
        
        return s;
    }
}
