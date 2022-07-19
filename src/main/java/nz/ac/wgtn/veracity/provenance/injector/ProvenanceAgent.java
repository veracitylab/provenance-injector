package nz.ac.wgtn.veracity.provenance.injector;

import net.bytebuddy.agent.builder.*;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.jar.JarFile;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class ProvenanceAgent {

    private static final String AGENT_PACKAGE = ProvenanceAgent.class.getPackageName();
    private static final String BYTEBUDDY_PACKAGE = "net.bytebuddy.";
    private static final ElementMatcher.Junction<TypeDescription> IGNORED_TYPES =
            nameStartsWith(AGENT_PACKAGE)
                    .or(nameStartsWith(BYTEBUDDY_PACKAGE))
                    .or(nameStartsWith("com.sun.proxy"))
                    .or(nameStartsWith("java.instrument."))
                    .or(nameStartsWith("java.io"))
                    .or(nameStartsWith("java.lang"))
                    .or(nameStartsWith("java.lang.invoke"))
                    .or(nameStartsWith("java.lang.reflect."))
                    .or(nameStartsWith("java.nio").and(nameContains("Buffer")))
                    .or(nameStartsWith("java.nio.charset"))
                    .or(nameStartsWith("java.security."))
                    .or(nameStartsWith("java.util.").and(not(nameStartsWith("java.util.UUID"))))
                    .or(nameStartsWith("jdk.internal."))
                    .or(nameStartsWith("sun."))
                    .or(nameStartsWith("sun.reflect"))
                    .or(nameStartsWith("sun.security."))
                    .or(nameStartsWith("org.springframework.boot."))
                    .or(named("java.io.PrintStream"))
                    .or(named("java.lang.Character"))
                    .or(named("java.lang.Class"))
                    .or(named("java.lang.Integer"))
                    .or(named("java.lang.Long"))
                    .or(named("java.lang.Math"))
                    .or(named("java.lang.Object"))
                    .or(named("java.lang.PublicMethods"))
                    .or(named("java.lang.SecurityManager"))
                    .or(named("java.lang.String"))
                    .or(named("java.lang.StringBuilder"))
                    .or(named("java.lang.Throwable"))
                    .or(named("java.lang.WeakPairMap"))
                    .or(named("java.lang.ref.SoftReference"))
                    .or(named("java.util.Arrays"))
                    .or(named("java.util.HashMap"))
                    .or(named("java.util.Stack"))
                    .or(named("java.util.String"))
                    .or(nameEndsWith("Exception"))
                    .or(nameMatches(".*[.]instrument[.].*"))
                    .or(nameMatches("java[.]io[.].*Writer"))
                    .or(nameMatches("java[.]net[.]URL.*"));
    /**
     * Allows installation of java agent from command line.
     *
     * @param agentArguments
     *            arguments for agent
     * @param instrumentation
     *            instrumentation instance
     */
    public static void premain(String agentArguments,
                               Instrumentation instrumentation) {
            install(instrumentation);
    }

    /**
     * Allows installation of java agent with Attach API.
     *
     * @param agentArguments
     *            arguments for agent
     * @param instrumentation
     *            instrumentation instance
     */
    public static void agentmain(String agentArguments,
                                 Instrumentation instrumentation) {
        install(instrumentation);
    }

    private static void install(Instrumentation instrumentation) {
        createAgent().installOn(instrumentation);
    }

    private static AgentBuilder createAgent() {
        return new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                // Show logs
                .with(AgentBuilder.RedefinitionStrategy.Listener.StreamWriting.toSystemError())
                .with(AgentBuilder.Listener.StreamWriting.toSystemError())
                .with(AgentBuilder.InstallationListener.StreamWriting.toSystemError())
//                .with(AgentBuilder.RedefinitionStrategy.DiscoveryStrategy.Reiterating.INSTANCE)
//                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
//                .with(AgentBuilder.LambdaInstrumentationStrategy.ENABLED)

//                .with(AgentBuilder.TypeStrategy.Default.REBASE)
                .ignore(IGNORED_TYPES)
                .type(any())
//                .type(nameStartsWith("nz.ac.canterbury.dataprovenancedemo.controllers.LibraryController"))
                .transform(new AgentBuilder.Transformer.ForAdvice()
//                        .include(ProvenanceAgent.class.getClassLoader())
                        .include(Thread.currentThread().getContextClassLoader())

//                        .advice(any().and(not(ElementMatchers.isConstructor())), "nz.ac.wgtn.veracity.provenance.injector.ProvenanceAdvice"));
                        .advice(any().and(not(ElementMatchers.isConstructor())).and(not(ElementMatchers.isStatic())), "nz.ac.wgtn.veracity.provenance.injector.ProvenanceAdvice"));
//                .transform((builder, typeDescription, classLoader, javaModule) -> builder.visit(Advice.to(ProvenanceAdvice.class).on(
//                        any())));
    }

}