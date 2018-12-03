Colden
======

Colden is a compression application that uses a Huffman tree to make files
smaller, losslessly.

Detailed guide
--------------

There are two steps before the application can open: compilation and execution.
After Colden is open, see directions below for use

### Compiling Colden ###

To compile Colden, navigate to the source directory and run `javac *.java`.
This will compile every java file in the directory into class files, allowing
them to be run.

### Running Colden ###

Once you've compiled the source, run Colden by executing `java Main` in the
source directory. This will open Colden's main window.

### Using Colden ###

After Colden is open, you will be greeted two tabs, "Compress" and "Expand,"
corresponding to the action you want to take.

For compression, navigate to the "Compress" tab by clicking on it. Click the 
"Select input file" button, which will open a dialog to choose a file. Select
any individual file from the filesystem. Next, click the "Select output file"
button. This will open a save dialog, allowing you to select a new file to be
created once Colden is run. After both the input and output file locations are
selected, click "Run Colden" to compress the input file and save it to the
output location.

For expansion, navigate to the "Expand" tab and follow the same instructions as
the "Compress" tab. This time, however, instead of compressing the input file,
Colden will expand a compressed file given to it. Note, however, that Colden
only works with files made by Colden. It cannot expand compression formats other
than its own.