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

from peg import Peg
from tower_of_hanoi import TowerOfHanoi
from exceptions import IllegalTowerOfHanoiMoveException

class TOHApp:
    MIN_DISCS = 2
    MAX_DISCS = 20
    DEFAULT_DISCS = 5
    
    def __init__(self):
        self.gui = None  # Will be set by the GUI
        self.num_discs = self.DEFAULT_DISCS
        self.reset_toh()
    
    def set_gui(self, gui):
        self.gui = gui
    
    def solve(self, num_discs, from_peg, to_peg, intermediate):
        # code to be written to replace this feeble attempt
        try:
            self.move(from_peg, intermediate)  # move top disc to intermediate
            self.move(from_peg, to_peg)  # move next disc to target
        except IllegalTowerOfHanoiMoveException:
            pass
    
    def move(self, from_peg, to_peg):
        self.toh.move(from_peg, to_peg)
    
    def make_move(self):
        moves = self.toh.get_moves()
        
        if len(moves) > 0:
            move = moves.popleft()
            try:
                self.view_toh.move(move.from_peg, move.to_peg)
            except IllegalTowerOfHanoiMoveException:
                pass
    
    def retract_move(self):
        if len(self.view_toh.get_moves()) < 1:
            return
        
        move = self.view_toh.undo_last_move()
        self.toh.get_moves().appendleft(move)
    
    def reset_toh(self):
        self.toh = TowerOfHanoi(self.num_discs)
        self.view_toh = TowerOfHanoi(self.num_discs)
        
        try:
            self.solve(self.num_discs, Peg.A, Peg.C, Peg.B)
        except IllegalTowerOfHanoiMoveException as e:
            print(f'illegal move: {e}')
        
        if self.gui:
            self.gui.set_view_toh(self.view_toh)
    
    def set_num_discs(self, num: int):
        if self.MIN_DISCS <= num <= self.MAX_DISCS:
            self.num_discs = num
