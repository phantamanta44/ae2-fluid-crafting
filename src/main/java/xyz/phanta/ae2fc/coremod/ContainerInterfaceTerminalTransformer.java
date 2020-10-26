package xyz.phanta.ae2fc.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class ContainerInterfaceTerminalTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] code) {
        if (transformedName.equals("appeng.container.implementations.ContainerInterfaceTerminal")) {
            System.out.println("[ae2fc] Transforming ContainerInterfaceTerminalTransformer...");
            ClassReader reader = new ClassReader(code);
            ClassWriter writer = new ClassWriter(reader, 0);
            reader.accept(new TransformContainerInterfaceTerminal(Opcodes.ASM5, writer), 0);
            return writer.toByteArray();
        }
        return code;
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
