package xyz.phanta.ae2fc.inventory.base;

public interface TankDumpable {

    boolean canDumpTank(int index);

    void dumpTank(int index);

}
