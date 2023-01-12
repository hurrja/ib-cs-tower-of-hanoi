import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

abstract class KeyStrokeAction extends AbstractAction {
    KeyStrokeAction(String name, KeyStroke keyStroke) {
        super(name);
        putValue(Action.NAME, name);
        putValue(Action.SHORT_DESCRIPTION, name);
        this.keyStroke = keyStroke;
    }

    KeyStrokeAction(String name, KeyStroke keyStroke, String tooltip) {
        super(name);
        putValue(Action.NAME, name);
        putValue(Action.SHORT_DESCRIPTION, tooltip);
        this.keyStroke = keyStroke;
    }

    @Override
    abstract public void actionPerformed(ActionEvent e);

    private final KeyStroke keyStroke;
    public KeyStroke getKeyStroke() {
        return keyStroke;
    }
}