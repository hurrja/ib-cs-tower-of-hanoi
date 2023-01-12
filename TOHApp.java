// Copyright (C) 2020 Jarmo Hurri

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

public class TOHApp
{
  public static void main (String[] args)
  {
    SwingUtilities.invokeLater (() -> new TOHApp ());
  }
  
  public static void solve(TowerOfHanoi toh, int numDiscs, Peg from, Peg to, Peg intermediate)
    throws IllegalTowerOfHanoiMoveException
  {
    // FIXME: Solution given!
    if (numDiscs == 0) return; //base case
    solve(toh, numDiscs-1, from, intermediate, to); //move n-1 discs from the top to the intermediate
    toh.move(from, to); // move the bottom disk to the final destination
    solve(toh, numDiscs-1, intermediate, to, from); // move the n-1 discs back from intermediate to the final destination
  }
  
  public TOHApp ()
  {
    toh = new TowerOfHanoi (NUM_DISCS);
    viewToh = new TowerOfHanoi(NUM_DISCS);

    try
    {
      // task is to move all discs from peg A to peg C
      solve(toh, NUM_DISCS, Peg.A, Peg.C, Peg.B);
    }
    catch (IllegalTowerOfHanoiMoveException e)
    {
      System.out.println ("illegal move: " + e);
    }

    new TOHUserInterface (this, viewToh);
  }

  public void makeMove()
  {
    java.util.Queue<Move> moves = toh.getMoves ();

    if (!moves.isEmpty ())
    {
      Move move = moves.remove ();
      try
      {
        viewToh.move (move.from, move.to);
      }
      catch (IllegalTowerOfHanoiMoveException e)
      {
      }
    }
  }

  public void retractMove() {
    if (viewToh.getMoves().size() < 1) return;

    Move move = viewToh.undoLastMove();
    toh.getMoves().addFirst(move);
  }

  public void resetToh() {
    while (!viewToh.getMoves().isEmpty())
      retractMove();
  }

  private TowerOfHanoi toh;
  private TowerOfHanoi viewToh;
  final int NUM_DISCS = 5;
}



