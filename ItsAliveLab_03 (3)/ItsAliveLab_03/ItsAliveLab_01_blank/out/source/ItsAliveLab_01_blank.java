/* autogenerated by Processing revision 1281 on 2022-04-06 */
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

public class ItsAliveLab_01_blank extends PApplet {

int POP_COLS = 6;
int POP_ROWS = 5;
int OFFSET = 1;
int POP_SIZE = POP_COLS * POP_ROWS;
int GRID_SIZE = PApplet.parseInt(pow(2, Individual.SIZE_GENE_LENGTH+1) - 1);

Population pop;

 public void settings() {
  size(POP_COLS*GRID_SIZE + (POP_COLS-1)*OFFSET,
    POP_ROWS*GRID_SIZE + (POP_ROWS-1)*OFFSET);
}//settings
 public void setup() {
  pop = new Population(POP_SIZE);
  makePopulation();
}//setup

 public void draw() {
  background(255);
  pop.drawPopGrid(POP_COLS, POP_ROWS, GRID_SIZE, OFFSET, true);
  drawGrid();
}//draw

 public void keyPressed() {
  if (key == 'p') {
    makePopulation();
  }
  if (key == 'm') {
    pop = pop.matingSeason();
    pop.setFitness(pop.get(0));
    println(pop.totalFitness);
  }
}//keypressed


 public void makePopulation() {
  pop.randomPop();
  pop.setFitness(pop.get(0));
  println("Total fitness: ", pop.totalFitness);
}//makepopulation()


 public void drawGrid() {
  stroke(0);
  for (int i=1; i < POP_COLS; i++) {
    int x = i * (GRID_SIZE + OFFSET);
    line(x, 0, x, height-1);
  }//row dividers

  for (int i=1; i < POP_ROWS; i++) {
    int y = i * (GRID_SIZE + OFFSET);
    line(0, y, width-1, y);
  }//column dividers
}//drawgrid
/*
  A Gene is designed to store "genetic" data for
  a single trait to be used in a genetic algorithm.

  The main part of the gene is the genotype, which
  is an array representation of a binary number. The
  length of the array will determine the number of digits
  and as a result, the maximum possible value of the Gene.
  So a Gene of legth 5 has a maximum value of
  11111 base 2, or 31.
*/

class Gene {
  int[] genotype; // bit array, g[0] is little end
  
  Gene(int gl) {
    genotype = new int[gl];
    
    for (int i = 0; i < genotype.length; i++) {
      genotype[i] = PApplet.parseInt(random(2));
    }
  }
  
  Gene(int gl, boolean random) {
    genotype = new int[gl];
  }
  
  Gene(Gene copyFrom) {
    //genotype = otherG.genotype //BAD WONT WORK!
    genotype = new int[copyFrom.genotype.length];
    arrayCopy(copyFrom.genotype, genotype);
    
  }
  
   public int getValue() {
    // bit array: g[0] is little end
    int v = 0;
    int placeval = 1;
    for(int i = 0; i < genotype.length ; i++) {
      v += genotype[i] * placeval;
      placeval *= 2;
    }
    return v;
  }
  
   public void mutate() {
    int rand_i = PApplet.parseInt(random(genotype.length));
    genotype[rand_i] = 1 - genotype[rand_i];
  }
  
   public String toString() {
    //Prints bigendian
    String s = "BE->";
    //Put together binary string
    //Start with big end
    for (int i = genotype.length - 1; i >= 0; i--) {
      s += genotype[i];
    }
    s += " " + getValue();
    return s;
  }
}
class Individual {

  /*
    Note on the type modifiers used here:
    final: Value cannot be changed after initialization
    static: Value is not attached to a specific instance
            To reference a static variable outside of
            this class use: Individual.<variable name>
            i.e. Indivudal.CHROMOSOME_LENGTH
  */
  static final int CHROMOSOME_LENGTH = 6;
  static final int SIDES_GENE_LENGTH = 5;
  static final int SIZE_GENE_LENGTH = 6;
  static final int SPIN_GENE_LENGTH = 4;
  static final int COLOR_GENE_LENGTH = 8;

