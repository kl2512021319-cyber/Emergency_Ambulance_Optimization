import java.util.*;
public class EmergencyAmbulanceOptimization
{
    // Travel Cost Matrix (Adjacency Matrix)
    static int[][] costMatrix = {
        {0, 15, 25, 35},
        {15, 0, 30, 28},
        {25, 30, 0, 20},
        {35, 28, 20, 0}
    };

    // Location names
    static String[] locations = {
        "Hospital",
        "Emergency Location B",
        "Emergency Location C",
        "Emergency Location D"
    };

    // ============================================
    // GREEDY ROUTE OPTIMIZATION
    // ============================================
    public static String greedyEAROP(int[][] dist)
    {
        int n = dist.length;
        boolean[] visited = new boolean[n];
        int current = 0;
        int totalCost = 0;
        visited[current] = true;
        StringBuilder path = new StringBuilder();
        path.append(locations[current]);

        for (int count = 1; count < n; count++)
        {
            int nearestLocation = -1;
            int minimumCost = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++)
            {
                if (!visited[i] && dist[current][i] < minimumCost)
                {
                    minimumCost = dist[current][i];
                    nearestLocation = i;
                }
            }

            visited[nearestLocation] = true;
            totalCost = totalCost + minimumCost;
            current = nearestLocation;
            path.append(" -> ").append(locations[current]);
        }

