package com.metapatrol.gitlab.ci.runner.engine.service;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public abstract class ProgressStateListener{
    public abstract void enterState(State state);

    public State fromStdOut(String string){
        return new State(State.Stream.STDOUT, string);
    }

    public State fromStdErr(String string){
        return new State(State.Stream.STDERR, string);
    }

    public static class State{
        private Stream stream;
        private String message;
        public State(Stream stream, String message){
            this.stream = stream;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Stream getStream() {
            return stream;
        }

        public void setStream(Stream stream) {
            this.stream = stream;
        }

        public enum Stream {
            STDOUT,
            STDERR;
        }
    }

}
