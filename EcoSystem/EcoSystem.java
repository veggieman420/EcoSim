/*
 * [EcoSystem.java]
 * An ecosystem simulator
 * Author: Josh Cai
 * Dec. 10, 2017
*/

import java.util.*;
class EcoSystem { 
  
  
  public static void main(String[] args) { 
    
    Scanner sc = new Scanner (System.in);
    
    //Greetings and initial configurations 
    System.out.println("Welcome to EcoSim, the #1 most realistic ecosystem simulator as voted for by Peoples Magazine!");
    
    System.out.println("Initial Configuration");
    System.out.print("Map size (Don't go too big, it'll look cool but won't work as well): ");
    int size = sc.nextInt();
    Life map[][] = new Life[size][size];
    System.out.print("Plant spawn rate (%): ");
    int plantRate = sc.nextInt();
    System.out.print("Sheep spawn rate (%): ");
    int sheepRate = sc.nextInt();
    System.out.print("Wolf spawn rate (%): ");
    int wolfRate = sc.nextInt();
    System.out.print("Plant spawn health: ");
    int plantHealth = sc.nextInt();
    System.out.print("Sheep spawn health: ");
    int sheepHealth = sc.nextInt();
    System.out.print("Wolf spawn health: ");
    int wolfHealth = sc.nextInt();
    
    int days = 0;
    
    // Initialize Map
    initializeGrid(map, plantRate, sheepRate, wolfRate, plantHealth, sheepHealth, wolfHealth);
    
    //Set up Grid Panel
    DisplayGrid grid = new DisplayGrid(map);
    
    //Display the grid on a Panel
    grid.refresh();
    
    while((numSheep(map) != 0) && (numWolf(map) != 0) && (numPlant(map) != 0)){ //Run until one species is extinct
      //Small delay
      try{ Thread.sleep(750); }catch(Exception e) {};
      
      
      //Making changes to map
      moveItemsOnGrid(map, plantRate, plantHealth, sheepHealth);
      canMove(map);
      //Display the grid on a Panel
      grid.refresh();
      
      days++;
    }
    
    //Display ending stats
    System.out.println("Sheep left: " + numSheep(map));
    System.out.println("Wolves left: " + numWolf(map));
    System.out.println("Plants left: " + numPlant(map));
    System.out.println("Days elapsed: " + days);
  }
  
  public static int numSheep(Life[][] map){  //Method to count sheep
    int num = 0;
    for(int i = 0; i<map[0].length;i++){        
      for(int j = 0; j<map.length;j++){ 
        if (map[i][j] instanceof Sheep || map[i][j] instanceof BabySheep){
          num++;
        }
      }
    }
    return num;
  }
  
  public static int numWolf(Life[][] map){  //Method to count wolves
    int num = 0;
    for(int i = 0; i<map[0].length;i++){        
      for(int j = 0; j<map.length;j++){ 
        if (map[i][j] instanceof Wolf){
          num++;
        }
      }
    }
    return num;
  }
  
  public static int numPlant(Life[][] map){  //Method to count plants
    int num = 0;
    for(int i = 0; i<map[0].length;i++){        
      for(int j = 0; j<map.length;j++){ 
        if (map[i][j] instanceof Plant){
          num++;
        }
      }
    }
    return num;
  }
  
  public static void canMove(Life[][] map){  ////Method to reset all animals to moveable
    for(int i = 0; i<map[0].length;i++){        
      for(int j = 0; j<map.length;j++){ 
        if (map[i][j] != null){
          map[i][j].changeMove(true);
        }
      }
    }
  }
  
  public static boolean spaceFree(Life[][] map){  //Method to check free spaces
    boolean b = false;
    for(int i = 0; i<map[0].length;i++){        
      for(int j = 0; j<map.length;j++){ 
        if (map[i][j] == null){
          b = true;
        }
      }
    }
    return b;
  }
  
