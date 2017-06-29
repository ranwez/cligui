# Sequences alignment

**{{main.path}}**

The **alignSequences** program will read and align a FASTA file, here is a basic example of its usage :

* *{{main.macse}}*

In the above example, MACSE will read **sequences.fasta** and create a nucleotides alignment in **sequences_NT.fasta**
and an amino acids alignment in **sequences_AA.fasta**.

The **seq** option just needs a FASTA file, being aligned or not.


## 1. Nucleotides and amino acids outputs

**{{outputs/outputs.path}}**

Default output files created by MACSE can be redefined this way :

* *{{outputs/outputs.macse}}*

MACSE will read **sequences.fasta** and create the **output_NT.fasta** file containing aligned nucleotides and the
**output_AA.fasta** file with aligned amino acids.


## 2. Less reliable sequences

**{{seq_lr/seq_lr.path}}**

In case you think some sequences are less reliable than others, then you can use the **seq_lr** option like this :

* *{{seq_lr/seq_lr.macse}}*

In this example, MACSE will read **sequences.fasta** and **sequences_lr.fasta** files and merge all sequences while
keeping reliability information.

The final set of sequences will then be aligned considering each sequence reliability.


## 3. Maximum refining

**{{maxRefines/maxRefines.path}}**

You can limit the number of alignment refining using the **maxRefines** option this way :

**{{maxRefines/maxRefines.macse}}**


## 4. Other options

You can find other options related to this program in the following links :

* [alphabet](alphabet.html)
* [aminos](aminos.html)
* [costs](costs.html)
* [nucleotides](nucleos.html)
