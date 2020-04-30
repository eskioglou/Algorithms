import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

//-----------------------------------Helping Functions and Classes-----------------------------------------------
/**
 *
 * The algorithm I picked to implement in the first project is QuickHull.
 * The reason I picked this Algorithm is because its time complexity is 0(n*logn) as stated in the project.
 * Also, we are not allowed to go through the obstacles, but it is permitted for the agent to move close to the outer mines.
 *
 */

public class Comments {
    //This is a helping class in order to depict the Mines in two dimensions, with parameters as x and y.
    public static class MinePos {

        public int x;
        public int y;

        //The constructor: Initializing the dimensions.
        public MinePos(int x, int y) {
            this.x = x;
            this.y = y;
        }
        //Getters
        public int getX(){return x;}
        public int getY(){return y;}
    }

    /*
        This method estimates if a point belongs on the left or on the right side of the points A and B.
     */
    public static int findSide(MinePos A, MinePos B, MinePos P) {
        int side = (B.x - A.x) * (P.y - A.y) - (B.y - A.y) * (P.x - A.x);
        return Integer.compare(side, 0);
    }

    /*
        This method estimates the length of the path.
     */
    public static double pathLength(ArrayList<MinePos> hull) {
        double length = 0;
        for (int i = 0; i < hull.size() - 1; i++) {
            length += Math.sqrt(Math.pow(hull.get(i).x - hull.get(i + 1).x, 2) + Math.pow(hull.get(i).y - hull.get(i + 1).y, 2));
        }
        return length;
    }

    /**
     * Calculates the distance of a third point C from the line defined by Points A and B.
     */
    public static double distance(MinePos A, MinePos B, MinePos C) {
        int ABx = B.x - A.x;
        int ABy = B.y - A.y;
        double dist = ABx * (A.y - C.y) - ABy * (A.x - C.x);
        if (dist < 0) //Distance must be positive.
            dist = -dist;
        return dist;
    }

//----------------------------------------------------------------------------------------------------------------

//------------------------------------------QuickHull--------------------------------------------------------------
//-------------Credits: https://www.sanfoundry.com/java-program-implement-quick-hull-algorithm-find-convex-hull/----
    /**
     * The quickHull method implements the logic of the QuickHull algorithm.
     * I took the algorithm from: https://www.sanfoundry.com/java-program-implement-quick-hull-algorithm-find-convex-hull/
     * However, I adjusted it in order to fully operate in the given example.
     *
     *
     * @return
     */

    public static ArrayList quickHull(ArrayList<MinePos> mines) {
        //Creates two empty convexHulls.
        ArrayList<MinePos> path1= new ArrayList<>();
        ArrayList<MinePos> path2= new ArrayList<>();

        if (mines.size() < 3)

            return (ArrayList) mines.clone(); //If we are given 3 points then the shortest path are these 3 points.
        //That's why we clone these three mines.

        MinePos Start = mines.get(0);
        MinePos Finish = mines.get(1);
        path1.add(Start);
        path2.add(Start);
        path1.add(Finish);
        path2.add(Finish);
        mines.remove(0);
        mines.remove(1);
        ArrayList<MinePos> leftSet = new ArrayList<>(); //Mines on the left of the line.
        ArrayList<MinePos> rightSet = new ArrayList<>(); //Mines on the right of the line.

        // divide into two: The right and the left and we separate the mines proportionally.
        for (MinePos p : mines) {
            if (findSide(Start, Finish, p) == -1)       // All points that are on the left side of the start and finish.
                leftSet.add(p);
            else if (findSide(Start, Finish, p) == 1)   // All points that are on the right side.
                rightSet.add(p);
        }

        hullSet(Start, Finish, rightSet, path1);
        hullSet(Start, Finish, leftSet, path2);
        if(pathLength(path1)>pathLength(path2)){
            return path2;}
        else {
            return path1;
        }
    }

    public static void hullSet(MinePos A, MinePos B, ArrayList<MinePos> set, ArrayList<MinePos> hull) {
        int insertPosition = hull.indexOf(B);
        if (set.size() == 0) //If there are no points then stop here.
            return;
        if (set.size() == 1) {
            MinePos p = set.get(0);
            set.remove(p);
            hull.add(insertPosition, p);
            return;
        }
        double dist = Integer.MIN_VALUE;
        int furthest = -1;

        for (int i = 0; i < set.size(); i++) { //Find the furthest mine from the line.
            MinePos p = set.get(i);
            double distance = distance(A, B, p);
            if (distance > dist) {
                dist = distance;
                furthest = i;
            }
        }
        MinePos P = set.get(furthest);
        set.remove(furthest);
        hull.add(insertPosition, P); //Add furthest to the hull.


        ArrayList<MinePos> leftSetAP = new ArrayList<>();
        for (MinePos M : set) {
            if (findSide(A, P, M) == 1) {
                leftSetAP.add(M);
            }
        }

        ArrayList<MinePos> leftSetPB = new ArrayList<>();
        for (MinePos M : set) {
            if (findSide(P, B, M) == 1) {
                leftSetPB.add(M);
            }
        }
        hullSet(A, P, leftSetAP, hull);
        hullSet(P, B, leftSetPB, hull);
    }

//-----------------------------------------------------------------------------------------------
//------------------------------------------Main-------------------------------------------------

    /*
        Reading the file arg[0] as argument from the main.
        I read each line as a string and then split the two numbers and with parseInt, I transform the string into an int.
        Then, we create a Point with a and b as parameters, where a is the x coordinates and b is the y coordinates.
     */

    public static void shortestPath(Mines.ArrayListMines mines, Mines.MinePos start, Mines.MinePos finish) {
        Mines.Paths path1 = new Mines.Paths();
        int i;
        for(i=0;i<mines.length();i++){ //table has first the down path. When we find the start,then begin the Up path{
            if(mines.getposition(i).equal(start)) { break; }
            else { path1.add(mines.getposition(i)); }
        }
        path1.add(start);
        path1.add(finish);
        Mines.Paths path2 = new Mines.Paths();
        for(int j=i;j<mines.length();j++) {
            path2.add(mines.getposition(j));
        }
        path1.sort();
        path2.sort();
        path1.distance();
        path2.distance();
        if(path1.pathLength()<path2.pathLength()) {
            System.out.println("The shortest distance is " + path1.pathLength());
            path1.print();
        }
        else {
            System.out.println("The shortest distance is " + path2.pathLength());
            path2.print();
        }
    }

    public static void main(String[] args) throws IOException {
        Mines.ArrayListMines mines=new Mines.ArrayListMines();
        Mines.MinePos start=null;
        Mines.MinePos finish=null;
        Scanner sc = new Scanner(new File("test3.txt"));

        int i = 1;
        while(sc.hasNextInt())
        {
            if(i==1) { start=new Mines.MinePos(sc.nextInt(),sc.nextInt()); }
            else if(i==2) { finish =new Mines.MinePos(sc.nextInt(),sc.nextInt()); }
            else { mines.add(new Mines.MinePos(sc.nextInt(),sc.nextInt())); }
            i++;
        }

        mines.add(start);
        mines.add(finish);
        mines.quickhull(); //Find the perimeter
        shortestPath(mines,start,finish); // Find the shortest path
        System.out.println();

    }

}