  // Method to initialize grid
  public static void initializeGrid(Life[][] map, int plantRate, int sheepRate, int wolfRate, int plantHealth, int sheepHealth, int wolfHealth) { 
    Random r = new Random();
    int random = 0;
    int count = 0;
    int count2 = (int)(map.length/5);
    if (count2 <= 0){
      count2 = 1;
    }
    int x = 0;
    int y = 0;
    
    for(int i = 0; i<map[0].length;i++){        
      for(int j = 0; j<map.length;j++){ 
        
        random = r.nextInt(100) + 1;
        
        if (random <= plantRate){
          if (count <= count2){
            while (true){
              x = r.nextInt(map[0].length);
              y = r.nextInt(map.length);
              
              if (map[x][y] == null){
                map[x][y] = new Plant(plantHealth);   //A set number of plants spawn randomly; after that, all plants must spawn next to another plant
                count++;
                break;
              }
            }
          }
          else{
            if (i != 0 && map[i-1][j] instanceof Plant){
              map[i][j] = new Plant(plantHealth);
            }
            if (i != map[0].length-1 && map[i+1][j] instanceof Plant){
              map[i][j] = new Plant(plantHealth);
            }
            if (j != 0 && map[i][j-1] instanceof Plant){
              map[i][j] = new Plant(plantHealth);
            }
            if (j != map.length-1 && map[i][j+1] instanceof Plant){
              map[i][j] = new Plant(plantHealth);
            }
          }
        }
        else if ((random <= plantRate + sheepRate) && (map[i][j] == null)){
          map[i][j] = new Sheep(sheepHealth);
        }
        else if ((random <= plantRate + sheepRate + wolfRate) && (map[i][j] == null)){
          map[i][j] = new Wolf(wolfHealth);
        }
        
        
      }
    }
  }
  
