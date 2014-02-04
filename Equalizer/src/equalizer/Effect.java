package equalizer;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Shevchik
 * Date: 04.02.14
 * Time: 12:53
 * To change this template use File | Settings | File Templates.
 */
public interface Effect {
    void paint(JComponent component, Graphics graphics);
}
