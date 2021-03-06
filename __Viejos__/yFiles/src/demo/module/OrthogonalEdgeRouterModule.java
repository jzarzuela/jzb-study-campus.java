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
package demo.module;

import y.layout.Layouter;
import y.layout.router.GroupNodeRouterStage;
import y.layout.router.OrthogonalEdgeRouter;
import y.layout.router.PatchRouterStage;
import y.layout.router.ReducedSphereOfActionStage;
import y.layout.router.EdgeGroupRouterStage;
import y.option.ConstraintManager;
import y.option.OptionHandler;
import y.option.OptionGroup;
import y.view.hierarchy.GroupLayoutConfigurator;
import y.module.LayoutModule;

/**
 * This module represents an interactive configurator and launcher for
 * {@link y.layout.router.OrthogonalEdgeRouter}.
 * It is similar to OrthogonalEdgeRouterModule found in the yFiles package
 * y.module.
 *
 */
public class OrthogonalEdgeRouterModule extends LayoutModule
{
  private static final String NAME  = "ORTHOGONAL_EDGE_ROUTER";
  private static final String SCOPE = "SCOPE";
  private static final String SCOPE_ALL_EDGES = "ALL_EDGES";
  private static final String SCOPE_SELECTED_EDGES = "SELECTED_EDGES";
  private static final String SCOPE_AT_SELECTED_NODES = "AT_SELECTED_NODES";
  private static final String MINIMUM_DISTANCE_TO_EDGE = "MINIMUM_DISTANCE_TO_EDGE";
  private static final String USE_CUSTOM_MINIMUM_DISTANCE_TO_NODE = "USE_CUSTOM_MINIMUM_DISTANCE_TO_NODE";
  private static final String CUSTOM_MINIMUM_DISTANCE_TO_NODE = "CUSTOM_MINIMUM_DISTANCE_TO_NODE";
  private static final String SPACE_DRIVEN_VS_CENTER_DRIVEN_SEARCH = "SPACE_DRIVEN_VS_CENTER_DRIVEN_SEARCH";
  private static final String LOCAL_CROSSING_MINIMIZATION = "LOCAL_CROSSING_MINIMIZATION";
  private static final String GRID_SPACING = "GRID_SPACING";
  private static final String ROUTE_ON_GRID = "ROUTE_ON_GRID";
  private static final String CROSSING_COST     = "CROSSING_COST";
  private static final String REROUTING_ENABLED = "REROUTING_ENABLED";

  private static final String LAYOUT_OPTIONS = "LAYOUT_OPTIONS";
  private static final String CROSSING_MINIMIZATION = "CROSSING_MINIMIZATION";


  //////////////////////////////////////////////////////////////////////////////
  //// Own stuff
  //////////////////////////////////////////////////////////////////////////////
  private OrthogonalEdgeRouter router;

  //////////////////////////////////////////////////////////////////////////////
  //// Construction
  //////////////////////////////////////////////////////////////////////////////
  public OrthogonalEdgeRouterModule()
  {
    super(NAME, "yFiles Layout Team", "Routes edges orthogonally.");
    setPortIntersectionCalculatorEnabled(true);
  }
  
  //////////////////////////////////////////////////////////////////////////////
  //// Implementation for abstract class y.module.YModule
  //////////////////////////////////////////////////////////////////////////////
  protected void init(){ instantiateRouter(); }
  
  protected void mainrun()
  {  
    // initialize potential grouping information
    GroupLayoutConfigurator glc = new GroupLayoutConfigurator(getGraph2D());
    try {
      // register grouping relevant DataProviders
      glc.prepareAll();
      // launch layouter in buffered mode
      launchLayouter(
          new EdgeGroupRouterStage(
              new GroupNodeRouterStage(
                  new ReducedSphereOfActionStage(
                      new PatchRouterStage(router)))));
    } finally {
      // make sure the DataProviders will always be unregistered
      glc.restoreAll();
    }
  }
  
  protected void dispose(){ router = null; }
  
  /**
   * Creates and initializes the Option Handler so that a convenient way for
   * manipulating the parameters is at the user's hand.
   */
  protected OptionHandler createOptionHandler()
  {
    OptionHandler oh = new OptionHandler(getModuleName());  
    initOptionHandler(oh, null);
    return oh;
  }
  
