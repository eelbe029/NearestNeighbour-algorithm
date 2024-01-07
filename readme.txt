-------------------------------------------------
El Bechir El Hadj
Student Number : 300294124
---------------------------------------------------

To execute GraphA1NN : 
javac GraphA1NN
java GraphA1NN 25 10 siftsmall_base.fvecs siftsmall_query.fvecs 

First argument => K
Second argument => S 
Third argument => dataset 
Fourth argument => QueryPoint file 

PS : Program does not run with the 1000000 point dataset since the knn.txt file used to construct the graph only contains 10000 points