        // Return to Hospital
        totalCost = totalCost + dist[current][0];
        path.append(" -> ").append(locations[0]);
        return "Greedy Ambulance Route: " + path + " | Total Cost: " + totalCost;
    }

    // ============================================
    // DYNAMIC PROGRAMMING ROUTE OPTIMIZATION
    // ============================================
    public static String dynamicProgrammingEAROP(int[][] dist)
    {
        int n = dist.length;
        int VISITED_ALL = (1 << n) - 1;
        int[][] memo = new int[n][1 << n];
        String[][] paths = new String[n][1 << n];

        for (int i = 0; i < n; i++)
        {
            Arrays.fill(memo[i], -1);
        }

        int totalCost = dynamicProgrammingEAROPHelper(0,1,dist,memo,VISITED_ALL,paths);
        String bestPath = paths[0][1];
        return "Dynamic Programming Ambulance Route: "+ bestPath+ " | Total Cost: "+ totalCost;
    }

    // Dynamic Programming Helper Method
    private static int dynamicProgrammingEAROPHelper(int pos,int mask,int[][] dist,int[][] memo,int VISITED_ALL,String[][] paths)
    {
        // All locations have been visited
        if (mask == VISITED_ALL)
        {
            paths[pos][mask] = locations[pos] + " -> " + locations[0];
            return dist[pos][0];
        }

        // Return stored result
        if (memo[pos][mask] != -1)
        {
            return memo[pos][mask];
        }

        int minimumCost = Integer.MAX_VALUE;
        String bestPath = "";

        for (int next = 0; next < dist.length; next++)
        {
            // Check whether location is not visited
            if ((mask & (1 << next)) == 0)
            {
                int newMask = mask | (1 << next);
                int newCost = dist[pos][next] + dynamicProgrammingEAROPHelper(next,newMask,dist,memo,VISITED_ALL,paths);

                if (newCost < minimumCost)
                {
                    minimumCost = newCost;
                    bestPath = locations[pos] + " -> " + paths[next][newMask];
                }
            }
        }

        memo[pos][mask] = minimumCost;
        paths[pos][mask] = bestPath;
        return minimumCost;
    }

    // ============================================
    // BACKTRACKING ROUTE OPTIMIZATION
    // ============================================
    static int backtrackingBestCost;
    static String backtrackingBestRoute;

    public static String backtrackingEAROP(int[][] dist)
    {
        int n = dist.length;
        boolean[] visited = new boolean[n];
        visited[0] = true;
        backtrackingBestCost = Integer.MAX_VALUE;
        backtrackingBestRoute = "";
        StringBuilder path = new StringBuilder();
        path.append(locations[0]);
        earopBacktracking(0,dist,visited,n,1,0,path);

        return "Backtracking Ambulance Route: "+ backtrackingBestRoute+ " | Total Cost: "+ backtrackingBestCost;
    }

    // Backtracking Helper Method
    private static int earopBacktracking(int pos,int[][] dist,boolean[] visited,int n,int count,int cost,StringBuilder path)
    {
        // If all emergency locations have been visited
        if (count == n)
        {
            int totalCost = cost + dist[pos][0];

            if (totalCost < backtrackingBestCost)
            {
                backtrackingBestCost = totalCost;
                backtrackingBestRoute = path.toString() + " -> " + locations[0];
            }
            return totalCost;
        }

        int minimumCost = Integer.MAX_VALUE;

        for (int next = 0; next < n; next++)
        {
            if (!visited[next])
            {
                visited[next] = true;
                int oldLength = path.length();
                path.append(" -> ").append(locations[next]);

                int newCost = earopBacktracking(next,dist,visited,n,count + 1,cost + dist[pos][next],path);
                minimumCost = Math.min(minimumCost, newCost);
                // Backtrack
                path.setLength(oldLength);
                visited[next] = false;
            }
        }
        return minimumCost;
    }

    // ============================================
    // DIVIDE AND CONQUER ROUTE OPTIMIZATION
    // ============================================
    static int divideBestCost;
    static String divideBestRoute;

    public static String divideAndConquerEAROP(int[][] dist)
    {
        int n = dist.length;
        boolean[] visited = new boolean[n];
        visited[0] = true;
        divideBestCost = Integer.MAX_VALUE;
        divideBestRoute = "";
        StringBuilder path = new StringBuilder();
        path.append(locations[0]);
        divideAndConquerHelper(0,visited,0,dist,n,path);
        return "Divide & Conquer Ambulance Route: "+ divideBestRoute+ " | Total Cost: "+ divideBestCost;
    }

    // Divide and Conquer Helper Method
    private static int divideAndConquerHelper(int pos,boolean[] visited,int currentCost,int[][] dist,int n,StringBuilder path)
    {
        // Base case
        if (allVisited(visited))
        {
            int totalCost = currentCost + dist[pos][0];

            if (totalCost < divideBestCost)
            {
                divideBestCost = totalCost;
                divideBestRoute = path.toString() + " -> " + locations[0];
            }
            return totalCost;
        }

        int minimumCost = Integer.MAX_VALUE;

        for (int next = 0; next < n; next++)
        {
            if (!visited[next])
            {
                visited[next] = true;
                int oldLength = path.length();
                path.append(" -> ").append(locations[next]);
                int result = divideAndConquerHelper(next,visited,currentCost + dist[pos][next],dist,n,path);

                minimumCost = Math.min(minimumCost,result);
                path.setLength(oldLength);
                visited[next] = false;
            }
        }
        return minimumCost;
    }

    // Check whether all emergency locations have been visited
    private static boolean allVisited(boolean[] visited)
    {
        for (boolean location : visited)
        {
            if (!location)
            {
                return false;
            }
        }
        return true;
    }

    // ============================================
    // INSERTION SORT
    // ============================================
    public static String insertionSort(int[] arr)
    {
        for (int i = 1; i < arr.length; i++)
        {
            int key = arr[i];
            int j = i - 1;

            while (j >= 0 && arr[j] > key)
            {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
        return Arrays.toString(arr);
    }

    // ============================================
    // BINARY SEARCH
    // ============================================
    public static String binarySearch(int[] arr, int target)
    {
        int low = 0;
        int high = arr.length - 1;

        while (low <= high)
        {
            int middle = low + (high - low) / 2;

            if (arr[middle] == target)
            {
                return String.valueOf(middle);
            }

            else if (arr[middle] < target)
            {
                low = middle + 1;
            }

            else
            {
                high = middle - 1;
            }
        }
        return "-1";
    }

    // ============================================
    // MIN-HEAP
    // ============================================
    static class MinHeap
    {
        private PriorityQueue<Integer> heap = new PriorityQueue<Integer>();

        // Insert a value into the Min-Heap
        public void insert(int value)
        {
            heap.add(value);
        }
        // Remove and return the minimum value
        public int extractMin()
        {
            if (heap.isEmpty())
            {
                System.out.println("Heap is empty.");
                return -1;
            }
            return heap.poll();
        }
    }

    // ============================================
    // SPLAY TREE
    // ============================================
    static class SplayTree
    {
        // Node class
        private class Node
        {
            int key;
            Node left;
            Node right;

            Node(int key)
            {
                this.key = key;
            }
        }
        private Node root;
        // Insert value
        public void insert(int value)
        {
            root = insertNode(root, value);
            root = splay(root, value);
        }

        // Normal BST insertion
        private Node insertNode(Node node, int value)
        {
            if (node == null)
            {
                return new Node(value);
            }

            if (value < node.key)
            {
                node.left = insertNode(node.left, value);
            }

            else if (value > node.key)
            {
                node.right = insertNode(node.right,value);
            }
            return node;
        }

        // Search value
        public boolean search(int value)
        {
            root = splay(root, value);
            return root != null && root.key == value;
        }

        // Splay operation
        private Node splay(Node root, int key)
        {
            if (root == null || root.key == key)
            {
                return root;
            }

            // ==================================
            // Key is in left subtree
            // ==================================
            if (key < root.key)
            {
                if (root.left == null)
                {
                    return root;
                }

                // Zig-Zig
                if (key < root.left.key)
                {
                    root.left.left = splay(root.left.left,key);
                    root = rotateRight(root);
                }

                // Zig-Zag
                else if (key > root.left.key)
                {
                    root.left.right = splay(root.left.right,key);

                    if (root.left.right != null)
                    {
                        root.left = rotateLeft(root.left);
                    }
                }

                if (root.left == null)
                {
                    return root;
                }

                else
                {
                    return rotateRight(root);
                }
            }

            // ==================================
            // Key is in right subtree
            // ==================================
            else
            {
                if (root.right == null)
                {
                    return root;
                }
                
                // Zag-Zag
                if (key > root.right.key)
                {
                    root.right.right = splay(root.right.right,key);
                    root = rotateLeft(root);
                }

                // Zag-Zig
                else if (key < root.right.key)
                {
                    root.right.left = splay(root.right.left, key);

                    if (root.right.left != null)
                    {
                        root.right = rotateRight(root.right);
                    }
                }

                if (root.right == null)
                {
                    return root;
                }

                else
                {
                    return rotateLeft(root);
                }
            }
        }

        // Right rotation
        private Node rotateRight(Node x)
        {
            Node y = x.left;
            x.left = y.right;
            y.right = x;
            return y;
        }

        // Left rotation
        private Node rotateLeft(Node x)
        {
            Node y = x.right;
            x.right = y.left;
            y.left = x;
            return y;
        }
    }

    // ============================================
    // DRIVER METHOD
    // ============================================
    public static void main(String[] args)
    {
        // ========================================
        // Route Optimization Algorithms
        // ========================================
        System.out.println(greedyEAROP(costMatrix));
        System.out.println();
        System.out.println(dynamicProgrammingEAROP(costMatrix));
        System.out.println();
        System.out.println(backtrackingEAROP(costMatrix));
        System.out.println();
        System.out.println(divideAndConquerEAROP(costMatrix));
        System.out.println();
        // ========================================
        // Sorting and Searching
        // ========================================
        int[] arr = {8, 3, 5, 1, 9, 2};
        insertionSort(arr);
        System.out.println("Sorted Emergency Response Times: " + Arrays.toString(arr));
        System.out.println();
        System.out.println("Binary Search " + "(Response Time 5 found at index): " + binarySearch(arr, 5));
        System.out.println();
        // ========================================
        // Min-Heap Test
        // ========================================
        MinHeap heap = new MinHeap();
        heap.insert(10);
        heap.insert(3);
        heap.insert(15);
        System.out.println("Min-Heap Extract Minimum Priority Value: " + heap.extractMin());
        System.out.println();
        // ========================================
        // Splay Tree Test
        // ========================================
        SplayTree tree = new SplayTree();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);
        System.out.println("Splay Tree Search " + "(Emergency Case 10 found): " + tree.search(10));
    }
}