  static final int SIDES_IND = 0;
  static final int SIZE_IND = 1;
  static final int SPIN_IND = 2;
  static final int RED_IND = 3;
  static final int GREEN_IND = 4;
  static final int BLUE_IND = 5;

  RegularGon phenotype;
  Gene[] chromosome;
  float fitness;


  Individual(boolean random) {

    chromosome = new Gene[CHROMOSOME_LENGTH];

    if (random) {
      chromosome[SIDES_IND] = new Gene(SIDES_GENE_LENGTH);
      chromosome[SIZE_IND] = new Gene(SIZE_GENE_LENGTH);
      chromosome[SPIN_IND] = new Gene(SPIN_GENE_LENGTH);
      chromosome[RED_IND] = new Gene(COLOR_GENE_LENGTH);
      chromosome[GREEN_IND] = new Gene(COLOR_GENE_LENGTH);
      chromosome[BLUE_IND] = new Gene(COLOR_GENE_LENGTH);

      setPhenotype();
    } else {
      chromosome[SIDES_IND] = new Gene(SIDES_GENE_LENGTH, false);
      chromosome[SIZE_IND] = new Gene(SIZE_GENE_LENGTH, false);
      chromosome[SPIN_IND] = new Gene(SPIN_GENE_LENGTH, false);
      chromosome[RED_IND] = new Gene(COLOR_GENE_LENGTH, false);
      chromosome[GREEN_IND] = new Gene(COLOR_GENE_LENGTH, false);
      chromosome[BLUE_IND] = new Gene(COLOR_GENE_LENGTH, false);
      
      setPhenotype();
    }
  }//constructor

   public void setPhenotype() {
    int sides = chromosome[SIDES_IND].getValue();
    int siz = chromosome[SIZE_IND].getValue();
    float spin = chromosome[SPIN_IND].getValue() - 7;
    int c = color(chromosome[RED_IND].getValue(), chromosome[GREEN_IND].getValue(), chromosome[BLUE_IND].getValue());
    phenotype = new RegularGon(sides, siz, spin, c);
  }

  /*==========================
    Create a new Individual object based on "mating" the
    calling and parameter objects.

    Child should inherit genes from both parents with crossover:
    Randomly select a number of genes to select from
    one parent, copy those into the chromosome of the
    new, "child" Individual. Then copy the remaining
    Genes from the other parent.

    Do not always start from the same parent. The
    "first" parent should be assigned randomly.
    
    Remember, there is a Gene construtor that takes a Gene object
    and creates a copy. Use it.
    ==========================*/
   public Individual mate(Individual partner) {
    Individual child = new Individual(true);
    for (int i = 0; i < chromosome.length; i++) {
      child.chromosome[i] = new Gene(chromosome[i].genotype.length);
      child.chromosome[i] = (int)random(2) == 1 ? new Gene(chromosome[i].genotype) : new Gene(partner.chromosome[i].genotype);
    }
    return child;
  }//mate
  
  // int[] copyOf(int[] array) {
  //   int[] copy = new int[array.length];
  //   for (int i = 0; i < array.length; i++) {
  //     copy[i] = array[i];
  //   }
  //   return copy;
  // }

   public void setFitness(Individual target) {
    fitness = 0; 
    for (int i = 0; i < chromosome.length; i++) {
      fitness += (1-abs((float)target.chromosome[i].getValue()-chromosome[i].getValue())/(float)Math.pow(2,chromosome[i].genotype.length))/6.0f;
    }
  }//setFitness

   public void mutate(float rate) {
    if (random(1) < rate) {
      chromosome[(int)random(0, CHROMOSOME_LENGTH)].mutate();
      setPhenotype();
    }
  }//mutate

   public void display(int x, int y, boolean showFitness) {
    phenotype.display(x, y);
    if (showFitness) {

      //println(fitness);
      textSize(15);
      fill(0);
      textAlign(CENTER);
      text(fitness, x, y);
    }
  }//display()

