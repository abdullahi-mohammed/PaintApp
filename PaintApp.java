import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class PaintApp extends JFrame {

    private int x = -1;
    private int y = -1;

    private Color currentColor = Color.BLACK;
    private float brushSize = 4f;

    private DrawPanel canvas;

    public PaintApp() {
        setTitle("Basic Paint Program");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top panel for buttons
        JPanel topPanel = new JPanel();

        JButton blackButton = new JButton("Black");
        JButton redButton = new JButton("Red");
        JButton blueButton = new JButton("Blue");
        JButton undoButton = new JButton("Undo");
        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load");
        JButton clearButton = new JButton("Clear");

        topPanel.add(blackButton);
        topPanel.add(redButton);
        topPanel.add(blueButton);
        topPanel.add(undoButton);
        topPanel.add(saveButton);
        topPanel.add(loadButton);
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
        undoButton.addActionListener(e -> canvas.undoLastStroke());
        saveButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save project (.pnt) or export image (.png)");
            javax.swing.filechooser.FileNameExtensionFilter pntFilter = new javax.swing.filechooser.FileNameExtensionFilter("Paint project (*.pnt)", "pnt");
            javax.swing.filechooser.FileNameExtensionFilter pngFilter = new javax.swing.filechooser.FileNameExtensionFilter("PNG image (*.png)", "png");
            fc.addChoosableFileFilter(pntFilter);
            fc.addChoosableFileFilter(pngFilter);
            fc.setAcceptAllFileFilterUsed(true);
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                // if no extension provided, append extension from chosen file filter
                if (!f.getName().contains(".")) {
                    javax.swing.filechooser.FileFilter chosen = fc.getFileFilter();
                    String ext = "pnt"; // default
                    if (chosen instanceof javax.swing.filechooser.FileNameExtensionFilter) {
                        String[] exts = ((javax.swing.filechooser.FileNameExtensionFilter) chosen).getExtensions();
                        if (exts != null && exts.length > 0) ext = exts[0];
                    }
                    f = new File(f.getParentFile(), f.getName() + "." + ext);
                }
                try {
                    canvas.saveToFile(f);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
                }
            }
        });

        loadButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Open project (.pnt) or image (.png)");
            fc.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Paint project (*.pnt)", "pnt"));
            fc.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG image (*.png)", "png"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    canvas.loadFromFile(f);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Load failed: " + ex.getMessage());
                }
            }
        });

        clearButton.addActionListener(e -> canvas.clearCanvas());
    }

    // Abstract class demonstrating inheritance with 'extends' and 'abstract'
    private static abstract class ShapeStorage {
        abstract void addSegment(LineSegment s);
        abstract List<List<LineSegment>> getStrokes();
        abstract void clear();
        abstract void undo();
    }

    // Interface demonstrating use of 'implements'
    private interface Persistable {
        void saveToFile(File f) throws IOException;
        void loadFromFile(File f) throws IOException, ClassNotFoundException;
    }

    // Concrete storage using ArrayList; also implements Persistable for file IO
    private static class ListShapeStorage extends ShapeStorage implements Persistable, Serializable {
        private static final long serialVersionUID = 1L;
        private final List<List<LineSegment>> strokes = new ArrayList<>();
        private List<LineSegment> currentStroke = null;

        @Override
        void addSegment(LineSegment s) {
            if (currentStroke == null) {
                currentStroke = new ArrayList<>();
                strokes.add(currentStroke);
            }
            currentStroke.add(s);
        }

        void startStroke() {
            currentStroke = new ArrayList<>();
            strokes.add(currentStroke);
        }

        void endStroke() {
            currentStroke = null;
        }

        @Override
        List<List<LineSegment>> getStrokes() {
            return strokes;
        }

        @Override
        void clear() {
            strokes.clear();
        }

        @Override
        void undo() {
            if (!strokes.isEmpty()) strokes.remove(strokes.size() - 1);
        }

        @Override
        public void saveToFile(File f) throws IOException {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
                oos.writeObject(strokes);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void loadFromFile(File f) throws IOException, ClassNotFoundException {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                Object obj = ois.readObject();
                if (obj instanceof List) {
                    strokes.clear();
                    strokes.addAll((List<List<LineSegment>>) obj);
                }
            }
        }
    }

    private class DrawPanel extends JPanel {
        // storage for strokes
        private final ListShapeStorage storage = new ListShapeStorage();
        private BufferedImage backgroundImage = null;

        DrawPanel() {
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    int newX = e.getX();
                    int newY = e.getY();

                    if (x != -1 && y != -1) {
                        storage.addSegment(new LineSegment(x, y, newX, newY, currentColor, brushSize));
                        repaint();
                    }

                    x = newX;
                    y = newY;
                }
            });

            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    storage.startStroke();
                    x = e.getX();
                    y = e.getY();
                }

                public void mouseReleased(MouseEvent e) {
                    x = -1;
                    y = -1;
                    storage.endStroke();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw background image if present, otherwise clear to white
            if (backgroundImage != null) {
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            // Draw all strokes (nested loops demonstrate loops and collections)
            for (List<LineSegment> stroke : storage.getStrokes()) {
                for (LineSegment s : stroke) {
                    g2.setColor(s.color);
                    g2.setStroke(new BasicStroke(s.strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(s.x1, s.y1, s.x2, s.y2);
                }
            }
        }

        public void clearCanvas() {
            storage.clear();
            repaint();
        }

        public void undoLastStroke() {
            storage.undo();
            repaint();
        }

        // Save: if filename ends with .png -> export image; otherwise serialize project (.pnt)
        public void saveToFile(File f) throws IOException {
            String name = f.getName().toLowerCase();
            if (name.endsWith(".png")) {
                // render the panel to a BufferedImage and write PNG
                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = img.createGraphics();
                this.paint(g2);
                g2.dispose();
                ImageIO.write(img, "png", f);
            } else {
                // default project format
                storage.saveToFile(f);
            }
        }

        // Load: if PNG -> load as background image; otherwise attempt to read project file
        public void loadFromFile(File f) throws IOException, ClassNotFoundException {
            String name = f.getName().toLowerCase();
            if (name.endsWith(".png")) {
                BufferedImage img = ImageIO.read(f);
                backgroundImage = img;
                // clear strokes when loading an image
                storage.clear();
                repaint();
            } else {
                storage.loadFromFile(f);
                backgroundImage = null;
                repaint();
            }
        }
    }

    private static class LineSegment implements Serializable {
        private static final long serialVersionUID = 1L;
        final int x1, y1, x2, y2;
        final Color color;
        final float strokeWidth;

        LineSegment(int x1, int y1, int x2, int y2, Color color, float strokeWidth) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
            this.strokeWidth = strokeWidth;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PaintApp app = new PaintApp();
            app.setVisible(true);
        });
    }
}
