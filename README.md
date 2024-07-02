# Network Flow and Page Rank Algorithms

Developed for an assignment focused on network flow optimization and page rank identification, this repository contains the implementation of the Edmond Karp and Page Rank algorithms. 

## Algorithms

### Edmonds-Karp Algorithm:
#### EdmondKarp.java

The Edmonds-Karp algorithm is an implementation of the Ford-Fulkerson method for computing the maximum flow in a flow network. It utilizes the breadth-first search (BFS) to find the shortest augmenting paths, which guarantees a time complexity of O(VE^2), where V is the number of vertices and E is the number of edges. This approach is effective for solving network flow problems, making it a valuable tool in fields like telecommunications, transportation, and computer networks.

### PageRank Algorithm:
#### PageRank.java

The PageRank algorithm, developed by Larry Page and Sergey Brin, is used by search engines to rank web pages in their search results. It operates on the principle of link analysis, assigning a ranking score to each page based on the quantity and quality of links pointing to it. Pages that are linked by many high-quality pages are assigned higher ranks, which makes PageRank an essential algorithm for improving the relevance and reliability of search engine results.

## Useage
1. **Compilation:** Use a Java compiler to compile the Java files.

2. **Integration:** Incorporate the compiled Java programs into your projects to leverage the algorithms.

3. **Guidelines:** Refer to the specific usage instructions within each program for detailed guidelines on each function.

## Acknowledgements

These implementations were developed as part of a project for COMP261 at Victoria University of Wellington.
