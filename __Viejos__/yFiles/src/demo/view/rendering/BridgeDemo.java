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
import y.base.Edge;
import y.base.Node;
import y.geom.YPoint;
import y.view.Arrow;
import y.view.BridgeCalculator;
import y.view.DefaultGraph2DRenderer;
import y.view.Graph2D;
import y.view.Graph2DRenderer;

import javax.swing.JComboBox;
import javax.swing.JToolBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class demonstrates how to utilize {@link BridgeCalculator} to draw bridges/gaps for crossing edges.
 * It demonstrates how {@link y.view.PolyLineEdgeRealizer} automatically makes use of
 * the {@link BridgeCalculator} registered with the default {@link Graph2DRenderer} instance to
 * incorporate the calculation of bridges.
 *
 */
public class BridgeDemo extends DemoBase {
  BridgeCalculator bridgeCalculator;

  public BridgeDemo() {
    // create the BridgeCalculator
    bridgeCalculator = new BridgeCalculator();

    // register it with the DefaultGraph2DRenderer
    ((DefaultGraph2DRenderer) view.getGraph2DRenderer()).setBridgeCalculator(bridgeCalculator);

    // create a nice graph that shows all possible configurations...
    final Graph2D graph = view.getGraph2D();
    graph.getDefaultEdgeRealizer().setTargetArrow(Arrow.SHORT);
    Node[] nodes = new Node[16];
    int count = 0;
    for (int i = 1; i < 5; i++) {
      nodes[count++] = graph.createNode(50 + 40 * i, 260);
      nodes[count++] = graph.createNode(50 + 40 * i, 40);
      nodes[count++] = graph.createNode(40, 50 + 40 * i);
      nodes[count++] = graph.createNode(260, 50 + 40 * i);
    }

    graph.createEdge(nodes[0], nodes[1]);

    Edge e = graph.createEdge(nodes[0], nodes[1]);
    graph.setSourcePointRel(e, new YPoint(5, 0));
    graph.setTargetPointRel(e, new YPoint(5, 0));

    graph.createEdge(nodes[5], nodes[4]);

    graph.createEdge(nodes[2], nodes[3]);
    e = graph.createEdge(nodes[2], nodes[3]);
    graph.setSourcePointRel(e, new YPoint(0, 5));
    graph.setTargetPointRel(e, new YPoint(0, 5));

    graph.createEdge(nodes[7], nodes[6]);

    graph.createEdge(nodes[2 + 8], nodes[3 + 8]);
    graph.createEdge(nodes[7 + 8], nodes[6 + 8]);

    graph.createEdge(nodes[0 + 8], nodes[1 + 8]);
    graph.createEdge(nodes[5 + 8], nodes[4 + 8]);
  }

  protected JToolBar createToolBar() {
    final JToolBar toolBar = super.createToolBar();
    final JComboBox crossingModeCB = new JComboBox(new Object[]{"Order Induced", "Horizontal Crosses Vertical", "Vertical Crosses Horizontal"});
    crossingModeCB.setSelectedIndex(0);
    crossingModeCB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bridgeCalculator.setCrossingMode((short) crossingModeCB.getSelectedIndex());
        view.getGraph2D().updateViews();
      }
    });
    toolBar.add(crossingModeCB);
    final JComboBox crossingStyleCB = new JComboBox(new Object[]{"Gap", "Arc", "Square", "Two Sides", "Scaled Arc", "Scaled Square", "Scaled Two Sides"});
    crossingStyleCB.setSelectedIndex(1);
    crossingStyleCB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bridgeCalculator.setCrossingStyle((short) crossingStyleCB.getSelectedIndex());
        view.getGraph2D().updateViews();
      }
    });
    toolBar.add(crossingStyleCB);
    final JComboBox orientationStyleCB = new JComboBox(
        new Object[]{"Up", "Down", "Left", "Right", "Positive", "Negative", "Flow Left", "Flow Right"});
    orientationStyleCB.setSelectedIndex(4);
    orientationStyleCB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bridgeCalculator.setOrientationStyle((short) (orientationStyleCB.getSelectedIndex() + 1));
        view.getGraph2D().updateViews();
      }
    });
    toolBar.add(orientationStyleCB);
    return toolBar;
  }

  public static void main(String[] args) {
    initLnF();
    new BridgeDemo().start("Bridge Demo");
  }
}
