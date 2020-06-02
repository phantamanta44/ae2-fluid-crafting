package xyz.phanta.ae2fc.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class DualityInterfaceTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] code) {
        if (transformedName.equals("appeng.helpers.DualityInterface")) {
            System.out.println("[ae2fc] Transforming DualityInterface...");
            ClassReader reader = new ClassReader(code);
            ClassWriter writer = new ClassWriter(reader, 0);
            reader.accept(new TransformDualityInterface(Opcodes.ASM5, writer), 0);
            return writer.toByteArray();
        }
        return code;
    }

    private static class TransformDualityInterface extends ClassVisitor {

        TransformDualityInterface(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            switch (name) {
                case "pushItemsOut":
                case "pushPattern":
                case "isBusy":
                    return new TransformInvAdaptorCalls(api, super.visitMethod(access, name, desc, signature, exceptions));
                default:
                    return super.visitMethod(access, name, desc, signature, exceptions);
            }
        }

    }

    private static class TransformInvAdaptorCalls extends MethodVisitor {

        TransformInvAdaptorCalls(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (opcode == Opcodes.INVOKESTATIC && owner.equals("appeng/util/InventoryAdaptor") && name.equals("getAdaptor")) {
                super.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "xyz/phanta/ae2fc/handler/CoreModHooks",
                        "wrapInventory",
                        "(Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/util/EnumFacing;)Lappeng/util/InventoryAdaptor;",
                        false);
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }

    }

}
