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
package demo.view.hierarchy;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;

import y.algo.GraphConnectivity;
import y.algo.Trees;
import y.base.EdgeCursor;
import y.base.Graph;
import y.base.Node;
import y.base.NodeCursor;
import y.base.NodeList;
import y.geom.YPoint;
import y.layout.LayoutTool;
import y.module.LaunchModuleAction;
import y.view.Arrow;
import y.view.EditMode;
import y.view.Graph2D;
import y.view.Graph2DView;
import y.view.NodeLabel;
import y.view.NodeRealizer;
import y.view.Overview;
import y.view.PopupMode;
import y.view.ProxyShapeNodeRealizer;
import y.view.ViewMode;
import y.view.hierarchy.DefaultNodeChangePropagator;
import y.view.hierarchy.GroupNodeRealizer;
import y.view.hierarchy.HierarchyEditMode;
import y.view.hierarchy.HierarchyJTree;
import y.view.hierarchy.HierarchyManager;
import y.view.hierarchy.HierarchyTreeModel;
import y.view.hierarchy.HierarchyTreeTransferHandler;
import demo.view.DemoBase;

/**
 * This application demonstrates the use of <b>Nested Graph Hierarchy</b> technology 
 * and also <b>Node Grouping</b>. 
 * <p>
 * The main view displays a nested graph hierarchy from a specific hierarchy level 
 * on downward. 
 * So-called folder nodes are used to nest graphs within them, so-called group nodes 
 * are used to group a set of nodes. 
 * <br>
 * Both these types of node look similar but represent different concepts: while 
 * grouped nodes still belong to the same graph as their enclosing group node, the 
 * graph that is contained within a folder node is a separate entity. 
 * </p>
 * <p>
 * There are several ways provided to create, modify, and navigate a graph hierarchy: 
 * <ul>
 * <li>
 * By means of popup menu actions selected nodes can be grouped and also nested. 
 * Reverting these operations is also supported. 
 * </li>
 * <li>
 * By Shift-dragging nodes they can be moved into and out of group nodes. 
 * </li>
 * <li>
 * Double-clicking on a folder node "drills" into the nested graph hierarchy and 
 * displays only the folder node's content, i.e., effectively moves a level deeper 
 * in the hierarchy. 
 * A button in the tool bar allows to move back to see the folder node again (one 
 * level higher in the hierarchy). 
 * </li>
 * <li>
 * Folder node and group node both allow switching to the other type by either using 
 * popup menu actions or clicking the icon in their upper-left corner. 
 * </li>
 * </ul>
 * </p>
 * <p>
 * Note that the size of group nodes is determined by the space requirements of 
 * their content, i.e., their resizing behavior is restricted. 
 * </p>
 */
public class HierarchyDemo extends DemoBase
{
  
  /**
   * The graph hierarchy manager. This is the central class for managing
   * a hierarchy of graphs.
   */
  protected HierarchyManager hierarchy;


