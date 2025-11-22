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

from collections import deque
from peg import Peg
from move import Move
from exceptions import IllegalTowerOfHanoiMoveException

class TowerOfHanoi:
    def __init__(self, num_discs):
        self.num_discs = num_discs
        self.towers = {peg: [] for peg in Peg}
        self.moves = deque()
        
        for i in range(num_discs, 0, -1):
            self.towers[Peg.A].append(i)
    
    def move(self, from_peg, to_peg):
        if self.is_empty(from_peg) or (not self.is_empty(to_peg) and 
                                       self.peek(from_peg) > self.peek(to_peg)):
            raise IllegalTowerOfHanoiMoveException(
                f'illegal move: {from_peg} -> {to_peg}')
        
        disc = self.towers[from_peg].pop()
        self.towers[to_peg].append(disc)
        self.moves.append(Move(from_peg, to_peg))
    
    def undo_last_move(self):
        last_move = self.moves.pop()
        from_peg = last_move.from_peg
        to_peg = last_move.to_peg
        
        disc = self.towers[to_peg].pop()
        self.towers[from_peg].append(disc)
        
        return last_move
    
    def get_num_discs(self):
        return self.num_discs
    
    def get_discs(self, peg):
        return list(self.towers[peg])
    
    def is_empty(self, peg):
        return len(self.towers[peg]) == 0
    
    def peek(self, peg):
        if self.is_empty(peg):
            return 0
        return self.towers[peg][-1]
    
    def get_moves(self):
        return self.moves
    
    def __str__(self):
        result = []
        for peg in Peg:
            result.append(f'{peg}: {self.towers[peg]}')
        return '\n'.join(result)
