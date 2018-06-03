
package umlcreator.file;

import java.io.IOException;
import saf.components.AppDataComponent;
import saf.components.AppFileComponent;

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
    public void saveData(AppDataComponent data, String filePath) throws IOException {
        
    }

    @Override
    public void loadData(AppDataComponent data, String filePath) throws IOException {
        
    }

    @Override
    public void exportData(AppDataComponent data, String filePath) throws IOException {
        
    }

    @Override
    public void importData(AppDataComponent data, String filePath) throws IOException {
        
    }
    
}
