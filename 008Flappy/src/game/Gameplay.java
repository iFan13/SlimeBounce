package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Gameplay implements ActionListener, MouseListener, KeyListener
{
	public static Gameplay flappyBird;
	private static final int WIDTH = 1280, HEIGHT =WIDTH*9/16;
	public Renderer renderer;
	public Rectangle bird;
	public ArrayList<Rectangle> columns;
	public int ticks,yMotion,score;
	public boolean gameOver, started;
	public Random rand;
	private int birdSize = (int) Math.round(Double.valueOf(HEIGHT)*0.05);
	
	public static void main(String[] args)
	{
		flappyBird = new Gameplay();
	}
	
	public Gameplay()
	{
		//initialize frame object & timer
		JFrame jframe = new JFrame();
		Timer timer = new Timer(20,this);
		
		//initialize rendering panel & random for columns
		renderer = new Renderer();
		rand = new Random();
		
		//window & settings
		jframe.add(renderer);
		jframe.setTitle("Flappy Bird");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(WIDTH, HEIGHT);
		jframe.addMouseListener(this);
		jframe.addKeyListener(this);
		jframe.setResizable(false);
		jframe.setVisible(true);
				
		bird = new Rectangle(WIDTH / 4 - birdSize/2, HEIGHT / 2 - birdSize/2, birdSize, birdSize);
		
		columns = new ArrayList<Rectangle>();
		
		addColumn(true); 
		addColumn(true); //queue second & 3rd pipe, adjusted by width & positional spacing
		addColumn(true);
		
		timer.start();

	}
	
	private void addColumn(boolean start) 	{
		int space = (int) Math.round(Double.valueOf(HEIGHT)*0.5);
		int width = (int) Math.round(Double.valueOf(WIDTH)*0.125);
		int height = (int) Math.round(Double.valueOf(HEIGHT)*0.10) + rand.nextInt((int) Math.round(Double.valueOf(HEIGHT)*0.5));

		if (start)
		{
			//positional spacing between columns: (int) Math.round(Double.valueOf(WIDTH)*0.375) & (int) Math.round(Double.valueOf(WIDTH)*0.75)
			
			//bottom of pipe
			columns.add(new Rectangle(WIDTH + width +  columns.size()    * (int) Math.round(Double.valueOf(WIDTH)*0.375), HEIGHT - height - (int) Math.round(Double.valueOf(HEIGHT)*0.2) /*ground height*/, width, height));
			//top of pipe
			columns.add(new Rectangle(WIDTH + width + (columns.size()-1) * (int) Math.round(Double.valueOf(WIDTH)*0.375), 0, width, HEIGHT - height - space));
		}
		
		else
		{
			//bottom of pipe
			columns.add(new Rectangle(columns.get(columns.size() - 1).x + (int) Math.round(Double.valueOf(WIDTH)*0.75), HEIGHT - height - (int) Math.round(Double.valueOf(HEIGHT)*0.2), width, height));
			//top of pipe
			columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
		}
		
	}

	private void paintColumn(Graphics g, Rectangle column){
		g.setColor(Color.green.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}

	public void jump()
	{
		if (gameOver)
		{
			bird = new Rectangle(WIDTH / 4 - birdSize/2, HEIGHT / 2 - birdSize/2, birdSize, birdSize);
			columns.clear();
			yMotion = 0;
			score = 0;
			addColumn(true);
			addColumn(true);
			gameOver = false;
		}

		if (!started)
		{
			started = true;
		}
		
		else if (!gameOver)
		{
			if (yMotion > 0)
			{
				yMotion = 0;
			}

			yMotion -= 10;
		}
	}

	public void repaint(Graphics g) {
		
		g.setColor(Color.cyan);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		g.setColor(Color.orange);
		g.fillRect(0, HEIGHT - (int) Math.round(Double.valueOf(HEIGHT)*0.2) , WIDTH, (int) Math.round(Double.valueOf(HEIGHT)*0.2));
		// (int) Math.round(Double.valueOf(HEIGHT)*0.2) = ground height
		
		g.setColor(Color.green);
		g.fillRect(0, HEIGHT - (int) Math.round(Double.valueOf(HEIGHT)*0.2), WIDTH, (int) Math.round(Double.valueOf(HEIGHT)*0.03));

		g.setColor(Color.red);
		g.fillOval(bird.x, bird.y, bird.width, bird.height);

		for (Rectangle column : columns)
		{
			paintColumn(g, column);
		}

		g.setColor(Color.white);
		g.setFont(new Font("Arial", 1, (int) Math.round(Double.valueOf(HEIGHT)*0.15)));

		if (!started)
		{
			g.drawString("Click to start!", WIDTH/6, HEIGHT / 2 - HEIGHT/16);
		}

		if (gameOver)
		{
			g.drawString("Game Over!", WIDTH/4, HEIGHT / 2 - HEIGHT/16);
			g.drawString(String.valueOf(score), WIDTH / 2 - HEIGHT/32, (int) Math.round(Double.valueOf(HEIGHT)*0.2));
		}

		if (!gameOver && started)
		{
			g.drawString(String.valueOf(score), WIDTH / 2 - HEIGHT/32, (int) Math.round(Double.valueOf(HEIGHT)*0.2));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		int speed = 10;

		ticks++;

		if (started)
		{
			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);
				column.x -= speed;
			}

			//create falling acceleration; increases fall speed every tick
			if (ticks % 2 == 0 && yMotion < (int) Math.round(Double.valueOf(HEIGHT)*0.03))
			{
				yMotion += 2;
			}

			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);
				if (column.x + column.width < 0) //if column is off screen
				{
					columns.remove(column); //removes column 
					if (column.y == 0) //if what was removed was top column 
					{
						addColumn(false); //call addColumn; with false, always adds column 
					}
				}
			}

			bird.y += yMotion;

			for (Rectangle column : columns) 
			{
				if (column.y == 0 && bird.x + bird.width/2 > column.x+column.width/4-10 && bird.x + bird.width/2 < column.x+column.width/4 + 10) //-10to+10's being timer delay 
				{
					score++;
				}

				if (column.intersects(bird))
				{
					gameOver = true;

					if (bird.x <= column.x) //collision front side of pipe
					{
						bird.x = column.x - bird.width; //pipe pushes back
					}
					else	
					{
						if (column.y != 0)  //hits bottom of top pipe 
						{
							bird.y = column.y - bird.height;
						}
						else if (bird.y < column.height) //lands on pipe
						{
							bird.y = column.height;
						}
					}
				}
			}

			if (bird.y > HEIGHT - (int) Math.round(Double.valueOf(HEIGHT)*0.2) || bird.y < 0)
			{
				gameOver = true;
			}

			if (bird.y + yMotion >= HEIGHT - (int) Math.round(Double.valueOf(HEIGHT)*0.2))
			{
				bird.y = HEIGHT - (int) Math.round(Double.valueOf(HEIGHT)*0.2) - bird.height;
				gameOver = true;
			}
		}

		renderer.repaint();
	}


	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			jump();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		jump();	
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {		
	}
	
	
}

