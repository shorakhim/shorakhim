import javax.swing.*;  //import graphics window
import java.awt.*;  //imports colors shapes and fonts
import java.awt.event.*;    //imports keyboard and timer
import java.util.Random;    //imports random ints

public class SpaceRaceGame extends JPanel implements ActionListener {

Timer timer = new Timer(30,this);   //timer runs loop 30 milli sec
Random r = new Random(); 

String screen = "start";    //display starting screen

// players
int p1x = 150;  //pos of player 1 
int p2x = 350;  //pos of player 2
int y = 650;    //both players move up together

//set life for players
int life1 = 5;  //5 bar of health 
int life2 = 5;

//hit invicibility flags
boolean hit1 = false;
boolean hit2 = false;

//hit timer for blinker 
int hitTimer1 = 0;
int hitTimer2 = 0;

// obstacles
int[] ox = new int[6];  //obstacle positions
int[] oy = new int[6];
int speed = 5;  //obstacle speed

// stars
int[] sx = new int[50]; //stars loc
int[] sy = new int[50];

// explosion
int ex=0;
int ey=0;
int boom=0;

int flash=0;
int counter=0;

//game mechanics
public SpaceRaceGame(){

setFocusable(true); //allows keyboard inputs

for(int i=0;i<6;i++){   //generate obstacles at random places
ox[i]=r.nextInt(500);
oy[i]=r.nextInt(800);
}

for(int i=0;i<50;i++){  //generate stars at random places
sx[i]=r.nextInt(600);
sy[i]=r.nextInt(800);
}

//keyboard  controls
addKeyListener(new KeyAdapter(){
public void keyPressed(KeyEvent e){

int k=e.getKeyCode();

if(screen.equals("start")){ //starts game
screen="play";
timer.start();
return;
}

if(screen.equals("over")){  //ends game
reset();
screen="start";
repaint();
return;
}
//player 1 movements
if(k==65)p1x-=20;
if(k==68)p1x+=20;
//player 2 movements
if(k==37)p2x-=20;
if(k==39)p2x+=20;
//speed boost
if(k==87)y-=30;
}
});
}
//reset game
void reset(){
life1=5;
life2=5;
p1x=150;
p2x=350;
y=650;
speed=5;
counter=0;
}

void explode(int x,int y){
ex=x;   
ey=y;
boom=15;
}
// set game loop
public void actionPerformed(ActionEvent e){

if(!screen.equals("play")) return;

counter++;  //count frames

if(counter%300==0){     // increase difficulty 
speed++;
}

for(int i=0;i<6;i++){

oy[i]+=speed;

if(oy[i]>800){
oy[i]=0;
ox[i]=r.nextInt(500);
}
// detect if playe 1 is hit
if(p1x+20>ox[i] && p1x<ox[i]+60 &&
y+35>oy[i] && y<oy[i]+10 && !hit1){

life1--;
p1x=150;
hit1=true;
hitTimer1=40;
flash=8;
explode(p1x,y);
Toolkit.getDefaultToolkit().beep();
}
// detect if player 2 is hit 
if(p2x+20>ox[i] && p2x<ox[i]+60 &&
y+35>oy[i] && y<oy[i]+10 && !hit2){

life2--;
p2x=350;
hit2=true;
hitTimer2=40;
flash=8;
explode(p2x,y);
Toolkit.getDefaultToolkit().beep();
}
}

if(hit1){
hitTimer1--;
if(hitTimer1<=0) hit1=false;
}

if(hit2){
hitTimer2--;
if(hitTimer2<=0) hit2=false;
}

if(flash>0) flash--;
if(boom>0) boom--;

y--;

if(y<40){
y=650;
speed++;
}

if(life1<=0 || life2<=0){   //end game if player loses all lives
screen="over";
timer.stop();
}

repaint();  //redraw screen
}
// draw rocket 
void rocket(Graphics g,int x,int y,Color c){

if(c==Color.red && hit1 && hitTimer1%4<2) return;
if(c==Color.cyan && hit2 && hitTimer2%4<2) return;

g.setColor(c);
g.fillRect(x+5,y,10,30);
g.fillRect(x,y+8,20,8);

g.setColor(Color.white);
g.fillRect(x+7,y+5,6,5);

if(System.currentTimeMillis()/150%2==0){
g.setColor(Color.orange);
g.fillRect(x+8,y+30,4,8);
}
}
// draw life bars
void life(Graphics g,int x,int y,int n,Color c){

for(int i=0;i<5;i++){

if(i<n)
g.setColor(c);
else
g.setColor(Color.darkGray);

g.fillRect(x+i*25,y,18,10);

g.setColor(Color.white);
g.drawRect(x+i*25,y,18,10);
}
}
// draw whole screen 
public void paintComponent(Graphics g){
super.paintComponent(g);

g.setColor(Color.black);    //set background color
g.fillRect(0,0,600,800);    

// stars
for(int i=0;i<50;i++){
g.setColor(Color.white);
g.fillRect(sx[i],sy[i],2,2);

sy[i]+=2;

if(sy[i]>800){
sy[i]=0;
sx[i]=r.nextInt(600);
}
}

// scan lines
for(int i=0;i<800;i+=4){
g.setColor(new Color(40,40,40));
g.drawLine(0,i,600,i);
}

if(flash>0){
g.setColor(new Color(255,0,0,70));
g.fillRect(0,0,600,800);
}

if(screen.equals("start")){
g.setColor(Color.green);
g.setFont(new Font("Monospaced",1,34));
g.drawString("SPACE RACE",170,300);

if(System.currentTimeMillis()/500%2==0)
g.drawString("PRESS KEY",180,370);

return;
}

if(screen.equals("over")){
g.setColor(Color.yellow);
g.setFont(new Font("Monospaced",1,28));

if(life1>0)
g.drawString("PLAYER 1 WINS",120,320);
else
g.drawString("PLAYER 2 WINS",120,320);

g.drawString("PRESS KEY",180,390);
return;
}

// obstacles
g.setColor(Color.white);
for(int i=0;i<6;i++){
g.fillRect(ox[i],oy[i],60,10);
}

// explosion
if(boom>0){
g.setColor(Color.yellow);

for(int i=0;i<20;i++){
int px=ex+r.nextInt(30)-15;
int py=ey+r.nextInt(30)-15;
g.fillRect(px,py,3,3);
}
}

rocket(g,p1x,y,Color.red);
rocket(g,p2x,y,Color.cyan);

g.setColor(Color.green);
g.drawLine(0,40,600,40);

g.setColor(Color.black);

g.drawString("P1",20,25);
life(g,50,15,life1,Color.green);

g.drawString("P2",400,25);
life(g,430,15,life2,Color.cyan);

g.drawString("SPD "+speed,270,25);
}

public static void main(String[] args){

JFrame f = new JFrame("Space Race");
SpaceRaceGame game = new SpaceRaceGame();

f.add(game);
f.setSize(600,800);
f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
f.setResizable(false);
f.setVisible(true);

}

}