  // Method to simulate grid movement
  public static void moveItemsOnGrid(Life[][] map, int plantRate, int plantHealth, int sheepHealth) { 
    Random r = new Random();
    int move = 0;
    int direction = 0;
    int spawn = 0;
    int x = 0;
    int y = 0;
    
    for(int i = 0; i<map[0].length;i++){        
      for(int j = 0; j<map.length;j++){ 
        
        if(map[i][j] instanceof Sheep){ /////////////////////////////////IF SHEEP/////////////////////////////
          
          map[i][j].hunger();
          if(map[i][j].returnHealth() <= 0){
            map[i][j] = new Blood (3);
          }
          else{
            move = r.nextInt(10) + 1;
            
            if (move >= 4 && map[i][j].returnMove() == true){
              direction = r.nextInt(4) + 1;
              
              if (i != map[0].length-1 && direction == 1){                 //move down
                if (map[i+1][j] instanceof Sheep){
                  if ((map[i][j].returnHealth() > 20) && (map[i+1][j].returnHealth() > 20)){
                    while (spaceFree(map)){
                      x = r.nextInt(map[0].length);
                      y = r.nextInt(map.length);
                      
                      if(map[x][y] == null){
                        map[x][y] = new BabySheep(sheepHealth);
                        map[i][j].loseHealth(10);
                        map[i+1][j].loseHealth(10);
                        break;
                      }
                    }
                  }
                }
                else if (map[i+1][j] instanceof Wolf){
                  map[i+1][j].eat(map[i][j].returnHealth());
                  map[i][j] = new Blood(3);
                }
                else if (map[i+1][j] instanceof Plant){
                  map[i][j].eat(map[i+1][j].returnHealth());
                  map[i+1][j] = null;
                  map[i+1][j] = map[i][j];
                  map[i][j] = null;
                }
                else if (map[i+1][j] instanceof BabySheep){
                }
                else{
                  map[i+1][j] = map[i][j];
                  map[i][j] = null;
                }
                map[i+1][j].changeMove(false);
              }
              else if(i != 0 && direction == 2){               //move up
                if (map[i-1][j] instanceof Sheep){
                  if ((map[i][j].returnHealth() > 20) && (map[i-1][j].returnHealth() > 20)){
                    while (spaceFree(map)){
                      x = r.nextInt(map[0].length);
                      y = r.nextInt(map.length);
                      
                      if(map[x][y] == null){
                        map[x][y] = new BabySheep(sheepHealth);
                        map[i][j].loseHealth(10);
                        map[i-1][j].loseHealth(10);
                        break;
                      }
                    }
                  }
                }
                else if (map[i-1][j] instanceof Wolf){
                  map[i-1][j].eat(map[i][j].returnHealth());
                  map[i][j] = new Blood(3);
                }
                else if (map[i-1][j] instanceof Plant){
                  map[i][j].eat(map[i-1][j].returnHealth());
                  map[i-1][j] = null;
                  map[i-1][j] = map[i][j];
                  map[i][j] = null;
                }
                else if (map[i-1][j] instanceof BabySheep){
                }
                else{
                  map[i-1][j] = map[i][j];
                  map[i][j] = null;
                }
                map[i-1][j].changeMove(false);
              }
              else if(j != 0 && direction == 3){               //move left
                if (map[i][j-1] instanceof Sheep){
                  if ((map[i][j].returnHealth() > 20) && (map[i][j-1].returnHealth() > 20)){
                    while (spaceFree(map)){
                      x = r.nextInt(map[0].length);
                      y = r.nextInt(map.length);
                      
                      if(map[x][y] == null){
                        map[x][y] = new BabySheep(sheepHealth);
                        map[i][j].loseHealth(10);
                        map[i][j-1].loseHealth(10);
                        break;
                      }
                    }
                  }
                }
                else if (map[i][j-1] instanceof Wolf){
                  map[i][j-1].eat(map[i][j].returnHealth());
                  map[i][j] = new Blood(3);
                }
                else if (map[i][j-1] instanceof Plant){
                  map[i][j].eat(map[i][j-1].returnHealth());
                  map[i][j-1] = null;
                  map[i][j-1] = map[i][j];
                  map[i][j] = null;
                }
                else if (map[i][j-1] instanceof BabySheep){
                }
                else{
                  map[i][j-1] = map[i][j];
                  map[i][j] = null;
                }
                map[i][j-1].changeMove(false);
              }
              else if(j != map.length-1) {               //move right
                if (map[i][j+1] instanceof Sheep){
                  if ((map[i][j].returnHealth() > 20) && (map[i][j+1].returnHealth() > 20)){
                    while (spaceFree(map)){
                      x = r.nextInt(map[0].length);
                      y = r.nextInt(map.length);
                      
                      if(map[x][y] == null){
                        map[x][y] = new BabySheep(sheepHealth);
                        map[i][j].loseHealth(10);
                        map[i][j+1].loseHealth(10);
                        break;
                      }
                    }
                  }
                }
                else if (map[i][j+1] instanceof Wolf){
                  map[i][j+1].eat(map[i][j].returnHealth());
                  map[i][j] = new Blood(3);
                }
                else if (map[i][j+1] instanceof Plant){
                  map[i][j].eat(map[i][j+1].returnHealth());
                  map[i][j+1] = null;
                  map[i][j+1] = map[i][j];
                  map[i][j] = null;
                }
                else if (map[i][j+1] instanceof BabySheep){
                }
                else{
                  map[i][j+1] = map[i][j];
                  map[i][j] = null;
                }
                map[i][j+1].changeMove(false);
              }
            }
            
          }
        }
        else if (map[i][j] instanceof Wolf){ ////////////////////////////IF WOLF///////////////////////////////
          
          map[i][j].hunger();
          if (map[i][j].returnHealth() <= 0){
            map[i][j] = new Blood(3);
          }
          else{
            move = r.nextInt(10) + 1;
            
            if (move >= 4 && map[i][j].returnMove() == true){
              direction = r.nextInt(4) + 1;
              
              if (i != map[0].length-1 && direction == 1){                 //move down
                if (map[i+1][j] instanceof Sheep || map[i+1][j] instanceof BabySheep){
                  map[i][j].eat(map[i+1][j].returnHealth());
                  map[i+1][j] = null;
                  map[i+1][j] = map[i][j];
                  map[i][j] = new Blood(3);
                }
                else if (map[i+1][j] instanceof Wolf){
                  if (map[i][j].compareTo(map[i+1][j]) == 1){ 
                    map[i+1][j].loseHealth(10);
                  }
                  else if (map[i][j].compareTo(map[i+1][j]) == -1){
                    map[i][j].loseHealth(10);
                  }
                }
                else if (map[i+1][j] instanceof Plant){
                  map[i+1][j] = null;
                  map[i+1][j] = map[i][j];
                  map[i][j] = null;
                }
                else{
                  map[i+1][j] = map[i][j];
                  map[i][j] = null;
                }
                map[i+1][j].changeMove(false);
              }
              else if(i != 0 && direction == 2){               //move up
                if (map[i-1][j] instanceof Sheep || map[i-1][j] instanceof BabySheep){
                  map[i][j].eat(map[i-1][j].returnHealth());
                  map[i-1][j] = null;
                  map[i-1][j] = map[i][j];
                  map[i][j] = new Blood(3);
                }
                else if (map[i-1][j] instanceof Wolf){
                  if (map[i][j].compareTo(map[i-1][j]) == 1){ 
                    map[i-1][j].loseHealth(10);
                  }
                  else if (map[i][j].compareTo(map[i-1][j]) == -1){
                    map[i][j].loseHealth(10);
                  }
                }
                else if (map[i-1][j] instanceof Plant){
                  map[i-1][j] = null;
                  map[i-1][j] = map[i][j];
                  map[i][j] = null;
                }
                else{
                  map[i-1][j] = map[i][j];
                  map[i][j] = null;
                }
                map[i-1][j].changeMove(false);
              }
              else if(j != 0 && direction == 3){               //move left
                if (map[i][j-1] instanceof Sheep || map[i][j-1] instanceof BabySheep){
                  map[i][j].eat(map[i][j-1].returnHealth());
                  map[i][j-1] = null;
                  map[i][j-1] = map[i][j];
                  map[i][j] = new Blood(3);
                }
                else if (map[i][j-1] instanceof Wolf){
                  if (map[i][j].compareTo(map[i][j-1]) == 1){ 
                    map[i][j-1].loseHealth(10);
                  }
                  else if (map[i][j].compareTo(map[i][j-1]) == -1){
                    map[i][j].loseHealth(10);
                  }
                }
                else if (map[i][j-1] instanceof Plant){
                  map[i][j-1] = null;
                  map[i][j-1] = map[i][j];
                  map[i][j] = null;
                }
                else{
                  map[i][j-1] = map[i][j];
                  map[i][j] = null;
                }
                map[i][j-1].changeMove(false);
              }
              else if(j != map.length-1) {               //move right
                if (map[i][j+1] instanceof Sheep || map[i][j+1] instanceof BabySheep){
                  map[i][j].eat(map[i][j+1].returnHealth());
                  map[i][j+1] = null;
                  map[i][j+1] = map[i][j];
                  map[i][j] = new Blood(3);
                }
                else if (map[i][j+1] instanceof Wolf){
                  if (map[i][j].compareTo(map[i][j+1]) == 1){ 
                    map[i][j+1].loseHealth(10);
                  }
                  else if (map[i][j].compareTo(map[i][j+1]) == -1){
                    map[i][j].loseHealth(10);
                  }
                }
                else if (map[i][j+1] instanceof Plant){
                  map[i][j+1] = null;
                  map[i][j+1] = map[i][j];
                  map[i][j] = null;
                }
                else{
                  map[i][j+1] = map[i][j];
                  map[i][j] = null;
                }
                map[i][j+1].changeMove(false);
              }
            }
          }
        }
        else if (map[i][j] instanceof Plant){     //////////////////////////////IF PLANT////////////////
          
          map[i][j].hunger();
          
          if (map[i][j].returnHealth() <= 0){
            map[i][j] = null;
          }
        }
        else if (map[i][j] instanceof Blood){    ///////////////////////IF BLOOD///////////////
          map[i][j].hunger();
          
          if (map[i][j].returnHealth() <= 0){
            map[i][j] = null;
          }
        }
        else if (map[i][j] instanceof BabySheep){ ////////////////////////////IF BABYSHEEP///////////////////////////////
          
          map[i][j].grow();
          map[i][j].hunger();
          if (map[i][j].returnHealth() <= 0){
            map[i][j] = new Blood(3);
          }
          else if (map[i][j].returnAge() >= 20){
            map[i][j] = new Sheep(sheepHealth);
          }
          else{
            move = r.nextInt(10) + 1;
            
            if (move >= 4 && map[i][j].returnMove() == true){
              direction = r.nextInt(4) + 1;
              
              if (i != map[0].length-1 && direction == 1){                 //move down
                if (map[i+1][j] instanceof Sheep || map[i+1][j] instanceof BabySheep){
                }
                else if (map[i+1][j] instanceof Wolf){
                  map[i+1][j].eat(map[i][j].returnHealth());
                  map[i][j] = new Blood(3);
                }
                else if (map[i+1][j] instanceof Plant){
                  map[i][j].eat(map[i+1][j].returnHealth());
                  map[i+1][j] = null;
                  map[i+1][j] = map[i][j];
                  map[i][j] = null;
                }
                else{
                  map[i+1][j] = map[i][j];
                  map[i][j] = null;
                }
                map[i+1][j].changeMove(false);
              }
              else if(i != 0 && direction == 2){               //move up
                if (map[i-1][j] instanceof Sheep || map[i-1][j] instanceof BabySheep){
                }
                else if (map[i-1][j] instanceof Wolf){
                  map[i-1][j].eat(map[i][j].returnHealth());
                  map[i][j] = new Blood(3);
                }
                else if (map[i-1][j] instanceof Plant){
                  map[i][j].eat(map[i-1][j].returnHealth());
                  map[i-1][j] = null;
                  map[i-1][j] = map[i][j];
                  map[i][j] = null;
                }
                else{
                  map[i-1][j] = map[i][j];
                  map[i][j] = null;
                }
                map[i-1][j].changeMove(false);
              }
              else if(j != 0 && direction == 3){               //move left
                if (map[i][j-1] instanceof Sheep || map[i][j-1] instanceof BabySheep){
                }
                else if (map[i][j-1] instanceof Wolf){
                  map[i][j-1].eat(map[i][j].returnHealth());
                  map[i][j] = new Blood(3);
                }
                else if (map[i][j-1] instanceof Plant){
                  map[i][j].eat(map[i][j-1].returnHealth());
                  map[i][j-1] = null;
                  map[i][j-1] = map[i][j];
                  map[i][j] = null;
                }
                else{
                  map[i][j-1] = map[i][j];
                  map[i][j] = null;
                }
                map[i][j-1].changeMove(false);
              }
              else if(j != map.length-1) {               //move right
                if (map[i][j+1] instanceof Sheep || map[i][j+1] instanceof BabySheep){
                }
                else if (map[i][j+1] instanceof Wolf){
                  map[i][j+1].eat(map[i][j].returnHealth());
                  map[i][j] = new Blood(3);
                }
                else if (map[i][j+1] instanceof Plant){
                  map[i][j].eat(map[i][j+1].returnHealth());
                  map[i][j+1] = null;
                  map[i][j+1] = map[i][j];
                  map[i][j] = null;
                }
                else{
                  map[i][j+1] = map[i][j];
                  map[i][j] = null;
                }
                map[i][j+1].changeMove(false);
              }
            }
          }
        }
        else{     //////////////////////////////IF BLANK SPACE///////////////////////
          spawn = r.nextInt(100) + 1;
          
          if (spawn <= plantRate){
            while (true){
              x = r.nextInt(map[0].length);
              y = r.nextInt(map.length);
              
              if (x != 0 && map[x-1][y] instanceof Plant && map[x][y] == null){
                map[x][y] = new Plant(plantHealth);
                break;
              }
              if (x != map[0].length-1 && map[x+1][y] instanceof Plant && map[x][y] == null){
                map[x][y] = new Plant(plantHealth);
                break;
              }
              if (y != 0 && map[x][y-1] instanceof Plant && map[x][y] == null){
                map[x][y] = new Plant(plantHealth);
                break;
              }
              if (y != map.length-1 && map[x][y+1] instanceof Plant && map[x][y] == null){
                map[x][y] = new Plant(plantHealth);
                break;
              }
            }
            
          }
          
        } 
      }
      
    }
  }
  
}