/****************************************************************************
 **
 ** This file is part of yFiles-2.5. 
 ** 
 ** yWorks proprietary/confidential. Use is subject to license terms.
 **
 ** Redistribution of this file or of an unauthorized byte-code version
 ** of this file is strictly forbidden.
 **
 ** Copyright (c) 2000-2007 by yWorks GmbH, Vor dem Kreuzberg 28, 
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ***************************************************************************/
package demo.view.rendering;

import demo.view.DemoBase;
import y.view.Drawable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * This demo shows how to add objects of type Drawable to 
 * a Graph2DView and how to implement such a Drawable object.
 * Drawables represent graphical objects that can be displayed 
 * by a Graph2DView. The main purpose of Drawables is to highlight
 * certain regions of the displayed graph.
 * <br>
 * The Drawable implemented in this demo draws itself as a red box 
 * drawn underneath the displayed graph. The size and location of the box changes 
 * dynamically as the bounding box of the graph changes.
 */
public class DrawablesDemo extends DemoBase {
  public DrawablesDemo() {
    //add the drawable
    view.addBackgroundDrawable( new BoundingBox() );
    loadGraph( "resource/drawablesDemo.gml" );
  }

  /**
   * Represents a graphical bounding box of the displayed graph.
   */
  class BoundingBox implements Drawable {
    public Rectangle getBounds() {
      Rectangle r = view.getGraph2D().getBoundingBox();
      if ( r.getWidth() > 0.0 ) {
        r.setFrame( r.getX() - 30, r.getY() - 30, r.getWidth() + 60, r.getHeight() + 60 );
      }
      return r;
    }

    public void paint( Graphics2D gfx ) {
      Rectangle r = getBounds();
      gfx.setColor( Color.red );
      gfx.fill( r );
      gfx.setColor( Color.black );
      gfx.draw( r );
    }
  }

  public static void main( String args[] ) {
    DrawablesDemo demo = new DrawablesDemo();
    demo.start( "Drawables Demo" );
  }


}

    

      
