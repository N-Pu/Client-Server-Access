import javax.swing.*;
import java.awt.*;

class MyCanvas extends JPanel
{
    private String pointClicked = "";
    private int x = 0;
    private int y = 0;


    MyCanvas (int width, int height) {
        this.setPreferredSize(new Dimension(width,height));

    }

    public void setValues(String point, int x, int y)
    {
        pointClicked = point;
        this.x = x;
        this.y = y;
        repaint();
    }



    @Override
    public synchronized void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawString(pointClicked, x, y);
    }

}