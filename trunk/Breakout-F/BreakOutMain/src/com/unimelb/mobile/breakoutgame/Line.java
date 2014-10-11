// Line.java
// Class Line represents a line with two endpoints.
package com.unimelb.mobile.breakoutgame;

import android.graphics.Point;

public class Line
{
   public Point start; // starting Point
   public Point end; // ending Point

   // default constructor initializes Points to (0, 0)
   public Line()
   {
      start = new Point(0, 0); // start Point
      end = new Point(0, 0); // end Point
   }
} 
