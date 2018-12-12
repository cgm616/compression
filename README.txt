Colden
======

Colden is a compression application that uses a Huffman tree to make files
smaller, losslessly.

Detailed guide
--------------

There are two steps before the application can open: compilation and execution.
After Colden is open, see directions below for use

### Compiling Colden ###

To compile Colden, navigate to the source directory and run 

```
> javac *.java
```

This will compile every java file in the directory into class files, allowing
them to be run.

### Running Colden ###

Once you've compiled the source, run Colden by executing 

```
> java Main
```

in the source directory. This will open Colden's main window.

### Using Colden ###

After Colden is open, you will be greeted two tabs, "Compress" and "Expand,"
corresponding to the action you want to take.

For compression, navigate to the "Compress" tab by clicking on it. Click the 
"Choose file" button next to "Input," which will open a dialog to choose a file.
Select any individual file from the filesystem. Next, click the "Choose file"
button next to "Output." This will open a save dialog, allowing you to select a
new file to be created once Colden is run. After both the input and output file
locations are selected, click "Run Colden" to compress the input file and save
it to the output location. The logs and any errors from the operation will
appear in the text box.

For expansion, navigate to the "Expand" tab and follow the same instructions as
the "Compress" tab. This time, however, instead of compressing the input file,
Colden will expand a compressed file given to it. Note, however, that Colden
only works with files made by Colden. It cannot expand compression formats other
than its own.

During both expansion and compression, select the checkbox next to "Save Huffman
Tree Graph" to output a pdf file displaying the Huffman tree used during program
operation. To set where the file is saved, click the button "Choose file" under
the checkbox. Note that in order for output to work, the `dot` command (part of
graphviz) must be installed and in the path.

To clear any selected file, click the "Clear" button next to that file's path.