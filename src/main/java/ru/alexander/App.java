package ru.alexander;

import ru.alexander.scheme.Circuit;
import ru.alexander.scheme.Component;
import ru.alexander.scheme.components.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        VoltageSource source = new VoltageSource("V0", "l4", "l1");
        Resistor r0 = new Resistor("R0", "l3", "l4");
        Resistor r1 = new Resistor("R1", "l2", "l5");
        Capacitor c = new Capacitor("C0", "l2", "l3");
        Inductor coil = new Inductor("I0", "l5", "l3");
        Circuit circuit = new Circuit(
                source,
                new Diode("D0", "l1", "l2"),
                c,
                r0,
                r1,
                coil
        );
        Component[] components = circuit.getComponents();
        source.U = 5;
        r0.R = 1e4;
        r1.R = 10;
        coil.L = 0.25;

        int time = 1000;
        int tScale = 1;
        int max = 300;
        BufferedImage img = new BufferedImage(time * tScale, 2 * max, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int scaleV = 50;
        int scaleI = 400000;
        for (int i = 1; i <= time; i++) {

            double i0 = components[0].I;
            double u0 = components[0].U;

            double i1 = components[1].I;
            double u1 = components[1].U;

            double i2 = r0.I;
            double u2 = r0.U;

            double i3 = c.I;
            double u3 = c.U;

            double i4 = coil.I;
            double u4 = coil.U;

            if (i % 100 == 0) components[0].U = -components[0].U;

            for (int j = 0; j < 400; j++)
                circuit.calculate(5e-6);
            System.out.printf("%1.3f %1.3f   %1.3f %1.3f   %1.3f %1.3f   %1.3f %1.3f   %1.3f %1.3f   \n",
                    components[0].I, components[0].U,
                    components[1].I, components[1].U,
                    r0.I, r0.U,
                    c.I, c.U,
                    coil.I, coil.U);

            g.setColor(Color.RED);
            g.drawLine((i - 1) * tScale, (int) (max - i0 * scaleI), i * tScale, (int) (max - components[0].I * scaleI));
            g.setColor(Color.pink);
            g.drawLine((i - 1) * tScale, (int) (max - u0 * scaleV), i * tScale, (int) (max - components[0].U * scaleV));

            g.setColor(Color.YELLOW);
            g.drawLine((i - 1) * tScale, (int) (max - i1 * scaleI), i * tScale, (int) (max - components[1].I * scaleI));
            g.setColor(Color.orange);
            g.drawLine((i - 1) * tScale, (int) (max - u1 * scaleV), i * tScale, (int) (max - components[1].U * scaleV));

            g.setColor(Color.BLUE);
            g.drawLine((i - 1) * tScale, (int) (max - i2 * scaleI), i * tScale, (int) (max - r0.I * scaleI));
            g.setColor(Color.MAGENTA);
            g.drawLine((i - 1) * tScale, (int) (max - u2 * scaleV), i * tScale, (int) (max - r0.U * scaleV));

            g.setColor(Color.GREEN);
            g.drawLine((i - 1) * tScale, (int) (max - i3 * scaleI), i * tScale, (int) (max - c.I * scaleI));
            g.setColor(Color.gray);
            g.drawLine((i - 1) * tScale, (int) (max - u3 * scaleV), i * tScale, (int) (max - c.U * scaleV));

            g.setColor(Color.WHITE);
            g.drawLine((i - 1) * tScale, (int) (max - i4 * scaleI), i * tScale, (int) (max - coil.I * scaleI));
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine((i - 1) * tScale, (int) (max - u4 * scaleV), i * tScale, (int) (max - coil.U * scaleV));
        }
        ImageIO.write(img, "png",new File("test.png"));

    }
}
