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
package demo.view.layout.hierarchic;

import demo.view.DemoBase;
import y.base.DataMap;
import y.base.Node;
import y.base.NodeCursor;
import y.base.NodeList;
import y.base.NodeMap;
import y.layout.BufferedLayouter;
import y.layout.GraphLayout;
import y.layout.Layouter;
import y.layout.NodeLayout;
import y.layout.hierarchic.IncrementalHierarchicLayouter;
import y.layout.hierarchic.incremental.IncrementalHintsFactory;
import y.layout.hierarchic.incremental.NodeLayoutDescriptor;
import y.layout.hierarchic.incremental.SimplexNodePlacer;
import y.layout.hierarchic.incremental.SwimLaneDescriptor;
import y.util.Maps;
import y.view.Arrow;
import y.view.Drawable;
import y.view.EdgeRealizer;
import y.view.EditMode;
import y.view.Graph2D;
import y.view.LayoutMorpher;
import y.view.PopupMode;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This simple demo shows how to use the swim lane feature of the 
 * new {@link y.layout.hierarchic.IncrementalHierarchicLayouter}.
 * </p>
 * <p>
 * It can either calculate a new layout or calculate a new layout given the current 
 * sketch or incrementally layout selected nodes to an already existing graph whose
 * layout is read from the current sketch. 
 * </p>
 * <p>
 * Things to try:<br/>
 * Create a graph and assign nodes to layers by editing the label of the nodes.
 * Nodes with the same layer will be placed into the same swim lane. Swim lanes
 * are sorted from left to right in ascending label order.
 * <br/>
 * Use the <b>Layout</b> button to lay it out from scratch.
 * Modify the graph (move nodes and or bends), deselect all elements and 
 * choose <b>Layout from Sketch</b> to recalculate the layout using the given sketch
 * Add some nodes and connect them to the graph, select the newly added nodes
 * and choose <b>Layout Incrementally</b> to incrementally "add" the selected 
 * elements optimally into the existing graph.
 * </p>
 */
public class SimpleSwimLaneLayouterDemo extends DemoBase
{
  /** Used to store the hints for the incremental layout */
  private DataMap hintMap;

  /** Used to store the swim lane information for each node */
  private NodeMap swimLaneMap;

  /** the layouter and the hintsfactory */
  private IncrementalHierarchicLayouter hierarchicLayouter;
  private IncrementalHintsFactory hintsFactory;

  /** the drawable for the swimlanes */
  private SwimLaneDrawable swimLaneDrawable;

  public SimpleSwimLaneLayouterDemo()
  {
    final Graph2D graph = view.getGraph2D();
    EdgeRealizer defaultER = graph.getDefaultEdgeRealizer();
    defaultER.setArrow(Arrow.STANDARD);

    // create a map to store the hints for the incremental layout mechanism
    hintMap = Maps.createHashedDataMap();
    swimLaneMap = graph.createNodeMap();
    graph.addDataProvider(IncrementalHierarchicLayouter.INCREMENTAL_HINTS_DPKEY, hintMap);
    graph.addDataProvider(IncrementalHierarchicLayouter.SWIMLANE_DESCRIPTOR_DPKEY, swimLaneMap);

    // create the layouter
    hierarchicLayouter = new IncrementalHierarchicLayouter();

    // set some defaults
    hierarchicLayouter.getEdgeLayoutDescriptor().setMinimumFirstSegmentLength(15);
    hierarchicLayouter.getEdgeLayoutDescriptor().setMinimumLastSegmentLength(20);
    hierarchicLayouter.getEdgeLayoutDescriptor().setOrthogonallyRouted(true);
    hierarchicLayouter.getEdgeLayoutDescriptor().setMinimumDistance(10.0d);

    hierarchicLayouter.getNodeLayoutDescriptor().setLayerAlignment(0.5d);
    hierarchicLayouter.setMinimumLayerDistance(30.0d);
    hierarchicLayouter.getNodeLayoutDescriptor().setNodeLabelMode(NodeLayoutDescriptor.NODE_LABEL_MODE_CONSIDER_FOR_DRAWING);
    hierarchicLayouter.setConsiderNodeLabelsEnabled(true);

    // set the node placer to barycenter mode so that the results are centered
    // nicely in the swimlanes if there is more room 
    ((SimplexNodePlacer) hierarchicLayouter.getNodePlacer()).setBaryCenterModeEnabled(true);

    // get a reference to a hints factory
    hintsFactory = hierarchicLayouter.createIncrementalHintsFactory();

    // disable the component layouter (optional)
    hierarchicLayouter.setComponentLayouterEnabled(false);

    // add a drawable to visualize the swim lane geometry
    view.addBackgroundDrawable(swimLaneDrawable = new SwimLaneDrawable(view.getGraph2D(), swimLaneMap));

    loadGraph( "resource/swimlane.gml" );
    calcLayout( hierarchicLayouter );
  }