  void initOptionHandler(OptionHandler oh, Layouter layouter)
  {
    oh.clear();
    if(layouter == null) layouter = new OrthogonalEdgeRouter();
    OrthogonalEdgeRouter router = (OrthogonalEdgeRouter)layouter;

    OptionGroup og = new OptionGroup();
    og.setAttribute(OptionGroup.ATTRIBUTE_TITLE, LAYOUT_OPTIONS);
    String enums[] = { SCOPE_ALL_EDGES, SCOPE_SELECTED_EDGES, SCOPE_AT_SELECTED_NODES };
    if(router.getSphereOfAction() == OrthogonalEdgeRouter.ROUTE_ALL_EDGES)
      og.addItem(oh.addEnum(SCOPE, enums, 0));
    else if(router.getSphereOfAction() == OrthogonalEdgeRouter.ROUTE_EDGES_AT_SELECTED_NODES)
      og.addItem(oh.addEnum(SCOPE, enums, 1));
    else
      og.addItem(oh.addEnum(SCOPE, enums, 2));
    
    // The value given for 'minimum distance' denotes a halo to the left and
    // right of an edge segment.
    og.addItem(oh.addInt(MINIMUM_DISTANCE_TO_EDGE, router.getMinimumDistance()));
    og.addItem(oh.addBool(USE_CUSTOM_MINIMUM_DISTANCE_TO_NODE, !router.getCoupledDistances()));
    og.addItem(oh.addInt(CUSTOM_MINIMUM_DISTANCE_TO_NODE, router.getMinimumDistanceToNode()));
    og.addItem(oh.addBool(ROUTE_ON_GRID, router.isGridRoutingEnabled()));
    og.addItem(oh.addInt(GRID_SPACING, router.getGridSpacing()));
    og.addItem(oh.addDouble(SPACE_DRIVEN_VS_CENTER_DRIVEN_SEARCH,
                            router.getCenterToSpaceRatio(), 0.0, 1.0));
    
    og = new OptionGroup();
    og.setAttribute(OptionGroup.ATTRIBUTE_TITLE, CROSSING_MINIMIZATION);
    og.addItem(oh.addBool(LOCAL_CROSSING_MINIMIZATION,
                          router.isLocalCrossingMinimizationEnabled()));
    
    og.addItem(oh.addDouble(CROSSING_COST, router.getCrossingCost()));
    og.addItem(oh.addBool(REROUTING_ENABLED, router.isReroutingEnabled()));
  
    ConstraintManager cm = new ConstraintManager(oh);
    cm.setEnabledOnValueEquals(ROUTE_ON_GRID, Boolean.TRUE, GRID_SPACING);
    cm.setEnabledOnValueEquals(USE_CUSTOM_MINIMUM_DISTANCE_TO_NODE,
                               Boolean.TRUE,
                               CUSTOM_MINIMUM_DISTANCE_TO_NODE);
    
  }
  
  /**
   * Initializes the option hander of this module with the 
   * properties of the given router. 
   * @param layouter an instance of {@link y.layout.router.OrthogonalEdgeRouter}.
   */
  public void initOptionHandler(Layouter layouter)
  {
    OptionHandler oh = getOptionHandler();
    initOptionHandler(oh, layouter);
  }
  
  /**
   * Configures an instance of OrthogonalEdgeRouter. The values provided by
   * this module's option handler are being used for this purpose.
   */
  public void configure(Layouter layouter)
  {
    if (layouter instanceof OrthogonalEdgeRouter)
    {
      OrthogonalEdgeRouter router = (OrthogonalEdgeRouter)layouter;
      OptionHandler oh = getOptionHandler();
      
      String choice = oh.getString(SCOPE);
      if (choice.equals(SCOPE_AT_SELECTED_NODES))
        router.setSphereOfAction(OrthogonalEdgeRouter.ROUTE_EDGES_AT_SELECTED_NODES);
      else if (choice.equals(SCOPE_SELECTED_EDGES))
        router.setSphereOfAction(OrthogonalEdgeRouter.ROUTE_SELECTED_EDGES);
      else
        router.setSphereOfAction(OrthogonalEdgeRouter.ROUTE_ALL_EDGES);
      
      router.setMinimumDistance(oh.getInt(MINIMUM_DISTANCE_TO_EDGE));
      router.setCoupledDistances(!oh.getBool(USE_CUSTOM_MINIMUM_DISTANCE_TO_NODE));
      router.setMinimumDistanceToNode(oh.getInt(CUSTOM_MINIMUM_DISTANCE_TO_NODE));
      
      router.setGridRoutingEnabled(oh.getBool(ROUTE_ON_GRID));
      router.setGridSpacing(oh.getInt(GRID_SPACING));
      
      router.setCenterToSpaceRatio(oh.getDouble(SPACE_DRIVEN_VS_CENTER_DRIVEN_SEARCH));
      
      router.setLocalCrossingMinimizationEnabled(oh.getBool(LOCAL_CROSSING_MINIMIZATION));

      router.setCrossingCost(oh.getDouble(CROSSING_COST));
      router.setReroutingEnabled(oh.getBool(REROUTING_ENABLED));
      
      // Further, non-public options.
      //router.setInnerPortsEnabled(true);
    }
    else
    {
      throw new IllegalArgumentException("argument must be of type y.layout.router.OrthogonalEdgeRouter");
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////
  //// Own stuff
  //////////////////////////////////////////////////////////////////////////////
  private void instantiateRouter()
  {
    if (router != null)
      return;
    router = new OrthogonalEdgeRouter();
    configure(router);
  }
}
