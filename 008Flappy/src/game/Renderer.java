package game;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Renderer extends JPanel
{

	private static final long serialVersionUID = 1L;

	@Override
	protected void paintComponent(Graphics g)
	{
		//adds method paintComponent to panel
		super.paintComponent(g);
		
		//calls the repaint; where first time repaint is blank but is now able to paint due to adding paintComponent method being added above
		Gameplay.flappyBird.repaint(g);
	}
	
}
