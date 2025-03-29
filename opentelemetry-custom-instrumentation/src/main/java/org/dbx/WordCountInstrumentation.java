package org.dbx;

/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

// Import necessary libraries.
// ElementMatchers helps to define custom matchers for Byte Buddy's ElementMatcher interface.

// OpenTelemetry libraries for tracing capabilities
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;

// Libraries for Java agent instrumentation
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;

// Byte Buddy libraries for method intercepting
import net.bytebuddy.asm.Advice;
        import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

// Java logging library
import java.util.Objects;
import java.util.logging.Logger;

// The WordCountInstrumentation class implements the TypeInstrumentation interface.
// This allows us to specify which types of classes (based on some matching criteria) will have their methods instrumented.
public class WordCountInstrumentation implements TypeInstrumentation {
    // Create a Logger instance for logging.
    private static Logger logger = Logger.getLogger(WordCountInstrumentation.class.getName());

    // The typeMatcher method is used to define which classes the instrumentation should apply to.
    // In this case, it's the "org.dbx.Main" class.
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        logger.info("TEST typeMatcher");
        return a -> {
            if(Objects.isNull(a) || Objects.isNull(a.getCanonicalName())) {
                return false;
            }
            return a.getCanonicalName().contains("org.dbx");
        };
    }

    // In the transform method, we specify which methods of the classes matched above will be instrumented, 
    // and also the advice (a piece of code) that will be added to these methods.
    @Override
    public void transform(TypeTransformer typeTransformer) {
        logger.info("TEST transform");
        typeTransformer.applyAdviceToMethod(MethodDescription::isMethod,this.getClass().getName() + "$WordCountAdvice");
    }

    // The WordCountAdvice class contains the actual pieces of code (advices) that will be added to the instrumented methods.
    @SuppressWarnings("unused")
    public static class WordCountAdvice {
        // This advice is added at the beginning of the instrumented method (OnMethodEnter).
        // It creates and starts a new span, and makes it active.
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static Scope onEnter(@Advice.Local("otelSpan") Span span,
                                    @Advice.Origin Class<?> clazz,
                                    @Advice.Origin("#m") String methodName) {
            // Get a Tracer instance from OpenTelemetry.
            Tracer tracer = GlobalOpenTelemetry.getTracer("instrumentation-library-name","semver:1.0.0");
            String className = clazz.getCanonicalName();
            String fullMethodName = className + "." + methodName;
            System.out.println("Entering method: " + fullMethodName);

            // Get the current active span (parent span).
            Span parentSpan = Span.current();

            // Start a new child span from the parent span.
            span = tracer.spanBuilder(fullMethodName)
                    .setParent(Context.current().with(parentSpan))
                    .startSpan();

            // Start a new span with the name "mySpan".
//            span = tracer.spanBuilder(fullMethodName).startSpan();
            // Make this new span the current active span.
            Scope scope = span.makeCurrent();

            // Return the Scope instance. This will be used in the exit advice to end the span's scope.
            return scope; 
        }

        // This advice is added at the end of the instrumented method (OnMethodExit).
        // It first closes the span's scope, then checks if any exception was thrown during the method's execution.
        // If an exception was thrown, it sets the span's status to ERROR and ends the span.
        // If no exception was thrown, it sets a custom attribute "wordCount" on the span, and ends the span.
        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void onExit(@Advice.Thrown Throwable throwable,
                                  @Advice.Local("otelSpan") Span span,
                                  @Advice.Enter Scope scope) {
            // Close the scope to end it.
            scope.close();

            // If an exception was thrown during the method's execution, set the span's status to ERROR.
            if (throwable != null) {
                span.setStatus(StatusCode.ERROR, "Exception thrown in method");
            } else {
                // If no exception was thrown, set a custom attribute "wordCount" on the span.
            }

            // End the span. This makes it ready to be exported to the configured exporter (e.g., Jaeger, Zipkin).
            span.end();
        }
    }
}
