package xyz.phanta.ae2fc.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class PackageCraftingPatternHelperTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] code) {
        if (transformedName.equals("thelm.packagedauto.integration.appeng.recipe.PackageCraftingPatternHelper")) {
            System.out.println("[ae2fc] Transforming PackageCraftingPatternHelper...");
            ClassReader reader = new ClassReader(code);
            ClassWriter writer = new ClassWriter(reader, 0);
            reader.accept(new TransformPackageCraftingPatternHelper(Opcodes.ASM5, writer), 0);
            return writer.toByteArray();
        }
        return code;
    }

    private static class TransformPackageCraftingPatternHelper extends ClassVisitor {

        TransformPackageCraftingPatternHelper(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals("<init>")) {
                return new TransformCtor(api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

    }

    private static class TransformCtor extends MethodVisitor {

        TransformCtor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            if (opcode == Opcodes.PUTFIELD && desc.equals("[Lappeng/api/storage/data/IAEItemStack;")) {
                switch (name) {
                    case "inputs":
                    case "outputs":
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "xyz/phanta/ae2fc/handler/CoreModHooks", "flattenFluidPackets",
                                "([Lappeng/api/storage/data/IAEItemStack;)[Lappeng/api/storage/data/IAEItemStack;", false);
                        break;
                }
            }
            super.visitFieldInsn(opcode, owner, name, desc);
        }

    }

}
