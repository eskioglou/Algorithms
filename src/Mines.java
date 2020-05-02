/*
    Name: Eskioglou Maria
    AEM: 3237
    Email: eskioglou@csd.auth.gr
 */
/*
 *
 * The algorithm I picked to implement in the first project is QuickHull.
 * The reason I picked this Algorithm is because its time complexity is 0(n*logn) as stated in the project.
 * Also, we are not allowed to go through the obstacles, but it is permitted for the agent to move close to the outer mines.
 *
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Mines{

    //This method calculates the shortest path between the up and down side of the area.
    public static void shortestPath(ArrayListMines mines,MinePos start,MinePos finish) {
        Paths path1 = new Paths();
        int i;
        for(i=0;i<mines.length();i++){ //table has first the down path. When we find the start,then begin the Up path
            if(mines.getposition(i).equal(start)) { break; }
            else { path1.add(mines.getposition(i)); }
        }

        Paths path2 = new Paths();
        for(int j=i;j<mines.length();j++) {
            path2.add(mines.getposition(j));
        }
        path1.add(start);
        path1.add(finish);

        path1.sort();
        path2.sort();
        path1.distance();
        path2.distance();

        if(path1.pathLength()<path2.pathLength()) {
            System.out.println("The shortest distance is " + Math.round(path1.pathLength()*100000.0)/100000.0);
            path1.print();
        }
        else {
            System.out.println("The shortest distance is " + Math.round(path2.pathLength()*100000.0)/100000.0 );
            path2.print();
        }
    }

    public static void main(String[] args) throws IOException {
        ArrayListMines mines=new ArrayListMines();
        MinePos start=null;
        MinePos finish=null;
        Scanner sc = new Scanner(new File(args[0]));

        int i = 1;
        while(sc.hasNextInt())
        {
            if(i==1) { start=new MinePos(sc.nextInt(),sc.nextInt()); }
            else if(i==2) { finish =new MinePos(sc.nextInt(),sc.nextInt()); }
            else { mines.add(new MinePos(sc.nextInt(),sc.nextInt())); }
            i++;
        }

        mines.add(start);
        mines.add(finish);
        mines.quickhull(); //Find the perimeter
        shortestPath(mines,start,finish); // Find the shortest path
        System.out.println();

    }

//-----------------------------------------Sub Class: MinePos--------------------------------------
// This is a helping class in order to depict the Mines in two dimensions, with parameters as x and y.
// This subclass contains the constructor, getters for x and y, a method equal and another that calculates
// the distance.
    public static class MinePos {
        private final int x;
        private final int y;

    //The constructor: Initializing the dimensions.
    public MinePos(int x1,int y1){
            x=x1;
            y=y1;
        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }

        public boolean equal(MinePos mine){
            return this.x==mine.x && this.y==mine.y;
        }

        public double distance(MinePos mine) {
            return Math.sqrt(Math.pow(mine.x-this.x,2) + Math.pow(mine.y-this.y,2));
        }
    }

//-------------------------------------------------------------------------------------------------------
//------------------------------------Sub Class: Paths----------------------------------------------------
//  This subclass contains the constructor and methods to add a MinePos, the position of a mine, sort it,
//  distance and return distance and then print the same format as asked in the project.

    public static class Paths {
        private final ArrayList<MinePos> path;
        private double distance;

        public Paths() {
            path=new ArrayList<>();
            distance=0;
        }
        public void add(MinePos mine) {
            path.add(mine);
        }

        public void sort() {
            path.sort((m1, m2) -> Float.compare(m1.getX(), m2.getX()));
        }

        public void distance() {
            for (int i = 1; i < path.size(); i++) {
                distance+=path.get(i-1).distance(path.get(i));
            }
        }
        public double pathLength() {return distance;}

        public void print() {
            System.out.print("The shortest path is:");
            int i;
            for(i=0;i<path.size()-1;i++) {
                System.out.print("("+ path.get(i).getX()+","+path.get(i).getY()+")-->");
            }
            System.out.print("("+ path.get(i).getX()+","+path.get(i).getY()+")");
        }
    }


//-------------------------------------------------------------------------------------------------------
//------------------------------------Sub Class: QuickHull-----------------------------------------------
//  The quickHull method implements the logic of the QuickHull algorithm.
//  I took the algorithm from: https://www.sanfoundry.com/java-program-implement-quick-hull-algorithm-find-convex-hull/
//  However, I adjusted it in order to fully operate in the given example.
//
    public static class QuickHull {
        public ArrayList<MinePos> quickHull(ArrayList<MinePos> mines) {
            ArrayList<MinePos> convexHull = new ArrayList<>();
            if (mines.size() < 3) //If we are given 3 points then the shortest path are these 3 points.
                return (ArrayList<MinePos>) mines.clone(); //That's why we clone these three mines.

            //min - the starting point of the convexHull
            //max - the final point of the convexHull
            int min = -1, max = -1;
            float minX = Integer.MAX_VALUE;
            float maxX = Integer.MIN_VALUE;

            //I am identifying the start and the finish of the convexHull
            //from the ArrayList containing all the available mines
            for (int i = 0; i < mines.size(); i++) {
                if (mines.get(i).getX() < minX) {
                    minX = mines.get(i).getX();
                    min = i;
                }
                if (mines.get(i).getX() > maxX)
                {
                    maxX = mines.get(i).getX();
                    max = i;
                }
            }
            MinePos Start = mines.get(min); //The start
            MinePos Finish = mines.get(max); // The finish - treasure.
            convexHull.add(Start);
            convexHull.add(Finish);
            mines.remove(Start);
            mines.remove(Finish);
            ArrayList<MinePos> leftSet = new ArrayList<>();  //Mines on the left of the line.
            ArrayList<MinePos> rightSet = new ArrayList<>(); //Mines on the right of the line.

            // divide into two: The right and the left and we separate the mines proportionally.
            for (MinePos m : mines) {
                if (findSide(Start, Finish, m) == -1) // All points that are on the left side of the start and finish.
                    leftSet.add(m);
                else if (findSide(Start, Finish, m) == 1)  // All points that are on the right side.
                    rightSet.add(m);

            }
            hullSet(Start, Finish, rightSet, convexHull);
            hullSet(Finish, Start, leftSet, convexHull);
            return convexHull;
        }

     // Determines the exact mines the convexHull will be consisted of from leftSet and rightSet through repetition and adds them to the convexHull.
     // This method ,along the quickhull method, implements and completes the QuickHull algorithm and allows the convexHull to be formed.
     // The quickhull method calls this one once with the leftSet as the set parameter, and once with the rightSet as the set parameter.

        public void hullSet(MinePos Start, MinePos Finish, ArrayList<MinePos> set, ArrayList<MinePos> hull)
        {
            int insertPosition = hull.indexOf(Finish);
            if (set.isEmpty()) //If there are no points then stop here.
                return;
            if (set.size() == 1) {
                MinePos m = set.get(0);
                set.remove(m);
                hull.add(insertPosition, m);
                return;
            }
            double dist = Integer.MIN_VALUE;
            int furthestPoint = -1;

            //This loop finds the furthest point of the set from the Start - Finish line
            for (int i = 0; i < set.size(); i++) {
                MinePos m = set.get(i);
                double distance = distance(Start, Finish, m);
                if (distance > dist) {
                    dist = distance;
                    furthestPoint = i;
                }
            }
            MinePos P = set.get(furthestPoint);
            set.remove(furthestPoint);
            hull.add(insertPosition, P);

            // Determine what's to the left of StartP
            ArrayList<MinePos> leftSetAP = new ArrayList<>();
            for (MinePos M : set) {
                if (findSide(Start, P, M) == 1) {
                    leftSetAP.add(M);
                }
            }
            // Determine what's to the left of PFinish
            ArrayList<MinePos> leftSetPB = new ArrayList<>();
            for (MinePos M : set) {
                if (findSide(P, Finish, M) == 1) {
                    leftSetPB.add(M);
                }
            }

            //Repetition in order to figure out the rest of the mines needed to complete the convexHull.
            hullSet(Start, P, leftSetAP, hull);
            hullSet(P, Finish, leftSetPB, hull);
        }


    //Calculates the distance of a third point C from the line defined by Points A and B.
    public static double distance(MinePos A, MinePos B, MinePos C) {
        int ABx = B.x - A.x;
        int ABy = B.y - A.y;
        double dist = ABx * (A.y - C.y) - ABy * (A.x - C.x);
        if (dist < 0) //Distance must be positive.
            dist = -dist;
        return dist;
    }



    // This method estimates if a point belongs on the left or on the right side of the points A and B.
    public static float findSide(MinePos A, MinePos B, MinePos P) {
        float side = (B.x - A.x) * (P.y - A.y) - (B.y - A.y) * (P.x - A.x);
        if (side > 0)
            return 1;
        else if (side == 0)
            return 0;
        else
            return -1;
    }
    }
    //---------------------------------------------------------------------------------------------------------
    //---------------------------------SubClass: ArrayListMines -----------------------------------------------
    //  This is a helping class for some main operations of the ArrayList such as the addition, the length, the position.
    public static class ArrayListMines {
        private final ArrayList<MinePos> mines;
        public ArrayListMines() {
            mines=new ArrayList<>();
        }

        public void add(MinePos p) {
            mines.add(p);
        }

        public int length() {
            return mines.size();
        }

        public MinePos getposition(int i){ return mines.get(i);}

        public void quickhull() {
            QuickHull qh = new QuickHull();
            ArrayList<MinePos> p = qh.quickHull(mines);
            mines.clear();
            mines.addAll(p);
            p.clear();
        }
    }
}
