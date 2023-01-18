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
    GUI = new TOHUserInterface (this);

    numDiscs = DEFAULT_DISCS;
    resetToh();
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
    toh = new TowerOfHanoi(numDiscs);
    viewToh = new TowerOfHanoi(numDiscs);

    try {
      solve(toh, numDiscs, Peg.A, Peg.C, Peg.B);
    } catch (IllegalTowerOfHanoiMoveException e) {
      System.out.println ("illegal move: " + e);
    }

    GUI.setViewToh(viewToh);
  }

  public void setNumDiscs(int num) {
    if (num >= MIN_DISCS && num <= MAX_DISCS) this.numDiscs = num;
  }
  public int getNumDiscs() {
    return numDiscs; //used to return the default n of discs to the view
  }

  private TOHUserInterface GUI;

  public final static int MIN_DISCS = 2;
  public final static int MAX_DISCS = 20;
  public final static int DEFAULT_DISCS = 5;
  private int numDiscs;

  private TowerOfHanoi toh;
  private TowerOfHanoi viewToh;
}