  /**
   * Instantiates this demo. Builds the GUI.
   */
  public HierarchyDemo()
  {
    Graph2D rootGraph = view.getGraph2D();
    
    //add arrows to the default edges of the graph
    rootGraph.getDefaultEdgeRealizer().setTargetArrow(Arrow.STANDARD);
    
    //create a hierarchy manager with the given root graph
    hierarchy = new HierarchyManager(rootGraph);
    
    //register a hierarchy listener that will automatically adjust the state of 
    //the realizers that are used for the group nodes
    hierarchy.addHierarchyListener(new GroupNodeRealizer.StateChangeListener());
    
    //propagates text label changes on nodes as change events
    //on the hierarchy.
    rootGraph.addGraph2DListener(new DefaultNodeChangePropagator());

    //create a TreeModel, that represents the hierarchy of the nodes.
    HierarchyTreeModel htm = new HierarchyTreeModel(hierarchy);
    
    //use a convenience comparator that sorts the elements in the tree model
    htm.setChildComparator(HierarchyTreeModel.createNodeStateComparator(hierarchy));

    //display the graph hierarchy in a special JTree using the given TreeModel
    JTree tree = new HierarchyJTree(hierarchy, htm);
    
    //add a navigational action to the tree.
    tree.addMouseListener(new HierarchyJTreeDoubleClickListener(view));

    //add drag and drop functionality to HierarchyJTree. The drag and drop gesture
    //will allow to reorganize the group structure using HierarchyJTree.
    tree.setDragEnabled(true);
    tree.setTransferHandler(new HierarchyTreeTransferHandler(hierarchy));


    //add another view mode that acts upon clicking on
    //a folder node and clicking on the open/close icon
    view.addViewMode(new HierarchicClickViewMode());
    
    //plug the gui elements together and add them to the pane
    JScrollPane scrollPane = new JScrollPane(tree);
    scrollPane.setPreferredSize(new Dimension(150,0));
    JPanel leftPane = new JPanel(new BorderLayout());
    
    view.fitContent();
    
    Overview overView = new Overview(view);
    overView.setPreferredSize(new Dimension(150,150));
    leftPane.add(overView,BorderLayout.NORTH);
    leftPane.add(scrollPane);

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, view);

    contentPane.add(splitPane,BorderLayout.CENTER);

