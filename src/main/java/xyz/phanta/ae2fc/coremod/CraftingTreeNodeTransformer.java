package xyz.phanta.ae2fc.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class CraftingTreeNodeTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] code) {
        if (transformedName.equals("appeng.crafting.CraftingTreeNode")) {
            System.out.println("[ae2fc] Transforming CraftingTreeNode...");
            ClassReader reader = new ClassReader(code);
            ClassWriter writer = new ClassWriter(reader, 0);
            reader.accept(new TransformCraftingTreeNode(Opcodes.ASM5, writer), 0);
            return writer.toByteArray();
        }
        return code;
    }

    private static class TransformCraftingTreeNode extends ClassVisitor {

        TransformCraftingTreeNode(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals("request")) {
                return new TransformRequest(api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

    }

    private static class TransformRequest extends MethodVisitor {

        private boolean writingBytes = false;

        TransformRequest(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            if (opcode == Opcodes.GETFIELD && owner.equals("appeng/crafting/CraftingTreeNode") && name.equals("bytes")) {
                writingBytes = true;
            }
            super.visitFieldInsn(opcode, owner, name, desc);
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            writingBytes = false; // no write here
            super.visitLineNumber(line, start);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (writingBytes && opcode == Opcodes.INVOKEINTERFACE
                    && owner.equals("appeng/api/storage/data/IAEItemStack") && name.equals("getStackSize")) {
                writingBytes = false;
                super.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "xyz/phanta/ae2fc/handler/CoreModHooks",
                        "getCraftingByteCost",
                        "(Lappeng/api/storage/data/IAEItemStack;)J",
                        false);
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }

    }

}