  class LayoutFromSketchAction extends AbstractAction
  {
    LayoutFromSketchAction()
    {
      super("Layout From Sketch");
    }

    public void actionPerformed(ActionEvent ev)
    {
      calcIncrementalLayout(new NodeList().nodes());
    }
  }

  class LayoutIncrementallyAction extends AbstractAction
  {
    LayoutIncrementallyAction()
    {
      super("Layout Incrementally");
    }

    public void actionPerformed(ActionEvent ev)
    {
      calcIncrementalLayout(view.getGraph2D().selectedNodes());
    }
  }

  class LayoutAction extends AbstractAction
  {
    LayoutAction()
    {
      super("Layout");
    }

    public void actionPerformed(ActionEvent ev)
    {
      calcFreshLayout();
    }
  }

  protected JToolBar createToolBar()
  {
    JToolBar tb = super.createToolBar();
    tb.add(new LayoutAction());
    tb.add(new LayoutFromSketchAction());
    tb.add(new LayoutIncrementallyAction());
    return tb;
  }

  public void calcFreshLayout(){
    hierarchicLayouter.setLayoutMode(IncrementalHierarchicLayouter.LAYOUT_MODE_FROM_SCRATCH);
    calcLayout(hierarchicLayouter);
  }

  public void updateSwimLanes(){
    for (NodeCursor nc = view.getGraph2D().nodes(); nc.ok(); nc.next()){
      SwimLaneDescriptor sld = new SwimLaneDescriptor(view.getGraph2D().getLabelText(nc.node()));
      sld.setLeftLaneInset(5);
      sld.setRightLaneInset(5);
      sld.setMinimumLaneWidth(100);
      swimLaneMap.set(nc.node(), sld);
    }
  }

  public void calcIncrementalLayout(NodeCursor incrementalNodes){
    try {
      // mark nodes as "new"
      for (incrementalNodes.toFirst(); incrementalNodes.ok(); incrementalNodes.next()){
        hintMap.set(incrementalNodes.node(), hintsFactory.createLayerIncrementallyHint(incrementalNodes.node()));
      }
      // read the old nodes from the sketch
      hierarchicLayouter.setLayoutMode(IncrementalHierarchicLayouter.LAYOUT_MODE_INCREMENTAL);
      // calculate the layout incrementally
      calcLayout(hierarchicLayouter);
    } finally {
      // reset the marks
      for (incrementalNodes.toFirst(); incrementalNodes.ok(); incrementalNodes.next()){
        hintMap.set(incrementalNodes.node(), null);
      }
    }
  }

  protected void calcLayout(Layouter layouter){
    Graph2D graph = view.getGraph2D();
    if (!graph.isEmpty()){
      Cursor oldCursor = view.getCanvasComponent().getCursor();
      try {
        // associate swim lane descriptors with each node...
        updateSwimLanes();
        GraphLayout result = new BufferedLayouter(layouter).calcLayout(view.getGraph2D());
        LayoutMorpher morpher = new LayoutMorpher(view, result);
        morpher.setSmoothViewTransform(true);
        morpher.setPreferredDuration(300);
        morpher.execute();
      } finally {
        view.getCanvasComponent().setCursor(oldCursor);
      }
    }
    swimLaneDrawable.updateLanes();
    view.fitContent();
    view.updateView();
  }

