/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  Office
 *  WorkerClass
 */
import java.util.Set;

public class ClassDefinition
implements Statement {
    private String name;

    public ClassDefinition(String string, Set set) {
        this.name = string;
    }

    public void exec(Environment environment) {
        Office.addClass((WorkerClass)new WorkerClass(this.name));
    }
}

