# expenseTracker
Epic expense tracker application for Capgemini.

## Build instructions:
1. Clone the repository.
2. ```make``` with the provided ```Makefile``` (yes I'm using Make with Java, sue me).
3. The result of the build process is ```expenseTracker.jar```.
4. Clean up the compiled binaries with ```make clean``` once you're done with evaluation.

## Usage:
- ```java -jar expenseTracker.jar```
- The program is fully interactive.
- While running, it stores data to ```expenses.csv``` in the current directory.

## Dependencies:
1. At least OpenJDK 17. This project was made with OpenJDK 24. You'll need the ```java```, ```javac```, and ```jar``` command line utilities.
2. GNU ```make```. I don't know which version, but I'm sure anything from the last five years will work.