package xyz.phanta.ae2fc.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class TileUnpackagerTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] code) {
        if (transformedName.equals("thelm.packagedauto.tile.TileUnpackager")) {
            System.out.println("[ae2fc] Transforming TileUnpackager...");
            ClassReader reader = new ClassReader(code);
            ClassWriter writer = new ClassWriter(reader, 0);
            reader.accept(new TransformTileUnpackager(Opcodes.ASM5, writer), 0);
            return writer.toByteArray();
        }
        return code;
    }

    private static class TransformTileUnpackager extends ClassVisitor {

        TransformTileUnpackager(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals("emptyTrackers") && desc.equals("()V")) {
                return new TransformEmptyTrackers(api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

    }

    private static class TransformEmptyTrackers extends MethodVisitor {

        private boolean gettingItemHandlerCap = false;

        TransformEmptyTrackers(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            super.visitFieldInsn(opcode, owner, name, desc);
            if (opcode == Opcodes.GETSTATIC && desc.equals("Lnet/minecraftforge/common/capabilities/Capability;")) {
                gettingItemHandlerCap = name.equals("ITEM_HANDLER_CAPABILITY");
            }
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (opcode == Opcodes.INVOKEVIRTUAL && gettingItemHandlerCap) {
                switch (name) {
                    case "hasCapability":
                        if (desc.equals("(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/util/EnumFacing;)Z")) {
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "xyz/phanta/ae2fc/handler/CoreModHooks", "checkForItemHandler",
                                    "(Lnet/minecraftforge/common/capabilities/ICapabilityProvider;Lnet/minecraftforge/common/capabilities/Capability;" +
                                            "Lnet/minecraft/util/EnumFacing;)Z", false);
                            gettingItemHandlerCap = false;
                        }
                        break;
                    case "getCapability":
                        if (desc.equals("(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/util/EnumFacing;)Ljava/lang/Object;")) {
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "xyz/phanta/ae2fc/handler/CoreModHooks", "wrapItemHandler",
                                    "(Lnet/minecraftforge/common/capabilities/ICapabilityProvider;Lnet/minecraftforge/common/capabilities/Capability;" +
                                            "Lnet/minecraft/util/EnumFacing;)Lnet/minecraftforge/items/IItemHandler;", false);
                            gettingItemHandlerCap = false;
                        }
                        break;
                    default:
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        break;
                }
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }

    }

}
