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
package demo.view.advanced;

import demo.view.DemoBase;
import y.base.Node;
import y.base.NodeCursor;
import y.base.NodeList;
import y.view.DefaultGraph2DRenderer;
import y.view.Drawable;
import y.view.EditMode;
import y.view.Graph2D;
import y.view.Graph2DRenderer;
import y.view.NodeRealizer;
import y.view.PopupMode;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;


/**
 * Demonstrates how to put a part of a graph in an inactive background layer of the view.
 * When parts of the graph are selected, then a right mouse click brings up a menu that offers to
 * put the selected part of the graph to the inactive background.  
 * If none is selected then a right mouse click brings up a popup menu that allows to bring 
 * the inactive graph part back to life.
 */
public class InactiveLayerDemo extends DemoBase
{
  Graph2D inactiveGraph;

  protected void initialize() {
    super.initialize();
    loadGraph( "resource/5.gml" );
    Graph2D graph = view.getGraph2D();
    graph.getDefaultNodeRealizer().setFillColor( Color.orange );
    inactiveGraph = new Graph2D();
    view.addBackgroundDrawable( new Graph2DDrawable( inactiveGraph ) );
  }

  protected void registerViewModes() {
    EditMode editMode = new EditMode();
    editMode.setPopupMode( new MyPopupMode() );
    view.addViewMode( editMode );
  }

  class MyPopupMode extends PopupMode {
    public JPopupMenu getNodePopup( final Node v ) {
      JPopupMenu pm = new JPopupMenu();
      pm.add( new AbstractAction( "Deactivate" ) {
        public void actionPerformed( ActionEvent e ) {
          Graph2D graph = view.getGraph2D();
          NodeList selectedNodes = new NodeList( graph.selectedNodes() );

          NodeRealizer r = graph.getRealizer( v );
          r.setSelected( false );
          r.setFillColor( Color.lightGray );
          r.setLineColor( Color.gray );
          graph.moveSubGraph( selectedNodes, inactiveGraph );
          view.updateView();
        }
      } );
      return pm;
    }

    public JPopupMenu getSelectionPopup(double x, double y)
    {
      JPopupMenu pm = new JPopupMenu();
      pm.add(new AbstractAction("Deactivate") {
        public void actionPerformed(ActionEvent e)
        {
          Graph2D graph = view.getGraph2D();
          NodeList selectedNodes = new NodeList(graph.selectedNodes());
          for(NodeCursor nc = selectedNodes.nodes(); nc.ok(); nc.next())
          {
            NodeRealizer r = graph.getRealizer(nc.node());
            r.setSelected(false);
            r.setFillColor(Color.lightGray);
            r.setLineColor(Color.gray);
          }
          graph.moveSubGraph(selectedNodes, inactiveGraph);
          view.updateView();
        }
      });
      return pm;
    }
    public JPopupMenu getPaperPopup(double x, double y)
    {
      JPopupMenu pm = new JPopupMenu();
      pm.add(new AbstractAction("Activate All") {
        public void actionPerformed(ActionEvent e)
        {
          Graph2D graph = view.getGraph2D();
          NodeList selectedNodes = new NodeList(inactiveGraph.nodes());
          for(NodeCursor nc = selectedNodes.nodes(); nc.ok(); nc.next())
          {
            NodeRealizer r = graph.getRealizer(nc.node());
            r.setFillColor(Color.orange);
            r.setLineColor(Color.black);
          }
          inactiveGraph.moveSubGraph(selectedNodes, graph);
          view.updateView();
        }
      });
      return pm;
    }
  };

  static class Graph2DDrawable implements Drawable
  {
    Graph2D graph;
    Graph2DRenderer renderer;

    Graph2DDrawable(Graph2D g)
    {
      this.graph = g;
      renderer = new DefaultGraph2DRenderer();
    }

    public void paint(Graphics2D gfx)
    {
      renderer.paint(gfx, graph);
    }

    public Rectangle getBounds()
    {
      return graph.getBoundingBox();
    }
  }

  public static void main(String args[])
  {
    initLnF();
    InactiveLayerDemo demo = new InactiveLayerDemo();
    demo.start("Inactive Layer Demo");
  }

}

    

      
