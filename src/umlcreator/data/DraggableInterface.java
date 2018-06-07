
package umlcreator.data;

import java.util.ArrayList;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import static umlcreator.data.UMLCreatorState.STARTING_INTERFACE;
import umlcreator.gui.Method;
import umlcreator.gui.Var;

/**
 *
 * @author Vincent Cramer
 */
public class DraggableInterface extends Rectangle implements Draggable{
    
    private double startX, startY;
    
    private StackPane stackPane, apiPane;
    
    private Label nameLabel;
    private String nameString, packageName;
    
    private ArrayList<Method> methodList; 
    private ArrayList<Var> variableList;
    
    private ArrayList<Label> methodLabelList;
    private ArrayList<Label> variableLabelList;
    
    private Line apiLine;
    private ArrayList<Line> parentLines;
    private ArrayList<StackPane> parentPanes, childPanes;
    
    private ArrayList<StackPane> classPanes;
    
    private VBox methodBox, varBox, nameBox, holderBox;
    
    
    public DraggableInterface(){
        methodBox = new VBox();
        varBox = new VBox();
        nameBox = new VBox();
        holderBox = new VBox();
        
        
        setX(100);
        setY(100);
        
        
        setWidth(100);
        setHeight(100);
        
        startX=100;
        startY=100;
    
        methodList = new ArrayList();
        variableList = new ArrayList();
        methodLabelList = new ArrayList();
        variableLabelList = new ArrayList();
        
        this.setFill(Color.WHITE);
        
        packageName="";
        
        apiPane = null;
        apiLine = null;
        
        parentLines = new ArrayList();
        childPanes = new ArrayList();
        parentPanes = new ArrayList();
        classPanes = new ArrayList();
    }

    @Override
    public UMLCreatorState getStartingState() {
        return STARTING_INTERFACE;
    }

    @Override
    public void start(int x, int y) {
        setStartX(x);
        setStartY(y);
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
	setStartX(x);
	setStartY(y);
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
        return INTERFACE;
    }
    
    public void addVar(Var v){
        variableList.add(v);
    }
    
    public void addMethod(Method m){
        methodList.add(m);
    }
    
    public Var getVar(int index){
        return variableList.get(index);
    }
    
    public Method getMethod(int index){
        return methodList.get(index);
    }
    
    public Label getVarLabel(int index){
        return variableLabelList.get(index);
    }
    
    public Label getMethodLabel(int index){
        return methodLabelList.get(index);
    }
    
    public void addVarLabel(Label l){
        variableLabelList.add(l);
    }
    
    public void addMethodLabel(Label l){
        methodLabelList.add(l);
    }
    
    public void setVariable(int x, Var v){
        variableList.set(x, v);
    }
    
    public void setMethod(int x, Method m){
        methodList.set(x,m);
    }
    
    public void setVariableLabel(int x, Label l){
        variableLabelList.set(x, l);
        varBox.getChildren().set(x, l);
    }
    
    public void setMethodLabel(int x, Label l){
        methodLabelList.set(x, l);
        methodBox.getChildren().set(x, l);
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
    
    public boolean hasParent(){
        return parentPanes.size()>0;
    }
    
    public boolean hasChild(){
        return childPanes.size()>0;
    }
    
    public boolean hasClass(){
        return classPanes.size()>0;
    }
    
    public ArrayList<StackPane> getClassPanes(){
        return classPanes;
    }
    
    public void addClassPane(StackPane sp){
        classPanes.add(sp);
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public ArrayList<StackPane> getParentPanes() {
        return parentPanes;
    }

    

    public StackPane getStackPane() {
        return stackPane;
    }

    public void setStackPane(StackPane stackPane) {
        this.stackPane = stackPane;
    }

    public StackPane getAPIPane() {
        return apiPane;
    }

    public void setAPIPane(StackPane apiPane) {
        this.apiPane = apiPane;
    }

    public ArrayList<StackPane> getChildPanes() {
        return childPanes;
    }

    

    public Label getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(Label nameLabel) {
        this.nameLabel = nameLabel;
    }

    public String getNameString() {
        return nameString;
    }

    public void setNameString(String nameString) {
        this.nameString = nameString;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public Line getAPILine() {
        return apiLine;
    }

    public void setAPILine(Line apiLine) {
        this.apiLine = apiLine;
    }

    public ArrayList<Line> getParentLines() {
        return parentLines;
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
    
    public void addParentPane(StackPane parent){
        parentPanes.add(parent);
    }
    
    public void addParentLine(Line l){
        parentLines.add(l);
    }
    
    public void addChildPane(StackPane child){
        childPanes.add(child);
    }
    
    public void removeChildPane(StackPane child){
        childPanes.remove(child);
    }
    
    public void removeParentLine(Line l){
        parentLines.remove(l);
    }

    public void removeClassPane(StackPane p){
        classPanes.remove(p);
    }
    
    public void removeParentPane(StackPane sp){
        parentPanes.remove(sp);
    }
    
}
