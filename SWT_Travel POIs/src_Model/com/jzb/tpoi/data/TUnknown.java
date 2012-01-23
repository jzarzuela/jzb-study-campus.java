/**
 * 
 */
package com.jzb.tpoi.data;

/**
 * @author n63636
 * 
 */
public class TUnknown extends TMapFigure {

    // ---------------------------------------------------------------------------------
    public TUnknown(TMap ownerMap) {
        super(EntityType.Unknown, ownerMap);
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see com.jzb.tpoi.data.TBaseEntity#getDefaultIcon()
     */
    @Override
    protected TIcon getDefaultIcon() {
        // TODO Auto-generated method stub
        return null;
    }

}