   public String toString() {
    String s = "<Individual:\n";
    for (int i=0; i<CHROMOSOME_LENGTH; i++) {
      s+= "  " + chromosome[i] + "\n";
    }
    s+= "  fitness: " + fitness + ">";
    return s;
  }//toString()

}//Individual
class PathShape {

  int MAX_RANDOM_POINTS = 10;
  //x, y point lists
  IntList xs;
  IntList ys;
  //bounding box for the shape
  int topY;
  int leftX;
  int shapeWidth;
  int shapeHeight;

  int centroid[];
  float area;
  float rotationSpeed;
  float displayAngle;

  int inside;
  int border;

  PathShape() {
    xs = new IntList();
    ys = new IntList();
    inside = color(PApplet.parseInt(random(256)), PApplet.parseInt(random(256)), PApplet.parseInt(random(256)));
    border = color(0);
    centroid = new int[2];
    area = 0;
    topY = 0;
    leftX = 0;
    shapeWidth = width;
    shapeHeight = height;
    rotationSpeed = 0;
    displayAngle = 0;
  }//constructor

  PathShape(int lx, int ty, int sw, int sh) {
    this();
    leftX = lx;
    topY = ty;
    shapeWidth = sw;
    shapeHeight = sh;
  }

   public void randomize() {
    xs.clear();
    ys.clear();
    int right_x = leftX + shapeWidth;
    int bottom_y = topY + shapeHeight;
    int numPoints = PApplet.parseInt(random(3, MAX_RANDOM_POINTS+1));
    for (int s=0; s < numPoints; s++) {
      int x = PApplet.parseInt(random(leftX, right_x));
      int y = PApplet.parseInt(random(topY, bottom_y));
      addPoint(x, y);
    }
  }//random PathShape

   public boolean isValid() {
    int right_x = leftX + shapeWidth;
    int bottom_y = topY + shapeHeight;
    boolean valid =  centroid[0] > leftX && centroid[0] < right_x;
    valid = valid && centroid[1] > topY && centroid[1] < bottom_y;
    return valid;
  }//isValid

   public void display() {

    stroke(border);
    fill(inside);
    beginShape();
    for ( int i = 0; i < xs.size(); i++ )
      vertex( xs.get(i), ys.get(i) );
    endShape(CLOSE);
    noStroke();
    fill(0, 0, 255);
    circle(centroid[0], centroid[1], 5);

  }//display

   public void display(int x, int y) {

    stroke(border);
    fill(inside);

    pushMatrix();
    translate(x, y);
    displayAngle+= rotationSpeed;
    rotate(radians(displayAngle));

    beginShape();
    for ( int i = 0; i < xs.size(); i++ )
      vertex( xs.get(i), ys.get(i) );
    endShape(CLOSE);
    noStroke();
    fill(0, 0, 255);
    circle(centroid[0], centroid[1], 5);


    //smiley!
    fill(255);
    stroke(0);
    circle(-20, -20, 20);
    circle(20, -20, 20);
    fill(0);
    circle(-20, -20, 5);
    circle(20, -20, 5);

    noFill();
    strokeWeight(4);
    arc(0, 20, 80, 24, 0, PI);
    strokeWeight(1);


    popMatrix();
  }//display


   public void setCentroid() {
    int sumX = 0;
    int sumY = 0;
    for (int i=0; i < xs.size(); i++ ) {
      int p0,p1;
      p0=i;
      if (i == xs.size()-1) {
        p1 = 0;
      }
      else {
        p1 = i+1;
      }
      sumX += (xs.get(p0) + xs.get(p1)) * ((xs.get(p0) * ys.get(p1)) - (xs.get(p1) * ys.get(p0)));
      sumY += (ys.get(p0) + ys.get(p1)) * ((xs.get(p0) * ys.get(p1)) - (xs.get(p1) * ys.get(p0)));
    }
    setArea();
    centroid[0] = PApplet.parseInt( (1 / (6 * area)) * sumX );
    centroid[1] = PApplet.parseInt( (1 / (6 * area)) * sumY );
  }//setCent

