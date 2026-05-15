import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PaintApp extends JFrame {

    private int x = -1;
    private int y = -1;

    private Color currentColor = Color.BLACK;

    private DrawPanel canvas;

    private class DrawPanel extends JPanel {

        Image image;
        Graphics2D graphics2D;

        DrawPanel() {
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    int newX = e.getX();
                    int newY = e.getY();

                    if (graphics2D != null && x != -1 && y != -1) {
                        graphics2D.setColor(currentColor);
                        graphics2D.setStroke(new BasicStroke(4));
                        graphics2D.drawLine(x, y, newX, newY);
                        repaint();
                    }

                    x = newX;
                    y = newY;
                }
            });

            addMouseListener(new MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    x = -1;
                    y = -1;
                }
            });
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (image == null) {
                image = createImage(getWidth(), getHeight());
                graphics2D = (Graphics2D) image.getGraphics();
                graphics2D.setColor(Color.WHITE);
                graphics2D.fillRect(0, 0, getWidth(), getHeight());
            }

            g.drawImage(image, 0, 0, null);
        }

        public void clearCanvas() {
            if (graphics2D != null) {
                graphics2D.setColor(Color.WHITE);
                graphics2D.fillRect(0, 0, getWidth(), getHeight());
                repaint();
            }
        }
    }

    public PaintApp() {

        setTitle("Basic Paint Program");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top panel for buttons
        JPanel topPanel = new JPanel();

        JButton blackButton = new JButton("Black");
        JButton redButton = new JButton("Red");
        JButton blueButton = new JButton("Blue");
        JButton clearButton = new JButton("Clear");

        topPanel.add(blackButton);
        topPanel.add(redButton);
        topPanel.add(blueButton);
        topPanel.add(clearButton);

        add(topPanel, BorderLayout.NORTH);

        // Canvas panel
        canvas = new DrawPanel();

        canvas.setBackground(Color.WHITE);

        add(canvas, BorderLayout.CENTER);

        // Button actions
        blackButton.addActionListener(e -> currentColor = Color.BLACK);

        redButton.addActionListener(e -> currentColor = Color.RED);

        blueButton.addActionListener(e -> currentColor = Color.BLUE);

        clearButton.addActionListener(e -> canvas.clearCanvas());
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            PaintApp app = new PaintApp();

            app.setVisible(true);
        });
    }
}