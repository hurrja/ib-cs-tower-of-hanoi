import javax.swing.*;
import java.awt.event.ActionEvent;

abstract class KeyStrokeAction extends AbstractAction {
    KeyStrokeAction(String name, KeyStroke keyStroke) {
        super(name);
        putValue(Action.NAME, name);
        this.keyStroke = keyStroke;
    }

    @Override
    abstract public void actionPerformed(ActionEvent e);

    private final KeyStroke keyStroke;
    public KeyStroke getKeyStroke() {
        return keyStroke;
    }
}