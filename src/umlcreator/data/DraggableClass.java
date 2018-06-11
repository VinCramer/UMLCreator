
package umlcreator.data;

import java.util.ArrayList;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import static umlcreator.data.UMLCreatorState.STARTING_CLASS;
import umlcreator.gui.Method;
import umlcreator.gui.Var;

/**
 *
 * @author Vincent Cramer
 */
public class DraggableClass extends Rectangle implements Draggable{

    
    private VBox methodBox, varBox, nameBox,holderBox;
    
    private StackPane stackPane;
    
    private Label nameLabel;
    
    private ArrayList<Method> methodList; 
    private ArrayList<Var> variableList;
    
    
    private ArrayList<Label> methodLabelList;
    private ArrayList<Label> variableLabelList;
    
    public double startX, startY;
    
    private String packageName;
    
    private StackPane apiPane;
    private Line apiLine;
    
    private StackPane parentPane;
    private Line parentLine;
    
    private ArrayList<StackPane> childPanes;
    
    private ArrayList<StackPane> implementPanes;
    private ArrayList<Line> implementLines;
    
    private boolean isAbstract;
    
    private boolean loadHasParent, loadHasAPIPane, loadHasInterface;
    
    private String loadParentName;
    private ArrayList<String> loadInterfaceNames;
    
    
    public DraggableClass(){
        
        methodBox = new VBox();
        varBox = new VBox();
        nameBox = new VBox();
        holderBox = new VBox();
        
        
        //don't want pane to appear in the upper left of the screen, so we'll 
        //use an offset
        setX(100);
        setY(100);
        
        //magic numbers that specify a default width and height for these panes.
        //will be changed into variables later
        setWidth(100);
        setHeight(100);
        
        startX=100;
        startY=100;
    
        methodList = new ArrayList();
        variableList = new ArrayList();
        methodLabelList = new ArrayList();
        variableLabelList = new ArrayList();
        
        //prevents class from looking like black blob on user's screen
        this.setFill(Color.WHITE);
        
        packageName="";
        
        apiPane = null;
        apiLine = null;
        
        parentPane = null;
        parentLine = null;
          
        childPanes = new ArrayList();
        implementLines = new ArrayList();
        implementPanes = new ArrayList();
        
        isAbstract = false;
        loadInterfaceNames = new ArrayList();
    }
    
    /**
     * Adds a given variable to the list field of variables for future 
     * references
     * 
     * @param v
     * Variable that will be added
     */
    public void addVar(Var v){
        variableList.add(v);
    }
    
    /**
     * Adds a method to the list field to be referenced later
     * 
     * @param m 
     * Method that will be added
     */
    public void addMethod(Method m){
        methodList.add(m);
    }
    
    @Override
    public UMLCreatorState getStartingState() {
        return STARTING_CLASS;
    }

    @Override
    public void start(int x, int y) {
        startX=x;
        startY=y;
        setX(x);
        setY(y);
    }
    
    @Override
    public void drag(int x, int y) {
        
        //find the difference in position, and change location by that much
        double diffX = x - (getX() + (getWidth()/2));
	double diffY = y - (getY() + (getHeight()/2));
	double newX = getX() + diffX;
	double newY = getY() + diffY;
	xProperty().set(newX);
	yProperty().set(newY);
	startX = x;
	startY = y;
    }

    @Override
    public void resize(int x, int y) {
        double width = x - getX();
	widthProperty().set(width);
	double height = y - getY();
	heightProperty().set(height);
    }

    @Override
    public void setLocationAndSize(double initX, double initY, double initWidth,
            double initHeight) {
        xProperty().set(initX);
	yProperty().set(initY);
	widthProperty().set(initWidth);
	heightProperty().set(initHeight);
    }

    @Override
    public String getFileType() {
        return CLASS;
    }

    public VBox getMethodBox() {
        return methodBox;
    }

    public void setMethodBox(VBox methodBox) {
        this.methodBox = methodBox;
    }

    public VBox getVarBox() {
        return varBox;
    }

    public void setVarBox(VBox varBox) {
        this.varBox = varBox;
    }

    public VBox getNameBox() {
        return nameBox;
    }

    public void setNameBox(VBox nameBox) {
        this.nameBox = nameBox;
    }

    public VBox getHolderBox() {
        return holderBox;
    }

