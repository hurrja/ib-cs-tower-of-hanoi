# Copyright (C) 2020-2025 Jarmo Hurri, Aleksei Terin

# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.

# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.

import sys
from PySide6.QtWidgets import QApplication

from toh_app import TOHApp
from toh_user_interface import TOHUserInterface

app = QApplication(sys.argv)
    
# Create controller
controller = TOHApp()
    
# Create and show GUI
gui = TOHUserInterface(controller)
controller.set_gui(gui)
controller.reset_toh()  # Reset again to update GUI with initial state
gui.show()
    
sys.exit(app.exec())
