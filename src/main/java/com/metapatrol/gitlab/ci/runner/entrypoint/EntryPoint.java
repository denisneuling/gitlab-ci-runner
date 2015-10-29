package com.metapatrol.gitlab.ci.runner.entrypoint;

import com.metapatrol.gitlab.ci.runner.commands.AbstractBaseCommand;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public abstract class EntryPoint<T extends AbstractBaseCommand> {

    public abstract boolean enter(T command);

    public static class EntryPointInvoker{
        private EntryPoint entryPoint;

        private EntryPointInvoker(){}
        private EntryPointInvoker(EntryPoint entryPoint){
            this.entryPoint = entryPoint;
        }

        public static EntryPointInvoker get(EntryPoint entryPoint){
            return new EntryPointInvoker(entryPoint);
        }

        public boolean invoke(AbstractBaseCommand command){
            return entryPoint.enter(command);
        }
    }
}
