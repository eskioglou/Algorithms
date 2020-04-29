import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Mine3 {

    public static double pathLength(ArrayList<Point> hull) {
        double length = 0;
        for (int i = 0; i < hull.size() - 1; i++) {
            length += Math.sqrt(Math.pow(hull.get(i).x - hull.get(i + 1).x, 2) + Math.pow(hull.get(i).y - hull.get(i + 1).y, 2));
        }
        return length;
    }
    public static class Point {
        private int x;
        private int y;

        /**
         * Create a new point in R^2.
         *
         * @param x the x-coordinate
         * @param y the y-coordinate
         */
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }


        public void setX(int x) {
            this.x = x;
        }


        public void setY(int y) {
            this.y = y;
        }


        public int getX() {
            return x;
        }


        public int getY() {
            return y;
        }


        /**
         * Calculate the cross product of three points.
         *
         * @param A point located in the very left side.
         * @param B point located in the very right side.
         * @return cross product.
         */
        private double crossProduct(Point A, Point B) {
            return (B.x - A.x) * (this.y - A.y) - (B.y - A.y) * (this.x - A.x);
        }


        /**
         * Determine the area in which point P(this) is located according to the line
         * traced between point A and point B.
         *
         * @param A point located in the very left side.
         * @param B point located in the very right side.
         * @return Returns true if P is located in the right side of the line.
         */
        public boolean isRightOfLine(Point A, Point B) {
            return Double.compare(crossProduct(A, B), 0) > 0;
        }


        /**
         * Calculate the distance of (this) point to the line which is formed by points A and B.
         *
         * @param A
         * @param B
         * @return The distance to the line.
         */
        public double getDistanceToLine(Point A, Point B) {
            double cp;

            cp = (B.getX() - A.getX()) * (A.getY() - this.y) - (B.getY() - A.getY()) * (A.getX() - this.x);

            if (cp < 0)
                cp = -cp;

            return cp;
        }


        /**
         * Calculate the euclidean distance between two points ('this' and B)
         *
         * @param B the second point.
         * @return The euclideanDistance distance
         */
        public double euclideanDistance(Point B) {
            return Math.sqrt(Math.pow(B.getX() - this.x, 2) + Math.pow(B.getY() - this.y, 2));
        }
    }

    public static class QuickHull {
        public void hullSet(Point A, Point B, ArrayList<Point> set, ArrayList<Point> hull) {
            int insertPosition = hull.indexOf(B);

            if (set.size() == 0)
                return;

            if (set.size() == 1) {
                Point p = set.get(0);
                set.remove(p);

                hull.add(insertPosition, p);

                return;
            }

            double dist = 0.0;
            int farthestPointIndex = -1;

            int index = 0;

            for (Point p : set) {
                double distance = p.getDistanceToLine(A, B);
                if (distance > dist) {
                    dist = distance;
                    farthestPointIndex = index;
                }
                index++;
            }

            Point P = set.get(farthestPointIndex);
            set.remove(P);
            hull.add(insertPosition, P);

            // Determine points on the right of the line traced by points AP
            ArrayList<Point> rightPointsAP = new ArrayList<Point>();
            index = 0;

            for (Point q : set) {
                if (q.isRightOfLine(A, P))
                    rightPointsAP.add(q);
            }

            // Determine points on the right of the line traced by points PB
            ArrayList<Point> rightPointsPB = new ArrayList<Point>();
            index = 0;

            for (Point q : set)
                if (q.isRightOfLine(P, B))
                    rightPointsPB.add(q);

            hullSet(A, P, rightPointsAP, hull);
            hullSet(P, B, rightPointsPB, hull);
        }

        public ArrayList<Point> run(ArrayList<Point> points) {
            ArrayList<Point> convexHull = new ArrayList<Point>();

            if (points.size() < 3)
                return points;

            int minPoint = -1, maxPoint = -1;

            // make sure to take the very (minX, maxX)
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;

            int index = 0;

            for (Point p : points) {
                int x = p.getX();

                if (x < minX) {
                    minX = x;
                    minPoint = index;
                }
                if (x > maxX) {
                    maxX = x;
                    maxPoint = index;
                }
                index++;
            }

            // initialize convexHull with the min/max point on the x axis.
            Point A = points.get(minPoint);
            Point B = points.get(maxPoint);

            convexHull.add(A);
            convexHull.add(B);

            // discard from the original points
            points.remove(A);
            points.remove(B);

            // the original set is splitted by the line formed by point (A,B).
            ArrayList<Point> leftPoints = new ArrayList<Point>();
            ArrayList<Point> rightPoints = new ArrayList<Point>();

            for (Point p : points) {
                if (p.isRightOfLine(A, B))
                    rightPoints.add(p);
                else
                    leftPoints.add(p);
            }

            hullSet(A, B, rightPoints, convexHull);
            hullSet(B, A, leftPoints, convexHull);

            return convexHull;
        }
    }


        public static void main(String args[]) throws IOException {
            ArrayList<Point> finalPath;
            double shortestDistance;
            QuickHull q=new QuickHull();

            //ReadFile.
            FileReader file= new FileReader("file1.txt");
            BufferedReader br = new BufferedReader(file);
            ArrayList<Point> list = new ArrayList<>(); //Create list of mines.
            String st;
            int counter = 0;
            while ((st = br.readLine()) != null) {
                String[] coords = st.split(" ");
                int a = Integer.parseInt(coords[0]); //First column is x.
                int b = Integer.parseInt(coords[1]); //First column is y.
                Point pos = new Point(a, b); //Add them as the coords of the pos.
                list.add(counter, pos);
                counter++;
            }

            finalPath = q.run(list);
            shortestDistance = pathLength(finalPath);
            System.out.printf("The shortest distance is %.5f%n", shortestDistance); //First 5 decimals.
            System.out.print("The shortest path is:"); //There is a certain format in the output.
            for (int i = 0; i < finalPath.size(); i++) {
                if (i != finalPath.size() - 1){
                    System.out.print("(" + finalPath.get(i).x + "," + finalPath.get(i).y + ")" + "-->");}
                else{
                    System.out.print("(" + finalPath.get(i).x + "," + finalPath.get(i).y + ")");}
            }
        }
}
