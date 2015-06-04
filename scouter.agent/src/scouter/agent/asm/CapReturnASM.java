/*
 *  Copyright 2015 LG CNS.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

package scouter.agent.asm;

import java.util.List;

import scouter.agent.ClassDesc;
import scouter.agent.Configure;
import scouter.agent.asm.util.AsmUtil;
import scouter.agent.asm.util.MethodSet;
import scouter.agent.trace.TraceMain;
import scouter.org.objectweb.asm.ClassVisitor;
import scouter.org.objectweb.asm.MethodVisitor;
import scouter.org.objectweb.asm.Opcodes;
import scouter.org.objectweb.asm.Type;
import scouter.org.objectweb.asm.commons.LocalVariablesSorter;



public class CapReturnASM implements IASM, Opcodes {
	private  List< MethodSet> target = MethodSet.getHookingMethodSet(Configure.getInstance().hook_return);

	
	public boolean isTarget(String className) {
		for (int i = 0; i < target.size(); i++) {
			MethodSet mset = target.get(i);
			if (mset.classMatch.include(className)) {
				return true;
			}
		}
		return false;
	}
	public ClassVisitor transform(ClassVisitor cv, String className, ClassDesc classDesc) {

		for (int i = 0; i < target.size(); i++) {
			MethodSet mset = target.get(i);
			if (mset.classMatch.include(className)) {
				return new CapReturnCV(cv, mset, className);
			}
		}
		return cv;
	}

}

// ///////////////////////////////////////////////////////////////////////////
class CapReturnCV extends ClassVisitor implements Opcodes {

	public String className;
	private MethodSet mset;

	public CapReturnCV(ClassVisitor cv, MethodSet mset, String className) {
		super(ASM4, cv);
		this.mset = mset;
		this.className = className;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		if (mv == null || mset.isA(name, desc) == false) {
			return mv;
		}
		if(AsmUtil.isSpecial(name)){
			return mv;
		}		

		return new CapReturnMV(access, desc, mv, className, name, desc);
	}
}

// ///////////////////////////////////////////////////////////////////////////
class CapReturnMV extends LocalVariablesSorter implements Opcodes {
	private static final String CLASS = TraceMain.class.getName().replace('.', '/');
	private static final String METHOD = "capReturn";
	private static final String SIGNATURE = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V";

	private Type returnType;
	private String className;
	private String methodName;
	private String methodDesc;
	
	public CapReturnMV(int access, String desc, MethodVisitor mv,
			String classname,
			String methodname,
			String methoddesc) {
		super(ASM4, access, desc, mv);
		this.returnType = Type.getReturnType(desc);
		this.className = classname;
		this.methodName = methodname;
		this.methodDesc = methoddesc;

	}

	@Override
	public void visitInsn(int opcode) {
		if ((opcode >= IRETURN && opcode <= RETURN)) {
			capReturn();
		}
		mv.visitInsn(opcode);
	}

	private void capReturn() {
		Type tp = returnType;

		if (tp == null || tp.equals(Type.VOID_TYPE)) {
			AsmUtil.PUSH(mv, className);
			AsmUtil.PUSH(mv, methodName);
			AsmUtil.PUSH(mv, methodDesc);
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, CLASS, METHOD, SIGNATURE);
			return;
		}
		int i = newLocal(tp);
		switch (tp.getSort()) {
		case Type.BOOLEAN:
			mv.visitVarInsn(Opcodes.ISTORE, i);
			mv.visitVarInsn(Opcodes.ILOAD, i);

			AsmUtil.PUSH(mv, className);
			AsmUtil.PUSH(mv, methodName);
			AsmUtil.PUSH(mv, methodDesc);

			mv.visitVarInsn(Opcodes.ILOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
			break;
		case Type.BYTE:
			mv.visitVarInsn(Opcodes.ISTORE, i);
			mv.visitVarInsn(Opcodes.ILOAD, i);

			AsmUtil.PUSH(mv, className);
			AsmUtil.PUSH(mv, methodName);
			AsmUtil.PUSH(mv, methodDesc);

			mv.visitVarInsn(Opcodes.ILOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
			break;
		case Type.CHAR:
			mv.visitVarInsn(Opcodes.ISTORE, i);
			mv.visitVarInsn(Opcodes.ILOAD, i);

			AsmUtil.PUSH(mv, className);
			AsmUtil.PUSH(mv, methodName);
			AsmUtil.PUSH(mv, methodDesc);

			mv.visitVarInsn(Opcodes.ILOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
			break;
		case Type.SHORT:
			mv.visitVarInsn(Opcodes.ISTORE, i);
			mv.visitVarInsn(Opcodes.ILOAD, i);

			AsmUtil.PUSH(mv, className);
			AsmUtil.PUSH(mv, methodName);
			AsmUtil.PUSH(mv, methodDesc);

			mv.visitVarInsn(Opcodes.ILOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
			break;
		case Type.INT:
			mv.visitVarInsn(Opcodes.ISTORE, i);
			mv.visitVarInsn(Opcodes.ILOAD, i);

			AsmUtil.PUSH(mv, className);
			AsmUtil.PUSH(mv, methodName);
			AsmUtil.PUSH(mv, methodDesc);

			mv.visitVarInsn(Opcodes.ILOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
			break;
		case Type.LONG:
			mv.visitVarInsn(Opcodes.LSTORE, i);
			mv.visitVarInsn(Opcodes.LLOAD, i);

			AsmUtil.PUSH(mv, className);
			AsmUtil.PUSH(mv, methodName);
			AsmUtil.PUSH(mv, methodDesc);

			mv.visitVarInsn(Opcodes.LLOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
			break;
		case Type.FLOAT:
			mv.visitVarInsn(Opcodes.FSTORE, i);
			mv.visitVarInsn(Opcodes.FLOAD, i);

			AsmUtil.PUSH(mv, className);
			AsmUtil.PUSH(mv, methodName);
			AsmUtil.PUSH(mv, methodDesc);

			mv.visitVarInsn(Opcodes.FLOAD, i);

			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
			break;
		case Type.DOUBLE:
			mv.visitVarInsn(Opcodes.DSTORE, i);
			mv.visitVarInsn(Opcodes.DLOAD, i);

			AsmUtil.PUSH(mv, className);
			AsmUtil.PUSH(mv, methodName);
			AsmUtil.PUSH(mv, methodDesc);
			
			mv.visitVarInsn(Opcodes.DLOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
			break;
		default:
			mv.visitVarInsn(Opcodes.ASTORE, i);
			mv.visitVarInsn(Opcodes.ALOAD, i);

			AsmUtil.PUSH(mv, className);
			AsmUtil.PUSH(mv, methodName);
			AsmUtil.PUSH(mv, methodDesc);

			mv.visitVarInsn(Opcodes.ALOAD, i);
		}

		mv.visitMethodInsn(Opcodes.INVOKESTATIC, CLASS, METHOD, SIGNATURE);
	}
}