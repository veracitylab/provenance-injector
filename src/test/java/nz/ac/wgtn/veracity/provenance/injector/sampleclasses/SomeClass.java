package nz.ac.wgtn.veracity.provenance.injector.sampleclasses;

public class SomeClass {

    public SomeClass() {

    }

    public String doSomeThing() {
        return "something";
    }
    public String doSomethingDynamically(String theArg) {
        return theArg;
    }

    public static String doSomethingStatically(String theArg) {
        return theArg;
    }

    public void somethingElse() {
        System.out.println("Beans");
    }

    public void somethingWithArg(boolean item) {

    }

    public void somethingWithArg(char item) {

    }

    public void somethingWithArg(byte item) {

    }

    public void somethingWithArg(short item) {

    }

    public void somethingWithArg(int item) {

    }

    public void somethingWithArg(float item) {

    }

    public void somethingWithArg(long item) {

    }

    public void somethingWithArg(double item) {

    }

    public void somethingWithArg(Object item) {

    }
}