    public void setHolderBox(VBox holderBox) {
        this.holderBox = holderBox;
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public void setStackPane(StackPane stackPane) {
        this.stackPane = stackPane;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(Label nameLabel) {
        this.nameLabel = nameLabel;
    }

    public ArrayList<Method> getMethodList() {
        return methodList;
    }

    public void setMethodList(ArrayList<Method> methodList) {
        this.methodList = methodList;
    }

    public ArrayList<Var> getVariableList() {
        return variableList;
    }
    
    public Var getVar(int x){
        return variableList.get(x);
    }
    
    public Method getMethod(int x){
        return methodList.get(x);
    }
    
    public Label getVarLabel(int x){
        return variableLabelList.get(x);
    }
    
    public void addVarLabel(Label l){
        variableLabelList.add(l);
    }
    
    public void addMethodLabel(Label l){
        methodLabelList.add(l);
    }
    
    public Label getMethodLabel(int x){
        return methodLabelList.get(x);
    }

    public void setVariableList(ArrayList<Var> variableList) {
        this.variableList = variableList;
    }

    public ArrayList<Label> getMethodLabelList() {
        return methodLabelList;
    }

    public void setMethodLabelList(ArrayList<Label> methodLabelList) {
        this.methodLabelList = methodLabelList;
    }

    public ArrayList<Label> getVariableLabelList() {
        return variableLabelList;
    }

    public void setVariableLabelList(ArrayList<Label> variableLabelList) {
        this.variableLabelList = variableLabelList;
    }
    
    public void setVariable(int x, Var v){
        variableList.set(x, v);
    }
    
    public void setVariableLabel(int x, Label l){
        variableLabelList.set(x, l);
        varBox.getChildren().set(x, l);
    }
    
    public void setMethod(int x, Method m){
        methodList.set(x,m);
    }
    
    public void setMethodLabel(int x, Label l){
        methodLabelList.set(x, l);
        methodBox.getChildren().set(x, l);
    }
    
    public String getPackageName(){
        return packageName;
    }
    
    public void setPackageName(String s){
        this.packageName=s;
    }
    
    public void removeVar(Var v){
        variableList.remove(v);
    }
    
    public void removeVarLabel(int i){
        variableLabelList.remove(i);
    }
    
    public void removeMethod(Method m){
        methodList.remove(m);
    }
    
    public void removeMethodLabel(int i){
        methodLabelList.remove(i);
    }
    
    public boolean hasAPIPane(){
        return apiPane!=null;
    }
    
    public void setAPILine(Line l){
        apiLine=l;
    }
    
    public Line getAPILine(){
        return apiLine;
    }
    
    public void setAPIPane(StackPane apiPane){
        this.apiPane=apiPane;
    }
    
    public StackPane getAPIPane(){
        return apiPane;
    }
    
    public boolean hasParent(){
        return parentPane!=null;
    }
    
    public void setLoadInterfaceNames(ArrayList<String> loadInterfaceNames){
        this.loadInterfaceNames = loadInterfaceNames;
    }
    
    public ArrayList<String> getLoadInterfaceNames(){
        return loadInterfaceNames;
    }
    
    public Line getParentLine(){
        return parentLine;
    }
    
    public StackPane getParentPane(){
        return parentPane;
    }
    
    public void setParentLine(Line parentLine){
        this.parentLine=parentLine;
    }
    
    public void setParentPane(StackPane parentPane){
        this.parentPane = parentPane;
    }
    
    public boolean hasChild(){
        return childPanes.size()>0;
    }
    
    public boolean hasInterface(){
        return implementPanes.size()>0;
    }
    
    
    public ArrayList<StackPane> getChildPanes(){
        return childPanes;
    }
    
    public void addImplementLine(Line l){
        implementLines.add(l);
    }
    
    public void addImplementPane(StackPane sp){
        implementPanes.add(sp);
    }
    
    public void setLoadParentName(String loadParentName){
        this.loadParentName = loadParentName;
    }
    
    public String getLoadParentName(){
        return loadParentName;
    }
    
    public void addChildPane(StackPane child){
        childPanes.add(child);
    }
    
    public void removeChildPane(StackPane sp){
        childPanes.remove(sp);
    }
    
    public void removeParentPane(){
        parentPane=null;
        parentLine = new Line();
    }
    
    public ArrayList<StackPane> getImplementPanes(){
        return implementPanes;
    }
    
    public ArrayList<Line> getImplementLines(){
        return implementLines;
    }
    
    public void removeImplementPane(StackPane sp){
        implementPanes.remove(sp);
    }
    
    public boolean getIsAbstract(){
        return isAbstract;
    }
    
    public void setIsAbstract(boolean isAbstract){
        this.isAbstract=isAbstract;
    }
    
    public void setLoadHasAPIPane(boolean loadHasAPIPane){
        this.loadHasAPIPane = loadHasAPIPane;
    }
    
    public boolean getLoadHasAPIPane(){
        return loadHasAPIPane;
    }
    
    public void setLoadHasParent(boolean loadHasParent){
        this.loadHasParent = loadHasParent;
    }
    
    public boolean getLoadHasParent(){
        return loadHasParent;
    }
    
    public void setLoadHasInterface(boolean loadHasInterface){
        this.loadHasInterface = loadHasInterface;
    }
    
    /**
     * Returns a formatted String of the entire class, including its variables 
     * and methods
     * 
     * @return 
     * A formatted String of the entire class
     */
    public String toExportString(){
        String s = "";
        
        if(!packageName.equals("")){
            s+="package " + packageName + ";\n";
        }
        
        if(hasAPIPane()){
            VBox v = (VBox)apiPane.getChildren().get(1);
            for(int i=0;i<v.getChildren().size();i++){
                
                
                Label apiLabel = (Label)v.getChildren().get(i);
                
                String api = apiLabel.getText();
                
                //use this library to get the full name of the Java library, if 
                //the user entered a valid name
                Package[] pack = Package.getPackages();
                
                for(Package p:pack){
                    try{
                        Class cl = Class.forName(p.getName()+"."+api);
                        s+="import "+cl.getName()+";\n";
                    }
                    catch(ClassNotFoundException c){
                        
                    }
                }
                
            }
            
            
        }
        
        //if parent isn't in same package, need to import it
        if(hasParent()){
            String parentPackage, parentName;
            DraggableClass parentDC = (DraggableClass)
                    parentPane.getChildren().get(0);
            parentPackage = parentDC.getPackageName();
            Label tempParentLabel = (Label)parentDC.getNameBox().getChildren().get(0);
            parentName = tempParentLabel.getText();
            if(!parentPackage.equals(packageName)){
                s+="import " + parentPackage + "." + parentName + "\n";
            }
        }
        
        //if interface isn't in same package, need to import it
        if(hasInterface()){
            for(StackPane intPane: implementPanes){
                DraggableInterface di = (DraggableInterface)
                        intPane.getChildren().get(0);
                String intPackage = di.getPackageName();
                Label iLabel = di.getNameLabel();
                String intName = iLabel.getText();
                intName = intName.replaceAll("<<","");
                intName = intName.replaceAll(">>","");
                if(!intPackage.equals(packageName)){
                    s+="import " + intPackage + "." + intName + ";\n";
                }
            }
        }
        
        s+="public ";
        if(nameBox.getChildren().size()==2){
            s+="abstract ";
        }
        
        s+="class " + ((Label)(nameBox.getChildren().get(0))).getText();
        
        if(hasParent()){
            s+=" extends ";
            DraggableClass parent = (DraggableClass)
                getParentPane().getChildren().get(0);
            Label temp = (Label)parent.getNameBox().getChildren().get(0); 
            s+= temp.getText() + " ";
        }
        
        if(hasInterface()){
            s+=" implements ";
            for(StackPane sp:getImplementPanes()){
                DraggableInterface di = (DraggableInterface)
                    sp.getChildren().get(0);
                String intName = ((Label)(di.getNameBox().getChildren().get(0)))
                    .getText();
                intName = intName.replaceAll("<<","");
                intName = intName.replaceAll(">>","");
                s+=intName + ", ";
            }
            //remove last comma
            s=s.substring(0,s.length()-2);
            s+=" ";
        }
        
        s+="{\n";
        
        for(Var v: variableList){
            s+=v.toExportString();
        }
        
        for(Method m:methodList){
            if(nameBox.getChildren().size()==2 && m.getIsAbstract()){
                s+=m.toExportStringInterfaceAndAbstractMethod();
            }
            else{
                s+=m.toExportStringClassAndStaticInterface();
            }
            
        }
        
        //need to add actual implementation of interface methods if it's not 
        //abstract
        if(hasInterface() && nameBox.getChildren().size()!=2){
            for(StackPane sp:getImplementPanes()){
                DraggableInterface di = (DraggableInterface)
                    sp.getChildren().get(0);
                for(Method m:di.getMethodList()){
                    s+=m.toExportStringClassAndStaticInterface();
                }
            }
        }
        
        //need to add implementation of abstract methods from parent if 
        //necessary
        if(hasParent() && nameBox.getChildren().size()!=2){
            DraggableClass dc = (DraggableClass)
                getParentPane().getChildren().get(0);
            //if parent is abstract - already know child isn't
            if(dc.getNameBox().getChildren().size()==2){
                for(Method m:dc.getMethodList()){
                    if(m.getIsAbstract()){
                        s+=m.toExportStringClassAndStaticInterface();
                    }
                }
            }
        }
        
        s+="\n}\n";
        
        return s;
    }
    
    
    
}