   public void setArea() {
    area = 0;
    for ( int i=0; i < xs.size(); i++) {
      int p0, p1;
      p0 = i;
      if (i == xs.size()-1) {
        p1 = 0;
      }
      else {
        p1 = i+1;
      }
      area += (xs.get(p0) * ys.get(p1)) - (xs.get(p1) * ys.get(p0));
    }
    area = area * 0.5f;
  }//setArea

   public void addPoint(int x, int y) {
    xs.append(x);
    ys.append(y);

    setCentroid();
  }//addPoint


}//class PathShape
class Population {
  float totalFitness,mutationRate;
  Individual[] pop;
  
  public Population(int popSize) {
    pop = new Individual[popSize];
    mutationRate = 0.05f;
    totalFitness = 0;
  }
  
  public void randomPop() {
    for (int i = 0; i < pop.length; i++) {
      pop[i] = new Individual(true);
    }
  }
  
  public void drawPopGrid(int cols, int rows, int gridSize, int offset, boolean showFitness) {
    for (int i = 0; i < pop.length; i++) {
      int x = i / rows;
      int y = i % rows;
      pop[i].display(gridSize * (x) + gridSize / 2 + offset, gridSize * (y) + gridSize / 2 + offset, showFitness);
    }
  }
  
  public Individual get(int index) {
    return pop[index];  
  }
  
   public void setFitness(Individual target) {
    totalFitness = 0;
    for (int i = 0; i < pop.length; i++) {
      pop[i].setFitness(target);
      totalFitness += pop[i].fitness;
    }
  }

  public Population matingSeason() {
    Population p = new Population(pop.length);
    for (int i = 0; i < p.pop.length; i++) {
      p.pop[i] = new Individual(true);
    }
    p.pop[0] = pop[0];
    p.mutationRate = mutationRate;
    for (int i = 1; i < p.pop.length; i++) {
      Individual p1 = select();
      Individual p2 = select();
      Individual child = p1.mate(p2);
      child.mutate(mutationRate);
      p.pop[i] = child;
    }
    return p;
  }

   public Individual select() {
    return pop[rouletteSelection()];
  }

  private int rouletteSelection() {
    float r = random(totalFitness-pop[0].fitness);
    float runningFitness = 0;
    for (int i = 1; i < pop.length; i++) {
      runningFitness += pop[i].fitness;
      if (r < runningFitness) {
        return i-1;
      }
    }
    return pop.length - 1;
  }
}
class RegularGon extends PathShape {
  int MAX_SIDES = 20;
  int numSides;
  int gonLength;

  RegularGon(int lx, int ty, int sw) {
    super(lx, ty, sw, sw);
    numSides = 0;
    gonLength = 0;
  }

  RegularGon(int cx, int cy, int sides, int length) {
    centroid[0] = cx;
    centroid[1] = cy;
    numSides = sides;
    gonLength = length;
    leftX = centroid[0] - gonLength;
    topY = centroid[1] - gonLength;
    shapeWidth = gonLength;
    shapeHeight = gonLength;

    generateRegularPolygon();
  }//PathShape

  RegularGon(int sides, int length, float rSpeed, int c) {
    this(0, 0, sides, length);
    rotationSpeed = rSpeed;
    inside = c;
  }//PathShape

   public boolean isValid() {
    return xs.size() > 0;
  }//isValid

   public void randomize() {
    centroid[0] = leftX + shapeWidth/2;
    centroid[1] = topY+ shapeHeight/2;
    numSides = PApplet.parseInt(random(3, MAX_SIDES+1));

    gonLength = PApplet.parseInt(random(5, shapeWidth/2));
    generateRegularPolygon();
  }

   public void generateRegularPolygon() {
    if (numSides > 0) {
      float theta = radians(360 / numSides);
      for (int n=0; n < numSides; n++) {
        int x = PApplet.parseInt(gonLength * cos(n * theta)) + centroid[0];
        int y = PApplet.parseInt(gonLength * sin(n * theta)) + centroid[1];
        xs.append(x);
        ys.append(y);
      }
    }
  }//generateRegularPolygon

}//RegularGon


  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ItsAliveLab_01_blank" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}