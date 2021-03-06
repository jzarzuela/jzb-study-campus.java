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
import y.base.Edge;
import y.base.EdgeCursor;
import y.base.EdgeMap;
import y.base.Node;
import y.base.NodeCursor;
import y.base.NodeMap;
import y.layout.BufferedLayouter;
import y.layout.GraphLayout;
import y.layout.hierarchic.ConstraintLayerer;
import y.layout.hierarchic.ConstraintLayerer.ConstraintFactory;
import y.layout.hierarchic.HierarchicLayouter;
import y.view.Arrow;
import y.view.EdgeRealizer;
import y.view.Graph2D;
import y.view.LayoutMorpher;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Demo that shows how to use the {@link ConstraintLayerer} class.
 * <p> With the buttons on the left side of the GUI,
 * various constraints can be set on the currently selected nodes (either absolute top/bottom level or relative layering
 * constraints). The "Top-most"/"Bottom-most" buttons set absolute layering constraints, whereas the other buttons assign
 * relative layering constraints. The top button ("Remove constraints") clears all constraints from the currently
 * selected nodes.
 * </p>
 * <p>
 * Additionally, a DataProvider is registered under the key <code>ConstraintLayerer.EDGE_WEIGHTS_DPKEY</code>, and if a numeric edge label is set, that label gets set as
 * value for that DataProvider.
 * </p>
 */
public class ConstraintLayererDemo extends DemoBase {

  public static final int TOP_LEVEL = 1;
  public static final int BOTTOM_LEVEL = 5;

  private NodeMap levels;
  private EdgeMap weights;

