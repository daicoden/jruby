package org.jruby.compiler.ir.instructions.ruby18;

import org.jruby.compiler.ir.Interp;
import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.instructions.CopyInstr;
import org.jruby.compiler.ir.instructions.Instr;
import org.jruby.compiler.ir.instructions.RestArgMultipleAsgnInstr;
import org.jruby.compiler.ir.instructions.ReceiveRestArgBase;
import org.jruby.compiler.ir.operands.Variable;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;

/*
 * Assign rest arg passed into method to a result variable
 */
public class ReceiveRestArgInstr extends ReceiveRestArgBase {
    public ReceiveRestArgInstr(Variable result, int argIndex) {
        super(result, argIndex);
    }

    @Override
    public Instr cloneForInlinedScope(InlinerInfo ii) {
        return new CopyInstr(ii.getRenamedVariable(result), ii.getCallArg(argIndex, true));
    }

    @Override
    public Instr cloneForBlockCloning(InlinerInfo ii) {
        return new ReceiveRestArgInstr(ii.getRenamedVariable(result), argIndex);
    }

    @Override
    public Instr cloneForInlinedClosure(InlinerInfo ii) {
        return new RestArgMultipleAsgnInstr(ii.getRenamedVariable(result), ii.getYieldArg(), -1, -1, argIndex);
    }

    private IRubyObject[] NO_PARAMS = new IRubyObject[0];    
    public IRubyObject receiveRestArg(Ruby runtime, IRubyObject[] parameters) {
        int available = parameters.length - argIndex;
        
        IRubyObject[] args;
        if (available <= 0) { 
            args = NO_PARAMS;
        } else {
            args = new IRubyObject[available];
            System.arraycopy(parameters, argIndex, args, 0, available);
        }
        
        return runtime.newArray(args);
    }
}