    loadInitialGraph();
  }

  protected void loadInitialGraph() {
    loadGraph("resource/hierarchy.gml");
  }

  /**
   * Creates the application toolbar
   */
  protected JToolBar createToolBar()
  {
    JToolBar bar = super.createToolBar();
    bar.add(new ViewParentAction());
    return bar;
  }
  
  protected JMenuBar createMenuBar(){
    JMenuBar mb = super.createMenuBar();
    JMenu menu = new JMenu("Tools");
    menu.add(new JMenuItem(new FoldComponentsAction()));
    menu.add(new JMenuItem(new FoldSubtreesAction()));
    menu.add(new JMenuItem(new UnfoldAllAction()));
    
    menu.addSeparator();                              //@VIEW_EXCLUSION@
    Action a;                                         //@VIEW_EXCLUSION@
    a = new LaunchModuleAction(view,                  //@VIEW_EXCLUSION@
      new demo.module.HierarchicLayoutModule());      //@VIEW_EXCLUSION@
    a.putValue(Action.NAME, "Hierarchical Layout");   //@VIEW_EXCLUSION@
    menu.add(a);                                      //@VIEW_EXCLUSION@
    a = new LaunchModuleAction(view,                  //@VIEW_EXCLUSION@
      new demo.module.OrthogonalLayoutModule());      //@VIEW_EXCLUSION@
    a.putValue(Action.NAME, "Orthogonal Layout");     //@VIEW_EXCLUSION@
    menu.add(a);                                      //@VIEW_EXCLUSION@
    a = new LaunchModuleAction(view,                  //@VIEW_EXCLUSION@
      new demo.module.OrganicLayoutModule());         //@VIEW_EXCLUSION@
    a.putValue(Action.NAME, "Organic Layout");        //@VIEW_EXCLUSION@
    menu.add(a);                                      //@VIEW_EXCLUSION@
    mb.add(menu);                                     //@VIEW_EXCLUSION@
    
    menu.addSeparator();
    menu.add(new JMenuItem(new LoadInitialGraphAction()));
    
    return mb;
  }

  protected void registerViewModes() {
    EditMode mode = new HierarchyEditMode();
    //add hierarchy actions to the views popup menu
    mode.setPopupMode(new HierarchicPopupMode());
    view.addViewMode( mode );
  }


  /**
   * Launches this demo.
   */
  public static void main(String args[])
  {
    initLnF();
    
    HierarchyDemo demo = new HierarchyDemo();

    demo.start("Hierarchy Demo");
  }


  //////////////////////////////////////////////////////////////////////////////
  // VIEW MODES ////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////

  /**
   * provides the context sensitive popup menus
   */
  class HierarchicPopupMode extends PopupMode
  {
    public JPopupMenu getPaperPopup(double x, double y)
    {
      return addFolderPopupItems(new JPopupMenu(), x,y, null, false);
    }

    public JPopupMenu getNodePopup(Node v)
    {
      Graph2D graph = getGraph2D();
      return addFolderPopupItems(new JPopupMenu(),
                                 graph.getCenterX(v),
                                 graph.getCenterY(v),
                                 v, true);
    }

    public JPopupMenu getSelectionPopup(double x, double y)
    {
      return addFolderPopupItems(new JPopupMenu(),x,y, null, getGraph2D().selectedNodes().ok());
    }

    JPopupMenu addFolderPopupItems(JPopupMenu pm, double x, double y, Node node, boolean selected)
    {
      AbstractAction action;
      action = new GroupSelectionAction(x, y);
      pm.add(action);
      action = new UngroupSelectionAction();
      pm.add(action);
      action = new CloseGroupAction(node);
      action.setEnabled(node != null && hierarchy.isGroupNode(node));
      pm.add(action);
      pm.addSeparator();
      action = new CreateFolderNodeAction(getGraph2D(),x,y);
      action.setEnabled(node == null);
      pm.add(action);
      action = new FoldSelectionAction();
      action.setEnabled(selected);
      pm.add(action);
      action = new UnfoldSelectionAction();
      action.setEnabled(selected && !hierarchy.isRootGraph(getGraph2D()));
      pm.add(action);
      action = new ExtractFolderAction(node);
      action.setEnabled(node != null && hierarchy.isFolderNode(node));
      pm.add(action);
      action = new OpenFolderAction(node);
      action.setEnabled(node != null && hierarchy.isFolderNode(node));
      pm.add(action);
      return pm;
    }
  }
  
  /**
   * view mode that allows to navigate to the inner graph of a folder node.
   * a double click on a folder node triggers the action.
   */ 
  class HierarchicClickViewMode extends ViewMode
  {
    private static final String SRC_PORTS_DP_KEY = "HierarchicClickViewMode.SRC_PORTS";
    private static final String TGT_PORTS_DP_KEY = "HierarchicClickViewMode.TGT_PORTS";

    public void mouseClicked(MouseEvent e)
    {
      if(e.getClickCount() == 2)
      {
        Node v = getHitInfo(e).getHitNode();
        if(v != null)
        {
          navigateToInnerGraph(v);
        }
        else
        {
          navigateToParentGraph();
        }
      } else {
        Node v = getHitInfo(e).getHitNode(); 
        if(v != null && !hierarchy.isNormalNode(v))
        {
          double x = translateX(e.getX());
          double y = translateY(e.getY());
          Graph2D graph =  this.view.getGraph2D();
          NodeRealizer r = graph.getRealizer(v);
          GroupNodeRealizer gnr = null;
          if(r instanceof GroupNodeRealizer)
          {
            gnr = (GroupNodeRealizer)r;
          }
          else if(r instanceof ProxyShapeNodeRealizer &&
                  ((ProxyShapeNodeRealizer)r).getRealizerDelegate() instanceof GroupNodeRealizer)
          {
            gnr = (GroupNodeRealizer)((ProxyShapeNodeRealizer)r).getRealizerDelegate();
          }
          if(gnr != null)
          {
            NodeLabel handle = gnr.getStateLabel();
            if(handle.getBox().contains(x,y))
            {
              if(hierarchy.isFolderNode(v))
              {
                openFolder(v);
              }
              else
              {
                closeGroup(v);
              }
            }
          }
        }
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////
  // OPERATIONS ////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////

  /**
   * navigates to the graph inside of the given folder node
   */ 
  void navigateToInnerGraph(Node folderNode)
  {
    Graph2D graph = view.getGraph2D();
    if(hierarchy.isFolderNode(folderNode))
    {
      Graph2D innerGraph =  (Graph2D)hierarchy.getInnerGraph(folderNode);
      Rectangle box = innerGraph.getBoundingBox();
      view.setGraph2D(innerGraph);
      view.setCenter(box.x+box.width/2,box.y+box.height/2);
      innerGraph.updateViews();
    }
  }

  /**
   * navigates to the parent graph of the graph currently displayed
   * in the graph view.
   */ 
  void navigateToParentGraph()
  {
    Graph2D graph = view.getGraph2D();
    if(!hierarchy.isRootGraph(graph))
    {
      Graph2D parentGraph = (Graph2D)hierarchy.getParentGraph(graph);
      view.setGraph2D(parentGraph);
      Node anchor = hierarchy.getAnchorNode(graph);
      Graph2DView view = this.view;
      view.setZoom(1.0);
      view.setCenter(parentGraph.getCenterX(anchor),parentGraph.getCenterY(anchor));
      view.updateView();
    }
  }

  /**
   * creates a new folder node and moves the subgraph induced by the 
   * current node selection to the inner graph of that folder node.
   */  
  void foldSelection()
  {
    Graph2D graph = view.getGraph2D();
    Node folderNode = hierarchy.createFolderNode(graph);
    graph.setLabelText(folderNode, "Folder");
    
    hierarchy.foldSubgraph(new NodeList(graph.selectedNodes()), folderNode);

    Graph2D innerGraph = (Graph2D)hierarchy.getInnerGraph(folderNode);
    innerGraph.unselectAll();

    Rectangle box = innerGraph.getBoundingBox();
    graph.setSize(folderNode,box.width+10, box.height+10);
    graph.setLocation(folderNode,box.x-5,box.y-5);
    graph.updateViews();
  }

  
  /**
   * moves the graph induced by the 
   * current node selection to the parent graph of the
   * currently viewed graph.
   */
  void unfoldSelection()
  {
    Graph2D graph = view.getGraph2D();
    NodeList selectedNodes = new NodeList(graph.selectedNodes());
    
    if(!selectedNodes.isEmpty() && !hierarchy.isRootGraph(graph))
    {
      hierarchy.unfoldSubgraph(graph, selectedNodes );
    }
    graph.updateViews();
  }
  
  /**
   * moves all nodes within the given folder node to the
   * parent graph and removes the now empty folder node.
   */
  void extractFolder(Node folderNode)
  {
    Graph2D graph      = (Graph2D)folderNode.getGraph();
    Graph   innerGraph = hierarchy.getInnerGraph(folderNode);
    NodeList subNodes = new NodeList(innerGraph.nodes());
    hierarchy.unfoldSubgraph(innerGraph, subNodes);
    
    //cleanup  metaNode
    //hierarchy.removeFolderNode(folderNode);
    graph.removeNode(folderNode);

    //ok, some sugar follows...
    for(NodeCursor nc = subNodes.nodes(); nc.ok(); nc.next())
      graph.setSelected(nc.node(),true);
    
    graph.updateViews();
  }
  
  /**
   * folds each separate connected graph component to 
   * a newly created folder node,
   */
  void foldComponents()
  {
    Graph2D graph  = view.getGraph2D();

    NodeList[] components = GraphConnectivity.connectedComponents(graph);

    if(components.length > 1)
    {
      for(int i = 0; i < components.length; i++)
      {
        NodeList subNodes = components[i];
        Node folderNode = hierarchy.createFolderNode(graph);
        graph.setCenter(folderNode,150*(i+1),0);
        graph.setLabelText(folderNode,"Comp " + (i+1));
        hierarchy.foldSubgraph(subNodes, folderNode);
      }
      view.fitContent();
      view.updateView();
    }

  }

  /**
   * This method finds tree-structures that are part of the
   * displayed graph. For each of these trees a new
   * folder-node will be created. Each tree will be
   * moved from the displayed graph to the corrsponding folder node.
   * Each nested tree could be be automatically layed out using,
   * for example, ballon layouter. The size-ratio of the folder-nodes will be
   * automatically adjusted to the size of the nested trees.
   * The code to automatically layout the subgraphs is commented out by default.
   * If the yFiles layout package is available uncomment the lines again to
   * activate the layouter.
   */
  void foldSubtrees()
  {
    Graph2D graph = view.getGraph2D();
    
    NodeList[] trees = Trees.getTreeNodes(graph);

    for(int i = 0; i < trees.length; i++)
    {
      NodeList tree = trees[i];

      Node folderNode = hierarchy.createFolderNode(graph);

      hierarchy.foldSubgraph(tree, folderNode);

      Graph2D innerGraph = (Graph2D)hierarchy.getInnerGraph(folderNode);

      new y.layout.BufferedLayouter(new y.layout.tree.BalloonLayouter()).doLayout(innerGraph); //@VIEW_EXCLUSION@
      
      //adjust label and size of folderNode
      Node root = tree.firstNode();
      String rootName = innerGraph.getLabelText(root);
      graph.setLabelText(folderNode,rootName + " Tree");

      Rectangle box = innerGraph.getBoundingBox();
      if(box.height > box.width)
        graph.setSize(folderNode, Math.max(150*box.width/box.height, 40), 170);
      else
        graph.setSize(folderNode, 150, Math.max(40,150*box.height/box.width+20));
    }

    view.fitContent();
    graph.updateViews();
  }
  
  /**
   * recursively unfold all folder nodes in the displayed view.
   */
  void unfoldAll()
  {
    NodeList result = hierarchy.getFolderNodes(view.getGraph2D(),true);
    while(!result.isEmpty())
    {
      Node folderNode = result.popNode();
      extractFolder(folderNode);
    }
  }
  
  
  protected void closeGroup(Node groupNode)
  {
    Graph2D graph = view.getGraph2D();

    final double w = graph.getWidth(groupNode);
    final double h = graph.getHeight(groupNode);

    NodeList groupNodes = new NodeList();
    if(groupNode == null)
    {
      //use selected top level groups
      for(NodeCursor nc = graph.selectedNodes(); nc.ok(); nc.next())
      {
        Node v = nc.node();
        if(hierarchy.isGroupNode(v) && hierarchy.getLocalGroupDepth(v) == 0)
        {
          groupNodes.add(v);
        }
      }
    }
    else
    {
      groupNodes.add(groupNode);
    }

    graph.firePreEvent();
    for(NodeCursor nc = groupNodes.nodes(); nc.ok(); nc.next())
    {
      hierarchy.closeGroup(nc.node());
    }
    graph.firePostEvent();
    
    graph.unselectAll();
    for(NodeCursor nc = groupNodes.nodes(); nc.ok(); nc.next())
    {
      graph.setSelected(nc.node(), true);
    }

    // if the node size has changed, delete source ports of out-edges
    // and target ports of in-edges to ensure that all edges still connect
    // to the node
    if (w != graph.getWidth(groupNode) || h != graph.getHeight(groupNode)) {
      for (EdgeCursor ec = groupNode.outEdges(); ec.ok(); ec.next()) {
        graph.setSourcePointRel(ec.edge(), YPoint.ORIGIN);
      }
      for (EdgeCursor ec = groupNode.inEdges(); ec.ok(); ec.next()) {
        graph.setTargetPointRel(ec.edge(), YPoint.ORIGIN);
      }
    }

    graph.updateViews();
  }

  protected void openFolder(Node folderNode)
  {
    Graph2D graph = view.getGraph2D();

    final double w = graph.getWidth(folderNode);
    final double h = graph.getHeight(folderNode);

    NodeList folderNodes = new NodeList();
    if(folderNode == null)
    {
      //use selected top level groups
      for(NodeCursor nc = graph.selectedNodes(); nc.ok(); nc.next())
      {
        Node v = nc.node();
        if(hierarchy.isFolderNode(v))
        {
          folderNodes.add(v);
        }
      }
    }
    else
    {
      folderNodes.add(folderNode);
    }
    
    graph.firePreEvent();
    
    for(NodeCursor nc = folderNodes.nodes(); nc.ok(); nc.next())
    {
      //get original location of folder node
      Graph2D innerGraph = (Graph2D)hierarchy.getInnerGraph(nc.node());
      YPoint folderP = graph.getLocation(nc.node());
      NodeList innerNodes = new NodeList(innerGraph.nodes());
      
      hierarchy.openFolder(nc.node());
      
      //get new location of group node
      Rectangle2D.Double gBox = graph.getRealizer(nc.node()).getBoundingBox();
      //move grouped nodes to former location of folder node
      LayoutTool.moveSubgraph(graph, innerNodes.nodes(),
                              folderP.x - gBox.x,
                              folderP.y - gBox.y);
      YPoint groupP = graph.getLocation(nc.node());
    }
    graph.firePostEvent();
    
    
    graph.unselectAll();
    for(NodeCursor nc = folderNodes.nodes(); nc.ok(); nc.next())
    {
      graph.setSelected(nc.node(), true);
    }

    // if the node size has changed, delete source ports of out-edges
    // and target ports of in-edges to ensure that all edges still connect
    // to the node
    if (w != graph.getWidth(folderNode) || h != graph.getHeight(folderNode)) {
      for (EdgeCursor ec = folderNode.outEdges(); ec.ok(); ec.next()) {
        graph.setSourcePointRel(ec.edge(), YPoint.ORIGIN);
      }
      for (EdgeCursor ec = folderNode.inEdges(); ec.ok(); ec.next()) {
        graph.setTargetPointRel(ec.edge(), YPoint.ORIGIN);
      }
    }

    graph.updateViews();
  }

  void groupSelection(double x, double y)
  {
    Graph2D graph = view.getGraph2D();
    
    graph.firePreEvent();
    
    NodeList subNodes = new NodeList(graph.selectedNodes());
    Node groupNode;
    if(subNodes.isEmpty()) 
    {
      groupNode = hierarchy.createGroupNode(graph);
      if(Double.isNaN(x)  || Double.isNaN(y))
      {
        x = view.getCenter().getX();
        y = view.getCenter().getY();
      }
      graph.setCenter(groupNode, x,y);
    }
    else
    {
      Node nca = hierarchy.getNearestCommonAncestor(subNodes);
      groupNode = hierarchy.createGroupNode(nca);
      hierarchy.groupSubgraph(new NodeList(graph.selectedNodes()), groupNode);
    }
    graph.setLabelText(groupNode, "Group");
    graph.firePostEvent();
    
    graph.unselectAll();
    graph.setSelected(groupNode, true);
        
    graph.updateViews();
  }
  
  
  void ungroupSelection()
  {
    Graph2D graph = view.getGraph2D();
    graph.firePreEvent();
    
    hierarchy.ungroupSubgraph(new NodeList(graph.selectedNodes()));
    
    graph.firePostEvent();
    
    graph.updateViews();    
  }
  
  void down(Node v)
  {
    Graph2D graph = view.getGraph2D();
    
    if(v == null)
    {
      NodeCursor nc = graph.selectedNodes();
      if(nc.size() == 1)
      {
        v = nc.node();
      }
    }
      
    if(hierarchy.isFolderNode(v))
    {
      Graph2D innerGraph =  (Graph2D)hierarchy.getInnerGraph(v);
      Rectangle box = innerGraph.getBoundingBox();
      view.setGraph2D(innerGraph);
      view.setCenter(box.x+box.width/2,box.y+box.height/2);
      innerGraph.updateViews();
    }
  }
  
  void up()
  {
    Graph2D graph = view.getGraph2D();
    if(!hierarchy.isRootGraph(graph))
    {
      Graph2D parentGraph = (Graph2D)hierarchy.getParentGraph(graph);
      view.setGraph2D(parentGraph);
      Node anchor = hierarchy.getAnchorNode(graph);
      
      Graph2DView view = this.view;
      view.setZoom(1.0);
      view.setCenter(parentGraph.getCenterX(anchor),parentGraph.getCenterY(anchor));
      parentGraph.unselectAll();
      parentGraph.setSelected(anchor,true);
      view.updateView();
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////
  // ACTIONS ///////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////

  class FoldSubtreesAction extends AbstractAction
  {
    FoldSubtreesAction()
    {
      super("Fold Subtrees");
    }

    public void actionPerformed(ActionEvent e)
    {
      foldSubtrees();
    }
  }

  class FoldComponentsAction extends AbstractAction
  {
    FoldComponentsAction()
    {
      super("Fold Components");
    }

    public void actionPerformed(ActionEvent e)
    {
      foldComponents();
    }
  }

  class FoldSelectionAction extends AbstractAction
  {
    FoldSelectionAction()
    {
      super("Fold Selection");
    }

    public void actionPerformed(ActionEvent e)
    {
      foldSelection();
    }
  }

  class UnfoldSelectionAction extends AbstractAction
  {
    UnfoldSelectionAction()
    {
      super("Unfold Selection");
    }

    public void actionPerformed(ActionEvent e)
    {
      unfoldSelection();
    }
  }

  class UnfoldAllAction extends AbstractAction
  {
    UnfoldAllAction()
    {
      super("Unfold All");
    }

    public void actionPerformed(ActionEvent e)
    {
      unfoldAll();
    }
  }

  class ExtractFolderAction extends AbstractAction
  {
    Node folderNode;

    ExtractFolderAction(Node folderNode)
    {
      super("Extract Folder");
      this.folderNode = folderNode;
    }

    public void actionPerformed(ActionEvent e)
    {
      extractFolder(folderNode);
    }
  }

  class ViewParentAction extends AbstractAction
  {
    ViewParentAction()
    {
      super("View Parent");
    }

    public void actionPerformed(ActionEvent e)
    {
      navigateToParentGraph();
    }
  }
  
  class CreateFolderNodeAction extends AbstractAction
  {
    double x, y;
    Graph2D graph;

    CreateFolderNodeAction(Graph2D graph, double x, double y)
    {
      super("Create Folder");
      this.graph = graph;
      this.x = x;
      this.y = y;
    }

    public void actionPerformed(ActionEvent e)
    {
      Node folderNode = hierarchy.createFolderNode(graph);
      graph.setCenter(folderNode, x, y);
      graph.setLabelText(folderNode, "Folder");
      graph.updateViews();
    }
  }
  
  class CloseGroupAction extends AbstractAction
  {
    Node groupNode;
    CloseGroupAction(Node groupNode)
    {
      super("Close Group");
      this.groupNode = groupNode;
    }

    public void actionPerformed(ActionEvent e)
    {
      closeGroup(groupNode);   
    }
  }
  
  class OpenFolderAction extends AbstractAction
  {
    Node folderNode;
    OpenFolderAction(Node folderNode)
    {
      super("Open Folder");
      this.folderNode = folderNode;
    }

    public void actionPerformed(ActionEvent e)
    {
      openFolder(folderNode);   
    }
  }
  
  class GroupSelectionAction extends AbstractAction
  {
    double x;
    double y;
    GroupSelectionAction(double x, double y)
    {
      super("Group Selection");
      this.x = x;
      this.y = y;
    }

    public void actionPerformed(ActionEvent e)
    {
      groupSelection(x, y);
    }
  }
  
  class UngroupSelectionAction extends AbstractAction
  {
    UngroupSelectionAction()
    {
      super("Ungroup Selection");
    }

    public void actionPerformed(ActionEvent e)
    {
      ungroupSelection();
    }
  }

  class UpAction extends AbstractAction
  {
    UpAction()
    {
      super("View Parent");
    }

    public void actionPerformed(ActionEvent e)
    {
      up();
    }
  }
  
  class DownAction extends AbstractAction
  {
    Node v;
    DownAction(Node v)
    {
      super("View Folder");
      this.v = v;
    }

    public void actionPerformed(ActionEvent e)
    {
      down(v);
    }
  }
  
  class LoadInitialGraphAction extends AbstractAction
  {
    LoadInitialGraphAction() { super("Load Initial Graph"); }
    
    public void actionPerformed(ActionEvent ae)
    {
      view.getGraph2D().clear();
      loadInitialGraph();
      view.getGraph2D().updateViews();
    }
  }
}
