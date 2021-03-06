/* autogenerated by Processing revision 1281 on 2022-03-21 */
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class pathshape2 extends PApplet {

class PathShape {
  int bX, bY, bW, bH; 
  IntList xs, ys;
  final int border = color(0);
  final int fill = color(226,88,34);
  final int centroidColor = color(155,155,155);
  int[] centroid = new int[2];
  float area;
  
  public PathShape(int bX, int bY, int bW, int bH) {
    this.xs = new IntList();
    this.ys = new IntList();
 
    area = 0.0f;
    
    this.bX = bX;
    this.bY = bY;
    this.bW = bW;
    this.bH = bH;
  }
  
   public void addPoint(int x, int y) {
    xs.append(x);
    ys.append(y);
    setArea();
    setCentroid();
  }
  
   public void display() {
    fill(fill);
    stroke(border);
    beginShape();
    for (int i = 0; i < xs.size(); i++) {
      vertex(xs.get(i), ys.get(i));
    }
    vertex(xs.get(0), ys.get(0));
    endShape();
    fill(centroidColor);
    circle(centroid[0],centroid[1],5);
  }
  
   public void setCentroid() {
    int sumX = 0;
    int sumY = 0;
    for (int i = 0; i < xs.size(); i++) {
      int nextX = xs.get(0);
      int nextY = ys.get(0);
      if (i < xs.size() - 1) {
        nextX = xs.get(i+1);
        nextY = ys.get(i+1);
      }
      int x = xs.get(i);
      int y = ys.get(i);
      int mp = x * nextY - nextX * y;
      sumX += (x + nextX) * mp;
      sumY += (y + nextY) * mp;
    }
    centroid[0] = PApplet.parseInt(sumX / (6 * area));
    centroid[1] = PApplet.parseInt(sumY / (6 * area));
  }
  
   public void setArea() {
    float sum = 0;
    for (int i = 0; i < xs.size(); i++) {
      int nextX = xs.get(0);
      int nextY = ys.get(0);
      if (i < xs.size() - 1) {
        nextX = xs.get(i+1);
        nextY = ys.get(i+1);
      }
      int x = xs.get(i);
      int y = ys.get(i);
      sum += x * nextY - nextX * y;
    }
    area = sum / 2;
  }
}

PathShape shape;
RegularGon regular;

 public void setup() {
  /* size commented out by preprocessor */;
  //shape = new PathShape(0,0,0,0);
  regular = new RegularGon(0,0,20,30, 1000000,100);
}

 public void draw() {
  background(200);
  //shape.display();
  regular.display();
}

 public void mousePressed() {
  shape.addPoint(mouseX,mouseY);
}

 public void keyPressed() {
  if (key == ' ') {
    shape.xs = new IntList();
    shape.ys = new IntList();
    shape.centroid = new int[2];
  }
}
class RegularGon extends PathShape {
  int nsides;
  int radius;
  
  public RegularGon(int bx, int by, int bw, int bh, int nsides, int radius) {
    super(bx,by,bw,bh);
    centroid[0] = bx + bw / 2 + width / 2;
    centroid[1] = by + bh / 2 + height / 2;
    this.nsides = nsides;
    this.radius = radius;
    generateRegularPolygon();
  }
  
   public void generateRegularPolygon() {
    float innerAngle = radians(360.0f / nsides);
    int cx = centroid[0];
    int cy = centroid[1];
    for (int i = 0; i < nsides; i++) {
      xs.append(PApplet.parseInt(radius * cos(innerAngle * i) + cx));
      ys.append(PApplet.parseInt(radius * sin(innerAngle * i) + cy));
    }
  }
}


  public void settings() { size(400, 400); }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "pathshape2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
