package kladipolis.apocalypsemobs.entity;

import com.minecolonies.api.colony.IColony;

public interface IColonyBound {
    IColony getColony();
    void initialize(IColony colony);
}
