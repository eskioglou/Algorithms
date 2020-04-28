/*
    Name: Eskioglou Maria
    AEM: 3237
    Email: eskioglou@csd.auth.gr
 */
package Algorithms;

import java.io.*;
import java.util.ArrayList;

//-----------------------------------Helping Functions and Classes-----------------------------------------------
/**
 * @author Eskioglou Maria
 * AEM: 3237
 * Email: eskioglou@csd.auth.gr
 *
 * The algorithm I picked to implement in the first project is QuickHull.
 * The reason I picked this Algorithm is because its time complexity is 0(n*logn) as stated in the project.
 * Also, we are not allowed to go through the obstacles, but it is permitted to move close to the outer mines.
 *
 */

public class Mines {
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
        if (side > 0)
            return 1;
        else if (side == 0)
            return 0;
        else
            return -1;
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
     *
     */

    public static ArrayList<MinePos> quickHull(ArrayList<MinePos> mines) {
        ArrayList<MinePos> path1= new ArrayList<MinePos>();
        ArrayList<MinePos> path2= new ArrayList<MinePos>();

        if (mines.size() < 3)

            return (ArrayList) mines.clone(); //If only 3 points in our project then the shortest path are these 3 points themselves so the function returns a clone of the parameter ArrayList


        MinePos C = mines.get(0);
        MinePos D = mines.get(1);
        path1.add(C);
        path2.add(C);
        path1.add(D);
        path2.add(D);
        mines.remove(0);
        mines.remove(1);
        ArrayList<MinePos> leftSet = new ArrayList<MinePos>();
        ArrayList<MinePos> rightSet = new ArrayList<MinePos>();

        for (int i = 0; i < mines.size(); i++) {
            MinePos p = mines.get(i);
            if (findSide(C, D, p) == -1)       // Βρίσκω τα σημεία που είναι αριστερά των δύο προηγούμενων σημείων
                leftSet.add(p);
            else if (findSide(C, D, p) == 1)   // Βρίσκω τα σημεία που είναι δεξιά των δύο προηγούμενων σημείων
                rightSet.add(p);
        }

        hullSet(C, D, rightSet, path1);
        hullSet(C, D, leftSet, path2);
        if(pathLength(path1)>pathLength(path2))
            return path2;
        return path1;
    }

    public static void hullSet(MinePos A, MinePos B, ArrayList<MinePos> set,    // Τρέχει αναδρομικά το σύνολο των σημείων που βρίσκονται είτε αριστερά είτα δεξιά
                               ArrayList<MinePos> hull) {                     // από τα δύο αρχικά σημεία
        int insertPosition = hull.indexOf(B);
        if (set.size() == 0)
            return;
        if (set.size() == 1) {
            MinePos p = set.get(0);
            set.remove(p);
            hull.add(insertPosition, p);
            return;
        }
        double dist = Integer.MIN_VALUE;
        int furthestPoint = -1;
        for (int i = 0; i < set.size(); i++) {
            MinePos p = set.get(i);
            double distance = distance(A, B, p);
            if (distance > dist) {
                dist = distance;
                furthestPoint = i;
            }
        }
        MinePos P = set.get(furthestPoint);
        set.remove(furthestPoint);
        hull.add(insertPosition, P);


        ArrayList<MinePos> leftSetAP = new ArrayList<MinePos>();
        for (int i = 0; i < set.size(); i++) {
            MinePos M = set.get(i);
            if (findSide(A, P, M) == 1) {
                leftSetAP.add(M);
            }
        }

        ArrayList<MinePos> leftSetPB = new ArrayList<MinePos>();
        for (int i = 0; i < set.size(); i++) {
            MinePos M = set.get(i);
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
    public static void main(String[] args) throws IOException {

        ArrayList<MinePos> finalPath;
        double shortestDistance;

        FileReader file= new FileReader("file.txt");
        BufferedReader br = new BufferedReader(file);
        ArrayList<MinePos> list = new ArrayList<>();
        String st;
        int counter = 0;
        while ((st = br.readLine()) != null) {
            String[] coords = st.split(" ");
            int a = Integer.parseInt(coords[0]);
            int b = Integer.parseInt(coords[1]);
            MinePos point = new MinePos(a, b);
            list.add(counter, point);
            counter++;
        }

        finalPath = quickHull(list);
        shortestDistance = pathLength(finalPath);

        System.out.printf("The shortest distance is %.5f%n", shortestDistance); //First 5 decimals.
        System.out.print("The shortest path is: "); //There is a certain format in the output.
        for (int i = 0; i < finalPath.size(); i++) {
            if (i != finalPath.size() - 1){
                System.out.print("(" + finalPath.get(i).x + ", " + finalPath.get(i).y + ")" + "-->");}
            else{
                System.out.print("(" + finalPath.get(i).x + ", " + finalPath.get(i).y + ")");}
        }
    }
}