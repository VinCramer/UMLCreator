
package umlcreator.gui;

/**
 *
 * @author Vincent Cramer
 */
public class Var {
    private String visibility;
    private boolean isStatic;
    private String type;
    private boolean isFinal;
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
        isFinal = false;
        isStatic = false;
    }
    
    /**
     * Returns a String representation of the variable, which will be displayed 
     * in the class or interface selected in the UML
     * 
     * @return
     * Properly formatted String representing the variable
     */
    public String toString(){
        
        //generic empty String to add info to
        String s = "";
        
        //provide the proper symbol depending on visibility: + for public, # for
        //protected, - for private. We will default to public in case the user 
        //enters some other type of visibility not recognized.
        if(visibility.equals("private")){
            s+="-";
        }
        else if(visibility.equals("protected")){
            s+="#";
        }
        else{
            s+="+";
        }
        
        //add the static symbol if necessary
        if(isStatic){
            s+="$";
        }
        
        //if field is private, proper coding style says the name should be 
        //capitalized
        if(isFinal){
            s+=name.toUpperCase();
        }
        else{
            s+=name;
        }
        
        //add the other information
        s+=": " + type;
        
        return s;
    }
}
