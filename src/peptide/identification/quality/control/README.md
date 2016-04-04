================================================
	    IDENTIFICATION GUIDE 
================================================
Before you start the identification process in the commandline
a quick check of file names and locations is needed.
Be sure of the following:

- /home/LundAnalysis/1D50CM/ should contain folders with sample names.
- Each sample folder should contain a protein-peptides.csv and a DB search psm.csv.
- Each sample has a number: COPD1, Control10 etc.
- The COPDHealthy-database.fasta files should atleast contain a sample name of the matching sample:
example 1:	Control_1_database.fasta for Control1
example 2:	Control1_database.fasta for Control1

Some steps are hard-coded inside the program itself:
Changing the samples names will result in errors unless the appropriate code is added.

================================================
	    COMMANDLINE INFORMATION
================================================
There are a multitude of commandline options that require a varying amount of input.
-help		Displays the help function.
-in		Path to one or more datasets which should be used.
-psm		Name of the psm file. In this case it is most likely called DB search psm.csv
-pp		Name of the protein-peptide relation file, which is most likely called protein-peptides.csv
-db		Path to a protein database file. The uniprot taxonomy fasta file is recommended.
-cdb		Path to the combined fasta file. This is a fasta file with combined protein data of the samples.
-idb		Path to the folder with indvidiual fasta files of each sample.
-out		Path to the output file where the data will be written. Please let the file end with .csv
-threads	Amount of threads used for multithreading. The default setting is 2 threads.


vikthor@fedot13:~/java> lscpu
Architecture:          x86_64
CPU op-mode(s):        32-bit, 64-bit
Byte Order:            Little Endian
CPU(s):                8
On-line CPU(s) list:   0-7
Thread(s) per core:    1
Core(s) per socket:    4
Socket(s):             2


4 cores per socket * 2 cores * 1 thread(s) per core = 4*2*1 = 8 threads total.
It is possible to use hyperthreading which allows up to two times the amount of threads, but no more.
* It is NOT advised to use all threads on a cluster when multiple tasks are running.

================================================
	 COMMANDLINE OPTIONS WITH DATA
================================================
java -jar	Executes the jar file.
-Xms3G		sets the minimum memory allocation to 3 Gigabytes.
-Xmx4G		Sets the maximum memory allocation to 4 Gigabytes.
-in 		/home/vikthor/human_COPD_dataset/1D25CM/RNASeqUnique/ /home/vikthor/human_COPD_dataset/1D50CM/RNASeqUnique/ /home/vikthor/human_COPD_dataset/2DLCMSMS/RNASeqUnique/
-psm 		"DB search psm.csv"
-pp 		protein-peptides.csv
-db 		/home/vikthor/human_COPD_dataset/Fasta/uniprot/uniprot_taxonomy_3A9606.fasta
-cdb		/home/vikthor/human_COPD_dataset/Fasta/COPDHealthy/COPD19-database.fa
-idb	 	/home/vikthor/human_COPD_dataset/Fasta/COPDHealthy/
-out 		/home/vikthor/Output/multi_test_peptide_matrix.csv
-threads 	6

================================================
	COMMANDLINE RUN IN BACKGROUND
================================================
It is possible to run the program in the background. This allows further usage of the commandline interface.
It is also possible to completely shutdown the commandline interface, as long as the server where the process
is running stays alive.

nohup > file.out &

Writes all commandline output to file.out 

================================================
	    COMMANDLINE EXAMPLE
================================================
nohup java -Xms3G -Xmx4G -jar PeptideIdentifictionModule.jar -in /home/vikthor/human_COPD_dataset/1D25CM/RNASeqUnique/ /home/vikthor/human_COPD_dataset/1D50CM/RNASeqUnique/ /home/vikthor/human_COPD_dataset/2DLCMSMS/RNASeqUnique/ -psm "DB search psm.csv" -pp protein-peptides.csv -db /home/vikthor/human_COPD_dataset/Fasta/uniprot/uniprot_taxonomy_3A9606.fasta -cdb /home/vikthor/human_COPD_dataset/Fasta/COPDHealthy/COPD19-database.fa -idb /home/vikthor/human_COPD_dataset/Fasta/COPDHealthy/ -out /home/vikthor/Output/multi_test_peptide_matrix.csv -threads 4 > unique.out &
