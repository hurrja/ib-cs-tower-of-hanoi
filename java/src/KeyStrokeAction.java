// Copyright (C) 2023 Aleksei Terin

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

abstract class KeyStrokeAction extends AbstractAction {
    KeyStrokeAction(String name, KeyStroke keyStroke) {
        super(name);
        putValue(Action.NAME, name);
        this.keyStroke = keyStroke;
    }

    KeyStrokeAction(String name, KeyStroke keyStroke, String tooltip) {
        super(name);
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
