# UMLCreator
A JavaFX application based on the SBU CSE 219 Spring 2016 course project using the given libraries (PropertiesManager, SimpleAppFramework, XMLUtilities, javax-json-1.0.4) from Professor Richard McKenna(https://www.cs.stonybrook.edu/people/faculty/RichardMcKenna) that creates fully functional UML diagrams.

This repository is a remastering of my older version of the project to bring the code quality up to my current standards. 

# Program functions:
- Launching a new project
- Adding new classes and interfaces
- Moving classes and interfaces, as well as their API panes and inhreitance/implementation lines
- Toggleable grid in the background
- Ability to snap to aforementioned grid, even if the grid is not displayed
- Removing classes and interface
- Adding methods and variables to classes and interfaces
- Editing aspects variables and methods in classes such as the visibility and if they are static
- Updating display depending on the currently selected class or interface
- Removing variables and methods added by the user
- Taking screenshots of UML diagram
- Automatically adding a pane to indicate what external libraries are potentially necessary
- Exporting UML diagram to compiling Java code
- Saving to a JSON file
- Loading from a JSON file
- Zooming in and out

# Program functions to include:
- Resizing classes and interfaces

# Program functions to consider adding:
- Undo and redo. These may be added if I can find a good exteranl library to handle the functionality.






This is the new layout, with icons provided by feather.netlify (https://feather.netlify.com/) (updated 2018-06-07):
![newer layout 2018-06-07](https://user-images.githubusercontent.com/32882792/41116346-0782ca04-6a58-11e8-82fd-55a84a7cf1e8.PNG)



This is an example of the grid functionality and class method/variable editing, captured on 2018-06-02:
![updated project 2018-06-02](https://user-images.githubusercontent.com/32882792/40878986-435cbf98-6667-11e8-93a4-711beac21414.PNG)

This is how variable and method API requirements will be handled in the current version, as of 2018-06-03:
![updated project 2018-06-03](https://user-images.githubusercontent.com/32882792/40889319-9c0a9b44-6732-11e8-9df6-e0f167dcd293.PNG)

This is an example of interfaces having the same method/variable editing functionality as classes, captured on 2018-06-07:
![updated project 2018-06-07-2](https://user-images.githubusercontent.com/32882792/41116250-be2edb36-6a57-11e8-87e8-2c904ddef9ca.PNG)

This is how inheritance and implementation is displayed, as of 2018-06-07. Note that in this example, Class A extends Class B, which extends class C. InterfaceA extends both InterfaceB and InterfaceC. Class C implements all of the interfaces shown. 
![updated project 2018-06-07](https://user-images.githubusercontent.com/32882792/41116104-5faae348-6a57-11e8-8e8c-a19d58bad1b6.PNG)

Note that the "<<" and ">>" are how UML indicates an interface, and will not be part of the itnerface's names when exported.

The lines showing inheritance and implementation are the same in layout, where the child/class has the line starting from the top-center of the pane, and the other end of the line is in the bottom-middle of the parent class/interface. This is acceptable because no class can extend an interface, which removes the initial amibiguity in how it looks. This may change in the future to be more obvious.

