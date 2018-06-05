
package umlcreator.data;

import java.util.ArrayList;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
    
    //see if we need these lists - may be able to update by removing all 
    //children from their repsective HBoxes, and then re-adding labels based on 
    //their method/var lists
    private ArrayList<Label> methodLabelList;
    private ArrayList<Label> variableLabelList;
    
    public double startX, startY;
    
    private String packageName;
    
    private StackPane apiPane;
    private Line apiLine;
    
    private StackPane parentPane;
    private Line parentLine;
    
    private StackPane childPane;
    
    
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
          
        childPane = null;
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
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight) {
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
        return childPane!=null;
    }
    
    public void setChildPane(StackPane sp){
        childPane = sp;
    }
    
    public StackPane getChildPane(){
        return childPane;
    }
    
}
