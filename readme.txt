This algorithm constructs a graph using a dataset of points, the graph is then used to find the nearest point of each query point in the querypoint file
The base codes are written by prof Robert laganiere, algorithm for graph construction and nearest point by me.
-------------------------------------------------
El Bechir El Hadj
Student Number : 300294124
---------------------------------------------------

To execute GraphA1NN : 
javac GraphA1NN
java GraphA1NN 25 10 siftsmall_base.fvecs siftsmall_query.fvecs 

First argument => K (Variable determining the number of vertices each vertex has in the graph)
Second argument => S (Variable determining the size of the array in the nearest point search)
Third argument => dataset 
Fourth argument => QueryPoint file 

PS : Program does not run with the 1000000 point dataset since the knn.txt file used to construct the graph only contains 10000 points
