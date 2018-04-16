
package umlcreator.data;

/**
 * This interface will be used for dragging our classes and interfaces
 * @author Vincent Cramer
 */
public interface Draggable {
    
    public static final String CLASS = "CLASS";
    public static final String INTERFACE = "INTERFACE";
    
    
    public UMLCreatorState getStartingState();
   
   //methods for moving and resizing files
   public void start(int x, int y);
   public void drag(int x, int y);
   public void resize(int x, int y);
   public double getX();
   public double getY();
   public double getWidth();
   public double getHeight();
   public void setLocationAndSize(double initX, double initY, double initWidth, 
           double initHeight);
   
   public String getFileType();
}
