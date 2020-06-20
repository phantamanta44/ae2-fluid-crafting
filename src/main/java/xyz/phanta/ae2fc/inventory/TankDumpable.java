package xyz.phanta.ae2fc.inventory;

public interface TankDumpable {

    boolean canDumpTank(int index);

    void dumpTank(int index);

}
