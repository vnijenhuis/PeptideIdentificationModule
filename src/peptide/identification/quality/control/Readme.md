* Tool designed to read protein-peptides.csv files from either PEAKS or MzIdConvert. *

The PeptideIdentificationModule functions as a peptide database identification tool.
This tool matches peptide sequences to public databases (if they are provided) and matches peptide sequences
to their original protein sequence database fasta file to ensure that they are present in the sample database.
After this process the spectra count and score of each sample is written in to a matrix-like setup.
The output file is a CSV file containing this matrix. Each provided dataset is written into its own output file.


How to run the tool:

  1. ```java -jar PeptideIdentificationModule.jar -help```  
    shows help/usage information 
  2. ```java -Xms2G -Xmx16G -jar PeptideIdentificationModule.jar -proteinPeptides C:\Users\User1\Input\protein-peptide\main.txt -publicDatabases C:\Users\User1\Input\public_database\main.txt -referenceDatabases C:\Users\User1\Input\reference_database\main.txt -output C:\Users\User1\PeptideIdentOutput\ -removeEnsemblHits F -threads 24```  
    Runs the PeptideIdentificationModule tool using the command line interface.
    The input requires a specific setup for the main text file which can be found in the ..\Data\PeptideIdentificationModule.pdf
  3. ```nohup java -Xms2G -Xmx16G -jar PeptideIdentificationModule.jar -proteinPeptides C:\Users\User1\Input\protein-peptide\main.txt -publicDatabases C:\Users\User1\Input\public_database\main.txt -referenceDatabases C:\Users\User1\Input\reference_database\main.txt -output C:\Users\User1\PeptideIdentOutput\ -removeEnsemblHits F -threads 24 > cmd_output.out &```
    Runs the PeptideIdentificationModule tool using the command line interface, but writes command line inteface output to a nohup.out file.
    This allows the command line interface to be used for other purposes, or allows the command line interface to be closed.
    Output of the tool itself is still written to the given output directory.


Explanation of arguments:

  1. ```java -jar PeptideIdentificationModule.jar -help```  
    Shows help/usage information 
  2. ```nohup "arguments" > cmd_output.out &```  
    Nohup allows a command line argument to run in the background, so that the current interface can either be closed or used
    for other purposes. The command starts with nohup, then the given tool and arguments are written.
    After this step the  "> cmd_output.out &" line is written behind the last argument. This line allows data to be written to
    the cmd_output.out file and allows the commandline to be used for other purposes.
  3. ```java -Xms1G -Xmx3G -jar FindIonSeries.jar```
    Executes the provided jar file while trying to reserve a minimum and maximum amount of memory.
    Providing memory is essential for processing large data sets. 
  4. ```-proteinPeptides C:\Users\User1\Documents\Entry\1D50CM\protein_peptides_main.txt```
    Reads a text file linking to other text files that contain file paths to MzIdentML files.
    Look at the \Data\PeptideIdentificationModule.pdf file that is present in this project folder for the correct format.
  5. ```-publicDatabases C:\Users\User1\Documents\Entry\1D50CM\public_database\main.txt```
    Requires a text file as input. This text file contains file paths to public protein sequence fasta files.
    Public databases are databases that consist already known protein isoforms (Uniprot, Ensembl etc.).
    Look at the \Data\PeptideIdentificationModule.pdf file that is present in this project folder for the correct format.
  6. ```-referenceDatabases C:\Users\User1\Documents\Entry\1D50CM\reference_database\main.txt```
    Requires a text file as input. This text file contains file paths to reference protein sequence fasta files.
    Reference databases are the protein sequence fasta files of each sample that was processed.
    Look at the \Data\PeptideIdentificationModule.pdf file that is present in this project folder for the correct format.
  7. ```-output C:\Users\Vikthor\Documents\DatabaseIdentOutput\```
    File path to the output directory.
  8. ```-threads 8```
    * Optional Argument *
    Optional argument to set the amount of threads used. No argument: default threads is set at 2 threads.
    Execute the tool using 8 threads. Some parts of this tool are multithreaded which allows for faster processing.
    The 8 can be replaced by any number, but please check the available amount of threads and take other tasks into consideration.
  9. ```-removeEnsemblHits F```
     ```-removeEnsemblHits T```
    Using T (True) removes the peptide sequences that have an accession id starting with ENST.
    Using F (False) keeps the peptide sequences that have an accession id starting with ENST.
    Removing these accession ids can be usefull when only interested in new protein sequences.
