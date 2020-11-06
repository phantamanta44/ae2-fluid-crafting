package xyz.phanta.ae2fc.coremod.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import xyz.phanta.ae2fc.coremod.FcClassTransformer;

public class ContainerInterfaceTerminalTransformer extends FcClassTransformer.ClassMapper {

    public static final ContainerInterfaceTerminalTransformer INSTANCE = new ContainerInterfaceTerminalTransformer();

    private ContainerInterfaceTerminalTransformer() {
        // NO-OP
    }

    @Override
    protected ClassVisitor getClassMapper(ClassVisitor downstream) {
        return new TransformContainerInterfaceTerminal(Opcodes.ASM5, downstream);
    }

    private static class TransformContainerInterfaceTerminal extends ClassVisitor {

        TransformContainerInterfaceTerminal(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals("detectAndSendChanges") || name.equals("func_75142_b") || name.equals("regenList")) {
                return new TransformDetectAndSendChanges(api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

    }

    private static class TransformDetectAndSendChanges extends MethodVisitor {

        TransformDetectAndSendChanges(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (opcode == Opcodes.INVOKEINTERFACE
                    && owner.equals("appeng/api/networking/IGrid") && name.equals("getMachines")) {
                super.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "xyz/phanta/ae2fc/handler/CoreModHooks",
                        "getMachines",
                        "(Lappeng/api/networking/IGrid;Ljava/lang/Class;)Lappeng/api/networking/IMachineSet;",
                        false);
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }

    }
}
