package vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class DrawDot {

    //public static void main(String[] args) {
      //  new DrawDot();
    //}

    public DrawDot(String url) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                TestPane panel = new TestPane(url);
                frame.add(panel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setVisible(true);

            }
        });
    }


    private class TestPane extends JPanel {

        private List<Point> points;
        private BufferedImage image;
        //"/Users/macbookpro/Desarrollo/temp/pdfs/img/1.jpg"

        public TestPane(String url) {
            JButton boton = new JButton("Borrar");
            boton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    points = new ArrayList<>();
                    repaint();
                }
            });

            add(boton);

            points = new ArrayList<>(25);
            try {
                System.out.println(url);
                File f = new File(url);
                image = ImageIO.read(f);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            MouseAdapter eventosMouse = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    points.add(e.getPoint());
                    repaint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    points.add(e.getPoint());
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    points.add(e.getPoint());
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    points.add(e.getPoint());
                    repaint();
                }
            };

            addMouseListener(eventosMouse);
            addMouseMotionListener(eventosMouse);
        }

        @Override
        public Dimension getPreferredSize() {
            return image == null ? new Dimension(200, 200) : new Dimension(image.getWidth(), image.getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            if (image != null) {
                g2d.drawImage(image, 0, 0, this);
            }
            g2d.setColor(Color.RED);
            for (Point p : points) {
                g2d.fillOval(p.x - 4, p.y - 4, 8, 8);
            }
            g2d.dispose();
        }

    }

}