  protected EditMode createEditMode() {
    EditMode editMode = super.createEditMode();
    editMode.setPopupMode( new PopupMode( ){
      public JPopupMenu getNodePopup( Node v ) {
        return createPopup();
      }

      private JPopupMenu createPopup() {
        JPopupMenu menu = new JPopupMenu();
        JMenu laneMenu = new JMenu( "Move to lane" );
        menu.add( laneMenu );
        laneMenu.add( new MoveSelectedNodesToLayerAction(1) );
        laneMenu.add( new MoveSelectedNodesToLayerAction(2) );
        laneMenu.add( new MoveSelectedNodesToLayerAction(3) );
        laneMenu.add( new MoveSelectedNodesToLayerAction(4) );
        laneMenu.add( new MoveSelectedNodesToLayerAction(5) );
        laneMenu.add( new MoveSelectedNodesToLayerAction(6) );
        return menu;
      }

      public JPopupMenu getSelectionPopup( double x, double y ) {
        return createPopup();
      }
    });
    return editMode;
  }

  /**
   * Drawable implementation that displays the swim lanes
   */
  static final class SwimLaneDrawable implements Drawable {
    private List lanes = new ArrayList(20);
    private static final Color[] colors = new Color[] {new Color(150, 150, 255), new Color(0 , 0, 150)};

    private Rectangle bounds = new Rectangle(20,20,200,200);

    private Graph2D graph;
    private NodeMap swimLaneMap;
    private double spacing = 20.0d;

    SwimLaneDrawable(Graph2D graph, NodeMap swimLaneMap){
      this.graph = graph;
      this.swimLaneMap = swimLaneMap;
    }

    public Rectangle getBounds()
    {
      return bounds;
    }

    public void updateLanes(){
      lanes.clear();
      if (graph.N() < 1) return;
      double minY = Double.MAX_VALUE;
      double maxY = -Double.MAX_VALUE;
      for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()){
        final Node node = nc.node();
        final SwimLaneDescriptor swd = (SwimLaneDescriptor) swimLaneMap.get(node);
        if (swd == null) continue;
        while (lanes.size() - 1 < swd.getComputedLaneIndex()){
          lanes.add(new Rectangle2D.Double(0,0,-1,-1));
        }
        Rectangle2D.Double laneRect = (Rectangle2D.Double) lanes.get(swd.getComputedLaneIndex());
        final NodeLayout nl = graph.getNodeLayout(node);
        laneRect.x = swd.getComputedLanePosition() + swd.getLeftLaneInset();
        laneRect.width = swd.getComputedLaneWidth() - (swd.getLeftLaneInset() + swd.getRightLaneInset());
        minY = Math.min(laneRect.y, nl.getY());
        maxY = Math.max(maxY, nl.getY() + nl.getHeight());
      }

      double minX = Double.MAX_VALUE;
      double maxX = -Double.MAX_VALUE;

      for (int i = 0; i < lanes.size(); i++){
        Rectangle2D.Double rect = (Rectangle2D.Double) lanes.get(i);
        rect.y = minY - spacing;
        rect.height = maxY - minY + spacing * 2;
        minX = Math.min(rect.x, minX);
        maxX = Math.max(rect.getMaxX(), maxX);
      }
      bounds.setFrame(minX, minY, maxX - minX, maxY - minY);
      graph.updateViews();
    }

    final Line2D.Double line = new Line2D.Double();
    public void paint(Graphics2D g)
    {
      for (int i = 0; i < lanes.size(); i++){
        Color color = colors[i % colors.length];
        g.setColor(color);
        Rectangle2D.Double lane = (Rectangle2D.Double) lanes.get(i);
        line.x1 = line.x2 = lane.x;
        line.y1 = lane.y;
        line.y2 = lane.y + lane.height;
        g.draw(line);
        line.x1 = line.x2 = lane.x + lane.width;
        g.draw(line);
      }
    }
  }


  /**
   * Launches this demo.
   */
  public static void main(String args[])
  {
    initLnF();
    SimpleSwimLaneLayouterDemo demo = new SimpleSwimLaneLayouterDemo();
    demo.start("Simple Swimlane Layout Demo");
  }

  private class MoveSelectedNodesToLayerAction extends AbstractAction {
    private int layer;

    MoveSelectedNodesToLayerAction( int layer ) {
      super( String.valueOf( layer ) );
      this.layer = layer;
    }

    public void actionPerformed( ActionEvent e ) {
      for ( NodeCursor nodeCursor = view.getGraph2D().selectedNodes(); nodeCursor.ok(); nodeCursor.next() ) {
        Node node = nodeCursor.node();
        view.getGraph2D().setLabelText( node, String.valueOf( layer ) );
      }
      calcIncrementalLayout( view.getGraph2D().selectedNodes() );
    }
  }
}