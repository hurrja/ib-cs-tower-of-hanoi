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

from PySide6.QtWidgets import (QMainWindow, QWidget, QVBoxLayout, QHBoxLayout,
                                QPushButton, QLabel, QSpinBox, QSlider, QApplication)
from PySide6.QtCore import Qt, QTimer, Slot
from PySide6.QtGui import QPainter, QColor, QPen, QFont, QKeySequence, QShortcut

from peg import Peg
from tower_of_hanoi import TowerOfHanoi
from exceptions import IllegalTowerOfHanoiMoveException

class TOHPanel(QWidget):
    def __init__(self, parent=None):
        super().__init__(parent)
        self.view_toh = None
        self.setMinimumSize(800, 600)
    
    def set_view_toh(self, view_toh):
        self.view_toh = view_toh
        self.update()
    
    def paintEvent(self, event):
        if not self.view_toh:
            return
        
        painter = QPainter(self)
        painter.setRenderHint(QPainter.Antialiasing)
        painter.fillRect(self.rect(), QColor(Qt.white))
        
        width = self.width()
        height = self.height()
        
        NUM_PEGS = 3

        # graphical element scaling to window dimensions
        peg_label_y = 11 * height // 12
        peg_base_y = 5 * height // 6
        peg_height = 2 * height // 3
        peg_top_y = peg_base_y - peg_height
        peg_width = width // 36
        num_discs = self.view_toh.get_num_discs()
        disc_height = peg_height // (num_discs + 1)
        disc_max_width = width // (NUM_PEGS + 3)
        disc_min_width = peg_width * 2
        
        peg_index = 1
        for peg in Peg:
            x = peg_index * width // (NUM_PEGS + 1)
            
            painter.setPen(QPen(Qt.black))
            font = QFont("Courier", 16)
            painter.setFont(font)
            painter.drawText(x - 5, peg_label_y, str(peg))
            
            painter.drawRect(x - peg_width // 2, peg_top_y, peg_width, peg_height)
            
            # discs
            discs = self.view_toh.get_discs(peg)
            num_peg_discs = len(discs)
            
            for (i, disc) in enumerate(discs):
                if num_discs > 1:
                    disc_width = ((disc_max_width - disc_min_width) * disc + 
                                  disc_min_width * num_discs - disc_max_width) // (num_discs - 1)
                else:
                    disc_width = disc_max_width
                
                rect_x = x - disc_width // 2
                rect_y = peg_base_y - (i + 1) * disc_height
                
                # Fill disc
                painter.fillRect(rect_x, rect_y, disc_width, disc_height, QColor(Qt.black))
                
                # Draw disc border
                painter.setPen(QPen(Qt.white))
                painter.drawRect(rect_x, rect_y, disc_width, disc_height)
            
            peg_index += 1


class TOHUserInterface(QMainWindow):
    DEFAULT_UPDATE_INTERVAL = 1000
    MIN_UPDATE_INTERVAL = 0
    MAX_UPDATE_INTERVAL = 3000
    
    def __init__(self, controller):
        super().__init__()
        self.controller = controller
        self.view_toh = None
        
        self.setWindowTitle('Tower of Hanoi solution framework')
        self.resize(1024, 768)
        
        central_widget = QWidget()
        self.setCentralWidget(central_widget)
        main_layout = QVBoxLayout(central_widget)
        main_layout.setContentsMargins(0, 0, 0, 0)
        
        self.toh_panel = TOHPanel()
        main_layout.addWidget(self.toh_panel, 1)
        
        self.init_controls(main_layout)
        
        self.application_update_timer = QTimer(self)
        self.application_update_timer.timeout.connect(self.move_forward)
        self.application_update_timer.setInterval(self.DEFAULT_UPDATE_INTERVAL)
        
        self.init_key_bindings()
    
    def init_controls(self, main_layout):
        controls = QWidget()
        controls_layout = QHBoxLayout(controls)
        controls.setStyleSheet('background-color: white;')
        
        discs_label = QLabel('Number of discs:')
        controls_layout.addWidget(discs_label)
        
        self.discs_spinner = QSpinBox()
        self.discs_spinner.setRange(self.controller.MIN_DISCS, self.controller.MAX_DISCS)
        self.discs_spinner.setValue(self.controller.DEFAULT_DISCS)
        self.discs_spinner.setToolTip("Press 'reset' to apply changes.")
        self.discs_spinner.setFocusPolicy(Qt.NoFocus)
        self.discs_spinner.valueChanged.connect(self.on_discs_changed)
        controls_layout.addWidget(self.discs_spinner)
        
        controls_layout.addSpacing(50)
        
        self.move_back_button = QPushButton('<')
        self.move_back_button.setToolTip('Left arrow')
        self.move_back_button.setFocusPolicy(Qt.NoFocus)
        self.move_back_button.clicked.connect(self.move_back)
        controls_layout.addWidget(self.move_back_button)
        
        self.play_button = QPushButton('Play')
        self.play_button.setToolTip('Space bar')
        self.play_button.setFocusPolicy(Qt.NoFocus)
        self.play_button.clicked.connect(self.play_pause)
        controls_layout.addWidget(self.play_button)
        
        self.reset_button = QPushButton('Reset')
        self.reset_button.setToolTip('R')
        self.reset_button.setFocusPolicy(Qt.NoFocus)
        self.reset_button.clicked.connect(self.reset)
        controls_layout.addWidget(self.reset_button)
        
        self.move_forward_button = QPushButton('>')
        self.move_forward_button.setToolTip('Right arrow')
        self.move_forward_button.setFocusPolicy(Qt.NoFocus)
        self.move_forward_button.clicked.connect(self.move_forward_button_clicked)
        controls_layout.addWidget(self.move_forward_button)
        
        controls_layout.addSpacing(50)
        
        speed_label = QLabel('Speed:')
        controls_layout.addWidget(speed_label)
        
        self.speed_slider = QSlider(Qt.Horizontal)
        self.speed_slider.setRange(self.MIN_UPDATE_INTERVAL, self.MAX_UPDATE_INTERVAL)
        self.speed_slider.setValue(self.DEFAULT_UPDATE_INTERVAL)
        self.speed_slider.setMinimumWidth(100)
        self.speed_slider.setFocusPolicy(Qt.NoFocus)
        self.speed_slider.setToolTip("You can also use the '+' and '-' keys to adjust the speed.")
        self.speed_slider.valueChanged.connect(self.on_speed_changed)
        controls_layout.addWidget(self.speed_slider)
        
        controls_layout.addStretch()
        
        quit_button = QPushButton('Quit')
        quit_button.setToolTip('Q')
        quit_button.setFocusPolicy(Qt.NoFocus)
        quit_button.clicked.connect(self.close)
        controls_layout.addWidget(quit_button)
        
        main_layout.addWidget(controls)
    
    def init_key_bindings(self):
        QShortcut(QKeySequence(Qt.Key_Left), self, self.move_back)
        QShortcut(QKeySequence(Qt.Key_Right), self, self.move_forward_button_clicked)
        QShortcut(QKeySequence(Qt.Key_Space), self, self.play_pause)
        QShortcut(QKeySequence(Qt.Key_R), self, self.reset)
        QShortcut(QKeySequence(Qt.Key_Plus), self, self.speed_up)
        QShortcut(QKeySequence(Qt.Key_Minus), self, self.slow_down)
        QShortcut(QKeySequence(Qt.Key_Q), self, self.close)
    
    @Slot()
    def on_discs_changed(self, value):
        self.controller.set_num_discs(value)
    
    @Slot()
    def on_speed_changed(self, value):
        # invert interval (makes more intuitive sense with slider mechanic)
        interval = self.MAX_UPDATE_INTERVAL - value + self.MIN_UPDATE_INTERVAL
        self.application_update_timer.setInterval(interval)
    
    @Slot()
    def move_forward(self):
        self.controller.make_move()
        self.toh_panel.update()
    
    @Slot()
    def move_forward_button_clicked(self):
        if self.application_update_timer.isActive():
            self.play_pause()
        self.move_forward()
    
    @Slot()
    def move_back(self):
        if self.application_update_timer.isActive():
            self.play_pause()
        self.controller.retract_move()
        self.toh_panel.update()
    
    @Slot()
    def play_pause(self):
        if self.application_update_timer.isActive():
            self.application_update_timer.stop()
            self.play_button.setText('Play')
        else:
            self.application_update_timer.start()
            self.play_button.setText('Pause')
    
    @Slot()
    def reset(self):
        if self.application_update_timer.isActive():
            self.play_pause()
        self.controller.reset_toh()
        self.toh_panel.update()
    
    @Slot()
    def speed_up(self):
        self.speed_slider.setValue(self.speed_slider.value() + 100)
    
    @Slot()
    def slow_down(self):
        self.speed_slider.setValue(self.speed_slider.value() - 100)
    
    def set_view_toh(self, view_toh: TowerOfHanoi):
        self.view_toh = view_toh
        self.toh_panel.set_view_toh(view_toh)