  public ConstraintLayererDemo() {
    super();
    final Graph2D graph = view.getGraph2D();
    EdgeRealizer defaultER = graph.getDefaultEdgeRealizer();
    defaultER.setArrow(Arrow.STANDARD);

    levels = graph.createNodeMap();

    //additionally, register an edge weight map
    weights = graph.createEdgeMap();
    graph.addDataProvider(ConstraintLayerer.EDGE_WEIGHTS_DPKEY, weights);

    JPanel left = new JPanel(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.NORTHWEST;

    gbc.gridx = 0;
    gbc.gridy = GridBagConstraints.RELATIVE;

    JPanel groupSpec;
    groupSpec = new JPanel(new GridBagLayout());
    groupSpec.setBorder(BorderFactory.createTitledBorder("Layering constraints"));

    // build the grouping mechanism

    Color[] groupColors = new Color[]{null, Color.RED, Color.ORANGE, Color.yellow, new Color(204, 255, 0), Color.GREEN};
    String[] groupLabels = new String[]{"Remove constraint", "Top-most level", "Above medium level", "Medium level", "Below medium level",
        "Bottom-most level"};
    gbc.insets = new Insets(5, 0, 5, 0);
    for (int i = 0; i < groupColors.length; i++) {
      if(i > 1 && i < 5) {
        gbc.insets = new Insets(0, 0, 0, 0);
      }
      else {
        gbc.insets = new Insets(5, 0, 5, 0);
      }
      GroupButton groupButton = new GroupButton(groupColors[i], groupLabels[i], i);
      groupSpec.add(groupButton, gbc);
    }
    gbc.weightx = gbc.weighty = 1;
    groupSpec.add(new JPanel(), gbc);
    gbc.weightx = gbc.weighty = 0;
    gbc.gridwidth = 2;
    left.add(groupSpec, gbc);
    gbc.gridwidth = 1;

    gbc.weighty = 1.0d;
    left.add(new JPanel(), gbc);

    contentPane.add(new JScrollPane(left), BorderLayout.WEST);
  }

  protected JToolBar createToolBar() {
    JToolBar bar = super.createToolBar();
    bar.add(new LayoutAction());
//    bar.add(new OptionAction());
    return bar;
  }

  /** this method assigns the level id and the corresponding color hint to the currently selected nodes */
  protected void assignLevel(Color color, int index) {
    Graph2D graph = view.getGraph2D();

    for (NodeCursor nc = graph.selectedNodes(); nc.ok(); nc.next()) {
      Node n = nc.node();
      // set the color hint
      if (color != null) {
        graph.getRealizer(n).setFillColor(color);
      } else {
        graph.getRealizer(n).setFillColor(graph.getDefaultNodeRealizer().getFillColor());
      }
      levels.setInt(n, index);
    }
    graph.updateViews();
  }


  // helper class
  class GroupButton extends JButton implements ActionListener {
    Color color;
    int index;

    GroupButton(Color color, String groupLabel, int index) {
      super("");
      setText(groupLabel);
      this.color = color;
      this.index = index;
      setBackground(color);
      this.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
      ConstraintLayererDemo.this.assignLevel(color, index);
    }
  }

  class LayoutAction extends AbstractAction {
    LayoutAction() {
      super("Layout");
    }

    public void actionPerformed(ActionEvent ev) {
      Graph2D graph = view.getGraph2D();


      doLayout(graph);
      graph.updateViews();
      view.fitContent();
    }
  }

  private void doLayout(Graph2D graph) {
    HierarchicLayouter hl = new HierarchicLayouter();
    ConstraintFactory cf = ConstraintLayerer.createConstraintFactory(graph);
    createConstraints(graph, cf);
    ConstraintLayerer layerer = new ConstraintLayerer();
    layerer.setAllowSameLayerEdges(false);
    hl.setLayerer(layerer);

    GraphLayout gl = new BufferedLayouter(hl).calcLayout(graph);
    new LayoutMorpher(view, gl).execute();
    cf.dispose();
  }

  /**
   * Assign constraints to nodes and edges
   *
   * @param graph
   * @param cf
   */
  private void createConstraints(Graph2D graph, ConstraintFactory cf) {
    Node flr, slr, thrdlr;
    flr = slr = thrdlr = null;
    for (NodeCursor nodeCursor = graph.nodes(); nodeCursor.ok(); nodeCursor.next()) {
      Node node = nodeCursor.node();
      int index = levels.getInt(node);
      switch (index) {
        case TOP_LEVEL:
          cf.addPlaceNodeAtTopConstraint(node);
          break;
        case BOTTOM_LEVEL:
          cf.addPlaceNodeAtBottomConstraint(node);
          break;
        case TOP_LEVEL + 1:
          if (flr == null) {
            flr = node;
          } else {
            cf.addPlaceNodeInSameLayerConstraint(flr, node);
          }
          break;
        case TOP_LEVEL + 2:
          if (slr == null) {
            slr = node;
          } else {
            cf.addPlaceNodeInSameLayerConstraint(slr, node);
          }
          break;
        case TOP_LEVEL + 3:
          if (thrdlr == null) {
            thrdlr = node;
          } else {
            cf.addPlaceNodeInSameLayerConstraint(thrdlr, node);
          }
          break;
        default:
          break;
      }
      if (flr != null && slr != null) {
        //place second layer below first layer
        cf.addPlaceNodeBelowConstraint(flr, slr);
      }
      if (slr != null && thrdlr != null) {
        //place thrd layer below second layer
        cf.addPlaceNodeBelowConstraint(slr, thrdlr);
      }
      if (flr != null && thrdlr != null) {
        //place first layer above 3rd layer
        cf.addPlaceNodeAboveConstraint(thrdlr, flr);
      }
    }
    //assign weights from edge labels
    for (EdgeCursor edgeCursor = graph.edges(); edgeCursor.ok(); edgeCursor.next()) {
      Edge edge = edgeCursor.edge();
      String str = graph.getLabelText(edge);
      try {
        weights.setInt(edge, Integer.parseInt(str));
      }
      catch (NumberFormatException e) {
        weights.setInt(edge, 1);
      }
    }
  }


  /** Launches this demo. */
  public static void main(String args[]) {
    initLnF();
    ConstraintLayererDemo demo = new ConstraintLayererDemo();

    demo.start("ConstraintLayerer Demo");
  }


}
